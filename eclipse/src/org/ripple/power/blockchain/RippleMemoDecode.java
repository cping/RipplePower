package org.ripple.power.blockchain;

import org.address.utils.CoinUtils;
import org.json.JSONObject;
import org.ripple.power.config.LSystem;
import org.ripple.power.txns.TransactionTx;
import org.ripple.power.utils.Base64Coder;
import org.ripple.power.wallet.OpenSSL;

public class RippleMemoDecode {

	public String account;
	
	public String modeName;

	public String data;

	public String type;

	public long date = -1;

	public RippleMemoDecode(String account,JSONObject obj, long date, String password) {
		this.account = account;
		TransactionTx.Memo tx_memo = new TransactionTx.Memo(obj, date);
		if (tx_memo.memo_format != null) {
			modeName = convertBase64(tx_memo.memo_format);
		}
		if (tx_memo.memo_format == null || tx_memo.memo_data == null
				|| tx_memo.memo_type == null) {
			modeName = "UNKOWN";
			if (tx_memo.memo_data == null) {
				data = "UNKOWN";
			} else {
				data = convertBase64(tx_memo.memo_data);
			}
			if (tx_memo.memo_type == null) {
				type = "UNKOWN";
			} else {
				type = convertBase64(tx_memo.memo_type);
			}
		} else {
			switch (modeName) {
			case "UNKOWN":
			case "NONE":
				data = convertHash(tx_memo.memo_data);
				type = convertHash(tx_memo.memo_type);
				break;
			case "BASE64":
				data = convertBase64(tx_memo.memo_data);
				type = convertBase64(tx_memo.memo_type);
				break;
			case "ENCODE":
				if (password == null) {
					data = convertBase64(tx_memo.memo_data);
					type = convertBase64(tx_memo.memo_type);
				} else {
					data = convertEncode(tx_memo.memo_data, password);
					type = convertEncode(tx_memo.memo_type, password);
				}
				break;
			}
			this.date = date;
		}
	}

	private static String convertEncode(String res, String password) {
		byte[] result = CoinUtils.fromHex(res);
		if (Base64Coder.isArrayByteBase64(result)) {
			byte[] buffer = Base64Coder.decode(CoinUtils.fromHex(res));
			try {
				buffer = new OpenSSL().decrypt(buffer, password);
				return new String(buffer, LSystem.encoding);
			} catch (Exception e) {
				return new String(buffer);
			}
		} else {
			try {
				return new String(result, LSystem.encoding);
			} catch (Exception e) {
				return new String(result);
			}
		}
	}

	private static String convertBase64(String res) {
		byte[] result = CoinUtils.fromHex(res);
		if (Base64Coder.isArrayByteBase64(result)) {
			byte[] buffer = Base64Coder.decode(CoinUtils.fromHex(res));
			try {
				return new String(buffer, LSystem.encoding);
			} catch (Exception e) {
				return new String(buffer);
			}
		} else {
			try {
				return new String(result, LSystem.encoding);
			} catch (Exception e) {
				return new String(result);
			}
		}
	}

	private static String convertHash(String res) {
		byte[] result = CoinUtils.fromHex(res);
		try {
			return new String(result, LSystem.encoding);
		} catch (Exception e) {
			return new String(result);
		}
	}

	public long getDate() {
		return date;
	}
}
