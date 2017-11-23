package com.github.s0nerik.shoppingassistant.base

import android.arch.lifecycle.LiveData
import android.arch.lifecycle.ViewModel
import android.databinding.Bindable
import android.databinding.Observable.OnPropertyChangedCallback
import android.databinding.ObservableField
import android.databinding.PropertyChangeRegistry
import com.github.s0nerik.shoppingassistant.ext.asLiveData
import io.reactivex.Flowable
import io.reactivex.Maybe
import io.reactivex.Observable
import io.reactivex.Single
import io.reactivex.disposables.Disposable
import io.reactivex.processors.PublishProcessor
import io.reactivex.subjects.PublishSubject
import java.util.concurrent.CancellationException


/**
 * Created by Alex Isaienko on 10/16/17.
 * GitHub: https://github.com/s0nerik
 * LinkedIn: https://linkedin.com/in/sonerik
 */
open class BaseViewModel : ViewModel(), android.databinding.Observable {
    @Transient private var callbacks: PropertyChangeRegistry? = null

    private val clearedProcessor = PublishProcessor.create<Unit>()
    private val clearedSubject = PublishSubject.create<Unit>()

    override fun onCleared() {
        super.onCleared()
        clearedProcessor.onNext(Unit)
        clearedSubject.onNext(Unit)
    }

    //region takeUntilCleared implementations
    fun <T> Flowable<T>.takeUntilCleared(): Flowable<T> = takeUntil(clearedProcessor)
            .onErrorResumeNext { t: Throwable -> if (t is CancellationException) Flowable.empty() else Flowable.error(t) }

    fun <T> Observable<T>.takeUntilCleared(): Observable<T> = takeUntil(clearedSubject)
            .onErrorResumeNext { t: Throwable -> if (t is CancellationException) Observable.empty() else Observable.error(t) }

    fun <T> Maybe<T>.takeUntilCleared(): Maybe<T> = takeUntil(clearedProcessor)
            .onErrorResumeNext { t: Throwable -> if (t is CancellationException) Maybe.empty() else Maybe.error(t) }

    fun <T> Single<T>.takeUntilCleared(): Maybe<T> = toMaybe().takeUntil(clearedProcessor)
            .onErrorResumeNext { t: Throwable -> if (t is CancellationException) Maybe.empty() else Maybe.error(t) }
    //endregion

    //region subscribeUntilCleared implementations
    fun <T> Flowable<T>.subscribeUntilCleared(
            onNext: (T) -> Unit,
            onError: (Throwable) -> Unit = {},
            onComplete: () -> Unit = {}
    ): Disposable = takeUntilCleared().subscribe(onNext, onError, onComplete)

    fun <T> Flowable<T>.subscribeUntilCleared(
            onNext: (T) -> Unit
    ): Disposable = takeUntilCleared().subscribe(onNext, {}, {})

    fun <T> Observable<T>.subscribeUntilCleared(
            onNext: (T) -> Unit,
            onError: (Throwable) -> Unit = {},
            onComplete: () -> Unit = {}
    ): Disposable = takeUntilCleared().subscribe(onNext, onError, onComplete)

    fun <T> Observable<T>.subscribeUntilCleared(
            onNext: (T) -> Unit
    ): Disposable = takeUntilCleared().subscribe(onNext, {}, {})

    fun <T> Maybe<T>.subscribeUntilCleared(
            onSuccess: (T) -> Unit,
            onError: (Throwable) -> Unit = {},
            onComplete: () -> Unit = {}
    ): Disposable = takeUntilCleared().subscribe(onSuccess, onError, onComplete)

    fun <T> Maybe<T>.subscribeUntilCleared(
            onSuccess: (T) -> Unit
    ): Disposable = takeUntilCleared().subscribe(onSuccess, {}, {})

    fun <T> Single<T>.subscribeUntilCleared(
            onSuccess: (T) -> Unit,
            onError: (Throwable) -> Unit = {},
            onComplete: () -> Unit = {}
    ): Disposable = takeUntilCleared().subscribe(onSuccess, onError, onComplete)

    fun <T> Single<T>.subscribeUntilCleared(
            onSuccess: (T) -> Unit
    ): Disposable = takeUntilCleared().subscribe(onSuccess, {}, {})
    //endregion

    fun <T> ObservableField<T>.asLiveData(): LiveData<T> {
        return Observable.create<T> { emitter ->
            val callback = object : OnPropertyChangedCallback() {
                override fun onPropertyChanged(dataBindingObservable: android.databinding.Observable, propertyId: Int) {
                    if (dataBindingObservable === this@asLiveData) {
                        emitter.onNext(this@asLiveData.get())
                    }
                }
            }

            this@asLiveData.addOnPropertyChangedCallback(callback)
            emitter.setCancellable { this@asLiveData.removeOnPropertyChangedCallback(callback) }
        }.takeUntilCleared().asLiveData()
    }

    fun <T : android.databinding.Observable> T.observePropertyChanges(bindablePropertyId: Int): Observable<T> {
        return Observable.create<T> { emitter ->
            val callback = object : OnPropertyChangedCallback() {
                override fun onPropertyChanged(dataBindingObservable: android.databinding.Observable, propertyId: Int) {
                    if (dataBindingObservable === this@observePropertyChanges && propertyId == bindablePropertyId) {
                        emitter.onNext(this@observePropertyChanges)
                    }
                }
            }

            this@observePropertyChanges.addOnPropertyChangedCallback(callback)
            emitter.setCancellable { this@observePropertyChanges.removeOnPropertyChangedCallback(callback) }
        }.takeUntilCleared()
    }

    override fun addOnPropertyChangedCallback(callback: OnPropertyChangedCallback) {
        synchronized(this) {
            if (callbacks == null) {
                callbacks = PropertyChangeRegistry()
            }
        }
        callbacks!!.add(callback)
    }

    override fun removeOnPropertyChangedCallback(callback: OnPropertyChangedCallback) {
        synchronized(this) {
            if (callbacks == null) {
                return
            }
        }
        callbacks!!.remove(callback)
    }

    /**
     * Notifies listeners that all properties of this instance have changed.
     */
    fun notifyChange() {
        synchronized(this) {
            if (callbacks == null) {
                return
            }
        }
        callbacks!!.notifyCallbacks(this, 0, null)
    }

    /**
     * Notifies listeners that a specific property has changed. The getter for the property
     * that changes should be marked with [Bindable] to generate a field in
     * `BR` to be used as `fieldId`.
     *
     * @param fieldId The generated BR id for the Bindable field.
     */
    fun notifyPropertyChanged(fieldId: Int) {
        synchronized(this) {
            if (callbacks == null) {
                return
            }
        }
        callbacks!!.notifyCallbacks(this, fieldId, null)
    }
}