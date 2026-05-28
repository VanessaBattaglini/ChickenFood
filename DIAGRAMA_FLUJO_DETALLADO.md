# 📊 DIAGRAMA DETALLADO DEL FLUJO - Paso a Paso

## 🎬 ESCENA 1: SPLASH SCREEN
```
┌─────────────────────────────────────────────────────────────┐
│                    SPLASH ACTIVITY                          │
├─────────────────────────────────────────────────────────────┤
│                                                             │
│  onCreate()                                                │
│    ├─ setContent { SplashScreen(...) }                    │
│    │   ├─ Muestra imagen de introducción                  │
│    │   ├─ Muestra logo del pollo                          │
│    │   ├─ Muestra texto motivacional                      │
│    │   └─ Botón "Get Started"                             │
│    │                                                       │
│    └─ onGetStartedClick()                                 │
│       ├─ startActivity(Intent(MainActivity))              │
│       └─ finish()  ← Cierra SplashActivity                │
│                                                             │
│  ⚠️ PROBLEMA: NO tiene enableEdgeToEdge()                 │
│  ⚠️ PROBLEMA: NO tiene MaterialTheme wrapper              │
│                                                             │
└─────────────────────────────────────────────────────────────┘
                          ↓
                    (Usuario hace click)
                          ↓
```

---

## 🏠 ESCENA 2: MAIN ACTIVITY - DASHBOARD
```
┌─────────────────────────────────────────────────────────────┐
│                    MAIN ACTIVITY                            │
├─────────────────────────────────────────────────────────────┤
│                                                             │
│  onCreate()                                                │
│    ├─ enableEdgeToEdge()  ✅ CORRECTO                      │
│    ├─ setContent { MainScreen(...) }                      │
│    │   ├─ TopBar (búsqueda)                               │
│    │   ├─ SearchBar                                       │
│    │   ├─ Banner (carrusel de imágenes)                   │
│    │   │   └─ Cargado desde Firebase                      │
│    │   ├─ CategorySection                                 │
│    │   │   ├─ Pollo                                       │
│    │   │   ├─ Bebidas                                     │
│    │   │   ├─ Postres                                     │
│    │   │   └─ Otros                                       │
│    │   │       └─ onCategoryClick(category)               │
│    │   │           └─ navigateToItemsList(id, name)       │
│    │   └─ BottomBar                                       │
│    │       ├─ Home                                        │
│    │       ├─ Search                                      │
│    │       ├─ Cart (con badge de cantidad)                │
│    │       └─ Profile                                     │
│    │                                                       │
│    └─ navigateToItemsList(categoryId, categoryName)       │
│       ├─ Intent.putExtra("id", categoryId.toString())     │
│       ├─ Intent.putExtra("title", categoryName)           │
│       └─ startActivity(ItemsListActivity)                 │
│                                                             │
│  ✅ CORRECTO: Tiene enableEdgeToEdge()                    │
│  ✅ CORRECTO: Tiene MaterialTheme wrapper                 │
│                                                             │
└─────────────────────────────────────────────────────────────┘
                          ↓
                    (Usuario hace click en categoría)
                          ↓
```

---

## 📋 ESCENA 3: ITEMS LIST ACTIVITY
```
┌─────────────────────────────────────────────────────────────┐
│                 ITEMS LIST ACTIVITY                         │
├─────────────────────────────────────────────────────────────┤
│                                                             │
│  onCreate()                                                │
│    ├─ categoryId = intent.getStringExtra("id")            │
│    ├─ title = intent.getStringExtra("title")              │
│    ├─ setContent { ItemsListScreen(...) }                 │
│    │   ├─ HeaderSection (título de categoría)             │
│    │   ├─ LaunchedEffect(categoryId)                      │
│    │   │   └─ viewModel.loadFoodsByCategory(categoryId)   │
│    │   │       └─ Carga desde Firebase                    │
│    │   └─ ItemsList                                       │
│    │       ├─ FoodCard 1                                  │
│    │       │   ├─ Imagen                                  │
│    │       │   ├─ Nombre                                  │
│    │       │   ├─ Tiempo                                  │
│    │       │   ├─ Rating                                  │
│    │       │   ├─ Precio                                  │
│    │       │   └─ onClick → onFoodClick(food)             │
│    │       ├─ FoodCard 2                                  │
│    │       ├─ FoodCard 3                                  │
│    │       └─ ...                                         │
│    │                                                       │
│    └─ navigateToDetail(food)                              │
│       ├─ Intent.putExtra("object", food)                  │
│       └─ startActivity(DetailEachFoodActivity)            │
│                                                             │
│  ⚠️ PROBLEMA: NO tiene enableEdgeToEdge()                 │
│  ⚠️ PROBLEMA: NO tiene MaterialTheme wrapper              │
│                                                             │
└─────────────────────────────────────────────────────────────┘
                          ↓
                    (Usuario hace click en producto)
                          ↓
```

