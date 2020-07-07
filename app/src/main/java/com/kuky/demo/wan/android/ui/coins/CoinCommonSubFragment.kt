package com.kuky.demo.wan.android.ui.coins

import android.os.Bundle
import android.view.View
import androidx.core.os.bundleOf
import androidx.paging.ExperimentalPagingApi
import androidx.paging.LoadState
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.kuky.demo.wan.android.R
import com.kuky.demo.wan.android.base.BaseFragment
import com.kuky.demo.wan.android.base.scrollToTop
import com.kuky.demo.wan.android.databinding.FragmentCommonCoinSubBinding
import com.kuky.demo.wan.android.ui.app.PagingLoadStateAdapter
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import org.koin.android.ext.android.inject
import org.koin.androidx.viewmodel.ext.android.viewModel

/**
 * @author kuky.
 * @description
 */
class CoinCommonSubFragment : BaseFragment<FragmentCommonCoinSubBinding>() {

    private val mViewModel by viewModel<CoinViewModel>()

    private val mRankAdapter by inject<CoinRankPagingAdapter>()

    private val mRecordAdapter by inject<CoinRecordPagingAdapter>()

    private val type by lazy { arguments?.getInt("type", 0) ?: 0 }

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
        mBinding?.run {
            injectAdapterListener()

            refreshColor = R.color.colorAccent

            refreshListener = SwipeRefreshLayout.OnRefreshListener {
                if (type == 0) mRecordAdapter.refresh() else mRankAdapter.refresh()
            }

            adapter = if (type == 0) mRecordAdapter.withLoadStateFooter(
                PagingLoadStateAdapter { mRecordAdapter.retry() }
            ) else mRankAdapter.withLoadStateFooter(
                PagingLoadStateAdapter { mRankAdapter.retry() }
            )

            divider = DividerItemDecoration(requireContext(), DividerItemDecoration.VERTICAL)
        }
    }

    @OptIn(ExperimentalPagingApi::class)
    private fun injectAdapterListener() {
        mRankAdapter.run {
            addLoadStateListener { loadState ->
                mBinding?.refreshing = loadState.refresh is LoadState.Loading
                mBinding?.loadingStatus = loadState.refresh is LoadState.Loading
                mBinding?.errorStatus = loadState.refresh is LoadState.Error
            }

            addDataRefreshListener {
                mBinding?.emptyStatus = itemCount == 0
            }
        }

        mRecordAdapter.run {
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

    fun scrollToTop() = mBinding?.coinList?.scrollToTop()

    companion object {
        fun instance(type: Int) = CoinCommonSubFragment().apply {
            arguments = bundleOf(Pair("type", type))
        }
    }
}