package com.kuky.demo.wan.android.ui.system

import android.os.Bundle
import android.view.View
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.kuky.demo.wan.android.R
import com.kuky.demo.wan.android.base.*
import com.kuky.demo.wan.android.databinding.FragmentKnowledgeSystemBinding
import com.kuky.demo.wan.android.ui.collection.CollectionModelFactory
import com.kuky.demo.wan.android.ui.collection.CollectionRepository
import com.kuky.demo.wan.android.ui.collection.CollectionViewModel
import com.kuky.demo.wan.android.ui.dialog.KnowledgeSystemDialogFragment
import com.kuky.demo.wan.android.ui.main.MainModelFactory
import com.kuky.demo.wan.android.ui.main.MainRepository
import com.kuky.demo.wan.android.ui.main.MainViewModel
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
        ViewModelProvider(requireActivity(), CollectionModelFactory(CollectionRepository()))
            .get(CollectionViewModel::class.java)
    }

    private val mLoginViewModel by lazy {
        ViewModelProvider(requireActivity(), MainModelFactory(MainRepository()))
            .get(MainViewModel::class.java)
    }

    // 体系id
    private var mCid: Int = 0
    private var errorOnTypes = false
    private var isFirstObserver = true

    override fun getLayoutId(): Int = R.layout.fragment_knowledge_system

    override fun initFragment(view: View, savedInstanceState: Bundle?) {
        mBinding.refreshColor = R.color.colorAccent
        mBinding.refreshListener = SwipeRefreshLayout.OnRefreshListener {
            if (errorOnTypes) fetchSystemTypes()
            else fetchArticles(mCid)
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
                requireContext().alert(
                    if (article.collect) "「${article.title}」已收藏"
                    else " 是否收藏 「${article.title}」"
                ) {
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
            }.showAllowStateLoss(childFragmentManager, "knowledgeSystem")
        }, {
            mBinding.projectList.scrollToTop()
        })

        mBinding.errorReload = ErrorReload {
            if (errorOnTypes) fetchSystemTypes()
            else fetchArticles(mCid)
        }

        fetchSystemTypes()

        // 登录状态切换
        mLoginViewModel.hasLogin.observe(this, Observer<Boolean> {
            if (isFirstObserver) {
                isFirstObserver = false
                return@Observer
            }

            if (!it) {
                mViewModel.mArticles?.value?.forEach { arc ->
                    arc.collect = false
                }
            } else {
                fetchArticles(mCid)
            }
        })
    }

    private fun fetchSystemTypes() {
        mViewModel.fetchType()
        mViewModel.typeNetState.observe(this, Observer {
            when (it.state) {
                State.RUNNING, State.SUCCESS -> injectStates(refreshing = true, loading = true)

                State.FAILED -> {
                    errorOnTypes = true
                    mBinding.systemFirst.text = resources.getString(R.string.text_place_holder)
                    mBinding.systemSec.text = resources.getString(R.string.text_place_holder)
                    injectStates(error = true)
                }
            }
        })

        mViewModel.mType.observe(this, Observer { data ->
            data?.let {
                updateSystemArticles(it[0].name, it[0].children[0].name, it[0].children[0].id, false)
            }
        })
    }

    /**
     * 选择体系后更新文章列表
     */
    private fun updateSystemArticles(first: String?, sec: String?, cid: Int, isRefresh: Boolean = true) {
        this.mCid = cid
        mBinding.systemFirst.text = first
        mBinding.systemSec.text = sec
        fetchArticles(mCid, isRefresh)
    }

    /**
     * 刷新文章列表
     */
    private fun fetchArticles(cid: Int, isRefresh: Boolean = true) {
        mViewModel.fetchArticles(cid) {
            mBinding.emptyStatus = true
        }

        mViewModel.netState?.observe(this, Observer {
            when (it.state) {
                State.RUNNING -> injectStates(refreshing = true, loading = !isRefresh)

                State.SUCCESS -> injectStates()

                State.FAILED -> {
                    errorOnTypes = false
                    mBinding.systemFirst.text = resources.getString(R.string.text_place_holder)
                    mBinding.systemSec.text = resources.getString(R.string.text_place_holder)
                    if (it.code == ERROR_CODE_INIT) injectStates(error = true)
                    else requireContext().toast(R.string.no_net_on_loading)
                }
            }
        })

        mViewModel.mArticles?.observe(this, Observer {
            mAdapter.submitList(it)
        })
    }

    private fun injectStates(refreshing: Boolean = false, loading: Boolean = false, error: Boolean = false) {
        mBinding.refreshing = refreshing
        mBinding.loadingStatus = loading
        mBinding.errorStatus = error
    }
}