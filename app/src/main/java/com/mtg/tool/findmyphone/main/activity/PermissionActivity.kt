package com.mtg.tool.findmyphone.main.activity


import android.content.Intent
import android.content.res.ColorStateList
import android.util.Log
import android.widget.Toast
import androidx.core.content.ContextCompat
import com.common.control.manager.AdmobManager
import com.common.control.manager.AppOpenManager
import com.mtg.tool.findmyphone.BuildConfig
import com.mtg.tool.findmyphone.R
import com.mtg.tool.findmyphone.REQUEST_MICRO_PERMISSION_CODE
import com.mtg.tool.findmyphone.base.BaseActivity
import com.mtg.tool.findmyphone.databinding.ActivityPermissionBinding
import com.mtg.tool.findmyphone.main.dialog.RecordPermissionDialog
import com.mtg.tool.findmyphone.utils.PermissionUtils

class PermissionActivity : BaseActivity<ActivityPermissionBinding>(ActivityPermissionBinding::inflate) {

//    private var hasMicrophonePermission = false

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
//            if (PermissionUtils.checkMicroPermission(mContext)) {
                val intent = Intent(this, MainActivity::class.java)
                startActivity(intent)
//            } else {
//                Toast.makeText(this, getString(R.string.toast_permission_continue), Toast.LENGTH_SHORT).show()
//            }
        }

    }

    override fun addEvent() {
        binding.llCreateSound.setOnClickListener {
            checkPermissionMicro()
        }
    }

    private fun checkPermissionMicro() {
        if (!PermissionUtils.checkMicroPermission(mContext)) {
            PermissionUtils.requestMicroPermission(this)
        } else {
            Log.d("Error Permission Micro", "Check Permission")
//            hasMicrophonePermission = true
//            updateLLContinueBackground()
        }
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
                binding.sbPermission.isEnabled = true
                binding.sbPermission.isClickable = true
                binding.sbPermission.isChecked = true
                binding.sbPermission.isEnabled = false
//                hasMicrophonePermission = true

                // Change color button ll_continue
//                updateLLContinueBackground()
            }
        }
    }

//    private fun showRecordPermissionDialog() {
//        RecordPermissionDialog(this) { granted ->
//            if (granted) {
//                hasMicrophonePermission = true
//                updateLLContinueBackground()
//
//                binding.sbPermission.isEnabled = true
//                binding.sbPermission.isClickable = true
//                binding.sbPermission.isChecked = true
//                binding.sbPermission.isEnabled = false
//
//                PermissionUtils.goSettingsForMicroPermission(this)
//            }
//        }.show()
//    }

//    private fun updateLLContinueBackground() {
//        if (hasMicrophonePermission) {
//            binding.llContinue.backgroundTintList = ColorStateList.valueOf(
//                ContextCompat.getColor(this, R.color.txt_inactive2)
//            )
//        } else {
//            binding.llContinue.backgroundTintList = ColorStateList.valueOf(
//                ContextCompat.getColor(this, R.color.gray_continue)
//            )
//        }
//    }
}