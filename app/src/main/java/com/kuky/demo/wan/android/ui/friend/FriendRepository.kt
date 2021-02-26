package com.kuky.demo.wan.android.ui.friend

import com.kuky.demo.wan.android.network.ApiService
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

/**
 * @author kuky.
 * @description
 */
class FriendRepository(private val api: ApiService) {

    suspend fun fetchFriendsWebsites() = withContext(Dispatchers.IO) { api.commonlyUsedWebsite() }
}