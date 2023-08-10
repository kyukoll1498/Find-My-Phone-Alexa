package com.common.control.interfaces;

import android.content.Context;

import androidx.annotation.Nullable;

public interface PermissionCallback {
    void onPermissionGranted(@Nullable Context context);

    void onPermissionDenied();

    void onPressDenied();

    void onPressGrant();
}
