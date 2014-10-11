package org.address;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashSet;

import org.ripple.power.utils.FileUtils;

public class TTT2 {

	public static void main(String[] args) throws IOException {
	
		ArrayList<String> list = FileUtils.getAllFiles("D:\\bit_list", "txt");
		HashSet<String> caches = new HashSet<String>(10000);
		ArrayList<String> lists = new ArrayList<String>(10000);
		for (String file : list) {
			BufferedReader reader = new BufferedReader(new InputStreamReader(new FileInputStream(file),"utf-8"));
			String text = null;
			for (; (text = reader.readLine()) != null;) {
				    String tmp = text.trim();
				    if(tmp.startsWith("1")&&tmp.split(",")[0].length()>32){
				    	String res = tmp.split(",")[0];
					if(caches.add(res)){
						lists.add(tmp);
					}
				    }
				
			}
			reader.close();
		}
		FileUtils.write(new File("d:\\fffffdgftrdsds.txt"), lists,false);
	/*	ArrayList<String> list = FileUtils.getAllFiles("D:\\bit_list", "txt");
		HashSet<String> caches = new HashSet<String>(10000);
		ArrayList<String> lists = new ArrayList<String>(10000);
		for (String file : list) {
			BufferedReader reader = new BufferedReader(new FileReader(file));
			String text = null;
			for (; (text = reader.readLine()) != null;) {
				if (text.indexOf(",") == -1) {
					if(caches.add(text)){
						lists.add(text);
					}
				} else {
					if(caches.add(text.split(",")[0])){
						lists.add(text);
					}
				}
			}
			reader.close();
		}
		FileUtils.write(new File("d:\\fffffdgftr.txt"), lists,false);*/
	}
}
