package com.github.s0nerik.shoppingassistant.screens.settings

import android.os.Bundle
import android.support.design.widget.BottomSheetBehavior
import android.support.design.widget.BottomSheetDialog
import android.support.design.widget.CoordinatorLayout
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import com.github.nitrico.lastadapter.LastAdapter
import com.github.nitrico.lastadapter.Type
import com.github.s0nerik.shoppingassistant.BR
import com.github.s0nerik.shoppingassistant.ExpensesLimitPeriod
import com.github.s0nerik.shoppingassistant.MainPrefs
import com.github.s0nerik.shoppingassistant.R
import com.github.s0nerik.shoppingassistant.base.BaseBottomSheet
import com.github.s0nerik.shoppingassistant.databinding.ActivitySettingsBinding
import com.github.s0nerik.shoppingassistant.databinding.ItemCurrencyBinding
import com.github.s0nerik.shoppingassistant.databinding.SheetSelectExpensesLimitBinding
import com.github.s0nerik.shoppingassistant.ext.currenciesSorted
import com.jakewharton.rxbinding2.widget.textChanges
import io.reactivex.disposables.Disposable
import kotlinx.android.synthetic.main.sheet_select_category.*
import kotlinx.android.synthetic.main.sheet_select_expenses_limit.*
import java.text.DecimalFormat
import java.util.*

/**
 * Created by Alex on 3/23/2017.
 * GitHub: https://github.com/s0nerik
 * LinkedIn: https://linkedin.com/in/sonerik
 */

class SelectDefaultCurrencyBottomSheet(
        vm: SettingsActivityViewModel
) : BaseBottomSheet<SettingsActivityViewModel, ActivitySettingsBinding>(vm, R.layout.sheet_select_currency) {
    override fun onViewCreated(view: View?, savedInstanceState: Bundle?) {
        LastAdapter.with(currenciesSorted, BR.item)
                .type {
                    Type<ItemCurrencyBinding>(R.layout.item_currency)
                            .onClick {
                                val selectedCurrency = item as Currency
                                MainPrefs.defaultCurrency = selectedCurrency
                                vm.defaultCurrency.set(selectedCurrency)
                                dismiss()
                            }
                }
                .into(recycler)
    }
}

class SelectExpensesLimitViewModel(val currencySign: String) {
    lateinit var f: SelectExpensesLimitBottomSheet

    fun accept() {
        if (f.wasChanged && f.expensesLimit != null && f.expensesLimitPeriod != null) {
            MainPrefs.expensesLimit = f.expensesLimit!!
            MainPrefs.expensesLimitPeriod = f.expensesLimitPeriod!!
        }
        f.dismiss()
    }
}

class SelectExpensesLimitBottomSheet(
        vm: SelectExpensesLimitViewModel
) : BaseBottomSheet<SelectExpensesLimitViewModel, SheetSelectExpensesLimitBinding>(vm, R.layout.sheet_select_expenses_limit) {
    private lateinit var limitTextDisposable: Disposable

    var expensesLimit: Float? = MainPrefs.expensesLimit
    var expensesLimitPeriod: ExpensesLimitPeriod? = MainPrefs.expensesLimitPeriod
    var wasChanged = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        vm.f = this
    }

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
        if (expensesLimit != null && expensesLimit!! > 0f)
            etLimit.setText(DecimalFormat("0.##").format(expensesLimit))
        etLimit.setSelection(etLimit.text.length)

        spinnerPeriod.adapter = ArrayAdapter(activity, android.R.layout.simple_spinner_dropdown_item, ExpensesLimitPeriod.values())
        spinnerPeriod.setSelection(ExpensesLimitPeriod.values().indexOf(expensesLimitPeriod))
        spinnerPeriod.onItemSelectedListener = object: AdapterView.OnItemSelectedListener {
            override fun onNothingSelected(parent: AdapterView<*>?) {}
            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                expensesLimitPeriod = ExpensesLimitPeriod.values()[position]
                wasChanged = true
            }
        }
    }

    override fun onResume() {
        super.onResume()
        limitTextDisposable = etLimit.textChanges().skip(1).subscribe {
            expensesLimit = it.toString().toFloatOrNull()
            wasChanged = true
        }
    }

    override fun onPause() {
        super.onPause()
        limitTextDisposable.dispose()
    }
}