package com.mtg.tool.findmyphone.main.fragment

import android.content.Intent
import androidx.recyclerview.widget.GridLayoutManager
import com.common.control.base.OnActionCallback
import com.mtg.tool.findmyphone.ADAPTER_ADS_TYPE
import com.mtg.tool.findmyphone.ADAPTER_ITEM_TYPE
import com.mtg.tool.findmyphone.KEY_SOUND
import com.mtg.tool.findmyphone.KEY_SOUND_ITEM_DATA
import com.mtg.tool.findmyphone.base.BaseFragment
import com.mtg.tool.findmyphone.data.model.SoundItem
import com.mtg.tool.findmyphone.data.repo.AppRepository
import com.mtg.tool.findmyphone.databinding.FragmentSoundBinding
import com.mtg.tool.findmyphone.main.activity.PlaySoundActivity
import com.mtg.tool.findmyphone.main.adapter.SoundAdapter

class SoundFragment : BaseFragment<FragmentSoundBinding>(FragmentSoundBinding::inflate) {
    private lateinit var soundAdapter: SoundAdapter

    override fun initView() {
        setupList()

    }

    private fun setupList() {
        soundAdapter = SoundAdapter(AppRepository.getAllSound(requireContext()), requireActivity())
        var gridLayoutManager = GridLayoutManager(context, 3)
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
            if (key.equals(KEY_SOUND)) {
                val soundItem = data[0] as SoundItem
                logEvent("click_detail_play_" + soundItem.name?.replace(" ", "_")?.lowercase())
                val intent = Intent(activity, PlaySoundActivity::class.java)
                intent.putExtra(KEY_SOUND_ITEM_DATA, soundItem)
                startActivity(intent)
            }
        }
        binding.rcvSound.layoutManager = gridLayoutManager
        binding.rcvSound.adapter = soundAdapter
    }

    override fun addEvent() {

    }

    override fun onResume() {
        super.onResume()
        loadInter()
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