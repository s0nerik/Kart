package com.github.s0nerik.shoppingassistant.screens.main.fragments

import android.databinding.Bindable
import android.support.design.widget.TabLayout
import android.support.v4.app.FragmentManager
import android.support.v4.app.FragmentPagerAdapter
import android.support.v4.view.PagerAdapter
import android.support.v4.view.ViewPager
import android.support.v4.widget.NestedScrollView
import android.support.v7.widget.RecyclerView
import com.github.nitrico.lastadapter.LastAdapter
import com.github.nitrico.lastadapter.Type
import com.github.s0nerik.shoppingassistant.*
import com.github.s0nerik.shoppingassistant.base.BaseViewModel
import com.github.s0nerik.shoppingassistant.databinding.ItemPurchaseBinding
import com.github.s0nerik.shoppingassistant.ext.RecyclerDivider
import com.github.s0nerik.shoppingassistant.ext.observableListOf
import com.github.s0nerik.shoppingassistant.ext.observeOnMainThread
import com.github.s0nerik.shoppingassistant.model.Cart
import com.github.s0nerik.shoppingassistant.model.Purchase
import com.github.s0nerik.shoppingassistant.repositories.IMainRepository
import com.github.s0nerik.shoppingassistant.repositories.MainRepository
import com.github.s0nerik.shoppingassistant.repositories.stats.StatsRepository
import com.github.s0nerik.shoppingassistant.screens.main.MainViewModel
import com.github.s0nerik.shoppingassistant.screens.main.dashboard.StatsDistributionFragment
import com.github.s0nerik.shoppingassistant.utils.weak
import java.text.DecimalFormat


/**
 * Created by Alex Isaienko on 10/8/17.
 * GitHub: https://github.com/s0nerik
 * LinkedIn: https://linkedin.com/in/sonerik
 */
class DashboardViewModel : BaseViewModel() {
    var interactor by weak<DashboardViewModelInteractor>()

    private lateinit var mainVm: MainViewModel
    lateinit var repo: IMainRepository
    private val recentPurchases = observableListOf<Purchase>()

    var dataPeriod: DashboardDataPeriod = DashboardDataPeriod.from(MainPrefs.expensesLimitPeriod)
        @Bindable get
        set(value) {
            field = value
            notifyPropertyChanged(BR.dataPeriod)
        }

    var adjustRecentPurchasesHeight: Boolean = false
        @Bindable get
        set(value) {
            field = value
            notifyPropertyChanged(BR.adjustRecentPurchasesHeight)
        }

    val moneySpentAmountString: String
        @Bindable("dataPeriod") get() = "${MainPrefs.defaultCurrency.symbol} ${DecimalFormat("0.##").format(
                repo.getMoneySpent(dataPeriod.startDate).blockingGet()
        )}"

    val expensesLimitString: String
        @Bindable("dataPeriod") get() = MainPrefs.formattedShortExpensesLimitOrEmpty

    val showDistribution: Boolean
        @Bindable("dataPeriod") get() = StatsRepository.getPurchaseCategoryDistribution().blockingGet().keys.size > 2

    val showMoneySpent: Boolean
        @Bindable("dataPeriod") get() = repo.getMoneySpent().blockingGet() > 0

    fun init(mainVm: MainViewModel, repo: IMainRepository = MainRepository) {
        this.mainVm = mainVm
        this.repo = repo
        mainVm.observeDataPeriodChanges()
                .map {
                    dataPeriod = it
                    it
                }
                .flatMapSingle {
                    repo.getRecentPurchases(it.startDate)
                }
                .takeUntilCleared()
                .observeOnMainThread()
                .subscribe {
                    recentPurchases.clear()
//                    recentPurchases.forEach { recentPurchases.remove(it) }
                    recentPurchases += it
//                    adjustRecentPurchasesHeight = recentPurchases.isEmpty()
                }
    }

    fun initRecentsRecycler(recentsRecycler: RecyclerView, scrollView: NestedScrollView) {
        LastAdapter(recentPurchases, BR.item)
                .type { _, _ -> Type<ItemPurchaseBinding>(R.layout.item_purchase) }
                .into(recentsRecycler)

        recentsRecycler.isNestedScrollingEnabled = false
        recentsRecycler.setHasFixedSize(true)

        scrollView.applyWrongNestedScrollWorkaround()

        recentsRecycler.addItemDecoration(RecyclerDivider.horizontal)
    }

    fun initStatsViewPager(childFragmentManager: FragmentManager, pager: ViewPager, pagerTabs: TabLayout) {
        val statsDistributionFragment = StatsDistributionFragment()

        val statsAdapter: PagerAdapter = object : FragmentPagerAdapter(childFragmentManager) {
            val fragments = listOf(statsDistributionFragment)

            val names = listOf("Purchases distribution", "Expenses")

            override fun getItem(position: Int) = fragments[position]
            override fun getCount() = fragments.size
            override fun getPageTitle(position: Int) = names[position]
        }

        pager.adapter = statsAdapter
        pagerTabs.setupWithViewPager(pager)
    }

    fun onCreateNewPurchase() {
        interactor!!.createNewItem()
                .takeUntilCleared()
                .subscribe { Cart.add(it) }
    }
}