package com.kuky.demo.wan.android.ui.system

import android.annotation.SuppressLint
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
import com.kuky.demo.wan.android.ui.main.MainFragment
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

    override fun actionsOnViewInflate() {
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

    override fun getLayoutId(): Int = R.layout.fragment_knowledge_system

    @SuppressLint("ClickableViewAccessibility")
    override fun initFragment(view: View, savedInstanceState: Bundle?) {
        mBinding?.let { binding ->
            binding.refreshColor = R.color.colorAccent
            binding.refreshListener = SwipeRefreshLayout.OnRefreshListener {
                if (errorOnTypes) fetchSystemTypes()
                else fetchArticles(mCid)
            }

            binding.holder = this
            binding.adapter = mAdapter
            binding.itemClick = OnItemClickListener { position, _ ->
                mAdapter.getItemData(position)?.let {
                    (parentFragment as? MainFragment)?.closeMenu()
                    WebsiteDetailFragment.viewDetail(
                        mNavController,
                        R.id.action_mainFragment_to_websiteDetailFragment,
                        it.link
                    )
                }
            }
            binding.itemLongClick = OnItemLongClickListener { position, _ ->
                mAdapter.getItemData(position)?.let { article ->
                    (parentFragment as? MainFragment)?.closeMenu()
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
            }

            binding.projectList.setOnTouchListener { _, _ ->
                (parentFragment as? MainFragment)?.closeMenu()
                false
            }

            // 单击弹出选择框，双击返回顶部
            binding.gesture = DoubleClickListener {
                singleTap = {
                    KnowledgeSystemDialogFragment().apply {
                        mOnClick = { dialog, first, sec, cid ->
                            updateSystemArticles(first, sec, cid)
                            dialog.dismiss()
                        }
                    }.showAllowStateLoss(childFragmentManager, "knowledgeSystem")
                }
                doubleTap = {
                    binding.projectList.scrollToTop()
                }
            }

            binding.errorReload = ErrorReload {
                if (errorOnTypes) fetchSystemTypes()
                else fetchArticles(mCid)
            }
        }
    }

    private fun fetchSystemTypes() {
        mViewModel.fetchType()
        mViewModel.typeNetState.observe(this, Observer {
            when (it.state) {
                State.RUNNING, State.SUCCESS -> injectStates(refreshing = true, loading = true)

                State.FAILED -> {
                    errorOnTypes = true
                    mBinding?.systemFirst?.text = resources.getString(R.string.text_place_holder)
                    mBinding?.systemSec?.text = resources.getString(R.string.text_place_holder)
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
        mBinding?.systemFirst?.text = first
        mBinding?.systemSec?.text = sec
        fetchArticles(mCid, isRefresh)
    }

    /**
     * 刷新文章列表
     */
    private fun fetchArticles(cid: Int, isRefresh: Boolean = true) {
        mViewModel.fetchArticles(cid) {
            mBinding?.emptyStatus = true
        }

        mViewModel.netState?.observe(this, Observer {
            when (it.state) {
                State.RUNNING -> injectStates(refreshing = true, loading = !isRefresh)

                State.SUCCESS -> injectStates()

                State.FAILED -> {
                    errorOnTypes = false
                    mBinding?.systemFirst?.text = resources.getString(R.string.text_place_holder)
                    mBinding?.systemSec?.text = resources.getString(R.string.text_place_holder)
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
        mBinding?.let { binding ->
            binding.refreshing = refreshing
            binding.loadingStatus = loading
            binding.errorStatus = error
        }
    }
}