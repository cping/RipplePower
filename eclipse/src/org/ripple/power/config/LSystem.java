package org.ripple.power.config;

import java.awt.image.BufferedImage;
import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.LineNumberReader;
import java.io.OutputStream;
import java.lang.reflect.Method;
import java.math.BigDecimal;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.UnknownHostException;
import java.security.AccessControlException;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Random;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.swing.SwingUtilities;

import org.ripple.power.CoinUtils;
import org.ripple.power.NativeSupport;
import org.ripple.power.i18n.Language;
import org.ripple.power.timer.NanoTimer;
import org.ripple.power.timer.SystemTimer;
import org.ripple.power.txns.Updateable;
import org.ripple.power.ui.MainForm;
import org.ripple.power.ui.graphics.geom.RectBox;
import org.ripple.power.ui.projector.UIContext;
import org.ripple.power.ui.projector.UIView;
import org.ripple.power.ui.projector.Resources;
import org.ripple.power.ui.projector.core.LHandler;
import org.ripple.power.ui.projector.core.graphics.Screen;
import org.ripple.power.utils.GraphicsUtils;
import org.ripple.power.utils.IP46Utils;
import org.ripple.power.utils.MathUtils;
import org.ripple.power.utils.StringUtils;
import org.ripple.power.utils.SwingUtils;
import org.ripple.power.wallet.OpenSSL;
import org.ripple.power.wallet.WalletCache;

public final class LSystem {

	final static public String FRAMEWORK_IMG_NAME = "assets/loon_";

	public static boolean AUTO_REPAINT;

	public static int DEFAULT_MAX_FPS = 60;

	public static float EMULATOR_BUTTIN_SCALE = 1f;

	public static RectBox screenRect;

	public static boolean isPaused;
	/**
	 * 返回一个Random对象
	 * 
	 * @return
	 */
	public static Random getRandomObject() {
		return random;
	}

	/**
	 * 返回一个随机数
	 * 
	 * @param i
	 * @param j
	 * @return
	 */
	public static int getRandom(int i, int j) {
		if (j < i) {
			int tmp = j;
			i = tmp;
			j = i;
		}
		return i + random.nextInt((j - i) + 1);
	}

	/**
	 * 返回一个随机数
	 * 
	 * @param i
	 * @param j
	 * @return
	 */
	public static int getRandomBetWeen(int i, int j) {
		if (j < i) {
			int tmp = j;
			i = tmp;
			j = i;
		}
		return LSystem.random.nextInt(j + 1 - i) + i;
	}
	public static void repaint() {
		UIContext context = getInstance().getContext();
		if (context != null) {
			context.getView().update();
		}
	}

	public static void repaint(BufferedImage buffer) {
		UIContext context = getInstance().getContext();
		if (context != null) {
			context.getView().update(buffer);
		}
	}

	public static void repaintLocation(BufferedImage buffer, int x, int y) {
		UIContext context = getInstance().getContext();
		if (context != null) {
			context.getView().updateLocation(buffer, x, y);
		}
	}

	public static void repaint(BufferedImage buffer, int w, int h) {
		UIContext context = getInstance().getContext();
		if (context != null) {
			context.getView().update(buffer, w, h);
		}
	}

	public static void repaintFull(BufferedImage buffer, int w, int h) {
		UIContext context = getInstance().getContext();
		if (context != null) {
			context.getView().updateFull(buffer, w, h);
		}
	}

	public static SystemTimer getSystemTimer() {
		return new SystemTimer();
	}

	/**
	 * 清空系统缓存资源
	 * 
	 */
	public static void destroy() {
		GraphicsUtils.destroyImages();
		Resources.destroy();
	}

	/**
	 * 退出游戏引擎
	 * 
	 */
	public static void exit() {
		LSystem.destroy();
		System.exit(0);
	}

	public static class GameManager {

		private SystemTimer timer;

		private UIContext mainContext, initContext;

		private List<UIContext> allContexts;

		private Thread initContextThread;

		private GameManager() {
			try {
				timer = new NanoTimer();
				return;
			} catch (Throwable t) {
				t.printStackTrace();
			}
		}

