package com.kuky.demo.wan.android.ui.wxchapterlist


import android.os.Bundle
import android.os.Handler
import android.view.View
import androidx.lifecycle.Observer
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.kuky.demo.wan.android.R
import com.kuky.demo.wan.android.base.BaseFragment
import com.kuky.demo.wan.android.base.OnItemClickListener
import com.kuky.demo.wan.android.databinding.FragmentWxChapterListBinding
import com.kuky.demo.wan.android.ui.websitedetail.WebsiteDetailFragment

/**
 * @author Taonce.
 * @description 公众号作者对应的文章列表页
 */
class WxChapterListFragment : BaseFragment<FragmentWxChapterListBinding>() {
    companion object {
        private val mHandler = Handler()
    }

    private val mAdapter by lazy { WxChapterListAdapter() }
    private val mViewMode by lazy { getViewModel(WxChapterListViewModel::class.java) }

    override fun getLayoutId(): Int = R.layout.fragment_wx_chapter_list

    override fun initFragment(view: View, savedInstanceState: Bundle?) {
        val id = arguments?.getInt("id")

        mBinding.refreshColor = R.color.colorAccent
        mBinding.refreshListener = SwipeRefreshLayout.OnRefreshListener {
            fetchWxChapterList(id)
        }

        mBinding.adapter = mAdapter
        mBinding.listener = OnItemClickListener { position, _ ->
            mAdapter.getItemData(position)?.let {
                WebsiteDetailFragment.viewDetail(
                    mNavController,
                    R.id.action_wxChapterListFragment_to_websiteDetailFragment,
                    it.link
                )
            }
        }
        fetchWxChapterList(id)
    }

    private fun fetchWxChapterList(id: Int?) {
        mBinding.refreshing = true
        mViewMode.fetchResult(id ?: 0).observe(this, Observer {
            mAdapter.submitList(it)
            mHandler.postDelayed({ mBinding.refreshing = false }, 500)
        })
    }
}
