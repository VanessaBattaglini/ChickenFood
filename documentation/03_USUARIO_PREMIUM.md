# 👑 Guía del Usuario Premium (Con Autenticación)

## Resumen

Un usuario Premium es quien se ha autenticado con Google. Disfruta de **todas** las actividades del Usuario No Premium **PLUS** acceso al sistema de puntos y recompensas.

**Diferencia clave:** Puede acumular puntos en cada compra y usar esos puntos para obtener descuentos.

## 🎯 Actividades Disponibles

### ✅ Actividades Comunes (También Usuario No Premium)

El Usuario Premium puede hacer TODO lo que hace el Usuario No Premium:

1. ✅ Ver Dashboard
2. ✅ Seleccionar Categoría
3. ✅ Ver Detalle de Producto
4. ✅ Agregar al Carrito
5. ✅ Ver Carrito
6. ✅ Cambiar Cantidades en Carrito

---

### 🆕 Actividades Exclusivas (Solo Usuario Premium)

#### 1️⃣ Ver Saldo de Puntos

**Ubicación:** TopBar o PointsCard en Dashboard

```
┌──────────────────────┐
│  💰 Mis Puntos       │
│  500 puntos          │
│  Nivel: Oro 🏆      │
│  Progreso al Platino │
│  ████░░░░░░░ 40%    │
└──────────────────────┘
```

**ViewModel:** `RewardsViewModel`

**Métodos:**
```kotlin
// Obtener recompensas del usuario
rewardsViewModel.getUserRewards(userId)

// Observar cambios de puntos
lifecycleScope.launch {
    rewardsViewModel.userRewardsState.collect { state ->
        when (state) {
            is RewardsState.Success -> {
                val rewards = state.userRewards
                mostrarPuntos(rewards.pointsBalance)
                mostrarNivel(rewards.totalPoints)
            }
            is RewardsState.Error -> { }
            is RewardsState.Loading -> { }
        }
    }
}
```

**Datos mostrados:**
```
├─ Puntos Balance: Puntos disponibles para usar
├─ Puntos Totales: Puntos acumulados en la historia
├─ Puntos Gastados: Puntos ya canjeados
├─ Nivel: Regular, Bronce, Plata, Oro, Platino
└─ Progreso: % hacia siguiente nivel
```

**Niveles y Cashback:**

| Nivel | Rango de Puntos | Cashback | Requisito |
|-------|-----------------|----------|-----------|
| Regular | 0 - 99 | 10% | Nuevo usuario |
| Bronce | 100 - 499 | 10.5% | 100 puntos |
| Plata | 500 - 999 | 11% | 500 puntos |
| Oro | 1000 - 4999 | 12% | 1000 puntos |
| Platino | 5000+ | 15% | 5000 puntos |

---

#### 2️⃣ Usar Puntos en el Carrito

**Actividad:** `CartActivity.kt`

Después de autenticarse, el usuario ve una opción para usar puntos:

```
┌─────────────────────────────┐
│  Mi Carrito                 │
├─────────────────────────────┤
│  Producto 1 × 2 = $25.98   │
│  Producto 2 × 1 = $12.99   │
├─────────────────────────────┤
│  Subtotal:        $38.97   │
│  ┌─────────────────────────┐│
│  │ 💰 Usar Puntos          ││
│  └─────────────────────────┘│
│                             │
│  Puntos disponibles: 500    │
│  ┌─────────────────────────┐│
│  │ Puntos a usar: [250]    ││
│  │ Descuento: -$2.50       ││
│  └─────────────────────────┘│
│                             │
│  Subtotal:        $38.97   │
│  Descuento:       -$2.50   │
│  ──────────────────────────  │
│  TOTAL FINAL:     $36.47   │
├─────────────────────────────┤
│  ┌─────────────────────────┐│
│  │ 💳 Proceder al Pago    ││
│  └─────────────────────────┘│
│                             │
│  ┌─────────────────────────┐│
│  │ Continuar Comprando     ││
│  └─────────────────────────┘│
└─────────────────────────────┘
```

**Métodos:**
```kotlin
// En CartActivity
private fun handleUsePointsClick() {
    // 1. Obtener puntos disponibles del usuario
    rewardsViewModel.getUserRewards(userId)
    
    // 2. Mostrar input para ingresar puntos
    mostrarDialogoUsorPuntos()
    
    // 3. Validar puntos
    fun validarPuntos(puntosAUsar: Int): Boolean {
        return puntosAUsar <= puntosDisponibles
    }
    
    // 4. Calcular descuento
    fun calcularDescuento(puntos: Int): Double {
        return puntos * 0.01 // 1 punto = $0.01
    }
    
    // 5. Actualizar total
    totalFinal = subtotal - descuento
}
```

