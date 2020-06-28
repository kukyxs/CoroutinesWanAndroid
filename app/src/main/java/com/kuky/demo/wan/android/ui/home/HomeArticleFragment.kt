package com.kuky.demo.wan.android.ui.home

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.View
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
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
import com.kuky.demo.wan.android.utils.Injection
import com.kuky.demo.wan.android.widget.ErrorReload
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

/**
 * @author kuky.
 * @description 主页面首页模块界面
 */
class HomeArticleFragment : BaseFragment<FragmentHomeArticleBinding>() {

    @OptIn(ExperimentalPagingApi::class)
    private val mAdapter by lazy {
        HomeArticlePagingAdapter().apply {
            addLoadStateListener { loadState ->
                mBinding?.refreshing = loadState.refresh is LoadState.Loading
                mBinding?.loadingStatus = loadState.refresh is LoadState.Loading
                mBinding?.errorStatus = loadState.refresh is LoadState.Error
            }

            addDataRefreshListener {
                mBinding?.emptyStatus = itemCount == 0
            }
        }
    }

    private val mAppViewModel by lazy {
        getSharedViewModel(AppViewModel::class.java)
    }

    private val mViewModel by lazy {
        ViewModelProvider(
            requireActivity(),
            Injection.provideHomeArticleViewModelFactory(requireActivity())
        ).get(HomeArticleViewModel::class.java)
    }

    private val mCollectionViewModel by lazy {
        ViewModelProvider(requireActivity(), Injection.provideCollectionViewModelFactory())
            .get(CollectionViewModel::class.java)
    }

    private val mLoginViewModel by lazy {
        ViewModelProvider(requireActivity(), Injection.provideMainViewModelFactory())
            .get(MainViewModel::class.java)
    }

    private var isFirstObserver = true

    @OptIn(ExperimentalCoroutinesApi::class)
    override fun actionsOnViewInflate() {
        launch {
            mViewModel.getHomeArticlesByRoomCache()
                .catch { mBinding?.errorStatus = true }
                .collectLatest { mAdapter.submitData(it) }
        }

        // 根据登录状态做修改，过滤首次监听，防止多次加载造成页面状态显示错误
        mLoginViewModel.hasLogin.observe(this, Observer<Boolean> {
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
    override fun initFragment(view: View, savedInstanceState: Bundle?) {
        mBinding?.let { binding ->
            binding.refreshColor = R.color.colorAccent
            binding.refreshListener = SwipeRefreshLayout.OnRefreshListener {
                mAdapter.refresh()
            }

            binding.adapter = mAdapter.withLoadStateFooter(
                PagingLoadStateAdapter { mAdapter.retry() }
            )

            binding.itemClick = OnItemClickListener { position, _ ->
                mAdapter.getItemData(position)?.let { art ->
                    (parentFragment as? MainFragment)?.closeMenu()
                    WebsiteDetailFragment.viewDetail(
                        mNavController,
                        R.id.action_mainFragment_to_websiteDetailFragment,
                        art.link
                    )
                }
            }

            binding.itemLongClick = OnItemLongClickListener { position, _ ->
                (parentFragment as? MainFragment)?.closeMenu()
                mAdapter.getItemData(position)?.let { article ->
                    showCollectDialog(article, position)
                }
            }

            binding.articleList.setOnTouchListener { _, _ ->
                (parentFragment as? MainFragment)?.closeMenu(true)
                false
            }

            // 双击回顶部
            binding.gesture = DoubleClickListener {
                doubleTap = {
                    binding.articleList.scrollToTop()
                }
            }

            binding.indicator = resources.getString(R.string.blog_articles)

            binding.errorReload = ErrorReload { mAdapter.retry() }
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