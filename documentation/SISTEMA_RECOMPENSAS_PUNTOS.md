# Sistema de Recompensas con Puntos (Cashback 10%)

## 📋 Descripción General

Se ha implementado un sistema completo de recompensas con puntos de cashback del 10% para usuarios premium. Los usuarios acumulan puntos con cada compra que pueden canjear por productos o descuentos.

---

## 🎯 Características Principales

### 1. **Acumulación de Puntos**
- **10% de cashback** en cada compra aprobada
- Ejemplo: Compra de $100 USD = 10 puntos acumulados
- Los puntos se guardan de forma segura en Firebase
- Historial completo de transacciones

### 2. **Canje de Puntos**
- 1 punto = $0.01 de descuento
- Mínimo de puntos para canjear: configurable
- Historial de canjes disponible
- Transacciones auditables

### 3. **Niveles de Usuario**
- **Regular**: 0 puntos
- **Bronce**: 1-99 puntos (10% cashback)
- **Plata**: 100-499 puntos (11% cashback)
- **Oro**: 500-999 puntos (12% cashback)
- **Platino**: 1000+ puntos (15% cashback)

### 4. **Seguridad**
- Datos guardados en Firebase Realtime Database
- Transacciones auditables
- Validación de saldo antes de canjear
- Historial completo de movimientos

---

## 🏗️ Arquitectura Implementada

### Modelos de Datos

#### UserRewardsModel
```kotlin
data class UserRewardsModel(
    val userId: String,           // UID de Firebase Auth
    val totalPoints: Int,         // Puntos totales acumulados
    val pointsBalance: Int,       // Puntos disponibles
    val pointsSpent: Int,         // Puntos gastados
    val createdAt: Long,          // Fecha de creación
    val lastUpdated: Long,        // Última actualización
    val isPremium: Boolean        // Indica si es usuario premium
)
```

#### OrderModel
```kotlin
data class OrderModel(
    val orderId: String,          // ID único de la orden
    val userId: String,           // UID del usuario
    val items: List<OrderItemModel>,  // Items de la orden
    val totalPrice: Double,       // Precio total
    val pointsEarned: Int,        // Puntos ganados (10% del total)
    val orderDate: Long,          // Fecha de la orden
    val status: String,           // "pending", "completed", "cancelled"
    val deliveryAddress: String,  // Dirección de entrega
    val notes: String             // Notas adicionales
)
```

#### PointsTransactionModel
```kotlin
data class PointsTransactionModel(
    val transactionId: String,    // ID único de la transacción
    val userId: String,           // UID del usuario
    val points: Int,              // Positivo = ganados, Negativo = gastados
    val type: String,             // "purchase", "redemption", "bonus", "adjustment"
    val description: String,      // Descripción de la transacción
    val orderId: String,          // Referencia a la orden
    val timestamp: Long,          // Fecha de la transacción
    val balanceBefore: Int,       // Saldo antes
    val balanceAfter: Int         // Saldo después
)
```

### Repositorios

#### RewardsRepository
```kotlin
interface RewardsRepository {
    fun getUserRewards(userId: String): Flow<UserRewardsModel>
    fun updateUserRewards(rewards: UserRewardsModel): Flow<Boolean>
    fun addPointsTransaction(transaction: PointsTransactionModel): Flow<Boolean>
    fun getPointsHistory(userId: String): Flow<List<PointsTransactionModel>>
    fun redeemPoints(userId: String, points: Int, description: String): Flow<Boolean>
    fun addPointsFromPurchase(userId: String, orderTotal: Double, orderId: String): Flow<Boolean>
    fun getPointsBalance(userId: String): Flow<Int>
}
```

#### OrderRepository
```kotlin
interface OrderRepository {
    fun createOrder(order: OrderModel): Flow<String>
    fun getOrderHistory(userId: String): Flow<List<OrderModel>>
    fun getOrderById(orderId: String): Flow<OrderModel>
    fun updateOrderStatus(orderId: String, status: String): Flow<Boolean>
    fun getCompletedOrders(userId: String): Flow<List<OrderModel>>
    fun getTotalSpent(userId: String): Flow<Double>
}
```

### ViewModels

#### RewardsViewModel
Gestiona el estado de los puntos de recompensa:
- Cargar puntos del usuario
- Cargar historial de transacciones
- Canjear puntos
- Agregar puntos por compra

#### OrderViewModel
Gestiona el estado de las órdenes:
- Crear órdenes
- Cargar historial de órdenes
- Obtener orden específica
- Actualizar estado de orden
- Calcular total gastado

### Helpers

#### RewardsHelper
Funciones utilitarias para el sistema de recompensas:
- Calcular puntos (10% del total)
- Convertir FoodModel a OrderItemModel
- Crear orden desde carrito
- Calcular descuento en dinero
- Determinar nivel de usuario
- Formatear puntos y dinero

---

## 📊 Estructura Firebase

