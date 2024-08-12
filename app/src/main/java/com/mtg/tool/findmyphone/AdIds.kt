package com.mtg.tool.findmyphone

import com.mtg.tool.findmyphone.consent_dialog.remote_config.RemoteConfigManager

object AdIds {
    fun updateIdAdsWithRemoteConfig() {
        banner_splash_high =
            if (RemoteConfigManager.instance!!._101_splash_n_banner_high) BuildConfig.banner_splash_high else ""
        banner_splash =
            if (RemoteConfigManager.instance!!._101_splash_n_banner) BuildConfig.banner_splash else ""
        inter_splash_high =
            if (RemoteConfigManager.instance!!._102_splash_n_inter_high) BuildConfig.inter_splash_high else ""
        inter_splash =
            if (RemoteConfigManager.instance!!._102_splash_n_inter) BuildConfig.inter_splash else ""
        lfo1_native_high =
            if (RemoteConfigManager.instance!!._201_lfo_n_native_high) BuildConfig.lfo1_native_high else ""
        lfo1_native =
            if (RemoteConfigManager.instance!!._201_lfo_n_native) BuildConfig.lfo1_native else ""
        lfo2_native_high =
            if (RemoteConfigManager.instance!!._202_lfo_n_native_high) BuildConfig.lfo2_native_high else ""
        lfo2_native_high1 =
            if (RemoteConfigManager.instance!!._202_lfo_n_native_high_1) BuildConfig.lfo2_native_high1 else ""
        lfo2_native_high2 =
            if (RemoteConfigManager.instance!!._202_lfo_n_native_high_2) BuildConfig.lfo2_native_high2 else ""
        lfo2_native =
            if (RemoteConfigManager.instance!!._202_lfo_n_native) BuildConfig.lfo2_native else ""
        ob1_native_high =
            if (RemoteConfigManager.instance!!._301_ob1_n_native_high) BuildConfig.ob1_native_high else ""
        ob1_native =
            if (RemoteConfigManager.instance!!._301_ob1_n_native) BuildConfig.ob1_native else ""
        ob2_native_high =
            if (RemoteConfigManager.instance!!._302_ob2_n_native_high) BuildConfig.ob2_native_high else ""
        ob2_native =
            if (RemoteConfigManager.instance!!._302_ob2_n_native) BuildConfig.ob2_native else ""
        ob4_native_high =
            if (RemoteConfigManager.instance!!._304_ob4_n_native_high) BuildConfig.ob4_native_high else ""
        ob4_native_high1 =
            if (RemoteConfigManager.instance!!._304_ob4_n_native_high_1) BuildConfig.ob4_native_high1 else ""
        ob4_native_high2 =
            if (RemoteConfigManager.instance!!._304_ob4_n_native_high_2) BuildConfig.ob4_native_high2 else ""
        ob4_native =
            if (RemoteConfigManager.instance!!._304_ob4_n_native) BuildConfig.ob4_native else ""
        ob5_native_high =
            if (RemoteConfigManager.instance!!._305_ob5_n_native_high) BuildConfig.ob5_native_high else ""
        ob5_native =
            if (RemoteConfigManager.instance!!._305_ob5_n_native) BuildConfig.ob5_native else ""
        ob6_native_high =
            if (RemoteConfigManager.instance!!._306_ob6_n_native_high) BuildConfig.ob6_native_high else ""
        ob6_native =
            if (RemoteConfigManager.instance!!._306_ob6_n_native) BuildConfig.ob6_native else ""
        app_reopen =
            if (RemoteConfigManager.instance!!._401_app_o_reopen) BuildConfig.app_reopen else ""


        native_home =
            if (RemoteConfigManager.instance!!._501_home_o_native) BuildConfig.native_home else ""
        banner_home =
            if (RemoteConfigManager.instance!!._502_home_o_banner) BuildConfig.banner_home else ""
        banner_sound =
            if (RemoteConfigManager.instance!!._601_sound_o_banner) BuildConfig.banner_sound else ""
        native_sound =
            if (RemoteConfigManager.instance!!._601_sound_o_native) BuildConfig.native_sound else ""
        native_effect =
            if (RemoteConfigManager.instance!!._602_sound_o_native) BuildConfig.native_effect else ""
        banner_add =
            if (RemoteConfigManager.instance!!._701_add_o_banner) BuildConfig.banner_add else ""
        native_create =
            if (RemoteConfigManager.instance!!._702_create_o_native) BuildConfig.native_create else ""
        native_import =
            if (RemoteConfigManager.instance!!._703_import_o_native) BuildConfig.native_import else ""
        native_record =
            if (RemoteConfigManager.instance!!._704_record_o_native) BuildConfig.native_record else ""
        native_record_save =
            if (RemoteConfigManager.instance!!._704_record_o_native) BuildConfig.native_record_save else ""
        banner_setting =
            if (RemoteConfigManager.instance!!._801setting_o_banner) BuildConfig.banner_setting else ""
        native_tutorial =
            if (RemoteConfigManager.instance!!._901_tutorial_o_banner) BuildConfig.native_tutorial else ""


    }

    var app_reopen = BuildConfig.app_reopen
    var banner_splash_high = BuildConfig.banner_splash_high
    var banner_splash = BuildConfig.banner_splash
    var inter_splash_high = BuildConfig.inter_splash_high
    var inter_splash = BuildConfig.inter_splash
    var lfo1_native_high = BuildConfig.lfo1_native_high
    var lfo1_native = BuildConfig.lfo1_native
    var lfo2_native_high = BuildConfig.lfo2_native_high
    var lfo2_native_high1 = BuildConfig.lfo2_native_high1
    var lfo2_native_high2 = BuildConfig.lfo2_native_high2
    var lfo2_native = BuildConfig.lfo2_native
    var ob1_native_high = BuildConfig.ob1_native_high
    var ob1_native = BuildConfig.ob1_native
    var ob2_native_high = BuildConfig.ob2_native_high
    var ob2_native = BuildConfig.ob2_native
    var ob4_native_high = BuildConfig.ob4_native_high
    var ob4_native_high1 = BuildConfig.ob4_native_high1
    var ob4_native_high2 = BuildConfig.ob4_native_high2
    var ob4_native = BuildConfig.ob4_native
    var ob5_native_high = BuildConfig.ob5_native_high
    var ob5_native = BuildConfig.ob5_native
    var ob6_native_high = BuildConfig.ob6_native_high
    var ob6_native = BuildConfig.ob6_native

    var native_home = BuildConfig.native_home
    var banner_home = BuildConfig.banner_home
    var banner_sound = BuildConfig.banner_sound
    var native_sound = BuildConfig.native_sound
    var native_effect = BuildConfig.native_effect
    var banner_add = BuildConfig.banner_add
    var native_create = BuildConfig.native_create
    var native_import = BuildConfig.native_import
    var native_record = BuildConfig.native_record
    var native_record_save = BuildConfig.native_record_save
    var banner_setting = BuildConfig.banner_setting
    var native_tutorial = BuildConfig.native_tutorial
}