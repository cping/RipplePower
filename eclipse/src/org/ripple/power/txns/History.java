package org.ripple.power.txns;

import org.json.JSONArray;
import org.json.JSONObject;
import org.ripple.power.utils.HttpRequest;

public class History {

	public static String def_historyApi = "https://history-dev.ripple.com:7443/v1";

	private String _baseUrl;

	private String _account;

	public History(String url, String account) {
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

	public History(String account) {
		this(def_historyApi, account);
	}

	public JSONObject getHistory() {
		String url = _baseUrl + "accounts/" + this._account + "/transactions";
		String result = HttpRequest.getHttps(url);
		if (result != null) {
			return new JSONObject(result);
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
