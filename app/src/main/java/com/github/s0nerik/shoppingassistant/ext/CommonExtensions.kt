package com.github.s0nerik.shoppingassistant.ext

import android.databinding.ObservableArrayList
import android.databinding.ObservableList
import java.lang.ref.WeakReference

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