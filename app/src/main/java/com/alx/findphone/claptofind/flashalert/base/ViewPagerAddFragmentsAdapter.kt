package com.alx.findphone.claptofind.flashalert.base

import android.content.Context
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.Lifecycle
import androidx.viewpager2.adapter.FragmentStateAdapter

class ViewPagerAddFragmentsAdapter(
    private val context: Context,
    fragmentManager: FragmentManager,
    lifecycle: Lifecycle
) : FragmentStateAdapter(fragmentManager, lifecycle) {

    private var pageSize = 3
    val mFragmentList: MutableList<Fragment> = ArrayList()

    init {
        updatePageSize()
    }

    private fun updatePageSize() {
//        val isNetworkAvailable = InternetUtil.isNetworkAvailable(context)
//        val isShowNativeFullScreenOnboard = RemoteConfigManager.instance!!.isShowNativeFullScreenOnboard
//
//        pageSize = if (!isShowNativeFullScreenOnboard || !isNetworkAvailable) {
//            3
//        } else {
//            4
//        }
    }


    override fun createFragment(position: Int): Fragment {
        return mFragmentList[position]
    }

    override fun getItemCount(): Int {
        updatePageSize()
        return pageSize
    }

    fun addFrag(fragment: Fragment) {
        mFragmentList.add(fragment)
    }

    fun removeFag(pos: Int) {
        if (pos in mFragmentList.indices)
            mFragmentList.removeAt(pos)
    }

    fun clear() {
        mFragmentList.clear()
    }

    override fun getItemId(position: Int): Long {
        return mFragmentList[position].hashCode().toLong()
    }

    override fun containsItem(itemId: Long): Boolean {
        val pageIds = mFragmentList.map { it.hashCode().toLong() }
        return pageIds.contains(itemId)
    }
}
