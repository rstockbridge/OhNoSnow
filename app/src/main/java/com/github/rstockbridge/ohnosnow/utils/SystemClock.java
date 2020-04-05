package com.github.rstockbridge.ohnosnow.utils;

public class SystemClock implements Clock {
    @Override
    public long getCurrentMillis() {
        return System.currentTimeMillis();
    }
}
