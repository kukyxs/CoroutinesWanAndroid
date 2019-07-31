package com.kuky.demo.wan.android.ui.collectedwebsites

import androidx.databinding.ViewDataBinding
import androidx.recyclerview.widget.DiffUtil
import com.kuky.demo.wan.android.R
import com.kuky.demo.wan.android.WanApplication
import com.kuky.demo.wan.android.base.*
import com.kuky.demo.wan.android.data.PreferencesHelper
import com.kuky.demo.wan.android.databinding.RecyclerCollectedWebsitesBinding
import com.kuky.demo.wan.android.entity.WebsiteData
import com.kuky.demo.wan.android.network.RetrofitManager
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine

/**
 * @author Taonce.
 * @description
 */
class CollectedWebsitesRepository {
    private fun getCookie() = PreferencesHelper.fetchCookie(WanApplication.instance)

    suspend fun getCollectedWebsites(): List<WebsiteData>? = withContext(Dispatchers.IO) {
        try {
            RetrofitManager.apiService.collectWebsiteList(getCookie()).data
        } catch (throwable: Throwable) {
            // 网络请求异常后，一定要回调 null
            null
        }
    }

    suspend fun addWebsite(name: String, link: String): ResultBack = withContext(Dispatchers.IO) {
        val response = RetrofitManager.apiService.addWebsite(name, link, getCookie())
        suspendCoroutine<ResultBack> { con ->
            response.let {
                if (response.errorCode == 0) con.resume(ResultBack(CODE_SUCCEED, ""))
                else con.resume(ResultBack(CODE_FAILED, response.errorMsg))
            }
        }
    }

    suspend fun deleteWebsite(id: Int) = withContext(Dispatchers.IO) {
        RetrofitManager.apiService.deleteWebsite(id, getCookie())
    }
}

class CollectedWebsitesAdapter : BaseRecyclerAdapter<WebsiteData>(null) {

    override fun setVariable(data: WebsiteData, position: Int, holder: BaseViewHolder<ViewDataBinding>) {
        (holder.binding as RecyclerCollectedWebsitesBinding).data = data
    }

    override fun getLayoutId(viewType: Int) = R.layout.recycler_collected_websites

    /**
     * 利用diffutil更新数据
     */
    fun update(newData: MutableList<WebsiteData>?) {
        val result = DiffUtil.calculateDiff(CollectedDiffUtil(newData, getAdapterData()), true)
        if (mData == null) {
            mData = mutableListOf()
        }
        mData?.clear()
        mData?.addAll(newData ?: mutableListOf())
        result.dispatchUpdatesTo(this)
    }
}

class CollectedDiffUtil(
    private val newData: MutableList<WebsiteData>?,
    private val oldData: MutableList<WebsiteData>?
) : DiffUtil.Callback() {
    override fun areItemsTheSame(oldItemPosition: Int, newItemPosition: Int) =
        if (newData.isNullOrEmpty() || oldData.isNullOrEmpty()) false
        else newData[newItemPosition].id == oldData[oldItemPosition].id

    override fun getOldListSize() = oldData?.size ?: 0

    override fun getNewListSize() = newData?.size ?: 0

    override fun areContentsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean =
        if (newData.isNullOrEmpty() || oldData.isNullOrEmpty()) false
        else newData[newItemPosition].name == oldData[oldItemPosition].name
                && newData[newItemPosition].link == oldData[oldItemPosition].link
}

