package com.tryine.tenor.manger;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.util.Log;

import com.tryine.tenor.services.PlayerMusicService;


/**
 * <pre>
 *     author : Aaglnny
 *     time   : 2018/05/02
 *     desc   :
 * </pre>
 */
public class ScreenBroadcastListener {
    private static final String TAG = "ScreenBroadcastListener";

    private Context mContext;

    private ScreenBroadcastReceiver mScreenReceiver;

    private ScreenStateListener mListener;

    public ScreenBroadcastListener(Context context) {
        mContext = context.getApplicationContext();
        mScreenReceiver = new ScreenBroadcastReceiver();
    }

    public interface ScreenStateListener {

        void onScreenOn();

        void onScreenOff();
    }

    /**
     * screen状态广播接收者
     */
    public class ScreenBroadcastReceiver extends BroadcastReceiver {
        private String action = null;

        @Override
        public void onReceive(Context context, Intent intent) {
            action = intent.getAction();
            if (Intent.ACTION_SCREEN_OFF.equals(action)) { // 锁屏
                mListener.onScreenOff();

                //开启后台无声音乐
                mContext.startService(new Intent(mContext, PlayerMusicService.class));
                Log.i(TAG, "startService PlayerMusicService");
            } else { // 开屏或解锁
                mListener.onScreenOn();
            }
        }
    }

    public void registerListener(ScreenStateListener listener) {
        mListener = listener;
        registerListener();
    }

    private void registerListener() {
        IntentFilter filter = new IntentFilter();
        filter.addAction(Intent.ACTION_SCREEN_ON);
        filter.addAction(Intent.ACTION_SCREEN_OFF);
        filter.addAction(Intent.ACTION_USER_PRESENT);
        mContext.registerReceiver(mScreenReceiver, filter);
    }
}