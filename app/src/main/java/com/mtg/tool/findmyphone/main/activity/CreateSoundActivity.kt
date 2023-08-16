package com.mtg.tool.findmyphone.main.activity

import android.annotation.SuppressLint
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import com.mtg.tool.findmyphone.ACTION_FINISH_CREATE_SOUND_SCREEN
import com.mtg.tool.findmyphone.KEY_SOUND
import com.mtg.tool.findmyphone.REQUEST_MICRO_PERMISSION_CODE
import com.mtg.tool.findmyphone.base.BaseActivity
import com.mtg.tool.findmyphone.data.model.SoundItem
import com.mtg.tool.findmyphone.databinding.ActivityCreateSoundBinding
import com.mtg.tool.findmyphone.main.dialog.RecordPermissionDialog
import com.mtg.tool.findmyphone.utils.PermissionUtils

class CreateSoundActivity :
    BaseActivity<ActivityCreateSoundBinding>(ActivityCreateSoundBinding::inflate) {

    private val finishReceiver: BroadcastReceiver = object : BroadcastReceiver() {
        @SuppressLint("NotifyDataSetChanged")
        override fun onReceive(context: Context, intent: Intent) {
            finish()
        }
    }


    override fun initView() {
        registerReceiver(finishReceiver, IntentFilter(ACTION_FINISH_CREATE_SOUND_SCREEN))
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

    override fun onDestroy() {
        super.onDestroy()
        unregisterReceiver(finishReceiver)
    }
}