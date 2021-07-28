package com.kuky.demo.wan.android.di

import androidx.fragment.app.Fragment
import com.kuky.demo.wan.android.base.ViewPager2FragmentAdapter
import com.kuky.demo.wan.android.data.WanDatabase
import com.kuky.demo.wan.android.entity.TodoChoiceGroup
import com.kuky.demo.wan.android.network.RetrofitManager
import com.kuky.demo.wan.android.ui.app.AppViewModel
import com.kuky.demo.wan.android.ui.app.LoadingDialog
import com.kuky.demo.wan.android.ui.app.MainActivity
import com.kuky.demo.wan.android.ui.coins.*
import com.kuky.demo.wan.android.ui.collectedarticles.CollectedArticlesFragment
import com.kuky.demo.wan.android.ui.collectedarticles.CollectedArticlesPagingAdapter
import com.kuky.demo.wan.android.ui.collectedarticles.CollectedArticlesRepository
import com.kuky.demo.wan.android.ui.collectedarticles.CollectedArticlesViewModel
import com.kuky.demo.wan.android.ui.collectedwebsites.CollectedWebsitesAdapter
import com.kuky.demo.wan.android.ui.collectedwebsites.CollectedWebsitesFragment
import com.kuky.demo.wan.android.ui.collectedwebsites.CollectedWebsitesRepository
import com.kuky.demo.wan.android.ui.collectedwebsites.CollectedWebsitesViewModel
import com.kuky.demo.wan.android.ui.collection.CollectionFragment
import com.kuky.demo.wan.android.ui.collection.CollectionRepository
import com.kuky.demo.wan.android.ui.collection.CollectionViewModel
import com.kuky.demo.wan.android.ui.friend.FriendRepository
import com.kuky.demo.wan.android.ui.friend.FriendViewModel
import com.kuky.demo.wan.android.ui.home.HomeArticleFragment
import com.kuky.demo.wan.android.ui.home.HomeArticlePagingAdapter
import com.kuky.demo.wan.android.ui.home.HomeArticleRepository
import com.kuky.demo.wan.android.ui.home.HomeArticleViewModel
import com.kuky.demo.wan.android.ui.hotproject.*
import com.kuky.demo.wan.android.ui.main.*
import com.kuky.demo.wan.android.ui.search.*
import com.kuky.demo.wan.android.ui.system.*
import com.kuky.demo.wan.android.ui.todoedit.TodoEditRepository
import com.kuky.demo.wan.android.ui.todoedit.TodoEditViewModel
import com.kuky.demo.wan.android.ui.todolist.*
import com.kuky.demo.wan.android.ui.userarticles.UserArticleFragment
import com.kuky.demo.wan.android.ui.userarticles.UserArticlePagingAdapter
import com.kuky.demo.wan.android.ui.userarticles.UserArticleRepository
import com.kuky.demo.wan.android.ui.userarticles.UserArticleViewModel
import com.kuky.demo.wan.android.ui.usershared.UserSharedFragment
import com.kuky.demo.wan.android.ui.usershared.UserSharedPagingAdapter
import com.kuky.demo.wan.android.ui.usershared.UserSharedRepository
import com.kuky.demo.wan.android.ui.usershared.UserSharedViewModel
import com.kuky.demo.wan.android.ui.usersharelist.ShareArticleDialogFragment
import com.kuky.demo.wan.android.ui.usersharelist.UserShareListFragment
import com.kuky.demo.wan.android.ui.usersharelist.UserShareListRepository
import com.kuky.demo.wan.android.ui.usersharelist.UserShareListViewModel
import com.kuky.demo.wan.android.ui.wxchapter.WxChapterAdapter
import com.kuky.demo.wan.android.ui.wxchapter.WxChapterFragment
import com.kuky.demo.wan.android.ui.wxchapter.WxChapterRepository
import com.kuky.demo.wan.android.ui.wxchapter.WxChapterViewModel
import com.kuky.demo.wan.android.ui.wxchapterlist.WxChapterListFragment
import com.kuky.demo.wan.android.ui.wxchapterlist.WxChapterListRepository
import com.kuky.demo.wan.android.ui.wxchapterlist.WxChapterListViewModel
import com.kuky.demo.wan.android.ui.wxchapterlist.WxChapterPagingAdapter
import org.koin.android.ext.koin.androidApplication
import org.koin.android.ext.koin.androidContext
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module
import java.util.*

/**
 * @author kuky.
 * @description
 */
val dataSourceModule = module {
    single { RetrofitManager.apiService }

    single { WanDatabase.buildDatabase(androidContext()) }

    single { Calendar.getInstance() }
}

