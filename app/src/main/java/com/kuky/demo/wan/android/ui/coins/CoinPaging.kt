package com.kuky.demo.wan.android.ui.coins

import android.text.Spannable
import android.text.SpannableStringBuilder
import android.text.style.ForegroundColorSpan
import androidx.core.content.ContextCompat
import androidx.lifecycle.MutableLiveData
import androidx.paging.AsyncPagedListDiffer
import androidx.paging.DataSource
import androidx.paging.PageKeyedDataSource
import androidx.recyclerview.widget.DiffUtil
import com.kuky.demo.wan.android.R
import com.kuky.demo.wan.android.WanApplication
import com.kuky.demo.wan.android.base.*
import com.kuky.demo.wan.android.data.PreferencesHelper
import com.kuky.demo.wan.android.databinding.RecyclerCoinRankBinding
import com.kuky.demo.wan.android.databinding.RecyclerCoinRecordBinding
import com.kuky.demo.wan.android.entity.CoinRankDetail
import com.kuky.demo.wan.android.entity.CoinRecordDetail
import com.kuky.demo.wan.android.network.RetrofitManager
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.cancel
import kotlinx.coroutines.withContext

/**
 * @author kuky.
 * @description
 */
class CoinRepository {
    suspend fun getCoinRecord(page: Int): List<CoinRecordDetail>? = withContext(Dispatchers.IO) {
        RetrofitManager.apiService.fetchCoinsRecord(page, PreferencesHelper.fetchCookie(WanApplication.instance)).data.datas
    }

    suspend fun getCoinRanks(page: Int): List<CoinRankDetail>? = withContext(Dispatchers.IO) {
        RetrofitManager.apiService.fetchCoinRanks(page).data.datas
    }
}

class CoinRecordDataSource(
    private val repository: CoinRepository
) : PageKeyedDataSource<Int, CoinRecordDetail>(), CoroutineScope by IOScope() {
    val initState = MutableLiveData<NetworkState>()

    override fun loadInitial(params: LoadInitialParams<Int>, callback: LoadInitialCallback<Int, CoinRecordDetail>) {
        safeLaunch({
            initState.postValue(NetworkState.LOADING)
            repository.getCoinRecord(1)?.let {
                callback.onResult(it, null, 2)
                initState.postValue(NetworkState.LOADED)
            }
        }, { initState.postValue(NetworkState.error(it.message, ERROR_CODE_INIT)) })
    }

    override fun loadAfter(params: LoadParams<Int>, callback: LoadCallback<Int, CoinRecordDetail>) {
        safeLaunch({
            repository.getCoinRecord(params.key)?.let {
                callback.onResult(it, params.key + 1)
            }
        }, { initState.postValue(NetworkState.error(it.message, ERROR_CODE_MORE)) })
    }

    override fun loadBefore(params: LoadParams<Int>, callback: LoadCallback<Int, CoinRecordDetail>) {}

    override fun invalidate() {
        super.invalidate()
        cancel()
    }
}

class CoinRecordDataSourceFactory(
    private val repository: CoinRepository
) : DataSource.Factory<Int, CoinRecordDetail>() {
    val sourceLiveData = MutableLiveData<CoinRecordDataSource>()

    override fun create(): DataSource<Int, CoinRecordDetail> =
        CoinRecordDataSource(repository).apply {
            sourceLiveData.postValue(this)
        }
}

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

class CoinRankDataSource(
    private val repository: CoinRepository
) : PageKeyedDataSource<Int, CoinRankDetail>(), CoroutineScope by IOScope() {
    val initState = MutableLiveData<NetworkState>()

    override fun loadInitial(params: LoadInitialParams<Int>, callback: LoadInitialCallback<Int, CoinRankDetail>) {
        safeLaunch({
            initState.postValue(NetworkState.LOADING)
            repository.getCoinRanks(1)?.let {
                callback.onResult(it, null, 2)
                initState.postValue(NetworkState.LOADED)
            }
        }, { initState.postValue(NetworkState.error(it.message, ERROR_CODE_INIT)) })
    }

    override fun loadAfter(params: LoadParams<Int>, callback: LoadCallback<Int, CoinRankDetail>) {
        safeLaunch({
            repository.getCoinRanks(params.key)?.let {
                callback.onResult(it, params.key + 1)
            }
        }, { initState.postValue(NetworkState.error(it.message, ERROR_CODE_MORE)) })
    }

    override fun loadBefore(params: LoadParams<Int>, callback: LoadCallback<Int, CoinRankDetail>) {}

    override fun invalidate() {
        super.invalidate()
        cancel()
    }
}

class CoinRankDataSourceFactory(
    private val repository: CoinRepository
) : DataSource.Factory<Int, CoinRankDetail>() {
    val sourceLiveData = MutableLiveData<CoinRankDataSource>()

    override fun create(): DataSource<Int, CoinRankDetail> =
        CoinRankDataSource(repository).apply {
            sourceLiveData.postValue(this)
        }
}

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