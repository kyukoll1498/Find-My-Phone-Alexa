package com.mtg.tool.findmyphone.main.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.common.control.manager.AdmobManager
import com.mtg.tool.findmyphone.ADAPTER_ADS_TYPE
import com.mtg.tool.findmyphone.ADAPTER_ITEM_TYPE
import com.mtg.tool.findmyphone.ADS_SOUND_TYPE
import com.mtg.tool.findmyphone.BuildConfig
import com.mtg.tool.findmyphone.KEY_SOUND
import com.mtg.tool.findmyphone.R
import com.mtg.tool.findmyphone.base.BaseAdapter
import com.mtg.tool.findmyphone.data.model.SoundItem
import com.mtg.tool.findmyphone.databinding.ItemNativeHolderBinding
import com.mtg.tool.findmyphone.databinding.ItemSoundBinding

class SoundAdapter(mList: List<SoundItem?>?, context: Context?) :
    BaseAdapter<SoundItem?>(mList!!, context) {


    override fun viewHolder(parent: ViewGroup?, viewType: Int): RecyclerView.ViewHolder {
        return if (viewType == ADAPTER_ADS_TYPE) {
            val binding =
                ItemNativeHolderBinding.inflate(LayoutInflater.from(parent!!.context), parent, false)
            NativeViewHolder(binding)
        } else {
            val binding =
                ItemSoundBinding.inflate(LayoutInflater.from(parent!!.context), parent, false)
            SoundViewHolder(binding)
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
            binding.tvName.text = soundItem.name
        }

        override fun onClick(v: View) {
            mCallback?.run { callback(KEY_SOUND, itemView.tag) }
        }
    }

    inner class NativeViewHolder(binding: ItemNativeHolderBinding) :
        RecyclerView.ViewHolder(binding.root), View.OnClickListener {
        init {
            AdmobManager.getInstance().loadNative(
                context,
                BuildConfig.native_sound,
                binding.frAds,
                R.layout.custom_native_ads_item
            )
        }

        override fun onClick(v: View?) {

        }

    }


    override fun getItemViewType(position: Int): Int {
        return if (mList[position]?.type == ADS_SOUND_TYPE) {
            ADAPTER_ADS_TYPE
        } else {
            ADAPTER_ITEM_TYPE
        }
    }
}