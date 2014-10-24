package org.ripple.power.ui;

import java.util.ArrayList;
import java.util.StringTokenizer;

import org.json.JSONObject;
import org.ripple.power.config.LSystem;
import org.ripple.power.qr.WebRippled;
import org.ripple.power.txns.Updateable;
import org.ripple.power.utils.HttpRequest;
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

	public static ArrayList<String> loadRippleLabsIPS() {
		ArrayList<String> list = new ArrayList<String>(30);
		WebRippled rippled = loadWebRippledConfig("https://ripple.com/ripple.txt");
		for (String ips : rippled.ips) {
			String result = StringUtils.split(ips, " ")[0];
			list.add(result);
		}
		return list;
	}

	public static WebRippled loadWebRippledConfig(String url) {
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

	public static void main(String[] args) {
		loadRippleLabsIPS();
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
					Runnable runnable = new Runnable() {
						@Override
						public void run() {
							for (;;) {
								Client client = _rippleClient.getClinet();
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
								_rippleClient.load();
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
					Thread thread = new Thread(runnable);
					thread.start();
				}
			}
		}
		return _rippleClient;
	}

	private final Client pClinet;

	private String node_path = "unkown";

	public RPClient() {
		ClientLogger.quiet = true;
		pClinet = new Client(new JavaWebSocketTransportImpl());
		if (LSystem.applicationProxy != null) {
			pClinet.setProxy(LSystem.applicationProxy);
		}
		if (!testing) {
			node_path = LSystem.getRippledNode();
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
