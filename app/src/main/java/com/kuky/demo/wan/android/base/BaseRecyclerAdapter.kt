package com.kuky.demo.wan.android.base

import android.content.Context
import android.util.SparseArray
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.StaggeredGridLayoutManager

/**
 * @author kuky.
 * @description RecyclerView 通用适配器
 *
 * @see BaseRecyclerAdapter.addHeaderView 支持添加多个头部
 * @see BaseRecyclerAdapter.addFooterView 支持添加多个尾部
 * @see BaseRecyclerAdapter.setOnItemClickListener 支持点击事件
 * @see BaseRecyclerAdapter.setOnItemLongClickListener 支持长按事件
 * @see BaseRecyclerAdapter.getConvertType 支持多布局
 * @see BaseRecyclerAdapter.mSelectedPosition 处理选中 item 与未选中不同状态
 */
abstract class BaseRecyclerAdapter<T : Any>(context: Context, dataList: ArrayList<T>? = null) :
    RecyclerView.Adapter<BaseRecyclerAdapter.BaseRecyclerHolder>() {

    private val mHeaderViews: SparseArray<View> = SparseArray()
    private val mFooterViews: SparseArray<View> = SparseArray()
    protected val mContext = context
    protected var mDataList = dataList
    protected var mSelectedPosition = -1
    private val mInflater = LayoutInflater.from(mContext)
    private var mOnItemClickListener: ((position: Int, view: View) -> Unit)? = null
    private var mOnItemLongClickListener: ((position: Int, view: View) -> Unit)? = null

    fun setOnItemClickListener(listener: ((position: Int, view: View) -> Unit)?) {
        this.mOnItemClickListener = listener
    }

    fun setOnItemLongClickListener(listener: ((position: Int, view: View) -> Unit)?) {
        this.mOnItemLongClickListener = listener
    }

    fun updateAdapterData(dataList: ArrayList<T>?) {
        this.mDataList = dataList
        notifyDataSetChanged()
    }

    fun updateSelectItem(position: Int) {
        this.mSelectedPosition = position
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BaseRecyclerHolder =
        if (haveHeader() && mHeaderViews.get(viewType) != null) BaseRecyclerHolder(mHeaderViews.get(viewType))
        else if (haveFooter() && mFooterViews.get(viewType) != null) BaseRecyclerHolder(mFooterViews.get(viewType))
        else BaseRecyclerHolder(mInflater.inflate(getAdapterLayoutId(viewType), parent, false))

    abstract fun getAdapterLayoutId(viewType: Int): Int

    override fun getItemCount(): Int = getHeaderSize() + getDataSize() + getFooterSize()

    override fun onBindViewHolder(holder: BaseRecyclerHolder, position: Int) {
        if (!isHeader(position) && !isFooter(position)) {
            val pos = position - getHeaderSize()

            convertView(holder.itemView, mDataList!![pos], pos)

            holder.itemView.setOnClickListener { v ->
                mOnItemClickListener?.invoke(pos, v)
            }

            holder.itemView.setOnLongClickListener { v ->
                mOnItemLongClickListener?.invoke(pos, v)
                false
            }
        }
    }

    /** position is the data position in list, not item position */
    abstract fun convertView(itemView: View, t: T, position: Int)

    /**
     * return the key of view in SparseArray, save it and you will use it in
     * @see removeHeaderView,
     * or you can call this by removeHeaderView(view.tag as Int)
     */
    fun addHeaderView(header: View): Int {
        val headKey = HEADER + getHeaderSize()
        mHeaderViews.put(headKey, header)
        header.tag = headKey
        notifyItemInserted(getHeaderSize())
        return headKey
    }

    /**
     * tag is returned by addHeaderView or (yourHeaderView.tag as Int)
     */
    fun removeHeaderView(tag: Int) {
        mHeaderViews.remove(tag)
        notifyDataSetChanged()
    }

    /**
     * return the key of view in SparseArray, save it and you will use it in
     * @see removeFooterView,
     * or you can call this by removeFooterView(view.tag as Int)
     */
    fun addFooterView(footer: View): Int {
        val footKey = FOOTER + getFooterSize()
        mFooterViews.put(footKey, footer)
        footer.tag = footKey
        var pos = if (mDataList == null) getFooterSize() else getFooterSize() + getDataSize() - 1
        if (haveHeader()) pos += getHeaderSize()
        notifyItemInserted(pos)
        return footKey
    }

    /**
     * tag is returned by addFooterView or (yourFooterView.tag as Int)
     */
    fun removeFooterView(tag: Int) {
        mFooterViews.remove(tag)
        notifyDataSetChanged()
    }

    override fun getItemViewType(position: Int): Int =
        when {
            isHeader(position) -> mHeaderViews.keyAt(position)
            isFooter(position) -> mFooterViews.keyAt(position - getDataSize() - getHeaderSize())
            else -> getConvertType(position)
        }

    protected open fun getConvertType(position: Int): Int = 0

    private fun getHeaderSize(): Int = mHeaderViews.size()

    private fun getDataSize(): Int = mDataList?.size ?: 0

    private fun getFooterSize(): Int = mFooterViews.size()

    private fun haveHeader(): Boolean = mHeaderViews.size() > 0

    private fun haveFooter(): Boolean = mFooterViews.size() > 0

    private fun isHeader(position: Int): Boolean = haveHeader() && position < getHeaderSize()

    private fun isFooter(position: Int): Boolean = haveFooter() && position >= getHeaderSize() + getDataSize()

    /** return the position of item in recyclerView include the headerViews and footerViews */
    private fun getRealPosition(viewHolder: RecyclerView.ViewHolder): Int = viewHolder.layoutPosition

    fun getAdapterData(): ArrayList<T>? = mDataList

    fun addData(data: T) {
        if (mDataList != null) {
            this.mDataList!!.add(data)
            notifyDataSetChanged()
        } else {
            throw IllegalStateException("data list has not been initial")
        }
    }

    fun addDataAtPosition(position: Int, data: T) {
        if (mDataList != null) {
            this.mDataList!!.add(position, data)
            notifyDataSetChanged()
        } else {
            throw IllegalStateException("data list has not been initial")
        }
    }

    fun addDataList(dataList: ArrayList<T>) {
        if (mDataList != null) {
            this.mDataList!!.addAll(dataList)
            notifyDataSetChanged()
        } else {
            throw IllegalStateException("data list has not been initial")
        }
    }

    fun removeDataAtPosition(position: Int) {
        if (mDataList != null) {
            this.mDataList!!.removeAt(position)
            notifyDataSetChanged()
        } else {
            throw IllegalStateException("data list has not been initial")
        }
    }

    fun removeData(data: T) {
        if (mDataList != null && data in mDataList!!) {
            mDataList!!.remove(data)
            notifyDataSetChanged()
        } else {
            throw IllegalStateException("data not in data list and check it before remove")
        }
    }

    fun clearData() {
        if (mDataList != null) {
            mDataList!!.clear()
            notifyDataSetChanged()
        } else {
            throw IllegalStateException("data list has not been initial")
        }
    }

    override fun onAttachedToRecyclerView(recyclerView: RecyclerView) {
        super.onAttachedToRecyclerView(recyclerView)
        val lm = recyclerView.layoutManager
        if (lm is GridLayoutManager)
            lm.spanSizeLookup = object : GridLayoutManager.SpanSizeLookup() {
                override fun getSpanSize(position: Int): Int =
                    if (isHeader(position) || isFooter(position)) lm.spanCount
                    else 1
            }
    }

    override fun onViewAttachedToWindow(holder: BaseRecyclerHolder) {
        super.onViewAttachedToWindow(holder)
        val lp = holder.itemView.layoutParams
        if (lp is StaggeredGridLayoutManager.LayoutParams)
            lp.isFullSpan = isHeader(getRealPosition(holder)) || isFooter(getRealPosition(holder))
    }

    class BaseRecyclerHolder(itemView: View) : RecyclerView.ViewHolder(itemView)

    companion object {
        private const val HEADER = 0x00100000
        private const val FOOTER = 0x00200000
    }
}