package com.kuky.demo.wan.android.ui.wxchapterlist


import android.os.Bundle
import android.view.View
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import android.view.inputmethod.EditorInfo
import android.widget.TextView
import androidx.annotation.IdRes
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.NavController
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.kuky.demo.wan.android.R
import com.kuky.demo.wan.android.base.*
import com.kuky.demo.wan.android.databinding.FragmentWxChapterListBinding
import com.kuky.demo.wan.android.ui.collection.CollectionModelFactory
import com.kuky.demo.wan.android.ui.collection.CollectionRepository
import com.kuky.demo.wan.android.ui.collection.CollectionViewModel
import com.kuky.demo.wan.android.ui.websitedetail.WebsiteDetailFragment
import com.kuky.demo.wan.android.ui.widget.ErrorReload
import org.jetbrains.anko.alert
import org.jetbrains.anko.noButton
import org.jetbrains.anko.toast
import org.jetbrains.anko.yesButton

/**
 * @author Taonce.
 * @description 公众号作者对应的文章列表页
 */
class WxChapterListFragment : BaseFragment<FragmentWxChapterListBinding>() {
    companion object {
        /**
         * 公众号跳转到列表
         * [articleId] 文章id
         * [name] 作者名称
         */
        fun navigate(
            controller: NavController, @IdRes id: Int,
            articleId: Int, name: String
        ) = controller.navigate(id,
            Bundle().apply {
                putInt("articleId", articleId)
                putString("name", name)
            })
    }

    private var mSearchKeyword = ""
    private val mAdapter by lazy { WxChapterListAdapter() }
    private val mViewMode by lazy {
        ViewModelProvider(requireActivity(), WxChapterListModelFactory(WxChapterListRepository()))
            .get(WxChapterListViewModel::class.java)
    }

    private val mCollectionViewModel by lazy {
        ViewModelProvider(requireActivity(), CollectionModelFactory(CollectionRepository()))
            .get(CollectionViewModel::class.java)
    }

    private val searchIn: Animation by lazy {
        AnimationUtils.loadAnimation(requireContext(), R.anim.slide_right_in).apply {
            setAnimationListener(object : CustomAnimationAdapter() {
                override fun onAnimationStart(animation: Animation?) {
                    super.onAnimationStart(animation)
                    mBinding.searchMode = true
                }

                override fun onAnimationEnd(animation: Animation?) {
                    super.onAnimationEnd(animation)
                    mBinding.wxSearch.requestFocus()
                    mBinding.wxSearch.showSoftInput()
                }
            })
        }
    }

    private val searchOut: Animation by lazy {
        AnimationUtils.loadAnimation(requireContext(), R.anim.slide_right_out).apply {
            setAnimationListener(object : CustomAnimationAdapter() {
                override fun onAnimationEnd(animation: Animation?) {
                    super.onAnimationEnd(animation)
                    mBinding.searchMode = false
                }
            })
        }
    }

    override fun getLayoutId(): Int = R.layout.fragment_wx_chapter_list

    override fun initFragment(view: View, savedInstanceState: Bundle?) {
        val id = arguments?.getInt("articleId")

        mBinding.wxChapter = arguments?.getString("name") ?: ""

        mBinding.refreshColor = R.color.colorAccent
        mBinding.refreshListener = SwipeRefreshLayout.OnRefreshListener {
            fetchWxChapterList(id, mSearchKeyword)
        }

        mBinding.adapter = mAdapter
        mBinding.listener = OnItemClickListener { position, _ ->
            if (mBinding.searchMode == true) {
                mBinding.wxSearch.startAnimation(searchOut)
            }

            mAdapter.getItemData(position)?.let {
                WebsiteDetailFragment.viewDetail(
                    mNavController,
                    R.id.action_wxChapterListFragment_to_websiteDetailFragment,
                    it.link
                )
            }
        }
        mBinding.longClickListener = OnItemLongClickListener { position, _ ->
            if (mBinding.searchMode == true) {
                mBinding.wxSearch.startAnimation(searchOut)
            }

            mAdapter.getItemData(position)?.let { article ->
                requireContext().alert(
                    if (article.collect) "「${article.title}」已收藏"
                    else " 是否收藏 「${article.title}」"
                ) {
                    yesButton {
                        if (!article.collect) mCollectionViewModel.collectArticle(article.id, {
                            mViewMode.chapters?.value?.get(position)?.collect = true
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

        mBinding.errorReload = ErrorReload {
            fetchWxChapterList(id, mSearchKeyword)
        }

        mBinding.gesture = DoubleClickListener(null, {
            mBinding.chapterList.scrollToTop()
        })

        mBinding.editAction = TextView.OnEditorActionListener { v, actionId, _ ->
            if (actionId == EditorInfo.IME_ACTION_SEARCH && !v.text.isNullOrBlank()) {
                fetchWxChapterList(id, v.text.toString(), isRefresh = false)
                mBinding.wxChapter = v.text.toString()
                mBinding.wxSearch.hideSoftInput()
                mBinding.wxSearch.startAnimation(searchOut)
            }
            true
        }

        mBinding.searchGesture = DoubleClickListener({
            if (mBinding.searchMode == false || mBinding.searchMode == null) {
                mBinding.wxSearch.clearText()
                mBinding.wxSearch.startAnimation(searchIn)
            }
        }, null)

        fetchWxChapterList(id, isRefresh = false)
    }

    private fun fetchWxChapterList(id: Int?, keyword: String = "", isRefresh: Boolean = true) {
        if (mSearchKeyword != keyword) mSearchKeyword = keyword

        mViewMode.fetchWxArticles(id ?: 0, keyword) {
            mBinding.emptyStatus = true
        }

        mViewMode.netState?.observe(this, Observer {
            when (it.state) {
                State.RUNNING -> injectStates(refreshing = true, loading = !isRefresh)

                State.SUCCESS -> injectStates()

                State.FAILED -> {
                    if (it.code == ERROR_CODE_INIT) injectStates(error = true)
                    else requireContext().toast(R.string.no_net_on_loading)
                }
            }
        })

        mViewMode.chapters?.observe(this, Observer {
            mAdapter.submitList(it)
        })
    }

    private fun injectStates(refreshing: Boolean = false, loading: Boolean = false, error: Boolean = false) {
        mBinding.refreshing = refreshing
        mBinding.loadingStatus = loading
        mBinding.errorStatus = error
    }

    override fun onDestroy() {
        super.onDestroy()
        searchIn.cancel()
        searchOut.cancel()
    }
}
