package com.tryine.tenor.services;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.util.Log;

import com.tryine.tenor.manger.ScreenBroadcastListener;
import com.tryine.tenor.manger.ScreenManager;

/**
 * Name: LiveService
 * Details:
 * Created by PC on 2018/8/24.
 * Update:
 */

public class LiveService extends Service {
    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.i("LiveService", "onStartCommand: ");
        //屏幕关闭的时候启动一个1像素的Activity，开屏的时候关闭Activity
        final ScreenManager screenManager = ScreenManager.getInstance(LiveService.this);
        ScreenBroadcastListener listener = new ScreenBroadcastListener(this);
        listener.registerListener(new ScreenBroadcastListener.ScreenStateListener() {
            @Override
            public void onScreenOn() {
                Log.i("LiveService", "onScreenOn: 关闭1px activity");
                screenManager.finishActivity();
            }

            @Override
            public void onScreenOff() {
                Log.i("LiveService", "onScreenOff: 启动1px activity");
                screenManager.startActivity();
            }
        });
        return START_REDELIVER_INTENT;
    }
}
