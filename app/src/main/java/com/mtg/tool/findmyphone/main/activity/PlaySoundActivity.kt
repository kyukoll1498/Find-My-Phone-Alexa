package com.mtg.tool.findmyphone.main.activity

import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.media.AudioManager
import android.os.Bundle
import android.view.View
import android.widget.SeekBar
import android.widget.Toast
import com.bumptech.glide.Glide
import com.common.control.manager.AdmobManager
import com.common.control.manager.AppOpenManager
import com.common.control.utils.BroadcastUtils
import com.mtg.tool.findmyphone.ACTION_FINISH_DETECT
import com.mtg.tool.findmyphone.ACTION_UPDATE_AUDIO_IMPORT
import com.mtg.tool.findmyphone.ACTION_VOLUME_CHANGED
import com.mtg.tool.findmyphone.AdIds
import com.mtg.tool.findmyphone.IMPORT_SOUND_TYPE
import com.mtg.tool.findmyphone.KEY_SOUND_ITEM_DATA
import com.mtg.tool.findmyphone.R
import com.mtg.tool.findmyphone.base.BaseActivity
import com.mtg.tool.findmyphone.data.model.SoundItem
import com.mtg.tool.findmyphone.data.repo.AppRepository
import com.mtg.tool.findmyphone.databinding.ActivityPlaySoundBinding
import com.mtg.tool.findmyphone.main.dialog.DeleteDialog
import com.mtg.tool.findmyphone.main.dialog.RenameDialog
import com.mtg.tool.findmyphone.receiver.VolumeChangeReceiver
import com.mtg.tool.findmyphone.utils.app.AppPreferences
import com.mtg.tool.findmyphone.utils.app.MediaPlayerAppUtil

class PlaySoundActivity :
    BaseActivity<ActivityPlaySoundBinding>(ActivityPlaySoundBinding::inflate),
    VolumeChangeReceiver.VolumeChangeListener {
    private lateinit var currentSoundItem: SoundItem
    private var currentDuration = 15
    private var max = 100
    private var volume = 70
    private var appPreferences = AppPreferences.instance

    private lateinit var receiver: VolumeChangeReceiver
    private lateinit var audioManager: AudioManager


    override fun initView() {
        logEvent("effect_view")
        registerVolumeReceiver()
        audioManager = getSystemService(Context.AUDIO_SERVICE) as AudioManager
        currentSoundItem = intent.getSerializableExtra(KEY_SOUND_ITEM_DATA) as SoundItem
        if (currentSoundItem.avatar == R.drawable.ic_default_audio_avatar) {
            binding.ivSoundAvatar.setPadding(100, 100, 100, 100)
        }
        setUpUI()
        setUpWithFileSound()
        setSeekbarView()
        setDetailCommandView()
        AdmobManager.getInstance()
            .loadNative(this, AdIds.native_effect, binding.frAd, AdmobManager.NativeAdType.SMALL)
        AppOpenManager.getInstance().hideNativeOrBannerWhenShowOpenApp(this, binding.frAd)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding.tvName.setSelected(true)
        binding.tvVolume.setSelected(true)
        binding.tvDuration.setSelected(true)
        binding.tvDuration15s.isSelected = true
    }

    private fun setUpUI() {
        if (currentSoundItem.type == IMPORT_SOUND_TYPE) {
            binding.ivDelete.visibility = View.VISIBLE
            binding.ivEdit.visibility = View.VISIBLE
        }
        if (appPreferences.currentLanguage == "ar") {
            binding.seekBar.setRtL(true)
        }
    }

    private fun setDetailCommandView() {
        val soundItem = intent.getSerializableExtra(KEY_SOUND_ITEM_DATA) as? SoundItem
        binding.tvName.text = soundItem?.name
    }

    private fun setUpWithFileSound() {
        Glide.with(this).load(currentSoundItem.avatar).into(binding.ivSoundAvatar)
    }

    override fun addEvent() {
        binding.llController.setOnClickListener {
            if (binding.tvPlayerController.text == getString(R.string.play)) {
                if (currentSoundItem.type == IMPORT_SOUND_TYPE) {
                    logEvent("effect_play_click")
                }
                logEvent("click_detail_play")
                sendBroadcast(Intent(ACTION_FINISH_DETECT))
                startAudio()
                binding.tvPlayerController.text = getString(R.string.pause)
                binding.ivPlayerController.setImageDrawable(getDrawable(R.drawable.ic_resume))
                binding.animationView.visibility = View.VISIBLE
                binding.animationView.playAnimation()
                binding.animationView.speed = 2f
            } else if (binding.tvPlayerController.text == getString(R.string.pause)) {
                logEvent("effect_pause_click")
                pauseAudio()
                binding.tvPlayerController.text = getString(R.string.play)
                binding.ivPlayerController.setImageDrawable(getDrawable(R.drawable.ic_pause))
                binding.animationView.pauseAnimation()
                binding.animationView.visibility = View.GONE
            }
        }
        binding.tvDuration15s.setOnClickListener {
            logEvent("effect_duration_15s_click")
            updateDuration(15)
        }
        binding.tvDuration30s.setOnClickListener {
            logEvent("effect_duration_30s_click")
            updateDuration(30)
        }
        binding.tvDuration1m.setOnClickListener {
            logEvent("effect_duration_1m_click")
            updateDuration(60)
        }
        binding.tvDuration2m.setOnClickListener {
            logEvent("effect_duratiion_2m_click")
            updateDuration(120)
        }

        binding.seekBar.setSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar, newVolume: Int, fromUser: Boolean) {
                if (fromUser) {
                    updateVolume(newVolume)
                }
            }

            override fun onStartTrackingTouch(seekBar: SeekBar) {
            }

            override fun onStopTrackingTouch(seekBar: SeekBar) {

            }
        })

