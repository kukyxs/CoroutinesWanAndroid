package com.kuky.demo.wan.android.ui.hotproject

import android.os.Bundle
import android.view.View
import androidx.lifecycle.Observer
import androidx.paging.PagedList
import com.kuky.demo.wan.android.R
import com.kuky.demo.wan.android.base.BaseFragment
import com.kuky.demo.wan.android.base.PagingItemClickListener
import com.kuky.demo.wan.android.databinding.FragmentHotProjectBinding
import com.kuky.demo.wan.android.entity.ProjectDetailData
import com.kuky.demo.wan.android.ui.websitedetail.WebsiteDetailFragment

/**
 * @author kuky.
 * @description 首页项目模块界面
 */
class HotProjectFragment : BaseFragment<FragmentHotProjectBinding>() {

    private val mAdapter: HomeProjectAdapter by lazy { HomeProjectAdapter() }

    override fun getLayoutId(): Int = R.layout.fragment_hot_project

    override fun initFragment(view: View, savedInstanceState: Bundle?) {
        mBinding.adapter = mAdapter
        mBinding.itemClick = PagingItemClickListener { position, _ ->
            mAdapter.getItemData(position)?.let {
                WebsiteDetailFragment.viewDetail(
                    mNavController,
                    R.id.action_mainFragment_to_websiteDetailFragment,
                    it.link
                )
            }
        }

        val viewModel = getViewModel(HotProjectViewModel::class.java)

        viewModel.projects.observe(this, Observer<PagedList<ProjectDetailData>> {
            mAdapter.submitList(it)
        })
    }
}