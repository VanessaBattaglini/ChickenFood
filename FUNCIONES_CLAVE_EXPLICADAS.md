# 🔧 FUNCIONES CLAVE EXPLICADAS

## 1️⃣ SplashActivity.onCreate()
**Archivo:** `presentation/activity/splash/SplashActivity.kt`

```kotlin
override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    
    setContent {
        SplashScreen(
            onGetStartedClick = {
                startActivity(Intent(this, MainActivity::class.java))
                finish()
            }
        )
    }
}
```

**Qué hace:**
- Se ejecuta cuando la app inicia
- Llama a `setContent` para mostrar la UI de Compose
- Pasa un callback `onGetStartedClick` que se ejecuta cuando el usuario hace click en "Get Started"
- El callback inicia `MainActivity` y cierra `SplashActivity`

**Información importante:**
- ⚠️ NO tiene `enableEdgeToEdge()` → Posible problema de renderización
- ⚠️ NO tiene `MaterialTheme` wrapper → Posible problema de renderización

---

## 2️⃣ MainActivity.onCreate()
**Archivo:** `presentation/activity/dashboard/MainActivity.kt`

```kotlin
override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    enableEdgeToEdge()  // ✅ CORRECTO
    
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
```

**Qué hace:**
- Se ejecuta cuando se inicia MainActivity
- Llama a `enableEdgeToEdge()` para permitir que la UI se extienda a los bordes
- Llama a `setContent` para mostrar la UI de Compose
- Pasa dos callbacks:
  - `onCategoryClick`: Se ejecuta cuando el usuario hace click en una categoría
  - `onCartClick`: Se ejecuta cuando el usuario hace click en "Cart" en el BottomBar

**Información importante:**
- ✅ TIENE `enableEdgeToEdge()` → Renderización correcta
- ✅ TIENE `MaterialTheme` wrapper (en MainScreen) → Renderización correcta

---

## 3️⃣ MainActivity.navigateToItemsList()
**Archivo:** `presentation/activity/dashboard/MainActivity.kt`

```kotlin
private fun navigateToItemsList(categoryId: Int, categoryName: String) {
    Log.d(TAG, "navigateToItemsList called with categoryId=$categoryId, categoryName=$categoryName")
    
    val intent = Intent(this, ItemsListActivity::class.java).apply {
        putExtra("id", categoryId.toString())
        putExtra("title", categoryName)
    }
    
    Log.d(TAG, "Starting ItemsListActivity with id=${categoryId}")
    startActivity(intent)
}
```

**Qué hace:**
- Crea un Intent para iniciar `ItemsListActivity`
- Pasa dos datos en el Intent:
  - `"id"`: El ID de la categoría (convertido a String)
  - `"title"`: El nombre de la categoría
- Inicia la actividad con `startActivity(intent)`

**Información importante:**
- El `categoryId` se convierte a String con `.toString()`
- Se registra en logcat para debugging
- Los datos se pasan como "extras" en el Intent

---

## 4️⃣ ItemsListActivity.onCreate()
**Archivo:** `presentation/activity/itemList/ItemListActivity.kt`

```kotlin
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
                navigateToDetail(food)
            }
        )
    }
}
```

**Qué hace:**
- Se ejecuta cuando se inicia ItemsListActivity
- Extrae los datos del Intent:
  - `categoryId`: El ID de la categoría
  - `title`: El nombre de la categoría
- Llama a `setContent` para mostrar la UI de Compose
- Pasa dos callbacks:
  - `onBackClick`: Se ejecuta cuando el usuario hace click en "Back"
  - `onFoodClick`: Se ejecuta cuando el usuario hace click en un producto

**Información importante:**
- ⚠️ NO tiene `enableEdgeToEdge()` → Posible problema de renderización
- ⚠️ NO tiene `MaterialTheme` wrapper → Posible problema de renderización
- Usa `.orEmpty()` para evitar null si los extras no existen

---

## 5️⃣ ItemsListActivity.navigateToDetail()
**Archivo:** `presentation/activity/itemList/ItemListActivity.kt`

