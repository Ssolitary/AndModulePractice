package com.finddreams.module_base.utils.broadcast;

import android.os.Build;
import android.os.Handler;
import android.os.Looper;

import com.finddreams.module_base.utils.broadcast.wrappers.ChannelWrapper;

import java.util.HashMap;

public class BroadcastManager {

    private static BroadcastManager instance;
    private final HashMap<String, ChannelWrapper<Object>> mChannelMap;
    private final Object mLock;
    // http://www.cs.umd.edu/~pugh/java/memoryModel/DoubleCheckedLocking.html
    // Fixing Double-Checked Locking using volatile
    private volatile Handler mMainHandler;

    private BroadcastManager() {
        mLock = new Object();
        mChannelMap = new HashMap<>();
    }

    public static BroadcastManager getInstance() {
        if (instance == null) {
            instance = new BroadcastManager();
        }
        return instance;
    }

    private static Handler createAsync(Looper looper) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
            return Handler.createAsync(looper);
        }
        return new Handler(looper);
    }

    public ChannelWrapper<Object> getChannel(String channel) {
        if (!mChannelMap.containsKey(channel)) {
            mChannelMap.put(channel, new ChannelWrapper<>());
        }
        return mChannelMap.get(channel);
    }

    // http://www.cs.umd.edu/~pugh/java/memoryModel/DoubleCheckedLocking.html
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

