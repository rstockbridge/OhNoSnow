package com.github.rstockbridge.ohnosnow.api;

import com.github.rstockbridge.ohnosnow.api.models.Forecast;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface ClimaCellService {
    @GET("v3/weather/forecast/hourly?unit_system=us&start_time=now&fields=precipitation,precipitation_type")
    Call<List<Forecast>> getForecasts(
            @Query("apikey") final String apiKey,
            @Query("lat") final String latitude,
            @Query("lon") final String longitude);
}
