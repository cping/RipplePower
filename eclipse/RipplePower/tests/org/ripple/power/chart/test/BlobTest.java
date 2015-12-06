package org.ripple.power.chart.test;

import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Arrays;

import javax.script.Invocable;
import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;

import org.apache.http.client.HttpClient;
import org.json.JSONObject;
import org.ripple.bouncycastle.crypto.InvalidCipherTextException;
import org.ripple.bouncycastle.util.encoders.Base64;
import org.ripple.power.CoinUtils;
import org.ripple.power.Helper;
import org.ripple.power.RippleBlobObj;
import org.ripple.power.RippleBlobObj.UnlockInfoRes;
import org.ripple.power.collection.Array;
import org.ripple.power.collection.ArrayByte;
import org.ripple.power.collection.ArraySlice;
import org.ripple.power.collection.LongArray;
import org.ripple.power.config.LSystem;
import org.ripple.power.nodejs.BigNumber;
import org.ripple.power.nodejs.BitArray;
import org.ripple.power.nodejs.JSCrypt;
import org.ripple.power.txns.RippleHistoryAPI;
import org.ripple.power.ui.UIRes;
import org.ripple.power.utils.Base64Coder;
import org.ripple.power.utils.HttpRequest;
import org.ripple.power.utils.HttpsUtils;
import org.ripple.power.utils.HttpsUtils.ResponseResult;

public class BlobTest {

	public static int extract(byte[] a, int bstart, int blength) {
		int x, sh = (int) Math.floor((-bstart - blength) & 31);
		if (((bstart + blength - 1 ^ bstart) & -32) > 0) {
			x = (a[bstart / 32 | 0] << (32 - sh))
					^ (a[bstart / 32 + 1 | 0] >>> sh);
		} else {
			x = a[bstart / 32 | 0] >>> sh;
		}
		return x & ((1 << blength) - 1);
	}

