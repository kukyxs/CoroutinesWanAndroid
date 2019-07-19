package com.kuky.demo.wan.android.ui.collectedarticles

import android.os.Bundle
import android.view.View
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import com.kuky.demo.wan.android.R
import com.kuky.demo.wan.android.base.BaseFragment
import com.kuky.demo.wan.android.databinding.FragmentCollectedArticlesBinding

/**
 * @author kuky.
 * @description
 */
class CollectedArticlesFragment : BaseFragment<FragmentCollectedArticlesBinding>() {
    private val viewModel by lazy {
        ViewModelProviders.of(requireActivity(), CollectedArticlesFactory(CollectedArticlesRepository()))
            .get(CollectedArticlesViewModel::class.java)
    }
    private val mAdapter by lazy { CollectedArticlesAdapter() }

    override fun getLayoutId(): Int = R.layout.fragment_collected_articles

    override fun initFragment(view: View, savedInstanceState: Bundle?) {
//        mBinding.adapter = mAdapter
//        mBinding.setListener { position, _ ->
//
//        }
//        mBinding.setLongListener { position, _ ->
//            return@setLongListener true
//        }
        viewModel.articles?.observe(requireActivity(), Observer {
            mAdapter.submitList(it)
        })
    }
}