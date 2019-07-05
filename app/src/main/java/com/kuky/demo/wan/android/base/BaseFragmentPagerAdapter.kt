package com.kuky.demo.wan.android.base

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentPagerAdapter

/**
 * @author kuky.
 * @description ViewPager + Fragment Adapter 基类
 */
class BaseFragmentPagerAdapter(fm: FragmentManager, fragments: ArrayList<out Fragment>, titles: Array<String>) :
    FragmentPagerAdapter(fm) {

    private var mFragments = fragments
    private var mTitles = titles

    override fun getItem(position: Int): Fragment = mFragments[position]

    override fun getCount(): Int = mTitles.size

    override fun getPageTitle(position: Int): CharSequence? =
        if (mTitles.isNullOrEmpty()) super.getPageTitle(position) else mTitles[position]
}