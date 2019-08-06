package com.kuky.demo.wan.android.ui.todolist

import androidx.core.content.ContextCompat
import androidx.databinding.ViewDataBinding
import androidx.paging.DataSource
import androidx.paging.PageKeyedDataSource
import androidx.recyclerview.widget.DiffUtil
import com.kuky.demo.wan.android.R
import com.kuky.demo.wan.android.WanApplication
import com.kuky.demo.wan.android.base.*
import com.kuky.demo.wan.android.data.PreferencesHelper
import com.kuky.demo.wan.android.databinding.RecyclerParentTodoChoiceBinding
import com.kuky.demo.wan.android.databinding.RecyclerSubTodoChoiceBinding
import com.kuky.demo.wan.android.databinding.RecyclerTodoItemBinding
import com.kuky.demo.wan.android.entity.Choice
import com.kuky.demo.wan.android.entity.ITodoChoice
import com.kuky.demo.wan.android.entity.TodoChoiceGroup
import com.kuky.demo.wan.android.entity.TodoInfo
import com.kuky.demo.wan.android.network.RetrofitManager
import kotlinx.coroutines.*
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine

/**
 * @author kuky.
 * @description
 */

class TodoRepository {
    suspend fun fetchTodoList(page: Int, param: HashMap<String, Int>): List<TodoInfo>? = withContext(Dispatchers.IO) {
        RetrofitManager.apiService
            .fetchTodoList(page, PreferencesHelper.fetchCookie(WanApplication.instance), param)
            .data.datas
    }

    suspend fun updateTodoState(id: Int, state: Int) = withContext(Dispatchers.IO) {
        val result = RetrofitManager.apiService.updateTodoState(
            id, state,
            PreferencesHelper.fetchCookie(WanApplication.instance)
        )

        suspendCoroutine<ResultBack> { continuation ->
            if (result.errorCode == 0) continuation.resume(ResultBack(CODE_SUCCEED, ""))
            else continuation.resume(ResultBack(CODE_FAILED, result.errorMsg))
        }
    }
}

class TodoDataSource(private val repository: TodoRepository, private val param: HashMap<String, Int>) :
    PageKeyedDataSource<Int, TodoInfo>(), CoroutineScope by MainScope() {

    override fun loadInitial(params: LoadInitialParams<Int>, callback: LoadInitialCallback<Int, TodoInfo>) {
        safeLaunch {
            val result = repository.fetchTodoList(1, param)
            result?.let {
                callback.onResult(it, null, 2)
            }
        }
    }

    override fun loadAfter(params: LoadParams<Int>, callback: LoadCallback<Int, TodoInfo>) {
        safeLaunch {
            repository.fetchTodoList(params.key, param)?.let {
                callback.onResult(it, params.key + 1)
            }
        }
    }

    override fun loadBefore(params: LoadParams<Int>, callback: LoadCallback<Int, TodoInfo>) {
        safeLaunch {
            repository.fetchTodoList(params.key, param)?.let {
                callback.onResult(it, params.key - 1)
            }
        }
    }

    override fun invalidate() {
        super.invalidate()
        cancel()
    }
}

class TodoDataSourceFactory(private val repository: TodoRepository, private val param: HashMap<String, Int>) :
    DataSource.Factory<Int, TodoInfo>() {
    override fun create(): DataSource<Int, TodoInfo> = TodoDataSource(repository, param)
}

class TodoPagingAdapter : BasePagedListAdapter<TodoInfo, RecyclerTodoItemBinding>(DIFF_CALLBACK) {

    override fun getLayoutId(viewType: Int): Int = R.layout.recycler_todo_item

    override fun setVariable(data: TodoInfo, position: Int, holder: BaseViewHolder<RecyclerTodoItemBinding>) {
        holder.binding.todo = data

        holder.binding.todoTypeStr = when (data.type) {
            0 -> "只用这一个"
            1 -> "工作"
            2 -> "学习"
            3 -> "生活"
            else -> ""
        }

        holder.binding.priorityBg = ContextCompat.getDrawable(
            holder.binding.root.context, when (data.priority) {
                1 -> R.drawable.type_important
                2 -> R.drawable.type_general
                3 -> R.drawable.type_normal
                else -> R.drawable.search_bg
            }
        )
    }

    companion object {
        private val DIFF_CALLBACK = object : DiffUtil.ItemCallback<TodoInfo>() {
            override fun areItemsTheSame(oldItem: TodoInfo, newItem: TodoInfo): Boolean =
                oldItem.id == newItem.id

            override fun areContentsTheSame(oldItem: TodoInfo, newItem: TodoInfo): Boolean =
                oldItem.title == newItem.title && oldItem.content == oldItem.content &&
                        oldItem.completeDateStr == newItem.completeDateStr &&
                        oldItem.priority == newItem.priority && oldItem.type == newItem.type
        }
    }
}

class TodoChoiceAdapter(choices: MutableList<TodoChoiceGroup>) : BaseRecyclerAdapter<ITodoChoice>(null) {
    companion object {
        const val PARENT_ITEM = 0xFF01
        const val CHILD_ITEM = 0xFF02
    }

    private val mIndexList = MutableList(choices.size) { 0 }
    private val mEndList = MutableList(choices.size) { 0 }
    private val mParamIndex = MutableList(choices.size) { "" }

    init {
        var totalEnd = -1
        var totalInitSelected = 0

        mData = arrayListOf<ITodoChoice>().apply {
            for (i in choices.indices) {
                val group = choices[i]

                this.add(group)
                this.addAll(group.choices)

                // 记录参数表
                mParamIndex[i] = group.param_key

                // 记录末尾表
                totalEnd += (1 + group.choices.size)
                mEndList[i] = totalEnd

                // 记录选中表
                totalInitSelected += 1
                mIndexList[i] = totalInitSelected
                totalInitSelected += group.choices.size
            }
        }
    }

    fun updateSelectedIndex(position: Int) {
        for (i in mEndList.indices) {
            if (mData!![position] is Choice && position <= mEndList[i]) {
                mIndexList[i] = position
                notifyDataSetChanged()
                break
            }
        }
    }

    fun getApiParams(): HashMap<String, Int> {
        val param = hashMapOf<String, Int>()

        for (i in mIndexList.indices) {
            val value = mData!![mIndexList[i]]

            if (value is Choice && value.type >= 0) {
                param[mParamIndex[i]] = value.type
            }
        }

        return param
    }

    override fun getLayoutId(viewType: Int): Int =
        if (viewType == PARENT_ITEM) R.layout.recycler_parent_todo_choice
        else R.layout.recycler_sub_todo_choice

    override fun setVariable(data: ITodoChoice, position: Int, holder: BaseViewHolder<ViewDataBinding>) {
        if (getItemViewType(position) == PARENT_ITEM) {
            (holder.binding as RecyclerParentTodoChoiceBinding).parentTodo = data as TodoChoiceGroup
        } else {
            (holder.binding as RecyclerSubTodoChoiceBinding).let {
                val context = holder.binding.root.context
                it.subTodo = data as Choice
                if (position in mIndexList) {
                    it.backgroundRes = ContextCompat.getDrawable(context, R.drawable.choice_selected)
                    it.colorRes = ContextCompat.getColor(context, R.color.colorPrimary)
                } else {
                    it.backgroundRes = ContextCompat.getDrawable(context, R.drawable.choice_unselected)
                    it.colorRes = ContextCompat.getColor(context, android.R.color.black)
                }
            }
        }
    }

    override fun getItemViewType(position: Int): Int =
        if (mData!![position] is TodoChoiceGroup) PARENT_ITEM else CHILD_ITEM

}