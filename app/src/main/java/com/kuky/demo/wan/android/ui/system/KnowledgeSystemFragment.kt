package com.kuky.demo.wan.android.ui.system

import android.os.Bundle
import android.os.Handler
import android.view.View
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
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
    companion object {
        private val mHandler = Handler()
    }

    private val mAdapter by lazy { WxChapterListAdapter() }
    private val mViewModel by lazy {
        ViewModelProviders.of(requireActivity(), KnowledgeSystemModelFactory(KnowledgeSystemRepository()))
            .get(KnowledgeSystemViewModel::class.java)
    }
    private var mCid: Int = 0 // 体系id

    override fun getLayoutId(): Int = R.layout.fragment_knowledge_system

    override fun initFragment(view: View, savedInstanceState: Bundle?) {
        mBinding.refreshColor = R.color.colorAccent
        mBinding.refreshListener = SwipeRefreshLayout.OnRefreshListener {
            // 防止第一次进去没拿到体系分类，需先获取下体系分类
            if (mCid == 0) {
                mViewModel.fetchType()
            }
            fetchType(mCid)
        }

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
        mViewModel.fetchType()
        mViewModel.mType.observe(this, Observer { data ->
            data?.let {
                updateSystemArticles(it[0].name, it[0].children[0].name, it[0].children[0].id)
            }
            data ?: let { mBinding.dataNull = true }
        })
    }

    /**
     * 刷新文章列表
     */
    private fun fetchType(cid: Int) {
        mViewModel.fetchArticles(cid)
        mBinding.refreshing = true
        mViewModel.mArticles?.observe(this, Observer {
            mAdapter.submitList(it)
            mHandler.postDelayed({
                mBinding.refreshing = false
                mBinding.dataNull = it.isEmpty()
            }, 500L)
        })
    }

    /**
     * 选择体系后更新文章列表
     */
    private fun updateSystemArticles(
        first: String?,
        sec: String?,
        cid: Int
    ) {
        this.mCid = cid
        system_first.text = first
        system_sec.text = sec
        fetchType(cid)
    }

    fun typeClick(view: View) {
        KnowledgeSystemDialogFragment().setSelect { dialog, first, sec, cid ->
            updateSystemArticles(first, sec, cid)
            dialog.dismiss()
        }.show(childFragmentManager, "knowledgeSystem")
    }
}