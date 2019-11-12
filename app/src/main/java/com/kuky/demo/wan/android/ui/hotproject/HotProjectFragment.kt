package com.kuky.demo.wan.android.ui.hotproject

import android.os.Bundle
import android.view.View
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.paging.PagedList
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.kuky.demo.wan.android.R
import com.kuky.demo.wan.android.base.*
import com.kuky.demo.wan.android.databinding.FragmentHotProjectBinding
import com.kuky.demo.wan.android.entity.ProjectCategoryData
import com.kuky.demo.wan.android.entity.ProjectDetailData
import com.kuky.demo.wan.android.ui.collection.CollectionModelFactory
import com.kuky.demo.wan.android.ui.collection.CollectionRepository
import com.kuky.demo.wan.android.ui.collection.CollectionViewModel
import com.kuky.demo.wan.android.ui.dialog.ProjectCategoryDialog
import com.kuky.demo.wan.android.ui.main.MainModelFactory
import com.kuky.demo.wan.android.ui.main.MainRepository
import com.kuky.demo.wan.android.ui.main.MainViewModel
import com.kuky.demo.wan.android.ui.websitedetail.WebsiteDetailFragment
import com.kuky.demo.wan.android.ui.widget.ErrorReload
import org.jetbrains.anko.alert
import org.jetbrains.anko.noButton
import org.jetbrains.anko.toast
import org.jetbrains.anko.yesButton

/**
 * @author kuky.
 * @description 首页项目模块界面
 */
class HotProjectFragment : BaseFragment<FragmentHotProjectBinding>() {
    private var mId = 0
    private var mTitle = ""

    private val mViewModel: HotProjectViewModel by lazy {
        ViewModelProvider(requireActivity(), HotProjectModelFactory(HotProjectRepository()))
            .get(HotProjectViewModel::class.java)
    }

    private val mCollectionViewModel by lazy {
        ViewModelProvider(requireActivity(), CollectionModelFactory(CollectionRepository()))
            .get(CollectionViewModel::class.java)
    }

    private val mLoginViewModel by lazy {
        ViewModelProvider(requireActivity(), MainModelFactory(MainRepository()))
            .get(MainViewModel::class.java)
    }

    private var errorOnCategories = false

    private val mAdapter: HomeProjectAdapter by lazy { HomeProjectAdapter() }

    override fun getLayoutId(): Int = R.layout.fragment_hot_project

    override fun initFragment(view: View, savedInstanceState: Bundle?) {
        mBinding.refreshColor = R.color.colorAccent
        mBinding.refreshListener = SwipeRefreshLayout.OnRefreshListener {
            fetchProjects(mId, mTitle)
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
                            mViewModel.projects?.value?.get(position)?.collect = true
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

        mBinding.gesture = DoubleClickListener({
            ProjectCategoryDialog().apply {
                onSelectedListener = { dialog, category ->
                    mId = category.id
                    mTitle = category.name
                    fetchProjects(category.id, category.name)
                    dialog?.dismiss()
                }
            }.show(childFragmentManager, "category")
        }, { mBinding.projectList.scrollToTop() })

        mBinding.errorReload = ErrorReload {
            if (errorOnCategories) fetchCategories()
            else fetchProjects(mId, mTitle)
        }

        fetchCategories()

        mViewModel.categories.observe(this, Observer<List<ProjectCategoryData>> { list ->
            list[0].let {
                // 保存当前选项，用于刷新
                mId = it.id
                mTitle = it.name
                fetchProjects(it.id, it.name)
            }
        })

        // 登录状态切换
        mLoginViewModel.hasLogin.observe(this, Observer<Boolean> {
            if (!it) {
                mViewModel.projects?.value?.forEach { arc ->
                    arc.collect = false
                }
            } else {
                fetchProjects(mId, mTitle)
            }
        })
    }

    // 获取分类信息
    private fun fetchCategories() {
        mViewModel.fetchCategories {
            errorOnCategories = true
            mBinding.errorStatus = true
            mBinding.projectType.text = resources.getString(R.string.text_place_holder)
        }
    }


    // 获取分类下列表
    private fun fetchProjects(id: Int, title: String) {
        mBinding.projectType.text = title.renderHtml()
        mViewModel.fetchDiffCategoryProjects(id) { code, _ ->
            errorOnCategories = false
            when (code) {
                PAGING_THROWABLE_LOAD_CODE_INITIAL -> mBinding.errorStatus = true
                PAGING_THROWABLE_LOAD_CODE_AFTER -> requireContext().toast("加载更多数据出错啦~请检查网络")
            }
        }

        mBinding.errorStatus = false
        mBinding.refreshing = true
        mViewModel.projects?.observe(this, Observer<PagedList<ProjectDetailData>> {
            mAdapter.submitList(it)
            delayLaunch(1000) { mBinding.refreshing = false }
        })
    }
}