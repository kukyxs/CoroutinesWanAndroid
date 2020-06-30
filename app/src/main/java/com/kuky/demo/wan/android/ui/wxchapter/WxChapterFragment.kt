package com.kuky.demo.wan.android.ui.wxchapter

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.View
import androidx.lifecycle.ViewModelProvider
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.kuky.demo.wan.android.R
import com.kuky.demo.wan.android.base.*
import com.kuky.demo.wan.android.databinding.FragmentWxChapterBinding
import com.kuky.demo.wan.android.ui.main.MainFragment
import com.kuky.demo.wan.android.ui.wxchapterlist.WxChapterListFragment
import com.kuky.demo.wan.android.utils.Injection
import com.kuky.demo.wan.android.widget.ErrorReload
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.launch

/**
 * @author kuky.
 * @description 首页公众号模块界面
 */
class WxChapterFragment : BaseFragment<FragmentWxChapterBinding>() {

    private val mViewModel by lazy {
        ViewModelProvider(requireActivity(), Injection.provideWxChapterViewModelFactory())
            .get(WxChapterViewModel::class.java)
    }

    private val mAdapter by lazy { WxChapterAdapter() }

    private var mChapterJob: Job? = null

    override fun actionsOnViewInflate() = fetchWxChapter()

    override fun getLayoutId(): Int = R.layout.fragment_wx_chapter

    @SuppressLint("ClickableViewAccessibility")
    override fun initFragment(view: View, savedInstanceState: Bundle?) {
        mBinding?.let { binding ->
            binding.refreshColor = R.color.colorAccent
            binding.refreshListener = SwipeRefreshLayout.OnRefreshListener {
                fetchWxChapter()
            }

            binding.adapter = mAdapter
            binding.listener = OnItemClickListener { position, _ ->
                (parentFragment as? MainFragment)?.closeMenu()
                mAdapter.getItemData(position)?.let {
                    WxChapterListFragment.navigate(mNavController, R.id.action_mainFragment_to_wxChapterListFragment, it.id, it.name)
                }
            }

            binding.rcvChapter.setOnTouchListener { _, _ ->
                (parentFragment as? MainFragment)?.closeMenu()
                false
            }

            binding.errorReload = ErrorReload { fetchWxChapter() }

            binding.gesture = DoubleClickListener {
                doubleTap = {
                    binding.rcvChapter.scrollToTop()
                }
            }
        }
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    private fun fetchWxChapter() {
        mChapterJob?.cancel()
        mChapterJob = launch {
            mViewModel.getWxChapterList().catch {
                pageState(NetworkState.FAILED)
                mBinding?.wxChapterType?.text = resources.getText(R.string.text_place_holder)
            }.onStart {
                pageState(NetworkState.RUNNING)
            }.collectLatest {
                mAdapter.update(it)
                pageState(NetworkState.SUCCESS)
                mBinding?.wxChapterType?.text = resources.getText(R.string.wx_chapter)
                if (it.isEmpty()) mBinding?.emptyStatus = true
            }
        }
    }

    private fun pageState(state: NetworkState) = mBinding?.run {
        refreshing = state == NetworkState.RUNNING
        loadingStatus = state == NetworkState.RUNNING
        errorStatus = state == NetworkState.FAILED
    }
}