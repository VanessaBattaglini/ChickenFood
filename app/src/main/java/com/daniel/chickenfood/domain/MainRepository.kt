package com.daniel.chickenfood.domain

import com.daniel.chickenfood.domain.model.BannerModel
import kotlinx.coroutines.flow.Flow

interface MainRepository {

    suspend fun loadBanner(): Flow<List<BannerModel>>
}