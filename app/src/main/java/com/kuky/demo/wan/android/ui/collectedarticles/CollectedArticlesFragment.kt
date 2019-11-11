package com.kuky.demo.wan.android.ui.collectedarticles

import android.os.Bundle
import android.view.View
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.kuky.demo.wan.android.R
import com.kuky.demo.wan.android.base.*
import com.kuky.demo.wan.android.databinding.FragmentCollectedArticlesBinding
import com.kuky.demo.wan.android.ui.websitedetail.WebsiteDetailFragment
import com.kuky.demo.wan.android.ui.widget.ErrorReload
import org.jetbrains.anko.alert
import org.jetbrains.anko.noButton
import org.jetbrains.anko.okButton
import org.jetbrains.anko.toast

/**
 * @author kuky.
 * @description
 */
class CollectedArticlesFragment : BaseFragment<FragmentCollectedArticlesBinding>() {

    private val mViewModel by lazy {
        ViewModelProvider(requireActivity(), CollectedArticlesModelFactory(CollectedArticlesRepository()))
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
                        mViewModel.deleteCollectedArticle(data.id, data.originId,
                            { requireContext().toast("删除成功") }, { requireContext().toast(it) })
                    }
                }
                noButton { }
            }.show()
            true
        }

        mBinding.errorReload = ErrorReload { fetchCollectedArticleDatas() }

        fetchCollectedArticleDatas()
    }

    fun scrollToTop() = mBinding.collectedArticleList.scrollToTop()

    private fun fetchCollectedArticleDatas() {
        mViewModel.fetchCollectedArticleDatas { code, _ ->
            when (code) {
                PAGING_THROWABLE_LOAD_CODE_INITIAL -> mBinding.errorStatus = true

                PAGING_THROWABLE_LOAD_CODE_AFTER -> requireContext().toast("加载更多出错啦~请检查网络")
            }
        }

        mBinding.errorStatus = false
        mBinding.refreshing = true
        mViewModel.mArticles?.observe(requireActivity(), Observer {
            mAdapter.submitList(it)
            delayLaunch(1000) {
                mBinding.refreshing = false
            }
        })
    }
}