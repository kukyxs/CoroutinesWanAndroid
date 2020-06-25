package com.kuky.demo.wan.android.ui.collectedwebsites

import androidx.lifecycle.ViewModel

/**
 * @author Taonce.
 * @description
 */
class CollectedWebsitesViewModel(private val repository: CollectedWebsitesRepository) : ViewModel() {
    fun getWebsites() = repository.getCollectedWebsitesStream()

    fun addWebsites(name: String, link: String) = repository.getAddWebsiteResultStream(name, link)

    fun editWebsite(id: Int, name: String, link: String) = repository.getEditWebsiteResultStream(id, name, link)

    fun deleteFavouriteWebsite(id: Int) = repository.getDeleteWebsiteResultStream(id)
}
