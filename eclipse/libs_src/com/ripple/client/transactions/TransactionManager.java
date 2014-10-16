package com.ripple.client.transactions;

import com.ripple.client.Client;
import com.ripple.client.enums.Command;
import com.ripple.client.pubsub.CallbackContext;
import com.ripple.client.pubsub.Publisher;
import com.ripple.client.requests.Request;
import com.ripple.client.responses.Response;
import com.ripple.client.subscriptions.TrackedAccountRoot;
import com.ripple.client.subscriptions.ServerInfo;
import com.ripple.core.serialized.enums.EngineResult;
import com.ripple.core.coretypes.AccountID;
import com.ripple.core.coretypes.Amount;
import com.ripple.core.coretypes.hash.Hash256;
import com.ripple.core.coretypes.uint.UInt32;
import com.ripple.core.types.known.tx.Transaction;
import com.ripple.core.types.known.tx.result.TransactionResult;
import com.ripple.core.types.known.tx.txns.AccountSet;
import com.ripple.crypto.ecdsa.IKeyPair;

import java.util.*;

public class TransactionManager extends Publisher<TransactionManager.events> {
    public static interface events<T> extends Publisher.Callback<T> {}
    // This event is emitted with the Sequence of the AccountRoot
    public static interface OnValidatedSequence extends events<UInt32> {}

    Client client;
    TrackedAccountRoot accountRoot;
    AccountID accountID;
    IKeyPair keyPair;
    AccountTransactionsRequester txnRequester;

    private ArrayList<ManagedTxn> pending = new ArrayList<ManagedTxn>();

    public TransactionManager(Client client, final TrackedAccountRoot accountRoot, AccountID accountID, IKeyPair keyPair) {
        this.client = client;
        this.accountRoot = accountRoot;
        this.accountID = accountID;
        this.keyPair = keyPair;

        // We'd be subscribed yeah ;)
        this.client.on(Client.OnLedgerClosed.class, new Client.OnLedgerClosed() {
            @Override
            public void called(ServerInfo serverInfo) {
                checkAccountTransactions(serverInfo.ledger_index);

                if (!canSubmit() || getPending().isEmpty()) {
                    return;
                }
                ArrayList<ManagedTxn> sorted = pendingSequenceSorted();

                ManagedTxn first = sorted.get(0);
                Submission previous = first.lastSubmission();

                if (previous != null) {
                    long ledgersClosed = serverInfo.ledger_index - previous.ledgerSequence;
                    if (ledgersClosed > 5) {
                        resubmitWithSameSequence(first);
                    }
                }
            }
        });
    }


    Set<Long> seenValidatedSequences = new TreeSet<Long>();
    public long sequence = 0;

    private UInt32 locallyPreemptedSubmissionSequence() {
        if (!accountRoot.primed()) {
            throw new IllegalStateException("The AccountRoot hasn't been populated from the server");
        }
        long server = accountRoot.Sequence.longValue();
        if (server > sequence) {
            sequence = server;
        }
        return new UInt32(sequence++);
    }
    private boolean txnNotFinalizedAndSeenValidatedSequence(ManagedTxn txn) {
        return !txn.isFinalized() &&
               seenValidatedSequences.contains(txn.sequence().longValue());
    }

    public void queue(final ManagedTxn tx) {
        if (accountRoot.primed()) {
            queue(tx, locallyPreemptedSubmissionSequence());
        } else {
            accountRoot.once(TrackedAccountRoot.OnUpdate.class, new TrackedAccountRoot.OnUpdate() {
                @Override
                public void called(TrackedAccountRoot accountRoot) {
                    queue(tx, locallyPreemptedSubmissionSequence());
                }
            });
        }
    }

    // TODO: data structure that keeps txns in sequence sorted order
    public ArrayList<ManagedTxn> getPending() {
        return pending;
    }

    public ArrayList<ManagedTxn> pendingSequenceSorted() {
        ArrayList<ManagedTxn> queued = new ArrayList<ManagedTxn>(getPending());
        Collections.sort(queued, new Comparator<ManagedTxn>() {
            @Override
            public int compare(ManagedTxn lhs, ManagedTxn rhs) {
                return lhs.sequence().compareTo(rhs.sequence());
            }
        });
        return queued;
    }

