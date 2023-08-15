package com.mtg.tool.findmyphone.main.activity

import android.content.Intent
import com.mtg.tool.findmyphone.REQUEST_MICRO_PERMISSION_CODE
import com.mtg.tool.findmyphone.base.BaseActivity
import com.mtg.tool.findmyphone.databinding.ActivityCreateSoundBinding
import com.mtg.tool.findmyphone.main.dialog.RecordPermissionDialog
import com.mtg.tool.findmyphone.utils.PermissionUtils

class CreateSoundActivity :
    BaseActivity<ActivityCreateSoundBinding>(ActivityCreateSoundBinding::inflate) {
    override fun initView() {

    }

    override fun addEvent() {
        binding.llRecordAudio.setOnClickListener {
            if (!PermissionUtils.checkMicroPermission(this)) {
                PermissionUtils.requestMicroPermission(this)
            } else {
                startRecordAudio()
            }

        }
    }

    private fun startRecordAudio() {
        startActivity(Intent(this, RecordAudioActivity::class.java))
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == REQUEST_MICRO_PERMISSION_CODE) {
            if (!PermissionUtils.checkMicroPermission(this)) {
                showRecordPermissionDialog()
            } else {
                startRecordAudio()
            }
        }
    }

    private fun showRecordPermissionDialog() {
        RecordPermissionDialog(this) {
            if (it) {
                PermissionUtils.goSettingsForMicroPermission(this)
            }
        }.show()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == REQUEST_MICRO_PERMISSION_CODE) {
            if (!PermissionUtils.checkMicroPermission(this)) {
                showRecordPermissionDialog()
            } else {
                startRecordAudio()
            }
        }
    }
}