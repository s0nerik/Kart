package com.github.s0nerik.shoppingassistant.screens.main.cart

import android.support.v4.view.animation.FastOutSlowInInterpolator
import android.transition.Fade
import android.transition.Slide
import android.view.Gravity
import com.github.s0nerik.shoppingassistant.R
import com.github.s0nerik.shoppingassistant.anim.Scale
import com.github.s0nerik.shoppingassistant.ext.KTransition
import com.github.s0nerik.shoppingassistant.ext.KTransitionSet
import com.github.s0nerik.shoppingassistant.model.Cart

/**
 * Created by Alex Isaienko on 11/23/2017.
 * GitHub: https://github.com/s0nerik
 * LinkedIn: https://linkedin.com/in/sonerik
 */
object CartAnimator {
    fun attach(f: CartFragment) {
        with(f) {
            enterTransition = KTransitionSet.new {
                ordering(KTransitionSet.Ordering.SEQUENTIAL)
                transition(Fade(Fade.OUT)) { duration(0); views(R.id.fab, R.id.recycler, R.id.emptyCart, R.id.bottomButtons) }
                transitionSet {
                    ordering(KTransitionSet.Ordering.TOGETHER)
                    transitionSet {
                        view(R.id.recycler)
                        duration(200)
                        interpolator(FastOutSlowInInterpolator())
                        transition(Slide(Gravity.BOTTOM))
                        transition(Fade(Fade.IN))
                    }
                    if (Cart.isEmpty()) {
                        transition(Fade(Fade.IN)) { view(R.id.emptyCart); duration(200) }
                    } else {
                        transitionSet {
                            view(R.id.bottomButtons)
                            duration(200)
                            delay(100)
                            interpolator(FastOutSlowInInterpolator())
                            transition(Slide(Gravity.BOTTOM))
                            transition(Fade(Fade.IN))
                        }
                    }
                    transitionSet {
                        view(R.id.fab)
                        duration(200)
                        delay(if (Cart.isEmpty()) 100 else 300)
                        interpolator(FastOutSlowInInterpolator())
                        transition(Scale(0.7f))
                        transition(Fade(Fade.IN))
                        transition(Slide(Gravity.BOTTOM))
                    }
                }
            }

            exitTransition = KTransition.new(Fade(Fade.OUT)) { duration(200) }
        }
    }
}