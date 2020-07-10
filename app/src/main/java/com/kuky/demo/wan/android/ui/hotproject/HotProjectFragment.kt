package com.kuky.demo.wan.android.ui.hotproject

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.View
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import androidx.paging.ExperimentalPagingApi
import androidx.paging.LoadState
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.kuky.demo.wan.android.R
import com.kuky.demo.wan.android.base.*
import com.kuky.demo.wan.android.databinding.FragmentHotProjectBinding
import com.kuky.demo.wan.android.ui.app.AppViewModel
import com.kuky.demo.wan.android.ui.app.PagingLoadStateAdapter
import com.kuky.demo.wan.android.ui.collection.CollectionViewModel
import com.kuky.demo.wan.android.ui.main.MainFragment
import com.kuky.demo.wan.android.ui.main.MainViewModel
import com.kuky.demo.wan.android.ui.websitedetail.WebsiteDetailFragment
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
import org.koin.androidx.scope.lifecycleScope
import org.koin.androidx.viewmodel.ext.android.sharedViewModel
import org.koin.androidx.viewmodel.ext.android.viewModel

/**
 * @author kuky.
 * @description 首页项目模块界面
 */
class HotProjectFragment : BaseFragment<FragmentHotProjectBinding>() {
    private val mAppViewModel by sharedViewModel<AppViewModel>()

    private val mLoginViewModel by sharedViewModel<MainViewModel>()

    private val mViewModel by viewModel<HotProjectViewModel>()

    private val mCollectionViewModel by viewModel<CollectionViewModel>()

    private val mAdapter by lifecycleScope.inject<HomeProjectPagingAdapter>()

    private val mCategoryDialog by lifecycleScope.inject<ProjectCategoryDialog>()

    private var mCategoryJob: Job? = null
    private var mSearchJob: Job? = null

    private var errorOnCategories = false
    private var isFirstObserver = true

    private var mId = 0
    private var mTitle = ""

    @OptIn(ExperimentalCoroutinesApi::class)
    override fun actionsOnViewInflate() {
        fetchCategories()

        mAppViewModel.reloadHomeData.observe(this, Observer {
            if (it) mAdapter.refresh()
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

    override fun getLayoutId(): Int = R.layout.fragment_hot_project

    @OptIn(ExperimentalPagingApi::class)
    @SuppressLint("ClickableViewAccessibility")
    override fun initFragment(view: View, savedInstanceState: Bundle?) {
        mBinding?.run {
            refreshColor = R.color.colorAccent
            refreshListener = SwipeRefreshLayout.OnRefreshListener {
                mAdapter.refresh()
            }

            adapter = mAdapter.apply {
                addLoadStateListener { loadState ->
                    refreshing = loadState.refresh is LoadState.Loading
                    statusCode = when (loadState.refresh) {
                        is LoadState.Loading -> RequestStatusCode.Loading
                        is LoadState.Error -> RequestStatusCode.Error
                        else -> RequestStatusCode.Succeed
                    }
                }

                addDataRefreshListener {
                    if (itemCount == 0) statusCode = RequestStatusCode.Empty
                }
            }.withLoadStateFooter(
                PagingLoadStateAdapter { mAdapter.retry() }
            )

            holder = this@HotProjectFragment
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
                    // 根据是否收藏显示不同信息
                    context?.alert(
                        if (article.collect) "「${article.title}」已收藏"
                        else " 是否收藏 「${article.title}」"
                    ) {
                        yesButton {
                            if (!article.collect) launch { collectArticle(article.id, position) }
                        }
                        if (!article.collect) noButton { }
                    }?.show()
                }
            }

            projectList.setOnTouchListener { _, _ ->
                (parentFragment as? MainFragment)?.closeMenu()
                false
            }

            gesture = DoubleClickListener {
                singleTap = {
                    mCategoryDialog.apply {
                        onSelectedListener = { dialog, category ->
                            mId = category.id; mTitle = category.name
                            fetchProjects(mId, mTitle); dialog?.dismiss()
                        }
                    }.showAllowStateLoss(childFragmentManager, "category")
                }

                doubleTap = { projectList.scrollToTop() }
            }

            errorReload = ErrorReload {
                if (errorOnCategories) fetchCategories() else mAdapter.retry()
            }
        }
    }

    // 获取分类信息
    @OptIn(ExperimentalCoroutinesApi::class)
    private fun fetchCategories() {
        mCategoryJob?.cancel()
        mCategoryJob = launch {
            mViewModel.getCategories().catch {
                errorOnCategories = true
                mBinding?.projectType?.text = resources.getString(R.string.text_place_holder)
                mBinding?.statusCode = RequestStatusCode.Error
            }.onStart {
                mBinding?.refreshing = true
                mBinding?.statusCode = RequestStatusCode.Loading
            }.collectLatest { cat ->
                cat[0].let { mId = it.id; mTitle = it.name; fetchProjects(mId, mTitle) }
            }
        }
    }

    // 获取分类下列表
    @OptIn(ExperimentalCoroutinesApi::class)
    private fun fetchProjects(id: Int, title: String) {
        mBinding?.projectType?.text = title.renderHtml()

        mSearchJob?.cancel()
        mSearchJob = launch {
            mViewModel.getDiffCategoryProjects(id)
                .catch { mBinding?.statusCode = RequestStatusCode.Error }
                .collectLatest { mAdapter.submitData(it) }
        }
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
}