package com.alx.findphone.claptofind.flashalert.main.adapter

import android.app.Activity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import androidx.recyclerview.widget.RecyclerView
import com.alx.findphone.claptofind.flashalert.ADAPTER_ADS_TYPE
import com.alx.findphone.claptofind.flashalert.ADAPTER_ITEM_TYPE
import com.alx.findphone.claptofind.flashalert.ADS_SOUND_TYPE
import com.alx.findphone.claptofind.flashalert.AdIds
import com.alx.findphone.claptofind.flashalert.KEY_SOUND
import com.alx.findphone.claptofind.flashalert.R
import com.alx.findphone.claptofind.flashalert.base.BaseAdapter
import com.alx.findphone.claptofind.flashalert.consent_dialog.base.EventLogger
import com.alx.findphone.claptofind.flashalert.data.model.SoundItem
import com.alx.findphone.claptofind.flashalert.databinding.ItemNativeHolderBinding
import com.alx.findphone.claptofind.flashalert.databinding.ItemSoundBinding
import com.alx.findphone.claptofind.flashalert.utils.hide
import com.alx.findphone.claptofind.flashalert.utils.show
import com.bumptech.glide.Glide
import com.common.control.interfaces.AdCallback
import com.common.control.manager.AdmobManager
import com.google.android.gms.ads.nativead.NativeAd

class SoundAdapter(
    mList: List<SoundItem?>?, activity: Activity?, private val isFirstLoad: Boolean? = null, private val showItemName: Boolean = true, private var itemWidth: Int = -1
) : BaseAdapter<SoundItem?>(mList!!, activity) {
    private var adContainer: FrameLayout? = null
    private val nativeAds by lazy {
        arrayListOf(AdIds.native_sound_high, AdIds.native_sound)
    }

    override fun viewHolder(parent: ViewGroup?, viewType: Int): RecyclerView.ViewHolder {
        return if (viewType == ADAPTER_ADS_TYPE) {
            val binding = ItemNativeHolderBinding.inflate(
                LayoutInflater.from(parent!!.context), parent, false
            )
            adContainer = binding.frAds
            NativeViewHolder(binding)
        } else {
            val binding = ItemSoundBinding.inflate(LayoutInflater.from(parent!!.context), parent, false)
            SoundViewHolder(binding)
        }
    }

    fun reloadNativeAd() {
        adContainer?.let { container: FrameLayout ->
            AdmobManager.getInstance().preloadAlternateNative(
                context, nativeAds, object : AdCallback() {
                    override fun onNativeAds(nativeAd: NativeAd?) {
                        super.onNativeAds(nativeAd)
                        AdmobManager.getInstance().showNative(context, nativeAd, container, AdmobManager.NativeAdType.MEDIUM)
                    }

                    override fun onAdImpression() {
                        super.onAdImpression()
                        EventLogger.firebaseLog(
                            context, "sound_native_view"
                        )
                    }

                    override fun onAdClicked() {
                        super.onAdClicked()
                        EventLogger.firebaseLog(
                            context, "sound_native_click"
                        )
                    }
                })
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

    private inner class SoundViewHolder(private val binding: ItemSoundBinding) : RecyclerView.ViewHolder(binding.root), View.OnClickListener {
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
            if (soundItem.isSelected) {
                binding.ctlContainer.setBackgroundResource(R.drawable.bg_selected_sound)
                binding.ivChecked.show()
            } else {
                binding.ctlContainer.setBackgroundResource(R.drawable.bg_unselected_sound)
                binding.ivChecked.hide()
            }

            if (showItemName) {
                binding.tvName.visibility = View.VISIBLE
                binding.tvName.text = soundItem.name
            } else {
                binding.tvName.visibility = View.GONE
            }

            if (itemWidth > 0) {
                val layoutParams = itemView.layoutParams
                layoutParams.width = itemWidth
                itemView.layoutParams = layoutParams
            }

            binding.tvName.text = soundItem.name
        }

        override fun onClick(v: View) {
            mCallback?.run { callback(KEY_SOUND, itemView.tag) }
        }
    }

    inner class NativeViewHolder(binding: ItemNativeHolderBinding) : RecyclerView.ViewHolder(binding.root), View.OnClickListener {
        init {
            if (isFirstLoad == true) {
                reloadNativeAd()
            }
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