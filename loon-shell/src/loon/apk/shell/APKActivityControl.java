package loon.apk.shell;

import android.app.Activity;
import android.app.Application;
import android.app.FragmentManager;
import android.app.Instrumentation;
import android.os.Build;
import android.os.Bundle;
import android.view.KeyEvent;

public class APKActivityControl implements APKActivityCallback {

	private Activity proxy;
	private Activity apk_object;
	private APKReflect proxyRef;
	private APKReflect apkRef;
	private Application app;
	private APKSetting setting;

	public APKActivityControl(Activity proxy, Activity apk_object,APKSetting set) {
		this(proxy, apk_object, null,null);

	}

	public APKActivityControl(Activity proxy, Activity apk_object,
			Application app,APKSetting set) {
		this.proxy = proxy;
		this.apk_object = apk_object;
		this.app = app;
		this.setting = set;
		this.proxyRef = APKReflect.in(proxy);
		this.apkRef = APKReflect.in(apk_object);
	}

	public void dispatchProxyToAPK() {
		try {

			apkRef.set("mDecor", proxyRef.get("mDecor"));
			apkRef.set("mTitleColor", proxyRef.get("mTitleColor"));
			apkRef.set("mWindowManager", proxyRef.get("mWindowManager"));
			apkRef.set("mWindow", proxy.getWindow());
			apkRef.set("mManagedDialogs", proxyRef.get("mManagedDialogs"));
			apkRef.set("mCurrentConfig", proxyRef.get("mCurrentConfig"));
			apkRef.set("mSearchManager", proxyRef.get("mSearchManager"));
			apkRef.set("mMenuInflater", proxyRef.get("mMenuInflater"));
			apkRef.set("mConfigChangeFlags",
					proxyRef.get("mConfigChangeFlags"));
			apkRef.set("mIntent", proxyRef.get("mIntent"));
			apkRef.set("mToken", proxyRef.get("mToken"));

			Instrumentation instrumentation = proxyRef.get("mInstrumentation");
			apkRef.set("mInstrumentation", new APKInstrumentObject(
					instrumentation,setting));

			apkRef.set("mMainThread", proxyRef.get("mMainThread"));
			apkRef.set("mEmbeddedID", proxyRef.get("mEmbeddedID"));

			apkRef.set("mApplication", app == null ? proxy.getApplication()
					: app);
			apkRef.set("mComponent", proxyRef.get("mComponent"));
			apkRef.set("mActivityInfo", proxyRef.get("mActivityInfo"));
			apkRef.set("mAllLoaderManagers",
					proxyRef.get("mAllLoaderManagers"));
			apkRef.set("mLoaderManager", proxyRef.get("mLoaderManager"));

			if (Build.VERSION.SDK_INT >= 13) {

				FragmentManager mFragments = proxy.getFragmentManager();
				apkRef.set("mFragments", mFragments);
				apkRef.set("mContainer", proxyRef.get("mContainer"));
			}

			if (Build.VERSION.SDK_INT >= 12) {
				apkRef.set("mActionBar", proxyRef.get("mActionBar"));
			}

			apkRef.set("mUiThread", proxyRef.get("mUiThread"));
			apkRef.set("mHandler", proxyRef.get("mHandler"));
			apkRef.set("mInstanceTracker", proxyRef.get("mInstanceTracker"));
			apkRef.set("mTitle", proxyRef.get("mTitle"));
			apkRef.set("mResultData", proxyRef.get("mResultData"));
			apkRef.set("mDefaultKeySsb", proxyRef.get("mDefaultKeySsb"));

			apk_object.getWindow().setCallback(apk_object);

		} catch (Exception e) {
		}

	}

	public Activity getAPK() {
		return apk_object;
	}

	public void setAPK(Activity a) {
		this.apk_object = a;
		this.proxyRef = APKReflect.in(a);
	}

	public Activity getProxy() {
		return proxy;
	}

	public void setProxy(Activity proxy) {
		this.proxy = proxy;
		proxyRef = APKReflect.in(proxy);
	}

	public APKReflect getProxyRef() {
		return proxyRef;
	}

	public APKReflect getAPKRef() {
		return apkRef;
	}

	@Override
	public void runOnCreate(Bundle saveInstance) {
		try {
			getAPKRef().call("onCreate", saveInstance);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@Override
	public void runOnStart() {
		try {
			getAPKRef().call("onStart");
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@Override
	public void runOnResume() {
		try {
			getAPKRef().call("onResume");
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@Override
	public void runOnDestroy() {
		try {
			getAPKRef().call("onDestroy");
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@Override
	public void runOnStop() {
		try {
			getAPKRef().call("onStop");
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@Override
	public void runOnRestart() {
		try {
			getAPKRef().call("onRestart");
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@Override
	public void runOnSaveInstanceState(Bundle outState) {
		try {
			getAPKRef().call("onSaveInstanceState", outState);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@Override
	public void runOnRestoreInstanceState(Bundle savedInstanceState) {
		try {
			getAPKRef().call("onRestoreInstanceState", savedInstanceState);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@Override
	public void runOnPause() {
		try {
			getAPKRef().call("onStop");
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@Override
	public void runOnBackPressed() {
		try {
			getAPKRef().call("onBackPressed");
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@Override
	public boolean runOnKeyDown(int keyCode, KeyEvent event) {
		try {
			return getAPKRef().call("onKeyDown", keyCode, event).get();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return false;
	}

}
