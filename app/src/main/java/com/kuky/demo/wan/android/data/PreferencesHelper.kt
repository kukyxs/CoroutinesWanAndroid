package com.kuky.demo.wan.android.data

import android.content.Context
import com.kuky.demo.wan.android.entity.ProjectCategoryData
import com.kuky.demo.wan.android.utils.SharePreferencesUtils

/**
 * @author kuky.
 * @description
 */
object PreferencesHelper {
    private const val USER_KEY_ID = "wan.user.id"
    private const val USER_KEY_NAME = "wan.user.name"
    private const val USER_KEY_COOKIE = "wan.user.cookie"
    private const val PROJECT_KEY_ID = "wan.project.id"
    private const val PROJECT_KEY_TITLE = "wan.project.title"
    private const val SEARCH_KEY_KEYWORD = "wan.search.keyword"
    private const val CACHE_KEY_BANNER = "wan.cache.banner"
    private const val CACHE_KEY_HOME_ARTICLES = "wan.cache.home.articles"

    fun saveUserId(context: Context, id: Int) =
        SharePreferencesUtils.saveInteger(context, USER_KEY_ID, id)

    fun hasLogin(context: Context) =
        SharePreferencesUtils.getInteger(context, USER_KEY_ID) > 0

    fun saveUserName(context: Context, name: String) =
        SharePreferencesUtils.saveString(context, USER_KEY_NAME, name)

    fun fetchUserName(context: Context) =
        SharePreferencesUtils.getString(context, USER_KEY_NAME)

    fun saveCookie(context: Context, cookie: String) =
        SharePreferencesUtils.saveString(context, USER_KEY_COOKIE, cookie)

    fun fetchCookie(context: Context) =
        SharePreferencesUtils.getString(context, USER_KEY_COOKIE)

    fun saveProjectCategory(context: Context, category: ProjectCategoryData) {
        SharePreferencesUtils.saveInteger(context, PROJECT_KEY_ID, category.id)
        SharePreferencesUtils.saveString(context, PROJECT_KEY_TITLE, category.name)
    }

    fun fetchProjectCategory(context: Context) = mapOf(
        "title" to SharePreferencesUtils.getString(context, PROJECT_KEY_TITLE),
        "id" to SharePreferencesUtils.getInteger(context, PROJECT_KEY_ID)
    )

    fun saveSearchKeyword(context: Context, keyword: String) =
        SharePreferencesUtils.saveString(context, SEARCH_KEY_KEYWORD, keyword)

    fun fetchSearchKeyword(context: Context) =
        SharePreferencesUtils.getString(context, SEARCH_KEY_KEYWORD)

    // =======================> LOCAL CACHES <=================================

    fun saveBannerCache(context: Context, bannerJson: String) =
        SharePreferencesUtils.saveString(context, CACHE_KEY_BANNER, bannerJson)

    fun fetchBannerCache(context: Context) =
        SharePreferencesUtils.getString(context, CACHE_KEY_BANNER)

    fun saveHomeArticleCache(context: Context, articleJson: String) =
        SharePreferencesUtils.saveString(context, CACHE_KEY_HOME_ARTICLES, articleJson)

    fun fetchHomeArticleCache(context: Context) =
        SharePreferencesUtils.getString(context, CACHE_KEY_HOME_ARTICLES)
}