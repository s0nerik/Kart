package com.github.s0nerik.shoppingassistant.ext

import android.databinding.ObservableField
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers


/**
 * Created by Alex on 2/21/2017.
 * GitHub: https://github.com/s0nerik
 * LinkedIn: https://linkedin.com/in/sonerik
 */

val <T> Observable<T>.lastEmission: T
    get() = blockingMostRecent(null).first()

val <T> Observable<T>.firstEmission: T
    get() = blockingFirst()

fun <T> Observable<T>.onMainThread(): Observable<T> {
    return subscribeOn(AndroidSchedulers.mainThread())
}

fun <T> ObservableField<T>.toObservable(startWithCurrentValue: Boolean = true): Observable<T> {
    return Observable.create { emitter ->
        val callback = object : android.databinding.Observable.OnPropertyChangedCallback() {
            override fun onPropertyChanged(dataBindingObservable: android.databinding.Observable, propertyId: Int) {
                if (dataBindingObservable === this@toObservable) {
                    emitter.onNext(get())
                }
            }
        }
        addOnPropertyChangedCallback(callback)
        emitter.setCancellable { removeOnPropertyChangedCallback(callback) }
        if (startWithCurrentValue) emitter.onNext(get())
    }
}

fun android.databinding.Observable.observeChanges(brId: Int? = null): Observable<Int> {
    return Observable.create { emitter ->
        val callback = object : android.databinding.Observable.OnPropertyChangedCallback() {
            override fun onPropertyChanged(dataBindingObservable: android.databinding.Observable, propertyId: Int) {
                if (dataBindingObservable === this@observeChanges) {
                    if (brId == null || propertyId == brId) emitter.onNext(propertyId)
                }
            }
        }
        addOnPropertyChangedCallback(callback)
        emitter.setCancellable { removeOnPropertyChangedCallback(callback) }
    }
}