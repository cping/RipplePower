package loon.apk.shell;

import android.app.Activity;
import android.app.Application;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.res.AssetManager;
import android.content.res.Resources;
import android.os.Build;
import android.os.Bundle;
import android.os.Looper;
import android.view.KeyEvent;
import android.widget.Toast;

import java.io.File;
import java.io.IOException;

import dalvik.system.DexClassLoader;

public abstract class APKShellActivity extends Activity implements APKProxy {

	private APKObject apk_object;

	private boolean error = false;

	private APKSetting setting;

	public void register(APKSetting set) {
		this.setting = set;
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		this.onMain();
		Thread.setDefaultUncaughtExceptionHandler(new Thread.UncaughtExceptionHandler() {
			@Override
			public void uncaughtException(Thread thread, final Throwable ex) {
				ex.printStackTrace();
				try {
					final Context context = APKReflect
							.in("android.app.ActivityThread")
							.call("currentActivityThread")
							.call("getSystemContext").get();
					new Thread() {
						@Override
						public void run() {
							Looper.prepare();
							Toast.makeText(context, "Data Error!",
									Toast.LENGTH_SHORT).show();
							Looper.loop();
						}
					}.start();
					try {
						Thread.sleep(1500);
					} catch (InterruptedException e) {
					}
					Thread.setDefaultUncaughtExceptionHandler(Thread
							.getDefaultUncaughtExceptionHandler());
					android.os.Process.killProcess(android.os.Process.myPid());
					System.exit(0);
				} catch (Exception exce) {
					exce.printStackTrace();
				}

			}
		});
		super.onCreate(savedInstanceState);

		apk_object = loadAPK(APKShellActivity.this, setting.apkPath, false);
		apk_object.setTopActivityName(setting.apkActivityName);

		if (!apk_object.isAPKInit()) {

			fillAPK(apk_object);
		}
		if (!apk_object.isAPKInit()) {
			throw new APKCreateFailedException("Create APK failed!");
		}
		try {
			APKActivityControl control = new APKActivityControl(
					APKShellActivity.this, apk_object.getCurrentAPKActivity(),
					apk_object.getAPKApplication(), setting);
			apk_object.setControl(control);
			control.dispatchProxyToAPK();
			APKReflect.in(apk_object.getCurrentAPKActivity()).call(
					"attachBaseContext", this);
			control.runOnCreate(savedInstanceState);
			APKCallbacks.runAllOnCreate(savedInstanceState);
		} catch (Exception e) {
			error = true;
			processError(e);
		}

	}

	public abstract void onMain();

	private void processError(Exception ex) {

	}

	@Override
	public Resources getResources() {
		if (apk_object == null) {
			return super.getResources();
		}
		return apk_object.getAPKRes() == null ? super.getResources()
				: apk_object.getAPKRes();
	}

	@Override
	public Resources.Theme getTheme() {
		if (apk_object == null) {
			return super.getTheme();
		}
		return apk_object.getCurrentAPKTheme() == null ? super.getTheme()
				: apk_object.getCurrentAPKTheme();
	}

	@Override
	public AssetManager getAssets() {
		if (apk_object == null) {
			return super.getAssets();
		}
		return apk_object.getAPKAssetManager() == null ? super.getAssets()
				: apk_object.getAPKAssetManager();
	}

	@Override
	public ClassLoader getClassLoader() {
		if (apk_object == null) {
			return super.getClassLoader();
		}
		if (apk_object.isAPKInit()) {
			return apk_object.getAPKLoader();
		}
		return super.getClassLoader();
	}

	@Override
	protected void onResume() {
		super.onResume();
		if (apk_object == null) {
			return;
		}
		APKActivityCallback caller = apk_object.getControl();
		if (caller != null) {
			caller.runOnResume();
			APKCallbacks.runAllOnResume();
		}
	}

	@Override
	protected void onStart() {
		super.onStart();
		if (apk_object == null) {
			return;
		}
		APKActivityCallback caller = apk_object.getControl();
		if (caller != null) {
			try {
				caller.runOnStop();
				APKCallbacks.runAllOnStop();
			} catch (Exception e) {
				error = true;
				processError(e);
			}
		}
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		if (apk_object == null) {
			return;
		}
		APKActivityCallback caller = apk_object.getControl();
		if (caller != null) {
			if (!error) {
				try {
					caller.runOnDestroy();
					APKCallbacks.runAllOnDestroy();
				} catch (Exception e) {
					error = true;
					processError(e);
				}
			}
		}

	}

