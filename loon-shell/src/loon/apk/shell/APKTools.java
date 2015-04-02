package loon.apk.shell;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

import android.app.Activity;
import android.content.Context;
import android.content.pm.ActivityInfo;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.graphics.drawable.Drawable;
import android.os.Build;

public class APKTools {

	public static void retrieveFromAssets(Activity activity, String filename)
			throws IOException {
		InputStream is = activity.getAssets().open(filename);
		File outFile = new File(activity.getFilesDir(), filename);
		makedirs(outFile);
		FileOutputStream fos = new FileOutputStream(outFile);
		byte[] buffer = new byte[2048];
		int length;
		while ((length = is.read(buffer)) > 0) {
			fos.write(buffer, 0, length);
		}
		fos.flush();
		fos.close();
		is.close();
	}

	public static void makedirs(String file) throws IOException {
		makedirs(new File(file));
	}

	public static void makedirs(File file) throws IOException {
		if (!file.exists()) {
			File parentFile = file.getParentFile();
			if (parentFile != null) {
				if (!parentFile.exists() && !parentFile.mkdirs()) {
					throw new IOException("Creating directories "
							+ parentFile.getPath() + " failed.");
				}
			} else {
				file.mkdirs();
			}
		}
	}


	public static boolean directoryExists(String dirPath) {
		File f = new File(dirPath);
		return f.exists() && f.isDirectory();
	}

	public static boolean copyToFile(InputStream inputStream, File destFile,
			int bufferSize) {
		if (bufferSize < 4096) {
			bufferSize = 4096;
		}
		try {
			if (destFile.exists()) {
				destFile.delete();
			}
			FileOutputStream out = new FileOutputStream(destFile);
			try {
				byte[] buffer = new byte[bufferSize];
				int bytesRead;
				while ((bytesRead = inputStream.read(buffer)) >= 0) {
					out.write(buffer, 0, bytesRead);
				}
			} finally {
				out.flush();
				try {
					out.getFD().sync();
				} catch (IOException e) {
				}
				out.close();
			}
			return true;
		} catch (IOException e) {
			return false;
		}
	}

	public static PackageInfo getAppInfo(Context cxt, String apkPath)
			throws PackageManager.NameNotFoundException {
		PackageManager pm = cxt.getPackageManager();
		PackageInfo pkgInfo = null;
		pkgInfo = pm.getPackageArchiveInfo(apkPath,
				PackageManager.GET_ACTIVITIES | PackageManager.GET_SERVICES);
		return pkgInfo;
	}

	public static ActivityInfo getActivityInfo(Context cxt, String apkPath)
			throws PackageManager.NameNotFoundException {
		return getActivityInfo(getAppInfo(cxt, apkPath), 0);
	}

	public static ActivityInfo getActivityInfo(Context cxt, String apkPath,
			int index) throws PackageManager.NameNotFoundException {
		return getActivityInfo(getAppInfo(cxt, apkPath), index);
	}

	public static ActivityInfo getActivityInfo(PackageInfo pkgInfo, int index) {
		return pkgInfo.activities[index];
	}

	public static Drawable getAppIcon(Context cxt, String apkPath)
			throws PackageManager.NameNotFoundException {
		PackageManager pm = cxt.getPackageManager();
		PackageInfo pkgInfo = getAppInfo(cxt, apkPath);
		if (pkgInfo == null) {
			return null;
		} else {
			ApplicationInfo appInfo = pkgInfo.applicationInfo;
			if (Build.VERSION.SDK_INT >= 8) {
				appInfo.sourceDir = apkPath;
				appInfo.publicSourceDir = apkPath;
			}
			return pm.getApplicationIcon(appInfo);
		}
	}

	public static CharSequence getAppName(Context cxt, String apkPath)
			throws PackageManager.NameNotFoundException {
		PackageManager pm = cxt.getPackageManager();
		PackageInfo pkgInfo = getAppInfo(cxt, apkPath);
		if (pkgInfo == null) {
			return null;
		} else {
			ApplicationInfo appInfo = pkgInfo.applicationInfo;
			if (Build.VERSION.SDK_INT >= 8) {
				appInfo.sourceDir = apkPath;
				appInfo.publicSourceDir = apkPath;
			}
			return pm.getApplicationLabel(appInfo);
		}
	}

	public static Resources getResFromPkgName(Context cxt, String pkgName)
			throws PackageManager.NameNotFoundException {
		return getCxtFromPkgName(cxt, pkgName).getResources();
	}

	public static Resources getResFromApkPath(Context cxt, String apkPath)
			throws PackageManager.NameNotFoundException {
		return getCxtFromApkPath(cxt, apkPath).getResources();
	}

	public static Context getCxtFromPkgName(Context cxt, String pkgName)
			throws PackageManager.NameNotFoundException {
		return cxt.createPackageContext(pkgName,
				Context.CONTEXT_IGNORE_SECURITY);
	}

	public static Context getCxtFromApkPath(Context cxt, String apkPath)
			throws PackageManager.NameNotFoundException {
		return getCxtFromPkgName(cxt, getAppInfo(cxt, apkPath).packageName);
	}
}
