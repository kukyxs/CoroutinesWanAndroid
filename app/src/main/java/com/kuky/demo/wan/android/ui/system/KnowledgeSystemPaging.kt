package com.kuky.demo.wan.android.ui.system

import androidx.databinding.ViewDataBinding
import androidx.lifecycle.MutableLiveData
import androidx.paging.DataSource
import androidx.paging.PageKeyedDataSource
import androidx.recyclerview.widget.DiffUtil
import com.kuky.demo.wan.android.R
import com.kuky.demo.wan.android.WanApplication
import com.kuky.demo.wan.android.base.*
import com.kuky.demo.wan.android.data.PreferencesHelper
import com.kuky.demo.wan.android.databinding.RecyclerKnowledgeSystemBinding
import com.kuky.demo.wan.android.entity.SystemCategory
import com.kuky.demo.wan.android.entity.SystemData
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

class KnowledgeSystemRepository {
    suspend fun loadSystemType() = withContext(Dispatchers.IO) {
        RetrofitManager.apiService.knowledgeSystem().data
    }

    suspend fun loadArticle4System(page: Int, cid: Int): List<WxChapterListDatas>? = withContext(Dispatchers.IO) {
        RetrofitManager.apiService.articleInCategory(page, cid, PreferencesHelper.fetchCookie(WanApplication.instance)).data.datas
    }
}

class KnowledgeSystemDataSource(
    private val repository: KnowledgeSystemRepository,
    private val cid: Int
) : PageKeyedDataSource<Int, WxChapterListDatas>(), CoroutineScope by IOScope() {

    val initState = MutableLiveData<NetworkState>()

    override fun loadInitial(params: LoadInitialParams<Int>, callback: LoadInitialCallback<Int, WxChapterListDatas>) {
        safeLaunch({
            initState.postValue(NetworkState.LOADING)
            repository.loadArticle4System(0, cid)?.let {
                callback.onResult(it, null, 1)
                initState.postValue(NetworkState.LOADED)
            }
        }, { initState.postValue(NetworkState.error(it.message, ERROR_CODE_INIT)) })
    }

    override fun loadAfter(params: LoadParams<Int>, callback: LoadCallback<Int, WxChapterListDatas>) {
        safeLaunch({
            repository.loadArticle4System(params.key, cid)?.let {
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

class KnowledgeSystemDataSourceFactory(
    private val repository: KnowledgeSystemRepository,
    private val pid: Int
) : DataSource.Factory<Int, WxChapterListDatas>() {
    val sourceLiveData = MutableLiveData<KnowledgeSystemDataSource>()

    override fun create(): DataSource<Int, WxChapterListDatas> = KnowledgeSystemDataSource(repository, pid).apply {
        sourceLiveData.postValue(this)
    }
}

class KnowledgeSystemTypeAdapter(
    mData: MutableList<SystemData>? = null
) : BaseRecyclerAdapter<SystemData>(mData) {

    override fun getLayoutId(viewType: Int): Int = R.layout.recycler_knowledge_system

    override fun setVariable(
        data: SystemData, position: Int, holder: BaseViewHolder<ViewDataBinding>
    ) {
        (holder.binding as RecyclerKnowledgeSystemBinding).let {
            it.name = mData?.get(position)?.name
            it.selected = mSelectionPosition == position
        }
    }

    fun setNewData(newData: MutableList<SystemData>?) {
        val result = DiffUtil.calculateDiff(TypeDiffUtil(getAdapterData(), newData), true)
        result.dispatchUpdatesTo(this)
        if (mData == null) {
            mData = arrayListOf()
        }

        mData?.clear()
        mData?.addAll(newData ?: arrayListOf())
    }
}

class KnowledgeSystemSecTypeAdapter(
    mData: MutableList<SystemCategory>? = null
) : BaseRecyclerAdapter<SystemCategory>(mData) {

    override fun getLayoutId(viewType: Int): Int = R.layout.recycler_knowledge_system

    override fun setVariable(
        data: SystemCategory, position: Int, holder: BaseViewHolder<ViewDataBinding>
    ) {
        (holder.binding as RecyclerKnowledgeSystemBinding).let {
            it.name = mData?.get(position)?.name
            it.selected = mSelectionPosition == position
        }
    }

    fun setNewData(newData: MutableList<SystemCategory>?) {
        val result = DiffUtil.calculateDiff(SecTypeDiffUtil(getAdapterData(), newData), true)
        result.dispatchUpdatesTo(this)
        if (mData == null) {
            mData = arrayListOf()
        }

        mData?.clear()
        mData?.addAll(newData ?: arrayListOf())
    }
}

class TypeDiffUtil(
    private val oldData: MutableList<SystemData>?,
    private val newData: MutableList<SystemData>?
) : DiffUtil.Callback() {
    override fun areItemsTheSame(oldItemPosition: Int, newItemPosition: Int) =
        if (oldData.isNullOrEmpty() || newData.isNullOrEmpty()) false
        else oldData[oldItemPosition].id == newData[newItemPosition].id

    override fun getOldListSize() = oldData?.size ?: 0

    override fun getNewListSize() = newData?.size ?: 0

    override fun areContentsTheSame(oldItemPosition: Int, newItemPosition: Int) =
        if (oldData.isNullOrEmpty() || newData.isNullOrEmpty()) false
        else oldData[oldItemPosition].name == newData[newItemPosition].name
}

class SecTypeDiffUtil(
    private val oldData: MutableList<SystemCategory>?,
    private val newData: MutableList<SystemCategory>?
) : DiffUtil.Callback() {
    override fun areItemsTheSame(oldItemPosition: Int, newItemPosition: Int) =
        if (oldData.isNullOrEmpty() || newData.isNullOrEmpty()) false
        else oldData[oldItemPosition].id == newData[newItemPosition].id

    override fun getOldListSize() = oldData?.size ?: 0

    override fun getNewListSize() = newData?.size ?: 0

    override fun areContentsTheSame(oldItemPosition: Int, newItemPosition: Int) =
        if (oldData.isNullOrEmpty() || newData.isNullOrEmpty()) false
        else oldData[oldItemPosition].name == newData[newItemPosition].name
}

