package com.kuky.demo.wan.android.ui.userarticles

import android.os.Bundle
import android.view.View
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.paging.PagedList
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.kuky.demo.wan.android.R
import com.kuky.demo.wan.android.base.*
import com.kuky.demo.wan.android.databinding.FragmentUserArticlesBinding
import com.kuky.demo.wan.android.entity.UserArticleDetail
import com.kuky.demo.wan.android.ui.collection.CollectionModelFactory
import com.kuky.demo.wan.android.ui.collection.CollectionRepository
import com.kuky.demo.wan.android.ui.collection.CollectionViewModel
import com.kuky.demo.wan.android.ui.main.MainModelFactory
import com.kuky.demo.wan.android.ui.main.MainRepository
import com.kuky.demo.wan.android.ui.main.MainViewModel
import com.kuky.demo.wan.android.ui.shareduser.SharedUserFragment
import com.kuky.demo.wan.android.ui.websitedetail.WebsiteDetailFragment
import com.kuky.demo.wan.android.ui.widget.ErrorReload
import org.jetbrains.anko.alert
import org.jetbrains.anko.noButton
import org.jetbrains.anko.toast
import org.jetbrains.anko.yesButton

/**
 * @author kuky.
 * @description
 */
class UserArticleFragment : BaseFragment<FragmentUserArticlesBinding>() {

    private val mViewModel: UserArticleViewModel by lazy {
        ViewModelProvider(requireActivity(), UserArticleModelFactory(UserArticleRepository()))
            .get(UserArticleViewModel::class.java)
    }

    private val mCollectionViewModel by lazy {
        ViewModelProvider(requireActivity(), CollectionModelFactory(CollectionRepository()))
            .get(CollectionViewModel::class.java)
    }

    private val mLoginViewModel by lazy {
        ViewModelProvider(requireActivity(), MainModelFactory(MainRepository()))
            .get(MainViewModel::class.java)
    }

    private val mAdapter: UserArticleAdapter by lazy {
        UserArticleAdapter().apply {
            userListener = { id, nick ->
                SharedUserFragment.navToUser(mNavController, R.id.action_mainFragment_to_sharedUserFragment, id, nick)
            }
        }
    }

    override fun getLayoutId(): Int = R.layout.fragment_user_articles

    override fun initFragment(view: View, savedInstanceState: Bundle?) {

        mBinding.refreshColor = R.color.colorAccent
        mBinding.refreshListener = SwipeRefreshLayout.OnRefreshListener {
            fetchSharedArticles()
        }

        mBinding.adapter = mAdapter
        mBinding.itemClick = OnItemClickListener { position, _ ->
            mAdapter.getItemData(position)?.let {
                WebsiteDetailFragment.viewDetail(
                    mNavController,
                    R.id.action_mainFragment_to_websiteDetailFragment,
                    it.link
                )
            }
        }
        mBinding.itemLongClick = OnItemLongClickListener { position, _ ->
            mAdapter.getItemData(position)?.let { article ->
                requireContext().alert(
                    if (article.collect) "「${article.title}」已收藏"
                    else " 是否收藏 「${article.title}」"
                ) {
                    yesButton {
                        if (!article.collect) mCollectionViewModel.collectArticle(article.id, {
                            mViewModel.userArticles?.value?.get(position)?.collect = true
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

        // 双击回顶部
        mBinding.gesture = DoubleClickListener(null, {
            mBinding.articleList.scrollToTop()
        })

        mBinding.errorReload = ErrorReload {
            fetchSharedArticles()
        }

        // 登录状态切换
        mLoginViewModel.hasLogin.observe(this, Observer<Boolean> {
            if (!it) {
                mViewModel.userArticles?.value?.forEach { arc ->
                    arc.collect = false
                }
            } else {
                fetchSharedArticles()
            }
        })

        fetchSharedArticles()
    }

    private fun fetchSharedArticles() {
        mViewModel.fetchSharedArticles { code, _ ->
            when (code) {
                PAGING_THROWABLE_LOAD_CODE_INITIAL -> {
                    mBinding.errorStatus = true
                    mBinding.indicator = resources.getString(R.string.text_place_holder)
                }

                PAGING_THROWABLE_LOAD_CODE_AFTER -> requireContext().toast("加载更多数据出错啦~请检查网络")
            }
        }

        mBinding.refreshing = true
        mBinding.errorStatus = false
        mViewModel.userArticles?.observe(this, Observer<PagedList<UserArticleDetail>> {
            mAdapter.submitList(it)
            mBinding.indicator = resources.getString(R.string.share_articles)
            delayLaunch(1000) { mBinding.refreshing = false }
        })
    }
}