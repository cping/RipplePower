package org.address;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.regex.Pattern;

import org.address.database.AddressManager;
import org.address.password.PasswordGenerator;
import org.address.utils.CoinUtils;
import org.json.JSONObject;
import org.json.JSONTokener;
import org.ripple.power.config.Session;
import org.ripple.power.utils.FileUtils;
import org.ripple.power.utils.MathUtils;
import org.ripple.power.utils.StringUtils;

import com.ripple.config.Config;

public class TEC3 {

	final static String flag = "qwertyuiopasdfghjklzxcvbnm0123456789!@#$%^&*~_+-";

	public static String removeHtml(String text) {
		try {
			int idx = text.indexOf('<');
			if (idx == -1)
				return text;

			StringBuffer plainText = new StringBuffer();
			String htmlText = text;
			int htmlStartIndex = htmlText.indexOf('<');
			if (htmlStartIndex == -1) {
				return text;
			}
			while (htmlStartIndex >= 0) {
				plainText.append(htmlText.substring(0, htmlStartIndex));
				int htmlEndIndex = htmlText.indexOf('>', htmlStartIndex);
				// If we have unmatched '<' without '>' stop or we
				// get into infinite loop.
				if (htmlEndIndex < 0) {
					break;
				}
				htmlText = htmlText.substring(htmlEndIndex + 1);
				htmlStartIndex = htmlText.indexOf('<');
			}
			return plainText.toString().trim();
		} catch (Exception e) {
			// #ifdef rider.debugEnabled
			// InternalLogger.getInstance().fatal(e);
			// #endif
			return text;
		}
	}

	private static String getMD5(String message)
			throws NoSuchAlgorithmException {
		MessageDigest md5 = MessageDigest.getInstance("MD5");
		md5.reset();
		md5.update(message.getBytes());
		byte[] digest = md5.digest();
		return String.format("%0" + (digest.length << 1) + "x", new BigInteger(
				1, digest));
	}

	public static void find(AddressManager manager, String name)
			throws Exception {

		String text = null;
		String address = null;

		text = name.trim();
		if(text.length()<2){
			return;
		}
	
		String result = NativeSupport.getRipplePrivateKey(text);
		address = result.split(",")[0];
		if (manager.findBlock(address)) {
			System.out.println("万万没想到" + result + "," + text);
			FileUtils.write(new File("d://bit_save_dic_data.txt"), result + ","
					+ text + "\n", true);
		}
		if (text.indexOf("<") != -1 && text.indexOf(">") != -1) {
			String tmp = removeHtml(text);
			if (tmp.length() > 1) {
				result = NativeSupport.getRipplePrivateKey(tmp);
				address = result.split(",")[0];
				if (manager.findBlock(address)) {
					System.out.println("万万没想到" + result + "," + text);
					FileUtils.write(new File("d://bit_save_dic_data.txt"),
							result + "," + text + "\n", true);
				}
			}
		}
		if(text.indexOf(" ")!=-1){
			String r = StringUtils.split(text, " ")[1];
			if(r.length()>0){
			 result = NativeSupport.getRipplePrivateKey(r);
			address = result.split(",")[0];
			if (manager.findBlock(address)) {
				System.out.println("万万没想到" + result + "," + text);
				FileUtils.write(new File("d://bit_save_dic_data.txt"), result + ","
						+ text + "\n", true);
			}
			}
			if (r.indexOf(",") != -1) {
				String[] res = r.split(",");
				for (int i = 0; i < res.length; i++) {
					String t1=res[i].trim();
					if(t1.length()>0){
					 result = NativeSupport.getRipplePrivateKey(t1);
						address = result.split(",")[0];
						if (manager.findBlock(address)) {
							System.out.println("万万没想到" + result + "," + t1);
							FileUtils.write(new File("d://bit_save_dic_data.txt"), result + ","
									+ t1 + "\n", true);
						}
					}
				}
			}
		}
		if(text.indexOf("@")!=-1){
			try{
			String r = StringUtils.split(text, "@")[0];
			if(r.length()>0){
			 result = NativeSupport.getRipplePrivateKey(r);
			address = result.split(",")[0];
			if (manager.findBlock(address)) {
				System.out.println("万万没想到" + result + "," + text);
				FileUtils.write(new File("d://bit_save_dic_data.txt"), result + ","
						+ text + "\n", true);
			}
			}
		
			if(r.length()>0&&r.indexOf(".")!=-1){
			 result = NativeSupport.getRipplePrivateKey(r.replace(".", ""));
			address = result.split(",")[0];
			if (manager.findBlock(address)) {
				System.out.println("万万没想到" + result + "," + text);
				FileUtils.write(new File("d://bit_save_dic_data.txt"), result + ","
						+ text + "\n", true);
			}
		
			}
			}catch(Exception ex){
				
			}
		}
		if (MathUtils.isNan(text.substring(0, 1))
				&& !text.substring(1, 2).equals(" ")) {
			result = NativeSupport.getRipplePrivateKey(
					text.substring(1, text.length()));
			address = result.split(",")[0];
			if (manager.findBlock(address)) {
				System.out.println("万万没想到" + result + "," + text);
				FileUtils.write(new File("d://bit_save_dic_data.txt"), result
						+ "," + text + "\n", true);
			}
		}
		
	}

	public static void main(String[] args) throws Exception {
		AddressManager manager = new AddressManager("F:\\xrp_database");

		String text = null;

		ArrayList<String> files = FileUtils.getAllFiles("D:\\finds", "txt");
		for (String file : files) {
			System.out.println(file);
			BufferedReader reader = new BufferedReader(new InputStreamReader(
					new FileInputStream(file), "utf-8"));
			HashSet<String> lists = new HashSet<String>(1000000);
			Session session = new Session("bitcoin_md5"
					+ FileUtils.getFileName(file));
			int max = session.getInt("save") == -1 ? 0 : session.getInt("save");
			for (int j = 0; (text = reader.readLine()) != null; j++) {
				if (j < max) {
					continue;
				}
				text = text.trim();
				// System.out.println(text);
				if (j % 1000 == 0) {
					System.out.println(text + "," + j);
					session.set("save", j);
					session.save();
				}
				if (text.indexOf("\t") != -1) {
					String[] res = text.split("\t");
					for (int i = 0; i < res.length; i++) {
						if (lists.add(res[i])) {
							find(manager, res[i]);
						}
					}
				} else if (text.indexOf("#") != -1) {
					String[] res = text.split("#");
					for (int i = 0; i < res.length; i++) {
						String t = res[i].trim();
						if (lists.add(t)) {
							find(manager, t);
						}
					}
					/*
					 * if(res.length>1){ String t = res[0]+res[1]; if
					 * (lists.add(t)) { find(manager, t); } t = res[1]+res[0];
					 * if (lists.add(t)) { find(manager, t); } }
					 */
				} else if (text.indexOf(",") != -1) {
					String[] res = text.split(",");
					for (int i = 0; i < res.length; i++) {
						String t = res[i].trim();
						if (lists.add(t)) {
							find(manager, t);
						}
					}
					/*
					 * if(res.length>1){ String t = res[0]+res[1]; if
					 * (lists.add(t)) { find(manager, t); } t = res[1]+res[0];
					 * if (lists.add(t)) { find(manager, t); } }
					 */
				} else {
					if (lists.add(text)) {
						find(manager, text);
					}
				}

			}
			reader.close();
		}
	}

}
