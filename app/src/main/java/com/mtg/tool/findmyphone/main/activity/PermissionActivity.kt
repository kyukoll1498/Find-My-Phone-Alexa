package com.mtg.tool.findmyphone.main.activity


import android.Manifest
import android.content.Intent
import com.common.control.manager.AdmobManager
import com.common.control.manager.AppOpenManager
import com.mtg.tool.findmyphone.BuildConfig
import com.mtg.tool.findmyphone.R
import com.mtg.tool.findmyphone.REQUEST_MICRO_PERMISSION_CODE
import com.mtg.tool.findmyphone.base.BaseActivity
import com.mtg.tool.findmyphone.databinding.ActivityPermissionBinding
import com.mtg.tool.findmyphone.utils.PermissionUtils

class PermissionActivity : BaseActivity<ActivityPermissionBinding>(ActivityPermissionBinding::inflate) {

    override fun initView() {

        // Do not allow user interaction sbPermission
        binding.sbPermission.isEnabled = false
        binding.sbPermission.isClickable = false

//        updateLLContinueBackground()

        AdmobManager.getInstance().loadNative(
            this,
            BuildConfig.native_permission,
            binding.frAd,
            R.layout.custom_native_language
        )
        AppOpenManager.getInstance().hideNativeOrBannerWhenShowOpenApp(this, binding.frAd)

        binding.llContinue.setOnClickListener {
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
        }
    }

    override fun addEvent() {
        binding.llCreateSound.setOnClickListener {
            checkPermissionMicro()
        }
    }

    private fun checkPermissionMicro() {
        if (!PermissionUtils.checkMicroPermission(mContext) && !shouldShowRequestPermissionRationale(Manifest.permission.RECORD_AUDIO)) {
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
//                hasMicrophonePermission = true

                // Change color button ll_continue
//                updateLLContinueBackground()
            }
        }
    }
}