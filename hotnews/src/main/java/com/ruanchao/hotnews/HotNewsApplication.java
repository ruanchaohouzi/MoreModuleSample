package com.ruanchao.hotnews;

import android.app.Application;
import android.content.Context;

import com.ruanchao.common.application.IApplicationLife;
import com.ruanchao.common.application.IModuleConfig;

import java.util.List;

/**
 * Created by ruanchao on 2018/2/5.
 */

public class HotNewsApplication implements IApplicationLife,IModuleConfig {
    @Override
    public void attachBaseContext(Context baseContext) {

    }

    @Override
    public void onCreate(Application baseContext) {
        //在这里处理当前模块的Application信息

        //测试效果
        TestApp.getInstance().setText("哈哈，我被Application给改掉了");
    }

    @Override
    public void onTerminate(Application baseContext) {

    }

    @Override
    public void addApplicationLife(Context context, List<IApplicationLife> applicationLifeList) {

        applicationLifeList.add(this);
    }

    @Override
    public void addActivityLife(Context context, List<Application.ActivityLifecycleCallbacks> activityLifecycleCallbacks) {

    }
}
