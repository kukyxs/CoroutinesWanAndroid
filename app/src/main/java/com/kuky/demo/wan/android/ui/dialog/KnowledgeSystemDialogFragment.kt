package com.kuky.demo.wan.android.ui.dialog

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
import com.kuky.demo.wan.android.databinding.DialogKnowledgeSystemBinding
import com.kuky.demo.wan.android.entity.SystemCategory
import com.kuky.demo.wan.android.entity.SystemData
import com.kuky.demo.wan.android.ui.system.*
import com.kuky.demo.wan.android.utils.ScreenUtils

/**
 * @author Taonce.
 * @description
 */
class KnowledgeSystemDialogFragment : BaseDialogFragment<DialogKnowledgeSystemBinding>() {
    private val mFirstAdapter by lazy { KnowledgeSystemTypeAdapter() }
    private val mSecAdapter by lazy { KnowledgeSystemSecTypeAdapter() }
    private val mViewModel by lazy {
        ViewModelProvider(requireActivity(), KnowledgeSystemModelFactory(KnowledgeSystemRepository()))
            .get(KnowledgeSystemViewModel::class.java)
    }
    private var mFirstData: SystemData? = null
    private var mOnClick: SystemClick? = null

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

    override fun getLayoutId() = R.layout.dialog_knowledge_system

    override fun initFragment(view: View, savedInstanceState: Bundle?) {
        mBinding.firstAdapter = mFirstAdapter
        mBinding.secAdapter = mSecAdapter
        mBinding.divider = DividerItemDecoration(requireContext(), DividerItemDecoration.VERTICAL)
        mBinding.firstItemClick = OnItemClickListener { position, _ ->
            mFirstAdapter.getItemData(position)?.let {
                mFirstData = it
                mViewModel.firstSelectedPosition.value = position
                mViewModel.children.value = it.children as MutableList<SystemCategory>
                mViewModel.secSelectedPosition.value = -1
            }
        }
        mBinding.offset = (ScreenUtils.getScreenWidth(requireContext()) * 0.4f).toInt()

        mViewModel.children.observe(this, Observer<MutableList<SystemCategory>> {
            mSecAdapter.setNewData(it)
        })

        mBinding.secItemClick = OnItemClickListener { position, _ ->
            mSecAdapter.getItemData(position)?.let {
                mOnClick?.invoke(this, mFirstData?.name, it.name, it.id)
                mViewModel.secSelectedPosition.value = position
            }
        }
        mViewModel.mType.observe(this, Observer { data ->
            data?.let {
                mFirstAdapter.setNewData(it as MutableList<SystemData>)
                mFirstData = it[mViewModel.firstSelectedPosition.value ?: 0]
                mViewModel.children.value =
                    it[mViewModel.firstSelectedPosition.value ?: 0].children as MutableList<SystemCategory>
            }
        })

        mViewModel.firstSelectedPosition.observe(this, Observer<Int> {
            mFirstAdapter.updateSelectedPosition(it)
            mBinding.firstPosition = it
        })

        mViewModel.secSelectedPosition.observe(this, Observer<Int> {
            mSecAdapter.updateSelectedPosition(it)
            mBinding.secPosition = it
        })
    }

    fun setSelect(block: SystemClick): KnowledgeSystemDialogFragment {
        this.mOnClick = block
        return this
    }
}

typealias SystemClick = (KnowledgeSystemDialogFragment, String?, String?, Int) -> Unit

