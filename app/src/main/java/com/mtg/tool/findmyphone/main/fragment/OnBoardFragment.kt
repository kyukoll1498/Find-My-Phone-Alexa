package com.mtg.tool.findmyphone.main.fragment

import android.view.View
import android.widget.Toast
import com.bumptech.glide.Glide
import com.common.control.interfaces.AdCallback
import com.common.control.manager.AdmobManager
import com.google.firebase.remoteconfig.FirebaseRemoteConfig
import com.google.firebase.remoteconfig.FirebaseRemoteConfigSettings
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
            0, 1 -> {}
            2 -> {
                run {
                    if (Objects.requireNonNull(RemoteConfigManager.instance)
                        !!.isShowNativeFullScreenOnboard
                    ) {
                        binding.llMain.visibility = View.GONE
                        binding.adsContainer.visibility = View.VISIBLE
                        AdmobManager.getInstance().loadNativeFullScreenWithCallback(
                            requireContext(),
                            BuildConfig.native_language,
                            binding.adsContainer,
                            object : AdCallback() {
                                override fun onAdImpression() {
                                    super.onAdImpression()
                                    EventLogger.firebaseLog(
                                        requireActivity(),
                                        "ads_impression_native_home_guide_fullscreen"
                                    )
                                }

                                override fun onAdClicked() {
                                    super.onAdClicked()
                                    EventLogger.firebaseLog(
                                        requireActivity(),
                                        "ads_clicked_native_home_guide_fullscreen"
                                    )
                                }
                            })
                    } else {
                        Glide.with(requireContext()).load(idImage)
                            .into(binding.imgInside)
                        binding.tvInside.setText(getString(idText))
                    }
                }
                run {
                    Glide.with(requireContext()).load(idImage)
                        .into(binding.imgInside)
                    binding.tvInside.text = getString(idText)
                }
            }

            3 -> {
                Glide.with(requireContext()).load(idImage).into(binding.imgInside)
                binding.tvInside.text = getString(idText)
            }
        }
    }

    override fun addEvent() {

    }

}