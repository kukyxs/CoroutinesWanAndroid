package com.kuky.demo.wan.android.ui.search

import android.content.Context
import android.os.Bundle
import android.view.View
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import android.widget.TextView
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.paging.PagedList
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.google.android.flexbox.FlexboxLayout
import com.kuky.demo.wan.android.R
import com.kuky.demo.wan.android.base.*
import com.kuky.demo.wan.android.data.SearchHistoryUtils
import com.kuky.demo.wan.android.databinding.FragmentSearchBinding
import com.kuky.demo.wan.android.entity.ArticleDetail
import com.kuky.demo.wan.android.entity.HotKeyData
import com.kuky.demo.wan.android.ui.collection.CollectionModelFactory
import com.kuky.demo.wan.android.ui.collection.CollectionRepository
import com.kuky.demo.wan.android.ui.collection.CollectionViewModel
import com.kuky.demo.wan.android.ui.home.HomeArticleAdapter
import com.kuky.demo.wan.android.ui.websitedetail.WebsiteDetailFragment
import com.kuky.demo.wan.android.ui.widget.ErrorReload
import com.kuky.demo.wan.android.utils.ScreenUtils
import org.jetbrains.anko.alert
import org.jetbrains.anko.noButton
import org.jetbrains.anko.toast
import org.jetbrains.anko.yesButton

/**
 * @author kuky.
 * @description
 */
class SearchFragment : BaseFragment<FragmentSearchBinding>() {
    private var resultMode = false
    private var errorOnLabel = false
    private var mKey = ""

    private val mResultAdapter: HomeArticleAdapter by lazy { HomeArticleAdapter() }

    private val mHistoryAdapter: HistoryAdapter by lazy { HistoryAdapter() }

    private val mViewModel: SearchViewModel by lazy {
        ViewModelProvider(this, SearchModelFactory(SearchRepository()))
            .get(SearchViewModel::class.java)
    }

    private val mCollectionViewModel by lazy {
        ViewModelProvider(requireActivity(), CollectionModelFactory(CollectionRepository()))
            .get(CollectionViewModel::class.java)
    }

    override fun getLayoutId(): Int = R.layout.fragment_search

    override fun initFragment(view: View, savedInstanceState: Bundle?) {
        mBinding.enable = false
        mBinding.refreshColor = R.color.colorAccent
        mBinding.refreshListener = SwipeRefreshLayout.OnRefreshListener {
            searchArticles(mKey)
        }

        mBinding.editAction = TextView.OnEditorActionListener { v, actionId, _ ->
            if (actionId == EditorInfo.IME_ACTION_SEARCH && !v.text.isNullOrBlank()) {
                searchArticles(v.text.toString())
            }
            true
        }

        mBinding.hasHistory = SearchHistoryUtils.hasHistory(requireContext())
        mBinding.needOverScroll = false
        mBinding.adapter = mHistoryAdapter
        mBinding.listener = OnItemClickListener { position, _ ->
            mHistoryAdapter.getItemData(position)?.let {
                mBinding.searchContent.setText(it)
                searchArticles(it)
            }
        }

        mViewModel.history.observe(this, Observer<List<String>> {
            mHistoryAdapter.updateHistory(it as MutableList<String>)
        })

        mViewModel.hotKeys.observe(this, Observer<List<HotKeyData>> {
            addLabel(it)
        })

        mBinding.errorReload = ErrorReload {
            if (errorOnLabel) loadKeys()
            else searchArticles(mKey)
        }

        loadKeys()
    }

    private fun loadKeys() =
        mViewModel.fetchKeys {
            errorOnLabel = true
            mBinding.errorStatus = true
        }

    /**
     * 搜索
     */
    private fun searchArticles(keyword: String) {
        if (mKey != keyword) mKey = keyword

        if (!resultMode) {
            resultMode = true

            mBinding.adapter = mResultAdapter
            mBinding.listener = OnItemClickListener { position, _ ->
                mResultAdapter.getItemData(position)?.let {
                    WebsiteDetailFragment.viewDetail(
                        mNavController,
                        R.id.action_searchFragment_to_websiteDetailFragment,
                        it.link
                    )
                }
            }
            mBinding.longListener = OnItemLongClickListener { position, _ ->
                mResultAdapter.getItemData(position)?.let { article ->
                    requireContext().alert(
                        if (article.collect) "「${article.title.renderHtml()}」已收藏"
                        else " 是否收藏 「${article.title.renderHtml()}」"
                    ) {
                        yesButton {
                            if (!article.collect) mCollectionViewModel.collectArticle(article.id, {
                                mViewModel.result?.value?.get(position)?.collect = true
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
        }

        (requireActivity().getSystemService(Context.INPUT_METHOD_SERVICE)
                as InputMethodManager).hideSoftInputFromWindow(mBinding.searchContent.windowToken, 0)

        SearchHistoryUtils.saveHistory(requireActivity(), keyword.trim())

        mViewModel.fetchResult(keyword) { code, _ ->
            errorOnLabel = false
            when (code) {
                PAGING_THROWABLE_LOAD_CODE_INITIAL -> mBinding.errorStatus = true
                PAGING_THROWABLE_LOAD_CODE_AFTER -> requireContext().toast("加载更多出错啦~请检查网络")
            }
        }

        mBinding.enable = true
        mBinding.errorStatus = false
        mBinding.refreshing = true
        mBinding.needOverScroll = true

        mViewModel.result?.observe(this, Observer<PagedList<ArticleDetail>> {
            mResultAdapter.submitList(it)
            delayLaunch(1000) {
                mBinding.refreshing = false
            }
        })
    }

    /**
     * 添加热词
     */
    private fun addLabel(hotKeys: List<HotKeyData>) {
        val marginValue = ScreenUtils.dip2px(requireContext(), 8f).toInt()
        val paddingValue = ScreenUtils.dip2px(requireContext(), 6f).toInt()
        mBinding.keysBox.removeAllViews()

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
                    mBinding.searchContent.setText(name)
                    searchArticles(name)
                }
            }

            mBinding.keysBox.addView(label)
        }
    }
}