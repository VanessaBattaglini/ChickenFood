# 🔄 FLUJO COMPLETO: Desde Splash hasta Botón "Agregar al Carrito"

## 📱 PASO 1: SPLASH SCREEN (SplashActivity)
**Archivo:** `presentation/activity/splash/SplashActivity.kt`

### Qué ocurre:
1. La app inicia y carga `SplashActivity` (definida en AndroidManifest.xml como LAUNCHER)
2. Se llama `onCreate()`
3. Se ejecuta `setContent { SplashScreen(...) }`
4. Se muestra la pantalla de bienvenida con:
   - Imagen de introducción
   - Logo del pollo
   - Texto motivacional
   - Botón "Get Started"

### Código clave:
```kotlin
class SplashActivity : BaseActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            SplashScreen(
                onGetStartedClick = {
                    startActivity(Intent(this, MainActivity::class.java))
                    finish()  // Cierra SplashActivity
                }
            )
        }
    }
}
```

### Información importante:
- ⚠️ **PROBLEMA DETECTADO**: SplashActivity NO tiene `enableEdgeToEdge()` ni `MaterialTheme`
- El usuario hace click en "Get Started"
- Se inicia `MainActivity`

---

## 🏠 PASO 2: MAIN ACTIVITY - DASHBOARD (MainActivity)
**Archivo:** `presentation/activity/dashboard/MainActivity.kt`

### Qué ocurre:
1. Se llama `onCreate()`
2. Se ejecuta `enableEdgeToEdge()` ✅ (CORRECTO)
3. Se ejecuta `setContent { MainScreen(...) }`
4. Se muestra el dashboard con:
   - TopBar (búsqueda)
   - Banners (carrusel)
   - Categorías (Pollo, Bebidas, Postres, etc.)
   - BottomBar (navegación inferior)

### Código clave:
```kotlin
class MainActivity : BaseActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()  // ✅ CORRECTO
        setContent {
            MainScreen(
                onCategoryClick = { categoryId, categoryName ->
                    navigateToItemsList(categoryId, categoryName)
                },
                onCartClick = { navigateToCart() }
            )
        }
    }

    private fun navigateToItemsList(categoryId: Int, categoryName: String) {
        val intent = Intent(this, ItemsListActivity::class.java).apply {
            putExtra("id", categoryId.toString())
            putExtra("title", categoryName)
        }
        startActivity(intent)
    }
}
```

### Información importante:
- Se cargan banners desde Firebase (MainViewModel)
- Se cargan categorías desde Firebase (MainViewModel)
- El usuario ve todas las categorías disponibles
- El BottomBar muestra el contador de productos en el carrito

### Datos que fluyen:
```
MainViewModel.banners → Banner composable
MainViewModel.categories → CategorySection composable
ManagmentCart.getListCart().size → BottomBar badge
```

---

## 📋 PASO 3: SELECCIONAR CATEGORÍA (CategorySection)
**Archivo:** `presentation/activity/dashboard/CategorySection.kt`

### Qué ocurre:
1. El usuario ve las categorías (Pollo, Bebidas, Postres, etc.)
2. El usuario hace click en una categoría (ej: "Pollo")
3. Se ejecuta el callback `onCategoryClick(category.id, category.name)`
4. MainActivity recibe el callback y llama `navigateToItemsList(categoryId, categoryName)`

### Código clave:
```kotlin
// En MainActivity.MainScreen
CategorySection(
    categories = categories,
    isLoading = isLoadingCategories,
    onCategoryClick = { category ->
        Log.d(TAG, "Category clicked: id=${category.id}, name=${category.name}")
        onCategoryClick(category.id, category.name)  // ← Callback
    }
)

// En MainActivity
private fun navigateToItemsList(categoryId: Int, categoryName: String) {
    Log.d(TAG, "navigateToItemsList called with categoryId=$categoryId, categoryName=$categoryName")
    val intent = Intent(this, ItemsListActivity::class.java).apply {
        putExtra("id", categoryId.toString())
        putExtra("title", categoryName)
    }
    startActivity(intent)
}
```

### Información importante:
- Se pasa `categoryId` como String en el Intent
- Se pasa `categoryName` como String en el Intent
- Se inicia `ItemsListActivity`

### Datos que fluyen:
```
Category.id (Int) → Intent extra "id" (String)
Category.name (String) → Intent extra "title" (String)
```

---

## 🍗 PASO 4: LISTA DE PRODUCTOS (ItemsListActivity)
**Archivo:** `presentation/activity/itemList/ItemListActivity.kt`

