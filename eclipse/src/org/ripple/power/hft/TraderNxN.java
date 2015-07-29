package org.ripple.power.hft;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;

import org.json.JSONObject;
import org.ripple.power.RippleAddress;
import org.ripple.power.RippleSeedAddress;
import org.ripple.power.config.LSystem;
import org.ripple.power.password.PasswordGeneratorArray;
import org.ripple.power.txns.Currencies.Item;
import org.ripple.power.txns.Gateway;
import org.ripple.power.txns.NameFind;
import org.ripple.power.txns.OfferPrice;
import org.ripple.power.txns.OfferPrice.OfferFruit;
import org.ripple.power.txns.RippleItem;
import org.ripple.power.txns.RippleChartsAPI;
import org.ripple.power.utils.MathUtils;
import org.ripple.power.utils.StringUtils;
import org.ripple.power.wallet.WalletItem;

import com.ripple.core.coretypes.Amount;
import com.ripple.core.types.known.sle.entries.Offer;

public class TraderNxN {

	private ArrayList<WalletItem> _myCurrency = new ArrayList<WalletItem>(10);

	private ArrayList<String> _currencies = new ArrayList<String>(10);

	private HashMap<String, Float> _spreads = new HashMap<String, Float>();

	private HashMap<String, Float> _limit_max_cross = new HashMap<String, Float>();

	private HashMap<String, Float> _limit_min_cross = new HashMap<String, Float>();

	private ArrayList<String> _issuerAddress = new ArrayList<String>();

	private float _price_warning = 0.015f, _price_adjust = 0.009f;

	private static HashMap<String, Integer> _cache_count = new HashMap<String, Integer>(
			10);

	private int _sleep = 10;

	// 为了多网关高频交易方便换算计量单位，测试过几种方式后确认只有使用本地(Native)货币作为计量才是最高效的，具体到Ripple则是XRP了。
	// (用IOU的话，因为不同网关的不能完全等价，高频交易过程中需要查询转换的数据太多，交易速度没有保障，反而不如直接统一到XRP作为基本单位效果好,
	// 而且实际转移时也是XRP速度最快，也就是说，XRP在这里起到了“共识计量单位”的作用)
	private String baseCurrency = LSystem.nativeCurrency;

	// 默认至少有15笔买/卖交易才会在对应网关进行交易()
	// default you need at least count 15 to buy / sell transactions
	private int _min_transaction_count = 15;

	/**
	 * default money
	 */
	public void defaultCurrencies() {
		// _currencies.add("XRP");
		_currencies.add("USD");
		_currencies.add("BTC");
		// _currencies.add("CNY");
		// _currencies.add("JPY");
	}

