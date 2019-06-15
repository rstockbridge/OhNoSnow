package com.github.rstockbridge.ohnosnow.api.models;

import androidx.annotation.Nullable;

class Forecast {

    @Nullable
    private Double precipAccumulation;

    @Nullable
    private String precipType;

    double getSnowAccumulation() {
        if (precipAccumulation != null && precipType.equals("snow")) {
            return precipAccumulation;
        } else {
            return 0;
        }
    }
}
