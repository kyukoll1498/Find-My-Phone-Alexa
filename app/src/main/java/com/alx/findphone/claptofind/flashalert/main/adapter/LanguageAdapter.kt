package com.alx.findphone.claptofind.flashalert.main.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.alx.findphone.claptofind.flashalert.base.BaseAdapter
import com.alx.findphone.claptofind.flashalert.data.model.ItemLanguage
import com.alx.findphone.claptofind.flashalert.databinding.ItemLanguageBinding
import com.alx.findphone.claptofind.flashalert.utils.constant.Constants
import com.alx.findphone.claptofind.flashalert.utils.setSize

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
//                itemView.backgroundTintList = ColorStateList.valueOf(Color.parseColor(itemLanguage.colorBackground))
//                binding.tvLanguage.setTextColor("#FFFFFF".toColorInt())
            } else {
//                itemView.backgroundTintList = null
//                binding.tvLanguage.setTextColor("#221F29".toColorInt())
            }
        }

        override fun onClick(v: View) {
            mCallback?.run { callback(Constants.KEY_LANGUAGE, itemView.tag) }
        }
    }
}