package com.daniel.chickenfood.di

import com.google.firebase.database.FirebaseDatabase
import org.koin.dsl.module

val appModule = module {
    //Firebase
    single {
        FirebaseDatabase.getInstance()
    }
}
   /* // Repository
    single<ChickenRepository> {
        ChickenRepositoryImpl(get())
    }
*/
/*
    // UseCase
    factory {
        GetChickenListUseCase(get())
    }
*/
/*// ViewModel
viewModel {
    HomeViewModel(get())
}
*/

