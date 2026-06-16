package com.daniel.chickenfood.presentation.activity.dashboard

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.activity.SystemBarStyle
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawingPadding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.daniel.chickenfood.R
import com.daniel.chickenfood.helper.AuthHelper
import com.daniel.chickenfood.helper.ManagmentCart
import com.daniel.chickenfood.domain.model.FoodModel
import com.daniel.chickenfood.helper.AppConfigs
import com.daniel.chickenfood.presentation.activity.BaseActivity
import com.daniel.chickenfood.presentation.activity.cart.CartActivity
import com.daniel.chickenfood.presentation.activity.detailEachFood.DetailEachFoodActivity
import com.daniel.chickenfood.presentation.activity.itemList.ItemsListActivity
import com.daniel.chickenfood.presentation.activity.splash.SplashActivity
import com.daniel.chickenfood.presentation.viewModel.MainViewModel
import com.daniel.chickenfood.presentation.viewModel.RewardsViewModel
import org.koin.androidx.compose.koinViewModel

private const val TAG = "MainActivity"

// Variables globales para actualizar desde CheckoutActivity/CartActivity
private var cartUpdateCallback: (() -> Unit)? = null
private var rewardsUpdateCallback: (() -> Unit)? = null

class MainActivity : BaseActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge(
            statusBarStyle = SystemBarStyle.dark(
                scrim = android.graphics.Color.TRANSPARENT, // Fondo de la barra transparente
            )
        )
        setContent {
            MainScreen(
                onCategoryClick = { categoryId, categoryName ->
                    navigateToItemsList(categoryId, categoryName)
                },
                onSearchResultClick = { food ->
                    navigateToDetail(food)
                },
                onCartClick = {
                    navigateToCart()
                },
                onLogoutClick = {
                    logout()
                },
                onScreenReady = { cartCallback, rewardsCallback ->
                    // Guardar callbacks para usarlos en onResume
                    cartUpdateCallback = cartCallback
                    rewardsUpdateCallback = rewardsCallback
                }
            )
        }
    }

    override fun onResume() {
        super.onResume()
        Log.d(TAG, "onResume called - updating cart count and rewards")
        // Actualizar contador de carrito cuando regreses a MainActivity
        cartUpdateCallback?.invoke()
        // ✅ NUEVO: Actualizar puntos cuando regreses de CheckoutActivity
        // ✨ MEJORADO: Agregar pequeño delay para asegurar que Firebase actualizó
        Thread {
            Thread.sleep(500)  // Esperar 500ms a que Firebase responda
            rewardsUpdateCallback?.invoke()
        }.start()
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

    private fun navigateToDetail(food: FoodModel) {
        Log.d(TAG, "navigateToDetail called with food id=${food.id}, title=${food.title}")
        val intent = Intent(this, DetailEachFoodActivity::class.java).apply {
            putExtra("food", food)
        }
        Log.d(TAG, "Starting DetailEachFoodActivity with food=${food.title}")
        startActivity(intent)
    }

    private fun navigateToCart() {
        Log.d(TAG, "navigateToCart called")
        val intent = Intent(this, CartActivity::class.java)
        startActivity(intent)
    }

    private fun logout() {
        Log.d(TAG, "Logout clicked")
        AuthHelper.signOut()
        val intent = Intent(this, SplashActivity::class.java)
        startActivity(intent)
        finish()
    }
}

