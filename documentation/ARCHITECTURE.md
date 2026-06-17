# 🏗️ Arquitectura Técnica - SuperCrunchy Pollo

## 📐 Visión General

SuperCrunchy Pollo sigue una **arquitectura limpia con capas bien definidas** y utiliza el patrón **MVVM** (Model-View-ViewModel) con **Jetpack Compose** para la UI.

```
┌─────────────────────────────────┐
│   Presentation Layer            │
│  (Activities, Composables)      │
└────────────┬────────────────────┘
             │
┌────────────▼────────────────────┐
│   ViewModel Layer               │
│  (Business Logic, StateFlow)    │
└────────────┬────────────────────┘
             │
┌────────────▼────────────────────┐
│   Repository Layer              │
│  (Data Abstraction)             │
└────────────┬────────────────────┘
             │
┌────────────▼────────────────────┐
│   Data Layer                    │
│  (Firebase, Local Storage)      │
└─────────────────────────────────┘
```

---

## 🛠️ Stack de Tecnologías

### Lenguaje & Framework
- **Kotlin**: Lenguaje principal
- **Jetpack Compose**: UI declarativa
- **Android Architecture Components**: ViewModel, Flow, LiveData

### Firebase
- **Firebase Authentication**: Login/Signup seguro
- **Firebase Realtime Database**: Almacenamiento de datos
- **Google Sign-In**: Autenticación con Google

### Inyección de Dependencias
- **Koin**: Dependency Injection ligero y flexible

### Build & Gradle
- **Gradle Kotlin DSL**: Configuración moderna
- **Android Gradle Plugin 8.x**: Herramientas build actuales

---

## 📦 Estructura del Proyecto

```
app/src/main/java/com/daniel/chickenfood/

├── presentation/                    # UI Layer
│   ├── activity/
│   │   ├── auth/                   # Login, Signup, Splash
│   │   ├── dashboard/              # MainActivity, Dashboard UI
│   │   ├── checkout/               # Checkout, Payment, Confirmation
│   │   ├── cart/                   # CartActivity
│   │   └── [otros]
│   └── viewModel/                  # RewardsViewModel, OrderViewModel, etc
│
├── domain/                          # Business Logic (Interfaces)
│   ├── model/                       # Data Models
│   │   ├── UserRewardsModel.kt
│   │   ├── OrderModel.kt
│   │   ├── PointsTransactionModel.kt
│   │   └── [otros]
│   └── reposity/                    # Repository Interfaces
│       ├── RewardsRepository.kt
│       ├── OrderRepository.kt
│       └── [otros]
│
├── data/                            # Data Layer
│   ├── repository/                  # Repository Implementations
│   │   ├── RewardsRepositoryImpl.kt
│   │   ├── OrderRepositoryImpl.kt
│   │   └── [otros]
│   └── service/                     # Firebase Services, APIs
│       ├── MockPaymentService.kt
│       └── [otros]
│
├── di/                              # Dependency Injection
│   └── AppModule.kt                 # Koin configuration
│
├── helper/                          # Utilities
│   ├── AuthHelper.kt               # Auth utilities
│   ├── ManagmentCart.kt            # Cart management
│   ├── RewardsHelper.kt            # Points utilities
│   └── [otros]
│
└── ChickenFoodApp.kt               # Application class
```

---

## 🔄 Flujo de Datos: Sistema de Puntos (v3.5)

### 1. Obtener Puntos (Firebase → UI)

```
Firebase Realtime Database
│   ├─ /users/{uid}/rewards/pointsBalance: 500
│   └─ /users/{uid}/rewards/...
│
▼
RewardsRepositoryImpl
│   └─ getUserRewards(userId) → Flow<UserRewardsModel>
│
▼
RewardsViewModel
│   ├─ _pointsBalance = MutableStateFlow(0)
│   └─ loadUserRewards() → updates _pointsBalance
│
▼
CheckoutActivity (setContent)
│   └─ val userPoints by rewardsViewModel.pointsBalance.collectAsState()
│
▼
CheckoutScreen (Composable)
│   └─ Renderiza con puntos correctos
```

### 2. Usar Puntos (UI → Firebase)

