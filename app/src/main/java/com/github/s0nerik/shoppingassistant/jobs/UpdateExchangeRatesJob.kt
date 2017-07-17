package com.github.s0nerik.shoppingassistant.jobs

import android.app.job.JobInfo
import android.app.job.JobParameters
import android.app.job.JobScheduler
import android.app.job.JobService
import android.content.ComponentName
import android.content.Context
import com.github.debop.kodatimes.hours
import com.github.s0nerik.shoppingassistant.api.CurrenciesApi
import io.reactivex.disposables.Disposable
import java.util.*

/**
 * Created by Alex on 4/8/2017.
 * GitHub: https://github.com/s0nerik
 * LinkedIn: https://linkedin.com/in/sonerik
 */
class UpdateExchangeRatesJob : JobService() {
    private lateinit var disposable: Disposable

    override fun onStartJob(params: JobParameters?): Boolean {
        disposable = CurrenciesApi.loadExchangeRates(Date())
                .subscribe({ jobFinished(params, false) }, { jobFinished(params, true) })

        return true
    }

    override fun onStopJob(params: JobParameters?): Boolean {
        disposable.dispose()
        return true
    }

    companion object {
        val JOB_ID = 643

        fun schedule(ctx: Context) {
            val jobScheduler = ctx.getSystemService(Context.JOB_SCHEDULER_SERVICE) as JobScheduler
            jobScheduler.schedule(JobInfo.Builder(JOB_ID, ComponentName(ctx, UpdateExchangeRatesJob::class.java))
                                          .setPeriodic(6.hours().millis)
                                          .setRequiredNetworkType(JobInfo.NETWORK_TYPE_ANY)
                                          .setBackoffCriteria(1.hours().millis, JobInfo.BACKOFF_POLICY_EXPONENTIAL)
                                          .build())
        }
    }
}