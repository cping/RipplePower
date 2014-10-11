package org.ripple.power.txns;

import java.math.BigDecimal;

import org.ripple.power.ui.RPClient;

import com.ripple.client.transactions.ManagedTxn;
import com.ripple.core.types.known.tx.result.TransactionResult;
import com.ripple.core.types.known.tx.txns.Payment;
import org.json.JSONException;
import org.json.JSONObject;

import com.ripple.client.Account;
import com.ripple.client.responses.Response;
import com.ripple.client.transactions.TransactionManager;
import com.ripple.core.coretypes.AccountID;
import com.ripple.core.coretypes.Amount;
import com.ripple.core.coretypes.Currency;

public class PaymentSend {

	private Currency _currency;

	private String _currencyName;

	private AccountID _issuer;

	public PaymentSend() {
		this(null, null);
	}

	public PaymentSend(String cName, AccountID issuer) {
		if (cName != null) {
			_currencyName = _currencyName.toUpperCase();
			_currency = Currency.fromString(_currencyName);
		} else {
			_currencyName = "XRP";
			_currency = Currency.XRP;
		}
		if (issuer != null) {
			_issuer = issuer;
		}
	}

	public void makePayment(String seed, String dstAddress, String amount,
			Rollback back) {
		RPClient client = RPClient.ripple();
		if (client != null) {
			Account account = client.getClinet().accountFromSeed(seed);
			makePayment(account, dstAddress, amount, back);
		}
	}

	//PS:当向非本地Rippled请求需要使用签名的交易时，原则上应该使用TransactionManager产生，
	//而不能直接使用json方式向服务器发送Transaction请求(tx_json)，否则将意味着明文上传私钥。
	private void makePayment(Account account, String destination, String amt,
			final Rollback back) {
		TransactionManager tm = account.transactionManager();
		Payment payment = new Payment();
		ManagedTxn tx = tm.manage(payment);
		payment.putTranslated(AccountID.Destination, destination);
		if ("XRP".equals(_currencyName)) {
			payment.putTranslated(Amount.Amount, amt);
		} else {
			if (_issuer != null) {
				Amount amount = new Amount(new BigDecimal(amt), _currency,
						_issuer);
				payment.amount(amount);
			} else {
				back.error("not find issuer!");
			}
		}
		tx.once(ManagedTxn.OnSubmitSuccess.class,
				new ManagedTxn.OnSubmitSuccess() {
					@Override
					public void called(Response response) {
						back.success(response.engineResult().name());
					}
				});
		tx.once(ManagedTxn.OnSubmitFailure.class,
				new ManagedTxn.OnSubmitFailure() {
					@Override
					public void called(Response response) {
						back.failure(response.engineResult().name());
					}
				});
		tx.once(ManagedTxn.OnSubmitError.class, new ManagedTxn.OnSubmitError() {
			@Override
			public void called(Response response) {
				back.error(response.rpcerr.name());
			}
		});
		tx.once(ManagedTxn.OnTransactionValidated.class,
				new ManagedTxn.OnTransactionValidated() {
					@Override
					public void called(TransactionResult result) {
						back.validated(prettyJSON(result.message));
					}
				});
		tm.queue(tx);

	}

	private static String prettyJSON(JSONObject message) {
		try {
			return message.toString(4);
		} catch (JSONException e) {
			throw new RuntimeException(e);
		}
	}
}
