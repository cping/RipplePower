package org.ripple.power.txns.btc;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Locale;

import org.json.JSONArray;
import org.json.JSONObject;
import org.ripple.power.utils.HttpRequest;

//not included in China can not be linked site
public class BTCStoreQuery {

	private final static ArrayList<BTCPrice> _usdList = new ArrayList<BTCPrice>(
			40);
	private final static ArrayList<BTCPrice> _cnyList = new ArrayList<BTCPrice>(
			40);
	private final static ArrayList<BTCPrice> _jpyList = new ArrayList<BTCPrice>(
			40);
	private final static ArrayList<BTCPrice> _eurList = new ArrayList<BTCPrice>(
			40);
	private final static ArrayList<BTCPrice> _cadList = new ArrayList<BTCPrice>(
			40);

	static {
		_cnyList.add(new BTCPrice(BTCStore.LAKEBTC));
		_cnyList.add(new BTCPrice(BTCStore.JUSTCOIN));
		_cnyList.add(new BTCPrice(BTCStore.BTC_CHINA));
		_cnyList.add(new BTCPrice(BTCStore.COINBASE));
		_cnyList.add(new BTCPrice(BTCStore.HUOBI));
		_cnyList.add(new BTCPrice(BTCStore.OKCOIN));
		_cnyList.add(new BTCPrice(BTCStore.BITCOIN_AVERAGE));
		_cnyList.add(new BTCPrice(BTCStore.BITCOIN_AVERAGE_GLOBAL));

		_jpyList.add(new BTCPrice(BTCStore.COINBASE));
		_jpyList.add(new BTCPrice(BTCStore.BITCOIN_AVERAGE_GLOBAL));
		_jpyList.add(new BTCPrice(BTCStore.BITPAY));
		_jpyList.add(new BTCPrice(BTCStore.JUSTCOIN));
		_jpyList.add(new BTCPrice(BTCStore.KRAKEN));

		_usdList.add(new BTCPrice(BTCStore.BITSTAMP));
		_usdList.add(new BTCPrice(BTCStore.COINBASE));
		_usdList.add(new BTCPrice(BTCStore.OKCOIN));
		_usdList.add(new BTCPrice(BTCStore.BITCOIN_AVERAGE));
		_usdList.add(new BTCPrice(BTCStore.BITCUREX));
		_usdList.add(new BTCPrice(BTCStore.BITFINEX));
		_usdList.add(new BTCPrice(BTCStore.BITCOIN_AVERAGE_GLOBAL));
		_usdList.add(new BTCPrice(BTCStore.BITPAY));
		_usdList.add(new BTCPrice(BTCStore.KRAKEN));
		_usdList.add(new BTCPrice(BTCStore.JUSTCOIN));
		_usdList.add(new BTCPrice(BTCStore.LAKEBTC));
		_usdList.add(new BTCPrice(BTCStore.CRYPTSY));
		_usdList.add(new BTCPrice(BTCStore.BITBAY));
		_usdList.add(new BTCPrice(BTCStore.CEXIO));
		_usdList.add(new BTCPrice(BTCStore.ITBIT));
		_usdList.add(new BTCPrice(BTCStore.INDEPENDENT_RESERVER));
		_usdList.add(new BTCPrice(BTCStore.QUADRIGA));

		_eurList.add(new BTCPrice(BTCStore.COINBASE));
		_eurList.add(new BTCPrice(BTCStore.BITCOIN_AVERAGE));
		_eurList.add(new BTCPrice(BTCStore.BITCUREX));
		_eurList.add(new BTCPrice(BTCStore.BITCOIN_AVERAGE_GLOBAL));
		_eurList.add(new BTCPrice(BTCStore.BITPAY));
		_eurList.add(new BTCPrice(BTCStore.KRAKEN));
		_eurList.add(new BTCPrice(BTCStore.JUSTCOIN));
		_eurList.add(new BTCPrice(BTCStore.CEXIO));
		_eurList.add(new BTCPrice(BTCStore.ITBIT));
		_eurList.add(new BTCPrice(BTCStore.PAYMIUM));
		_eurList.add(new BTCPrice(BTCStore.ZYADO));
		_eurList.add(new BTCPrice(BTCStore.CLEVERCOIN));

		_cadList.add(new BTCPrice(BTCStore.COINBASE));
		_cadList.add(new BTCPrice(BTCStore.BITCOIN_AVERAGE));
		_cadList.add(new BTCPrice(BTCStore.BITCOIN_AVERAGE_GLOBAL));
		_cadList.add(new BTCPrice(BTCStore.BITPAY));
		_cadList.add(new BTCPrice(BTCStore.JUSTCOIN));
		_cadList.add(new BTCPrice(BTCStore.QUADRIGA));
		_cadList.add(new BTCPrice(BTCStore.VIRTEX));
	}

