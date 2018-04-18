package com.sanginfo.intripper.libraryproject.ui;

import android.app.Application;
import android.content.Context;
import android.support.multidex.MultiDex;
import android.util.Log;

import com.sanginfo.intripper.libraryproject.controls.TopExceptionHandler;


public class IntripperApplication extends Application {

    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(base);

        MultiDex.install(this);
        Log.d("application","attachbase");

    }

    @Override
    public void onCreate() {
        super.onCreate();
        try{

            Log.d("application","oncreate");
            Thread.setDefaultUncaughtExceptionHandler(new TopExceptionHandler(getApplicationContext()));
        }
        catch(Exception ex){
            System.err.println(ex);
        }
    }


}
