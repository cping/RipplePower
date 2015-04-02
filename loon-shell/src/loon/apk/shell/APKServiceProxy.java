package loon.apk.shell;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;

public class APKServiceProxy extends Service {

	private APKObject apk_object;
	private APKReflect serviceRef;
	private APKReflect thisRef;

	private void hackAPKService() {
		try {
			serviceRef
					.call("attach",
							APKServiceProxy.this,
							thisRef.get("mApplication"),
							thisRef.get("mClassName"),
							thisRef.get("mToken"),
							apk_object.getAPKApplication() == null ? getApplication()
									: apk_object.getAPKApplication(),
							thisRef.get("mActivityManager"));
		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	public void putAPKService(APKObject apk) {
		if (apk_object.isServiceInit()) {
			return;
		}
		Service APKService = null;
		String serviceClassName = apk_object.getCurrentServiceClassName();
		if (serviceClassName == null) {
			throw new RuntimeException("APK can't found service name!");
		}
		try {
			APKService = (Service) apk_object.getAPKLoader()
					.loadClass(serviceClassName).newInstance();
		} catch (ClassNotFoundException e) {
			throw new APKCreateFailedException(e.getMessage());
		} catch (InstantiationException e) {
			throw new APKCreateFailedException(e.getMessage());
		} catch (IllegalAccessException e) {
			throw new APKCreateFailedException(e.getMessage());
		}
		serviceRef = APKReflect.in(apk_object.getAPKService());
		hackAPKService();
		apk_object.setAPKService(APKService);
	}

	@SuppressWarnings("deprecation")
	@Override
	public void onStart(Intent intent, int startId) {
		super.onStart(intent, startId);
		if (apk_object == null || !apk_object.isServiceInit()) {
			return;
		}
	}

	@Override
	public IBinder onBind(Intent intent) {
		return null;
	}

	@Override
	public void onCreate() {
		super.onCreate();
		thisRef = APKReflect.in(APKServiceProxy.this);
		apk_object = APKManager.getLoadedAPK(APKManager.finalApkPath);
		if (apk_object == null) {
			throw new RuntimeException("Service is depend on a apk.");
		}
		if (!apk_object.isAPKInit()) {
			throw new RuntimeException(
					"Service meet a apk which is not init..");
		}
		putAPKService(apk_object);
		try {
			serviceRef.call("onCreate");
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@Override
	public void onLowMemory() {
		super.onLowMemory();
		if (apk_object != null)
			try {
				serviceRef.call("onLowMemory");
			} catch (Exception e) {
				e.printStackTrace();
			}
	}

	@Override
	public void onTrimMemory(int level) {
		super.onTrimMemory(level);
		if (apk_object != null) {
			try {
				serviceRef.call("onTrimMemory");
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	@Override
	public boolean onUnbind(Intent intent) {
		if (apk_object != null) {
			try {
				return serviceRef.call("onUnbind", intent).get();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		return super.onUnbind(intent);
	}

	@Override
	public void onRebind(Intent intent) {
		super.onRebind(intent);
		if (apk_object != null) {
			try {
				serviceRef.call("onRebind", intent);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
		if (apk_object != null) {
			try {
				serviceRef.call("onDestroy");
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		if (apk_object == null || !apk_object.isServiceInit()) {
			return super.onStartCommand(intent, flags, startId);
		}
		try {
			return serviceRef.call("onStartCommand", intent, flags, startId)
					.get();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return -1;
	}

	@Override
	public void onTaskRemoved(Intent rootIntent) {
		super.onTaskRemoved(rootIntent);
		if (apk_object == null || !apk_object.isServiceInit()) {
			return;
		}
		try {
			serviceRef.call("onTaskRemoved", rootIntent);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
