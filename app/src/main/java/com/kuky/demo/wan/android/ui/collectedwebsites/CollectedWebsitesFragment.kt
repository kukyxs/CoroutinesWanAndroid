package com.kuky.demo.wan.android.ui.collectedwebsites

import android.os.Bundle
import android.view.View
import androidx.lifecycle.Observer
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.kuky.demo.wan.android.R
import com.kuky.demo.wan.android.base.BaseFragment
import com.kuky.demo.wan.android.base.DoubleClickListener
import com.kuky.demo.wan.android.base.UiState
import com.kuky.demo.wan.android.base.scrollToTop
import com.kuky.demo.wan.android.databinding.FragmentCollectedWebsitesBinding
import com.kuky.demo.wan.android.extension.handleResult
import com.kuky.demo.wan.android.extension.pageStateByUiState
import com.kuky.demo.wan.android.listener.OnItemClickListener
import com.kuky.demo.wan.android.listener.OnItemLongClickListener
import com.kuky.demo.wan.android.ui.app.AppViewModel
import com.kuky.demo.wan.android.ui.websitedetail.WebsiteDetailFragment
import com.kuky.demo.wan.android.widget.ErrorReload
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.*
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
        mBinding.run {
            refreshColor = R.color.colorAccent
            refreshListener = SwipeRefreshLayout.OnRefreshListener { fetchWebSitesData() }

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
                            0 -> launch { mViewModel.deleteFavouriteWebsite(data.id) }

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

        lifecycleScope.launchWhenCreated {
            mViewModel.uiState.collect { mBinding.statusCode = it.pageStateByUiState() }
        }

        lifecycleScope.launchWhenCreated {
            mViewModel.removeState.collect {
                when (it) {
                    is UiState.Error -> context?.toast(R.string.no_network)
                    UiState.Loading -> mAppViewModel.showLoading()
                    else -> mAppViewModel.dismissLoading()
                }
            }
        }
    }

    fun scrollToTop() = mBinding.websiteList.scrollToTop()

    private fun fetchWebSitesData() {
        mFavouriteJob?.cancel()
        mFavouriteJob = lifecycleScope.launchWhenCreated {
            mViewModel.getWebsites().collect { mAdapter.update(it) }
        }
    }
}