package com.kuky.demo.wan.android.ui.search

import android.content.Context
import android.os.Bundle
import android.os.Handler
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
import com.kuky.demo.wan.android.base.BaseFragment
import com.kuky.demo.wan.android.base.OnItemClickListener
import com.kuky.demo.wan.android.data.PreferencesHelper
import com.kuky.demo.wan.android.data.SearchHistoryUtils
import com.kuky.demo.wan.android.databinding.FragmentSearchBinding
import com.kuky.demo.wan.android.entity.ArticleDetail
import com.kuky.demo.wan.android.entity.HotKeyData
import com.kuky.demo.wan.android.ui.home.HomeArticleAdapter
import com.kuky.demo.wan.android.ui.websitedetail.WebsiteDetailFragment
import com.kuky.demo.wan.android.utils.ScreenUtils
import kotlinx.android.synthetic.main.fragment_search.*
import kotlinx.android.synthetic.main.fragment_search.view.*

/**
 * @author kuky.
 * @description
 */
class SearchFragment : BaseFragment<FragmentSearchBinding>() {
    companion object {
        private val mHandler = Handler()
    }

    private var mResultMode = false

    private val mResultAdapter: HomeArticleAdapter by lazy { HomeArticleAdapter() }

    private val mHistoryAdapter: HistoryAdapter by lazy { HistoryAdapter() }

    private val mViewModel: SearchViewModel by lazy {
        ViewModelProvider(this, SearchModelFactory(SearchRepository()))
            .get(SearchViewModel::class.java)
    }

    override fun getLayoutId(): Int = R.layout.fragment_search

    override fun initFragment(view: View, savedInstanceState: Bundle?) {
        mBinding.enable = false
        mBinding.refreshColor = R.color.colorAccent
        mBinding.refreshListener = SwipeRefreshLayout.OnRefreshListener {
            searchArticles(PreferencesHelper.fetchSearchKeyword(requireContext()))
        }

        mBinding.editAction = TextView.OnEditorActionListener { v, actionId, _ ->
            if (actionId == EditorInfo.IME_ACTION_SEARCH && !v.text.isNullOrBlank()) {
                searchArticles(v.text.toString())
            }
            true
        }

        mBinding.needOverScroll = false
        mBinding.adapter = mHistoryAdapter
        mBinding.listener = OnItemClickListener { position, _ ->
            mHistoryAdapter.getItemData(position)?.let {
                search_content.setText(it)
                searchArticles(it)
            }
        }

        mViewModel.history.observe(this, Observer<List<String>> {
            mHistoryAdapter.updateHistory(it as MutableList<String>)
        })

        mViewModel.hotKeys.observe(this, Observer<List<HotKeyData>> {
            addLabel(it)
        })

        mViewModel.fetchHistory()

        mViewModel.fetchKeys()
    }

    /**
     * 搜索
     */
    private fun searchArticles(keyword: String) {
        PreferencesHelper.saveSearchKeyword(requireContext(), keyword)

        if (!mResultMode) {
            mResultMode = true

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
        }

        (requireActivity().getSystemService(Context.INPUT_METHOD_SERVICE)
                as InputMethodManager).hideSoftInputFromWindow(search_content.windowToken, 0)

        SearchHistoryUtils.saveHistory(requireActivity(), keyword.trim())

        mViewModel.fetchResult(keyword)

        mBinding.enable = true
        mBinding.refreshing = true
        mBinding.needOverScroll = true

        mViewModel.result?.observe(this, Observer<PagedList<ArticleDetail>> {
            mResultAdapter.submitList(it)
            mHandler.postDelayed({
                mBinding.refreshing = false
                mBinding.dataNull = it.isEmpty()
            }, 500)
        })
    }

    /**
     * 添加热词
     */
    private fun addLabel(hotKeys: List<HotKeyData>) {
        val verticalValue = ScreenUtils.dip2px(requireContext(), 2f).toInt()
        val marginValue = ScreenUtils.dip2px(requireContext(), 4f).toInt()
        val paddingValue = ScreenUtils.dip2px(requireContext(), 6f).toInt()
        mBinding.root.keys_box.removeAllViews()

        hotKeys.forEach {
            val name = it.name
            val lp = FlexboxLayout.LayoutParams(
                FlexboxLayout.LayoutParams.WRAP_CONTENT,
                FlexboxLayout.LayoutParams.WRAP_CONTENT
            ).apply {
                leftMargin = marginValue
                rightMargin = marginValue
                topMargin = marginValue
                bottomMargin = marginValue
            }

            val label = TextView(requireActivity()).apply {
                text = name
                textSize = 14f
                setBackgroundResource(R.drawable.label_outline)
                layoutParams = lp
                setPadding(paddingValue, verticalValue, paddingValue, verticalValue)
                setOnClickListener {
                    mBinding.root.search_content.setText(name)
                    searchArticles(name)
                }
            }

            mBinding.root.keys_box.addView(label)
        }
    }
}