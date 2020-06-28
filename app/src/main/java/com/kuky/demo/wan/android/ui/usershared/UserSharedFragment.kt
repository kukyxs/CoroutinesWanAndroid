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
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.NavController
import androidx.paging.ExperimentalPagingApi
import androidx.paging.LoadState
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.kuky.demo.wan.android.R
import com.kuky.demo.wan.android.base.*
import com.kuky.demo.wan.android.databinding.FragmentSharedUserBinding
import com.kuky.demo.wan.android.ui.app.AppViewModel
import com.kuky.demo.wan.android.ui.app.PagingLoadStateAdapter
import com.kuky.demo.wan.android.ui.collection.CollectionViewModel
import com.kuky.demo.wan.android.ui.websitedetail.WebsiteDetailFragment
import com.kuky.demo.wan.android.utils.Injection
import com.kuky.demo.wan.android.utils.LogUtils
import com.kuky.demo.wan.android.widget.ErrorReload
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
import java.util.*

/**
 * @author kuky.
 * @description
 */
class UserSharedFragment : BaseFragment<FragmentSharedUserBinding>() {

    private val mAppViewModel by lazy {
        getSharedViewModel(AppViewModel::class.java)
    }

    private val mViewModel by lazy {
        ViewModelProvider(requireActivity(), Injection.provideUserSharedViewModelFactory())
            .get(UserSharedViewModel::class.java)
    }

    private val mCollectionViewModel by lazy {
        ViewModelProvider(requireActivity(), Injection.provideCollectionViewModelFactory())
            .get(CollectionViewModel::class.java)
    }

    @OptIn(ExperimentalPagingApi::class)
    private val mAdapter by lazy {
        UserSharedPagingAdapter().apply {
            addLoadStateListener { loadState ->
                mBinding?.refreshing = loadState.refresh is LoadState.Loading
                mBinding?.loadingStatus = loadState.refresh is LoadState.Loading
                mBinding?.errorStatus = loadState.refresh is LoadState.Error
            }

            addDataRefreshListener {
                mBinding?.emptyStatus = itemCount == 0
            }
        }
    }

    private val userId by lazy { arguments?.getInt("user") ?: 0 }

    private var mArticleJob: Job? = null
    private var mUserInfoJob: Job? = null

    override fun actionsOnViewInflate() {
        fetchUserInfo()
        fetchSharedArticles()
    }

    override fun getLayoutId(): Int = R.layout.fragment_shared_user

    override fun initFragment(view: View, savedInstanceState: Bundle?) {
        mBinding?.let { binding ->
            arguments?.getString("name")?.let {
                binding.nick = it
                binding.avatarKey = it.toCharArray()[0].toString().toUpperCase(Locale.getDefault())
            }

            binding.refreshColor = R.color.colorAccent
            binding.refreshListener = SwipeRefreshLayout.OnRefreshListener {
                fetchSharedArticles()
            }

            binding.adapter = mAdapter.withLoadStateFooter(PagingLoadStateAdapter { mAdapter.retry() })
            binding.itemClick = OnItemClickListener { position, _ ->
                mAdapter.getItemData(position)?.let {
                    WebsiteDetailFragment.viewDetail(
                        mNavController,
                        R.id.action_sharedUserFragment_to_websiteDetailFragment,
                        it.link
                    )
                }
            }
            binding.itemLongClick = OnItemLongClickListener { position, _ ->
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
            binding.gesture = DoubleClickListener {
                doubleTap = {
                    binding.articleList.scrollToTop()
                }
            }

            binding.errorReload = ErrorReload {
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

    @OptIn(ExperimentalCoroutinesApi::class)
    private fun fetchSharedArticles() {
        mArticleJob?.cancel()
        mArticleJob = launch {
            mViewModel.getSharedArticles(userId)
                .catch { mBinding?.errorStatus = true }
                .collectLatest { mAdapter.submitData(it) }
        }
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    private fun fetchUserInfo() {
        mUserInfoJob?.cancel()
        mUserInfoJob = launch {
            mViewModel.getUserCoinInfo(userId)
                .catch { LogUtils.debug("error occur") }
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