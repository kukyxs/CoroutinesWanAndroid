package com.kuky.demo.wan.android.ui.coins

import android.os.Bundle
import android.view.View
import androidx.core.os.bundleOf
import androidx.lifecycle.lifecycleScope
import androidx.paging.LoadState
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.kuky.demo.wan.android.R
import com.kuky.demo.wan.android.base.BaseFragment
import com.kuky.demo.wan.android.base.PageStateByUiState
import com.kuky.demo.wan.android.base.scrollToTop
import com.kuky.demo.wan.android.databinding.FragmentCommonCoinSubBinding
import com.kuky.demo.wan.android.ui.app.PagingLoadStateAdapter
import kotlinx.coroutines.flow.collect
import org.koin.android.ext.android.inject
import org.koin.android.scope.AndroidScopeComponent
import org.koin.androidx.scope.fragmentScope
import org.koin.androidx.viewmodel.ext.android.viewModel
import org.koin.core.scope.Scope

/**
 * @author kuky.
 * @description
 */
class CoinCommonSubFragment : BaseFragment<FragmentCommonCoinSubBinding>(), AndroidScopeComponent {

    override val scope: Scope by fragmentScope()

    private val mViewModel by viewModel<CoinViewModel>()

    private val mRankAdapter by inject<CoinRankPagingAdapter>()

    private val mRecordAdapter by inject<CoinRecordPagingAdapter>()

    private val mType by lazy { arguments?.getInt("type", 0) ?: 0 }

    override fun actionsOnViewInflate() {
        lifecycleScope.launchWhenStarted {
            mViewModel.uiState.collect { mBinding.statusCode = it.PageStateByUiState() }
        }

        lifecycleScope.launchWhenStarted {
            if (mType == 0) {
                mViewModel.getCoinRecordList().collect { mRecordAdapter.submitData(it) }
            } else {
                mViewModel.getCoinRankList().collect { mRankAdapter.submitData(it) }
            }
        }
    }

    override fun getLayoutId(): Int = R.layout.fragment_common_coin_sub

    override fun initFragment(view: View, savedInstanceState: Bundle?) {
        mBinding.run {
            refreshColor = R.color.colorAccent

            val orgPagerAdapter = (if (mType == 0) mRecordAdapter else mRankAdapter).apply {
                addLoadStateListener { loadState ->
                    refreshing = loadState.refresh is LoadState.Loading

                    mViewModel.listenPagerLoadState(loadState) { itemCount == 0 }
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

    fun scrollToTop() = mBinding.coinList.scrollToTop()

    companion object {
        fun instance(type: Int) = CoinCommonSubFragment().apply {
            arguments = bundleOf(Pair("type", type))
        }
    }
}