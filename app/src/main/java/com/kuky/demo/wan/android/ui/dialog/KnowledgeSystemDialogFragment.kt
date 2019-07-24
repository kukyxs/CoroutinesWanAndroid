package com.kuky.demo.wan.android.ui.dialog

import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.Gravity
import android.view.View
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
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
        ViewModelProviders.of(requireActivity(), KnowledgeSystemModelFactory(KnowledgeSystemRepository()))
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
        mBinding.firstItemClick = OnItemClickListener { position, _ ->
            mFirstAdapter.getItemData(position)?.let {
                mFirstData = it
                mSecAdapter.setNewData(it.children as MutableList<SystemCategory>)
            }
        }
        mBinding.secItemClick = OnItemClickListener { position, _ ->
            mSecAdapter.getItemData(position)?.let {
                mOnClick?.invoke(this, mFirstData?.name, it.name, it.id)
            }
        }
        mViewModel.mType.observe(this, Observer {
            mFirstAdapter.setNewData(it as MutableList<SystemData>)
            mFirstData = it[0]
            mSecAdapter.setNewData(it[0].children as MutableList<SystemCategory>)
        })
    }

    fun setSelect(block: SystemClick): KnowledgeSystemDialogFragment {
        this.mOnClick = block
        return this
    }
}
typealias SystemClick = (KnowledgeSystemDialogFragment, String?, String?, Int) -> Unit

