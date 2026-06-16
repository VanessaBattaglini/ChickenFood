# 📋 SPEC: Etapa 4 - Ver Detalle + Canjear Puntos

**Fecha**: 16 de Junio, 2026  
**Versión**: 3.4  
**Duración Estimada**: 4-4.5 horas  
**Estado**: 📋 SPEC EN CONSTRUCCIÓN

---

## 🎯 Objetivo General

Implementar dos features complementarias:
1. **Ver Detalle de Compra** - Completar botón vacío en ConfirmationScreen
2. **Canjear Puntos por Descuento** - Nueva opción en Checkout para usar puntos y ahorrar

---

## 📌 FEATURE 1: Ver Detalle de Compra

### Descripción
Cuando usuario presiona "Ver Detalle de Compra" en ConfirmationScreen, navega a una pantalla que muestra todos los detalles de la orden que acaba de completar.

### Pantalla: Order Detail

#### Layout
```
┌─────────────────────────────────┐
│ ← Volver                   🔖    │
├─────────────────────────────────┤
│ Orden #ORD_12345                │
│ 📅 16 Junio, 2026 14:30         │
│ ✅ Completada                   │
├─────────────────────────────────┤
│ ITEMS ORDENADOS                 │
│ ┌───────────────────────────┐   │
│ │ 🍔 Hamburgesa Doble x2    │   │
│ │ $10.00 cada ($20.00)      │   │
│ └───────────────────────────┘   │
│ ┌───────────────────────────┐   │
│ │ 🍟 Papas Fritas x1        │   │
│ │ $5.00                     │   │
│ └───────────────────────────┘   │
├─────────────────────────────────┤
│ RESUMEN                         │
│ Subtotal:          $25.00       │
│ Envío:             $0.00        │
│ Descuento:         -$0.00       │
├─────────────────────────────────┤
│ TOTAL:             $25.00       │
├─────────────────────────────────┤
│ MÉTODO DE PAGO                  │
│ 💳 Tarjeta (últimos 4: 9010)    │
│ O                               │
│ 💎 Puntos (500 gastados)        │
├─────────────────────────────────┤
│ PUNTOS                          │
│ Puntos ganados: +2              │
│ (10% de $20)                    │
│ Saldo anterior: 0               │
│ Saldo actual: 2                 │
├─────────────────────────────────┤
│ [📲 Compartir]  [🖨️ Imprimir]    │
└─────────────────────────────────┘
```

#### Información a Mostrar
- ✅ ID de Orden
- ✅ Fecha y Hora exacta
- ✅ Estado (Completada, Pendiente)
- ✅ Items comprados (con imagen, cantidad, precio)
- ✅ Subtotal, Envío, Total
- ✅ Método de pago usado
- ✅ Puntos ganados o gastados
- ✅ Saldo antes y después

#### Funcionalidades
- [ ] Botón Volver (regresa a MainActivity)
- [ ] Botón Compartir (compartir detalles vía WhatsApp/Email)
- [ ] Botón Imprimir (generar PDF o captura)
- [ ] Scroll si hay muchos items

### Archivos a Crear
```
NEW:
- OrderDetailActivity.kt
- OrderDetailScreen.kt

MODIFY:
- CheckoutActivity.kt (completar onViewOrderClick)
```

### Implementación
```kotlin
// CheckoutActivity.kt - MODIFICAR
onViewOrderClick = {
    Log.d(TAG, "View order clicked")
    // ✨ NUEVO: Navegar a detalle
    val intent = Intent(this@CheckoutActivity, OrderDetailActivity::class.java)
    intent.putExtra("orderId", orderId)
    intent.putExtra("cartItems", ArrayList(cartItems))
    intent.putExtra("cartTotal", cartTotal)
    intent.putExtra("paymentMethod", paymentMethod)
    intent.putExtra("pointsBefore", userPoints)
    intent.putExtra("pointsChange", pointsChange)
    startActivity(intent)
}

// OrderDetailActivity.kt - CREAR
class OrderDetailActivity : BaseActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        val orderId = intent.getStringExtra("orderId") ?: ""
        val cartItems = intent.getParcelableArrayListExtra<OrderItemModel>() ?: mutableListOf()
        val cartTotal = intent.getDoubleExtra("cartTotal", 0.0)
        // ... recibir resto de parámetros
        
        setContent {
            OrderDetailScreen(
                orderId = orderId,
                cartItems = cartItems,
                // ... pasar parámetros
            )
        }
    }
}
```

---

## 📌 FEATURE 2: Canjear Puntos por Descuento

### Descripción
En CheckoutScreen, agregar nueva opción para que el usuario use sus puntos acumulados para obtener descuento en la compra actual.

### Cambios en CheckoutScreen