	final static String BITSTAMP = "https://www.bitstamp.net/api/ticker/";
	final static String COINBASE = "https://coinbase.com/api/v1/prices/spot_rate?currency=%s";
	final static String BITCOIN_AVERAGE = "https://api.bitcoinaverage.com/ticker/%s";
	final static String BITCOINDE = "https://bitcoinapi.de/widget/current-btc-price/rate.json";
	final static String BITCUREX = "https://bitcurex.com/api/%s/ticker.json";
	final static String BITFINEX = "https://api.bitfinex.com/v1/ticker/btcusd";
	final static String BITCOIN_AVERAGE_GLOBAL = "https://api.bitcoinaverage.com/ticker/global/%s";
	final static String BTC_CHINA = "https://data.btcchina.com/data/ticker?market=btccny";
	final static String BITPAY = "https://bitpay.com/api/rates";
	final static String KRAKEN = "https://api.kraken.com/0/public/Ticker?pair=XBT%s";
	final static String BTCTURK = "https://www.btcturk.com/api/ticker";
	final static String VIRTEX = "https://cavirtex.com/api2/ticker.json";
	final static String JUSTCOIN = "https://justcoin.com/api/2/BTC%s/money/ticker";
	final static String LAKEBTC = "https://www.lakebtc.com/api_v1/ticker";
	final static String CRYPTONIT = "http://cryptonit.net/apiv2/rest/public/ccorder.json?bid_currency=usd&ask_currency=btc&ticker";
	final static String COINTREE = "https://www.cointree.com.au/api/price/btc/aud";
	final static String BTCMARKETS = "https://api.btcmarkets.net/market/BTC/AUD/tick";
	final static String HUOBI = "http://market.huobi.com/staticmarket/ticker_btc_json.js";
	final static String KORBIT = "https://api.korbit.co.kr/v1/ticker/detailed";
	final static String PAYMIUM = "https://paymium.com/api/v1/data/eur/ticker";
	final static String BITSO = "https://api.bitso.com/public/info";
	final static String ZYADO = "http://chart.zyado.com/ticker.json";
	final static String CRYPTSY = "https://www.cryptsy.com/trades/ajaxlasttrades";
	final static String BITBAY = "https://bitbay.net/API/Public/BTC%s/ticker.json";
	final static String CEXIO = "https://cex.io/api/last_price/BTC/%s";
	final static String HITBTC = "https://api.hitbtc.com/api/1/public/BTC%s/ticker";
	final static String ITBIT = "https://api.itbit.com/v1/markets/XBT%s/ticker";
	final static String BITCOINCOID = "https://vip.bitcoin.co.id/api/BTC_IDR/ticker/";
	final static String FOXBIT = "https://api.blinktrade.com/api/v1/BRL/ticker?crypto_currency=BTC";
	final static String INDEPENDENT_RESERVER = "https://api.independentreserve.com/Public/GetMarketSummary?primaryCurrencyCode=xbt&secondaryCurrencyCode=%s";
	final static String CLEVERCOIN = "https://api.clevercoin.com/v1/ticker";
	final static String BITMARKET24 = "https://bitmarket24.pl/api/BTC_PLN/status.json";
	final static String QUADRIGA = "https://api.quadrigacx.com/v2/ticker?book=BTC_%s";
	final static String GATECOIN = "https://www.gatecoin.com/api/Public/LiveTicker/BTC%s";
	final static String MEXBT = "https://data.mexbt.com/ticker/btc%s";
	final static String OKCOIN_USD = "https://www.okcoin.com/api/ticker.do?ok=1";
	final static String OKCOIN_CNY = "https://www.okcoin.cn/api/ticker.do?ok=1";

	private String _cur = "USD";

