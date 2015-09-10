package org.ripple.power.txns;

import java.util.ArrayList;

import org.ripple.power.txns.data.Take;

public class RippleDefault {

	private final static ArrayList<Take> _caches = new ArrayList<Take>(10);
	
	public final static Take BASE = new Take();
	
	public final static Pair BitstampUSD = new Pair(new Take(
			"USD", "rvYAfWj5gh67oV6fW32ZzP3Aw4Eubs59B"), BASE);

	public final static Pair BitstampBTC = new Pair(new Take(
			"BTC", "rvYAfWj5gh67oV6fW32ZzP3Aw4Eubs59B"), BASE);

	public final static Pair RippleCNCNY = new Pair(new Take(
			"CNY", "rnuF96W4SZoCJmbHYBFoJZpR8eCaxNvekK"), BASE);

	public final static Pair RippleChinaCNY = new Pair(
			new Take("CNY", "razqQKzJRdB4UxFPWf5NEpEG3WMkmwgcXA"), BASE);

	public final static Pair RippleFoxCNY = new Pair(new Take(
			"CNY", "rKiCet8SdvWxPXnAgYarFUXMh1zCPz432Y"), BASE);

	public final static Pair SnapSwapUSD = new Pair(new Take(
			"USD", "rMwjYedjc7qqtKYVLiAccJSmCwih4LnE2q"), BASE);

	public final static Pair SnapSwapEUR = new Pair(new Take(
			"EUR", "rMwjYedjc7qqtKYVLiAccJSmCwih4LnE2q"), BASE);

	public final static Pair SnapSwapBTC = new Pair(new Take(
			"BTC", "rMwjYedjc7qqtKYVLiAccJSmCwih4LnE2q"), BASE);

	public final static Pair TokyoJPYJPY = new Pair(new Take(
			"JPY", "r94s8px6kSw1uZ1MV98dhSRTvc6VMPoPcN"), BASE);

	public final static Pair DigitalGateJapanJPY = new Pair(
			new Take("JPY", "rJRi8WW24gt9X85PHAxfWNPCizMMhqUQwg"), BASE);

	public final static Pair RippleExchangeTokyoJPYJPY = new Pair(
			new Take("JPY", "r9ZFPSb1TFdnJwbTMYHvVwFK1bQPUCVNfJ"), BASE);

	public final static Pair RippleFoxSTR = new Pair(new Take(
			"STR", "rKiCet8SdvWxPXnAgYarFUXMh1zCPz432Y"), BASE);

	public final static Pair RippleFoxFMM = new Pair(new Take(
			"FMM", "rKiCet8SdvWxPXnAgYarFUXMh1zCPz432Y"), BASE);

	public final static Pair BitsoMXN = new Pair(new Take(
			"MXN", "rG6FZ31hDHN1K5Dkbma3PSB5uVCuVVRzfn"), BASE);

	public final static Pair BitsoBTC = new Pair(new Take(
			"BTC", "rG6FZ31hDHN1K5Dkbma3PSB5uVCuVVRzfn"), BASE);

	public final static Pair SnapswapEUR_SnapswapUSD = new Pair(
			new Take("EUR", "rMwjYedjc7qqtKYVLiAccJSmCwih4LnE2q"),
			new Take("USD", "rMwjYedjc7qqtKYVLiAccJSmCwih4LnE2q"));

	public final static Pair SnapswapBTC_SnapswapUSD = new Pair(
			new Take("BTC", "rMwjYedjc7qqtKYVLiAccJSmCwih4LnE2q"),
			new Take("USD", "rMwjYedjc7qqtKYVLiAccJSmCwih4LnE2q"));

	public final static Pair BitstampBTC_BitstampUSD = new Pair(
			new Take("BTC", "rvYAfWj5gh67oV6fW32ZzP3Aw4Eubs59B"), new Take(
					"USD", "rvYAfWj5gh67oV6fW32ZzP3Aw4Eubs59B"));

	public final static Pair BitstampBTC_SnapswapBTC = new Pair(
			new Take("BTC", "rvYAfWj5gh67oV6fW32ZzP3Aw4Eubs59B"), new Take(
					"BTC", "rMwjYedjc7qqtKYVLiAccJSmCwih4LnE2q"));

