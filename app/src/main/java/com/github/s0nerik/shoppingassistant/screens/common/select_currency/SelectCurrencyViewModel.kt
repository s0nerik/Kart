package com.github.s0nerik.shoppingassistant.screens.common.select_currency

import android.support.v7.widget.RecyclerView
import com.github.nitrico.lastadapter.LastAdapter
import com.github.nitrico.lastadapter.Type
import com.github.s0nerik.shoppingassistant.BR
import com.github.s0nerik.shoppingassistant.R
import com.github.s0nerik.shoppingassistant.base.BaseViewModel
import com.github.s0nerik.shoppingassistant.databinding.ItemCurrencyBinding
import com.github.s0nerik.shoppingassistant.ext.currenciesSorted
import com.github.s0nerik.shoppingassistant.utils.weak
import java.util.*

/**
 * Created by Alex Isaienko on 11/14/2017.
 * GitHub: https://github.com/s0nerik
 * LinkedIn: https://linkedin.com/in/sonerik
 */
class SelectCurrencyViewModel : BaseViewModel() {
    private var interactor by weak<SelectCurrencyViewModelInteractor>()

    fun init(interactor: SelectCurrencyViewModelInteractor) {
        this.interactor = interactor
    }

    fun initRecycler(recycler: RecyclerView) {
        LastAdapter(currenciesSorted, BR.item)
                .type { item, _ ->
                    Type<ItemCurrencyBinding>(R.layout.item_currency)
                            .onClick { interactor?.onCurrencySelected(item as Currency) }
                }
                .into(recycler)
    }
}