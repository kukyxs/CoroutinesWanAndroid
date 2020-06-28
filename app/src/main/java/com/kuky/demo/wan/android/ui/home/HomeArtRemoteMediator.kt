package com.kuky.demo.wan.android.ui.home

import androidx.paging.ExperimentalPagingApi
import androidx.paging.LoadType
import androidx.paging.PagingState
import androidx.paging.RemoteMediator
import androidx.room.withTransaction
import com.kuky.demo.wan.android.data.WanDatabase
import com.kuky.demo.wan.android.data.db.HomeArticleDetail
import com.kuky.demo.wan.android.data.db.HomeArticleRemoteKey
import java.io.InvalidObjectException

/**
 * @author kuky.
 * @description
 */
@OptIn(ExperimentalPagingApi::class)
class HomeArtRemoteMediator(
    private val repository: HomeArticleRepository,
    private val wanDatabase: WanDatabase
) : RemoteMediator<Int, HomeArticleDetail>() {

    override suspend fun load(loadType: LoadType, state: PagingState<Int, HomeArticleDetail>): MediatorResult {
        val page = when (loadType) {
            LoadType.REFRESH -> {
                val remoteKey = getRemoteKeyForCurrentItem(state)
                remoteKey?.nextKey?.minus(1) ?: 0
            }

            LoadType.PREPEND -> {
                val remoteKey = getRemoteKeyForFirstItem(state)
                    ?: throw InvalidObjectException("RemoteKey should not be null")

                if (remoteKey.prevKey == null)
                    return MediatorResult.Success(endOfPaginationReached = false)

                remoteKey.prevKey
            }

            LoadType.APPEND -> {
                val remoteKey = getRemoteKeyForLastItem(state)
                if (remoteKey?.nextKey == null)
                    throw InvalidObjectException("RemoteKey should not be null for $loadType")

                remoteKey.nextKey
            }
        }

        return try {
            val articles = if (page == 0) mutableListOf<HomeArticleDetail>().apply {
                addAll(repository.loadTops() ?: mutableListOf())
                addAll(repository.loadPageData(page) ?: mutableListOf())
            } else (repository.loadPageData(page) ?: mutableListOf())

            val endOfPagingReached = articles.isEmpty()

            wanDatabase.withTransaction {
                if (loadType == LoadType.REFRESH) {
                    wanDatabase.homeArticleCacheDao().clearHomeArtRemoteKey()
                    wanDatabase.homeArticleCacheDao().clearHomeArticleCache()
                }

                val prevKey = if (page == 0) null else page - 1
                val nextKey = if (endOfPagingReached) null else page + 1
                val keys = articles.map { HomeArticleRemoteKey(it.id, prevKey, nextKey) }

                wanDatabase.homeArticleCacheDao().cacheHomeArtRemoteKey(keys)
                wanDatabase.homeArticleCacheDao().cacheHomeArticles(articles)
            }

            MediatorResult.Success(endOfPagingReached)
        } catch (e: Exception) {
            MediatorResult.Error(e)
        }
    }

    private suspend fun getRemoteKeyForFirstItem(state: PagingState<Int, HomeArticleDetail>) =
        state.pages.firstOrNull { it.data.isNotEmpty() }
            ?.data?.firstOrNull()?.let { art ->
                wanDatabase.homeArticleCacheDao().remoteKeyByArtId(art.id)
            }

    private suspend fun getRemoteKeyForCurrentItem(state: PagingState<Int, HomeArticleDetail>) =
        state.anchorPosition?.let { position ->
            state.closestItemToPosition(position)?.id?.let {
                wanDatabase.homeArticleCacheDao().remoteKeyByArtId(it)
            }
        }

    private suspend fun getRemoteKeyForLastItem(state: PagingState<Int, HomeArticleDetail>) =
        state.pages.lastOrNull { it.data.isNotEmpty() }
            ?.data?.lastOrNull()?.let { art ->
                wanDatabase.homeArticleCacheDao().remoteKeyByArtId(art.id)
            }
}