package com.kuky.demo.wan.android.ui.home

import android.os.Bundle
import android.view.View
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.paging.PagedList
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.kuky.demo.wan.android.R
import com.kuky.demo.wan.android.base.*
import com.kuky.demo.wan.android.databinding.FragmentHomeArticleBinding
import com.kuky.demo.wan.android.entity.ArticleDetail
import com.kuky.demo.wan.android.ui.collection.CollectionFactory
import com.kuky.demo.wan.android.ui.collection.CollectionRepository
import com.kuky.demo.wan.android.ui.collection.CollectionViewModel
import com.kuky.demo.wan.android.ui.websitedetail.WebsiteDetailFragment
import com.kuky.demo.wan.android.ui.widget.ErrorReload
import org.jetbrains.anko.alert
import org.jetbrains.anko.noButton
import org.jetbrains.anko.toast
import org.jetbrains.anko.yesButton

/**
 * @author kuky.
 * @description 主页面首页模块界面
 */
class HomeArticleFragment : BaseFragment<FragmentHomeArticleBinding>() {

    private val mAdapter: HomeArticleAdapter by lazy { HomeArticleAdapter() }

    private val mViewModel: HomeArticleViewModel by lazy {
        ViewModelProvider(requireActivity(), HomeArticleModelFactory(HomeArticleRepository()))
            .get(HomeArticleViewModel::class.java)
    }
    private val mCollectionViewModel by lazy {
        ViewModelProvider(requireActivity(), CollectionFactory(CollectionRepository()))
            .get(CollectionViewModel::class.java)
    }

    override fun getLayoutId(): Int = R.layout.fragment_home_article

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
                requireContext().alert(if (article.collect) "「${article.title}」已收藏" else " 是否收藏 「${article.title}」") {
                    yesButton {
                        if (!article.collect) mCollectionViewModel.collectArticle(article.id, {
                            mViewModel.articles?.value?.get(position)?.collect = true
                            requireContext().toast("收藏成功")
                        }, { message ->
                            requireContext().toast(message)
                        })
                    }
                    if (!article.collect) noButton { }
                }.show()
            }
            true
        }

        // 双击回顶部
        mBinding.gesture = DoubleClickListener(null, {
            mBinding.articleList.scrollToTop()
        })

        mBinding.errorReload = ErrorReload {
            fetchHomeArticleList()
        }

        fetchHomeArticleList()
    }

    private fun fetchHomeArticleList() {
        mViewModel.fetchHomeArticle { code, _ ->
            when (code) {
                PAGING_THROWABLE_LOAD_CODE_INITIAL -> {
                    mBinding.errorStatus = true
                    mBinding.indicator = resources.getString(R.string.text_place_holder)
                }

                PAGING_THROWABLE_LOAD_CODE_AFTER -> requireContext().toast("加载更多数据出错啦~请检查网络")
            }
        }

        mBinding.refreshing = true
        mBinding.errorStatus = false
        mViewModel.articles?.observe(this, Observer<PagedList<ArticleDetail>> {
            mAdapter.submitList(it)
            mBinding.indicator = resources.getString(R.string.blog_articles)
            delayLaunch(1000) { mBinding.refreshing = false }
        })
    }
}