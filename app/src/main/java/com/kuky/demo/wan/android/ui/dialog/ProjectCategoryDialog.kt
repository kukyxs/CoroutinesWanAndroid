package com.kuky.demo.wan.android.ui.dialog

import android.app.Dialog
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.Gravity
import android.view.View
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.DividerItemDecoration
import com.kuky.demo.wan.android.R
import com.kuky.demo.wan.android.base.BaseDialogFragment
import com.kuky.demo.wan.android.base.OnItemClickListener
import com.kuky.demo.wan.android.databinding.DialogProjectCategoryBinding
import com.kuky.demo.wan.android.entity.ProjectCategoryData
import com.kuky.demo.wan.android.ui.hotproject.HotProjectModelFactory
import com.kuky.demo.wan.android.ui.hotproject.HotProjectRepository
import com.kuky.demo.wan.android.ui.hotproject.HotProjectViewModel
import com.kuky.demo.wan.android.ui.hotproject.ProjectCategoryAdapter
import com.kuky.demo.wan.android.utils.ScreenUtils

/**
 * @author kuky.
 * @description
 */
class ProjectCategoryDialog : BaseDialogFragment<DialogProjectCategoryBinding>() {

    private val mAdapter: ProjectCategoryAdapter by lazy { ProjectCategoryAdapter() }

    private val mViewModel: HotProjectViewModel by lazy {
        ViewModelProvider(requireActivity(), HotProjectModelFactory(HotProjectRepository()))
            .get(HotProjectViewModel::class.java)
    }

    private var onSelectedListener: OnSelectedListener? = null

    fun setOnSelectedListener(l: OnSelectedListener?): ProjectCategoryDialog {
        this.onSelectedListener = l
        return this@ProjectCategoryDialog
    }

    override fun onStart() {
        super.onStart()

        val attrs = dialog?.window?.attributes?.apply {
            width = (ScreenUtils.getScreenWidth(requireContext()) * 0.8f).toInt()
            height = width
            gravity = Gravity.CENTER
        }

        dialog?.window?.apply {
            setBackgroundDrawable(ColorDrawable(0))
            attributes = attrs
        }
    }

    override fun getLayoutId(): Int = R.layout.dialog_project_category

    override fun initFragment(view: View, savedInstanceState: Bundle?) {
        mBinding.adapter = mAdapter
        mBinding.divider = DividerItemDecoration(requireContext(), DividerItemDecoration.VERTICAL)
        mBinding.listener = OnItemClickListener { position, _ ->
            onSelectedListener?.invoke(dialog!!, mAdapter.getItemData(position)!!)
            mViewModel.selectedCategoryPosition.value = position
        }

        mBinding.offset = (ScreenUtils.getScreenWidth(requireContext()) * 0.4f).toInt()

        mViewModel.categories.observe(this, Observer<List<ProjectCategoryData>> {
            mAdapter.setCategories(it as MutableList<ProjectCategoryData>?)
        })

        mViewModel.selectedCategoryPosition.observe(this, Observer<Int> {
            mAdapter.updateSelectedPosition(it)
            mBinding.position = it
        })
    }
}

typealias OnSelectedListener = (Dialog, ProjectCategoryData) -> Unit