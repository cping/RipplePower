package org.ripple.power.ui;

import java.io.IOException;
import java.util.ArrayList;
import java.util.StringTokenizer;

import org.json.JSONObject;
import org.ripple.power.config.LSystem;
import org.ripple.power.config.Session;
import org.ripple.power.i18n.LangConfig;
import org.ripple.power.qr.WebRippled;
import org.ripple.power.txns.Updateable;
import org.ripple.power.utils.HttpRequest;
import org.ripple.power.utils.HttpRequest.HttpRequestException;
import org.ripple.power.utils.MathUtils;
import org.ripple.power.utils.StringUtils;
import org.ripple.power.wallet.WalletItem;

import com.ripple.client.Client;
import com.ripple.client.ClientLogger;
import com.ripple.client.enums.Command;
import com.ripple.client.requests.Request;
import com.ripple.client.responses.Response;
import com.ripple.client.transport.impl.JavaWebSocketTransportImpl;
import com.ripple.core.coretypes.AccountID;

public class RPClient {

	// 测试状态不实际联网
	public static boolean testing = false;

	private static RPClient _rippleClient = null;

	private final ArrayList<Updateable> loads = new ArrayList<Updateable>(10);

	private final static String[] applicationRippleLabes = new String[] {
			"wss://s1.ripple.com:443", "wss://s-west.ripple.com:443",
			"wss://s-east.ripple.com:443" };

	public static ArrayList<String> getRLNodes(boolean flag) {
		ArrayList<String> tmp = new ArrayList<String>(40);
		tmp.add("wss://localhost:443");
		tmp.add(applicationRippleLabes[0]);
		tmp.add(applicationRippleLabes[1]);
		tmp.add(applicationRippleLabes[2]);
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
		if (result == null) {
			if ("宋体".equals(LangConfig.fontName)) {
				return applicationRippleLabes[2];
			} else {
				return applicationRippleLabes[MathUtils.random(0,
						applicationRippleLabes.length - 1)];
			}
		} else if (result.startsWith("wss://") && result.length() > 6) {
			return result;
		} else {
			return applicationRippleLabes[2];
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
		HttpRequest request = HttpRequest.get(url);
		if (request.ok()) {
			String result = request.body();
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

	public void addLoad(Updateable u) {
		synchronized (loads) {
			loads.add(u);
		}
	}

	public void removeLoad(Updateable u) {
		synchronized (loads) {
			loads.remove(u);
		}
	}

	public void removeAllLoad() {
		synchronized (loads) {
			loads.clear();
		}
	}

	public void load() {
		final int count = loads.size();
		if (count > 0) {
			callUpdateable(loads);
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
								}
							}
						}
						load();
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
		ClientLogger.quiet = true;
		pClinet = new Client(new JavaWebSocketTransportImpl());
		if (LSystem.applicationProxy != null) {
			pClinet.setProxy(LSystem.applicationProxy);
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
		Request req = pClinet.newRequest(Command.account_info);
		req.json("account", item.getPublicKey());
		req.once(Request.OnSuccess.class, new Request.OnSuccess() {
			@Override
			public void called(Response response) {
				JSONObject arrays = response.result;
				JSONObject result = arrays.getJSONObject("account_data");
				item.setAmount(String.valueOf((result.getDouble("Balance") / 1000000)));
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
	}

	public Client getClinet() {
		return pClinet;
	}

}
