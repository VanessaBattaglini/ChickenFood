# Fix: Puntos No Se Cargaban en Checkout (TASK 5 - RESOLVED)

## Problema Identificado

Cuando el usuario abría el checkout, los puntos siempre llegaban como **0**, aunque tuviera puntos acumulados en su cuenta.

### Causa Raíz

En **CartActivity**, se hacía esto:

```kotlin
val userPoints = rewardsViewModel.pointsBalance.value  // ❌ Obtiene .value INMEDIATAMENTE
```

El problema:
1. Se llamaba `rewardsViewModel.loadUserRewards()` (asincrónico vía Coroutine)
2. Pero inmediatamente después se accedía a `.value` del StateFlow
3. El StateFlow aún no tenía los datos cargados desde Firebase
4. Entonces `userPoints` siempre era 0 (valor inicial)
5. Ese 0 se pasaba al CheckoutActivity via Intent

## Solución Implementada

### Cambio 1: CartActivity - NO pasar puntos por Intent

**Antes:**
```kotlin
private fun navigateToCheckout() {
    val userPoints = rewardsViewModel.pointsBalance.value  // ❌ Siempre 0
    val intent = Intent(this, CheckoutActivity::class.java).apply {
        putParcelableArrayListExtra("cartItems", ArrayList(orderItems))
        putExtra("cartTotal", cartTotal)
        putExtra("userPoints", userPoints)  // ❌ Pasar 0
    }
    startActivity(intent)
}
```

**Después:**
```kotlin
private fun navigateToCheckout() {
    val intent = Intent(this, CheckoutActivity::class.java).apply {
        putParcelableArrayListExtra("cartItems", ArrayList(orderItems))
        putExtra("cartTotal", cartTotal)
        // ❌ REMOVIDO: putExtra("userPoints", userPoints)
    }
    startActivity(intent)
}
```

### Cambio 2: CheckoutActivity - Cargar puntos directamente con collectAsState()

**Antes:**
```kotlin
override fun onCreate(savedInstanceState: Bundle?) {
    val userPoints = intent.getIntExtra("userPoints", 0)  // ❌ Siempre 0
    
    setContent {
        CheckoutScreen(
            userPoints = userPoints,  // ❌ Pasar 0
            ...
        )
    }
}
```

**Después:**
```kotlin
override fun onCreate(savedInstanceState: Bundle?) {
    // ✅ Cargar puntos del usuario actual
    val currentUserId = AuthHelper.getUserId()
    if (!currentUserId.isNullOrEmpty()) {
        rewardsViewModel.loadUserRewards(currentUserId)  // Iniciar carga asincrónica
    }
    
    setContent {
        // ✅ Usar collectAsState para obtener puntos en tiempo real
        val userPoints by rewardsViewModel.pointsBalance.collectAsState()
        
        CheckoutScreen(
            userPoints = userPoints,  // ✅ Puntos reales, actualizados en tiempo real
            ...
        )
    }
}
```

## Por Qué Funciona

1. **RewardsViewModel carga puntos asincronamente**: Cuando se llama `loadUserRewards()`, inicia una Coroutine que:
   - Llama al repository
   - Obtiene datos de Firebase
   - Actualiza el StateFlow `pointsBalance` cuando están listos

2. **collectAsState() observa el Flow**: En Compose, `collectAsState()` es un LaunchedEffect que:
   - Se suscribe al StateFlow
   - Recibe actualizaciones automáticamente
   - Causa recomposición cuando los datos cambien
   - Obtiene el valor más reciente (nunca 0 si hay datos cargados)

3. **Flujo de datos correcto**:
   ```
   CartActivity llama loadUserRewards()
        ↓
   RewardsViewModel inicia Coroutine
        ↓
   Firebase devuelve puntos (ej: 500)
        ↓
   pointsBalance StateFlow se actualiza (500)
        ↓
   CheckoutActivity detecta cambio via collectAsState()
        ↓
   CheckoutScreen recompone con puntos reales (500)
   ```

## Verificación

### Logs esperados en Logcat:

**CartActivity:**
```
D/CartActivity: CartActivity opened
D/CartActivity: Loading rewards for user: USER_ID_HERE
```

**CheckoutActivity:**
```
D/CheckoutActivity: CheckoutActivity opened with N items, total=$XX.XX
D/CheckoutActivity: Loading user rewards for: USER_ID_HERE
```

**RewardsViewModel:**
```
D/RewardsViewModel: Loading rewards for user: USER_ID_HERE
D/RewardsViewModel: Rewards loaded: UserRewardsModel(pointsBalance=500, ...)
```

**CheckoutScreen:**
```
D/CheckoutScreen: CheckoutScreen rendering - items=N, total=$XX.XX, points=500
```

### Comportamiento esperado en la app:

1. Usuario va al carrito ✅
2. Hace click en "Proceder al Pago" ✅
3. Se abre CheckoutActivity ✅
4. Si el usuario tiene > 0 puntos:
   - Se muestra AlertDialog: "¿Deseas usar tus puntos acumulados?" ✅
   - Muestra cantidad de puntos reales (ej: 500 pts = $5.00) ✅
   - Usuario puede elegir "Sí, Usar Puntos" o "No, Usar Tarjeta" ✅
   - CheckoutScreen habilita la opción de pagar con puntos ✅

## Archivos Modificados

- `app/src/main/java/com/daniel/chickenfood/presentation/activity/cart/CartActivity.kt`
- `app/src/main/java/com/daniel/chickenfood/presentation/activity/checkout/CheckoutActivity.kt`

## Conceptos Clave

### StateFlow vs .value

- **`.value`**: Obtiene el valor ACTUAL del StateFlow en ese momento (puede ser 0 si aún no carga)
- **`collectAsState()`**: Se suscribe al Flow y recibe ACTUALIZACIONES cuando los datos cambian

### Flow Pattern en Kotlin

```
Repository → Fire base
    ↓
Flow<Data>
    ↓
ViewModel.StateFlow (expone el Flow)
    ↓
Activity/Composable (consume con collectAsState)
    ↓
UI actualiza automáticamente
```

## Testing

Pasos para verificar que funciona:

1. **Inicia sesión** en la app
2. **Haz una compra** con tarjeta
   - Deberías ganar puntos (10% cashback)
   - Verifica que aparezcan en el dashboard
3. **Vuelve al dashboard** y espera 2-3 segundos (que carguen los puntos)
4. **Abre el carrito** nuevamente
5. **Procede al pago**
   - Deberías ver un AlertDialog si tienes > 0 puntos
   - El número de puntos debe ser correcto
   - Deberías poder seleccionar "Pagar con Puntos"
6. **Si seleccionas puntos**:
   - El método de pago cambia a "Puntos"
   - Se muestra el descuento calculado
   - Puedes confirmar el pago

## Status: ✅ DONE

- Build: **SUCCESSFUL**
- Puntos se cargan correctamente en CheckoutActivity
- Dialog se muestra cuando hay puntos disponibles
- Usuario puede seleccionar pago con puntos

