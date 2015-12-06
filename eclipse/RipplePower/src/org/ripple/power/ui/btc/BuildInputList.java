package org.ripple.power.ui.btc;

import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import org.ripple.power.txns.btc.Address;
import org.ripple.power.txns.btc.BTCLoader;
import org.ripple.power.txns.btc.BlockStoreException;
import org.ripple.power.txns.btc.ECKey;
import org.ripple.power.txns.btc.OutPoint;
import org.ripple.power.txns.btc.ReceiveTransaction;
import org.ripple.power.txns.btc.SignedInput;

public class BuildInputList {

	static class ReceiveTransactionComparator implements Comparator<ReceiveTransaction>{

		@Override
		public int compare(ReceiveTransaction rcv1, ReceiveTransaction rcv2) {
			return  rcv1.getValue().compareTo(rcv2.getValue());
		}
		
	}
	
    public static List<SignedInput> buildSignedInputs() throws BlockStoreException {
        List<SignedInput> inputList = new LinkedList<SignedInput>();
        List<ReceiveTransaction> txList = BTCLoader.blockStore.getReceiveTxList();
        Iterator<ReceiveTransaction> it = txList.iterator();
        while (it.hasNext()) {
            ReceiveTransaction tx = it.next();
            if (tx.inSafe() || tx.isSpent()) {
                it.remove();
            } else {
                int depth = BTCLoader.blockStore.getTxDepth(tx.getTxHash());
                if ((tx.isCoinBase() && depth < BTCLoader.COINBASE_MATURITY) ||
                                    (!tx.isCoinBase() && depth < 1)) {
                    it.remove();
                }
            }
        }

        Collections.sort(txList,new ReceiveTransactionComparator());
       
        for (ReceiveTransaction rcvTx : txList) {
            Address outAddress = rcvTx.getAddress();
            ECKey key = null;
            for (ECKey chkKey : BTCLoader.keys) {
                if (Arrays.equals(chkKey.getPubKeyHash(), outAddress.getHash())) {
                    key = chkKey;
                    break;
                }
            }
            if (key == null){
                throw new BlockStoreException(String.format("No key available for transaction output\n  %s : %d",
                                                        rcvTx.getTxHash().toString(), rcvTx.getTxIndex()));
            }
            OutPoint outPoint = new OutPoint(rcvTx.getTxHash(), rcvTx.getTxIndex());
            SignedInput input = new SignedInput(key, outPoint, rcvTx.getValue(), rcvTx.getScriptBytes());
            inputList.add(input);
        }
        return inputList;
    }
}
