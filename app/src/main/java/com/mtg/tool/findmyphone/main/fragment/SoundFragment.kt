package com.mtg.tool.findmyphone.main.fragment

import android.content.Intent
import androidx.recyclerview.widget.GridLayoutManager
import com.common.control.base.OnActionCallback
import com.common.control.interfaces.AdCallback
import com.common.control.manager.AdmobManager
import com.google.android.gms.ads.LoadAdError
import com.google.android.gms.ads.interstitial.InterstitialAd
import com.mtg.tool.findmyphone.ADAPTER_ADS_TYPE
import com.mtg.tool.findmyphone.ADAPTER_ITEM_TYPE
import com.mtg.tool.findmyphone.ADS_SOUND_TYPE
import com.mtg.tool.findmyphone.AdCache
import com.mtg.tool.findmyphone.BuildConfig
import com.mtg.tool.findmyphone.DEFAULT_SOUND_TYPE
import com.mtg.tool.findmyphone.KEY_SOUND
import com.mtg.tool.findmyphone.KEY_SOUND_ITEM_DATA
import com.mtg.tool.findmyphone.base.BaseFragment
import com.mtg.tool.findmyphone.data.model.SoundItem
import com.mtg.tool.findmyphone.data.repo.AppRepository
import com.mtg.tool.findmyphone.databinding.FragmentSoundBinding
import com.mtg.tool.findmyphone.main.activity.MainActivity
import com.mtg.tool.findmyphone.main.activity.PlaySoundActivity
import com.mtg.tool.findmyphone.main.adapter.SoundAdapter
import com.mtg.tool.findmyphone.utils.EventLogger
import kotlin.math.log

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
                logEvent("click_sound_play")
                val soundItem = data[0] as SoundItem
                logEvent("click_sound_"+soundItem.name?.replace(" ", "_"))
                val intent = Intent(activity, PlaySoundActivity::class.java)
                intent.putExtra(KEY_SOUND_ITEM_DATA, soundItem)
                AdCache.getInstance().pullCountInterSound()
                if (AdCache.getInstance().canShowInterSound() && AdCache.getInstance().interSound!= null) {
                     AdmobManager.getInstance().showInterstitial(requireActivity(), AdCache.getInstance().interSound, object : AdCallback() {
                         override fun onAdClosed() {
                             super.onAdClosed()
                             AdCache.getInstance().interSound = null
                             startActivity(intent)
                         }

                         override fun onAdFailedToLoad(i: LoadAdError) {
                             super.onAdFailedToLoad(i)
                             AdCache.getInstance().interSound = null
                             startActivity(intent)
                         }
                     })
                } else {
                    startActivity(intent)
                }

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
        if (AdCache.getInstance().interSound == null) {
            AdmobManager.getInstance()
                .loadInterAds(requireActivity(), BuildConfig.inter_sound, object : AdCallback() {
                    override fun onResultInterstitialAd(interstitialAd: InterstitialAd) {
                        super.onResultInterstitialAd(interstitialAd)
                        AdCache.getInstance().interSound = interstitialAd
                    }
                })
        }
    }
}