package com.daniel.chickenfood.presentation.activity.itemList

import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.daniel.chickenfood.R
import com.daniel.chickenfood.presentation.activity.BaseActivity
import com.daniel.chickenfood.presentation.viewModel.MainViewModel
import org.koin.androidx.compose.koinViewModel
import org.koin.androidx.viewmodel.ext.android.viewModel

class ItemsListActivity : BaseActivity() {

    private val viewModel: MainViewModel by viewModel()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val categoryId =
            intent.getStringExtra("id").orEmpty()
        val title =
            intent.getStringExtra("title").orEmpty()
        setContent {
            ItemsListScreen(
                title = title,
                categoryId = categoryId,
                onBackClick = ::finish
            )
        }
    }
}
@Composable
fun ItemsListScreen(
    title: String,
    categoryId: String,
    onBackClick: () -> Unit,
    viewModel: MainViewModel = koinViewModel()
) {
    val foods by viewModel.foods.collectAsState()
    val isLoading by viewModel
        .isLoadingFoods
        .collectAsState()
    LaunchedEffect(categoryId) {
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
                LoadingSection()
            }
            foods.isEmpty() -> {
                EmptyFoodSection()
            }
            else -> {
                ItemsList(
                    items = foods
                )
            }
        }
    }
}