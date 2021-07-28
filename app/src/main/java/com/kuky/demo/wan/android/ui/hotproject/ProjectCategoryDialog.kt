package com.kuky.demo.wan.android.ui.hotproject

import android.app.Dialog
import android.os.Bundle
import android.view.Gravity
import android.view.View
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.DividerItemDecoration
import com.kuky.demo.wan.android.R
import com.kuky.demo.wan.android.base.BaseDialogFragment
import com.kuky.demo.wan.android.databinding.DialogProjectCategoryBinding
import com.kuky.demo.wan.android.entity.ProjectCategoryData
import com.kuky.demo.wan.android.listener.OnItemClickListener
import com.kuky.demo.wan.android.utils.screenWidth
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import org.koin.androidx.scope.fragmentScope
import org.koin.androidx.viewmodel.ext.android.viewModel
import org.koin.core.component.KoinScopeComponent
import org.koin.core.component.inject
import org.koin.core.scope.Scope

/**
 * @author kuky.
 * @description
 */
class ProjectCategoryDialog : BaseDialogFragment<DialogProjectCategoryBinding>(), KoinScopeComponent {

    override val scope: Scope by fragmentScope()

    var onSelectedListener: ((Dialog?, ProjectCategoryData) -> Unit)? = null

    private val mViewModel by viewModel<HotProjectViewModel>()

    private val mAdapter by inject<ProjectCategoryAdapter>()

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