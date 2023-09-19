package com.mtg.tool.findmyphone.main.adapter

import android.content.Context
import android.content.res.ColorStateList
import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.mtg.tool.findmyphone.base.BaseAdapter
import com.mtg.tool.findmyphone.data.model.ItemLanguage
import com.mtg.tool.findmyphone.databinding.ItemLanguageBinding
import com.mtg.tool.findmyphone.utils.constant.Constants
import com.mtg.tool.findmyphone.utils.setSize

class LanguageAdapter(mList: List<ItemLanguage?>?, context: Context?) :
    BaseAdapter<ItemLanguage?>(mList!!, context) {
    override fun viewHolder(parent: ViewGroup?, viewType: Int): RecyclerView.ViewHolder {
        val binding =
            ItemLanguageBinding.inflate(LayoutInflater.from(parent!!.context), parent, false)
        return LanguageViewHolder(binding)
    }

    override fun onBindView(viewHolder: RecyclerView.ViewHolder?, position: Int) {
        val itemLanguage = mList[position]!!
        if (viewHolder is LanguageViewHolder) {
            viewHolder.loadData(itemLanguage)
        }
    }

    override fun getItemCount(): Int {
        return mList.size
    }

    private inner class LanguageViewHolder(private val binding: ItemLanguageBinding) :
        RecyclerView.ViewHolder(binding.root), View.OnClickListener {
        init {
            itemView.setOnClickListener(this)
        }

        fun loadData(itemLanguage: ItemLanguage) {
            itemView.tag = itemLanguage
            binding.imgFlag.setImageResource(itemLanguage.imageFlag)
            binding.tvLanguage.text = itemLanguage.name
            binding.rbCheck.setImageResource(itemLanguage.imgSelect)
            binding.tvLanguage.setSize(16)
            if (itemLanguage.colorBackground?.isNotEmpty() == true) {
                itemView.backgroundTintList = ColorStateList.valueOf(Color.parseColor(itemLanguage.colorBackground))
                binding.tvLanguage.setTextColor(Color.parseColor("#FFFFFF"))
            } else {
                itemView.backgroundTintList = null
                binding.tvLanguage.setTextColor(Color.parseColor("#221F29"))
            }
        }

        override fun onClick(v: View) {
            mCallback?.run { callback(Constants.KEY_LANGUAGE, itemView.tag) }
        }
    }
}