		public UIContext getContext() {

			if (allContexts == null) {
				return mainContext;
			}

			synchronized (this) {
				if (allContexts == null) {
					return mainContext;
				}

				ThreadGroup currentThreadGroup = Thread.currentThread()
						.getThreadGroup();
				for (int i = 0; i < allContexts.size(); i++) {
					UIContext context = (UIContext) allContexts.get(i);
					ThreadGroup contextThreadGroup = context.getThreadGroup();
					if (contextThreadGroup == currentThreadGroup
							|| contextThreadGroup.parentOf(currentThreadGroup)) {
						return context;
					}
				}

				if (initContext != null
						&& Thread.currentThread() == initContextThread) {
					return initContext;
				}

				return (UIContext) (allContexts
						.get(UIContext.nextContextID - 1));

			}
		}

		private synchronized UIContext getAppContext(UIView app) {
			if (mainContext != null && mainContext.getView() == app) {
				return mainContext;
			}

			if (allContexts != null) {
				for (int i = 0; i < allContexts.size(); i++) {
					UIContext context = (UIContext) allContexts.get(i);
					if (context.getView() == app) {
						return context;
					}
				}
			}

			return null;
		}

		public synchronized boolean isRegistered(UIView app) {
			return (getAppContext(app) != null);
		}

		private synchronized int getNumRegisteredApps() {
			if (allContexts == null) {
				if (mainContext == null) {
					return 0;
				} else {
					return 1;
				}
			} else {
				return allContexts.size();
			}
		}

		public synchronized UIContext registerApp(UIView app) {

			if (app == null) {
				return null;
			}

			UIContext context = getAppContext(app);
			if (context != null) {
				return context;
			}

			boolean wasEmpty = (getNumRegisteredApps() == 0);

			UIContext newContext = new UIContext(app, timer);
			if (mainContext == null) {
				mainContext = newContext;
			} else {
				if (allContexts == null) {
					allContexts = new ArrayList<UIContext>();
					allContexts.add(mainContext);
				}
				allContexts.add(newContext);
			}
			initContext = newContext;
			initContextThread = Thread.currentThread();
			initContext = null;
			initContextThread = null;
			if (wasEmpty) {
				timer.start();
			}

			return newContext;
		}

		public synchronized void unregisterApp(UIView app) {
			if (app == null || !isRegistered(app)) {
				return;
			}

			if (mainContext != null && mainContext.getView() == app) {
				mainContext = null;
			}

			if (allContexts != null) {
				for (int i = 0; i < allContexts.size(); i++) {
					UIContext context = (UIContext) allContexts.get(i);
					if (context.getView() == app) {
						allContexts.remove(i);
						break;
					}
				}

				if (mainContext == null) {
					mainContext = (UIContext) allContexts.get(0);
				}

				if (allContexts.size() == 1) {
					allContexts = null;
				}
			}

			if (getNumRegisteredApps() == 0) {
				timer.stop();
			}
		}

		public long getTimeMillis() {
			return timer.getTimeMillis();
		}

		public long getTimeMicros() {
			return timer.getTimeMicros();
		}

		public LHandler getSystemHandler(int id) {
			if (allContexts != null) {
				if (id > 0 && id < allContexts.size()) {
					return ((UIContext) allContexts.get(id)).getView()
							.getHandler();
				}
			}
			return null;
		}

	}

	final static private GameManager INSTANCE = new GameManager();

	public static GameManager getInstance() {
		return INSTANCE;
	}

	public static LHandler getSystemHandler() {
		UIContext context = INSTANCE.getContext();
		if (context != null) {
			return context.getView().getHandler();
		} else {
			return null;
		}
	}

	/**
	 * 执行一个位于Screen线程中的Runnable
	 * 
	 * @param runnable
	 */
	public final static void callScreenRunnable(Runnable runnable) {
		LHandler process = getSystemHandler();
		if (process != null) {
			Screen screen = process.getScreen();
			if (screen != null) {
				synchronized (screen) {
					screen.callEvent(runnable);
				}
			}
		}
	}

	final static public ClassLoader classLoader;

	private static String MIN_AMOUNT = "0.0001";

	private static String FEE = "0.012";

	public final static String nativeCurrency = "xrp";

	public final static DecimalFormat NUMBER_8_FORMAT = new DecimalFormat(
			"0.00000000");

	public final static DecimalFormat NUMBER_6_FORMAT = new DecimalFormat(
			"0.000000");

	final public static ArrayList<String> send_addresses = new ArrayList<String>(
			1000);

