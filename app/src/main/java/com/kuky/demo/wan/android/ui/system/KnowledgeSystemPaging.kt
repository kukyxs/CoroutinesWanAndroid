package com.kuky.demo.wan.android.ui.system

import androidx.databinding.ViewDataBinding
import androidx.paging.PagingSource
import androidx.recyclerview.widget.DiffUtil
import com.kuky.demo.wan.android.R
import com.kuky.demo.wan.android.base.BaseRecyclerAdapter
import com.kuky.demo.wan.android.base.BaseViewHolder
import com.kuky.demo.wan.android.databinding.RecyclerKnowledgeSystemBinding
import com.kuky.demo.wan.android.entity.SystemCategory
import com.kuky.demo.wan.android.entity.SystemData
import com.kuky.demo.wan.android.entity.WxChapterListDatas

/**
 * @author Taonce.
 * @description
 */

class KnowledgeSystemPagingSource(
    private val repository: KnowledgeSystemRepository, private val cid: Int
) : PagingSource<Int, WxChapterListDatas>() {
    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, WxChapterListDatas> {
        val page = params.key ?: 0
        return try {
            val chapters = repository.loadArticle4System(page, cid) ?: mutableListOf()
            LoadResult.Page(
                data = chapters,
                prevKey = if (page == 0) null else page - 1,
                nextKey = if (chapters.isEmpty()) null else page + 1
            )
        } catch (e: Exception) {
            LoadResult.Error(e)
        }
    }
}

class KnowledgeSystemTypeAdapter(
    mData: MutableList<SystemData>? = null
) : BaseRecyclerAdapter<SystemData>(mData) {

    override fun getLayoutId(viewType: Int): Int = R.layout.recycler_knowledge_system

    override fun setVariable(
        data: SystemData, position: Int, holder: BaseViewHolder<ViewDataBinding>
    ) {
        (holder.binding as RecyclerKnowledgeSystemBinding).let {
            it.name = mData?.get(position)?.name
            it.selected = mSelectionPosition == position
        }
    }

    fun setNewData(newData: MutableList<SystemData>?) {
        val result = DiffUtil.calculateDiff(TypeDiffUtil(getAdapterData(), newData), true)
        result.dispatchUpdatesTo(this)
        if (mData == null) {
            mData = arrayListOf()
        }

        mData?.clear()
        mData?.addAll(newData ?: arrayListOf())
    }
}

class KnowledgeSystemSecTypeAdapter(
    mData: MutableList<SystemCategory>? = null
) : BaseRecyclerAdapter<SystemCategory>(mData) {

    override fun getLayoutId(viewType: Int): Int = R.layout.recycler_knowledge_system

    override fun setVariable(
        data: SystemCategory, position: Int, holder: BaseViewHolder<ViewDataBinding>
    ) {
        (holder.binding as RecyclerKnowledgeSystemBinding).let {
            it.name = mData?.get(position)?.name
            it.selected = mSelectionPosition == position
        }
    }

    fun setNewData(newData: MutableList<SystemCategory>?) {
        val result = DiffUtil.calculateDiff(SecTypeDiffUtil(getAdapterData(), newData), true)
        result.dispatchUpdatesTo(this)
        if (mData == null) {
            mData = arrayListOf()
        }

        mData?.clear()
        mData?.addAll(newData ?: arrayListOf())
    }
}

class TypeDiffUtil(
    private val oldData: MutableList<SystemData>?,
    private val newData: MutableList<SystemData>?
) : DiffUtil.Callback() {
    override fun areItemsTheSame(oldItemPosition: Int, newItemPosition: Int) =
        if (oldData.isNullOrEmpty() || newData.isNullOrEmpty()) false
        else oldData[oldItemPosition].id == newData[newItemPosition].id

    override fun getOldListSize() = oldData?.size ?: 0

    override fun getNewListSize() = newData?.size ?: 0

    override fun areContentsTheSame(oldItemPosition: Int, newItemPosition: Int) =
        if (oldData.isNullOrEmpty() || newData.isNullOrEmpty()) false
        else oldData[oldItemPosition].name == newData[newItemPosition].name
}

class SecTypeDiffUtil(
    private val oldData: MutableList<SystemCategory>?,
    private val newData: MutableList<SystemCategory>?
) : DiffUtil.Callback() {
    override fun areItemsTheSame(oldItemPosition: Int, newItemPosition: Int) =
        if (oldData.isNullOrEmpty() || newData.isNullOrEmpty()) false
        else oldData[oldItemPosition].id == newData[newItemPosition].id

    override fun getOldListSize() = oldData?.size ?: 0

    override fun getNewListSize() = newData?.size ?: 0

    override fun areContentsTheSame(oldItemPosition: Int, newItemPosition: Int) =
        if (oldData.isNullOrEmpty() || newData.isNullOrEmpty()) false
        else oldData[oldItemPosition].name == newData[newItemPosition].name
}