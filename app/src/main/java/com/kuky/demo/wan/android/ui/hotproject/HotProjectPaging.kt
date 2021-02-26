package com.kuky.demo.wan.android.ui.hotproject

import androidx.databinding.ViewDataBinding
import androidx.paging.PagingSource
import androidx.paging.PagingState
import androidx.recyclerview.widget.DiffUtil
import com.kuky.demo.wan.android.R
import com.kuky.demo.wan.android.base.BasePagingDataAdapter
import com.kuky.demo.wan.android.base.BaseRecyclerAdapter
import com.kuky.demo.wan.android.base.BaseViewHolder
import com.kuky.demo.wan.android.base.renderHtml
import com.kuky.demo.wan.android.databinding.RecyclerHomeProjectBinding
import com.kuky.demo.wan.android.databinding.RecyclerProjectCategoryBinding
import com.kuky.demo.wan.android.entity.ProjectCategoryData
import com.kuky.demo.wan.android.entity.ProjectDetailData

/**
 * @author kuky.
 * @description
 */
class HotProjectPagingSource(
    private val repository: HotProjectRepository, private val pid: Int
) : PagingSource<Int, ProjectDetailData>() {

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, ProjectDetailData> {
        val page = params.key ?: 0

        return try {
            val projects = repository.loadProjects(page, pid) ?: mutableListOf()

            return LoadResult.Page(
                data = projects,
                prevKey = if (page == 0) null else page - 1,
                nextKey = if (projects.isEmpty()) null else page + 1
            )
        } catch (e: Exception) {
            LoadResult.Error(e)
        }
    }

    override fun getRefreshKey(state: PagingState<Int, ProjectDetailData>) = state.anchorPosition
}

class HomeProjectPagingAdapter :
    BasePagingDataAdapter<ProjectDetailData, RecyclerHomeProjectBinding>(DIFF_CALLBACK) {

    override fun getLayoutId(): Int = R.layout.recycler_home_project

    override fun setVariable(
        data: ProjectDetailData, position: Int,
        holder: BaseViewHolder<RecyclerHomeProjectBinding>
    ) {
        holder.binding.project = data
        holder.binding.title = data.title.renderHtml()
        holder.binding.desc = data.desc.renderHtml()
    }

    companion object {
        private val DIFF_CALLBACK = object : DiffUtil.ItemCallback<ProjectDetailData>() {
            override fun areItemsTheSame(oldItem: ProjectDetailData, newItem: ProjectDetailData): Boolean =
                oldItem.id == newItem.id

            override fun areContentsTheSame(oldItem: ProjectDetailData, newItem: ProjectDetailData): Boolean =
                oldItem == newItem
        }
    }
}

class ProjectCategoryAdapter(categories: MutableList<ProjectCategoryData>? = null) :
    BaseRecyclerAdapter<ProjectCategoryData>(categories) {

    // 通过 diffutil 更新数据
    fun setCategories(categories: MutableList<ProjectCategoryData>?) {
        val result = DiffUtil.calculateDiff(CategoryDiffCall(getAdapterData(), categories), true)
        result.dispatchUpdatesTo(this)
        mData = (mData ?: arrayListOf()).apply {
            clear()
            addAll(categories ?: arrayListOf())
        }
    }

    override fun getLayoutId(viewType: Int): Int = R.layout.recycler_project_category

    override fun setVariable(data: ProjectCategoryData, position: Int, holder: BaseViewHolder<ViewDataBinding>) {
        (holder.binding as RecyclerProjectCategoryBinding).let {
            it.categoryName = data.name.renderHtml()
            it.selected = mSelectionPosition == position
        }
    }
}

class CategoryDiffCall(
    private val oldList: MutableList<ProjectCategoryData>?,
    private val newList: MutableList<ProjectCategoryData>?
) : DiffUtil.Callback() {

    override fun areItemsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean =
        if (oldList.isNullOrEmpty() || newList.isNullOrEmpty()) false
        else oldList[oldItemPosition].id == newList[newItemPosition].id

    override fun getOldListSize(): Int = oldList?.size ?: 0

    override fun getNewListSize(): Int = newList?.size ?: 0

    override fun areContentsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean =
        if (oldList.isNullOrEmpty() || newList.isNullOrEmpty()) false
        else oldList[oldItemPosition].name == newList[newItemPosition].name
}