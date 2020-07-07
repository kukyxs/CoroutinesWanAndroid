package com.kuky.demo.wan.android.base

import androidx.fragment.app.Fragment
import androidx.viewpager2.adapter.FragmentStateAdapter

/**
 * @author kuky.
 * @description
 */
class BaseViewPager2FragmentAdapter(
    holder: Fragment, private val childFragments: MutableList<Fragment>
) : FragmentStateAdapter(holder) {

    override fun getItemCount() = childFragments.size

    override fun createFragment(position: Int) = childFragments[position]
}