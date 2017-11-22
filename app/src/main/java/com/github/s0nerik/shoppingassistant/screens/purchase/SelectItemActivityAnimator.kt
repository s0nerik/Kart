package com.github.s0nerik.shoppingassistant.screens.purchase

import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.animation.AnimatorSet
import android.support.v4.view.animation.FastOutSlowInInterpolator
import android.view.animation.DecelerateInterpolator
import com.bartoszlipinski.viewpropertyobjectanimator.ViewPropertyObjectAnimator
import com.github.s0nerik.shoppingassistant.applyWrongNestedScrollWorkaround
import com.github.s0nerik.shoppingassistant.databinding.ActivitySelectItemBinding
import kotlinx.android.synthetic.main.activity_select_item.*
import org.jetbrains.anko.dip

class SelectItemActivityAnimator(val a: SelectItemActivity, val binding: ActivitySelectItemBinding) {
    fun appear(callback: (() -> Unit)? = null) {
        animate(true, callback)
    }

    fun disappear(callback: (() -> Unit)? = null) {
        animate(false, callback)
    }

    private fun animate(appear: Boolean, callback: (() -> Unit)?) {
        with(a) {
            var views = listOf(bg, searchCard, btnCreateNewProduct, favoritesCard, frequentsCard)

            if (appear) {
                scrollView.applyWrongNestedScrollWorkaround()
                views.subList(1, views.size).forEach {
                    it.translationY = dip(80).toFloat()
                    it.alpha = 0f
                }
            } else {
                views = views.reversed()
            }

            val durations = arrayOf(500, 200, 200, 200, 200)
            val delays = if (binding.vm!!.favorites.isNotEmpty()) {
                arrayOf(0, 0, 200, 400, 600)
            } else {
                arrayOf(0, 0, 200, 400, 400)
            }

            val interpolator = if (appear) FastOutSlowInInterpolator() else DecelerateInterpolator(2f)

            val anim = AnimatorSet()
            anim.playTogether(
                    views.mapIndexed { i, view ->
                        ViewPropertyObjectAnimator.animate(view)
                                .alpha(if (appear) 1f else 0f)
                                .translationY(if (appear) 0f else dip(80).toFloat())
                                .setDuration(durations[i].toLong())
                                .setStartDelay(delays[i].toLong())
                                .setInterpolator(interpolator)
                                .get()
                    }
            )
            callback?.let {
                anim.addListener(object : AnimatorListenerAdapter() {
                    override fun onAnimationCancel(animation: Animator?) = it()
                    override fun onAnimationEnd(animation: Animator?) = it()
                })
            }
            anim.start()
        }
    }
}