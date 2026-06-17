# 💎 Pago con Puntos - Opción de Descuento

**Problema actual**:
- No se puede seleccionar "Pagar con Puntos" porque necesitas 1000+ puntos ($10 = 1000 pts)
- Un usuario típico gana 50-100 puntos por compra
- Es prácticamente imposible usar esta opción

**Solución**: Cambiar a un modelo de **DESCUENTO con PUNTOS**
- El usuario puede gastar sus puntos acumulados como descuento
- 100 puntos = $1 de descuento
- Se muestra cuánto dinero se ahorra con sus puntos

---

## 🎯 REQUISITOS

### Opción 1: Descuento automático (SIMPLE - RECOMENDADO)
```
Compra: $20
Puntos disponibles: 250 pts

Al seleccionar "Pagar con Puntos":
- Descuento: 250 pts = $2.50
- Total final: $20 - $2.50 = $17.50
- Pagar: $17.50 con tarjeta o gastar todos los puntos
```

**Ventajas**:
- ✅ Simple de entender
- ✅ Siempre puedes usar tus puntos
- ✅ Se muestran los ahorros
- ✅ El cliente entiende el valor

### Opción 2: Slider para elegir puntos a gastar (AVANZADO)
```
Compra: $20
Puntos disponibles: 250 pts

[Slider] Puntos a usar: ___250___ (máximo)
         Descuento: $2.50
         Total final: $17.50
```

**Ventajas**:
- ✅ El cliente elige cuántos gastar
- ✅ Puede ahorrar puntos para después
- ✅ Más flexible

---

## 📋 IMPLEMENTACIÓN (Opción 1 - Simple)

### Cambios en CheckoutScreen.kt

**1. Nueva función de cálculo**
```kotlin
private fun calculatePointsDiscount(userPoints: Int): Double {
    // 100 puntos = $1
    return userPoints * 0.01  // userPoints / 100
}
```

**2. Nueva lógica de pago con puntos**
```kotlin
selectedPaymentMethod == "points" -> {
    val discount = calculatePointsDiscount(userPoints)
    val finalTotal = (cartTotal - discount).coerceAtLeast(0.0)  // No puede ser negativo
    
    // Si discount >= cartTotal, es gratis
    if (discount >= cartTotal) {
        onConfirmPayment("points_full", null)  // Pago completamente cubierto por puntos
    } else {
        // Mostrar que faltan X pesos, pero se aplica el descuento
        onConfirmPayment("points_partial", null)  // Descuento + tarjeta
    }
}
```

**3. Actualizar PointsPaymentInfo**
```kotlin
@Composable
private fun PointsPaymentInfo(
    userPoints: Int,
    cartTotal: Double
) {
    val discount = calculatePointsDiscount(userPoints)  // $
    val finalTotal = (cartTotal - discount).coerceAtLeast(0.0)
    
    Column {
        Row {
            Text("Puntos disponibles:")
            Text("$userPoints pts = $${"%.2f".format(discount)}")
        }
        
        Row {
            Text("Descuento:")
            Text("$${"%.2f".format(discount)}", color = Color.Green)
        }
        
        if (finalTotal > 0) {
            Row {
                Text("Debes pagar con tarjeta:")
                Text("$${"%.2f".format(finalTotal)}")
            }
        } else {
            Row {
                Text("✅ ¡Compra cubierta por puntos!")
                Text("Ahorras $${"%.2f".format(cartTotal)}")
            }
        }
    }
}
```

**4. Cambiar condición de habilitación del botón**
```kotlin
enabled = !isLoading && (
    (selectedPaymentMethod == "card" && cardNumber.isNotEmpty()) ||
    (selectedPaymentMethod == "points" && userPoints > 0)  // ✨ CAMBIO: Solo necesita > 0
)
```

---

## 💰 EJEMPLO DE FLUJO

