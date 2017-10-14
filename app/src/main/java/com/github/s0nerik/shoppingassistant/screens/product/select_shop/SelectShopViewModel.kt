package com.github.s0nerik.shoppingassistant.screens.product.select_shop

import android.arch.lifecycle.LifecycleOwner
import android.arch.lifecycle.Observer
import android.databinding.Bindable
import android.support.v7.widget.RecyclerView
import com.github.nitrico.lastadapter.LastAdapter
import com.github.nitrico.lastadapter.Type
import com.github.s0nerik.shoppingassistant.App
import com.github.s0nerik.shoppingassistant.BR
import com.github.s0nerik.shoppingassistant.R
import com.github.s0nerik.shoppingassistant.base.BaseViewModel
import com.github.s0nerik.shoppingassistant.databinding.ItemShopBinding
import com.github.s0nerik.shoppingassistant.ext.asLiveData
import com.github.s0nerik.shoppingassistant.ext.observableListOf
import com.github.s0nerik.shoppingassistant.ext.replaceWith
import com.github.s0nerik.shoppingassistant.model.Shop
import com.github.s0nerik.shoppingassistant.repositories.MainRepository
import com.github.s0nerik.shoppingassistant.screens.product.select_shop.SelectShopViewModel.State.CREATE
import com.github.s0nerik.shoppingassistant.screens.product.select_shop.SelectShopViewModel.State.SELECT
import com.github.s0nerik.shoppingassistant.utils.weak
import org.jetbrains.anko.longToast


/**
 * Created by Alex Isaienko on 10/8/17.
 * GitHub: https://github.com/s0nerik
 * LinkedIn: https://linkedin.com/in/sonerik
 */
class SelectShopViewModel : BaseViewModel() {
    enum class State { SELECT, CREATE }

    private var interactor by weak<SelectShopViewModelInteractor>()

    var newShopName: String = ""
        @Bindable get
        set(value) {
            field = value
            notifyPropertyChanged(BR.newShopName)
        }

    var state: State = SELECT
        @Bindable get
        private set(value) {
            field = value
            notifyPropertyChanged(BR.state)
        }

    private val _shops = observableListOf<Shop>()
    private val shops = MainRepository.getShops()
            .doOnSuccess { _shops.replaceWith(it) }
            .takeUntilCleared()
            .asLiveData()

    fun init(interactor: SelectShopViewModelInteractor, lifecycleOwner: LifecycleOwner) {
        this.interactor = interactor
        shops.observe(lifecycleOwner, Observer {  })
    }

    fun initRecycler(recycler: RecyclerView) {
        LastAdapter(_shops, BR.item)
                .type { _, _ ->
                    Type<ItemShopBinding>(R.layout.item_shop)
                            .onClick {
                                interactor!!.finishWithResult(it.binding.item)
                                it.binding.item
                            }
                }
                .into(recycler)
    }

    fun toggleShopCreation() {
        when(state) {
            SELECT -> state = CREATE
            CREATE -> state = SELECT
        }
    }

    fun confirmShopCreation() {
        // TODO: save to db
        interactor!!.finishWithResult(Shop(name = newShopName))
        App.context.longToast(newShopName)
    }
}