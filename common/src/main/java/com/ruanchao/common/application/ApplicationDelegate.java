package com.ruanchao.common.application;

import android.app.Application;
import android.content.Context;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by ruanchao on 2018/2/5.
 */

public class ApplicationDelegate implements IApplicationLife {

    List<IModuleConfig> moduleConfigList = new ArrayList<>();
    List<IApplicationLife> applicationLifeList = new ArrayList<>();
    List<Application.ActivityLifecycleCallbacks> activityLifecycleCallbackList = new ArrayList<>();

    @Override
    public void attachBaseContext(Context baseContext) {
        ManifestParser manifestParser = new ManifestParser(baseContext);
        moduleConfigList = manifestParser.parseModuleConfig();
        for (IModuleConfig moduleConfig : moduleConfigList) {
            moduleConfig.addApplicationLife(baseContext, applicationLifeList);
            moduleConfig.addActivityLife(baseContext, activityLifecycleCallbackList);
        }
        for (IApplicationLife applicationLife : applicationLifeList) {
            applicationLife.attachBaseContext(baseContext);
        }

    }

    @Override
    public void onCreate(Application baseContext) {

        for (IApplicationLife applicationLife : applicationLifeList) {
            applicationLife.onCreate(baseContext);
        }
        for (Application.ActivityLifecycleCallbacks activityLifecycleCallbacks : activityLifecycleCallbackList) {
            baseContext.registerActivityLifecycleCallbacks(activityLifecycleCallbacks);
        }
    }

    @Override
    public void onTerminate(Application baseContext) {
        for (IApplicationLife applicationLife : applicationLifeList) {
            applicationLife.onTerminate(baseContext);
        }
        for (Application.ActivityLifecycleCallbacks activityLifecycleCallbacks : activityLifecycleCallbackList) {
            baseContext.unregisterActivityLifecycleCallbacks(activityLifecycleCallbacks);
        }

    }
}
