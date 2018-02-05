package com.ruanchao.robot;

import android.app.Application;
import android.content.Context;

import com.ruanchao.common.application.IApplicationLife;
import com.ruanchao.common.application.IModuleConfig;

import java.util.List;

/**
 * Created by ruanchao on 2018/2/5.
 */

public class RobotApplication implements IApplicationLife,IModuleConfig {
    @Override
    public void attachBaseContext(Context baseContext) {

    }

    @Override
    public void onCreate(Application baseContext) {

    }

    @Override
    public void onTerminate(Application baseContext) {

    }

    @Override
    public void addApplicationLife(Context context, List<IApplicationLife> applicationLifeList) {

    }

    @Override
    public void addActivityLife(Context context, List<Application.ActivityLifecycleCallbacks> activityLifecycleCallbacks) {

    }
}
