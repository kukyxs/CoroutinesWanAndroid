package com.kuky.demo.wan.android.ui.hotproject

import android.os.Bundle
import android.view.View
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.paging.PagedList
import com.kuky.demo.wan.android.R
import com.kuky.demo.wan.android.base.BaseFragment
import com.kuky.demo.wan.android.base.OnItemClickListener
import com.kuky.demo.wan.android.base.OnItemLongClickListener
import com.kuky.demo.wan.android.databinding.FragmentHotProjectBinding
import com.kuky.demo.wan.android.entity.ProjectCategoryData
import com.kuky.demo.wan.android.entity.ProjectDetailData
import com.kuky.demo.wan.android.ui.dialog.ProjectCategoryDialog
import com.kuky.demo.wan.android.ui.websitedetail.WebsiteDetailFragment
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

    private val mViewModel: HotProjectViewModel by lazy {
        ViewModelProviders.of(requireActivity(), HotProjectModelFactory(HotProjectRepository()))
            .get(HotProjectViewModel::class.java)
    }

    private val mAdapter: HomeProjectAdapter by lazy { HomeProjectAdapter() }

    override fun getLayoutId(): Int = R.layout.fragment_hot_project

    override fun initFragment(view: View, savedInstanceState: Bundle?) {
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
                requireContext().alert("是否收藏「${article.title}」") {
                    yesButton {
                        mViewModel.collectProject(article.id, {
                            requireContext().toast("收藏成功")
                        }, { message ->
                            requireContext().toast(message)
                        })
                    }
                    noButton { }
                }.show()
            }
            true
        }

        mViewModel.fetchCategories()

        mViewModel.categories.observe(this, Observer<List<ProjectCategoryData>> {
            updateProjects(it[0])
        })
    }

    private fun updateProjects(category: ProjectCategoryData) {
        mBinding.root.project_type.text = category.name
        mViewModel.fetchDiffCategoryProjects(category.id)
        mViewModel.projects?.observe(this, Observer<PagedList<ProjectDetailData>> {
            mAdapter.submitList(it)
        })
    }

    fun selectCategory(view: View) {
        ProjectCategoryDialog().setOnSelectedListener { dialog, category ->
            dialog.dismiss()
            updateProjects(category)
        }.show(childFragmentManager, "category")
    }
}