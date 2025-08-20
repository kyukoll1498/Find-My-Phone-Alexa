package com.alx.findphone.claptofind.flashalert.main.adapter

import android.content.Context
import android.content.res.Resources
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.alx.findphone.claptofind.flashalert.base.BaseAdapter
import com.alx.findphone.claptofind.flashalert.data.model.ItemAlert
import com.alx.findphone.claptofind.flashalert.databinding.ItemScreenAlertBinding
import com.alx.findphone.claptofind.flashalert.utils.constant.Constants
import com.bumptech.glide.Glide

class ScreenAlertAdapter(mList: MutableList<ItemAlert>, context: Context) :
    BaseAdapter<ItemAlert>(mList, context) {
    override fun viewHolder(parent: ViewGroup?, viewType: Int): RecyclerView.ViewHolder {
        val binding = ItemScreenAlertBinding.inflate(LayoutInflater.from(context), parent, false)
        return ItemScreenAlertHolder(binding)
    }

    override fun getItemCount(): Int {
        return mList.size
    }

    private inner class ItemScreenAlertHolder(private val binding: ItemScreenAlertBinding) :
        RecyclerView.ViewHolder(binding.root), View.OnClickListener {
        init {
            itemView.setOnClickListener(this)
        }

        override fun onClick(v: View?) {
            mCallback?.callback(Constants.KEY_DATA, itemView.tag)
        }

        fun loadData(itemAlert: ItemAlert) {
            itemView.tag = itemAlert
            Glide.with(context!!).load(itemAlert.image).into(binding.lavLottie)

            val screenHeight = Resources.getSystem().displayMetrics.heightPixels
            val itemHeight = screenHeight / 3

            val params = itemView.layoutParams
            params?.height = itemHeight
            itemView.layoutParams = params
            binding.viewSelect.visibility = if (itemAlert.isSelected) View.VISIBLE else View.GONE
            binding.ivSelected.visibility = if (itemAlert.isSelected) View.VISIBLE else View.GONE
        }
    }

    override fun onBindView(viewHolder: RecyclerView.ViewHolder?, position: Int) {
        val itemTheme = mList[position]
        if (viewHolder is ItemScreenAlertHolder) {
            viewHolder.loadData(itemTheme)
        }
    }
}