package com.github.s0nerik.shoppingassistant.ext

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import io.reactivex.*
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers
import org.jetbrains.anko.bundleOf
import org.reactivestreams.Subscription
import rx_activity_result2.RxActivityResult


/**
 * Created by Alex Isaienko on 10/4/17.
 * GitHub: https://github.com/s0nerik
 * LinkedIn: https://linkedin.com/in/sonerik
 */
inline fun <reified TActivity: Activity, TResult> Activity.startForResult(resultKey: String, extras: Bundle = Bundle.EMPTY): Maybe<TResult> {
    return RxActivityResult.on(this)
            .startIntent(Intent(this, TActivity::class.java).putExtras(extras))
            .firstElement()
            .filter { it.resultCode() == Activity.RESULT_OK }
            .map {
                @Suppress("UNCHECKED_CAST")
                it.data().extras[resultKey] as TResult
            }
}

inline fun <reified TActivity: Activity> Activity.startForResultCompletable(extras: Bundle = Bundle.EMPTY): Completable {
    return RxActivityResult.on(this)
            .startIntent(Intent(this, TActivity::class.java).putExtras(extras))
            .firstElement()
            .onErrorComplete()
            .ignoreElement()
}

inline fun <reified TActivity: Activity> Activity.startForResult(vararg extras: Pair<String, Any>): Completable =
        startForResultCompletable<TActivity>(bundleOf(*extras))

inline fun <reified TActivity: Activity> Activity.startForResultCompletable(vararg extras: Pair<String, Any>): Completable =
        startForResultCompletable<TActivity>(bundleOf(*extras))

//region Scheduler extensions
fun <T> Flowable<T>.subscribeOnIoThread(): Flowable<T> = subscribeOn(Schedulers.io())
fun <T> Flowable<T>.observeOnMainThread(): Flowable<T> = observeOn(AndroidSchedulers.mainThread())
fun <T> Flowable<T>.async(): Flowable<T> = subscribeOnIoThread().observeOnMainThread()

fun <T> Observable<T>.subscribeOnIoThread(): Observable<T> = subscribeOn(Schedulers.io())
fun <T> Observable<T>.observeOnMainThread(): Observable<T> = observeOn(AndroidSchedulers.mainThread())
fun <T> Observable<T>.async(): Observable<T> = subscribeOnIoThread().observeOnMainThread()

fun <T> Single<T>.subscribeOnIoThread(): Single<T> = subscribeOn(Schedulers.io())
fun <T> Single<T>.observeOnMainThread(): Single<T> = observeOn(AndroidSchedulers.mainThread())
fun <T> Single<T>.async(): Single<T> = subscribeOnIoThread().observeOnMainThread()

fun <T> Maybe<T>.subscribeOnIoThread(): Maybe<T> = subscribeOn(Schedulers.io())
fun <T> Maybe<T>.observeOnMainThread(): Maybe<T> = observeOn(AndroidSchedulers.mainThread())
fun <T> Maybe<T>.async(): Maybe<T> = subscribeOnIoThread().observeOnMainThread()
//endregion

