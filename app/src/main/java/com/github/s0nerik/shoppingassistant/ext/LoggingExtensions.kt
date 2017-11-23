@file:Suppress("NOTHING_TO_INLINE")

package com.github.s0nerik.shoppingassistant.ext

import timber.log.Timber

/**
 * Created by Alex Isaienko on 11/13/2017.
 * GitHub: https://github.com/s0nerik
 * LinkedIn: https://linkedin.com/in/sonerik
 */

inline fun Throwable?.log() {
    if (this != null)
        Timber.e(this)
}

inline fun Any?.log() {
    Timber.d(this.toString())
}

inline fun Any?.logVerbose() {
    Timber.v(this.toString())
}

inline fun Any?.logInfo() {
    Timber.i(this.toString())
}