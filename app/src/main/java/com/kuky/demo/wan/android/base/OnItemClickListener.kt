package com.kuky.demo.wan.android.base

import android.view.View

/**
 * @author kuky.
 * @description Paging 下的点击事件
 */
fun interface OnItemClickListener {
    fun onItemClick(position: Int, view: View?)
}