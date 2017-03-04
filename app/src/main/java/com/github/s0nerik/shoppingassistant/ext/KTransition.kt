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
class KTransition private constructor(
        @IdRes val viewIds: List<Int>,
        val innerTransition: Transition,
        val duration: Long?,
        val delay: Long?,
        val interpolator: TimeInterpolator?
) {
    private constructor(builder: Builder) : this(builder.viewIds, builder.transition, builder.duration, builder.delay, builder.interpolator)

    private val transition: Transition
        get () {
            val t = innerTransition.clone()
            duration?.let { t.duration = it }
            delay?.let { t.startDelay = it }
            interpolator?.let { t.interpolator = it }
            viewIds.forEach { t.addTarget(it) }
            return t
        }

    companion object {
        fun new(init: Builder.() -> Unit): Transition = Builder(init).build().transition
    }

    class Builder private constructor() {

        constructor(init: Builder.() -> Unit) : this() {
            init()
        }

        val viewIds = mutableListOf<Int>()
        var viewId: Int = 0
            set(value) {
                viewIds.clear()
                viewIds += value
            }
        lateinit var transition: Transition
        var duration: Long? = null
        var delay: Long? = null
        var interpolator: TimeInterpolator? = null

//        fun viewIds(init: Builder.() -> List<Int>) = apply {
//            viewIds.clear()
//            viewIds += init()
//        }
//        fun viewId(init: Builder.() -> Int) = apply {
//            viewIds.clear()
//            viewIds += init()
//        }
//
//        fun transition(init: Builder.() -> Transition) = apply { transition = init() }
//        fun duration(init: Builder.() -> Long) = apply { duration = init() }
//        fun delay(init: Builder.() -> Long) = apply { delay = init() }
//        fun interpolator(init: Builder.() -> TimeInterpolator) = apply { interpolator = init() }

        fun build() = KTransition(this)
    }
}

class KTransitionSet private constructor(
        @IdRes val viewIds: List<Int>,
        val innerTransitions: List<Transition>,
        val duration: Long?,
        val delay: Long?,
        val interpolator: TimeInterpolator?,
        val ordering: Ordering?
) {
    enum class Ordering { SEQUENTIAL, TOGETHER }

    private constructor(builder: Builder) : this(builder.viewIds, builder.transitions, builder.duration, builder.delay, builder.interpolator, builder.ordering)

    private val transition: TransitionSet
        get () {
            val t = TransitionSet()
            innerTransitions.forEach { t.addTransition(it) }
            duration?.let { t.duration = it }
            delay?.let { t.startDelay = it }
            interpolator?.let { t.interpolator = it }
            viewIds.forEach { t.addTarget(it) }
            ordering?.let {
                t.ordering = when (it) {
                    KTransitionSet.Ordering.SEQUENTIAL -> TransitionSet.ORDERING_SEQUENTIAL
                    KTransitionSet.Ordering.TOGETHER -> TransitionSet.ORDERING_TOGETHER
                }
            }
            return t
        }

    companion object {
        fun new(init: Builder.() -> Unit): TransitionSet = Builder(init).build().transition
    }

    class Builder private constructor() {

        constructor(init: Builder.() -> Unit) : this() {
            init()
        }

        val viewIds = mutableListOf<Int>()
        var viewId: Int = 0
            set(value) {
                viewIds.clear()
                viewIds += value
            }
        val transitions = mutableListOf<Transition>()
        var duration: Long? = null
        var delay: Long? = null
        var interpolator: TimeInterpolator? = null
        var ordering: Ordering? = null

        fun build() = KTransitionSet(this)
    }
}