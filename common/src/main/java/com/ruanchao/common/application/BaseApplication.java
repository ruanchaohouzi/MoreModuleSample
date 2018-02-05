package com.ruanchao.common.application;

import android.app.Application;
import android.content.Context;
import android.support.multidex.MultiDexApplication;

import com.alibaba.android.arouter.launcher.ARouter;

/**
 * Created by ruanchao on 2018/2/5.
 */

public class BaseApplication extends MultiDexApplication {

    ApplicationDelegate applicationDelegate;
    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(base);
        applicationDelegate = new ApplicationDelegate();
        applicationDelegate.attachBaseContext(base);
    }

    @Override
    public void onCreate() {
        super.onCreate();
        ARouter.init(this);

        applicationDelegate.onCreate(this);
    }

    @Override
    public void onTerminate() {
        super.onTerminate();
        applicationDelegate.onTerminate(this);
    }
}