	final static public long SECOND = 1000;
	final static public long MINUTE = SECOND * 60;
	final static public long MSEC = 1L;
	final static public long HOUR = 60 * MINUTE;
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

	private static String applicationPassword = "mynameiscping0o5498^%1032%%76!7*(%$.com%.~";

	public static ProxySettings applicationProxy = null;

	public static MainForm applicationMain = null;

	public static Language applicationLang = Language.DEF;

	public static long applicationSleep = SECOND * 30;

	private static HashMap<String, Session> ripple_store = new HashMap<String, Session>(
			100);

	private static boolean _isWindows = false;
	private static boolean _isWindowsNTor2000 = false;
	private static boolean _isWindowsXP = false;
	private static boolean _isWindowsVista = false;
	private static boolean _isWindows7 = false;
	private static boolean _isWindows8 = false;
	private static boolean _isWindows2003 = false;
	private static boolean _isClassicWindows = false;
	private static boolean _isWindows95 = false;
	private static boolean _isWindows98 = false;
	private static boolean _supportsTray = false;
	private static boolean _isMacClassic = false;
	private static boolean _isMacOSX = false;
	private static boolean _isLinux = false;
	private static boolean _isSolaris = false;
	private static JavaVersion _currentVersion;

	static {
		classLoader = Thread.currentThread().getContextClassLoader();
		String os = getProperty("os.name", "Windows XP");
		_isWindows = os.indexOf("Windows") != -1;
		try {
			String osVersion = getProperty("os.version", "5.0");
			Float version = Float.valueOf(osVersion);
			_isClassicWindows = version <= 4.0;
		} catch (NumberFormatException ex) {
			_isClassicWindows = false;
		}
		if (os.indexOf("Windows XP") != -1 || os.indexOf("Windows NT") != -1
				|| os.indexOf("Windows 2000") != -1) {
			_isWindowsNTor2000 = true;
		}
		if (os.indexOf("Windows XP") != -1) {
			_isWindowsXP = true;
		}
		if (os.indexOf("Windows Vista") != -1) {
			_isWindowsVista = true;
		}
		if (os.indexOf("Windows 7") != -1) {
			_isWindows7 = true;
		}
		if (os.indexOf("Windows 8") != -1) {
			_isWindows8 = true;
		}
		if (os.indexOf("Windows 2003") != -1) {
			_isWindows2003 = true;
			_isWindowsXP = true;
		}
		if (os.indexOf("Windows 95") != -1) {
			_isWindows95 = true;
		}
		if (os.indexOf("Windows 98") != -1) {
			_isWindows98 = true;
		}
		if (_isWindows) {
			_supportsTray = true;
		}
		_isSolaris = (os.indexOf("Solaris") != -1)
				|| (os.indexOf("SunOS") != -1);
		_isLinux = os.indexOf("Linux") != -1;
		if (os.startsWith("Mac OS")) {
			if (os.endsWith("X")) {
				_isMacOSX = true;
			} else {
				_isMacClassic = true;
			}
		}
	}

	public static void setMinAmountAndFee(String amount, String fee) {
		Session system = session("system");
		system.set("min_send", amount.trim());
		system.set("fee", fee.trim());
		system.save();
	}

	public static String getFee() {
		Session system = session("system");
		String result = system.get("fee");
		return result == null ? FEE : result;
	}

	public static String getMinSend() {
		Session system = session("system");
		String result = system.get("min_send");
		return result == null ? MIN_AMOUNT : result;
	}

	public static String getNumberShort(Number value) {
		return LSystem.getNumberShort(String.valueOf(value));
	}

	public static String getNumberShort(String value) {
		return LSystem.getNumber(new BigDecimal(value), false);
	}

	public static String getNumber(BigDecimal value) {
		return LSystem.getNumber(value, true);
	}

	public static String getNumber(BigDecimal big, boolean flag) {
		StringBuilder sbr = null;
		if (flag) {
			sbr = new StringBuilder(NUMBER_8_FORMAT.format(big));
		} else {
			sbr = new StringBuilder(NUMBER_6_FORMAT.format(big));
		}
		if (sbr.toString().indexOf('.') != -1) {
			int size = sbr.length();
			for (int i = 0; i < size; i++) {
				if (sbr.toString().endsWith("0")) {
					sbr.delete(sbr.length() - 1, sbr.length());
				} else {
					break;
				}
			}
		}
		if (sbr.toString().endsWith(".")) {
			sbr.delete(sbr.length() - 1, sbr.length());
		}
		return sbr.toString();
	}

