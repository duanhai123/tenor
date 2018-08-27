package com.tryine.tenor.manger;

import android.app.Activity;
import android.content.Context;

import java.lang.ref.WeakReference;

/**
 * <pre>
 *     author : Aaglnny
 *     time   : 2018/05/02
 *     desc   :
 * </pre>
 */
public class ScreenManager {
    private static final String TAG = "ScreenManager";
    private Context mContext;

    private WeakReference<Activity> mActivityWref;

    public static ScreenManager gDefualt;

    public static ScreenManager getInstance(Context pContext) {
        if (gDefualt == null) {
            gDefualt = new ScreenManager(pContext.getApplicationContext());
        }
        return gDefualt;
    }
    private ScreenManager(Context pContext) {
        this.mContext = pContext;
    }

    public void setActivity(Activity pActivity) {
        mActivityWref = new WeakReference<Activity>(pActivity);
    }

    public void startActivity() {
        LiveActivity.actionToLiveActivity(mContext);
    }

    public void finishActivity() {
        //结束掉LiveActivity
        if (mActivityWref != null) {
            Activity activity = mActivityWref.get();
            if (activity != null) {
                activity.finish();
            }
        }
    }
}
