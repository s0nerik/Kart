package com.github.s0nerik.shoppingassistant.screens.product

import android.os.Bundle
import android.view.View
import com.github.nitrico.lastadapter.LastAdapter
import com.github.nitrico.lastadapter.Type
import com.github.s0nerik.shoppingassistant.BR
import com.github.s0nerik.shoppingassistant.R
import com.github.s0nerik.shoppingassistant.base.BaseBottomSheet
import com.github.s0nerik.shoppingassistant.databinding.*
import com.github.s0nerik.shoppingassistant.ext.currenciesSorted
import com.github.s0nerik.shoppingassistant.model.RealmCategory
import com.github.s0nerik.shoppingassistant.model.RealmShop
import kotlinx.android.synthetic.main.sheet_select_category.*
import java.util.*

/**
 * Created by Alex on 1/26/2017.
 * GitHub: https://github.com/s0nerik
 * LinkedIn: https://linkedin.com/in/sonerik
 */

//class SelectCategoryBottomSheet(
//        vm: CreateProductViewModel
//) : BaseBottomSheet<CreateProductViewModel, SheetSelectCategoryBinding>(vm, R.layout.sheet_select_category) {
//    override fun onViewCreated(view: View?, savedInstanceState: Bundle?) {
//        super.onViewCreated(view, savedInstanceState)
//        LastAdapter(realm.where(RealmCategory::class.java).findAll(), BR.item)
//                .type { item, _ ->
//                    Type<ItemCategoryBinding>(R.layout.item_category)
//                            .onClick {
//                                vm.setCategory(item as RealmCategory)
//                                dismiss()
//                            }
//                }
//                .into(recycler)
//    }
//}
//
//class SelectPriceBottomSheet(
//        vm: CreateProductViewModel
//) : BaseBottomSheet<CreateProductViewModel, SheetSelectPriceBinding>(vm, R.layout.sheet_select_price) {
//    override fun onViewCreated(view: View?, savedInstanceState: Bundle?) {
//        super.onViewCreated(view, savedInstanceState)
//        with(vm) {
//            etNewPriceValue
//                    .textChanges()
//                    .map { it.toString() }
//                    .map { if (!it.isNullOrBlank()) it.toFloat() else -1f }
//                    .bindUntilEvent(this@SelectPriceBottomSheet, FragmentEvent.DESTROY)
//                    .subscribe {
//                        itemPriceChange.value = if (it < 0) null else it
//                        notifyPropertyChanged(BR.item)
//                        notifyPropertyChanged(BR.priceSet)
//                    }
//
//            spinnerQuantityQualifier.adapter = ArrayAdapter.createFromResource(activity, R.array.price_quantity_qualifiers, android.R.layout.simple_spinner_dropdown_item)
//            spinnerQuantityQualifier.itemSelections()
//                    .bindUntilEvent(this@SelectPriceBottomSheet, FragmentEvent.DESTROY)
//                    .subscribe { i ->
//                        itemPriceChange.quantityQualifier = when (i) {
//                            0 -> RealmPrice.QuantityQualifier.ITEM
//                            1 -> RealmPrice.QuantityQualifier.KG
//                            else -> RealmPrice.QuantityQualifier.ITEM
//                        }
//                    }
//        }
//    }
//}
//
//class SelectCurrencyBottomSheet(
//        vm: CreateProductViewModel
//) : BaseBottomSheet<CreateProductViewModel, SheetSelectCurrencyBinding>(vm, R.layout.sheet_select_currency) {
//    override fun onViewCreated(view: View?, savedInstanceState: Bundle?) {
//        super.onViewCreated(view, savedInstanceState)
//        LastAdapter(currenciesSorted, BR.item)
//                .type { item, _ ->
//                    Type<ItemCurrencyBinding>(R.layout.item_currency)
//                            .onClick {
//                                vm.pendingCurrency.set(item as Currency)
//                                dismiss()
//                            }
//                }
//                .into(recycler)
//    }
//}
//
//class SelectShopBottomSheet(
//        vm: CreateProductViewModel
//) : BaseBottomSheet<CreateProductViewModel, SheetSelectShopBinding>(vm, R.layout.activity_select_shop) {
//    override fun onViewCreated(view: View?, savedInstanceState: Bundle?) {
//        super.onViewCreated(view, savedInstanceState)
//        LastAdapter(realm.where(RealmShop::class.java).findAll(), BR.item)
//                .type { item, _ ->
//                    Type<ItemCurrencyBinding>(R.layout.item_shop)
//                            .onClick {
//                                vm.setShop(item as RealmShop)
//                                dismiss()
//                            }
//                }
//                .into(recycler)
//    }
//}