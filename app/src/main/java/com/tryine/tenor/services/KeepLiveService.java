package com.tryine.tenor.services;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Build;
import android.os.IBinder;
import android.os.SystemClock;
import android.util.Log;

import com.blankj.utilcode.util.AppUtils;
import com.tryine.tenor.R;


/**
 * <pre>
 *     author : Aaglnny
 *     time   : 2018/05/02
 *     desc   : 前台服务
 * </pre>
 */
public class KeepLiveService extends Service {
    private static final String TAG = "KeepLiveService";
    public static final int NOTIFICATION_ID = 0x11;

    public KeepLiveService() {
    }

    @Override
    public IBinder onBind(Intent intent) {
        throw new UnsupportedOperationException("Not yet implemented");
    }

    @Override
    public void onCreate() {
        super.onCreate();
        Log.i(TAG, "onCreate: ");
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        // 如果Service被终止
        // 当资源允许情况下，重启service
        //API 18以下，直接发送Notification并将其置为前台
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.JELLY_BEAN_MR2) {
            startForeground(NOTIFICATION_ID, new Notification());
        }
//        else if (Build.VERSION.SDK_INT < Build.VERSION_CODES.N) {
        //Android 7.0之后startForeground两次之后不能取消
        else {
            Bitmap largeIcon = BitmapFactory.decodeResource(getResources(), R.drawable.ic_launcher);
            //API 18以上，发送Notification并将其置为前台后，启动InnerService

            Intent msgIntent = getPackageManager().getLaunchIntentForPackage(getApplicationInfo().packageName);
            PendingIntent pendingIntent = PendingIntent.getActivity(this, NOTIFICATION_ID, msgIntent, PendingIntent.FLAG_UPDATE_CURRENT);

            Notification.Builder builder = new Notification.Builder(this);
            builder.setSmallIcon(R.drawable.ic_launcher);
            builder.setLargeIcon(largeIcon);
            builder.setContentTitle(getResources().getString(R.string.app_name));
            builder.setContentText(AppUtils.getAppName() + "正在后台运行");
            builder.setContentIntent(pendingIntent);
            startForeground(NOTIFICATION_ID, builder.build());
            startService(new Intent(this, InnerService.class));


//            NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(this)
//                    .setLargeIcon(largeIcon)
//                    .setContentText(AppUtils.getAppName() + "正在后台运行")
//                    .setWhen(System.currentTimeMillis())
//                    .setAutoCancel(false);
//
//            startForeground(NOTIFICATION_ID, mBuilder.build());
//            startService(new Intent(this, InnerService.class));
        }

        return START_REDELIVER_INTENT;
    }

    public static class InnerService extends Service {
        @Override
        public IBinder onBind(Intent intent) {
            return null;
        }

        @Override
        public void onCreate() {
            super.onCreate();

            Intent msgIntent = getPackageManager().getLaunchIntentForPackage(getApplicationInfo().packageName);
            PendingIntent pendingIntent = PendingIntent.getActivity(this, NOTIFICATION_ID, msgIntent, PendingIntent.FLAG_UPDATE_CURRENT);

            //发送与KeepLiveService中ID相同的Notification，然后将其取消并取消自己的前台显示
            Bitmap largeIcon = BitmapFactory.decodeResource(getResources(), R.drawable.ic_launcher);
            Notification.Builder builder = new Notification.Builder(this);
            builder.setSmallIcon(R.drawable.ic_launcher);
            builder.setLargeIcon(largeIcon);
            builder.setContentTitle(getResources().getString(R.string.app_name));
            builder.setContentText(AppUtils.getAppName() + "正在后台运行");
            builder.setContentIntent(pendingIntent);
            startForeground(NOTIFICATION_ID, builder.build());

//            Bitmap largeIcon = BitmapFactory.decodeResource(getResources(), com.hyphenate.easeui.R.drawable.ic_launcher);
//            NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(this)
//                    .setLargeIcon(largeIcon)
//                    .setContentText(AppUtils.getAppName() + "正在后台运行")
//                    .setWhen(System.currentTimeMillis())
//                    .setAutoCancel(false);
//
//            startForeground(NOTIFICATION_ID, mBuilder.build());
            // 开启一条线程，去移除Notification
            new Thread(new Runnable() {
                @Override
                public void run() {
                    // 延迟1s
                    SystemClock.sleep(1000);
                    // 取消CancelNoticeService的前台
                    stopForeground(true);
                    // 移除DaemonService弹出的通知
                    NotificationManager manager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
                    manager.cancel(NOTIFICATION_ID);
                    // 任务完成，终止自己
                    stopSelf();
                }
            }).start();
        }
    }
}