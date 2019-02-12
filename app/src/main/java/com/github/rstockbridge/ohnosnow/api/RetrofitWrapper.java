package com.github.rstockbridge.ohnosnow.api;

import android.support.annotation.NonNull;

import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.converter.moshi.MoshiConverterFactory;

final class RetrofitWrapper {

    private static Retrofit retrofit;
    private static final String BASE_URL = "https://api.darksky.net/forecast/";

    private RetrofitWrapper() {
    }

    @NonNull
    static Retrofit getRetrofitInstance() {
        if (retrofit == null) {
            retrofit = new retrofit2.Retrofit.Builder()
                    .baseUrl(BASE_URL)
                    .client(getClient())
                    .addConverterFactory(MoshiConverterFactory.create())
                    .build();
        }

        return retrofit;
    }

    @NonNull
    private static OkHttpClient getClient() {
        final HttpLoggingInterceptor interceptor = new HttpLoggingInterceptor();
        interceptor.setLevel(HttpLoggingInterceptor.Level.BODY);
        return new OkHttpClient.Builder().addInterceptor(interceptor).build();
    }
}
