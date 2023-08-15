package com.mtg.tool.findmyphone.main.activity

import android.content.Intent
import com.mtg.tool.findmyphone.REQUEST_MICRO_PERMISSION_CODE
import com.mtg.tool.findmyphone.base.BaseActivity
import com.mtg.tool.findmyphone.databinding.ActivityCreateSoundBinding
import com.mtg.tool.findmyphone.databinding.ActivityRecordSoundBinding
import com.mtg.tool.findmyphone.main.dialog.RecordPermissionDialog
import com.mtg.tool.findmyphone.utils.PermissionUtils

class RecordAudioActivity :
    BaseActivity<ActivityRecordSoundBinding>(ActivityRecordSoundBinding::inflate) {
    override fun initView() {

    }

    override fun addEvent() {
       binding.btnBack.setOnClickListener { finish() }
    }

    override fun onResume() {
        super.onResume()
        if (!PermissionUtils.checkMicroPermission(this)) {
             showRecordPermissionDialog()
        }
    }

    private fun showRecordPermissionDialog() {
        RecordPermissionDialog(this) {
            if (it) {
                PermissionUtils.goSettingsForMicroPermission(this)
            } else {
                finish()
            }
        }.setDialogCancellable(false).show()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == REQUEST_MICRO_PERMISSION_CODE) {
            if (!PermissionUtils.checkMicroPermission(this)) {
                showRecordPermissionDialog()
            }
        }
    }
}