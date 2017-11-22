package com.github.s0nerik.shoppingassistant.screens.purchase

import android.databinding.Bindable
import android.support.v7.widget.RecyclerView
import com.github.nitrico.lastadapter.LastAdapter
import com.github.nitrico.lastadapter.Type
import com.github.s0nerik.shoppingassistant.BR
import com.github.s0nerik.shoppingassistant.R
import com.github.s0nerik.shoppingassistant.R.id.*
import com.github.s0nerik.shoppingassistant.base.BaseViewModel
import com.github.s0nerik.shoppingassistant.databinding.ItemPurchaseItemBinding
import com.github.s0nerik.shoppingassistant.databinding.ItemPurchaseItemHorizontalBinding
import com.github.s0nerik.shoppingassistant.ext.RecyclerDivider
import com.github.s0nerik.shoppingassistant.ext.observableListOf
import com.github.s0nerik.shoppingassistant.model.Item
import com.github.s0nerik.shoppingassistant.repositories.IMainRepository
import com.github.s0nerik.shoppingassistant.repositories.MainRepository
import com.github.s0nerik.shoppingassistant.utils.weak

class SelectItemViewModel : BaseViewModel() {
    private var interactor by weak<SelectItemVmInteractor>()

    private lateinit var mainRepo: IMainRepository

    private val itemAdapterType = Type<ItemPurchaseItemBinding>(R.layout.item_purchase_item)
            .onClick { interactor?.onItemSelected(it.binding.item!!) }

    private val horizontalItemAdapterType = Type<ItemPurchaseItemHorizontalBinding>(R.layout.item_purchase_item_horizontal)
            .onClick { interactor?.onItemSelected(it.binding.item!!) }

    val frequents by lazy { observableListOf(mainRepo.getFrequentItems().blockingGet()) }
    val favorites by lazy { observableListOf(mainRepo.getFavoriteItems().blockingGet()) }
    val items by lazy { observableListOf(mainRepo.getItems().blockingGet()) }

    val filteredSearchResults = observableListOf<Item>()

    var searchText: String = ""
        @Bindable get
        set(value) {
            field = value
            notifyPropertyChanged(BR.searchText)

            filteredSearchResults.clear()
            filteredSearchResults += items.filter { it.readableName.contains(searchText, true) }
        }

    val isSearching: Boolean
        @Bindable("searchText") get() = searchText.isNotEmpty()

    fun init(interactor: SelectItemVmInteractor, mainRepo: IMainRepository = MainRepository) {
        this.interactor = interactor
        this.mainRepo = mainRepo
    }

    fun initViews(favoritesRecycler: RecyclerView, frequentsRecycler: RecyclerView, searchResultsRecycler: RecyclerView) {
        initFavorites(favoritesRecycler)
        initFrequents(frequentsRecycler)
        initSearchResults(searchResultsRecycler)
    }

    private fun initFavorites(recycler: RecyclerView) {
        with(recycler) {
            LastAdapter(favorites, BR.item)
                    .type { _, _ -> horizontalItemAdapterType }
                    .into(recycler)

            isNestedScrollingEnabled = false
            setHasFixedSize(true)

            addItemDecoration(RecyclerDivider.vertical)
        }
    }

    private fun initFrequents(recycler: RecyclerView) {
        with(recycler) {
            LastAdapter(frequents, BR.item)
                    .type { _, _ -> itemAdapterType }
                    .into(recycler)

            isNestedScrollingEnabled = false
            addItemDecoration(RecyclerDivider.horizontal)
        }
    }

    private fun initSearchResults(recycler: RecyclerView) {
        with(recycler) {
            LastAdapter(filteredSearchResults, BR.item)
                    .type { _, _ -> itemAdapterType }
                    .into(recycler)

            isNestedScrollingEnabled = false
        }
    }

    fun clearSearch() {
        searchText = ""
    }

    fun voiceSearch() {
        interactor?.let {
            it.provideVoiceRecognitionResult()
                    .subscribeUntilCleared({ searchText = it })
        }
    }

    fun createItem() {
        interactor?.let { interactor ->
            interactor.createItem(searchText)
                    .subscribeUntilCleared({ interactor.onItemSelected(it) })
        }
    }
}