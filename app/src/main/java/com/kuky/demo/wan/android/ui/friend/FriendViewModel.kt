package com.kuky.demo.wan.android.ui.friend

import androidx.lifecycle.ViewModel
import com.kuky.demo.wan.android.entity.FriendWebsite
import kotlinx.coroutines.flow.MutableStateFlow

/**
 * @author kuky.
 * @description
 */
class FriendViewModel(private val repository: FriendRepository) : ViewModel() {
    val friendWebsiteList = MutableStateFlow(mutableListOf<FriendWebsite>())

    suspend fun requestFriendWebsites() {
        friendWebsiteList.value = try {
            repository.fetchFriendsWebsites().data
        } catch (e: Exception) {
            mutableListOf()
        }
    }
}