package com.github.s0nerik.shoppingassistant

import android.support.annotation.DimenRes
import android.support.annotation.StringRes

/**
 * Created by Alex on 1/21/2017.
 * GitHub: https://github.com/s0nerik
 * LinkedIn: https://linkedin.com/in/sonerik
 */
fun Any.getString(@StringRes resId: Int, vararg formatArgs: Any): String {
    return App.context.getString(resId, *formatArgs)
}

fun Any.getDimen(@DimenRes resId: Int): Float {
    return App.context.resources.getDimension(resId)
}

fun Any.getDimenPixelSize(@DimenRes resId: Int): Int {
    return App.context.resources.getDimensionPixelSize(resId)
}

fun Any.getDimenPixelOffset(@DimenRes resId: Int): Int {
    return App.context.resources.getDimensionPixelOffset(resId)
}