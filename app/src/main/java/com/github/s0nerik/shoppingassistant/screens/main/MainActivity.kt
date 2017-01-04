package com.github.s0nerik.shoppingassistant.screens.main

import android.os.Bundle
import android.view.MenuItem
import com.github.s0nerik.shoppingassistant.R
import com.github.s0nerik.shoppingassistant.base.BaseBoundActivity
import com.github.s0nerik.shoppingassistant.databinding.ActivityMainBinding
import kotlinx.android.synthetic.main.activity_main.*

class MainActivityViewModel(private val activity: MainActivity) {
    init {
        activity.bottomNavigation.setOnNavigationItemSelectedListener { onNavigationItemSelected(it) }
        // TODO: uncomment when updated to Kotlin 1.1
//        activity.bottomNavigation.setOnNavigationItemSelectedListener(this::onNavigationItemSelected)
    }

    fun onNavigationItemSelected(item: MenuItem): Boolean {
        activity.supportFragmentManager
                .beginTransaction()
                .replace(R.id.container, when(item.itemId) {
                    R.id.dashboard -> DashboardFragment()
                    R.id.history -> HistoryFragment()
                    else -> DashboardFragment()
                })
                .commit()
        return true
    }
}

class MainActivity : BaseBoundActivity<ActivityMainBinding>(R.layout.activity_main) {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding.viewModel = MainActivityViewModel(this)

        supportFragmentManager.beginTransaction()
                .replace(R.id.container, DashboardFragment())
                .commit()
    }
}
