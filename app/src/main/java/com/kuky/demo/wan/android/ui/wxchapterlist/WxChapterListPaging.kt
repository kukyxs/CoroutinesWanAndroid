package com.kuky.demo.wan.android.ui.wxchapterlist

import androidx.paging.DataSource
import androidx.paging.PageKeyedDataSource
import androidx.recyclerview.widget.DiffUtil
import com.kuky.demo.wan.android.R
import com.kuky.demo.wan.android.WanApplication
import com.kuky.demo.wan.android.base.*
import com.kuky.demo.wan.android.data.PreferencesHelper
import com.kuky.demo.wan.android.databinding.RecyclerWxChapterListBinding
import com.kuky.demo.wan.android.entity.WxChapterListDatas
import com.kuky.demo.wan.android.network.RetrofitManager
import kotlinx.coroutines.*

/**
 * @author Taonce.
 * @description
 */
class WxChapterListRepository {
    suspend fun loadPage(wxId: Int, page: Int, key: String): List<WxChapterListDatas>? = withContext(Dispatchers.IO) {
        RetrofitManager.apiService.wxChapterList(wxId, page, PreferencesHelper.fetchCookie(WanApplication.instance), key).data.datas
    }
}

class WxChapterListDataSource(
    private val repository: WxChapterListRepository,
    private val wxId: Int, private val keyword: String,
    private val handler: PagingThrowableHandler
) : PageKeyedDataSource<Int, WxChapterListDatas>(), CoroutineScope by MainScope() {
    override fun loadInitial(params: LoadInitialParams<Int>, callback: LoadInitialCallback<Int, WxChapterListDatas>) {
        safeLaunch({
            handler.invoke(PAGING_THROWABLE_LOAD_CODE_INITIAL, it)
        }, {
            val result = repository.loadPage(wxId, 0, keyword)
            result?.let {
                callback.onResult(it, null, 1)
            }
        })
    }

    override fun loadAfter(params: LoadParams<Int>, callback: LoadCallback<Int, WxChapterListDatas>) {
        safeLaunch({
            handler.invoke(PAGING_THROWABLE_LOAD_CODE_AFTER, it)
        }, {
            val result = repository.loadPage(wxId, params.key, keyword)
            result?.let {
                callback.onResult(it, params.key + 1)
            }
        })
    }

    override fun loadBefore(params: LoadParams<Int>, callback: LoadCallback<Int, WxChapterListDatas>) {
        safeLaunch({
            handler.invoke(PAGING_THROWABLE_LOAD_CODE_BEFORE, it)
        }, {
            val result = repository.loadPage(wxId, params.key, keyword)
            result?.let {
                callback.onResult(it, params.key - 1)
            }
        })
    }

    override fun invalidate() {
        super.invalidate()
        cancel()
    }
}

class WxChapterListDataSourceFactory(
    private val repository: WxChapterListRepository,
    private val wxId: Int, private val keyword: String,
    private val handler: PagingThrowableHandler
) : DataSource.Factory<Int, WxChapterListDatas>() {

    override fun create(): DataSource<Int, WxChapterListDatas> = WxChapterListDataSource(repository, wxId, keyword, handler)
}

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