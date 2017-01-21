package com.github.s0nerik.shoppingassistant

import android.support.annotation.StringRes

/**
 * Created by Alex on 1/21/2017.
 * GitHub: https://github.com/s0nerik
 * LinkedIn: https://linkedin.com/in/sonerik
 */
fun Any.getString(@StringRes resId: Int, vararg formatArgs: Any): String {
    return App.context.getString(resId, *formatArgs)
}