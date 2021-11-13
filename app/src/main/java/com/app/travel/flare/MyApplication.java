package com.app.travel.flare;

import android.app.Application;
import android.content.Context;

public class MyApplication extends Application {

    @Override
    public void onCreate() {
        sApplication = this;
        super.onCreate();
    }

    private static Application sApplication;

    public static Context getApplication() {
        return sApplication;
    }

    public static Context getContext() {
        return getApplication();
    }
}
