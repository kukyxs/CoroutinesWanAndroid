package com.kuky.demo.wan.android.ui.widget

import android.content.Context
import android.graphics.Color
import android.util.AttributeSet
import androidx.core.content.ContextCompat
import com.kuky.demo.wan.android.R

/**
 * @author kuky.
 * @description
 */
class StatusEmpty @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : CenterDrawableTextView(context, attrs, defStyleAttr) {

    init {
        val errDrawable = ContextCompat.getDrawable(context, R.drawable.tag_empty)
        errDrawable?.setBounds(0, 0, errDrawable.minimumWidth / 2, errDrawable.minimumHeight / 2)
        compoundDrawablePadding = 12
        setCompoundDrawables(null, errDrawable, null, null)
        text = resources.getString(R.string.empty_data)
        setTextColor(Color.parseColor("#FFCCCCCC"))
    }
}