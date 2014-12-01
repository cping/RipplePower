package org.ripple.power;

import org.json.JSONObject;
import org.ripple.power.collection.LongArray;
import org.ripple.power.config.LSystem;
import org.ripple.power.password.PasswordEasy;
import org.ripple.power.sjcl.BigNumber;
import org.ripple.power.sjcl.BitArray;
import org.ripple.power.sjcl.HMAC;
import org.ripple.power.sjcl.JSCall;
import org.ripple.power.utils.HttpRequest;

import com.ripple.crypto.sjcljson.JSONEncrypt;

public class RippleBlobObj {

	public static String test_keyHash(String res, String token) throws Exception {
		JSCall call = JSCall.get("sjs.js");
		return (String) call.function("keyHash", res, token);
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
	}

	public final static String authinfo_url = "https://id.ripple.com/";

	private final static String authinfo = "v1/authinfo";

	private String baseUrl;

	public JSONEncrypt sjcl = new JSONEncrypt();

	public RippleBlobObj(String baseUrl) {
		this.baseUrl = baseUrl;
	}

	public RippleBlobObj() {
		this(authinfo_url);
	}

	public boolean derive(final String username, final String secret)
			throws Exception {
		final String purpose = "login";
		String page = baseUrl + authinfo + "?domain=rippletrade.com&username="
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
				String publicInfo = "PAKDF_1_0_0:" + host.length() + ":" + host
						+ ":" + username.length() + ":" + username + ":"
						+ purpose.length() + ":" + purpose + ":";
				long publicSize = (long) Math.ceil(Math.min(
						(7 + iModulus.bitLength()) >>> 3, 256) / 8);
				LongArray publicHash = BigNumber.fdh(publicInfo, publicSize);
				String publicHex = BigNumber.hex_fromBits(publicHash);
				BigNumber iPublic = BigNumber.bn(publicHex).setBitM(0);
				String secretInfo = publicInfo + ":" + secret.length() + ":"
						+ secret + ":";
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
					String signres = jsonObject.getString("signres");
					BigNumber iSignres = BigNumber.bn(signres);
					BigNumber iRandomInv = iRandom.inverseMod(iModulus);

					BigNumber iSigned = iSignres.mulmod(iRandomInv, iModulus);

					LongArray key = iSigned.toBits();

					HMAC hmac = new HMAC(key);
					System.out.println(BigNumber.hex_fromBits(BitArray
							.bitSlice(hmac.encrypt("id"), 0, 256)));
					System.out.println("test:"
							+ test_keyHash(iSigned.toString(), "id"));
				}
			}

		}

		return false;
	}

	public String createSecret(int size) {
		PasswordEasy pass = new PasswordEasy();
		pass.setPassMatrix(LSystem.hex16);
		return pass.pass(size);
	}

}
