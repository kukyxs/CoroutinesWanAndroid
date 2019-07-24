package com.kuky.demo.wan.android.ui.collectedarticles

import android.os.Bundle
import android.view.View
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import com.kuky.demo.wan.android.R
import com.kuky.demo.wan.android.base.BaseFragment
import com.kuky.demo.wan.android.databinding.FragmentCollectedArticlesBinding
import com.kuky.demo.wan.android.ui.websitedetail.WebsiteDetailFragment
import org.jetbrains.anko.alert
import org.jetbrains.anko.noButton
import org.jetbrains.anko.okButton

/**
 * @author kuky.
 * @description
 */
class CollectedArticlesFragment : BaseFragment<FragmentCollectedArticlesBinding>() {
    private val viewModel by lazy {
        ViewModelProviders.of(requireActivity(), CollectedArticlesFactory(CollectedArticlesRepository()))
            .get(CollectedArticlesViewModel::class.java)
    }
    private val mAdapter by lazy { CollectedArticlesAdapter() }

    override fun getLayoutId(): Int = R.layout.fragment_collected_articles

    override fun initFragment(view: View, savedInstanceState: Bundle?) {
        mBinding.adapter = mAdapter
        mBinding.setListener { position, _ ->
            mAdapter.getItemData(position)?.let { data ->
                WebsiteDetailFragment.viewDetail(
                    mNavController,
                    R.id.action_collectionFragment_to_websiteDetailFragment,
                    data.link
                )
            }
        }
        mBinding.setLongListener { position, _ ->
            requireActivity().alert("是否删除本条收藏？") {
                okButton {
                    mAdapter.getItemData(position)?.let { data ->
                        viewModel.deleteCollectedArticle(data.id, data.originId) {
                            // TODO("目前根据官方文档，通过 dataSource.invalidate 刷新 Paging 数据")
                            viewModel.articles?.value?.dataSource?.invalidate()
                        }
                    }
                }
                noButton { }
            }.show()
            return@setLongListener true
        }
        viewModel.fetchCollectedArticleDatas()
        viewModel.articles?.observe(requireActivity(), Observer {
            mAdapter.submitList(it)
        })
    }
}