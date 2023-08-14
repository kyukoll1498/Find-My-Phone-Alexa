package com.mtg.tool.findmyphone.main.activity

import android.content.Context
import android.content.IntentFilter
import android.content.res.ColorStateList
import android.graphics.Color
import android.media.AudioManager
import android.os.Handler
import android.widget.SeekBar
import com.bumptech.glide.Glide
import com.mtg.tool.findmyphone.ACTION_VOLUME_CHANGED
import com.mtg.tool.findmyphone.KEY_SOUND_ITEM_DATA
import com.mtg.tool.findmyphone.R
import com.mtg.tool.findmyphone.base.BaseActivity
import com.mtg.tool.findmyphone.data.model.SoundItem
import com.mtg.tool.findmyphone.databinding.ActivityPlaySoundBinding
import com.mtg.tool.findmyphone.receiver.VolumeChangeReceiver
import com.mtg.tool.findmyphone.utils.app.MediaPlayerAppUtil

class PlaySoundActivity :
    BaseActivity<ActivityPlaySoundBinding>(ActivityPlaySoundBinding::inflate), VolumeChangeReceiver.VolumeChangeListener {
    private lateinit var currentSoundItem: SoundItem
    private var currentDuration = 15

    private lateinit var receiver: VolumeChangeReceiver
    private lateinit var audioManager: AudioManager


    override fun initView() {
        registerVolumeReceiver()
        audioManager = getSystemService(Context.AUDIO_SERVICE) as AudioManager
        currentSoundItem = intent.getSerializableExtra(KEY_SOUND_ITEM_DATA) as SoundItem
        setUpWithFileSound()
        setSeekbarView()
    }

    private fun setUpWithFileSound() {
        Glide.with(this).load(currentSoundItem.avatar).into(binding.ivSoundAvatar)
    }

    override fun addEvent() {
        binding.llController.setOnClickListener {
            if (binding.tvPlayerController.text == getString(R.string.play)) {
                startAudio()
                binding.tvPlayerController.text = getString(R.string.pause)
                binding.ivPlayerController.setImageDrawable(getDrawable(R.drawable.ic_resume))
            } else if (binding.tvPlayerController.text == getString(R.string.pause)) {
                pauseAudio()
                binding.tvPlayerController.text = getString(R.string.play)
                binding.ivPlayerController.setImageDrawable(getDrawable(R.drawable.ic_pause))
            }
        }
        binding.tvDuration15s.setOnClickListener {
            updateDuration(15)
        }
        binding.tvDuration30s.setOnClickListener {
            updateDuration(30)
        }
        binding.tvDuration1m.setOnClickListener {
            updateDuration(60)
        }
        binding.tvDuration2m.setOnClickListener {
            updateDuration(120)
        }

        binding.seekBar.setSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar, newVolume: Int, fromUser: Boolean) {

                audioManager.setStreamVolume(AudioManager.STREAM_MUSIC, newVolume, 0);
            }

            override fun onStartTrackingTouch(seekBar: SeekBar) {
            }

            override fun onStopTrackingTouch(seekBar: SeekBar) {

            }
        })

        binding.btnBack.setOnClickListener { finish() }
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
        Handler().postDelayed(Runnable {
            try {
                binding.seekBar.setProgress(volume)
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }, 500)
    }
}