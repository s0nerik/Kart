package com.github.s0nerik.shoppingassistant.api

import com.github.debop.kodatimes.asUtc
import com.github.debop.kodatimes.toDateTime
import com.github.debop.kodatimes.toIsoFormatDateString
import com.github.s0nerik.shoppingassistant.model.RemoteExchangeRates
import io.reactivex.Single
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET
import retrofit2.http.Path
import java.util.*

/**
 * Created by Alex on 3/30/2017.
 * GitHub: https://github.com/s0nerik
 * LinkedIn: https://linkedin.com/in/sonerik
 */

interface CurrenciesApi {
    @GET("{date}")
    fun loadExchangeRates(@Path("date") formattedDate: String): Single<RemoteExchangeRates>

    companion object {
        private val instance: CurrenciesApi by lazy {
            Retrofit.Builder()
                    .addCallAdapterFactory(RxJava2CallAdapterFactory.createAsync())
                    .addConverterFactory(GsonConverterFactory.create())
                    .baseUrl("https://s3-us-west-2.amazonaws.com/kart-currencies/")
                    .build()
                    .create(CurrenciesApi::class.java)
        }

        fun loadExchangeRates(date: Date): Single<RemoteExchangeRates> {
            return instance.loadExchangeRates(date.toDateTime().asUtc().toIsoFormatDateString())
                    .doOnSuccess { it.saveToDatabase() }
        }
    }
}