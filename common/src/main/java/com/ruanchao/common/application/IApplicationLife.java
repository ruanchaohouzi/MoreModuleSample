package com.ruanchao.common.application;

import android.app.Application;
import android.content.Context;

/**
 * Created by ruanchao on 2018/2/5.
 */

public interface IApplicationLife {

    void attachBaseContext(Context baseContext);

    void onCreate(Application baseContext);

    void onTerminate(Application baseContext);
}
