package com.kuky.demo.wan.android.ui.coins

import android.os.Bundle
import android.view.View
import androidx.core.os.bundleOf
import androidx.lifecycle.ViewModelProvider
import androidx.paging.LoadState
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.kuky.demo.wan.android.R
import com.kuky.demo.wan.android.base.BaseFragment
import com.kuky.demo.wan.android.base.scrollToTop
import com.kuky.demo.wan.android.databinding.FragmentCommonCoinSubBinding
import com.kuky.demo.wan.android.ui.PagingLoadStateAdapter
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch

/**
 * @author kuky.
 * @description
 */
class CoinCommonSubFragment : BaseFragment<FragmentCommonCoinSubBinding>() {

    private val mViewModel: CoinViewModel by lazy {
        ViewModelProvider(requireActivity(), CoinModelFactory(CoinRepository()))
            .get(CoinViewModel::class.java)
    }

    private val mRankAdapter: CoinRankPagingAdapter by lazy {
        CoinRankPagingAdapter()
    }

    private val mRecordAdapter: CoinRecordPagingAdapter by lazy {
        CoinRecordPagingAdapter()
    }

    private val type by lazy(mode = LazyThreadSafetyMode.NONE) {
        arguments?.getInt("type", 0) ?: 0
    }

    override fun getLayoutId(): Int = R.layout.fragment_common_coin_sub

    override fun initFragment(view: View, savedInstanceState: Bundle?) {
        mBinding?.let { binding ->
            binding.refreshColor = R.color.colorAccent
            binding.refreshListener = SwipeRefreshLayout.OnRefreshListener {
                if (type == 0) mRecordAdapter.refresh() else mRankAdapter.refresh()
            }

            binding.refreshing = true
            binding.adapter = if (type == 0) mRecordAdapter.withLoadStateFooter(
                PagingLoadStateAdapter {
                    mRecordAdapter.retry()
                }) else mRankAdapter.withLoadStateFooter(
                PagingLoadStateAdapter {
                    mRankAdapter.retry()
                })
            binding.divider = DividerItemDecoration(requireContext(), DividerItemDecoration.VERTICAL)

            launch {
                if (type == 0) {
                    mViewModel.coinRecordList.collect {
                        binding.refreshing = false
                        mRecordAdapter.submitData(it)
                    }
                } else {
                    mViewModel.coinRankList.collect {
                        binding.refreshing = false
                        mRankAdapter.submitData(it)
                    }
                }
            }

            if (type == 0) {
                mRecordAdapter.addLoadStateListener { loadState ->
                    binding.refreshing = loadState.refresh is LoadState.Loading
                    binding.loadingStatus = loadState.refresh is LoadState.Loading
                    binding.errorStatus = loadState.refresh is LoadState.Error
                }
            } else {
                mRankAdapter.addLoadStateListener { loadState ->
                    binding.refreshing = loadState.refresh is LoadState.Loading
                    binding.loadingStatus = loadState.refresh is LoadState.Loading
                    binding.errorStatus = loadState.refresh is LoadState.Error
                }
            }
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