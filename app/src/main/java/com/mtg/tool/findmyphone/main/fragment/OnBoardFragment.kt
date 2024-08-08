package com.mtg.tool.findmyphone.main.fragment

import android.util.Log
import android.view.View
import android.widget.Toast
import com.bumptech.glide.Glide
import com.common.control.interfaces.AdCallback
import com.common.control.manager.AdmobManager
import com.common.control.manager.AppOpenManager
import com.google.firebase.remoteconfig.FirebaseRemoteConfig
import com.google.firebase.remoteconfig.FirebaseRemoteConfigSettings
import com.mtg.tool.findmyphone.AdCache
import com.mtg.tool.findmyphone.BuildConfig
import com.mtg.tool.findmyphone.R
import com.mtg.tool.findmyphone.base.BaseFragment
import com.mtg.tool.findmyphone.consent_dialog.base.EventLogger
import com.mtg.tool.findmyphone.consent_dialog.remote_config.RemoteConfigManager
import com.mtg.tool.findmyphone.databinding.FragmentOnboadingBinding
import com.mtg.tool.findmyphone.utils.setSize
import java.util.Objects


class OnBoardFragment(
    private var position: Int = R.drawable.img_inside_1,
    var idImage: Int = R.drawable.img_inside_1,
    var idText: Int = R.string.text_on_boarding_1
) : BaseFragment<FragmentOnboadingBinding>(FragmentOnboadingBinding::inflate) {


    override fun initView() {
        val config = FirebaseRemoteConfig.getInstance()
        val configSettings =
            FirebaseRemoteConfigSettings.Builder().setMinimumFetchIntervalInSeconds(3600).build()
        config.setConfigSettingsAsync(configSettings)
        config.setDefaultsAsync(R.xml.default_config)
        config.fetchAndActivate().addOnCompleteListener { task ->
            if (task.isSuccessful) {
            }
        }

        binding.imgInside.setImageResource(idImage)
        binding.tvInside.text = getString(idText)
        binding.tvInside.setSize(20)

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
                        if (AdCache.getInstance().ob4NativeHigh1 != null) {
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

}