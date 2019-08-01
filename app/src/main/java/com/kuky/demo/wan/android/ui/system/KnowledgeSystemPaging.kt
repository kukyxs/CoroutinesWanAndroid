package com.kuky.demo.wan.android.ui.system

import androidx.databinding.ViewDataBinding
import androidx.paging.DataSource
import androidx.paging.PageKeyedDataSource
import androidx.recyclerview.widget.DiffUtil
import com.kuky.demo.wan.android.R
import com.kuky.demo.wan.android.WanApplication
import com.kuky.demo.wan.android.base.BaseRecyclerAdapter
import com.kuky.demo.wan.android.base.BaseViewHolder
import com.kuky.demo.wan.android.base.safeLaunch
import com.kuky.demo.wan.android.data.PreferencesHelper
import com.kuky.demo.wan.android.databinding.RecyclerKnowledgeSystemBinding
import com.kuky.demo.wan.android.entity.SystemCategory
import com.kuky.demo.wan.android.entity.SystemData
import com.kuky.demo.wan.android.entity.WxChapterListDatas
import com.kuky.demo.wan.android.network.RetrofitManager
import kotlinx.coroutines.*


/**
 * @author Taonce.
 * @description
 */

class KnowledgeSystemRepository {
    suspend fun loadSystemType() = withContext(Dispatchers.IO) {
        try {
            RetrofitManager.apiService.knowledgeSystem().data
        } catch (throwable: Throwable) {
            null
        }
    }

    suspend fun loadArticle4System(page: Int, cid: Int): List<WxChapterListDatas>? = withContext(Dispatchers.IO) {
        RetrofitManager.apiService.articleInCategory(page, cid, PreferencesHelper.fetchCookie(WanApplication.instance)).data.datas
    }
}

class KnowledgeSystemDataSource(private val repository: KnowledgeSystemRepository, private val cid: Int) :
    PageKeyedDataSource<Int, WxChapterListDatas>(), CoroutineScope by MainScope() {

    override fun loadInitial(params: LoadInitialParams<Int>, callback: LoadInitialCallback<Int, WxChapterListDatas>) {
        safeLaunch {
            val data = repository.loadArticle4System(0, cid)
            data?.let {
                callback.onResult(it, null, 1)
            }
        }
    }

    override fun loadAfter(params: LoadParams<Int>, callback: LoadCallback<Int, WxChapterListDatas>) {
        safeLaunch {
            val data = repository.loadArticle4System(params.key, cid)
            data?.let {
                callback.onResult(it, params.key + 1)
            }
        }
    }

    override fun loadBefore(params: LoadParams<Int>, callback: LoadCallback<Int, WxChapterListDatas>) {
        safeLaunch {
            val data = repository.loadArticle4System(params.key, cid)
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

class KnowledgeSystemDataSourceFactory(private val repository: KnowledgeSystemRepository, private val pid: Int) :
    DataSource.Factory<Int, WxChapterListDatas>() {

    override fun create(): DataSource<Int, WxChapterListDatas> = KnowledgeSystemDataSource(repository, pid)
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

