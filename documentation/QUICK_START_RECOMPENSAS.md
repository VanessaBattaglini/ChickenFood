# Quick Start: Sistema de Recompensas

## 🚀 Inicio Rápido

### 1. Crear una Orden con Puntos

```kotlin
// En CartActivity o donde proceses el checkout
val order = RewardsHelper.createOrderFromCart(
    userId = AuthHelper.getUserId() ?: return,
    cartItems = managmentCart.getListCart(),
    totalPrice = managmentCart.getTotalFee(),
    deliveryAddress = "123 Main St",
    notes = "Sin cebolla"
)

// Crear la orden (automáticamente agrega puntos)
orderViewModel.createOrder(order)
```

### 2. Mostrar Puntos del Usuario

```kotlin
// En MainActivity o Dashboard
val rewardsViewModel: RewardsViewModel = koinViewModel()

// Cargar puntos
rewardsViewModel.loadUserRewards(userId = AuthHelper.getUserId() ?: return)

// Observar cambios
rewardsViewModel.userRewards.collect { rewards ->
    if (rewards != null) {
        println("Puntos: ${rewards.pointsBalance}")
        println("Total: ${rewards.totalPoints}")
        println("Nivel: ${RewardsHelper.getUserLevel(rewards.totalPoints)}")
    }
}
```

### 3. Canjear Puntos

```kotlin
rewardsViewModel.redeemPoints(
    userId = AuthHelper.getUserId() ?: return,
    points = 100,
    description = "Descuento en compra"
)
```

### 4. Ver Historial de Puntos

```kotlin
rewardsViewModel.loadPointsHistory(userId = AuthHelper.getUserId() ?: return)

rewardsViewModel.pointsHistory.collect { transactions ->
    transactions.forEach { transaction ->
        println("${transaction.type}: ${transaction.points} puntos")
        println("Descripción: ${transaction.description}")
        println("Fecha: ${Date(transaction.timestamp)}")
    }
}
```

---

## 📊 Cálculos Rápidos

### Puntos Ganados
```kotlin
val puntos = RewardsHelper.calculatePointsFromTotal(100.0)  // 10 puntos
```

### Dinero Equivalente
```kotlin
val dinero = RewardsHelper.calculateDiscountFromPoints(100)  // $1.00
```

### Nivel de Usuario
```kotlin
val nivel = RewardsHelper.getUserLevel(500)  // "Oro"
```

### Cashback Percentage
```kotlin
val cashback = RewardsHelper.getCashbackPercentage("Oro")  // 0.12 (12%)
```

---

## 🎯 Casos de Uso

### Caso 1: Usuario Realiza Compra
```kotlin
// 1. Crear orden
val order = RewardsHelper.createOrderFromCart(
    userId = userId,
    cartItems = cartItems,
    totalPrice = 45.99
)

// 2. Guardar orden (automáticamente agrega puntos)
orderViewModel.createOrder(order)

// 3. Mostrar confirmación
orderViewModel.orderCreatedId.collect { orderId ->
    if (orderId != null) {
        println("Orden creada: $orderId")
        println("Puntos ganados: ${order.pointsEarned}")
    }
}
```

### Caso 2: Usuario Canjea Puntos
```kotlin
// 1. Verificar saldo
rewardsViewModel.userRewards.collect { rewards ->
    if (rewards != null && rewards.pointsBalance >= 100) {
        // 2. Canjear puntos
        rewardsViewModel.redeemPoints(
            userId = userId,
            points = 100,
            description = "Descuento en compra"
        )
    }
}
```

### Caso 3: Mostrar Puntos en Dashboard
```kotlin
@Composable
fun PointsWidget(rewardsViewModel: RewardsViewModel) {
    val rewards by rewardsViewModel.userRewards.collectAsState(null)
    
    rewards?.let { reward ->
        Column {
            Text("Puntos: ${reward.pointsBalance}")
            Text("Nivel: ${RewardsHelper.getUserLevel(reward.totalPoints)}")
            Text("Equivalencia: ${RewardsHelper.formatPointsAsMoney(reward.pointsBalance)}")
        }
    }
}
```

---

## 🔧 Configuración

### En AppModule.kt (ya configurado)
```kotlin
single<RewardsRepository> { RewardsRepositoryImpl(get(), get()) }
single<OrderRepository> { OrderRepositoryImpl(get(), get()) }

viewModel { RewardsViewModel(rewardsRepository = get()) }
viewModel { OrderViewModel(orderRepository = get(), rewardsRepository = get()) }
```

### Inyectar en Activity/Fragment
```kotlin
val rewardsViewModel: RewardsViewModel = koinViewModel()
val orderViewModel: OrderViewModel = koinViewModel()
```

---

## 📊 Estructura de Datos

