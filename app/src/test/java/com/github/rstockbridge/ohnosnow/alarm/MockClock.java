package com.github.rstockbridge.ohnosnow.alarm;

import com.github.rstockbridge.ohnosnow.utils.Clock;

public final class MockClock implements Clock {

    private final long currentTimeMillis;

    public MockClock(final long currentTimeMillis) {
        this.currentTimeMillis = currentTimeMillis;
    }

    @Override
    public long getCurrentMillis() {
        return currentTimeMillis;
    }

}
