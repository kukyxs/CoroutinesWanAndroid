package com.kuky.demo.wan.android.ui.wxchapter

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.View
import androidx.navigation.fragment.findNavController
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.kuky.demo.wan.android.R
import com.kuky.demo.wan.android.base.BaseFragment
import com.kuky.demo.wan.android.base.DoubleClickListener
import com.kuky.demo.wan.android.base.OnItemClickListener
import com.kuky.demo.wan.android.base.scrollToTop
import com.kuky.demo.wan.android.databinding.FragmentWxChapterBinding
import com.kuky.demo.wan.android.ui.main.MainFragment
import com.kuky.demo.wan.android.ui.wxchapterlist.WxChapterListFragment
import com.kuky.demo.wan.android.widget.ErrorReload
import com.kuky.demo.wan.android.widget.RequestStatusCode
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.launch
import org.koin.androidx.scope.lifecycleScope
import org.koin.androidx.viewmodel.ext.android.viewModel

/**
 * @author kuky.
 * @description 首页公众号模块界面
 */
class WxChapterFragment : BaseFragment<FragmentWxChapterBinding>() {

    private val mViewModel by viewModel<WxChapterViewModel>()

    private val mAdapter by lifecycleScope.inject<WxChapterAdapter>()

    private var mChapterJob: Job? = null

    override fun actionsOnViewInflate() = fetchWxChapter()

    override fun getLayoutId(): Int = R.layout.fragment_wx_chapter

    @SuppressLint("ClickableViewAccessibility")
    override fun initFragment(view: View, savedInstanceState: Bundle?) {
        mBinding?.run {
            refreshColor = R.color.colorAccent
            refreshListener = SwipeRefreshLayout.OnRefreshListener {
                fetchWxChapter()
            }

            adapter = mAdapter
            listener = OnItemClickListener { position, _ ->
                (parentFragment as? MainFragment)?.closeMenu()
                mAdapter.getItemData(position)?.let {
                    WxChapterListFragment.navigate(
                        findNavController(),
                        R.id.action_mainFragment_to_wxChapterListFragment,
                        it.id, it.name
                    )
                }
            }

            rcvChapter.setOnTouchListener { _, _ ->
                (parentFragment as? MainFragment)?.closeMenu()
                false
            }

            errorReload = ErrorReload { fetchWxChapter() }

            gesture = DoubleClickListener {
                doubleTap = { rcvChapter.scrollToTop() }
            }
        }
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    private fun fetchWxChapter() {
        mChapterJob?.cancel()
        mChapterJob = launch {
            mViewModel.getWxChapterList().catch {
                mBinding?.statusCode = RequestStatusCode.Error
                mBinding?.wxChapterType?.text = resources.getText(R.string.text_place_holder)
            }.onStart {
                mBinding?.refreshing = true
                mBinding?.statusCode = RequestStatusCode.Loading
            }.collectLatest {
                mAdapter.update(it)
                mBinding?.refreshing = false
                mBinding?.wxChapterType?.text = resources.getText(R.string.wx_chapter)
                mBinding?.statusCode = if (it.isNullOrEmpty()) RequestStatusCode.Empty else RequestStatusCode.Succeed
            }
        }
    }
}