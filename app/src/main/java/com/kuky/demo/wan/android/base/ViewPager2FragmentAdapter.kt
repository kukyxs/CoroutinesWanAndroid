package com.kuky.demo.wan.android.base

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.viewpager2.adapter.FragmentStateAdapter

/**
 * @author kuky.
 * @description
 */
class ViewPager2FragmentAdapter : FragmentStateAdapter {
    private val mChildFragments = mutableListOf<Fragment>()

    constructor(holder: Fragment, childFragments: MutableList<Fragment>) : super(holder) {
        mChildFragments.clear()
        mChildFragments.addAll(childFragments)
    }

    constructor(holder: FragmentActivity, childFragments: MutableList<Fragment>) : super(holder) {
        mChildFragments.clear()
        mChildFragments.addAll(childFragments)
    }

    override fun getItemCount() = mChildFragments.size

    override fun createFragment(position: Int) = mChildFragments[position]
}