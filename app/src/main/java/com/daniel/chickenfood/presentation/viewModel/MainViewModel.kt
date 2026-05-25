package com.daniel.chickenfood.presentation.viewModel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.daniel.chickenfood.domain.model.BannerModel
import com.daniel.chickenfood.domain.model.CategoryModel
import com.daniel.chickenfood.domain.model.FoodModel
import com.daniel.chickenfood.domain.reposity.MainRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

private const val TAG = "MainViewModel"

class MainViewModel(
    private val repository: MainRepository
) : ViewModel() {
    // ---------------------------------
    // BANNERS
    // ---------------------------------
    private val _banners =
        MutableStateFlow<List<BannerModel>>(emptyList())
    val banners = _banners.asStateFlow()
    private val _isLoadingBanners =
        MutableStateFlow(false)
    val isLoadingBanners =
        _isLoadingBanners.asStateFlow()

    // ---------------------------------
    // CATEGORIES
    // ---------------------------------
    private val _categories =
        MutableStateFlow<List<CategoryModel>>(emptyList())
    val categories =
        _categories.asStateFlow()
    private val _isLoadingCategories =
        MutableStateFlow(false)
    val isLoadingCategories =
        _isLoadingCategories.asStateFlow()

    // ---------------------------------
    // FOODS
    // ---------------------------------
    private val _foods =
        MutableStateFlow<List<FoodModel>>(emptyList())
    val foods =
        _foods.asStateFlow()
    private val _isLoadingFoods =
        MutableStateFlow(false)
    val isLoadingFoods =
        _isLoadingFoods.asStateFlow()

    // ---------------------------------
    // SELECTED CATEGORY
    // ---------------------------------
    private val _selectedCategoryId =
        MutableStateFlow<String?>(null)
    val selectedCategoryId =
        _selectedCategoryId.asStateFlow()
    // ---------------------------------
    // INIT
    // ---------------------------------

    init {
        loadBanners()
        loadCategories()
    }

    // ---------------------------------
    // LOAD BANNERS
    // ---------------------------------

    private fun loadBanners() {
        viewModelScope.launch {
            try {
                _isLoadingBanners.value = true
                Log.d(TAG, "Loading banners...")
                val list = repository.loadBanner().first()
                Log.d(TAG, "Banners loaded: ${list.size} items")
                list.forEachIndexed { index, banner ->
                    Log.d(TAG, "Banner $index: image='${banner.image}'")
                }
                _banners.value = list
            } catch (e: Exception) {
                Log.e(TAG, "Error loading banners", e)
                e.printStackTrace()
            } finally {
                _isLoadingBanners.value = false
            }
        }
    }

    // ---------------------------------
    // LOAD CATEGORIES
    // ---------------------------------

    private fun loadCategories() {
        viewModelScope.launch {
            try {
                _isLoadingCategories.value = true
                Log.d(TAG, "Loading categories...")
                val list = repository.loadCategory().first()
                Log.d(TAG, "Categories loaded: ${list.size} items")
                list.forEachIndexed { index, category ->
                    Log.d(TAG, "Category $index: id=${category.id}, name='${category.name}', imagePath='${category.imagePath}'")
                }
                _categories.value = list
            } catch (e: Exception) {
                Log.e(TAG, "Error loading categories", e)
                e.printStackTrace()
            } finally {
                _isLoadingCategories.value = false
            }
        }
    }

    // ---------------------------------
    // LOAD FOODS BY CATEGORY
    // ---------------------------------

    fun loadFoodsByCategory(
        categoryId: String
    ) {
        _selectedCategoryId.value = categoryId
        viewModelScope.launch {
            try {
                _isLoadingFoods.value = true
                Log.d(TAG, "Loading foods for category: $categoryId")
                val list = repository.loadFiltered(categoryId).first()
                Log.d(TAG, "Foods loaded: ${list.size} items for category $categoryId")
                list.forEachIndexed { index, food ->
                    Log.d(TAG, "Food $index: id='${food.id}', title='${food.title}', categoryId='${food.categoryId}'")
                }
                _foods.value = list
            } catch (e: Exception) {
                Log.e(TAG, "Error loading foods for category $categoryId", e)
                e.printStackTrace()
            } finally {
                _isLoadingFoods.value = false
            }
        }
    }
}