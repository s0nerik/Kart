package com.github.s0nerik.shoppingassistant.screens.main

import android.os.Bundle
import android.support.v4.view.animation.FastOutSlowInInterpolator
import android.transition.Fade
import android.transition.Slide
import android.view.Gravity
import android.view.View
import com.github.nitrico.lastadapter.LastAdapter
import com.github.nitrico.lastadapter.Type
import com.github.s0nerik.shoppingassistant.BR
import com.github.s0nerik.shoppingassistant.R
import com.github.s0nerik.shoppingassistant.anim.Scale
import com.github.s0nerik.shoppingassistant.applyWrongNestedScrollWorkaround
import com.github.s0nerik.shoppingassistant.base.BaseBoundFragment
import com.github.s0nerik.shoppingassistant.databinding.FragmentDashboardBinding
import com.github.s0nerik.shoppingassistant.databinding.ItemPurchaseBinding
import com.github.s0nerik.shoppingassistant.ext.KTransitionSet
import com.github.s0nerik.shoppingassistant.recentPurchases
import com.github.s0nerik.shoppingassistant.screens.main.dashboard.StatsDistributionFragment
import com.github.s0nerik.shoppingassistant.screens.main.dashboard.StatsExpensesFragment
import com.github.s0nerik.shoppingassistant.screens.purchase.CreatePurchaseActivity
import kotlinx.android.synthetic.main.fragment_dashboard.*
import org.jetbrains.anko.support.v4.startActivity
import xyz.santeri.wvp.WrappingFragmentStatePagerAdapter

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

class DashboardFragment : BaseBoundFragment<FragmentDashboardBinding>(R.layout.fragment_dashboard) {
    val recentPurchases
        get() = recentPurchases(realm)

    init {
        enterTransition = KTransitionSet.new {
            ordering(KTransitionSet.Ordering.SEQUENTIAL)
            interpolator(FastOutSlowInInterpolator())
            transition(Fade(Fade.OUT)) {
                duration(0)
                views(R.id.fab, R.id.recentsCard, R.id.statsCard)
            }
            transitionSet {
                ordering(KTransitionSet.Ordering.TOGETHER)
                transitionSet {
                    view(R.id.statsCard)
                    duration(200)
                    transition(Slide(Gravity.BOTTOM))
                    transition(Fade(Fade.IN))
                }
                transitionSet {
                    view(R.id.recentsCard)
                    duration(200)
                    delay(100)
                    transition(Slide(Gravity.BOTTOM))
                    transition(Fade(Fade.IN))
                }
                transitionSet {
                    view(R.id.fab)
                    duration(200)
                    delay(300)
                    transition(Scale(0.7f))
                    transition(Fade(Fade.IN))
                }
            }
        }

        exitTransition = KTransitionSet.new {
            interpolator(FastOutSlowInInterpolator())
            transition(Fade(Fade.OUT)) { duration(200) }
        }
    }

    override fun onViewCreated(view: View?, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.vm = DashboardViewModel(this)

        initRecents()
        initStats()
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
        statsPager.adapter = object : WrappingFragmentStatePagerAdapter(childFragmentManager){
            val fragments = listOf(
                    StatsDistributionFragment(),
                    StatsExpensesFragment()
            )

            val names = listOf("Purchases distribution", "Expenses")

            override fun getItem(position: Int) = fragments[position]
            override fun getCount() = fragments.size
            override fun getPageTitle(position: Int) = names[position]
        }
        statsPagerTabs.setupWithViewPager(statsPager)
    }
}