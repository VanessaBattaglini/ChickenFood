package com.daniel.chickenfood.presentation.activity.dashboard

import android.content.Intent
import android.os.Bundle
import android.util.Log
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
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.unit.dp
import com.daniel.chickenfood.R
import com.daniel.chickenfood.helper.ManagmentCart
import com.daniel.chickenfood.presentation.activity.BaseActivity
import com.daniel.chickenfood.presentation.activity.cart.CartActivity
import com.daniel.chickenfood.presentation.activity.itemList.ItemsListActivity
import com.daniel.chickenfood.presentation.viewModel.MainViewModel
import org.koin.androidx.compose.koinViewModel

private const val TAG = "MainActivity"

class MainActivity : BaseActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            MainScreen(
                onCategoryClick = { categoryId, categoryName ->
                    navigateToItemsList(categoryId, categoryName)
                },
                onCartClick = {
                    navigateToCart()
                }
            )
        }
    }

    private fun navigateToItemsList(categoryId: Int, categoryName: String) {
        Log.d(TAG, "navigateToItemsList called with categoryId=$categoryId, categoryName=$categoryName")
        val intent = Intent(this, ItemsListActivity::class.java).apply {
            putExtra("id", categoryId.toString())
            putExtra("title", categoryName)
        }
        Log.d(TAG, "Starting ItemsListActivity with id=${categoryId}")
        startActivity(intent)
    }

    private fun navigateToCart() {
        Log.d(TAG, "navigateToCart called")
        val intent = Intent(this, CartActivity::class.java)
        startActivity(intent)
    }
}

@Composable
fun MainScreen(
    viewModel: MainViewModel = koinViewModel(),
    onCategoryClick: (Int, String) -> Unit = { _, _ -> },
    onCartClick: () -> Unit = {}
) {
    val banners by viewModel.banners.collectAsState()
    val isLoadingBanners by viewModel.isLoadingBanners.collectAsState()
    val categories by viewModel.categories.collectAsState()
    val isLoadingCategories by viewModel.isLoadingCategories.collectAsState()

    var selectedItem by rememberSaveable { mutableStateOf("Home") }
    var cartItemCount by rememberSaveable { mutableIntStateOf(0) }
    
    // Cargar contador de carrito
    val managmentCart = ManagmentCart(androidx.compose.ui.platform.LocalContext.current)
    cartItemCount = managmentCart.getListCart().size

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        containerColor = colorResource(R.color.darkBrown),
        bottomBar = {
            BottomBar(
                selectedItem = selectedItem,
                onItemSelected = { item ->
                    selectedItem = item
                    if (item == "Cart") {
                        onCartClick()
                    }
                },
                cartItemCount = cartItemCount
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
                    isLoading = isLoadingBanners,
                    height = 240
                )
            }
            item {
                CategorySection(
                    categories = categories,
                    isLoading = isLoadingCategories,
                    onCategoryClick = { category ->
                        Log.d(TAG, "Category clicked: id=${category.id}, name=${category.name}")
                        onCategoryClick(category.id, category.name)
                    }
                )
            }
        }
    }
}