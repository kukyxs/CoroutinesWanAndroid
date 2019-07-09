package com.kuky.demo.wan.android.ui.wxchapter

import android.widget.LinearLayout
import androidx.databinding.BindingAdapter
import androidx.navigation.Navigation
import com.kuky.demo.wan.android.entity.WxChapterData
import com.kuky.demo.wan.android.utils.LogUtils

/**
 * @author Taonce.
 * @description 微信公众号列表的item点击事件
 */

@BindingAdapter("bind:onItemClick")
fun onItemClick(linearLayout: LinearLayout, data: WxChapterData) {
    linearLayout.setOnClickListener {
        // 预计使用navigation进行[Fragment]跳转
        val controller = Navigation.findNavController(linearLayout)
//        controller.navigate()
        LogUtils.info("WxChapter item data is $data")
    }
}