package com.mtg.tool.findmyphone.main.fragment

import android.app.Activity
import android.text.Spannable
import android.text.SpannableString
import android.text.style.ForegroundColorSpan
import android.view.View
import androidx.core.content.ContextCompat
import com.common.control.manager.AdmobManager
import com.common.control.manager.AppOpenManager
import com.mtg.tool.findmyphone.BuildConfig
import com.mtg.tool.findmyphone.R
import com.mtg.tool.findmyphone.base.BaseFragment
import com.mtg.tool.findmyphone.databinding.FragmentHomeBinding


class HomeFragment : BaseFragment<FragmentHomeBinding>(FragmentHomeBinding::inflate) {

    private var isCircleActiveVisible = false

    override fun initView() {
        changeColorText()
    }

    override fun addEvent() {
        clickClap()
    }

    private fun changeColorText() {
        val activationText = getString(R.string.activation_text)
        val spannableString = SpannableString(activationText)
        val colorGreen = ContextCompat.getColor(requireContext(), R.color.txt_inactive2)
        spannableString.setSpan(ForegroundColorSpan(colorGreen), 29, 34, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
        spannableString.setSpan(ForegroundColorSpan(colorGreen), 9, 25, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
        binding.tvInactive.text = spannableString
    }

    override fun loadAds() {
        super.loadAds()
        AdmobManager.getInstance().loadNative(context, BuildConfig.native_language, binding.frAd, R.layout.custom_native_language)
        AppOpenManager.getInstance().hideNativeOrBannerWhenShowOpenApp(context as Activity?, binding.frAd)
    }

    private fun clickClap() {
        val visible = View.VISIBLE
        val invisible = View.INVISIBLE
        binding.clClickTab.setOnClickListener {
            isCircleActiveVisible = !isCircleActiveVisible
            if (isCircleActiveVisible) {
                binding.apply {
                    ivCircleActive.visibility = visible
                    txtActive.visibility = visible
                    txtInactive.visibility =invisible
                    llTvInactive.visibility = visible
                    tvInactive.visibility = invisible
                }

            } else {
                binding.apply {
                    ivCircleActive.visibility = invisible
                    txtInactive.visibility = visible
                    txtActive.visibility = invisible
                    tvInactive.visibility = visible
                    llTvInactive.visibility = invisible
                }
            }
        }
    }
}