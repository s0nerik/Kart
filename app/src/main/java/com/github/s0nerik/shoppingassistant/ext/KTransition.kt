package com.github.s0nerik.shoppingassistant.ext

import android.animation.TimeInterpolator
import android.support.annotation.IdRes
import android.transition.Transition
import android.transition.TransitionSet
import android.view.View

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
        fun new(transition: Transition, init: Builder.() -> Unit): Transition = Builder(transition, init).build().transition
    }

    class Builder private constructor(val transition: Transition) {

        constructor(transition: Transition, init: Builder.() -> Unit) : this(transition) {
            init()
        }

        val viewIds = mutableListOf<Int>()
        var duration: Long? = null
        var delay: Long? = null
        var interpolator: TimeInterpolator? = null

        fun views(vararg ids: Int) {
            ids.forEach { viewIds += it }
        }

        fun views(vararg views: View) {
            views.forEach { viewIds += it.id }
        }

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
        val transitions = mutableListOf<Transition>()
        var duration: Long? = null
        var delay: Long? = null
        var interpolator: TimeInterpolator? = null
        var ordering: Ordering? = null

        fun view(id: Int) = views(id)
        fun views(vararg ids: Int) {
            ids.forEach { viewIds += it }
        }

        fun view(view: View) = views(view)
        fun views(vararg views: View) {
            views.forEach { viewIds += it.id }
        }

        fun transition(transition: Transition, init: KTransition.Builder.() -> Unit = {}) {
            transitions += KTransition.new(transition, init)
        }

        fun transitions(vararg t: Transition) {
            transitions += t
        }

        fun build() = KTransitionSet(this)
    }
}