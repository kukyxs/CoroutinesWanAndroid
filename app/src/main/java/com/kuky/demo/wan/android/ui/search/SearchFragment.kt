package com.kuky.demo.wan.android.ui.search

import android.os.Bundle
import android.view.View
import android.view.inputmethod.EditorInfo
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
    private var errorOnLabel = false
    private var mKey = ""

    private val mResultAdapter: SearchArticleAdapter by lazy { SearchArticleAdapter() }

    private val mHistoryAdapter: HistoryAdapter by lazy { HistoryAdapter() }

    private val mViewModel: SearchViewModel by lazy {
        ViewModelProvider(this, SearchModelFactory(SearchRepository()))
            .get(SearchViewModel::class.java)
    }

    private val mCollectionViewModel by lazy {
        ViewModelProvider(requireActivity(), CollectionModelFactory(CollectionRepository()))
            .get(CollectionViewModel::class.java)
    }

    override fun actionsOnViewInflate() {
        loadKeys()
    }

    override fun getLayoutId(): Int = R.layout.fragment_search

    override fun initFragment(view: View, savedInstanceState: Bundle?) {
        mBinding?.let { binding ->
            binding.refreshColor = R.color.colorAccent
            binding.refreshListener = SwipeRefreshLayout.OnRefreshListener {
                if (errorOnLabel) loadKeys()
                else searchArticles(mKey)
            }

            binding.editAction = TextView.OnEditorActionListener { v, actionId, _ ->
                if (actionId == EditorInfo.IME_ACTION_SEARCH && !v.text.isNullOrBlank()) {
                    searchArticles(v.text.toString())
                }
                true
            }

            binding.errorReload = ErrorReload {
                if (errorOnLabel) loadKeys()
                else searchArticles(mKey)
            }

            mViewModel.resultMode.observe(this, Observer {
                if (it) {
                    binding.enable = true
                    binding.needOverScroll = true
                    binding.adapter = mResultAdapter
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
                        true
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
        }
    }

    private fun showCollectionDialog(article: ArticleDetail, position: Int) =
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

    private fun loadKeys() {
        mViewModel.fetchKeys()

        mViewModel.keyNetState.observe(this, Observer {
            when (it.state) {
                State.RUNNING -> injectStates(refreshing = true, loading = true)

                State.SUCCESS -> {
                    injectStates()
                    mBinding?.hasHistory = SearchHistoryUtils.hasHistory(requireContext())
                }

                State.FAILED -> {
                    errorOnLabel = true
                    injectStates(error = true)
                }
            }
        })

        mViewModel.history.observe(this, Observer<List<String>> {
            mHistoryAdapter.updateHistory(it as MutableList<String>)
        })

        mViewModel.hotKeys.observe(this, Observer<List<HotKeyData>> {
            addLabel(it)
        })
    }

    /**
     * 搜索
     */
    private fun searchArticles(keyword: String) {
        if (mKey != keyword) mKey = keyword

        mViewModel.resultMode.postValue(true)

        mBinding?.searchContent?.hideSoftInput()

        SearchHistoryUtils.saveHistory(requireActivity(), keyword.trim())

        mViewModel.fetchResult(keyword) {
            mBinding?.emptyStatus = true
        }

        mViewModel.netState?.observe(this, Observer {
            when (it.state) {
                State.RUNNING -> injectStates(refreshing = true, loading = true)

                State.SUCCESS -> injectStates()

                State.FAILED -> {
                    errorOnLabel = false
                    if (it.code == ERROR_CODE_INIT) injectStates(error = true)
                    else requireContext().toast(R.string.no_net_on_loading)
                }
            }
        })

        mViewModel.result?.observe(this, Observer<PagedList<ArticleDetail>> {
            mResultAdapter.submitList(it)
        })
    }

    private fun injectStates(refreshing: Boolean = false, loading: Boolean = false, error: Boolean = false) {
        mBinding?.let { binding ->
            binding.refreshing = refreshing
            binding.loadingStatus = loading
            binding.errorStatus = error
        }
    }

    /**
     * 添加热词
     */
    private fun addLabel(hotKeys: List<HotKeyData>) {
        val marginValue = ScreenUtils.dip2px(requireContext(), 8f).toInt()
        val paddingValue = ScreenUtils.dip2px(requireContext(), 6f).toInt()
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