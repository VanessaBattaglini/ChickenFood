package com.daniel.chickenfood.presentation.viewModel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.daniel.chickenfood.domain.model.BannerModel
import com.daniel.chickenfood.domain.model.CategoryModel
import com.daniel.chickenfood.domain.model.FoodModel
import com.daniel.chickenfood.domain.reposity.MainRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

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
            _isLoadingBanners.value = true
            repository.loadBanner().collect { list ->
                _banners.value = list
                _isLoadingBanners.value = false
            }
        }
    }

    // ---------------------------------
    // LOAD CATEGORIES
    // ---------------------------------

    private fun loadCategories() {
        viewModelScope.launch {
            _isLoadingCategories.value = true
            repository.loadCategory().collect { list ->
                _categories.value = list
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
            _isLoadingFoods.value = true
            repository.loadFiltered(categoryId)
                .collect { list ->
                    _foods.value = list
                    _isLoadingFoods.value = false
                }
        }
    }
}