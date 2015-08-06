package org.ripple.power.txns;

import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONObject;
import org.ripple.power.config.LSystem;
import org.ripple.power.config.Loop;
import org.ripple.power.config.Session;
import org.ripple.power.timer.LTimer;
import org.ripple.power.timer.LTimerContext;
import org.ripple.power.ui.RPBubbleDialog;
import org.ripple.power.utils.DateUtils;

public class RipplePriceMonitor extends Loop {

	private static RipplePriceMonitor instance;

	class PriceMonitorItem {

		IssuedCurrency currency;

		LTimer delay;

		double value;

		String query;

	}

	public static RipplePriceMonitor get() {
		synchronized (RipplePriceMonitor.class) {
			if (instance == null) {
				instance = new RipplePriceMonitor();
			}
			return instance;
		}
	}

	private final String[] querys = { ">", ">=", "<", "<=", "==" };

	private ArrayList<RipplePriceMonitor.PriceMonitorItem> loops = new ArrayList<RipplePriceMonitor.PriceMonitorItem>(
			10);

	private RipplePriceMonitor() {
		reset();
	}

	public void reset() {
		stop();
		loops.clear();
		Session session = LSystem.session("check_price");
		String result = session.get("warn");
		if (result != null) {
			JSONArray json = new JSONArray(result);
			for (int i = 0; i < json.length(); i++) {
				String res = json.getString(i);
				int idx = res.indexOf("/");
				if (idx != -1) {

					String gateway = res.substring(0, idx);
					int empty = res.indexOf(" ");
					if (empty != -1) {
						String cur = res.substring(idx + 1, empty);

						if (Gateway.getAddress(gateway).accounts.size() > 0) {

							String address = (Gateway.getAddress(gateway).accounts
									.get(0).address);
							if (Gateway.getAddress(gateway).accounts.get(0).currencies
									.contains(cur)) {
								idx = -1;
								for (String q : querys) {
									idx = res.indexOf(q);
									if (idx != -1) {
										break;
									}
								}
								res = res.substring(idx, res.length());
								empty = res.indexOf(" ");
								if (empty != -1) {
									String query = res.substring(0, empty);
									idx = res.indexOf(",");
									if (idx != -1) {
										String value = res.substring(empty + 1,
												idx);
										String delay = res.substring(idx + 1,
												res.length());
										PriceMonitorItem item = new PriceMonitorItem();
										item.currency = new IssuedCurrency(
												address, cur);
										item.delay = new LTimer(
												Long.parseLong(delay));
										item.query = query;
										item.value = Double.valueOf(value);
										loops.add(item);
									}
								}
							}
						}
					}

				}
			}
		}
		if (loops.size() > 0) {
			initCheck();
			loop();
		}
	}

	@Override
	public void runTaskTimer(LTimerContext context) {
		for (PriceMonitorItem item : loops) {
			if (item.delay.action(context)) {
				double value = -1;
				Object result = RippleChartsAPI.getExchange(item.currency);
				if (result != null) {
					if (result instanceof JSONArray) {
						value = ((JSONArray) result).getJSONObject(0)
								.getDouble("rate");
					} else if (result instanceof JSONObject) {
						value = ((JSONObject) result).getDouble("rate");
					}
				}
				value = (1d / value) + 0.0006d;
				switch (item.query) {
				case ">":
					if (value > item.value) {
						showPrice(item, value);
					}
					break;
				case ">=":
					if (value >= item.value) {
						showPrice(item, value);
					}
					break;
				case "<":
					if (value < item.value) {
						showPrice(item, value);
					}
					break;
				case "<=":
					if (value <= item.value) {
						showPrice(item, value);
					}
					break;
				case "==":
					if (value == item.value) {
						showPrice(item, value);
					}
					break;
				default:
					break;
				}

			}
		}
	}

	private void initCheck() {
		for (PriceMonitorItem item : loops) {
			double value = -1;
			Object result = RippleChartsAPI.getExchange(item.currency);
			if (result != null) {
				if (result instanceof JSONArray) {
					value = ((JSONArray) result).getJSONObject(0).getDouble(
							"rate");
				} else if (result instanceof JSONObject) {
					value = ((JSONObject) result).getDouble("rate");
				}
			}
			value = (1d / value) + 0.0006d;
			switch (item.query) {
			case ">":
				if (value > item.value) {
					showPrice(item, value);
				}
				break;
			case ">=":
				if (value >= item.value) {
					showPrice(item, value);
				}
				break;
			case "<":
				if (value < item.value) {
					showPrice(item, value);
				}
				break;
			case "<=":
				if (value <= item.value) {
					showPrice(item, value);
				}
				break;
			case "==":
				if (value == item.value) {
					showPrice(item, value);
				}
				break;
			default:
				break;
			}
		}
	}

	@Override
	public Updateable main() {
		Updateable updateable = new Updateable() {

			@Override
			public void action(Object o) {
				mainLoop();
			}
		};
		return updateable;
	}

	private void showPrice(PriceMonitorItem item, double value) {
		RPBubbleDialog.pop("Price Monitor : " + DateUtils.toDate() + ", now "
				+ Gateway.getGateway(item.currency.issuer.toString()).name
				+ "/" + item.currency.currency + " convert 1/XRP == " + value);
	}

	public static void main(String[] args) {
		RipplePriceMonitor.get();
	}

}