	public BTCStoreQuery(String cur) {
		this._cur = cur;
	}

	public String get(BTCStore store) {
		String result = "unkown";
		try {
			switch (store) {
			case BITSTAMP:
				result = HttpRequest.getHttps(BTCStoreQuery.BITSTAMP);
				if (result != null && result.indexOf("error") == -1) {
					JSONObject obj = new JSONObject(result);
					result = obj.optString("last");
				}
				break;
			case COINBASE:
				result = HttpRequest.getHttps(String.format(
						BTCStoreQuery.COINBASE, _cur));
				if (result != null && result.indexOf("error") == -1) {
					JSONObject obj = new JSONObject(result);
					result = obj.optString("amount");
				}
				break;
			case BITCOIN_AVERAGE:
				result = HttpRequest.getHttps(String.format(
						BTCStoreQuery.BITCOIN_AVERAGE, _cur));
				if (result != null && result.indexOf("error") == -1) {
					JSONObject obj = new JSONObject(result);
					result = obj.optString("last");
				}
				break;
			case BITCOINDE:
				result = HttpRequest.getHttps(BTCStoreQuery.BITCOINDE);
				if (result != null && result.indexOf("error") == -1) {
					JSONObject obj = new JSONObject(result);
					String price = obj.optString("price_eur");
					String[] amount = price.split("\\s");
					return amount[0].replaceAll(",", ".");
				}
				break;
			case BITCUREX:
				result = HttpRequest.getHttps(String.format(
						BTCStoreQuery.BITCUREX, _cur));
				if (result != null && result.indexOf("error") == -1) {
					JSONObject obj = new JSONObject(result);
					return obj.optString("last_tx_price_h");
				}
				break;
			case BITFINEX:
				result = HttpRequest.getHttps(BTCStoreQuery.BITFINEX);
				if (result != null && result.indexOf("error") == -1) {
					JSONObject obj = new JSONObject(result);
					return obj.optString("last_price");
				}
				break;
			case BITCOIN_AVERAGE_GLOBAL:
				result = HttpRequest.getHttps(String.format(
						BTCStoreQuery.BITCOIN_AVERAGE_GLOBAL, _cur));
				if (result != null && result.indexOf("error") == -1) {
					JSONObject obj = new JSONObject(result);
					return obj.optString("last");
				}
				break;
			case BTC_CHINA:
				result = HttpRequest.getHttps(BTCStoreQuery.BTC_CHINA);
				if (result != null && result.indexOf("error") == -1) {
					JSONObject obj = new JSONObject(result);
					return obj.optJSONObject("ticker").optString("last");
				}
				break;
			case BITPAY:
				result = HttpRequest.getHttps(BTCStoreQuery.BITPAY);
				if (result != null && result.indexOf("error") == -1) {
					JSONArray array = new JSONArray(result);
					for (int i = 0; i < array.length(); i++) {
						JSONObject obj = array.getJSONObject(i);
						if (_cur.equals(obj.getString("code"))) {
							return String.valueOf(obj.optDouble("rate"));
						}
					}
				}
				break;
			case KRAKEN:
				result = HttpRequest.getHttps(String.format(
						BTCStoreQuery.KRAKEN, _cur));
				if (result != null && result.indexOf("\"c\":") != -1) {
					JSONObject obj = new JSONObject(result);
					JSONObject obj2 = obj.optJSONObject("result")
							.optJSONObject("XXBTZ" + _cur);
					result = (String) obj2.optJSONArray("c").get(0);
				}
				break;
			case BTCTURK:
				result = HttpRequest.getHttps(BTCStoreQuery.BTCTURK);
				if (result != null && result.indexOf("error") == -1) {
					JSONObject obj = new JSONObject(result);
					result = obj.optString("last");
				}
				break;
			case VIRTEX:
				result = HttpRequest.getHttps(BTCStoreQuery.VIRTEX);
				if (result != null && result.indexOf("error") == -1) {
					JSONObject obj = new JSONObject(result);
					result = obj.optJSONObject("ticker")
							.optJSONObject("BTCCAD").optString("last");
				}
				break;
			case JUSTCOIN:
				result = HttpRequest.getHttps(String.format(
						BTCStoreQuery.JUSTCOIN, _cur));
				if (result != null && result.indexOf("error") == -1) {
					JSONObject obj = new JSONObject(result);
					result = obj.optJSONObject("data").optJSONObject("last")
							.optString("value");
				}
				break;
			case LAKEBTC:
				result = HttpRequest.getHttps(BTCStoreQuery.LAKEBTC);
				if (result != null && result.indexOf("error") == -1
						&& result.indexOf(_cur) != -1) {
					JSONObject obj = new JSONObject(result);
					result = String.valueOf(obj.optJSONObject(_cur).optDouble(
							"last"));
				}
				break;
			case CRYPTONIT:
				result = HttpRequest.getHttps(BTCStoreQuery.CRYPTONIT);
				if (result != null && result.indexOf("error") == -1) {
					JSONObject obj = new JSONObject(result);
					result = obj.optJSONObject("rate").optString("last");
				}
				break;
			case COINTREE:
				result = HttpRequest.getHttps(BTCStoreQuery.COINTREE);
				if (result != null && result.indexOf("error") == -1) {
					JSONObject obj = new JSONObject(result);
					result = obj.optString("Spot");
				}
				break;
			case BTCMARKETS:
				result = HttpRequest.getHttps(BTCStoreQuery.BTCMARKETS);
				if (result != null && result.indexOf("error") == -1) {
					JSONObject obj = new JSONObject(result);
					result = obj.optString("lastPrice");
				}
				break;
			case HUOBI:
				result = HttpRequest.getHttps(BTCStoreQuery.HUOBI);
				if (result != null && result.indexOf("error") == -1) {
					JSONObject obj = new JSONObject(result);
					result = obj.optJSONObject("ticker").optString("last");
				}
				break;
			case KORBIT:
				result = HttpRequest.getHttps(BTCStoreQuery.KORBIT);
				if (result != null && result.indexOf("error") == -1) {
					JSONObject obj = new JSONObject(result);
					result = obj.optString("last");
				}
				break;
			case PAYMIUM:
				result = HttpRequest.getHttps(BTCStoreQuery.PAYMIUM);
				if (result != null && result.indexOf("error") == -1) {
					JSONObject obj = new JSONObject(result);
					result = obj.optString("price");
				}
				break;
			case BITSO:
				result = HttpRequest.getHttps(BTCStoreQuery.BITSO);
				if (result != null && result.indexOf("error") == -1) {
					JSONObject obj = new JSONObject(result);
					result = obj.optJSONObject("btc_mxn").optString("rate");
				}
				break;
			case ZYADO:
				result = HttpRequest.getHttps(BTCStoreQuery.ZYADO);
				if (result != null && result.indexOf("error") == -1) {
					JSONObject obj = new JSONObject(result);
					result = obj.optString("last");
				}
				break;
			case CRYPTSY:
				result = HttpRequest.getHttps(BTCStoreQuery.CRYPTSY);
				if (result != null && result.indexOf("error") == -1) {
					JSONObject obj = new JSONObject(result);
					result = obj.optString("2");
				}
				break;
			case BITBAY:
				result = HttpRequest.getHttps(String.format(
						BTCStoreQuery.BITBAY, _cur));
				if (result != null && result.indexOf("error") == -1) {
					JSONObject obj = new JSONObject(result);
					result = obj.optString("last");
				}
				break;
			case CEXIO:
				result = HttpRequest.getHttps(String.format(
						"https://cex.io/api/last_price/BTC/%s", _cur));
				if (result != null && result.indexOf("error") == -1) {
					JSONObject obj = new JSONObject(result);
					result = obj.optString("lprice");
				}
				break;
			case OKCOIN:
				if ("cny".equalsIgnoreCase(_cur)) {
					result = HttpRequest.getHttps(BTCStoreQuery.OKCOIN_CNY);
				} else {
					result = HttpRequest.getHttps(BTCStoreQuery.OKCOIN_USD);
				}
				if (result != null && result.indexOf("error") == -1) {
					JSONObject obj = new JSONObject(result);
					result = obj.optJSONObject("ticker").optString("last");
				}
				break;
			case HITBTC:
				try {
					result = HttpRequest.fix_ssl_open(String.format(
							BTCStoreQuery.HITBTC, _cur));
				} catch (Exception e) {
					result = null;
				}
				if (result != null && result.indexOf("error") == -1) {
					JSONObject obj = new JSONObject(result);
					result = obj.optString("last");
				}
				break;
			case ITBIT:
				result = HttpRequest.getHttps(String.format(
						BTCStoreQuery.ITBIT, _cur));
				if (result != null && result.indexOf("error") == -1) {
					JSONObject obj = new JSONObject(result);
					result = obj.optString("lastPrice");
				}
				break;
			case BITCOINCOID:
				result = HttpRequest.getHttps(BTCStoreQuery.BITCOINCOID);
				if (result != null && result.indexOf("error") == -1) {
					JSONObject obj = new JSONObject(result);
					result = obj.optJSONObject("ticker").optString("last");
				}
				break;
			case FOXBIT:
				result = HttpRequest.getHttps(BTCStoreQuery.FOXBIT);
				if (result != null && result.indexOf("error") == -1) {
					JSONObject obj = new JSONObject(result);
					result = obj.optString("last");
				}
				break;
			case INDEPENDENT_RESERVER:
				result = HttpRequest.getHttps(String.format(
						BTCStoreQuery.INDEPENDENT_RESERVER, _cur));
				if (result != null && result.indexOf("error") == -1) {
					JSONObject obj = new JSONObject(result);
					result = obj.optString("LastPrice");
				}
				break;
			case CLEVERCOIN:
				result = HttpRequest.getHttps(BTCStoreQuery.CLEVERCOIN);
				if (result != null && result.indexOf("error") == -1) {
					JSONObject obj = new JSONObject(result);
					result = obj.optString("last");
				}
				break;
			case QUADRIGA:
				result = HttpRequest.getHttps(String.format(
						BTCStoreQuery.QUADRIGA, _cur));
				if (result != null && result.indexOf("error") == -1) {
					JSONObject obj = new JSONObject(result);
					result = obj.optString("last");
				}
				break;
			case GATECOIN:
				result = HttpRequest.getHttps(String.format(
						BTCStoreQuery.GATECOIN, _cur));
				if (result != null && result.indexOf("error") == -1) {
					JSONObject obj = new JSONObject(result);
					result = obj.optJSONObject("ticker").optString("last");
				}
				break;
			case MEXBT:
				result = HttpRequest.getHttps(String.format(
						BTCStoreQuery.MEXBT, _cur.toLowerCase(Locale.US)));
				if (result != null && result.indexOf("error") == -1) {
					JSONObject obj = new JSONObject(result);
					result = obj.optString("last");
				}
				break;
			default:
				break;
			}
		} catch (Throwable ex) {
			return result;
		}
		return result;
	}

