package com.tryine.tenor.utils;

import android.app.ActivityManager;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;

import java.util.List;

/**
 * <pre>
 *     author : Aaglnny
 *     time   : 2018/04/27
 *     desc   :
 * </pre>
 */
public class SystemUtils {
    public static boolean isProessRunning(Context context, String proessName) {

        boolean isRunning = false;
        ActivityManager am = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);

        List<ActivityManager.RunningAppProcessInfo> lists = am.getRunningAppProcesses();
        for (ActivityManager.RunningAppProcessInfo info : lists) {
            if (info.processName.equals(proessName)) {
                //Log.i("Service2进程", ""+info.processName);
                isRunning = true;
            }
        }

        return isRunning;
    }

    public static void toSelfSetting(Context context) {
        Intent mIntent = new Intent();
        mIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        if (Build.VERSION.SDK_INT >= 9) {
            mIntent.setAction("android.settings.APPLICATION_DETAILS_SETTINGS");
            mIntent.setData(Uri.fromParts("package", context.getPackageName(), null));
        } else if (Build.VERSION.SDK_INT <= 8) {
            mIntent.setAction(Intent.ACTION_VIEW);
            mIntent.setClassName("com.android.settings", "com.android.setting.InstalledAppDetails");
            mIntent.putExtra("com.android.settings.ApplicationPkgName", context.getPackageName());
        }
        context.startActivity(mIntent);
    }
    public static boolean isClassExist(String className) {
        boolean isWoExist = true;
        try {
            Class.forName(className);
        } catch (Exception ex) {
            isWoExist = false;
        }
        return isWoExist;
    }
}