### Escenario 1: Usuario con pocos puntos
```
Compra: $25
Puntos: 150 pts (= $1.50)

Selecciona: "Pagar con Puntos"
App muestra:
  - Tienes 150 puntos
  - Descuento: $1.50
  - Total a pagar con tarjeta: $23.50
  - Botón "Confirmar" HABILITADO ✅

Usuario confirma:
  - Se aplica $1.50 de descuento
  - Paga $23.50 con tarjeta
  - Pierde 150 puntos (aplicados como descuento)
```

### Escenario 2: Usuario con muchos puntos
```
Compra: $25
Puntos: 2500 pts (= $25)

Selecciona: "Pagar con Puntos"
App muestra:
  - Tienes 2500 puntos
  - Descuento: $25.00
  - ✅ ¡La compra es GRATIS con tus puntos!
  - Botón "Confirmar" HABILITADO ✅

Usuario confirma:
  - Se aplican 2500 puntos como pago completo
  - No paga nada con tarjeta
  - Puntos se reducen a 0
```

---

## 🔧 LÓGICA DE BACKEND

### En CheckoutActivity.kt

**Cuando paga con puntos**:
```kotlin
selectedPaymentMethod == "points" -> {
    val discount = userPoints * 0.01  // 100 pts = $1
    val finalTotal = (cartTotal - discount).coerceAtLeast(0.0)
    
    // Transacción de puntos: GASTAR los puntos
    val pointsTransaction = PointsTransactionModel(
        userId = currentUserId,
        orderId = orderId,
        points = -userPoints,  // NEGATIVO = Se gastan
        type = "discount",  // "discount" no "purchase"
        description = "Descuento de $${"%.2f".format(discount)} en compra (Puntos)",
        timestamp = System.currentTimeMillis()
    )
    
    // Guardar en Firebase
    rewardsViewModel.recordPointsTransaction(pointsTransaction)
}
```

---

## ✅ CRITERIOS DE ACEPTACIÓN

1. ✅ Al seleccionar "Pagar con Puntos", el botón se HABILITA si tienes > 0 puntos
2. ✅ Se muestra cuánto dinero ahorras (puntos * 0.01)
3. ✅ Se muestra total final a pagar (cartTotal - descuento)
4. ✅ Si descuento >= cartTotal, muestra "Compra gratis"
5. ✅ Al confirmar, los puntos se restan de la cuenta
6. ✅ Después de la compra, ves cuántos puntos te quedaron

---

## 🎨 DISEÑO DE PANTALLA

```
┌─────────────────────────────────────┐
│  Información de Puntos              │
├─────────────────────────────────────┤
│  💎 Puntos disponibles: 250 pts     │
│     Valor: $2.50                    │
│                                     │
│  💰 Si usas tus puntos:             │
│     Descuento: -$2.50               │
│     Total compra: $20.00            │
│     **Total a pagar: $17.50**       │
│                                     │
│  📌 Pagarás $17.50 con tarjeta     │
│     + $2.50 de descuento con puntos │
│                                     │
│  ✨ Te quedarán 0 puntos            │
│     después de esta compra          │
└─────────────────────────────────────┘
```

O si la compra es gratis:

```
┌─────────────────────────────────────┐
│  Información de Puntos              │
├─────────────────────────────────────┤
│  💎 Puntos disponibles: 2500 pts    │
│     Valor: $25.00                   │
│                                     │
│  🎉 ¡COMPRA GRATIS!                │
│     Usarás todos tus puntos         │
│     Total a pagar: $0.00            │
│     Descuento: $25.00 (COMPLETO)    │
│                                     │
│  ✨ Te quedarán 0 puntos            │
│     después de esta compra          │
└─────────────────────────────────────┘
```

---

## 🚀 SIGUIENTE PASO

¿Implementamos esta lógica de "Pago con Puntos como Descuento"?

Cambios necesarios:
1. ✏️ Actualizar `calculatePointsNeeded()` → `calculatePointsDiscount()`
2. ✏️ Actualizar `PointsPaymentInfo` composable
3. ✏️ Cambiar condición de habilitación del botón
4. ✏️ Actualizar lógica en CheckoutActivity para registrar transacción

**Tiempo estimado**: ~1.5 horas
