package org.ripple.power;

import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Stack;

import javax.script.Invocable;
import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;

import org.json.JSONObject;
import org.ripple.power.collection.Array;
import org.ripple.power.collection.ArrayByte;
import org.ripple.power.config.LSystem;
import org.ripple.power.password.PasswordEasy;
import org.ripple.power.ui.UIRes;
import org.ripple.power.utils.FileUtils;
import org.ripple.power.utils.HttpRequest;
import org.ripple.power.utils.MathUtils;
import org.ripple.power.utils.StringUtils;

import com.google.zxing.common.BitArray;
import com.ripple.core.coretypes.hash.Hash256;
import com.ripple.crypto.sjcljson.JSEscape;
import com.ripple.crypto.sjcljson.JSONEncrypt;

public class RippleBlobObj {

	private static String sjcl1 = null;

	private static ScriptEngine engine = null;

	private static void initSjcl() throws Exception {
		if (engine == null) {
			ScriptEngineManager factory = new ScriptEngineManager();
			engine = factory.getEngineByName("js");
			engine.put("engine", engine);
			if (sjcl1 == null) {
				BufferedReader reader = new BufferedReader(
						new InputStreamReader(UIRes.getStream("sjs.js")));
				StringBuilder sbr = new StringBuilder();
				String line = null;
				for (; (line = reader.readLine()) != null;) {
					line = line.trim();
					if (line.length() > 0) {
						if (!line.startsWith("\\")) {
							sbr.append(line);
							sbr.append("\n");
						}
					}
				}
				reader.close();
				sjcl1 = sbr.toString();
			}
			engine.eval(sjcl1);
		}
	}

	public static String loadData(String host, String exponent, String modulus,
			String alpha, String purpose, String user, String secret)
			throws Exception {
		initSjcl();
		Invocable inv = (Invocable) engine;
		return (String) inv.invokeFunction("loadData", host, exponent, modulus,
				alpha, purpose, user, secret);
	}

	public static String keyHash(String key, String token) throws Exception {
		initSjcl();
		Invocable inv = (Invocable) engine;
		return (String) inv.invokeFunction("keyHash", key, token);
	}

	public static String loadData2(String model,String signres, String random,
			String modulus) throws Exception {
		initSjcl();
		Invocable inv = (Invocable) engine;
		return (String) inv.invokeFunction("loadData2",model, signres, random,
				modulus);
	}

	public static String decrypt(String key, String data) throws Exception {
		initSjcl();
		Invocable inv = (Invocable) engine;
		return (String) inv.invokeFunction("decrypt", key, data);
	}

	public static class BitArray {

		public static Array<Integer> bitSlice(Array<Integer> a, int bstart,
				int bend) {
			a = _shiftRight(a.slice(bstart / 32), 32 - (bstart & 31), 0, null)
					.slice(1);
			return (bend == 0) ? a : clamp(a, bend - bstart);
		}

		public static int getPartial(int x) {
			long r = Math.round(x / 0x10000000000l);
			return (int) (r | 32);
		}

		public static Array<Integer> _shiftRight(Array<Integer> a, int shift,
				int carry, Array<Integer> out) {
			int i, last2 = 0;
			long shift2;
			if (out == null) {
				out = new Array<Integer>();
			}

			for (; shift >= 32; shift -= 32) {
				out.add(carry);
				carry = 0;
			}
			if (shift == 0) {
				return out.concat(a);
			}

			for (i = 0; i < a.size(); i++) {
				out.add(carry | a.get(i) >>> shift);
				carry = a.get(i) << (32 - shift);
			}
			last2 = a.size() > 0 ? a.get(a.size() - 1) : 0;
			shift2 = getPartial(last2);
			out.add(partial((int) (shift + shift2 & 31),
					(shift + shift2 > 32) ? carry : out.pop(), true));
			return out;
		}

		public static Array<Integer> _xor4(int[] x, int[] y) {
			Array<Integer> out = new Array<Integer>();
			out.add(x[0] ^ y[0]);
			out.add(x[1] ^ y[1]);
			out.add(x[2] ^ y[2]);
			out.add(x[3] ^ y[3]);

			return out;
		}

		public static Array<Integer> clamp(Array<Integer> a, int len) {
			if (a.size() * 32 < len) {
				return a;
			}
			a = a.slice(0, (int) Math.ceil(len / 32));
			int l = a.size();
			len = len & 31;
			if (l > 0 && len > 0) {
				a.set(l - 1,
						partial(len, a.get(l - 1) & 0x80000000 >> (len - 1),
								true));
			}
			return a;
		}

