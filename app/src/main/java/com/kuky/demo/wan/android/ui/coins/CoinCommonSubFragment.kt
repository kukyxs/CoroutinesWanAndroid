package com.kuky.demo.wan.android.ui.coins

import android.os.Bundle
import android.view.View
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.kuky.demo.wan.android.R
import com.kuky.demo.wan.android.base.*
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

    override fun getLayoutId(): Int = R.layout.fragment_common_coin_sub

    override fun initFragment(view: View, savedInstanceState: Bundle?) {
        val type = arguments?.getInt("type") ?: 0

        mBinding.refreshColor = R.color.colorAccent
        mBinding.refreshListener = SwipeRefreshLayout.OnRefreshListener {
            if (type == 0) fetchRecords()
            else fetchRanks()
        }

        mBinding.adapter = if (type == 0) mRecordAdapter else mRankAdapter
        mBinding.divider = DividerItemDecoration(requireContext(), DividerItemDecoration.VERTICAL)

        mBinding.errorReload = ErrorReload {
            if (type == 0) fetchRecords()
            else fetchRanks()
        }

        if (type == 0) fetchRecords() else fetchRanks()
    }

    private fun fetchRanks() {
        mViewModel.fetchRankList { code, _ ->
            when (code) {
                PAGING_THROWABLE_LOAD_CODE_INITIAL -> mBinding.errorStatus = true
                PAGING_THROWABLE_LOAD_CODE_AFTER -> requireContext().toast("加载更多出错啦~请检查网络")
            }
        }

        mBinding.errorStatus = false
        mBinding.refreshing = true
        mViewModel.coinRanks?.observe(this, Observer {
            mRankAdapter.submitList(it)
            delayLaunch(1000) {
                mBinding.refreshing = false
            }
        })
    }

    private fun fetchRecords() {
        mViewModel.fetchRecordList { code, _ ->
            when (code) {
                PAGING_THROWABLE_LOAD_CODE_INITIAL -> mBinding.errorStatus = true
                PAGING_THROWABLE_LOAD_CODE_AFTER -> requireContext().toast("加载更多出错啦~请检查网络")
            }
        }

        mBinding.errorStatus = false
        mBinding.refreshing = true
        mViewModel.coinRecords?.observe(this, Observer {
            mRecordAdapter.submitList(it)
            delayLaunch(1000) {
                mBinding.refreshing = false
            }
        })
    }

    fun scrollToTop() = mBinding.coinList.scrollToTop()

    companion object {
        private fun instance(type: Int) = CoinCommonSubFragment().apply {
            arguments = Bundle().apply {
                putInt("type", type)
            }
        }

        fun recordInstance() = instance(0)

        fun rankInstance() = instance(1)
    }
}