package com.github.s0nerik.shoppingassistant.screens.settings

import android.content.SharedPreferences
import android.databinding.ObservableField
import android.os.Bundle
import com.github.s0nerik.shoppingassistant.MainPrefs
import com.github.s0nerik.shoppingassistant.R
import com.github.s0nerik.shoppingassistant.base.BaseBoundActivity
import com.github.s0nerik.shoppingassistant.databinding.ActivitySettingsBinding
import com.github.s0nerik.shoppingassistant.model.Currency
import kotlinx.android.synthetic.main.activity_settings.*

/**
 * Created by Alex on 3/23/2017.
 * GitHub: https://github.com/s0nerik
 * LinkedIn: https://linkedin.com/in/sonerik
 */

class SettingsActivityViewModel(val a: SettingsActivity) {
    val defaultCurrency = ObservableField<Currency>(Currency.default)

    val expensesLimitString = ObservableField(MainPrefs.formattedExpensesLimit)
    val sharedPreferencesChangeListener = SharedPreferences.OnSharedPreferenceChangeListener { prefs: SharedPreferences, key: String ->
        if (key == "expensesLimitPeriod" || key == "expensesLimit") {
            expensesLimitString.set(MainPrefs.formattedExpensesLimit)
        }
    }

    fun selectDefaultCurrency() {
        SelectDefaultCurrencyBottomSheet(this).show(a.supportFragmentManager, null)
    }

    fun selectExpensesLimit() {
        SelectExpensesLimitBottomSheet(SelectExpensesLimitViewModel()).show(a.supportFragmentManager, null)
    }
}

class SettingsActivity : BaseBoundActivity<ActivitySettingsBinding>(R.layout.activity_settings, false) {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding.vm = SettingsActivityViewModel(this)
        toolbar.setNavigationOnClickListener { finish() }

        MainPrefs.sharedPreferences
                .registerOnSharedPreferenceChangeListener(binding.vm.sharedPreferencesChangeListener)
    }

    override fun onDestroy() {
        super.onDestroy()
        MainPrefs.sharedPreferences
                .unregisterOnSharedPreferenceChangeListener(binding.vm.sharedPreferencesChangeListener)
    }
}