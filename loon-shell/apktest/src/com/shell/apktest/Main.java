package com.shell.apktest;

import loon.apk.shell.APKSetting;
import loon.apk.shell.APKShellActivity;


public class Main extends APKShellActivity {

	@Override
	public void onMain() {
		APKSetting setting = new APKSetting();
		setting.apkPath = "game.zip";
		setting.apkActivityName = "com.mygame.Main";
		register(setting);
	}


}
