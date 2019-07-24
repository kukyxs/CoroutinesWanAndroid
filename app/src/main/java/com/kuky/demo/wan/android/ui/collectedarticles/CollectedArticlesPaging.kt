package com.kuky.demo.wan.android.ui.collectedarticles

import androidx.paging.DataSource
import androidx.paging.PageKeyedDataSource
import androidx.recyclerview.widget.DiffUtil
import com.kuky.demo.wan.android.R
import com.kuky.demo.wan.android.WanApplication
import com.kuky.demo.wan.android.base.BasePagedListAdapter
import com.kuky.demo.wan.android.base.BaseViewHolder
import com.kuky.demo.wan.android.base.safeLaunch
import com.kuky.demo.wan.android.data.PreferencesHelper
import com.kuky.demo.wan.android.databinding.RecyclerCollectedArticleBinding
import com.kuky.demo.wan.android.entity.UserCollectDetail
import com.kuky.demo.wan.android.network.RetrofitManager
import kotlinx.coroutines.*


/**
 * Author: Taonce
 * Date: 2019/7/19
 * Project: CoroutinesWanAndroid
 * Desc:
 */
class CollectedArticlesRepository {
    private fun getCookie() = PreferencesHelper.fetchCookie(WanApplication.instance)


    suspend fun getCollectedArticleDatas(page: Int): List<UserCollectDetail>? = withContext(Dispatchers.IO) {
        RetrofitManager.apiService.userCollectedArticles(page, getCookie()).data.datas
    }

    suspend fun deleteCollectedArticle(articleId: Int, originId: Int) = withContext(Dispatchers.IO) {
        RetrofitManager.apiService.unCollectCollection(articleId, originId, getCookie())
    }
}

class CollectedArticlesDataSources(private val repo: CollectedArticlesRepository) :
    PageKeyedDataSource<Int, UserCollectDetail>(), CoroutineScope by MainScope() {
    override fun loadInitial(params: LoadInitialParams<Int>, callback: LoadInitialCallback<Int, UserCollectDetail>) {
        safeLaunch {
            val data = repo.getCollectedArticleDatas(0)
            data?.let {
                callback.onResult(it, null, 1)
            }
        }
    }

    override fun loadAfter(params: LoadParams<Int>, callback: LoadCallback<Int, UserCollectDetail>) {
        safeLaunch {
            val data = repo.getCollectedArticleDatas(params.key)
            data?.let {
                callback.onResult(it, params.key + 1)
            }
        }
    }

    override fun loadBefore(params: LoadParams<Int>, callback: LoadCallback<Int, UserCollectDetail>) {
        safeLaunch {
            val data = repo.getCollectedArticleDatas(params.key)
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

class CollectedArticlesDataSourceFactory(private val repo: CollectedArticlesRepository) :
    DataSource.Factory<Int, UserCollectDetail>() {
    override fun create(): DataSource<Int, UserCollectDetail> = CollectedArticlesDataSources(repo)
}

class CollectedArticlesAdapter :
    BasePagedListAdapter<UserCollectDetail, RecyclerCollectedArticleBinding>(DIFF_CALLBACK) {
    override fun getLayoutId(viewType: Int) = R.layout.recycler_collected_article

    override fun setVariable(
        data: UserCollectDetail,
        position: Int,
        holder: BaseViewHolder<RecyclerCollectedArticleBinding>
    ) {
        holder.binding.data = data
    }

    companion object {
        private val DIFF_CALLBACK = object : DiffUtil.ItemCallback<UserCollectDetail>() {
            override fun areItemsTheSame(oldItem: UserCollectDetail, newItem: UserCollectDetail): Boolean =
                oldItem.id == newItem.id

            override fun areContentsTheSame(oldItem: UserCollectDetail, newItem: UserCollectDetail): Boolean =
                oldItem == newItem
        }
    }
}



