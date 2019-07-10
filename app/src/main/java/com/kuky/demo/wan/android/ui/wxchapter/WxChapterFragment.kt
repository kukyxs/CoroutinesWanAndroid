package com.kuky.demo.wan.android.ui.wxchapter

import android.os.Bundle
import android.view.View
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import com.kuky.demo.wan.android.R
import com.kuky.demo.wan.android.base.BaseFragment
import com.kuky.demo.wan.android.data.wxchapter.WxChapterRepository
import com.kuky.demo.wan.android.databinding.FragmentWxChapterBinding

/**
 * @author kuky.
 * @description 首页公众号模块界面
 */
class WxChapterFragment : BaseFragment<FragmentWxChapterBinding>() {
    private val viewModel by lazy {
        ViewModelProviders.of(this, WxChapterFactory(WxChapterRepository()))
            .get(WxChapterViewModel::class.java)
    }

    override fun getLayoutId(): Int = R.layout.fragment_wx_chapter

    override fun initFragment(view: View, savedInstanceState: Bundle?) {
        viewModel.getWxChapter()
        val adapter = WxChapterAdapter(this.context, viewModel.mData)
        mBinding.rcvChapter.adapter = adapter
        viewModel.isRefresh.observe(this, Observer {
            if (it) adapter.notifyDataSetChanged()
        })
    }
}