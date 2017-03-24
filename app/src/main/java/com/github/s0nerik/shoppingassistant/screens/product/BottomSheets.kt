package com.github.s0nerik.shoppingassistant.screens.product

import android.os.Bundle
import android.view.View
import com.github.nitrico.lastadapter.LastAdapter
import com.github.nitrico.lastadapter.Type
import com.github.s0nerik.shoppingassistant.BR
import com.github.s0nerik.shoppingassistant.R
import com.github.s0nerik.shoppingassistant.base.BaseBottomSheet
import com.github.s0nerik.shoppingassistant.databinding.*
import com.github.s0nerik.shoppingassistant.model.Category
import com.github.s0nerik.shoppingassistant.model.Currency
import com.github.s0nerik.shoppingassistant.model.Shop
import kotlinx.android.synthetic.main.sheet_select_category.*

/**
 * Created by Alex on 1/26/2017.
 * GitHub: https://github.com/s0nerik
 * LinkedIn: https://linkedin.com/in/sonerik
 */

class SelectCategoryBottomSheet(
        vm: CreateProductViewModel
) : BaseBottomSheet<CreateProductViewModel, SheetSelectCategoryBinding>(vm, R.layout.sheet_select_category) {
    override fun onViewCreated(view: View?, savedInstanceState: Bundle?) {
        LastAdapter.with(realm.where(Category::class.java).findAll(), BR.item)
                .type {
                    Type<ItemCategoryBinding>(R.layout.item_category)
                            .onClick {
                                vm.setCategory(item as Category)
                                dismiss()
                            }
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
                            .onClick {
                                vm.pendingCurrency.set(item as Currency)
                                dismiss()
                            }
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
                            .onClick {
                                vm.setShop(item as Shop)
                                dismiss()
                            }
                }
                .into(recycler)
    }
}