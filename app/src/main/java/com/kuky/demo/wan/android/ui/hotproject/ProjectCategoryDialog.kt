package com.kuky.demo.wan.android.ui.hotproject

import android.app.Dialog
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
import com.kuky.demo.wan.android.utils.Injection
import com.kuky.demo.wan.android.utils.screenWidth
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

/**
 * @author kuky.
 * @description
 */
class ProjectCategoryDialog : BaseDialogFragment<DialogProjectCategoryBinding>() {

    var onSelectedListener: ((Dialog?, ProjectCategoryData) -> Unit)? = null

    private val mAdapter by lazy { ProjectCategoryAdapter() }

    private val mViewModel by lazy {
        ViewModelProvider(requireActivity(), Injection.provideHotProjectViewModelFactory())
            .get(HotProjectViewModel::class.java)
    }

    override fun layoutId(): Int = R.layout.dialog_project_category

    @OptIn(ExperimentalCoroutinesApi::class)
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

        mViewModel.selectedCategoryPosition.observe(this, Observer<Int> {
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