# 💰 Sistema de Recompensas y Puntos

## Resumen

El sistema de recompensas permite que los usuarios Premium acumulen puntos en cada compra y canjearlos por descuentos. El sistema incluye:

- ✅ Cashback del 10% (+ bonus según nivel)
- ✅ 5 niveles de usuario (Regular, Bronce, Plata, Oro, Platino)
- ✅ Puntos que se convierten en dinero (1 punto = $0.01)
- ✅ Historial de transacciones
- ✅ Multi-dispositivo

## 📊 Estructura del Sistema

### Cálculo de Puntos

**Fórmula básica:**
```
Puntos Ganados = Total de Compra × 10%

Ejemplo: Compra $100 → Gana 10 puntos
```

**Con bonus por nivel:**
```
Puntos Finales = Puntos Base × Multiplicador del Nivel

Ejemplo: Usuario Platino con $100 = 10 × 1.5 = 15 puntos
```

### Niveles y Multiplicadores

| Nivel | Rango | Multiplicador | Requisito |
|-------|-------|---------------|-----------|
| Regular | 0 - 99 | 1.0x (10%) | Nuevo usuario |
| Bronce 🥉 | 100 - 499 | 1.05x (10.5%) | 100 puntos |
| Plata 🥈 | 500 - 999 | 1.10x (11%) | 500 puntos |
| Oro 🏆 | 1000 - 4999 | 1.20x (12%) | 1000 puntos |
| Platino 👑 | 5000+ | 1.50x (15%) | 5000 puntos |

### Ejemplos de Ganancias por Nivel

```
Compra de $100:

Regular:     $100 × 10% × 1.0 = 10 puntos
Bronce:      $100 × 10% × 1.05 = 10.5 ≈ 10 puntos
Plata:       $100 × 10% × 1.10 = 11 puntos
Oro:         $100 × 10% × 1.20 = 12 puntos
Platino:     $100 × 10% × 1.50 = 15 puntos
```

## 🎯 Tipos de Puntos

### 1. Puntos Balance (Puntos Disponibles)

Son los puntos que el usuario **puede usar AHORA**.

```kotlin
val puntosBalance = userRewards.pointsBalance

// Ejemplo: 500 puntos disponibles para usar
```

### 2. Puntos Totales (Histórico)

Son **TODOS** los puntos que ha ganado en la historia del usuario.

```kotlin
val puntosTotales = userRewards.totalPoints

// Ejemplo: 1500 puntos acumulados en total
```

### 3. Puntos Gastados (Usados)

Son los puntos que ya **ha canjeado** por descuentos.

```kotlin
val puntosGastados = userRewards.pointsSpent

// Ejemplo: 1000 puntos ya usados

// Verificación: puntosTotales - puntosGastados = puntosBalance
// 1500 - 1000 = 500 ✅
```

## 💳 Canje de Puntos

### Conversión

```
1 punto = $0.01 (1 centavo)
10 puntos = $0.10
100 puntos = $1.00
500 puntos = $5.00
1000 puntos = $10.00
```

### Ejemplo de Canje

```
Carrito:
├─ Hamburguesa × 2  = $25.98
├─ Pizza × 1        = $12.99
└─ Bebida × 3       = $9.99
├─────────────────────────────
Subtotal:            $48.96

Usuario Premium decide usar 200 puntos:
Descuento = 200 × $0.01 = $2.00

Subtotal:    $48.96
Descuento:   -$2.00
─────────────────────
TOTAL:       $46.96

Puntos ganados en esta compra:
$46.96 × 10% = 4.696 ≈ 5 puntos

Nuevo balance = 200 - 200 + 5 = 5 puntos
```

### Validaciones

