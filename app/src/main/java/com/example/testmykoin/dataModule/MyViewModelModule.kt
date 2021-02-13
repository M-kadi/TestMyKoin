package com.example.testmykoin.dataModule

import org.koin.android.viewmodel.dsl.viewModel
import org.koin.dsl.module
// inject ViewModel
val MyViewModelModule = module {
    single { Data() }

    viewModel {
        MyViewModel(get())
    }
}