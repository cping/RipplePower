package org.ripple.power.hft;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;

import org.ripple.power.password.PasswordGeneratorArray;
import org.ripple.power.txns.NameFind;

public class TraderNxN {

	private ArrayList<String> _currencies = new ArrayList<String>(10);

	private HashMap<String, Float> _spreads = new HashMap<String, Float>();

	private HashMap<String, Float> _cross = new HashMap<String, Float>();

	private ArrayList<String> _issuerAddress = new ArrayList<String>();

	private float _price_warning = 0.015f, _price_adjust = 0.009f;

	/**
	 * default money
	 */
	public void defaultCurrencies() {
		_currencies.add("XRP");
		_currencies.add("USD");
		_currencies.add("BTC");
		_currencies.add("CNY");
		_currencies.add("JPY");
	}

	/**
	 * default gateway
	 */
	public void defaultIssuerAddress() {
		// Bitstamp
		_issuerAddress.add("rvYAfWj5gh67oV6fW32ZzP3Aw4Eubs59B");
		// SnapSwap
		_issuerAddress.add("rMwjYedjc7qqtKYVLiAccJSmCwih4LnE2q");
		// RippleCN
		_issuerAddress.add("rnuF96W4SZoCJmbHYBFoJZpR8eCaxNvekK");
		// RippleChina
		_issuerAddress.add("razqQKzJRdB4UxFPWf5NEpEG3WMkmwgcXA");
		// Ripple Trade Japan
		_issuerAddress.add("rMAz5ZnK73nyNUL4foAvaxdreczCkG3vA6");
	}

	/**
	 * default transaction spread
	 */
	public void defaultSpreads() {
		_spreads.put("XRP", 50f);
		_spreads.put("USD", 0.25f);
		_spreads.put("BTC", 0.005f);
		_spreads.put("CNY", 2f);
		_spreads.put("JPY", 30f);
	}

	/**
	 * default transaction base amount
	 */
	public void defaultAmountCross() {
		_cross.put("XRP", 1000f);
		_cross.put("USD", 5f);
		_cross.put("BTC", 0.1f);
		_cross.put("CNY", 35f);
		_cross.put("JPY", 700f);
	}

	public void putIssuerAddress(String address) {
		if (address.startsWith("~")) {
			try {
				address = NameFind.getAddress(address);
			} catch (Exception e) {
				return;
			}
		}
		_issuerAddress.add(address);
	}

	public void putSpreads(String name, float value) {
		_spreads.put(name, value);
	}

	public void putAmountCross(String name, float value) {
		_cross.put(name, value);
	}

	public HashSet<String> crossTx(char flag) {
		return crossTx(flag, _currencies.size());
	}

	public HashSet<String> crossTx(char flag, int length) {
		HashSet<String> list = new HashSet<String>();
		PasswordGeneratorArray arrays = new PasswordGeneratorArray(length,
				length, _currencies);
		arrays.setFlag(flag);
		arrays.setNot_repeat(true);
		String next = null;
		String strFlag = String.valueOf(flag);
		for (; (next = arrays.generateNextWord()) != null;) {
			if (next.indexOf(strFlag + strFlag) == -1
					&& !next.endsWith(strFlag)) {
				list.add(next);
			}
		}
		return list;
	}

	public TraderNxN() {
		defaultAmountCross();
		defaultCurrencies();
		defaultIssuerAddress();
		defaultSpreads();
	}

	public ArrayList<String> getCurrencies() {
		return _currencies;
	}

	public HashMap<String, Float> getSpreads() {
		return _spreads;
	}

	public HashMap<String, Float> getCross() {
		return _cross;
	}

	public ArrayList<String> getIssuerAddress() {
		return _issuerAddress;
	}

	public float getPriceWarning() {
		return _price_warning;
	}

	public float getPriceAdjust() {
		return _price_adjust;
	}

}