**Lógica de canje:**

```kotlin
fun aplicarCanje(puntos: Int, orderId: String, userId: String) {
    // 1. Restar puntos del balance
    rewardsRepository.updatePointsBalance(userId, puntos, "spent")
    
    // 2. Guardar transacción
    val transaction = PointsTransaction(
        userId = userId,
        points = -puntos,
        type = "discount",
        timestamp = System.currentTimeMillis(),
        orderId = orderId,
        balanceAfter = nuevoBalance
    )
    transactionRepository.saveTransaction(transaction)
    
    // 3. Guardar orden con descuento aplicado
    val order = Order(
        orderId = orderId,
        userId = userId,
        items = cartItems,
        totalPrice = precioOriginal,
        pointsUsed = puntos,
        discountAmount = puntos * 0.01,
        finalPrice = totalFinal,
        pointsEarned = (totalFinal * 0.10).toInt(),
        status = "pending"
    )
    orderRepository.saveOrder(order)
}
```

**Validaciones:**
```kotlin
// No puede usar más puntos de los que tiene
if (puntosAUsar > puntosDisponibles) {
    mostrarError("No tienes suficientes puntos")
    return
}

// No puede usar puntos negativos
if (puntosAUsar < 0) {
    mostrarError("Cantidad inválida")
    return
}

// El descuento no puede ser mayor que el total
if (descuento > subtotal) {
    mostrarError("Descuento mayor al total")
    return
}
```

---

#### 3️⃣ Proceder al Pago (Completar Compra)

**Actividad:** `CartActivity.kt` → Botón "Proceder al Pago"

Cuando el usuario Premium hace clic en "Proceder al Pago":

```
1. Validar que el usuario esté autenticado
   ↓
2. Validar que haya items en el carrito
   ↓
3. Si usó puntos → Aplicar descuento
   ↓
4. Guardar orden en Firebase
   ↓
5. Calcular puntos ganados (10% + bonus por nivel)
   ↓
6. Guardar puntos ganados
   ↓
7. Guardar transacción (auditoría)
   ↓
8. Mostrar confirmación
   ↓
9. Limpiar carrito
   ↓
10. Navegar a OrderConfirmationActivity
```

**Código:**
```kotlin
fun procesarPago() {
    // 1. Validar autenticación
    if (!AuthHelper.isAuthenticated()) {
        mostrarError("Debes autenticarte")
        return
    }
    
    // 2. Validar carrito
    if (cart.isEmpty()) {
        mostrarError("Carrito vacío")
        return
    }
    
    // 3. Crear orden
    val order = Order(
        orderId = UUID.randomUUID().toString(),
        userId = FirebaseAuth.getInstance().currentUser?.uid!!,
        items = cart,
        totalPrice = total,
        pointsUsed = puntosUsados,
        discountAmount = puntosUsados * 0.01,
        finalPrice = totalFinal,
        status = "completed",
        timestamp = System.currentTimeMillis()
    )
    
    // 4. Guardar orden
    orderRepository.saveOrder(order).collect { success ->
        if (success) {
            // 5. Calcular y guardar puntos
            val puntosGanados = calcularPuntosGanados(
                totalFinal = totalFinal,
                nivelUsuario = userLevel
            )
            
            rewardsViewModel.agregarPuntos(
                userId = userId,
                puntos = puntosGanados
            )
            
            // 6. Mostrar confirmación
            mostrarConfirmacion(order, puntosGanados)
            
            // 7. Limpiar y navegar
            ManagmentCart.clearCart()
            navigateToConfirmation(order)
        } else {
            mostrarError("Error al procesar la compra")
        }
    }
}
```

**Cálculo de puntos ganados:**
```kotlin
fun calcularPuntosGanados(totalFinal: Double, nivelUsuario: String): Int {
    val basePoints = (totalFinal * 0.10).toInt()
    
    return when (nivelUsuario) {
        "regular" -> basePoints
        "bronce" -> (basePoints * 1.005).toInt()
        "plata" -> (basePoints * 1.10).toInt()
        "oro" -> (basePoints * 1.20).toInt()
        "platino" -> (basePoints * 1.50).toInt()
        else -> basePoints
    }
}
```

---

#### 4️⃣ Ver Confirmación de Orden

**Actividad:** `OrderConfirmationActivity.kt` (a implementar)

Después de completar la compra, se muestra:

