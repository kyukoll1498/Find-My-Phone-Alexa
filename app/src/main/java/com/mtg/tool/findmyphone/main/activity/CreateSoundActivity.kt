package com.mtg.tool.findmyphone.main.activity

import android.annotation.SuppressLint
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.Handler
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.MotionEvent
import android.view.View
import android.widget.Toast
import com.common.control.interfaces.AdCallback
import com.common.control.manager.AdmobManager
import com.common.control.manager.AppOpenManager
import com.common.control.utils.BroadcastUtils
import com.google.android.gms.ads.nativead.NativeAd
import com.mtg.tool.findmyphone.ACTION_FINISH_CREATE_SOUND_SCREEN
import com.mtg.tool.findmyphone.ACTION_FINISH_DETECT
import com.mtg.tool.findmyphone.ACTION_UPDATE_AUDIO_IMPORT
import com.mtg.tool.findmyphone.AdIds
import com.mtg.tool.findmyphone.IMPORT_SOUND_TYPE
import com.mtg.tool.findmyphone.KEY_SOUND
import com.mtg.tool.findmyphone.R
import com.mtg.tool.findmyphone.REQUEST_FILE_AUDIO_CODE
import com.mtg.tool.findmyphone.REQUEST_MICRO_PERMISSION_CODE
import com.mtg.tool.findmyphone.REQUEST_READ_AUDIO_PERMISSION_CODE
import com.mtg.tool.findmyphone.REQUEST_READ_PERMISSION_CODE
import com.mtg.tool.findmyphone.base.BaseActivity
import com.mtg.tool.findmyphone.data.model.SoundItem
import com.mtg.tool.findmyphone.data.preferences.SharedPrefs
import com.mtg.tool.findmyphone.data.repo.AppRepository
import com.mtg.tool.findmyphone.databinding.ActivityCreateSoundBinding
import com.mtg.tool.findmyphone.main.dialog.ReadAudioPermissionDialog
import com.mtg.tool.findmyphone.main.dialog.RecordPermissionDialog
import com.mtg.tool.findmyphone.utils.CacheUtils
import com.mtg.tool.findmyphone.utils.FileUtils
import com.mtg.tool.findmyphone.utils.PermissionUtils
import com.mtg.tool.findmyphone.utils.app.MediaPlayerAppUtil
import com.mtg.tool.findmyphone.utils.constant.Constants