### UserRewardsModel
```kotlin
UserRewardsModel(
    userId = "abc123",
    totalPoints = 500,        // Total acumulado
    pointsBalance = 350,      // Disponibles
    pointsSpent = 150,        // Gastados
    isPremium = true
)
```

### OrderModel
```kotlin
OrderModel(
    orderId = "order-123",
    userId = "abc123",
    items = listOf(...),
    totalPrice = 45.99,
    pointsEarned = 4,         // 10% del total
    status = "completed"
)
```

### PointsTransactionModel
```kotlin
PointsTransactionModel(
    transactionId = "trans-123",
    userId = "abc123",
    points = 4,               // Positivo = ganados, Negativo = gastados
    type = "purchase",        // "purchase", "redemption", "bonus"
    description = "Cashback 10% - Compra de $45.99 USD",
    balanceBefore = 0,
    balanceAfter = 4
)
```

---

## 🎯 Fórmulas

### Puntos Ganados
```
Puntos = Total × 0.10
Ejemplo: $100 × 0.10 = 10 puntos
```

### Dinero Equivalente
```
Dinero = Puntos × 0.01
Ejemplo: 100 puntos × 0.01 = $1.00
```

### Nivel de Usuario
```
Regular:  0 puntos
Bronce:   1-99 puntos
Plata:    100-499 puntos
Oro:      500-999 puntos
Platino:  1000+ puntos
```

---

## 🔐 Validaciones

### Antes de Canjear
```kotlin
if (rewards.pointsBalance >= pointsToRedeem) {
    rewardsViewModel.redeemPoints(userId, pointsToRedeem, description)
} else {
    println("Saldo insuficiente")
}
```

### Antes de Crear Orden
```kotlin
if (cartItems.isNotEmpty() && totalPrice > 0) {
    val order = RewardsHelper.createOrderFromCart(...)
    orderViewModel.createOrder(order)
} else {
    println("Carrito vacío")
}
```

---

## 📱 Ejemplo Completo en Composable

```kotlin
@Composable
fun RewardsScreen(
    rewardsViewModel: RewardsViewModel,
    orderViewModel: OrderViewModel
) {
    val rewards by rewardsViewModel.userRewards.collectAsState(null)
    val history by rewardsViewModel.pointsHistory.collectAsState(emptyList())
    val isLoading by rewardsViewModel.isLoading.collectAsState(false)
    
    Column(modifier = Modifier.fillMaxSize().padding(16.dp)) {
        // Mostrar puntos actuales
        rewards?.let { reward ->
            Card(modifier = Modifier.fillMaxWidth()) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text("Puntos Disponibles: ${reward.pointsBalance}")
                    Text("Total Acumulado: ${reward.totalPoints}")
                    Text("Nivel: ${RewardsHelper.getUserLevel(reward.totalPoints)}")
                    Text("Equivalencia: ${RewardsHelper.formatPointsAsMoney(reward.pointsBalance)}")
                }
            }
        }
        
        Spacer(modifier = Modifier.height(16.dp))
        
        // Botón para canjear
        Button(
            onClick = {
                rewardsViewModel.redeemPoints(
                    userId = AuthHelper.getUserId() ?: return@Button,
                    points = 100,
                    description = "Descuento"
                )
            },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Canjear 100 Puntos")
        }
        
        Spacer(modifier = Modifier.height(16.dp))
        
        // Mostrar historial
        Text("Historial de Transacciones", fontSize = 18.sp, fontWeight = FontWeight.Bold)
        LazyColumn {
            items(history) { transaction ->
                Card(modifier = Modifier.fillMaxWidth().padding(8.dp)) {
                    Column(modifier = Modifier.padding(12.dp)) {
                        Text("${transaction.type}: ${transaction.points} puntos")
                        Text(transaction.description, fontSize = 12.sp)
                        Text(Date(transaction.timestamp).toString(), fontSize = 10.sp)
                    }
                }
            }
        }
    }
}
```

---

## 🚀 Próximos Pasos

1. **Crear UI para puntos en Dashboard**
2. **Integrar canje en carrito**
3. **Agregar notificaciones**
4. **Crear pantalla de historial**
5. **Implementar bonus y promociones**

---

## 📞 Referencia Rápida

| Función | Uso |
|---------|-----|
| `calculatePointsFromTotal()` | Calcular puntos ganados |
| `calculateDiscountFromPoints()` | Calcular dinero equivalente |
| `getUserLevel()` | Obtener nivel de usuario |
| `getCashbackPercentage()` | Obtener % de cashback |
| `createOrderFromCart()` | Crear orden desde carrito |
| `formatPoints()` | Formatear puntos para UI |
| `formatPointsAsMoney()` | Formatear dinero equivalente |

---

**Última actualización**: 29 de Mayo de 2026
