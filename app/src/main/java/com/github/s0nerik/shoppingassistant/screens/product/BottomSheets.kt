package com.github.s0nerik.shoppingassistant.screens.product

/**
 * Created by Alex on 1/26/2017.
 * GitHub: https://github.com/s0nerik
 * LinkedIn: https://linkedin.com/in/sonerik
 */

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