package com.kuky.demo.wan.android.base

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.DiffUtil

/**
 * @author kuky.
 * @description paging adapter 基类, 如果需要多布局, 请使用 MergeAdapter
 */
abstract class BasePagingDataAdapter<T : Any, VB : ViewDataBinding>(val callback: DiffUtil.ItemCallback<T>) :
    PagingDataAdapter<T, BaseViewHolder<VB>>(callback), KLogger {

    var itemListener: OnItemClickListener? = null
    var itemLongClickListener: OnItemLongClickListener? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BaseViewHolder<VB> {
        return BaseViewHolder(
            DataBindingUtil.inflate(LayoutInflater.from(parent.context), getLayoutId(), parent, false)
        )
    }

    override fun onBindViewHolder(holder: BaseViewHolder<VB>, position: Int) {
        val data = getItem(position) ?: return
        setVariable(data, position, holder)
        holder.binding.executePendingBindings()
        holder.binding.root.run {
            setOnClickListener { itemListener?.onItemClick(position, it) }
            setOnLongClickListener {
                itemLongClickListener?.onItemLongClick(position, it)
                false
            }
        }
    }

    /**
     * 获取对应 position 下的数据
     * @param position
     */
    open fun getItemData(position: Int): T? = getItem(position)

    abstract fun getLayoutId(): Int

    abstract fun setVariable(data: T, position: Int, holder: BaseViewHolder<VB>)
}