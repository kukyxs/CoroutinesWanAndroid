package com.kuky.demo.wan.android.ui.hotproject

import android.app.Dialog
import android.os.Bundle
import android.view.Gravity
import android.view.View
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.DividerItemDecoration
import com.kuky.demo.wan.android.R
import com.kuky.demo.wan.android.base.BaseDialogFragment
import com.kuky.demo.wan.android.base.OnItemClickListener
import com.kuky.demo.wan.android.databinding.DialogProjectCategoryBinding
import com.kuky.demo.wan.android.entity.ProjectCategoryData
import com.kuky.demo.wan.android.utils.screenWidth
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import org.koin.androidx.scope.lifecycleScope
import org.koin.androidx.viewmodel.ext.android.viewModel

/**
 * @author kuky.
 * @description
 */
class ProjectCategoryDialog : BaseDialogFragment<DialogProjectCategoryBinding>() {

    var onSelectedListener: ((Dialog?, ProjectCategoryData) -> Unit)? = null

    private val mViewModel by viewModel<HotProjectViewModel>()

    private val mAdapter by lifecycleScope.inject<ProjectCategoryAdapter>()

    override fun layoutId(): Int = R.layout.dialog_project_category

    override fun initDialog(view: View, savedInstanceState: Bundle?) {
        mBinding.adapter = mAdapter
        mBinding.divider = DividerItemDecoration(requireContext(), DividerItemDecoration.VERTICAL)
        mBinding.listener = OnItemClickListener { position, _ ->
            onSelectedListener?.invoke(dialog, mAdapter.getItemData(position)!!)
            mViewModel.selectedCategoryPosition.value = position
        }

        mBinding.offset = (screenWidth * 0.4f).toInt()

        launch {
            mViewModel.getCategories().catch { dismiss() }
                .collectLatest { mAdapter.setCategories(it) }
        }

        mViewModel.selectedCategoryPosition.observe(this, Observer {
            mAdapter.updateSelectedPosition(it)
            mBinding.position = it
        })
    }

    override fun dialogFragmentAttributes() = dialog?.window?.attributes?.apply {
        width = (screenWidth * 0.8f).toInt()
        height = width
        gravity = Gravity.CENTER
    }
}