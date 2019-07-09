package com.kuky.demo.wan.android.ui.home

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.paging.DataSource
import androidx.paging.PageKeyedDataSource
import androidx.paging.PagedListAdapter
import androidx.recyclerview.widget.DiffUtil
import com.kuky.demo.wan.android.R
import com.kuky.demo.wan.android.base.BaseViewHolder
import com.kuky.demo.wan.android.databinding.RecyclerHomeArticleBinding
import com.kuky.demo.wan.android.entity.ArticleDetail
import com.kuky.demo.wan.android.network.RetrofitManager
import kotlinx.coroutines.*

/**
 * @author kuky.
 * @description
 */
class HomeArticleRepository {
    suspend fun loadPageData(page: Int): List<ArticleDetail>? = withContext(Dispatchers.IO) {
        RetrofitManager.apiService.homeArticles(page).data.datas
    }
}

class HomeArticleDataSource(private val repository: HomeArticleRepository) :
    PageKeyedDataSource<Int, ArticleDetail>(), CoroutineScope by MainScope() {

    override fun loadInitial(params: LoadInitialParams<Int>, callback: LoadInitialCallback<Int, ArticleDetail>) {
        launch {
            val data = repository.loadPageData(0)
            data?.let {
                callback.onResult(data, null, 1)
            }
        }
    }

    override fun loadAfter(params: LoadParams<Int>, callback: LoadCallback<Int, ArticleDetail>) {
        launch {
            val data = repository.loadPageData(params.key)
            data?.let {
                callback.onResult(data, params.key + 1)
            }
        }
    }

    override fun loadBefore(params: LoadParams<Int>, callback: LoadCallback<Int, ArticleDetail>) {
        launch {
            val data = repository.loadPageData(params.key)
            data?.let {
                callback.onResult(data, params.key - 1)
            }
        }
    }
}

class HomeArticleDataSourceFactory(private val repository: HomeArticleRepository) :
    DataSource.Factory<Int, ArticleDetail>() {

    override fun create(): DataSource<Int, ArticleDetail> = HomeArticleDataSource(repository)
}

class HomeArticleAdapter : PagedListAdapter<ArticleDetail, BaseViewHolder<RecyclerHomeArticleBinding>>(DIFF_CALLBACK) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BaseViewHolder<RecyclerHomeArticleBinding> {
        return BaseViewHolder(
            DataBindingUtil.inflate(LayoutInflater.from(parent.context), R.layout.recycler_home_article, parent, false)
        )
    }

    override fun onBindViewHolder(holder: BaseViewHolder<RecyclerHomeArticleBinding>, position: Int) {
        val article = getItem(position) ?: return
        holder.binding.detail = article
        holder.binding.executePendingBindings()
    }

    companion object {
        private val DIFF_CALLBACK = object : DiffUtil.ItemCallback<ArticleDetail>() {
            override fun areItemsTheSame(oldItem: ArticleDetail, newItem: ArticleDetail): Boolean =
                oldItem.id == newItem.id

            override fun areContentsTheSame(oldItem: ArticleDetail, newItem: ArticleDetail): Boolean =
                oldItem == newItem
        }
    }
}