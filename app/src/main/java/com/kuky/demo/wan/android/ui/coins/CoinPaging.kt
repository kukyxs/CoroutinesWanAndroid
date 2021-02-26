package com.kuky.demo.wan.android.ui.coins

import android.text.Spannable
import android.text.SpannableStringBuilder
import android.text.style.ForegroundColorSpan
import androidx.core.content.ContextCompat
import androidx.paging.PagingSource
import androidx.paging.PagingState
import androidx.recyclerview.widget.DiffUtil
import com.kuky.demo.wan.android.R
import com.kuky.demo.wan.android.base.BasePagingDataAdapter
import com.kuky.demo.wan.android.base.BaseViewHolder
import com.kuky.demo.wan.android.databinding.RecyclerCoinRankBinding
import com.kuky.demo.wan.android.databinding.RecyclerCoinRecordBinding
import com.kuky.demo.wan.android.entity.CoinRankDetail
import com.kuky.demo.wan.android.entity.CoinRecordDetail

/**
 * @author kuky.
 * @description
 */
class CoinRecordPagingSource(
    private val repository: CoinRepository
) : PagingSource<Int, CoinRecordDetail>() {

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, CoinRecordDetail> {
        val page = params.key ?: 1

        return try {
            val records = repository.getCoinRecord(page) ?: mutableListOf()
            LoadResult.Page(
                data = records,
                prevKey = if (page == 1) null else page - 1,
                nextKey = if (records.isEmpty()) null else page + 1
            )
        } catch (e: Exception) {
            LoadResult.Error(e)
        }
    }

    override fun getRefreshKey(state: PagingState<Int, CoinRecordDetail>) = state.anchorPosition
}

class CoinRecordPagingAdapter :
    BasePagingDataAdapter<CoinRecordDetail, RecyclerCoinRecordBinding>(DIFF_CALLBACK) {

    override fun getLayoutId() = R.layout.recycler_coin_record

    override fun setVariable(data: CoinRecordDetail, position: Int, holder: BaseViewHolder<RecyclerCoinRecordBinding>) {
        holder.binding.coinRecord = data
    }

    companion object {
        private val DIFF_CALLBACK = object : DiffUtil.ItemCallback<CoinRecordDetail>() {
            override fun areItemsTheSame(oldItem: CoinRecordDetail, newItem: CoinRecordDetail): Boolean =
                oldItem.id == newItem.id

            override fun areContentsTheSame(oldItem: CoinRecordDetail, newItem: CoinRecordDetail): Boolean =
                oldItem == newItem
        }
    }
}

class CoinRankPagingSource(
    private val repository: CoinRepository
) : PagingSource<Int, CoinRankDetail>() {

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, CoinRankDetail> {
        val page = params.key ?: 1

        return try {
            val ranks = repository.getCoinRanks(page) ?: mutableListOf()

            LoadResult.Page(
                data = ranks,
                prevKey = if (page == 1) null else page - 1,
                nextKey = if (ranks.isEmpty()) null else page + 1
            )
        } catch (e: Exception) {
            LoadResult.Error(e)
        }
    }

    override fun getRefreshKey(state: PagingState<Int, CoinRankDetail>) = state.anchorPosition
}

class CoinRankPagingAdapter :
    BasePagingDataAdapter<CoinRankDetail, RecyclerCoinRankBinding>(DIFF_CALLBACK) {

    override fun getLayoutId(): Int = R.layout.recycler_coin_rank

    override fun setVariable(data: CoinRankDetail, position: Int, holder: BaseViewHolder<RecyclerCoinRankBinding>) {
        holder.binding.rank = data

        val context = holder.binding.root.context
        holder.binding.coinSpan = SpannableStringBuilder("${data.coinCount}").apply {
            setSpan(
                ForegroundColorSpan(ContextCompat.getColor(context, R.color.coin_color)),
                0, length, Spannable.SPAN_INCLUSIVE_EXCLUSIVE
            )
            setSpan(
                ForegroundColorSpan(ContextCompat.getColor(context, R.color.colorPrimary)),
                run { append("\t/\t");length },
                run { append("Lv${data.level}");length },
                Spannable.SPAN_INCLUSIVE_EXCLUSIVE
            )
        }

        holder.binding.imageRes = ContextCompat.getDrawable(
            context, when (position) {
                0 -> R.drawable.ic_no_1
                1 -> R.drawable.ic_no_2
                2 -> R.drawable.ic_no_3
                else -> R.drawable.ic_no_other
            }
        )
    }

    companion object {
        private val DIFF_CALLBACK = object : DiffUtil.ItemCallback<CoinRankDetail>() {
            override fun areItemsTheSame(oldItem: CoinRankDetail, newItem: CoinRankDetail): Boolean =
                oldItem.userId == newItem.userId

            override fun areContentsTheSame(oldItem: CoinRankDetail, newItem: CoinRankDetail): Boolean =
                oldItem == newItem
        }
    }
}