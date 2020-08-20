package com.github.rstockbridge.ohnosnow.api.models;

import androidx.annotation.NonNull;

import com.squareup.moshi.Json;

public class Forecast {

    @NonNull
    private Precipitation precipitation;

    @Json(name = "precipitation_type")
    @NonNull
    private PrecipitationType precipitationType;


    public double getSnowAccumulation() {
        if (precipitationType.isSnow()) {
            return precipitation.getAmount();
        } else {
            return 0;
        }
    }
}
