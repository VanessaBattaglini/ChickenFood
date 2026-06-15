package com.daniel.chickenfood.di

import com.daniel.chickenfood.data.repository.MainRepositoryImpl
import com.daniel.chickenfood.data.repository.OrderRepositoryImpl
import com.daniel.chickenfood.data.repository.PointTransactionRepositoryImpl
import com.daniel.chickenfood.data.repository.RewardsRepositoryImpl
import com.daniel.chickenfood.data.repository.TokenRepositoryImpl
import com.daniel.chickenfood.data.service.MockPaymentServiceImpl
import com.daniel.chickenfood.domain.reposity.MainRepository
import com.daniel.chickenfood.domain.reposity.OrderRepository
import com.daniel.chickenfood.domain.reposity.PointTransactionRepository
import com.daniel.chickenfood.domain.reposity.RewardsRepository
import com.daniel.chickenfood.domain.reposity.TokenRepository
import com.daniel.chickenfood.data.service.MockPaymentService
import com.daniel.chickenfood.presentation.viewModel.MainViewModel
import com.daniel.chickenfood.presentation.viewModel.OrderViewModel
import com.daniel.chickenfood.presentation.viewModel.RewardsViewModel
import com.daniel.chickenfood.presentation.viewModel.TokenViewModel
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
    single<TokenRepository> { TokenRepositoryImpl(get(), get()) }
    single<PointTransactionRepository> { PointTransactionRepositoryImpl(get(), get()) }

    // Services
    single<MockPaymentService> { MockPaymentServiceImpl() }

    // ViewModels
    viewModel { MainViewModel(repository = get()) }
    viewModel { RewardsViewModel(rewardsRepository = get()) }
    viewModel { OrderViewModel(orderRepository = get(), rewardsRepository = get()) }
    viewModel { TokenViewModel(tokenRepository = get()) }
}
