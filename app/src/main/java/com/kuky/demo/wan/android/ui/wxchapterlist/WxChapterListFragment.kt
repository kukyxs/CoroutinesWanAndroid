package com.kuky.demo.wan.android.ui.wxchapterlist


import android.os.Bundle
import android.view.View
import androidx.lifecycle.Observer
import com.kuky.demo.wan.android.R
import com.kuky.demo.wan.android.base.BaseFragment
import com.kuky.demo.wan.android.base.OnItemClickListener
import com.kuky.demo.wan.android.databinding.FragmentWxChapterListBinding
import com.kuky.demo.wan.android.ui.websitedetail.WebsiteDetailFragment
import kotlinx.android.synthetic.main.fragment_wx_chapter_list.*

/**
 * @author Taonce.
 * @description 公众号作者对应的文章列表页
 */
class WxChapterListFragment : BaseFragment<FragmentWxChapterListBinding>() {
    private val mAdapter by lazy { WxChapterListAdapter() }

    override fun getLayoutId(): Int = R.layout.fragment_wx_chapter_list

    override fun initFragment(view: View, savedInstanceState: Bundle?) {
        val id = arguments?.getInt("id")
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
        chapter_list.setHasFixedSize(true)

        val viewModel = getViewModel(WxChapterListViewModel::class.java)
        viewModel.fetchResult(id ?: 0).observe(this, Observer(mAdapter::submitList))
    }
}