	public static void putThread(final Runnable runnable) {
		ThreadPoolService.addWork(runnable);
	}

	public static void submitThread() {
		ThreadPoolService.exectueAll();
	}

	public static Thread postThread(final Updateable update) {
		Thread thread = new Thread() {
			public void run() {
				if (update != null) {
					update.action(null);
				}
			}
		};
		thread.start();
		return thread;
	}

	public static void invokeLater(final Updateable update) {
		SwingUtilities.invokeLater(new Runnable() {
			@Override
			public void run() {
				if (update != null) {
					update.action(null);
				}
			}
		});
	}

	public static String getProperty(String key, String defaultValue) {
		try {
			return System.getProperty(key, defaultValue);
		} catch (AccessControlException e) {
			return defaultValue;
		}
	}

	public static String getJavaVersion() {
		return getProperty("java.version", "1.4.2");
	}

	public static String getJavaVendor() {
		return getProperty("java.vendor", "");
	}

	public static String getJavaClassVersion() {
		return getProperty("java.class.version", "");
	}

	public static String getOS() {
		return getProperty("os.name", "Windows");
	}

	public static String getOSVersion() {
		return getProperty("os.version", "");
	}

	public static String getOSArchitecture() {
		return getProperty("os.arch", "");
	}

	public static String getCurrentDirectory() {
		return getProperty("user.dir", "");
	}

	public static File getUserHomeDir() {
		return new File(getUserHome());
	}

	public static String getUserHome() {
		return getProperty("user.home", "");
	}

	public static String getUserName() {
		return getProperty("user.name", "");
	}

	public static boolean supportsTray() {
		return _supportsTray;
	}

	public static void setSupportsTray(boolean support) {
		_supportsTray = support;
	}

	public static boolean isWindows() {
		return _isWindows;
	}

	public static boolean isClassicWindows() {
		return _isClassicWindows;
	}

	public static boolean isWindowsNTor2000() {
		return _isWindowsNTor2000;
	}

	public static boolean isWindowsXP() {
		return _isWindowsXP;
	}

	public static boolean isWindowsVista() {
		return _isWindowsVista;
	}

	public static boolean isWindows7() {
		return _isWindows7;
	}

	public static boolean isWindows8() {
		return _isWindows8;
	}

	public static boolean isWindowsVistaAbove() {
		return _isWindowsVista || _isWindows7 || _isWindows8;
	}

	public static boolean isWindows95() {
		return _isWindows95;
	}

	public static boolean isWindows98() {
		return _isWindows98;
	}

	public static boolean isWindows2003() {
		return _isWindows2003;
	}

	public static boolean isMacClassic() {
		return _isMacClassic;
	}

	public static boolean isMacOSX() {
		return _isMacOSX;
	}

	public static boolean isAnyMac() {
		return _isMacClassic || _isMacOSX;
	}

	public static boolean isSolaris() {
		return _isSolaris;
	}

	public static boolean isLinux() {
		return _isLinux;
	}

	public static boolean isUnix() {
		return _isLinux || _isSolaris;
	}

	private static void checkJdkVersion() {
		if (_currentVersion == null) {
			_currentVersion = new JavaVersion(getJavaVersion());
		}
	}

	public static boolean isJdk13Above() {
		checkJdkVersion();
		return _currentVersion.compareVersion(1.3, 0, 0) >= 0;
	}

	public static boolean isJdk142Above() {
		checkJdkVersion();
		return _currentVersion.compareVersion(1.4, 2, 0) >= 0;
	}

	public static boolean isJdk14Above() {
		checkJdkVersion();
		return _currentVersion.compareVersion(1.4, 0, 0) >= 0;
	}

	public static boolean isJdk15Above() {
		checkJdkVersion();
		return _currentVersion.compareVersion(1.5, 0, 0) >= 0;
	}

	public static boolean isJdk6Above() {
		checkJdkVersion();
		return _currentVersion.compareVersion(1.6, 0, 0) >= 0;
	}

	public static boolean isJdk6u10Above() {
		checkJdkVersion();
		return _currentVersion.compareVersion(1.6, 0, 10) >= 0;
	}

	public static boolean isJdk6u14Above() {
		checkJdkVersion();
		return _currentVersion.compareVersion(1.6, 0, 14) >= 0;
	}

