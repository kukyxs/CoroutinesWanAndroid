package com.kuky.demo.wan.android.ui.home

import android.os.Bundle
import android.os.Handler
import android.view.View
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.paging.PagedList
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.kuky.demo.wan.android.R
import com.kuky.demo.wan.android.base.BaseFragment
import com.kuky.demo.wan.android.base.OnItemClickListener
import com.kuky.demo.wan.android.base.OnItemLongClickListener
import com.kuky.demo.wan.android.databinding.FragmentHomeBinding
import com.kuky.demo.wan.android.entity.ArticleDetail
import com.kuky.demo.wan.android.ui.websitedetail.WebsiteDetailFragment
import org.jetbrains.anko.alert
import org.jetbrains.anko.noButton
import org.jetbrains.anko.toast
import org.jetbrains.anko.yesButton

/**
 * @author kuky.
 * @description 主页面首页模块界面
 */
class HomeFragment : BaseFragment<FragmentHomeBinding>() {
    companion object {
        private val mHandler = Handler()
    }

    private val mAdapter: HomeArticleAdapter by lazy { HomeArticleAdapter() }

    private val mViewModel: HomeArticleViewModel by lazy {
        ViewModelProviders.of(requireActivity(), HomeArticleModelFactory(HomeArticleRepository()))
            .get(HomeArticleViewModel::class.java)
    }

    override fun getLayoutId(): Int = R.layout.fragment_home

    override fun initFragment(view: View, savedInstanceState: Bundle?) {
        // 绑定 SwipeRefreshLayout 属性
        mBinding.refreshColor = R.color.colorAccent
        mBinding.refreshListener = SwipeRefreshLayout.OnRefreshListener {
            fetchHomeArticleList()
        }

        // 绑定 rv 属性
        mBinding.adapter = mAdapter
        mBinding.itemClick = OnItemClickListener { position, _ ->
            mAdapter.getItemData(position)?.let {
                WebsiteDetailFragment.viewDetail(
                    mNavController,
                    R.id.action_mainFragment_to_websiteDetailFragment,
                    it.link
                )
            }
        }
        mBinding.itemLongClick = OnItemLongClickListener { position, _ ->
            mAdapter.getItemData(position)?.let { article ->
                requireContext().alert("是否收藏「${article.title}」") {
                    yesButton {
                        mViewModel.collectArticle(article.id, {
                            requireContext().toast("收藏成功")
                        }, { message ->
                            requireContext().toast(message)
                        })
                    }
                    noButton { }
                }.show()
            }
            true
        }

        fetchCache()

        // 必要延时有利于提升体验
        mHandler.postDelayed({ fetchHomeArticleList() }, 300)
    }

    private fun fetchCache() {
        mViewModel.fetchHomeArticleCache()
        mViewModel.articles?.observe(this, Observer<PagedList<ArticleDetail>> {
            mAdapter.submitList(it)
        })
    }

    private fun fetchHomeArticleList() {
        mViewModel.fetchHomeArticle()
        mBinding.refreshing = true
        mViewModel.articles?.observe(this, Observer<PagedList<ArticleDetail>> {
            mAdapter.submitList(it)
            mHandler.postDelayed({ mBinding.refreshing = false }, 500)
        })
    }
}