---

## 🔍 ESCENA 4: DETAIL EACH FOOD ACTIVITY - PARTE 1 (SETUP)
```
┌─────────────────────────────────────────────────────────────┐
│            DETAIL EACH FOOD ACTIVITY - SETUP                │
├─────────────────────────────────────────────────────────────┤
│                                                             │
│  onCreate()                                                │
│    ├─ enableEdgeToEdge()  ✅ CORRECTO (FIJO)              │
│    ├─ item = intent.getSerializableExtra("object")        │
│    │   └─ Obtiene el FoodModel completo                   │
│    ├─ managmentCart = ManagmentCart(applicationContext)   │
│    ├─ item.numberInCart = 0  ← IMPORTANTE                 │
│    │   └─ Resetea a 0 para que DetailScreen inicie en 1   │
│    ├─ Log.d(TAG, "DetailEachFoodActivity opened...")      │
│    │   └─ Debería aparecer en logcat                      │
│    │                                                       │
│    └─ setContent {                                        │
│       MaterialTheme {  ✅ CORRECTO (FIJO)                 │
│           DetailScreen(                                   │
│               item = item,                                │
│               onBackClick = { finish() },                 │
│               onHomeClick = { navigateToHome() },         │
│               onAddToCartClick = { quantity -> ... }      │
│           )                                               │
│       }                                                   │
│    }                                                      │
│                                                             │
│  ✅ CORRECTO: Tiene enableEdgeToEdge()                    │
│  ✅ CORRECTO: Tiene MaterialTheme wrapper                 │
│                                                             │
└─────────────────────────────────────────────────────────────┘
```

---

## 🎨 ESCENA 5: DETAIL SCREEN - COMPOSABLE
```
┌─────────────────────────────────────────────────────────────┐
│                    DETAIL SCREEN                            │
├─────────────────────────────────────────────────────────────┤
│                                                             │
│  @Composable DetailScreen(...)                             │
│    ├─ Log.d(TAG, "DetailScreen rendering...")             │
│    │   └─ Debería aparecer en logcat                      │
│    ├─ var quantity by remember { mutableIntStateOf(1) }   │
│    │   └─ Estado local: cantidad inicial = 1              │
│    │                                                       │
│    ├─ Column (fillMaxSize, background White)              │
│    │   ├─ Column (scrolleable, weight 1f)                 │
│    │   │   ├─ HeaderSection                               │
│    │   │   │   ├─ AsyncImage (imagen del producto)        │
│    │   │   │   ├─ BackButton                              │
│    │   │   │   ├─ HomeButton                              │
│    │   │   │   ├─ FavoriteButton                          │
│    │   │   │   ├─ Nombre del producto                     │
│    │   │   │   ├─ RowDetail (tiempo, rating, calorías)    │
│    │   │   │   └─ NumberRow                               │
│    │   │   │       ├─ Precio: $${item.price}              │
│    │   │   │       └─ QuantitySelector                    │
│    │   │   │           ├─ Botón "-"                       │
│    │   │   │           │   └─ onClick → quantity--         │
│    │   │   │           ├─ Texto: quantity                 │
│    │   │   │           └─ Botón "+"                       │
│    │   │   │               └─ onClick → quantity++         │
│    │   │   │                                               │
│    │   │   └─ DescriptionSection                          │
│    │   │       └─ Descripción del producto                │
│    │   │                                                   │
│    │   └─ FooterSection (fijo en la parte inferior)        │
│    │       ├─ Surface (sombra, fondo blanco)              │
│    │       ├─ Row                                         │
│    │       │   ├─ Column (lado izquierdo)                 │
│    │       │   │   ├─ Text "Total"                        │
│    │       │   │   └─ Text "$${totalPrice}"               │
│    │       │   │       └─ totalPrice = item.price * qty   │
│    │       │   │                                           │
│    │       │   └─ Button "Agregar al carrito"             │
│    │       │       ├─ onClick = {                         │
│    │       │       │   Log.d(TAG, "Button clicked")       │
│    │       │       │   onAddToCartClick()  ← CALLBACK     │
│    │       │       │ }                                    │
│    │       │       └─ colors: orange                      │
│    │       │                                               │
│    │       └─ Log.d(TAG, "Rendering FooterSection...")    │
│    │           └─ Debería aparecer en logcat              │
│    │                                                       │
│    └─ Log.d(TAG, "DetailScreen rendering...")             │
│        └─ Debería aparecer en logcat                      │
│                                                             │
└─────────────────────────────────────────────────────────────┘
```

