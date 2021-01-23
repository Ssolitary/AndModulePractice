package com.finddreams.module_base.utils.broadcast.wrappers;

import android.os.Looper;

import com.finddreams.module_base.utils.broadcast.BroadcastManager;

import androidx.lifecycle.LifecycleOwner;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Observer;

/**
 * Each channel has a sequence to record the order of observer's registration
 * Pass channel's sequence when we want to call setValue()
 * By default only observer with sequence smaller than channel's sequence will receive the broadcast
 * <p>
 * Ex.
 * A = new ChannelWrapper()   : A.mSequence is 0
 * B observe A                : A.mSequence is 1 / B.mSequence is 0 => B receive nothing after observing
 * C observe A                : A.mSequence is 2 / C.mSequence is 1 => C receive nothing after observing
 * <p>
 * A setValue(T = "hello") (T can be type of object), passing A.mSequence is 2 to create ValueWrapper
 * <p>
 * Because of B.mSequence is 0 < A.mSequence == 2 => B receives "hello"
 * Because of C.mSequence is 1 < A.mSequence == 2 => C receives "hello"
 * <p>
 * D observeSticky A          : A.Sequence is 2 / D.mSequence is -1 => D receives "hello" immediately after observing
 */

public class ChannelWrapper<T> {

    private final MutableLiveData<ValueWrapper<T>> mMutableLiveData;
    private int mSequence = 0;

    public ChannelWrapper() {
        mMutableLiveData = new MutableLiveData<>();
    }

    public T getValue() {
        if (mMutableLiveData.getValue() == null) {
            return null;
        }
        return mMutableLiveData.getValue().mValue;
    }

    /**
     * No need to define postValue() because of auto thread checking
     */
    public void setValue(final T value) {
        checkThread(new Runnable() {
            @Override
            public void run() {
                mMutableLiveData.setValue(new ValueWrapper<>(mSequence, value));
            }
        });
    }

    /**
     * Call this method with observer from a LifecycleOwner (Ex. UI classes)
     * Note: observer will not receive any value after initial observe
     */
    public void observe(LifecycleOwner owner, ObserverWrapper<T> observerWrapper) {
        observe(owner, observerWrapper, false);
    }

    /**
     * Call this method with observer from a non LifecycleOwner (Ex. Managers)
     * Note: observer will not receive any value after initial observe
     */
    public void observeForever(ObserverWrapper<T> observerWrapper) {
        observe(null, observerWrapper, false);
    }

    /**
     * Call this method with observer from a LifecycleOwner (Ex. UI classes)
     * Note: observer will immediately receive the previous value insdie mMutableLiveData after initial observe (sticky)
     */
    public void observeSticky(LifecycleOwner owner, ObserverWrapper<T> observerWrapper) {
        observe(owner, observerWrapper, true);
    }

    /**
     * Call this method with observer from a non LifecycleOwner (Ex. Managers)
     * Note: observer will immediately receive the previous value insdie mMutableLiveData after initial observe (sticky)
     */
    public void observeSticky(ObserverWrapper<T> observer) {
        observe(null, observer, true);
    }

    private void observe(final LifecycleOwner owner, final ObserverWrapper<T> observerWrapper, boolean isSticky) {
        observerWrapper.mIsSticky = isSticky;
        observerWrapper.mSequence = observerWrapper.mIsSticky ? -1 : mSequence++;
        checkThread(new Runnable() {
            @Override
            public void run() {
                if (owner != null) {
                    mMutableLiveData.observe(owner, filterObserver(observerWrapper));
                } else {
                    mMutableLiveData.observeForever(filterObserver(observerWrapper));
                }
            }
        });
    }

    private Observer<ValueWrapper<T>> filterObserver(final ObserverWrapper<T> observerWrapper) {
        if (observerWrapper.mObserver != null) {
            return observerWrapper.mObserver;
        }
        return observerWrapper.mObserver = new Observer<ValueWrapper<T>>() {
            @Override
            public void onChanged(ValueWrapper<T> valueWrapper) {
                // Only observer with sequence smaller than channel's sequence (which is valueWrapper's sequence)
                // will receive the broadcast
                if (valueWrapper != null && observerWrapper.mSequence < valueWrapper.mSequence) {
                    observerWrapper.onChanged(valueWrapper.mValue);
                }
            }
        };
    }

    public boolean hasObservers() {
        return mMutableLiveData.hasObservers();
    }

    public boolean hasActiveObservers() {
        return mMutableLiveData.hasActiveObservers();
    }

    public void removeObserver(final ObserverWrapper<T> observerWrapper) {
        checkThread(new Runnable() {
            @Override
            public void run() {
                mMutableLiveData.removeObserver(filterObserver(observerWrapper));
            }
        });
    }

    public void removeObservers(final LifecycleOwner owner) {
        checkThread(new Runnable() {
            @Override
            public void run() {
                mMutableLiveData.removeObservers(owner);
            }
        });
    }

    /**
     * Make sure we are on main thread before make any change.
     */
    private void checkThread(Runnable runnable) {
        if (Looper.getMainLooper().getThread() == Thread.currentThread()) {
            runnable.run();
        } else {
            // wait to execute in main thread.
            BroadcastManager
                .getInstance()
                .getMainHandler()
                .post(runnable);
        }
    }
}
