package com.kuky.demo.wan.android.ui.wxchapterlist

import androidx.lifecycle.MutableLiveData
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
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.cancel
import kotlinx.coroutines.withContext

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
    private val wxId: Int, private val keyword: String
) : PageKeyedDataSource<Int, WxChapterListDatas>(), CoroutineScope by IOScope() {
    val initState = MutableLiveData<NetworkState>()

    override fun loadInitial(params: LoadInitialParams<Int>, callback: LoadInitialCallback<Int, WxChapterListDatas>) {
        safeLaunch({
            initState.postValue(NetworkState.LOADING)
            repository.loadPage(wxId, 0, keyword)?.let {
                callback.onResult(it, null, 1)
                initState.postValue(NetworkState.LOADED)
            }
        }, { initState.postValue(NetworkState.error(it.message, ERROR_CODE_INIT)) })
    }

    override fun loadAfter(params: LoadParams<Int>, callback: LoadCallback<Int, WxChapterListDatas>) {
        safeLaunch({
            repository.loadPage(wxId, params.key, keyword)?.let {
                callback.onResult(it, params.key + 1)
            }
        }, { initState.postValue(NetworkState.error(it.message, ERROR_CODE_MORE)) })
    }

    override fun loadBefore(params: LoadParams<Int>, callback: LoadCallback<Int, WxChapterListDatas>) {}

    override fun invalidate() {
        super.invalidate()
        cancel()
    }
}

class WxChapterListDataSourceFactory(
    private val repository: WxChapterListRepository,
    private val wxId: Int, private val keyword: String
) : DataSource.Factory<Int, WxChapterListDatas>() {
    val sourceLiveData = MutableLiveData<WxChapterListDataSource>()

    override fun create(): DataSource<Int, WxChapterListDatas> = WxChapterListDataSource(repository, wxId, keyword).apply {
        sourceLiveData.postValue(this)
    }
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