package com.github.s0nerik.shoppingassistant.screens.main

import android.os.Bundle
import android.support.v4.content.ContextCompat
import android.support.v4.view.animation.FastOutSlowInInterpolator
import android.transition.Fade
import android.transition.Slide
import android.transition.TransitionManager
import android.transition.TransitionSet
import android.view.Gravity
import android.view.View
import co.metalab.asyncawait.async
import co.metalab.asyncawait.await
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
import com.github.s0nerik.shoppingassistant.screens.purchase.CreatePurchaseActivity
import com.jakewharton.rxbinding.view.preDraws
import kotlinx.android.synthetic.main.fragment_dashboard.*
import org.jetbrains.anko.support.v4.act
import org.jetbrains.anko.support.v4.startActivity
import rx.android.schedulers.AndroidSchedulers
import rx.functions.Func0

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

    override fun onViewCreated(view: View?, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.vm = DashboardViewModel(this)

        initRecents()
        initDistributionChart()
    }

    override fun onResume() {
        super.onResume()
        animateAppear()
    }

    private fun animateAppear() {
        async {
            statsCard.visibility = View.INVISIBLE
            recentsCard.visibility = View.INVISIBLE
            fab.visibility = View.INVISIBLE

            await(view!!.preDraws(Func0 { true }).subscribeOn(AndroidSchedulers.mainThread()))

            scrollView.applyWrongNestedScrollWorkaround()

            val fabSet = TransitionSet()
                    .addTransition(Scale(0.7f))
                    .addTransition(Fade())
                    .addTarget(fab)
                    .setDuration(200)
                    .setStartDelay(300)

            val statsSet = TransitionSet()
                    .addTransition(Slide(Gravity.BOTTOM))
                    .addTarget(statsCard)
                    .setDuration(200)

            val recentsSet = TransitionSet()
                    .addTransition(Slide(Gravity.BOTTOM))
                    .addTarget(recentsCard)
                    .setDuration(200)
                    .setStartDelay(100)

            val finalSet = TransitionSet()
                    .addTransition(fabSet)
                    .addTransition(statsSet)
                    .addTransition(recentsSet)
                    .setInterpolator(FastOutSlowInInterpolator())

            TransitionManager.beginDelayedTransition(root, finalSet)
            fab.visibility = View.VISIBLE
            statsCard.visibility = View.VISIBLE
            recentsCard.visibility = View.VISIBLE
        }
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