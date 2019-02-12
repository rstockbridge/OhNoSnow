package com.github.rstockbridge.ohnosnow.api;

import android.support.annotation.NonNull;

public interface MyCallback<T> {

    void onSuccess(@NonNull T result);

    void onError();

    void onFailure();
}
