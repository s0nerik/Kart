package com.github.s0nerik.shoppingassistant.ext

import android.databinding.ObservableArrayList
import android.databinding.ObservableList
import android.view.View
import java.lang.ref.WeakReference
import java.util.*

/**
 * Created by Alex on 1/4/2017.
 * GitHub: https://github.com/s0nerik
 * LinkedIn: https://linkedin.com/in/sonerik
 */

fun <T> WeakReference<T>.safe(action: T.() -> Unit) {
    this.get()?.action()
}

fun <T> observableListOf(collection: Collection<T> = emptyList()): ObservableList<T> {
    val list = ObservableArrayList<T>()
    list.addAll(collection)
    return list
}

var View.scales: Float
    get() = throw IllegalAccessError()
    set(value) {
        scaleX = value
        scaleY = value
    }

val currenciesSorted: List<Currency>
    get() = Currency.getAvailableCurrencies()
            .toList()
            .sortedBy { it.symbol }
            .sortedBy { it.symbol.length }