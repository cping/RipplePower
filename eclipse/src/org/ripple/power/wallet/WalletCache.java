package org.ripple.power.wallet;

import java.io.File;
import java.math.BigDecimal;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.StringTokenizer;

import org.address.collection.ArrayMap;
import org.ripple.power.config.LSystem;
import org.ripple.power.ui.RPAddress;
import org.ripple.power.ui.RPClient;
import org.ripple.power.utils.MathUtils;

public class WalletCache {

	public static void loadDefWallet() throws Exception {
		String fileName = LSystem.getDirectory() + LSystem.FS
				+ LSystem.walletName;
		File file = new File(fileName);
		if (file.exists() && file.length() > 0) {
			get().load(file);
		}
	}

	public static void saveDefWallet() throws Exception {
		String fileName = LSystem.getDirectory() + LSystem.FS
				+ LSystem.walletName;
		File file = new File(fileName);
		get().save(file);
	}

	private static WalletCache instance;

	public static WalletCache get() {
		synchronized (WalletCache.class) {
			if (instance == null) {
				instance = new WalletCache();
			}
			return instance;
		}
	}

	private ArrayMap pCaches = new ArrayMap(1000);

	public void add(String pubKey, String priKey) {
		String key = pubKey.concat(priKey);
		if (!pCaches.containsKey(key)) {
			GregorianCalendar cal = new GregorianCalendar();
			cal.setTime(Calendar.getInstance().getTime());
			String date = String.format("%04d-%02d-%02d",
					cal.get(Calendar.YEAR), cal.get(Calendar.MONTH) + 1,
					cal.get(Calendar.DAY_OF_MONTH));
			WalletItem walletItem = new WalletItem(date, priKey, "0.000000",
					"none");
			pCaches.put(key, walletItem);
		}
	}

	private String amounts = "0.000000";

	public void reset() {
		synchronized (pCaches) {
			BigDecimal count = new BigDecimal("0");
			int size = pCaches.size();
			for (int i = 0; i < size; i++) {
				WalletItem item = (WalletItem) pCaches.getEntry(i).getValue();
				if (MathUtils.isNan(item.getAmount())) {
					count = count.add(new BigDecimal(item.getAmount()));
				}
				RPClient.ripple().xrp(item);
			}
			int res = count.intValue();
			if (res > 0) {
				amounts = String.valueOf(res);
			}
		}
	}

	public String getAmounts() {
		return amounts;
	}

	public void save(File file) throws Exception {
		int idx = 0;
		int size = pCaches.size();
		if (size == 0) {
			return;
		}
		synchronized (pCaches) {
			StringBuilder sbr = new StringBuilder();
			WalletSeed seed = new WalletSeed(LSystem.applicationPassword);
			for (int i = 0; i < size; i++) {
				WalletItem item = (WalletItem) pCaches.getEntry(i).getValue();
				RPAddress address = new RPAddress(item.getPublicKey(),
						item.getPrivateKey());
				sbr.append(item.getDate());
				sbr.append(',');
				sbr.append(address.getPublic());
				sbr.append(',');
				sbr.append(address.getPrivate());
				sbr.append(',');
				sbr.append(item.getAmount());
				sbr.append(',');
				sbr.append(item.getStatus());
				idx++;
				if (idx < size) {
					sbr.append(LSystem.LS);
				}
			}
			seed.save(file, sbr.toString());
		}
	}

	public String load(File file) throws Exception {
		synchronized (pCaches) {
			WalletSeed seed = new WalletSeed(LSystem.applicationPassword);
			String text = seed.load(file);
			StringTokenizer tokenizer = new StringTokenizer(text, LSystem.LS);
			String result = null;
			BigDecimal count = new BigDecimal("0");
			for (; tokenizer.hasMoreElements();) {
				result = tokenizer.nextToken();
				if (result != null && result.length() > 0) {
					String[] split = result.split(",");
					if (split.length > 4) {
						String date = split[0];
						String pubKey = split[1];
						String priKey = split[2];
						String amount = split[3];
						String status = split[4];
						RPAddress address = new RPAddress(pubKey, priKey);
						pubKey = new String(address.getPublic());
						priKey = new String(address.getPrivate());
						String key = pubKey.concat(priKey);
						if (!pCaches.containsKey(key)) {
							WalletItem walletItem = new WalletItem(date,
									priKey, amount, status);
							if (MathUtils.isNan(amount)) {
								count = count.add(new BigDecimal(amount));
							}
							pCaches.put(key, walletItem);
						}
					}
				}
				int res = count.intValue();
				if (res > 0) {
					amounts = String.valueOf(res);
				}
			}
			return text;
		}
	}

	public int size() {
		return pCaches.size();
	}

	public WalletItem readRow(int index) {
		return (WalletItem) pCaches.get(index);
	}

}
