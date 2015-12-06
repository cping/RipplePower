package org.ripple.power.blockchain;

import org.ripple.power.CoinUtils;
import org.ripple.power.config.LSystem;
import org.ripple.power.utils.Base64Coder;
import org.ripple.power.utils.StringUtils;
import org.ripple.power.wallet.OpenSSL;

public class RippleMemoEncode {

	public static class Mode {
		public final static int NONE = 0;
		public final static int BASE64 = 1;
		public final static int ENCODE = 2;
		public final static int UNKOWN = 3;
	}

	private byte[] type;
	private byte[] data;
	private byte[] format;

	private String modeName = "UNKOWN";

	public RippleMemoEncode(int modeIdx, String mtype, String mdata,
			String password) {
		try {
			type = mtype.getBytes(LSystem.encoding);
			data = mdata.getBytes(LSystem.encoding);
			switch (modeIdx) {
			case Mode.UNKOWN:
			case Mode.NONE:
				modeName = "NONE";
				break;
			case Mode.BASE64:
				modeName = "BASE64";
				type = Base64Coder.encode(mtype.getBytes(LSystem.encoding));
				data = Base64Coder.encode(mdata.getBytes(LSystem.encoding));
				break;
			case Mode.ENCODE:
				modeName = "ENCODE";
				type = mtype.getBytes(LSystem.encoding);
				data = mdata.getBytes(LSystem.encoding);
				if (!StringUtils.isEmpty(password)) {
					OpenSSL ssl = new OpenSSL();
					type = ssl.encrypt(type, password);
					data = ssl.encrypt(data, password);
				}
				type = Base64Coder.encode(type);
				data = Base64Coder.encode(data);
				break;
			}
			format = Base64Coder.encode(modeName.getBytes(LSystem.encoding));
		} catch (Exception e) {
			type = null;
			data = null;
			format = null;
		}
	}

	public String getType() {
		if (type == null) {
			return null;
		}
		return CoinUtils.toHex(type);
	}

	public String getData() {
		if (data == null) {
			return null;
		}
		return CoinUtils.toHex(data);
	}

	public String getFormat() {
		if (format == null) {
			return null;
		}
		return CoinUtils.toHex(format);
	}

}
