package com.kuky.demo.wan.android.utils

import com.kuky.demo.wan.android.ui.coins.CoinRepository
import com.kuky.demo.wan.android.ui.coins.CoinViewModelFactory
import com.kuky.demo.wan.android.ui.collectedarticles.CollectedArticlesRepository
import com.kuky.demo.wan.android.ui.collectedarticles.CollectedArticlesViewModelFactory
import com.kuky.demo.wan.android.ui.collectedwebsites.CollectedWebsitesRepository
import com.kuky.demo.wan.android.ui.collectedwebsites.CollectedWebsitesViewModelFactory
import com.kuky.demo.wan.android.ui.collection.CollectionRepository
import com.kuky.demo.wan.android.ui.collection.CollectionViewModelFactory
import com.kuky.demo.wan.android.ui.home.HomeArticleRepository
import com.kuky.demo.wan.android.ui.home.HomeArticleViewModelFactory
import com.kuky.demo.wan.android.ui.hotproject.HotProjectRepository
import com.kuky.demo.wan.android.ui.hotproject.HotProjectViewModelFactory
import com.kuky.demo.wan.android.ui.main.MainRepository
import com.kuky.demo.wan.android.ui.main.MainViewModelFactory
import com.kuky.demo.wan.android.ui.search.SearchRepository
import com.kuky.demo.wan.android.ui.search.SearchViewModelFactory
import com.kuky.demo.wan.android.ui.system.KnowledgeSystemRepository
import com.kuky.demo.wan.android.ui.system.KnowledgeSystemViewModelFactory
import com.kuky.demo.wan.android.ui.todoedit.TodoEditRepository
import com.kuky.demo.wan.android.ui.todoedit.TodoEditViewModelFactory
import com.kuky.demo.wan.android.ui.todolist.TodoListRepository
import com.kuky.demo.wan.android.ui.todolist.TodoListViewModelFactory
import com.kuky.demo.wan.android.ui.userarticles.UserArticleRepository
import com.kuky.demo.wan.android.ui.userarticles.UserArticleViewModelFactory
import com.kuky.demo.wan.android.ui.usershared.UserSharedRepository
import com.kuky.demo.wan.android.ui.usershared.UserSharedViewModelFactory
import com.kuky.demo.wan.android.ui.usersharelist.UserShareListRepository
import com.kuky.demo.wan.android.ui.usersharelist.UserShareListViewModelFactory
import com.kuky.demo.wan.android.ui.wxchapter.WxChapterRepository
import com.kuky.demo.wan.android.ui.wxchapter.WxChapterViewModelFactory
import com.kuky.demo.wan.android.ui.wxchapterlist.WxChapterListRepository
import com.kuky.demo.wan.android.ui.wxchapterlist.WxChapterListViewModelFactory

/**
 * @author kuky.
 * @description
 */
object Injection {

    fun provideMainViewModelFactory() = MainViewModelFactory(MainRepository())

    fun provideCoinViewModelFactory() = CoinViewModelFactory(CoinRepository())

    fun provideCollectionViewModelFactory() = CollectionViewModelFactory(CollectionRepository())

    fun provideCollectedArticlesViewModelFactory() = CollectedArticlesViewModelFactory(CollectedArticlesRepository())

    fun provideCollectedWebsitesViewModelFactory() = CollectedWebsitesViewModelFactory(CollectedWebsitesRepository())

    fun provideHomeArticleViewModelFactory() = HomeArticleViewModelFactory(HomeArticleRepository())

    fun provideHotProjectViewModelFactory() = HotProjectViewModelFactory(HotProjectRepository())

    fun provideSearchViewModelFactory() = SearchViewModelFactory(SearchRepository())

    fun provideKnowledgeSystemViewModelFactory() = KnowledgeSystemViewModelFactory(KnowledgeSystemRepository())

    fun provideTodoEditViewModelFactory() = TodoEditViewModelFactory(TodoEditRepository())

    fun provideTodoListViewModelFactory() = TodoListViewModelFactory(TodoListRepository())

    fun provideUserArticleViewModelFactory() = UserArticleViewModelFactory(UserArticleRepository())

    fun provideUserSharedViewModelFactory() = UserSharedViewModelFactory(UserSharedRepository())

    fun provideUserShareListViewModelFactory() = UserShareListViewModelFactory(UserShareListRepository())

    fun provideWxChapterViewModelFactory() = WxChapterViewModelFactory(WxChapterRepository())

    fun provideWxChapterListViewModelFactory() = WxChapterListViewModelFactory(WxChapterListRepository())
}