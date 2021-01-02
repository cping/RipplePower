package org.ripple.power.wallet;

import java.io.File;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.StringTokenizer;

import org.ripple.power.collection.ArrayMap;
import org.ripple.power.config.LSystem;
import org.ripple.power.ui.RPAddress;
import org.ripple.power.ui.RPClient;
import org.ripple.power.utils.MathUtils;

public class WalletCache {

	public static void loadDefWallet() throws Exception {
		String fileName = LSystem.getRippleDirectory() + LSystem.FS + LSystem.walletName;
		File file = new File(fileName);
		if (file.exists() && file.length() > 0) {
			get().load(file);
		}
	}

	public static void saveDefWallet() throws Exception {
		String fileName = LSystem.getRippleDirectory() + LSystem.FS + LSystem.walletName;
		File file = new File(fileName);
		if (file.exists()) {
			File rename = new File(fileName);
			// 每次保存前，备份加密的上一次私钥信息，以防意外丢失
			rename.renameTo(new File(fileName + "." + System.currentTimeMillis() + ".bak"));
		}
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
		add(pubKey, priKey, false);
	}

	public void add(String pubKey, String priKey, boolean online) {
		String key = pubKey.concat(priKey);
		if (!pCaches.containsKey(key)) {
			GregorianCalendar cal = new GregorianCalendar();
			cal.setTime(Calendar.getInstance().getTime());
			String date = String.format("%04d-%02d-%02d", cal.get(Calendar.YEAR), cal.get(Calendar.MONTH) + 1,
					cal.get(Calendar.DAY_OF_MONTH));
			WalletItem walletItem = new WalletItem(date, priKey, "0.000000", "none");
			walletItem.setOnline(online);
			pCaches.put(key, walletItem);
			isSort = true;
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
			isSort = true;
		}
	}

	public void deleted(int idx) {
		synchronized (pCaches) {
			int size = pCaches.size();
			if (idx > -1 && idx < size) {
				pCaches.remove(idx);
			}
			if (pCaches.size() == 0) {
				String fileName = LSystem.getRippleDirectory() + LSystem.FS + LSystem.walletName;
				File file = new File(fileName);
				if (file.exists()) {
					file.deleteOnExit();
				}
			}
			isSort = true;
		}
	}

	public WalletItem findItem(String address) {
		synchronized (pCaches) {
			int size = pCaches.size();
			if (size > 0) {
				for (int i = 0; i < size; i++) {
					WalletItem item = (WalletItem) pCaches.get(i);
					if (item.getPublicKey().equals(address)) {
						return item;
					}
				}
			}
			return null;
		}
	}

	public String findSecret(String address) {
		synchronized (pCaches) {
			int size = pCaches.size();
			if (size > 0) {
				for (int i = 0; i < size; i++) {
					WalletItem item = (WalletItem) pCaches.get(i);
					if (item.getPublicKey().equals(address)) {
						return item.getPrivateKey();
					}
				}
			}
			return null;
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
			WalletSeed seed = new WalletSeed(LSystem.getAppPassword());
			for (int i = 0; i < size; i++) {
				WalletItem item = (WalletItem) pCaches.getEntry(i).getValue();
				if (!item.isOnline()) {
					RPAddress address = new RPAddress(item.getPublicKey(), item.getPrivateKey());
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
			}
			String context = sbr.toString();
			if (context.length() > 0 && context.indexOf(',') != -1) {
				seed.save(file, context);
			}
		}
	}

	public String load(File file) throws Exception {
		synchronized (pCaches) {
			WalletSeed seed = new WalletSeed(LSystem.getAppPassword());
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
							WalletItem walletItem = new WalletItem(date, priKey, amount, status);
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
				isSort = true;
			}
			return text;
		}
	}

	public int size() {
		return pCaches.size();
	}

	private boolean isSort = true;

	private static ArrayMap sortMapByValues(Map<String, WalletItem> aMap) {

		Set<Map.Entry<String, WalletItem>> mapEntries = aMap.entrySet();

		List<Map.Entry<String, WalletItem>> aList = new LinkedList<Map.Entry<String, WalletItem>>(mapEntries);

		Collections.sort(aList, new Comparator<Map.Entry<String, WalletItem>>() {

			@Override
			public int compare(Map.Entry<String, WalletItem> ele1, Map.Entry<String, WalletItem> ele2) {
				Double a1 = Double.parseDouble(ele1.getValue().getAmount());
				Double a2 = Double.parseDouble(ele2.getValue().getAmount());
				return a2.compareTo(a1);
			}
		});
		ArrayMap map = new ArrayMap(aList.size());
		for (Map.Entry<String, WalletItem> entry : aList) {
			map.put(entry.getKey(), entry.getValue());
		}
		return map;
	}

	private void sort() {
		if (isSort) {
			Map<String, WalletItem> temp = new HashMap<String, WalletItem>(pCaches.size());
			for (int i = 0; i < pCaches.size(); i++) {
				ArrayMap.Entry entry = pCaches.getEntry(i);
				temp.put((String) entry.getKey(), (WalletItem) entry.getValue());
			}
			ArrayMap map = sortMapByValues(temp);
			pCaches.clear();
			pCaches.putAll(map);
			isSort = false;
		}
	}

	public WalletItem readRow(int index) {
		sort();
		return (WalletItem) pCaches.get(index);
	}

	public ArrayList<WalletItem> all() {
		ArrayList<WalletItem> list = new ArrayList<WalletItem>(pCaches.size());
		for (int i = 0; i < pCaches.size(); i++) {
			list.add((WalletItem) pCaches.get(i));
		}
		return list;
	}

}
