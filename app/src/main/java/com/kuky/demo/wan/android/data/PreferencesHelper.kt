package com.kuky.demo.wan.android.data

import android.content.Context
import com.kuky.demo.wan.android.utils.SharePreferencesUtils

/**
 * @author kuky.
 * @description
 */
object PreferencesHelper {
    private const val USER_KEY_ID = "wan.user.id"
    private const val USER_KEY_NAME = "wan.user.name"
    private const val USER_KEY_COOKIE = "wan.user.cookie"
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