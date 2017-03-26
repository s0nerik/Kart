package com.github.s0nerik.shoppingassistant.screens.main

import android.animation.Animator
import android.animation.AnimatorSet
import android.os.Bundle
import android.support.v4.app.FragmentPagerAdapter
import android.support.v4.view.PagerAdapter
import android.support.v4.view.animation.FastOutSlowInInterpolator
import android.transition.Fade
import android.view.View
import com.bartoszlipinski.viewpropertyobjectanimator.ViewPropertyObjectAnimator
import com.github.nitrico.lastadapter.LastAdapter
import com.github.nitrico.lastadapter.Type
import com.github.s0nerik.shoppingassistant.BR
import com.github.s0nerik.shoppingassistant.R
import com.github.s0nerik.shoppingassistant.applyWrongNestedScrollWorkaround
import com.github.s0nerik.shoppingassistant.base.BaseBoundFragment
import com.github.s0nerik.shoppingassistant.databinding.FragmentDashboardBinding
import com.github.s0nerik.shoppingassistant.databinding.ItemPurchaseBinding
import com.github.s0nerik.shoppingassistant.ext.KTransitionSet
import com.github.s0nerik.shoppingassistant.ext.scales
import com.github.s0nerik.shoppingassistant.recentPurchases
import com.github.s0nerik.shoppingassistant.screens.main.dashboard.StatsDistributionFragment
import com.github.s0nerik.shoppingassistant.screens.main.dashboard.StatsExpensesFragment
import com.github.s0nerik.shoppingassistant.screens.purchase.CreatePurchaseActivity
import kotlinx.android.synthetic.main.fragment_dashboard.*
import org.jetbrains.anko.support.v4.dip
import org.jetbrains.anko.support.v4.startActivity

/**
 * Created by Alex on 12/25/2016.
 * GitHub: https://github.com/s0nerik
 * LinkedIn: https://linkedin.com/in/sonerik
 */
class DashboardViewModel(val fragment: DashboardFragment) {
    fun onCreateNewPurchase() {
        fragment.startActivity<CreatePurchaseActivity>()
    }
}

class DashboardAnimator(val f: DashboardFragment) {
    init {
        f.exitTransition = KTransitionSet.new {
            interpolator(FastOutSlowInInterpolator())
            transition(Fade(Fade.OUT)) { duration(200) }
        }
    }

    fun start() {
        with(f) {
            val cards = arrayOf(statsCard, recentsCard)
            val cardDuration = arrayOf(200, 200)
            val cardDelay = arrayOf(0, 400)
            val cardAnimations = cards.mapIndexed { i, v ->
                v.alpha = 0f
                v.translationY = dip(80f).toFloat()

                ViewPropertyObjectAnimator.animate(v)
                        .setDuration(cardDuration[i].toLong())
                        .setStartDelay(cardDelay[i].toLong())
                        .alpha(1f)
                        .translationY(0f)
                        .setInterpolator(FastOutSlowInInterpolator())
                        .get()
            }

            fab.alpha = 0f
            fab.scales = 0.7f
            val fabAnim = ViewPropertyObjectAnimator.animate(fab)
                    .setDuration(200)
                    .setStartDelay(500)
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

class DashboardFragment : BaseBoundFragment<FragmentDashboardBinding>(R.layout.fragment_dashboard) {
    private val recentPurchases
        get() = recentPurchases(realm)

    private val statsAdapter: PagerAdapter
        get() = object : FragmentPagerAdapter(childFragmentManager) {
            val fragments = listOf(
                    StatsDistributionFragment(),
                    StatsExpensesFragment()
            )

            val names = listOf("Purchases distribution", "Expenses")

            override fun getItem(position: Int) = fragments[position]
            override fun getCount() = fragments.size
            override fun getPageTitle(position: Int) = names[position]
        }

    private val animator = DashboardAnimator(this)

    override fun onViewCreated(view: View?, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.vm = DashboardViewModel(this)

        initRecents()
        initStats()

        animator.start()
    }

    private fun initRecents() {
        LastAdapter.with(recentPurchases, BR.item)
                .type { Type<ItemPurchaseBinding>(R.layout.item_purchase) }
                .into(recentsRecycler)

        recentsRecycler.isNestedScrollingEnabled = false
        recentsRecycler.setHasFixedSize(true)

        scrollView.applyWrongNestedScrollWorkaround()
    }

    private fun initStats() {
        statsPager.adapter = statsAdapter
        statsPagerTabs.setupWithViewPager(statsPager)
    }
}