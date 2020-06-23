package com.kuky.demo.wan.android.ui.coins

import android.text.Spannable
import android.text.SpannableStringBuilder
import android.text.style.ForegroundColorSpan
import androidx.core.content.ContextCompat
import androidx.lifecycle.MutableLiveData
import androidx.paging.AsyncPagedListDiffer
import androidx.paging.DataSource
import androidx.paging.PageKeyedDataSource
import androidx.paging.PagingSource
import androidx.recyclerview.widget.DiffUtil
import com.kuky.demo.wan.android.R
import com.kuky.demo.wan.android.base.*
import com.kuky.demo.wan.android.databinding.RecyclerCoinRankBinding
import com.kuky.demo.wan.android.databinding.RecyclerCoinRecordBinding
import com.kuky.demo.wan.android.entity.CoinRankDetail
import com.kuky.demo.wan.android.entity.CoinRecordDetail
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.cancel

/**
 * @author kuky.
 * @description
 */
class CoinRecordPagingSource(private val repository: CoinRepository) :
    PagingSource<Int, CoinRecordDetail>() {

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, CoinRecordDetail> {
        val page = params.key ?: 1

        return try {
            val records = repository.getCoinRecord(page)
            LoadResult.Page(
                data = records ?: mutableListOf(),
                prevKey = if (page == 1) null else page - 1,
                nextKey = if (records.isNullOrEmpty()) null else page + 1
            )
        } catch (e: Exception) {
            LoadResult.Error(e)
        }
    }
}

class CoinRecordPagingAdapter : BasePagingDataAdapter<CoinRecordDetail, RecyclerCoinRecordBinding>(DIFF_CALLBACK) {

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

class CoinRankPagingSource(private val repository: CoinRepository) :
    PagingSource<Int, CoinRankDetail>() {

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, CoinRankDetail> {
        val page = params.key ?: 1
        return try {
            val ranks = repository.getCoinRanks(page)
            LoadResult.Page(
                data = ranks ?: mutableListOf(),
                prevKey = if (page == 1) null else page - 1,
                nextKey = if (ranks.isNullOrEmpty()) null else page + 1
            )
        } catch (e: Exception) {
            LoadResult.Error(e)
        }
    }
}

class CoinRankPagingAdapter : BasePagingDataAdapter<CoinRankDetail, RecyclerCoinRankBinding>(DIFF_CALLBACK) {
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

//region adapter by paging2, has migrate to paging3
@Deprecated("migrate to paging3", level = DeprecationLevel.WARNING)
class CoinRecordDataSource(
    private val repository: CoinRepository
) : PageKeyedDataSource<Int, CoinRecordDetail>(), CoroutineScope by IOScope() {
    val initState = MutableLiveData<NetworkState>()

    override fun loadInitial(params: LoadInitialParams<Int>, callback: LoadInitialCallback<Int, CoinRecordDetail>) {
        safeLaunch {
            block = {
                initState.postValue(NetworkState.LOADING)
                repository.getCoinRecord(1)?.let {
                    callback.onResult(it, null, 2)
                    initState.postValue(NetworkState.LOADED)
                }
            }
            onError = {
                initState.postValue(NetworkState.error(it.message, ERROR_CODE_INIT))
            }
        }
    }

    override fun loadAfter(params: LoadParams<Int>, callback: LoadCallback<Int, CoinRecordDetail>) {
        safeLaunch {
            block = {
                repository.getCoinRecord(params.key)?.let {
                    callback.onResult(it, params.key + 1)
                }
            }
            onError = {
                initState.postValue(NetworkState.error(it.message, ERROR_CODE_MORE))
            }
        }
    }

    override fun loadBefore(params: LoadParams<Int>, callback: LoadCallback<Int, CoinRecordDetail>) {}

    override fun invalidate() {
        super.invalidate()
        cancel()
    }
}

@Deprecated("migrate to paging3", level = DeprecationLevel.WARNING)
class CoinRecordDataSourceFactory(
    private val repository: CoinRepository
) : DataSource.Factory<Int, CoinRecordDetail>() {
    val sourceLiveData = MutableLiveData<CoinRecordDataSource>()

    override fun create(): DataSource<Int, CoinRecordDetail> =
        CoinRecordDataSource(repository).apply {
            sourceLiveData.postValue(this)
        }
}

@Deprecated("migrate to paging3", level = DeprecationLevel.WARNING)
class CoinRecordAdapter : BaseNoBlinkingPagedListAdapter<CoinRecordDetail, RecyclerCoinRecordBinding>(DIFF_CALLBACK) {