```kotlin
private fun navigateToDetail(food: FoodModel) {
    Log.d(TAG, "Navigating to detail for food: ${food.title}")
    
    val intent = Intent(this, DetailEachFoodActivity::class.java).apply {
        putExtra("object", food)
    }
    
    startActivity(intent)
}
```

**Qué hace:**
- Crea un Intent para iniciar `DetailEachFoodActivity`
- Pasa el objeto `FoodModel` completo en el Intent
- Inicia la actividad con `startActivity(intent)`

**Información importante:**
- Pasa el objeto `FoodModel` completo (no solo el ID)
- El objeto debe ser `Serializable` para poder pasarlo en el Intent
- Se registra en logcat para debugging

---

## 6️⃣ ItemsListScreen (Composable)
**Archivo:** `presentation/activity/itemList/ItemListActivity.kt`

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
    
    Log.d(TAG, "ItemsListScreen: foods=${foods.size}, isLoading=$isLoading")
    
    LaunchedEffect(categoryId) {
        Log.d(TAG, "LaunchedEffect triggered with categoryId=$categoryId")
        viewModel.loadFoodsByCategory(categoryId)
    }
    
    Column(modifier = Modifier.fillMaxSize()) {
        HeaderSection(title = title, onBackClick = onBackClick)
        
        when {
            isLoading -> LoadingSection()
            foods.isEmpty() -> EmptyFoodSection()
            else -> ItemsList(
                items = foods,
                onFoodClick = { food ->
                    Log.d(TAG, "Food clicked: ${food.title}")
                    onFoodClick(food)
                }
            )
        }
    }
}
```

**Qué hace:**
- Es un Composable que muestra la lista de productos
- Obtiene la lista de productos del ViewModel
- Cuando se monta, ejecuta `LaunchedEffect` que carga los productos de Firebase
- Muestra:
  - HeaderSection (título)
  - LoadingSection (si está cargando)
  - EmptyFoodSection (si no hay productos)
  - ItemsList (si hay productos)
- Cuando el usuario hace click en un producto, ejecuta `onFoodClick(food)`

**Información importante:**
- `LaunchedEffect` se ejecuta cuando `categoryId` cambia
- `viewModel.loadFoodsByCategory()` carga desde Firebase
- El estado se obtiene con `collectAsState()`

---

## 7️⃣ DetailEachFoodActivity.onCreate()
**Archivo:** `presentation/activity/detailEachFood/DetailEachFoodActivity.kt`

```kotlin
override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    enableEdgeToEdge()  // ✅ CORRECTO (FIJO)

    item = intent.getSerializableExtra("object") as FoodModel
    managmentCart = ManagmentCart(applicationContext)
    item.numberInCart = 0  // ← IMPORTANTE

    Log.d(TAG, "DetailEachFoodActivity opened with item: ${item.title} (id=${item.id})")

    setContent {
        MaterialTheme {  // ✅ CORRECTO (FIJO)
            DetailScreen(
                item = item,
                onBackClick = { finish() },
                onHomeClick = { navigateToHome() },
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
            )
        }
    }
}
```

**Qué hace:**
- Se ejecuta cuando se inicia DetailEachFoodActivity
- Extrae el objeto `FoodModel` del Intent
- Crea una instancia de `ManagmentCart`
- **IMPORTANTE**: Resetea `item.numberInCart = 0` para que DetailScreen inicie en 1
- Llama a `enableEdgeToEdge()` para permitir que la UI se extienda a los bordes
- Llama a `setContent` con `MaterialTheme` wrapper
- Pasa tres callbacks:
  - `onBackClick`: Se ejecuta cuando el usuario hace click en "Back"
  - `onHomeClick`: Se ejecuta cuando el usuario hace click en "Home"
  - `onAddToCartClick`: Se ejecuta cuando el usuario hace click en "Agregar al carrito"

**Información importante:**
- ✅ TIENE `enableEdgeToEdge()` → Renderización correcta
- ✅ TIENE `MaterialTheme` wrapper → Renderización correcta
- El callback `onAddToCartClick` recibe la cantidad como parámetro
- Usa `lifecycleScope.launch` para ejecutar código asincrónico
- Usa `delay(500)` para esperar antes de navegar

---

## 8️⃣ DetailScreen (Composable)
**Archivo:** `presentation/activity/detailEachFood/DetailScreen.kt`

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
        
        Log.d(TAG, "Rendering FooterSection with totalPrice: ${item.price * quantity}")
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

**Qué hace:**
- Es un Composable que muestra el detalle del producto
- Mantiene un estado local `quantity` que inicia en 1
- Muestra:
  - HeaderSection (imagen, botones, nombre, detalles)
  - DescriptionSection (descripción)
  - FooterSection (precio total, botón "Agregar al carrito")
- Cuando el usuario hace click en "+", incrementa `quantity`
- Cuando el usuario hace click en "-", decrementa `quantity` (mínimo 1)
- Cuando el usuario hace click en "Agregar al carrito", ejecuta `onAddToCartClick(quantity)`

**Información importante:**
- El estado `quantity` es local a este Composable
- Se actualiza dinámicamente cuando el usuario hace click en +/-
- El precio total se calcula como `item.price * quantity`
- Se registra en logcat para debugging

---

## 9️⃣ FooterSection (Composable)
**Archivo:** `presentation/activity/detailEachFood/FooterSection.kt`

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
                Text(
                    text = "Total",
                    style = MaterialTheme.typography.bodyMedium,
                    color = Color.Gray
                )
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
                colors = ButtonDefaults.buttonColors(
                    containerColor = colorResource(R.color.orange)
                )
            ) {
                Text(text = "Agregar al carrito")
            }
        }
    }
}
```

