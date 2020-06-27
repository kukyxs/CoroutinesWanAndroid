package com.kuky.demo.wan.android.ui.wxchapterlist

import androidx.lifecycle.MutableLiveData
import androidx.paging.DataSource
import androidx.paging.PageKeyedDataSource
import androidx.paging.PagingSource
import androidx.recyclerview.widget.DiffUtil
import com.kuky.demo.wan.android.R
import com.kuky.demo.wan.android.base.*
import com.kuky.demo.wan.android.databinding.RecyclerWxChapterListBinding
import com.kuky.demo.wan.android.entity.WxChapterListDatas
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.cancel

/**
 * @author Taonce.
 * @description
 */

class WxChapterListPagingSource(
    private val repository: WxChapterListRepository,
    private val wxId: Int, private val keyword: String
) : PagingSource<Int, WxChapterListDatas>() {
    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, WxChapterListDatas> {
        val page = params.key ?: 0

        return try {
            val chapters = repository.loadPage(wxId, page, keyword) ?: mutableListOf()
            LoadResult.Page(
                data = chapters,
                prevKey = if (page == 0) null else page - 1,
                nextKey = if (chapters.isEmpty()) null else page + 1
            )
        } catch (e: Exception) {
            LoadResult.Error(e)
        }
    }
}

class WxChapterPagingAdapter : BasePagingDataAdapter<WxChapterListDatas, RecyclerWxChapterListBinding>(DIFF_CALLBACK) {

    override fun getLayoutId(): Int = R.layout.recycler_wx_chapter_list

    override fun setVariable(
        data: WxChapterListDatas,
        position: Int,
        holder: BaseViewHolder<RecyclerWxChapterListBinding>
    ) {
        holder.binding.data = data
    }

    companion object {
        private val DIFF_CALLBACK = object : DiffUtil.ItemCallback<WxChapterListDatas>() {
            override fun areItemsTheSame(oldItem: WxChapterListDatas, newItem: WxChapterListDatas): Boolean =
                oldItem.id == newItem.id

            override fun areContentsTheSame(oldItem: WxChapterListDatas, newItem: WxChapterListDatas): Boolean =
                oldItem == newItem
        }
    }
}

//region adapter by paging2, has migrate to paging3
@Deprecated("migrate to paging3", level = DeprecationLevel.WARNING)
class WxChapterListDataSource(
    private val repository: WxChapterListRepository,
    private val wxId: Int, private val keyword: String
) : PageKeyedDataSource<Int, WxChapterListDatas>(), CoroutineScope by IOScope() {
    val initState = MutableLiveData<NetworkState>()

    override fun loadInitial(params: LoadInitialParams<Int>, callback: LoadInitialCallback<Int, WxChapterListDatas>) {
        safeLaunch {
            block = {
                initState.postValue(NetworkState.LOADING)
                repository.loadPage(wxId, 0, keyword)?.let {
                    callback.onResult(it, null, 1)
                    initState.postValue(NetworkState.LOADED)
                }
            }
            onError = {
                initState.postValue(NetworkState.error(it.message, ERROR_CODE_INIT))
            }
        }
    }

    override fun loadAfter(params: LoadParams<Int>, callback: LoadCallback<Int, WxChapterListDatas>) {
        safeLaunch {
            block = {
                repository.loadPage(wxId, params.key, keyword)?.let {
                    callback.onResult(it, params.key + 1)
                }
            }
            onError = {
                initState.postValue(NetworkState.error(it.message, ERROR_CODE_MORE))
            }
        }
    }

    override fun loadBefore(params: LoadParams<Int>, callback: LoadCallback<Int, WxChapterListDatas>) {}

    override fun invalidate() {
        super.invalidate()
        cancel()
    }
}

@Deprecated("migrate to paging3", level = DeprecationLevel.WARNING)
class WxChapterListDataSourceFactory(
    private val repository: WxChapterListRepository,
    private val wxId: Int, private val keyword: String
) : DataSource.Factory<Int, WxChapterListDatas>() {
    val sourceLiveData = MutableLiveData<WxChapterListDataSource>()

    override fun create(): DataSource<Int, WxChapterListDatas> = WxChapterListDataSource(repository, wxId, keyword).apply {
        sourceLiveData.postValue(this)
    }
}

@Deprecated("migrate to paging3", level = DeprecationLevel.WARNING)
class WxChapterListAdapter : BasePagedListAdapter<WxChapterListDatas, RecyclerWxChapterListBinding>(DIFF_CALLBACK) {
    override fun getLayoutId(viewType: Int): Int = R.layout.recycler_wx_chapter_list

    override fun setVariable(
        data: WxChapterListDatas,
        position: Int,
        holder: BaseViewHolder<RecyclerWxChapterListBinding>
    ) {
        holder.binding.data = data
    }

    companion object {
        private val DIFF_CALLBACK = object : DiffUtil.ItemCallback<WxChapterListDatas>() {
            override fun areItemsTheSame(oldItem: WxChapterListDatas, newItem: WxChapterListDatas): Boolean =
                oldItem.id == newItem.id

            override fun areContentsTheSame(oldItem: WxChapterListDatas, newItem: WxChapterListDatas): Boolean =
                oldItem == newItem
        }
    }
}
//endregion