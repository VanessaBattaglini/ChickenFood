# 🏗️ Arquitectura Técnica - Stack Completo

**Patrón**: MVVM (Model-View-ViewModel)  
**DI**: Koin  
**Backend**: Firebase Realtime Database  
**UI**: Jetpack Compose

---

## Estructura del Proyecto

```
app/src/main/java/com/daniel/chickenfood/
│
├── presentation/
│   ├── activity/
│   │   ├── dashboard/
│   │   │   ├── MainActivity.kt
│   │   │   ├── SearchBar.kt
│   │   │   ├── PointsCard.kt
│   │   │   ├── Banner.kt
│   │   │   ├── TopBar.kt
│   │   │   ├── BottomBar.kt
│   │   │   └── CategorySection.kt
│   │   ├── detailEachFood/
│   │   │   └── DetailEachFoodActivity.kt
│   │   ├── itemList/
│   │   │   └── ItemsListActivity.kt
│   │   ├── cart/
│   │   │   └── CartActivity.kt
│   │   ├── auth/
│   │   │   └── SignUpActivity.kt (Google Sign-In)
│   │   ├── splash/
│   │   │   └── SplashActivity.kt (Empecemos / Inscribete)
│   │   └── BaseActivity.kt (Base class)
│   │
│   └── viewModel/
│       ├── MainViewModel.kt (Banners, Categorías, Búsqueda)
│       ├── RewardsViewModel.kt (Puntos, Niveles)
│       ├── OrderViewModel.kt (Órdenes)
│       └── TokenViewModel.kt (Autenticación, Tokens)
│
├── domain/
│   ├── model/
│   │   ├── BannerModel.kt
│   │   ├── CategoryModel.kt
│   │   ├── FoodModel.kt
│   │   ├── OrderModel.kt
│   │   ├── UserRewardsModel.kt
│   │   ├── UserTokenModel.kt
│   │   ├── PointsTransactionModel.kt
│   │   └── OrderItemModel.kt
│   │
│   └── repository/ (Interfaces)
│       ├── MainRepository.kt
│       ├── OrderRepository.kt
│       ├── RewardsRepository.kt
│       └── TokenRepository.kt
│
├── data/
│   └── repository/ (Implementaciones)
│       ├── MainRepositoryImpl.kt
│       ├── OrderRepositoryImpl.kt
│       ├── RewardsRepositoryImpl.kt
│       └── TokenRepositoryImpl.kt
│
├── helper/
│   ├── AuthHelper.kt (Singleton - Firebase Auth)
│   ├── ManagmentCart.kt (Singleton - Carrito local)
│   ├── RewardsHelper.kt (Utilidades de puntos)
│   └── ChangeNumberItemsListener.kt
│
└── di/
    └── AppModule.kt (Inyección con Koin)
```

---

## Patrón MVVM

### Capas

```
Presentation Layer (UI)
├─ Activity/Fragment
├─ Composable @Composable
├─ ViewModel (State)
└─ Navigation

Domain Layer (Lógica)
├─ Models (Entidades)
├─ Interfaces (Repositories)
└─ Lógica de negocio

Data Layer (Datos)
├─ Repository Implementation
├─ API (Firebase)
├─ Local DB (Room)
└─ Cachés
```

---

## Flujo de Datos MVVM

### Búsqueda de Productos (Ejemplo Completo)

```
1. UI (SearchBar.kt)
   ├─ Usuario escribe "pollo"
   ├─ SearchBar.onValueChange("pollo")
   └─ viewModel.searchFoods("pollo")

2. ViewModel (MainViewModel.kt)
   ├─ _isSearching.value = true
   ├─ viewModelScope.launch { }
   ├─ repository.loadFiltered(categoryId).first()
   ├─ Filtra resultados
   ├─ _searchResults.value = [3 items]
   └─ _isSearching.value = false

3. Repository (MainRepositoryImpl.kt)
   ├─ loadFiltered(categoryId): Flow<List<FoodModel>>
   ├─ Obtiene datos de Firebase
   ├─ Mapea a FoodModel
   └─ Devuelve Flow

4. Data Source (Firebase)
   ├─ /categories/{categoryId}/foods
   ├─ Retorna JSON
   └─ RepositoryImpl lo deserializa

5. UI se renderiza (observa searchResults)
   ├─ LazyColumn renderiza items
   ├─ Cada item: SearchResultItem
   └─ Usuario ve resultados

6. Usuario toca resultado
   ├─ SearchResultItem.onItemClick(food)
   ├─ MainActivity.navigateToDetail(food)
   ├─ Intent a DetailEachFoodActivity
   └─ Pasa FoodModel
```

