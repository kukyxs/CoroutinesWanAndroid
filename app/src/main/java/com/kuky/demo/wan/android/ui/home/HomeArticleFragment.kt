package com.kuky.demo.wan.android.ui.home

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.View
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import androidx.paging.ExperimentalPagingApi
import androidx.paging.LoadState
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.kuky.demo.wan.android.R
import com.kuky.demo.wan.android.base.*
import com.kuky.demo.wan.android.data.db.HomeArticleDetail
import com.kuky.demo.wan.android.databinding.FragmentHomeArticleBinding
import com.kuky.demo.wan.android.ui.app.AppViewModel
import com.kuky.demo.wan.android.ui.app.PagingLoadStateAdapter
import com.kuky.demo.wan.android.ui.collection.CollectionViewModel
import com.kuky.demo.wan.android.ui.main.MainFragment
import com.kuky.demo.wan.android.ui.main.MainViewModel
import com.kuky.demo.wan.android.ui.websitedetail.WebsiteDetailFragment
import com.kuky.demo.wan.android.widget.ErrorReload
import com.kuky.demo.wan.android.widget.RequestStatusCode
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.onCompletion
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.launch
import org.jetbrains.anko.alert
import org.jetbrains.anko.noButton
import org.jetbrains.anko.toast
import org.jetbrains.anko.yesButton
import org.koin.androidx.scope.lifecycleScope
import org.koin.androidx.viewmodel.ext.android.sharedViewModel
import org.koin.androidx.viewmodel.ext.android.viewModel

/**
 * @author kuky.
 * @description 主页面首页模块界面
 */
class HomeArticleFragment : BaseFragment<FragmentHomeArticleBinding>() {

    private val mAppViewModel by sharedViewModel<AppViewModel>()

    private val mLoginViewModel by sharedViewModel<MainViewModel>()

    private val mViewModel by viewModel<HomeArticleViewModel>()

    private val mCollectionViewModel by viewModel<CollectionViewModel>()

    private val mAdapter by lifecycleScope.inject<HomeArticlePagingAdapter>()

    private var isFirstObserver = true

    @OptIn(ExperimentalCoroutinesApi::class)
    override fun actionsOnViewInflate() {
        launch {
            mViewModel.getHomeArticlesByRoomCache()
                .catch { mBinding?.statusCode = RequestStatusCode.Error }
                .collectLatest { mAdapter.submitData(it) }
        }

        mAppViewModel.reloadHomeData.observe(this, Observer {
            if (it) mAdapter.refresh()
        })

        // 根据登录状态做修改，过滤首次监听，防止多次加载造成页面状态显示错误
        mLoginViewModel.hasLogin.observe(this, Observer {
            if (isFirstObserver) {
                isFirstObserver = false
                return@Observer
            }

            if (!it) {
                for (index in 0 until mAdapter.itemCount) {
                    mAdapter.getItemData(index)?.collect = false
                }
            } else {
                mAdapter.refresh()
            }
        })
    }

    override fun getLayoutId(): Int = R.layout.fragment_home_article

    @SuppressLint("ClickableViewAccessibility")
    @OptIn(ExperimentalPagingApi::class)
    override fun initFragment(view: View, savedInstanceState: Bundle?) {
        mBinding?.run {
            refreshColor = R.color.colorAccent
            refreshListener = SwipeRefreshLayout.OnRefreshListener {
                mAdapter.refresh()
            }

            adapter = mAdapter.apply {
                addLoadStateListener { loadState ->
                    refreshing = loadState.refresh is LoadState.Loading
                    statusCode = when (loadState.refresh) {
                        is LoadState.Loading -> RequestStatusCode.Loading
                        is LoadState.Error -> RequestStatusCode.Error
                        else -> RequestStatusCode.Succeed
                    }
                }

                addDataRefreshListener {
                    if (itemCount == 0) statusCode = RequestStatusCode.Empty
                }
            }.withLoadStateFooter(
                PagingLoadStateAdapter { mAdapter.retry() }
            )

            itemClick = OnItemClickListener { position, _ ->
                mAdapter.getItemData(position)?.let { art ->
                    WebsiteDetailFragment.viewDetail(
                        findNavController(),
                        R.id.action_mainFragment_to_websiteDetailFragment,
                        art.link
                    )
                }
            }

            itemLongClick = OnItemLongClickListener { position, _ ->
                mAdapter.getItemData(position)?.let { article ->
                    showCollectDialog(article, position)
                }
            }

            articleList.setOnTouchListener { _, _ ->
                (parentFragment as? MainFragment)?.closeMenu(true)
                false
            }

            // 双击回顶部
            gesture = DoubleClickListener {
                doubleTap = { articleList.scrollToTop() }
            }

            errorReload = ErrorReload { mAdapter.retry() }
        }
    }

    private fun showCollectDialog(article: HomeArticleDetail, position: Int) =
        context?.alert(
            if (article.collect) "「${article.title}」已收藏"
            else " 是否收藏 「${article.title}」"
        ) {
            yesButton {
                if (!article.collect) launch { collectArticle(article.id, position) }
            }

            if (!article.collect) noButton { }
        }?.show()

    @OptIn(ExperimentalCoroutinesApi::class)
    private suspend fun collectArticle(id: Int, position: Int) {
        mCollectionViewModel.collectArticle(id).catch {
            context?.toast(R.string.no_network)
        }.onStart {
            mAppViewModel.showLoading()
        }.onCompletion {
            mAppViewModel.dismissLoading()
        }.collectLatest {
            it.handleResult {
                mAdapter.getItemData(position)?.collect = true
                context?.toast(R.string.add_favourite_succeed)
            }
        }
    }
}