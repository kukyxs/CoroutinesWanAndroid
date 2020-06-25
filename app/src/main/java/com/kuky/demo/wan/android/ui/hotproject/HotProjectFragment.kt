package com.kuky.demo.wan.android.ui.hotproject

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
import com.kuky.demo.wan.android.databinding.FragmentHotProjectBinding
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
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import org.jetbrains.anko.alert
import org.jetbrains.anko.noButton
import org.jetbrains.anko.toast
import org.jetbrains.anko.yesButton

/**
 * @author kuky.
 * @description 首页项目模块界面
 */
class HotProjectFragment : BaseFragment<FragmentHotProjectBinding>() {
    private var mId = 0
    private var mTitle = ""

    private val mViewModel: HotProjectViewModel by lazy {
        ViewModelProvider(requireActivity(), HotProjectModelFactory(HotProjectRepository()))
            .get(HotProjectViewModel::class.java)
    }

    private val mCollectionViewModel by lazy {
        ViewModelProvider(requireActivity(), CollectionModelFactory(CollectionRepository()))
            .get(CollectionViewModel::class.java)
    }

    private val mLoginViewModel by lazy {
        ViewModelProvider(requireActivity(), MainModelFactory(MainRepository()))
            .get(MainViewModel::class.java)
    }

    private var mCategoryJob: Job? = null
    private var mSearchJob: Job? = null
    private var errorOnCategories = false
    private var isFirstObserver = true

    @OptIn(ExperimentalPagingApi::class)
    private val mAdapter: HomeProjectPagingAdapter by lazy {
        HomeProjectPagingAdapter().apply {
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

    @OptIn(ExperimentalCoroutinesApi::class)
    override fun actionsOnViewInflate() {
        fetchCategories()

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

    override fun getLayoutId(): Int = R.layout.fragment_hot_project

    @SuppressLint("ClickableViewAccessibility")
    override fun initFragment(view: View, savedInstanceState: Bundle?) {
        mBinding?.let { binding ->
            binding.refreshColor = R.color.colorAccent
            binding.refreshListener = SwipeRefreshLayout.OnRefreshListener {
                mAdapter.refresh()
            }

            binding.adapter = mAdapter.withLoadStateFooter(PagingLoadStateAdapter { mAdapter.retry() })
            binding.holder = this@HotProjectFragment
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
                    // 根据是否收藏显示不同信息
                    requireContext().alert(if (article.collect) "「${article.title}」已收藏" else " 是否收藏 「${article.title}」") {
                        yesButton {
                            if (!article.collect) mCollectionViewModel.collectArticle(article.id, {
                                mAdapter.getItemData(position)?.collect = true
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

            binding.gesture = DoubleClickListener {
                singleTap = {
                    ProjectCategoryDialog().apply {
                        onSelectedListener = { dialog, category ->
                            mId = category.id; mTitle = category.name
                            fetchProjects(mId, mTitle); dialog?.dismiss()
                        }
                    }.showAllowStateLoss(childFragmentManager, "category")
                }

                doubleTap = { binding.projectList.scrollToTop() }
            }

            binding.errorReload = ErrorReload {
                if (errorOnCategories) fetchCategories() else mAdapter.retry()
            }
        }
    }

    // 获取分类信息
    @OptIn(ExperimentalCoroutinesApi::class)
    private fun fetchCategories() {
        mCategoryJob?.cancel()
        mCategoryJob = launch {
            pageState(State.RUNNING)
            mViewModel.getCategories().catch {
                errorOnCategories = true
                mBinding?.projectType?.text = resources.getString(R.string.text_place_holder)
                pageState(State.FAILED)
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
            mViewModel.getDiffCategoryProjects(id).collectLatest {
                mAdapter.submitData(it)
            }
        }
    }

    private fun pageState(state: State) = mBinding?.run {
        refreshing = state == State.RUNNING
        loadingStatus = state == State.RUNNING
        errorStatus = state == State.FAILED
    }
}