	public static String getCurrency(String cur, BTCStore store) {
		return new BTCStoreQuery(cur).get(store);
	}

	public static String getCurrency(BTCStore store) {
		return getCurrency("USD", store);
	}

	public static ArrayList<BTCPrice> getPrices(String cur,
			ArrayList<BTCPrice> prices, BTCMonitor monitor, boolean sort) {
		ArrayList<BTCPrice> result = new ArrayList<BTCPrice>(prices.size());
		for (BTCPrice p : prices) {
			BTCPrice price = new BTCPrice(p.store, getCurrency(cur, p.store));
			if (monitor != null) {
				monitor.update(price);
			}
			result.add(price);
		}
		if (sort) {
			Collections.sort(result);
		}
		if (monitor != null) {
			monitor.end();
		}
		return result;
	}

	public static ArrayList<BTCPrice> getUSDPrices(BTCMonitor monitor,
			boolean sort) {
		return getPrices("USD", _usdList, monitor, sort);
	}

	public static ArrayList<BTCPrice> getCNYPrices(BTCMonitor monitor,
			boolean sort) {
		return getPrices("CNY", _cnyList, monitor, sort);
	}

	public static ArrayList<BTCPrice> getJPYPrices(BTCMonitor monitor,
			boolean sort) {
		return getPrices("JPY", _jpyList, monitor, sort);
	}

	public static ArrayList<BTCPrice> getEURPrices(BTCMonitor monitor,
			boolean sort) {
		return getPrices("EUR", _eurList, monitor, sort);
	}

	public static ArrayList<BTCPrice> getCADPrices(BTCMonitor monitor,
			boolean sort) {
		return getPrices("CAD", _cadList, monitor, sort);
	}

}
