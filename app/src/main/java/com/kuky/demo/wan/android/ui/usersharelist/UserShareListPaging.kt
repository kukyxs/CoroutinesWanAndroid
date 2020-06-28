package com.kuky.demo.wan.android.ui.usersharelist

import androidx.paging.PagingSource
import com.kuky.demo.wan.android.entity.UserArticleDetail

/**
 * @author kuky.
 * @description
 */

class UserSharePagingSource(
    private val repository: UserShareListRepository
) : PagingSource<Int, UserArticleDetail>() {

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, UserArticleDetail> {
        val page = params.key ?: 1

        return try {
            val articles = repository.fetchUserShareList(page) ?: mutableListOf()

            LoadResult.Page(
                data = articles,
                prevKey = if (page == 1) null else page - 1,
                nextKey = if (articles.isEmpty()) null else page + 1
            )
        } catch (e: Exception) {
            LoadResult.Error(e)
        }
    }
}