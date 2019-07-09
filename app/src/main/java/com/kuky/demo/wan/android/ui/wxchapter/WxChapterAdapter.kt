package com.kuky.demo.wan.android.ui.wxchapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.kuky.demo.wan.android.R
import com.kuky.demo.wan.android.databinding.ItemWxChapterBinding
import com.kuky.demo.wan.android.entity.WxChapterData

/**
 * @author Taonce.
 * @description
 */

class WxChapterAdapter(
    private val context: Context?,
    private val mData: MutableList<WxChapterData>?
) : RecyclerView.Adapter<WxChapterAdapter.WxChapterHolder>() {
    var binding: ItemWxChapterBinding? = null
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): WxChapterHolder {
        val view = LayoutInflater.from(context).inflate(R.layout.item_wx_chapter, parent, false)
        binding = DataBindingUtil.bind(view)
        return WxChapterHolder(view)
    }

    override fun getItemCount(): Int {
        return mData?.size ?: 0
    }

    override fun onBindViewHolder(holder: WxChapterHolder, position: Int) {
        binding?.data = mData?.get(position)
    }


    inner class WxChapterHolder(val view: View) : RecyclerView.ViewHolder(view)
}