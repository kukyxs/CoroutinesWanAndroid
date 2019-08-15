package com.kuky.demo.wan.android.ui.system

import android.os.Bundle
import android.os.Handler
import android.view.View
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.paging.PagedList
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.kuky.demo.wan.android.R
import com.kuky.demo.wan.android.base.BaseFragment
import com.kuky.demo.wan.android.base.OnItemClickListener
import com.kuky.demo.wan.android.base.OnItemLongClickListener
import com.kuky.demo.wan.android.databinding.FragmentKnowledgeSystemBinding
import com.kuky.demo.wan.android.entity.WxChapterListDatas
import com.kuky.demo.wan.android.ui.collection.CollectionFactory
import com.kuky.demo.wan.android.ui.collection.CollectionRepository
import com.kuky.demo.wan.android.ui.collection.CollectionViewModel
import com.kuky.demo.wan.android.ui.dialog.KnowledgeSystemDialogFragment
import com.kuky.demo.wan.android.ui.websitedetail.WebsiteDetailFragment
import com.kuky.demo.wan.android.ui.wxchapterlist.WxChapterListAdapter
import kotlinx.android.synthetic.main.fragment_knowledge_system.*
import org.jetbrains.anko.alert
import org.jetbrains.anko.noButton
import org.jetbrains.anko.toast
import org.jetbrains.anko.yesButton

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
        ViewModelProvider(requireActivity(), KnowledgeSystemModelFactory(KnowledgeSystemRepository()))
            .get(KnowledgeSystemViewModel::class.java)
    }
    private val mCollectionViewModel by lazy {
        ViewModelProvider(requireActivity(), CollectionFactory(CollectionRepository()))
            .get(CollectionViewModel::class.java)
    }
    // 体系id
    private var mCid: Int = 0
    // 用来修改article的collect字段，并且submitList()
    private lateinit var mProjectList: PagedList<WxChapterListDatas>

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
        mBinding.itemLongClick = OnItemLongClickListener { position, _ ->
            mAdapter.getItemData(position)?.let { article ->
                requireContext().alert(if (article.collect) "「${article.title}」已收藏" else " 是否收藏 「${article.title}」") {
                    yesButton {
                        if (!article.collect) mCollectionViewModel.collectArticle(article.id, {
                            mProjectList[position]?.collect = true
                            mAdapter.submitList(mProjectList)
                            requireContext().toast("收藏成功")
                        }, { message ->
                            requireContext().toast(message)
                        })
                    }
                    if (!article.collect) noButton { }
                }.show()
            }
            true
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
            mProjectList = it
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