---

## 🛒 ESCENA 6: BOTÓN "AGREGAR AL CARRITO" - FLUJO DE CALLBACKS
```
┌─────────────────────────────────────────────────────────────┐
│              FLUJO DE CALLBACKS - BOTÓN CLICK               │
├─────────────────────────────────────────────────────────────┤
│                                                             │
│  1. Usuario hace click en botón "Agregar al carrito"       │
│     ↓                                                       │
│  2. FooterSection.Button.onClick ejecuta:                  │
│     ├─ Log.d(TAG, "Agregar al carrito button clicked")    │
│     └─ onAddToCartClick()  ← Callback 1                   │
│        ↓                                                    │
│  3. DetailScreen.onAddToCartClick ejecuta:                 │
│     ├─ Log.d(TAG, "FooterSection.onAddToCartClick...")    │
│     └─ onAddToCartClick(quantity)  ← Callback 2           │
│        ↓                                                    │
│  4. DetailEachFoodActivity.onAddToCartClick ejecuta:       │
│     ├─ Log.d(TAG, "onAddToCartClick triggered...")        │
│     ├─ item.numberInCart = quantity                       │
│     ├─ Log.d(TAG, "Updated item quantity to...")          │
│     ├─ managmentCart.insertItem(item)                     │
│     │   └─ Guarda en SQLite                               │
│     ├─ Log.d(TAG, "insertItem completed...")              │
│     ├─ lifecycleScope.launch {                            │
│     │   ├─ delay(500)  ← Espera 500ms                     │
│     │   └─ navigateToCart()                               │
│     │       ├─ Intent(CartActivity)                       │
│     │       └─ startActivity(intent)                      │
│     │           ↓                                          │
│     └─ CartActivity se abre                               │
│                                                             │
│  LOGS ESPERADOS EN LOGCAT:                                 │
│  ✓ D/FooterSection: Agregar al carrito button clicked     │
│  ✓ D/DetailScreen: FooterSection.onAddToCartClick...      │
│  ✓ D/DetailEachFoodActivity: onAddToCartClick triggered...│
│  ✓ D/DetailEachFoodActivity: Updated item quantity to...  │
│  ✓ D/DetailEachFoodActivity: insertItem completed...      │
│  ✓ D/DetailEachFoodActivity: Navigating to CartActivity   │
│                                                             │
└─────────────────────────────────────────────────────────────┘
```

---

## 🛒 ESCENA 7: CART ACTIVITY
```
┌─────────────────────────────────────────────────────────────┐
│                    CART ACTIVITY                            │
├─────────────────────────────────────────────────────────────┤
│                                                             │
│  onCreate()                                                │
│    ├─ enableEdgeToEdge()  ✅ CORRECTO (FIJO)              │
│    ├─ managmentCart = ManagmentCart(applicationContext)   │
│    ├─ cartItems = managmentCart.getListCart()             │
│    │   └─ Obtiene lista de productos del carrito          │
│    ├─ setContent { CartScreen(...) }                      │
│    │   ├─ Header                                          │
│    │   │   ├─ BackButton → onHomeClick()                  │
│    │   │   └─ Título "Mi Carrito"                         │
│    │   ├─ LazyColumn (lista de productos)                 │
│    │   │   ├─ CartItemCard 1                              │
│    │   │   │   ├─ Imagen                                  │
│    │   │   │   ├─ Nombre                                  │
│    │   │   │   ├─ Precio unitario                         │
│    │   │   │   ├─ Cantidad                                │
│    │   │   │   ├─ Subtotal                                │
│    │   │   │   └─ Botón Eliminar (X)                      │
│    │   │   ├─ CartItemCard 2                              │
│    │   │   └─ ...                                         │
│    │   └─ CartFooter                                      │
│    │       ├─ Subtotal                                    │
│    │       ├─ Envío                                       │
│    │       ├─ Total                                       │
│    │       └─ Botón "Proceder al Pago"                    │
│    │                                                       │
│    └─ navigateToHome()                                    │
│       ├─ Intent(MainActivity)                             │
│       ├─ flags = CLEAR_TOP | SINGLE_TOP                   │
│       ├─ startActivity(intent)                            │
│       └─ finish()                                         │
│                                                             │
│  ✅ CORRECTO: Tiene enableEdgeToEdge()                    │
│  ⚠️ PROBLEMA: NO tiene MaterialTheme wrapper              │
│                                                             │
└─────────────────────────────────────────────────────────────┘
```

---

## 📊 TABLA DE ESTADO DE CADA ACTIVIDAD

