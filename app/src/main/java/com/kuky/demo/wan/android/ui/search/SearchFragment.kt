package com.kuky.demo.wan.android.ui.search

import android.os.Bundle
import android.view.View
import android.view.inputmethod.EditorInfo
import android.widget.TextView
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
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
import com.kuky.demo.wan.android.ui.app.AppViewModel
import com.kuky.demo.wan.android.ui.app.PagingLoadStateAdapter
import com.kuky.demo.wan.android.ui.collection.CollectionViewModel
import com.kuky.demo.wan.android.ui.websitedetail.WebsiteDetailFragment
import com.kuky.demo.wan.android.utils.Injection
import com.kuky.demo.wan.android.utils.dp2px
import com.kuky.demo.wan.android.widget.ErrorReload
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
 * @description
 */
class SearchFragment : BaseFragment<FragmentSearchBinding>() {
    private var errorOnLabel = false
    private var mKey = ""

    @OptIn(ExperimentalPagingApi::class)
    private val mResultAdapter by lazy {
        SearchArticlePagingAdapter().apply {
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

    private val mHistoryAdapter by lazy {
        HistoryAdapter().apply {
            onKeyRemove = { mViewModel.updateHistory() }
        }
    }

    private val mAppViewModel by lazy {
        getSharedViewModel(AppViewModel::class.java)
    }

    private val mViewModel by lazy {
        ViewModelProvider(this, Injection.provideSearchViewModelFactory())
            .get(SearchViewModel::class.java)
    }

    private val mCollectionViewModel by lazy {
        ViewModelProvider(requireActivity(), Injection.provideCollectionViewModelFactory())
            .get(CollectionViewModel::class.java)
    }

    private var mKeyJob: Job? = null
    private var mSearchJob: Job? = null

    override fun actionsOnViewInflate() {
        loadHotKeys()
    }

    override fun getLayoutId(): Int = R.layout.fragment_search

    override fun initFragment(view: View, savedInstanceState: Bundle?) {
        mBinding?.let { binding ->
            binding.refreshColor = R.color.colorAccent
            binding.refreshListener = SwipeRefreshLayout.OnRefreshListener {
                if (errorOnLabel) loadHotKeys() else mResultAdapter.refresh()
            }

            binding.editAction = TextView.OnEditorActionListener { v, actionId, _ ->
                if (actionId == EditorInfo.IME_ACTION_SEARCH && !v.text.isNullOrBlank()) {
                    searchArticles(v.text.toString())
                }
                true
            }

            binding.errorReload = ErrorReload {
                if (errorOnLabel) loadHotKeys() else mResultAdapter.retry()
            }

            mViewModel.resultMode.observe(this, Observer {
                if (it) {
                    binding.enable = true
                    binding.needOverScroll = true
                    binding.adapter = mResultAdapter.withLoadStateFooter(PagingLoadStateAdapter { mResultAdapter.retry() })
                    binding.listener = OnItemClickListener { position, _ ->
                        mResultAdapter.getItemData(position)?.let { art ->
                            WebsiteDetailFragment.viewDetail(
                                mNavController,
                                R.id.action_searchFragment_to_websiteDetailFragment,
                                art.link
                            )
                        }
                    }

                    binding.longListener = OnItemLongClickListener { position, _ ->
                        mResultAdapter.getItemData(position)?.let { article ->
                            showCollectionDialog(article, position)
                        }
                    }
                } else {
                    binding.enable = false
                    binding.needOverScroll = false
                    binding.adapter = mHistoryAdapter
                    binding.listener = OnItemClickListener { position, _ ->
                        mHistoryAdapter.getItemData(position)?.let { key ->
                            binding.searchContent.setText(key)
                            searchArticles(key)
                        }
                    }
                }
            })

            mViewModel.history.observe(this, Observer<MutableList<String>> {
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
                pageState(State.FAILED)
            }.onStart {
                pageState(State.RUNNING)
            }.collectLatest {
                addLabel(it)
                pageState(State.SUCCESS)
                mBinding?.emptyStatus = it.isEmpty()
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
                .catch { mBinding?.errorStatus = true }
                .collectLatest { mResultAdapter.submitData(it) }
        }
    }

    private fun pageState(state: State) = mBinding?.run {
        refreshing = state == State.RUNNING
        loadingStatus = state == State.RUNNING
        errorStatus = state == State.FAILED
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