		public static Array<Integer> concat(Array<Integer> a1, Array<Integer> a2) {
			if (a1.size() == 0 || a2.size() == 0) {
				return a1.concat(a2);
			}
			int last = a1.get(a1.size() - 1), shift = getPartial(last);
			if (shift == 32) {
				return a1.concat(a2);
			} else {
				return _shiftRight(a2, shift, last | 0,
						a1.slice(0, a1.size() - 1));
			}
		}

		public static int partial(int len, int x, boolean _end) {
			if (len == 32) {
				return x;
			}
			return (int) ((_end ? x | 0 : x << (32 - len)) + len * 0x10000000000l);
		}
	}

	public static class ExampleData {
		public String id = "57d6ed12d3b98ca91b61afac2fb30212f642daabefd9c7cda623f145f384830c";
		public String crypt = "1733480ceea2970e5f979c8d8e508d79e446b42d54f593e640814ea91deb53ef";
		public String unlock = "452b02b80469a6a2ad692264c04d2a3794ea0ab11d8c902ef774190294db2ce2";
		public String blobURL = "https://id.staging.ripple.com";
		public String username = "testUser";
		public String password = "pass word";
		public String domain = "staging.ripple.com";
		public String encrypted_secret = "QUh5dnBqR0pTTVpjcjVoY0FhN1cxcEdTdW1XS1hLS2VzNlpQT2ZvQkFJWmg1UHRYS1RobUhKTkZUcWNyNlZEVlZYZDNhS1l0";
	}

	static final BigInteger TWO = new BigInteger("2");
	static final BigInteger THREE = TWO.add(BigInteger.ONE);
	static final BigInteger FOUR = TWO.add(TWO);
	static final BigInteger SEVEN = FOUR.add(THREE);
	static final BigInteger EIGHT = FOUR.add(FOUR);

	public static class AuthInfoRes {
		public int version;
		public String blobvault;
		public boolean exists;
		public String username;
		public String address;
		public boolean emailVerified;
		public boolean reserved;
		public boolean profile_verified;
		public boolean identity_verified;
	}

	public final static String authinfo_url = "https://id.ripple.com/v1/authinfo";

	private String baseUrl;

	public JSONEncrypt sjcl = new JSONEncrypt();

	public RippleBlobObj(String baseUrl) {
		this.baseUrl = baseUrl;
	}

	public RippleBlobObj() {
		this(authinfo_url);
	}

	public boolean derive(final String user, final String secret)
			throws Exception {

		final String purpose = "login";
		String page = baseUrl + "?domain=rippletrade.com&username=" + user;

		HttpRequest request = HttpRequest.get(page);

		if (request.ok()) {
			AuthInfoRes res = new AuthInfoRes();
			String data = request.body();
			JSONObject obj = new JSONObject(data);
			if (obj.has("version")) {
				res.version = obj.getInt("version");
			}
			if (obj.has("blobvault")) {
				res.blobvault = obj.getString("blobvault");
			}
			if (obj.has("exists")) {
				res.exists = obj.getBoolean("exists");
			}
			if (obj.has("username")) {
				res.username = obj.getString("username");
			}
			if (obj.has("address")) {
				res.address = obj.getString("address");
			}
			if (obj.has("emailVerified")) {
				res.emailVerified = obj.getBoolean("emailVerified");
			}
			if (obj.has("reserved")) {
				res.reserved = obj.getBoolean("reserved");
			}
			if (obj.has("profile_verified")) {
				res.profile_verified = obj.getBoolean("profile_verified");
			}
			if (obj.has("identity_verified")) {
				res.identity_verified = obj.getBoolean("identity_verified");
			}
			if (obj.has("pakdf")) {

				JSONObject pakdf = obj.getJSONObject("pakdf");
				String host = pakdf.getString("host");
				String exponent = pakdf.getString("exponent");
				String alpha = pakdf.getString("alpha");
				String url = pakdf.getString("url");
				String modulus = pakdf.getString("modulus");

				System.out.println(url);
				try {
					String result = loadData(host, exponent, modulus, alpha,
							purpose, user, secret);
					String[] split = StringUtils.split(result, ",");
					String publicinfo = split[0];
					String signreq = split[1];
					String random = split[2];
					modulus = split[3];
					HttpRequest post = HttpRequest
							.post("https://auth1.ripple.com/api/sign");
					post.send("info=" + publicinfo + "&signreq=" + signreq);
					if (post.ok()) {
						JSONObject json = new JSONObject(post.body());
						String signres = json.getString("signres");
						split = StringUtils.split(
								loadData2("login",signres, random, modulus), ",");
						String id = split[0];
						String crypt = split[1];
						System.out.println(crypt);
						System.out.println(id);
						page = "https://id.ripple.com/v1/blob/" + id
								+ "?device_id=" + createSecret(32);
						HttpRequest get = HttpRequest.get(page);
						if (get.ok()) {
							JSONObject blob_obj = new JSONObject(get.body());
							if (blob_obj.has("result")) {
								String finalResult = blob_obj
										.getString("result");
								if ("success".equals(finalResult)) {
									String encrypted_secret = blob_obj
											.getString("encrypted_secret");
									String blob = blob_obj.getString("blob");
									int revision = blob_obj.getInt("revision");
									String email = blob_obj.getString("email");
									int quota = blob_obj.getInt("quota");
									String identity_id = blob_obj
											.getString("identity_id");
									System.out.println(blob);
									
									//String jsonString = decrypt(crypt, blob);
									//System.out.println(jsonString);
								} else {
									return false;
								}
							}
						}
					}
				} catch (Exception e) {
					e.printStackTrace();
				}

			}

		}

		return false;
	}

