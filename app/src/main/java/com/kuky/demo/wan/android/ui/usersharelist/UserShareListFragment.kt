package com.kuky.demo.wan.android.ui.usersharelist

import android.os.Bundle
import android.view.View
import androidx.lifecycle.ViewModelProvider
import androidx.paging.ExperimentalPagingApi
import androidx.paging.LoadState
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.kuky.demo.wan.android.R
import com.kuky.demo.wan.android.base.*
import com.kuky.demo.wan.android.databinding.FragmentUserShareListBinding
import com.kuky.demo.wan.android.ui.app.AppViewModel
import com.kuky.demo.wan.android.ui.app.PagingLoadStateAdapter
import com.kuky.demo.wan.android.ui.usershared.UserSharedPagingAdapter
import com.kuky.demo.wan.android.ui.websitedetail.WebsiteDetailFragment
import com.kuky.demo.wan.android.utils.Injection
import com.kuky.demo.wan.android.widget.ErrorReload
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

/**
 * @author kuky.
 * @description
 */
class UserShareListFragment : BaseFragment<FragmentUserShareListBinding>() {

    @OptIn(ExperimentalPagingApi::class)
    private val mAdapter by lazy {
        UserSharedPagingAdapter().apply {
            addLoadStateListener { loadState ->
                mBinding?.refreshing = loadState.refresh is LoadState.Loading
                mBinding?.loadingStatus = loadState.refresh is LoadState.Loading
                mBinding?.errorStatus = loadState.refresh is LoadState.Error
            }

            addDataRefreshListener {
                mBinding?.emptyStatus = itemCount == 0
            }
        }
    }

    private val mAppViewModel by lazy {
        getSharedViewModel(AppViewModel::class.java)
    }

    private val mViewModel by lazy {
        ViewModelProvider(requireActivity(), Injection.provideUserShareListViewModelFactory())
            .get(UserShareListViewModel::class.java)
    }

    private var mShareJob: Job? = null

    override fun actionsOnViewInflate() {
        fetchSharedArticles()
    }

    override fun getLayoutId(): Int = R.layout.fragment_user_share_list

    @OptIn(ExperimentalCoroutinesApi::class)
    override fun initFragment(view: View, savedInstanceState: Bundle?) {
        mBinding?.let { binding ->
            binding.refreshColor = R.color.colorAccent
            binding.refreshListener = SwipeRefreshLayout.OnRefreshListener {
                mAdapter.refresh()
            }

            binding.adapter = mAdapter.withLoadStateFooter(PagingLoadStateAdapter { mAdapter.retry() })
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
                        noButton { }
                    }.show()
                }
            }

            // 双击回顶部
            binding.gesture = DoubleClickListener {
                doubleTap = {
                    binding.articleList.scrollToTop()
                }
            }

            binding.shareGesture = DoubleClickListener {
                singleTap = {
                    ShareArticleDialogFragment().apply {
                        onDialogFragmentDismissListener = { fetchSharedArticles() }
                    }.showAllowStateLoss(childFragmentManager, "share_art")
                }
            }

            binding.errorReload = ErrorReload { mAdapter.retry() }
        }
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    private fun fetchSharedArticles() {
        mShareJob?.cancel()
        mShareJob = launch {
            mViewModel.getSharedArticles()
                .catch { mBinding?.errorStatus = true }
                .collectLatest { mAdapter.submitData(it) }
        }
    }
}