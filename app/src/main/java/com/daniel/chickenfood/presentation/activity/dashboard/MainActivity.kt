package com.daniel.chickenfood.presentation.activity.dashboard

import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.unit.dp
import com.daniel.chickenfood.R
import com.daniel.chickenfood.presentation.activity.BaseActivity
import com.daniel.chickenfood.presentation.viewModel.MainViewModel
import org.koin.androidx.compose.koinViewModel

class MainActivity : BaseActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            MainScreen()
        }
    }
}

@Composable
fun MainScreen(
    viewModel: MainViewModel = koinViewModel()
) {
    val banners by viewModel.banners.collectAsState()
    var selectedItem by rememberSaveable {
        mutableStateOf("Home")
    }

    val categories by viewModel.categories.collectAsState()
    val isLoadingCategories by viewModel.isLoadingCategories.collectAsState()

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        containerColor = colorResource(R.color.darkBrown),
        bottomBar = {
            BottomBar(
                selectedItem = selectedItem,
                onItemSelected = {
                    selectedItem = it
                }
            )
        }
    ) { paddingValues ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .background(colorResource(R.color.darkBrown)),
            verticalArrangement = Arrangement.spacedBy(20.dp),
            contentPadding = PaddingValues(
                bottom = 24.dp
            )
        ) {
            item {
                TopBar()
            }
            item {
                SearchBar()
            }
            item {
                Banner(
                    banners = banners,
                    isLoading = banners.isEmpty()
                )
            }
            item {
                CategorySection(
                    categories = categories,
                    isLoading = isLoadingCategories,
                    onCategoryClick = {
                    }
                )
            }
        }
    }
}