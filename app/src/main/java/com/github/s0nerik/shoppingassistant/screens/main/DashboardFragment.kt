package com.github.s0nerik.shoppingassistant.screens.main

import android.os.Bundle
import android.support.v4.content.ContextCompat
import android.view.View
import com.github.mikephil.charting.components.Legend
import com.github.mikephil.charting.data.PieData
import com.github.mikephil.charting.data.PieDataSet
import com.github.mikephil.charting.data.PieEntry
import com.github.nitrico.lastadapter.LastAdapter
import com.github.s0nerik.shoppingassistant.BR
import com.github.s0nerik.shoppingassistant.R
import com.github.s0nerik.shoppingassistant.applyWrongNestedScrollWorkaround
import com.github.s0nerik.shoppingassistant.base.BaseBoundFragment
import com.github.s0nerik.shoppingassistant.databinding.FragmentDashboardBinding
import com.github.s0nerik.shoppingassistant.model.Purchase
import io.realm.PurchaseRealmProxy
import io.realm.Realm
import io.realm.RealmResults
import kotlinx.android.synthetic.main.fragment_dashboard.*
import org.jetbrains.anko.support.v4.act

/**
 * Created by Alex on 12/25/2016.
 * GitHub: https://github.com/s0nerik
 * LinkedIn: https://linkedin.com/in/sonerik
 */
class DashboardFragment : BaseBoundFragment<FragmentDashboardBinding>(R.layout.fragment_dashboard) {

    private val realm: Realm by lazy { Realm.getDefaultInstance() }

    private val purchases: RealmResults<Purchase> by lazy {
        realm.where(Purchase::class.java).findAll()
    }

    override fun onViewCreated(view: View?, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initRecents()
        initDistributionChart()
    }

    private fun initRecents() {
        LastAdapter.with(purchases, BR.item)
                .map<PurchaseRealmProxy>(R.layout.item_recent_purchases)
                .into(recentsRecycler)

        recentsRecycler.isNestedScrollingEnabled = false
        recentsRecycler.setHasFixedSize(true)

        scrollView.applyWrongNestedScrollWorkaround()
    }

    private fun initDistributionChart() {
        val dataSet = PieDataSet(listOf(PieEntry(10f, "Food"), PieEntry(10f, "Clothes"), PieEntry(10f, "Instruments")), null)
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

    override fun onDestroy() {
        realm.close()
        super.onDestroy()
    }
}