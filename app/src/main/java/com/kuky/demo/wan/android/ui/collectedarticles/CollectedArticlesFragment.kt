package com.kuky.demo.wan.android.ui.collectedarticles

import android.os.Bundle
import android.view.View
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.paging.LoadState
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.kuky.demo.wan.android.R
import com.kuky.demo.wan.android.base.BaseFragment
import com.kuky.demo.wan.android.base.PageStateByUiState
import com.kuky.demo.wan.android.base.UiState
import com.kuky.demo.wan.android.base.scrollToTop
import com.kuky.demo.wan.android.databinding.FragmentCollectedArticlesBinding
import com.kuky.demo.wan.android.listener.OnItemClickListener
import com.kuky.demo.wan.android.listener.OnItemLongClickListener
import com.kuky.demo.wan.android.ui.app.AppViewModel
import com.kuky.demo.wan.android.ui.app.PagingLoadStateAdapter
import com.kuky.demo.wan.android.ui.websitedetail.WebsiteDetailFragment
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import org.jetbrains.anko.alert
import org.jetbrains.anko.noButton
import org.jetbrains.anko.okButton
import org.jetbrains.anko.toast
import org.koin.android.ext.android.inject
import org.koin.android.scope.AndroidScopeComponent
import org.koin.androidx.scope.fragmentScope
import org.koin.androidx.viewmodel.ext.android.sharedViewModel
import org.koin.androidx.viewmodel.ext.android.viewModel
import org.koin.core.scope.Scope

/**
 * @author kuky.
 * @description
 */
class CollectedArticlesFragment : BaseFragment<FragmentCollectedArticlesBinding>(), AndroidScopeComponent {

    override val scope: Scope by fragmentScope()

    private val mAppViewModel by sharedViewModel<AppViewModel>()

    private val mViewModel by viewModel<CollectedArticlesViewModel>()

    private val mAdapter by inject<CollectedArticlesPagingAdapter>()

    private var mArticleJob: Job? = null

    override fun actionsOnViewInflate() {
        getCollectedArticles()

        lifecycleScope.launchWhenCreated {
            mViewModel.uiState.collect { mBinding.statusCode = it.PageStateByUiState() }
        }

        lifecycleScope.launchWhenCreated {
            mViewModel.removeState.collect {
                when (it) {
                    is UiState.Error -> context?.toast(R.string.no_network)

                    is UiState.Loading -> mAppViewModel.showLoading()

                    is UiState.Succeed -> {
                        mAppViewModel.reloadHomeData.postValue(true)
                        requireContext().toast(R.string.remove_favourite_succeed)
                        getCollectedArticles()
                    }
                }
            }
        }
    }

    override fun getLayoutId(): Int = R.layout.fragment_collected_articles

    override fun initFragment(view: View, savedInstanceState: Bundle?) {
        mBinding.run {
            refreshColor = R.color.colorAccent

            refreshListener = SwipeRefreshLayout.OnRefreshListener {
                getCollectedArticles()
            }

            adapter = mAdapter.apply {
                addLoadStateListener { loadState ->
                    refreshing = loadState.refresh is LoadState.Loading
                    mViewModel.listenPagerLoadState(loadState) { itemCount == 0 }
                }
            }.withLoadStateFooter(PagingLoadStateAdapter { mAdapter.retry() })

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

    private fun getCollectedArticles() {
        mArticleJob?.cancel()
        mArticleJob = lifecycleScope.launch {
            mViewModel.getCollectedArticles().collectLatest { mAdapter.submitData(it) }
        }
    }

    private fun removeFavourite(position: Int) {
        mAdapter.getItemData(position)?.let { data ->
            launch { mViewModel.removeCollectedArticle(data.id, data.originId) }
        }
    }

    fun scrollToTop() = mBinding.collectedArticleList.scrollToTop()
}