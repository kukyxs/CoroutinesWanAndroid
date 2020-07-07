package com.kuky.demo.wan.android.ui.userarticles

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.View
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.paging.ExperimentalPagingApi
import androidx.paging.LoadState
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.kuky.demo.wan.android.R
import com.kuky.demo.wan.android.base.*
import com.kuky.demo.wan.android.databinding.FragmentUserArticlesBinding
import com.kuky.demo.wan.android.ui.app.AppViewModel
import com.kuky.demo.wan.android.ui.app.PagingLoadStateAdapter
import com.kuky.demo.wan.android.ui.collection.CollectionViewModel
import com.kuky.demo.wan.android.ui.main.MainFragment
import com.kuky.demo.wan.android.ui.main.MainViewModel
import com.kuky.demo.wan.android.ui.usershared.UserSharedFragment
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
import org.koin.androidx.viewmodel.ext.android.viewModel

/**
 * @author kuky.
 * @description
 */
class UserArticleFragment : BaseFragment<FragmentUserArticlesBinding>() {

    private val mAppViewModel by lazy { getSharedViewModel(AppViewModel::class.java) }

    private val mViewModel by lazy {
        ViewModelProvider(requireActivity(), Injection.provideUserArticleViewModelFactory())
            .get(UserArticleViewModel::class.java)
    }

    private val mCollectionViewModel by viewModel<CollectionViewModel>()

    private val mLoginViewModel by activityViewModels<MainViewModel>()

    @OptIn(ExperimentalPagingApi::class)
    private val mAdapter by lazy {
        UserArticlePagingAdapter().apply {
            userListener = { id, nick ->
                UserSharedFragment.navToUser(
                    mNavController,
                    R.id.action_mainFragment_to_sharedUserFragment,
                    id, nick
                )
            }

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

    private var isFirstObserver = true

    override fun actionsOnViewInflate() {
        fetchSharedArticles()

        // 登录状态切换
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

    override fun getLayoutId(): Int = R.layout.fragment_user_articles

    @SuppressLint("ClickableViewAccessibility")
    override fun initFragment(view: View, savedInstanceState: Bundle?) {
        mBinding?.let { binding ->
            binding.refreshColor = R.color.colorAccent
            binding.refreshListener = SwipeRefreshLayout.OnRefreshListener {
                mAdapter.refresh()
            }

            binding.adapter = mAdapter.withLoadStateFooter(PagingLoadStateAdapter { mAdapter.retry() })
            binding.itemClick = OnItemClickListener { position, _ ->
                (parentFragment as? MainFragment)?.closeMenu()
                mAdapter.getItemData(position)?.let {
                    WebsiteDetailFragment.viewDetail(
                        mNavController,
                        R.id.action_mainFragment_to_websiteDetailFragment,
                        it.link
                    )
                }
            }
            binding.itemLongClick = OnItemLongClickListener { position, _ ->
                (parentFragment as? MainFragment)?.closeMenu()
                mAdapter.getItemData(position)?.let { article ->
                    requireContext().alert(
                        if (article.collect) "「${article.title}」已收藏"
                        else " 是否收藏 「${article.title}」"
                    ) {
                        yesButton {
                            if (!article.collect) launch { collectArticle(article.id, position) }
                        }
                        if (!article.collect) noButton { }
                    }.show()
                }
            }
            binding.articleList.setOnTouchListener { _, _ ->
                (parentFragment as? MainFragment)?.closeMenu()
                false
            }

            // 双击回顶部
            binding.gesture = DoubleClickListener {
                doubleTap = {
                    binding.articleList.scrollToTop()
                }
            }

            binding.errorReload = ErrorReload { mAdapter.retry() }
        }
    }

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

    @OptIn(ExperimentalCoroutinesApi::class)
    private fun fetchSharedArticles() {
        launch {
            mViewModel.getSharedArticles()
                .catch {
                    mBinding?.errorStatus = true
                    mBinding?.indicator = resources.getString(R.string.text_place_holder)
                }.collectLatest {
                    mBinding?.indicator = resources.getString(R.string.share_articles)
                    mAdapter.submitData(it)
                }
        }
    }
}