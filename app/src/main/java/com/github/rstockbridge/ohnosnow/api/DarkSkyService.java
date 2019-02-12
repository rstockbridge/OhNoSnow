package com.github.rstockbridge.ohnosnow.api;

import com.github.rstockbridge.ohnosnow.api.models.ForecastData;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;

public interface DarkSkyService {
    @GET("{apiKey}/{latitude},{longitude}?&exclude=currently,minutely,daily,alerts,flags")
    Call<ForecastData> getForecast(
            @Path("apiKey") final String apiKey,
            @Path("latitude") final String latitude,
            @Path("longitude") final String longitude);
}
