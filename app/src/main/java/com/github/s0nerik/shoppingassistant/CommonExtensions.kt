package com.github.s0nerik.shoppingassistant

import java.lang.ref.WeakReference

/**
 * Created by Alex on 1/4/2017.
 * GitHub: https://github.com/s0nerik
 * LinkedIn: https://linkedin.com/in/sonerik
 */

fun <T> WeakReference<T>.safe(action: T.() -> Unit) {
    this.get()?.action()
}