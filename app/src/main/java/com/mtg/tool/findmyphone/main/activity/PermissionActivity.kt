package com.mtg.tool.findmyphone.main.activity


import android.app.Activity
import android.util.Log
import com.common.control.manager.AdmobManager
import com.common.control.manager.AppOpenManager
import com.mtg.tool.findmyphone.BuildConfig
import com.mtg.tool.findmyphone.R
import com.mtg.tool.findmyphone.base.BaseActivity
import com.mtg.tool.findmyphone.databinding.ActivityPermissionBinding
import com.mtg.tool.findmyphone.utils.PermissionUtils

class PermissionActivity : BaseActivity<ActivityPermissionBinding>(ActivityPermissionBinding::inflate) {

    override fun initView() {

        AdmobManager.getInstance().loadNative(
            this,
            BuildConfig.native_guide,
            binding.frAd,
            R.layout.custom_native_onboarding
        )
        AppOpenManager.getInstance().hideNativeOrBannerWhenShowOpenApp(this, binding.frAd)


    }

    override fun addEvent() {
        binding.llCreateSound.setOnClickListener {
            checkPermissionMicro()
            Log.d("ngu", "ngu")
        }
    }

    private fun checkPermissionMicro() {
        if (!PermissionUtils.checkMicroPermission(mContext)) {
            PermissionUtils.requestMicroPermission(this)
        } else {
            Log.d("Error Permission Micro", "Check Permission")
        }
    }
}