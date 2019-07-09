package com.kuky.demo.wan.android.ui.hotproject

import androidx.paging.DataSource
import androidx.paging.PageKeyedDataSource
import androidx.recyclerview.widget.DiffUtil
import com.kuky.demo.wan.android.R
import com.kuky.demo.wan.android.base.BasePagedListAdapter
import com.kuky.demo.wan.android.base.BaseViewHolder
import com.kuky.demo.wan.android.databinding.RecyclerHomeProjectBinding
import com.kuky.demo.wan.android.entity.ProjectDetailData
import com.kuky.demo.wan.android.network.RetrofitManager
import kotlinx.coroutines.*

/**
 * @author kuky.
 * @description
 */

class HotProjectRepository {
    suspend fun loadProjects(page: Int): List<ProjectDetailData>? = withContext(Dispatchers.IO) {
        RetrofitManager.apiService.homeProject(page).data.datas
    }
}

class HotProjectDataSource(private val repository: HotProjectRepository) :
    PageKeyedDataSource<Int, ProjectDetailData>(), CoroutineScope by MainScope() {

    override fun loadInitial(params: LoadInitialParams<Int>, callback: LoadInitialCallback<Int, ProjectDetailData>) {
        launch {
            val data = repository.loadProjects(0)
            data?.let {
                callback.onResult(it, null, 1)
            }
        }
    }

    override fun loadAfter(params: LoadParams<Int>, callback: LoadCallback<Int, ProjectDetailData>) {
        launch {
            val data = repository.loadProjects(params.key)
            data?.let {
                callback.onResult(it, params.key + 1)
            }
        }
    }

    override fun loadBefore(params: LoadParams<Int>, callback: LoadCallback<Int, ProjectDetailData>) {
        launch {
            val data = repository.loadProjects(params.key)
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

class HotProjectDataSourceFactory(private val repository: HotProjectRepository) :
    DataSource.Factory<Int, ProjectDetailData>() {

    override fun create(): DataSource<Int, ProjectDetailData> = HotProjectDataSource(repository)
}


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