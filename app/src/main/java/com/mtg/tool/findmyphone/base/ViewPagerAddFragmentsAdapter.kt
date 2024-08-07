package com.mtg.tool.findmyphone.base

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.Lifecycle
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.mtg.tool.findmyphone.consent_dialog.remote_config.RemoteConfigManager

class ViewPagerAddFragmentsAdapter(fragmentManager: FragmentManager, lifecycle: Lifecycle) :
    FragmentStateAdapter(fragmentManager, lifecycle) {
    private var pageSize = 4
    val mFragmentList: MutableList<Fragment> = ArrayList()

    init {
        if (!RemoteConfigManager.instance!!.isShowNativeFullScreenOnboard) {
            pageSize = 3
        }
    }

    override fun createFragment(position: Int): Fragment {
        return mFragmentList[position]
    }

    override fun getItemCount(): Int {
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
        return mFragmentList[position].hashCode().toLong() // make sure notifyDataSetChanged() works
    }

    override fun containsItem(itemId: Long): Boolean {
        val pageIds = mFragmentList.map { it.hashCode().toLong() }
        return pageIds.contains(itemId)
    }
}