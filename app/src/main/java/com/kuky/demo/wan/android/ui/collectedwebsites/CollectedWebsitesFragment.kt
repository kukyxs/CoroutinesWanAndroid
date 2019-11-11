package com.kuky.demo.wan.android.ui.collectedwebsites

import android.os.Bundle
import android.view.View
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.kuky.demo.wan.android.R
import com.kuky.demo.wan.android.base.*
import com.kuky.demo.wan.android.databinding.FragmentCollectedWebsitesBinding
import com.kuky.demo.wan.android.entity.WebsiteData
import com.kuky.demo.wan.android.ui.dialog.CollectedWebsiteDialogFragment
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
class CollectedWebsitesFragment : BaseFragment<FragmentCollectedWebsitesBinding>() {

    private val mViewModel by lazy {
        ViewModelProvider(requireActivity(), CollectedWebsitesModelFactory(CollectedWebsitesRepository()))
            .get(CollectedWebsitesViewModel::class.java)
    }
    private val mAdapter by lazy { CollectedWebsitesAdapter() }

    override fun getLayoutId(): Int = R.layout.fragment_collected_websites

    override fun initFragment(view: View, savedInstanceState: Bundle?) {
        mBinding.refreshColor = R.color.colorAccent
        mBinding.refreshListener = SwipeRefreshLayout.OnRefreshListener {
            fetchWebSitesData()
        }

        mBinding.fragment = this
        mBinding.adapter = mAdapter
        mBinding.listener = OnItemClickListener { position, _ ->
            mAdapter.getItemData(position)?.let {
                WebsiteDetailFragment.viewDetail(
                    mNavController,
                    R.id.action_collectionFragment_to_websiteDetailFragment,
                    it.link
                )
            }
        }
        mBinding.longListener = OnItemLongClickListener { position, _ ->
            mAdapter.getItemData(position)?.let { data ->
                requireActivity().alert("是否删除本条收藏？") {
                    yesButton {
                        mViewModel.deleteWebsite(data.id, {
                            requireContext().toast("删除成功")
                            mAdapter.removeItem(position)
                        }, {
                            requireContext().toast(it)
                        })
                    }
                    noButton { it.dismiss() }
                }.show()
            }
            true
        }

        mBinding.errorReload = ErrorReload { fetchWebSitesData() }

        mBinding.gesture = DoubleClickListener({
            CollectedWebsiteDialogFragment().show(childFragmentManager, "collectedWebsite")
        }, null)

        fetchWebSitesData()
    }

    fun scrollToTop() = mBinding.websiteList.scrollToTop()

    private fun fetchWebSitesData() {
        mViewModel.fetchWebSitesData {
            mBinding.errorStatus = true
        }

        mBinding.errorStatus = false
        mBinding.refreshing = true
        mViewModel.mWebsitesData.observe(this, Observer {
            mAdapter.update(it as MutableList<WebsiteData>?)
            delayLaunch(1000) {
                mBinding.refreshing = false
            }
        })
    }
}