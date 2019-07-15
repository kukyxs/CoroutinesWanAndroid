package com.kuky.demo.wan.android.ui.system

import android.os.Bundle
import android.view.View
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import com.kuky.demo.wan.android.R
import com.kuky.demo.wan.android.base.BaseFragment
import com.kuky.demo.wan.android.base.OnItemClickListener
import com.kuky.demo.wan.android.databinding.FragmentKnowledgeSystemBinding
import com.kuky.demo.wan.android.ui.dialog.KnowledgeSystemDialogFragment
import com.kuky.demo.wan.android.ui.websitedetail.WebsiteDetailFragment
import com.kuky.demo.wan.android.ui.wxchapterlist.WxChapterListAdapter
import kotlinx.android.synthetic.main.fragment_knowledge_system.*

/**
 * @author kuky.
 * @description 首页体系模块界面
 */
class KnowledgeSystemFragment : BaseFragment<FragmentKnowledgeSystemBinding>() {

    private val mAdapter by lazy { WxChapterListAdapter() }

    private val viewModel by lazy {
        ViewModelProviders.of(requireActivity(), KnowledgeSystemModelFactory(KnowledgeSystemRepository()))
            .get(KnowledgeSystemViewModel::class.java)
    }

    override fun getLayoutId(): Int = R.layout.fragment_knowledge_system

    override fun initFragment(view: View, savedInstanceState: Bundle?) {
        mBinding.holder = this
        mBinding.adapter = mAdapter
        mBinding.itemClick = OnItemClickListener { position, _ ->
            mAdapter.getItemData(position)?.let {
                WebsiteDetailFragment.viewDetail(
                    mNavController,
                    R.id.action_mainFragment_to_websiteDetailFragment,
                    it.link
                )
            }
        }
        viewModel.fetchType()
        viewModel.type.observe(this, Observer {
            updateSystemArticles(it[0].name, it[0].children[0].name, it[0].children[0].id)
        })
    }

    private fun updateSystemArticles(
        first: String?,
        sec: String?,
        cid: Int
    ) {
        system_first.text = first
        system_sec.text = sec
        viewModel.fetchArticles(cid)
        viewModel.articles?.observe(this, Observer {
            mAdapter.submitList(it)
        })
    }

    fun typeClick(view: View) {
        KnowledgeSystemDialogFragment().setSelect { dialog, first, sec, cid ->
            updateSystemArticles(first, sec, cid)
            dialog.dismiss()
        }.show(childFragmentManager, "knowledgeSystem")
    }
}