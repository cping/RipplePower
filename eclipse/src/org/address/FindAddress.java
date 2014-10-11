package org.address;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.RandomAccessFile;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;

import org.address.NativeSupport;
import org.address.database.AddressManager;
import org.address.password.PasswordGenerator;
import org.ripple.power.config.RHConfig;
import org.ripple.power.utils.FileUtils;
import org.ripple.power.utils.StringUtils;

import com.google.bitcoin.core.NetworkParameters;

public class FindAddress {

	public static ArrayList<String> list(String fileName) {
		File file = new File(fileName);
		if (!file.exists()) {
			return new ArrayList<String>(0);
		}
		FileReader fileReader = null;
		ArrayList<String> result = new ArrayList<String>(10000);
		try {

			fileReader = new FileReader(file);
			final BufferedReader reader = new BufferedReader(fileReader);
			for (;;) {
				String temp = reader.readLine();
				if (temp == null) {
					break;
				}
				if (temp.length() == 0) {
					continue;
				}
				 if (temp.indexOf('\t') != -1) {
					final String[] parts = temp.split("\t");
					if (parts.length > 1) {
						result.add(parts[0]);

					}
				} else if (temp.indexOf(',') != -1) {
					final String[] parts = temp.split(",");
					if (parts.length > 1) {
						result.add(parts[0]);

					}
				}  else {

					final String[] parts = StringUtils.split(temp, " ");
					if (parts.length > 1) {
						result.add(parts[0]);

					} else if (parts.length == 1) {
						result.add(parts[0]);

					}
				}

			}
		} catch (Exception ex) {
			ex.printStackTrace();
		} finally {
			try {
				fileReader.close();
				fileReader = null;
			} catch (IOException e) {
				e.printStackTrace();
			}

		}
		return result;
	}
	public static ArrayList<String> list2(String fileName) {
		File file = new File(fileName);
		if (!file.exists()) {
			return new ArrayList<String>(0);
		}
		FileReader fileReader = null;
		ArrayList<String> result = new ArrayList<String>(10000);
		try {

			fileReader = new FileReader(file);
			final BufferedReader reader = new BufferedReader(fileReader);
			for (;;) {
				String temp = reader.readLine();
				if (temp == null) {
					break;
				}
				if (temp.length() == 0) {
					continue;
				}
				result.add(temp.trim());
			}
			reader.close();
		} catch (Exception ex) {
			ex.printStackTrace();
		} finally {
			try {
				fileReader.close();
				fileReader = null;
			} catch (IOException e) {
				e.printStackTrace();
			}

		}
		return result;
	}
	public static void test2(String dstSavefile, String dstFile)
			throws IOException {
		final ArrayList<String> save = list(dstSavefile);
		ArrayList<String> files = FileUtils.getAllFiles(dstFile, "txt");
		ArrayList<String> contents = new ArrayList<String>(10000);
		int count = 0;
		NetworkParameters node = NetworkParameters.prodNet();
		
		System.out.println(save);
		for (String file : files) {
			if (save.contains(file)) {
				continue;
			}
			ArrayList<String> context1 = list(file);

			System.out.println("正在处理:" + file+","+context1.size());
			for (String text : context1) {
				//System.out.println(text);
				String result = NativeSupport.getBitcoinPrivateKey(text);
			
				if (!contents.contains(result)) {
					contents.add(result);
					count++;
				}
				if (count > 1000) {
				//	System.out.println(result);
					FileUtils.write(
							new File("f:\\bit_name_address_00\\"
									+ System.currentTimeMillis() + ".txt"),
							contents, true);
					count = 0;
					contents.clear();
				}
			}
			FileUtils.write(dstSavefile, file + "\n", true);
		}
		FileUtils.write(
				new File("f:\\bit_name_address_00\\" + System.currentTimeMillis()
						+ ".txt"), contents, true);
		count = 0;
		contents.clear();

	}

