package com.alx.findphone.claptofind.flashalert.main.activity

import android.content.Intent
import android.os.CountDownTimer
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.MotionEvent
import android.view.View
import android.widget.Toast
import com.alx.findphone.claptofind.flashalert.ACTION_FINISH_CREATE_SOUND_SCREEN
import com.alx.findphone.claptofind.flashalert.ACTION_FINISH_DETECT
import com.alx.findphone.claptofind.flashalert.ACTION_UPDATE_AUDIO_IMPORT
import com.alx.findphone.claptofind.flashalert.AdIds
import com.alx.findphone.claptofind.flashalert.IMPORT_SOUND_TYPE
import com.alx.findphone.claptofind.flashalert.KEY_SOUND
import com.alx.findphone.claptofind.flashalert.R
import com.alx.findphone.claptofind.flashalert.REQUEST_MICRO_PERMISSION_CODE
import com.alx.findphone.claptofind.flashalert.base.BaseActivity
import com.alx.findphone.claptofind.flashalert.data.model.SoundItem
import com.alx.findphone.claptofind.flashalert.data.repo.AppRepository
import com.alx.findphone.claptofind.flashalert.databinding.ActivityRecordSoundBinding
import com.alx.findphone.claptofind.flashalert.main.dialog.RecordPermissionDialog
import com.alx.findphone.claptofind.flashalert.utils.AudioUtil
import com.alx.findphone.claptofind.flashalert.utils.CacheUtils
import com.alx.findphone.claptofind.flashalert.utils.FileUtils
import com.alx.findphone.claptofind.flashalert.utils.PermissionUtils
import com.alx.findphone.claptofind.flashalert.utils.RecordUtil
import com.alx.findphone.claptofind.flashalert.utils.app.MediaPlayerAppUtil
import com.common.control.interfaces.AdCallback
import com.common.control.manager.AdmobManager
import com.common.control.manager.AppOpenManager
import com.google.android.gms.ads.nativead.NativeAd

