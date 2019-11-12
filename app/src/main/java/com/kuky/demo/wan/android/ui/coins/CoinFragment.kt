package com.kuky.demo.wan.android.ui.coins

import android.os.Bundle
import android.view.View
import com.kuky.demo.wan.android.R
import com.kuky.demo.wan.android.base.BaseFragment
import com.kuky.demo.wan.android.base.BaseFragmentPagerAdapter
import com.kuky.demo.wan.android.base.DoubleClickListener
import com.kuky.demo.wan.android.databinding.FragmentCoinsBinding

/**
 * @author kuky.
 * @description
 */
class CoinFragment : BaseFragment<FragmentCoinsBinding>() {

    private val mAdapter: BaseFragmentPagerAdapter by lazy {
        BaseFragmentPagerAdapter(
            childFragmentManager, arrayListOf(
                CoinCommonSubFragment.recordInstance(),
                CoinCommonSubFragment.rankInstance()
            ), arrayOf(
                resources.getString(R.string.coin_record),
                resources.getString(R.string.coin_rank)
            )
        )
    }

    override fun getLayoutId(): Int = R.layout.fragment_coins

    override fun initFragment(view: View, savedInstanceState: Bundle?) {
        mBinding.adapter = mAdapter
        mBinding.coinIndicator.setupWithViewPager(mBinding.coinVp)
        mBinding.gesture = DoubleClickListener(null, {
            (childFragmentManager.fragments[mBinding.coinVp.currentItem] as? CoinCommonSubFragment)?.scrollToTop()
        })
    }
}