	// 958019637,2032227926,-1436301260,-1498211948,201000887,1083603298,1592104892,-582399526
	// 958019637,2032227926,-1436301260,-1498211948,201000887,1083603298,1592104892,-582399526
	// https://www.rippletrade.com/
	public static void main(String[] args) throws Exception {
		System.out.println(HttpRequest
				.get("https://id.ripple.com/v1/user/baidu"));
		// String url =
		// "https://history-dev.ripple.com:7443/v1"+"/accounts/"+"rvYAfWj5gh67oV6fW32ZzP3Aw4Eubs59B"+"/transactions";

		// System.out.println(new
		// History("rvYAfWj5gh67oV6fW32ZzP3Aw4Eubs59B").transactions());
		// System.out.println(HttpUtils.getSSL(url, LSystem.encoding,
		// LSystem.encoding).getResult());
		/*
		 * System.out.println(url); HttpRequest request=HttpRequest.get(url);
		 * if(request.ok()){ System.out.println(request.body()); } HttpClient
		 */

		/*
		 * LongArray encryptedBits = BigNumber .base64_toBits(
		 * "AInTBWAlYqS35c2p77mlLFf844OiRH7xQk1IDACXYbyWnJQNXmSpbpTlKQJgPW/ooWikyluC6XOOYfr2ipy77/ky2+gXUcZTcCcOZvPuGPkf/0lp+MKxXgTBK0wgMejcq1MTSViWk2r3T6tQwanF+K//qtBTPy3Sbfz60lTAmpPScPrgfUGuKYdoMdhvizD6Wf4Upex/RM/+HZMnpvJ2gog5T07aC+mIug/TUpxnRtsV1SP/RORMJCnvGh/pwAhx24m9ZjDZT22SN/aKyl1af8SoN3dIfqHAlpsi0APXfHx9WRACgoJhTw8zqQwqETuOFl7UeE+A8nvZJVKkq36fRXVHRWXTdGDcYaNF7HrtbYaYK7d3lYheNYO8OiuAi8EqMzF1iFQw+jlwzLNQHOU2l1P8kaulTkraRPxEYx1GCg=="
		 * ); long version = BitArray.extract(encryptedBits, 0, 8); if (version
		 * != 0) { throw new RuntimeException("Unsupported encryption version: "
		 * + version); }
		 * 
		 * System.out.println(BitArray.bitSlice(encryptedBits, 8+128)); String
		 * iv = BigNumber.base64_fromBits(BitArray.bitSlice(encryptedBits, 8,
		 * 8+128)); String ct =
		 * BigNumber.base64_fromBits(BitArray.bitSlice(encryptedBits, 8+128));
		 * System.out.println(iv); System.out.println(ct); JSONObject obj=new
		 * JSONObject(); obj.put("iv", iv); obj.put("ct", ct); obj.put("adata",
		 * ""); obj.put("mode", "ccm"); obj.put("cipher","aes");
		 * obj.put("ts",64); obj.put("ks",256); obj.put("iter",1000); try {
		 * JSONEncrypt sjcl = new JSONEncrypt();
		 * System.out.println(sjcl.decrypt(
		 * "3b3a53bf1d4d753800044e6f8219cf97d10752850b961ce6f330a74039eeb3f1",
		 * obj)); } catch (InvalidCipherTextException e) { // TODO
		 * Auto-generated catch block e.printStackTrace(); }
		 */
		// System.out.println(JSCrypt.decrypt("3b3a53bf1d4d753800044e6f8219cf97d10752850b961ce6f330a74039eeb3f1","AInTBWAlYqS35c2p77mlLFf844OiRH7xQk1IDACXYbyWnJQNXmSpbpTlKQJgPW/ooWikyluC6XOOYfr2ipy77/ky2+gXUcZTcCcOZvPuGPkf/0lp+MKxXgTBK0wgMejcq1MTSViWk2r3T6tQwanF+K//qtBTPy3Sbfz60lTAmpPScPrgfUGuKYdoMdhvizD6Wf4Upex/RM/+HZMnpvJ2gog5T07aC+mIug/TUpxnRtsV1SP/RORMJCnvGh/pwAhx24m9ZjDZT22SN/aKyl1af8SoN3dIfqHAlpsi0APXfHx9WRACgoJhTw8zqQwqETuOFl7UeE+A8nvZJVKkq36fRXVHRWXTdGDcYaNF7HrtbYaYK7d3lYheNYO8OiuAi8EqMzF1iFQw+jlwzLNQHOU2l1P8kaulTkraRPxEYx1GCg=="
		// ));

		RippleBlobObj rp = new RippleBlobObj();
		UnlockInfoRes res = rp.unlock("htp0943562", "testing0oopg141");
		if (res != null) {
			System.out.println(res.ripple_secret);
		}
		/*
		 * LongArray key = BigNumber .hex_toBits(
		 * "3b3a53bf1d4d753800044e6f8219cf97d10752850b961ce6f330a74039eeb3f1");
		 * LongArray encryptedBits = BigNumber .base64_toBits(
		 * "AInTBWAlYqS35c2p77mlLFf844OiRH7xQk1IDACXYbyWnJQNXmSpbpTlKQJgPW/ooWikyluC6XOOYfr2ipy77/ky2+gXUcZTcCcOZvPuGPkf/0lp+MKxXgTBK0wgMejcq1MTSViWk2r3T6tQwanF+K//qtBTPy3Sbfz60lTAmpPScPrgfUGuKYdoMdhvizD6Wf4Upex/RM/+HZMnpvJ2gog5T07aC+mIug/TUpxnRtsV1SP/RORMJCnvGh/pwAhx24m9ZjDZT22SN/aKyl1af8SoN3dIfqHAlpsi0APXfHx9WRACgoJhTw8zqQwqETuOFl7UeE+A8nvZJVKkq36fRXVHRWXTdGDcYaNF7HrtbYaYK7d3lYheNYO8OiuAi8EqMzF1iFQw+jlwzLNQHOU2l1P8kaulTkraRPxEYx1GCg=="
		 * ); long version = BitArray.extract(encryptedBits, 0, 8); if (version
		 * != 0) { throw new RuntimeException("Unsupported encryption version: "
		 * + version); }
		 * 
		 * System.out.println(BitArray.bitSlice(encryptedBits, 8+128)); String
		 * iv = BigNumber.base64_fromBits(BitArray.bitSlice(encryptedBits, 8,
		 * 8+128)); String ct =
		 * BigNumber.base64_fromBits(BitArray.bitSlice(encryptedBits, 8+128));
		 * System.out.println(iv); System.out.println(ct); JSONObject obj=new
		 * JSONObject(); obj.put("iv", iv); obj.put("ct", ct); obj.put("adata",
		 * ""); obj.put("mode", "ccm"); obj.put("cipher","aes");
		 * obj.put("ts",64); obj.put("ks",256); obj.put("iter",1000);
		 * 
		 * try { JSONEncrypt sjcl = new JSONEncrypt();
		 * System.out.println(sjcl.decrypt
		 * ("3b3a53bf1d4d753800044e6f8219cf97d10752850b961ce6f330a74039eeb3f1",
		 * obj)); } catch (InvalidCipherTextException e) { // TODO
		 * Auto-generated catch block e.printStackTrace(); }
		 */
		// BigNumber key=new
		// BigNumber("3b3a53bf1d4d753800044e6f8219cf97d10752850b961ce6f330a74039eeb3f1");
		// BigNumber blob= BigNumber.bytes_toBits();
		// test
		// 9589865e98180de5b7639e6373bc296c91f0921c5c01fc9e6d4c2dfff92c89abdafd5ebed0ff04dc84641dc2d77a2e5f063da8cf2ee32b8efb111a9620d25be84ebde5f19c53345f235ca6c54963bb588df9cdb7b2724e02c38800a721ef485432c595034157f6519cb7984203dee9f9ebb283e44156966f24a1d6747bb8d7239e6f8061e4d7e30c36719f55c5e41c6df302d47c32094d626ba01359056155756a8c080f4bd6e9e77a8617f75970859486faeb60e52fc47e31c095fdf10cd111a6e41d3633f798c6ce4407c8e6dab3b8e84d7249e25e92adf486e3d57e2eee61fa6ec8c4d8edef1f6a5b1338ef62ec7f205ad5c09aaad8e2e58316f7ae9c71d0

		// 0xa378917b7a5c34739df8f31a8a822c6a67e1913fed52b4c1190f1a6e142c08b653a1810c00d5721cb08d715cf7911d2ee92a5a5b1657437fa5fd6ffc16b48bdd2acbcd77acc1dcea80e79c57151504125d922779edabbd46142ce5134858830ee93bfc6e97361ca0564a1e4c943b9ec7f23006802f89b9e20f4bd59d8d2296cd057f1ce1220bf44ce586cd5c3c786d85d559f8149c4d93121193c1f911d6feb7ca3a7fae6cf61eab75021821ec1695f059b4cb00ec258ffa6467ef03afe6531ff13c84bcf27f578a3677256330ce454d38f44b8865fdd9a30a40c9569c81d3815342717acb30a2ed14aa7a042ea2aa1278ba2e202a71c3eae9df17b215a9fefe

		// System.out.println(RippleBlobObj.keyHash2("FDFD0x8dfc1bf8b9947dce5ab44f17f7d64d21146a56bd84844d02a1fd807f343d007aefc6e81aff3fb493a1abc8dfa911d38ff0a021bad154a88833e6d9af7dfe5d201e20cb90d24b165138756d02e438d2086842bfdf84d54e5cbcf05dbe3667f4c9f64a2328830b616a4bb96e484e7b7ea35aaaa212dc83159a8933e406ec57ebaf2aa832520dea31d8bd8bf255c9c8f37d42035847eb7062acf82b04798eb85e0ab3b7c8c9ceefc4d48a665b7396abf975a0cc751f42d64ebe508cd54d97c8034bd23446fa246be5adeb81807220374249bc0f6598936d6a09d8e445d94cbea29b888714b6c209453a92ab10fbe11d7cffdd6555b33eb7b6090e94763e26bd40f3",
		// "id"));
		// System.out.println(RippleBlobObj.keyHash2("FDFD0x8dfc1bf8b9947dce5ab44f17f7d64d21146a56bd84844d02a1fd807f343d007aefc6e81aff3fb493a1abc8dfa911d38ff0a021bad154a88833e6d9af7dfe5d201e20cb90d24b165138756d02e438d2086842bfdf84d54e5cbcf05dbe3667f4c9f64a2328830b616a4bb96e484e7b7ea35aaaa212dc83159a8933e406ec57ebaf2aa832520dea31d8bd8bf255c9c8f37d42035847eb7062acf82b04798eb85e0ab3b7c8c9ceefc4d48a665b7396abf975a0cc751f42d64ebe508cd54d97c8034bd23446fa246be5adeb81807220374249bc0f6598936d6a09d8e445d94cbea29b888714b6c209453a92ab10fbe11d7cffdd6555b33eb7b6090e94763e26bd40f3",
		// "crypt"));
		// 8b8095994d7640064020422abaed6ae2de7d020ae0c862c97e63ed3b3339f4ac
		// RippleBlobObj rp = new RippleBlobObj();
		// System.out.println(rp.derive("htp0943562",
		// "testing0oopg141",RippleBlobObj.LOGIN).result[1]);
		/*
		 * System.out.println(JSEscape.unescape(BigNumber.encodeURIComponent(
		 * "http://www.baidu.com")));
		 * 
		 * BigNumber iAlpha=new BigNumber(
		 * "7283d19e784f48a96062271a4fa6e2c3addf14e6edf78a4bb61364856d580f13552008d7b9e3b60ebd9555e9f6c7778ec69f976757d206134e54d61ba9d588a7e37a77cf48060522478352d76db000366ef669a1b1ca93c5e3e05bc344afa1e8ccb15d3343da94180dccf590c2c32408c3f3f176c8885e95d988f1565ee9b80c12f72503ab49917792f907bbb9037487b0afed967fefc9ab090164597fcd391c43fab33029b38e66ff4af96cbf6d90a01b891f856ddd3d94e9c9b307fe01e1353a8c30edd5a94a0ebba5fe7161569000ad3b0d3568872d52b6fbdfce987a687e4b346ea702e8986b03b6b1b85536c813e46052a31ed64ec490d3ba38029544aa"
		 * ); BigNumber iModulus=new BigNumber(
		 * "c7f1bc1dfb1be82d244aef01228c1409c198894eca9e21430f1669b4aa3864c9f37f3d51b2b4ba1ab9e80f59d267fda1521e88b05117993175e004543c6e3611242f24432ce8efa3b81f0ff660b4f91c5d52f2511a6f38181a7bf9abeef72db056508bbb4eeb5f65f161dd2d5b439655d2ae7081fcc62fdcb281520911d96700c85cdaf12e7d1f15b55ade867240722425198d4ce39019550c4c8a921fc231d3e94297688c2d77cd68ee8fdeda38b7f9a274701fef23b4eaa6c1a9c15b2d77f37634930386fc20ec291be95aed9956801e1c76601b09c413ad915ff03bfdc0b6b233686ae59e8caf11750b509ab4e57ee09202239baee3d6e392d1640185e1cd"
		 * ); BigNumber iExponent = new BigNumber("010001");
		 * 
		 * String secret = "testing0oopg141"; String publicInfo =
		 * "PAKDF_1_0_0::16:auth1.ripple.com:10:htp0943562:5:login:"; long
		 * publicSize = (long) Math.ceil(Math.min((7+iModulus.bitLength()) >>>
		 * 3, 256)/8); LongArray publicHash = BigNumber.fdh(publicInfo,
		 * publicSize);
		 * 
		 * String publicHex = BigNumber.hex_fromBits(publicHash); BigNumber
		 * iPublic = new BigNumber(publicHex).setBitM(0);
		 * 
		 * String secretInfo = publicInfo+":"+secret.length()+":"+secret+":";
		 * long secretSize = (7+iModulus.bitLength()) >>> 3; LongArray
		 * secretHash = BigNumber.fdh(secretInfo, secretSize);
		 * 
		 * String secretHex = BigNumber.hex_fromBits(secretHash);
		 * //PAKDF_1_0_0::
		 * 16:auth1.ripple.com:10:htp0943562:5:login::15:testing0oopg141:
		 * //PAKDF_1_0_0
		 * ::16:auth1.ripple.com:10:htp0943562:5:login::15:testing0oopg141:
		 * 
		 * 
		 * BigNumber iSecret = new BigNumber(secretHex).mod(iModulus);
		 * System.out.println(iSecret); if (iSecret.jacobi(iModulus) != 1) {
		 * iSecret = iSecret.mul(iAlpha).mod(iModulus); } //
		 * 0x87915bd5beaa0ea9fb854ce072ecae05add6ebfc891498d2926c61cffeab5e6cebfca93a126554ac969459ec26481b4b42e57286f30d7514c915cca4a3b063dfb1abec1b8199aa169de12edcd43993535d2fc4baa04af82fd0cd135fc25a86891eb96ef9dcb41f737bd261215c315a15ef36506843e80ac59a1b19583e04c833638ac569ef5f92074965be1bf702165b58b8debefeb67c6d23c0192c5ba466a947b9373721a6b685efe28b1c0fca5e9e9cc2e89ff75dc196ca69a44ec6ff5b26b5a337a6eadb9a1170ba6ec41e422662c3c32c4f9141d12dc05b538bc081489c62edb25464b88f34c1e1a90377d9b741fd6158f138bf0ee649c76b1222b6c256
		 * 
		 * BigNumber iRandom=new BigNumber(
		 * "0x5f74cf214737d1f09177504925d7e4779d0a5a89edb2ce9ee6b72dfb809649ac29ba2bae52f40b1afb600184e1caf5569dcc3e9d87eba864e21c86e9485eaadf7eeab1e78c9c5e8db75f9e8e6c94090ce5ec9ac9e0b309e00bc6081872a5d7b958397a9c55b1b6c7c635a99a041ac3d2da26ad50897fa7809cda65c8944ea5bda0b6fd122f796addc7ba966c01c6828253d0b44e77ab1924b47cf0d5573486eb3447d0d1e0634dcd403b04667e0f2a878689e0e3d9800ed73c502c313feb902da7a7fb1987701c22425b43a6d48de2a94511e9f2936724a13828800b49151891c5760168c508c862b18018088260d93f65a29f3d0a3b77bee0f0bd982ce5a144"
		 * ); BigNumber a=iPublic.mul(iExponent);
		 * 
		 * BigNumber iBlind = iRandom.powermodMontgomery(a, iModulus); BigNumber
		 * iSignreq = iSecret.mulmod(iBlind, iModulus);
		 * 
		 * HttpRequest post = HttpRequest
		 * .post("https://auth1.ripple.com/api/sign"); post.send("info=" +
		 * publicInfo + "&signreq=" + iSignreq);
		 * System.out.println(post.body()); //
		 * 0x00000391a714fb156cf7800ba72986ee7c047258f13b2484db9f8d847f6a27506272449db
		 * //
		 * 0x00000391a714fb156cf7800ba72986ee7c047258f13b2484db9f8d847f6a27506272449db
		 * // System.out.println(a); //
		 * 0x00000391a714fb156cf7800ba72986ee7c047258f13b2484db9f8d847f6a27506272449db
		 * //
		 * 0x00000001a391ab135797756567263c8e7a6b324940bb207b7b99679475ee574bcdd2449db
		 * // System.out.println(a); //System.out.println(
		 * iRandom.powermodMontgomery(a, iModulus));
		 * //System.out.println(iRandom.jacobi(iModulus) == 1); /* for (;;) {
		 * iRandom = BigNumber.random(iModulus, 0);
		 * 
		 * if (iRandom.jacobi(iModulus) == 1) break; }
		 */

		// 0x470e4c6746b0c340f21a96e8de7b388bb8ee81a77e5f2d2d477c5b99855f08d199672421e36c7c366985b33e8e9bb50146f7e5137e07a4f2b3a3a956f6203c7254211718424ab54e71dca1d104ea1743bcb8dadb06c8c9ea81830e3fb6437c0ecae22fc8045f17e62ea1bb81c79a725aa004edf3393d763bd84f3a0625e6e28b8ceb6605abd0679bf89c7129e7933b34ff946dac8f65d1d5992084ddeb10f2e3d914ae05daa4f20b3551c8b12ca27f2adc45b59a4dcc8ac2a95a85503977335d60b0ef93f0a0b14c52a144d1f9199fe3d2c2223bd2f86a988825fbd6ab4bc10e1d53e2f8709eec454360696bdd6989850baee97e2e0e32a33404b92dfee074c9

		// 0x391a383579215656aa63c834a6b319940bfb07b7409679625ee597bcdd4949db
		// 0x391a383579215656aa63c834a6b319940bfb07b7409679625ee597bcdd4949db
		// System.out.println(iPublic);

		// 4cf443226094832e30974466fe87c614e980401dd2656b8aba8012d5cddc5713
		/*
		 * System.out.println(CoinUtils.toHex("testing0oopg141".getBytes()));
		 * //JSONEncrypt e=new JSONEncrypt(); // JSONObject o =
		 * e.decrypt("testing0oopg141",
		 * "AKrb2qX7ayQukF/0FmVfdyb9BskjyIYQ5CAooCtFMqCw6ctKzeX77waA2QNQ4dU+3vx4z/vd"
		 * ); byte[] encryptedBits = Base64.decode(
		 * "AKrb2qX7ayQukF/0FmVfdyb9BskjyIYQ5CAooCtFMqCw6ctKzeX77waA2QNQ4dU+3vx4z/vd"
		 * .getBytes()); ArrayByte buffer =new ArrayByte(encryptedBits);
		 * 
		 * System.out.println(buffer.readLong()); System.out.println(new
		 * String(buffer.readByteArray(128))); System.out.println(new
		 * String(buffer.readByteArray(128)));
		 */
		// RippleBlobObj rp = new RippleBlobObj();
		// System.out.println(rp.derive("htp0943562", "testing0oopg141"));
		// 3ef3fea0afe9d55e8afaef7c83d936d549b88fed4e2df6a021eac047d475fee5
		// b22bcb8db179502211371290fbf82c1fd0a67f381bb5ad242c1bf102b5353780
		/**
		 * 176d
		 * d5e8a9ed6f50dd042968bf112c0a3b11d2e255b788e7ce107da4631580480633f34cf78f9d1f15a7a28dba9e7fcd6fa76521c3b9ad22f691da22eff614f19cdcac6ba8139f9db815c9d860206f596e5e65866a8d210eddde371866257b8e5791bd18aa7d1a27fc023de74ce66a2beb375cb170f7f7f8dda000aeb077a4ed601c3fa3d84e7075067bcba48bfce2630fe757eef5bffdbe9f6898122e88e89f8f42c8baed8de91aa10f1722ff2d643e82d0fabcb1ea577a57e2ae974c01f8e76c8a890ac5d3e7fb62c1d903c438df7bfe4b8ed5f60a94c92a52305589795a826a6ab5ce40c3a28ac814d65e52dc6a95ba1a7a71dda987dca31e4d2b9b1a652b
		 */
		// https://id.ripple.com/v1/blob/3ef3fea0afe9d55e8afaef7c83d936d549b88fed4e2df6a021eac047d475fee5?device_id=ac3be7473f72e161c55ace5b3ae6cbdf
		// https://id.ripple.com/v1/blob/3ef3fea0afe9d55e8afaef7c83d936d549b88fed4e2df6a021eac047d475fee5?device_id=89cea53717c0fc435f8bfa2e337aba64
		// ac3be7473f72e161c55ace5b3ae6cbdf
		// 69518d655d9e5b04e951819553f64fa5
	}
}
