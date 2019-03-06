package com.github.rstockbridge.ohnosnow.api;

import android.support.annotation.NonNull;

import com.github.rstockbridge.ohnosnow.api.models.ForecastData;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public final class DarkSkyApi {

    private static DarkSkyApi sharedInstance;

    @NonNull
    public static DarkSkyApi getSharedInstance() {
        if (sharedInstance == null) {
            sharedInstance = new DarkSkyApi();
        }

        return sharedInstance;
    }

    private final DarkSkyService service;

    private DarkSkyApi() {
        service = RetrofitWrapper.getRetrofitInstance().create(DarkSkyService.class);
    }

    public void fetchForecast(
            @NonNull final String apiKey,
            @NonNull final String latitude,
            @NonNull final String longitude,
            @NonNull final MyCallback<ForecastData> myCallback) {

        final Call<ForecastData> call = service.getForecast(apiKey, latitude, longitude);

        call.enqueue(new Callback<ForecastData>() {
            @Override
            public void onResponse(final Call<ForecastData> call, final Response<ForecastData> response) {
                if (response.isSuccessful()) {
                    myCallback.onSuccess(response.body());
                } else {
                    myCallback.onError();
                }
            }

            @Override
            public void onFailure(final Call<ForecastData> call, final Throwable t) {
                myCallback.onFailure(t);
            }
        });
    }
}
