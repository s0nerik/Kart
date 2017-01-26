package com.github.s0nerik.shoppingassistant.screens.product

import android.databinding.DataBindingUtil
import android.os.Bundle
import android.support.design.widget.BottomSheetDialogFragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.github.nitrico.lastadapter.LastAdapter
import com.github.nitrico.lastadapter.Type
import com.github.s0nerik.shoppingassistant.BR
import com.github.s0nerik.shoppingassistant.R
import com.github.s0nerik.shoppingassistant.databinding.ItemCategoryBinding
import com.github.s0nerik.shoppingassistant.databinding.SheetSelectCategoryBinding
import com.github.s0nerik.shoppingassistant.model.Category
import com.vicpin.krealmextensions.allItems
import io.realm.Realm
import kotlinx.android.synthetic.main.sheet_select_category.*

/**
 * Created by Alex on 1/26/2017.
 * GitHub: https://github.com/s0nerik
 * LinkedIn: https://linkedin.com/in/sonerik
 */

class SelectCategoryBottomSheet(val vm: CreateProductViewModel) : BottomSheetDialogFragment() {
    val realm: Realm by lazy { Realm.getDefaultInstance() }

    lateinit var binding: SheetSelectCategoryBinding

    override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        binding = DataBindingUtil.inflate<SheetSelectCategoryBinding>(inflater, R.layout.sheet_select_category, container, false)
        binding.vm = vm
        return binding.root
    }

    override fun onViewCreated(view: View?, savedInstanceState: Bundle?) {
        LastAdapter.with(Category().allItems, BR.item)
                .type {
                    Type<ItemCategoryBinding>(R.layout.item_category)
                            .onClick { vm.setCategory(item as Category) }
                }
                .into(recycler)
    }
}