class RecordAudioActivity :
    BaseActivity<ActivityRecordSoundBinding>(ActivityRecordSoundBinding::inflate) {
    companion object {
        const val MODE_PREPARE_START = 432
        const val MODE_PREPARE_PAUSE = 433
        const val MODE_PREPARE_RESUME = 434
    }

    private var isSave = false
    private lateinit var currentSoundItem: SoundItem
    private var mode = MODE_PREPARE_START
    private var currentTime = 0
    private var timer: CountDownTimer? = null
    private val listNative by lazy {
        arrayListOf(AdIds.native_add_high, AdIds.native_add)
    }
    override fun initView() {
        logEvent("record_view")
        binding.tvNext.isSelected = true
    }

    private fun loadAlternateNative() {
        Log.d("Refresh","Record Refresh")
        AdmobManager.getInstance().preloadAlternateNative(
            this,
            listNative,
            object : AdCallback(){
                override fun onNativeAds(nativeAd: NativeAd?) {
                    super.onNativeAds(nativeAd)
                    AdmobManager.getInstance().showNative(
                        this@RecordAudioActivity,
                        nativeAd,
                        binding.frAd,
                        AdmobManager.NativeAdType.BIG
                    )
                    Log.d("Refresh","ShowRefreshHowToUse")
                }
                override fun onAdImpression() {
                    super.onAdImpression()
                    logEvent("record_sound_native_view")
                }

                override fun onAdClicked() {
                    super.onAdClicked()
                    logEvent("record_sound_native_click")
                }
            }
        )
    }

    override fun addEvent() {
        binding.btnBack.setOnClickListener {
            logEvent("record_sound_back_click")
            onBackPressed()
        }
        binding.ivRecordController.setOnClickListener {
            when (mode) {
                MODE_PREPARE_START -> {
                    startRecord()
                    logEvent("record_start_click")
                }

                MODE_PREPARE_PAUSE -> {
                    pauseRecord()
                    logEvent("record_pause_click")
                }

                MODE_PREPARE_RESUME -> {
                    resumeRecord()
                    logEvent("record_play_click")
                }
            }
        }
        binding.ivRestartRecord.setOnClickListener {
            restartRecord()
        }
        binding.tvNext.setOnClickListener {
            logEvent("record_next_click")
            stopRecord()
        }
        binding.btnSave.setOnClickListener {
            logEvent("record_sound_save_click")
            saveSoundItem()
        }
        binding.llAudioController.setOnClickListener {
            if (binding.tvPlayerController.text == getString(R.string.play)) {
                startAudio()
                binding.tvPlayerController.text = getString(R.string.pause)
                binding.ivPlayerController.setImageDrawable(getDrawable(R.drawable.ic_resume))
                logEvent("record_sound_play_click")
            } else if (binding.tvPlayerController.text == getString(R.string.pause)) {
                logEvent("record_sound_pause_click")
                pauseAudio()
                binding.tvPlayerController.text = getString(R.string.play)
                binding.ivPlayerController.setImageDrawable(getDrawable(R.drawable.ic_pause))
            }
        }
        binding.edtName.setOnTouchListener { _, event ->
            if (event.action == MotionEvent.ACTION_DOWN) {
                logEvent("record_sound_edit_click")
            }
            false
        }
        binding.edtName.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                s?.let {
                    logEvent("record_sound_name_enter")
                }
            }
            override fun afterTextChanged(s: Editable?) {
            }
        })

    }

    private fun startAudio() {
        MediaPlayerAppUtil.playAudio(this, currentSoundItem) {
//            onComplete
            try {
                binding.tvPlayerController.text = getString(R.string.play)
                binding.ivPlayerController.setImageDrawable(getDrawable(R.drawable.ic_pause))
            } catch (e: Exception) {
                e.printStackTrace()
            }

        }
    }

    private fun pauseAudio() {
        MediaPlayerAppUtil.stopAudio()
    }

    private fun stopRecord() {
        RecordUtil.stopRecording()
//        cutAudio15s()
        mode = MODE_PREPARE_START
        hideToolsMoreRecord()
        restartTimer()
        binding.ivRecordController.setImageDrawable(getDrawable(R.drawable.ic_micro_start))
        updateCurrentSound()
        binding.edtName.setText(getNameAudio())
        gotoSave()
    }

    private fun getNameAudio(): String {
        var name = "audio"
        var id = 1
        while (AppRepository.checkHasSound("$name$id")){
            id++
        }
        return "$name$id"
    }

    private fun updateCurrentSound() {
        currentSoundItem = SoundItem(
            IMPORT_SOUND_TYPE,
            "",
            System.currentTimeMillis(),
            R.drawable.avatar_audio_default,
            R.drawable.ic_default_audio_avatar,
            CacheUtils.getLastFilePathAudio(), 0, 15000, FileUtils.getDurationFromAudioFile(CacheUtils.getLastFilePathAudio())!! <= 15000.toLong()
        )
    }

    private fun gotoSave() {
        binding.ctRecordController.visibility = View.GONE
        binding.ctSaveRecord.visibility = View.VISIBLE
        binding.btnSave.visibility = View.VISIBLE
//        binding.frAd.visibility = View.GONE
//        binding.frAd2.visibility = View.VISIBLE
//        AdmobManager.getInstance().loadNative(this, BuildConfig.native_record_save, binding.frAd2, com.common.control.R.layout.custom_native_ads_2)
    }

    private fun cutAudio15s() {
        if (currentTime >= 15) {
            AudioUtil.cutAudio(this, 0, 15, CacheUtils.getLastFilePathAudio()) { }
        }
    }

    private fun saveSoundItem() {
        logEvent("click_add_record_save_sound")
        currentSoundItem.name = binding.edtName.text.toString().trim()
        if (currentSoundItem.name!!.isEmpty()) {
            Toast.makeText(this, getString(R.string.name_sound_is_empty), Toast.LENGTH_SHORT).show()
        } else if (AppRepository.checkHasSound(currentSoundItem.name!!)) {
            Toast.makeText(this, getString(R.string.name_sound_already_exists), Toast.LENGTH_SHORT).show()
        } else {
            AppRepository.insertSound(currentSoundItem)
            var intent = Intent(ACTION_UPDATE_AUDIO_IMPORT)
            intent.putExtra(KEY_SOUND, currentSoundItem)
            sendBroadcast(intent)
            sendBroadcast(Intent(ACTION_FINISH_CREATE_SOUND_SCREEN))
            isSave = true
            finish()
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
        sendBroadcast(Intent(ACTION_FINISH_DETECT))
        RecordUtil.startRecord(CacheUtils.getNewNameFileAudio(this))
        mode = MODE_PREPARE_PAUSE
        hideToolsMoreRecord()
        startTimer()
        binding.ivRecordController.setImageDrawable(getDrawable(R.drawable.iv_pause_record))
    }

    private fun startTimer() {
        currentTime = 0
        timer = object : CountDownTimer(15000, 1000) {
            override fun onTick(millisUntilFinished: Long) {
                currentTime++
                binding.tvTime.text = String.format(
                    "%02d:%02d",
                    (currentTime % 3600 / 60).toLong(),
                    (currentTime % 60).toLong()
                )
            }

            override fun onFinish() {
                stopRecord()
            }
        }
        timer?.run { start() }
    }

    override fun onResume() {
        super.onResume()
        if (!AppOpenManager.getInstance().isShowingAd){
            loadAlternateNative()
        }
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

    override fun onBackPressed() {
        logEvent("User click back in record sound screen")
        if (binding.ctSaveRecord.visibility == View.VISIBLE) {
//            binding.frAd.visibility = View.VISIBLE
//            binding.frAd2.visibility = View.GONE
//            binding.frAd2.removeAllViews()
//            LayoutInflater.from(this).inflate(R.layout.fake_loading, binding.frAd2)
            pauseAudio()
            binding.tvPlayerController.text = getString(R.string.play)
            binding.ivPlayerController.setImageDrawable(getDrawable(R.drawable.ic_pause))
            CacheUtils.removeLastFileAudio()
            binding.ctRecordController.visibility = View.VISIBLE
            binding.ctSaveRecord.visibility = View.GONE
            binding.btnSave.visibility = View.GONE
        } else {
            super.onBackPressed()
        }
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
        if (binding.ctRecordController.visibility == View.VISIBLE) {
            restartRecord()
        }
        pauseAudio()
        binding.tvPlayerController.text = getString(R.string.play)
        binding.ivPlayerController.setImageDrawable(getDrawable(R.drawable.ic_pause))
    }

    override fun onDestroy() {
        super.onDestroy()
        if (!isSave) {
            CacheUtils.removeLastFileAudio()
        }
    }

}