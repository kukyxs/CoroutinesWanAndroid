package com.kuky.demo.wan.android.ui.home

import androidx.paging.DataSource
import androidx.paging.PageKeyedDataSource
import androidx.recyclerview.widget.DiffUtil
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.kuky.demo.wan.android.R
import com.kuky.demo.wan.android.WanApplication
import com.kuky.demo.wan.android.base.BasePagedListAdapter
import com.kuky.demo.wan.android.base.BaseViewHolder
import com.kuky.demo.wan.android.base.safeLaunch
import com.kuky.demo.wan.android.data.PreferencesHelper
import com.kuky.demo.wan.android.databinding.RecyclerHomeArticleBinding
import com.kuky.demo.wan.android.entity.ArticleDetail
import com.kuky.demo.wan.android.network.RetrofitManager
import kotlinx.coroutines.*

/**
 * @author kuky.
 * @description
 */
class HomeArticleRepository {
    fun getHomeArticleCache(): List<ArticleDetail>? {
        return Gson().fromJson(
            PreferencesHelper.fetchHomeArticleCache(WanApplication.instance),
            object : TypeToken<List<ArticleDetail>>() {}.type
        )
    }

    suspend fun loadPageData(page: Int): List<ArticleDetail>? = withContext(Dispatchers.IO) {
        val result = RetrofitManager.apiService.homeArticles(page).data.datas
        if (page == 0) {
            PreferencesHelper.saveHomeArticleCache(WanApplication.instance, Gson().toJson(result))
        }
        result
    }

    // 加载首页置顶文章
    suspend fun loadTops(): List<ArticleDetail>? = withContext(Dispatchers.IO) {
        RetrofitManager.apiService.topArticle(PreferencesHelper.fetchCookie(WanApplication.instance)).data
    }
}

/**
 * 网络数据加载
 */
class HomeArticleDataSource(private val repository: HomeArticleRepository) :
    PageKeyedDataSource<Int, ArticleDetail>(), CoroutineScope by MainScope() {

    override fun loadInitial(params: LoadInitialParams<Int>, callback: LoadInitialCallback<Int, ArticleDetail>) {
        safeLaunch {
            val result = ArrayList<ArticleDetail>()
            val tops = repository.loadTops()
            val data = repository.loadPageData(0)

            result.addAll(tops ?: arrayListOf())
            result.addAll(data ?: arrayListOf())

            callback.onResult(result, null, 1)
        }
    }

    override fun loadAfter(params: LoadParams<Int>, callback: LoadCallback<Int, ArticleDetail>) {
        safeLaunch {
            val data = repository.loadPageData(params.key)
            data?.let {
                callback.onResult(it, params.key + 1)
            }
        }
    }

    override fun loadBefore(params: LoadParams<Int>, callback: LoadCallback<Int, ArticleDetail>) {
        safeLaunch {
            val data = repository.loadPageData(params.key)
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

class HomeArticleDataSourceFactory(private val repository: HomeArticleRepository) :
    DataSource.Factory<Int, ArticleDetail>() {

    override fun create(): DataSource<Int, ArticleDetail> = HomeArticleDataSource(repository)
}

/**
 * 本地数据加载
 */
class HomeArticleCacheDataSource(private val repository: HomeArticleRepository) :
    PageKeyedDataSource<Int, ArticleDetail>() {

    override fun loadInitial(params: LoadInitialParams<Int>, callback: LoadInitialCallback<Int, ArticleDetail>) =
        callback.onResult(repository.getHomeArticleCache() ?: arrayListOf(), null, null)

    override fun loadAfter(params: LoadParams<Int>, callback: LoadCallback<Int, ArticleDetail>) {

    }

    override fun loadBefore(params: LoadParams<Int>, callback: LoadCallback<Int, ArticleDetail>) {

    }
}

class HomeArticleCacheDataSourceFactory(private val repository: HomeArticleRepository) :
    DataSource.Factory<Int, ArticleDetail>() {
    override fun create(): DataSource<Int, ArticleDetail> = HomeArticleCacheDataSource(repository)
}

/**
 * 方便绑定 recyclerView 的点击事件，可继承 [BasePagedListAdapter] 实现
 */
class HomeArticleAdapter : BasePagedListAdapter<ArticleDetail, RecyclerHomeArticleBinding>(DIFF_CALLBACK) {

    override fun getLayoutId(viewType: Int): Int = R.layout.recycler_home_article

    @Suppress("DEPRECATION")
    override fun setVariable(data: ArticleDetail, position: Int, holder: BaseViewHolder<RecyclerHomeArticleBinding>) {
        holder.binding.detail = data
        holder.binding.description = data.title
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