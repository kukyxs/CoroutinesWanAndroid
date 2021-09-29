package com.kuky.demo.wan.android.ui.collectedwebsites

import android.app.Application
import com.kuky.demo.wan.android.base.BaseViewModel
import com.kuky.demo.wan.android.base.UiState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.flow

/**
 * @author kuky.
 * @description
 */
class CollectedWebsitesViewModel(
    application: Application,
    private val repository: CollectedWebsitesRepository
) : BaseViewModel(application) {

    private val _editState: MutableStateFlow<UiState> = MutableStateFlow(UiState.Create)
    val editState: StateFlow<UiState> = _editState

    private val _addState: MutableStateFlow<UiState> = MutableStateFlow(UiState.Create)
    val addState: StateFlow<UiState> = _addState

    private val _removeState: MutableStateFlow<UiState> = MutableStateFlow(UiState.Create)
    val removeState: StateFlow<UiState> = _removeState

    suspend fun getWebsites() = flow {
        emit(repository.getCollectedWebsites())
    }.doRequest()

    suspend fun addWebsites(name: String, link: String) = flow {
        emit(repository.addWebsite(name, link))
    }.doRequest(_addState)

    suspend fun editWebsite(id: Int, name: String, link: String) = flow {
        emit(repository.editWebsite(id, name, link))
    }.doRequest(_editState)

    suspend fun deleteFavouriteWebsite(id: Int) = flow {
        emit(repository.deleteWebsite(id))
    }.doRequest(_removeState)
}
