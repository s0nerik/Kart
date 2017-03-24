package com.github.s0nerik.shoppingassistant.screens.settings

import android.databinding.ObservableField
import android.os.Bundle
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

    fun selectDefaultCurrency() {
        SelectDefaultCurrencyBottomSheet(this).show(a.supportFragmentManager, null)
    }
}

class SettingsActivity : BaseBoundActivity<ActivitySettingsBinding>(R.layout.activity_settings) {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding.vm = SettingsActivityViewModel(this)
        toolbar.setNavigationOnClickListener { finish() }
    }
}