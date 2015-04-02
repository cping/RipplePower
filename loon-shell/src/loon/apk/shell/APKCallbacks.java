package loon.apk.shell;

import android.os.Bundle;
import android.view.KeyEvent;
import java.util.ArrayList;

public class APKCallbacks {

	private static final ArrayList<APKActivityCallback> apksMapForPath = new ArrayList<APKActivityCallback>();

	public static void addActivityCallback(APKActivityCallback callback) {
		apksMapForPath.add(callback);
	}

	public static void removeActivityCallback(APKActivityCallback callback) {
		apksMapForPath.remove(callback);
	}

	public static void runAllOnCreate(final Bundle args) {
		for (APKActivityCallback element : apksMapForPath) {
			element.runOnCreate(args);
		}
	}

	public static void runAllOnStart() {
		for (APKActivityCallback element : apksMapForPath) {
			element.runOnStart();
		}
	}

	public static void runAllOnStop() {
		for (APKActivityCallback element : apksMapForPath) {
			element.runOnStop();
		}
	}

	public static void runAllOnResume() {
		for (APKActivityCallback element : apksMapForPath) {
			element.runOnResume();
		}
	}

	public static void runAllOnRestart() {
		for (APKActivityCallback element : apksMapForPath) {
			element.runOnRestart();
		}
	}

	public static void runAllOnBackPressed() {
		for (APKActivityCallback element : apksMapForPath) {
			element.runOnBackPressed();
		}
	}

	public static void runAllOnDestroy() {
		for (APKActivityCallback element : apksMapForPath) {
			element.runOnDestroy();
		}
	}

	public static void runAllOnSaveInstanceState(final Bundle out) {
		for (APKActivityCallback element : apksMapForPath) {
			element.runOnDestroy();
			element.runOnSaveInstanceState(out);
		}
	}

	public static void runAllOnRestoreInstanceState(
			final Bundle savedInstanceState) {
		for (APKActivityCallback element : apksMapForPath) {
			element.runOnRestoreInstanceState(savedInstanceState);
		}
	}

	public static void runAllOnKeyDown(final int keyCode, final KeyEvent event) {
		for (APKActivityCallback element : apksMapForPath) {
			element.runOnKeyDown(keyCode, event);
		}
	}

	public static void runAllOnPause() {
		for (APKActivityCallback element : apksMapForPath) {
			element.runOnPause();
		}
	}
}