	public static void test1() throws IOException {

		ArrayList<String> list = new ArrayList<String>(100000);
		FileReader fileReader = new FileReader("d:\\rockyou.txt");
		final BufferedReader reader = new BufferedReader(fileReader);

		for (;;) {
			String temp = reader.readLine();
			if (temp == null) {
				break;
			}
			if (temp.length() == 0) {
				continue;
			}
			temp = temp.trim();
			if (!list.contains(temp)) {
				list.add(temp);
			}
			if (list.size() > 100000) {
				FileUtils.write(
						new File("d:\\passwords\\" + System.currentTimeMillis()
								+ ".txt"), list, true);
				list.clear();
			}
		}
		FileUtils.write(new File("d:\\passwords\\" + System.currentTimeMillis()
				+ ".txt"), list, true);
		list.clear();
		fileReader.close();
		fileReader = null;

	}



	public static void web() {

		try {

			ArrayList<String> texts = 
					list2("d:\\fffffdgftrdsds.txt");

			int count = 0;
			for (String text : texts) {
				// System.out.println(text+","+count);
				String address = text.split(",")[0];

				if(count % 100 == 0){
				System.out.println(address+","+count);
				}
				try {
					if (!HttpUtil.sendHttpPost("http://btc.ondn.net/search",
							"address=" + address, 8000, "No result".getBytes())) {
						System.out.println(text + "," + count);
						System.out.println("万中有一:" + text);
					}
				} catch (Exception ex) {
					ex.printStackTrace();
				}
				count++;

			}

		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}


	public static String firstLetterToUpper(String string) {
		char[] buffer = string.toCharArray();
		buffer[0] = Character.toUpperCase(string.charAt(0));
		return new String(buffer);
	}

	public static void test2() {

		try {
			String toDir = "f:\\tmp_bit_list";
			final ArrayList<String> loadData = list2("d:\\bitdata.txt");
			ArrayList<String> lists = FileUtils.getAllFiles("D:\\bit_nlist");
			System.out.println(lists.size());
			NetworkParameters node = NetworkParameters.prodNet();
			for (int j = lists.size() - 1; j > -1; j--) {

				final String file = lists.get(j);
				if (loadData.contains(file)) {
					continue;
				}

				ArrayList<String> texts = list(file);

				/*
				 * ArrayList<String> password = new ArrayList<String>(10000);
				 * for (String text : texts) {
				 * 
				 * for (int i = 0; i < 24; i++) { String tmp = text; switch (i)
				 * { case 0: tmp = text.toLowerCase(); break; case 1: tmp =
				 * firstLetterToUpper(text); break; case 2: tmp = text + "e";
				 * break; case 3: tmp = text + "er"; break; case 4: tmp = text +
				 * "s"; break; case 5: tmp = text + "ed"; break; case 6: tmp =
				 * text + "1"; break; case 7: tmp = text + "12"; break; case 8:
				 * tmp = text + "123"; break; case 9: tmp = text + "1234";
				 * break; case 10: tmp = text + "12345"; break; case 11: tmp =
				 * text + "123456"; break; case 12: tmp = text + "1234567";
				 * break; case 13: tmp = text + "12345678"; break; case 14: tmp
				 * = text + "123456789"; break; case 15: tmp = text + "11";
				 * break; case 16: tmp = text + "22"; break; case 17: tmp = text
				 * + "33"; break; case 18: tmp = text + "111"; break; case 19:
				 * tmp = text + "222"; break; case 20: tmp = text + "333";
				 * break; case 21: tmp = text + "112233"; break; case 22: tmp =
				 * "321" + text; break; case 23: tmp = "654321" + text; break;
				 * default: break; } if (tmp == null) { tmp = text; } //
				 * System.out.println(tmp); if (!password.contains(tmp)) {
				 * password.add(tmp); } } }
				 */

				for (String text : texts) {

					String pass = NativeSupport.getBitcoinPrivateKey(text
							.toLowerCase());

					String address = pass.split(",")[0];

					char[] chars = address.toCharArray();
					String path = (toDir + "\\" + chars[1] + "\\" + chars[2] + "\\list.txt")
							.toLowerCase();

					/*
					 * ArrayList<String> result = list2(path);
					 * 
					 * if (result.contains(address)) { FileUtils.write(new
					 * File("d://bit_save_data.txt"), text + "\n", true);
					 * System.out.println("万中有一:" + text); }
					 */
					BufferedInputStream stream = new BufferedInputStream(
							new FileInputStream(path));
					if (findPatternInStream(address.getBytes(), stream)) {
						FileUtils.write(new File(
								"d://bit_save_random_data2.txt"), pass + ","
								+ text + "\n", true);
						System.out.println("万中有一:" + pass + "," + text);
					}
					stream.close();

				}

				FileUtils.write(new File("d://bitdata.txt"), file + "\n", true);
			}

		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	private static boolean findPatternInStream(byte[] pattern, InputStream is)
			throws IOException {
		int patternOffset = 0;
		int len = pattern.length;
		int b = is.read();
		for (; b != -1;) {
			if (pattern[patternOffset] == ((byte) b)) {
				patternOffset++;
				if (patternOffset == len) {
					return true;
				}
			} else {
				patternOffset = 0;
			}
			b = is.read();
		}

		return false;
	}

	public static void testone(String def) {

		try {
			RHConfig config = new RHConfig(def);

			String load = config.get("load_data");
			String save = config.get("save_data");
			//String bit_list = config.get("bit_list");
			final ArrayList<String> loadData = list2(load);
			ArrayList<String> lists = FileUtils.getAllFiles("f:\\tolist_bit", "txt");

			//System.out.println(bit_list);

			String text;

			String address = null;
	
			

			AddressManager manager = new AddressManager("f:\\bitcoin_data");

			// HashSet<String> list = new HashSet<String>(10000);
	
			for (String file : lists) {

				if (loadData.contains(file)) {
					continue;
				}

				File path = new File(file);

				if (!path.exists() || path.length() < 32) {
					continue;
				}
				
				//System.out.println(FileUtils.getKB(path));

			
				 BufferedReader rafile = new BufferedReader(new FileReader(file));

				for (;(text = rafile.readLine()) != null;) {

					/*
					 * if (list.size() > 10000) { list.clear(); }
					 */
					StringBuilder sbr = new StringBuilder();
					for (int i = 0; i < text.length(); i++) {
						char c = text.charAt(i);
						if (c == ',') {
							break;
						}
						sbr.append(c);
					}
					address = sbr.toString();

					
					if (manager.findBlock(address)) {
						try {
							FileUtils.write(new File(save), text + "\n", true);
							System.out.println("万中有一:" + text);
						} catch (IOException e) {
							e.printStackTrace();
						}

					}

				}
				System.out.println(file);
				rafile.close();
				FileUtils.write(new File(load), file + "\n", true);
				System.gc();
			}

		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	public static void main(String[] args) {
web();
		// test1();
		//testone("d:\\default2.cfg");

	}

	public static void writer(String toDir, NetworkParameters node, String pass)
			throws Exception {
		String text = NativeSupport.getBitcoinPrivateKey(pass);

		String address = text.split(",")[0];

		char[] chars = address.toCharArray();
		String path = (toDir + "\\" + chars[1] + "\\" + chars[2] + "\\list.txt")
				.toLowerCase();

		BufferedInputStream stream = new BufferedInputStream(
				new FileInputStream(path));
		if (findPatternInStream(address.getBytes(), stream)) {
			FileUtils.write(new File("d://bit_save_random_data_new.txt"), text
					+ "\n", true);
			System.out.println("万中有一:" + text + "," + pass);
		}
		stream.close();
	}
}
