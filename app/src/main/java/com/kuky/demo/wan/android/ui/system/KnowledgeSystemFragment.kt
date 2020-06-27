package com.kuky.demo.wan.android.ui.system

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.View
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.paging.ExperimentalPagingApi
import androidx.paging.LoadState
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.kuky.demo.wan.android.R
import com.kuky.demo.wan.android.base.*
import com.kuky.demo.wan.android.databinding.FragmentKnowledgeSystemBinding
import com.kuky.demo.wan.android.ui.app.AppViewModel
import com.kuky.demo.wan.android.ui.app.PagingLoadStateAdapter
import com.kuky.demo.wan.android.ui.collection.CollectionModelFactory
import com.kuky.demo.wan.android.ui.collection.CollectionRepository
import com.kuky.demo.wan.android.ui.collection.CollectionViewModel
import com.kuky.demo.wan.android.ui.main.MainFragment
import com.kuky.demo.wan.android.ui.main.MainModelFactory
import com.kuky.demo.wan.android.ui.main.MainRepository
import com.kuky.demo.wan.android.ui.main.MainViewModel
import com.kuky.demo.wan.android.ui.websitedetail.WebsiteDetailFragment
import com.kuky.demo.wan.android.ui.widget.ErrorReload
import com.kuky.demo.wan.android.ui.wxchapterlist.WxChapterPagingAdapter
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.onCompletion
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.launch
import org.jetbrains.anko.alert
import org.jetbrains.anko.noButton
import org.jetbrains.anko.toast
import org.jetbrains.anko.yesButton

/**
 * @author kuky.
 * @description 首页体系模块界面
 */
class KnowledgeSystemFragment : BaseFragment<FragmentKnowledgeSystemBinding>() {

    @OptIn(ExperimentalPagingApi::class)
    private val mAdapter by lazy {
        WxChapterPagingAdapter().apply {
            addLoadStateListener { loadState ->
                mBinding?.refreshing = loadState.refresh is LoadState.Loading
                mBinding?.loadingStatus = loadState.refresh is LoadState.Loading
                mBinding?.errorStatus = loadState.refresh is LoadState.Error
            }

            addDataRefreshListener {
                mBinding?.emptyStatus = itemCount == 0
            }
        }
    }

    private val mAppViewModel by lazy { getSharedViewModel(AppViewModel::class.java) }

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

    private var mTypeJob: Job? = null
    private var mArticleJob: Job? = null

    override fun actionsOnViewInflate() {
        fetchSystemTypes()

        // 登录状态切换
        mLoginViewModel.hasLogin.observe(this, Observer<Boolean> {
            if (isFirstObserver) {
                isFirstObserver = false
                return@Observer
            }

            if (!it) {
                for (index in 0 until mAdapter.itemCount) {
                    mAdapter.getItemData(index)?.collect = false
                }
            } else {
                mAdapter.refresh()
            }
        })
    }

    override fun getLayoutId(): Int = R.layout.fragment_knowledge_system

    @SuppressLint("ClickableViewAccessibility")
    override fun initFragment(view: View, savedInstanceState: Bundle?) {
        mBinding?.let { binding ->
            binding.refreshColor = R.color.colorAccent
            binding.refreshListener = SwipeRefreshLayout.OnRefreshListener {
                mAdapter.refresh()
            }

            binding.holder = this
            binding.adapter = mAdapter.withLoadStateFooter(PagingLoadStateAdapter { mAdapter.retry() })
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
                            if (!article.collect) launch { collectArticle(article.id, position) }
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
                if (errorOnTypes) fetchSystemTypes() else mAdapter.retry()
            }
        }
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    private fun fetchSystemTypes() {
        mTypeJob?.cancel()
        mTypeJob = launch {
            mViewModel.getTypeList().catch {
                errorOnTypes = true
                pageState(State.FAILED)
                mBinding?.systemFirst?.text = resources.getString(R.string.text_place_holder)
                mBinding?.systemSec?.text = resources.getString(R.string.text_place_holder)
            }.collectLatest {
                pageState(State.SUCCESS)
                updateSystemArticles(it[0].name, it[0].children[0].name, it[0].children[0].id)
            }
        }
    }

    /**
     * 选择体系后更新文章列表
     */
    private fun updateSystemArticles(first: String?, sec: String?, cid: Int) {
        this.mCid = cid
        mBinding?.systemFirst?.text = first
        mBinding?.systemSec?.text = sec
        fetchArticles(mCid)
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    private suspend fun collectArticle(id: Int, position: Int) {
        mCollectionViewModel.collectArticle(id).catch {
            context?.toast(R.string.no_network)
        }.onStart {
            mAppViewModel.showLoading()
        }.onCompletion {
            mAppViewModel.dismissLoading()
        }.collectLatest {
            it.handleResult {
                mAdapter.getItemData(position)?.collect = true
                context?.toast(R.string.add_favourite_succeed)
            }
        }
    }

    /**
     * 刷新文章列表
     */
    @OptIn(ExperimentalCoroutinesApi::class)
    private fun fetchArticles(cid: Int) {
        mArticleJob?.cancel()
        mArticleJob = launch {
            mViewModel.getArticles(cid)
                .catch { mBinding?.errorStatus = true }
                .collectLatest { mAdapter.submitData(it) }
        }
    }

    private fun pageState(state: State) = mBinding?.run {
        refreshing = state == State.RUNNING
        loadingStatus = state == State.RUNNING
        errorStatus = state == State.FAILED
    }
}