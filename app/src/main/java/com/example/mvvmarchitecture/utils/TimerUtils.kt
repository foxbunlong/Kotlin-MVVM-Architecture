package com.example.mvvmarchitecture.utils

import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.flow
import kotlin.time.ExperimentalTime

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