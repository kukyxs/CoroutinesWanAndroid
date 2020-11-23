package com.kuky.demo.wan.android.data

import android.content.Context
import com.kuky.demo.wan.android.utils.*

/**
 * @author kuky.
 * @description
 */
object PreferencesHelper {
    private const val STATE_KEY_FIRST_INT = "wan.state.first.in"
    private const val USER_KEY_ID = "wan.user.id"
    private const val USER_KEY_NAME = "wan.user.name"
    private const val USER_KEY_COOKIE = "wan.user.cookie"
    private const val CACHE_KEY_BANNER = "wan.cache.banner"

    suspend fun saveFirstInState(context: Context, isFirst: Boolean) =
        context.saveToDataStore(STATE_KEY_FIRST_INT, isFirst)

    fun isFirstLaunchIn(context: Context) = context.fetchDataStoreData(STATE_KEY_FIRST_INT) { true }

    suspend fun saveUsername(context: Context, username: String) =
        context.saveToDataStore(USER_KEY_NAME, username)

    fun fetchUsername(context: Context) = context.fetchDataStoreData(USER_KEY_NAME) { "" }

    fun saveUserId(context: Context, id: Int) = context.saveInteger(USER_KEY_ID, id)

    fun hasLogin(context: Context) = context.getInteger(USER_KEY_ID) > 0

    fun saveUserName(context: Context, name: String) = context.saveString(USER_KEY_NAME, name)

    fun fetchUserName(context: Context) = context.getString(USER_KEY_NAME)

    fun saveCookie(context: Context, cookie: String) = context.saveString(USER_KEY_COOKIE, cookie)

    fun fetchCookie(context: Context) = context.getString(USER_KEY_COOKIE)

    // =======================> LOCAL CACHES <=================================

    fun saveBannerCache(context: Context, bannerJson: String) = context.saveString(CACHE_KEY_BANNER, bannerJson)

    fun fetchBannerCache(context: Context) = context.getString(CACHE_KEY_BANNER)
}