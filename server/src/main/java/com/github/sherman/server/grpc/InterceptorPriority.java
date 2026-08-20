package com.github.sherman.server.grpc;

/** Numeric priorities used to order server interceptors. */
public enum InterceptorPriority {

    LAST(Integer.MIN_VALUE),
    NONE(Integer.MAX_VALUE / 2),
    FIRST(Integer.MAX_VALUE - 1),
    AUTH(Integer.MAX_VALUE);

    private final int value;

    InterceptorPriority(int value) {
        this.value = value;
    }

    public int getValue() {
        return value;
    }
}
