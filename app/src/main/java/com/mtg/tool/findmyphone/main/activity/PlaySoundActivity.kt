package com.mtg.tool.findmyphone.main.activity

import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.content.res.ColorStateList
import android.graphics.Color
import android.media.AudioManager
import android.view.View
import android.widget.SeekBar
import android.widget.Toast
import com.bumptech.glide.Glide
import com.common.control.manager.AdmobManager
import com.mtg.tool.findmyphone.ACTION_UPDATE_AUDIO_IMPORT
import com.mtg.tool.findmyphone.ACTION_VOLUME_CHANGED
import com.mtg.tool.findmyphone.BuildConfig
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
        registerVolumeReceiver()
        audioManager = getSystemService(Context.AUDIO_SERVICE) as AudioManager
        currentSoundItem = intent.getSerializableExtra(KEY_SOUND_ITEM_DATA) as SoundItem
        setUpUI()
        setUpWithFileSound()
        setSeekbarView()
        setDetailCommandView()
        AdmobManager.getInstance().loadCollapsibleBanner(this, BuildConfig.collapsible_banner_detail_sound, binding.frAd)
    }

    private fun setUpUI() {
        if (currentSoundItem.type == IMPORT_SOUND_TYPE) {
            binding.ivDelete.visibility = View.VISIBLE
            binding.ivEdit.visibility = View.VISIBLE
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
                logEvent("click_detail_play")
                startAudio()
                binding.tvPlayerController.text = getString(R.string.pause)
                binding.ivPlayerController.setImageDrawable(getDrawable(R.drawable.ic_resume))
            } else if (binding.tvPlayerController.text == getString(R.string.pause)) {
                logEvent("click_detail_pause")
                pauseAudio()
                binding.tvPlayerController.text = getString(R.string.play)
                binding.ivPlayerController.setImageDrawable(getDrawable(R.drawable.ic_pause))
            }
        }
        binding.tvDuration15s.setOnClickListener {
            logEvent("click_detail_15s")
            updateDuration(15)
        }
        binding.tvDuration30s.setOnClickListener {
            logEvent("click_detail_30s")
            updateDuration(30)
        }
        binding.tvDuration1m.setOnClickListener {
            logEvent("click_detail_1m")
            updateDuration(60)
        }
        binding.tvDuration2m.setOnClickListener {
            logEvent("click_detail_2m")
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

        binding.btnVolumeDown.setOnClickListener {
            logEvent("click_detail_volume_down")
            if (volume != 0) {
                updateVolume(--volume)
                binding.seekBar.setProgress(volume)
            }
        }
        binding.btnVolumeUp.setOnClickListener {
            logEvent("click_detail_volume_up")
            if (volume != max) {
                updateVolume(++volume)
                binding.seekBar.setProgress(volume)
            }
        }
        binding.tvApply.setOnClickListener {
            saveSoundAndDuration()
            Toast.makeText(this, "Save successfully!", Toast.LENGTH_SHORT).show()

        }

        binding.ivEdit.setOnClickListener {
            RenameDialog(this@PlaySoundActivity){state, name ->
                run {
                    if (state) {
                        binding.tvName.text = name
                        currentSoundItem.soundPath?.let { it1 -> AppRepository.updateName(it1, name) }
                        sendBroadcast(Intent(ACTION_UPDATE_AUDIO_IMPORT))
                    }
                }
            }.show()
        }

        binding.ivDelete.setOnClickListener {
            DeleteDialog(this@PlaySoundActivity, binding.tvName.text.toString()){
                if (it) {
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
        registerReceiver(receiver, IntentFilter(ACTION_VOLUME_CHANGED))
    }

    private fun updateDuration(duration: Int) {
        if (currentDuration != duration) {
            when (currentDuration) {
                15 -> {
                    binding.tvDuration15s.setTextColor(ColorStateList.valueOf(Color.parseColor("#828287")))
                    binding.tvDuration15s.backgroundTintList =
                        ColorStateList.valueOf(Color.parseColor("#EDEDED"))
                }

                30 -> {
                    binding.tvDuration30s.setTextColor(ColorStateList.valueOf(Color.parseColor("#828287")))
                    binding.tvDuration30s.backgroundTintList =
                        ColorStateList.valueOf(Color.parseColor("#EDEDED"))
                }

                60 -> {
                    binding.tvDuration1m.setTextColor(ColorStateList.valueOf(Color.parseColor("#828287")))
                    binding.tvDuration1m.backgroundTintList =
                        ColorStateList.valueOf(Color.parseColor("#EDEDED"))
                }

                120 -> {
                    binding.tvDuration2m.setTextColor(ColorStateList.valueOf(Color.parseColor("#828287")))
                    binding.tvDuration2m.backgroundTintList =
                        ColorStateList.valueOf(Color.parseColor("#EDEDED"))
                }
            }

            when (duration) {
                15 -> {
                    binding.tvDuration15s.setTextColor(ColorStateList.valueOf(Color.parseColor("#FFFFFF")))
                    binding.tvDuration15s.backgroundTintList =
                        ColorStateList.valueOf(Color.parseColor("#F06A33"))
                }

                30 -> {
                    binding.tvDuration30s.setTextColor(ColorStateList.valueOf(Color.parseColor("#FFFFFF")))
                    binding.tvDuration30s.backgroundTintList =
                        ColorStateList.valueOf(Color.parseColor("#F06A33"))
                }

                60 -> {
                    binding.tvDuration1m.setTextColor(ColorStateList.valueOf(Color.parseColor("#FFFFFF")))
                    binding.tvDuration1m.backgroundTintList =
                        ColorStateList.valueOf(Color.parseColor("#F06A33"))
                }

                120 -> {
                    binding.tvDuration2m.setTextColor(ColorStateList.valueOf(Color.parseColor("#FFFFFF")))
                    binding.tvDuration2m.backgroundTintList =
                        ColorStateList.valueOf(Color.parseColor("#F06A33"))
                }
            }
            currentDuration = duration
        }
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