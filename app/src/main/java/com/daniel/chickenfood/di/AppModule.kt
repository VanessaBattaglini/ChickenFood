package com.daniel.chickenfood.di

import com.daniel.chickenfood.data.repository.MainRepositoryImpl
import com.daniel.chickenfood.domain.MainRepository
import com.daniel.chickenfood.presentation.viewModel.MainViewModel
import com.google.firebase.database.FirebaseDatabase
import org.koin.core.module.dsl.viewModel
import org.koin.dsl.module

val appModule = module {

    // Firebase
    single { FirebaseDatabase.getInstance() }

    // Repository
    single<MainRepository> { MainRepositoryImpl(get()) }

    // ViewModels
    viewModel { MainViewModel(
        repository = get()
    )}

    /* Descomentar cuando estén implementados:

    // UseCase
    factory { GetChickenListUseCase(get()) }
    */
}

