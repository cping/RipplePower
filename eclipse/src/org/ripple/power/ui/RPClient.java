package org.ripple.power.ui;

import java.io.IOException;
import java.util.ArrayList;
import java.util.StringTokenizer;

import org.json.JSONObject;
import org.ripple.power.config.LSystem;
import org.ripple.power.config.Session;
import org.ripple.power.i18n.LangConfig;
import org.ripple.power.qr.WebRippled;
import org.ripple.power.txns.CurrencyUtils;
import org.ripple.power.txns.Updateable;
import org.ripple.power.utils.HttpRequest;
import org.ripple.power.utils.HttpRequest.HttpRequestException;
import org.ripple.power.utils.MathUtils;
import org.ripple.power.utils.StringUtils;
import org.ripple.power.wallet.WalletItem;

import com.ripple.client.Client;
import com.ripple.client.enums.Command;
import com.ripple.client.requests.Request;
import com.ripple.client.responses.Response;
import com.ripple.client.transport.impl.JavaWebSocketTransportImpl;
import com.ripple.core.coretypes.AccountID;
import com.ripple.core.coretypes.RippleDate;

public class RPClient {

	static {
		System.setProperty("https.protocols", "TLSv1.2");
	}

	// Test status is not networking
	public static boolean testing = false;

	private static RPClient _rippleClient = null;

	private final ArrayList<Updateable> _loads = new ArrayList<Updateable>(10);

	private final ArrayList<Updateable> _longloads = new ArrayList<Updateable>(
			10);

	private final static String[] applicationRippleLabes = new String[] {
			"wss://s2.ripple.com:443", "wss://s1.ripple.com:443",
			"wss://s-west.ripple.com:443", "wss://s-east.ripple.com:443" };

	public static ArrayList<String> getRLNodes(boolean flag) {
		ArrayList<String> tmp = new ArrayList<String>(40);
		tmp.add("wss://localhost:443");
		tmp.add(applicationRippleLabes[0]);
		tmp.add(applicationRippleLabes[1]);
		tmp.add(applicationRippleLabes[2]);
		tmp.add(applicationRippleLabes[3]);
		if (flag) {
			try {
				ArrayList<String> list = RPClient.loadRLNodes();
				if (list != null && list.size() > 0) {
					tmp.addAll(list);
				}
			} catch (Exception ex) {
			}
		}
		return tmp;
	}

	public static void saveRippledNode(String wss) {
		if (wss == null) {
			return;
		}
		Session session = LSystem.session("ripple_node");
		session.set("data", wss.trim());
		session.save();
	}

	public static String getRippledNode() {
		Session session = LSystem.session("ripple_node");
		String result = session.get("data");
		if (result != null && result.startsWith("wss://")
				&& result.length() > 6) {
			return result;
		} else {
			return applicationRippleLabes[MathUtils.random(0,
					applicationRippleLabes.length - 1)];
		}
	}

	public static ArrayList<String> loadRLNodes() throws HttpRequestException,
			IOException {
		ArrayList<String> list = new ArrayList<String>(30);
		WebRippled rippled = loadWebRippledConfig("https://ripple.com/ripple.txt");
		for (String ips : rippled.ips) {
			String result = "wss://" + StringUtils.split(ips, " ")[0] + ":443";
			list.add(result);
		}
		return list;
	}

