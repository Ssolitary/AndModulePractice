/*
 * ************************************************************
 * 文件：LiveEventWrapper.java  模块：core  项目：CleanFramework
 * 当前修改时间：2019年03月31日 22:55:27
 * 上次修改时间：2019年03月31日 22:54:04
 * 作者：Cody.yi   https://github.com/codyer
 *
 * Copyright (c) 2019
 * ************************************************************
 */

package com.finddreams.module_base.utils.eventbus.wrapper;

import android.os.Looper;

import com.finddreams.module_base.utils.eventbus.factory.BusFactory;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.LifecycleOwner;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Observer;

/**
 * Created by xu.yi. on 2019/3/31.
 * 和lifecycle绑定的事件总线
 * 每添加一个observer，LiveEventWrapper 的序列号增加1，并赋值给新加的observer，
 * 每次消息更新使用目前的序列号进行请求，持有更小的序列号才需要获取变更通知。
 * <p>
 * 解决会收到注册前发送的消息更新问题
 */
@SuppressWarnings("unused")
final public class LiveEventWrapper<T> {
    private int mSequence = 0;
    private MutableLiveData<ValueWrapper<T>> mMutableLiveData;

    public LiveEventWrapper() {
        mMutableLiveData = new MutableLiveData<>();
    }

    /**
     * 是否是在主线程
     *
     * @return 是主线程
     */
    private boolean isMainThread() {
        return Looper.getMainLooper().getThread() == Thread.currentThread();
    }

    /**
     * 检查线程并执行不同的操作
     *
     * @param runnable 可运行的一段代码
     */
    private void checkThread(Runnable runnable) {
        if (isMainThread()) {
            runnable.run();
        } else {
            // 主线程中观察
            BusFactory
                .ready()
                .getMainHandler()
                .post(runnable);
        }
    }

    @Nullable
    public T getValue() {
        if (mMutableLiveData.getValue() == null) {
            return null;
        }
        return mMutableLiveData.getValue().value;
    }

    // no need to define postValue()
    public void setValue(final T value) {
        checkThread(new Runnable() {
            @Override
            public void run() {
                mMutableLiveData.setValue(new ValueWrapper<>(value, mSequence));
            }
        });
    }

    public boolean hasObservers() {
        return mMutableLiveData.hasObservers();
    }

    public boolean hasActiveObservers() {
        return mMutableLiveData.hasActiveObservers();
    }

    public void removeObserver(@NonNull final ObserverWrapper<T> observer) {
        checkThread(new Runnable() {
            @Override
            public void run() {
                mMutableLiveData.removeObserver(filterObserver(observer));
            }
        });
    }

    public void removeObservers(@NonNull final LifecycleOwner owner) {
        checkThread(new Runnable() {
            @Override
            public void run() {
                mMutableLiveData.removeObservers(owner);
            }
        });
    }

    public void observeForever(@NonNull final ObserverWrapper<T> observer) {
        observer.sequence = mSequence++;
        checkThread(new Runnable() {
            @Override
            public void run() {
                mMutableLiveData.observeForever(filterObserver(observer));
            }
        });
    }

    /**
     * 设置监听之前发送的消息也可以接受到
     */
    public void observeAny(@NonNull LifecycleOwner owner, @NonNull ObserverWrapper<T> observer) {
        observer.sequence = -1;
        observe(owner, observer);
    }

    /**
     * 设置监听之前发送的消息不可以接受到
     */
    public void observe(@NonNull final LifecycleOwner owner, @NonNull final ObserverWrapper<T> observer) {
        observer.sequence = mSequence++;
        checkThread(new Runnable() {
            @Override
            public void run() {
                mMutableLiveData.observe(owner, filterObserver(observer));
            }
        });
    }

    @NonNull
    private Observer<ValueWrapper<T>> filterObserver(@NonNull final ObserverWrapper<T> observerWrapper) {
        if (observerWrapper.observer != null) {
            return observerWrapper.observer;
        }
        return observerWrapper.observer = new Observer<ValueWrapper<T>>() {
            @Override
            public void onChanged(@Nullable ValueWrapper<T> valueWrapper) {
                if (valueWrapper != null && valueWrapper.sequence > observerWrapper.sequence) {
                    observerWrapper.onChanged(valueWrapper.value);
                }
            }
        };
    }
}