class CreateSoundActivity :
    BaseActivity<ActivityCreateSoundBinding>(ActivityCreateSoundBinding::inflate) {
    private val finishReceiver: BroadcastReceiver = object : BroadcastReceiver() {
        @SuppressLint("NotifyDataSetChanged")
        override fun onReceive(context: Context, intent: Intent) {
            finish()
        }
    }

    companion object {
        @JvmStatic
        fun start(context: Context) {
            val starter = Intent(context, CreateSoundActivity::class.java)
            context.startActivity(starter)
        }
    }

    private lateinit var currentSoundItem: SoundItem
    private val listNative by lazy {
        arrayListOf(AdIds.native_add_high, AdIds.native_add)
    }

    override fun initView() {
        BroadcastUtils.registerReceiver(
            this,
            finishReceiver,
            IntentFilter(ACTION_FINISH_CREATE_SOUND_SCREEN)
        )
    }

    private fun loadAlternateNative() {
        Log.d("Refresh", "Create Refresh")
        AdmobManager.getInstance().preloadAlternateNative(
            this,
            listNative,
            object : AdCallback() {
                override fun onNativeAds(nativeAd: NativeAd?) {
                    super.onNativeAds(nativeAd)
                    AdmobManager.getInstance().showNative(
                        this@CreateSoundActivity,
                        nativeAd,
                        binding.frAd,
                        AdmobManager.NativeAdType.BIG
                    )
                    Log.d("Refresh", "ShowRefreshHowToUse")
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

    override fun onResume() {
        super.onResume()
        if (!AppOpenManager.getInstance().isShowingAd) {
            loadAlternateNative()
        }
        if (binding.tvAppName.text == getString(R.string.import_audio)) {
            logEvent("import_view")
        } else {
            logEvent("create_view")
        }
    }

    @SuppressLint("ClickableViewAccessibility")
    override fun addEvent() {
        binding.btnBack.setOnClickListener { onBackPressed() }
        binding.llRecordAudio.setOnClickListener {
            logEvent("create_record_click")
            binding.llImportAudio.isEnabled = false
            binding.llImportAudio.isClickable = false
            binding.llRecordAudio.isEnabled = false
            binding.llRecordAudio.isClickable = false
            Handler().postDelayed(Runnable {
                binding.llImportAudio.isEnabled = true
                binding.llImportAudio.isClickable = true
                binding.llRecordAudio.isEnabled = true
                binding.llRecordAudio.isClickable = true
            }, 1000)
            sendBroadcast(Intent(ACTION_FINISH_DETECT))
            logEvent("click_add_import_pms")
            if (!PermissionUtils.checkMicroPermission(this)) {
                PermissionUtils.requestMicroPermission(this)
            } else {
                startRecordAudio()
            }

        }
        binding.llImportAudio.setOnClickListener {
            logEvent("create_import_click")
            binding.llImportAudio.isEnabled = false
            binding.llImportAudio.isClickable = false
            binding.llRecordAudio.isEnabled = false
            binding.llRecordAudio.isClickable = false
            Handler().postDelayed(Runnable {
                binding.llImportAudio.isEnabled = true
                binding.llImportAudio.isClickable = true
                binding.llRecordAudio.isEnabled = true
                binding.llRecordAudio.isClickable = true
            }, 1000)
            sendBroadcast(Intent(ACTION_FINISH_DETECT))
            logEvent("click_add_record_pms")
            if (!PermissionUtils.checkReadAudioPermission(this)) {
                PermissionUtils.requestReadAudioPermission(this)
            } else {
                startImportAudio()
            }
        }
        binding.btnSave.setOnClickListener {
            saveSoundItem()
            logEvent("import_save_click")
        }
        binding.llAudioController.setOnClickListener {
            if (binding.tvPlayerController.text == getString(R.string.play)) {
                logEvent("import_play_click")
                sendBroadcast(Intent(ACTION_FINISH_DETECT))
                startAudio()
                binding.tvPlayerController.text = getString(R.string.pause)
                binding.ivPlayerController.setImageDrawable(getDrawable(R.drawable.ic_resume))
            } else if (binding.tvPlayerController.text == getString(R.string.pause)) {
                logEvent("import_pause_click")
                pauseAudio()
                binding.tvPlayerController.text = getString(R.string.play)
                binding.ivPlayerController.setImageDrawable(getDrawable(R.drawable.ic_pause))
            }
        }
        binding.edtName.setOnTouchListener { _, event ->
            if (event.action == MotionEvent.ACTION_DOWN) {
                logEvent("import_edit_click")
            }
            false
        }
        binding.edtName.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                s?.let {
                    logEvent("import_name_enter")
                }
            }

            override fun afterTextChanged(s: Editable?) {
            }
        })
    }

    private fun saveSoundItem() {
        logEvent("click_add_save_sound")
        currentSoundItem.name = binding.edtName.text.toString().trim()
        if (currentSoundItem.name!!.isEmpty()) {
            Toast.makeText(this, getString(R.string.name_sound_is_empty), Toast.LENGTH_SHORT).show()
        } else if (AppRepository.checkHasSound(currentSoundItem.name!!)) {
            Toast.makeText(this, getString(R.string.name_sound_already_exists), Toast.LENGTH_SHORT)
                .show()
        } else {
            AppRepository.insertSound(currentSoundItem)
            var intent = Intent(ACTION_UPDATE_AUDIO_IMPORT)
            intent.putExtra(KEY_SOUND, currentSoundItem)
            sendBroadcast(intent)
            finish()
        }
    }

    private fun updateCurrentSound() {
        currentSoundItem = SoundItem(
            IMPORT_SOUND_TYPE,
            "",
            System.currentTimeMillis(),
            R.drawable.avatar_audio_default,
            R.drawable.ic_default_audio_avatar,
            CacheUtils.getLastFilePathAudio(),
            0,
            15000,
            FileUtils.getDurationFromAudioFile(CacheUtils.getLastFilePathAudio())!! <= 15000.toLong()
        )
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

    private fun startImportAudio() {
        val intent = Intent(Intent.ACTION_GET_CONTENT)
        intent.type = "audio/*"
        startActivityForResult(intent, REQUEST_FILE_AUDIO_CODE)
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
            SharedPrefs.put(this, Constants.REQUEST_POPUP_PMS_MICRO, false)
            if (!PermissionUtils.checkMicroPermission(this)) {
                showRecordPermissionDialog()
            } else {
                startRecordAudio()
            }
        }

        if (requestCode == REQUEST_READ_AUDIO_PERMISSION_CODE || requestCode == REQUEST_READ_PERMISSION_CODE) {
            SharedPrefs.put(this, Constants.REQUEST_POPUP_PMS_ACCESS, false)
            if (!PermissionUtils.checkReadAudioPermission(this)) {
                showReadAudioPermissionDialog()
            } else {
                startImportAudio()
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

        if (requestCode == REQUEST_READ_AUDIO_PERMISSION_CODE) {
            if (!PermissionUtils.checkReadAudioPermission(this)) {
                showReadAudioPermissionDialog()
            } else {
                startImportAudio()
            }
        }

        if (requestCode == REQUEST_FILE_AUDIO_CODE) {
            var uri = data?.data
            try {
                if (uri != null) {
                    var file =
                        FileUtils.saveFileFromUri(uri, CacheUtils.getNewNameFileAudio(this), this)

                    binding.tvPath.text = FileUtils.getFileNameAudioFromUri(uri, this, 12)
                    binding.edtName.setText(FileUtils.getFileNameAudioFromUri(uri, this, 100))
                    var duration = file?.let { FileUtils.getDurationFromAudioFile(it.path) }
//                if (duration != null && duration > 15000) {
//                    file?.path?.let { AudioUtil.cutAudio(this, 0, 15*1000, duration, it, true){} }
//                }
                    updateCurrentSound()
                    gotoSave()
                }
            } catch (e: Exception) {
                e.printStackTrace()
                Toast.makeText(this, "The audio file is error!", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun gotoSave() {
        binding.ctOptions.visibility = View.GONE
        binding.ctSaveRecord.visibility = View.VISIBLE
        binding.btnSave.visibility = View.VISIBLE
        binding.tvAppName.text = getString(R.string.import_audio)
//        binding.frAd.visibility = View.GONE
//        binding.frAd2.visibility = View.VISIBLE
//        AdmobManager.getInstance().loadNative(this, BuildConfig.native_import, binding.frAd2, com.common.control.R.layout.custom_native_ads_2)
    }


    private fun showReadAudioPermissionDialog() {
        ReadAudioPermissionDialog(this) {
            if (it) {
                PermissionUtils.goSettingsForReadAudioPermission(this)
            }
        }.show()
    }

    override fun onDestroy() {
        super.onDestroy()
        unregisterReceiver(finishReceiver)
    }

    override fun onBackPressed() {
        logEvent("import_back_click")
        if (binding.ctSaveRecord.visibility == View.VISIBLE) {
//            binding.frAd.visibility = View.VISIBLE
//            binding.frAd2.visibility = View.GONE
//            binding.frAd2.removeAllViews()
//            LayoutInflater.from(this).inflate(R.layout.fake_loading, binding.frAd2)
            pauseAudio()
            binding.tvPlayerController.text = getString(R.string.play)
            binding.ivPlayerController.setImageDrawable(getDrawable(R.drawable.ic_pause))
            CacheUtils.removeLastFileAudio()
            binding.ctOptions.visibility = View.VISIBLE
            binding.ctSaveRecord.visibility = View.GONE
            binding.btnSave.visibility = View.GONE
            binding.tvAppName.text = getString(R.string.create_sound)
        } else {
            super.onBackPressed()
        }
    }

    override fun onPause() {
        super.onPause()
        pauseAudio()
        binding.tvPlayerController.text = getString(R.string.play)
        binding.ivPlayerController.setImageDrawable(getDrawable(R.drawable.ic_pause))
    }
}