package com.github.s0nerik.shoppingassistant.screens.main

import android.os.Bundle
import android.support.v4.content.ContextCompat
import android.support.v4.view.animation.FastOutSlowInInterpolator
import android.transition.Fade
import android.transition.Slide
import android.view.Gravity
import android.view.View
import com.github.mikephil.charting.components.Legend
import com.github.mikephil.charting.data.PieData
import com.github.mikephil.charting.data.PieDataSet
import com.github.mikephil.charting.data.PieEntry
import com.github.nitrico.lastadapter.LastAdapter
import com.github.nitrico.lastadapter.Type
import com.github.s0nerik.shoppingassistant.*
import com.github.s0nerik.shoppingassistant.anim.Scale
import com.github.s0nerik.shoppingassistant.base.BaseBoundFragment
import com.github.s0nerik.shoppingassistant.databinding.FragmentDashboardBinding
import com.github.s0nerik.shoppingassistant.databinding.ItemPurchaseBinding
import com.github.s0nerik.shoppingassistant.ext.KTransitionSet
import com.github.s0nerik.shoppingassistant.screens.purchase.CreatePurchaseActivity
import kotlinx.android.synthetic.main.fragment_dashboard.*
import org.jetbrains.anko.support.v4.act
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

class DashboardFragment : BaseBoundFragment<FragmentDashboardBinding>(R.layout.fragment_dashboard) {
    val purchases
        get() = purchases(realm)

    val recentPurchases
        get() = recentPurchases(realm)

    init {
        val statsFadeIn = KTransitionSet.new { view(R.id.statsCard); duration = 200
            transition(Slide(Gravity.BOTTOM))
            transition(Fade(Fade.IN))
        }

        val recentsFadeIn = KTransitionSet.new { view(R.id.recentsCard); duration = 200; delay = 100
            transition(Slide(Gravity.BOTTOM))
            transition(Fade(Fade.IN))
        }

        val fabFadeIn = KTransitionSet.new { view(R.id.fab); duration = 200; delay = 300
            transition(Scale(0.7f))
            transition(Fade(Fade.IN))
        }

        val enterSet = KTransitionSet.new { interpolator = FastOutSlowInInterpolator()
            transitions(statsFadeIn, recentsFadeIn, fabFadeIn)
        }

        val finalEnterSet = KTransitionSet.new { ordering = KTransitionSet.Ordering.SEQUENTIAL
            transition(Fade(Fade.OUT)) { duration = 0; views(R.id.fab, R.id.recentsCard, R.id.statsCard) }
            transitions(enterSet)
        }

        enterTransition = finalEnterSet
    }

    override fun onViewCreated(view: View?, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.vm = DashboardViewModel(this)

        initRecents()
        initDistributionChart()
    }

    private fun initRecents() {
        LastAdapter.with(recentPurchases, BR.item)
                .type { Type<ItemPurchaseBinding>(R.layout.item_purchase) }
                .into(recentsRecycler)

        recentsRecycler.isNestedScrollingEnabled = false
        recentsRecycler.setHasFixedSize(true)

        scrollView.applyWrongNestedScrollWorkaround()
    }

    private fun initDistributionChart() {
        val entries = purchases.groupBy { it.item?.category }.map { PieEntry(it.value.size.toFloat(), it.key?.name) }

        val dataSet = PieDataSet(entries, null)
        dataSet.apply {
            setColors(intArrayOf(R.color.colorPrimary, R.color.colorPrimaryDark, R.color.colorAccent), act)
            sliceSpace = 4f
            valueTextColor = ContextCompat.getColor(act, R.color.material_color_white)
            valueTextSize = 24f
        }

        distributionChart.apply {
            data = PieData(dataSet)
            description = null
            holeRadius = 5f
            transparentCircleRadius = 10f

            legend.apply {
                isEnabled = true
                orientation = Legend.LegendOrientation.VERTICAL
                direction = Legend.LegendDirection.LEFT_TO_RIGHT
                horizontalAlignment = Legend.LegendHorizontalAlignment.RIGHT
                verticalAlignment = Legend.LegendVerticalAlignment.CENTER
                textSize = 16f
                setDrawInside(false)
            }
        }
    }
}