```
root/
├── users/
│   └── {userId}/
│       ├── email: "user@example.com"
│       ├── displayName: "John Doe"
│       ├── createdAt: 1234567890
│       └── rewards/
│           ├── totalPoints: 500
│           ├── pointsBalance: 350
│           ├── pointsSpent: 150
│           ├── isPremium: true
│           ├── createdAt: 1234567890
│           └── lastUpdated: 1234567890
│
├── orders/
│   └── {orderId}/
│       ├── userId: "{userId}"
│       ├── items: [...]
│       ├── totalPrice: 45.99
│       ├── pointsEarned: 45
│       ├── orderDate: 1234567890
│       ├── status: "completed"
│       ├── deliveryAddress: "..."
│       └── notes: "..."
│
└── pointsTransactions/
    └── {transactionId}/
        ├── userId: "{userId}"
        ├── points: 45
        ├── type: "purchase"
        ├── description: "Cashback 10% - Compra de $45.99 USD"
        ├── orderId: "{orderId}"
        ├── timestamp: 1234567890
        ├── balanceBefore: 0
        └── balanceAfter: 45
```

---

## 🔄 Flujo de Compra con Puntos

```
┌─────────────────────────────────────────────────────────────┐
│ 1. Usuario agrega productos al carrito                      │
│    ↓                                                         │
│ 2. Usuario hace clic en "Proceder al Pago"                 │
│    ↓                                                         │
│ 3. Se crea una orden con los items del carrito             │
│    ├─ orderId: UUID generado                               │
│    ├─ userId: UID del usuario autenticado                  │
│    ├─ items: Lista de OrderItemModel                       │
│    ├─ totalPrice: Suma de subtotales                       │
│    ├─ pointsEarned: 10% del totalPrice                     │
│    └─ status: "pending"                                    │
│    ↓                                                         │
│ 4. Se guarda la orden en Firebase                          │
│    ├─ orders/{orderId}                                     │
│    └─ Se retorna el orderId                                │
│    ↓                                                         │
│ 5. Se agregan puntos al usuario                            │
│    ├─ Obtener recompensas actuales                         │
│    ├─ Calcular puntos: totalPrice * 0.10                   │
│    ├─ Actualizar totalPoints y pointsBalance               │
│    └─ Crear transacción de puntos                          │
│    ↓                                                         │
│ 6. Se actualiza el estado de la orden a "completed"        │
│    ├─ orders/{orderId}/status = "completed"                │
│    └─ Se notifica al usuario                               │
│    ↓                                                         │
│ 7. Usuario ve confirmación de compra                       │
│    ├─ Orden creada exitosamente                            │
│    ├─ Puntos ganados: X puntos                             │
│    ├─ Saldo total: Y puntos                                │
│    └─ Botón para ver historial de puntos                   │
└─────────────────────────────────────────────────────────────┘
```

---

## 💰 Cálculo de Puntos

### Fórmula Básica
```
Puntos Ganados = Total de Compra × 10%
Ejemplo: $100 USD × 0.10 = 10 puntos
```

### Equivalencia de Dinero
```
1 punto = $0.01
10 puntos = $0.10
100 puntos = $1.00
1000 puntos = $10.00
```

### Niveles y Cashback
```
Regular (0 pts):      10% cashback
Bronce (1-99 pts):    10% cashback
Plata (100-499 pts):  11% cashback
Oro (500-999 pts):    12% cashback
Platino (1000+ pts):  15% cashback
```

---

## 🔐 Seguridad

### Validaciones
- ✅ Verificar que el usuario está autenticado
- ✅ Validar que el saldo es suficiente antes de canjear
- ✅ Verificar que los puntos son positivos
- ✅ Auditar todas las transacciones

### Almacenamiento
- ✅ Datos guardados en Firebase Realtime Database
- ✅ Encriptación en tránsito (HTTPS)
- ✅ Reglas de seguridad en Firebase
- ✅ Historial completo de transacciones

### Transacciones Atómicas
- ✅ Actualizar recompensas y crear transacción juntas
- ✅ Validar antes de actualizar
- ✅ Registrar saldo antes y después

---

## 📱 Integración en la UI

### Dashboard (MainActivity)
- Mostrar saldo de puntos actual
- Mostrar nivel de usuario
- Botón para ver historial de puntos
- Botón para canjear puntos

### Carrito (CartActivity)
- Mostrar puntos que se ganarán con esta compra
- Opción de usar puntos para descuento
- Mostrar total con descuento aplicado

### Perfil de Usuario
- Mostrar puntos totales acumulados
- Mostrar puntos disponibles
- Mostrar puntos gastados
- Historial de transacciones

### Historial de Puntos
- Lista de todas las transacciones
- Fecha y hora de cada transacción
- Tipo de transacción (compra, canje, bonus)
- Descripción detallada
- Saldo antes y después

---

## 🔧 Cómo Usar

### Crear una Orden
```kotlin
val order = RewardsHelper.createOrderFromCart(
    userId = "user123",
    cartItems = listOf(food1, food2, food3),
    totalPrice = 45.99,
    deliveryAddress = "123 Main St",
    notes = "Sin cebolla"
)

orderViewModel.createOrder(order)
```