    override fun getLayoutId(viewType: Int): Int = R.layout.recycler_coin_record

    override fun setVariable(data: CoinRecordDetail, position: Int, holder: BaseViewHolder<RecyclerCoinRecordBinding>) {
        holder.binding.coinRecord = data
    }

    override fun generateItemId(differ: AsyncPagedListDiffer<CoinRecordDetail>?, position: Int): Long =
        differ?.getItem(position)?.id?.toLong() ?: 0L

    companion object {
        private val DIFF_CALLBACK = object : DiffUtil.ItemCallback<CoinRecordDetail>() {
            override fun areItemsTheSame(oldItem: CoinRecordDetail, newItem: CoinRecordDetail): Boolean =
                oldItem.id == newItem.id

            override fun areContentsTheSame(oldItem: CoinRecordDetail, newItem: CoinRecordDetail): Boolean =
                oldItem == newItem
        }
    }
}

@Deprecated("migrate to paging3", level = DeprecationLevel.WARNING)
class CoinRankDataSource(
    private val repository: CoinRepository
) : PageKeyedDataSource<Int, CoinRankDetail>(), CoroutineScope by IOScope() {
    val initState = MutableLiveData<NetworkState>()

    override fun loadInitial(params: LoadInitialParams<Int>, callback: LoadInitialCallback<Int, CoinRankDetail>) {
        safeLaunch {
            block = {
                initState.postValue(NetworkState.LOADING)
                repository.getCoinRanks(1)?.let {
                    callback.onResult(it, null, 2)
                    initState.postValue(NetworkState.LOADED)
                }
            }
            onError = {
                initState.postValue(NetworkState.error(it.message, ERROR_CODE_INIT))
            }
        }
    }

    override fun loadAfter(params: LoadParams<Int>, callback: LoadCallback<Int, CoinRankDetail>) {
        safeLaunch {
            block = {
                repository.getCoinRanks(params.key)?.let {
                    callback.onResult(it, params.key + 1)
                }
            }
            onError = {
                initState.postValue(NetworkState.error(it.message, ERROR_CODE_MORE))
            }
        }
    }

    override fun loadBefore(params: LoadParams<Int>, callback: LoadCallback<Int, CoinRankDetail>) {}

    override fun invalidate() {
        super.invalidate()
        cancel()
    }
}

@Deprecated("migrate to paging3", level = DeprecationLevel.WARNING)
class CoinRankDataSourceFactory(
    private val repository: CoinRepository
) : DataSource.Factory<Int, CoinRankDetail>() {
    val sourceLiveData = MutableLiveData<CoinRankDataSource>()

    override fun create(): DataSource<Int, CoinRankDetail> =
        CoinRankDataSource(repository).apply {
            sourceLiveData.postValue(this)
        }
}

@Deprecated("migrate to paging3", level = DeprecationLevel.WARNING)
class CoinRankAdapter : BaseNoBlinkingPagedListAdapter<CoinRankDetail, RecyclerCoinRankBinding>(DIFF_CALLBACK) {

    override fun getLayoutId(viewType: Int): Int = R.layout.recycler_coin_rank

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
                run {
                    append("\t/\t")
                    length
                }, run {
                    append("Lv${data.level}")
                    length
                }, Spannable.SPAN_INCLUSIVE_EXCLUSIVE
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

    override fun generateItemId(differ: AsyncPagedListDiffer<CoinRankDetail>?, position: Int): Long =
        differ?.getItem(position)?.userId?.toLong() ?: 0L

    companion object {
        private val DIFF_CALLBACK = object : DiffUtil.ItemCallback<CoinRankDetail>() {
            override fun areItemsTheSame(oldItem: CoinRankDetail, newItem: CoinRankDetail): Boolean =
                oldItem.userId == newItem.userId

            override fun areContentsTheSame(oldItem: CoinRankDetail, newItem: CoinRankDetail): Boolean =
                oldItem == newItem
        }
    }
}
//endregion