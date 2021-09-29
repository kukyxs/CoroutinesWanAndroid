package com.kuky.demo.wan.android.widget

import android.content.Context
import android.graphics.Color
import android.graphics.drawable.Drawable
import android.util.AttributeSet
import androidx.core.view.isVisible
import com.kuky.demo.wan.android.R
import com.kuky.demo.wan.android.extension.drawableValue
import com.kuky.demo.wan.android.extension.stringValue

/**
 * @author kuky.
 * @description
 */
class RequestStatusView @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : CenterDrawableTextView(context, attrs, defStyleAttr) {

    var errorReload: ErrorReload? = null

    init {
        injectRequestStatus(RequestStatusCode.Loading)
    }

    fun injectRequestStatus(status: RequestStatusCode) {
        val statusDrawable: Drawable?

        when (status) {
            RequestStatusCode.Loading -> {
                statusDrawable = context.drawableValue(R.drawable.tag_loading)
                text = context.stringValue(R.string.loading_data)
            }

            RequestStatusCode.Empty -> {
                statusDrawable = context.drawableValue(R.drawable.tag_empty)
                text = context.stringValue(R.string.empty_data)
            }

            RequestStatusCode.Error -> {
                statusDrawable = context.drawableValue(R.drawable.tag_load_error)
                text = context.stringValue(R.string.reload_data)
                setOnClickListener { errorReload?.reload() }
            }

            RequestStatusCode.Succeed -> {
                isVisible = false
                return
            }
        }

        isVisible = true
        statusDrawable?.setBounds(0, 0, statusDrawable.minimumWidth / 2, statusDrawable.minimumHeight / 2)
        compoundDrawablePadding = 12
        setCompoundDrawables(null, statusDrawable, null, null)
        setTextColor(Color.parseColor("#FFCCCCCC"))
    }
}