---

## ViewModels

### MainViewModel

**Responsabilidad**: Datos principales (banners, categorías, búsqueda)

**Estados**:
```kotlin
// Banners
val banners: StateFlow<List<BannerModel>>
val isLoadingBanners: StateFlow<Boolean>
val bannerError: StateFlow<String?>

// Categorías
val categories: StateFlow<List<CategoryModel>>
val isLoadingCategories: StateFlow<Boolean>
val categoryError: StateFlow<String?>

// Búsqueda
val searchQuery: StateFlow<String>
val searchResults: StateFlow<List<FoodModel>>
val isSearching: StateFlow<Boolean>

// General
val isLoadingAll: StateFlow<Boolean>
```

**Métodos**:
```kotlin
fun loadBanners()
fun loadCategories()
fun loadFoodsByCategory(categoryId: String)
fun searchFoods(query: String)
fun clearSearch()
```

---

### RewardsViewModel

**Responsabilidad**: Puntos y recompensas del usuario

**Estados**:
```kotlin
val userRewards: StateFlow<UserRewardsModel?>
val pointsBalance: StateFlow<Int>
val pointsHistory: StateFlow<List<PointsTransactionModel>>
val isLoading: StateFlow<Boolean>
val error: StateFlow<String?>
```

**Métodos**:
```kotlin
fun loadUserRewards(userId: String)
fun loadPointsHistory(userId: String)
fun redeemPoints(userId: String, points: Int, description: String)
fun addPointsFromPurchase(userId: String, orderTotal: Double, orderId: String)
fun clearError()
```

---

### TokenViewModel

**Responsabilidad**: Autenticación y tokens

**Métodos**:
```kotlin
fun saveUserToken(user: FirebaseUser, idToken: String, firebaseIdToken: String)
fun updateFirebaseToken(userId: String, token: String)
fun updateLastLogin(userId: String)
fun isTokenValid(userId: String)
fun deleteUserToken(userId: String)
fun revokeToken(userId: String)
```

---

## Repositories

### Patrón Repository

```
ViewModel
    ↓
Repository Interface
    ↓
Repository Implementation
    ↓
Data Source (Firebase/Local)
```

### MainRepository

```kotlin
interface MainRepository {
    fun loadBanner(): Flow<List<BannerModel>>
    fun loadCategory(): Flow<List<CategoryModel>>
    fun loadFiltered(categoryId: String): Flow<List<FoodModel>>
}
```

### RewardsRepository

```kotlin
interface RewardsRepository {
    fun getUserRewards(userId: String): Flow<UserRewardsModel>
    fun getPointsHistory(userId: String): Flow<List<PointsTransactionModel>>
    fun redeemPoints(userId: String, points: Int, description: String): Flow<Boolean>
    fun addPointsFromPurchase(userId: String, orderTotal: Double, orderId: String): Flow<Boolean>
}
```

---

## Inyección de Dependencias (Koin)

**Ubicación**: `di/AppModule.kt`

```kotlin
val appModule = module {
    // Repositories
    single<MainRepository> { MainRepositoryImpl() }
    single<RewardsRepository> { RewardsRepositoryImpl() }
    single<OrderRepository> { OrderRepositoryImpl() }
    single<TokenRepository> { TokenRepositoryImpl() }
    
    // ViewModels
    viewModel { MainViewModel(get()) }
    viewModel { RewardsViewModel(get()) }
    viewModel { OrderViewModel(get()) }
    viewModel { TokenViewModel(get()) }
}
```

**En Activity/Composable**:
```kotlin
@Composable
fun MainScreen(
    viewModel: MainViewModel = koinViewModel(),
    rewardsViewModel: RewardsViewModel = koinViewModel()
)
```

---

## Firebase Integration

### Realtime Database Structure

```
users/
  {userId}/
    rewards/
      totalPoints: 150
      pointsBalance: 120
      pointsSpent: 30
      userLevel: "bronce"
    tokens/
      googleIdToken: "..."
      firebaseIdToken: "..."
      refreshToken: "..."
    orders/
      {orderId}/
        total: 100
        points: 10
        date: 1622505600000

categories/
  1/
    name: "Aves"
    image: "url..."
    foods/
      food1/
        id: "1"
        title: "Pollo Asado"
        price: 8.99
        description: "..."
        image: "url..."

banners/
  banner1/
    image: "url..."
    title: "Promo 50%"
```

