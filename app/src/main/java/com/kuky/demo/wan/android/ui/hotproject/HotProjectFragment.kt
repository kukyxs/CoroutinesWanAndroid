package com.kuky.demo.wan.android.ui.hotproject

import android.annotation.SuppressLint
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
import com.kuky.demo.wan.android.ui.main.MainFragment
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
    private var isFirstObserver = true

    private val mAdapter: HomeProjectAdapter by lazy { HomeProjectAdapter() }

    override fun actionsOnViewInflate() {
        fetchCategories()

        // 登录状态切换
        mLoginViewModel.hasLogin.observe(this, Observer<Boolean> {
            if (isFirstObserver) {
                isFirstObserver = false
                return@Observer
            }

            if (!it) {
                mViewModel.projects?.value?.forEach { arc ->
                    arc.collect = false
                }
            } else {
                fetchProjects(mId, mTitle)
            }
        })
    }

    override fun getLayoutId(): Int = R.layout.fragment_hot_project

    @SuppressLint("ClickableViewAccessibility")
    override fun initFragment(view: View, savedInstanceState: Bundle?) {
        mBinding?.let { binding ->
            binding.refreshColor = R.color.colorAccent
            binding.refreshListener = SwipeRefreshLayout.OnRefreshListener {
                fetchProjects(mId, mTitle)
            }

            binding.adapter = mAdapter
            binding.holder = this@HotProjectFragment
            binding.itemClick = OnItemClickListener { position, _ ->
                mAdapter.getItemData(position)?.let {
                    (parentFragment as? MainFragment)?.closeMenu()
                    WebsiteDetailFragment.viewDetail(
                        mNavController,
                        R.id.action_mainFragment_to_websiteDetailFragment,
                        it.link
                    )
                }
            }

            binding.itemLongClick = OnItemLongClickListener { position, _ ->
                mAdapter.getItemData(position)?.let { article ->
                    (parentFragment as? MainFragment)?.closeMenu()
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
            }

            binding.projectList.setOnTouchListener { _, _ ->
                (parentFragment as? MainFragment)?.closeMenu()
                false
            }

            binding.gesture = DoubleClickListener {
                singleTap = {
                    ProjectCategoryDialog().apply {
                        onSelectedListener = { dialog, category ->
                            mId = category.id
                            mTitle = category.name
                            fetchProjects(category.id, category.name)
                            dialog?.dismiss()
                        }
                    }.showAllowStateLoss(childFragmentManager, "category")
                }
                doubleTap = { binding.projectList.scrollToTop() }
            }

            binding.errorReload = ErrorReload {
                if (errorOnCategories) fetchCategories()
                else fetchProjects(mId, mTitle)
            }
        }
    }

    // 获取分类信息
    private fun fetchCategories() {
        mViewModel.fetchCategories()

        mViewModel.typeNetState.observe(this, Observer {
            when (it.state) {
                State.RUNNING, State.SUCCESS -> injectStates(refreshing = true, loading = true)

                State.FAILED -> {
                    errorOnCategories = true
                    mBinding?.projectType?.text = resources.getString(R.string.text_place_holder)
                    injectStates(error = true)
                }
            }
        })

        mViewModel.categories.observe(this, Observer<List<ProjectCategoryData>> { list ->
            list[0].let {
                mId = it.id
                mTitle = it.name
                fetchProjects(it.id, it.name, false)
            }
        })
    }


    // 获取分类下列表
    private fun fetchProjects(id: Int, title: String, isRefresh: Boolean = true) {
        mBinding?.projectType?.text = title.renderHtml()

        mViewModel.fetchDiffCategoryProjects(id) {
            mBinding?.emptyStatus = true
        }

        mViewModel.netState?.observe(this, Observer {
            when (it.state) {
                State.RUNNING -> injectStates(refreshing = true, loading = !isRefresh)

                State.SUCCESS -> injectStates()

                State.FAILED -> {
                    errorOnCategories = false
                    if (it.code == ERROR_CODE_INIT) injectStates(error = true)
                    else requireContext().toast(R.string.no_net_on_loading)
                }
            }
        })

        mViewModel.projects?.observe(this, Observer<PagedList<ProjectDetailData>> {
            mAdapter.submitList(it)
        })
    }

    private fun injectStates(refreshing: Boolean = false, loading: Boolean = false, error: Boolean = false) {
        mBinding?.let { binding ->
            binding.refreshing = refreshing
            binding.loadingStatus = loading
            binding.errorStatus = error
        }
    }
}