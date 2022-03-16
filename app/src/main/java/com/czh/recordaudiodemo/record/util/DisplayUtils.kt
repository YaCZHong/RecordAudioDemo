package com.czh.recordaudiodemo.record.util

import android.content.Context
import android.util.TypedValue
import kotlin.math.roundToInt

fun dp2px(context: Context, dp: Float): Int {
    val metrics = context.resources.displayMetrics
    val px = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp, metrics)
    return px.roundToInt()
}