#### Layout Nuevo
```
┌─────────────────────────────────┐
│ 💳 Pagar con Tarjeta   [ACTIVO] │
│ 💎 Pagar con Puntos    [ACTIVO] │
│ 🎁 Usar Puntos Descuent[ACTIVO]✨│ ← NUEVO
├─────────────────────────────────┤
│ Si selecciona "Usar Puntos Desc"│
│                                 │
│ Total sin descuento: $25.00     │
│                                 │
│ Puntos disponibles: 500         │
│ Máximo descuento: $5.00         │
│                                 │
│ ┌─ Desliza para descuento ─┐   │
│ │ 0% ◆════════════════ 100%│   │
│ │ $0 descuento - $25 total│   │
│ └─────────────────────────┘   │
│                                 │
│ O ingresa puntos:               │
│ ┌──────────────────┐            │
│ │ [        250 pts]│ = $2.50   │
│ └──────────────────┘            │
│                                 │
│ TOTAL CON DESCUENTO: $22.50    │
│ Puntos a usar: 250             │
│ Puntos sobrantes: 250          │
│                                 │
│ [Confirmar con Descuento]      │
└─────────────────────────────────┘
```

#### Lógica de Conversión
```
100 puntos = $1 descuento
250 puntos = $2.50 descuento
500 puntos = $5.00 descuento (máximo disponible)

Validación:
- No puede usar más puntos de lo que tiene
- No puede generar descuento mayor al total
- Si total = $25, máximo descuento = $25
```

#### Estados Posibles
```
1. Sin seleccionar "Usar Puntos Descuento"
   - Métodos: Tarjeta o Puntos
   
2. Selecciona "Usar Puntos Descuento"
   - Slider aparece
   - Muestra: "0% - $0 descuento"
   - User puede deslizar

3. User desliza slider
   - Total se actualiza en tiempo real
   - "Total con descuento: $25 - $2.50 = $22.50"
   - Puntos a usar: 250

4. Click "Confirmar"
   - Valida puntos disponibles
   - Procesa pago normal (no mezcla con "Pagar Puntos")
   - Registra: orden + descuento + puntos gastados
```

### Archivos a Modificar
```
MODIFY:
- CheckoutScreen.kt (agregar opción descuento)
- CheckoutActivity.kt (calcular y aplicar descuento)
- RewardsRepositoryImpl.kt (registrar gasto de puntos)
- RewardsViewModel.kt (nuevo método para descuento)
```

### Implementación Pseudocódigo

```kotlin
// CheckoutScreen.kt - MODIFICAR
var selectedPaymentMethod by remember { mutableStateOf("card") }
var usePointsForDiscount by remember { mutableStateOf(false) }
var discountPercentage by remember { mutableStateOf(0f) }

// Calcular descuento
val discountAmount = if (usePointsForDiscount) {
    (cartTotal * discountPercentage).toDouble()
} else {
    0.0
}
val pointsToUse = (discountAmount * 100).toInt()
val totalWithDiscount = cartTotal - discountAmount

// Mostrar opciones
if (usePointsForDiscount) {
    Slider(
        value = discountPercentage,
        onValueChange = { discountPercentage = it },
        modifier = Modifier.fillMaxWidth(),
        valueRange = 0f..minOf(100f, userPointsPercentage)
    )
    Text("Total: $$cartTotal - $$discountAmount = $$totalWithDiscount")
    Text("Puntos a usar: $pointsToUse")
}

// Botón confirmar
Button(
    onClick = {
        if (usePointsForDiscount) {
            // Procesar pago normal CON descuento
            val finalTotal = totalWithDiscount
            onConfirmPayment("card", cardData, discountPoints = pointsToUse)
        } else {
            // Procesar pago normal SIN descuento
            onConfirmPayment("card", cardData, discountPoints = 0)
        }
    }
)

// CheckoutActivity.kt - MODIFICAR
onConfirmPayment = { method, cardData, discountPoints ->
    // Si tiene descuento por puntos
    if (discountPoints > 0) {
        // 1. Registrar que se usaron X puntos para descuento
        val discountTransaction = PointsTransactionModel(
            userId = currentUserId,
            orderId = orderId,
            points = -discountPoints,
            type = "discount",
            description = "Descuento en compra de $$finalTotal"
        )
        rewardsViewModel.recordPointsTransaction(discountTransaction)
        
        // 2. Guardar orden normalmente
        val order = OrderModel(...)
        orderViewModel.createOrder(order)
    }
}
```

---

## 🔄 Flujo Completo

### Flujo: Ver Detalle de Compra
```
ConfirmationScreen
  ↓ Usuario click "Ver Detalle"
  ↓
CheckoutActivity.onViewOrderClick()
  ↓
Intent → OrderDetailActivity
  ↓
OrderDetailScreen renderiza:
  ├─ Información de orden
  ├─ Items comprados
  ├─ Totales
  ├─ Puntos ganados
  └─ Botones (Volver, Compartir, etc)
```

