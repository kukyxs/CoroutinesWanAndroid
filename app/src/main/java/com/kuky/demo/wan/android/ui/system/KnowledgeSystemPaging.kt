package com.kuky.demo.wan.android.ui.system

import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.kuky.demo.wan.android.entity.WxChapterListDatas

/**
 * @author kuky.
 * @description
 */
class KnowledgeSystemPagingSource(
    private val repository: KnowledgeSystemRepository, private val cid: Int
) : PagingSource<Int, WxChapterListDatas>() {
    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, WxChapterListDatas> {
        val page = params.key ?: 0
        return try {
            val chapters = repository.loadArticle4System(page, cid) ?: mutableListOf()
            LoadResult.Page(
                data = chapters,
                prevKey = if (page == 0) null else page - 1,
                nextKey = if (chapters.isEmpty()) null else page + 1
            )
        } catch (e: Exception) {
            LoadResult.Error(e)
        }
    }

    override fun getRefreshKey(state: PagingState<Int, WxChapterListDatas>) = state.anchorPosition
}