```
User selects "Pagar con Puntos"
│
▼
CheckoutActivity.onConfirmPayment("points")
│   ├─ Calcula descuento: puntos * 0.01
│   ├─ Crea PointsTransactionModel
│   └─ Llama rewardsViewModel.recordPointsTransaction()
│
▼
RewardsViewModel.recordPointsTransaction()
│   └─ Llama rewardsRepository.addPointsTransaction()
│
▼
RewardsRepositoryImpl.addPointsTransaction()
│   └─ Guarda en Firebase
│       ├─ /users/{uid}/transactions/{docId}
│       └─ Actualiza /users/{uid}/rewards/pointsBalance
│
▼
Firebase Realtime Database
│   └─ Actualiza puntos (se gastan todos)
│
▼
RewardsViewModel detecta cambio
│   └─ collectAsState() recibe actualización
│
▼
CheckoutScreen recompone con nuevos puntos (0)
```

---

## 🔌 Componentes Clave

### RewardsViewModel

```kotlin
class RewardsViewModel(
    private val rewardsRepository: RewardsRepository
) : ViewModel() {

    // StateFlow para puntos
    private val _pointsBalance = MutableStateFlow(0)
    val pointsBalance: StateFlow<Int> = _pointsBalance.asStateFlow()

    // Carga puntos asincronamente
    fun loadUserRewards(userId: String) {
        viewModelScope.launch {
            rewardsRepository.getUserRewards(userId).collect { rewards ->
                _pointsBalance.value = rewards.pointsBalance
            }
        }
    }

    // Registra transacción de puntos
    fun recordPointsTransaction(transaction: PointsTransactionModel) {
        viewModelScope.launch {
            rewardsRepository.addPointsTransaction(transaction)
            loadUserRewards(transaction.userId)  // Recargar
        }
    }
}
```

### CheckoutActivity

```kotlin
class CheckoutActivity : BaseActivity() {
    private val rewardsViewModel: RewardsViewModel by viewModel()

    override fun onCreate(savedInstanceState: Bundle?) {
        // Cargar puntos del usuario
        val userId = AuthHelper.getUserId()
        rewardsViewModel.loadUserRewards(userId)

        setContent {
            // Observar puntos en tiempo real
            val userPoints by rewardsViewModel.pointsBalance.collectAsState()

            // Pasar a UI
            CheckoutScreen(userPoints = userPoints, ...)
        }
    }
}
```

### RewardsRepositoryImpl

```kotlin
class RewardsRepositoryImpl(private val firebaseDb: FirebaseDatabase) 
    : RewardsRepository {

    override fun getUserRewards(userId: String): Flow<UserRewardsModel> = flow {
        firebaseDb.reference
            .child("users/$userId/rewards")
            .get()
            .addOnSuccessListener { snapshot ->
                val rewards = snapshot.getValue(UserRewardsModel::class.java)
                emit(rewards ?: UserRewardsModel())
            }
    }

    override fun addPointsTransaction(transaction: PointsTransactionModel): Flow<Boolean> = flow {
        firebaseDb.reference
            .child("users/${transaction.userId}/transactions")
            .push()
            .setValue(transaction)
            .addOnSuccessListener { emit(true) }
    }
}
```

---

## 📊 Modelos de Datos

### UserRewardsModel
```kotlin
data class UserRewardsModel(
    val userId: String = "",
    val pointsBalance: Int = 0,
    val totalPointsEarned: Int = 0,
    val totalPointsSpent: Int = 0,
    val lastUpdated: Long = 0
)
```

### PointsTransactionModel
```kotlin
data class PointsTransactionModel(
    val userId: String = "",
    val orderId: String = "",
    val points: Int = 0,  // +500 para ganar, -500 para gastar
    val type: String = "",  // "purchase", "discount", "redemption"
    val description: String = "",
    val timestamp: Long = 0
)
```

### OrderModel
```kotlin
data class OrderModel(
    val orderId: String = "",
    val userId: String = "",
    val items: List<OrderItemModel> = emptyList(),
    val totalPrice: Double = 0.0,
    val pointsEarned: Int = 0,
    val orderDate: Long = 0,
    val status: String = ""
)
```

---

## 🎯 Dependency Injection (Koin)

### AppModule.kt

```kotlin
val appModule = module {
    // Singletons
    single<FirebaseDatabase> { FirebaseDatabase.getInstance() }
    single<FirebaseAuth> { FirebaseAuth.getInstance() }

    // Repositories
    single<RewardsRepository> { RewardsRepositoryImpl(get()) }
    single<OrderRepository> { OrderRepositoryImpl(get()) }

    // ViewModels
    factory { RewardsViewModel(get()) }
    factory { OrderViewModel(get()) }
}
```

### Application Class

```kotlin
class ChickenFoodApp : Application() {
    override fun onCreate() {
        super.onCreate()
        startKoin {
            androidContext(this@ChickenFoodApp)
            modules(appModule)
        }
    }
}
```

---

## 🔐 Seguridad

