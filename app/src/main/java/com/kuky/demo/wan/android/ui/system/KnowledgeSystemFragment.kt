package com.kuky.demo.wan.android.ui.system

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.View
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import androidx.paging.LoadState
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.kuky.demo.wan.android.R
import com.kuky.demo.wan.android.base.*
import com.kuky.demo.wan.android.databinding.FragmentKnowledgeSystemBinding
import com.kuky.demo.wan.android.listener.OnItemClickListener
import com.kuky.demo.wan.android.listener.OnItemLongClickListener
import com.kuky.demo.wan.android.ui.app.AppViewModel
import com.kuky.demo.wan.android.ui.app.PagingLoadStateAdapter
import com.kuky.demo.wan.android.ui.collection.CollectionViewModel
import com.kuky.demo.wan.android.ui.main.MainFragment
import com.kuky.demo.wan.android.ui.main.MainViewModel
import com.kuky.demo.wan.android.ui.websitedetail.WebsiteDetailFragment
import com.kuky.demo.wan.android.ui.wxchapterlist.WxChapterPagingAdapter
import com.kuky.demo.wan.android.widget.ErrorReload
import com.kuky.demo.wan.android.widget.RequestStatusCode
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
import org.koin.android.ext.android.inject
import org.koin.android.scope.AndroidScopeComponent
import org.koin.androidx.scope.fragmentScope
import org.koin.androidx.viewmodel.ext.android.sharedViewModel
import org.koin.androidx.viewmodel.ext.android.viewModel
import org.koin.core.scope.Scope

/**
 * @author kuky.
 * @description 首页体系模块界面
 */
class KnowledgeSystemFragment : BaseFragment<FragmentKnowledgeSystemBinding>(), AndroidScopeComponent {

    override val scope: Scope by fragmentScope()

    private val mAppViewModel by sharedViewModel<AppViewModel>()

    private val mLoginViewModel by sharedViewModel<MainViewModel>()

    private val mViewModel by viewModel<KnowledgeSystemViewModel>()

    private val mCollectionViewModel by viewModel<CollectionViewModel>()

    private val mAdapter by inject<WxChapterPagingAdapter>()

    private val mCategoryDialog by inject<KnowledgeSystemDialogFragment>()

    // 体系id
    private var mCid: Int = 0
    private var errorOnTypes = false
    private var isFirstObserver = true

    private var mTypeJob: Job? = null
    private var mArticleJob: Job? = null

    override fun actionsOnViewInflate() {
        fetchSystemTypes()

        mAppViewModel.reloadHomeData.observe(this, Observer {
            mAdapter.refresh()
        })

        // 登录状态切换
        mLoginViewModel.hasLogin.observe(this, Observer {
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
        mBinding?.run {
            refreshColor = R.color.colorAccent
            refreshListener = SwipeRefreshLayout.OnRefreshListener {
                mAdapter.refresh()
            }

            holder = this@KnowledgeSystemFragment
            adapter = mAdapter.apply {
                addLoadStateListener { loadState ->
                    refreshing = loadState.refresh is LoadState.Loading
                    statusCode = when (loadState.refresh) {
                        is LoadState.Loading -> RequestStatusCode.Loading
                        is LoadState.Error -> RequestStatusCode.Error
                        else -> {
                            if (itemCount == 0) RequestStatusCode.Empty
                            else RequestStatusCode.Succeed
                        }
                    }
                }
            }.withLoadStateFooter(
                PagingLoadStateAdapter { mAdapter.retry() }
            )

            itemClick = OnItemClickListener { position, _ ->
                mAdapter.getItemData(position)?.let {
                    WebsiteDetailFragment.viewDetail(
                        findNavController(),
                        R.id.action_mainFragment_to_websiteDetailFragment,
                        it.link
                    )
                }
            }

            itemLongClick = OnItemLongClickListener { position, _ ->
                mAdapter.getItemData(position)?.let { article ->
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

            projectList.setOnTouchListener { _, _ ->
                (parentFragment as? MainFragment)?.closeMenu()
                false
            }

            // 单击弹出选择框，双击返回顶部
            gesture = DoubleClickListener {
                singleTap = {
                    mCategoryDialog.apply {
                        mOnClick = { dialog, first, sec, cid ->
                            updateSystemArticles(first, sec, cid)
                            dialog.dismiss()
                        }
                    }.showAllowStateLoss(childFragmentManager, "knowledgeSystem")
                }

                doubleTap = { projectList.scrollToTop() }
            }

            errorReload = ErrorReload {
                if (errorOnTypes) fetchSystemTypes() else mAdapter.retry()
            }
        }
    }

    private fun fetchSystemTypes() {
        mTypeJob?.cancel()
        mTypeJob = launch {
            mViewModel.getTypeList().catch {
                errorOnTypes = true
                mBinding?.statusCode = RequestStatusCode.Error
                mBinding?.systemFirst?.text = resources.getString(R.string.text_place_holder)
                mBinding?.systemSec?.text = resources.getString(R.string.text_place_holder)
            }.collectLatest {
                mBinding?.statusCode = RequestStatusCode.Succeed
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
                .catch { mBinding?.statusCode = RequestStatusCode.Error }
                .collectLatest { mAdapter.submitData(it) }
        }
    }
}