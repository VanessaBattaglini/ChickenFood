package com.daniel.chickenfood.di

import com.daniel.chickenfood.presentation.dashboard.DashboardViewModel
import com.google.firebase.database.FirebaseDatabase
import org.koin.core.module.dsl.viewModel
import org.koin.dsl.module

val appModule = module {

    // Firebase
    single { FirebaseDatabase.getInstance() }

    // ViewModels
    viewModel { DashboardViewModel() }

    /* Descomentar cuando estén implementados:

    // Repository
    single<ChickenRepository> { ChickenRepositoryImpl(get()) }

    // UseCase
    factory { GetChickenListUseCase(get()) }
    */
}

