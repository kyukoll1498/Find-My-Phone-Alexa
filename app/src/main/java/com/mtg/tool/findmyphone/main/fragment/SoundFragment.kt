package com.mtg.tool.findmyphone.main.fragment

import android.content.Context
import android.content.Intent
import androidx.recyclerview.widget.GridLayoutManager
import com.common.control.base.OnActionCallback
import com.common.control.manager.AppOpenManager
import com.mtg.tool.findmyphone.ADAPTER_ADS_TYPE
import com.mtg.tool.findmyphone.ADAPTER_ITEM_TYPE
import com.mtg.tool.findmyphone.KEY_SOUND
import com.mtg.tool.findmyphone.KEY_SOUND_ITEM_DATA
import com.mtg.tool.findmyphone.base.BaseActivity
import com.mtg.tool.findmyphone.data.model.SoundItem
import com.mtg.tool.findmyphone.data.repo.AppRepository
import com.mtg.tool.findmyphone.databinding.FragmentSoundBinding
import com.mtg.tool.findmyphone.main.activity.PlaySoundActivity
import com.mtg.tool.findmyphone.main.adapter.SoundAdapter
import com.mtg.tool.findmyphone.utils.app.AppPreferences

class SoundFragment : BaseActivity<FragmentSoundBinding>(FragmentSoundBinding::inflate) {
    private lateinit var soundAdapter: SoundAdapter
    private var isFirstLoad = true;

    companion object {
        @JvmStatic
        fun start(context: Context) {
            val starter = Intent(context, SoundFragment::class.java)
            context.startActivity(starter)
        }
    }

    override fun initView() {
        setupList()
    }

    private fun setupList() {
        soundAdapter = SoundAdapter(AppRepository.getMoreSound(this), this, isFirstLoad)
        val gridLayoutManager = GridLayoutManager(this, 3)
        gridLayoutManager.spanSizeLookup = object : GridLayoutManager.SpanSizeLookup() {
            override fun getSpanSize(position: Int): Int {
                return when (soundAdapter.getItemViewType(position)) {
                    ADAPTER_ADS_TYPE -> 3
                    ADAPTER_ITEM_TYPE -> 1
                    else -> -1
                }
            }
        }
        soundAdapter.mCallback = OnActionCallback { key, data ->
            if (key == KEY_SOUND) {
                val soundItem = data[0] as SoundItem
                logEvent("click_detail_play_" + soundItem.name?.replace(" ", "_")?.lowercase())
                val intent = Intent(this, PlaySoundActivity::class.java)
                intent.putExtra(KEY_SOUND_ITEM_DATA, soundItem)
                startActivity(intent)
            }
        }
        binding?.rcvSound?.layoutManager = gridLayoutManager
        binding?.rcvSound?.adapter = soundAdapter
    }

    override fun addEvent() {
        binding.ivBack.setOnClickListener { finish() }
    }

    override fun onResume() {
        super.onResume()
        loadInter()
        if (!AppOpenManager.getInstance().isShowingAd) {
            if (!isFirstLoad) {
                soundAdapter.reloadNativeAd()
            }
            isFirstLoad = false
        }
        val currentSound = AppPreferences.instance.currentSound
        val sounds = AppRepository.getMoreSound(this)
        for (item in sounds) {
            item.isSelected = (item.soundPath == currentSound.soundPath)
        }
        soundAdapter.submitList(sounds)
    }

    private fun loadInter() {
//        if (AdCache.getInstance().interSound == null) {
//            AdmobManager.getInstance()
//                .loadInterAds(requireActivity(), BuildConfig.inter_sound, object : AdCallback() {
//                    override fun onResultInterstitialAd(interstitialAd: InterstitialAd) {
//                        super.onResultInterstitialAd(interstitialAd)
//                        AdCache.getInstance().interSound = interstitialAd
//                    }
//                })
//        }
    }
}