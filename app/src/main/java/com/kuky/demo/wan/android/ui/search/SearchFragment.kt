package com.kuky.demo.wan.android.ui.search

import android.os.Bundle
import android.view.View
import android.view.inputmethod.EditorInfo
import android.widget.TextView
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import androidx.paging.ExperimentalPagingApi
import androidx.paging.LoadState
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.google.android.flexbox.FlexboxLayout
import com.kuky.demo.wan.android.R
import com.kuky.demo.wan.android.base.*
import com.kuky.demo.wan.android.data.SearchHistoryUtils
import com.kuky.demo.wan.android.databinding.FragmentSearchBinding
import com.kuky.demo.wan.android.entity.ArticleDetail
import com.kuky.demo.wan.android.entity.HotKeyData
import com.kuky.demo.wan.android.listener.OnItemClickListener
import com.kuky.demo.wan.android.listener.OnItemLongClickListener
import com.kuky.demo.wan.android.ui.app.AppViewModel
import com.kuky.demo.wan.android.ui.app.PagingLoadStateAdapter
import com.kuky.demo.wan.android.ui.collection.CollectionViewModel
import com.kuky.demo.wan.android.ui.websitedetail.WebsiteDetailFragment
import com.kuky.demo.wan.android.utils.dp2px
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
import org.koin.androidx.scope.fragmentScope
import org.koin.androidx.viewmodel.ext.android.sharedViewModel
import org.koin.androidx.viewmodel.ext.android.viewModel
import org.koin.core.component.KoinScopeComponent
import org.koin.core.component.inject
import org.koin.core.scope.Scope

/**
 * @author kuky.
 * @description
 */
class SearchFragment : BaseFragment<FragmentSearchBinding>(), KoinScopeComponent {

    override val scope: Scope by fragmentScope()

    private val mAppViewModel by sharedViewModel<AppViewModel>()

    private val mViewModel by viewModel<SearchViewModel>()

    private val mCollectionViewModel by viewModel<CollectionViewModel>()

    private val mResultAdapter by inject<SearchArticlePagingAdapter>()

    private val mHistoryAdapter by inject<HistoryAdapter>()

    private var mKeyJob: Job? = null
    private var mSearchJob: Job? = null

    private var errorOnLabel = false
    private var mKey = ""

    override fun actionsOnViewInflate() = loadHotKeys()

    override fun getLayoutId(): Int = R.layout.fragment_search

    @OptIn(ExperimentalPagingApi::class)
    override fun initFragment(view: View, savedInstanceState: Bundle?) {
        mBinding?.run {
            refreshColor = R.color.colorAccent
            refreshListener = SwipeRefreshLayout.OnRefreshListener {
                if (errorOnLabel) loadHotKeys() else mResultAdapter.refresh()
            }

            editAction = TextView.OnEditorActionListener { v, actionId, _ ->
                if (actionId == EditorInfo.IME_ACTION_SEARCH && !v.text.isNullOrBlank()) {
                    searchArticles(v.text.toString())
                }
                true
            }

            errorReload = ErrorReload {
                if (errorOnLabel) loadHotKeys() else mResultAdapter.retry()
            }

            mViewModel.resultMode.observe(this@SearchFragment, Observer {
                if (it) {
                    enable = true
                    needOverScroll = true
                    adapter = mResultAdapter.apply {
                        addLoadStateListener { loadState ->
                            mBinding?.refreshing = loadState.refresh is LoadState.Loading
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
                        PagingLoadStateAdapter { mResultAdapter.retry() }
                    )
                    listener = OnItemClickListener { position, _ ->
                        mResultAdapter.getItemData(position)?.let { art ->
                            WebsiteDetailFragment.viewDetail(
                                findNavController(),
                                R.id.action_searchFragment_to_websiteDetailFragment,
                                art.link
                            )
                        }
                    }

                    longListener = OnItemLongClickListener { position, _ ->
                        mResultAdapter.getItemData(position)?.let { article ->
                            showCollectionDialog(article, position)
                        }
                    }
                } else {
                    enable = false
                    needOverScroll = false
                    adapter = mHistoryAdapter.apply { onKeyRemove = { mViewModel.updateHistory() } }
                    listener = OnItemClickListener { position, _ ->
                        mHistoryAdapter.getItemData(position)?.let { key ->
                            searchContent.setText(key)
                            searchArticles(key)
                        }
                    }
                }
            })

            mViewModel.history.observe(this@SearchFragment, Observer {
                mHistoryAdapter.updateHistory(it)
            })
        }
    }

    private fun showCollectionDialog(article: ArticleDetail, position: Int) =
        context?.alert(
            if (article.collect) "「${article.title.renderHtml()}」已收藏"
            else " 是否收藏 「${article.title.renderHtml()}」"
        ) {
            yesButton {
                if (!article.collect) launch { collectArticle(article.id, position) }
            }

            if (!article.collect) noButton { }
        }?.show()

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
                mResultAdapter.getItemData(position)?.collect = true
                context?.toast(R.string.add_favourite_succeed)
            }
        }
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    private fun loadHotKeys() {
        mKeyJob?.cancel()
        mKeyJob = launch {
            mViewModel.getHotKeys().catch {
                errorOnLabel = true
                mBinding?.statusCode = RequestStatusCode.Error
            }.onStart {
                mBinding?.refreshing = true
                mBinding?.statusCode = RequestStatusCode.Loading
            }.collectLatest {
                addLabel(it)
                mBinding?.refreshing = false
                mBinding?.statusCode = if (it.isEmpty()) RequestStatusCode.Empty else RequestStatusCode.Succeed
                mBinding?.hasHistory = SearchHistoryUtils.hasHistory(requireContext())
                mViewModel.updateHistory()
            }
        }
    }

    /**
     * 搜索
     */
    @OptIn(ExperimentalCoroutinesApi::class)
    private fun searchArticles(keyword: String) {
        if (mKey == keyword) return

        mKey = keyword
        mViewModel.resultMode.postValue(true)
        mBinding?.searchContent?.hideSoftInput()
        SearchHistoryUtils.saveHistory(requireActivity(), keyword.trim())

        mSearchJob?.cancel()
        mSearchJob = launch {
            mViewModel.getSearchResult(keyword)
                .catch { mBinding?.statusCode = RequestStatusCode.Error }
                .collectLatest { mResultAdapter.submitData(it) }
        }
    }

    /**
     * 添加热词
     */
    private fun addLabel(hotKeys: MutableList<HotKeyData>) {
        val marginValue = 8f.dp2px().toInt()
        val paddingValue = 6f.dp2px().toInt()
        mBinding?.keysBox?.removeAllViews()

        hotKeys.forEach {
            val name = it.name
            val lp = FlexboxLayout.LayoutParams(
                FlexboxLayout.LayoutParams.WRAP_CONTENT,
                FlexboxLayout.LayoutParams.WRAP_CONTENT
            ).apply {
                rightMargin = marginValue
                topMargin = marginValue
            }

            val label = TextView(requireActivity()).apply {
                text = name
                textSize = 14f
                setBackgroundResource(R.drawable.label_outline)
                layoutParams = lp
                setPadding(paddingValue, paddingValue, paddingValue, paddingValue)
                setOnClickListener {
                    mBinding?.searchContent?.setText(name)
                    searchArticles(name)
                }
            }

            mBinding?.keysBox?.addView(label)
        }
    }
}