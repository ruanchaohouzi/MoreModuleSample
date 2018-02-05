package com.ruanchao.common.application;

import android.app.Activity;
import android.app.Application;
import android.content.Context;

import java.util.List;

/**
 * Created by ruanchao on 2018/2/5.
 */

public interface IModuleConfig {

    void addApplicationLife(Context context, List<IApplicationLife> applicationLifeList);

    void addActivityLife(Context context, List<Application.ActivityLifecycleCallbacks> activityLifecycleCallbacks);
}
