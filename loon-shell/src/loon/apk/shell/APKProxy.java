package loon.apk.shell;

import android.app.Activity;

public interface APKProxy {

    APKObject loadAPK(Activity proxyParent,String apkPath);

    APKObject loadAPK(Activity proxyParent,String apkPath ,boolean checkInit);

    APKObject loadAPK(Activity proxyParent,String apkPath,String activityName);

    APKObject loadAPK(Activity proxyParent,String apkPath,int index);

    void fillAPK(APKObject apk);

}
