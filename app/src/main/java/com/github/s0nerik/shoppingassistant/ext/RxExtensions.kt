package com.github.s0nerik.shoppingassistant.ext

import android.databinding.Observable.OnPropertyChangedCallback
import android.databinding.ObservableField
import rx.Emitter
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

fun <T> ObservableField<T>.toObservable(startWithCurrentValue: Boolean = true): Observable<T> {
    return Observable.fromEmitter({ emitter ->
                                      val callback = object : OnPropertyChangedCallback() {
                                          override fun onPropertyChanged(dataBindingObservable: android.databinding.Observable, propertyId: Int) {
                                              if (dataBindingObservable === this@toObservable) {
                                                  emitter.onNext(get())
                                              }
                                          }
                                      }
                                      addOnPropertyChangedCallback(callback)
                                      emitter.setCancellation { removeOnPropertyChangedCallback(callback) }
                                      if (startWithCurrentValue) emitter.onNext(get())
                                  }, Emitter.BackpressureMode.LATEST)
}

fun android.databinding.Observable.observeChanges(brId: Int? = null): Observable<Int> {
    return Observable.fromEmitter({ emitter ->
                                      val callback = object : OnPropertyChangedCallback() {
                                          override fun onPropertyChanged(dataBindingObservable: android.databinding.Observable, propertyId: Int) {
                                              if (dataBindingObservable === this@observeChanges) {
                                                  if (brId == null || propertyId == brId) emitter.onNext(propertyId)
                                              }
                                          }
                                      }
                                      addOnPropertyChangedCallback(callback)
                                      emitter.setCancellation { removeOnPropertyChangedCallback(callback) }
                                  }, Emitter.BackpressureMode.LATEST)
}