package org.ripple.power.txns.btc;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.zip.GZIPOutputStream;

import org.ripple.power.Helper;
import org.ripple.power.config.LSystem;

public class CreateBootstrap {

	/**
	 * Create the bootstrap files
	 *
	 * @param dirPath
	 *            Bootstrap directory
	 * @param startHeight
	 *            Start chain height
	 * @param stopHeight
	 *            Stop chain height
	 */
	public static void process(String dirPath, int startHeight, int stopHeight) {
		//
		// Make sure the bootstrap directory exists
		//
		File dirFile = new File(dirPath);
		if (!dirFile.exists() || !dirFile.isDirectory()) {
			BTCLoader.error(String.format("'%s' is not a directory", dirPath));
			return;
		}
		BTCLoader.info(String.format("Creating bootstrap files in %s", dirPath));
		//
		// Create the 'blocks' subdirectory if it does not exist
		//
		dirFile = new File(String.format("%s%sblocks", dirPath, LSystem.FS));
		if (!dirFile.exists())
			dirFile.mkdir();
		//
		// Erase existing bootstrap files
		//
		File fileList[] = dirFile.listFiles();
		if (fileList != null && fileList.length > 0) {
			for (File file : fileList) {
				String fileName = file.getName();
				if (fileName.startsWith("blk") && fileName.endsWith(".gz"))
					file.delete();
			}
		}
		//
		// Process the block chain
		//
		String fileName = "";
		int fileNumber = -1;
		int byteCount = 0;
		int start = Math.max(startHeight, 0);
		int stop = Math.min(stopHeight, BTCLoader.blockStore.getChainHeight());
		File file = null;
		GZIPOutputStream zipOut = null;
		byte[] prefix = new byte[8];
		try {
			for (int height = start; height <= stop; height++) {
				//
				// Close the current bootstrap file after processing 1GB
				//
				if (byteCount > 1024 * 1024 * 1024) {
					zipOut.close();
					zipOut = null;
				}
				//
				// Open the next bootstrap file
				//
				if (zipOut == null) {
					fileName = String.format("blk%05d.dat.gz", ++fileNumber);
					byteCount = 0;
					file = new File(String.format("%s%sblocks%s%s", dirPath, LSystem.FS, LSystem.FS, fileName));
					zipOut = new GZIPOutputStream(new FileOutputStream(file), 1024 * 1024);
					BTCLoader.info(String.format("Creating bootstrap file %s", fileName));
				}
				//
				// Write the block to the bootstrap file
				//
				Block block = BTCLoader.blockStore.getBlock(BTCLoader.blockStore.getBlockId(height));
				byte[] blockBytes = block.getBytes();
				Helper.uint32ToByteArrayLE(NetParams.MAGIC_NUMBER, prefix, 0);
				Helper.uint32ToByteArrayLE(blockBytes.length, prefix, 4);
				zipOut.write(prefix);
				zipOut.write(blockBytes);
				byteCount += blockBytes.length;
			}
		} catch (IOException exc) {
			BTCLoader.error(String.format("I/O error creating bootstrap file %s", fileName), exc);
		} catch (BlockStoreException exc) {
			BTCLoader.error("Unable to get block from database", exc);
		} catch (Exception exc) {
			BTCLoader.error("Exception while creating bootstrap files", exc);
		} finally {
			if (file != null && zipOut != null) {
				try {
					zipOut.close();
					if (file.length() == 0)
						file.delete();
				} catch (IOException exc) {
					BTCLoader.error(String.format("Unable to close bootstrap file %s", fileName), exc);
				}
			}
		}
	}
}
