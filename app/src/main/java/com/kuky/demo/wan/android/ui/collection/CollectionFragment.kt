package com.kuky.demo.wan.android.ui.collection

import android.os.Bundle
import android.view.View
import androidx.annotation.IdRes
import androidx.navigation.NavController
import com.kuky.demo.wan.android.R
import com.kuky.demo.wan.android.base.BaseFragment
import com.kuky.demo.wan.android.base.BaseFragmentPagerAdapter
import com.kuky.demo.wan.android.databinding.FragmentCollectionBinding
import com.kuky.demo.wan.android.ui.collectedarticles.CollectedArticlesFragment
import com.kuky.demo.wan.android.ui.collectedwebsites.CollectedWebsitesFragment
import kotlinx.android.synthetic.main.fragment_collection.view.*

/**
 * @author kuky.
 * @description 个人收藏页面
 */
class CollectionFragment : BaseFragment<FragmentCollectionBinding>() {

    private val mAdapter: BaseFragmentPagerAdapter by lazy {
        BaseFragmentPagerAdapter(
            childFragmentManager,
            arrayListOf(
                CollectedArticlesFragment(),
                CollectedWebsitesFragment()
            ),
            arrayOf("文章", "网址")
        )
    }

    override fun getLayoutId(): Int = R.layout.fragment_collection

    override fun initFragment(view: View, savedInstanceState: Bundle?) {
        mBinding.adapter = mAdapter
        mBinding.current = arguments?.getInt("position", 0) ?: 0
        view.collection_indicator.setupWithViewPager(view.collection_vp)
    }

    companion object {
        fun viewCollections(controller: NavController, @IdRes navId: Int, position: Int = 0) {
            controller.navigate(navId, Bundle().apply {
                putInt("position", position)
            })
        }
    }
}