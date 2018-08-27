package com.tryine.tenor.manger;

import android.annotation.TargetApi;
import android.app.job.JobInfo;
import android.app.job.JobScheduler;
import android.content.ComponentName;
import android.content.Context;
import android.os.Build;
import android.util.Log;

import com.tryine.tenor.services.AliveJobService;


/**
 * JobScheduler管理类，单例模式
 * 执行系统任务
 * <p>
 * Created by jianddongguo on 2017/7/10.
 * http://blog.csdn.net/andrexpert
 */

public class JobSchedulerManager {
    private static final String TAG = "JobSchedulerManager";
    private static final int JOB_ID = 1;
    private static JobSchedulerManager mJobManager;
    private JobScheduler mJobScheduler;
    private Context mContext;

    private JobSchedulerManager(Context ctxt) {
        this.mContext = ctxt;
        mJobScheduler = (JobScheduler) ctxt.getSystemService(Context.JOB_SCHEDULER_SERVICE);
    }

    public final static JobSchedulerManager getJobSchedulerInstance(Context ctxt) {
        if (mJobManager == null) {
            mJobManager = new JobSchedulerManager(ctxt.getApplicationContext());
        }
        return mJobManager;
    }

    @TargetApi(21)
    public void startJobScheduler() {
        if (mJobScheduler == null) {
            return;
        }
        // 如果JobService已经启动或API<21，返回
        if (AliveJobService.isJobServiceAlive() || isBelowLOLLIPOP()) {
            Log.i(TAG, "startJobScheduler: JobScheduler已经启动");
            return;
        }
        Log.i(TAG, "startJobScheduler: 启动JobScheduler");
        // 构建JobInfo对象，传递给JobSchedulerService
        JobInfo.Builder builder = new JobInfo.Builder(JOB_ID, new ComponentName(mContext, AliveJobService.class));
        // 任务最少延迟时间为10s
//        builder.setMinimumLatency(10 * 1000);
        //循环执行，循环时长为一天（最小为15分钟）
        builder.setPeriodic(15 * 60 * 1000);
        // 需要满足网络条件，默认值NETWORK_TYPE_NONE
        builder.setRequiredNetworkType(JobInfo.NETWORK_TYPE_NONE);
        // 设置设备重启时，执行该任务
        builder.setPersisted(true);
        builder.setRequiresCharging(false);
        JobInfo info = builder.build();
        //开始定时执行该系统任务
        mJobScheduler.schedule(info);
    }

    @TargetApi(21)
    public void stopJobScheduler() {
        if (isBelowLOLLIPOP())
            return;
        mJobScheduler.cancelAll();
    }

    private boolean isBelowLOLLIPOP() {
        // API< 21
        return Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP;
    }
}
