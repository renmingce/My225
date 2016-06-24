package com.example.administrator.imageloader.activity;

import android.app.Application;

import com.example.administrator.imageloader.util.AppUtils;

import org.xutils.BuildConfig;
import org.xutils.x;

/**
 * Created by Administrator on 2016/6/20.
 */
public class MyApplication extends Application {


    @Override
    public void onCreate() {
        super.onCreate();

        x.Ext.init(this);
        x.Ext.setDebug(BuildConfig.DEBUG);

        AppUtils.setContext(this);
    }
}
