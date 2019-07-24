package com.kuky.demo.wan.android.ui.wxchapter

import android.os.Bundle
import android.os.Handler
import android.view.View
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.kuky.demo.wan.android.R
import com.kuky.demo.wan.android.base.BaseFragment
import com.kuky.demo.wan.android.base.OnItemClickListener
import com.kuky.demo.wan.android.databinding.FragmentWxChapterBinding

/**
 * @author kuky.
 * @description 首页公众号模块界面
 */
class WxChapterFragment : BaseFragment<FragmentWxChapterBinding>() {
    companion object {
        private val mHandler = Handler()
    }

    private val mViewModel by lazy {
        ViewModelProviders.of(this, WxChapterFactory(WxChapterRepository()))
            .get(WxChapterViewModel::class.java)
    }
    private val mAdapter by lazy { WxChapterAdapter(null) }

    override fun getLayoutId(): Int = R.layout.fragment_wx_chapter

    override fun initFragment(view: View, savedInstanceState: Bundle?) {
        mBinding.refreshColor = R.color.colorAccent
        mBinding.refreshListener = SwipeRefreshLayout.OnRefreshListener {
            fetchWxChapter()
        }

        mViewModel.getWxChapter()
        mBinding.rcvChapter.adapter = mAdapter
        mBinding.listener = OnItemClickListener { position, _ ->
            mAdapter.getItemData(position)?.let {
                mNavController.navigate(R.id.action_mainFragment_to_wxChapterListFragment,
                    Bundle().apply
                    {
                        putInt("id", it.id)
                    })
            }
        }
        fetchWxChapter()
    }

    private fun fetchWxChapter() {
        mViewModel.getWxChapter()
        mBinding.refreshing = true
        mViewModel.mData.observe(this, Observer {
            mAdapter.update(it)
            mHandler.postDelayed({ mBinding.refreshing = false }, 500)
        })
    }
}