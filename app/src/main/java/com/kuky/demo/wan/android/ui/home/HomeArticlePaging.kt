package com.kuky.demo.wan.android.ui.home

import androidx.databinding.ViewDataBinding
import androidx.lifecycle.MutableLiveData
import androidx.paging.DataSource
import androidx.paging.PageKeyedDataSource
import androidx.recyclerview.widget.DiffUtil
import com.kuky.demo.wan.android.R
import com.kuky.demo.wan.android.WanApplication
import com.kuky.demo.wan.android.base.*
import com.kuky.demo.wan.android.data.PreferencesHelper
import com.kuky.demo.wan.android.data.WanDatabaseUtils
import com.kuky.demo.wan.android.data.db.HomeArticleDetail
import com.kuky.demo.wan.android.databinding.RecyclerHomeArticleBinding
import com.kuky.demo.wan.android.network.RetrofitManager
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.cancel
import kotlinx.coroutines.withContext

/**
 * @author kuky.
 * @description
 */
class HomeArticleRepository {

    suspend fun loadPageData(page: Int): List<HomeArticleDetail>? = withContext(Dispatchers.IO) {
        RetrofitManager.apiService.homeArticles(page).data.datas
    }

    // 加载首页置顶文章
    suspend fun loadTops(): List<HomeArticleDetail>? = withContext(Dispatchers.IO) {
        RetrofitManager.apiService.topArticle(PreferencesHelper.fetchCookie(WanApplication.instance)).data
    }
}

/**
 * 网络数据加载
 */
class HomeArticleDataSource(
    private val repository: HomeArticleRepository
) : PageKeyedDataSource<Int, HomeArticleDetail>(), CoroutineScope by IOScope() {

    val initState = MutableLiveData<NetworkState>()

    override fun loadInitial(params: LoadInitialParams<Int>, callback: LoadInitialCallback<Int, HomeArticleDetail>) {
        safeLaunch({
            initState.postValue(NetworkState.LOADING)
            val tops = repository.loadTops()
            val data = repository.loadPageData(0)

            withContext(Dispatchers.IO) {
                WanDatabaseUtils.homeArticleCacheDao.clearHomeCache()
                tops?.let { WanDatabaseUtils.homeArticleCacheDao.cacheHomeArticles(it) }
                data?.let { WanDatabaseUtils.homeArticleCacheDao.cacheHomeArticles(it) }
            }

            callback.onResult(arrayListOf<HomeArticleDetail>().apply {
                addAll(tops ?: arrayListOf())
                addAll(data ?: arrayListOf())
            }, null, 1)
            initState.postValue(NetworkState.LOADED)
        }, {
            initState.postValue(NetworkState.error(it.message, ERROR_CODE_INIT))
        })
    }

    override fun loadAfter(params: LoadParams<Int>, callback: LoadCallback<Int, HomeArticleDetail>) {
        safeLaunch({
            repository.loadPageData(params.key)?.let {
                withContext(Dispatchers.IO) {
                    WanDatabaseUtils.homeArticleCacheDao.cacheHomeArticles(it)
                }

                callback.onResult(it, params.key + 1)
            }
        }, { initState.postValue(NetworkState.error(it.message, ERROR_CODE_MORE)) })
    }

    override fun loadBefore(params: LoadParams<Int>, callback: LoadCallback<Int, HomeArticleDetail>) {}

    override fun invalidate() {
        super.invalidate()
        cancel()
    }
}

class HomeArticleDataSourceFactory(
    private val repository: HomeArticleRepository
) : DataSource.Factory<Int, HomeArticleDetail>() {
    val sourceLiveData = MutableLiveData<HomeArticleDataSource>()

    override fun create(): DataSource<Int, HomeArticleDetail> = HomeArticleDataSource(repository).apply {
        sourceLiveData.postValue(this)
    }
}

/**
 * 方便绑定 recyclerView 的点击事件，可继承 [BasePagedListAdapter] 实现
 */
class HomeArticleAdapter : BasePagedListAdapter<HomeArticleDetail, RecyclerHomeArticleBinding>(DIFF_CALLBACK) {

    override fun getLayoutId(viewType: Int): Int = R.layout.recycler_home_article

    override fun setVariable(data: HomeArticleDetail, position: Int, holder: BaseViewHolder<RecyclerHomeArticleBinding>) {
        holder.binding.detail = data
    }

    companion object {
        private val DIFF_CALLBACK = object : DiffUtil.ItemCallback<HomeArticleDetail>() {
            override fun areItemsTheSame(oldItem: HomeArticleDetail, newItem: HomeArticleDetail): Boolean =
                oldItem.id == newItem.id

            override fun areContentsTheSame(oldItem: HomeArticleDetail, newItem: HomeArticleDetail): Boolean =
                oldItem == newItem
        }
    }
}

// 首页缓存 Adapter
class HomeArticleCacheAdapter(data: MutableList<HomeArticleDetail>? = null) :
    BaseRecyclerAdapter<HomeArticleDetail>(data) {

    fun injectAdapterData(articles: MutableList<HomeArticleDetail>) {
        DiffUtil.calculateDiff(HomeArticleCacheDiffCall(articles, getAdapterData()), true).let {
            it.dispatchUpdatesTo(this)
            mData = (mData ?: arrayListOf()).apply {
                clear()
                addAll(articles)
            }
        }
    }

    override fun getLayoutId(viewType: Int): Int = R.layout.recycler_home_article

    override fun setVariable(data: HomeArticleDetail, position: Int, holder: BaseViewHolder<ViewDataBinding>) {
        (holder.binding as? RecyclerHomeArticleBinding)?.let {
            it.detail = data
        }
    }
}

class HomeArticleCacheDiffCall(
    private val newList: MutableList<HomeArticleDetail>?,
    private val oldList: MutableList<HomeArticleDetail>?
) : DiffUtil.Callback() {

    override fun areItemsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean =
        if (newList.isNullOrEmpty() || oldList.isNullOrEmpty()) false
        else newList[newItemPosition].id == oldList[oldItemPosition].id

    override fun getOldListSize(): Int = oldList?.size ?: 0

    override fun getNewListSize(): Int = newList?.size ?: 0

    override fun areContentsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean =
        if (newList.isNullOrEmpty() || oldList.isNullOrEmpty()) false
        else newList[newItemPosition] == oldList[oldItemPosition]
}
