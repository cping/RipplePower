package loon.apk.shell;

import android.app.Activity;
import android.app.Application;
import android.app.Service;
import android.content.IntentFilter;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageInfo;
import android.content.res.AssetManager;
import android.content.res.Resources;


import java.util.List;

public class APKObject {

   private Application apkApplication;

    private Service apkService;

    private String path;
  
    private AssetManager apkAssetManager;

    private Resources apkRes;

    private Resources.Theme currentAPKTheme;

    private ActivityInfo activityInfos[];

    private PackageInfo apkPkgInfo;
  
    private Activity proxyParent;

    private Activity currentAPKActivity;
   
    private ClassLoader APKLoader;

    boolean isAPKInit;

    private List<IntentFilter> apkFilters;

    private String topActivityName = null;

    private APKActivityControl control;

    private boolean isComplete = false;

    private String appName;

    private String currentServiceClassName = null;

    public boolean isCompleted() {
        return isComplete;
    }

    public void setCompleted(boolean c) {
        this.isComplete = c;
    }

    public boolean isServiceInit() {
        return apkService != null;
    }


    public String getCurrentServiceClassName() {
        return currentServiceClassName;
    }

    public void setCurrentServiceClassName(String currentServiceClassName) {
        this.currentServiceClassName = currentServiceClassName;
    }

    public String getAppName() {
        return appName;
    }

    public void setAppName(String appName) {
        this.appName = appName;
    }

    public Service getAPKService() {
        return apkService;
    }

    public void setAPKService(Service apkService) {
        this.apkService = apkService;
    }

    public Application getAPKApplication() {
        return apkApplication;
    }

    public void bindAPKApp(Application APKApp) {
        this.apkApplication = APKApp;
    }

    public void setAPKFilters(List<IntentFilter> apkFilters) {
        this.apkFilters = apkFilters;
    }

    public String getTopActivityName() {
        return topActivityName;
    }

    public void setTopActivityName(String topActivityName) {
        this.topActivityName = topActivityName;
    }

    public APKActivityControl getControl() {
        return control;
    }

    public void setControl(APKActivityControl control) {
        this.control = control;
    }


    public APKObject(Activity proxyParent,String apkPath){
        this.proxyParent = proxyParent;
        this.path = apkPath;

    }

    public Activity getProxyParent() {
        return proxyParent;
    }

    public List<IntentFilter> getAPKFilters() {
        return apkFilters;
    }

    public void setProxyParent(Activity proxyParent) {
        this.proxyParent = proxyParent;
    }

    public String getAPKPath() {
        return path;
    }

    public void setAPKPath(String path) {
        this.path = path;
    }

    public AssetManager getAPKAssetManager() {
        return apkAssetManager;
    }

    public void setAPKAssetManager(AssetManager apkAssetManager) {
        this.apkAssetManager = apkAssetManager;
    }

    public Resources getAPKRes() {
        return apkRes;
    }

    public void setAPKRes(Resources apkRes) {
        this.apkRes = apkRes;
    }

    public Resources.Theme getCurrentAPKTheme() {
        return currentAPKTheme;
    }

    public void setCurrentAPKTheme(Resources.Theme currentAPKTheme) {
        this.currentAPKTheme = currentAPKTheme;
    }

    public ActivityInfo[] getActivityInfos() {
        return activityInfos;
    }

    public void setActivityInfos(ActivityInfo[] activityInfos) {
        this.activityInfos = activityInfos;
    }

    public PackageInfo getAPKPkgInfo() {
        return apkPkgInfo;
    }

    public void setAPKPkgInfo(PackageInfo apkPkgInfo) {
        this.apkPkgInfo = apkPkgInfo;
        this.activityInfos = apkPkgInfo.activities;
    }

    public Activity getCurrentAPKActivity() {
        return currentAPKActivity;
    }

    public void setCurrentAPKActivity(Activity c) {
        currentAPKActivity = c;
    }

    public ClassLoader getAPKLoader() {
        return APKLoader;
    }

    public void setAPKLoader(ClassLoader APKLoader) {
        this.APKLoader = APKLoader;
    }

    public boolean isAPKInit() {
        isAPKInit = activityInfos != null;
        isAPKInit = getCurrentAPKActivity() != null;
        return isAPKInit;
    }


}
