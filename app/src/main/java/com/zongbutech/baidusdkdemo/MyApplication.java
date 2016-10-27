package com.zongbutech.baidusdkdemo;

import android.app.Application;

import com.baidu.mapapi.SDKInitializer;

/**
 * Created by lixian on 2016/9/28.
 */
public class MyApplication extends Application {

    @Override
    public void onCreate() {
        super.onCreate();
        SDKInitializer.initialize(getApplicationContext());

    }
}
