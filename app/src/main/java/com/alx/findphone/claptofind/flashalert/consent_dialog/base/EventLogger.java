package com.alx.findphone.claptofind.flashalert.consent_dialog.base;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;

import com.google.firebase.analytics.FirebaseAnalytics;

public class EventLogger {
    public static void firebaseLog(Context context, String value) {
        try {
            Log.d("android_log", "logEvent: " + value);
            Bundle bundle = new Bundle();
            bundle.putString("EVENT", value);
            FirebaseAnalytics mFirebaseAnalytics = FirebaseAnalytics.getInstance(context);
            mFirebaseAnalytics.logEvent(value, bundle);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}