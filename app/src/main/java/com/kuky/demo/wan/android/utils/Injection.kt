package com.kuky.demo.wan.android.utils

import com.kuky.demo.wan.android.ui.main.MainRepository
import com.kuky.demo.wan.android.ui.main.MainViewModelFactory

/**
 * @author kuky.
 * @description
 */
object Injection {

    fun provideMainViewModelFactory() = MainViewModelFactory(MainRepository())
}