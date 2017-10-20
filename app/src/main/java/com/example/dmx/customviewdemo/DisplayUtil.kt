package com.example.dmx.customviewdemo

import android.content.Context

/**
 * Created by dmx on 17-10-20.
 */
object DisplayUtil {
    fun dp2px(context: Context, dpValue: Float): Int {
        val scale: Float = context.resources.displayMetrics.density
        return (dpValue * scale + 0.5f).toInt()
    }
}