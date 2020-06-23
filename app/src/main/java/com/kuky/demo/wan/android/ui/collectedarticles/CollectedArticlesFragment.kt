package com.kuky.demo.wan.android.ui.collectedarticles

import android.os.Bundle
import android.view.View
import androidx.lifecycle.ViewModelProvider
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

    private val mAdapter by lazy { CollectedArticlesPagingAdapter() }

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
                requireActivity().alert("是否删除本条收藏？") {
                    okButton {
                        mAdapter.getItemData(position)?.let { data ->
                            mViewModel.removeCollectedArticle(data.id, data.originId,
                                {
                                    requireContext().toast("删除成功")
                                    mAdapter.refresh()
                                }, { requireContext().toast(it) })
                        }
                    }
                    noButton { }
                }.show()
            }

            mAdapter.addLoadStateListener { loadState ->
                binding.refreshing = loadState.refresh is LoadState.Loading
                binding.loadingStatus = loadState.refresh is LoadState.Loading
                binding.errorStatus = loadState.refresh is LoadState.Error
            }

            launch {
                mViewModel.articleList.collect {
                    binding.refreshing = false
                    mAdapter.submitData(it)
                }
            }
        }
    }

    fun scrollToTop() = mBinding?.collectedArticleList?.scrollToTop()
}