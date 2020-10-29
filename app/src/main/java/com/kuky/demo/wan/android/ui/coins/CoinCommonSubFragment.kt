package com.kuky.demo.wan.android.ui.coins

import android.os.Bundle
import android.view.View
import androidx.core.os.bundleOf
import androidx.paging.LoadState
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.kuky.demo.wan.android.R
import com.kuky.demo.wan.android.base.BaseFragment
import com.kuky.demo.wan.android.base.scrollToTop
import com.kuky.demo.wan.android.databinding.FragmentCommonCoinSubBinding
import com.kuky.demo.wan.android.ui.app.PagingLoadStateAdapter
import com.kuky.demo.wan.android.widget.RequestStatusCode
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import org.koin.androidx.scope.lifecycleScope
import org.koin.androidx.viewmodel.ext.android.viewModel

/**
 * @author kuky.
 * @description
 */
class CoinCommonSubFragment : BaseFragment<FragmentCommonCoinSubBinding>() {

    private val mViewModel by viewModel<CoinViewModel>()

    private val mRankAdapter by lifecycleScope.inject<CoinRankPagingAdapter>()

    private val mRecordAdapter by lifecycleScope.inject<CoinRecordPagingAdapter>()

    private val mType by lazy { arguments?.getInt("type", 0) ?: 0 }

    override fun actionsOnViewInflate() {
        launch {
            if (mType == 0) {
                mViewModel.getCoinRecordList()
                    .catch { mBinding?.statusCode = RequestStatusCode.Error }
                    .collectLatest { mRecordAdapter.submitData(it) }
            } else {
                mViewModel.getCoinRankList()
                    .catch { mBinding?.statusCode = RequestStatusCode.Error }
                    .collectLatest { mRankAdapter.submitData(it) }
            }
        }
    }

    override fun getLayoutId(): Int = R.layout.fragment_common_coin_sub

    override fun initFragment(view: View, savedInstanceState: Bundle?) {
        mBinding?.run {
            refreshColor = R.color.colorAccent

            val orgPagerAdapter = (if (mType == 0) mRecordAdapter else mRankAdapter).apply {
                addLoadStateListener { loadState ->
                    refreshing = loadState.refresh is LoadState.Loading
                    statusCode = when (loadState.refresh) {
                        is LoadState.Loading -> RequestStatusCode.Loading
                        is LoadState.Error -> RequestStatusCode.Error
                        else -> {
                            if (itemCount == 0) RequestStatusCode.Empty
                            else RequestStatusCode.Succeed
                        }
                    }
                }
            }

            refreshListener = SwipeRefreshLayout.OnRefreshListener {
                orgPagerAdapter.refresh()
            }

            adapter = orgPagerAdapter.withLoadStateFooter(
                PagingLoadStateAdapter { orgPagerAdapter.retry() }
            )

            divider = DividerItemDecoration(requireContext(), DividerItemDecoration.VERTICAL)
        }
    }

    fun scrollToTop() = mBinding?.coinList?.scrollToTop()

    companion object {
        fun instance(type: Int) = CoinCommonSubFragment().apply {
            arguments = bundleOf(Pair("type", type))
        }
    }
}