	public static boolean isJdk6u25Above() {
		checkJdkVersion();
		return _currentVersion.compareVersion(1.6, 0, 25) >= 0;
	}

	public static boolean isJdk7Above() {
		checkJdkVersion();
		return _currentVersion.compareVersion(1.7, 0, 0) >= 0;
	}

	public static boolean isJdk8Above() {
		checkJdkVersion();
		return _currentVersion.compareVersion(1.8, 0, 0) >= 0;
	}

	public static boolean isJdkVersion(double majorVersion, int minorVersion,
			int build) {
		checkJdkVersion();
		return _currentVersion
				.compareVersion(majorVersion, minorVersion, build) == 0;
	}

	public static boolean isJdkVersionAbove(double majorVersion,
			int minorVersion, int build) {
		checkJdkVersion();
		return _currentVersion
				.compareVersion(majorVersion, minorVersion, build) >= 0;
	}

	public static boolean isJdkVersionBelow(double majorVersion,
			int minorVersion, int build) {
		checkJdkVersion();
		return _currentVersion
				.compareVersion(majorVersion, minorVersion, build) <= 0;
	}

	public static boolean isMinJavaVersion(int major, int minor) {
		String version = System.getProperty("java.version");
		String installedVersion = version;
		int index = version.indexOf("_");
		if (index > 0) {
			installedVersion = version.substring(0, index);
		}
		String[] installed = StringUtils.split(installedVersion, ".");
		if (installed.length < 2) {
			return false;
		}
		int _version = Integer.parseInt(installed[0]);
		if (_version < major) {
			return false;
		}
		_version = Integer.parseInt(installed[1]);
		if (_version < minor) {
			return false;
		}
		return true;
	}

	public static void sendRESTCoin(String address, String name, String label,
			long amount) {
		LSystem.sendRESTCoin(address, name, label, amount,
				nativeCurrency.toUpperCase(), MathUtils.random(1, 9999));
	}

	public static void sendRESTCoin(String address, String name, String label,
			long amount, String currency, long dt) {
		String page = "https://ripple.com//send?to=" + address + "&name="
				+ name + "&label=" + label.replace(" ", "%20") + "&amount="
				+ amount + "/" + currency + "&dt=" + dt;
		openURL(page);
	}

	public static void openURL(String url) {
		try {
			java.net.URI uri = new java.net.URI(url);
			java.awt.Desktop.getDesktop().browse(uri);
		} catch (Exception e) {
			try {
				browse(url);
			} catch (Exception err) {
				err.printStackTrace();
			}
		}
	}

	private static void browse(String url) throws Exception {
		String osName = System.getProperty("os.name", "");
		if (osName.startsWith("Mac OS")) {
			Class<?> fileMgr = Class.forName("com.apple.eio.FileManager");
			Method openURL = fileMgr.getDeclaredMethod("openURL",
					new Class[] { String.class });
			openURL.invoke(null, new Object[] { url });
		} else if (osName.startsWith("Windows")) {
			Runtime.getRuntime().exec(
					"rundll32 url.dll,FileProtocolHandler " + url);
		} else {
			String[] browsers = { "firefox", "opera", "konqueror", "epiphany",
					"mozilla", "netscape" };
			String browser = null;
			for (int count = 0; count < browsers.length && browser == null; count++) {
				if (Runtime.getRuntime()
						.exec(new String[] { "which", browsers[count] })
						.waitFor() == 0) {
					browser = browsers[count];
				}
			}
			if (browser == null) {
				throw new Exception("Could not find web browser");
			} else {
				Runtime.getRuntime().exec(new String[] { browser, url });
			}
		}
	}

	public static String getIPAddress() throws UnknownHostException {
		InetAddress address = InetAddress.getLocalHost();
		return address.getHostAddress();
	}

