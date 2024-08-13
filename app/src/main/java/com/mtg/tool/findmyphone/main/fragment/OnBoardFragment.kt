package com.mtg.tool.findmyphone.main.fragment

import android.util.Log
import android.view.View
import com.bumptech.glide.Glide
import com.common.control.interfaces.AdCallback
import com.common.control.manager.AdmobManager
import com.google.android.gms.ads.nativead.NativeAd
import com.mtg.tool.findmyphone.AdCache
import com.mtg.tool.findmyphone.AdIds
import com.mtg.tool.findmyphone.R
import com.mtg.tool.findmyphone.base.BaseFragment
import com.mtg.tool.findmyphone.consent_dialog.remote_config.RemoteConfigManager
import com.mtg.tool.findmyphone.databinding.FragmentOnboadingBinding
import com.mtg.tool.findmyphone.utils.setSize
import java.util.Objects


class OnBoardFragment(
    private var position: Int = R.drawable.img_inside_1,
    var idImage: Int = R.drawable.img_inside_1,
    var idText: Int = R.string.text_on_boarding_1
) : BaseFragment<FragmentOnboadingBinding>(FragmentOnboadingBinding::inflate) {

    private var isFirstResume = true
    private var isFirstPause = true

    private val onb2NativeAds = arrayListOf(AdIds.ob2_native_high, AdIds.ob2_native)
    private val onb4NativeAds = arrayListOf(AdIds.ob4_native_high, AdIds.ob4_native)
    private val onb5NativeAds = arrayListOf(AdIds.ob5_native_high, AdIds.ob5_native)


    override fun initView() {
        binding.imgInside.setImageResource(idImage)
        binding.tvInside.text = getString(idText)

        when (position) {
            0 -> {
                AdmobManager.getInstance().showNative(
                    context,
                    AdCache.getInstance().ob2NativeHigh,
                    binding.frAd,
                    com.common.control.R.layout.custom_native_ads_2
                )
                Log.d("nativeOB", "ob2NativeHigh")
            }

            1 -> {
                binding.lottie.setVisibility(View.VISIBLE)
                binding.frAd.visibility = View.GONE
            }

            2 -> {
                run {
                    if (Objects.requireNonNull(RemoteConfigManager.instance)
                        !!.isShowNativeFullScreenOnboard
                    ) {
                        binding.llMain.visibility = View.GONE
                        binding.adsContainer.visibility = View.VISIBLE
                        logEvent("complete_lfo1")
                        logEvent("view_lfo2")
                        if (AdCache.getInstance().ob4NativeHigh != null) {
                            Log.d("showFullNative", "ob4NativeHigh1")
                            AdmobManager.getInstance().showNative(
                                context,
                                AdCache.getInstance().ob4NativeHigh,
                                binding.adsContainer,
                                com.common.control.R.layout.custom_full_screen_native_ads
                            )
                        } else if (AdCache.getInstance().ob4NativeHigh1 != null) {
                            Log.d("showFullNative", "ob4NativeHigh1")
                            AdmobManager.getInstance().showNative(
                                context,
                                AdCache.getInstance().ob4NativeHigh1,
                                binding.adsContainer,
                                com.common.control.R.layout.custom_full_screen_native_ads
                            )
                        } else if (AdCache.getInstance().ob4NativeHigh2AndNative != null) {
                            Log.d("showFullNative", "ob4NativeHigh2AndNative")
                            AdmobManager.getInstance().showNative(
                                context,
                                AdCache.getInstance().ob4NativeHigh2AndNative,
                                binding.adsContainer,
                                com.common.control.R.layout.custom_full_screen_native_ads
                            )
                        }
                    } else {
                        Glide.with(requireContext()).load(idImage)
                            .into(binding.imgInside)
                        binding.tvInside.text = getString(idText)
                    }
                }
                run {
                    Glide.with(requireContext()).load(idImage)
                        .into(binding.imgInside)
                    binding.tvInside.text = getString(idText)
                }
            }

            3 -> {
                AdmobManager.getInstance().showNative(
                    context,
                    AdCache.getInstance().ob5NativeHigh,
                    binding.frAd,
                    com.common.control.R.layout.custom_native_ads_2
                )
                Log.d("nativeOB", "ob5NativeHigh")
            }
        }
    }

    override fun addEvent() {

    }

    override fun onResume() {
        super.onResume()
        if (isFirstResume) {
            isFirstResume = false
            logEvent("view_ob" + (position + 2))
            return
        }
        when (position) {
            0 -> {
                AdmobManager.getInstance().preloadAlternateNative(
                    requireActivity(),
                    onb2NativeAds,
                    object : AdCallback() {
                        override fun onNativeAds(nativeAd: NativeAd?) {
                            super.onNativeAds(nativeAd)
                            AdmobManager.getInstance().showNative(
                                requireActivity(),
                                nativeAd,
                                binding.frAd,
                                com.common.control.R.layout.custom_native_ads_2
                            )
                        }
                    })
            }

            1 -> {

            }

            2 -> {
                AdmobManager.getInstance().preloadFullScreenAlternateNative(
                    requireActivity(),
                    onb4NativeAds,
                    object : AdCallback() {
                        override fun onNativeAds(nativeAd: NativeAd?) {
                            super.onNativeAds(nativeAd)
                            AdmobManager.getInstance().showNative(
                                requireActivity(), nativeAd, binding.adsContainer,
                                com.common.control.R.layout.custom_full_screen_native_ads
                            )
                        }
                    })
            }

            3 -> {
                AdmobManager.getInstance().preloadAlternateNative(
                    requireActivity(),
                    onb5NativeAds,
                    object : AdCallback() {
                        override fun onNativeAds(nativeAd: NativeAd?) {
                            super.onNativeAds(nativeAd)
                            AdmobManager.getInstance().showNative(
                                requireActivity(),
                                nativeAd,
                                binding.frAd,
                                com.common.control.R.layout.custom_native_ads_2
                            )
                        }
                    })
            }
        }
    }

    override fun onPause() {
        super.onPause()
        if (isFirstPause) {
            isFirstPause = false
            logEvent("complete_onb" + (position + 2))
        }
    }

}