    public int txnsPending() {
        return getPending().size();
    }

    // TODO, maybe this is an instance configurable strategy parameter
    public static long LEDGERS_BETWEEN_ACCOUNT_TX = 15;
    public static long ACCOUNT_TX_TIMEOUT = 5;
    private long lastTxnRequesterUpdate = 0;
    private long lastLedgerCheckedAccountTxns = 0;
    AccountTransactionsRequester.OnPage onTxnsPage = new AccountTransactionsRequester.OnPage() {
        @Override
        public void onPage(AccountTransactionsRequester.Page page) {
            lastTxnRequesterUpdate = client.serverInfo.ledger_index;

            if (page.hasNext()) {
                page.requestNext();
            } else {
                lastLedgerCheckedAccountTxns = Math.max(lastLedgerCheckedAccountTxns, page.ledgerMax());
                txnRequester = null;
            }

            for (TransactionResult tr : page.transactionResults()) {
                notifyTransactionResult(tr);
            }
        }
    };

    private void checkAccountTransactions(long currentLedgerIndex) {
        if (pending.size() == 0) {
            lastLedgerCheckedAccountTxns = 0;
            return;
        }

        long ledgersPassed = currentLedgerIndex - lastLedgerCheckedAccountTxns;

        if ((lastLedgerCheckedAccountTxns == 0 || ledgersPassed >= LEDGERS_BETWEEN_ACCOUNT_TX)) {
            if (lastLedgerCheckedAccountTxns == 0) {
                lastLedgerCheckedAccountTxns = currentLedgerIndex;
                for (ManagedTxn txn : pending) {
                    for (Submission submission : txn.submissions) {
                        lastLedgerCheckedAccountTxns = Math.min(lastLedgerCheckedAccountTxns, submission.ledgerSequence);
                    }
                }
                return; // and wait for next ledger close
            }
            if (txnRequester != null) {
                if ((currentLedgerIndex - lastTxnRequesterUpdate) >= ACCOUNT_TX_TIMEOUT) {
                    txnRequester.abort(); // no more OnPage
                    txnRequester = null; // and wait for next ledger close
                }
                // else keep waiting ;)
            } else {
                lastTxnRequesterUpdate = currentLedgerIndex;
                txnRequester = new AccountTransactionsRequester(client,
                                                                accountID,
                                                                onTxnsPage,
                                                                /* for good measure */
                                                                lastLedgerCheckedAccountTxns - 5);

                // Very important VVVVV
                txnRequester.setForward(true);
                txnRequester.request();
            }
        }
    }

    private void queue(final ManagedTxn txn, final UInt32 sequence) {
        getPending().add(txn);
        makeSubmitRequest(txn, sequence);
    }

    public boolean canSubmit() {
        return client.connected  &&
               client.serverInfo.primed() &&
               client.serverInfo.load_factor < 768 &&
               accountRoot.primed();
    }

    private void makeSubmitRequest(final ManagedTxn txn, final UInt32 sequence) {
        if (canSubmit()) {
            doSubmitRequest(txn, sequence);
        }
        else {
            // If we have submitted again, before this gets to execute
            // we should just bail out early, and not submit again.
            final int n = txn.submissions.size();
            client.on(Client.OnStateChange.class, new CallbackContext() {
                @Override
                public boolean shouldExecute() {
                    return canSubmit() && !shouldRemove();
                }

                @Override
                public boolean shouldRemove() {
                    // The next state change should cause this to remove
                    return txn.isFinalized() || n != txn.submissions.size();
                }
            }, new Client.OnStateChange() {
                @Override
                public void called(Client client) {
                    doSubmitRequest(txn, sequence);
                }
            });
        }
    }