	/**
	 * default gateway
	 */
	public void defaultIssuerAddress() {
		// Bitstamp
		_issuerAddress.add("rvYAfWj5gh67oV6fW32ZzP3Aw4Eubs59B");
		// SnapSwap
		_issuerAddress.add("rMwjYedjc7qqtKYVLiAccJSmCwih4LnE2q");
		// Justcoin
		_issuerAddress.add("rJHygWcTLVpSXkowott6kzgZU6viQSVYM1");
		// RippleCN
		_issuerAddress.add("rnuF96W4SZoCJmbHYBFoJZpR8eCaxNvekK");
		// RippleChina
		_issuerAddress.add("razqQKzJRdB4UxFPWf5NEpEG3WMkmwgcXA");
		// RippleFox
		_issuerAddress.add("rKiCet8SdvWxPXnAgYarFUXMh1zCPz432Y");
		// Ripple Trade Japan
		_issuerAddress.add("rMAz5ZnK73nyNUL4foAvaxdreczCkG3vA6");
		// TokyoJPY
		_issuerAddress.add("r94s8px6kSw1uZ1MV98dhSRTvc6VMPoPcN");
		// Ripple Market JPY
		_issuerAddress.add("rJRi8WW24gt9X85PHAxfWNPCizMMhqUQwg");
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
	 * default transaction max amount
	 */
	public void defaultMaxAmountCross() {
		_limit_max_cross.put("XRP", 50000f);
		_limit_max_cross.put("USD", 500f);
		_limit_max_cross.put("BTC", 2f);
		_limit_max_cross.put("CNY", 3000f);
		_limit_max_cross.put("JPY", 30000f);
	}

	/**
	 * default transaction min amount
	 */
	public void defaultMinAmountCross() {
		_limit_min_cross.put("XRP", 500f);
		_limit_min_cross.put("USD", 5f);
		_limit_min_cross.put("BTC", 0.1f);
		_limit_min_cross.put("CNY", 35f);
		_limit_min_cross.put("JPY", 300f);
	}

	boolean synchroing = true;

	public void check(RippleSeedAddress seed, int limit) {

		final char flag = ',';

		for (final String source_issuer : _issuerAddress) {

			HashSet<String> list = crossTx(flag,
					limit < _currencies.size() ? limit : _currencies.size());

			HashSet<String> cache = new HashSet<String>(100);

			for (String result : list) {

				String[] nodes = StringUtils
						.split(result, String.valueOf(flag));

				for (int i = 0; i < nodes.length - 1; i++) {

					final String source_currency = nodes[i];
					final String target_currency = nodes[i + 1];

					if (!LSystem.nativeCurrency
							.equalsIgnoreCase(source_currency)) {

						Gateway g = Gateway.getGateway(source_issuer);
						if (g != null) {
							boolean found = false;
							for (Gateway.Item item : g.accounts) {
								if (item.currencies.contains(source_currency)) {
									found = true;
								}
							}
							if (!found) {
								continue;
							}
						}
					}
					if (!LSystem.nativeCurrency
							.equalsIgnoreCase(target_currency)) {
						Gateway g = Gateway.getGateway(source_issuer);
						if (g != null) {
							boolean found = false;
							for (Gateway.Item item : g.accounts) {
								if (item.currencies.contains(target_currency)) {
									found = true;
								}
							}
							if (!found) {
								continue;
							}
						}
					}

					final String name = source_currency + "," + target_currency;

					final String rname = target_currency + ","
							+ source_currency;
					if (cache.contains(name) || cache.contains(rname)) {
						continue;
					}
					cache.add(name);

					try {
						Integer count = _cache_count.get(source_issuer + name);

						if (count == null || count.intValue() > 0) {

							System.out.println(source_currency + ","
									+ target_currency);
							synchroing = true;
							OfferPrice.load(source_issuer, source_currency,
									target_currency, new OfferPrice() {

										ArrayList<double[]> buy_list = new ArrayList<double[]>();

										ArrayList<double[]> sell_list = new ArrayList<double[]>();

										private float buy_price = 0;

										private float sell_price = 0;

										private float buy_amount = 0;

										private float sell_amount = 0;

										private float buy_count = 0;

										private float sell_count = 0;

										private float high_buy_price;

										private float high_sell_price;

										@Override
										public void buy(Offer offer) {
											Amount traerPays = offer
													.takerPays();
											String cur = traerPays
													.currencyString();
											float filter = _limit_min_cross
													.get(cur);
											float v = traerPays.floatValue();

											if (v >= filter
													|| TraderBase.equals(v,
															filter)) {

												float lastPrice = buy_price;
												BigDecimal payForOne = offer
														.askQuality();
												Amount getsOne = offer
														.getsOne();

												float result = getsOne.divide(
														payForOne).floatValue();
												if (buy_price == 0) {
													buy_price = result;
													buy_amount = v;
												} else {
													if (buy_price < result) {
														buy_price = result;
														buy_amount = v;
													}
												}
												float limit = _limit_max_cross
														.get(cur);
												if (buy_amount > limit) {
													buy_amount = limit;
												}
												high_buy_price = MathUtils.max(
														high_buy_price,
														lastPrice);
												buy_list.add(new double[] {
														buy_price, buy_amount });
												buy_count++;
											}
										}

										@Override
										public void sell(Offer offer) {
											Amount traerGets = offer
													.takerGets();
											String cur = traerGets
													.currencyString();
											float filter = _limit_min_cross
													.get(cur);
											float v = traerGets.floatValue();
											if (v >= filter
													|| TraderBase.equals(v,
															filter)) {

												float lastPrice = sell_price;
												BigDecimal payForOne = offer
														.askQuality();
												Amount paysOne = offer
														.paysOne();
												float result = paysOne
														.multiply(payForOne)
														.floatValue();

												if (sell_price == 0) {
													sell_price = result;
													sell_amount = v;
												} else {
													if (sell_price > result) {
														sell_price = result;
														sell_amount = v;
													}
												}
												float limit = _limit_max_cross
														.get(cur);
												if (sell_amount > limit) {
													sell_amount = limit;
												}
												high_sell_price = MathUtils
														.max(high_sell_price,
																lastPrice);
												sell_list
														.add(new double[] {
																sell_price,
																sell_amount });
												sell_count++;
											}
										}

										@Override
										public void error(JSONObject obj) {
											_cache_count.put(source_issuer
													+ name, null);
											synchroing = false;
										}

										@Override
										public void empty() {
											_cache_count.put(source_issuer
													+ name, 0);
											synchroing = false;
										}

										@Override
										public void complete(
												ArrayList<OfferFruit> buys,
												ArrayList<OfferFruit> sells,
												OfferPrice price) {
											if (sell_count > _min_transaction_count
													&& buys.size() > _min_transaction_count
													&& buy_count > _min_transaction_count
													&& sells.size() > _min_transaction_count) {
												System.out.println("buy:"
														+ buy_price
														+ ","
														+ buy_amount
														+ ","
														+ LSystem
																.getNumberShort(buy_price
																		* buy_amount));

												System.out.println("sell:"
														+ sell_price
														+ ","
														+ sell_amount
														+ ","
														+ LSystem
																.getNumberShort(sell_price
																		* sell_amount));

												// 筛选条件后最高买卖价格(此最高指显示排序在第一顺位)
												System.out
														.println(high_buy_price
																+ ","
																+ high_sell_price);

												// 单纯最高买卖价格
												System.out
														.println("1/"
																+ source_currency
																+ "=="
																+ price.highBuy
																+ ","
																+ "1/"
																+ source_currency
																+ "=="
																+ price.highSell);

												// 若本地货币和设置的基础货币一致,则实际后台计算的交易额,首先转化为本地货币进行计量
												if (baseCurrency
														.equalsIgnoreCase(LSystem.nativeCurrency)) {

													int idx = 0;
													double buyPrice = buy_list
															.get(idx)[0];
													int buyQuantity = (int) buy_list
															.get(idx)[1];
													double sellPrice = sell_list
															.get(idx)[0];
													int sellQuantity = (int) sell_list
															.get(idx)[1];

													idx++;

													double secondBuyPrice = buy_list
															.get(idx)[0];
													int secondBuyQuantity = (int) buy_list
															.get(idx)[1];
													double secondSellPrice = sell_list
															.get(idx)[0];
													int secondSellQuantity = (int) sell_list
															.get(idx)[1];

													idx++;

													double thirdBuyPrice = buy_list
															.get(idx)[0];
													int thirdBuyQuantity = (int) buy_list
															.get(idx)[1];
													double thirdSellPrice = sell_list
															.get(idx)[0];
													int thirdSellQuantity = (int) sell_list
															.get(idx)[1];

													if (!source_currency
															.equalsIgnoreCase(baseCurrency)) {
														swap(name,
																source_currency,
																source_issuer,
																buyPrice,
																buyQuantity,
																sellPrice,
																sellQuantity,
																secondBuyPrice,
																secondBuyQuantity,
																secondSellPrice,
																secondSellQuantity,
																thirdBuyPrice,
																thirdBuyQuantity,
																thirdSellPrice,
																thirdSellQuantity);
													}
													if (!target_currency
															.equalsIgnoreCase(baseCurrency)) {
														swap(name,
																target_currency,
																source_issuer,
																buyPrice,
																buyQuantity,
																sellPrice,
																sellQuantity,
																secondBuyPrice,
																secondBuyQuantity,
																secondSellPrice,
																secondSellQuantity,
																thirdBuyPrice,
																thirdBuyQuantity,
																thirdSellPrice,
																thirdSellQuantity);
													}
												}

												_cache_count.put(source_issuer
														+ name, buys.size());

											} else {
												_cache_count.put(source_issuer
														+ name, 0);
											}
											synchroing = false;
										}

									}, false);
							for (; synchroing;) {
								try {
									Thread.sleep(_sleep);
								} catch (InterruptedException e) {
								}
							}

						}
					} catch (Exception e) {
						e.printStackTrace();
					}
				}

			}
			System.out.println(source_issuer);

		}

	}

	private void swap(String name, String currency, String issuer,
			double buyPrice, int buyQuantity, double sellPrice,
			int sellQuantity, double secondBuyPrice, int secondBuyQuantity,
			double secondSellPrice, int secondSellQuantity,
			double thirdBuyPrice, int thirdBuyQuantity, double thirdSellPrice,
			int thirdSellQuantity) {

		ArrayList<RippleItem> src_list = RippleChartsAPI.getExchangeRateItems(
				currency, issuer);

		if (src_list.size() > 0) {

			RippleItem item = src_list.get(0);

			Coin swap_xrp = new Coin(Code.newInstance(currency + "/" + name),
					Symbol.newInstance(issuer), name, 0d, item.open, item.vwap,
					item.high, item.low, (int) item.counterVolume, 0, buyPrice,
					buyQuantity, sellPrice, sellQuantity, secondBuyPrice,
					secondBuyQuantity, secondSellPrice, secondSellQuantity,
					thirdBuyPrice, thirdBuyQuantity, thirdSellPrice,
					thirdSellQuantity,

					System.currentTimeMillis());
		}

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

	public void putMaxAmountCross(String name, float value) {
		_limit_max_cross.put(name, value);
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

	// default init
	public TraderNxN() {
		defaultMaxAmountCross();
		defaultMinAmountCross();
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

	public HashMap<String, Float> getMaxCross() {
		return _limit_max_cross;
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
