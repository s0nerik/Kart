package com.github.s0nerik.shoppingassistant.ext

import rx.Observable
import rx.android.schedulers.AndroidSchedulers

/**
 * Created by Alex on 2/21/2017.
 * GitHub: https://github.com/s0nerik
 * LinkedIn: https://linkedin.com/in/sonerik
 */

val <T> Observable<T>.lastEmission: T
    get() = toBlocking().mostRecent(null).first()

val <T> Observable<T>.firstEmission: T
    get() = toBlocking().first()

fun <T> Observable<T>.onMainThread(): Observable<T> {
    return subscribeOn(AndroidSchedulers.mainThread())
}