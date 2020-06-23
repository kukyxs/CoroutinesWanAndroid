package com.kuky.demo.wan.android.ui.coins

import android.os.Bundle
import android.view.View
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.kuky.demo.wan.android.R
import com.kuky.demo.wan.android.base.BaseFragment
import com.kuky.demo.wan.android.base.DoubleClickListener
import com.kuky.demo.wan.android.base.setupWithViewPager2
import com.kuky.demo.wan.android.databinding.FragmentCoinsBinding

/**
 * @author kuky.
 * @description
 */
class CoinFragment : BaseFragment<FragmentCoinsBinding>() {

    private val mPagerAdapter by lazy {
        object : FragmentStateAdapter(this) {
            override fun getItemCount() = 2

            override fun createFragment(position: Int) =
                if (position == 0) CoinCommonSubFragment.recordInstance()
                else CoinCommonSubFragment.rankInstance()
        }
    }

    override fun getLayoutId(): Int = R.layout.fragment_coins

    override fun initFragment(view: View, savedInstanceState: Bundle?) {
        val titles = mutableListOf(
            resources.getString(R.string.coin_record),
            resources.getString(R.string.coin_rank)
        )

        mBinding?.let { binding ->
            binding.coinVp.adapter = mPagerAdapter
            binding.coinIndicator.setupWithViewPager2(binding.coinVp, titles)
            binding.gesture = DoubleClickListener {
                doubleTap = {
                    (childFragmentManager.fragments[binding.coinVp.currentItem] as? CoinCommonSubFragment)?.scrollToTop()
                }
            }
        }
    }
}