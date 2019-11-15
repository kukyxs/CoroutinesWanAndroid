package com.kuky.demo.wan.android.ui.home

import android.os.Bundle
import android.view.View
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.paging.PagedList
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.kuky.demo.wan.android.R
import com.kuky.demo.wan.android.base.*
import com.kuky.demo.wan.android.data.db.HomeArticleDetail
import com.kuky.demo.wan.android.databinding.FragmentHomeArticleBinding
import com.kuky.demo.wan.android.ui.collection.CollectionModelFactory
import com.kuky.demo.wan.android.ui.collection.CollectionRepository
import com.kuky.demo.wan.android.ui.collection.CollectionViewModel
import com.kuky.demo.wan.android.ui.main.MainModelFactory
import com.kuky.demo.wan.android.ui.main.MainRepository
import com.kuky.demo.wan.android.ui.main.MainViewModel
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

    private val mCacheAdapter: HomeArticleCacheAdapter by lazy { HomeArticleCacheAdapter() }

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
    private var hasCache = false

    override fun getLayoutId(): Int = R.layout.fragment_home_article

    override fun initFragment(view: View, savedInstanceState: Bundle?) {
        // 绑定 SwipeRefreshLayout 属性
        mBinding.refreshColor = R.color.colorAccent
        mBinding.refreshListener = SwipeRefreshLayout.OnRefreshListener {
            mViewModel.refreshArticle()
        }

        // 双击回顶部
        mBinding.gesture = DoubleClickListener(null, {
            mBinding.articleList.scrollToTop()
        })

        mBinding.errorReload = ErrorReload {
            mViewModel.refreshArticle()
        }

        fetchHomeArticleCache()

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
                mViewModel.refreshArticle()
            }
        })
    }

    /**
     * 获取本地缓存，优先加载缓存数据
     * 缓存数据通过 RecyclerView.Adapter 加载，
     * 网络数据加载成功后切回 Paging Adapter，目前未找到较好的方式，有更好方式可提 issue
     */
    private fun fetchHomeArticleCache() {
        mViewModel.fetchCache()
        mBinding.adapter = mCacheAdapter
        mViewModel.cache?.observe(this, Observer {
            hasCache = it.isNotEmpty()
            mCacheAdapter.injectAdapterData(it as MutableList<HomeArticleDetail>)
        })

        fetchHomeArticleList()
    }

    private fun fetchHomeArticleList() {
        mViewModel.fetchHomeArticle {
            mBinding.emptyStatus = true
        }

        mViewModel.netState?.observe(this, Observer {
            when (it.state) {
                // 请求网络数据的时候先切回缓存数据，缓存数据会根据上次请求记录
                State.RUNNING -> {
                    mBinding.adapter = mCacheAdapter
                    injectStates(refreshing = true, loading = !hasCache)
                }

                // 请求成功后，切回 paging adapter，展示新数据，可解决 Paging Adapter 数据刷新引起短时间空白的问题
                State.SUCCESS -> {
                    injectStates()
                    mBinding.adapter = mAdapter
                    mBinding.itemClick = OnItemClickListener { position, _ ->
                        mAdapter.getItemData(position)?.let { art ->
                            WebsiteDetailFragment.viewDetail(
                                mNavController,
                                R.id.action_mainFragment_to_websiteDetailFragment,
                                art.link
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
                    mBinding.indicator = resources.getString(R.string.blog_articles)
                }

                // 加载失败如果有缓存加载缓存页面，否则显示出错界面，提示用户点击刷新，重新加载
                State.FAILED -> {
                    if (it.code == ERROR_CODE_INIT) {
                        injectStates(error = !hasCache)
                        mBinding.indicator = resources.getString(if (hasCache) R.string.blog_articles else R.string.text_place_holder)
                    } else requireContext().toast(R.string.no_net_on_loading)
                }
            }
        })

        mViewModel.articles?.observe(this, Observer<PagedList<HomeArticleDetail>> {
            mAdapter.submitList(it)
        })
    }

    private fun injectStates(refreshing: Boolean = false, loading: Boolean = false, error: Boolean = false) {
        mBinding.refreshing = refreshing
        mBinding.loadingStatus = loading
        mBinding.errorStatus = error
    }
}