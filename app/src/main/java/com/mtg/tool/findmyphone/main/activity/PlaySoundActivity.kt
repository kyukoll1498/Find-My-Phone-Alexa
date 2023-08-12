package com.mtg.tool.findmyphone.main.activity

import com.mtg.tool.findmyphone.KEY_SOUND_ITEM_DATA
import com.mtg.tool.findmyphone.R
import com.mtg.tool.findmyphone.base.BaseActivity
import com.mtg.tool.findmyphone.data.model.SoundItem
import com.mtg.tool.findmyphone.databinding.ActivityMainBinding
import com.mtg.tool.findmyphone.databinding.ActivityPlaySoundBinding
import com.mtg.tool.findmyphone.utils.MediaPlayerUtil
import com.mtg.tool.findmyphone.utils.app.MediaPlayerAppUtil

class PlaySoundActivity : BaseActivity<ActivityPlaySoundBinding>(ActivityPlaySoundBinding::inflate)  {
    private lateinit var currentSoundItem: SoundItem

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
    }

    private fun startAudio() {
        MediaPlayerAppUtil.playAudio(this, currentSoundItem)
    }

    private fun pauseAudio() {

    }
}