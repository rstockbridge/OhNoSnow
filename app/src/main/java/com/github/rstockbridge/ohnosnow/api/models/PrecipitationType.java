package com.github.rstockbridge.ohnosnow.api.models;

import androidx.annotation.NonNull;

public class PrecipitationType {

    @NonNull
    private String value;

    boolean isSnow() {
        return value.equals("snow");
    }
}
