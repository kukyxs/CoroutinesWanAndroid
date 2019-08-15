package com.kuky.demo.wan.android.ui.collectedarticles

import android.os.Bundle
import android.os.Handler
import android.view.View
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.kuky.demo.wan.android.R
import com.kuky.demo.wan.android.base.BaseFragment
import com.kuky.demo.wan.android.base.OnItemClickListener
import com.kuky.demo.wan.android.base.OnItemLongClickListener
import com.kuky.demo.wan.android.databinding.FragmentCollectedArticlesBinding
import com.kuky.demo.wan.android.ui.websitedetail.WebsiteDetailFragment
import org.jetbrains.anko.alert
import org.jetbrains.anko.noButton
import org.jetbrains.anko.okButton
import org.jetbrains.anko.toast

/**
 * @author kuky.
 * @description
 */
class CollectedArticlesFragment : BaseFragment<FragmentCollectedArticlesBinding>() {
    companion object {
        private val mHandler = Handler()
    }

    private val mViewModel by lazy {
        ViewModelProvider(requireActivity(), CollectedArticlesFactory(CollectedArticlesRepository()))
            .get(CollectedArticlesViewModel::class.java)
    }
    private val mAdapter by lazy { CollectedArticlesAdapter() }

    override fun getLayoutId(): Int = R.layout.fragment_collected_articles

    override fun initFragment(view: View, savedInstanceState: Bundle?) {
        mBinding.refreshColor = R.color.colorAccent
        mBinding.refreshListener = SwipeRefreshLayout.OnRefreshListener {
            fetchCollectedArticleDatas()
        }

        mBinding.adapter = mAdapter
        mBinding.listener = OnItemClickListener { position, _ ->
            mAdapter.getItemData(position)?.let { data ->
                WebsiteDetailFragment.viewDetail(
                    mNavController,
                    R.id.action_collectionFragment_to_websiteDetailFragment,
                    data.link
                )
            }
        }
        mBinding.longListener = OnItemLongClickListener { position, _ ->
            requireActivity().alert("是否删除本条收藏？") {
                okButton {
                    mAdapter.getItemData(position)?.let { data ->
                        mViewModel.deleteCollectedArticle(data.id, data.originId, { requireContext().toast("删除成功") }, {
                            requireContext().toast(it)
                        })
                    }
                }
                noButton { }
            }.show()
            true
        }
        fetchCollectedArticleDatas()
    }

    private fun fetchCollectedArticleDatas() {
        mViewModel.fetchCollectedArticleDatas()
        mBinding.refreshing = true
        mViewModel.mArticles?.observe(requireActivity(), Observer {
            mAdapter.submitList(it)
            mHandler.postDelayed({
                mBinding.refreshing = false
                // 延时来获取数据
                mBinding.dataNull = it.isEmpty()
            }, 500L)
        })
    }
}