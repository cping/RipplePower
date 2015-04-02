package loon.apk.shell;

import android.app.Activity;


import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class APKManager{

    private static final Map<String,APKObject> apksMapForPath = new ConcurrentHashMap<String, APKObject>();
    public static String finalApkPath;

    public static APKObject loadAPK(Activity proxyParent,String apkPath){
        finalApkPath = apkPath;
        APKObject obj = null;
        obj = apksMapForPath.get(apkPath);
        if(obj == null){
        	obj = new APKObject(proxyParent,apkPath);
        }
        return obj;
    }

    public static APKObject getLoadedAPK(String apkPath){
        return apksMapForPath.get(apkPath);
    }

}
