package com.kuky.demo.wan.android.ui.collectedwebsites

import android.os.Bundle
import android.view.View
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.kuky.demo.wan.android.R
import com.kuky.demo.wan.android.base.BaseFragment
import com.kuky.demo.wan.android.base.DoubleClickListener
import com.kuky.demo.wan.android.base.handleResult
import com.kuky.demo.wan.android.base.scrollToTop
import com.kuky.demo.wan.android.databinding.FragmentCollectedWebsitesBinding
import com.kuky.demo.wan.android.listener.OnItemClickListener
import com.kuky.demo.wan.android.listener.OnItemLongClickListener
import com.kuky.demo.wan.android.ui.app.AppViewModel
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
import org.jetbrains.anko.selector
import org.jetbrains.anko.toast
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
class CollectedWebsitesFragment : BaseFragment<FragmentCollectedWebsitesBinding>(), AndroidScopeComponent {

    override val scope: Scope by fragmentScope()

    private val mAppViewModel by sharedViewModel<AppViewModel>()

    private val mViewModel by viewModel<CollectedWebsitesViewModel>()

    private val mAdapter by inject<CollectedWebsitesAdapter>()

    private val editSelector by lazy { arrayListOf(resources.getString(R.string.del_website), resources.getString(R.string.edit_website)) }

    private var mFavouriteJob: Job? = null

    override fun actionsOnViewInflate() {
        fetchWebSitesData()

        mAppViewModel.reloadCollectWebsite.observe(this, Observer {
            if (it) fetchWebSitesData()
        })
    }

    override fun getLayoutId(): Int = R.layout.fragment_collected_websites

    override fun initFragment(view: View, savedInstanceState: Bundle?) {
        mBinding?.run {
            refreshColor = R.color.colorAccent
            refreshListener = SwipeRefreshLayout.OnRefreshListener {
                fetchWebSitesData()
            }

            adapter = mAdapter

            listener = OnItemClickListener { position, _ ->
                mAdapter.getItemData(position)?.let {
                    WebsiteDetailFragment.viewDetail(
                        findNavController(),
                        R.id.action_collectionFragment_to_websiteDetailFragment,
                        it.link
                    )
                }
            }

            longListener = OnItemLongClickListener { position, _ ->
                mAdapter.getItemData(position)?.let { data ->
                    context?.selector(items = editSelector) { _, i ->
                        when (i) {
                            0 -> launch { removeFavouriteWebsite(data.id) }

                            1 -> CollectedWebsiteDialogFragment
                                .createCollectedDialog(true, data.id, data.name, data.link)
                                .showAllowStateLoss(childFragmentManager, "edit_website")
                        }
                    }
                }
            }

            errorReload = ErrorReload { fetchWebSitesData() }

            gesture = DoubleClickListener {
                singleTap = {
                    CollectedWebsiteDialogFragment
                        .createCollectedDialog(false)
                        .showAllowStateLoss(childFragmentManager, "new_website")
                }
            }
        }
    }

    fun scrollToTop() = mBinding?.websiteList?.scrollToTop()

    @OptIn(ExperimentalCoroutinesApi::class)
    private suspend fun removeFavouriteWebsite(id: Int) {
        mViewModel.deleteFavouriteWebsite(id).catch {
            context?.toast(R.string.no_network)
        }.onStart {
            mAppViewModel.showLoading()
        }.onCompletion {
            mAppViewModel.dismissLoading()
        }.collectLatest {
            it.handleResult {
                context?.toast(R.string.remove_favourite_succeed)
                mAppViewModel.reloadCollectWebsite.postValue(true)
            }
        }
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    private fun fetchWebSitesData() {
        mFavouriteJob?.cancel()
        mFavouriteJob = launch {
            mViewModel.getWebsites().catch {
                mBinding?.statusCode = RequestStatusCode.Error
            }.onStart {
                mBinding?.refreshing = true
                mBinding?.statusCode = RequestStatusCode.Loading
            }.collectLatest {
                mAdapter.update(it)
                mBinding?.refreshing = false
                mBinding?.statusCode = if (it.isNullOrEmpty())
                    RequestStatusCode.Empty else RequestStatusCode.Succeed
            }
        }
    }
}