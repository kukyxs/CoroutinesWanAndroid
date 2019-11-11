package com.kuky.demo.wan.android.ui.widget

import android.content.Context
import android.util.AttributeSet
import androidx.core.content.ContextCompat
import com.kuky.demo.wan.android.R

/**
 * @author kuky.
 * @description
 */

class StatusError @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : CenterDrawableTextView(context, attrs, defStyleAttr) {

    var errorReload: ErrorReload? = null

    init {
        val errDrawable = ContextCompat.getDrawable(context, R.drawable.tag_load_error)
        errDrawable?.setBounds(0, 0, errDrawable.minimumWidth, errDrawable.minimumHeight)
        setCompoundDrawables(null, errDrawable, null, null)
        text = resources.getString(R.string.reload_data)
        setOnClickListener { errorReload?.reload() }
    }
}