```kotlin
fun validarCanje(puntosAUsar: Int, puntosDisponibles: Int, subtotal: Double): Boolean {
    
    // 1. No puede usar más de lo que tiene
    if (puntosAUsar > puntosDisponibles) return false
    
    // 2. No puede usar puntos negativos
    if (puntosAUsar < 0) return false
    
    // 3. El descuento no puede superar el subtotal
    val descuento = puntosAUsar * 0.01
    if (descuento > subtotal) return false
    
    return true
}
```

## 📈 Ganancia de Puntos

### Al Comprar

```kotlin
fun calcularPuntosGanados(
    totalFinal: Double,
    nivelUsuario: String
): Int {
    // 1. Calcular 10% del total
    val basePuntos = (totalFinal * 0.10).toInt()
    
    // 2. Aplicar multiplicador del nivel
    val multiplicador = when (nivelUsuario) {
        "regular"  -> 1.0
        "bronce"   -> 1.05
        "plata"    -> 1.10
        "oro"      -> 1.20
        "platino"  -> 1.50
        else       -> 1.0
    }
    
    // 3. Retornar puntos finales
    return (basePuntos * multiplicador).toInt()
}

// Ejemplo:
// Usuario Oro compra $100 = (100 × 0.10) × 1.20 = 12 puntos
```

### Transacciones Guardadas

```
pointsTransactions/
├── {transactionId1}/
│   ├── userId: "abc123"
│   ├── type: "purchase"
│   ├── points: +12
│   ├── balanceAfter: 512
│   ├── orderId: "order_1"
│   └── timestamp: 1717000000000
│
└── {transactionId2}/
    ├── userId: "abc123"
    ├── type: "discount"
    ├── points: -200
    ├── balanceAfter: 312
    ├── orderId: "order_2"
    └── timestamp: 1717000000100
```

## 🏅 Promoción de Nivel

El nivel se actualiza automáticamente basado en `totalPoints`:

```kotlin
fun determinarNivel(totalPoints: Int): String {
    return when {
        totalPoints < 100      → "regular"
        totalPoints < 500      → "bronce"
        totalPoints < 1000     → "plata"
        totalPoints < 5000     → "oro"
        else                   → "platino"
    }
}
```

**Cambios automáticos:**

```
Usuario con 95 puntos: Regular
    ↓ Compra $100 (gana 10 puntos)
    ↓ Ahora tiene 105 puntos
    ↓ Sistema automáticamente lo promociona a Bronce
    ↓ Próximas compras ganarán 10.5% en lugar de 10%
```

## 📊 Datos Guardados en Firebase

### Estructura de UserRewards

```
users/{userId}/rewards/
├── totalPoints: 1500
├── pointsBalance: 500
├── pointsSpent: 1000
├── level: "oro"
├── isPremium: true
├── lastPurchaseDate: 1717000000000
└── joinDate: 1715000000000
```

### Estructura de Order (con puntos)

```
orders/{orderId}/
├── orderId: "uuid"
├── userId: "abc123"
├── items: [ ... ]
├── totalPrice: 48.96
├── pointsUsed: 200
├── discountAmount: 2.00
├── finalPrice: 46.96
├── pointsEarned: 5
├── pointsLevel: "oro"
├── status: "completed"
└── timestamp: 1717000000000
```

### Estructura de PointsTransaction

```
pointsTransactions/{transactionId}/
├── userId: "abc123"
├── type: "purchase" | "discount" | "bonus" | "refund"
├── points: 5
├── orderId: "order_123"
├── balanceAfter: 505
├── timestamp: 1717000000000
└── description: "Compra completada"
```

## 🔧 ViewModels y Métodos

### RewardsViewModel

```kotlin
// Obtener recompensas del usuario
fun getUserRewards(userId: String)

// Agregar puntos (después de compra)
fun agregarPuntos(userId: String, puntos: Int)

// Restar puntos (al canjear)
fun restarPuntos(userId: String, puntos: Int)

// Obtener nivel actual
fun determinarNivel(userId: String)

// Obtener historial de transacciones
fun getTransactionHistory(userId: String)

// Limpiar estados
fun clearStates()
```

