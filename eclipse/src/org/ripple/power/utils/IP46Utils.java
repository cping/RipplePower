package org.ripple.power.utils;

import java.math.BigInteger;
import java.net.Inet4Address;
import java.net.Inet6Address;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;

import org.ripple.power.config.LSystem;

public class IP46Utils {

	public static class InetAddressComparator implements
			Comparator<InetAddress> {
		private static final Inet4AddressComparator V4_COMPARATOR = new Inet4AddressComparator();
		private static final Inet6AddressComparator V6_COMPARATOR = new Inet6AddressComparator();
		private final boolean fourLessThanSix;

		public InetAddressComparator(final boolean fourLessThanSix) {
			this.fourLessThanSix = fourLessThanSix;
		}

		@Override
		public int compare(final InetAddress ip1, final InetAddress ip2) {
			if (ip1 instanceof Inet4Address) {
				if (ip2 instanceof Inet4Address) {
					return V4_COMPARATOR.compare((Inet4Address) ip1,
							(Inet4Address) ip2);
				} else {
					return this.fourLessThanSix ? -1 : 1;
				}
			} else {
				if (ip2 instanceof Inet4Address) {
					return this.fourLessThanSix ? 1 : -1;
				} else {
					return V6_COMPARATOR.compare((Inet6Address) ip1,
							(Inet6Address) ip2);
				}
			}
		}
	}

	private static class Inet4AddressComparator implements
			Comparator<Inet4Address> {

		@Override
		public int compare(final Inet4Address ipOne, final Inet4Address ipTwo) {
			final byte[] bytes1 = ipOne.getAddress();
			final byte[] bytes2 = ipTwo.getAddress();
			for (int i = 0; i < 4; i++) {
				final int a = bytes1[i] & 0xff;
				final int b = bytes2[i] & 0xff;
				if (a != b) {
					return a - b;
				}
			}
			return 0;
		}
	}

	private static class Inet6AddressComparator implements
			Comparator<Inet6Address> {

		@Override
		public int compare(final Inet6Address ipOne, final Inet6Address ipTwo) {
			final byte[] bytes1 = ipOne.getAddress();
			final byte[] bytes2 = ipTwo.getAddress();
			for (int i = 0; i < 16; i++) {
				final int a = bytes1[i] & 0xff;
				final int b = bytes2[i] & 0xff;
				if (a != b) {
					return a - b;
				}
			}
			return 0;
		}
	}

	public static final int UNSPEC_TYPE = 0;
	public static final int LOOPBACK_TYPE = 1;
	public static final int MULTICAST_TYPE = 2;
	public static final int LL_UNICAST_TYPE = 3;
	public static final int GL_UNICAST_TYPE = 4;
	private static Pattern VALID_IPV4_PATTERN = null;
	private static Pattern VALID_IPV6_PATTERN = null;
	private static final String ipv4Pattern = "(([01]?\\d\\d?|2[0-4]\\d|25[0-5])\\.){3}([01]?\\d\\d?|2[0-4]\\d|25[0-5])";
	private static final String ipv6Pattern = "^(((?=(?>.*?::)(?!.*::)))(::)?([0-9A-F]{1,4}::?){0,5}|([0-9A-F]{1,4}:){6})(\\2([0-9A-F]{1,4}(::?|$)){0,2}|((25[0-5]|(2[0-4]|1\\d|[1-9])?\\d)(\\.|$)){4}|[0-9A-F]{1,4}:[0-9A-F]{1,4})(?<![^:]:|\\.)\\z";
	static {
		try {
			VALID_IPV4_PATTERN = Pattern.compile(ipv4Pattern,
					Pattern.CASE_INSENSITIVE);
			VALID_IPV6_PATTERN = Pattern.compile(ipv6Pattern,
					Pattern.CASE_INSENSITIVE);
		} catch (PatternSyntaxException e) {
		}
	}

	private final String cidr;
	private InetAddress inetAddress;
	private InetAddress startAddress;
	private InetAddress endAddress;
	private final int prefixLength;

	public IP46Utils(String cidr) throws UnknownHostException {
		this.cidr = cidr;
		if (this.cidr.contains("/")) {
			int index = this.cidr.indexOf("/");
			String addressPart = this.cidr.substring(0, index);
			String networkPart = this.cidr.substring(index + 1);
			inetAddress = InetAddress.getByName(addressPart);
			prefixLength = Integer.parseInt(networkPart);
			calculate();
		} else {
			throw new IllegalArgumentException(
					"not an valid IPv4 or IPv6 format !");
		}
	}

