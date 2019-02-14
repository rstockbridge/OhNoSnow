package com.github.rstockbridge.ohnosnow.api.models;

import android.support.annotation.NonNull;

import com.squareup.moshi.Json;

import java.util.List;

class HourlyForecast {

    @Json(name = "data")
    @NonNull
    private List<Forecast> forecasts;

    @NonNull
    double get12HourSnowInInches() {
        double snowInInches = 0;

        for (int i = 0; i < 12; i++) {
            snowInInches += forecasts.get(i).getSnowAccumulation();
        }

        return (double) Math.round(snowInInches * 10) / 10;
    }
}
