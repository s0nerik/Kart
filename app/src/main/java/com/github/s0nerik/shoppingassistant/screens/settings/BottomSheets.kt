package com.github.s0nerik.shoppingassistant.screens.settings

import android.os.Bundle
import android.view.View
import com.github.nitrico.lastadapter.LastAdapter
import com.github.nitrico.lastadapter.Type
import com.github.s0nerik.shoppingassistant.BR
import com.github.s0nerik.shoppingassistant.R
import com.github.s0nerik.shoppingassistant.base.BaseBottomSheet
import com.github.s0nerik.shoppingassistant.databinding.ActivitySettingsBinding
import com.github.s0nerik.shoppingassistant.databinding.ItemCurrencyBinding
import com.github.s0nerik.shoppingassistant.model.Currency
import com.vicpin.krealmextensions.queryAll
import com.vicpin.krealmextensions.save
import kotlinx.android.synthetic.main.sheet_select_category.*

/**
 * Created by Alex on 3/23/2017.
 * GitHub: https://github.com/s0nerik
 * LinkedIn: https://linkedin.com/in/sonerik
 */

class SelectDefaultCurrencyBottomSheet(
        vm: SettingsActivityViewModel
) : BaseBottomSheet<SettingsActivityViewModel, ActivitySettingsBinding>(vm, R.layout.sheet_select_currency) {
    override fun onViewCreated(view: View?, savedInstanceState: Bundle?) {
        LastAdapter.with(Currency().queryAll(), BR.item)
                .type {
                    Type<ItemCurrencyBinding>(R.layout.item_currency)
                            .onClick {
                                val selectedCurrency = item as Currency

                                if (Currency.default.isDefault) {
                                    val defaultCurrency = Currency.default
                                    defaultCurrency.isDefault = false
                                    defaultCurrency.save()
                                }

                                selectedCurrency.isDefault = true
                                selectedCurrency.save()

                                vm.defaultCurrency.set(selectedCurrency)
                                dismiss()
                            }
                }
                .into(recycler)
    }
}