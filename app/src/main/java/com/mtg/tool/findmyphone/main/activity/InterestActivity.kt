package com.mtg.tool.findmyphone.main.activity

import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.graphics.Paint
import android.widget.Button
import com.common.control.interfaces.AdCallback
import com.common.control.manager.AdmobManager
import com.common.control.utils.InternetUtil
import com.google.android.gms.ads.nativead.NativeAd
import com.mtg.tool.findmyphone.AdCache
import com.mtg.tool.findmyphone.AdIds
import com.mtg.tool.findmyphone.R
import com.mtg.tool.findmyphone.base.BaseActivity
import com.mtg.tool.findmyphone.data.preferences.SharedPrefs
import com.mtg.tool.findmyphone.databinding.ActivityInterestBinding
import com.mtg.tool.findmyphone.utils.constant.Constants
import com.mtg.tool.findmyphone.utils.setSize

class InterestActivity :
    BaseActivity<ActivityInterestBinding>(ActivityInterestBinding::inflate) {
    private lateinit var selectedInterests: MutableList<String>
    val onb2NativeAds = arrayListOf(AdIds.ob2_native_high, AdIds.ob2_native)
    val onb1NativeAds = arrayListOf(AdIds.ob1_native_high, AdIds.ob1_native)
    var isFirstResume = true

    override fun binding() {
        isFullScreen = false
        super.binding()
    }

    companion object {
        fun start(context: Context) {
            val intent = Intent(context, InterestActivity::class.java)
            context.startActivity(intent)
        }
    }

    override fun initView() {
        logEvent("onboard1_view")
        AdmobManager.getInstance().showNative(
            this,
            AdCache.getInstance().ob1Native,
            binding.frAd,
            AdmobManager.NativeAdType.BIG
        )
        if (AdCache.getInstance().ob4NativeHigh == null) {
            if (InternetUtil.isNetworkAvailable(this)) {
                AdmobManager.getInstance().preloadFullScreenNative(
                    this@InterestActivity,
                    AdIds.ob4_native_high1,
                    object : AdCallback() {
                        override fun onNativeAds(nativeAd: NativeAd?) {
                            super.onNativeAds(nativeAd)
                            AdCache.getInstance().ob4NativeHigh1 =
                                nativeAd
                        }

                        override fun onAdImpression() {
                            super.onAdImpression()
                            logEvent("onboard4_native_view")
                        }

                        override fun onAdClicked() {
                            super.onAdClicked()
                            logEvent("onboard4_native_click")

                        }
                    })
            }
        }
        if (InternetUtil.isNetworkAvailable(this)) {
            AdmobManager.getInstance()
                .preloadAlternateNative(this, onb2NativeAds, object : AdCallback() {
                    override fun onNativeAds(nativeAd: NativeAd?) {
                        super.onNativeAds(nativeAd)
                        AdCache.getInstance().ob2NativeHigh =
                            nativeAd
                    }

                    override fun onAdImpression() {
                        super.onAdImpression()
                        logEvent("onboard2_native_view")
                    }

                    override fun onAdClicked() {
                        super.onAdClicked()
                        logEvent("onboard2_native_click")

                    }
                })
        }
        selectedInterests = mutableListOf()

        // Initialize buttons
        val buttons = listOf(
            binding.btnHome,
            binding.btnBedroom,
            binding.btnSofa,
            binding.btnWork,
            binding.btnGym,
            binding.btnFriendHouse,
            binding.btnCafe,
            binding.btnOutdoors,
            binding.btnSchool,
            binding.btnCar,
            binding.btnHotel,
            binding.btnEvents,
        )

        buttons.forEach { button ->
            button.setOnClickListener {
                toggleSelection(button)
            }
        }

    }

    override fun addEvent() {
        binding.tvNext.setOnClickListener {
            logEvent("onboard1_next_click")
            saveSelectedInterests()
        }
    }

    private fun toggleSelection(button: Button) {
        val isSelected = selectedInterests.contains(button.text.toString())
        if (isSelected) {
            selectedInterests.remove(button.text.toString())
            button.setBackgroundResource(R.drawable.button_unselected)
            button.setTextColor(Color.BLACK)
        } else {
            selectedInterests.add(button.text.toString())
            button.setBackgroundResource(R.drawable.button_selected)
            button.setTextColor(Color.WHITE)
        }
    }

    private fun saveSelectedInterests() {
//        val intent = Intent(this, OnBoardActivity::class.java)
//        intent.putStringArrayListExtra("selectedInterests", ArrayList(selectedInterests))
//        startActivity(intent)
        OnBoardActivity.start(this)
        finish()
    }

    override fun onResume() {
        super.onResume()
        if (!isFirstResume) {
            AdmobManager.getInstance()
                .preloadAlternateNative(this, onb1NativeAds, object : AdCallback() {
                    override fun onNativeAds(nativeAd: NativeAd?) {
                        super.onNativeAds(nativeAd)
                        AdmobManager.getInstance().showNative(
                            this@InterestActivity,
                            nativeAd,
                            binding.frAd,
                            AdmobManager.NativeAdType.BIG
                        )
                    }

                    override fun onAdImpression() {
                        super.onAdImpression()
                        logEvent("onboard1_native_view")
                    }

                    override fun onAdClicked() {
                        super.onAdClicked()
                        logEvent("onboard1_native_click")

                    }
                })
        } else {
            isFirstResume = false
        }
    }
}