package org.ripple.power.config;

import java.awt.Color;
import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.LineNumberReader;
import java.io.OutputStream;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.Proxy;
import java.net.UnknownHostException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Random;

import org.address.NativeSupport;
import org.ripple.power.ui.MainForm;
import org.ripple.power.wallet.WalletCache;

public final class LSystem {

	
	final public static ArrayList<String> send_addresses = new ArrayList<String>(
			1000);

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

	public static long applicationSleep = SECOND * 30;

	private static HashMap<String, Session> ripple_store = new HashMap<String, Session>(
			100);

	public static String getIPAddress() throws UnknownHostException {
		InetAddress address = InetAddress.getLocalHost();
		return address.getHostAddress();
	}

	public static String getMACAddress() {
		String macStr = "";
		try {
			InetAddress address = InetAddress.getLocalHost();
			NetworkInterface ni = NetworkInterface.getByInetAddress(address);
			if (ni != null) {
				byte[] mac = ni.getHardwareAddress();
				if (mac != null) {
					macStr = asMACHex(mac);
				}
			}
		} catch (Exception e) {
			return getOtherMACAddress();
		}
		return macStr;
	}

	private static String getWinMACAddress() {
		String address = "Error!";
		try {
			String command = "cmd.exe /c ipconfig /all";
			Process p = Runtime.getRuntime().exec(command);
			BufferedReader br = new BufferedReader(new InputStreamReader(
					p.getInputStream(), "gbk"));
			String line;
			while ((line = br.readLine()) != null) {
				if (line.indexOf("Physical Address") > 0) {
					int index = line.indexOf(":");
					index += 2;
					address = line.substring(index);
					break;
				} else if (line.indexOf("物理地址") > 0) {
					int index = line.indexOf(":");
					index += 2;
					address = line.substring(index);
					break;
				}
			}
			br.close();
			return address.trim();
		} catch (IOException e) {
			return "Error!";
		}
	}

	private static String asMACHex(byte buf[]) {
		StringBuffer sbr = new StringBuffer(buf.length * 2);
		int i;
		for (i = 0; i < buf.length; i++) {
			if (((int) buf[i] & 0xff) < 0x10) {
				sbr.append("0");
			}
			sbr.append(Long.toString((int) buf[i] & 0xff, 16));
			sbr.append(':');
		}
		if (sbr.toString().length() == 0) {
			return "";
		} else {
			sbr.delete(sbr.length() - 1, sbr.length());
			return sbr.toString().toUpperCase();
		}
	}

	private static String getOtherMACAddress() {
		String str = "", strMAC = "", macAddress = "";
		try {
			Process pp = Runtime.getRuntime().exec(
					"nbtstat -a " + getIPAddress());
			InputStreamReader ir = new InputStreamReader(pp.getInputStream(),
					LSystem.encoding);
			LineNumberReader input = new LineNumberReader(ir);
			for (int i = 1; i < 100; i++) {
				str = input.readLine();
				if (str != null) {
					if (str.toLowerCase().indexOf("mac") != -1) {
						strMAC = str.substring(str.indexOf("=") + 1,
								str.length()).trim();
						break;
					}
				}
			}
		} catch (IOException ex) {
			if (NativeSupport.isWindows) {
				strMAC = getWinMACAddress();
			}
		}
		if (strMAC.length() < 17) {
			return "Error!";
		}
		macAddress = (strMAC.substring(0, 2) + ":" + strMAC.substring(3, 5)
				+ ":" + strMAC.substring(6, 8) + ":" + strMAC.substring(9, 11)
				+ ":" + strMAC.substring(12, 14) + ":" + strMAC.substring(15,
				17)).toUpperCase();
		return macAddress;
	}

	public static String getTime() {
		Calendar cal = Calendar.getInstance();
		SimpleDateFormat sdf = new SimpleDateFormat("mm:dd:ss");
		return sdf.format(cal.getTime());
	}

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

	public static String getLanguage() {
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
