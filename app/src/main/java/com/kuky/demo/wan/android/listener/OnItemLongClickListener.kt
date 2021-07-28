package com.kuky.demo.wan.android.listener

import android.view.View

/**
 * @author kuky.
 * @description RecyclerList的长按事件
 */
fun interface OnItemLongClickListener {
    fun onItemLongClick(position: Int, view: View?)
}