### Flujo: Canjear Puntos por Descuento
```
CheckoutScreen
  ↓ Usuario selecciona "Usar Puntos Descuento"
  ↓ Slider aparece
  ↓ Usuario desliza → total se actualiza en tiempo real
  ↓ Click "Confirmar"
  ↓
CheckoutActivity calcula:
  ├─ Descuento a aplicar
  ├─ Puntos a usar
  └─ Total final
  ↓
registra puntos gastados en Firebase
  ↓
ConfirmationScreen muestra:
  ├─ Total pagado: $X
  ├─ Descuento usado: $Y (ZZ puntos)
  ├─ Puntos antes: 500
  ├─ Puntos gastados: -ZZ
  └─ Puntos después: 500-ZZ
```

---

## ✅ Checklist de Implementación

### Feature 1: Ver Detalle
- [ ] Crear OrderDetailActivity.kt
- [ ] Crear OrderDetailScreen.kt
- [ ] Recibir parámetros vía Intent
- [ ] Mostrar layout con información
- [ ] Botón Volver funciona
- [ ] Botón Compartir (opcional)
- [ ] Botón Imprimir (opcional)
- [ ] Modificar CheckoutActivity.onViewOrderClick()
- [ ] Testear flujo completo

### Feature 2: Canjear Puntos
- [ ] Modificar CheckoutScreen (agregar opción)
- [ ] Implementar Slider para descuento
- [ ] Validación de puntos disponibles
- [ ] Cálculo en tiempo real
- [ ] Modificar CheckoutActivity (calcular descuento)
- [ ] Registrar puntos gastados en Firebase
- [ ] Mostrar en ConfirmationScreen
- [ ] Testear múltiples escenarios

### General
- [ ] Build SUCCESS
- [ ] Sin errores de compilación
- [ ] Documentación actualizada
- [ ] README.md actualizado

---

## 📊 Detalles Técnicos

### Métodos a Crear/Modificar

**RewardsViewModel.kt**
```kotlin
// NUEVO MÉTODO
fun applyPointsDiscount(userId: String, discount: Double, orderId: String) {
    val pointsToUse = (discount * 100).toInt()
    rewardsRepository.deductPoints(userId, pointsToUse, "Descuento en compra")
}
```

**RewardsRepositoryImpl.kt**
```kotlin
// YA EXISTE, VERIFICAR
override fun deductPoints(userId: String, points: Int, reason: String): Flow<Int>
```

**CheckoutScreen.kt**
```kotlin
// AGREGAR NUEVO COMPOSABLE
fun PointsDiscountSection(
    userPoints: Int,
    cartTotal: Double,
    onDiscountChange: (discount: Double, pointsToUse: Int) -> Unit
)
```

---

## 🧪 Casos de Prueba

### Test 1: Ver Detalle
```
1. Completar compra
2. ConfirmationScreen aparece
3. Click "Ver Detalle"
4. OrderDetailActivity abre ✅
5. Muestra todos los datos ✅
6. Click "Volver" → regresa a MainActivity ✅
```

### Test 2: Canjear Puntos
```
1. Tener 500 puntos ✅
2. En Checkout, seleccionar "Usar Puntos Descuento" ✅
3. Slider aparece ✅
4. Deslizar al 50% → Total baja a $12.50 ✅
5. Muestra: "Puntos a usar: 250" ✅
6. Click Confirmar ✅
7. ConfirmationScreen:
   - Descuento: $12.50 ✅
   - Puntos gastados: -250 ✅
   - Saldo: 500 - 250 = 250 ✅
8. Dashboard muestra 250 puntos ✅
```

### Test 3: Validación
```
1. Puntos disponibles: 100 ($1 max) ✅
2. Total: $25 ✅
3. Intenta usar $10 descuento → ERROR: "Solo $1 disponible" ✅
4. Usa $1 descuento → Total: $24 ✅
```

---

## 📅 Timeline

| Tarea | Tiempo | Status |
|-------|--------|--------|
| Spec (este doc) | 30min | ✅ |
| Feature 1: Ver Detalle | 1-1.5h | ⏳ TODO |
| Feature 2: Canjear Puntos | 2-2.5h | ⏳ TODO |
| Testing + Fixes | 0.5-1h | ⏳ TODO |
| Documentación | 0.5h | ⏳ TODO |
| **TOTAL** | **4-5h** | ⏳ EN PROGRESO |

---

## 📝 Documentación a Generar

1. `20_FEATURE_VER_DETALLE_COMPRA.md` - Implementación Feature 1
2. `21_FEATURE_CANJEAR_PUNTOS_DESCUENTO.md` - Implementación Feature 2
3. Actualizar `README.md` con las nuevas features

---

**Estado**: 📋 SPEC LISTA PARA IMPLEMENTACIÓN

¿Proceder con implementación? ✅

