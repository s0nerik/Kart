package com.github.s0nerik.shoppingassistant.ext

import android.arch.lifecycle.LiveData
import android.arch.lifecycle.LiveDataReactiveStreams
import io.reactivex.Flowable
import io.reactivex.Maybe
import io.reactivex.Observable
import io.reactivex.Single


/**
 * Created by Alex Isaienko on 10/8/17.
 * GitHub: https://github.com/s0nerik
 * LinkedIn: https://linkedin.com/in/sonerik
 */
fun <T> Flowable<T>.asLiveData(): LiveData<T> = LiveDataReactiveStreams.fromPublisher(this)
fun <T> Maybe<T>.asLiveData(): LiveData<T> = LiveDataReactiveStreams.fromPublisher(this.toFlowable())
fun <T> Single<T>.asLiveData(): LiveData<T> = LiveDataReactiveStreams.fromPublisher(this.toFlowable())

fun <T> Observable<T>.asLiveData(): LiveData<T> = LiveDataReactiveStreams.fromPublisher(this.toFlowable(io.reactivex.BackpressureStrategy.BUFFER))