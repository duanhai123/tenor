package com.tryine.tenor;

import android.annotation.TargetApi;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

import com.tryine.tenor.manger.JobSchedulerManager;
import com.tryine.tenor.services.KeepLiveService;
import com.tryine.tenor.services.LiveService;
import com.tryine.tenor.services.PlayerMusicService;
import com.tryine.tenor.utils.SystemUtils;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initLive();

        findViewById(R.id.tv).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(MainActivity.this,MainActivitys.class));
            }
        });
    }

    //进程保活方法
    private void initLive() {
        startService(new Intent(this, LiveService.class));

        startService(new Intent(this, KeepLiveService.class));

        startService(new Intent(this, PlayerMusicService.class));

        if (android.os.Build.VERSION.SDK_INT >= 21) {
            initJobScheduler();
        }
    }
    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    private void initJobScheduler() {
        if (SystemUtils.isClassExist("android.app.job.JobScheduler")) {
            // 2. 启动系统任务
            try {
                JobSchedulerManager mJobManager = JobSchedulerManager.getJobSchedulerInstance(getApplicationContext());
                mJobManager.startJobScheduler();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}
