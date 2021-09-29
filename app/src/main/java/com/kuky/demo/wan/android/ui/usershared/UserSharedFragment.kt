@file:Suppress("BlockingMethodInNonBlockingContext")

package com.kuky.demo.wan.android.ui.usershared

import android.graphics.Color
import android.os.Bundle
import android.text.Spannable
import android.text.SpannableStringBuilder
import android.text.style.ForegroundColorSpan
import android.view.View
import androidx.annotation.IdRes
import androidx.core.content.ContextCompat
import androidx.core.os.bundleOf
import androidx.navigation.NavController
import androidx.navigation.fragment.findNavController
import androidx.paging.LoadState
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.kuky.demo.wan.android.R
import com.kuky.demo.wan.android.base.BaseFragment
import com.kuky.demo.wan.android.base.DoubleClickListener
import com.kuky.demo.wan.android.base.scrollToTop
import com.kuky.demo.wan.android.databinding.FragmentSharedUserBinding
import com.kuky.demo.wan.android.extension.handleResult
import com.kuky.demo.wan.android.helper.dPrint
import com.kuky.demo.wan.android.listener.OnItemClickListener
import com.kuky.demo.wan.android.listener.OnItemLongClickListener
import com.kuky.demo.wan.android.ui.app.AppViewModel
import com.kuky.demo.wan.android.ui.app.PagingLoadStateAdapter
import com.kuky.demo.wan.android.ui.collection.CollectionViewModel
import com.kuky.demo.wan.android.ui.websitedetail.WebsiteDetailFragment
import com.kuky.demo.wan.android.widget.ErrorReload
import com.kuky.demo.wan.android.widget.RequestStatusCode
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
import org.koin.android.ext.android.inject
import org.koin.android.scope.AndroidScopeComponent
import org.koin.androidx.scope.fragmentScope
import org.koin.androidx.viewmodel.ext.android.sharedViewModel
import org.koin.androidx.viewmodel.ext.android.viewModel
import org.koin.core.scope.Scope
import java.util.*

/**
 * @author kuky.
 * @description
 */
class UserSharedFragment : BaseFragment<FragmentSharedUserBinding>(), AndroidScopeComponent {
    override val scope: Scope by fragmentScope()

    private val mAppViewModel by sharedViewModel<AppViewModel>()

    private val mViewModel by viewModel<UserSharedViewModel>()

    private val mCollectionViewModel by viewModel<CollectionViewModel>()

    private val mAdapter by inject<UserSharedPagingAdapter>()

    private val userId by lazy { arguments?.getInt("user") ?: 0 }

    private var mArticleJob: Job? = null
    private var mUserInfoJob: Job? = null

    override fun actionsOnViewInflate() {
        fetchUserInfo()
        fetchSharedArticles()
    }

    override fun getLayoutId(): Int = R.layout.fragment_shared_user

    override fun initFragment(view: View, savedInstanceState: Bundle?) {
        mBinding?.run {
            arguments?.getString("name")?.let {
                nick = it
                avatarKey = it.toCharArray()[0].toString().toUpperCase(Locale.getDefault())
            }

            refreshColor = R.color.colorAccent
            refreshListener = SwipeRefreshLayout.OnRefreshListener {
                fetchSharedArticles()
            }

            adapter = mAdapter.apply {
                addLoadStateListener { loadState ->
                    mBinding?.refreshing = loadState.refresh is LoadState.Loading
                    statusCode = when (loadState.refresh) {
                        is LoadState.Loading -> RequestStatusCode.Loading
                        is LoadState.Error -> RequestStatusCode.Error
                        else -> {
                            if (itemCount == 0) RequestStatusCode.Empty
                            else RequestStatusCode.Succeed
                        }
                    }
                }
            }.withLoadStateFooter(
                PagingLoadStateAdapter { mAdapter.retry() }
            )
            itemClick = OnItemClickListener { position, _ ->
                mAdapter.getItemData(position)?.let {
                    WebsiteDetailFragment.viewDetail(
                        findNavController(),
                        R.id.action_sharedUserFragment_to_websiteDetailFragment,
                        it.link
                    )
                }
            }
            itemLongClick = OnItemLongClickListener { position, _ ->
                mAdapter.getItemData(position)?.let { article ->
                    requireContext().alert(
                        if (article.collect) "「${article.title}」已收藏"
                        else " 是否收藏 「${article.title}」"
                    ) {
                        yesButton {
                            if (!article.collect) launch { collectArticle(article.id, position) }
                        }
                        if (!article.collect) noButton { }
                    }.show()
                }
            }

            // 双击回顶部
            gesture = DoubleClickListener {
                doubleTap = { articleList.scrollToTop() }
            }

            errorReload = ErrorReload {
                fetchSharedArticles()
                fetchUserInfo()
            }
        }
    }

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
                mAdapter.getItemData(position)?.collect = true
                context?.toast(R.string.add_favourite_succeed)
            }
        }
    }

    private fun fetchSharedArticles() {
        mArticleJob?.cancel()
        mArticleJob = launch {
            mViewModel.getSharedArticles(userId)
                .catch { mBinding?.statusCode = RequestStatusCode.Error }
                .collectLatest { mAdapter.submitData(it) }
        }
    }

    private fun fetchUserInfo() {
        mUserInfoJob?.cancel()
        mUserInfoJob = launch {
            mViewModel.getUserCoinInfo(userId)
                .catch { dPrint { "error occur" } }
                .collectLatest {
                    mBinding?.shared = SpannableStringBuilder().apply {
                        setSpan(
                            ForegroundColorSpan(Color.RED),
                            run { append("共分享了"); length },
                            run { append("\t${it.shareArticles.total}\t"); length },
                            Spannable.SPAN_INCLUSIVE_EXCLUSIVE
                        )
                        append("篇文章")
                    }

                    it.coinInfo.let { coin ->
                        mBinding?.coin = SpannableStringBuilder("${coin.coinCount}").apply {
                            setSpan(
                                ForegroundColorSpan(ContextCompat.getColor(requireContext(), R.color.coin_color)),
                                0, length, Spannable.SPAN_INCLUSIVE_EXCLUSIVE
                            )

                            setSpan(
                                ForegroundColorSpan(ContextCompat.getColor(requireContext(), R.color.colorPrimary)),
                                run { append("\t/\t\t"); length },
                                run { append("Lv${coin.level}"); length },
                                Spannable.SPAN_INCLUSIVE_EXCLUSIVE
                            )

                            setSpan(
                                ForegroundColorSpan(ContextCompat.getColor(requireContext(), R.color.colorAccent)),
                                run { append("\t\t/\t\t"); length },
                                run { append("R${coin.rank}"); length },
                                Spannable.SPAN_INCLUSIVE_EXCLUSIVE
                            )
                        }
                    }
                }
        }
    }

    companion object {
        fun navToUser(controller: NavController, @IdRes navId: Int, user: Int, name: String) =
            controller.navigate(navId, bundleOf("user" to user, "name" to name))
    }
}