package com.kuky.demo.wan.android.ui.userarticles

import android.graphics.Paint
import androidx.paging.DataSource
import androidx.paging.PageKeyedDataSource
import androidx.recyclerview.widget.DiffUtil
import com.kuky.demo.wan.android.R
import com.kuky.demo.wan.android.WanApplication
import com.kuky.demo.wan.android.base.*
import com.kuky.demo.wan.android.data.PreferencesHelper
import com.kuky.demo.wan.android.databinding.RecyclerUserArticleBinding
import com.kuky.demo.wan.android.entity.UserArticleDetail
import com.kuky.demo.wan.android.network.RetrofitManager
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.withContext

/**
 * @author kuky.
 * @description
 */

class UserArticleRepository {
    suspend fun fetchUserArticles(page: Int): List<UserArticleDetail>? = withContext(Dispatchers.IO) {
        RetrofitManager.apiService.shareArticles(page, PreferencesHelper.fetchCookie(WanApplication.instance)).data.datas
    }
}

class UserArticleDataSource(
    private val repository: UserArticleRepository,
    private val handler: PagingThrowableHandler
) : PageKeyedDataSource<Int, UserArticleDetail>(), CoroutineScope by MainScope() {

    override fun loadInitial(params: LoadInitialParams<Int>, callback: LoadInitialCallback<Int, UserArticleDetail>) {
        safeLaunch({
            handler.invoke(PAGING_THROWABLE_LOAD_CODE_INITIAL, it)
        }, {
            repository.fetchUserArticles(0)?.let {
                callback.onResult(it, null, 1)
            }
        })
    }

    override fun loadAfter(params: LoadParams<Int>, callback: LoadCallback<Int, UserArticleDetail>) {
        safeLaunch({
            handler.invoke(PAGING_THROWABLE_LOAD_CODE_AFTER, it)
        }, {
            repository.fetchUserArticles(params.key)?.let {
                callback.onResult(it, params.key + 1)
            }
        })
    }

    override fun loadBefore(params: LoadParams<Int>, callback: LoadCallback<Int, UserArticleDetail>) {
        safeLaunch({
            handler.invoke(PAGING_THROWABLE_LOAD_CODE_BEFORE, it)
        }, {
            repository.fetchUserArticles(params.key)?.let {
                callback.onResult(it, params.key - 1)
            }
        })
    }
}

class UserArticleDataSourceFactory(
    private val repository: UserArticleRepository,
    private val handler: PagingThrowableHandler
) : DataSource.Factory<Int, UserArticleDetail>() {
    override fun create(): DataSource<Int, UserArticleDetail> = UserArticleDataSource(repository, handler)
}

class UserArticleAdapter : BasePagedListAdapter<UserArticleDetail, RecyclerUserArticleBinding>(DIFF_CALLBACK) {

    var userListener: ((Int, String) -> Unit)? = null

    override fun getLayoutId(viewType: Int): Int = R.layout.recycler_user_article

    override fun setVariable(data: UserArticleDetail, position: Int, holder: BaseViewHolder<RecyclerUserArticleBinding>) {
        holder.binding.article = data
        holder.binding.shareUser.let {
            it.paint.flags = it.paint.flags or Paint.UNDERLINE_TEXT_FLAG
            it.setOnClickListener {
                userListener?.invoke(data.userId, data.shareUser)
            }
        }
    }

    companion object {
        private val DIFF_CALLBACK = object : DiffUtil.ItemCallback<UserArticleDetail>() {
            override fun areItemsTheSame(oldItem: UserArticleDetail, newItem: UserArticleDetail): Boolean =
                oldItem.id == newItem.id

            override fun areContentsTheSame(oldItem: UserArticleDetail, newItem: UserArticleDetail): Boolean =
                oldItem == newItem
        }
    }
}