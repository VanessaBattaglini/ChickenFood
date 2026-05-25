package com.daniel.chickenfood.presentation.activity.itemList

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.activity.compose.setContent
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.daniel.chickenfood.R
import com.daniel.chickenfood.domain.model.FoodModel
import com.daniel.chickenfood.presentation.activity.BaseActivity
import com.daniel.chickenfood.presentation.activity.detailEachFood.DetailEachFoodActivity
import com.daniel.chickenfood.presentation.viewModel.MainViewModel
import org.koin.androidx.compose.koinViewModel
import org.koin.androidx.viewmodel.ext.android.viewModel

private const val TAG = "ItemsListActivity"

class ItemsListActivity : BaseActivity() {

    private val viewModel: MainViewModel by viewModel()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val categoryId =
            intent.getStringExtra("id").orEmpty()
        val title =
            intent.getStringExtra("title").orEmpty()
        Log.d(TAG, "ItemsListActivity created with categoryId=$categoryId, title=$title")
        setContent {
            ItemsListScreen(
                title = title,
                categoryId = categoryId,
                onBackClick = ::finish,
                onFoodClick = { food ->
                    navigateToDetail(food)
                }
            )
        }
    }

    private fun navigateToDetail(food: FoodModel) {
        Log.d(TAG, "Navigating to detail for food: ${food.title}")
        val intent = Intent(this, DetailEachFoodActivity::class.java).apply {
            putExtra("object", food)
        }
        startActivity(intent)
    }
}

@Composable
fun ItemsListScreen(
    title: String,
    categoryId: String,
    onBackClick: () -> Unit,
    onFoodClick: (FoodModel) -> Unit,
    viewModel: MainViewModel = koinViewModel()
) {
    val foods by viewModel.foods.collectAsState()
    val isLoading by viewModel
        .isLoadingFoods
        .collectAsState()
    
    Log.d(TAG, "ItemsListScreen: foods=${foods.size}, isLoading=$isLoading")
    
    LaunchedEffect(categoryId) {
        Log.d(TAG, "LaunchedEffect triggered with categoryId=$categoryId")
        viewModel.loadFoodsByCategory(
            categoryId
        )
    }
    Column(
        modifier = Modifier.fillMaxSize()
    ) {
        HeaderSection(
            title = title,
            onBackClick = onBackClick
        )
        when {
            isLoading -> {
                Log.d(TAG, "Showing LoadingSection")
                LoadingSection()
            }
            foods.isEmpty() -> {
                Log.d(TAG, "Showing EmptyFoodSection - foods list is empty")
                EmptyFoodSection()
            }
            else -> {
                Log.d(TAG, "Showing ItemsList with ${foods.size} items")
                ItemsList(
                    items = foods,
                    onFoodClick = { food ->
                        Log.d(TAG, "Food clicked: ${food.title}")
                        onFoodClick(food)
                    }
                )
            }
        }
    }
}

@Composable
fun HeaderSection(
    title: String,
    onBackClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            painter = painterResource(R.drawable.back),
            contentDescription = "Back",
            modifier = Modifier
                .size(24.dp)
                .clickable { onBackClick() }
        )
        Text(
            text = title,
            style = MaterialTheme.typography.titleLarge,
            modifier = Modifier.padding(start = 16.dp)
        )
    }
}

@Composable
fun LoadingSection() {
    Box(
        modifier = Modifier
            .fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        CircularProgressIndicator()
    }
}

@Composable
fun EmptyFoodSection() {
    Box(
        modifier = Modifier
            .fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = "No hay alimentos disponibles",
            style = MaterialTheme.typography.bodyLarge
        )
    }
}
