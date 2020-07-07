package com.kuky.demo.wan.android.di

import com.kuky.demo.wan.android.data.WanDatabase
import com.kuky.demo.wan.android.network.RetrofitManager
import com.kuky.demo.wan.android.ui.app.LoadingDialog
import com.kuky.demo.wan.android.ui.coins.CoinRepository
import com.kuky.demo.wan.android.ui.coins.CoinViewModel
import com.kuky.demo.wan.android.ui.collectedarticles.CollectedArticlesRepository
import com.kuky.demo.wan.android.ui.collectedarticles.CollectedArticlesViewModel
import com.kuky.demo.wan.android.ui.collectedwebsites.CollectedWebsitesRepository
import com.kuky.demo.wan.android.ui.collectedwebsites.CollectedWebsitesViewModel
import com.kuky.demo.wan.android.ui.collection.CollectionRepository
import com.kuky.demo.wan.android.ui.collection.CollectionViewModel
import com.kuky.demo.wan.android.ui.home.HomeArticleRepository
import com.kuky.demo.wan.android.ui.home.HomeArticleViewModel
import com.kuky.demo.wan.android.ui.hotproject.HotProjectRepository
import com.kuky.demo.wan.android.ui.hotproject.HotProjectViewModel
import org.koin.android.ext.koin.androidContext
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module

/**
 * @author kuky.
 * @description
 */
val dataSourceModule = module {
    single { RetrofitManager.apiService }

    single { WanDatabase.getInstance(androidContext()) }
}

val viewModelModule = module {
    viewModel { CoinViewModel(get()) }

    viewModel { CollectedArticlesViewModel(get()) }

    viewModel { CollectionViewModel(get()) }

    viewModel { CollectedWebsitesViewModel(get()) }

    viewModel { HomeArticleViewModel(get()) }

    viewModel { HotProjectViewModel(get()) }
}

val repositoryModule = module {
    single { CoinRepository(get()) }

    single { CollectedArticlesRepository(get()) }

    single { CollectionRepository(get()) }

    single { CollectedWebsitesRepository(get()) }

    single { HomeArticleRepository(get(), get()) }

    single { HotProjectRepository(get()) }
}

val dialogModule = module {
    single { LoadingDialog() }
}