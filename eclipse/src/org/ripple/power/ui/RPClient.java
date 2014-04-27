package org.ripple.power.ui;

import org.json.JSONObject;
import org.ripple.power.config.LSystem;
import org.ripple.power.wallet.WalletItem;

import com.ripple.client.Client;
import com.ripple.client.ClientLogger;
import com.ripple.client.enums.Command;
import com.ripple.client.requests.Request;
import com.ripple.client.responses.Response;
import com.ripple.client.transport.impl.JavaWebSocketTransportImpl;
import com.ripple.core.coretypes.AccountID;

public class RPClient {

	//测试状态不实际联网
	public static boolean testing = true;

	private static RPClient RippleClient = null;

	public static RPClient ripple() {
		final long sleep = LSystem.applicationSleep;
		synchronized (RPClient.class) {
			if (RippleClient == null) {
				RippleClient = new RPClient();
				if (!testing) {
					Runnable runnable = new Runnable() {
						@Override
						public void run() {
							for (;;) {
								Client client = RippleClient.getClinet();
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
			return RippleClient;
		}
	}

	private final Client pClinet;

	public RPClient() {
		ClientLogger.quiet = true;
		pClinet = new Client(new JavaWebSocketTransportImpl());
		if (LSystem.applicationProxy != null) {
			pClinet.setProxy(LSystem.applicationProxy);
		}
		if (!testing) {
			pClinet.connect(LSystem.applicationRippled);
		}
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
				JSONObject result = (JSONObject) arrays.get("account_data");
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

	public static void main(String[] args) {
		final RPClient client = new RPClient();
		Request req = client.getClinet().newRequest(Command.account_info);
		req.json("account", "rP1coskQzayaQ9geMdJgAV5f3tNZcHghzt");

		req.once(Request.OnSuccess.class, new Request.OnSuccess() {

			@Override
			public void called(Response response) {
				JSONObject arrays = response.result;

				System.out.println(response.status);
				JSONObject result = (JSONObject) arrays.get("account_data");

				System.out.println(result.get("Account") + ","
						+ (double) (result.getDouble("Balance") / 1000000));
			}

		});
		req.once(Request.OnError.class, new Request.OnError() {

			@Override
			public void called(Response response) {
				System.out.println(response.status);

			}

		});
		req.request();
	}

}