    private Request doSubmitRequest(final ManagedTxn txn, UInt32 sequence) {
        // Compute the fee for the current load_factor
        Amount fee = client.serverInfo.transactionFee(txn.txn);
        // Inside prepare we check if Fee and Sequence are the same, and if so
        // we don't recreate tx_blob, or resign ;)


        long currentLedgerIndex = client.serverInfo.ledger_index;
        UInt32 lastLedgerSequence = new UInt32(currentLedgerIndex + 8);
        Submission submission = txn.lastSubmission();
        if (submission != null) {
            if (currentLedgerIndex - submission.lastLedgerSequence.longValue() < 8) {
                lastLedgerSequence = submission.lastLedgerSequence;
            }
        }

        txn.prepare(keyPair, fee, sequence, lastLedgerSequence);

        final Request req = client.newRequest(Command.submit);
        // tx_blob is a hex string, right o' the bat
        req.json("tx_blob", txn.tx_blob);

        req.once(Request.OnSuccess.class, new Request.OnSuccess() {
            @Override
            public void called(Response response) {
                handleSubmitSuccess(txn, response);
            }
        });

        req.once(Request.OnError.class, new Request.OnError() {
            @Override
            public void called(Response response) {
                handleSubmitError(txn, response);
            }
        });

        // Keep track of the submission, including the hash submitted
        // to the network, and the ledger_index at that point in time.
        txn.trackSubmitRequest(req, client.serverInfo.ledger_index);
        req.request();
        return req;
    }

    public void handleSubmitError(ManagedTxn txn, Response res) {
        if (txn.finalizedOrResponseIsToPriorSubmission(res)) {
            return;
        }
        switch (res.rpcerr) {
            case noNetwork:
                resubmitWithSameSequence(txn);
                break;
            default:
                // TODO, what other cases should this retry?
                // TODO, what if this actually eventually clears?
                // TOOD, need to use LastLedgerSequence
                finalizeTxnAndRemoveFromQueue(txn);
                txn.emit(ManagedTxn.OnSubmitError.class, res);
                break;
        }
    }

    /**
     * We handle various transaction engine results specifically
     * and then by class of result.
     */
    public void handleSubmitSuccess(final ManagedTxn txn, final Response res) {
        if (txn.finalizedOrResponseIsToPriorSubmission(res)) {
            return;
        }
        EngineResult ter = res.engineResult();
        final UInt32 submitSequence = res.getSubmitSequence();
        switch (ter) {
            case tesSUCCESS:
                txn.emit(ManagedTxn.OnSubmitSuccess.class, res);
                return;
            case tefPAST_SEQ:
                resubmitWithNewSequence(txn);
                break;
            case tefMAX_LEDGER:
                resubmit(txn, submitSequence);
                break;
            case terPRE_SEQ:
                on(OnValidatedSequence.class, new OnValidatedSequence() {
                    @Override
                    public void called(UInt32 sequence) {
                        if (txn.finalizedOrResponseIsToPriorSubmission(res)) {
                            removeListener(OnValidatedSequence.class, this);
                        } else {
                            if (sequence.equals(submitSequence)) {
                                // resubmit:
                                resubmit(txn, submitSequence);
                                removeListener(OnValidatedSequence.class, this);
                            }
                        }
                    }
                });
                break;
            case telINSUF_FEE_P:
                resubmit(txn, submitSequence);
                break;
            case tefALREADY:
                // We only get this if we are submitting with exact same transactionID
                // Do nothing, the transaction has already been submitted
                break;
            default:
                switch (ter.resultClass()) {
                    case tecCLAIM:
                        // Sequence was consumed, do nothing
                        finalizeTxnAndRemoveFromQueue(txn);
                        txn.emit(ManagedTxn.OnSubmitFailure.class, res);
                        break;
                    // These are, according to the wiki, all of a final disposition
                    case temMALFORMED:
                    case tefFAILURE:
                    // TODO: Handle these with more panache
                    case telLOCAL_ERROR:
                    case terRETRY:
                        // TODO: txn.setAbortedAwaitingFinal();
                        finalizeTxnAndRemoveFromQueue(txn);
                        if (getPending().isEmpty()) {
                            sequence--;
                        } else {
                            // Plug a Sequence gap and pre-emptively resubmit some txns
                            // rather than waiting for `OnValidatedSequence` which will take
                            // quite some ledgers.
                            queueSequencePlugTxn(submitSequence);
                            resubmitGreaterThan(submitSequence);
                        }
                        txn.emit(ManagedTxn.OnSubmitFailure.class, res);
                        break;

                }
                break;
        }
    }

