package com.kuky.demo.wan.android.ui.wxchapterlist


import android.os.Bundle
import android.os.Handler
import android.view.View
import androidx.annotation.IdRes
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.NavController
import androidx.paging.PagedList
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.kuky.demo.wan.android.R
import com.kuky.demo.wan.android.base.BaseFragment
import com.kuky.demo.wan.android.base.OnItemClickListener
import com.kuky.demo.wan.android.base.OnItemLongClickListener
import com.kuky.demo.wan.android.databinding.FragmentWxChapterListBinding
import com.kuky.demo.wan.android.entity.WxChapterListDatas
import com.kuky.demo.wan.android.ui.collection.CollectionFactory
import com.kuky.demo.wan.android.ui.collection.CollectionRepository
import com.kuky.demo.wan.android.ui.collection.CollectionViewModel
import com.kuky.demo.wan.android.ui.websitedetail.WebsiteDetailFragment
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
        private val mHandler = Handler()

        /**
         * 公众号跳转到列表
         * [articleId] 文章id
         * [name] 作者名称
         */
        fun navigate(
            controller: NavController,
            @IdRes id: Int,
            articleId: Int,
            name: String
        ) {
            controller.navigate(id,
                Bundle().apply {
                    putInt("articleId", articleId)
                    putString("name", name)
                })
        }
    }

    private val mAdapter by lazy { WxChapterListAdapter() }
    private val mViewMode by lazy {
        ViewModelProvider(requireActivity(), WxChapterListModelFactory(WxChapterListRepository()))
            .get(WxChapterListViewModel::class.java)
    }
    private val mCollectionViewModel by lazy {
        ViewModelProvider(requireActivity(), CollectionFactory(CollectionRepository()))
            .get(CollectionViewModel::class.java)
    }
    private lateinit var mArticleList: PagedList<WxChapterListDatas>

    override fun getLayoutId(): Int = R.layout.fragment_wx_chapter_list

    override fun initFragment(view: View, savedInstanceState: Bundle?) {
        val id = arguments?.getInt("articleId")

        mBinding.wxChapter = arguments?.getString("name") ?: ""

        mBinding.refreshColor = R.color.colorAccent
        mBinding.refreshListener = SwipeRefreshLayout.OnRefreshListener {
            fetchWxChapterList(id)
        }

        mBinding.adapter = mAdapter
        mBinding.listener = OnItemClickListener { position, _ ->
            mAdapter.getItemData(position)?.let {
                WebsiteDetailFragment.viewDetail(
                    mNavController,
                    R.id.action_wxChapterListFragment_to_websiteDetailFragment,
                    it.link
                )
            }
        }
        mBinding.longClickListener = OnItemLongClickListener { position, _ ->
            mAdapter.getItemData(position)?.let { article ->
                requireContext().alert(if (article.collect) "「${article.title}」已收藏" else " 是否收藏 「${article.title}」") {
                    yesButton {
                        if (!article.collect) mCollectionViewModel.collectArticle(article.id, {
                            mArticleList[position]?.collect = true
                            mAdapter.submitList(mArticleList)
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
        fetchWxChapterList(id)
    }

    private fun fetchWxChapterList(id: Int?) {
        mBinding.refreshing = true
        mViewMode.fetchResult(id ?: 0)
        mViewMode.chapters?.observe(this, Observer {
            mArticleList = it
            mAdapter.submitList(it)
            mHandler.postDelayed({
                mBinding.refreshing = false
                mBinding.dataNull = it.isEmpty()
            }, 500)
        })
    }
}
