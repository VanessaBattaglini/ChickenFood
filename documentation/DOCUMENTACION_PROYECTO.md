# 📱 Documentación Completa - Proyecto ChickenFood

## Tabla de Contenidos
1. [Cambios Recientes](#cambios-recientes)
2. [Introducción](#introducción)
3. [Arquitectura Clean Architecture](#arquitectura-clean-architecture)
4. [AndroidManifest.xml](#androidmanifestxml)
5. [Inyección de Dependencias (Koin)](#inyección-de-dependencias-koin)
6. [Capas de la Aplicación](#capas-de-la-aplicación)
7. [Flujo de Datos](#flujo-de-datos)
8. [Componentes Principales](#componentes-principales)
9. [Conexiones entre Archivos](#conexiones-entre-archivos)

---

## Cambios Recientes

### Versión 1.1 - Mejoras en el Carrito (2026-05-26)

#### ✅ Persistencia de Cantidades
- **Problema**: Las cantidades seleccionadas en DetailScreen no se guardaban en el carrito
- **Solución**: DetailEachFoodActivity ahora preserva `numberInCart` si es mayor a 0
- **Archivo**: `DetailEachFoodActivity.kt`

#### ✅ Notificaciones Toast
- **Mejora**: Al agregar producto al carrito, muestra Toast con nombre y cantidad
- **Formato**: "Producto x Cantidad agregado al carrito"
- **Archivo**: `ManagmentCart.kt` - método `insertItem()`

#### ✅ Botón de Eliminar
- **Cambio**: Reemplaza controles +/- por botón de eliminar (✕)
- **Beneficio**: UX más clara y simple
- **Archivo**: `CartActivity.kt` - composable `CartItemCard`

#### ✅ Visualización de Cantidades
- **Mejora**: Muestra cantidad como "Cantidad: X" (no editable en carrito)
- **Razón**: Las cantidades se seleccionan en DetailScreen, no en el carrito
- **Archivo**: `CartActivity.kt` - composable `CartItemCard`

#### ✅ Cálculos Correctos
- **Subtotal**: Precio × Cantidad para cada item
- **Total**: Suma de todos los subtotales
- **Archivo**: `ManagmentCart.kt` - método `getTotalFee()`

#### 📝 Documentación Actualizada
- Agregada sección "Helper Classes" con explicación de ManagmentCart
- Actualizado flujo de datos con detalles del carrito
- Mejorada documentación de DetailEachFoodActivity
- Actualizada estructura de carpetas

---

## Introducción

**ChickenFood** es una aplicación Android de e-commerce de comida rápida desarrollada con:
- **Lenguaje**: Kotlin
- **Arquitectura**: Clean Architecture + MVVM
- **UI Framework**: Jetpack Compose
- **Base de Datos**: Firebase Realtime Database
- **Inyección de Dependencias**: Koin
- **Serialización**: Gson

### Objetivo Principal
Permitir a los usuarios:
1. Ver categorías de productos
2. Seleccionar productos de una categoría
3. Ajustar cantidades
4. Agregar al carrito
5. Ver el carrito con detalles de compra

---

## Arquitectura Clean Architecture

Clean Architecture divide la aplicación en 3 capas principales:

```
┌─────────────────────────────────────────┐
│      PRESENTATION LAYER (UI)            │
│  Activities, Composables, ViewModels    │
└─────────────────────────────────────────┘
                    ↓
┌─────────────────────────────────────────┐
│      DOMAIN LAYER (Lógica de Negocio)   │
│  Interfaces, Modelos, Use Cases         │
└─────────────────────────────────────────┘
                    ↓
┌─────────────────────────────────────────┐
│      DATA LAYER (Fuentes de Datos)      │
│  Repositorios, Firebase, Local Storage  │
└─────────────────────────────────────────┘
```

### Ventajas de Clean Architecture
- **Independencia de Frameworks**: Fácil cambiar Firebase por otra BD
- **Testabilidad**: Cada capa puede testearse independientemente
- **Mantenibilidad**: Código organizado y fácil de entender
- **Escalabilidad**: Fácil agregar nuevas funcionalidades

---

## AndroidManifest.xml

El archivo `AndroidManifest.xml` es el descriptor de la aplicación. Define:

### Permisos Requeridos
```xml
<uses-permission android:name="android.permission.INTERNET" />
<uses-permission android:name="android.permission.ACCESS_NETWORK_STATE" />
```
- **INTERNET**: Necesario para conectarse a Firebase
- **ACCESS_NETWORK_STATE**: Verificar estado de la conexión

### Configuración de la Aplicación
```xml
<application
    android:name=".ChickenFoodApp"
    android:theme="@style/Theme.ChickenFood">
```
- **android:name**: Clase Application personalizada (ChickenFoodApp)
- **android:theme**: Tema visual de la app

### Activities Registradas

#### 1. **SplashActivity** (LAUNCHER)
```xml
<activity
    android:name=".presentation.activity.splash.SplashActivity"
    android:exported="true">
    <intent-filter>
        <action android:name="android.intent.action.MAIN" />
        <category android:name="android.intent.category.LAUNCHER" />
    </intent-filter>
</activity>
```
- **Punto de entrada** de la aplicación
- Se muestra primero cuando se abre la app
- Navega a MainActivity después de 2-3 segundos

#### 2. **MainActivity** (Dashboard)
```xml
<activity
    android:name=".presentation.activity.dashboard.MainActivity"
    android:exported="false" />
```
- Pantalla principal con categorías y banners
- Muestra:
  - Banner carousel (imágenes promocionales)
  - Categorías de productos
  - Bottom navigation bar

#### 3. **ItemsListActivity**
```xml
<activity
    android:name=".presentation.activity.itemList.ItemsListActivity"
    android:exported="false" />
```
- Lista de productos de una categoría seleccionada
- Permite hacer click en un producto para ver detalles

#### 4. **DetailEachFoodActivity**
```xml
<activity
    android:name=".presentation.activity.detailEachFood.DetailEachFoodActivity"
    android:exported="false" />
```
- Pantalla de detalle de un producto
- Permite ajustar cantidad
- Botón para agregar al carrito

#### 5. **CartActivity**
```xml
<activity
    android:name=".presentation.activity.cart.CartActivity"
    android:exported="false" />
```
- Carrito de compras
- Muestra items agregados, cantidad, precio y total

---

## Inyección de Dependencias (Koin)

### ¿Qué es Koin?
Koin es un framework de inyección de dependencias para Kotlin. Permite:
- Crear instancias de objetos automáticamente
- Inyectar dependencias en Activities y ViewModels
- Centralizar la configuración de dependencias

### ChickenFoodApp.kt

```kotlin
class ChickenFoodApp : Application() {
    override fun onCreate() {
        super.onCreate()
        startKoin {
            androidLogger(Level.DEBUG)
            androidContext(this@ChickenFoodApp)
            modules(appModule)
        }
    }
}
```

**Función**: Inicializa Koin cuando la aplicación se inicia
- **startKoin**: Inicia el contenedor de Koin
- **androidLogger**: Muestra logs de Koin en Logcat
- **androidContext**: Proporciona contexto de Android a Koin
- **modules**: Carga los módulos de configuración

### AppModule.kt

```kotlin
val appModule = module {
    // JSON
    single { Gson() }
    
    // Firebase
    single { FirebaseDatabase.getInstance() }
    
    // Repository
    single<MainRepository> { MainRepositoryImpl(get(), get()) }
    
    // ViewModels
    viewModel { MainViewModel(repository = get()) }
}
```

**Función**: Define todas las dependencias de la aplicación

#### Explicación de cada dependencia:

1. **Gson (single)**
   ```kotlin
   single { Gson() }
   ```
   - Crea una única instancia de Gson
   - Se usa para serializar/deserializar JSON desde Firebase
   - `single` = Singleton (misma instancia siempre)

2. **FirebaseDatabase (single)**
   ```kotlin
   single { FirebaseDatabase.getInstance() }
   ```
   - Obtiene la instancia de Firebase Realtime Database
   - Se usa para conectarse a la base de datos

3. **MainRepository (single)**
   ```kotlin
   single<MainRepository> { MainRepositoryImpl(get(), get()) }
   ```
   - Crea instancia de MainRepositoryImpl
   - `get()` obtiene las dependencias automáticamente:
     - Primer `get()` = FirebaseDatabase
     - Segundo `get()` = Gson
   - `single<MainRepository>` = Interfaz, `MainRepositoryImpl` = Implementación

4. **MainViewModel (viewModel)**
   ```kotlin
   viewModel { MainViewModel(repository = get()) }
   ```
   - Crea instancia de MainViewModel
   - `get()` obtiene MainRepository automáticamente
   - `viewModel` = Scope especial para ViewModels

### Cómo se Inyectan las Dependencias

En una Activity o Composable:
```kotlin
class MainActivity : BaseActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MainScreen()
        }
    }
}

@Composable
fun MainScreen(
    viewModel: MainViewModel = koinViewModel()
) {
    // viewModel se inyecta automáticamente
}
```

---

## Capas de la Aplicación

### 1. PRESENTATION LAYER (Capa de Presentación)

**Ubicación**: `presentation/`

**Responsabilidades**:
- Mostrar UI al usuario
- Manejar interacciones del usuario
- Mostrar datos del ViewModel

**Componentes**:

#### Activities
- **SplashActivity**: Pantalla de inicio
- **MainActivity**: Dashboard principal
- **ItemsListActivity**: Lista de productos
- **DetailEachFoodActivity**: Detalle de producto
- **CartActivity**: Carrito de compras

#### Composables (Jetpack Compose)
- **Banner.kt**: Carousel de banners
- **CategorySection.kt**: Grid de categorías
- **ItemList.kt**: Lista de productos
- **HeaderSection.kt**: Encabezado con imagen
- **FooterSection.kt**: Pie con precio y botón

#### ViewModels
- **MainViewModel.kt**: Gestiona estado de banners, categorías y productos

### 2. DOMAIN LAYER (Capa de Dominio)

**Ubicación**: `domain/`

**Responsabilidades**:
- Definir interfaces de repositorios
- Definir modelos de datos
- Contener lógica de negocio

**Componentes**:

#### Interfaces
```
domain/reposity/
├── MainRepository.kt
```

**MainRepository.kt**:
```kotlin
interface MainRepository {
    fun loadBanner(): Flow<List<BannerModel>>
    fun loadCategory(): Flow<List<CategoryModel>>
    fun loadFiltered(categoryId: String): Flow<List<FoodModel>>
}
```

Define qué métodos debe implementar el repositorio.

#### Modelos
```
domain/model/
├── BannerModel.kt
├── CategoryModel.kt
├── FoodModel.kt
```

**BannerModel.kt**:
```kotlin
@Serializable
data class BannerModel(
    @SerializedName("image")
    val image: String = ""
)
```
- Representa un banner promocional
- `@SerializedName`: Mapea el campo JSON "image" al atributo Kotlin

**CategoryModel.kt**:
```kotlin
@Serializable
data class CategoryModel(
    @SerializedName("id")
    val id: Int = 0,
    @SerializedName("imagePath")
    val imagePath: String = "",
    @SerializedName("name")
    val name: String = ""
)
```
- Representa una categoría de productos
- Campos: id, nombre, imagen

**FoodModel.kt**:
```kotlin
@Serializable
data class FoodModel (
    @SerializedName("bestFood")val bestFood: Boolean = true,
    @SerializedName("calorie") val calorie: Int = 0,
    @SerializedName("categoryId")val categoryId: Int = 0,
    @SerializedName("id") val id: Int = 0,
    @SerializedName("description")val description: String = "",
    @SerializedName("imagePath")val imagePath: String = "",
    @SerializedName("price")val price: Int = 0,
    @SerializedName("star")val star: Double = 0.0,
    @SerializedName("timeValue")val timeValue: Int = 0,
    @SerializedName("title")val title: String = "",
    var numberInCart: Int = 0
) : Serializable
```
- Representa un producto de comida
- Implementa `Serializable` para pasar entre Activities

### 3. DATA LAYER (Capa de Datos)

**Ubicación**: `data/`

**Responsabilidades**:
- Obtener datos de Firebase
- Convertir datos JSON a modelos Kotlin
- Implementar la interfaz del repositorio

**Componentes**:

#### Repositorio
```
data/repository/
├── MainRepositoryImpl.kt
```

**MainRepositoryImpl.kt**:
```kotlin
class MainRepositoryImpl(
    private val database: FirebaseDatabase,
    private val gson: Gson
) : MainRepository {
    
    override fun loadBanner(): Flow<List<BannerModel>> = callbackFlow {
        val ref = database.getReference("banners")
        val listener = object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                try {
                    val banners = snapshot.children.mapNotNull { child ->
                        val json = gson.toJson(child.value)
                        gson.fromJson(json, BannerModel::class.java)
                    }
                    trySend(banners)
                } catch (e: Exception) {
                    close(e)
                }
            }
            override fun onCancelled(error: DatabaseError) {
                close(error.toException())
            }
        }
        ref.addValueEventListener(listener)
        awaitClose { ref.removeEventListener(listener) }
    }
}
```

**Explicación**:
1. **callbackFlow**: Crea un Flow que emite datos
2. **getReference("banners")**: Obtiene referencia a la rama "banners" en Firebase
3. **addValueEventListener**: Escucha cambios en los datos
4. **onDataChange**: Se ejecuta cuando hay datos nuevos
5. **gson.toJson/fromJson**: Convierte entre JSON y objetos Kotlin
6. **trySend**: Emite los datos al Flow
7. **awaitClose**: Limpia el listener cuando el Flow se cancela

---

## ViewModel

### MainViewModel.kt

```kotlin
class MainViewModel(
    private val repository: MainRepository
) : ViewModel() {
    
    private val _banners = MutableStateFlow<List<BannerModel>>(emptyList())
    val banners = _banners.asStateFlow()
    
    private val _isLoadingBanners = MutableStateFlow(false)
    val isLoadingBanners = _isLoadingBanners.asStateFlow()
    
    init {
        loadBanners()
        loadCategories()
    }
    
    private fun loadBanners() {
        viewModelScope.launch {
            try {
                _isLoadingBanners.value = true
                val list = repository.loadBanner().first()
                _banners.value = list
            } finally {
                _isLoadingBanners.value = false
            }
        }
    }
}
```

**Función**: Gestiona el estado de la UI

**Conceptos clave**:

1. **MutableStateFlow**: Estado mutable
   - `_banners`: Estado privado (solo lectura interna)
   - `banners`: Estado público (solo lectura externa)

2. **StateFlow**: Estado observable
   - La UI se actualiza automáticamente cuando cambia
   - `asStateFlow()`: Convierte MutableStateFlow a StateFlow

3. **viewModelScope**: Scope de corrutina
   - Las corrutinas se cancelan cuando el ViewModel se destruye
   - Evita memory leaks

4. **first()**: Obtiene el primer valor del Flow
   - Espera a que el Flow emita un valor
   - Luego completa la corrutina

5. **try-finally**: Manejo de errores
   - `finally` siempre se ejecuta, incluso si hay error
   - Asegura que `isLoadingBanners` se establezca en false

---

## Flujo de Datos

### Flujo Completo de una Compra

```
1. Usuario abre la app
   ↓
2. SplashActivity se muestra (2-3 segundos)
   ↓
3. MainActivity se abre
   ↓
4. MainViewModel.init() se ejecuta
   ├─ loadBanners()
   │  ├─ repository.loadBanner()
   │  │  ├─ Firebase obtiene datos
   │  │  ├─ Gson convierte JSON a BannerModel
   │  │  └─ Flow emite List<BannerModel>
   │  └─ _banners.value = list
   │
   └─ loadCategories()
      ├─ repository.loadCategory()
      │  ├─ Firebase obtiene datos
      │  ├─ Gson convierte JSON a CategoryModel
      │  └─ Flow emite List<CategoryModel>
      └─ _categories.value = list
   ↓
5. UI se actualiza con banners y categorías
   ↓
6. Usuario hace click en una categoría
   ↓
7. MainActivity.navigateToItemsList(categoryId)
   ├─ Crea Intent a ItemsListActivity
   └─ Pasa categoryId como extra
   ↓
8. ItemsListActivity se abre
   ↓
9. ItemsListScreen.LaunchedEffect(categoryId)
   ├─ viewModel.loadFoodsByCategory(categoryId)
   │  ├─ repository.loadFiltered(categoryId)
   │  │  ├─ Firebase query por categoryId
   │  │  ├─ Gson convierte JSON a FoodModel
   │  │  └─ Flow emite List<FoodModel>
   │  └─ _foods.value = list
   └─ UI muestra lista de productos
   ↓
10. Usuario hace click en un producto
    ├─ ItemsListActivity.navigateToDetail(food)
    └─ Crea Intent a DetailEachFoodActivity
    ↓
11. DetailEachFoodActivity se abre
    ├─ Recibe FoodModel como extra
    └─ Muestra detalle del producto
    ↓
12. Usuario ajusta cantidad y hace click "Agregar al carrito"
    ├─ managmentCart.insertItem(item)
    │  ├─ Obtiene carrito actual de TinyDB
    │  ├─ Verifica si el item ya existe
    │  ├─ Si existe: suma cantidades
    │  ├─ Si no existe: agrega nuevo item
    │  ├─ Guarda carrito en TinyDB
    │  └─ Muestra Toast: "Producto x Cantidad agregado al carrito"
    └─ DetailEachFoodActivity.navigateToCart()
    ↓
13. CartActivity se abre
    ├─ managmentCart.getListCart()
    │  └─ Obtiene items del carrito de TinyDB
    └─ UI muestra:
       ├─ Lista de items con:
       │  ├─ Imagen del producto
       │  ├─ Nombre
       │  ├─ Precio unitario
       │  ├─ Cantidad (la seleccionada en detail)
       │  ├─ Subtotal (precio × cantidad)
       │  └─ Botón eliminar (✕)
       ├─ CartFooter con:
       │  ├─ Subtotal total
       │  ├─ Envío
       │  └─ Total de la compra
       └─ Usuario puede:
          ├─ Eliminar items (botón ✕)
          │  └─ Toast: "Producto eliminado del carrito"
          └─ Proceder al pago
```
```

---

## Componentes Principales

### 1. MainActivity (Dashboard)

**Ubicación**: `presentation/activity/dashboard/MainActivity.kt`

**Función**: Pantalla principal de la aplicación

**Componentes**:
- TopBar: Encabezado con logo
- SearchBar: Barra de búsqueda
- Banner: Carousel de imágenes promocionales
- CategorySection: Grid de categorías
- BottomBar: Navegación inferior

**Flujo**:
```
MainActivity
├─ MainViewModel (inyectado)
├─ Observa: banners, categories, isLoading
├─ Muestra: Banner, CategorySection
└─ Navega a: ItemsListActivity (al hacer click en categoría)
```

### 2. ItemsListActivity

**Ubicación**: `presentation/activity/itemList/ItemsListActivity.kt`

**Función**: Mostrar lista de productos de una categoría

**Flujo**:
```
ItemsListActivity
├─ Recibe: categoryId, title (Intent extras)
├─ MainViewModel.loadFoodsByCategory(categoryId)
├─ Observa: foods, isLoading
├─ Muestra: ItemsList (LazyColumn de FoodCard)
└─ Navega a: DetailEachFoodActivity (al hacer click en producto)
```

### 3. DetailEachFoodActivity

**Ubicación**: `presentation/activity/detailEachFood/DetailEachFoodActivity.kt`

**Función**: Mostrar detalle de un producto con opción de ajustar cantidad y agregar al carrito

**Componentes**:
- HeaderSection: Imagen, nombre, rating, tiempo
- DescriptionSection: Descripción del producto
- DetailScreen: Pantalla completa con cantidad
- FooterSection: Precio total y botón "Agregar al carrito"

**Flujo**:
```
DetailEachFoodActivity
├─ Recibe: FoodModel (Intent extra)
├─ Preserva cantidad si ya existe (numberInCart > 0)
├─ Muestra: DetailScreen
│  ├─ Imagen del producto
│  ├─ Nombre, rating, tiempo
│  ├─ Descripción
│  ├─ Controles de cantidad (+/-)
│  └─ Precio total (precio × cantidad)
└─ Al hacer click "Agregar al carrito":
   ├─ managmentCart.insertItem(item)
   │  ├─ Guarda en TinyDB
   │  └─ Muestra Toast: "Producto x Cantidad agregado al carrito"
   └─ Navega a: CartActivity
```

**Mejora Implementada - Persistencia de Cantidad**:

```kotlin
override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)

    item = intent.getSerializableExtra("object") as FoodModel
    managmentCart = ManagmentCart(applicationContext)

    // No reiniciar numberInCart a 1, mantener el valor del intent
    if (item.numberInCart == 0) {
        item.numberInCart = 1
    }

    Log.d(TAG, "DetailEachFoodActivity opened with item: ${item.title}, quantity: ${item.numberInCart}")

    setContent {
        DetailScreen(
            item = item,
            onBackClick = { finish() },
            onAddToCartClick = {
                Log.d(TAG, "Adding to cart: ${item.title}, quantity: ${item.numberInCart}")
                managmentCart.insertItem(item)
                navigateToCart()
            }
        )
    }
}
```

**Características**:
- ✅ Solo establece cantidad a 1 si es 0 (primera vez)
- ✅ Mantiene la cantidad si ya fue modificada
- ✅ Logging para debugging
- ✅ Navega a CartActivity después de agregar

**Ejemplo de flujo**:
```
1. Usuario selecciona Pollo Asado
2. DetailScreen abre con cantidad = 1
3. Usuario aumenta a 2 (botón +)
4. Usuario hace click "Agregar al carrito"
5. Toast: "Pollo Asado x2 agregado al carrito"
6. CartActivity abre mostrando Pollo Asado x2
```

### 4. CartActivity

**Ubicación**: `presentation/activity/cart/CartActivity.kt`

**Función**: Mostrar carrito de compras con detalles de items, cantidades y totales

**Componentes**:

#### CartScreen
Composable principal que gestiona el estado del carrito:
```kotlin
@Composable
fun CartScreen(
    managmentCart: ManagmentCart,
    onBackClick: () -> Unit
)
```
- Obtiene lista de items del carrito
- Calcula el total de la compra
- Implementa listener para actualizar UI cuando cambian items

#### CartItemCard
Muestra cada producto en el carrito:
```kotlin
@Composable
fun CartItemCard(
    item: FoodModel,
    index: Int,
    managmentCart: ManagmentCart,
    changeListener: ChangeNumberItemsListener
)
```

**Características**:
- Imagen del producto (80dp)
- Nombre del producto
- Precio unitario (en naranja)
- **Cantidad seleccionada** (mostrada, no editable)
- Subtotal (precio × cantidad)
- **Botón de eliminar** (✕ en rojo) - reemplaza los botones +/-

**Ejemplo de visualización**:
```
┌─────────────────────────────────────┐
│ [Imagen] Pollo Asado                │
│          $15.00                     │
│          Cantidad: 2                │
│          Subtotal: $30.00      [✕]  │
└─────────────────────────────────────┘
```

#### CartFooter
Muestra resumen de la compra:
```kotlin
@Composable
fun CartFooter(
    totalPrice: Double,
    itemCount: Int
)
```

**Información mostrada**:
- Subtotal (cantidad de items)
- Envío ($0.00)
- Total (en naranja y grande)
- Botón "Proceder al Pago"

**Ejemplo**:
```
Subtotal (2 items):        $30.00
Envío:                     $0.00
─────────────────────────────────
Total:                     $30.00
[Proceder al Pago]
```

**Flujo**:
```
CartActivity
├─ managmentCart.getListCart()
├─ Muestra: CartScreen
│  ├─ Header con botón atrás
│  ├─ LazyColumn de CartItemCard
│  │  └─ Cada item muestra:
│  │     ├─ Imagen
│  │     ├─ Nombre
│  │     ├─ Precio unitario
│  │     ├─ Cantidad (del detail screen)
│  │     ├─ Subtotal
│  │     └─ Botón eliminar (✕)
│  └─ CartFooter con total
└─ Usuario puede:
   ├─ Eliminar items (botón ✕)
   └─ Proceder al pago
```

**Mejoras Implementadas**:
1. ✅ **Cantidades persistentes**: Las cantidades seleccionadas en DetailScreen se mantienen en el carrito
2. ✅ **Notificaciones Toast**: Al agregar producto muestra "Producto x Cantidad agregado al carrito"
3. ✅ **Botón de eliminar**: Reemplaza los controles +/- para mejor UX
4. ✅ **Cantidad mostrada**: Se visualiza la cantidad seleccionada sin permitir edición en el carrito
5. ✅ **Cálculo automático**: Subtotal y total se calculan correctamente

---

## Conexiones entre Archivos

### Diagrama de Dependencias

```
MainActivity
├─ MainViewModel
│  └─ MainRepository (interfaz)
│     └─ MainRepositoryImpl
│        ├─ FirebaseDatabase
│        └─ Gson
├─ Banner.kt
├─ CategorySection.kt
└─ BottomBar.kt

ItemsListActivity
├─ MainViewModel
├─ ItemList.kt
│  └─ FoodCard.kt
└─ DetailEachFoodActivity

DetailEachFoodActivity
├─ FoodModel
├─ ManagmentCart
│  └─ MyDB (TinyDB)
├─ HeaderSection.kt
├─ DescriptionSection.kt
└─ FooterSection.kt

CartActivity
├─ ManagmentCart
├─ CartItemCard.kt
└─ CartFooter.kt
```

### Flujo de Inyección de Dependencias

```
ChickenFoodApp.onCreate()
├─ startKoin()
└─ modules(appModule)
   ├─ single { Gson() }
   ├─ single { FirebaseDatabase.getInstance() }
   ├─ single<MainRepository> { MainRepositoryImpl(get(), get()) }
   └─ viewModel { MainViewModel(repository = get()) }

MainActivity
├─ koinViewModel() obtiene MainViewModel
└─ MainViewModel obtiene MainRepository

ItemsListActivity
├─ koinViewModel() obtiene MainViewModel
└─ MainViewModel obtiene MainRepository
```

## Helper Classes (Clases Auxiliares)

### ManagmentCart.kt

**Ubicación**: `helper/ManagmentCart.kt`

**Función**: Gestionar el carrito de compras usando TinyDB para almacenamiento local

**Métodos principales**:

#### 1. insertItem(item: FoodModel)
Agrega un producto al carrito o actualiza su cantidad si ya existe:

```kotlin
fun insertItem(item: FoodModel) {
    var listFood = getListCart()
    val existAlready = listFood.any { it.title == item.title }
    val index = listFood.indexOfFirst { it.title == item.title }

    if (existAlready) {
        // Si el item ya existe, sumar las cantidades
        listFood[index].numberInCart += item.numberInCart
    } else {
        // Si no existe, agregar el item
        listFood.add(item)
    }
    tinyDB.putListObject("CartList", listFood)
    
    // Toast con nombre del producto y cantidad
    val message = "${item.title} x${item.numberInCart} agregado al carrito"
    Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
}
```

**Características**:
- ✅ Verifica si el producto ya existe en el carrito
- ✅ Si existe: suma las cantidades
- ✅ Si no existe: agrega nuevo item
- ✅ Muestra Toast con nombre del producto y cantidad
- ✅ Guarda en TinyDB

**Ejemplo**:
```
Usuario agrega: Pollo Asado x2
Toast muestra: "Pollo Asado x2 agregado al carrito"

Si vuelve a agregar Pollo Asado x1:
- Cantidad anterior: 2
- Nueva cantidad: 2 + 1 = 3
Toast muestra: "Pollo Asado x1 agregado al carrito"
```

#### 2. getListCart(): ArrayList<FoodModel>
Obtiene la lista actual de items en el carrito:

```kotlin
fun getListCart(): ArrayList<FoodModel> {
    return tinyDB.getListObject("CartList")
}
```

**Retorna**: Lista de FoodModel con todos los items del carrito

#### 3. removeItem(listFood, position, listener)
Elimina un producto del carrito:

```kotlin
fun removeItem(listFood: ArrayList<FoodModel>, position: Int, listener: ChangeNumberItemsListener) {
    if (position < 0 || position >= listFood.size) return
    val itemName = listFood[position].title
    listFood.removeAt(position)
    tinyDB.putListObject("CartList", listFood)
    Toast.makeText(context, "$itemName eliminado del carrito", Toast.LENGTH_SHORT).show()
    listener.onChanged()
}
```

**Características**:
- ✅ Valida que la posición sea válida
- ✅ Obtiene el nombre del producto antes de eliminarlo
- ✅ Elimina el item de la lista
- ✅ Guarda cambios en TinyDB
- ✅ Muestra Toast de confirmación
- ✅ Notifica al listener para actualizar UI

**Ejemplo**:
```
Usuario hace click en botón ✕
Toast muestra: "Pollo Asado eliminado del carrito"
CartActivity se actualiza automáticamente
```

#### 4. getTotalFee(): Double
Calcula el total de la compra:

```kotlin
fun getTotalFee(): Double {
    val listFood = getListCart()
    var fee = 0.0
    for (item in listFood) {
        fee += item.price * item.numberInCart
    }
    return fee
}
```

**Cálculo**: Suma de (precio × cantidad) para cada item

**Ejemplo**:
```
Item 1: Pollo Asado $15 × 2 = $30
Item 2: Hamburguesa $8 × 1 = $8
Total: $30 + $8 = $38
```

#### 5. minusItem(listFood, position, listener)
Disminuye la cantidad de un producto (no se usa en CartActivity):

```kotlin
fun minusItem(listFood: ArrayList<FoodModel>, position: Int, listener: ChangeNumberItemsListener) {
    if (position < 0 || position >= listFood.size) return
    val currentCount = listFood[position].numberInCart
    if (currentCount <= 1) {
        listFood.removeAt(position)
    } else {
        listFood[position].numberInCart = currentCount - 1
    }
    tinyDB.putListObject("CartList", listFood)
    listener.onChanged()
}
```

#### 6. plusItem(listFood, position, listener)
Aumenta la cantidad de un producto (no se usa en CartActivity):

```kotlin
fun plusItem(listFood: ArrayList<FoodModel>, position: Int, listener: ChangeNumberItemsListener) {
    listFood[position].numberInCart++
    tinyDB.putListObject("CartList", listFood)
    listener.onChanged()
}
```

### ChangeNumberItemsListener.kt

**Ubicación**: `helper/ChangeNumberItemsListener.kt`

**Función**: Interfaz para notificar cambios en el carrito

```kotlin
interface ChangeNumberItemsListener {
    fun onChanged()
}
```

**Uso**: Cuando se agrega, elimina o modifica un item, se llama a `onChanged()` para actualizar la UI

### MyDB.kt

**Ubicación**: `helper/MyDB.kt`

**Función**: Wrapper de TinyDB para almacenamiento local

**Métodos principales**:
- `putListObject(key, list)`: Guarda una lista
- `getListObject(key)`: Obtiene una lista

**Ventajas de TinyDB**:
- ✅ Almacenamiento local sin necesidad de base de datos
- ✅ Persiste datos incluso si se cierra la app
- ✅ Fácil de usar para datos simples
- ✅ No requiere permisos especiales

---

### 1. Clean Architecture
Separación de responsabilidades en 3 capas:
- **Presentation**: UI y ViewModels
- **Domain**: Interfaces y modelos
- **Data**: Implementación de repositorios

### 2. MVVM (Model-View-ViewModel)
- **Model**: Datos (FoodModel, CategoryModel, etc.)
- **View**: UI (Activities, Composables)
- **ViewModel**: Lógica de presentación (MainViewModel)

### 3. Reactive Programming (Flow)
- Los datos fluyen desde la fuente (Firebase) hacia la UI
- La UI se actualiza automáticamente cuando los datos cambian
- No hay callbacks anidados (callback hell)

### 4. Inyección de Dependencias (Koin)
- Las dependencias se crean en un lugar centralizado (AppModule)
- Se inyectan automáticamente donde se necesitan
- Facilita testing y cambios de implementación

### 5. Jetpack Compose
- UI declarativa (describe qué mostrar, no cómo)
- Recomposición automática cuando el estado cambia
- Código más limpio y mantenible

### 6. Firebase Realtime Database
- Base de datos NoSQL en tiempo real
- Los datos se sincronizan automáticamente
- Ideal para aplicaciones que necesitan actualizaciones en tiempo real

### 7. Corrutinas
- Programación asincrónica sin callbacks
- `viewModelScope`: Scope que se cancela con el ViewModel
- `launch`: Inicia una corrutina
- `first()`: Obtiene el primer valor de un Flow

---

## Estructura de Carpetas

```
app/src/main/java/com/daniel/chickenfood/
├── ChickenFoodApp.kt (Inicializa Koin)
├── di/
│   └── AppModule.kt (Configuración de dependencias)
├── presentation/
│   ├── activity/
│   │   ├── BaseActivity.kt
│   │   ├── dashboard/
│   │   │   ├── MainActivity.kt
│   │   │   ├── Banner.kt
│   │   │   ├── CategorySection.kt
│   │   │   ├── TopBar.kt
│   │   │   ├── SearchBar.kt
│   │   │   └── BottomBar.kt
│   │   ├── itemList/
│   │   │   ├── ItemsListActivity.kt
│   │   │   └── ItemList.kt
│   │   ├── detailEachFood/
│   │   │   ├── DetailEachFoodActivity.kt
│   │   │   ├── DetailScreen.kt
│   │   │   ├── HeaderSection.kt
│   │   │   ├── DescriptionSection.kt
│   │   │   └── FooterSection.kt
│   │   ├── cart/
│   │   │   ├── CartActivity.kt
│   │   │   ├── CartScreen.kt (Composable)
│   │   │   ├── CartItemCard.kt (Composable)
│   │   │   └── CartFooter.kt (Composable)
│   │   └── splash/
│   │       └── SplashActivity.kt
│   └── viewModel/
│       └── MainViewModel.kt
├── domain/
│   ├── model/
│   │   ├── BannerModel.kt
│   │   ├── CategoryModel.kt
│   │   └── FoodModel.kt (Implementa Serializable)
│   └── reposity/
│       └── MainRepository.kt
├── data/
│   └── repository/
│       └── MainRepositoryImpl.kt
└── helper/
    ├── ManagmentCart.kt (Gestiona carrito con TinyDB)
    ├── MyDB.kt (Wrapper de TinyDB)
    └── ChangeNumberItemsListener.kt (Interfaz de callbacks)
```

---

## Resumen

**ChickenFood** es una aplicación bien estructurada que implementa:

✅ **Clean Architecture**: Separación clara de responsabilidades
✅ **MVVM**: Patrón de diseño moderno
✅ **Inyección de Dependencias**: Con Koin
✅ **Reactive Programming**: Con Flow y StateFlow
✅ **Jetpack Compose**: UI moderna y declarativa
✅ **Firebase**: Base de datos en tiempo real
✅ **Corrutinas**: Programación asincrónica limpia

Esta arquitectura permite que la aplicación sea:
- **Mantenible**: Código organizado y fácil de entender
- **Testeable**: Cada capa puede probarse independientemente
- **Escalable**: Fácil agregar nuevas funcionalidades
- **Flexible**: Fácil cambiar implementaciones (Firebase por otra BD)

---

## Glosario de Términos

| Término | Definición |
|---------|-----------|
| **Activity** | Pantalla de la aplicación |
| **Composable** | Función que define UI en Jetpack Compose |
| **ViewModel** | Gestiona estado y lógica de presentación |
| **Repository** | Abstracción de fuentes de datos |
| **Flow** | Stream de datos reactivo |
| **StateFlow** | Flow que emite estado |
| **Corrutina** | Programación asincrónica |
| **Koin** | Framework de inyección de dependencias |
| **Firebase** | Plataforma de backend en la nube |
| **Gson** | Librería para serializar/deserializar JSON |
| **Intent** | Mensaje para navegar entre Activities |
| **Modifier** | Objeto que modifica propiedades de Composables |
| **LazyColumn** | Lista vertical perezosa (solo renderiza items visibles) |

---

**Documento generado**: 2026-05-26
**Última actualización**: 2026-05-26
**Versión del Proyecto**: 1.1
**Autor**: Daniel Alvarado

### Historial de Versiones
- **v1.1** (2026-05-26): Mejoras en carrito, persistencia de cantidades, notificaciones Toast
- **v1.0** (2026-05-26): Versión inicial con Clean Architecture, MVVM, Firebase
