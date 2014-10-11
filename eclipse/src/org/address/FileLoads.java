package org.address;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.math.BigInteger;
import java.util.ArrayList;

import org.address.database.AddressManager;
import org.address.password.PasswordCrackerBF;
import org.address.password.PasswordEasy;
import org.address.password.PasswordGenerator;
import org.address.utils.CoinUtils;
import org.json.JSONArray;
import org.json.JSONObject;
import org.json.JSONTokener;
import org.ripple.power.utils.FileUtils;

import com.ripple.client.Account;
import com.ripple.client.Client;
import com.ripple.client.ClientLogger;
import com.ripple.client.enums.Command;
import com.ripple.client.requests.Request;
import com.ripple.client.responses.Response;
import com.ripple.client.transport.impl.JavaWebSocketTransportImpl;
import com.ripple.client.wallet.Wallet;
import com.ripple.config.Config;
import com.ripple.core.coretypes.AccountID;
import com.ripple.core.coretypes.uint.UInt32;
import com.ripple.core.types.known.tx.txns.Payment;
public class FileLoads {
    private static void walkAccountTx(final String theAccount,final Client c, final Object marker, final int pages) {
        Request request = c.newRequest(Command.account_tx);
        request.json("binary", true);
        request.json("account", theAccount);

        if (marker != null) {
            request.json("marker", marker);
        }
        request.json("ledger_index_max", -1);

        request.once(Request.OnSuccess.class, new Request.OnSuccess() {
            @Override
            public void called(Response response) {
                JSONObject result = response.result;
                try {
                    JSONArray transactions = result.getJSONArray("transactions");
                    System.out.printf("Found %d (more) transactions %n",transactions.length());
                    System.out.printf(result.toString());

                    Object newMarker = result.opt("marker");
                    System.out.printf("Marker %s%n", newMarker);
                    if (marker != null && newMarker != null && marker.toString().equals(newMarker.toString())) {
                        // This shouldn't happen since Stef's patch but who knows how
                        // pervasively it's been deployed ?
//                        return;
                        newMarker = null;
                    }
                    if ((newMarker != null) && (pages - 1 > 0) && transactions.length() > 0) {
                        System.out.printf("Found new marker %s%n", newMarker);
                        walkAccountTx(theAccount,c, newMarker, pages - 1);
                    }
                    else {
                        System.out.printf("Found all transactions");
                    }

                } catch (Exception e) {
                    throw new RuntimeException(e);
                }

            }
        });

        request.request();
    }
    
    public static void lines(final Client c,final String address){
    	   Request req= c.newRequest(Command.account_lines);
	       req.json("account", address);
	        req.once(Request.OnSuccess.class, new Request.OnSuccess() {

				@Override
				public void called(Response response) {
					JSONObject arrays = response.result;
					Object o = arrays.get("lines");
					if(o instanceof JSONArray){
						JSONArray list = (JSONArray)o;
						if(list.length()>0){
							for(int i=0;i<list.length();i++){
								JSONObject obj = (JSONObject) list.get(i);
								System.out.println(obj.get("balance"));
								
							}
						}
							
					}
				//	JSONObject result = (JSONObject) arrays.get("lines");
				
					// System.out.println(result.get("Account") +","+(double)(result.getDouble("Balance")/1000000));
					// c.onConnected();
				}
	        	
	        });

	        req.request();
    }
    public static void info(final Client c,final String address){
 	   Request req= c.newRequest(Command.account_info);
	       req.json("account", address);
	        req.once(Request.OnSuccess.class, new Request.OnSuccess() {

				@Override
				public void called(Response response) {
					JSONObject arrays = response.result;
					JSONObject result = (JSONObject) arrays.get("account_data");
				
					 System.out.println(result.get("Account") +","+(double)(result.getDouble("Balance")/1000000));
					
				}
	        	
	        });

	        req.request();
 }
	public static void main(String[] args) throws IOException {
		ArrayList<String> files = FileUtils.getAllFiles("D:\\files", "txt");
		ArrayList<String> lists = new ArrayList<String>(1000);

		for (String file : files) {

			String string = new String(FileUtils.readBytesFromFile(file),
					"utf-8");

			char[] chars = string.toCharArray();

			StringBuilder sbr = new StringBuilder();

			boolean flag = false;

			for (int i = 0; i < chars.length; i++) {
				char result = chars[i];
				if (flag) {
					sbr.append(result);
				}
				if (result == '{') {
					flag = true;
				}
				if (result == ',') {
					flag = false;
					sbr.delete(sbr.length() - 1, sbr.length());
					break;
				}
			}
			string = sbr.toString().replace("\"", "");

			String[] item = string.split(":");
			if (item.length > 1) {
				if (!lists.contains(item[1])) {
					lists.add(item[1]);
					System.out.println(item[1]);
			
				}
			}

		}

	      final  Client c = new Client(new JavaWebSocketTransportImpl());

	        c.connect("wss://s1.ripple.com:443");
		 ClientLogger.quiet = true;
		for(String address:lists){
			info(c,address);
			//lines(c,address);
		}
	
		
	}
}
