package com.daniel.chickenfood.domain.reposity

import com.daniel.chickenfood.domain.model.BannerModel
import com.daniel.chickenfood.domain.model.CategoryModel
import com.daniel.chickenfood.domain.model.FoodModel
import kotlinx.coroutines.flow.Flow

interface MainRepository {
    fun loadBanner(): Flow<List<BannerModel>>
    fun loadCategory(): Flow<List<CategoryModel>>
    //fun loadFiltered(categoryId: String): Flow<List<FoodModel>>
}