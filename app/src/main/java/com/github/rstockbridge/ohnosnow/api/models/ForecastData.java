package com.github.rstockbridge.ohnosnow.api.models;

import android.support.annotation.NonNull;

import com.squareup.moshi.Json;

public class ForecastData {

    @Json(name = "hourly")
    @NonNull
    private HourlyForecast hourlyForecast;

    public double get12HourSnowInInches() {
        return hourlyForecast.get12HourSnowInInches();
    }
}
