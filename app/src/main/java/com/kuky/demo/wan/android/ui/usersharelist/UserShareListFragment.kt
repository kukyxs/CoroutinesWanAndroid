package com.kuky.demo.wan.android.ui.usersharelist

import android.os.Bundle
import android.view.View
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.paging.PagedList
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.kuky.demo.wan.android.R
import com.kuky.demo.wan.android.base.*
import com.kuky.demo.wan.android.databinding.FragmentUserShareListBinding
import com.kuky.demo.wan.android.entity.UserArticleDetail
import com.kuky.demo.wan.android.ui.dialog.ShareArticleDialogFragment
import com.kuky.demo.wan.android.ui.shareduser.UserSharedArticleAdapter
import com.kuky.demo.wan.android.ui.websitedetail.WebsiteDetailFragment
import com.kuky.demo.wan.android.ui.widget.ErrorReload
import org.jetbrains.anko.alert
import org.jetbrains.anko.noButton
import org.jetbrains.anko.toast
import org.jetbrains.anko.yesButton

/**
 * @author kuky.
 * @description
 */
class UserShareListFragment : BaseFragment<FragmentUserShareListBinding>() {

    private val mAdapter: UserSharedArticleAdapter by lazy {
        UserSharedArticleAdapter()
    }

    private val mViewModel: UserShareListViewModel by lazy {
        ViewModelProvider(requireActivity(), UserShareListModelFactory(UserShareListRepository()))
            .get(UserShareListViewModel::class.java)
    }

    override fun getLayoutId(): Int = R.layout.fragment_user_share_list

    override fun initFragment(view: View, savedInstanceState: Bundle?) {
        mBinding.refreshColor = R.color.colorAccent
        mBinding.refreshListener = SwipeRefreshLayout.OnRefreshListener {
            fetchSharedArticles()
        }

        mBinding.adapter = mAdapter
        mBinding.itemClick = OnItemClickListener { position, _ ->
            mAdapter.getItemData(position)?.let {
                WebsiteDetailFragment.viewDetail(
                    mNavController,
                    R.id.action_userShareListFragment_to_websiteDetailFragment,
                    it.link
                )
            }
        }
        mBinding.itemLongClick = OnItemLongClickListener { position, _ ->
            mAdapter.getItemData(position)?.let {
                requireContext().alert("是否删除该分享") {
                    yesButton { _ ->
                        mViewModel.deleteAShare(it.id, {
                            requireContext().toast("删除成功")
                        }, { requireContext().toast(it) })
                    }
                    noButton { }
                }.show()
            }
            true
        }

        // 双击回顶部
        mBinding.gesture = DoubleClickListener(null, {
            mBinding.articleList.scrollToTop()
        })

        mBinding.shareGesture = DoubleClickListener({
            ShareArticleDialogFragment().show(childFragmentManager, "share_art")
        }, null)

        mBinding.errorReload = ErrorReload {
            fetchSharedArticles()
        }

        fetchSharedArticles()
    }

    private fun fetchSharedArticles() {
        mViewModel.fetchSharedArticles { code, _ ->
            when (code) {
                PAGING_THROWABLE_LOAD_CODE_INITIAL -> mBinding.errorStatus = true

                PAGING_THROWABLE_LOAD_CODE_AFTER -> requireContext().toast("加载更多数据出错啦~请检查网络")
            }
        }

        mBinding.refreshing = true
        mBinding.errorStatus = false
        mViewModel.articles?.observe(this, Observer<PagedList<UserArticleDetail>> {
            mAdapter.submitList(it)
            delayLaunch(1000) { mBinding.refreshing = false }
        })
    }
}