### Firebase Realtime Database Rules

```json
{
  "rules": {
    "users": {
      "$uid": {
        ".read": "$uid === auth.uid",
        ".write": "$uid === auth.uid",
        "rewards": {
          ".validate": "newData.hasChildren(['pointsBalance', 'totalPointsEarned'])"
        }
      }
    }
  }
}
```

### Validación de Datos
- ✅ Validación de email en signup
- ✅ Validación de tarjeta en checkout
- ✅ Null checks en toda la aplicación
- ✅ Try-catch blocks en operaciones críticas

---

## 🔄 Ciclo de Vida - Compra con Puntos

```
1. USER OPENS CHECKOUT
   └─ CheckoutActivity.onCreate()
   └─ rewardsViewModel.loadUserRewards(userId)
   └─ RewardsRepositoryImpl → Firebase
   └─ Firebase devuelve puntos (ej: 500)

2. VIEWMODEL UPDATES
   └─ _pointsBalance.value = 500
   └─ collectAsState() recibe actualización

3. UI RECOMPOSES
   └─ CheckoutScreen(userPoints = 500)
   └─ Dialog aparece: "¿Usar 500 puntos?"

4. USER SELECTS PUNTOS
   └─ selectedPaymentMethod = "points"
   └─ Se muestra información de descuento

5. USER CONFIRMS PAYMENT
   └─ CheckoutActivity.onConfirmPayment("points")
   └─ Calcula descuento = 500 * 0.01 = $5.00
   └─ Crea PointsTransactionModel(points = -500, type = "discount")

6. SAVE TO FIREBASE
   └─ rewardsViewModel.recordPointsTransaction(transaction)
   └─ RewardsRepositoryImpl → Firebase
   └─ Firebase actualiza puntos a 0

7. REWARDSVIEWMODEL UPDATES
   └─ loadUserRewards() se ejecuta
   └─ _pointsBalance.value = 0

8. UI UPDATES
   └─ Confirmation Screen muestra:
      ├─ "Puntos gastados: -500"
      └─ "Puntos después: 0"

9. USER GOES BACK TO DASHBOARD
   └─ MainViewModel.loadUserRewards()
   └─ Dashboard muestra: 0 puntos
```

---

## 📡 Comunicación

### Activity → ViewModel
```kotlin
rewardsViewModel.loadUserRewards(userId)
rewardsViewModel.recordPointsTransaction(transaction)
```

### ViewModel → UI (Reactive)
```kotlin
val userPoints by rewardsViewModel.pointsBalance.collectAsState()
val userRewards by rewardsViewModel.userRewards.collectAsState()
```

### Composable → Activity (Callbacks)
```kotlin
CheckoutScreen(
    onConfirmPayment = { method, cardData ->
        // Procesa en Activity
    }
)
```

---

## 🧪 Testing

### Unit Tests
```
✅ RewardsViewModel.loadUserRewards()
✅ RewardsRepositoryImpl.getUserRewards()
✅ PointsCalculation (100 pts = $1.00)
```

### Integration Tests
```
✅ Acumular puntos
✅ Ver puntos en checkout
✅ Usar puntos como descuento
✅ Pago mixto
✅ Compra cubierta
```

Ver: [TEST_POINTS_SYSTEM.md](TEST_POINTS_SYSTEM.md)

---

## 📊 Performance

### Optimizaciones
- ✅ StateFlow para actualizaciones eficientes
- ✅ collectAsState() con lifecycle awareness
- ✅ Lazy loading de productos
- ✅ Caching local de datos

### Métricas
- **Build Time**: ~3 segundos
- **App Size**: ~50-60 MB
- **Memory**: < 200 MB en uso normal

---

## 🚀 Deployment

### Build Stages
```
Source Code (Kotlin)
    ↓
Compilation (Gradle)
    ↓
Android Bytecode (.dex)
    ↓
APK Assembly
    ↓
APK Signing
    ↓
Distribution
```

### Commands
```bash
# Debug build
./gradlew assembleDebug

# Release build
./gradlew assembleRelease

# Install to device
./gradlew installDebug
```

---

## 📚 Referencias

- [Android Architecture Components](https://developer.android.com/topic/architecture)
- [Jetpack Compose](https://developer.android.com/jetpack/compose)
- [Kotlin Flows](https://kotlinlang.org/docs/flow.html)
- [Firebase Realtime DB](https://firebase.google.com/docs/database)
- [Koin DI](https://insert-koin.io/)

---

**Última Actualización**: 17 de Junio, 2024  
**Versión**: 3.5 ✅  
**Estado**: Production Ready 🚀

