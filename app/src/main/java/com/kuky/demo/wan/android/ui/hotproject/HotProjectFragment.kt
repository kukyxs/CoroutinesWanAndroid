package com.kuky.demo.wan.android.ui.hotproject

import android.os.Bundle
import android.os.Handler
import android.view.View
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.paging.PagedList
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.kuky.demo.wan.android.R
import com.kuky.demo.wan.android.base.BaseFragment
import com.kuky.demo.wan.android.base.OnItemClickListener
import com.kuky.demo.wan.android.base.OnItemLongClickListener
import com.kuky.demo.wan.android.data.PreferencesHelper
import com.kuky.demo.wan.android.databinding.FragmentHotProjectBinding
import com.kuky.demo.wan.android.entity.ProjectCategoryData
import com.kuky.demo.wan.android.entity.ProjectDetailData
import com.kuky.demo.wan.android.ui.collection.CollectionFactory
import com.kuky.demo.wan.android.ui.collection.CollectionRepository
import com.kuky.demo.wan.android.ui.collection.CollectionViewModel
import com.kuky.demo.wan.android.ui.dialog.ProjectCategoryDialog
import com.kuky.demo.wan.android.ui.websitedetail.WebsiteDetailFragment
import com.kuky.demo.wan.android.utils.TextFormatUtils
import kotlinx.android.synthetic.main.fragment_hot_project.view.*
import org.jetbrains.anko.alert
import org.jetbrains.anko.noButton
import org.jetbrains.anko.toast
import org.jetbrains.anko.yesButton

/**
 * @author kuky.
 * @description 首页项目模块界面
 */
class HotProjectFragment : BaseFragment<FragmentHotProjectBinding>() {
    companion object {
        private val mHandler = Handler()
    }

    private val mViewModel: HotProjectViewModel by lazy {
        ViewModelProvider(requireActivity(), HotProjectModelFactory(HotProjectRepository()))
            .get(HotProjectViewModel::class.java)
    }
    private val mCollectionViewModel by lazy {
        ViewModelProvider(requireActivity(), CollectionFactory(CollectionRepository()))
            .get(CollectionViewModel::class.java)
    }

    private val mAdapter: HomeProjectAdapter by lazy { HomeProjectAdapter() }
    // 用来修改article的collect字段，并且submitList()
    private lateinit var mProjectList: PagedList<ProjectDetailData>

    override fun getLayoutId(): Int = R.layout.fragment_hot_project

    override fun initFragment(view: View, savedInstanceState: Bundle?) {
        mBinding.refreshColor = R.color.colorAccent
        mBinding.refreshListener = SwipeRefreshLayout.OnRefreshListener {
            PreferencesHelper.fetchProjectCategory(requireContext()).let {
                fetchProjects(it["id"] as Int, it["title"] as String)
            }
        }

        mBinding.adapter = mAdapter
        mBinding.holder = this@HotProjectFragment
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
                // 根据是否收藏显示不同信息
                requireContext().alert(if (article.collect) "「${article.title}」已收藏" else " 是否收藏 「${article.title}」") {
                    yesButton {
                        if (!article.collect) mCollectionViewModel.collectArticle(article.id, {
                            mProjectList[position]?.collect = true
                            mAdapter.submitList(mProjectList)
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

        mViewModel.fetchCategories()

        mViewModel.categories.observe(this, Observer<List<ProjectCategoryData>> { list ->
            list[0].let {
                // 保存当前选项，用于刷新
                PreferencesHelper.saveProjectCategory(requireContext(), it)
                fetchProjects(it.id, it.name)
            }
        })
    }

    private fun fetchProjects(id: Int, title: String) {
        mBinding.root.project_type.text = TextFormatUtils.renderHtmlText(title)
        mViewModel.fetchDiffCategoryProjects(id)
        mBinding.refreshing = true
        mViewModel.projects?.observe(this, Observer<PagedList<ProjectDetailData>> {
            mProjectList = it
            mAdapter.submitList(it)
            mHandler.postDelayed({
                mBinding.refreshing = false
                mBinding.dataNull = it.isEmpty()
            }, 500)
        })
    }

    fun selectCategory(view: View) {
        ProjectCategoryDialog().setOnSelectedListener { dialog, category ->
            PreferencesHelper.saveProjectCategory(requireContext(), category)
            fetchProjects(category.id, category.name)
            dialog.dismiss()
        }.show(childFragmentManager, "category")
    }
}