package com.github.s0nerik.shoppingassistant.screens.main.fragments.history

import android.os.Bundle
import android.view.View
import com.github.s0nerik.shoppingassistant.R
import com.github.s0nerik.shoppingassistant.base.BaseBoundVmFragment
import com.github.s0nerik.shoppingassistant.databinding.FragmentHistoryBinding
import com.github.s0nerik.shoppingassistant.model.Purchase
import kotlinx.android.synthetic.main.fragment_history.*

/**
 * Created by Alex on 12/27/2016.
 * GitHub: https://github.com/s0nerik
 * LinkedIn: https://linkedin.com/in/sonerik
 */
class HistoryFragment : BaseBoundVmFragment<FragmentHistoryBinding, HistoryViewModel>(
        R.layout.fragment_history, HistoryViewModel::class
), HistoryInteractor {
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        vm.init(this)
        vm.initRecycler(recycler)
    }

    override fun onItemSelected(item: Purchase) {
        TODO("not implemented")
    }
}