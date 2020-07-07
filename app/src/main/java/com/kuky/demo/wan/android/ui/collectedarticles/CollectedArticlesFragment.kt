package com.kuky.demo.wan.android.ui.collectedarticles

import android.os.Bundle
import android.view.View
import androidx.fragment.app.activityViewModels
import androidx.paging.ExperimentalPagingApi
import androidx.paging.LoadState
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.kuky.demo.wan.android.R
import com.kuky.demo.wan.android.base.*
import com.kuky.demo.wan.android.databinding.FragmentCollectedArticlesBinding
import com.kuky.demo.wan.android.ui.app.AppViewModel
import com.kuky.demo.wan.android.ui.app.PagingLoadStateAdapter
import com.kuky.demo.wan.android.ui.websitedetail.WebsiteDetailFragment
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
import org.koin.androidx.viewmodel.ext.android.viewModel

/**
 * @author kuky.
 * @description
 */
class CollectedArticlesFragment : BaseFragment<FragmentCollectedArticlesBinding>() {

    private val mAppViewModel by activityViewModels<AppViewModel>()

    private val mViewModel by viewModel<CollectedArticlesViewModel>()

    @OptIn(ExperimentalPagingApi::class)
    private val mAdapter by lazy {
        CollectedArticlesPagingAdapter().apply {
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

    private var mArticleJob: Job? = null

    override fun actionsOnViewInflate() = getCollectedArticles()

    override fun getLayoutId(): Int = R.layout.fragment_collected_articles

    override fun initFragment(view: View, savedInstanceState: Bundle?) {
        mBinding?.let { binding ->
            binding.refreshColor = R.color.colorAccent
            binding.refreshListener = SwipeRefreshLayout.OnRefreshListener {
                mAdapter.refresh()
            }

            binding.adapter = mAdapter.withLoadStateFooter(PagingLoadStateAdapter { mAdapter.retry() })

            binding.listener = OnItemClickListener { position, _ ->
                mAdapter.getItemData(position)?.let { data ->
                    WebsiteDetailFragment.viewDetail(
                        mNavController,
                        R.id.action_collectionFragment_to_websiteDetailFragment,
                        data.link
                    )
                }
            }

            binding.longListener = OnItemLongClickListener { position, _ ->
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
                .catch { mBinding?.errorStatus = true }
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
                        requireContext().toast(R.string.remove_favourite_succeed)
                        getCollectedArticles()
                    }
                }
            }
        }
    }

    fun scrollToTop() = mBinding?.collectedArticleList?.scrollToTop()
}