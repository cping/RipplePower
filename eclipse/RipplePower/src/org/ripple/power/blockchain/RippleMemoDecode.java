package org.ripple.power.blockchain;

import org.json.JSONObject;
import org.ripple.power.CoinUtils;
import org.ripple.power.config.LSystem;
import org.ripple.power.txns.TransactionTx;
import org.ripple.power.utils.Base64Coder;
import org.ripple.power.utils.StringUtils;
import org.ripple.power.wallet.OpenSSL;

import com.ripple.core.coretypes.RippleDate;

public class RippleMemoDecode {

	private JSONObject json;

	private String dataHash;

	private String typeHash;

	private String account;

	private String modeName;

	private String data;

	private String type;

	public long date = -1;

	public RippleMemoDecode(String account, JSONObject obj, long date, String password) {
		this.json = obj;
		this.date = date;
		this.account = account;
		TransactionTx.Memo tx_memo = new TransactionTx.Memo(obj, date);
		if (tx_memo.memo_format != null) {
			modeName = convertBase64(tx_memo.memo_format);
		}
		this.typeHash = tx_memo.memo_type;
		this.dataHash = tx_memo.memo_data;
		if (tx_memo.memo_format == null || tx_memo.memo_data == null || tx_memo.memo_type == null) {
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
				data = convertHash(tx_memo.memo_data);
				type = convertHash(tx_memo.memo_type);
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
		}
		if (tx_memo.memo_type != null && type == null) {
			data = convertBase64(tx_memo.memo_type);
		}
		if (tx_memo.memo_data != null && data == null) {
			data = convertBase64(tx_memo.memo_data);
		}
	}

	public JSONObject getJson() {
		return json;
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

	public String getDataHash() {
		return dataHash;
	}

	public String getTypeHash() {
		return typeHash;
	}

	public String getAccount() {
		return account;
	}

	public String getModeName() {
		return modeName;
	}

	public String getData() {
		return data;
	}

	public String getType() {
		return type;
	}

	private String wrap(String res, int limit) {
		if (res.length() <= limit) {
			return res;
		}
		char[] chars = res.toCharArray();
		StringBuilder sbr = new StringBuilder();
		final int size = chars.length;
		for (int i = 0; i < size; i++) {
			sbr.append(chars[i]);
			if (i != 0 && i % limit == 0) {
				sbr.append("<br>");
			}
		}
		return sbr.toString();
	}

	public String toHTML() {
		StringBuilder sbr = new StringBuilder();
		sbr.append("<font size=3 color=red>Account </font>");
		sbr.append(account);
		sbr.append("<br>");
		sbr.append("<font size=3 color=red>Date </font>");
		sbr.append(RippleDate.fromSecondsSinceRippleEpoch(date).getTimeString());
		sbr.append("<br>");
		if (type != null) {
			sbr.append("<font size=3 color=red>Type </font>");
			sbr.append(type.toUpperCase());
			sbr.append(" ");
		}
		sbr.append("<font size=3 color=red>Mode </font>");
		sbr.append(modeName.toUpperCase());
		sbr.append("<br>");
		sbr.append("<font size=3 color=red>Type Hash </font>");
		sbr.append("<br>");
		if (typeHash != null) {
			sbr.append(wrap(typeHash, 60));
			sbr.append("<br>");
		}
		sbr.append("<font size=3 color=red>Data Hash </font>");
		sbr.append("<br>");
		if (dataHash != null) {
			sbr.append(wrap(dataHash, 60));
			sbr.append("<br>");
		}
		sbr.append("<font size=4 color=orange>");
		String html = data;
		html = StringUtils.replaceIgnoreCase(html, "\n", "<br>");
		html = StringUtils.replaceIgnoreCase(html, "\r", "<br>");
		sbr.append(html);
		sbr.append("</font>");
		return sbr.toString();
	}

}
