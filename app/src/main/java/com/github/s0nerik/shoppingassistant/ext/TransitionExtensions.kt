package com.github.s0nerik.shoppingassistant.ext

import android.animation.TimeInterpolator
import android.support.annotation.IdRes
import android.transition.Transition
import android.transition.TransitionSet

/**
 * Created by Alex on 3/2/2017.
 * GitHub: https://github.com/s0nerik
 * LinkedIn: https://linkedin.com/in/sonerik
 */

fun transition(@IdRes viewId: Int, transition: Transition, duration: Long, delay: Long = 0, interpolator: TimeInterpolator? = null): Transition {
    val t = transition.setDuration(duration).setStartDelay(delay).addTarget(viewId)
    return if (interpolator != null) t.setInterpolator(interpolator) else t
}

fun transitionSet(vararg transitions: Transition, delay: Long = 0): TransitionSet {
    var t = TransitionSet()
    transitions.forEach { t = t.addTransition(it) }
    return t.setStartDelay(delay)
}