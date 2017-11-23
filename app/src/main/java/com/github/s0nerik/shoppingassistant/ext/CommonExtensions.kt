package com.github.s0nerik.shoppingassistant.ext

import android.databinding.ObservableArrayList
import android.databinding.ObservableList
import android.view.View
import android.view.ViewTreeObserver
import com.github.s0nerik.shoppingassistant.SUPPORTED_CURRENCIES
import io.realm.RealmList
import io.realm.RealmObject
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

fun <T : RealmObject> realmListOf(collection: Collection<T> = emptyList()): RealmList<T> {
    val list = RealmList<T>()
    list.addAll(collection)
    return list
}

fun <T> List<T>.limit(count: Int): List<T> = this.subList(0, if (count < size) count else size)

fun <T> MutableCollection<T>.replaceWith(iterable: Iterable<T>) {
    clear()
    this += iterable
}

var View.scales: Float
    get() = throw IllegalAccessError()
    set(value) {
        scaleX = value
        scaleY = value
    }

val currenciesSorted: List<Currency>
    get() = SUPPORTED_CURRENCIES
            .toList()
            .sortedBy { it.symbol }
            .sortedBy { it.symbol.length }

fun View.doAfterLayout(removeCondition: () -> Boolean = { true }, block: () -> Unit) {
    viewTreeObserver.addOnPreDrawListener(object : ViewTreeObserver.OnPreDrawListener {
        override fun onPreDraw(): Boolean {
            if (removeCondition())
                viewTreeObserver.removeOnPreDrawListener(this)
            block()
            return false
        }

    })
}