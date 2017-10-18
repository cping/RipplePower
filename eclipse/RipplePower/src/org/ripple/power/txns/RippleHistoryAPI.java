package org.ripple.power.txns;

import org.json.JSONArray;
import org.json.JSONObject;
import org.ripple.power.utils.HttpRequest;

public class RippleHistoryAPI {

	public static String def_historyApi = "https://history.ripple.com/v1/";

	private String _baseUrl;

	private String _account;

	public RippleHistoryAPI(String url, String account) {
		_baseUrl = url;
		if (!_baseUrl.endsWith("/")) {
			_baseUrl += "/";
		}
		if (!AccountFind.isRippleAddress(account)) {
			try {
				_account = NameFind.getAddress(account);
			} catch (Exception e) {
				throw new RuntimeException("address does not exist !");
			}
		} else {
			_account = account;
		}
	}

	public RippleHistoryAPI(String account) {
		this(def_historyApi, account);
	}

	public JSONObject getHistory() {
		String url = _baseUrl + "accounts/" + this._account + "/transactions";
		String result;
		try {
			result = HttpRequest.fix_ssl_open(url);
			if (result != null) {
				return new JSONObject(result);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	public JSONArray transactions() {
		JSONObject obj = getHistory();
		if (obj != null && obj.has("transactions")) {
			return obj.getJSONArray("transactions");
		}
		return null;
	}

	public String getUrl() {
		return _baseUrl;
	}

	public String getAccount() {
		return _account;
	}

}
