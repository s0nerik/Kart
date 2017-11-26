package com.github.s0nerik.shoppingassistant.databinding

import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.databinding.BindingAdapter
import android.view.View
import com.appolica.flubber.Flubber
import java.util.*


/**
 * Created by Alex Isaienko on 11/26/2017.
 * GitHub: https://github.com/s0nerik
 * LinkedIn: https://linkedin.com/in/sonerik
 */
private val animators = WeakHashMap<View, Animator>()

@BindingAdapter("animatedVisibilityAppearBottom")
fun setVisibility(view: View, oldVisibility: Int, visibility: Int) {
    if (oldVisibility != visibility) {
        animators[view]?.cancel()

        view.visibility = View.VISIBLE

        val builder = if (visibility == View.VISIBLE) {
            Flubber.with().animation(Flubber.AnimationPreset.FADE_IN_UP)
                    .interpolator(Flubber.Curve.SPRING)
                    .force(1f)
                    .velocity(0.5f)
                    .duration(500)
        } else {
            Flubber.with().animation(Flubber.AnimationPreset.FADE_OUT)
                    .duration(200)
        }

        val animator = builder
                .listener(object : AnimatorListenerAdapter() {
                    private var isCanceled: Boolean = false

                    override fun onAnimationCancel(animation: Animator?) {
                        isCanceled = true
                    }

                    override fun onAnimationEnd(animation: Animator?) {
                        if (!isCanceled) {
                            view.visibility = visibility
                        }
                    }
                })
                .createFor(view)

        animators[view] = animator
        animator.start()
    }
}