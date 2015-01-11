package org.ripple.power.hft;

import java.util.ArrayList;

import org.json.JSONArray;
import org.ripple.power.config.LSystem;
import org.ripple.power.config.Loop;
import org.ripple.power.config.Session;
import org.ripple.power.timer.LTimer;
import org.ripple.power.timer.LTimerContext;
import org.ripple.power.txns.Gateway;
import org.ripple.power.txns.Updateable;

public class PriceMonitor extends Loop {

	class PriceMonitorItem {

		String gateway;

		String currency;

		LTimer delay;

		String query;

	}

	private final String[] querys = { ">", ">=", "<", "<=", "==" };

	private ArrayList<PriceMonitor.PriceMonitorItem> loops = new ArrayList<PriceMonitor.PriceMonitorItem>(
			10);

	public PriceMonitor() {
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
										String delay = res.substring(idx + 1,
												res.length());

										PriceMonitorItem item = new PriceMonitorItem();
										item.currency = cur;
										item.gateway = address;
										item.delay = new LTimer(
												Long.parseLong(delay));
										item.query = query;
										loops.add(item);
									}
								}
							}
						}
					}

				}
			}
		}
	}


	@Override
	public void runTaskTimer(LTimerContext context) {

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

}
