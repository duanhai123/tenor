package com.tryine.tenor.services;

import android.app.Service;
import android.content.Intent;
import android.content.res.AssetFileDescriptor;
import android.content.res.AssetManager;
import android.media.MediaPlayer;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.util.Log;

import java.io.FileDescriptor;
import java.io.IOException;


/**
 * 循环播放一段无声音频，以提升进程优先级
 * <p>
 * Created by jianddongguo on 2017/7/11.
 * http://blog.csdn.net/andrexpert
 */

public class PlayerMusicService extends Service {
    private final static String TAG = "PlayerMusicService";
    private MediaPlayer mMediaPlayer;

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.d(TAG, TAG + "---->onCreate,启动服务");
//        mMediaPlayer = MediaPlayer.create(getApplicationContext(), R.raw.silent);
        if (mMediaPlayer == null || !mMediaPlayer.isPlaying()) {
            try {
                AssetManager assetManager = getAssets();
                AssetFileDescriptor afd = null;
                afd = assetManager.openFd("silent.mp3");
                FileDescriptor fd = afd.getFileDescriptor();
                mMediaPlayer = new MediaPlayer();
                mMediaPlayer.setDataSource(fd, afd.getStartOffset(), afd.getLength());
                mMediaPlayer.setLooping(true);
                mMediaPlayer.prepareAsync();

                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        startPlayMusic();
                    }
                }).start();
                Log.d(TAG, TAG + "---->onStartCommand,startPlayMusic");
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        return START_STICKY;
    }

    private void startPlayMusic() {
        if (mMediaPlayer != null) {

            Log.d(TAG, "启动后台播放音乐");
            mMediaPlayer.start();
        }
    }

    private void stopPlayMusic() {
        if (mMediaPlayer != null) {

            Log.d(TAG, "关闭后台播放音乐");
            mMediaPlayer.stop();
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        stopPlayMusic();

        Log.d(TAG, TAG + "---->onCreate,停止服务");
        // 重启自己
        Intent intent = new Intent(getApplicationContext(), PlayerMusicService.class);
        startService(intent);
    }
}
