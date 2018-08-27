package com.tryine.tenor.services;

import android.annotation.TargetApi;
import android.app.Service;
import android.app.job.JobParameters;
import android.app.job.JobService;
import android.content.Intent;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

import com.blankj.utilcode.util.AppUtils;
import com.blankj.utilcode.util.ServiceUtils;
import com.tryine.tenor.MainActivity;
import com.tryine.tenor.utils.SystemUtils;


/**
 * JobService，支持5.0以上forcestop依然有效
 * <p>
 * Created by jianddongguo on 2017/7/10.
 */
@TargetApi(21)
public class AliveJobService extends JobService {
    private static final String TAG = "AliveJobService";
    // 告知编译器，这个变量不能被优化
    private volatile Service mKeepAliveService = null;

    private static boolean isStart = false;

    public static boolean isJobServiceAlive() {
//        return mKeepAliveService != null;
        return isStart;
    }

    private static final int MESSAGE_ID_TASK = 0x01;

    private Handler mHandler = new Handler(new Handler.Callback() {
        @Override
        public boolean handleMessage(Message msg) {
            // 具体任务逻辑
            if (SystemUtils.isProessRunning(getApplicationContext(), AppUtils.getAppPackageName())) {
//                Toast.makeText(getApplicationContext(), "APP活着的", Toast.LENGTH_SHORT)
//                        .show();


                startMessageService();

            } else {
                Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
                Log.i(TAG, "handleMessage: APP被杀死，重启...");
            }
            // 通知系统任务执行结束
            jobFinished((JobParameters) msg.obj, false);
            return true;
        }
    });

    private void startMessageService() {
        if (ServiceUtils.isServiceRunning("com.yaxunkeji.talkback.service.MessageService")) {
            Log.d(TAG, "handleMessage: MessageService is Running 试图播放未读消息");
        } else {
//

            startService(new Intent(getApplicationContext(), PlayerMusicService.class));
        }
    }

    @Override
    public boolean onStartJob(JobParameters params) {

        Log.d(TAG, "KeepAliveService----->JobService服务被启动...");
        mKeepAliveService = this;
        isStart = true;
        // 返回false，系统假设这个方法返回时任务已经执行完毕；
        // 返回true，系统假定这个任务正要被执行
        Message msg = Message.obtain(mHandler, MESSAGE_ID_TASK, params);
        mHandler.sendMessage(msg);
        return true;
    }

    @Override
    public boolean onStopJob(JobParameters params) {
        mHandler.removeMessages(MESSAGE_ID_TASK);
        isStart = false;

        Log.d(TAG, "KeepAliveService----->JobService服务被关闭");
        return false;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        isStart = false;
    }
}