**Qué hace:**
- Es un Composable que muestra el footer con el precio total y el botón
- Muestra:
  - Texto "Total"
  - Precio total formateado
  - Botón "Agregar al carrito"
- Cuando el usuario hace click en el botón, ejecuta `onAddToCartClick()`

**Información importante:**
- El botón es el que el usuario hace click
- El callback `onAddToCartClick` se ejecuta cuando el usuario hace click
- Se registra en logcat para debugging

---

## 🔟 DetailEachFoodActivity.navigateToCart()
**Archivo:** `presentation/activity/detailEachFood/DetailEachFoodActivity.kt`

```kotlin
private fun navigateToCart() {
    Log.d(TAG, "Navigating to CartActivity")
    val intent = Intent(this, CartActivity::class.java)
    startActivity(intent)
}
```

**Qué hace:**
- Crea un Intent para iniciar `CartActivity`
- Inicia la actividad con `startActivity(intent)`

**Información importante:**
- Se ejecuta después de que el producto se agrega al carrito
- Se registra en logcat para debugging

---

## 1️⃣1️⃣ DetailEachFoodActivity.navigateToHome()
**Archivo:** `presentation/activity/detailEachFood/DetailEachFoodActivity.kt`

```kotlin
private fun navigateToHome() {
    Log.d(TAG, "Navigating to MainActivity")
    val intent = Intent(this, MainActivity::class.java).apply {
        flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP
    }
    startActivity(intent)
}
```

**Qué hace:**
- Crea un Intent para iniciar `MainActivity`
- Usa flags para limpiar la pila de actividades
- Inicia la actividad con `startActivity(intent)`

**Información importante:**
- `FLAG_ACTIVITY_CLEAR_TOP`: Limpia todas las actividades encima de MainActivity
- `FLAG_ACTIVITY_SINGLE_TOP`: Si MainActivity ya está en la pila, no crea una nueva instancia
- Se registra en logcat para debugging

---

## 1️⃣2️⃣ ManagmentCart.insertItem()
**Archivo:** `helper/ManagmentCart.kt`

```kotlin
fun insertItem(item: FoodModel) {
    // Obtiene la lista actual del carrito
    val listCart = getListCart()
    
    // Busca si el producto ya existe en el carrito
    val existingItem = listCart.find { it.id == item.id }
    
    if (existingItem != null) {
        // Si existe, REEMPLAZA la cantidad (no suma)
        existingItem.numberInCart = item.numberInCart
    } else {
        // Si no existe, lo agrega a la lista
        listCart.add(item)
    }
    
    // Guarda la lista actualizada en SQLite
    saveListCart(listCart)
}
```