val viewModelModule = module {
    viewModel { AppViewModel(androidApplication()) }

    viewModel { CoinViewModel(get(), androidApplication()) }

    viewModel { CollectedArticlesViewModel(get(), androidApplication()) }

    viewModel { CollectionViewModel(get()) }

    viewModel { CollectedWebsitesViewModel(get()) }

    viewModel { HomeArticleViewModel(get()) }

    viewModel { HotProjectViewModel(get()) }

    viewModel { MainViewModel(get()) }

    viewModel { SearchViewModel(get()) }

    viewModel { KnowledgeSystemViewModel(get()) }

    viewModel { TodoEditViewModel(get()) }

    viewModel { TodoListViewModel(get()) }

    viewModel { UserArticleViewModel(get()) }

    viewModel { UserSharedViewModel(get()) }

    viewModel { UserShareListViewModel(get()) }

    viewModel { WxChapterViewModel(get()) }

    viewModel { WxChapterListViewModel(get()) }

    viewModel { FriendViewModel(get()) }
}

val repositoryModule = module {
    single { CoinRepository(androidContext(), get()) }

    single { CollectedArticlesRepository(androidContext(), get()) }

    single { CollectionRepository(get()) }

    single { CollectedWebsitesRepository(get()) }

    single { HomeArticleRepository(get(), get()) }

    single { HotProjectRepository(get()) }

    single { MainRepository(get()) }

    single { SearchRepository(get()) }

    single { KnowledgeSystemRepository(get()) }

    single { TodoEditRepository(get()) }

    single { TodoListRepository(get()) }

    single { UserArticleRepository(get()) }

    single { UserSharedRepository(get()) }

    single { UserShareListRepository(get()) }

    single { WxChapterRepository(get()) }

    single { WxChapterListRepository(get()) }

    single { FriendRepository(get()) }
}

val fragmentModule = module {
    factory { (type: Int) ->
        CoinCommonSubFragment.instance(type)
    }

    scope<CollectionFragment> {
        scoped { CollectedArticlesFragment() }
        scoped { CollectedWebsitesFragment() }
    }
}

val adapterModule = module {
    factory { (holder: Fragment, children: MutableList<Fragment>) ->
        ViewPager2FragmentAdapter(holder, children)
    }

    scope<CoinCommonSubFragment> {
        scoped { CoinRecordPagingAdapter() }
        scoped { CoinRankPagingAdapter() }
    }

    scope<CollectedArticlesFragment> {
        scoped { CollectedArticlesPagingAdapter() }
    }

    scope<CollectedWebsitesFragment> {
        scoped { CollectedWebsitesAdapter() }
    }

    scope<HomeArticleFragment> {
        scoped { HomeArticlePagingAdapter() }
    }

    scope<HotProjectFragment> {
        scoped { HomeProjectPagingAdapter() }
    }

    scope<ProjectCategoryDialog> {
        scoped { ProjectCategoryAdapter() }
    }

    scope<SearchFragment> {
        scoped { HistoryAdapter() }
        scoped { SearchArticlePagingAdapter() }
    }

    scope<KnowledgeSystemFragment> {
        scoped { WxChapterPagingAdapter() }
    }

    scope<KnowledgeSystemDialogFragment> {
        scoped { KnowledgeSystemTypeAdapter() }
        scoped { KnowledgeSystemSecTypeAdapter() }
    }

    scope<TodoListFragment> {
        scoped { TodoListPagingAdapter() }
        scoped { (choice: MutableList<TodoChoiceGroup>?) -> TodoChoiceAdapter(choice ?: mutableListOf()) }
    }

    scope<UserArticleFragment> {
        scoped { UserArticlePagingAdapter() }
    }

    scope<UserSharedFragment> {
        scoped { UserSharedPagingAdapter() }
    }

    scope<UserShareListFragment> {
        scoped { UserSharedPagingAdapter() }
    }

    scope<WxChapterFragment> {
        scoped { WxChapterAdapter() }
    }

    scope<WxChapterListFragment> {
        scoped { WxChapterPagingAdapter() }
    }
}

val dialogModule = module {
    scope<MainActivity> {
        scoped { LoadingDialog() }
    }

    scope<HotProjectFragment> {
        scoped { ProjectCategoryDialog() }
    }

    scope<MainFragment> {
        scoped { AboutUsDialogFragment() }
        scoped { WxDialogFragment() }
    }

    scope<KnowledgeSystemFragment> {
        scoped { KnowledgeSystemDialogFragment() }
    }

    scope<UserShareListFragment> {
        scoped { ShareArticleDialogFragment() }
    }
}