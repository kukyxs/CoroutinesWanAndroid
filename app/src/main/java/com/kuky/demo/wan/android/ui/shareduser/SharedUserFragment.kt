package com.kuky.demo.wan.android.ui.shareduser

import android.graphics.Color
import android.os.Bundle
import android.text.Spannable
import android.text.SpannableStringBuilder
import android.text.style.ForegroundColorSpan
import android.view.View
import androidx.annotation.IdRes
import androidx.core.content.ContextCompat
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.NavController
import androidx.paging.PagedList
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.kuky.demo.wan.android.R
import com.kuky.demo.wan.android.base.*
import com.kuky.demo.wan.android.databinding.FragmentSharedUserBinding
import com.kuky.demo.wan.android.entity.UserArticleDetail
import com.kuky.demo.wan.android.ui.collection.CollectionModelFactory
import com.kuky.demo.wan.android.ui.collection.CollectionRepository
import com.kuky.demo.wan.android.ui.collection.CollectionViewModel
import com.kuky.demo.wan.android.ui.websitedetail.WebsiteDetailFragment
import com.kuky.demo.wan.android.ui.widget.ErrorReload
import com.kuky.demo.wan.android.utils.LogUtils
import org.jetbrains.anko.alert
import org.jetbrains.anko.noButton
import org.jetbrains.anko.toast
import org.jetbrains.anko.yesButton
import java.util.*

/**
 * @author kuky.
 * @description
 */
class SharedUserFragment : BaseFragment<FragmentSharedUserBinding>() {

    private val mViewModel: SharedUserViewModel by lazy {
        ViewModelProvider(requireActivity(), SharedUserModelFactory(UserSharedRepository()))
            .get(SharedUserViewModel::class.java)
    }

    private val mCollectionViewModel by lazy {
        ViewModelProvider(requireActivity(), CollectionModelFactory(CollectionRepository()))
            .get(CollectionViewModel::class.java)
    }

    private val mAdapter: UserSharedArticleAdapter by lazy {
        UserSharedArticleAdapter()
    }

    private val userId by lazy {
        arguments?.getInt("user") ?: 0
    }

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

            binding.adapter = mAdapter
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
                            if (!article.collect) mCollectionViewModel.collectArticle(article.id, {
                                mViewModel.articles?.value?.get(position)?.collect = true
                                requireContext().toast("收藏成功")
                            }, { message ->
                                requireContext().toast(message)
                            })
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
            }
        }
    }

    private fun fetchSharedArticles() {
        mViewModel.fetchSharedArticles(userId) {
            mBinding?.emptyStatus = true
        }

        mViewModel.netState?.observe(this, Observer {
            when (it.state) {
                State.RUNNING -> injectStates(refreshing = true, loading = true)

                State.SUCCESS -> injectStates()

                State.FAILED -> {
                    LogUtils.error(it.msg)
                    if (it.code == ERROR_CODE_INIT) injectStates(error = true)
                    else requireContext().toast(R.string.no_net_on_loading)
                }
            }
        })

        mViewModel.articles?.observe(this, Observer<PagedList<UserArticleDetail>> {
            mAdapter.submitList(it)
        })
    }

    private fun fetchUserInfo() {
        mViewModel.fetchUserInfo(userId)
        mViewModel.userCoin.observe(this, Observer {
            mBinding?.shared = SpannableStringBuilder().apply {
                setSpan(ForegroundColorSpan(Color.RED), run {
                    append("共分享了")
                    length
                }, run {
                    append("\t${it?.shareArticles?.total ?: 0}\t")
                    length
                }, Spannable.SPAN_INCLUSIVE_EXCLUSIVE)
                append("篇文章")
            }

            it?.coinInfo?.let { coin ->
                mBinding?.coin = SpannableStringBuilder("${coin.coinCount}").apply {
                    setSpan(
                        ForegroundColorSpan(ContextCompat.getColor(requireContext(), R.color.coin_color)),
                        0, length, Spannable.SPAN_INCLUSIVE_EXCLUSIVE
                    )

                    setSpan(
                        ForegroundColorSpan(ContextCompat.getColor(requireContext(), R.color.colorPrimary)),
                        run {
                            append("\t/\t\t")
                            length
                        }, run {
                            append("Lv${coin.level}")
                            length
                        }, Spannable.SPAN_INCLUSIVE_EXCLUSIVE
                    )

                    setSpan(
                        ForegroundColorSpan(ContextCompat.getColor(requireContext(), R.color.colorAccent)),
                        run {
                            append("\t\t/\t\t")
                            length
                        }, run {
                            append("R${coin.rank}")
                            length
                        }, Spannable.SPAN_INCLUSIVE_EXCLUSIVE
                    )
                }
            }
        })
    }

    private fun injectStates(refreshing: Boolean = false, loading: Boolean = false, error: Boolean = false) {
        mBinding?.let { binding ->
            binding.refreshing = refreshing
            binding.loadingStatus = loading
            binding.errorStatus = error
        }
    }

    companion object {
        fun navToUser(controller: NavController, @IdRes navId: Int, user: Int, name: String) =
            controller.navigate(navId, Bundle().apply {
                putInt("user", user)
                putString("name", name)
            })
    }
}