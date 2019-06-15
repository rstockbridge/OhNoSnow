package com.github.rstockbridge.ohnosnow.api;

import androidx.annotation.NonNull;

public interface MyCallback<T> {

    void onSuccess(@NonNull final T result);

    void onError();

    void onFailure(@NonNull final Throwable t);
}
