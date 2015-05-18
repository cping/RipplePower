package org.ripple.power.txns;

public class BTC2Ripple {

	public static String def_bitcoin_bridge = "https://www.btc2ripple.com";

	// rMwjYedjc7qqtKYVLiAccJSmCwih4LnE2qBTC
	public static String def_b2rAddress = "rMwjYedjc7qqtKYVLiAccJSmCwih4LnE2q";

	private String _recipient;

	private String _baseUrl;

	public BTC2Ripple(String bitcoinAddress) {
		this(def_bitcoin_bridge, bitcoinAddress);
	}

	public BTC2Ripple(String url, String bitcoinAddress) {
		if (!AccountFind.isBitcoinAddress(bitcoinAddress)) {
			throw new RuntimeException(bitcoinAddress
					+ " not bitcoin address !");
		}
		_recipient = bitcoinAddress + '@' + (_baseUrl = def(url));
	}

	private final static String def(String bridge) {
		if (bridge.toLowerCase().contains("btc2ripple")) {
			return "btc2ripple.com";
		}
		return null;
	}

	public String getRecipient() {
		return _recipient;
	}

	public String getBaseUrl() {
		return _baseUrl;
	}
}
