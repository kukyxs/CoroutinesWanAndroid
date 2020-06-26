package com.kuky.demo.wan.android.ui.collectedwebsites

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.flow

/**
 * @author Taonce.
 * @description
 */
class CollectedWebsitesViewModel(private val repository: CollectedWebsitesRepository) : ViewModel() {
    fun getWebsites() = flow {
        emit(repository.getCollectedWebsites())
    }

    fun addWebsites(name: String, link: String) = flow {
        emit(repository.addWebsite(name, link))
    }

    fun editWebsite(id: Int, name: String, link: String) = flow {
        emit(repository.editWebsite(id, name, link))
    }

    fun deleteFavouriteWebsite(id: Int) = flow {
        emit(repository.deleteWebsite(id))
    }
}
