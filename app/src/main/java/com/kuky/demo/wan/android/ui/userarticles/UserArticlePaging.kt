package com.kuky.demo.wan.android.ui.userarticles

import android.graphics.Paint
import androidx.lifecycle.MutableLiveData
import androidx.paging.DataSource
import androidx.paging.PageKeyedDataSource
import androidx.recyclerview.widget.DiffUtil
import com.kuky.demo.wan.android.R
import com.kuky.demo.wan.android.base.*
import com.kuky.demo.wan.android.databinding.RecyclerUserArticleBinding
import com.kuky.demo.wan.android.entity.UserArticleDetail
import com.kuky.demo.wan.android.network.RetrofitManager
import com.kuky.demo.wan.android.ui.app.cookie
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

/**
 * @author kuky.
 * @description
 */

class UserArticleRepository {
    suspend fun fetchUserArticles(page: Int): List<UserArticleDetail>? = withContext(Dispatchers.IO) {
        RetrofitManager.apiService.shareArticles(page, cookie).data.datas
    }
}

class UserArticleDataSource(
    private val repository: UserArticleRepository
) : PageKeyedDataSource<Int, UserArticleDetail>(), CoroutineScope by IOScope() {
    val initState = MutableLiveData<NetworkState>()

    override fun loadInitial(params: LoadInitialParams<Int>, callback: LoadInitialCallback<Int, UserArticleDetail>) {
        safeLaunch {
            block = {
                initState.postValue(NetworkState.LOADING)
                repository.fetchUserArticles(0)?.let {
                    callback.onResult(it, null, 1)
                    initState.postValue(NetworkState.LOADED)
                }
            }
            onError = {
                initState.postValue(NetworkState.error(it.message, ERROR_CODE_INIT))
            }
        }
    }

    override fun loadAfter(params: LoadParams<Int>, callback: LoadCallback<Int, UserArticleDetail>) {
        safeLaunch {
            block = {
                repository.fetchUserArticles(params.key)?.let {
                    callback.onResult(it, params.key + 1)
                }
            }
            onError = {
                initState.postValue(NetworkState.error(it.message, ERROR_CODE_MORE))
            }
        }
    }

    override fun loadBefore(params: LoadParams<Int>, callback: LoadCallback<Int, UserArticleDetail>) {}
}

class UserArticleDataSourceFactory(
    private val repository: UserArticleRepository
) : DataSource.Factory<Int, UserArticleDetail>() {
    val sourceLiveData = MutableLiveData<UserArticleDataSource>()

    override fun create(): DataSource<Int, UserArticleDetail> = UserArticleDataSource(repository).apply {
        sourceLiveData.postValue(this)
    }
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