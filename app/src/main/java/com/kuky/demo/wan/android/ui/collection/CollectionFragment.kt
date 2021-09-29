package com.kuky.demo.wan.android.ui.collection

import android.os.Bundle
import android.view.View
import com.kuky.demo.wan.android.R
import com.kuky.demo.wan.android.base.BaseFragment
import com.kuky.demo.wan.android.base.DoubleClickListener
import com.kuky.demo.wan.android.base.ViewPager2FragmentAdapter
import com.kuky.demo.wan.android.base.setupWithViewPager2
import com.kuky.demo.wan.android.databinding.FragmentCollectionBinding
import com.kuky.demo.wan.android.ui.collectedarticles.CollectedArticlesFragment
import com.kuky.demo.wan.android.ui.collectedwebsites.CollectedWebsitesFragment
import org.koin.android.ext.android.inject
import org.koin.android.scope.AndroidScopeComponent
import org.koin.androidx.scope.fragmentScope
import org.koin.core.parameter.parametersOf
import org.koin.core.scope.Scope

/**
 * @author kuky.
 * @description 个人收藏页面
 */
class CollectionFragment : BaseFragment<FragmentCollectionBinding>(), AndroidScopeComponent {
    override val scope: Scope by fragmentScope()

    private val mCollectedArticleFragment by inject<CollectedArticlesFragment>()

    private val mCollectedWebsitesFragment by inject<CollectedWebsitesFragment>()

    private val mPagerAdapter by inject<ViewPager2FragmentAdapter> {
        parametersOf(this, mutableListOf(mCollectedArticleFragment, mCollectedWebsitesFragment))
    }

    override fun getLayoutId(): Int = R.layout.fragment_collection

    override fun initFragment(view: View, savedInstanceState: Bundle?) {
        mBinding.run {
            // Todo("if isSaveEnabled == true, will throw IllegalException else child fragment will destroyed and recreated when nav back")
            // when display with navigation, set `viewPager2.isSaveEnabled = false` to resolve
            // IllegalException("Expected the adapter to be 'fresh' while restoring state.")
            // but it will make fragment destroyed and recreated
            collectionVp.isSaveEnabled = false
            collectionVp.offscreenPageLimit = 2
            collectionVp.adapter = mPagerAdapter
            collectionIndicator.setupWithViewPager2(collectionVp, intArrayOf(R.string.articles, R.string.websites))
            gesture = DoubleClickListener {
                doubleTap = {
                    when (collectionVp.currentItem) {
                        0 -> (childFragmentManager.fragments[0] as? CollectedArticlesFragment)?.scrollToTop()

                        1 -> (childFragmentManager.fragments[1] as? CollectedWebsitesFragment)?.scrollToTop()
                    }
                }
            }
        }
    }
}