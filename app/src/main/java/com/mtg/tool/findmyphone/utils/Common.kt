package com.mtg.tool.findmyphone.utils

import android.content.res.Resources
import android.graphics.Color

/**
 * add common function here
 */
object Common {

    /**
     * get screen device
     */
    val screenWidth: Int
        get() = Resources.getSystem().displayMetrics.widthPixels
    val screenHeight: Int
        get() = Resources.getSystem().displayMetrics.heightPixels

    fun smartCheckColor(stringColor: String): Int {
        return try {
            Color.parseColor(stringColor)
        } catch (_: Exception) {
            return Color.TRANSPARENT
        }
    }


}