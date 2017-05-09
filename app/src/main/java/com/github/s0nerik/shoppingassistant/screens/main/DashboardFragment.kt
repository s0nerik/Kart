package com.github.s0nerik.shoppingassistant.screens.main

import android.animation.Animator
import android.animation.AnimatorSet
import android.databinding.BaseObservable
import android.databinding.Bindable
import android.os.Bundle
import android.support.v4.app.FragmentPagerAdapter
import android.support.v4.view.PagerAdapter
import android.support.v4.view.animation.FastOutSlowInInterpolator
import android.transition.Fade
import android.view.View
import com.bartoszlipinski.viewpropertyobjectanimator.ViewPropertyObjectAnimator
import com.github.nitrico.lastadapter.LastAdapter
import com.github.nitrico.lastadapter.Type
import com.github.s0nerik.shoppingassistant.*
import com.github.s0nerik.shoppingassistant.base.BaseBoundFragment
import com.github.s0nerik.shoppingassistant.databinding.FragmentDashboardBinding
import com.github.s0nerik.shoppingassistant.databinding.ItemPurchaseBinding
import com.github.s0nerik.shoppingassistant.ext.KTransitionSet
import com.github.s0nerik.shoppingassistant.ext.RecyclerDivider
import com.github.s0nerik.shoppingassistant.ext.scales
import com.github.s0nerik.shoppingassistant.model.Cart
import com.github.s0nerik.shoppingassistant.screens.main.dashboard.StatsDistributionFragment
import com.github.s0nerik.shoppingassistant.screens.main.dashboard.StatsExpensesFragment
import com.github.s0nerik.shoppingassistant.screens.purchase.SelectItemActivity
import com.trello.rxlifecycle2.android.FragmentEvent
import com.trello.rxlifecycle2.kotlin.bindUntilEvent
import kotlinx.android.synthetic.main.fragment_dashboard.*
import org.jetbrains.anko.support.v4.act
import org.jetbrains.anko.support.v4.dip
import java.text.DecimalFormat

/**
 * Created by Alex on 12/25/2016.
 * GitHub: https://github.com/s0nerik
 * LinkedIn: https://linkedin.com/in/sonerik
 */
class DashboardViewModel: BaseObservable() {
    lateinit var f: DashboardFragment

    var dataPeriod: DashboardDataPeriod = DashboardDataPeriod.from(MainPrefs.expensesLimitPeriod)
        @Bindable get
        set(value) {
            field = value
            notifyPropertyChanged(BR.dataPeriod)
        }

    val moneySpentAmountString: String
        @Bindable("dataPeriod") get() = "${MainPrefs.defaultCurrency.symbol} ${DecimalFormat("0.##").format(
                Db.recentPurchases(f.realm, dataPeriod.startDate).sumByDouble { it.fullPrice.toDouble() }
        )}"

    val expensesLimitString: String
        @Bindable("dataPeriod") get() = MainPrefs.formattedShortExpensesLimitOrEmpty

    fun onCreateNewPurchase() {
        SelectItemActivity.startForResult(f.act)
                .bindUntilEvent(f, FragmentEvent.DESTROY)
                .subscribe { Cart.add(it) }
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

class DashboardFragment : BaseBoundFragment<FragmentDashboardBinding>(R.layout.fragment_dashboard) {
    private val recentPurchases
        get() = Db.recentPurchases(realm)

    private val statsDistributionFragment = StatsDistributionFragment()
    private val statsExpensesFragment = StatsExpensesFragment()

    private val statsAdapter: PagerAdapter
        get() = object : FragmentPagerAdapter(childFragmentManager) {
            val fragments = listOf(statsDistributionFragment, statsExpensesFragment)

            val names = listOf("Purchases distribution", "Expenses")

            override fun getItem(position: Int) = fragments[position]
            override fun getCount() = fragments.size
            override fun getPageTitle(position: Int) = names[position]
        }

    private val animator = DashboardAnimator(this)

    override fun onViewCreated(view: View?, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.vm = vm
        vm.f = this

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

        recentsRecycler.addItemDecoration(RecyclerDivider.horizontal)
    }

    private fun initStats() {
        statsPager.adapter = statsAdapter
        statsPagerTabs.setupWithViewPager(statsPager)
    }

    companion object {
        @JvmStatic
        val vm = DashboardViewModel()
    }
}