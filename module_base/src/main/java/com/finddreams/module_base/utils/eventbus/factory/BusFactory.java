package com.finddreams.module_base.utils.eventbus.factory;

import android.os.Build;
import android.os.Handler;
import android.os.Looper;

import com.finddreams.module_base.utils.eventbus.wrapper.LiveEventWrapper;

import java.util.HashMap;

import androidx.annotation.NonNull;

public class BusFactory {
    private static final BusFactory sInstance = new BusFactory();
    private final Object mLock = new Object();
    HashMap<String, LiveEventWrapper<Object>> eventBus;
    private volatile Handler mMainHandler;

    private BusFactory() {
        eventBus = new HashMap<>();
    }

    public static BusFactory ready() {
        return sInstance;
    }

    private static Handler createAsync(@NonNull Looper looper) {
        if (Build.VERSION.SDK_INT >= 28) {
            return Handler.createAsync(looper);
        }
        return new Handler(looper);
    }

    public LiveEventWrapper<Object> create(String event) {
        if (!eventBus.containsKey(event)) {
            eventBus.put(event, new LiveEventWrapper());
        }
        return eventBus.get(event);
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

