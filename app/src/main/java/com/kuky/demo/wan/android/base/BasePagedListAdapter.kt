package com.kuky.demo.wan.android.base

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding
import androidx.paging.PagedListAdapter
import androidx.recyclerview.widget.DiffUtil

/**
 * @author kuky.
 * @description paging adapter 基类
 */
abstract class BasePagedListAdapter<T, VB : ViewDataBinding>(val callback: DiffUtil.ItemCallback<T>) :
    PagedListAdapter<T, BaseViewHolder<VB>>(callback) {

    private var itemListener: OnItemClickListener? = null
    private var itemLongListener: OnItemLongClickListener? = null

    /**
     * 点击监听
     * @param listener
     */
    fun setOnItemListener(listener: OnItemClickListener?) {
        this.itemListener = listener
    }

    fun setOnItemLongListener(listener: OnItemLongClickListener?) {
        this.itemLongListener = listener
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BaseViewHolder<VB> {
        return BaseViewHolder(
            DataBindingUtil.inflate(LayoutInflater.from(parent.context), getLayoutId(viewType), parent, false)
        )
    }

    override fun onBindViewHolder(holder: BaseViewHolder<VB>, position: Int) {
        val data = getItem(position) ?: return
        setVariable(data, position, holder)
        holder.binding.executePendingBindings()
        holder.binding.root.setOnClickListener { v -> itemListener?.onItemClick(position, v) }
        holder.binding.root.setOnLongClickListener { v ->
            return@setOnLongClickListener itemLongListener?.onItemLongClick(
                position, v
            ) ?: false
        }
    }

    /**
     * 获取对应 position 下的数据
     * @param position
     */
    fun getItemData(position: Int): T? = getItem(position)

    /**
     * 根据 viewType 返回不同布局
     * @param viewType, 通过重写 getItemViewType 支持多布局
     */
    abstract fun getLayoutId(viewType: Int): Int

    /**
     * 与 dataBinding 互相绑定的数据操作
     * @param data 列表中当前 position 的数据
     * @param position 数据的位置
     * @param holder
     */
    abstract fun setVariable(data: T, position: Int, holder: BaseViewHolder<VB>)
}