---

## Flow de Operaciones

### Compra y Ganancias de Puntos

```
1. CartActivity.proceedToPayment()
   └─ Simula pago exitoso

2. Si autenticado:
   └─ rewardsViewModel.addPointsFromPurchase(uid, total, orderId)

3. RewardsViewModel.addPointsFromPurchase()
   └─ rewardsRepository.addPointsFromPurchase(...)

4. RewardsRepositoryImpl.addPointsFromPurchase()
   ├─ Calcula puntos: total × cashback%
   ├─ Crea OrderModel
   ├─ Guarda en Firebase /users/{uid}/orders/{orderId}
   ├─ Obtiene UserRewardsModel
   ├─ Actualiza: totalPoints += puntos
   ├─ Actualiza: pointsBalance += puntos
   ├─ Guarda en Firebase /users/{uid}/rewards
   └─ Devuelve Flow<Boolean>

5. ViewModel emite cambios
   └─ _userRewards.value = updatedModel

6. Dashboard observa cambios
   ├─ PointsCard.collectAsState()
   ├─ Se re-renderiza
   └─ Usuario ve nuevo saldo
```

---

## Helpers

### AuthHelper (Singleton)

```kotlin
object AuthHelper {
    fun getCurrentUser(): FirebaseUser?
    fun isUserLoggedIn(): Boolean
    fun signOut()
    fun getCurrentUserId(): String?
}
```

**Uso**:
```kotlin
val user = AuthHelper.getCurrentUser()
if (user != null) {
    // Usuario autenticado
    loadUserData(user.uid)
}
```

---

### ManagmentCart (Singleton)

```kotlin
class ManagmentCart(val context: Context) {
    fun getListCart(): List<FoodModel>
    fun addToCart(food: FoodModel)
    fun removeFromCart(food: FoodModel)
    fun clearCart()
    fun getTotalPrice(): Double
}
```

**Uso**:
```kotlin
val cart = ManagmentCart(context)
cart.addToCart(foodModel)
val total = cart.getTotalPrice()
cart.clearCart()  // Después de pagar
```

---

## Coroutines y Flow

### StateFlow

```kotlin
// En ViewModel
private val _banners = MutableStateFlow<List<BannerModel>>(emptyList())
val banners = _banners.asStateFlow()  // Read-only

// En Composable
val banners by viewModel.banners.collectAsState()
```

### ViewModelScope

```kotlin
fun loadBanners() {
    viewModelScope.launch {  // Coroutine en scope del ViewModel
        try {
            val list = repository.loadBanner().first()
            _banners.value = list
        } catch (e: Exception) {
            _bannerError.value = e.message
        }
    }
}
```

---

## Compose Integration

### Recomposición

```kotlin
@Composable
fun MainScreen(viewModel: MainViewModel = koinViewModel()) {
    // Observar estado
    val banners by viewModel.banners.collectAsState()
    
    // Se re-renderiza cuando banners cambia
    LazyColumn {
        items(banners) { banner ->
            BannerItem(banner)
        }
    }
}
```

### Memory Management

```kotlin
// En LazyColumn
items(banners, key = { it.id }) { banner ->
    BannerItem(banner)  // Solo renderiza items visibles
}
```

---

## Estado de Compilación

```
Kotlin: ✅ Compilado
Gradle: ✅ Build successful
Firebase: ✅ Integrado
Compose: ✅ UI renderiza
Koin: ✅ DI inyección funciona
```

---

## Performance

### Optimizaciones Aplicadas

1. **Lazy Loading**: LazyColumn solo renderiza items visibles
2. **StateFlow**: Observables eficientes
3. **Coroutines**: No bloquea UI thread
4. **Timeouts**: 10 segundos máximo por operación
5. **Caching**: Repositorios cachean datos locales
6. **Composición**: Recomposición solo cuando hay cambios

---

## Debugging

### Logs

```
D/MainViewModel: Loading banners...
D/MainViewModel: ✅ Banners loaded successfully: 3 items
D/SearchBar: Searching foods for query: 'pollo'
D/MainViewModel: ✅ Search results: 3 items found for 'pollo'
D/RewardsViewModel: Loading rewards for user: dyBCD...
D/RewardsViewModel: Points added successfully from purchase
```

---

**Estado**: ✅ Arquitectura sólida con MVVM, Koin, Firebase y Compose
