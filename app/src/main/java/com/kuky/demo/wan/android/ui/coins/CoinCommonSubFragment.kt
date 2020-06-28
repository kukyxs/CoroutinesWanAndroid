package com.kuky.demo.wan.android.ui.coins

import android.os.Bundle
import android.view.View
import androidx.core.os.bundleOf
import androidx.lifecycle.ViewModelProvider
import androidx.paging.ExperimentalPagingApi
import androidx.paging.LoadState
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.kuky.demo.wan.android.R
import com.kuky.demo.wan.android.base.BaseFragment
import com.kuky.demo.wan.android.base.scrollToTop
import com.kuky.demo.wan.android.databinding.FragmentCommonCoinSubBinding
import com.kuky.demo.wan.android.ui.app.PagingLoadStateAdapter
import com.kuky.demo.wan.android.utils.Injection
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

/**
 * @author kuky.
 * @description
 */
class CoinCommonSubFragment : BaseFragment<FragmentCommonCoinSubBinding>() {

    private val mViewModel: CoinViewModel by lazy {
        ViewModelProvider(requireActivity(), Injection.provideCoinViewModelFactory())
            .get(CoinViewModel::class.java)
    }

    @OptIn(ExperimentalPagingApi::class)
    private val mRankAdapter: CoinRankPagingAdapter by lazy {
        CoinRankPagingAdapter().apply {
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

    @OptIn(ExperimentalPagingApi::class)
    private val mRecordAdapter: CoinRecordPagingAdapter by lazy {
        CoinRecordPagingAdapter().apply {
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

    private val type by lazy {
        arguments?.getInt("type", 0) ?: 0
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    override fun actionsOnViewInflate() {
        launch {
            if (type == 0) {
                mViewModel.getCoinRecordList()
                    .catch { mBinding?.errorStatus = true }
                    .collectLatest { mRecordAdapter.submitData(it) }
            } else {
                mViewModel.getCoinRankList()
                    .catch { mBinding?.errorStatus = true }
                    .collectLatest { mRankAdapter.submitData(it) }
            }
        }
    }

    override fun getLayoutId(): Int = R.layout.fragment_common_coin_sub

    override fun initFragment(view: View, savedInstanceState: Bundle?) {
        mBinding?.let { binding ->
            binding.refreshColor = R.color.colorAccent

            binding.refreshListener = SwipeRefreshLayout.OnRefreshListener {
                if (type == 0) mRecordAdapter.refresh() else mRankAdapter.refresh()
            }

            binding.adapter = if (type == 0) mRecordAdapter.withLoadStateFooter(
                PagingLoadStateAdapter { mRecordAdapter.retry() }
            ) else mRankAdapter.withLoadStateFooter(
                PagingLoadStateAdapter { mRankAdapter.retry() }
            )

            binding.divider = DividerItemDecoration(requireContext(), DividerItemDecoration.VERTICAL)
        }
    }

    fun scrollToTop() = mBinding?.coinList?.scrollToTop()

    companion object {
        private fun instance(type: Int) = CoinCommonSubFragment().apply {
            arguments = bundleOf(Pair("type", type))
        }

        fun recordInstance() = instance(0)

        fun rankInstance() = instance(1)
    }
}