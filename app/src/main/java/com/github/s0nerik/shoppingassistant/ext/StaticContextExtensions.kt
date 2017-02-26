package com.github.s0nerik.shoppingassistant.ext

import android.support.annotation.ColorRes
import android.support.annotation.DimenRes
import android.support.annotation.StringRes
import android.support.v4.content.ContextCompat
import com.github.s0nerik.shoppingassistant.App

/**
 * Created by Alex on 1/21/2017.
 * GitHub: https://github.com/s0nerik
 * LinkedIn: https://linkedin.com/in/sonerik
 */
fun Any.getString(@StringRes resId: Int, vararg formatArgs: Any): String {
    return App.context.getString(resId, *formatArgs)
}

fun Any.getColor(@ColorRes resId: Int): Int {
    return ContextCompat.getColor(App.context, resId)
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