package com.daniel.chickenfood.domain

import com.daniel.chickenfood.domain.model.BannerModel
import com.daniel.chickenfood.domain.model.CategoryModel
import kotlinx.coroutines.flow.Flow

interface MainRepository {

    suspend fun loadBanner(): Flow<List<BannerModel>>

    suspend fun loadCategory(): Flow<List<CategoryModel>>
}