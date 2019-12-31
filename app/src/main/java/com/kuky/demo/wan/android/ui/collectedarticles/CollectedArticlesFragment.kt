package com.kuky.demo.wan.android.ui.collectedarticles

import android.os.Bundle
import android.view.View
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.kuky.demo.wan.android.R
import com.kuky.demo.wan.android.base.*
import com.kuky.demo.wan.android.databinding.FragmentCollectedArticlesBinding
import com.kuky.demo.wan.android.ui.websitedetail.WebsiteDetailFragment
import com.kuky.demo.wan.android.ui.widget.ErrorReload
import org.jetbrains.anko.alert
import org.jetbrains.anko.noButton
import org.jetbrains.anko.okButton
import org.jetbrains.anko.toast

/**
 * @author kuky.
 * @description
 */
class CollectedArticlesFragment : BaseFragment<FragmentCollectedArticlesBinding>() {

    private val mViewModel by lazy {
        ViewModelProvider(requireActivity(), CollectedArticlesModelFactory(CollectedArticlesRepository()))
            .get(CollectedArticlesViewModel::class.java)
    }

    private val mAdapter by lazy { CollectedArticlesAdapter() }

    override fun actionsOnViewInflate() {
        fetchCollectedArticleList(false)
    }

    override fun getLayoutId(): Int = R.layout.fragment_collected_articles

    override fun initFragment(view: View, savedInstanceState: Bundle?) {
        mBinding?.let { binding ->
            binding.refreshColor = R.color.colorAccent
            binding.refreshListener = SwipeRefreshLayout.OnRefreshListener {
                fetchCollectedArticleList()
            }

            binding.adapter = mAdapter
            binding.listener = OnItemClickListener { position, _ ->
                mAdapter.getItemData(position)?.let { data ->
                    WebsiteDetailFragment.viewDetail(
                        mNavController,
                        R.id.action_collectionFragment_to_websiteDetailFragment,
                        data.link
                    )
                }
            }
            binding.longListener = OnItemLongClickListener { position, _ ->
                requireActivity().alert("是否删除本条收藏？") {
                    okButton {
                        mAdapter.getItemData(position)?.let { data ->
                            mViewModel.deleteCollectedArticle(data.id, data.originId,
                                { requireContext().toast("删除成功") }, { requireContext().toast(it) })
                        }
                    }
                    noButton { }
                }.show()
                true
            }

            binding.errorReload = ErrorReload { fetchCollectedArticleList() }
        }
    }

    fun scrollToTop() = mBinding?.collectedArticleList?.scrollToTop()

    private fun fetchCollectedArticleList(isRefresh: Boolean = true) {
        mViewModel.fetchCollectedArticleList { mBinding?.emptyStatus = true }

        mViewModel.netState?.observe(this, Observer {
            when (it.state) {
                State.RUNNING -> injectStates(refreshing = true, loading = !isRefresh)

                State.SUCCESS -> injectStates()

                State.FAILED -> {
                    if (it.code == ERROR_CODE_INIT) injectStates(error = true)
                    else requireContext().toast(R.string.no_net_on_loading)
                }
            }
        })

        mViewModel.mArticles?.observe(requireActivity(), Observer {
            mAdapter.submitList(it)
        })
    }

    private fun injectStates(refreshing: Boolean = false, loading: Boolean = false, error: Boolean = false) {
        mBinding?.let { binding->
            binding.refreshing = refreshing
            binding.loadingStatus = loading
            binding.errorStatus = error
        }
    }
}