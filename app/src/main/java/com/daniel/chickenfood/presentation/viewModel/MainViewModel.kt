package com.daniel.chickenfood.presentation.viewModel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.daniel.chickenfood.domain.MainRepository
import com.daniel.chickenfood.domain.model.BannerModel
import com.daniel.chickenfood.domain.model.CategoryModel
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
    private val _isLoadingBanners = MutableStateFlow(true)
    val isLoadingBanners = _isLoadingBanners.asStateFlow()

    // ---------------------------------
    // CATEGORIES
    // ---------------------------------
    private val _categories =
        MutableStateFlow<List<CategoryModel>>(emptyList())

    val categories = _categories.asStateFlow()
    private val _isLoadingCategories = MutableStateFlow(true)

    val isLoadingCategories = _isLoadingCategories.asStateFlow()

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
}