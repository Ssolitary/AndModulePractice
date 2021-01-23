package com.finddreams.module_base.utils.broadcast.wrappers;

class ValueWrapper<T> {

    int mSequence;
    T mValue;

    ValueWrapper(int sequence, T value) {
        this.mValue = value;
        this.mSequence = sequence;
    }
}
