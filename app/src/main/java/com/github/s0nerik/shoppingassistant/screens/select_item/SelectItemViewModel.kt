package com.github.s0nerik.shoppingassistant.screens.select_item

import android.databinding.Bindable
import android.support.v7.widget.RecyclerView
import com.github.nitrico.lastadapter.LastAdapter
import com.github.nitrico.lastadapter.Type
import com.github.s0nerik.shoppingassistant.BR
import com.github.s0nerik.shoppingassistant.R
import com.github.s0nerik.shoppingassistant.base.BaseViewModel
import com.github.s0nerik.shoppingassistant.databinding.ItemPurchaseItemBinding
import com.github.s0nerik.shoppingassistant.databinding.ItemPurchaseItemHorizontalBinding
import com.github.s0nerik.shoppingassistant.ext.RecyclerDivider
import com.github.s0nerik.shoppingassistant.ext.observableListOf
import com.github.s0nerik.shoppingassistant.ext.observeOnMainThread
import com.github.s0nerik.shoppingassistant.ext.subscribeOnIoThread
import com.github.s0nerik.shoppingassistant.model.Item
import com.github.s0nerik.shoppingassistant.repositories.IMainRepository
import com.github.s0nerik.shoppingassistant.repositories.MainRepository
import com.github.s0nerik.shoppingassistant.utils.weak
import io.reactivex.rxkotlin.Singles
import java.util.concurrent.TimeUnit

class SelectItemViewModel : BaseViewModel() {
    private var interactor by weak<SelectItemVmInteractor>()

    private lateinit var mainRepo: IMainRepository

    private val itemAdapterType = Type<ItemPurchaseItemBinding>(R.layout.item_purchase_item)
            .onClick { interactor?.onItemSelected(it.binding.item!!) }

    private val horizontalItemAdapterType = Type<ItemPurchaseItemHorizontalBinding>(R.layout.item_purchase_item_horizontal)
            .onClick { interactor?.onItemSelected(it.binding.item!!) }

    val frequents = observableListOf<Item>()
    val favorites = observableListOf<Item>()
    val items = observableListOf<Item>()
    val filteredSearchResults = observableListOf<Item>()

    //region Bindable properties
    var searchText: String = ""
        @Bindable get
        set(value) {
            field = value
            notifyPropertyChanged(BR.searchText)
        }

    val isSearching: Boolean
        @Bindable("searchText") get() = searchText.isNotEmpty()
    //endregion

    fun init(interactor: SelectItemVmInteractor, mainRepo: IMainRepository = MainRepository) {
        this.interactor = interactor
        this.mainRepo = mainRepo

        loadData()
        observeSearchQueryChanges()
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
//            setHasFixedSize(true)

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
            LastAdapter(filteredSearchResults, BR.item, false)
                    .type { _, _ -> itemAdapterType }
                    .into(recycler)

            isNestedScrollingEnabled = false
        }
    }

    private fun loadData() {
        Singles.zip(
                mainRepo.getFrequentItems().subscribeOnIoThread(),
                mainRepo.getFavoriteItems().subscribeOnIoThread(),
                mainRepo.getItems().subscribeOnIoThread(),
                { frequents, favorites, items -> Triple(frequents, favorites, items) }
        ).observeOnMainThread().subscribeUntilCleared {
            frequents += it.first
            favorites += it.second
            items += it.third
        }
    }

    private fun observeSearchQueryChanges() {
        observePropertyChanges(BR.searchText)
                .throttleLast(1000, TimeUnit.MILLISECONDS)
                .observeOnMainThread()
                .subscribeUntilCleared {
                    filteredSearchResults.clear()
                    filteredSearchResults += items.filter { it.readableName.contains(searchText, true) }
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