```
┌──────────────────────────────┐
│  ✅ ¡Compra Realizada!      │
│                              │
│  Número de Orden: #12345    │
│  Fecha: 01/06/2026          │
├──────────────────────────────┤
│  Productos:                  │
│  ├─ Hamburguesa × 2  $25.98 │
│  ├─ Pizza × 1        $12.99 │
│  └─ Bebida × 3        $9.99 │
├──────────────────────────────┤
│  Subtotal:           $48.96 │
│  Puntos Usados: -250      │
│  Descuento:           -$2.50│
│  ──────────────────────────── │
│  TOTAL:              $46.46 │
├──────────────────────────────┤
│  🎉 Puntos Ganados: +46   │
│  Nuevo Balance: 296 puntos  │
├──────────────────────────────┤
│  ┌────────────────────────┐  │
│  │ Ir al Dashboard        │  │
│  └────────────────────────┘  │
│                              │
│  ┌────────────────────────┐  │
│  │ Ver Historial          │  │
│  └────────────────────────┘  │
└──────────────────────────────┘
```

**Métodos:**
```kotlin
// Obtener detalles de la orden
orderViewModel.getOrderById(orderId)

// Mostrar datos
when (orderState) {
    is OrderState.Success -> {
        val order = state.order
        mostrarNumeroPedido(order.orderId)
        mostrarItems(order.items)
        mostrarTotal(order.finalPrice)
        mostrarPuntosGanados(order.pointsEarned)
    }
}
```

---

#### 5️⃣ Ver Historial de Compras

**Actividad:** `OrderHistoryActivity.kt` (a implementar)

El usuario puede ver todas sus compras anteriores:

```
┌─────────────────────────────┐
│  Mis Compras                │
├─────────────────────────────┤
│  Orden #12345               │
│  01/06/2026 - 14:30         │
│  Total: $46.46              │
│  Puntos ganados: +46        │
│  Estado: Completada ✅      │
├─────────────────────────────┤
│  Orden #12344               │
│  31/05/2026 - 19:45         │
│  Total: $32.99              │
│  Puntos ganados: +33        │
│  Estado: Completada ✅      │
├─────────────────────────────┤
│  Orden #12343               │
│  30/05/2026 - 12:15         │
│  Total: $28.50              │
│  Puntos ganados: +28        │
│  Estado: Completada ✅      │
└─────────────────────────────┘
```

**Métodos:**
```kotlin
// Obtener historial de órdenes
orderViewModel.getOrdersByUserId(userId)

// Obtener historial de transacciones de puntos
rewardsViewModel.getTransactionHistory(userId)

// Filtrar por rango de fechas
orderViewModel.filterOrdersByDate(startDate, endDate)
```

---

#### 6️⃣ Ver Historial de Transacciones de Puntos

**Pantalla:** Dentro de Perfil o Sección de Puntos

Muestra todas las transacciones de puntos:

```
┌─────────────────────────────┐
│  Historial de Puntos        │
├─────────────────────────────┤
│  ➕ Ganados                  │
│  01/06/2026 - +46 puntos   │
│  Compra #12345              │
├─────────────────────────────┤
│  ➖ Gastados                │
│  01/06/2026 - -250 puntos  │
│  Descuento en compra        │
├─────────────────────────────┤
│  ➕ Ganados                  │
│  31/05/2026 - +33 puntos   │
│  Compra #12344              │
└─────────────────────────────┘
```

**Métodos:**
```kotlin
rewardsViewModel.getTransactionHistory(userId).collect { transactions ->
    transactions.forEach { transaction ->
        val icon = if (transaction.type == "earned") "➕" else "➖"
        val color = if (transaction.type == "earned") Green else Red
        mostrarTransaccion(icon, transaction, color)
    }
}
```

**Tipos de transacciones:**
- `earned` - Puntos ganados por compra
- `spent` - Puntos gastados en descuento
- `bonus` - Puntos bonus por nivel
- `refund` - Reembolso de puntos

---

#### 7️⃣ Ver Perfil

**Ubicación:** Ícono de perfil en TopBar

Muestra información del usuario:

```
┌─────────────────────────────┐
│  Mi Perfil                  │
├─────────────────────────────┤
│  👤 John Doe                │
│  📧 john@gmail.com          │
│  📅 Miembro desde: 01/01/26 │
├─────────────────────────────┤
│  👑 Nivel: ORO              │
│  💰 Puntos: 500             │
│  🎯 Progreso: 40% a Platino │
├─────────────────────────────┤
│  Opciones:                  │
│  ├─ Ver compras             │
│  ├─ Ver transacciones       │
│  ├─ Mis direcciones         │
│  ├─ Métodos de pago         │
│  └─ Configuración           │
├─────────────────────────────┤
│  ┌─────────────────────────┐│
│  │ 🚪 Logout              ││
│  └─────────────────────────┘│
└─────────────────────────────┘
```