	public static WebRippled loadWebRippledConfig(String url)
			throws HttpRequestException, IOException {
		WebRippled rippled = new WebRippled();
		// 突然发现报错Could not generate DH
		// keypair（最早是没有的，不知道rl又换了什么……），似乎遇到java的bug了，目前验证ripple.com站的ssl证书会溢出，然后报错的是sun.security.ssl.SSLSocketImpl部分，私有代码想修改都不行，只能过几天换成openssl直接读吧……
		HttpRequest request = HttpRequest.get(url);
		request.trustAllCerts();
		request.trustAllHosts();
		if (request.ok()) {
			String result = request.body();
			if (result.indexOf('\r') != -1) {
				result = StringUtils.replace(result, "\r", "\n");
			}
			StringTokenizer str = new StringTokenizer(result, "\n");
			int flagNext = -1;
			for (; str.hasMoreTokens();) {
				String context = str.nextToken().trim();
				if (context != null && context.length() > 0) {
					if (context.startsWith("[")) {
						if ("[accounts]".equals(context)) {
							flagNext = 0;
						} else if ("[validation_public_key]".equals(context)) {
							flagNext = 1;
						} else if ("[domain]".equals(context)) {
							flagNext = 2;
						} else if ("[ips]".equals(context)) {
							flagNext = 3;
						} else if ("[validators]".equals(context)) {
							flagNext = 4;
						} else if ("[authinfo_url]".equals(context)) {
							flagNext = 5;
						} else {
							flagNext = -1;
						}
						continue;
					}
					switch (flagNext) {
					case 0:
						rippled.accounts.add(context);
						break;
					case 1:
						rippled.validation_public_key.add(context);
						break;
					case 2:
						rippled.domain.add(context);
						break;
					case 3:
						rippled.ips.add(context);
						break;
					case 4:
						rippled.validators.add(context);
						break;
					case 5:
						rippled.authinfo_url.add(context);
						break;
					default:
						break;
					}
				}
			}
		}
		return rippled;

	}

	//

	public void addLongLoad(Updateable u) {
		synchronized (_longloads) {
			_longloads.add(u);
		}
	}

	public void removeLongLoad(Updateable u) {
		synchronized (_longloads) {
			_longloads.remove(u);
		}
	}

	public void removeAllLongLoad() {
		synchronized (_longloads) {
			_longloads.clear();
		}
	}

	public void loadLong() {
		final int count = _longloads.size();
		if (count > 0) {
			callLongUpdateable(_longloads);
		}
	}

	private final static void callLongUpdateable(
			final ArrayList<Updateable> list) {
		synchronized (list) {
			for (int i = 0; i < list.size(); i++) {
				Updateable running = list.get(i);
				synchronized (running) {
					running.action(null);
				}
			}
		}
	}

	// Updateable Region
	public void addLoad(Updateable u) {
		synchronized (_loads) {
			_loads.add(u);
		}
	}

	public void removeLoad(Updateable u) {
		synchronized (_loads) {
			_loads.remove(u);
		}
	}

	public void removeAllLoad() {
		synchronized (_loads) {
			_loads.clear();
		}
	}

	public void load() {
		final int count = _loads.size();
		if (count > 0) {
			callUpdateable(_loads);
		}
	}

	private final static void callUpdateable(final ArrayList<Updateable> list) {
		ArrayList<Updateable> loadCache;
		synchronized (list) {
			loadCache = new ArrayList<Updateable>(list);
			list.clear();
		}
		for (int i = 0; i < loadCache.size(); i++) {
			Updateable running = loadCache.get(i);
			synchronized (running) {
				running.action(null);
			}
		}
		loadCache = null;
	}

	public static RPClient ripple() {
		if (_rippleClient == null) {
			synchronized (RPClient.class) {
				_rippleClient = new RPClient();
				if (!testing) {
					_rippleClient.threadLoop();
				} else {
					MainForm form = LSystem.applicationMain;
					if (form != null) {
						MainPanel panel = form.getMainPanel();
						if (panel != null) {
							panel.setSpeedIcon("empty");
						}
					}
				}
			}
		}
		return _rippleClient;
	}

	public static void stop() {
		if (_rippleClient != null) {
			synchronized (RPClient.class) {
				if (!testing) {
					_rippleClient.threadStop();
					_rippleClient = null;
				}
			}
		}
	}

	public synchronized static void reset() {
		stop();
		ripple();
	}

	private final Client pClinet;

	private Thread pThread;

	private String node_path = "unkown";

	private boolean pLooping;

	public String getBaseFee() {
		Client client = getClinet();
		if (client != null) {
			if (client.serverInfo.fee_ref == 0
					|| client.serverInfo.load_base == 0) {
				return "0.01";
			}
			float fee_unit = client.serverInfo.fee_base
					/ client.serverInfo.fee_ref;
			fee_unit *= client.serverInfo.load_factor
					/ client.serverInfo.load_base;
			fee_unit *= 10f;
			return CurrencyUtils.getRippleToValue(String.valueOf(fee_unit));
		}
		return "Unkown";
	}

