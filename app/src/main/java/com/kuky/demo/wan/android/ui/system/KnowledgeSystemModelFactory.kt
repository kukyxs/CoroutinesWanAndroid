package com.kuky.demo.wan.android.ui.system

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider


/**
 * @author Taonce.
 * @description
 */
@Suppress("UNCHECKED_CAST")
class KnowledgeSystemModelFactory(private val repository: KnowledgeSystemRepository) :
    ViewModelProvider.NewInstanceFactory() {
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        return KnowledgeSystemViewModel(repository) as T
    }
}
