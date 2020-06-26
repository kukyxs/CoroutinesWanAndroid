package com.kuky.demo.wan.android.ui.app

import androidx.paging.PagingConfig
import com.kuky.demo.wan.android.WanApplication
import com.kuky.demo.wan.android.data.PreferencesHelper

/**
 * @author kuky.
 * @description
 */

private const val PAGING_PAGER_SIZE = 20

val constPagerConfig = PagingConfig(PAGING_PAGER_SIZE)

val cookie = PreferencesHelper.fetchCookie(WanApplication.instance)
