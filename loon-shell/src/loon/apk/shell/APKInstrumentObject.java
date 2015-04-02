package loon.apk.shell;

import android.app.Activity;
import android.app.Application;
import android.app.Instrumentation;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.IBinder;

public class APKInstrumentObject extends Instrumentation {

	private Instrumentation apkInstrumentation;

	private APKReflect instrumentRef;

	private APKSetting setting;

	public APKInstrumentObject(Instrumentation apk, APKSetting set) {
		this.apkInstrumentation = apk;
		this.setting = set;
		this.instrumentRef = APKReflect.in(this.apkInstrumentation);
	}

	public ActivityResult execStartActivity(Context who, IBinder contextThread,
			IBinder token, Activity target, Intent intent, int requestCode,
			Bundle options) throws Exception {
		ComponentName componentName = intent.getComponent();
		if (componentName == null) {
			return instrumentRef.call("execStartActivity", who, contextThread,
					token, target, intent, requestCode, options).get();
		}
		String className = componentName.getClassName();
		intent.setClass(who, APKShellActivity.class);
		setting.apkPath = APKManager.finalApkPath;
		setting.apkActivityName = className;
		return instrumentRef.call("execStartActivity", who, contextThread,
				token, target, intent, requestCode, options).get();

	}

	@Override
	public void onStart() {
		if (apkInstrumentation != null) {
			apkInstrumentation.onStart();
		}
	}

	@Override
	public void onCreate(Bundle arguments) {
		if (apkInstrumentation != null) {
			apkInstrumentation.onCreate(arguments);
		}
	}

	@Override
	public void onDestroy() {
		if (apkInstrumentation != null) {
			apkInstrumentation.onDestroy();
		}
	}

	@Override
	public boolean onException(Object obj, Throwable e) {
		if (apkInstrumentation == null) {
			return false;
		}
		return apkInstrumentation.onException(obj, e);
	}

	@Override
	public void callActivityOnCreate(Activity activity, Bundle icicle) {
		if (apkInstrumentation == null) {
			apkInstrumentation.callActivityOnCreate(activity, icicle);
		}
	}

	@Override
	public void callActivityOnNewIntent(Activity activity, Intent intent) {
		if (apkInstrumentation == null) {
			apkInstrumentation.callActivityOnNewIntent(activity, intent);
		}
	}

	@Override
	public void callApplicationOnCreate(Application app) {
		if (apkInstrumentation == null) {
			apkInstrumentation.callApplicationOnCreate(app);
		}
	}

	@Override
	public void callActivityOnDestroy(Activity activity) {
		if (apkInstrumentation == null) {
			apkInstrumentation.callActivityOnDestroy(activity);
		}
	}

	@Override
	public void callActivityOnPause(Activity activity) {
		if (apkInstrumentation == null) {
			apkInstrumentation.callActivityOnDestroy(activity);
		}
	}

}
