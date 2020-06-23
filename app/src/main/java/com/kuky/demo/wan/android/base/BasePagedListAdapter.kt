package com.kuky.demo.wan.android.base

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding
import androidx.paging.AsyncPagedListDiffer
import androidx.paging.PagedList
import androidx.paging.PagedListAdapter
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.DiffUtil

/**
 * @author kuky.
 * @description paging adapter 基类, 如果需要多布局, 请使用 MergeAdapter
 */
abstract class BasePagingDataAdapter<T : Any, VB : ViewDataBinding>(val callback: DiffUtil.ItemCallback<T>) :
    PagingDataAdapter<T, BaseViewHolder<VB>>(callback) {

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

//region deprecated paging2 adapter
@Deprecated(
    message = "has migrated to paging3",
    replaceWith = ReplaceWith(
        expression = "use BasePagingDataAdapter replaced",
        imports = ["BasePagingDataAdapter"]
    ), level = DeprecationLevel.WARNING
)
abstract class BasePagedListAdapter<T : Any, VB : ViewDataBinding>(val callback: DiffUtil.ItemCallback<T>) :
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
        val data = getItemData(position) ?: return
        setVariable(data, position, holder)
        holder.binding.executePendingBindings()
        holder.binding.root.setOnClickListener { v -> itemListener?.onItemClick(position, v) }
        holder.binding.root.setOnLongClickListener { v ->
            itemLongListener?.onItemLongClick(position, v)
            false
        }
    }

    /**
     * 获取对应 position 下的数据
     * @param position
     */
    open fun getItemData(position: Int): T? = getItem(position)

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

/**
 * 解决刷新列表闪动的问题，参考自
 * `https://stackoverflow.com/questions/48438944/how-to-stop-blinking-on-recycler-view-with-architecture-components-paging-librar/54442392#54442392`
 * 部分 recyclerView 存在列表网上推的动画，消除可通过设置 `recyclerView.itemAnimator = null` 实现
 * 部分 recyclerView 不设置 `itemAnimator = null` 刷新时也不会闪动
 */
@Deprecated(
    message = "has migrated to paging3",
    replaceWith = ReplaceWith(
        expression = "use BasePagingDataAdapter replaced",
        imports = ["BasePagingDataAdapter"]
    ), level = DeprecationLevel.WARNING
)
@Suppress("LeakingThis")
abstract class BaseNoBlinkingPagedListAdapter<T : Any, VB : ViewDataBinding>(cb: DiffUtil.ItemCallback<T>) :
    BasePagedListAdapter<T, VB>(cb) {

    private var mDiffer: AsyncPagedListDiffer<T>? = null

    init {
        mDiffer = AsyncPagedListDiffer(this, callback)
        setHasStableIds(true)
    }

    override fun getItemData(position: Int): T? = mDiffer?.getItem(position)

    override fun getItemCount(): Int = mDiffer?.itemCount ?: 0

    override fun getItemId(position: Int): Long = generateItemId(mDiffer, position)

    override fun submitList(pagedList: PagedList<T>?) {
        pagedList?.addWeakCallback(pagedList.snapshot(), object : BasePagedListCallback() {
            override fun onInserted(position: Int, count: Int) {
                mDiffer?.submitList(pagedList)
            }
        })
    }

    /**
     * 生成 id
     */
    abstract fun generateItemId(differ: AsyncPagedListDiffer<T>?, position: Int): Long
}

@Deprecated(message = "has migrate to paging3")
abstract class BasePagedListCallback : PagedList.Callback() {
    override fun onChanged(position: Int, count: Int) {

    }

    override fun onInserted(position: Int, count: Int) {

    }

    override fun onRemoved(position: Int, count: Int) {

    }
}
//endregion