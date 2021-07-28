package com.kuky.demo.wan.android.ui.usersharelist

import android.os.Bundle
import android.view.View
import androidx.navigation.fragment.findNavController
import androidx.paging.LoadState
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.kuky.demo.wan.android.R
import com.kuky.demo.wan.android.base.BaseFragment
import com.kuky.demo.wan.android.base.DoubleClickListener
import com.kuky.demo.wan.android.base.scrollToTop
import com.kuky.demo.wan.android.databinding.FragmentUserShareListBinding
import com.kuky.demo.wan.android.entity.UserArticleDetail
import com.kuky.demo.wan.android.listener.OnDialogFragmentDismissListener
import com.kuky.demo.wan.android.listener.OnItemClickListener
import com.kuky.demo.wan.android.listener.OnItemLongClickListener
import com.kuky.demo.wan.android.ui.app.AppViewModel
import com.kuky.demo.wan.android.ui.app.PagingLoadStateAdapter
import com.kuky.demo.wan.android.ui.usershared.UserSharedPagingAdapter
import com.kuky.demo.wan.android.ui.websitedetail.WebsiteDetailFragment
import com.kuky.demo.wan.android.widget.ErrorReload
import com.kuky.demo.wan.android.widget.RequestStatusCode
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.onCompletion
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.launch
import org.jetbrains.anko.alert
import org.jetbrains.anko.noButton
import org.jetbrains.anko.toast
import org.jetbrains.anko.yesButton
import org.koin.android.ext.android.inject
import org.koin.android.scope.AndroidScopeComponent
import org.koin.androidx.scope.fragmentScope
import org.koin.androidx.viewmodel.ext.android.sharedViewModel
import org.koin.androidx.viewmodel.ext.android.viewModel
import org.koin.core.scope.Scope

/**
 * @author kuky.
 * @description
 */
class UserShareListFragment : BaseFragment<FragmentUserShareListBinding>(), AndroidScopeComponent {

    override val scope: Scope by fragmentScope()

    private val mAppViewModel by sharedViewModel<AppViewModel>()

    private val mViewModel by viewModel<UserShareListViewModel>()

    private val mAdapter by inject<UserSharedPagingAdapter>()

    private val mShareDialog by inject<ShareArticleDialogFragment>()

    private var mShareJob: Job? = null

    override fun actionsOnViewInflate() = fetchSharedArticles()

    override fun getLayoutId(): Int = R.layout.fragment_user_share_list

    override fun initFragment(view: View, savedInstanceState: Bundle?) {
        mBinding?.run {
            refreshColor = R.color.colorAccent
            refreshListener = SwipeRefreshLayout.OnRefreshListener {
                mAdapter.refresh()
            }

            adapter = mAdapter.apply {
                addLoadStateListener { loadState ->
                    refreshing = loadState.refresh is LoadState.Loading
                    statusCode = when (loadState.refresh) {
                        is LoadState.Loading -> RequestStatusCode.Loading
                        is LoadState.Error -> RequestStatusCode.Error
                        else -> {
                            if (itemCount == 0) RequestStatusCode.Empty
                            else RequestStatusCode.Succeed
                        }
                    }
                }
            }.withLoadStateFooter(
                PagingLoadStateAdapter { mAdapter.retry() }
            )

            itemClick = OnItemClickListener { position, _ ->
                mAdapter.getItemData(position)?.let {
                    WebsiteDetailFragment.viewDetail(
                        findNavController(),
                        R.id.action_userShareListFragment_to_websiteDetailFragment,
                        it.link
                    )
                }
            }

            itemLongClick = OnItemLongClickListener { position, _ ->
                mAdapter.getItemData(position)?.let {
                    requireContext().alert("是否删除该分享") {
                        yesButton { _ -> removeShare(it) }
                        noButton { }
                    }.show()
                }
            }

            // 双击回顶部
            gesture = DoubleClickListener {
                doubleTap = {
                    articleList.scrollToTop()
                }
            }

            shareGesture = DoubleClickListener {
                singleTap = {
                    mShareDialog.apply {
                        onDialogFragmentDismissListener = OnDialogFragmentDismissListener { fetchSharedArticles() }
                    }.showAllowStateLoss(childFragmentManager, "share_art")
                }
            }

            errorReload = ErrorReload { mAdapter.retry() }
        }
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    private fun removeShare(it: UserArticleDetail) {
        launch {
            mViewModel.deleteAShare(it.id).catch {
                context?.toast(R.string.no_network)
            }.onStart {
                mAppViewModel.showLoading()
            }.onCompletion {
                mAppViewModel.dismissLoading()
            }.collectLatest {
                context?.toast(R.string.delete_succeed)
                fetchSharedArticles()
            }
        }
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    private fun fetchSharedArticles() {
        mShareJob?.cancel()
        mShareJob = launch {
            mViewModel.getSharedArticles()
                .catch { mBinding?.statusCode = RequestStatusCode.Error }
                .collectLatest { mAdapter.submitData(it) }
        }
    }
}