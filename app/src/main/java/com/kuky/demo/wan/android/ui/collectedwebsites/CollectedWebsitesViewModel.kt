package com.kuky.demo.wan.android.ui.collectedwebsites

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.kuky.demo.wan.android.base.NetworkState
import com.kuky.demo.wan.android.base.safeLaunch
import com.kuky.demo.wan.android.entity.WebsiteData

/**
 * @author Taonce.
 * @description
 */
class CollectedWebsitesViewModel(private val repository: CollectedWebsitesRepository) : ViewModel() {
    val netState = MutableLiveData<NetworkState>()
    val mWebsitesData = MutableLiveData<List<WebsiteData>?>()

    fun fetchWebSitesData() {
        viewModelScope.safeLaunch {
            block = {
                netState.postValue(NetworkState.LOADING)
                mWebsitesData.value = repository.getCollectedWebsites()
                netState.postValue(NetworkState.LOADED)
            }

            onError = {
                netState.postValue(NetworkState.error(it.message))
            }
        }
    }

    fun addWebsites(name: String, link: String) = repository.getAddWebsiteResultStream(name, link)

    fun editWebsite(id: Int, name: String, link: String) = repository.getEditWebsiteResultStream(id, name, link)

    fun deleteFavouriteWebsite(id: Int) = repository.getDeleteWebsiteResultStream(id)
}
