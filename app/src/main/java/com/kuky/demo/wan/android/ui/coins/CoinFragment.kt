package com.kuky.demo.wan.android.ui.coins

import android.os.Bundle
import android.view.View
import com.kuky.demo.wan.android.R
import com.kuky.demo.wan.android.base.BaseFragment
import com.kuky.demo.wan.android.base.BaseViewPager2FragmentAdapter
import com.kuky.demo.wan.android.base.DoubleClickListener
import com.kuky.demo.wan.android.base.setupWithViewPager2
import com.kuky.demo.wan.android.databinding.FragmentCoinsBinding
import org.koin.android.ext.android.inject
import org.koin.core.parameter.parametersOf

/**
 * @author kuky.
 * @description
 */
class CoinFragment : BaseFragment<FragmentCoinsBinding>() {

    private val mRecordPage by inject<CoinCommonSubFragment> { parametersOf(0) }

    private val mRankPage by inject<CoinCommonSubFragment> { parametersOf(1) }

    private val mPagerAdapter by lazy {
        BaseViewPager2FragmentAdapter(this, mutableListOf(mRecordPage, mRankPage))
    }

    override fun getLayoutId(): Int = R.layout.fragment_coins

    override fun initFragment(view: View, savedInstanceState: Bundle?) {
        val titles = mutableListOf(
            resources.getString(R.string.coin_record),
            resources.getString(R.string.coin_rank)
        )

        mBinding?.run {
            coinVp.adapter = mPagerAdapter
            coinVp.offscreenPageLimit = 2
            coinIndicator.setupWithViewPager2(coinVp, titles)
            gesture = DoubleClickListener {
                doubleTap = {
                    (childFragmentManager.fragments[coinVp.currentItem] as? CoinCommonSubFragment)?.scrollToTop()
                }
            }
        }
    }
}