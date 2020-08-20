package com.github.rstockbridge.ohnosnow.api;

import androidx.annotation.NonNull;

import com.github.rstockbridge.ohnosnow.api.models.Forecast;

import org.jetbrains.annotations.NotNull;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public final class ClimaCellApi {

    private static ClimaCellApi sharedInstance;

    @NonNull
    public static ClimaCellApi getSharedInstance() {
        if (sharedInstance == null) {
            sharedInstance = new ClimaCellApi();
        }

        return sharedInstance;
    }

    private final ClimaCellService service;

    private ClimaCellApi() {
        service = RetrofitWrapper.getRetrofitInstance().create(ClimaCellService.class);
    }

    public void fetchForecasts(
            @NonNull final String apiKey,
            @NonNull final String latitude,
            @NonNull final String longitude,
            @NonNull final MyCallback<List<Forecast>> myCallback) {

        final Call<List<Forecast>> call = service.getForecasts(apiKey, latitude, longitude);

        call.enqueue(new Callback<List<Forecast>>() {
            @Override
            public void onResponse(@NotNull final Call<List<Forecast>> call, @NotNull final Response<List<Forecast>> response) {
                if (response.isSuccessful()) {
                    myCallback.onSuccess(response.body());
                } else {
                    myCallback.onError();
                }
            }

            @Override
            public void onFailure(@NotNull final Call<List<Forecast>> call, final Throwable t) {
                myCallback.onFailure(t);
            }
        });
    }
}
