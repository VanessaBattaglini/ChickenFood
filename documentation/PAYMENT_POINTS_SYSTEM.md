# 💎 Sistema de Pago con Puntos (Descuento Mixto)

**Versión**: 1.0  
**Fecha**: Junio 2026  
**Estado**: ✅ Implementado

---

## 📋 Descripción

El usuario ahora puede usar sus puntos acumulados para obtener descuento en sus compras. El sistema funciona de dos formas:

### 1️⃣ Pago completamente con puntos (Si hay suficientes)
```
Compra: $30.00
Puntos disponibles: 3000 pts ($30.00)

Resultado: ✅ Compra GRATIS
- Se usan 3000 puntos
- Se paga: $0.00
- Puntos restantes: 0 pts
```

### 2️⃣ Pago mixto (Puntos + Tarjeta)
```
Compra: $30.00
Puntos disponibles: 1500 pts ($15.00)

Resultado: Pago mixto
- Descuento con puntos: -$15.00
- Total a pagar: $15.00 (con tarjeta)
- Se usan 1500 puntos
- Puntos restantes: 0 pts
```

---

## 🔄 Cómo funciona

### Conversión de puntos
```
100 puntos = $1.00
```

### Flujo de pago

**Paso 1: Seleccionar método de pago**
- Usuario hace clic en "💎 Pagar con Puntos"
- Se muestra información del descuento

**Paso 2: Ver información de descuento**
- Puntos disponibles
- Descuento en dinero
- Total a pagar con tarjeta (si es necesario)
- O "Compra gratis" (si los puntos cubren todo)

**Paso 3: Confirmar pago**
- Si es gratis: Se gastan todos los puntos
- Si es mixto: Se gastan todos los puntos + se paga con tarjeta

**Paso 4: Confirmación**
- Se muestra número de orden
- Se muestra "Puntos gastados en descuento"
- Se muestra puntos restantes (siempre 0 después de pagar con puntos)

---

## 💻 Cambios Técnicos

### CheckoutScreen.kt

#### 1. Nueva función de cálculo
```kotlin
private fun calculatePointsDiscount(userPoints: Int): Double {
    // 100 puntos = $1.00
    return userPoints * 0.01  // userPoints / 100
}
```

#### 2. Actualización del composable PointsPaymentInfo
```kotlin
@Composable
private fun PointsPaymentInfo(
    userPoints: Int,
    cartTotal: Double
) {
    val discount = calculatePointsDiscount(userPoints)
    val finalTotal = (cartTotal - discount).coerceAtLeast(0.0)
    val isFullyCovered = discount >= cartTotal
    
    // Muestra:
    // - Puntos disponibles
    // - Descuento en dinero
    // - Total a pagar con tarjeta (si aplica)
    // - "Compra gratis" (si aplica)
}
```

#### 3. Condición de habilitación del botón
```kotlin
enabled = !isLoading && (
    (selectedPaymentMethod == "card" && cardNumber.isNotEmpty()) ||
    (selectedPaymentMethod == "points" && userPoints > 0)  // ✨ Solo necesita > 0
)
```

### CheckoutActivity.kt

#### Lógica de pago con puntos
```kotlin
if (method == "points") {
    val pointsUsedAsDiscount = userPoints  // Usar todos disponibles
    val discount = userPoints * 0.01      // Calcular descuento
    val finalTotal = (cartTotal - discount).coerceAtLeast(0.0)
    
    pointsChange = -pointsUsedAsDiscount  // Se restan
    pointsAfter = 0                       // Quedan 0
}
```

#### Transacción guardada en Firebase
```kotlin
PointsTransactionModel(
    userId = currentUserId,
    orderId = orderId,
    points = -userPoints,  // NEGATIVO = Se gastan
    type = "discount",     // Tipo: descuento
    description = "Descuento con puntos en compra de $X",
    timestamp = System.currentTimeMillis()
)
```

---

## 📊 Ejemplo de uso

### Usuario con 250 puntos compra $20

```
1️⃣ Ve opciones de pago
   - Tarjeta de crédito
   - Pagar con Puntos ← Selecciona esta

2️⃣ Ve información de descuento
   ┌─────────────────────────┐
   │ Puntos: 250 pts = $2.50 │
   │ Descuento: -$2.50       │
   │ Total original: $20.00  │
   │ Aún debes pagar: $17.50 │
   │ 💳 Pagarás $17.50 con   │
   │    tarjeta              │
   └─────────────────────────┘

3️⃣ Confirma pago
   - Se gastan 250 puntos
   - Se descuenta $2.50
   - Se paga $17.50 con tarjeta

4️⃣ Ver confirmación
   ✅ Compra exitosa
   - Orden: #12345
   - Total: $20.00
   - Descuento con puntos: -$2.50
   - Pagado con tarjeta: $17.50
   - Puntos gastados: -250 pts
   - Puntos restantes: 0 pts
```

---

## 🎯 Criterios de Aceptación

✅ El usuario puede seleccionar "Pagar con Puntos" si tiene > 0 puntos  
✅ Se muestra cuánto dinero ahorra (puntos * 0.01)  
✅ Se muestra total a pagar con tarjeta (si es necesario)  
✅ Si puntos cubren todo, muestra "Compra gratis"  
✅ Al confirmar, todos los puntos se gastan como descuento  
✅ Después de compra, puntos restantes = 0  
✅ Se guarda en Firebase con type="discount"  
✅ Se muestra en confirmación cuántos puntos se gastaron  

---

## 🔮 Futuro (v2.0)

- [ ] Slider para elegir cuántos puntos gastar (no todos)
- [ ] Redondeo inteligente de puntos
- [ ] Límite de puntos a gastar por compra
- [ ] Combinar puntos con cupones de descuento
- [ ] Mostrar proyección de puntos restantes

---

## 📝 Notas

- Los puntos se gastan COMPLETAMENTE cuando se elige esta opción
- No hay forma de gastar puntos parcialmente (v1.0)
- Los puntos no se pueden usar para ganar más puntos (no hay cashback)
- Después de pagar con puntos, siempre queda 0 puntos