**Métodos:**
```kotlin
// Obtener perfil del usuario
authHelper.getCurrentUser()

// Obtener información de recompensas
rewardsViewModel.getUserRewards(userId)

// Logout
AuthHelper.logout()
tokenViewModel.logout(userId)
```

---

#### 8️⃣ Logout

**Ubicación:** Botón de perfil en TopBar

```kotlin
fun handleLogout() {
    // 1. Marcar token como inactivo
    tokenViewModel.logout(userId)
    
    // 2. Cerrar sesión en Firebase
    FirebaseAuth.getInstance().signOut()
    
    // 3. Limpiar caché local
    AuthHelper.logout()
    
    // 4. Navegar a SplashActivity
    val intent = Intent(this, SplashActivity::class.java)
    intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
    startActivity(intent)
    finish()
}
```

**Resultado:** Retorna a SplashActivity y el usuario ve los botones "Empecemos" / "Inscribete"

---

## 📊 Resumen de Actividades

| Actividad | Descripción | ViewModel | Clase Principal |
|-----------|-------------|-----------|-----------------|
| Ver Saldo | Mostrar puntos y nivel | RewardsViewModel | TopBar/Dashboard |
| Usar Puntos | Aplicar descuento en carrito | RewardsViewModel | CartActivity |
| Proceder al Pago | Completar compra | OrderViewModel | CartActivity |
| Ver Confirmación | Mostrar resumen de orden | OrderViewModel | OrderConfirmationActivity |
| Ver Compras | Historial de órdenes | OrderViewModel | OrderHistoryActivity |
| Ver Transacciones | Historial de puntos | RewardsViewModel | ProfileActivity |
| Ver Perfil | Información del usuario | AuthHelper | ProfileActivity |
| Logout | Cerrar sesión | AuthHelper | TopBar |

---

## 🎯 Comparación: Usuario No Premium vs Premium

| Funcionalidad | No Premium | Premium |
|---------------|:----------:|:-------:|
| Ver productos | ✅ | ✅ |
| Filtrar categorías | ✅ | ✅ |
| Ver detalles | ✅ | ✅ |
| Agregar carrito | ✅ | ✅ |
| Ver carrito | ✅ | ✅ |
| **Completar compra** | ❌ | ✅ |
| **Ver puntos** | ❌ | ✅ |
| **Usar puntos** | ❌ | ✅ |
| **Historial de compras** | ❌ | ✅ |
| **Historial de puntos** | ❌ | ✅ |
| **Ver perfil** | ❌ | ✅ |

---

## 💾 Datos Guardados en Firebase

### Estructura de la compra:
```
orders/{orderId}/
├── orderId: "uuid"
├── userId: "firebase_uid"
├── items: [
│   {
│       productId: "prod_1",
│       name: "Hamburguesa",
│       quantity: 2,
│       price: 12.99,
│       subtotal: 25.98
│   }
]
├── totalPrice: 48.96
├── pointsUsed: 250
├── discountAmount: 2.50
├── finalPrice: 46.46
├── pointsEarned: 46
├── status: "completed"
└── timestamp: 1717000000000
```

### Estructura de transacción de puntos:
```
pointsTransactions/{transactionId}/
├── userId: "firebase_uid"
├── type: "earned" | "spent" | "bonus"
├── points: 46
├── orderId: "order_123"
├── balanceAfter: 546
├── timestamp: 1717000000000
└── description: "Compra #12345"
```

---

## 🔄 Flujo Completo del Usuario Premium

```
1. Se autentica con Google
   ↓
2. Se guardan tokens en Firebase
   ↓
3. Va a MainActivity (Dashboard)
   ↓
4. Ve TopBar con ícono de puntos
   ↓
5. Selecciona productos y agrega al carrito
   ↓
6. Va al carrito
   ↓
7. Ve opción "Usar Puntos"
   ↓
8. Ingresa cantidad de puntos a usar
   ↓
9. Ve descuento calculado
   ↓
10. Hace clic "Proceder al Pago"
    ↓
11. Compra se procesa en Firebase
    ↓
12. Se calculan puntos ganados
    ↓
13. Ve OrderConfirmationActivity
    ↓
14. Puede ver historial de compras
    ↓
15. Puede hacer logout
```

---

## 📝 Componentes Relacionados

- [Flujo de Autenticación](./01_AUTENTICACION.md)
- [Guía del Usuario No Premium](./02_USUARIO_NO_PREMIUM.md)
- [Sistema de Recompensas](./04_SISTEMA_RECOMPENSAS.md)

---

**Estado:** Parcialmente implementado (UI pendiente)
**Última actualización:** 2026-06-01

