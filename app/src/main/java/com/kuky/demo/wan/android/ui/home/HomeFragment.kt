package com.kuky.demo.wan.android.ui.home

import android.os.Bundle
import android.view.View
import androidx.lifecycle.Observer
import androidx.paging.PagedList
import com.kuky.demo.wan.android.R
import com.kuky.demo.wan.android.base.BaseFragment
import com.kuky.demo.wan.android.base.OnItemClickListener
import com.kuky.demo.wan.android.databinding.FragmentHomeBinding
import com.kuky.demo.wan.android.entity.ArticleDetail
import com.kuky.demo.wan.android.ui.websitedetail.WebsiteDetailFragment
import kotlinx.android.synthetic.main.fragment_home.*

/**
 * @author kuky.
 * @description 主页面首页模块界面
 */
class HomeFragment : BaseFragment<FragmentHomeBinding>() {

    private val mAdapter: HomeArticleAdapter by lazy { HomeArticleAdapter() }

    override fun getLayoutId(): Int = R.layout.fragment_home

    override fun initFragment(view: View, savedInstanceState: Bundle?) {
        mBinding.adapter = mAdapter
        mBinding.itemClick = OnItemClickListener { position, _ ->
            mAdapter.getItemData(position)?.let {
                WebsiteDetailFragment.viewDetail(
                    mNavController,
                    R.id.action_mainFragment_to_websiteDetailFragment,
                    it.link
                )
            }
        }

        article_list.setHasFixedSize(true)

        val viewModel = getViewModel(HomeArticleViewModel::class.java)

        viewModel.articles.observe(this, Observer<PagedList<ArticleDetail>> {
            mAdapter.submitList(it)
        })
    }
}