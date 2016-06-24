package com.example.administrator.imageloader.listenner;

import org.xutils.common.Callback;

/**
 * Created by Administrator on 2016/6/20 0020.
 */
public class MyCacheCallBack<T> implements Callback.CacheCallback<T> {
    @Override
    public boolean onCache(T result) {
        return false;
    }

    @Override
    public void onSuccess(T result) {

    }

    @Override
    public void onError(Throwable ex, boolean isOnCallback) {

    }

    @Override
    public void onCancelled(CancelledException cex) {

    }

    @Override
    public void onFinished() {

    }
}