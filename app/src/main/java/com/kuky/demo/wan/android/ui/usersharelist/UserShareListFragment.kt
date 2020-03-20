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

    override fun actionsOnViewInflate() {
        fetchSharedArticles(false)
    }

    override fun getLayoutId(): Int = R.layout.fragment_user_share_list

    override fun initFragment(view: View, savedInstanceState: Bundle?) {
        mBinding?.let { binding ->
            binding.refreshColor = R.color.colorAccent
            binding.refreshListener = SwipeRefreshLayout.OnRefreshListener {
                fetchSharedArticles()
            }

            binding.adapter = mAdapter
            binding.itemClick = OnItemClickListener { position, _ ->
                mAdapter.getItemData(position)?.let {
                    WebsiteDetailFragment.viewDetail(
                        mNavController,
                        R.id.action_userShareListFragment_to_websiteDetailFragment,
                        it.link
                    )
                }
            }
            binding.itemLongClick = OnItemLongClickListener { position, _ ->
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
            }

            // 双击回顶部
            binding.gesture = DoubleClickListener(null, {
                binding.articleList.scrollToTop()
            })

            binding.shareGesture = DoubleClickListener({
                ShareArticleDialogFragment().showAllowStateLoss(childFragmentManager, "share_art")
            }, null)

            binding.errorReload = ErrorReload {
                fetchSharedArticles()
            }
        }
    }

    private fun fetchSharedArticles(isRefresh: Boolean = true) {
        mViewModel.fetchSharedArticles {
            mBinding?.emptyStatus = true
        }

        mViewModel.netState?.observe(this, Observer {
            when (it.state) {
                State.RUNNING -> injectStates(refreshing = true, loading = !isRefresh)

                State.SUCCESS -> injectStates()

                State.FAILED -> {
                    if (it.code == ERROR_CODE_INIT) injectStates(error = true)
                    else requireContext().toast(R.string.no_net_on_loading)
                }
            }
        })

        mBinding?.refreshing = true
        mBinding?.errorStatus = false
        mViewModel.articles?.observe(this, Observer<PagedList<UserArticleDetail>> {
            mAdapter.submitList(it)
        })
    }

    private fun injectStates(refreshing: Boolean = false, loading: Boolean = false, error: Boolean = false) {
        mBinding?.let { binding ->
            binding.refreshing = refreshing
            binding.loadingStatus = loading
            binding.errorStatus = error
        }
    }
}