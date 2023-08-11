package com.mtg.tool.findmyphone.main.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.common.control.dialog.PermissionStorageDialog.callback
import com.mtg.tool.findmyphone.base.BaseAdapter
import com.mtg.tool.findmyphone.data.model.ItemLanguage
import com.mtg.tool.findmyphone.data.model.SoundItem
import com.mtg.tool.findmyphone.databinding.ItemLanguageBinding
import com.mtg.tool.findmyphone.databinding.ItemSoundBinding
import com.mtg.tool.findmyphone.utils.constant.Constants
import com.mtg.tool.findmyphone.utils.setSize

class SoundAdapter(mList: List<SoundItem?>?, context: Context?) :
    BaseAdapter<SoundItem?>(mList!!, context) {




    override fun viewHolder(parent: ViewGroup?, viewType: Int): RecyclerView.ViewHolder {
        val binding =
            ItemSoundBinding.inflate(LayoutInflater.from(parent!!.context), parent, false)
        return SoundViewHolder(binding)
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
            mCallback?.run { callback(Constants.KEY_LANGUAGE, itemView.tag) }
        }
    }
}