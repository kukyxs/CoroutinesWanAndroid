package com.kuky.demo.wan.android.ui.hotproject

import androidx.databinding.ViewDataBinding
import androidx.lifecycle.MutableLiveData
import androidx.paging.DataSource
import androidx.paging.PageKeyedDataSource
import androidx.paging.PagingSource
import androidx.recyclerview.widget.DiffUtil
import com.kuky.demo.wan.android.R
import com.kuky.demo.wan.android.base.*
import com.kuky.demo.wan.android.databinding.RecyclerHomeProjectBinding
import com.kuky.demo.wan.android.databinding.RecyclerProjectCategoryBinding
import com.kuky.demo.wan.android.entity.ProjectCategoryData
import com.kuky.demo.wan.android.entity.ProjectDetailData
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.cancel

/**
 * @author kuky.
 * @description
 */
class HotProjectPagingSource(private val repository: HotProjectRepository, private val pid: Int) :
    PagingSource<Int, ProjectDetailData>() {

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
}

class HomeProjectPagingAdapter : BasePagingDataAdapter<ProjectDetailData, RecyclerHomeProjectBinding>(DIFF_CALLBACK) {

    override fun getLayoutId(): Int = R.layout.recycler_home_project

    override fun setVariable(
        data: ProjectDetailData, position: Int,
        holder: BaseViewHolder<RecyclerHomeProjectBinding>
    ) {
        holder.binding.project = data
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
            it.category = data
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

//region adapter by paging2, has migrate to paging3
@Deprecated("migrate to paging3", level = DeprecationLevel.WARNING)
class HotProjectDataSource(
    private val repository: HotProjectRepository,
    private val pid: Int
) : PageKeyedDataSource<Int, ProjectDetailData>(), CoroutineScope by IOScope() {

    val initState = MutableLiveData<NetworkState>()

    override fun loadInitial(params: LoadInitialParams<Int>, callback: LoadInitialCallback<Int, ProjectDetailData>) {
        safeLaunch {
            block = {
                initState.postValue(NetworkState.LOADING)
                repository.loadProjects(0, pid)?.let {
                    callback.onResult(it, null, 1)
                    initState.postValue(NetworkState.LOADED)
                }
            }
            onError = {
                initState.postValue(NetworkState.error(it.message, ERROR_CODE_INIT))
            }
        }
    }

    override fun loadAfter(params: LoadParams<Int>, callback: LoadCallback<Int, ProjectDetailData>) {
        safeLaunch {
            block = {
                repository.loadProjects(params.key, pid)?.let {
                    callback.onResult(it, params.key + 1)
                }
            }
            onError = {
                initState.postValue(NetworkState.error(it.message, ERROR_CODE_MORE))
            }
        }
    }

    override fun loadBefore(params: LoadParams<Int>, callback: LoadCallback<Int, ProjectDetailData>) {}

    override fun invalidate() {
        super.invalidate()
        cancel()
    }
}

@Deprecated("migrate to paging3", level = DeprecationLevel.WARNING)
class HotProjectDataSourceFactory(
    private val repository: HotProjectRepository,
    private val pid: Int
) : DataSource.Factory<Int, ProjectDetailData>() {
    val sourceLiveData = MutableLiveData<HotProjectDataSource>()

    override fun create(): DataSource<Int, ProjectDetailData> = HotProjectDataSource(repository, pid).apply {
        sourceLiveData.postValue(this)
    }
}

@Deprecated("migrate to paging3", level = DeprecationLevel.WARNING)
class HomeProjectAdapter : BasePagedListAdapter<ProjectDetailData, RecyclerHomeProjectBinding>(DIFF_CALLBACK) {

    override fun getLayoutId(viewType: Int): Int = R.layout.recycler_home_project

    override fun setVariable(
        data: ProjectDetailData,
        position: Int, holder: BaseViewHolder<RecyclerHomeProjectBinding>
    ) {
        holder.binding.project = data
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
//endregion