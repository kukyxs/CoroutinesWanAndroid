package com.kuky.demo.wan.android.ui.search

import android.content.Context
import android.os.Bundle
import android.view.View
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import android.widget.TextView
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.paging.PagedList
import com.google.android.flexbox.FlexboxLayout
import com.kuky.demo.wan.android.R
import com.kuky.demo.wan.android.base.BaseFragment
import com.kuky.demo.wan.android.base.OnItemClickListener
import com.kuky.demo.wan.android.data.SearchHistoryUtils
import com.kuky.demo.wan.android.databinding.FragmentSearchBinding
import com.kuky.demo.wan.android.entity.ArticleDetail
import com.kuky.demo.wan.android.entity.HotKeyData
import com.kuky.demo.wan.android.ui.home.HomeArticleAdapter
import com.kuky.demo.wan.android.ui.websitedetail.WebsiteDetailFragment
import com.kuky.demo.wan.android.utils.ScreenUtils
import kotlinx.android.synthetic.main.fragment_search.*

/**
 * @author kuky.
 * @description
 */
class SearchFragment : BaseFragment<FragmentSearchBinding>() {

    private val mResultAdapter: HomeArticleAdapter by lazy { HomeArticleAdapter() }

    private val mHistoryAdapter: HistoryAdapter by lazy {
        HistoryAdapter(SearchHistoryUtils.fetchHistoryKeys(requireActivity()))
    }

    private var mResultMode = false

    private val mViewModel: SearchViewModel by lazy {
        ViewModelProviders
            .of(this, SearchModelFactory(SearchRepository()))
            .get(SearchViewModel::class.java)
    }

    override fun getLayoutId(): Int = R.layout.fragment_search

    override fun initFragment(view: View, savedInstanceState: Bundle?) {

        mBinding.adapter = mHistoryAdapter
        mBinding.listener = OnItemClickListener { position, _ ->
            mHistoryAdapter.getItemData(position)?.let {
                search_content.setText(it)
                searchArticles(it)
            }
        }

        mViewModel.hotKeys.observe(this, Observer<List<HotKeyData>> { keys ->
            addLabel(keys)
        })

        mViewModel.fetchKeys()

        search_content.setOnEditorActionListener { v, actionId, _ ->
            if (actionId == EditorInfo.IME_ACTION_SEARCH && !v.text.isNullOrBlank()) {
                searchArticles(v.text.toString())
            }
            true
        }
    }

    /**
     * 搜索
     */
    private fun searchArticles(keyword: String) {
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

        mViewModel.result?.observe(this, Observer<PagedList<ArticleDetail>> {
            mResultAdapter.submitList(it)
        })
    }

    /**
     * 添加热词
     */
    private fun addLabel(hotKeys: List<HotKeyData>) {
        val marginValue = ScreenUtils.dip2px(requireActivity(), 4f)
        val paddingValue = ScreenUtils.dip2px(requireActivity(), 6f)
        keys_box.removeAllViews()

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
                setPadding(paddingValue, paddingValue, paddingValue, paddingValue)
                setOnClickListener {
                    search_content.setText(name)
                    searchArticles(name)
                }
            }

            keys_box.addView(label)
        }
    }
}