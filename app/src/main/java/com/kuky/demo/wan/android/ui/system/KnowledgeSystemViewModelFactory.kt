@file:Suppress("UNCHECKED_CAST")

package com.kuky.demo.wan.android.ui.system

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider


/**
 * @author kuky.
 * @description
 */
class KnowledgeSystemViewModelFactory(
    private val repository: KnowledgeSystemRepository
) : ViewModelProvider.NewInstanceFactory() {

    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(KnowledgeSystemViewModel::class.java))
            return KnowledgeSystemViewModel(repository) as T
        throw IllegalArgumentException("Unknown ViewModel Type")
    }
}
