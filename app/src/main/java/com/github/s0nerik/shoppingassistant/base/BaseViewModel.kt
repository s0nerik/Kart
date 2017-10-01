package com.github.s0nerik.shoppingassistant.base

import android.arch.lifecycle.LiveData
import android.arch.lifecycle.ViewModel
import android.databinding.Bindable
import android.databinding.ObservableField
import android.databinding.PropertyChangeRegistry
import com.github.s0nerik.shoppingassistant.ext.asLiveData
import io.reactivex.Flowable
import io.reactivex.Maybe
import io.reactivex.Observable
import io.reactivex.Single
import io.reactivex.processors.PublishProcessor
import io.reactivex.subjects.PublishSubject


/**
 * Created by Alex Isaienko on 10/8/17.
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

    fun <T> Flowable<T>.takeUntilCleared(): Flowable<T> = this.takeUntil(clearedProcessor)
    fun <T> Maybe<T>.takeUntilCleared(): Maybe<T> = this.takeUntil(clearedProcessor)
    fun <T> Single<T>.takeUntilCleared(): Single<T> = this.takeUntil(clearedProcessor)

    fun <T> Observable<T>.takeUntilCleared(): Observable<T> = this.takeUntil(clearedSubject)

    fun <T> ObservableField<T>.asLiveData(): LiveData<T> {
        return Observable.create<T> { emitter ->
            val callback = object : android.databinding.Observable.OnPropertyChangedCallback() {
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

    override fun addOnPropertyChangedCallback(callback: android.databinding.Observable.OnPropertyChangedCallback) {
        synchronized(this) {
            if (callbacks == null) {
                callbacks = PropertyChangeRegistry()
            }
        }
        callbacks!!.add(callback)
    }

    override fun removeOnPropertyChangedCallback(callback: android.databinding.Observable.OnPropertyChangedCallback) {
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