package com.github.s0nerik.shoppingassistant.ext

import android.databinding.BindingAdapter
import android.view.View
import com.daimajia.swipe.SimpleSwipeListener
import com.daimajia.swipe.SwipeLayout
import org.jetbrains.anko.dip
import java.util.*

interface SwipeListener {
    fun run(swipeLayout: SwipeLayout)
}

private val swipeListeners = WeakHashMap<SwipeListener, SwipeLayout.SwipeListener>()
private val elevationListeners = WeakHashMap<View, SwipeLayout.SwipeListener>()

@BindingAdapter("onSwipeOpenLeft")
fun onSwipeOpenLeft(
        swipeLayout: SwipeLayout,
        oldListener: SwipeListener?,
        newListener: SwipeListener
) {
    addSwipeListener(swipeLayout, oldListener, newListener, object : SimpleSwipeListener() {
        override fun onOpen(layout: SwipeLayout) {
            if (layout.dragEdge == SwipeLayout.DragEdge.Left)
                newListener.run(layout)
        }
    })
}

@BindingAdapter("onSwipeOpenRight")
fun onSwipeOpenRight(
        swipeLayout: SwipeLayout,
        oldListener: SwipeListener?,
        newListener: SwipeListener
) {
    addSwipeListener(swipeLayout, oldListener, newListener, object : SimpleSwipeListener() {
        override fun onOpen(layout: SwipeLayout) {
            if (layout.dragEdge == SwipeLayout.DragEdge.Right)
                newListener.run(layout)
        }
    })
}

@BindingAdapter("onSwipeOpen")
fun onSwipeOpen(
        swipeLayout: SwipeLayout,
        oldListener: SwipeListener?,
        newListener: SwipeListener
) {
    addSwipeListener(swipeLayout, oldListener, newListener, object : SimpleSwipeListener() {
        override fun onOpen(layout: SwipeLayout) {
            newListener.run(layout)
        }
    })
}

@BindingAdapter("elevateViewOnSwipe")
fun elevateViewOnSwipe(swipeLayout: SwipeLayout, oldView: View?, newView: View) {
    swipeLayout.removeSwipeListener(elevationListeners[oldView])
    elevationListeners.remove(oldView)

    elevationListeners[newView] = object : SimpleSwipeListener() {
        var startElevation: Float = 0f

        override fun onStartOpen(layout: SwipeLayout) {
            startElevation = newView.elevation
            newView.elevation = startElevation + newView.dip(1)
        }

        override fun onOpen(layout: SwipeLayout) {
            newView.elevation = startElevation
        }

        override fun onClose(layout: SwipeLayout) {
            newView.elevation = startElevation
        }
    }
    swipeLayout.addSwipeListener(elevationListeners[newView])
}

private fun addSwipeListener(
        swipeLayout: SwipeLayout,
        oldListener: SwipeListener?,
        newListener: SwipeListener,
        wrappedListener: SwipeLayout.SwipeListener
) {
    swipeLayout.removeSwipeListener(swipeListeners[oldListener])
    swipeListeners.remove(oldListener)

    swipeListeners[newListener] = wrappedListener
    swipeLayout.addSwipeListener(wrappedListener)
}