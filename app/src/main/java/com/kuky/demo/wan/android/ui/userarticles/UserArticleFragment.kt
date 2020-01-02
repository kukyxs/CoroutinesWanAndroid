package com.kuky.demo.wan.android.ui.userarticles

import android.annotation.SuppressLint
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
import com.kuky.demo.wan.android.ui.main.MainFragment
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
                SharedUserFragment.navToUser(
                    mNavController,
                    R.id.action_mainFragment_to_sharedUserFragment,
                    id, nick
                )
            }
        }
    }

    private var isFirstObserver = true

    override fun actionsOnViewInflate() {
        fetchSharedArticles(false)

        // 登录状态切换
        mLoginViewModel.hasLogin.observe(this, Observer<Boolean> {
            if (isFirstObserver) {
                isFirstObserver = false
                return@Observer
            }

            if (!it) {
                mViewModel.userArticles?.value?.forEach { arc ->
                    arc.collect = false
                }
            } else {
                fetchSharedArticles()
            }
        })
    }

    override fun getLayoutId(): Int = R.layout.fragment_user_articles

    @SuppressLint("ClickableViewAccessibility")
    override fun initFragment(view: View, savedInstanceState: Bundle?) {
        mBinding?.let { binding ->
            binding.refreshColor = R.color.colorAccent
            binding.refreshListener = SwipeRefreshLayout.OnRefreshListener {
                fetchSharedArticles()
            }

            binding.adapter = mAdapter
            binding.itemClick = OnItemClickListener { position, _ ->
                (parentFragment as? MainFragment)?.closeMenu()
                mAdapter.getItemData(position)?.let {
                    WebsiteDetailFragment.viewDetail(
                        mNavController,
                        R.id.action_mainFragment_to_websiteDetailFragment,
                        it.link
                    )
                }
            }
            binding.itemLongClick = OnItemLongClickListener { position, _ ->
                (parentFragment as? MainFragment)?.closeMenu()
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
            binding.articleList.setOnTouchListener { _, _ ->
                (parentFragment as? MainFragment)?.closeMenu()
                false
            }

            // 双击回顶部
            binding.gesture = DoubleClickListener(null, {
                binding.articleList.scrollToTop()
            })

            binding.errorReload = ErrorReload {
                fetchSharedArticles()
            }
        }
    }

    private fun fetchSharedArticles(isRefresh: Boolean = true) {
        mViewModel.fetchSharedArticles {
            mBinding?.emptyStatus = true
        }

        mViewModel.netState?.observe(this, Observer {
            when (it.state) {
                State.RUNNING -> injectStates(refreshing = true, loading = !isRefresh)

                State.SUCCESS -> injectStates()

                State.FAILED -> {
                    mBinding?.indicator = resources.getString(R.string.text_place_holder)
                    if (it.code == ERROR_CODE_INIT) injectStates(error = true)
                    else requireContext().toast(R.string.no_net_on_loading)
                }
            }
        })

        mViewModel.userArticles?.observe(this, Observer<PagedList<UserArticleDetail>> {
            mAdapter.submitList(it)
            mBinding?.indicator = resources.getString(R.string.share_articles)
        })
    }

    private fun injectStates(refreshing: Boolean = false, loading: Boolean = false, error: Boolean = false) {
        mBinding?.let { binding ->
            binding.refreshing = refreshing
            binding.loadingStatus = loading
            binding.errorStatus = error
        }
    }
}