	private void calculate() throws UnknownHostException {
		ByteBuffer maskBuffer;
		int targetSize;
		if (inetAddress.getAddress().length == 4) {
			maskBuffer = ByteBuffer.allocate(4).putInt(-1);
			targetSize = 4;
		} else {
			maskBuffer = ByteBuffer.allocate(16).putLong(-1L).putLong(-1L);
			targetSize = 16;
		}
		BigInteger mask = (new BigInteger(1, maskBuffer.array())).not()
				.shiftRight(prefixLength);
		ByteBuffer buffer = ByteBuffer.wrap(inetAddress.getAddress());
		BigInteger ipVal = new BigInteger(1, buffer.array());
		BigInteger startIp = ipVal.and(mask);
		BigInteger endIp = startIp.add(mask.not());
		byte[] startIpArr = toBytes(startIp.toByteArray(), targetSize);
		byte[] endIpArr = toBytes(endIp.toByteArray(), targetSize);
		this.startAddress = InetAddress.getByAddress(startIpArr);
		this.endAddress = InetAddress.getByAddress(endIpArr);
	}

	private byte[] toBytes(byte[] array, int targetSize) {
		int counter = 0;
		List<Byte> newArr = new ArrayList<Byte>();
		while (counter < targetSize && (array.length - 1 - counter >= 0)) {
			newArr.add(0, array[array.length - 1 - counter]);
			counter++;
		}
		int size = newArr.size();
		for (int i = 0; i < (targetSize - size); i++) {
			newArr.add(0, (byte) 0);
		}
		byte[] ret = new byte[newArr.size()];
		for (int i = 0; i < newArr.size(); i++) {
			ret[i] = newArr.get(i);
		}
		return ret;
	}

	public String getNetworkAddress() {
		return this.startAddress.getHostAddress();
	}

	public String getBroadcastAddress() {
		return this.endAddress.getHostAddress();
	}

	public boolean isInRange(String ip) throws UnknownHostException {
		InetAddress address = InetAddress.getByName(ip);
		BigInteger start = new BigInteger(1, this.startAddress.getAddress());
		BigInteger end = new BigInteger(1, this.endAddress.getAddress());
		BigInteger target = new BigInteger(1, address.getAddress());
		int st = start.compareTo(target);
		int te = target.compareTo(end);
		return (st == -1 || st == 0) && (te == -1 || te == 0);
	}

	public static boolean isIPv4Address(String ipAddress) {
		Matcher m1 = VALID_IPV4_PATTERN.matcher(ipAddress);
		return m1.matches();
	}

	public static boolean isIPv6Address(String ipAddress) {
		Matcher m2 = VALID_IPV6_PATTERN.matcher(ipAddress);
		return m2.matches();
	}

	public static boolean isIpAddress(String ipAddress) {
		Matcher m1 = VALID_IPV4_PATTERN.matcher(ipAddress);
		if (m1.matches()) {
			return true;
		}
		Matcher m2 = VALID_IPV6_PATTERN.matcher(ipAddress);
		return m2.matches();
	}

	static boolean isUnspecified(Inet6Address a) {
		return false;
	}

	static boolean isLoopback(Inet6Address a) {
		return a.isLoopbackAddress();
	}

	static boolean isMulticast(Inet6Address a) {
		return a.isMulticastAddress();
	}

	static boolean isLLunicast(Inet6Address a) {
		return a.isLinkLocalAddress();
	}

	static boolean isGLunicast(Inet6Address a) {
		if (!isUnspecified(a) && !isLoopback(a) && !isMulticast(a)
				&& !isLLunicast(a)) {
			return true;
		} else {
			return false;
		}
	}

	public static int getType(Inet6Address a) {
		if (isUnspecified(a)) {
			return UNSPEC_TYPE;
		} else if (isLoopback(a)) {
			return LOOPBACK_TYPE;
		} else if (isMulticast(a)) {
			return MULTICAST_TYPE;
		} else if (isLLunicast(a)) {
			return LL_UNICAST_TYPE;
		} else if (isGLunicast(a)) {
			return GL_UNICAST_TYPE;
		}
		return -1;
	}

	public static String getTypeAsString(Inet6Address a) {
		String[] types = new String[] { "unspecified", "loopback", "multicast",
				"link-layer unicast", "global unicast" };
		int x = getType(a);
		return types[x];
	}

	public static Inet4Address randomInet4Address() {
		final byte[] bytes = new byte[4];
		LSystem.random.nextBytes(bytes);
		try {
			return (Inet4Address) InetAddress.getByAddress(bytes);
		} catch (final UnknownHostException uhe) {
			return null;
		}
	}

	public static Inet6Address randomInet6Address() {
		final byte[] bytes = new byte[16];
		LSystem.random.nextBytes(bytes);
		try {
			return (Inet6Address) InetAddress.getByAddress(bytes);
		} catch (final UnknownHostException uhe) {
			return null;
		}
	}

	public static InetAddress randomInetAddress() {
		return LSystem.random.nextBoolean() ? randomInet4Address()
				: randomInet6Address();
	}
}
