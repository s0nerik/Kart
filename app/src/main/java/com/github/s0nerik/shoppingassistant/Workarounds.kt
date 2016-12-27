package com.github.s0nerik.shoppingassistant

import android.support.v4.widget.NestedScrollView
import android.view.ViewTreeObserver

/**
 * Created by Alex on 12/27/2016.
 * GitHub: https://github.com/s0nerik
 * LinkedIn: https://linkedin.com/in/sonerik
 */
fun NestedScrollView?.applyWrongNestedScrollWorkaround() {
    this?.viewTreeObserver?.addOnPreDrawListener(object: ViewTreeObserver.OnPreDrawListener {
        override fun onPreDraw(): Boolean {
            this@applyWrongNestedScrollWorkaround?.viewTreeObserver?.removeOnPreDrawListener(this)
            this@applyWrongNestedScrollWorkaround?.scrollY = 0
            return true
        }
    })
}