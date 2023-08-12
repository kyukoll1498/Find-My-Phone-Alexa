package com.mtg.tool.findmyphone.main.activity

import android.content.res.ColorStateList
import android.graphics.Color
import com.mtg.tool.findmyphone.KEY_SOUND_ITEM_DATA
import com.mtg.tool.findmyphone.R
import com.mtg.tool.findmyphone.base.BaseActivity
import com.mtg.tool.findmyphone.data.model.SoundItem
import com.mtg.tool.findmyphone.databinding.ActivityPlaySoundBinding
import com.mtg.tool.findmyphone.utils.app.MediaPlayerAppUtil

class PlaySoundActivity :
    BaseActivity<ActivityPlaySoundBinding>(ActivityPlaySoundBinding::inflate) {
    private lateinit var currentSoundItem: SoundItem
    private var currentDuration = 15

    override fun initView() {
        currentSoundItem = intent.getSerializableExtra(KEY_SOUND_ITEM_DATA) as SoundItem
    }

    override fun addEvent() {
        binding.llController.setOnClickListener {
            if (binding.tvPlayerController.text == getString(R.string.play)) {
                pauseAudio()
                binding.tvPlayerController.text = getString(R.string.pause)
            } else if (binding.tvPlayerController.text == getString(R.string.pause)) {
                startAudio()
                binding.tvPlayerController.text = getString(R.string.play)
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
        MediaPlayerAppUtil.playAudio(this, currentSoundItem)
    }

    private fun pauseAudio() {

    }
}