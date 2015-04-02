package loon.apk.shell;

import android.content.Context;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

import dalvik.system.DexClassLoader;

public class APKDexLoader {

	private static final Map<String, DexClassLoader> apkLoader = new ConcurrentHashMap<String, DexClassLoader>();

	public static DexClassLoader getClassLoader(String apkPath, Context cxt,
			ClassLoader parent) throws IOException {
		DexClassLoader pluginDexLoader = apkLoader.get(apkPath);
		if (pluginDexLoader == null) {
			final String dexOutputPath = cxt.getDir("shell",
					Context.MODE_PRIVATE).getAbsolutePath();
			final String libOutputPath = cxt.getDir("shell_lib",
					Context.MODE_PRIVATE).getAbsolutePath();
			APKTools.makedirs(dexOutputPath);
			APKTools.makedirs(libOutputPath);
			extractLibraries(apkPath, libOutputPath);
			pluginDexLoader = new DexClassLoader(apkPath, dexOutputPath,
					libOutputPath, parent);
			apkLoader.put(apkPath, pluginDexLoader);
		}
		return pluginDexLoader;
	}

	private static void extractLibraries(String dexPath, String libOutputPath)
			throws IOException {
		FileInputStream fis = new FileInputStream(new File(dexPath));
		ZipInputStream zis = new ZipInputStream(new BufferedInputStream(fis));
		ZipEntry entry = null;
		while (null != (entry = zis.getNextEntry())) {
			if (entry.getName().startsWith("lib/")
					|| (entry.getName().startsWith("libs/"))) {
				decompressZipFile(zis, entry, libOutputPath);
			}
		}
	}

	public static void decompressZipFile(ZipInputStream zipInputStream,
			ZipEntry entry, String destPath) throws IOException {
		String entryName = entry.getName();
		String fileName = entryName.substring(entryName.lastIndexOf("/") + 1);
		File outFile = new File(destPath, fileName);
		if (!outFile.exists()) {
			outFile.createNewFile();
			BufferedOutputStream bos = new BufferedOutputStream(
					new FileOutputStream(outFile));
			int count = 0;
			byte buffer[] = new byte[2048];
			while ((count = zipInputStream.read(buffer)) > 0) {
				bos.write(buffer, 0, count);
			}
			bos.flush();
			bos.close();
		}
	}
}
