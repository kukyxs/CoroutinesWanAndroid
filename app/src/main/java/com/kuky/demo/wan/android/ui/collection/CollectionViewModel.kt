package com.kuky.demo.wan.android.ui.collection

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.flow

/**
 * @author kuky.
 * @description
 */
class CollectionViewModel(
    private val repository: CollectionRepository
) : ViewModel() {

    fun collectArticle(id: Int) = flow {
        emit(repository.collectArticle(id))
    }
}