### Cargar Puntos del Usuario
```kotlin
rewardsViewModel.loadUserRewards(userId = "user123")

// Observar cambios
rewardsViewModel.userRewards.collect { rewards ->
    println("Puntos: ${rewards?.pointsBalance}")
}
```

### Canjear Puntos
```kotlin
rewardsViewModel.redeemPoints(
    userId = "user123",
    points = 100,
    description = "Descuento en compra"
)
```

### Cargar Historial de Puntos
```kotlin
rewardsViewModel.loadPointsHistory(userId = "user123")

// Observar cambios
rewardsViewModel.pointsHistory.collect { transactions ->
    transactions.forEach { transaction ->
        println("${transaction.type}: ${transaction.points} puntos")
    }
}
```

---

## 📊 Ejemplo de Flujo Completo

### Escenario: Usuario Premium Realiza Compra

```
1. Usuario autenticado: user@example.com (UID: abc123)
   Puntos actuales: 50 puntos

2. Usuario agrega al carrito:
   - Pollo Asado x2 = $30
   - Hamburguesa x1 = $15
   Total: $45

3. Usuario hace clic en "Proceder al Pago"

4. Se crea la orden:
   {
     orderId: "order-xyz789",
     userId: "abc123",
     items: [
       { title: "Pollo Asado", price: 15, quantity: 2, subtotal: 30 },
       { title: "Hamburguesa", price: 15, quantity: 1, subtotal: 15 }
     ],
     totalPrice: 45.00,
     pointsEarned: 4,  // 45 * 0.10 = 4.5 → 4 puntos
     status: "pending"
   }

5. Se guarda en Firebase:
   orders/order-xyz789 = { ... }

6. Se agregan puntos:
   users/abc123/rewards = {
     totalPoints: 54,      // 50 + 4
     pointsBalance: 54,    // 50 + 4
     pointsSpent: 0,
     isPremium: true
   }

7. Se crea transacción:
   pointsTransactions/trans-123 = {
     userId: "abc123",
     points: 4,
     type: "purchase",
     description: "Cashback 10% - Compra de $45.00 USD",
     orderId: "order-xyz789",
     balanceBefore: 50,
     balanceAfter: 54
   }

8. Usuario ve confirmación:
   ✓ Orden creada exitosamente
   ✓ Puntos ganados: 4 puntos
   ✓ Saldo total: 54 puntos
```

---

## 🚀 Próximos Pasos

1. **Crear UI para mostrar puntos**
   - Widget de puntos en Dashboard
   - Pantalla de historial de puntos
   - Pantalla de canje de puntos

2. **Integrar canje de puntos en carrito**
   - Opción de usar puntos para descuento
   - Mostrar descuento en tiempo real
   - Validar saldo antes de aplicar

3. **Crear notificaciones**
   - Notificar cuando se ganan puntos
   - Notificar cuando se canjen puntos
   - Notificar cambios de nivel

4. **Agregar estadísticas**
   - Total gastado
   - Puntos ganados
   - Puntos canjeados
   - Nivel actual

5. **Implementar bonus**
   - Puntos de bienvenida
   - Puntos por referral
   - Puntos por cumpleaños

---

## 📝 Archivos Creados

### Modelos
- ✅ `UserRewardsModel.kt` - Modelo de recompensas del usuario
- ✅ `OrderModel.kt` - Modelo de orden
- ✅ `PointsTransactionModel.kt` - Modelo de transacción de puntos

### Repositorios
- ✅ `RewardsRepository.kt` - Interfaz de repositorio de recompensas
- ✅ `OrderRepository.kt` - Interfaz de repositorio de órdenes
- ✅ `RewardsRepositoryImpl.kt` - Implementación de repositorio de recompensas
- ✅ `OrderRepositoryImpl.kt` - Implementación de repositorio de órdenes

### ViewModels
- ✅ `RewardsViewModel.kt` - ViewModel de recompensas
- ✅ `OrderViewModel.kt` - ViewModel de órdenes

### Helpers
- ✅ `RewardsHelper.kt` - Funciones utilitarias

### Configuración
- ✅ `AppModule.kt` - Actualizado con nuevas dependencias

---

## ✅ Verificación

- ✅ Modelos de datos creados
- ✅ Repositorios implementados
- ✅ ViewModels creados
- ✅ Helpers creados
- ✅ Inyección de dependencias configurada
- ✅ Sin errores de compilación

---

## 🎯 Resumen

Se ha implementado un sistema completo de recompensas con puntos de cashback del 10%. Los usuarios premium acumulan puntos con cada compra que pueden canjear por descuentos. El sistema es seguro, auditable y escalable.

**Próximo paso**: Crear la UI para mostrar y gestionar los puntos en el Dashboard y Carrito.

---

**Última actualización**: 29 de Mayo de 2026
**Estado**: ✅ IMPLEMENTADO
