package com.mtg.tool.findmyphone.main.activity


import android.Manifest
import android.content.Intent
import android.graphics.Paint
import com.common.control.interfaces.AdCallback
import com.common.control.manager.AdmobManager
import com.common.control.manager.AppOpenManager
import com.google.android.gms.ads.nativead.NativeAd
import com.mtg.tool.findmyphone.AdCache
import com.mtg.tool.findmyphone.AdIds
import com.mtg.tool.findmyphone.R
import com.mtg.tool.findmyphone.REQUEST_MICRO_PERMISSION_CODE
import com.mtg.tool.findmyphone.base.BaseActivity
import com.mtg.tool.findmyphone.data.preferences.SharedPrefs
import com.mtg.tool.findmyphone.databinding.ActivityPermissionBinding
import com.mtg.tool.findmyphone.main.fragment.HomeFragment
import com.mtg.tool.findmyphone.utils.PermissionUtils
import com.mtg.tool.findmyphone.utils.constant.Constants

class PermissionActivity :
    BaseActivity<ActivityPermissionBinding>(ActivityPermissionBinding::inflate) {

    private var isFirstResume = true
    private val onb6NativeAds = arrayListOf(AdIds.ob6_native_high, AdIds.ob6_native)

    override fun initView() {
        binding.llContinue.paintFlags = Paint.UNDERLINE_TEXT_FLAG or Paint.ANTI_ALIAS_FLAG
        binding.llContinue.setText(R.string.skip)
        SharedPrefs.put(this, Constants.SKIP_ONBOARD, true)
        logEvent("complete_ob")
        logEvent("permission_view")
        showNative();
        // Do not allow user interaction sbPermission
        binding.sbPermission.isEnabled = false
        binding.sbPermission.isClickable = false

//        updateLLContinueBackground()

        binding.llContinue.setOnClickListener {
            logEvent("permission_skip_click")
            val intent = Intent(this, HomeFragment::class.java)
            startActivity(intent)
            finish()
        }
    }

    override fun addEvent() {
        binding.llCreateSound.setOnClickListener {
            logEvent("permission_accept_click")
            checkPermissionMicro()
        }
    }

    private fun checkPermissionMicro() {
        if (!PermissionUtils.checkMicroPermission(mContext) && !shouldShowRequestPermissionRationale(
                Manifest.permission.RECORD_AUDIO
            )
        ) {
            PermissionUtils.requestMicroPermission(this)
        } else {
            PermissionUtils.goSettingsForMicroPermission(this)
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (PermissionUtils.checkMicroPermission(mContext)) {
            turnOnSwitchButton()
        }
    }

    private fun turnOnSwitchButton() {
        binding.sbPermission.isEnabled = true
        binding.sbPermission.isClickable = true
        binding.sbPermission.isChecked = true
        binding.sbPermission.isEnabled = false
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == REQUEST_MICRO_PERMISSION_CODE) {
            if (!PermissionUtils.checkMicroPermission(this)) {
//                showRecordPermissionDialog()
                return
            } else {
                // Auto checked sbPermission when allow permission record micro
                turnOnSwitchButton()
                HomeFragment.start(this)
                finish()
//                hasMicrophonePermission = true

                // Change color button ll_continue
//                updateLLContinueBackground()
            }
        }
    }

    private fun showNative() {
        AdCache.getInstance().ob6NativeHigh.observe(
            this
        ) { value ->
            AdmobManager.getInstance().showNative(
                this@PermissionActivity,
                value,
                binding.frAd,
                AdmobManager.NativeAdType.BIG
            )
        }
        AppOpenManager.getInstance().hideNativeOrBannerWhenShowOpenApp(this, binding.frAd)
    }

    override fun onResume() {
        super.onResume()
        if (isFirstResume) {
            isFirstResume = false
        } else {
            AdmobManager.getInstance().preloadAlternateNative(this, onb6NativeAds, object : AdCallback() {
                override fun onNativeAds(nativeAd: NativeAd?) {
                    super.onNativeAds(nativeAd)
                    AdmobManager.getInstance().showNative(this@PermissionActivity, nativeAd, binding.frAd, AdmobManager.NativeAdType.BIG)
                }

                override fun onAdImpression() {
                    super.onAdImpression()
                    logEvent("onboard6_native_view")
                }

                override fun onAdClicked() {
                    super.onAdClicked()
                    logEvent("onboard6_native_click")

                }
            })
        }
    }

}