@Composable
fun MainScreen(
    viewModel: MainViewModel = koinViewModel(),
    rewardsViewModel: RewardsViewModel = koinViewModel(),
    onCategoryClick: (Int, String) -> Unit = { _, _ -> },
    onSearchResultClick: (FoodModel) -> Unit = {},
    onCartClick: () -> Unit = {},
    onLogoutClick: () -> Unit = {},
    onScreenReady: ((cartCallback: () -> Unit, rewardsCallback: () -> Unit) -> Unit)? = null  // Actualizado: 2 callbacks
) {
    val banners by viewModel.banners.collectAsState()
    val isLoadingBanners by viewModel.isLoadingBanners.collectAsState()
    val bannerError by viewModel.bannerError.collectAsState()
    
    val categories by viewModel.categories.collectAsState()
    val isLoadingCategories by viewModel.isLoadingCategories.collectAsState()
    val categoryError by viewModel.categoryError.collectAsState()
    
    val isLoadingAll by viewModel.isLoadingAll.collectAsState()

    // Rewards state
    val userRewards by rewardsViewModel.userRewards.collectAsState()
    val rewardsLoading by rewardsViewModel.isLoading.collectAsState()

    var selectedItem by rememberSaveable { mutableStateOf("Home") }
    var cartItemCount by rememberSaveable { mutableIntStateOf(0) }
    
    // ✅ FIX: Obtener currentUser sin persistencia (no usar rememberSaveable para auth)
    // Se obtiene fresh cada vez que el composable se recompone
    val currentUser = AppConfigs.appToken
    
    // Cargar rewards cuando el usuario está autenticado
    LaunchedEffect(currentUser) {
        if (currentUser != null) {
            Log.d(TAG, "Loading rewards for user: $currentUser")
            rewardsViewModel.loadUserRewards(currentUser.userId)
        }
    }
    
    // ✅ NUEVO: Obtener contexto para ManagmentCart
    val context = androidx.compose.ui.platform.LocalContext.current
    
    // ✅ NUEVO: Actualizar contador de carrito - se ejecuta en cada recomposición
    LaunchedEffect(Unit) {
        val managmentCart = ManagmentCart(context)
        val newCount = managmentCart.getListCart().size
        cartItemCount = newCount
        Log.d(TAG, "Cart counter initialized: $newCount items")
    }
    
    // Registrar callbacks para actualización desde onResume
    LaunchedEffect(Unit) {
        onScreenReady?.invoke(
            {
                // Callback para actualizar carrito
                val managmentCart = ManagmentCart(context)
                val newCount = managmentCart.getListCart().size
                cartItemCount = newCount
                Log.d(TAG, "Cart counter updated from onResume: $newCount items")
            },
            {
                // Callback para actualizar rewards
                if (currentUser != null) {
                    Log.d(TAG, "Reloading rewards for user: $currentUser from onResume")
                    rewardsViewModel.loadUserRewards(currentUser.userId)
                }
            }
        )
    }

    // Si está cargando, mostrar loading screen
    if (isLoadingAll) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(colorResource(R.color.darkBrown)),
            contentAlignment = Alignment.Center
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                CircularProgressIndicator(
                    color = colorResource(R.color.orange),
                    modifier = Modifier.size(50.dp)
                )
                Spacer(modifier = Modifier.height(16.dp))
                Text(
                    "Cargando...",
                    color = Color.White,
                    fontSize = 16.sp
                )
            }
        }
        return
    }

    Scaffold(
        modifier = Modifier
            .fillMaxSize(),
        containerColor = colorResource(R.color.darkBrown),
        topBar = {
            TopBar(
                modifier = Modifier
                    .padding(top = 35.dp),
                showLogout = currentUser != null,
                onLogoutClick = onLogoutClick
            )
        },
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
                .safeDrawingPadding()
                .background(colorResource(R.color.darkBrown)),
            verticalArrangement = Arrangement.spacedBy(20.dp),
            contentPadding = PaddingValues(
                bottom = 24.dp
            )
        ) {
            item {
                SearchBar(
                    onSearchResultClick = { food ->
                        Log.d(TAG, "Search result clicked: id=${food.id}, title=${food.title}")
                        onSearchResultClick(food)
                    }
                )
            }
            
            // Mostrar PointsCard si el usuario está autenticado
            if (currentUser != null) {
                item {
                    PointsCard(
                        userRewards = userRewards,
                        modifier = Modifier
                    )
                }
            }
            
            // Mostrar errores si existen
            if (!bannerError.isNullOrEmpty()) {
                item {
                    Text(
                        "Error cargando banners: $bannerError",
                        color = Color.Red,
                        modifier = Modifier.padding(16.dp)
                    )
                }
            }
            
            if (!categoryError.isNullOrEmpty()) {
                item {
                    Text(
                        "Error cargando categorías: $categoryError",
                        color = Color.Red,
                        modifier = Modifier.padding(16.dp)
                    )
                }
            }
            
            item {
                Banner(
                    banners = banners,
                    isLoading = isLoadingBanners,
                    height = 200
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