package org.ripple.power.config;

import java.awt.Color;
import java.awt.RenderingHints;
import java.awt.Toolkit;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.StringSelection;
import java.awt.datatransfer.Transferable;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Proxy;
import java.util.HashMap;
import java.util.Random;

import javax.swing.JLabel;

import org.ripple.power.ui.MainForm;
import org.ripple.power.ui.MainPanel;
import org.ripple.power.ui.RPClient;
import org.ripple.power.utils.FileUtils;
import org.ripple.power.wallet.WalletCache;

public final class LSystem {

	final static public Color background = Color.decode("#583F7E");

	final static public long SECOND = 1000;
	final static public long MINUTE = SECOND * 60;
	final static public long MSEC = 1L;
	final static public long SEC = 1000 * MSEC;
	final static public long MIN = 60 * SEC;
	final static public long HOUR = 60 * MIN;
	final static public long DAY = 24 * HOUR;
	final static public long WEEK = 7 * DAY;
	final static public long MONTH = 31 * DAY;
	final static public long YEAR = 365 * DAY;

	public static final int DEFAULT_MAX_CACHE_SIZE = 20;
	
	public static final String applicationName = "RipplePower";

	public static final String applicationVersion = "0.1";
	
	public static final String walletName = "ripple_wallet.dat";

	public static final char[] hex16 = "0123456789abcdef".toCharArray();

	private static String applicationDataDirectory = null;

	public static String applicationPassword = "mynameiscping0o5498^%1032%%76!7*(%$.com%.~";

	public static Proxy applicationProxy = null;

	public static String applicationRippled = "wss://s1.ripple.com";

	public static MainForm applicationMain = null;
	
	public static long applicationSleep =  SECOND * 30;

	private static HashMap<String, Session> ripple_store = new HashMap<String, Session>(
			100);

	// 存储会话数据
	public static Session session(String name) {
		Session session = ripple_store.get(name);
		if (session == null) {
			session = new Session(name);
			ripple_store.put(name, session);
		}
		return session;
	}

	// 关闭时存储数据
	public static void shutdown() {
		synchronized (LSystem.class) {
			try {
				WalletCache.saveDefWallet();
			} catch (Exception e) {
			}
			for (Session session : ripple_store.values()) {
				if (session != null) {
					session.save();
				}
			}
			System.exit(-1);
		}
	}

	// 数据存储空间定位
	public static String getDirectory() {
		if (applicationDataDirectory != null) {
			return applicationDataDirectory;
		}
		String operatingSystemName = System.getProperty("os.name");
		if (operatingSystemName != null
				&& operatingSystemName.startsWith("Windows")) {
			applicationDataDirectory = System.getenv("APPDATA")
					+ File.separator + applicationName;
		} else {
			if (operatingSystemName != null
					&& operatingSystemName.startsWith("Mac")) {
				applicationDataDirectory = System.getProperty("user.home")
						+ "/Library/Application Support/" + applicationName;
			} else {
				applicationDataDirectory = System.getProperty("user.home")
						+ "/" + applicationName;
			}
		}
		return applicationDataDirectory;
	}

	// 文字清晰过滤开
	final static public RenderingHints VALUE_TEXT_ANTIALIAS_ON = new RenderingHints(
			RenderingHints.KEY_TEXT_ANTIALIASING,
			RenderingHints.VALUE_TEXT_ANTIALIAS_ON);

	// 文字清晰过滤关
	final static public RenderingHints VALUE_TEXT_ANTIALIAS_OFF = new RenderingHints(
			RenderingHints.KEY_TEXT_ANTIALIASING,
			RenderingHints.VALUE_TEXT_ANTIALIAS_OFF);

	// 清晰过滤开
	final static public RenderingHints VALUE_ANTIALIAS_ON = new RenderingHints(
			RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

	// 清晰过滤关
	final static public RenderingHints VALUE_ANTIALIAS_OFF = new RenderingHints(
			RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_OFF);

	public String getLanguage() {
		return java.util.Locale.getDefault().getDisplayName();
	}

	public final static void close(InputStream in) {
		if (in != null) {
			try {
				in.close();
				in = null;
			} catch (Exception e) {
			}
		}
	}

	// 随机数
	final static public Random random = new Random();

	// 默认编码格式
	final static public String encoding = "UTF-8";

	// 行分隔符
	final static public String LS = System.getProperty("line.separator", "\n");

	// 文件分割符
	final static public String FS = System.getProperty("file.separator", "\\");

	/**
	 * 写入整型数据到OutputStream
	 * 
	 * @param out
	 * @param number
	 */
	public final static void writeInt(final OutputStream out, final int number) {
		byte[] bytes = new byte[4];
		try {
			for (int i = 0; i < 4; i++) {
				bytes[i] = (byte) ((number >> (i * 8)) & 0xff);
			}
			out.write(bytes);
		} catch (Exception ex) {
			throw new RuntimeException(ex);
		}
	}

	/**
	 * 从InputStream中获得整型数据
	 * 
	 * @param in
	 * @return
	 */
	final static public int readInt(final InputStream in) {
		int data = -1;
		try {
			data = (in.read() & 0xff);
			data |= ((in.read() & 0xff) << 8);
			data |= ((in.read() & 0xff) << 16);
			data |= ((in.read() & 0xff) << 24);
		} catch (IOException ex) {
			throw new RuntimeException(ex);
		}
		return data;
	}

	/**
	 * 合并hashCode和指定类型的数值生成新的Code值(以下同)
	 * 
	 * @param hashCode
	 * @param value
	 * @return
	 */
	public static int unite(int hashCode, boolean value) {
		int v = value ? 1231 : 1237;
		return unite(hashCode, v);
	}

	public static int unite(int hashCode, long value) {
		int v = (int) (value ^ (value >>> 32));
		return unite(hashCode, v);
	}

	public static int unite(int hashCode, float value) {
		int v = Float.floatToIntBits(value);
		return unite(hashCode, v);
	}

	public static int unite(int hashCode, double value) {
		long v = Double.doubleToLongBits(value);
		return unite(hashCode, v);
	}

	public static int unite(int hashCode, Object value) {
		return unite(hashCode, value.hashCode());
	}

	public static int unite(int hashCode, int value) {
		return 31 * hashCode + value;
	}

}
