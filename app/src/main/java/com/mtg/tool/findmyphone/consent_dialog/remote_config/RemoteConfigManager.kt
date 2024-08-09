package com.mtg.tool.findmyphone.consent_dialog.remote_config

import android.app.Activity
import com.google.firebase.remoteconfig.FirebaseRemoteConfig
import com.google.firebase.remoteconfig.FirebaseRemoteConfigSettings
import com.mtg.tool.findmyphone.R

class RemoteConfigManager {
    private var remoteConfig: FirebaseRemoteConfig? = null
    private var isLoading = false
    var isShowNativeFullScreenOnboard: Boolean = true

   var _101_splash_n_banner_high: Boolean = true
   var _101_splash_n_banner: Boolean = true
   var _102_splash_n_inter_high: Boolean = true
   var _102_splash_n_inter: Boolean = true
   var _201_lfo_n_native_high: Boolean = true
   var _201_lfo_n_native: Boolean = true
   var _202_lfo_n_native_high: Boolean = true
   var _202_lfo_n_native_high_1: Boolean = true
   var _202_lfo_n_native_high_2: Boolean = true
   var _202_lfo_n_native: Boolean = true
   var _301_ob1_n_native_high: Boolean = true
   var _301_ob1_n_native: Boolean = true
   var _302_ob2_n_native_high: Boolean = true
   var _302_ob2_n_native: Boolean = true
   var _304_ob4_n_native_high: Boolean = true
   var _304_ob4_n_native_high_1: Boolean = true
   var _304_ob4_n_native_high_2: Boolean = true
   var _304_ob4_n_native: Boolean = true
   var _305_ob5_n_native_high: Boolean = true
   var _305_ob5_n_native: Boolean = true
   var _306_ob6_n_native_high: Boolean = true
   var _306_ob6_n_native: Boolean = true
   var _401_app_o_reopen: Boolean = true

    fun loadRemote() {
        if (isLoading) {
            return
        }
        isLoading = true
        val config = FirebaseRemoteConfig.getInstance()
        val configSettings =
            FirebaseRemoteConfigSettings.Builder().setMinimumFetchIntervalInSeconds(0).build()
        config.setConfigSettingsAsync(configSettings)
        config.setDefaultsAsync(R.xml.default_config)
        config.fetchAndActivate().addOnCompleteListener { task ->
            isLoading = false
            if (task.isSuccessful) {
                remoteConfig = FirebaseRemoteConfig.getInstance()
                _101_splash_n_banner_high = config.getBoolean("101_splash_n_banner_high")
                _101_splash_n_banner = config.getBoolean("101_splash_n_banner")
                _102_splash_n_inter_high = config.getBoolean("102_splash_n_inter_high")
                _102_splash_n_inter = config.getBoolean("102_splash_n_inter")
                _201_lfo_n_native_high = config.getBoolean("201_lfo_n_native_high")
                _201_lfo_n_native = config.getBoolean("201_lfo_n_native")
                _202_lfo_n_native_high = config.getBoolean("202_lfo_n_native_high")
                _202_lfo_n_native_high_1 = config.getBoolean("202_lfo_n_native_high_1")
                _202_lfo_n_native_high_2 = config.getBoolean("202_lfo_n_native_high_2")
                _202_lfo_n_native = config.getBoolean("202_lfo_n_native")
                _301_ob1_n_native_high = config.getBoolean("301_ob1_n_native_high")
                _301_ob1_n_native = config.getBoolean("301_ob1_n_native")
                _302_ob2_n_native_high = config.getBoolean("302_ob2_n_native_high")
                _302_ob2_n_native = config.getBoolean("302_ob2_n_native")
                _304_ob4_n_native_high = config.getBoolean("304_ob4_n_native_high")
                _304_ob4_n_native_high_1 = config.getBoolean("304_ob4_n_native_high_1")
                _304_ob4_n_native_high_2 = config.getBoolean("304_ob4_n_native_high_2")
                _304_ob4_n_native = config.getBoolean("304_ob4_n_native")
                _305_ob5_n_native_high = config.getBoolean("305_ob5_n_native_high")
                _305_ob5_n_native = config.getBoolean("305_ob5_n_native")
                _306_ob6_n_native_high = config.getBoolean("306_ob6_n_native_high")
                _306_ob6_n_native = config.getBoolean("306_ob6_n_native")
                _401_app_o_reopen = config.getBoolean("401_app_o_reopen")

                isShowNativeFullScreenOnboard = _304_ob4_n_native_high || _304_ob4_n_native_high_1 || _304_ob4_n_native_high_2 || _304_ob4_n_native
            } else {
                loadRemote()
            }
        }
    }

    fun loadIsShowConsent(activity: Activity, callback: BooleanCallback) {
        if (isLoading && remoteConfig == null) {
            Thread {
                while (isLoading || remoteConfig == null) {
                    try {
                        Thread.sleep(100)
                    } catch (e: InterruptedException) {
                        e.printStackTrace()
                    }
                }
                activity.runOnUiThread {
                    if (remoteConfig != null) {
                        callback.onResult(remoteConfig!!.getBoolean(IS_SHOW_CONSENT))
                    }
                }
            }.start()
        } else {
            if (remoteConfig != null) {
                callback.onResult(remoteConfig!!.getBoolean(IS_SHOW_CONSENT))
            }
        }
    }

    fun loadLimitFunctionInAppCount(activity: Activity, callback: NumberCallback) {
        if (isLoading && remoteConfig == null) {
            Thread {
                while (isLoading && remoteConfig == null) {
                    try {
                        Thread.sleep(100)
                    } catch (e: InterruptedException) {
                        e.printStackTrace()
                    }
                }
                activity.runOnUiThread {
                    callback.onResult(
                        remoteConfig!!.getLong(
                            LIMIT_FUNCTION_IN_APP
                        )
                    )
                }
            }.start()
        } else {
            callback.onResult(remoteConfig!!.getLong(LIMIT_FUNCTION_IN_APP))
        }
    }

    fun loadReshowGDPRSplashCount(activity: Activity, callback: NumberCallback) {
        if (isLoading && remoteConfig == null) {
            Thread {
                while (isLoading && remoteConfig == null) {
                    try {
                        Thread.sleep(100)
                    } catch (e: InterruptedException) {
                        e.printStackTrace()
                    }
                }
                activity.runOnUiThread { callback.onResult(remoteConfig!!.getLong(RESHOW_GDPR_SPLASH)) }
            }.start()
        } else {
            callback.onResult(remoteConfig!!.getLong(RESHOW_GDPR_SPLASH))
        }
    }

    val isShowConsent: Boolean
        get() = remoteConfig != null && remoteConfig!!.getBoolean(IS_SHOW_CONSENT)

    fun limitFunctionClickCount(): Long {
        return if (remoteConfig == null) {
            0
        } else {
            remoteConfig!!.getLong(LIMIT_FUNCTION_IN_APP)
        }
    }

    interface BooleanCallback {
        fun onResult(value: Boolean)
    }

    interface NumberCallback {
        fun onResult(value: Long)
    }

    interface StringCallback {
        fun onResult(value: String?)
    }

    companion object {
        private const val IS_SHOW_CONSENT = "is_show_consent"
        private const val LIMIT_FUNCTION_IN_APP = "limit_function_in_app"
        private const val RESHOW_GDPR_SPLASH = "reshow_gdpr_splash"
        private var INSTANCE: RemoteConfigManager? = null

        @JvmStatic
        val instance: RemoteConfigManager?
            get() {
                if (INSTANCE == null) {
                    INSTANCE = RemoteConfigManager()
                }
                return INSTANCE
            }
    }
}