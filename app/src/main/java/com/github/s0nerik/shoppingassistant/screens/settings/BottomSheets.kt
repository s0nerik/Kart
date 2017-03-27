package com.github.s0nerik.shoppingassistant.screens.settings

import android.os.Bundle
import android.support.design.widget.BottomSheetBehavior
import android.support.design.widget.BottomSheetDialog
import android.support.design.widget.CoordinatorLayout
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import com.github.nitrico.lastadapter.LastAdapter
import com.github.nitrico.lastadapter.Type
import com.github.s0nerik.shoppingassistant.BR
import com.github.s0nerik.shoppingassistant.ExpensesLimitPeriod
import com.github.s0nerik.shoppingassistant.R
import com.github.s0nerik.shoppingassistant.base.BaseBottomSheet
import com.github.s0nerik.shoppingassistant.databinding.ActivitySettingsBinding
import com.github.s0nerik.shoppingassistant.databinding.ItemCurrencyBinding
import com.github.s0nerik.shoppingassistant.model.Currency
import com.vicpin.krealmextensions.queryAll
import com.vicpin.krealmextensions.save
import kotlinx.android.synthetic.main.sheet_select_category.*
import kotlinx.android.synthetic.main.sheet_select_expenses_limit.*

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

class SelectExpensesLimitBottomSheet(
        vm: SettingsActivityViewModel
) : BaseBottomSheet<SettingsActivityViewModel, ActivitySettingsBinding>(vm, R.layout.sheet_select_expenses_limit) {
    override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        dialog.setOnShowListener {
            val d = it as BottomSheetDialog
            val bottomSheet = d.findViewById(R.id.design_bottom_sheet)!!
            val coordinatorLayout = bottomSheet.parent as CoordinatorLayout
            val bottomSheetBehavior = BottomSheetBehavior.from(bottomSheet)
            bottomSheetBehavior.peekHeight = bottomSheet.height
            coordinatorLayout.parent.requestLayout()
        }
        return super.onCreateView(inflater, container, savedInstanceState)
    }

    override fun onViewCreated(view: View?, savedInstanceState: Bundle?) {
        spinnerPeriod.adapter = ArrayAdapter(activity, android.R.layout.simple_spinner_dropdown_item, ExpensesLimitPeriod.values())
    }

//    override fun setupDialog(dialog: Dialog?, style: Int) {
//        super.setupDialog(dialog, style)
//        val behavior = BottomSheetBehavior.from(view!!.parent as View)
//        behavior?.let {
////            it.setBottomSheetCallback(mBottomSheetBehaviorCallback)
//            it.peekHeight = dip(56)
//            view!!.requestLayout()
//        }
//    }
}