	static BigInteger inverseMod(BigInteger a, BigInteger b) {
		BigInteger b0 = b, t, q;
		BigInteger x0 = BigInteger.ZERO, x1 = BigInteger.ONE;
		if (b.equals(BigInteger.ONE))
			return BigInteger.ONE;
		while (a.subtract(BigInteger.ONE).signum() > 0) {
			q = a.divide(b);
			t = b;
			b = a.mod(b);
			a = t;
			t = x0;
			x0 = x1.subtract(q.multiply(x0));
			x1 = t;
		}
		if (x1.signum() < 0)
			x1 = x1.add(b0);
		return x1;
	}

	static BigInteger mulmod(BigInteger a, BigInteger b, BigInteger p) {
		BigInteger r = BigInteger.ZERO;
		while (b.compareTo(BigInteger.ZERO) > 0) {
			if (!b.and(BigInteger.ONE).equals(BigInteger.ZERO)) {
				r = addmod(r, a, p);
			}
			b = b.shiftRight(1);
			a = addmod(a, a, p);
		}
		return r;
	}

	static BigInteger addmod(BigInteger a, BigInteger b, BigInteger p) {
		if (p.subtract(b).compareTo(a) > 0) {
			return a.add(b);
		} else {
			return a.add(b).subtract(p);
		}
	}

	static BigInteger submod(BigInteger a, BigInteger b, BigInteger p) {
		if (a.compareTo(b) >= 0) {
			return a.subtract(b);
		} else {
			return p.subtract(b).add(a);
		}
	}

	static BigInteger powmod(BigInteger a, BigInteger e, BigInteger p) {
		BigInteger r = BigInteger.ONE;
		while (e.compareTo(BigInteger.ZERO) > 0) {
			if (!e.and(BigInteger.ONE).equals(BigInteger.ZERO)) {
				r = mulmod(r, a, p);
			}
			e = e.shiftRight(1);
			a = mulmod(a, a, p);
		}
		return r;
	}

	static int Jacobi(BigInteger m, BigInteger n) {
		if (m.compareTo(n) >= 0) {
			m = m.mod(n);
			return Jacobi(m, n);
		}
		if (n.equals(BigInteger.ONE) || m.equals(BigInteger.ONE)) {
			return 1;
		}
		if (m.equals(BigInteger.ZERO)) {
			return 0;
		}
		int twoCount = 0;
		while (m.mod(TWO) == BigInteger.ZERO) {
			twoCount++;
			m = m.divide(TWO);
		}
		int J2n = n.mod(EIGHT).equals(BigInteger.ONE)
				|| n.mod(EIGHT).equals(SEVEN) ? 1 : -1;
		int rule8multiplier = (twoCount % 2 == 0) ? 1 : J2n;
		int tmp = Jacobi(n, m);
		int rule6multiplier = n.mod(FOUR).equals(BigInteger.ONE)
				|| m.mod(FOUR).equals(BigInteger.ONE) ? 1 : -1;
		return tmp * rule6multiplier * rule8multiplier;
	}

	static int eulerCriterion(BigInteger p, BigInteger a) {
		BigInteger exponent = (p.subtract(BigInteger.ONE)).divide(TWO);
		BigInteger x = a.modPow(exponent, p);
		if (x.equals(BigInteger.ZERO) || x.equals(BigInteger.ONE)) {
			return x.intValue();
		}
		BigInteger y = x.add(BigInteger.ONE).mod(p);
		return (y.equals(BigInteger.ZERO)) ? -1 : 2;
	}

	public String createSecret(int size) {
		PasswordEasy pass = new PasswordEasy();
		pass.setPassMatrix(LSystem.hex16);
		return pass.pass(size);
	}

}
