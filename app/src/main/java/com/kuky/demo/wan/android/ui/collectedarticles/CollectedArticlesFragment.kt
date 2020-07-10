package com.kuky.demo.wan.android.ui.collectedarticles

import android.os.Bundle
import android.view.View
import androidx.navigation.fragment.findNavController
import androidx.paging.ExperimentalPagingApi
import androidx.paging.LoadState
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.kuky.demo.wan.android.R
import com.kuky.demo.wan.android.base.*
import com.kuky.demo.wan.android.databinding.FragmentCollectedArticlesBinding
import com.kuky.demo.wan.android.ui.app.AppViewModel
import com.kuky.demo.wan.android.ui.app.PagingLoadStateAdapter
import com.kuky.demo.wan.android.ui.websitedetail.WebsiteDetailFragment
import com.kuky.demo.wan.android.widget.RequestStatusCode
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.onCompletion
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.launch
import org.jetbrains.anko.alert
import org.jetbrains.anko.noButton
import org.jetbrains.anko.okButton
import org.jetbrains.anko.toast
import org.koin.androidx.scope.lifecycleScope
import org.koin.androidx.viewmodel.ext.android.sharedViewModel
import org.koin.androidx.viewmodel.ext.android.viewModel

/**
 * @author kuky.
 * @description
 */
class CollectedArticlesFragment : BaseFragment<FragmentCollectedArticlesBinding>() {

    private val mAppViewModel by sharedViewModel<AppViewModel>()

    private val mViewModel by viewModel<CollectedArticlesViewModel>()

    private val mAdapter by lifecycleScope.inject<CollectedArticlesPagingAdapter>()

    private var mArticleJob: Job? = null

    override fun actionsOnViewInflate() = getCollectedArticles()

    override fun getLayoutId(): Int = R.layout.fragment_collected_articles

    @OptIn(ExperimentalPagingApi::class)
    override fun initFragment(view: View, savedInstanceState: Bundle?) {
        mBinding?.run {
            refreshColor = R.color.colorAccent

            refreshListener = SwipeRefreshLayout.OnRefreshListener {
                getCollectedArticles()
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

            listener = OnItemClickListener { position, _ ->
                mAdapter.getItemData(position)?.let { data ->
                    WebsiteDetailFragment.viewDetail(
                        findNavController(),
                        R.id.action_collectionFragment_to_websiteDetailFragment,
                        data.link
                    )
                }
            }

            longListener = OnItemLongClickListener { position, _ ->
                requireActivity().alert("是否删除本条收藏?") {
                    okButton { removeFavourite(position) }
                    noButton { }
                }.show()
            }
        }
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    private fun getCollectedArticles() {
        mArticleJob?.cancel()
        mArticleJob = launch {
            mViewModel.getCollectedArticles()
                .catch { mBinding?.statusCode = RequestStatusCode.Error }
                .collectLatest { mAdapter.submitData(it) }
        }
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    private fun removeFavourite(position: Int) {
        mAdapter.getItemData(position)?.let { data ->
            launch {
                mViewModel.removeCollectedArticle(data.id, data.originId).catch {
                    requireContext().toast(R.string.no_network)
                }.onStart {
                    mAppViewModel.showLoading()
                }.onCompletion {
                    mAppViewModel.dismissLoading()
                }.collectLatest {
                    it.handleResult {
                        mAppViewModel.reloadHomeData.postValue(true)
                        requireContext().toast(R.string.remove_favourite_succeed)
                        getCollectedArticles()
                    }
                }
            }
        }
    }

    fun scrollToTop() = mBinding?.collectedArticleList?.scrollToTop()
}