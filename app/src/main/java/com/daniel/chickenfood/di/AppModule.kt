package com.daniel.chickenfood.di

import com.daniel.chickenfood.data.repository.MainRepositoryImpl
import com.daniel.chickenfood.domain.MainRepository
import com.daniel.chickenfood.presentation.viewModel.MainViewModel
import com.google.firebase.database.FirebaseDatabase
import com.google.gson.Gson
import org.koin.core.module.dsl.viewModel
import org.koin.dsl.module

val appModule = module {

    // JSON
    single { Gson() }

    // Firebase
    single { FirebaseDatabase.getInstance() }

    // Repository
    single<MainRepository> { MainRepositoryImpl(get(), get()) }

    // ViewModels
    viewModel { MainViewModel(repository = get()) }
}