| Actividad | enableEdgeToEdge() | MaterialTheme | Estado |
|-----------|-------------------|---------------|--------|
| SplashActivity | ❌ NO | ❌ NO | ⚠️ PROBLEMA |
| MainActivity | ✅ SÍ | ✅ SÍ | ✅ OK |
| ItemsListActivity | ❌ NO | ❌ NO | ⚠️ PROBLEMA |
| DetailEachFoodActivity | ✅ SÍ | ✅ SÍ | ✅ OK (FIJO) |
| CartActivity | ✅ SÍ | ❌ NO | ⚠️ PROBLEMA |

---

## 🔴 PROBLEMAS CRÍTICOS

### Problema 1: Botón no responde
**Síntomas:**
- Usuario hace click en "Agregar al carrito"
- Nada ocurre
- No aparecen logs en logcat

**Posibles causas:**
1. DetailScreen no está renderizando (FIJO con MaterialTheme)
2. FooterSection no está recibiendo clicks
3. El callback no se está ejecutando
4. Hay un error silencioso

**Solución:**
- Verificar que DetailScreen renderiza (buscar logs)
- Verificar que FooterSection renderiza (buscar logs)
- Verificar que Button recibe clicks (buscar logs)
- Verificar que callback se ejecuta (buscar logs)

### Problema 2: SplashActivity no renderiza correctamente
**Síntomas:**
- Pantalla de splash no se ve bien
- Posible problema de layout

**Solución:**
- Agregar `enableEdgeToEdge()`
- Agregar `MaterialTheme` wrapper

### Problema 3: ItemsListActivity no renderiza correctamente
**Síntomas:**
- Lista de productos no se ve bien
- Posible problema de layout

**Solución:**
- Agregar `enableEdgeToEdge()`
- Agregar `MaterialTheme` wrapper

### Problema 4: CartActivity no renderiza correctamente
**Síntomas:**
- Carrito no se ve bien
- Posible problema de layout

**Solución:**
- Agregar `MaterialTheme` wrapper

---

## 🎯 CHECKLIST DE VERIFICACIÓN

### Paso 1: Verificar que DetailScreen renderiza
- [ ] Buscar en logcat: "D/DetailScreen: DetailScreen rendering for item:"
- [ ] Si aparece → DetailScreen está renderizando ✅
- [ ] Si NO aparece → DetailScreen no está renderizando ❌

### Paso 2: Verificar que FooterSection renderiza
- [ ] Buscar en logcat: "D/DetailScreen: Rendering FooterSection with totalPrice:"
- [ ] Si aparece → FooterSection está renderizando ✅
- [ ] Si NO aparece → FooterSection no está renderizando ❌

### Paso 3: Verificar que Button recibe clicks
- [ ] Buscar en logcat: "D/FooterSection: Agregar al carrito button clicked"
- [ ] Si aparece → Button está recibiendo clicks ✅
- [ ] Si NO aparece → Button no está recibiendo clicks ❌

### Paso 4: Verificar que callback se ejecuta
- [ ] Buscar en logcat: "D/DetailEachFoodActivity: onAddToCartClick triggered for:"
- [ ] Si aparece → Callback está ejecutándose ✅
- [ ] Si NO aparece → Callback no está ejecutándose ❌

### Paso 5: Verificar que producto se agrega al carrito
- [ ] Buscar en logcat: "D/DetailEachFoodActivity: insertItem completed successfully"
- [ ] Si aparece → Producto se agregó al carrito ✅
- [ ] Si NO aparece → Producto NO se agregó al carrito ❌

### Paso 6: Verificar que se navega a CartActivity
- [ ] Buscar en logcat: "D/DetailEachFoodActivity: Navigating to CartActivity"
- [ ] Si aparece → Navegación funcionó ✅
- [ ] Si NO aparece → Navegación NO funcionó ❌

---

## 📝 RESUMEN

El flujo completo es:
1. **SplashActivity** → Usuario hace click en "Get Started"
2. **MainActivity** → Usuario selecciona una categoría
3. **ItemsListActivity** → Usuario selecciona un producto
4. **DetailEachFoodActivity** → Usuario selecciona cantidad y hace click en "Agregar al carrito"
5. **CartActivity** → Se muestra el carrito con el producto agregado

El problema está en el **Paso 4**: El botón "Agregar al carrito" no responde a clicks.

Posibles causas:
- DetailScreen no está renderizando (FIJO con MaterialTheme)
- FooterSection no está renderizando
- Button no está recibiendo clicks
- Callback no se está ejecutando

**Próximo paso:** Ejecutar la app y revisar los logs en logcat para identificar exactamente dónde se detiene el flujo.
