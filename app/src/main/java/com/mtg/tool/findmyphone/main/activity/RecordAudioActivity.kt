package com.mtg.tool.findmyphone.main.activity

import android.content.Intent
import android.os.CountDownTimer
import android.view.View
import com.mtg.tool.findmyphone.IMPORT_SOUND_TYPE
import com.mtg.tool.findmyphone.R
import com.mtg.tool.findmyphone.REQUEST_MICRO_PERMISSION_CODE
import com.mtg.tool.findmyphone.base.BaseActivity
import com.mtg.tool.findmyphone.data.model.SoundItem
import com.mtg.tool.findmyphone.data.repo.AppRepository
import com.mtg.tool.findmyphone.databinding.ActivityRecordSoundBinding
import com.mtg.tool.findmyphone.main.dialog.RecordPermissionDialog
import com.mtg.tool.findmyphone.utils.AudioUtil
import com.mtg.tool.findmyphone.utils.CacheUtils
import com.mtg.tool.findmyphone.utils.PermissionUtils
import com.mtg.tool.findmyphone.utils.RecordUtil

class RecordAudioActivity :
    BaseActivity<ActivityRecordSoundBinding>(ActivityRecordSoundBinding::inflate) {
    companion object {
        const val MODE_PREPARE_START = 432
        const val MODE_PREPARE_PAUSE = 433
        const val MODE_PREPARE_RESUME = 434
    }

    private var mode = MODE_PREPARE_START
    private var currentTime = 0
    private var timer: CountDownTimer? = null
    override fun initView() {

    }

    override fun addEvent() {
        binding.btnBack.setOnClickListener { finish() }
        binding.ivRecordController.setOnClickListener {
            when (mode) {
                MODE_PREPARE_START -> {
                    startRecord()
                }

                MODE_PREPARE_PAUSE -> {
                    pauseRecord()
                }

                MODE_PREPARE_RESUME -> {
                    resumeRecord()
                }
            }
        }
        binding.ivRestartRecord.setOnClickListener {
            restartRecord()
        }
        binding.tvNext.setOnClickListener { stopRecord() }
    }

    private fun stopRecord() {
        RecordUtil.stopRecording()
        cutAudio15s()
        mode = MODE_PREPARE_START
        hideToolsMoreRecord()
        restartTimer()
        binding.ivRecordController.setImageDrawable(getDrawable(R.drawable.ic_micro_start))
    }

    private fun cutAudio15s() {
        if (currentTime >= 15) {
            AudioUtil.cutAudio(this, 0, 15, CacheUtils.getLastFilePathAudio()) { }
        }
    }

    private fun restartRecord() {
        RecordUtil.stopRecording()
        mode = MODE_PREPARE_START
        CacheUtils.removeLastFileAudio()
        hideToolsMoreRecord()
        restartTimer()
        binding.ivRecordController.setImageDrawable(getDrawable(R.drawable.ic_micro_start))
    }

    private fun restartTimer() {
        timer?.run { cancel() }
        binding.tvTime.text = "00:00"
    }

    private fun resumeRecord() {
        RecordUtil.resumeRecording()
        mode = MODE_PREPARE_PAUSE
        hideToolsMoreRecord()
        resumeTimer()
        binding.ivRecordController.setImageDrawable(getDrawable(R.drawable.iv_pause_record))

    }

    private fun resumeTimer() {
        timer = object : CountDownTimer(1000000000, 1000) {
            override fun onTick(millisUntilFinished: Long) {
                currentTime++
                binding.tvTime.text = String.format(
                    "%02d:%02d",
                    (currentTime % 3600 / 60).toLong(),
                    (currentTime % 60).toLong()
                )
            }

            override fun onFinish() {
            }
        }
        timer?.run { start() }
    }

    private fun pauseRecord() {
        RecordUtil.pauseRecording()
        mode = MODE_PREPARE_RESUME
        showToolsMoreRecord()
        pauseTimer()
        binding.ivRecordController.setImageDrawable(getDrawable(R.drawable.iv_resume_record))
    }

    private fun pauseTimer() {
        timer?.run { cancel() }
    }

    private fun startRecord() {
        RecordUtil.startRecord(CacheUtils.getNewNameFileAudio(this))
        mode = MODE_PREPARE_PAUSE
        hideToolsMoreRecord()
        startTimer()
        binding.ivRecordController.setImageDrawable(getDrawable(R.drawable.iv_pause_record))
    }

    private fun startTimer() {
        currentTime = 0
        timer = object : CountDownTimer(1000000000, 1000) {
            override fun onTick(millisUntilFinished: Long) {
                currentTime++
                binding.tvTime.text = String.format(
                    "%02d:%02d",
                    (currentTime % 3600 / 60).toLong(),
                    (currentTime % 60).toLong()
                )
            }

            override fun onFinish() {
            }
        }
        timer?.run { start() }
    }

    override fun onResume() {
        super.onResume()
        if (!PermissionUtils.checkMicroPermission(this)) {
            showRecordPermissionDialog()
        }
    }

    private fun showToolsMoreRecord() {
        binding.tvNext.visibility = View.VISIBLE
        binding.ivRestartRecord.visibility = View.VISIBLE
    }

    private fun hideToolsMoreRecord() {
        binding.tvNext.visibility = View.GONE
        binding.ivRestartRecord.visibility = View.GONE
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

    override fun onPause() {
        super.onPause()
        restartRecord()
    }

}