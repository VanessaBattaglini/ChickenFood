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
import kotlinx.coroutines.withTimeoutOrNull

private const val TAG = "MainViewModel"
private const val LOAD_TIMEOUT_MS = 10000L  // 10 segundos timeout

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
    private val _bannerError =
        MutableStateFlow<String?>(null)
    val bannerError = _bannerError.asStateFlow()

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
    private val _categoryError =
        MutableStateFlow<String?>(null)
    val categoryError = _categoryError.asStateFlow()

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
    // OVERALL LOADING STATE
    // ---------------------------------
    private val _isLoadingAll = MutableStateFlow(true)
    val isLoadingAll = _isLoadingAll.asStateFlow()
    
    // ---------------------------------
    // SEARCH
    // ---------------------------------
    private val _searchQuery = MutableStateFlow("")
    val searchQuery = _searchQuery.asStateFlow()
    
    private val _searchResults = MutableStateFlow<List<FoodModel>>(emptyList())
    val searchResults = _searchResults.asStateFlow()
    
    private val _isSearching = MutableStateFlow(false)
    val isSearching = _isSearching.asStateFlow()
    
    // ---------------------------------
    // INIT
    // ---------------------------------

    init {
        Log.d(TAG, "MainViewModel initialized, starting data load...")
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
                _bannerError.value = null
                Log.d(TAG, "Loading banners... (timeout: ${LOAD_TIMEOUT_MS}ms)")
                
                val list = withTimeoutOrNull(LOAD_TIMEOUT_MS) {
                    repository.loadBanner().first()
                }
                
                if (list != null) {
                    Log.d(TAG, "✅ Banners loaded successfully: ${list.size} items")
                    list.forEachIndexed { index, banner ->
                        Log.d(TAG, "  Banner $index: image='${banner.image}'")
                    }
                    _banners.value = list
                } else {
                    Log.w(TAG, "⚠️ Banners load timed out after ${LOAD_TIMEOUT_MS}ms")
                    _bannerError.value = "Timeout loading banners"
                    _banners.value = emptyList()
                }
            } catch (e: Exception) {
                Log.e(TAG, "❌ Error loading banners: ${e.message}", e)
                _bannerError.value = e.message ?: "Unknown error loading banners"
                _banners.value = emptyList()
            } finally {
                _isLoadingBanners.value = false
                updateOverallLoadingState()
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
                _categoryError.value = null
                Log.d(TAG, "Loading categories... (timeout: ${LOAD_TIMEOUT_MS}ms)")
                
                val list = withTimeoutOrNull(LOAD_TIMEOUT_MS) {
                    repository.loadCategory().first()
                }
                
                if (list != null) {
                    Log.d(TAG, "✅ Categories loaded successfully: ${list.size} items")
                    list.forEachIndexed { index, category ->
                        Log.d(TAG, "  Category $index: id=${category.id}, name='${category.name}'")
                    }
                    _categories.value = list
                } else {
                    Log.w(TAG, "⚠️ Categories load timed out after ${LOAD_TIMEOUT_MS}ms")
                    _categoryError.value = "Timeout loading categories"
                    _categories.value = emptyList()
                }
            } catch (e: Exception) {
                Log.e(TAG, "❌ Error loading categories: ${e.message}", e)
                _categoryError.value = e.message ?: "Unknown error loading categories"
                _categories.value = emptyList()
            } finally {
                _isLoadingCategories.value = false
                updateOverallLoadingState()
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
                Log.d(TAG, "Loading foods for category: $categoryId (timeout: ${LOAD_TIMEOUT_MS}ms)")
                
                val list = withTimeoutOrNull(LOAD_TIMEOUT_MS) {
                    repository.loadFiltered(categoryId).first()
                }
                
                if (list != null) {
                    Log.d(TAG, "✅ Foods loaded successfully: ${list.size} items for category $categoryId")
                    list.forEachIndexed { index, food ->
                        Log.d(TAG, "  Food $index: id='${food.id}', title='${food.title}'")
                    }
                    _foods.value = list
                } else {
                    Log.w(TAG, "⚠️ Foods load timed out after ${LOAD_TIMEOUT_MS}ms for category $categoryId")
                }
            } catch (e: Exception) {
                Log.e(TAG, "❌ Error loading foods for category $categoryId: ${e.message}", e)
            } finally {
                _isLoadingFoods.value = false
            }
        }
    }
    
    // ---------------------------------
    // UPDATE OVERALL LOADING STATE
    // ---------------------------------
    
    private fun updateOverallLoadingState() {
        val isLoading = _isLoadingBanners.value || _isLoadingCategories.value
        _isLoadingAll.value = isLoading
        Log.d(TAG, "Overall loading state: $isLoading (banners: ${_isLoadingBanners.value}, categories: ${_isLoadingCategories.value})")
    }
    
    // ---------------------------------
    // SEARCH FOODS
    // ---------------------------------
    
    fun searchFoods(query: String) {
        _searchQuery.value = query
        
        if (query.isBlank()) {
            _searchResults.value = emptyList()
            _isSearching.value = false
            Log.d(TAG, "Search cleared")
            return
        }
        
        viewModelScope.launch {
            try {
                _isSearching.value = true
                Log.d(TAG, "Searching foods for query: '$query'")
                
                // Buscar en todas las categorías
                val allFoods = mutableListOf<FoodModel>()
                
                // Obtener todas las categorías
                val allCategories = _categories.value
                Log.d(TAG, "Searching across ${allCategories.size} categories")
                
                for (category in allCategories) {
                    try {
                        val foodsInCategory = withTimeoutOrNull(LOAD_TIMEOUT_MS) {
                            repository.loadFiltered(category.id.toString()).first()
                        }
                        
                        if (foodsInCategory != null) {
                            allFoods.addAll(foodsInCategory)
                            Log.d(TAG, "  Loaded ${foodsInCategory.size} foods from category '${category.name}'")
                        }
                    } catch (e: Exception) {
                        Log.e(TAG, "Error loading foods from category '${category.name}': ${e.message}")
                    }
                }
                
                // Filtrar por query (búsqueda case-insensitive)
                val filtered = allFoods.filter { food ->
                    food.title.lowercase().contains(query.lowercase()) ||
                    food.description.lowercase().contains(query.lowercase())
                }
                
                Log.d(TAG, "✅ Search results: ${filtered.size} items found for '$query'")
                filtered.forEachIndexed { index, food ->
                    Log.d(TAG, "  Result $index: '${food.title}' from category ${food.categoryId}")
                }
                
                _searchResults.value = filtered
            } catch (e: Exception) {
                Log.e(TAG, "❌ Error searching foods: ${e.message}", e)
                _searchResults.value = emptyList()
            } finally {
                _isSearching.value = false
            }
        }
    }
    
    fun clearSearch() {
        _searchQuery.value = ""
        _searchResults.value = emptyList()
        Log.d(TAG, "Search cleared")
    }
}