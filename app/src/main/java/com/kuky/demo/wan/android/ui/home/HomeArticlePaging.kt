package com.kuky.demo.wan.android.ui.home

import androidx.lifecycle.MutableLiveData
import androidx.paging.DataSource
import androidx.paging.PageKeyedDataSource
import androidx.recyclerview.widget.DiffUtil
import com.kuky.demo.wan.android.R
import com.kuky.demo.wan.android.WanApplication
import com.kuky.demo.wan.android.base.*
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

    suspend fun loadPageData(page: Int): List<ArticleDetail>? = withContext(Dispatchers.IO) {
        RetrofitManager.apiService.homeArticles(page).data.datas
    }

    // 加载首页置顶文章
    suspend fun loadTops(): List<ArticleDetail>? = withContext(Dispatchers.IO) {
        RetrofitManager.apiService.topArticle(PreferencesHelper.fetchCookie(WanApplication.instance)).data
    }
}

/**
 * 网络数据加载
 */
class HomeArticleDataSource(
    private val repository: HomeArticleRepository
) : PageKeyedDataSource<Int, ArticleDetail>(), CoroutineScope by MainScope() {

    val initState = MutableLiveData<NetworkState>()

    override fun loadInitial(params: LoadInitialParams<Int>, callback: LoadInitialCallback<Int, ArticleDetail>) {
        safeLaunch({
            initState.postValue(NetworkState.LOADING)
            val tops = repository.loadTops()
            val data = repository.loadPageData(0)

            callback.onResult(arrayListOf<ArticleDetail>().apply {
                addAll(tops ?: arrayListOf())
                addAll(data ?: arrayListOf())
            }, null, 1)
            initState.postValue(NetworkState.LOADED)
        }, {
            initState.postValue(NetworkState.error(it.message, ERROR_CODE_INIT))
        })
    }

    override fun loadAfter(params: LoadParams<Int>, callback: LoadCallback<Int, ArticleDetail>) {
        safeLaunch({
            repository.loadPageData(params.key)?.let {
                callback.onResult(it, params.key + 1)
            }
        }, { initState.postValue(NetworkState.error(it.message, ERROR_CODE_MORE)) })
    }

    override fun loadBefore(params: LoadParams<Int>, callback: LoadCallback<Int, ArticleDetail>) {}

    override fun invalidate() {
        super.invalidate()
        cancel()
    }
}

class HomeArticleDataSourceFactory(
    private val repository: HomeArticleRepository
) : DataSource.Factory<Int, ArticleDetail>() {
    val sourceLiveData = MutableLiveData<HomeArticleDataSource>()

    override fun create(): DataSource<Int, ArticleDetail> = HomeArticleDataSource(repository).apply {
        sourceLiveData.postValue(this)
    }
}

/**
 * 方便绑定 recyclerView 的点击事件，可继承 [BasePagedListAdapter] 实现
 */
class HomeArticleAdapter : BasePagedListAdapter<ArticleDetail, RecyclerHomeArticleBinding>(DIFF_CALLBACK) {

    override fun getLayoutId(viewType: Int): Int = R.layout.recycler_home_article

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