package com.kuky.demo.wan.android.ui.collectedarticles

import android.os.Bundle
import android.view.View
import androidx.lifecycle.ViewModelProvider
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
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import org.jetbrains.anko.alert
import org.jetbrains.anko.noButton
import org.jetbrains.anko.okButton
import org.jetbrains.anko.toast

/**
 * @author kuky.
 * @description
 */
class CollectedArticlesFragment : BaseFragment<FragmentCollectedArticlesBinding>() {

    private val mViewModel by lazy {
        ViewModelProvider(requireActivity(), CollectedArticlesModelFactory(CollectedArticlesRepository()))
            .get(CollectedArticlesViewModel::class.java)
    }

    private val mAppViewModel by lazy { getSharedViewModel(AppViewModel::class.java) }

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

    @OptIn(ExperimentalCoroutinesApi::class)
    override fun actionsOnViewInflate() {
        launch {
            mViewModel.getCollectedArticles().collectLatest {
                mAdapter.submitData(it)
            }
        }
    }

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
    private fun removeFavourite(position: Int) {
        mAdapter.getItemData(position)?.let { data ->
            launch {
                mAppViewModel.showLoading()
                mViewModel.removeCollectedArticle(data.id, data.originId)
                    .catch {
                        mAppViewModel.dismissLoading()
                        requireContext().toast(R.string.no_network)
                    }.collectLatest {
                        mAppViewModel.dismissLoading()
                        it.handleResult {
                            requireContext().toast(R.string.remove_favourite_succeed)
                            // TODO("删除 item 存在异常, 暂时使用 refresh 代替")
//                            mAdapter.notifyItemRemoved(position)
//                            mAdapter.notifyItemRangeChanged(position, mAdapter.itemCount - position)
                            mAdapter.refresh()
                        }
                    }
            }
        }
    }

    fun scrollToTop() = mBinding?.collectedArticleList?.scrollToTop()
}