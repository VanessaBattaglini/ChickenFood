package com.daniel.chickenfood.di

import com.daniel.chickenfood.data.repository.MainRepositoryImpl
import com.daniel.chickenfood.data.repository.OrderRepositoryImpl
import com.daniel.chickenfood.data.repository.RewardsRepositoryImpl
import com.daniel.chickenfood.domain.reposity.MainRepository
import com.daniel.chickenfood.domain.reposity.OrderRepository
import com.daniel.chickenfood.domain.reposity.RewardsRepository
import com.daniel.chickenfood.presentation.viewModel.MainViewModel
import com.daniel.chickenfood.presentation.viewModel.OrderViewModel
import com.daniel.chickenfood.presentation.viewModel.RewardsViewModel
import com.google.firebase.database.FirebaseDatabase
import com.google.gson.Gson
import org.koin.core.module.dsl.viewModel
import org.koin.dsl.module

val appModule = module {

    // JSON
    single { Gson() }

    // Firebase
    single { FirebaseDatabase.getInstance() }

    // Repositories
    single<MainRepository> { MainRepositoryImpl(get(), get()) }
    single<RewardsRepository> { RewardsRepositoryImpl(get(), get()) }
    single<OrderRepository> { OrderRepositoryImpl(get(), get()) }

    // ViewModels
    viewModel { MainViewModel(repository = get()) }
    viewModel { RewardsViewModel(rewardsRepository = get()) }
    viewModel { OrderViewModel(orderRepository = get(), rewardsRepository = get()) }
}
