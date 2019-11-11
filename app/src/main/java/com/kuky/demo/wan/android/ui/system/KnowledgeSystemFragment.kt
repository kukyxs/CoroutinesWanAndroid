package com.kuky.demo.wan.android.ui.system

import android.os.Bundle
import android.view.View
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.kuky.demo.wan.android.R
import com.kuky.demo.wan.android.base.*
import com.kuky.demo.wan.android.databinding.FragmentKnowledgeSystemBinding
import com.kuky.demo.wan.android.ui.collection.CollectionFactory
import com.kuky.demo.wan.android.ui.collection.CollectionRepository
import com.kuky.demo.wan.android.ui.collection.CollectionViewModel
import com.kuky.demo.wan.android.ui.dialog.KnowledgeSystemDialogFragment
import com.kuky.demo.wan.android.ui.websitedetail.WebsiteDetailFragment
import com.kuky.demo.wan.android.ui.widget.ErrorReload
import com.kuky.demo.wan.android.ui.wxchapterlist.WxChapterListAdapter
import org.jetbrains.anko.alert
import org.jetbrains.anko.noButton
import org.jetbrains.anko.toast
import org.jetbrains.anko.yesButton

/**
 * @author kuky.
 * @description 首页体系模块界面
 */
class KnowledgeSystemFragment : BaseFragment<FragmentKnowledgeSystemBinding>() {

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
    private var errorOnTypes = false

    override fun getLayoutId(): Int = R.layout.fragment_knowledge_system

    override fun initFragment(view: View, savedInstanceState: Bundle?) {
        mBinding.refreshColor = R.color.colorAccent
        mBinding.refreshListener = SwipeRefreshLayout.OnRefreshListener {
            fetchArticles(mCid)
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
                            mViewModel.mArticles?.value?.get(position)?.collect = true
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

        // 单击弹出选择框，双击返回顶部
        mBinding.gesture = DoubleClickListener({
            KnowledgeSystemDialogFragment().apply {
                mOnClick = { dialog, first, sec, cid ->
                    updateSystemArticles(first, sec, cid)
                    dialog.dismiss()
                }
            }.show(childFragmentManager, "knowledgeSystem")
        }, {
            mBinding.projectList.scrollToTop()
        })

        mBinding.errorReload = ErrorReload {
            if (errorOnTypes) fetchSystemTypes()
            else fetchArticles(mCid)
        }

        fetchSystemTypes()

        mViewModel.mType.observe(this, Observer { data ->
            data?.let { updateSystemArticles(it[0].name, it[0].children[0].name, it[0].children[0].id) }
        })
    }

    private fun fetchSystemTypes() {
        mViewModel.fetchType {
            mBinding.errorStatus = true
            errorOnTypes = true
        }
    }

    /**
     * 刷新文章列表
     */
    private fun fetchArticles(cid: Int) {
        mViewModel.fetchArticles(cid) { code, _ ->
            mBinding.systemFirst.text = resources.getString(R.string.text_place_holder)
            mBinding.systemSec.text = resources.getString(R.string.text_place_holder)

            errorOnTypes = false
            when (code) {
                PAGING_THROWABLE_LOAD_CODE_INITIAL -> mBinding.errorStatus = true
                PAGING_THROWABLE_LOAD_CODE_AFTER -> requireContext().toast("加载更多数据出错啦~请检查网络")
            }
        }

        mBinding.errorStatus = false
        mBinding.refreshing = true
        mViewModel.mArticles?.observe(this, Observer {
            mAdapter.submitList(it)
            delayLaunch(1000) { mBinding.refreshing = false }
        })
    }

    /**
     * 选择体系后更新文章列表
     */
    private fun updateSystemArticles(first: String?, sec: String?, cid: Int) {
        this.mCid = cid
        mBinding.systemFirst.text = first
        mBinding.systemSec.text = sec
        fetchArticles(mCid)
    }
}