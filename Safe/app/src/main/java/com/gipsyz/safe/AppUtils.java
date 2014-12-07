package com.gipsyz.safe;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.app.ActivityManager;
import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.util.Patterns;

import java.util.regex.Pattern;

/**
 * Created by batman on 06/12/2014.
 */
public class AppUtils {
    public final static String TAG = "Safe";
    public static final String HIDDEN_PREFS = "hidden_prefs";
    public static final String BASE_URL = "http://192.168.1.147:8080";
    public static final String OPEN_URL = BASE_URL + "/open";
    public static final String BEACON_URL = BASE_URL + "/api/beacon";


    public static boolean isServiceRunning(String className, Context context) {
        ActivityManager manager = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        for (ActivityManager.RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE)) {
            if (className.equals(service.service.getClassName())) {
                return true;
            }
        }
        return false;
    }

    public static int getAppVersion(Context context) {
        try {
            PackageInfo packageInfo = context.getPackageManager()
                    .getPackageInfo(context.getPackageName(), 0);
            return packageInfo.versionCode;
        } catch (PackageManager.NameNotFoundException e) {
            // should never happen
            throw new RuntimeException("Could not get package name: " + e);
        }
    }
}