//        binding.btnVolumeDown.setOnClickListener {
//            logEvent("click_detail_volume_down")
//            if (volume != 0) {
//                updateVolume(--volume)
//                binding.seekBar.setProgress(volume)
//            }
//        }
//        binding.btnVolumeUp.setOnClickListener {
//            logEvent("click_detail_volume_up")
//            if (volume != max) {
//                updateVolume(++volume)
//                binding.seekBar.setProgress(volume)
//            }
//        }
        binding.btnVolumeDown.setOnClickListener {
            logEvent("effect_volume_minus_click")
            val am = getSystemService(Context.AUDIO_SERVICE) as AudioManager
            var currentVolume = am.getStreamVolume(AudioManager.STREAM_MUSIC)
            currentVolume--
            audioManager.setStreamVolume(AudioManager.STREAM_MUSIC, currentVolume, 0);
            binding.seekBar.setProgress(currentVolume)
        }
        binding.btnVolumeUp.setOnClickListener {
            logEvent("effect_volume_add_click")
            val am = getSystemService(Context.AUDIO_SERVICE) as AudioManager
            var currentVolume = am.getStreamVolume(AudioManager.STREAM_MUSIC)
            currentVolume++
            audioManager.setStreamVolume(AudioManager.STREAM_MUSIC, currentVolume, 0);
            binding.seekBar.setProgress(currentVolume)
        }

        binding.tvApply.setOnClickListener {
            if (currentSoundItem.type == IMPORT_SOUND_TYPE) {
                logEvent("click_add_audio_apply")
            }
            logEvent("effect_apply_click")
            saveSoundAndDuration()
            Toast.makeText(this, getString(R.string.save_successfully), Toast.LENGTH_SHORT).show()

        }

        binding.ivEdit.setOnClickListener {
            RenameDialog(this@PlaySoundActivity) { state, name ->
                run {
                    if (state) {
                        binding.tvName.text = name
                        currentSoundItem.soundPath?.let { it1 ->
                            AppRepository.updateName(
                                it1,
                                name
                            )
                        }
                        sendBroadcast(Intent(ACTION_UPDATE_AUDIO_IMPORT))
                    }
                }
            }.show()
        }

        binding.ivDelete.setOnClickListener {
            DeleteDialog(this@PlaySoundActivity, binding.tvName.text.toString()) {
                if (it) {
                    logEvent("click_add_audio_delete")
                    currentSoundItem.soundPath?.let { it1 -> AppRepository.deleteSound(it1) }
                    sendBroadcast(Intent(ACTION_UPDATE_AUDIO_IMPORT))
                    finish()
                }
            }.show()
        }
        binding.btnBack.setOnClickListener { finish() }
    }

    private fun saveSoundAndDuration() {
        appPreferences.currentDuration = currentDuration * 1000
        appPreferences.currentSound = currentSoundItem
        appPreferences.currentVolume = volume
        finish()
    }

    private fun updateVolume(newVolume: Int) {
        audioManager.setStreamVolume(AudioManager.STREAM_MUSIC, newVolume, 0)
        volume = newVolume
    }

    private fun registerVolumeReceiver() {
        receiver = VolumeChangeReceiver(this, this)
        BroadcastUtils.registerReceiver(this, receiver, IntentFilter(ACTION_VOLUME_CHANGED))
    }

    private fun updateDuration(duration: Int) {
        if (currentDuration != duration) {
            binding.tvDuration15s.isSelected = duration == 15
            binding.tvDuration30s.isSelected = duration == 30
            binding.tvDuration1m.isSelected = duration == 60
            binding.tvDuration2m.isSelected = duration == 120
            currentDuration = duration
        }
    }

//    private fun startAudio() {
//        MediaPlayerAppUtil.playAudio(this, currentSoundItem) {
////            onComplete
//            try {
//                binding.tvPlayerController.text = getString(R.string.play)
//                binding.ivPlayerController.setImageDrawable(getDrawable(R.drawable.ic_pause))
//            } catch (e: Exception) {
//                e.printStackTrace()
//            }
//
//        }
//    }

    private fun startAudio() {
        MediaPlayerAppUtil.playAudio(this, currentSoundItem) {
            // onComplete
            try {
                binding.tvPlayerController.text = getString(R.string.play)
                binding.ivPlayerController.setImageDrawable(getDrawable(R.drawable.ic_pause))
                binding.animationView.pauseAnimation()
                binding.animationView.visibility = View.GONE
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    private fun pauseAudio() {
        MediaPlayerAppUtil.stopAudio()
    }

    override fun onDestroy() {
        super.onDestroy()

        unregisterReceiver(receiver)

        MediaPlayerAppUtil.stopAudio()
    }

    override fun onVolumeChanged(volume: Int) {
        binding.seekBar.setProgress(volume)
    }

    private fun setSeekbarView() {
        binding.seekBar.setMax(audioManager.getStreamMaxVolume(AudioManager.STREAM_MUSIC))
        val volume: Int = audioManager.getStreamMaxVolume(AudioManager.STREAM_MUSIC) * 70 / 100
        audioManager.setStreamVolume(AudioManager.STREAM_MUSIC, volume, 0);

        binding.seekBar.presetProgress(volume)
    }
}