package com.finddreams.module_base.utils.eventbus.factory;

import android.os.Build;
import android.os.Handler;
import android.os.Looper;

import com.finddreams.module_base.utils.eventbus.wrapper.LiveEventWrapper;

import java.util.HashMap;

import androidx.annotation.NonNull;

public class BroadcastManager {
    private static BroadcastManager instance;
    private Object mLock;
    private HashMap<String, LiveEventWrapper<Object>> mEventMap;
    private volatile Handler mMainHandler;

    private BroadcastManager() {
        mLock = new Object();
        mEventMap = new HashMap<>();
    }

    public static BroadcastManager getInstance() {
        if (instance == null) {
            instance = new BroadcastManager();
        }
        return instance;
    }

    private static Handler createAsync(@NonNull Looper looper) {
        if (Build.VERSION.SDK_INT >= 28) {
            return Handler.createAsync(looper);
        }
        return new Handler(looper);
    }

    public LiveEventWrapper<Object> create(String event) {
        if (!mEventMap.containsKey(event)) {
            mEventMap.put(event, new LiveEventWrapper());
        }
        return mEventMap.get(event);
    }

    public Handler getMainHandler() {
        if (mMainHandler == null) {
            synchronized (mLock) {
                if (mMainHandler == null) {
                    mMainHandler = createAsync(Looper.getMainLooper());
                }
            }
        }
        return mMainHandler;
    }
}

