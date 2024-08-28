package com.mtg.tool.findmyphone.main.adapter

import android.app.Activity
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.common.control.interfaces.AdCallback
import com.common.control.manager.AdmobManager
import com.common.control.manager.AppOpenManager
import com.mtg.tool.findmyphone.ADAPTER_ADS_TYPE
import com.mtg.tool.findmyphone.ADAPTER_ITEM_TYPE
import com.mtg.tool.findmyphone.ADS_SOUND_TYPE
import com.mtg.tool.findmyphone.AdIds
import com.mtg.tool.findmyphone.KEY_SOUND
import com.mtg.tool.findmyphone.R
import com.mtg.tool.findmyphone.base.BaseAdapter
import com.mtg.tool.findmyphone.data.model.SoundItem
import com.mtg.tool.findmyphone.databinding.ItemNativeHolderBinding
import com.mtg.tool.findmyphone.databinding.ItemSoundBinding

class SoundAdapter(mList: List<SoundItem?>?, activity: Activity?) :
    BaseAdapter<SoundItem?>(mList!!, activity) {
    private var adContainer: FrameLayout? = null

    override fun viewHolder(parent: ViewGroup?, viewType: Int): RecyclerView.ViewHolder {
        return if (viewType == ADAPTER_ADS_TYPE) {
            val binding =
                ItemNativeHolderBinding.inflate(
                    LayoutInflater.from(parent!!.context),
                    parent,
                    false
                )
            adContainer = binding.frAds
            NativeViewHolder(binding)
        } else {
            val binding =
                ItemSoundBinding.inflate(LayoutInflater.from(parent!!.context), parent, false)
            SoundViewHolder(binding)
        }
    }

    fun reloadNativeAd() {
        adContainer?.let { container: FrameLayout ->
            Log.d("Refresh", "Refresh Sound Adapter Refresh")
            AdmobManager.getInstance().loadNative(
                context,
                AdIds.native_sound,
                container,
                AdmobManager.NativeAdType.MEDIUM,
                object : AdCallback() {
                    override fun onAdImpression() {
                        super.onAdImpression()
                        com.mtg.tool.findmyphone.consent_dialog.base.EventLogger.firebaseLog(
                            context,
                            "sound_native_view"
                        )
                    }

                    override fun onAdClicked() {
                        super.onAdClicked()
                        com.mtg.tool.findmyphone.consent_dialog.base.EventLogger.firebaseLog(
                            context,
                            "sound_native_click"
                        )
                    }
                }
            )
        }
    }

    override fun onViewRecycled(holder: RecyclerView.ViewHolder) {
        super.onViewRecycled(holder)
        if (holder is NativeViewHolder) {
            adContainer = null
        }
    }

    override fun onBindView(viewHolder: RecyclerView.ViewHolder?, position: Int) {
        val itemSound = mList[position]!!
        if (viewHolder is SoundViewHolder) {
            viewHolder.loadData(itemSound)
        }
    }

    private inner class SoundViewHolder(private val binding: ItemSoundBinding) :
        RecyclerView.ViewHolder(binding.root), View.OnClickListener {
        init {
            itemView.setOnClickListener(this)
        }

        fun loadData(soundItem: SoundItem) {
            itemView.tag = soundItem
            context?.let { Glide.with(it).load(soundItem.image).into(binding.ivImage) }
            context?.let { Glide.with(it).load(soundItem.image).into(binding.ivImage2) }
            if (soundItem.image == R.drawable.avatar_audio_default) {
                binding.cardImage.visibility = View.VISIBLE
                binding.ivImage.visibility = View.GONE
            } else {
                binding.cardImage.visibility = View.GONE
                binding.ivImage.visibility = View.VISIBLE
            }
            binding.tvName.text = soundItem.name
        }

        override fun onClick(v: View) {
            mCallback?.run { callback(KEY_SOUND, itemView.tag) }
        }
    }

    inner class NativeViewHolder(binding: ItemNativeHolderBinding) :
        RecyclerView.ViewHolder(binding.root), View.OnClickListener {
        init {
            Log.d("Refresh", "Sound Adapter Refresh")
            AdmobManager.getInstance().loadNative(
                context,
                AdIds.native_sound,
                binding.frAds,
                AdmobManager.NativeAdType.MEDIUM,
                object : AdCallback() {
                    override fun onAdImpression() {
                        super.onAdImpression()
                        com.mtg.tool.findmyphone.consent_dialog.base.EventLogger.firebaseLog(
                            context,
                            "sound_native_view"
                        )
                    }

                    override fun onAdClicked() {
                        super.onAdClicked()
                        com.mtg.tool.findmyphone.consent_dialog.base.EventLogger.firebaseLog(
                            context,
                            "sound_native_click"
                        )
                    }
                }
            )
            AppOpenManager.getInstance()
                .hideNativeOrBannerWhenShowOpenApp(context as Activity, binding.frAds)
        }

        override fun onClick(v: View?) {}
    }


    override fun getItemViewType(position: Int): Int {
        return if (mList[position]?.type == ADS_SOUND_TYPE) {
            ADAPTER_ADS_TYPE
        } else {
            ADAPTER_ITEM_TYPE
        }
    }
}