	@Override
	protected void onPause() {
		super.onPause();
		if (apk_object == null) {
			return;
		}
		APKActivityCallback caller = apk_object.getControl();
		if (caller != null) {
			if (!error) {
				try {
					caller.runOnPause();
					APKCallbacks.runAllOnPause();
				} catch (Exception e) {
					error = true;
					processError(e);
				}
			}
		}
	}

	@Override
	protected void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);
		if (apk_object == null) {
			return;
		}
		APKActivityCallback caller = apk_object.getControl();
		if (caller != null) {
			if (!error) {
				try {
					caller.runOnSaveInstanceState(outState);
					APKCallbacks.runAllOnSaveInstanceState(outState);
				} catch (Exception e) {
					error = true;
					processError(e);
				}
			}
		}
	}

	@Override
	protected void onRestoreInstanceState(Bundle savedInstanceState) {
		super.onRestoreInstanceState(savedInstanceState);
		if (apk_object == null) {
			return;
		}
		APKActivityCallback caller = apk_object.getControl();
		if (caller != null) {
			if (!error) {
				try {
					caller.runOnRestoreInstanceState(savedInstanceState);
					APKCallbacks
							.runAllOnRestoreInstanceState(savedInstanceState);
				} catch (Exception e) {
					error = true;
					processError(e);
				}

			}
		}

	}

	@Override
	public APKObject loadAPK(Activity ctx, String apkPath) {
		return loadAPK(ctx, apkPath, true);

	}

	@Override
	public APKObject loadAPK(Activity ctx, String apkPath, boolean checkInit) {
		APKObject apkobj = APKManager.loadAPK(ctx, apkPath);
		if (checkInit) {
			if (!apkobj.isAPKInit()) {
				fillAPK(apkobj);
			}
		}
		return apkobj;
	}

	@Override
	public APKObject loadAPK(Activity ctx, String apkPath, String activityName) {
		APKObject apkobj = loadAPK(ctx, apkPath, false);
		apkobj.setTopActivityName(activityName);
		fillAPK(apkobj);
		return apkobj;
	}

	@Override
	public APKObject loadAPK(Activity ctx, String apkPath, int index) {
		APKObject apkobj = loadAPK(ctx, apkPath, false);
		if (apkobj.isAPKInit()) {
			apkobj.setTopActivityName(apkobj.getActivityInfos()[index].name);
			fillAPK(apkobj);
		} else {
			try {
				PackageInfo info = APKTools.getAppInfo(this, apkPath);
				String name = info.activities[index].name;
				apkobj.setTopActivityName(name);
				fillAPK(apkobj);
			} catch (PackageManager.NameNotFoundException e) {
				throw new RuntimeException();
			}

		}

		return apkobj;
	}

	@Override
	public void fillAPK(APKObject apkobj) {
		if (apkobj == null) {
			throw new RuntimeException("APKObject is null !");
		}
		String apkPath = apkobj.getAPKPath();
		File file = new File(getFilesDir(), apkPath);
		if (!file.exists()) {
			try {
				APKTools.retrieveFromAssets(this, apkPath);
			} catch (IOException e) {
			}
		}
		File apk = new File(file.getAbsolutePath());
		if (!apk.exists()) {
			throw new APKNotFoundException(apkPath);
		}
		apkPath = apk.getAbsolutePath();
		apkobj.setAPKPath(apkPath);

		apk = null;
		putAPKRes(apkobj);

		if (!apkobj.isCompleted()) {
			putAPKInfo(apkobj);
		}

		try {
			putAPKLoader(apkobj);
			putAPKTheme(apkobj);
			putAPKApplication(apkobj);
		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}

	private void putAPKTheme(APKObject apkobj) {
		PackageInfo packageInfo = apkobj.getAPKPkgInfo();
		String mClass = apkobj.getTopActivityName();
		int defaultTheme = packageInfo.applicationInfo.theme;
		ActivityInfo curActivityInfo = null;
		for (ActivityInfo a : packageInfo.activities) {
			if (a.name.equals(mClass)) {
				curActivityInfo = a;
				if (a.theme != 0) {
					defaultTheme = a.theme;
				} else if (defaultTheme != 0) {
				} else {
					if (Build.VERSION.SDK_INT >= 14) {
						defaultTheme = android.R.style.Theme_DeviceDefault;
					} else {
						defaultTheme = android.R.style.Theme;
					}
				}
				break;
			}
		}
		apkobj.getCurrentAPKTheme().applyStyle(defaultTheme, true);
		setTheme(defaultTheme);
		if (curActivityInfo != null) {
			getWindow().setSoftInputMode(curActivityInfo.softInputMode);
		}

	}

	private void putAPKApplication(APKObject apkobj) {
		String appName = apkobj.getAppName();
		if (appName == null) {
			return;
		}
		if (appName.isEmpty()) {
			return;
		}
		ClassLoader loader = apkobj.getAPKLoader();
		if (loader == null)
			throw new APKCreateFailedException();
		try {
			Application APKApp = (Application) loader.loadClass(appName)
					.newInstance();
			try {
				APKReflect.in(APKApp).call("attach", getApplicationContext());
			} catch (Exception e) {
				e.printStackTrace();
			}
			apkobj.bindAPKApp(APKApp);

		} catch (InstantiationException e) {
		} catch (IllegalAccessException e) {
		} catch (ClassNotFoundException e) {
		}

	}

	private void putAPKLoader(APKObject apkobj) throws IOException {
		DexClassLoader loader = APKDexLoader.getClassLoader(
				apkobj.getAPKPath(), this, getClassLoader());
		apkobj.setAPKLoader(loader);
		String top = apkobj.getTopActivityName();
		if (top == null) {
			top = apkobj.getActivityInfos()[0].name;
			apkobj.setTopActivityName(top);
		}
		try {
			Activity myAPK = (Activity) apkobj.getAPKLoader()
					.loadClass(apkobj.getTopActivityName()).newInstance();
			apkobj.setCurrentAPKActivity(myAPK);
		} catch (Exception e) {
			throw new RuntimeException(e.getMessage());
		}
	}

	private void putAPKInfo(APKObject apkobj) {
		PackageInfo info = null;
		try {
			info = APKTools.getAppInfo(this, apkobj.getAPKPath());
		} catch (PackageManager.NameNotFoundException e) {
			throw new RuntimeException(apkobj.getAPKPath());
		}
		if (info == null) {
			throw new APKCreateFailedException("Create APK from "
					+ apkobj.getAPKPath());
		}
		apkobj.setAPKPkgInfo(info);
		apkobj.setAppName(info.applicationInfo.className);
		apkobj.setCompleted(true);
	}

	private void putAPKRes(APKObject apkobj) {
		try {
			AssetManager assetManager = AssetManager.class.newInstance();
			APKReflect assetRef = APKReflect.in(assetManager);
			assetRef.call("addAssetPath", apkobj.getAPKPath());
			apkobj.setAPKAssetManager(assetManager);
			Resources superRes = super.getResources();
			Resources APKRes = new Resources(assetManager,
					superRes.getDisplayMetrics(), superRes.getConfiguration());
			apkobj.setAPKRes(APKRes);

			Resources.Theme APKTheme = apkobj.getAPKRes().newTheme();

			APKTheme.setTo(super.getTheme());
			apkobj.setCurrentAPKTheme(APKTheme);

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@Override
	public void onBackPressed() {
		if (apk_object == null || error) {
			super.onBackPressed();
		}
		APKActivityCallback caller = apk_object.getControl();
		if (caller != null) {
			try {
				caller.runOnBackPressed();
				APKCallbacks.runAllOnBackPressed();
			} catch (Exception e) {
				error = true;
				processError(e);
			}
		}
	}

	@Override
	protected void onStop() {
		super.onStop();
		if (apk_object == null) {
			return;
		}
		APKActivityCallback caller = apk_object.getControl();
		if (caller != null) {
			if (!error) {
				try {
					caller.runOnStop();
					APKCallbacks.runAllOnStop();
				} catch (Exception e) {
					error = true;
					processError(e);
				}

			}
		}
	}

	@Override
	protected void onRestart() {
		super.onRestart();
		if (apk_object == null) {
			return;
		}
		APKActivityCallback caller = apk_object.getControl();
		if (caller != null) {
			try {
				caller.runOnRestart();
				APKCallbacks.runAllOnRestart();
			} catch (Exception e) {
				error = true;
				processError(e);
			}
		}
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (apk_object == null) {
			return super.onKeyDown(keyCode, event);
		}
		APKActivityCallback caller = apk_object.getControl();
		if (caller != null) {
			if (!error) {
				APKCallbacks.runAllOnKeyDown(keyCode, event);
				return caller.runOnKeyDown(keyCode, event);
			}
		}
		return super.onKeyDown(keyCode, event);
	}

	@Override
	public ComponentName startService(Intent service) {
		service.setClass(this, APKServiceProxy.class);
		String className = service.getComponent().getClassName();
		apk_object.setCurrentServiceClassName(className);
		return super.startService(service);
	}

}