    private void resubmitGreaterThan(UInt32 submitSequence) {
        for (ManagedTxn txn : getPending()) {
            if (txn.sequence().compareTo(submitSequence) == 1) {
                resubmitWithSameSequence(txn);
            }
        }
    }

    private void queueSequencePlugTxn(UInt32 sequence) {
        ManagedTxn plug = manage(new AccountSet());
        plug.setSequencePlug(true);
        queue(plug, sequence);
    }

    public void finalizeTxnAndRemoveFromQueue(ManagedTxn transaction) {
        transaction.setFinalized();
        getPending().remove(transaction);
    }

    private void resubmitFirstTransactionWithTakenSequence(UInt32 sequence) {
        for (ManagedTxn txn : getPending()) {
            if (txn.sequence().compareTo(sequence) == 0) {
                resubmitWithNewSequence(txn);
                break;
            }
        }
    }
    // We only EVER resubmit a txn with a new Sequence if we have actually
    // seen that the Sequence has been consumed by a transaction we didn't
    // submit ourselves.
    // This is the method that handles that,
    private void resubmitWithNewSequence(final ManagedTxn txn) {
        // A sequence plug's sole purpose is to plug a Sequence
        // so that transactions may clear.
        if (txn.isSequencePlug()) {
            // The sequence has already been plugged (somehow)
            // So:
            return; // without further ado.
        }

        // ONLY ONLY ONLY if we've actually seen the Sequence
        if (txnNotFinalizedAndSeenValidatedSequence(txn)) {
            resubmit(txn, locallyPreemptedSubmissionSequence());
        } else {
            // requesting account_tx now and then (as we do) should ensure that
            // this doesn't stall forever. We'll either finalize the transaction
            // or Sequence will be seen to have been consumed by another txn.
            on(OnValidatedSequence.class,
                new CallbackContext() {
                    @Override
                    public boolean shouldExecute() {
                        return !txn.isFinalized();
                    }

                    @Override
                    public boolean shouldRemove() {
                        return txn.isFinalized();
                    }
                },
                new OnValidatedSequence() {
                    @Override
                    public void called(UInt32 uInt32) {
                        // Again, just to be safe.
                        if (txnNotFinalizedAndSeenValidatedSequence(txn)) {
                            resubmit(txn, locallyPreemptedSubmissionSequence());
                        }
                    }
            });
        }
    }

    private void resubmit(ManagedTxn txn, UInt32 sequence) {
//        if (txn.abortedAwaitingFinal()) {
//            return;
//        }
        makeSubmitRequest(txn, sequence);
    }

    private void resubmitWithSameSequence(ManagedTxn txn) {
        UInt32 previouslySubmitted = txn.sequence();
        resubmit(txn, previouslySubmitted);
    }

    public ManagedTxn manage(Transaction tt) {
        ManagedTxn txn = new ManagedTxn(tt);
        tt.account(accountID);
        return txn;
    }

    public void notifyTransactionResult(TransactionResult tr) {
        if (!tr.validated || !(tr.initiatingAccount().equals(accountID))) {
            return;
        }
        UInt32 txnSequence = tr.txn.get(UInt32.Sequence);
        seenValidatedSequences.add(txnSequence.longValue());

        ManagedTxn txn = submittedTransactionForHash(tr.hash);
        if (txn != null) {
            // TODO: icky
            // A result doesn't have a ManagedTxn
            // a ManagedTxn has a result
//            tr.submittedTransaction = txn;
            finalizeTxnAndRemoveFromQueue(txn);
            txn.emit(ManagedTxn.OnTransactionValidated.class, tr);
        } else {
            // TODO Check for transaction malleability, by computing a signing hash
            // for each.
            // preempt the terPRE_SEQ
            resubmitFirstTransactionWithTakenSequence(txnSequence);
            // Some transactions are waiting on this event before resubmission
            emit(OnValidatedSequence.class, txnSequence.add(new UInt32(1)));
        }
    }

    private ManagedTxn submittedTransactionForHash(Hash256 hash) {
        for (ManagedTxn transaction : getPending()) {
            if (transaction.wasSubmittedWith(hash)) {
                return transaction;
            }
        }
        return null;
    }
}