	public final static Pair BitstampUSD_SnapswapUSD = new Pair(
			new Take("USD", "rvYAfWj5gh67oV6fW32ZzP3Aw4Eubs59B"), new Take(
					"USD", "rMwjYedjc7qqtKYVLiAccJSmCwih4LnE2q"));

	public final static Pair BitstampUSD_RippleCNCNY = new Pair(
			new Take("USD", "rvYAfWj5gh67oV6fW32ZzP3Aw4Eubs59B"), new Take(
					"CNY", "rnuF96W4SZoCJmbHYBFoJZpR8eCaxNvekK"));

	public final static Pair BitstampUSD_RippleChinaCNY = new Pair(
			new Take("USD", "rvYAfWj5gh67oV6fW32ZzP3Aw4Eubs59B"), new Take(
					"CNY", "razqQKzJRdB4UxFPWf5NEpEG3WMkmwgcXA"));

	public final static Pair BitstampUSD_RippleFoxCNY = new Pair(
			new Take("USD", "rvYAfWj5gh67oV6fW32ZzP3Aw4Eubs59B"), new Take(
					"CNY", "rKiCet8SdvWxPXnAgYarFUXMh1zCPz432Y"));

	public final static Pair SnapswapUSD_RippleFoxCNY = new Pair(
			new Take("USD", "rMwjYedjc7qqtKYVLiAccJSmCwih4LnE2q"),
			new Take("CNY", "rKiCet8SdvWxPXnAgYarFUXMh1zCPz432Y"));

	public final static Pair SnapswapUSD_RippleFoxFMM = new Pair(
			new Take("USD", "rMwjYedjc7qqtKYVLiAccJSmCwih4LnE2q"),
			new Take("FMM", "rKiCet8SdvWxPXnAgYarFUXMh1zCPz432Y"));

	public final static Pair TokyoJPYJPY_RippleFoxCNY= new Pair(
			new Take("JPY", "r94s8px6kSw1uZ1MV98dhSRTvc6VMPoPcN"),
			new Take("CNY", "rKiCet8SdvWxPXnAgYarFUXMh1zCPz432Y"));
	
	public final static Pair TokyoJPYJPY_RippleFoxFMM = new Pair(
			new Take("JPY", "r94s8px6kSw1uZ1MV98dhSRTvc6VMPoPcN"),
			new Take("FMM", "rKiCet8SdvWxPXnAgYarFUXMh1zCPz432Y"));
	

	public final static Pair TokyoJPYJPY_SnapswapBTC = new Pair(
			new Take("JPY", "r94s8px6kSw1uZ1MV98dhSRTvc6VMPoPcN"),
			new Take("BTC", "rMwjYedjc7qqtKYVLiAccJSmCwih4LnE2q"));
	
	public final static Pair TokyoJPYJPY_SnapswapUSD = new Pair(
			new Take("JPY", "r94s8px6kSw1uZ1MV98dhSRTvc6VMPoPcN"),
			new Take("USD", "rMwjYedjc7qqtKYVLiAccJSmCwih4LnE2q"));
	

	public final static Pair TokyoJPYJPY_BitstampUSD = new Pair(
			new Take("JPY", "r94s8px6kSw1uZ1MV98dhSRTvc6VMPoPcN"),
			new Take("USD", "rvYAfWj5gh67oV6fW32ZzP3Aw4Eubs59B"));
	

	public final static Pair BitsoMXN_SnapswapUSD = new Pair(
			new Take("MXN", "rG6FZ31hDHN1K5Dkbma3PSB5uVCuVVRzfn"),
			new Take("USD", "rMwjYedjc7qqtKYVLiAccJSmCwih4LnE2q"));


	public final static ArrayList<Take> findCurrency(String c) {
		if (c == null) {
			return new ArrayList<Take>();
		}
		ArrayList<Take> takes = new ArrayList<Take>();
		for (int i = 0; i < _caches.size(); i++) {
			Take take = _caches.get(i);
			if (c.equalsIgnoreCase(take.currency)) {
				takes.add(take);
			}
		}
		return takes;
	}

	public static class Pair {

		protected Take base;

		protected Take counter;

		public Pair(Take b, Take c) {
			this.base = b;
			this.counter = c;
			if (!_caches.contains(b)) {
				_caches.add(b);
			}
			if (!_caches.contains(c)) {
				_caches.add(c);
			}
		}

		public Take getBase() {
			return base;
		}

		public Take getCounter() {
			return counter;
		}

	}

}
