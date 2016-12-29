package com.github.s0nerik.shoppingassistant.screens.purchase

import android.databinding.ObservableBoolean
import android.os.Bundle
import com.github.ajalt.timberkt.d
import com.github.s0nerik.shoppingassistant.R
import com.github.s0nerik.shoppingassistant.base.BaseBoundActivity
import com.github.s0nerik.shoppingassistant.databinding.ActivityCreatePurchaseBinding
import com.jakewharton.rxbinding.widget.textChanges
import com.trello.rxlifecycle.android.ActivityEvent
import com.trello.rxlifecycle.kotlin.bindUntilEvent
import kotlinx.android.synthetic.main.activity_create_purchase.*

class CreatePurchaseViewModel(private val activity: CreatePurchaseActivity) {
    val isSearching: ObservableBoolean = ObservableBoolean(false)

    init {
        activity.apply {
            etSearch.textChanges()
                    .bindUntilEvent(activity, ActivityEvent.DESTROY)
                    .subscribe {
                        d { "${etSearch.text}" }
                        isSearching.set(etSearch.text.isNotEmpty())
                    }
        }
    }
}

class CreatePurchaseActivity : BaseBoundActivity<ActivityCreatePurchaseBinding>(R.layout.activity_create_purchase) {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding.viewModel = CreatePurchaseViewModel(this)
    }
}
