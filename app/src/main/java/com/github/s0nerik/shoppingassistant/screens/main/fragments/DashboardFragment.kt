package com.github.s0nerik.shoppingassistant.screens.main.fragments

import android.animation.Animator
import android.animation.AnimatorSet
import android.os.Bundle
import android.support.v4.view.animation.FastOutSlowInInterpolator
import android.transition.Fade
import android.view.View
import com.bartoszlipinski.viewpropertyobjectanimator.ViewPropertyObjectAnimator
import com.github.s0nerik.shoppingassistant.R
import com.github.s0nerik.shoppingassistant.base.BaseBoundVmFragment
import com.github.s0nerik.shoppingassistant.databinding.FragmentDashboardBinding
import com.github.s0nerik.shoppingassistant.ext.KTransitionSet
import com.github.s0nerik.shoppingassistant.ext.scales
import kotlinx.android.synthetic.main.fragment_dashboard.*
import org.jetbrains.anko.support.v4.dip

/**
 * Created by Alex on 12/25/2016.
 * GitHub: https://github.com/s0nerik
 * LinkedIn: https://linkedin.com/in/sonerik
 */
class DashboardAnimator(val f: DashboardFragment) {
    init {
        f.exitTransition = KTransitionSet.new {
            interpolator(FastOutSlowInInterpolator())
            transition(Fade(Fade.OUT)) { duration(200) }
        }
    }

    fun start() {
        with(f) {
            val cards = arrayOf(moneySpentCard, statsCard, recentsCard)
            val cardDuration = 200
            val cardDelays = arrayOf(0, 300, 500)
            val cardAnimations = cards.mapIndexed { i, v ->
                v.alpha = 0f
                v.translationY = dip(80f).toFloat()

                ViewPropertyObjectAnimator.animate(v)
                        .setDuration(cardDuration.toLong())
                        .setStartDelay(cardDelays[i].toLong())
                        .alpha(1f)
                        .translationY(0f)
                        .setInterpolator(FastOutSlowInInterpolator())
                        .get()
            }

            fab.alpha = 0f
            fab.scales = 0.7f
            val fabAnim = ViewPropertyObjectAnimator.animate(fab)
                    .setDuration(200)
                    .setStartDelay(cardDelays.last() + cardDuration.toLong())
                    .alpha(1f)
                    .scales(1f)
                    .setInterpolator(FastOutSlowInInterpolator())
                    .get()

            val animators = mutableListOf<Animator>()
            animators += fabAnim
            animators += cardAnimations

            val set = AnimatorSet()
            set.playTogether(animators)
            set.start()
        }
    }
}

class DashboardFragment : BaseBoundVmFragment<FragmentDashboardBinding, DashboardViewModel>(
        R.layout.fragment_dashboard, DashboardViewModel::class
) {
    private val animator = DashboardAnimator(this)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        vm.init(getActivityViewModel())
        vm.initRecentsRecycler(recentsRecycler, scrollView)
        vm.initStatsViewPager(childFragmentManager, statsPager, statsPagerTabs)

        animator.start()
    }
}