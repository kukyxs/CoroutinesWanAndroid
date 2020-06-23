package com.kuky.demo.wan.android.ui.collectedarticles

import android.os.Bundle
import android.view.View
import androidx.lifecycle.ViewModelProvider
import androidx.paging.ExperimentalPagingApi
import androidx.paging.LoadState
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.kuky.demo.wan.android.R
import com.kuky.demo.wan.android.base.BaseFragment
import com.kuky.demo.wan.android.base.OnItemClickListener
import com.kuky.demo.wan.android.base.OnItemLongClickListener
import com.kuky.demo.wan.android.base.scrollToTop
import com.kuky.demo.wan.android.databinding.FragmentCollectedArticlesBinding
import com.kuky.demo.wan.android.ui.PagingLoadStateAdapter
import com.kuky.demo.wan.android.ui.websitedetail.WebsiteDetailFragment
import kotlinx.coroutines.flow.collect
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

    override fun actionsOnViewInflate() {
        launch {
            mViewModel.articleList.collect {
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
                    okButton {
                        mAdapter.getItemData(position)?.let { data ->
                            mViewModel.removeCollectedArticle(data.id, data.originId,
                                {
                                    requireContext().toast("删除成功")
                                    // TODO("删除 item 存在异常, 暂时使用 refresh 代替")
//                                    mAdapter.notifyItemRemoved(position)
//                                    mAdapter.notifyItemRangeChanged(position, mAdapter.itemCount - position)
                                    mAdapter.refresh()
                                }, { requireContext().toast(it) })
                        }
                    }
                    noButton { }
                }.show()
            }
        }
    }

    fun scrollToTop() = mBinding?.collectedArticleList?.scrollToTop()
}