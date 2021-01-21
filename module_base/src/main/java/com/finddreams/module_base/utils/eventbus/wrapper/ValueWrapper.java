package com.finddreams.module_base.utils.eventbus.wrapper;

final class ValueWrapper<T> {
    int sequence;
    T value;

    ValueWrapper(T value, int sequence) {
        this.sequence = sequence;
        this.value = value;
    }
}
