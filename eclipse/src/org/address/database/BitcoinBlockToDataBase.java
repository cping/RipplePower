package org.address.database;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.HashSet;

import org.ripple.power.config.LSystem;

import com.google.bitcoin.core.Address;
import com.google.bitcoin.core.Block;
import com.google.bitcoin.core.NetworkParameters;
import com.google.bitcoin.core.ProtocolException;
import com.google.bitcoin.core.Transaction;
import com.google.bitcoin.core.TransactionOutput;
import com.google.bitcoin.core.Utils;
import com.google.bitcoin.core.VerificationException;
import com.google.bitcoin.params.MainNetParams;
import com.google.bitcoin.script.Script;

public class BitcoinBlockToDataBase {

	public static AddressManager go(String blocksDir, String database, int startIndex)
			throws IOException, ProtocolException, VerificationException {
		HashSet<String> caches = new HashSet<String>(10000);
		File tmp = new File(database);
		if (tmp.exists()) {
			tmp.delete();
		}
		NetworkParameters node = MainNetParams.get();
		AddressManager mangager = new AddressManager(database);
		String defaultDataDir;
		if (blocksDir == null) {
			if (System.getProperty("os.name").toLowerCase().indexOf("win") >= 0) {
				defaultDataDir = System.getenv("APPDATA")
						+ "\\Bitcoin\\blocks\\";
			} else {
				defaultDataDir = System.getProperty("user.home")
						+ "/Bitcoin/blocks/";
			}
		} else {
			defaultDataDir = blocksDir;
		}
		if (!defaultDataDir.endsWith(LSystem.FS)) {
			defaultDataDir += LSystem.FS;
		}
		int i = 0;
		for (int j = startIndex; j > -1; j++) {
			FileInputStream stream;
			System.out.println("Opening " + defaultDataDir
					+ String.format("blk%05d.dat", j));
			try {
				stream = new FileInputStream(new File(defaultDataDir
						+ String.format("blk%05d.dat", j)));
			} catch (FileNotFoundException e1) {
				System.out.println(defaultDataDir
						+ String.format("blk%05d.dat", j));
				break;
			}
			while (stream.available() > 0) {
				try {
					int nextChar = stream.read();
					while (nextChar != -1) {
						if (nextChar != ((node.getPacketMagic() >>> 24) & 0xff)) {
							nextChar = stream.read();
							continue;
						}
						nextChar = stream.read();
						if (nextChar != ((node.getPacketMagic() >>> 16) & 0xff)) {
							continue;
						}
						nextChar = stream.read();
						if (nextChar != ((node.getPacketMagic() >>> 8) & 0xff)) {
							continue;
						}
						nextChar = stream.read();
						if (nextChar == (node.getPacketMagic() & 0xff)) {
							break;
						}
					}
				} catch (IOException e) {
					break;
				}
				byte[] bytes = new byte[4];
				stream.read(bytes, 0, 4);
				long size = Utils.readUint32BE(Utils.reverseBytes(bytes), 0);
				if (size > Block.MAX_BLOCK_SIZE || size <= 0) {
					continue;
				}
				bytes = new byte[(int) size];
				stream.read(bytes, 0, (int) size);
				Block block = new Block(node, bytes);
				for (Transaction t : block.getTransactions()) {
					for (TransactionOutput output : t.getOutputs()) {
						try {
							Script script = output.getScriptPubKey();
							if (script.isSentToRawPubKey()) {
								// 不读取交易hash
							} else if (script.isSentToAddress()) {

								try {
									String key = new Address(node,
											script.getPubKeyHash()).toString();
									if (caches.add(key)) {
										mangager.put(key);
									}
								} catch (Exception e) {

								}
								if (caches.size() > 10000) {
									caches.clear();
								}
							}
						} catch (Exception ex) {
							System.err.println(ex.getMessage());
						}
					}
				}
				if (i % 1000 == 0) {
					System.out.println(String.format("Has been read %s", i));
				}
				i++;
			}
			stream.close();
		}
		mangager.submit();
		return mangager;

	}
	

}
