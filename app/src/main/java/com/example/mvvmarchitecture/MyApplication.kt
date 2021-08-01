package com.example.mvvmarchitecture

import android.app.Application
import com.example.mvvmarchitecture.injection.coinListViewModelModule
import org.koin.android.ext.koin.androidContext
import org.koin.core.context.startKoin

class MyApplication : Application() {

    override fun onCreate() {
        super.onCreate()
        startKoin {
            androidContext(this@MyApplication)
            modules(
                listOf(
                    coinListViewModelModule
                )
            )
        }
    }

}