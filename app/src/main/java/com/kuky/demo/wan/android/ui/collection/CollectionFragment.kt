package com.kuky.demo.wan.android.ui.collection

import android.os.Bundle
import android.view.View
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.kuky.demo.wan.android.R
import com.kuky.demo.wan.android.base.BaseFragment
import com.kuky.demo.wan.android.base.DoubleClickListener
import com.kuky.demo.wan.android.base.setupWithViewPager2
import com.kuky.demo.wan.android.databinding.FragmentCollectionBinding
import com.kuky.demo.wan.android.ui.collectedarticles.CollectedArticlesFragment
import com.kuky.demo.wan.android.ui.collectedwebsites.CollectedWebsitesFragment

/**
 * @author kuky.
 * @description 个人收藏页面
 */
class CollectionFragment : BaseFragment<FragmentCollectionBinding>() {

    private val mPagerAdapter by lazy {
        object : FragmentStateAdapter(this) {
            override fun getItemCount() = 2

            override fun createFragment(position: Int) =
                if (position == 0) CollectedArticlesFragment()
                else CollectedWebsitesFragment()
        }
    }

    override fun getLayoutId(): Int = R.layout.fragment_collection

    override fun initFragment(view: View, savedInstanceState: Bundle?) {
        mBinding?.let { binding ->
            // when display with navigation, set `viewPager2.isSaveEnabled = false` to resolve
            // IllegalException("Expected the adapter to be 'fresh' while restoring state.")
            binding.collectionVp.isSaveEnabled = false
            binding.collectionVp.adapter = mPagerAdapter
            binding.collectionIndicator.setupWithViewPager2(binding.collectionVp, mutableListOf("文章", "网址"))
            binding.gesture = DoubleClickListener {
                doubleTap = {
                    when (binding.collectionVp.currentItem) {
                        0 -> (childFragmentManager.fragments[0] as? CollectedArticlesFragment)?.scrollToTop()

                        1 -> (childFragmentManager.fragments[1] as? CollectedWebsitesFragment)?.scrollToTop()
                    }
                }
            }
        }
    }
}