**Qué hace:**
- Obtiene la lista actual del carrito
- Busca si el producto ya existe (por ID)
- Si existe, REEMPLAZA la cantidad
- Si no existe, lo agrega a la lista
- Guarda la lista actualizada en SQLite

**Información importante:**
- ✅ REEMPLAZA la cantidad (no suma)
- Usa el `id` del producto para identificarlo (no el nombre)
- Guarda en SQLite para persistencia

---

## 1️⃣3️⃣ CartActivity.onCreate()
**Archivo:** `presentation/activity/cart/CartActivity.kt`

```kotlin
override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    enableEdgeToEdge()  // ✅ CORRECTO (FIJO)
    
    managmentCart = ManagmentCart(applicationContext)
    
    Log.d(TAG, "CartActivity opened")
    val cartItems = managmentCart.getListCart()
    Log.d(TAG, "Cart items loaded: ${cartItems.size} items")
    for (item in cartItems) {
        Log.d(TAG, "  - ${item.title} x${item.numberInCart} = $${item.price * item.numberInCart}")
    }

    setContent {
        CartScreen(
            managmentCart = managmentCart,
            onBackClick = { finish() },
            onHomeClick = { navigateToHome() }
        )
    }
}
```

**Qué hace:**
- Se ejecuta cuando se inicia CartActivity
- Crea una instancia de `ManagmentCart`
- Obtiene la lista de productos del carrito
- Registra en logcat todos los productos del carrito
- Llama a `setContent` para mostrar la UI de Compose
- Pasa dos callbacks:
  - `onBackClick`: Se ejecuta cuando el usuario hace click en "Back"
  - `onHomeClick`: Se ejecuta cuando el usuario hace click en "Home"

**Información importante:**
- ✅ TIENE `enableEdgeToEdge()` → Renderización correcta
- ⚠️ NO tiene `MaterialTheme` wrapper → Posible problema de renderización
- Se registra en logcat para debugging

---

## 📊 RESUMEN DE FLUJO DE FUNCIONES

```
1. SplashActivity.onCreate()
   ↓ (usuario hace click)
2. MainActivity.onCreate()
   ↓ (usuario selecciona categoría)
3. MainActivity.navigateToItemsList()
   ↓
4. ItemsListActivity.onCreate()
   ↓ (usuario selecciona producto)
5. ItemsListActivity.navigateToDetail()
   ↓
6. DetailEachFoodActivity.onCreate()
   ↓ (usuario selecciona cantidad y hace click)
7. FooterSection.Button.onClick()
   ↓
8. DetailScreen.onAddToCartClick()
   ↓
9. DetailEachFoodActivity.onAddToCartClick()
   ↓
10. ManagmentCart.insertItem()
    ↓
11. DetailEachFoodActivity.navigateToCart()
    ↓
12. CartActivity.onCreate()
```

---

## 🔍 DÓNDE BUSCAR LOGS

| Función | Log esperado | Archivo |
|---------|-------------|---------|
| SplashActivity.onCreate() | (sin logs específicos) | logcat |
| MainActivity.onCreate() | (sin logs específicos) | logcat |
| MainActivity.navigateToItemsList() | "navigateToItemsList called with..." | logcat |
| ItemsListActivity.onCreate() | "ItemsListActivity created with..." | logcat |
| ItemsListScreen | "ItemsListScreen: foods=..." | logcat |
| ItemsListActivity.navigateToDetail() | "Navigating to detail for food:..." | logcat |
| DetailEachFoodActivity.onCreate() | "DetailEachFoodActivity opened with item:..." | logcat |
| DetailScreen | "DetailScreen rendering for item:..." | logcat |
| FooterSection.Button.onClick() | "Agregar al carrito button clicked" | logcat |
| DetailScreen.onAddToCartClick() | "FooterSection.onAddToCartClick called..." | logcat |
| DetailEachFoodActivity.onAddToCartClick() | "onAddToCartClick triggered for:..." | logcat |
| ManagmentCart.insertItem() | (sin logs específicos) | logcat |
| DetailEachFoodActivity.navigateToCart() | "Navigating to CartActivity" | logcat |
| CartActivity.onCreate() | "CartActivity opened" | logcat |
