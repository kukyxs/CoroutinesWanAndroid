package com.kuky.demo.wan.android.ui.home

import android.os.Build
import android.text.Html
import androidx.paging.DataSource
import androidx.paging.PageKeyedDataSource
import androidx.recyclerview.widget.DiffUtil
import com.kuky.demo.wan.android.R
import com.kuky.demo.wan.android.base.BasePagedListAdapter
import com.kuky.demo.wan.android.base.BaseViewHolder
import com.kuky.demo.wan.android.base.safeLaunch
import com.kuky.demo.wan.android.databinding.RecyclerHomeArticleBinding
import com.kuky.demo.wan.android.entity.ArticleDetail
import com.kuky.demo.wan.android.network.RetrofitManager
import kotlinx.android.synthetic.main.recycler_home_article.view.*
import kotlinx.coroutines.*

/**
 * @author kuky.
 * @description
 */
class HomeArticleRepository {
    suspend fun loadPageData(page: Int): List<ArticleDetail>? = withContext(Dispatchers.IO) {
        RetrofitManager.apiService.homeArticles(page).data.datas
    }

    suspend fun loadTops(): List<ArticleDetail>? = withContext(Dispatchers.IO) {
        RetrofitManager.apiService.topArticle().data
    }
}

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
 * 方便绑定 recyclerView 的点击事件，可继承 [BasePagedListAdapter] 实现
 */
class HomeArticleAdapter : BasePagedListAdapter<ArticleDetail, RecyclerHomeArticleBinding>(DIFF_CALLBACK) {

    override fun getLayoutId(viewType: Int): Int = R.layout.recycler_home_article

    @Suppress("DEPRECATION")
    override fun setVariable(data: ArticleDetail, position: Int, holder: BaseViewHolder<RecyclerHomeArticleBinding>) {
        holder.binding.detail = data
        holder.itemView.article_title.text =
            if (Build.VERSION.SDK_INT > Build.VERSION_CODES.M)
                Html.fromHtml(data.title, Html.FROM_HTML_MODE_COMPACT)
            else Html.fromHtml(data.title)
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