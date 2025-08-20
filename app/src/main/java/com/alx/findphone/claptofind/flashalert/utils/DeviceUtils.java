package com.alx.findphone.claptofind.flashalert.utils;

import android.content.Context;
import android.content.res.Configuration;

public class DeviceUtils {
    public static boolean checkIfSmallScreen(Context context) {
        Configuration configuration = context.getResources().getConfiguration();
        int heightDB = configuration.screenHeightDp;
        if (heightDB < 720) {
            return true;
        } else {
            return false;
        }
    }
}
