package com.kuky.demo.wan.android.ui.search

import android.os.Bundle
import android.view.View
import android.widget.TextView
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.paging.PagedList
import com.google.android.flexbox.FlexboxLayout
import com.kuky.demo.wan.android.R
import com.kuky.demo.wan.android.base.BaseFragment
import com.kuky.demo.wan.android.databinding.FragmentSearchBinding
import com.kuky.demo.wan.android.entity.ArticleDetail
import com.kuky.demo.wan.android.entity.HotKeyData
import com.kuky.demo.wan.android.ui.home.HomeArticleAdapter
import com.kuky.demo.wan.android.utils.ScreenUtils
import kotlinx.android.synthetic.main.fragment_search.*

/**
 * @author kuky.
 * @description
 */
class SearchFragment : BaseFragment<FragmentSearchBinding>() {

    private val mAdapter: HomeArticleAdapter by lazy { HomeArticleAdapter() }

    private val mViewModel: SearchViewModel by lazy {
        ViewModelProviders
            .of(this, SearchModelFactory(SearchRepository()))
            .get(SearchViewModel::class.java)
    }

    override fun getLayoutId(): Int = R.layout.fragment_search

    override fun initFragment(view: View, savedInstanceState: Bundle?) {

        mBinding.adapter = mAdapter

        mViewModel.hotKeys.observe(this, Observer<List<HotKeyData>> { keys ->
            keys.forEach { addLabel(it.name) }
        })

        mViewModel.fetchKeys()
    }

    /**
     * 搜索
     */
    private fun searchArticles(keyword: String) {
        mViewModel.fetchResult(keyword)

        mViewModel.result?.let {
            it.observe(this, Observer<PagedList<ArticleDetail>> { details ->
                mAdapter.submitList(details)
            })
        }
    }

    /**
     * 添加热词
     */
    private fun addLabel(str: String) {
        val marginValue = ScreenUtils.dip2px(requireActivity(), 4f)
        val paddingValue = ScreenUtils.dip2px(requireActivity(), 6f)

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
            text = str
            textSize = 14f
            setBackgroundResource(R.drawable.label_outline)
            layoutParams = lp
            setPadding(paddingValue, paddingValue, paddingValue, paddingValue)
            setOnClickListener {
                search_content.setText(str)
                searchArticles(str)
            }
        }

        keys_box.addView(label)
    }
}