### Qué ocurre:
1. Se llama `onCreate()`
2. Se extrae `categoryId` y `title` del Intent
3. Se ejecuta `setContent { ItemsListScreen(...) }`
4. Se muestra la lista de productos de esa categoría

### Código clave:
```kotlin
class ItemsListActivity : BaseActivity() {
    private val viewModel: MainViewModel by viewModel()
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val categoryId = intent.getStringExtra("id").orEmpty()
        val title = intent.getStringExtra("title").orEmpty()
        Log.d(TAG, "ItemsListActivity created with categoryId=$categoryId, title=$title")
        
        setContent {
            ItemsListScreen(
                title = title,
                categoryId = categoryId,
                onBackClick = ::finish,
                onFoodClick = { food ->
                    navigateToDetail(food)  // ← Callback cuando hace click en un producto
                }
            )
        }
    }

    private fun navigateToDetail(food: FoodModel) {
        Log.d(TAG, "Navigating to detail for food: ${food.title}")
        val intent = Intent(this, DetailEachFoodActivity::class.java).apply {
            putExtra("object", food)  // ← Pasa el objeto FoodModel completo
        }
        startActivity(intent)
    }
}
```

### En ItemsListScreen:
```kotlin
@Composable
fun ItemsListScreen(
    title: String,
    categoryId: String,
    onBackClick: () -> Unit,
    onFoodClick: (FoodModel) -> Unit,
    viewModel: MainViewModel = koinViewModel()
) {
    val foods by viewModel.foods.collectAsState()
    val isLoading by viewModel.isLoadingFoods.collectAsState()
    
    LaunchedEffect(categoryId) {
        Log.d(TAG, "LaunchedEffect triggered with categoryId=$categoryId")
        viewModel.loadFoodsByCategory(categoryId)  // ← Carga productos de Firebase
    }
    
    // Muestra la lista de productos
    ItemsList(
        items = foods,
        onFoodClick = { food ->
            Log.d(TAG, "Food clicked: ${food.title}")
            onFoodClick(food)  // ← Callback al hacer click
        }
    )
}
```

### Información importante:
- ⚠️ **PROBLEMA DETECTADO**: ItemsListActivity NO tiene `enableEdgeToEdge()` ni `MaterialTheme`
- Se carga la lista de productos desde Firebase usando `viewModel.loadFoodsByCategory(categoryId)`
- El usuario ve todos los productos de esa categoría
- El usuario hace click en un producto (ej: "Combo Pollo Asado")

### Datos que fluyen:
```
categoryId (String) → viewModel.loadFoodsByCategory()
Firebase → MainViewModel.foods (List<FoodModel>)
FoodModel → Intent extra "object"
```

---

## 🔍 PASO 5: DETALLE DEL PRODUCTO (DetailEachFoodActivity)
**Archivo:** `presentation/activity/detailEachFood/DetailEachFoodActivity.kt`

### Qué ocurre:
1. Se llama `onCreate()`
2. Se extrae el objeto `FoodModel` del Intent
3. Se ejecuta `enableEdgeToEdge()` ✅ (AHORA CORRECTO)
4. Se resetea `item.numberInCart = 0`
5. Se ejecuta `setContent { MaterialTheme { DetailScreen(...) } }` ✅ (AHORA CORRECTO)
6. Se muestra la pantalla de detalle con:
   - Imagen del producto
   - Nombre del producto
   - Tiempo de preparación, rating, calorías
   - Descripción
   - Selector de cantidad (+/- botones)
   - Precio total
   - Botón "Agregar al carrito"

