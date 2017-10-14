package com.github.s0nerik.shoppingassistant.screens.product.select_category

import android.arch.lifecycle.LifecycleOwner
import android.arch.lifecycle.Observer
import android.databinding.Bindable
import android.support.v7.widget.RecyclerView
import com.github.nitrico.lastadapter.LastAdapter
import com.github.nitrico.lastadapter.Type
import com.github.s0nerik.shoppingassistant.BR
import com.github.s0nerik.shoppingassistant.R
import com.github.s0nerik.shoppingassistant.base.BaseViewModel
import com.github.s0nerik.shoppingassistant.databinding.ItemCategoryBinding
import com.github.s0nerik.shoppingassistant.ext.asLiveData
import com.github.s0nerik.shoppingassistant.ext.observableListOf
import com.github.s0nerik.shoppingassistant.ext.replaceWith
import com.github.s0nerik.shoppingassistant.model.Category
import com.github.s0nerik.shoppingassistant.repositories.MainRepository
import com.github.s0nerik.shoppingassistant.screens.product.select_category.SelectCategoryViewModel.State.CREATE
import com.github.s0nerik.shoppingassistant.screens.product.select_category.SelectCategoryViewModel.State.SELECT
import com.github.s0nerik.shoppingassistant.utils.weak


/**
 * Created by Alex Isaienko on 10/14/17.
 * GitHub: https://github.com/s0nerik
 * LinkedIn: https://linkedin.com/in/sonerik
 */
class SelectCategoryViewModel : BaseViewModel() {
    enum class State { SELECT, CREATE }

    private var interactor by weak<SelectCategoryViewModelInteractor>()

    var state: State = SELECT
        @Bindable get
        private set(value) {
            field = value
            notifyPropertyChanged(BR.state)
        }

    var categoryName: String = ""
        @Bindable get
        set(value) {
            field = value
            notifyPropertyChanged(BR.categoryName)
        }

    private val _categories= observableListOf<Category>()
    private val categories = MainRepository.getCategories()
            .doOnSuccess { _categories.replaceWith(it) }
            .takeUntilCleared()
            .asLiveData()

    fun init(interactor: SelectCategoryViewModelInteractor, lifecycleOwner: LifecycleOwner) {
        this.interactor = interactor
        categories.observe(lifecycleOwner, Observer {  })
    }

    fun initRecycler(recycler: RecyclerView) {
        LastAdapter(_categories, BR.item)
                .type { item, _ ->
                    Type<ItemCategoryBinding>(R.layout.item_category)
                            .onClick {
                                interactor!!.finishWithResult(it.binding.item)
                            }
                }
                .into(recycler)
    }

    fun toggleCategoryCreation() {
        when(state) {
            SELECT -> state = CREATE
            CREATE -> state = SELECT
        }
    }

    fun confirmCategoryCreation() {
        // TODO: save to database
        interactor!!.finishWithResult(Category(name = categoryName))
    }
}