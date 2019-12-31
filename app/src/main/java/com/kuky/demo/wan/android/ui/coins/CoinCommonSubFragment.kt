package com.kuky.demo.wan.android.ui.coins

import android.os.Bundle
import android.view.View
import androidx.core.os.bundleOf
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.kuky.demo.wan.android.R
import com.kuky.demo.wan.android.base.BaseFragment
import com.kuky.demo.wan.android.base.ERROR_CODE_INIT
import com.kuky.demo.wan.android.base.State
import com.kuky.demo.wan.android.base.scrollToTop
import com.kuky.demo.wan.android.databinding.FragmentCommonCoinSubBinding
import com.kuky.demo.wan.android.ui.widget.ErrorReload
import org.jetbrains.anko.toast

/**
 * @author kuky.
 * @description
 */
class CoinCommonSubFragment : BaseFragment<FragmentCommonCoinSubBinding>() {

    private val mViewModel: CoinViewModel by lazy {
        ViewModelProvider(requireActivity(), CoinModelFactory(CoinRepository()))
            .get(CoinViewModel::class.java)
    }

    private val mRankAdapter: CoinRankAdapter by lazy {
        CoinRankAdapter()
    }

    private val mRecordAdapter: CoinRecordAdapter by lazy {
        CoinRecordAdapter()
    }

    private val type by lazy(mode = LazyThreadSafetyMode.NONE) {
        arguments?.getInt("type", 0) ?: 0
    }

    override fun actionsOnViewInflate() {
        if (type == 0) fetchRecords(false) else fetchRanks(false)
    }

    override fun getLayoutId(): Int = R.layout.fragment_common_coin_sub

    override fun initFragment(view: View, savedInstanceState: Bundle?) {
        mBinding?.let { binding ->
            binding.refreshColor = R.color.colorAccent
            binding.refreshListener = SwipeRefreshLayout.OnRefreshListener {
                if (type == 0) fetchRecords()
                else fetchRanks()
            }

            binding.coinList.itemAnimator = null
            binding.adapter = if (type == 0) mRecordAdapter else mRankAdapter
            binding.divider = DividerItemDecoration(requireContext(), DividerItemDecoration.VERTICAL)

            binding.errorReload = ErrorReload {
                if (type == 0) fetchRecords()
                else fetchRanks()
            }
        }
    }

    private fun fetchRanks(isRefresh: Boolean = true) {
        mViewModel.fetchRankList {
            mBinding?.emptyStatus = true
        }

        mViewModel.rankNetState?.observe(this, Observer {
            when (it.state) {
                State.RUNNING -> injectStates(refreshing = true, loading = !isRefresh)

                State.SUCCESS -> injectStates()

                State.FAILED -> {
                    if (it.code == ERROR_CODE_INIT) injectStates(error = true)
                    else requireContext().toast(R.string.no_net_on_loading)
                }
            }
        })

        mViewModel.coinRanks?.observe(this, Observer {
            mRankAdapter.submitList(it)
        })
    }

    private fun fetchRecords(isRefresh: Boolean = true) {
        mViewModel.fetchRecordList {
            mBinding?.emptyStatus = true
        }

        mViewModel.rankNetState?.observe(this, Observer {
            when (it.state) {
                State.RUNNING -> injectStates(refreshing = true, loading = !isRefresh)

                State.SUCCESS -> injectStates()

                State.FAILED -> {
                    if (it.code == ERROR_CODE_INIT) injectStates(error = true)
                    else requireContext().toast(R.string.no_net_on_loading)
                }
            }
        })

        mViewModel.coinRecords?.observe(this, Observer {
            mRecordAdapter.submitList(it)
        })
    }

    fun scrollToTop() = mBinding?.coinList?.scrollToTop()

    private fun injectStates(refreshing: Boolean = false, loading: Boolean = false, error: Boolean = false) {
        mBinding?.let { binding ->
            binding.refreshing = refreshing
            binding.loadingStatus = loading
            binding.errorStatus = error
        }
    }

    companion object {
        private fun instance(type: Int) = CoinCommonSubFragment().apply {
            arguments = bundleOf(Pair("type", type))
        }

        fun recordInstance() = instance(0)

        fun rankInstance() = instance(1)
    }
}