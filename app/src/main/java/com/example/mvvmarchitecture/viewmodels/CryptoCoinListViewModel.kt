package com.example.mvvmarchitecture.viewmodels

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.MutableLiveData
import com.example.mvvmarchitecture.Config
import com.example.mvvmarchitecture.data.local.helpers.Resource
import com.example.mvvmarchitecture.data.local.models.CryptoCoin
import com.example.mvvmarchitecture.data.remote.CryptoCoinRepository
import com.example.mvvmarchitecture.utils.TimerUtils
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.launch
import kotlin.time.ExperimentalTime

class CryptoCoinListViewModel(application: Application) : AndroidViewModel(application) {

    interface CoinUpdateListener {
        fun onTimerUpdated()
    }

    val coins = MediatorLiveData<Resource<List<CryptoCoin>>>()
    var isLoading = MutableLiveData<Boolean>()
    private val coinRepository: CryptoCoinRepository

    // query extras
    private var isPerformingQuery = false
    private var currencyUnit: String? = null
    private var cancelRequest = false
    private var requestStartTime: Long = 0
    var isLocked = false
    var countNumber = (Config.UPDATE_ROUTINE_PERIOD / 1000).toFloat()

    private var mJob: Job? = null
    var filterName = ""

    companion object {
        private const val TAG = "ListViewModel"
    }

    init {
        coinRepository = CryptoCoinRepository.getInstance(application)
        isLoading.value = false
    }

    fun getCoinsApi(currencyUnit: String?, filterName: String) {
        if (!isPerformingQuery) {
            isLoading.value = true
            this.currencyUnit = currencyUnit
            countNumber = (Config.UPDATE_ROUTINE_PERIOD / 1000).toFloat()
            executeGetData(filterName, true)
        }
    }

    fun filterCoins(filterName: String) {
        executeGetData(filterName, false)
    }

    private fun executeGetData(filterName: String, shouldFetch: Boolean) {
        requestStartTime = System.currentTimeMillis()
        cancelRequest = false
        isPerformingQuery = true
        val repositorySource = coinRepository.getCoinsApi(currencyUnit, filterName, shouldFetch)
        coins.addSource(repositorySource) { listResource: Resource<List<CryptoCoin>>? ->
            if (!cancelRequest) {
                if (listResource != null) {
                    if (listResource.status == Resource.Status.SUCCESS) {
                        Log.d(
                            TAG,
                            "onChanged: REQUEST TIME: " + (System.currentTimeMillis() - requestStartTime) / 1000 + " seconds."
                        )
                        isPerformingQuery = false
                        isLoading.value = false
                        coins.removeSource(repositorySource)
                    } else if (listResource.status == Resource.Status.ERROR) {
                        Log.d(
                            TAG,
                            "onChanged: REQUEST TIME: " + (System.currentTimeMillis() - requestStartTime) / 1000 + " seconds."
                        )
                        isPerformingQuery = false
                        isLoading.value = false
                        coins.removeSource(repositorySource)
                    }
                    coins.setValue(listResource)
                } else {
                    coins.removeSource(repositorySource)
                }
            } else {
                coins.removeSource(repositorySource)
            }
        }
    }

    fun cancelSearchRequest() {
        if (isPerformingQuery) {
            Log.d(TAG, "cancelSearchRequest: canceling the search request.")
            cancelRequest = true
            isPerformingQuery = false
        }
    }

    @ExperimentalTime
    fun startRealtimeUpdate() {
        if (!isLocked) {
            CoroutineScope(Dispatchers.Main).launch {
                TimerUtils.tickerFlow(Config.UPDATE_ROUTINE_PERIOD, 0)
                    .onStart {
                        isLocked = true
                    }
                    .collect {
                        getCoinsApi("USD", filterName)
                    }
            }
        }
    }

    @ExperimentalTime
    fun startCounter(callback: CoinUpdateListener) {
        if (mJob != null && !mJob!!.isCancelled) {
            mJob!!.cancel()
        }
        mJob = CoroutineScope(Dispatchers.Main).launch {

            TimerUtils.secondTickerFlow()
                .collect {
                    // Every second
                    Log.d("AAAAA", countNumber.toString())
                    if (countNumber >= 0) {
                        callback.onTimerUpdated()
                        countNumber--
                    }
                }
        }
    }
}