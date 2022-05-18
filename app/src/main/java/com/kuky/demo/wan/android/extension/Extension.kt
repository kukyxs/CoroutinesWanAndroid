package com.kuky.demo.wan.android.extension

import android.content.Context
import android.os.Build
import android.text.Html
import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import androidx.core.content.ContextCompat
import androidx.core.text.HtmlCompat
import com.kuky.demo.wan.android.WanApplication
import com.kuky.demo.wan.android.base.BaseResultData
import com.kuky.demo.wan.android.base.UiState
import com.kuky.demo.wan.android.widget.RequestStatusCode
import org.jetbrains.anko.toast

/**
 * @author kuky.
 * @description
 */
fun Context.stringValue(@StringRes stringRes: Int) = resources.getString(stringRes)

fun Context.drawableValue(@DrawableRes drawableRes: Int) = ContextCompat.getDrawable(this, drawableRes)

// 根据 viewModel 返回的 uiState 转换成对应的加载状态 RequestStatusCode
fun UiState.pageStateByUiState() = when (this) {
    is UiState.Error -> RequestStatusCode.Error
    is UiState.Loading -> RequestStatusCode.Loading
    is UiState.Succeed -> if (isEmpty) RequestStatusCode.Empty else RequestStatusCode.Succeed
    is UiState.Create -> RequestStatusCode.Succeed
}

suspend fun <T> BaseResultData<T>.handleResult(
    fail: suspend (String) -> Unit = { WanApplication.instance.toast(it) },
    ok: suspend (T) -> Unit = {}
) {
    if (errorCode == 0) ok(data)
    else fail(errorMsg)
}

@Suppress("DEPRECATION")
fun String.renderHtml(): String =
    if (Build.VERSION.SDK_INT > Build.VERSION_CODES.M)
        Html.fromHtml(this, HtmlCompat.FROM_HTML_MODE_COMPACT).toString()
    else Html.fromHtml(this).toString()
