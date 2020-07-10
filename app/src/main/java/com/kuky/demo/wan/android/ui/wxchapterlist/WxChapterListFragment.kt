package com.kuky.demo.wan.android.ui.wxchapterlist


import android.os.Bundle
import android.view.View
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import android.view.inputmethod.EditorInfo
import android.widget.TextView
import androidx.annotation.IdRes
import androidx.core.os.bundleOf
import androidx.navigation.NavController
import androidx.navigation.fragment.findNavController
import androidx.paging.ExperimentalPagingApi
import androidx.paging.LoadState
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.kuky.demo.wan.android.R
import com.kuky.demo.wan.android.base.*
import com.kuky.demo.wan.android.databinding.FragmentWxChapterListBinding
import com.kuky.demo.wan.android.ui.app.AppViewModel
import com.kuky.demo.wan.android.ui.app.PagingLoadStateAdapter
import com.kuky.demo.wan.android.ui.collection.CollectionViewModel
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
 * @description 公众号作者对应的文章列表页
 */
class WxChapterListFragment : BaseFragment<FragmentWxChapterListBinding>() {
    companion object {
        fun navigate(controller: NavController, @IdRes id: Int, articleId: Int, name: String) =
            controller.navigate(id, bundleOf("articleId" to articleId, "name" to name))
    }

    private val mAppViewModel by sharedViewModel<AppViewModel>()

    private val mViewMode by viewModel<WxChapterListViewModel>()

    private val mCollectionViewModel by viewModel<CollectionViewModel>()

    private val mAdapter by lifecycleScope.inject<WxChapterPagingAdapter>()

    private var mSearchKeyword: String? = null
    private val name by lazy { arguments?.getString("name") ?: "" }
    private val mArticleId by lazy { arguments?.getInt("articleId") ?: -1 }

    private val searchIn by lazy {
        AnimationUtils.loadAnimation(requireContext(), R.anim.slide_right_in).apply {
            setAnimationListener(object : CustomAnimationAdapter() {
                override fun onAnimationStart(animation: Animation?) {
                    super.onAnimationStart(animation)
                    mBinding?.searchMode = true
                }

                override fun onAnimationEnd(animation: Animation?) {
                    super.onAnimationEnd(animation)
                    mBinding?.wxSearch?.requestFocus()
                    mBinding?.wxSearch?.showSoftInput()
                }
            })
        }
    }

    private val searchOut by lazy {
        AnimationUtils.loadAnimation(requireContext(), R.anim.slide_right_out).apply {
            setAnimationListener(object : CustomAnimationAdapter() {
                override fun onAnimationEnd(animation: Animation?) {
                    super.onAnimationEnd(animation)
                    mBinding?.searchMode = false
                }
            })
        }
    }

    private var mArticleJob: Job? = null

    override fun actionsOnViewInflate() = fetchWxChapterList()

    override fun getLayoutId(): Int = R.layout.fragment_wx_chapter_list

    @OptIn(ExperimentalPagingApi::class)
    override fun initFragment(view: View, savedInstanceState: Bundle?) {
        mBinding?.run {
            wxChapter = name

            refreshColor = R.color.colorAccent
            refreshListener = SwipeRefreshLayout.OnRefreshListener {
                fetchWxChapterList(mSearchKeyword ?: "")
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
            listener = OnItemClickListener { position, _ ->
                if (searchMode == true) {
                    wxSearch.startAnimation(searchOut)
                }

                mAdapter.getItemData(position)?.let {
                    WebsiteDetailFragment.viewDetail(
                        findNavController(),
                        R.id.action_wxChapterListFragment_to_websiteDetailFragment,
                        it.link
                    )
                }
            }
            longClickListener = OnItemLongClickListener { position, _ ->
                if (searchMode == true) {
                    wxSearch.startAnimation(searchOut)
                }

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

            errorReload = ErrorReload { mAdapter.retry() }

            gesture = DoubleClickListener {
                doubleTap = {
                    chapterList.scrollToTop()
                }
            }

            editAction = TextView.OnEditorActionListener { v, actionId, _ ->
                if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                    fetchWxChapterList(v.text.toString())
                    wxChapter = if (v.text.isEmpty()) name else v.text.toString()
                    wxSearch.hideSoftInput()
                    wxSearch.startAnimation(searchOut)
                }
                true
            }

            searchGesture = DoubleClickListener {
                singleTap = {
                    if (searchMode == false || searchMode == null) {
                        wxSearch.clearText()
                        wxSearch.startAnimation(searchIn)
                    }
                }
            }
        }
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    private fun fetchWxChapterList(keyword: String = "") {
        if (mSearchKeyword == keyword) return

        mSearchKeyword = keyword
        mArticleJob?.cancel()
        mArticleJob = launch {
            mViewMode.getWxChapters(mArticleId, keyword)
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

    override fun onDestroy() {
        super.onDestroy()
        searchIn.cancel()
        searchOut.cancel()
    }
}
