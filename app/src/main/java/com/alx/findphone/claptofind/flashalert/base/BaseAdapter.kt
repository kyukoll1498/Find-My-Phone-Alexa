package com.alx.findphone.claptofind.flashalert.base

import android.content.Context
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.common.control.base.OnActionCallback

abstract class BaseAdapter<T> : RecyclerView.Adapter<RecyclerView.ViewHolder> {
    var mCallback: OnActionCallback? = null
    var mList: List<T> = ArrayList()
    var context: Context? = null

    constructor()
    constructor(mList: List<T>) {
        this.mList = mList
    }

    constructor(mList: List<T>, context: Context?) {
        this.mList = mList
        this.context = context
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        context = parent.context
        return viewHolder(parent, viewType)
    }

    protected abstract fun viewHolder(parent: ViewGroup?, viewType: Int): RecyclerView.ViewHolder
    override fun onBindViewHolder(viewHolder: RecyclerView.ViewHolder, position: Int) {
        onBindView(viewHolder, position)
    }

    protected abstract fun onBindView(viewHolder: RecyclerView.ViewHolder?, position: Int)
    override fun getItemCount(): Int {
        return mList.size
    }

    fun submitList(mList: List<T>) {
        this.mList = mList
        notifyDataSetChanged()
    }
}