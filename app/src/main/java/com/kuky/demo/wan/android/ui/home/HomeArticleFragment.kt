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
import com.kuky.demo.wan.android.ui.collection.CollectionModelFactory
import com.kuky.demo.wan.android.ui.collection.CollectionRepository
import com.kuky.demo.wan.android.ui.collection.CollectionViewModel
import com.kuky.demo.wan.android.ui.main.MainModelFactory
import com.kuky.demo.wan.android.ui.main.MainRepository
import com.kuky.demo.wan.android.ui.main.MainViewModel
import com.kuky.demo.wan.android.ui.websitedetail.WebsiteDetailFragment
import com.kuky.demo.wan.android.ui.widget.ErrorReload
import com.kuky.demo.wan.android.utils.LogUtils
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
        ViewModelProvider(requireActivity(), CollectionModelFactory(CollectionRepository()))
            .get(CollectionViewModel::class.java)
    }

    private val mLoginViewModel by lazy {
        ViewModelProvider(requireActivity(), MainModelFactory(MainRepository()))
            .get(MainViewModel::class.java)
    }

    private var isFirstObserver = true

    override fun getLayoutId(): Int = R.layout.fragment_home_article

    override fun initFragment(view: View, savedInstanceState: Bundle?) {
        // 绑定 SwipeRefreshLayout 属性
        mBinding.refreshColor = R.color.colorAccent
        mBinding.refreshListener = SwipeRefreshLayout.OnRefreshListener {
            fetchHomeArticleList()
            LogUtils.error("== refresh == ")
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
                requireContext().alert(
                    if (article.collect) "「${article.title}」已收藏"
                    else " 是否收藏 「${article.title}」"
                ) {
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

        fetchHomeArticleList(false)

        // 根据登录状态做修改，过滤首次监听，防止多次加载造成页面状态显示错误
        mLoginViewModel.hasLogin.observe(this, Observer<Boolean> {
            if (isFirstObserver) {
                isFirstObserver = false
                return@Observer
            }

            if (!it) {
                mViewModel.articles?.value?.forEach { arc ->
                    arc.collect = false
                }
            } else {
                fetchHomeArticleList()
            }
        })
    }

    private fun fetchHomeArticleList(isRefresh: Boolean = true) {
        mViewModel.fetchHomeArticle {
            mBinding.emptyStatus = true
        }

        mViewModel.netState?.observe(requireActivity(), Observer {
            when (it.state) {
                State.RUNNING -> injectStates(refreshing = true, loading = !isRefresh)

                State.SUCCESS -> {
                    injectStates()
                    mBinding.indicator = resources.getString(R.string.blog_articles)
                }

                State.FAILED -> {
                    if (it.code == ERROR_CODE_INIT) {
                        injectStates(error = true)
                        mBinding.indicator = resources.getString(R.string.text_place_holder)
                    } else requireContext().toast(R.string.no_net_on_loading)
                }
            }
        })

        mViewModel.articles?.observe(this, Observer<PagedList<ArticleDetail>> {
            mAdapter.submitList(it)
        })
    }

    private fun injectStates(refreshing: Boolean = false, loading: Boolean = false, error: Boolean = false) {
        mBinding.refreshing = refreshing
        mBinding.loadingStatus = loading
        mBinding.errorStatus = error
    }
}