### Código clave:
```kotlin
class DetailEachFoodActivity : BaseActivity() {
    private lateinit var item: FoodModel
    private lateinit var managmentCart: ManagmentCart

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()  // ✅ CORRECTO

        item = intent.getSerializableExtra("object") as FoodModel
        managmentCart = ManagmentCart(applicationContext)
        item.numberInCart = 0  // ← Resetea a 0 para que DetailScreen inicie en 1

        Log.d(TAG, "DetailEachFoodActivity opened with item: ${item.title} (id=${item.id})")

        setContent {
            MaterialTheme {  // ✅ CORRECTO - Tema envuelto
                DetailScreen(
                    item = item,
                    onBackClick = { finish() },
                    onHomeClick = { navigateToHome() },
                    onAddToCartClick = { quantity ->
                        Log.d(TAG, "onAddToCartClick triggered for: ${item.title}, quantity: $quantity")
                        try {
                            item.numberInCart = quantity
                            managmentCart.insertItem(item)
                            
                            lifecycleScope.launch {
                                delay(500)
                                navigateToCart()
                            }
                        } catch (e: Exception) {
                            Log.e(TAG, "Error in onAddToCartClick: ${e.message}", e)
                            Toast.makeText(this@DetailEachFoodActivity, "Error: ${e.message}", Toast.LENGTH_LONG).show()
                        }
                    }
                )
            }
        }
    }

    private fun navigateToCart() {
        Log.d(TAG, "Navigating to CartActivity")
        val intent = Intent(this, CartActivity::class.java)
        startActivity(intent)
    }

    private fun navigateToHome() {
        Log.d(TAG, "Navigating to MainActivity")
        val intent = Intent(this, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP
        }
        startActivity(intent)
    }
}
```

### En DetailScreen:
```kotlin
@Composable
fun DetailScreen(
    item: FoodModel,
    onBackClick: () -> Unit,
    onHomeClick: () -> Unit = {},
    onAddToCartClick: (quantity: Int) -> Unit,
    modifier: Modifier = Modifier
) {
    Log.d(TAG, "DetailScreen rendering for item: ${item.title}")

    var quantity by remember {
        mutableIntStateOf(1)  // ← Inicia en 1
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(Color.White)
    ) {
        // Contenido scrolleable
        Column(
            modifier = Modifier
                .fillMaxSize()
                .weight(1f)
                .verticalScroll(rememberScrollState())
        ) {
            HeaderSection(
                item = item,
                quantity = quantity,
                onBackClick = onBackClick,
                onHomeClick = onHomeClick,
                onIncrement = {
                    Log.d(TAG, "Increment clicked, quantity: $quantity -> ${quantity + 1}")
                    quantity++
                },
                onDecrement = {
                    if (quantity > 1) {
                        Log.d(TAG, "Decrement clicked, quantity: $quantity -> ${quantity - 1}")
                        quantity--
                    }
                }
            )
            DescriptionSection(description = item.description)
        }
        
        // Footer fijo en la parte inferior
        FooterSection(
            totalPrice = (item.price * quantity).toDouble(),
            onAddToCartClick = {
                Log.d(TAG, "FooterSection.onAddToCartClick called with quantity: $quantity")
                onAddToCartClick(quantity)  // ← Pasa la cantidad al callback
            }
        )
    }
}
```

### En FooterSection:
```kotlin
@Composable
fun FooterSection(
    totalPrice: Double,
    onAddToCartClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Surface(
        modifier = modifier.fillMaxWidth(),
        shadowElevation = 8.dp,
        color = Color.White
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp, vertical = 16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text(text = "Total", style = MaterialTheme.typography.bodyMedium, color = Color.Gray)
                Text(
                    text = "$${"%,.0f".format(totalPrice)}",
                    style = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.Bold,
                    color = colorResource(R.color.orange)
                )
            }
            Button(
                onClick = {
                    Log.d(TAG, "Agregar al carrito button clicked")
                    onAddToCartClick()  // ← Ejecuta el callback
                },
                shape = RoundedCornerShape(16.dp),
                colors = ButtonDefaults.buttonColors(containerColor = colorResource(R.color.orange))
            ) {
                Text(text = "Agregar al carrito")
            }
        }
    }
}
```

### Información importante:
- ✅ DetailScreen ahora renderiza correctamente (con `enableEdgeToEdge()` y `MaterialTheme`)
- El usuario ve la cantidad inicial en 1
- El usuario puede aumentar/disminuir la cantidad con los botones +/-
- El precio total se actualiza dinámicamente: `item.price * quantity`
- El usuario hace click en "Agregar al carrito"

### Datos que fluyen:
```
FoodModel (del Intent) → DetailScreen
quantity (estado local) → FooterSection
quantity → onAddToCartClick callback
```

---

## 🛒 PASO 6: BOTÓN "AGREGAR AL CARRITO" (El Problema)
**Archivo:** `presentation/activity/detailEachFood/DetailEachFoodActivity.kt`

### Qué DEBERÍA ocurrir:
1. El usuario hace click en el botón "Agregar al carrito"
2. Se ejecuta `FooterSection.onAddToCartClick()`
3. Se ejecuta `DetailScreen.onAddToCartClick(quantity)`
4. Se ejecuta `DetailEachFoodActivity.onAddToCartClick { quantity -> ... }`
5. Se actualiza `item.numberInCart = quantity`
6. Se llama `managmentCart.insertItem(item)`
7. Se espera 500ms
8. Se navega a `CartActivity`

