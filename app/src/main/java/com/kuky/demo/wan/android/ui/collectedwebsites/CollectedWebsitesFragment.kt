package com.kuky.demo.wan.android.ui.collectedwebsites

import android.os.Bundle
import android.view.View
import androidx.lifecycle.ViewModelProvider
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.kuky.demo.wan.android.R
import com.kuky.demo.wan.android.base.*
import com.kuky.demo.wan.android.databinding.FragmentCollectedWebsitesBinding
import com.kuky.demo.wan.android.ui.app.AppViewModel
import com.kuky.demo.wan.android.ui.websitedetail.WebsiteDetailFragment
import com.kuky.demo.wan.android.ui.widget.ErrorReload
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import org.jetbrains.anko.selector
import org.jetbrains.anko.toast

/**
 * @author kuky.
 * @description
 */
class CollectedWebsitesFragment : BaseFragment<FragmentCollectedWebsitesBinding>() {

    private val mAppViewModel by lazy { getSharedViewModel(AppViewModel::class.java) }

    private val mViewModel by lazy {
        ViewModelProvider(requireActivity(), CollectedWebsitesModelFactory(CollectedWebsitesRepository()))
            .get(CollectedWebsitesViewModel::class.java)
    }

    private var mFavouriteJob: Job? = null

    private val mAdapter by lazy { CollectedWebsitesAdapter() }

    private val editSelector by lazy { arrayListOf(resources.getString(R.string.del_website), resources.getString(R.string.edit_website)) }

    override fun actionsOnViewInflate() {
        fetchWebSitesData()
    }

    override fun getLayoutId(): Int = R.layout.fragment_collected_websites

    override fun initFragment(view: View, savedInstanceState: Bundle?) {
        mBinding?.let { binding ->
            binding.refreshColor = R.color.colorAccent
            binding.refreshListener = SwipeRefreshLayout.OnRefreshListener {
                fetchWebSitesData()
            }

            binding.adapter = mAdapter

            binding.listener = OnItemClickListener { position, _ ->
                mAdapter.getItemData(position)?.let {
                    WebsiteDetailFragment.viewDetail(
                        mNavController,
                        R.id.action_collectionFragment_to_websiteDetailFragment,
                        it.link
                    )
                }
            }

            binding.longListener = OnItemLongClickListener { position, _ ->
                mAdapter.getItemData(position)?.let { data ->
                    requireContext().selector(items = editSelector) { _, i ->
                        when (i) {
                            0 -> launch { removeFavouriteWebsite(data.id) }

                            1 -> CollectedWebsiteDialogFragment().apply {
                                editMode = true
                                injectWebsiteData(data)
                            }.show(childFragmentManager, "edit_website")
                        }
                    }
                }
            }

            binding.errorReload = ErrorReload { fetchWebSitesData() }

            binding.gesture = DoubleClickListener {
                singleTap = {
                    CollectedWebsiteDialogFragment().apply {
                        editMode = false
                        injectWebsiteData()
                        onDialogFragmentDismissListener = { fetchWebSitesData() }
                    }.showAllowStateLoss(childFragmentManager, "new_website")
                }
            }
        }
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    private suspend fun removeFavouriteWebsite(id: Int) {
        mAppViewModel.showLoading()
        mViewModel.deleteFavouriteWebsite(id)
            .catch {
                mAppViewModel.dismissLoading()
                context?.toast(R.string.no_network)
            }.collectLatest {
                mAppViewModel.dismissLoading()
                it.handleResult {
                    context?.toast(R.string.remove_favourite_succeed)
                }
            }
    }

    fun scrollToTop() = mBinding?.websiteList?.scrollToTop()

    @OptIn(ExperimentalCoroutinesApi::class)
    private fun fetchWebSitesData() {
        mFavouriteJob?.cancel()
        mFavouriteJob = launch {
            pageState(State.RUNNING)
            mViewModel.getWebsites().catch {
                pageState(State.FAILED)
            }.collectLatest {
                pageState(State.SUCCESS)
                mBinding?.emptyStatus = it.isNullOrEmpty()
                mAdapter.update(it)
            }
        }
    }

    private fun pageState(state: State) = mBinding?.run {
        refreshing = state == State.RUNNING
        loadingStatus = state == State.RUNNING
        errorStatus = state == State.FAILED
    }
}