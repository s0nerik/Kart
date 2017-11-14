package com.github.s0nerik.shoppingassistant.screens.product.select_price

import android.databinding.Bindable
import android.support.v7.widget.RecyclerView
import android.view.View
import android.widget.ArrayAdapter
import android.widget.Spinner
import com.github.s0nerik.shoppingassistant.BR
import com.github.s0nerik.shoppingassistant.MainPrefs
import com.github.s0nerik.shoppingassistant.R
import com.github.s0nerik.shoppingassistant.base.BaseViewModel
import com.github.s0nerik.shoppingassistant.model.Price
import com.github.s0nerik.shoppingassistant.screens.common.select_currency.SelectCurrencyViewModel
import com.github.s0nerik.shoppingassistant.screens.common.select_currency.SelectCurrencyViewModelInteractor
import com.github.s0nerik.shoppingassistant.utils.weak
import com.jakewharton.rxbinding2.widget.itemSelections
import java.util.*


/**
 * Created by Alex Isaienko on 10/8/17.
 * GitHub: https://github.com/s0nerik
 * LinkedIn: https://linkedin.com/in/sonerik
 */
class SelectPriceViewModel : BaseViewModel(), SelectCurrencyViewModelInteractor {
    enum class State { PRICE, CURRENCY }

    private var interactor by weak<SelectPriceViewModelInteractor>()

    val selectCurrencyVm = SelectCurrencyViewModel()

    var state: State = State.PRICE
        @Bindable get
        private set(value) {
            field = value
            notifyPropertyChanged(BR.state)
        }

    var currency: Currency = MainPrefs.defaultCurrency
        @Bindable get
        private set(value) {
            field = value
            notifyPropertyChanged(BR.currency)
        }

    var value: String = ""
        @Bindable("currency") get
        set(value) {
            field = value
            notifyPropertyChanged(BR.value)
        }

    val isValueCorrect: Boolean
        @Bindable("value") get() = value.toFloatOrNull()?.let { true } ?: false

    val valueFloat: Float
        @Bindable("value") get() {
            if (!isValueCorrect) TODO()
            return value.toFloat()
        }

    var quantityQualifier: Price.QuantityQualifier = Price.QuantityQualifier.ITEM
        @Bindable get
        private set(value) {
            field = value
            notifyPropertyChanged(BR.quantityQualifier)
        }

    val priceVisibility
        @Bindable("state") get() = if (state == State.PRICE) View.VISIBLE else View.GONE

    val currenciesVisibility
        @Bindable("state") get() = if (state == State.CURRENCY) View.VISIBLE else View.GONE

    fun init(interactor: SelectPriceViewModelInteractor) {
        this.interactor = interactor
        selectCurrencyVm.init(this)
    }

    fun initQuantityQualifierSpinner(spinner: Spinner) {
        spinner.adapter = ArrayAdapter.createFromResource(spinner.context, R.array.price_quantity_qualifiers, android.R.layout.simple_spinner_dropdown_item)
        spinner.itemSelections()
                .takeUntilCleared()
                .subscribe { i ->
                    quantityQualifier = when (i) {
                        0 -> Price.QuantityQualifier.ITEM
                        1 -> Price.QuantityQualifier.KG
                        else -> throw IllegalStateException()
                    }
                }
    }

    fun initCurrenciesRecycler(recycler: RecyclerView) {
        selectCurrencyVm.initRecycler(recycler)
    }

    fun toggleCurrencySelection() {
        state = if (state == State.CURRENCY) State.PRICE else State.CURRENCY
    }

    fun confirmPriceSelection() {
        // TODO: save to db
        interactor!!.onPriceSelected(Price(value = valueFloat))
    }

    override fun onCurrencySelected(currency: Currency) {
        this.currency = currency
    }
}