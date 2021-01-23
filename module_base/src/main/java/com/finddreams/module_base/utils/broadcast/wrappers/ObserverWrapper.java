package com.finddreams.module_base.utils.broadcast.wrappers;

import androidx.lifecycle.Observer;

public abstract class ObserverWrapper<T> {

    int mSequence;
    boolean mIsSticky = false;
    Observer<ValueWrapper<T>> mObserver;

    public abstract void onChanged(T t);
}
