package org.ripple.power.ui;

public class RPAddress {

	private char[] pPublic;

	private char[] pPrivate;

	public RPAddress(String publicAddress, String privateAddress) {
		this(publicAddress.toCharArray(), privateAddress.toCharArray());
	}

	public RPAddress(char[] publicAddress, char[] privateAddress) {
		this.pPublic = new char[publicAddress.length];
		this.pPrivate = new char[privateAddress.length];
		if (pPublic.length > 32) {
			for (int i = 0; i < pPublic.length; i++) {
				switch (i) {
				case 2:
					pPublic[i] = publicAddress[32];
					break;
				case 4:
					pPublic[i] = publicAddress[16];
					break;
				case 8:
					pPublic[i] = publicAddress[8];
					break;
				case 16:
					pPublic[i] = publicAddress[4];
					break;
				case 32:
					pPublic[i] = publicAddress[2];
					break;
				default:
					pPublic[i] = publicAddress[i];
					break;
				}
			}
		}
		if (pPrivate.length > 9) {
			for (int i = 0; i < pPrivate.length; i++) {
				switch (i) {
				case 1:
					pPrivate[i] = privateAddress[9];
					break;
				case 3:
					pPrivate[i] = privateAddress[7];
					break;
				case 7:
					pPrivate[i] = privateAddress[3];
					break;
				case 9:
					pPrivate[i] = privateAddress[1];
					break;
				default:
					pPrivate[i] = privateAddress[i];
					break;
				}
			}
		}
	}

	public char[] getPublic() {
		return pPublic;
	}

	public char[] getPrivate() {
		return pPrivate;
	}

}
