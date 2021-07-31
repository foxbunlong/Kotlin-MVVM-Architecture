package com.example.mvvmarchitecture.utils

import android.util.Log
import kotlin.time.*
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.*

class TimerUtils {

    @ExperimentalTime
    companion object {
        fun tickerFlow(period: Long, initialDelay: Long) = flow {
            delay(initialDelay)
            while (true) {
                emit(Unit)
                delay(period)
            }
        }

        fun secondTickerFlow() = flow {
            while (true) {
                emit(Unit)
                delay(1000)
            }
        }
    }

}