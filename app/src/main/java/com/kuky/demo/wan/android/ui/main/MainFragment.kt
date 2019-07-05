package com.kuky.demo.wan.android.ui.main

import android.os.Bundle
import android.view.View
import com.kuky.demo.wan.android.R
import com.kuky.demo.wan.android.base.BaseFragment
import com.kuky.demo.wan.android.base.BaseFragmentPagerAdapter
import com.kuky.demo.wan.android.databinding.FragmentMainBinding
import com.kuky.demo.wan.android.ui.home.HomeFragment
import com.kuky.demo.wan.android.ui.hotproject.HotProjectFragment
import com.kuky.demo.wan.android.ui.projectcategory.ProjectCategoryFragment
import com.kuky.demo.wan.android.ui.system.KnowledgeSystemFragment
import com.kuky.demo.wan.android.ui.wxchapter.WxChapterFragment
import com.kuky.demo.wan.android.utils.GalleryTransformer
import kotlinx.android.synthetic.main.fragment_main.*

/**
 * @author kuky.
 * @description 主页面 fragment 持有者
 */
class MainFragment : BaseFragment<FragmentMainBinding>() {

    private lateinit var mAdapter: BaseFragmentPagerAdapter

    override fun getLayoutId(): Int = R.layout.fragment_main

    override fun initFragment(view: View, savedInstanceState: Bundle?) {

        mAdapter = BaseFragmentPagerAdapter(
            childFragmentManager, arrayListOf(
                HomeFragment(),
                HotProjectFragment(),
                KnowledgeSystemFragment(),
                WxChapterFragment(),
                ProjectCategoryFragment()
            )
        )

        main_page.adapter = mAdapter
        main_page.offscreenPageLimit = mAdapter.count
        main_page.setPageTransformer(true, GalleryTransformer())
    }
}