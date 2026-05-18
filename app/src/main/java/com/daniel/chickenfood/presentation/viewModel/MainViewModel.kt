package com.daniel.chickenfood.presentation.viewModel

import android.R.attr.banner
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.daniel.chickenfood.domain.MainRepository
import com.daniel.chickenfood.domain.model.BannerModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class MainViewModel(
    private val repository: MainRepository
): ViewModel() {

    private val _banners =
        MutableStateFlow<List<BannerModel>>(emptyList())

    val banners = _banners.asStateFlow()

    init {
        viewModelScope.launch {

            repository.loadBanner().collect{

                _banners.value = it
            }
        }
    }
}