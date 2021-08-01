package com.example.mvvmarchitecture.injection

import com.example.mvvmarchitecture.viewmodels.CryptoCoinListViewModel
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module

val coinListViewModelModule = module {
    viewModel { CryptoCoinListViewModel(get()) }
}