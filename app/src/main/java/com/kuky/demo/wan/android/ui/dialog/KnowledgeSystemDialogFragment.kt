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
    private val firstAdapter by lazy { KnowledgeSystemTypeAdapter() }
    private val secAdapter by lazy { KnowledgeSystemSecTypeAdapter() }
    private val viewModel by lazy {
        ViewModelProviders.of(requireActivity(), KnowledgeSystemModelFactory(KnowledgeSystemRepository()))
            .get(KnowledgeSystemViewModel::class.java)
    }
    private var firstData: SystemData? = null

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
        mBinding.firstAdapter = firstAdapter
        mBinding.secAdapter = secAdapter
        mBinding.firstItemClick = OnItemClickListener { position, _ ->
            firstAdapter.getItemData(position)?.let {
                firstData = it
                secAdapter.setNewData(it.children as MutableList<SystemCategory>)
            }
        }
        mBinding.secItemClick = OnItemClickListener { position, _ ->
            secAdapter.getItemData(position)?.let {
                onClick?.invoke(this, firstData?.name, it.name, it.id)
            }
        }
        viewModel.type.observe(this, Observer {
            firstAdapter.setNewData(it as MutableList<SystemData>)
            firstData = it[0]
            secAdapter.setNewData(it[0].children as MutableList<SystemCategory>)
        })
    }

    fun setSelect(block: (dialog: KnowledgeSystemDialogFragment, first: String?, sec: String?, cid: Int) -> Unit): KnowledgeSystemDialogFragment {
        this.onClick = block
        return this
    }

    private var onClick: ((KnowledgeSystemDialogFragment, String?, String?, Int) -> Unit)? = null
}

