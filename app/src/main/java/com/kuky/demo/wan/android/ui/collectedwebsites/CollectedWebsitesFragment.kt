package com.kuky.demo.wan.android.ui.collectedwebsites

import android.os.Bundle
import android.view.View
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import com.kuky.demo.wan.android.R
import com.kuky.demo.wan.android.base.BaseFragment
import com.kuky.demo.wan.android.databinding.FragmentCollectedWebsitesBinding
import com.kuky.demo.wan.android.entity.WebsiteData
import com.kuky.demo.wan.android.ui.dialog.CollectedWebsiteDialogFragment
import com.kuky.demo.wan.android.ui.websitedetail.WebsiteDetailFragment
import org.jetbrains.anko.alert
import org.jetbrains.anko.noButton
import org.jetbrains.anko.toast
import org.jetbrains.anko.yesButton

/**
 * @author kuky.
 * @description
 */
class CollectedWebsitesFragment : BaseFragment<FragmentCollectedWebsitesBinding>() {
    private val viewModel by lazy {
        ViewModelProviders.of(requireActivity(), CollectedWebsitesFactory(CollectedWebsitesRepository()))
            .get(CollectedWebsitesViewModel::class.java)
    }
    private val mAdapter by lazy { CollectedWebsitesAdapter() }

    override fun getLayoutId(): Int = R.layout.fragment_collected_websites

    override fun initFragment(view: View, savedInstanceState: Bundle?) {
        mBinding.fragment = this
        mBinding.adapter = mAdapter
        mBinding.setListener { position, _ ->
            mAdapter.getItemData(position)?.let {
                WebsiteDetailFragment.viewDetail(
                    mNavController,
                    R.id.action_collectionFragment_to_websiteDetailFragment,
                    it.link
                )
            }
        }
        mBinding.setLongListener { position, _ ->
            mAdapter.getItemData(position)?.let { data ->
                requireActivity().alert("是否删除本条收藏？") {
                    yesButton {
                        viewModel.deleteWebsite(data.id, {
                            requireContext().toast("删除成功")
                        }, {
                            requireContext().toast(it)
                        })
                    }
                    noButton { it.dismiss() }
                }.show()
            }
            true
        }
        viewModel.fetchWebSitesData()
        viewModel.mWebsitesData.observe(this, Observer {
            mAdapter.update(it as MutableList<WebsiteData>?)
        })
    }

    fun addCollectedWebsites(view: View) {
        CollectedWebsiteDialogFragment().show(childFragmentManager, "collectedWebsite")
    }
}