	public static String getIPAddressType() {
		InetAddress address;
		try {
			address = InetAddress.getLocalHost();
			if (IP46Utils.isIPv4Address(address.getHostAddress())) {
				return "IPv4";
			} else if (IP46Utils.isIPv6Address(address.getHostAddress())) {
				return "IPv6";
			}
		} catch (UnknownHostException e) {
			return "Unkown";
		}
		return "Unkown";
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
		} catch (Throwable e) {
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
			if (LSystem.applicationMain != null) {
				SwingUtils.close(LSystem.applicationMain);
			}
			ApplicationInfo.unlock();
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

	final static public String EMPTY = "";

	final static public String QUOTE_STRING = "'";

	final static public char QUOTE_CHAR = QUOTE_STRING.charAt(0);

	final static public char NEW_LINE_CHAR = LS.charAt(0);

	final static public char TAB_CHAR = '\t';

	final static public char COMMA_CHAR = ',';

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

	public static ApplicationVersion getVersion() {
		return new ApplicationVersion(applicationVersion, applicationName);
	}

	public static class JavaVersion {

		private static Pattern SUN_JAVA_VERSION = Pattern
				.compile("(\\d+\\.\\d+)(\\.(\\d+))?(_([^-]+))?(.*)");
		private static Pattern SUN_JAVA_VERSION_SIMPLE = Pattern
				.compile("(\\d+\\.\\d+)(\\.(\\d+))?(.*)");
		private double _majorVersion;
		private int _minorVersion;
		private int _buildNumber;
		private String _patch;

		public JavaVersion(String version) {
			_majorVersion = 1.4;
			_minorVersion = 0;
			_buildNumber = 0;
			try {
				Matcher matcher = SUN_JAVA_VERSION.matcher(version);
				if (matcher.matches()) {
					int groups = matcher.groupCount();
					_majorVersion = Double.parseDouble(matcher.group(1));
					if (groups >= 3 && matcher.group(3) != null) {
						_minorVersion = Integer.parseInt(matcher.group(3));
					}
					if (groups >= 5 && matcher.group(5) != null) {
						try {
							_buildNumber = Integer.parseInt(matcher.group(5));
						} catch (NumberFormatException e) {
							_patch = matcher.group(5);
						}
					}
					if (groups >= 6 && matcher.group(6) != null) {
						String s = matcher.group(6);
						if (s != null && s.trim().length() > 0)
							_patch = s;
					}
				}
			} catch (NumberFormatException e) {
				try {
					Matcher matcher = SUN_JAVA_VERSION_SIMPLE.matcher(version);
					if (matcher.matches()) {
						int groups = matcher.groupCount();
						_majorVersion = Double.parseDouble(matcher.group(1));
						if (groups >= 3 && matcher.group(3) != null) {
							_minorVersion = Integer.parseInt(matcher.group(3));
						}
					}
				} catch (NumberFormatException e1) {
					System.err
							.println("Please check the installation of your JDK. The version number "
									+ version + " is not right.");
				}
			}
		}

		public JavaVersion(double major, int minor, int build) {
			_majorVersion = major;
			_minorVersion = minor;
			_buildNumber = build;
		}

		public int compareVersion(double major, int minor, int build) {
			double majorResult = _majorVersion - major;
			if (majorResult != 0) {
				return majorResult < 0 ? -1 : 1;
			}
			int result = _minorVersion - minor;
			if (result != 0) {
				return result;
			}
			return _buildNumber - build;
		}

		public double getMajorVersion() {
			return _majorVersion;
		}

		public int getMinorVersion() {
			return _minorVersion;
		}

		public int getBuildNumber() {
			return _buildNumber;
		}

		public String getPatch() {
			return _patch;
		}
	}

	public static String setPassword(String password) throws Exception {
		if (StringUtils.isEmpty(password)) {
			return null;
		}
		password = password.trim();
		byte[] buffer = null;
		try {
			buffer = password.getBytes(LSystem.encoding);
		} catch (Exception ex) {
			buffer = password.getBytes();
		}
		OpenSSL ssl = new OpenSSL();
		buffer = ssl.encrypt(buffer, LSystem.getUserName()
				+ LSystem.applicationName + LSystem.getMACAddress());
		return CoinUtils.toHex(buffer);
	}

	public static String getPassword(String password) throws Exception {
		if (StringUtils.isEmpty(password)) {
			return null;
		}
		password = password.trim();
		OpenSSL ssl = new OpenSSL();
		byte[] buffer = CoinUtils.fromHex(password);
		buffer = ssl.decrypt(buffer, LSystem.getUserName()
				+ LSystem.applicationName + LSystem.getMACAddress());
		password = new String(buffer, LSystem.encoding);
		LSystem.applicationPassword = password.trim();
		return LSystem.applicationPassword;
	}

	public static String getAppPassword() {
		return applicationPassword.trim();
	}
}
