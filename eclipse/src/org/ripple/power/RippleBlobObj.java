package org.ripple.power;

import org.json.JSONObject;
import org.ripple.power.collection.LongArray;
import org.ripple.power.config.LSystem;
import org.ripple.power.nodejs.BigNumber;
import org.ripple.power.nodejs.BitArray;
import org.ripple.power.nodejs.HMAC;
import org.ripple.power.nodejs.JSCrypt;
import org.ripple.power.password.PasswordEasy;
import org.ripple.power.utils.HttpRequest;
import org.ripple.power.utils.StringUtils;

public class RippleBlobObj {

	public final static int LOGIN = 0;
	public final static int UNLOCK = 1;

	public static String keyHash(LongArray key, String token) throws Exception {
		return BigNumber.hex_fromBits(BitArray.bitSlice(
				new HMAC(key).encrypt(token), 0, 256));
	}

	public static class Missing {
		public String phone_2fa;
		public String region;
		public String phone_verified;
		public String phone;
		public String auth_id_2fa;
		public String country_code_2fa;
		public String enabled_2fa;
		public String city;
		public String country;
	}

	public static class BlobInfoRes {
		public String id;
		public String key;
		public String encrypted_secret;
		public String blob;
		public int revision;
		public boolean success;
		public int quota;
		public String email;
		public String identity_id;
		public Missing missing_fields;
	}

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
		public boolean success;
		public String signreq;
		public String[] result = new String[0];
	}

	public static class UnlockInfoRes {
		public String auth_secret;
		public String ripple_secret;
		public String account_id;
		public String email;
		public String created;
	}

	public static String def_pakdf_name = "PAKDF_1_0_0";

	public static String def_authinfo_url = "https://id.ripple.com/";

	public static String def_domain = "rippletrade.com";

	private final static String authinfo = "v1/authinfo";

	private String baseUrl;

	public RippleBlobObj(String url) {
		this.baseUrl = url;
		if (!baseUrl.endsWith("/")) {
			baseUrl += "/";
		}
	}

	public RippleBlobObj() {
		this(def_authinfo_url);
	}

	public UnlockInfoRes unlock(final String username, final String secret)
			throws Exception {
		return unlock(username, secret, def_domain, def_pakdf_name);
	}

	public UnlockInfoRes unlock(final String username, final String secret,
			final String domain, final String pakdfName) throws Exception {
		BlobInfoRes info = load(username, secret, domain, pakdfName);
		if (info != null && info.success) {
			String result = JSCrypt.decrypt(info.key, info.blob);
			JSONObject json = new JSONObject(result);
			
			UnlockInfoRes res = new UnlockInfoRes();
			res.account_id = json.getString("account_id");
			res.auth_secret = json.getString("auth_secret");
			res.email = json.getString("email");
			res.created = json.getString("created");
			AuthInfoRes unlock_info = derive(username, secret, domain,
					pakdfName, UNLOCK);
			if (unlock_info != null && unlock_info.result.length == 1) {
				String unlock = unlock_info.result[0];
				res.ripple_secret = JSCrypt.decrypt(unlock,
						info.encrypted_secret);
			}
			return res;
		}
		return null;
	}

	public BlobInfoRes load(final String username, final String secret)
			throws Exception {
		return load(username, secret, def_domain, def_pakdf_name);
	}

	public BlobInfoRes load(final String username, final String secret,
			final String domain, final String pakdfName) throws Exception {
		AuthInfoRes info = derive(username, secret, domain, pakdfName, LOGIN);
		if (info != null && info.success) {
			String[] result = info.result;
			if (result != null && result.length == 2) {
				String url = baseUrl + "v1/blob/" + result[0];
				url += "?device_id=" + createSecret(32);
				BlobInfoRes res = new BlobInfoRes();
				res.id = result[0];
				res.key = result[1];
				HttpRequest request = HttpRequest.get(url);
				if (request.ok()) {
					JSONObject json = new JSONObject(request.body());
					res.success = "success".equalsIgnoreCase(json
							.getString("result"));
					if (res.success) {
						res.blob = json.getString("blob");
						res.revision = json.getInt("revision");
						res.quota = json.getInt("quota");
						res.email = json.getString("email");
						res.identity_id = json.getString("identity_id");
						res.encrypted_secret = json
								.getString("encrypted_secret");
						if (json.has("missing_fields")) {
							JSONObject miss = json
									.getJSONObject("missing_fields");
							Missing missing = new Missing();
							missing.phone_2fa = miss.getString("2fa_phone");
							missing.region = miss.getString("region");
							missing.phone_verified = miss
									.getString("phone_verified");
							missing.phone = miss.getString("phone");
							missing.auth_id_2fa = miss.getString("2fa_auth_id");
							missing.country_code_2fa = miss
									.getString("2fa_country_code");
							missing.enabled_2fa = miss.getString("2fa_enabled");
							missing.city = miss.getString("city");
							missing.country = miss.getString("country");
						}
					}
					return res;
				}
			}
		}
		return null;
	}

	public AuthInfoRes derive(final String username, final String secret,
			final int mode) throws Exception {
		return derive(username, secret, def_domain, def_pakdf_name, mode);
	}

	public AuthInfoRes derive(final String username, final String secret,
			final String domain, final String pakdfName, final int mode)
			throws Exception {
		String purpose = null;
		if (mode == 0) {
			purpose = "login";
		} else {
			purpose = "unlock";
		}
		String page = baseUrl + authinfo + "?domain=" + domain + "&username="
				+ username;
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
				BigNumber iExponent = BigNumber.bn(exponent), iModulus = BigNumber
						.bn(modulus), iAlpha = BigNumber.bn(alpha);
				String publicInfo = StringUtils.join(":", pakdfName,
						host.length(), host, username.length(), username,
						purpose.length(), purpose);
				long publicSize = (long) Math.ceil(Math.min(
						(7 + iModulus.bitLength()) >>> 3, 256) / 8);
				LongArray publicHash = BigNumber.fdh(publicInfo, publicSize);
				String publicHex = BigNumber.hex_fromBits(publicHash);
				BigNumber iPublic = BigNumber.bn(publicHex).setBitM(0);
				String secretInfo = StringUtils.join(":", publicInfo,
						secret.length(), secret);
				long secretSize = (7 + iModulus.bitLength()) >>> 3;
				LongArray secretHash = BigNumber.fdh(secretInfo, secretSize);
				String secretHex = BigNumber.hex_fromBits(secretHash);
				BigNumber iSecret = BigNumber.bn(secretHex).mod(iModulus);
				if (iSecret.jacobi(iModulus) != 1) {
					iSecret = iSecret.mul(iAlpha).mod(iModulus);
				}
				BigNumber iRandom;
				for (;;) {
					iRandom = BigNumber.random(iModulus, 0);
					if (iRandom.jacobi(iModulus) == 1) {
						break;
					}
				}
				BigNumber iBlind = iRandom.powermodMontgomery(
						iPublic.mul(iExponent), iModulus), iSignreq = iSecret
						.mulmod(iBlind, iModulus);
				String signreq = BigNumber.hex_fromBits(iSignreq.toBits());

				HttpRequest post = HttpRequest.post(url);
				post.send("info=" + publicInfo + "&signreq=" + signreq);
				if (post.ok()) {
					JSONObject jsonObject = new JSONObject(post.body());
					res.success = "success".equalsIgnoreCase(jsonObject
							.getString("result"));
					if (!res.success) {
						return res;
					}
					res.signreq = jsonObject.getString("signres");
					BigNumber iSignres = BigNumber.bn(res.signreq);
					BigNumber iRandomInv = iRandom.inverseMod(iModulus);
					BigNumber iSigned = iSignres.mulmod(iRandomInv, iModulus);
					LongArray key = iSigned.toBits();
					String[] result = null;
					if (mode == 0) {
						result = new String[] { keyHash(key, "id"),
								keyHash(key, "crypt") };
					} else {
						result = new String[] { keyHash(key, "unlock") };
					}
					res.result = result;

					return res;
				}
			}
		}
		return null;
	}

	public String createSecret(int size) {
		PasswordEasy pass = new PasswordEasy();
		pass.setPassMatrix(LSystem.hex16);
		return pass.pass(size);
	}

}
