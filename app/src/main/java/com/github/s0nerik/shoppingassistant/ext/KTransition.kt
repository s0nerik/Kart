package com.github.s0nerik.shoppingassistant.ext

import android.animation.TimeInterpolator
import android.transition.Transition
import android.transition.TransitionSet
import android.view.View

/**
 * Created by Alex on 3/2/2017.
 * GitHub: https://github.com/s0nerik
 * LinkedIn: https://linkedin.com/in/sonerik
 */

@DslMarker
annotation class TransitionMarker

@TransitionMarker
abstract class KBaseTransition<out T : Transition>(
        protected val viewIds: MutableSet<Int> = mutableSetOf(),
        protected var duration: Long? = null,
        protected var delay: Long? = null,
        protected var interpolator: TimeInterpolator? = null
) {
    protected val transition: T
        get () {
            val t = provideTransition()
            duration?.let { t.duration = it }
            delay?.let { t.startDelay = it }
            interpolator?.let { t.interpolator = it }
            viewIds.forEach { t.addTarget(it) }
            return t
        }

    fun duration(d: Long) {
        duration = d
    }

    fun delay(d: Long) {
        delay = d
    }

    fun interpolator(i: TimeInterpolator) {
        interpolator = i
    }

    fun view(id: Int) = views(id)
    fun views(vararg ids: Int) {
        ids.forEach { viewIds += it }
    }

    fun view(view: View) = views(view)
    fun views(vararg views: View) {
        views.forEach { viewIds += it.id }
    }

    protected abstract fun provideTransition(): T
}

class KTransition private constructor(
        val innerTransition: Transition,
        builder: KTransition.() -> Unit
) : KBaseTransition<Transition>() {

    init {
        @Suppress("UNUSED_EXPRESSION")
        builder()
    }

    override fun provideTransition() = innerTransition.clone()

    companion object {
        fun new(transition: Transition, init: KTransition.() -> Unit = {}): Transition = KTransition(transition, init).transition
    }
}

class KTransitionSet private constructor() : KBaseTransition<TransitionSet>() {
    enum class Ordering { SEQUENTIAL, TOGETHER }

    private constructor(builder: KTransitionSet.() -> Unit) : this() {
        @Suppress("UNUSED_EXPRESSION")
        builder()
    }

    private val innerTransitions = mutableSetOf<Transition>()
    private var ordering: Ordering? = null

    fun ordering(o: Ordering) {
        ordering = o
    }

    fun transition(transition: Transition, init: KTransition.() -> Unit = {}) {
        innerTransitions += KTransition.new(transition, init)
    }

    fun transitions(vararg transitions: Transition) {
        innerTransitions += transitions
    }

    fun transitionSet(init: KTransitionSet.() -> Unit) {
        innerTransitions += KTransitionSet.new(init)
    }

    override fun provideTransition(): TransitionSet {
        val t = TransitionSet()
        innerTransitions.forEach { t.addTransition(it) }
        ordering?.let {
            t.ordering = when (it) {
                KTransitionSet.Ordering.SEQUENTIAL -> TransitionSet.ORDERING_SEQUENTIAL
                KTransitionSet.Ordering.TOGETHER -> TransitionSet.ORDERING_TOGETHER
            }
        }
        return t
    }

    companion object {
        fun new(init: KTransitionSet.() -> Unit): TransitionSet = KTransitionSet(init).transition
    }
}