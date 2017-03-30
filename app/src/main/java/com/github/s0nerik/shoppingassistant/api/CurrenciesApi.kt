package com.github.s0nerik.shoppingassistant.api

import com.github.debop.kodatimes.toDateTime
import com.github.debop.kodatimes.toIsoFormatDateString
import com.github.s0nerik.shoppingassistant.model.ExchangeRates
import retrofit2.Retrofit
import retrofit2.adapter.rxjava.RxJavaCallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET
import retrofit2.http.Path
import rx.Single
import java.util.*

/**
 * Created by Alex on 3/30/2017.
 * GitHub: https://github.com/s0nerik
 * LinkedIn: https://linkedin.com/in/sonerik
 */

interface CurrenciesApi {
    companion object {
        private val instance: CurrenciesApi by lazy {
            Retrofit.Builder()
                    .addCallAdapterFactory(RxJavaCallAdapterFactory.createAsync())
                    .addConverterFactory(GsonConverterFactory.create())
                    .baseUrl("https://s3-us-west-2.amazonaws.com/kart-currencies/")
                    .build()
                    .create(CurrenciesApi::class.java)
        }

        fun loadConversions(date: Date): Single<ExchangeRates> {
            return instance.loadConversions(date.toDateTime().toIsoFormatDateString())
        }
    }

    @GET("{date}")
    fun loadConversions(@Path("date") formattedDate: String): Single<ExchangeRates>
}