### Código que DEBERÍA ejecutarse:
```kotlin
onAddToCartClick = { quantity ->
    Log.d(TAG, "onAddToCartClick triggered for: ${item.title}, quantity: $quantity")
    try {
        item.numberInCart = quantity
        Log.d(TAG, "Updated item quantity to: ${item.numberInCart}")
        
        managmentCart.insertItem(item)
        Log.d(TAG, "insertItem completed successfully")
        
        lifecycleScope.launch {
            delay(500)
            navigateToCart()
        }
    } catch (e: Exception) {
        Log.e(TAG, "Error in onAddToCartClick: ${e.message}", e)
        Toast.makeText(this@DetailEachFoodActivity, "Error: ${e.message}", Toast.LENGTH_LONG).show()
    }
}
```

### ⚠️ PROBLEMA ACTUAL:
El botón NO responde a clicks. Posibles causas:

1. **DetailScreen no está renderizando** (aunque ahora debería con los fixes)
2. **FooterSection no está recibiendo los clicks** (problema de captura de eventos)
3. **El callback no se está ejecutando** (problema de composición)
4. **Hay un error silencioso** (excepción no capturada)

### Logs esperados en logcat:
```
D/DetailEachFoodActivity: DetailEachFoodActivity opened with item: Combo Pollo Asado (id=6)
D/DetailScreen: DetailScreen rendering for item: Combo Pollo Asado
D/FooterSection: Agregar al carrito button clicked
D/DetailEachFoodActivity: onAddToCartClick triggered for: Combo Pollo Asado, quantity: 2
D/DetailEachFoodActivity: Updated item quantity to: 2
D/DetailEachFoodActivity: insertItem completed successfully
D/DetailEachFoodActivity: Navigating to CartActivity
```

---

## 🔗 FLUJO COMPLETO EN DIAGRAMA

```
SplashActivity
    ↓ (click "Get Started")
MainActivity (Dashboard)
    ↓ (click Categoría)
ItemsListActivity (Lista de productos)
    ↓ (click Producto)
DetailEachFoodActivity (Detalle del producto)
    ↓ (click "Agregar al carrito")
CartActivity (Carrito de compras)
```

---

## 📊 RESUMEN DE FUNCIONES Y CALLBACKS

| Paso | Actividad | Función | Callback | Siguiente |
|------|-----------|---------|----------|-----------|
| 1 | SplashActivity | `onCreate()` | `onGetStartedClick()` | MainActivity |
| 2 | MainActivity | `onCreate()` | `onCategoryClick()` | ItemsListActivity |
| 3 | ItemsListActivity | `onCreate()` | `onFoodClick()` | DetailEachFoodActivity |
| 4 | DetailEachFoodActivity | `onCreate()` | `onAddToCartClick()` | CartActivity |
| 5 | CartActivity | `onCreate()` | `onHomeClick()` | MainActivity |

---

## 🔴 PROBLEMAS IDENTIFICADOS

### 1. SplashActivity
- ❌ NO tiene `enableEdgeToEdge()`
- ❌ NO tiene `MaterialTheme` wrapper
- **Impacto**: Posible problema de renderización

### 2. ItemsListActivity
- ❌ NO tiene `enableEdgeToEdge()`
- ❌ NO tiene `MaterialTheme` wrapper
- **Impacto**: Posible problema de renderización

### 3. DetailEachFoodActivity
- ✅ TIENE `enableEdgeToEdge()` (FIJO)
- ✅ TIENE `MaterialTheme` wrapper (FIJO)
- ⚠️ Botón aún no responde (INVESTIGAR)

### 4. CartActivity
- ✅ TIENE `enableEdgeToEdge()` (FIJO)
- ❌ NO tiene `MaterialTheme` wrapper
- **Impacto**: Posible problema de renderización

---

## 🎯 PRÓXIMOS PASOS

1. **Verificar que DetailScreen está renderizando** (agregar logs visuales)
2. **Verificar que FooterSection está recibiendo clicks** (agregar logs en Button)
3. **Verificar que el callback se ejecuta** (revisar logcat)
4. **Agregar MaterialTheme a SplashActivity e ItemsListActivity**
5. **Agregar MaterialTheme a CartActivity**
6. **Hacer prueba end-to-end completa**
