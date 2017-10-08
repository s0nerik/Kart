package com.github.s0nerik.shoppingassistant.screens.product.select_shop

import android.databinding.Bindable
import android.support.v7.widget.RecyclerView
import com.github.nitrico.lastadapter.LastAdapter
import com.github.nitrico.lastadapter.Type
import com.github.s0nerik.shoppingassistant.BR
import com.github.s0nerik.shoppingassistant.R
import com.github.s0nerik.shoppingassistant.base.BaseViewModel
import com.github.s0nerik.shoppingassistant.databinding.ItemShopBinding
import com.github.s0nerik.shoppingassistant.ext.asLiveData
import com.github.s0nerik.shoppingassistant.ext.observableListOf
import com.github.s0nerik.shoppingassistant.model.Shop
import com.github.s0nerik.shoppingassistant.repositories.MainRepository
import com.github.s0nerik.shoppingassistant.utils.weak


/**
 * Created by Alex Isaienko on 10/8/17.
 * GitHub: https://github.com/s0nerik
 * LinkedIn: https://linkedin.com/in/sonerik
 */
class SelectShopViewModel : BaseViewModel() {
    enum class State { SELECT, CREATE }

    var interactor by weak<SelectShopViewModelInteractor>()

    var state: State = State.SELECT
        @Bindable get
        private set

    private val _shops = observableListOf<Shop>()
    val shops = MainRepository.getShops()
            .takeUntilCleared()
            .asLiveData()

    fun initRecycler(recycler: RecyclerView) {
        LastAdapter(_shops, BR.item)
                .type { item, _ ->
                    Type<ItemShopBinding>(R.layout.item_shop)
                            .onClick {
                                interactor!!.finishWithResult(it.binding.item)
                                it.binding.item
                            }
                }
                .into(recycler)
    }

    fun toggleShopCreation() {
        TODO()
    }

    fun confirmShopCreation() {
        TODO()
    }
}