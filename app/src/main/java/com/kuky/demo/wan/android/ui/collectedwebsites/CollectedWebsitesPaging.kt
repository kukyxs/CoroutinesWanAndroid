package com.kuky.demo.wan.android.ui.collectedwebsites

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
import kotlin.coroutines.resumeWithException
import kotlin.coroutines.suspendCoroutine

/**
 * @author Taonce.
 * @description
 */
class CollectedWebsitesRepository {
    private suspend fun getCookie() = withContext(Dispatchers.IO) {
        PreferencesHelper.fetchCookie(WanApplication.instance)
    }

    suspend fun getCollectedWebsites() = withContext(Dispatchers.IO) {
        RetrofitManager.apiService.collectWebsiteList(getCookie())
    }

    suspend fun addWebsite(name: String, link: String): ResultBack = withContext(Dispatchers.IO) {
        val response = RetrofitManager.apiService.addWebsite(name, link, getCookie())
        //TODO 泛型问题
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

class CollectedWebsitesAdapter :
    BaseRecyclerAdapter<RecyclerCollectedWebsitesBinding, WebsiteData>(null) {
    override fun getLayoutId(viewType: Int) = R.layout.recycler_collected_websites

    override fun setVariable(
        data: WebsiteData,
        position: Int,
        holder: BaseViewHolder<RecyclerCollectedWebsitesBinding>
    ) {
        holder.binding.data = data
    }

    /**
     * 利用diffutil更新数据
     */
    fun update(newData: MutableList<WebsiteData>?) {
        val result = DiffUtil.calculateDiff(CollectedDiffUtil(newData, getAdapterData()), true)
        result.dispatchUpdatesTo(this)
        if (mData == null) {
            mData = mutableListOf()
        }
        mData?.clear()
        mData?.addAll(newData ?: arrayListOf())
    }
}

class CollectedDiffUtil(
    private val newData: MutableList<WebsiteData>?,
    private val oldData: MutableList<WebsiteData>?
) : DiffUtil.Callback() {
    override fun areItemsTheSame(oldItemPosition: Int, newItemPosition: Int) =
        newData?.get(newItemPosition)?.id == oldData?.get(oldItemPosition)?.id

    override fun getOldListSize() = oldData?.size ?: 0

    override fun getNewListSize() = newData?.size ?: 0


    override fun areContentsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean =
        newData?.get(newItemPosition) == oldData?.get(oldItemPosition)
}

