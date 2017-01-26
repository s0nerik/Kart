package com.github.s0nerik.shoppingassistant.screens.product

import android.databinding.DataBindingUtil
import android.databinding.ViewDataBinding
import android.os.Bundle
import android.support.annotation.LayoutRes
import android.support.design.widget.BottomSheetDialogFragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.github.nitrico.lastadapter.LastAdapter
import com.github.nitrico.lastadapter.Type
import com.github.s0nerik.shoppingassistant.BR
import com.github.s0nerik.shoppingassistant.R
import com.github.s0nerik.shoppingassistant.databinding.*
import com.github.s0nerik.shoppingassistant.model.Category
import com.github.s0nerik.shoppingassistant.model.Currency
import com.github.s0nerik.shoppingassistant.model.Shop
import io.realm.Realm
import kotlinx.android.synthetic.main.sheet_select_category.*

/**
 * Created by Alex on 1/26/2017.
 * GitHub: https://github.com/s0nerik
 * LinkedIn: https://linkedin.com/in/sonerik
 */

open class BaseBottomSheet<out T, V : ViewDataBinding>(val vm: T, @LayoutRes val layout: Int) : BottomSheetDialogFragment() {
    val realm: Realm by lazy { Realm.getDefaultInstance() }
    lateinit var binding: V

    override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        binding = DataBindingUtil.inflate<V>(inflater, layout, container, false)
        binding.setVariable(BR.vm, vm)
        return binding.root
    }
}

class SelectCategoryBottomSheet(
        vm: CreateProductViewModel
) : BaseBottomSheet<CreateProductViewModel, SheetSelectCategoryBinding>(vm, R.layout.sheet_select_category) {
    override fun onViewCreated(view: View?, savedInstanceState: Bundle?) {
        LastAdapter.with(realm.where(Category::class.java).findAll(), BR.item)
                .type {
                    Type<ItemCategoryBinding>(R.layout.item_category)
                            .onClick { vm.setCategory(item as Category) }
                }
                .into(recycler)
    }
}

class SelectCurrencyBottomSheet(
        vm: CreateProductViewModel
) : BaseBottomSheet<CreateProductViewModel, SheetSelectCurrencyBinding>(vm, R.layout.sheet_select_currency) {
    override fun onViewCreated(view: View?, savedInstanceState: Bundle?) {
        LastAdapter.with(realm.where(Currency::class.java).findAll(), BR.item)
                .type {
                    Type<ItemCurrencyBinding>(R.layout.item_currency)
                            .onClick { vm.pendingCurrency.set(item as Currency) }
                }
                .into(recycler)
    }
}

class SelectShopBottomSheet(
        vm: CreateProductViewModel
) : BaseBottomSheet<CreateProductViewModel, SheetSelectShopBinding>(vm, R.layout.sheet_select_shop) {
    override fun onViewCreated(view: View?, savedInstanceState: Bundle?) {
        LastAdapter.with(realm.where(Shop::class.java).findAll(), BR.item)
                .type {
                    Type<ItemCurrencyBinding>(R.layout.item_shop)
                            .onClick { vm.setShop(item as Shop) }
                }
                .into(recycler)
    }
}