	public synchronized void threadStop() {
		if (pThread != null) {
			this.pLooping = false;
			try {
				pThread.interrupt();
				pThread = null;
			} catch (Throwable t) {

			}
		}
	}

	public synchronized void threadLoop() {
		if (pThread == null) {
			this.pLooping = true;
			Runnable runnable = new Runnable() {
				@Override
				public void run() {
					for (; pLooping;) {
						Client client = getClinet();
						if (client != null) {
							MainForm form = LSystem.applicationMain;
							if (form != null) {
								MainPanel panel = form.getMainPanel();
								if (panel != null) {
									if (client.serverInfo.server_status != null) {
										panel.walletChanged(client.serverInfo.server_status);
									} else {
										panel.walletChanged("none");
									}
									if (client.getSpeed() <= 100) {
										panel.setSpeedIcon("all");
									} else if (client.getSpeed() <= 1000) {
										panel.setSpeedIcon("lv5");
									} else if (client.getSpeed() <= 1500) {
										panel.setSpeedIcon("lv4");
									} else if (client.getSpeed() <= 2500) {
										panel.setSpeedIcon("lv3");
									} else if (client.getSpeed() <= 3500) {
										panel.setSpeedIcon("lv2");
									} else if (client.getSpeed() <= 4500) {
										panel.setSpeedIcon("lv1");
									} else {
										panel.setSpeedIcon("lv0");
									}
								}
							}
						}
						load();
						loadLong();
						final long sleep = LSystem.applicationSleep;
						if (sleep > 0) {
							try {
								Thread.sleep(sleep);
							} catch (InterruptedException e) {
							}
						} else {
							Thread.yield();
						}
					}
				}
			};
			pThread = new Thread(runnable);
			pThread.start();
		}
	}

	public RPClient() {
		// ClientLogger.quiet = true;
		pClinet = new Client(new JavaWebSocketTransportImpl());
		if (LSystem.applicationProxy != null) {
			pClinet.setProxy(LSystem.applicationProxy.getProxy());
		}
		if (!testing) {
			node_path = getRippledNode();
			pClinet.connect(node_path);
		}
	}

	public String getNodePath() {
		return node_path;
	}

	public AccountID getAccountID(String address) {
		AccountID id = AccountID.fromAddress(address);
		return id;
	}

	public Request newRequest(Command command) {
		return pClinet.newRequest(command);
	}

	public void xrp(final WalletItem item) {
		try {
			Request req = pClinet.newRequest(Command.account_info);
			req.json("account", item.getPublicKey());
			req.once(Request.OnSuccess.class, new Request.OnSuccess() {
				@Override
				public void called(Response response) {
					JSONObject arrays = response.result;
					JSONObject result = arrays.getJSONObject("account_data");
					String number = CurrencyUtils.getRippleToValue(result
							.getString("Balance"));
					if (item.isTip()) {
						double new_amount = Double.parseDouble(number);
						double old_amount = Double.parseDouble(item.getAmount());
						if (old_amount > new_amount) {
							popXRP(item.getPublicKey(), LangConfig.get(
									RPClient.class, "lower", "Lower"),
									old_amount - new_amount);
						} else if (new_amount > old_amount) {
							popXRP(item.getPublicKey(), LangConfig.get(
									RPClient.class, "heighten", "Heighten"),
									new_amount - old_amount);
						}
					}
					item.setAmount(number);
					item.setStatus("full");
				}

			});

			req.once(Request.OnError.class, new Request.OnError() {
				@Override
				public void called(Response response) {
					item.setStatus("none");
				}

			});
			req.request();
		} catch (Exception ex) {
			// null
		}
	}

	private final static String time() {
		return LangConfig.get(RPClient.class, "in", "In")
				+ RippleDate.now().getTimeString() + ", "
				+ LangConfig.get(RPClient.class, "ya", "Your address") + ": ";
	}

	private static void popXRP(String address, String flag, double amount) {
		RPBubbleDialog.pop(time() + address + flag + " "
				+ LSystem.getNumberShort(amount) + LSystem.nativeCurrency);
	}

	public Client getClinet() {
		return pClinet;
	}

}
