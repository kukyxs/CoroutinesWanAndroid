package com.kuky.demo.wan.android.base

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding
import androidx.recyclerview.widget.RecyclerView


/**
 * @author Taonce.
 * @description recycler adapter 基类
 */
abstract class BaseRecyclerAdapter<VB : ViewDataBinding, T>(var mData: MutableList<T>?) :
    RecyclerView.Adapter<BaseViewHolder<VB>>() {

    var itemListener: OnItemClickListener? = null

    fun setOnItemListener(listener: OnItemClickListener?) {
        this.itemListener = listener
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BaseViewHolder<VB> {
        return BaseViewHolder(
            DataBindingUtil.inflate(LayoutInflater.from(parent.context), getLayoutId(viewType), parent, false)
        )
    }

    override fun onBindViewHolder(holder: BaseViewHolder<VB>, position: Int) {
        val data = getItemData(position) ?: return
        setVariable(data, position, holder)
        holder.binding.executePendingBindings()
        holder.binding.root.setOnClickListener { v -> itemListener?.onItemClick(position, v) }
    }

    override fun getItemCount(): Int = mData?.size ?: 0

    fun getAdapterData(): MutableList<T>? = mData

    /**
     * 获取对应 position 下的数据
     * @param position
     */
    fun getItemData(position: Int): T? = mData!![position]

    /**
     * 获取对应的 item 布局
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


