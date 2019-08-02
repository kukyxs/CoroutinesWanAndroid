package com.kuky.demo.wan.android.ui.hotproject

import androidx.databinding.ViewDataBinding
import androidx.paging.DataSource
import androidx.paging.PageKeyedDataSource
import androidx.recyclerview.widget.DiffUtil
import com.kuky.demo.wan.android.R
import com.kuky.demo.wan.android.WanApplication
import com.kuky.demo.wan.android.base.BasePagedListAdapter
import com.kuky.demo.wan.android.base.BaseRecyclerAdapter
import com.kuky.demo.wan.android.base.BaseViewHolder
import com.kuky.demo.wan.android.base.safeLaunch
import com.kuky.demo.wan.android.data.PreferencesHelper
import com.kuky.demo.wan.android.databinding.RecyclerHomeProjectBinding
import com.kuky.demo.wan.android.databinding.RecyclerProjectCategoryBinding
import com.kuky.demo.wan.android.entity.ProjectCategoryData
import com.kuky.demo.wan.android.entity.ProjectDetailData
import com.kuky.demo.wan.android.network.RetrofitManager
import kotlinx.coroutines.*

/**
 * @author kuky.
 * @description
 */

class HotProjectRepository {
    // 加载分类
    suspend fun loadProjectCategories() = withContext(Dispatchers.IO) {
        RetrofitManager.apiService.projectCategory().data
    }

    // 加载分类下的项目列表
    suspend fun loadProjects(page: Int, pid: Int): List<ProjectDetailData>? = withContext(Dispatchers.IO) {
        RetrofitManager.apiService.projectList(page, pid, PreferencesHelper.fetchCookie(WanApplication.instance)).data.datas
    }
}

/**
 * 网络数据来源
 */
class HotProjectDataSource(private val repository: HotProjectRepository, private val pid: Int) :
    PageKeyedDataSource<Int, ProjectDetailData>(), CoroutineScope by MainScope() {

    override fun loadInitial(params: LoadInitialParams<Int>, callback: LoadInitialCallback<Int, ProjectDetailData>) {
        safeLaunch {
            val data = repository.loadProjects(0, pid)
            data?.let {
                callback.onResult(it, null, 1)
            }
        }
    }

    override fun loadAfter(params: LoadParams<Int>, callback: LoadCallback<Int, ProjectDetailData>) {
        safeLaunch {
            val data = repository.loadProjects(params.key, pid)
            data?.let {
                callback.onResult(it, params.key + 1)
            }
        }
    }

    override fun loadBefore(params: LoadParams<Int>, callback: LoadCallback<Int, ProjectDetailData>) {
        safeLaunch {
            val data = repository.loadProjects(params.key, pid)
            data?.let {
                callback.onResult(it, params.key - 1)
            }
        }
    }

    override fun invalidate() {
        super.invalidate()
        cancel()
    }
}

class HotProjectDataSourceFactory(private val repository: HotProjectRepository, private val pid: Int) :
    DataSource.Factory<Int, ProjectDetailData>() {

    override fun create(): DataSource<Int, ProjectDetailData> = HotProjectDataSource(repository, pid)
}

/**
 * Adapter
 */
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