### OrderViewModel

```kotlin
// Crear orden con puntos
fun crearOrden(
    userId: String,
    items: List<CartItem>,
    puntosUsados: Int
)

// Obtener orden por ID
fun getOrderById(orderId: String)

// Obtener historial de órdenes
fun getOrdersByUserId(userId: String)

// Filtrar órdenes por fecha
fun filterOrdersByDate(userId: String, start: Long, end: Long)
```

## 📈 Flujo Completo: Compra y Puntos

```
1. Usuario Premium ve Dashboard
   └─ Saldo: 200 puntos
   └─ Nivel: Bronce
   
2. Selecciona productos (Total: $50)
   
3. En CartActivity:
   ├─ Ve opción "Usar Puntos"
   ├─ Decide usar 100 puntos
   ├─ Descuento calculado: $1.00
   └─ Total final: $49.00
   
4. Hace clic "Proceder al Pago"
   
5. Sistema procesa:
   ├─ Valida: 100 puntos ≤ 200 disponibles ✅
   ├─ Valida: $1.00 ≤ $50 ✅
   ├─ Guarda orden
   ├─ Calcula puntos ganados: $49 × 10% × 1.05 = 5 puntos
   └─ Actualiza balance: 200 - 100 + 5 = 105 puntos
   
6. Guarda transacciones en Firebase:
   ├─ Transaction 1: -100 puntos (descuento)
   └─ Transaction 2: +5 puntos (compra)
   
7. Muestra confirmación:
   ├─ Orden #12345 completada
   ├─ Puntos usados: 100
   ├─ Puntos ganados: 5
   └─ Nuevo balance: 105 puntos
```

## 🎁 Casos Especiales

### Compra sin usar puntos

```
Compra: $50
Puntos ganados: $50 × 10% = 5 puntos
Balance anterior: 200
Balance nuevo: 200 + 5 = 205 puntos
```

### Usar más puntos que el total (No permitido)

```
Total: $50
Puntos disponibles: 200
Usuario intenta usar: 600 puntos

Sistema valida: 600 > 500 ($5.00)
Resultado: ❌ Error "Descuento mayor al total"
```

### Usuario Regular vs Platino

```
Compra: $100

Usuario Regular (0-99 puntos):
└─ Gana: 10 puntos

Usuario Platino (5000+ puntos):
└─ Gana: 15 puntos (50% más)
```

## 📱 Pantallas Relacionadas

1. **TopBar** - Mostrar saldo de puntos
2. **PointsCard** - Mostrar nivel y progreso
3. **CartActivity** - Opción para usar puntos
4. **OrderConfirmationActivity** - Mostrar puntos ganados
5. **OrderHistoryActivity** - Historial de órdenes
6. **TransactionHistoryActivity** - Historial de transacciones
7. **ProfileActivity** - Ver perfil con puntos y nivel

## ✅ Características Implementadas

✅ Modelo de datos (UserRewardsModel)
✅ Repository (RewardsRepository)
✅ ViewModel (RewardsViewModel)
✅ Cálculo de puntos (RewardsHelper)
✅ 5 niveles con multiplicadores
✅ Transacciones de auditoría
✅ Multi-dispositivo
✅ Conversión puntos → dinero

## ⏳ Características Pendientes

⏳ UI para mostrar puntos
⏳ UI para usar puntos en carrito
⏳ UI para confirmación de orden
⏳ UI para historial de compras
⏳ UI para historial de transacciones
⏳ Renovación automática de tokens
⏳ Validación de tokens en SplashActivity

## 📝 Componentes Relacionados

- [Guía del Usuario Premium](./03_USUARIO_PREMIUM.md)
- [Flujo de Autenticación](./01_AUTENTICACION.md)
- [Arquitectura del Proyecto](./ARQUITECTURA.md)

---

**Estado:** Sistema implementado, UI pendiente
**Última actualización:** 2026-06-01

