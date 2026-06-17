# 📦 ETAPA 5: Order Tracking & Status

**Objetivo**: Mostrar el estado del pedido en tiempo real, desde que entra a cocina hasta que está listo para retirar.

**Problema actual**: 
- Al hacer clic "Ver detalle" se va al carrito (error de flujo)
- No hay pantalla de seguimiento del pedido
- No hay estados de preparación visibles

---

## 🎯 REQUISITOS

### 1. Pantalla OrderTrackingActivity
Debe mostrar:
- **Número de orden** (grande, visible)
- **Resumen de compra** (items, total, método pago)
- **Puntos generados** por la compra
- **Estado del pedido** (con timeline visual)
- **Botón de acción** según estado (ej: "Volver al inicio", "Ir al carrito")

### 2. Estados del Pedido
Timeline visual que muestre:
```
1. ⏳ EN COCINA (En preparación)
   ↓
2. ⏱ PREPARADO (Listo para retirar)
   ↓
3. ✅ ENTREGADO (Retirado por el cliente)
```

### 3. Flujo de navegación
**Actual (ROTO)**:
```
CheckoutScreen → ConfirmationScreen → [Click "Ver detalle"] → CartActivity (❌ WRONG)
```

**Nuevo (CORRECTO)**:
```
CheckoutScreen → ConfirmationScreen → [Click "Ver detalle"] → OrderTrackingActivity (✅ CORRECT)
                                                                  ↓
                                                          [Ver estados del pedido]
```

### 4. Datos necesarios
OrderTrackingActivity necesita recibir:
- `orderId` (String) - Número único del pedido
- `cartItems` (List<OrderItemModel>) - Items comprados
- `cartTotal` (Double) - Total de la compra
- `paymentMethod` (String) - "card" o "points"
- `pointsGenerated` (Int) - Puntos que se generaron
- `orderStatus` (String) - Estado actual ("cooking", "ready", "delivered")
- `orderTimestamp` (Long) - Cuándo se creó la orden

---

## 📊 DISEÑO DE PANTALLA

```
┌─────────────────────────────────────┐
│  ← Volver            📦 Mi Pedido  │  (Header)
├─────────────────────────────────────┤
│                                     │
│     Número de Orden: #12345         │  (Large, centered)
│                                     │
├─────────────────────────────────────┤
│  ESTADO DEL PEDIDO                  │
├─────────────────────────────────────┤
│                                     │
│  ⏳ EN COCINA                       │  (Timeline)
│     Estimado: 10 minutos            │
│                                     │
│  ⏱ PREPARADO                        │  (Next step)
│     Estará listo para retirar        │
│                                     │
│  ✅ ENTREGADO                       │  (Future step)
│     (Cuando retires tu pedido)      │
│                                     │
├─────────────────────────────────────┤
│  RESUMEN DE COMPRA                  │
├─────────────────────────────────────┤
│  Pollo Frito x2         $25.00      │
│  Papas Fritas x1        $5.00       │
│  Gaseosa x2             $4.00       │
│                                     │
│  Total                  $34.00      │
│  Método: Tarjeta                    │
│  Puntos generados: +3 pts           │
│                                     │
├─────────────────────────────────────┤
│     [Volver al Inicio]              │  (Footer)
│     [Ver Mi Historial]              │  (Optional)
└─────────────────────────────────────┘
```

---

## 🔧 CAMBIOS TÉCNICOS

### 1. ConfirmationScreen
**Cambiar**: El botón "Ver detalle" en lugar de ir a CartActivity, debe ir a OrderTrackingActivity

**Current**:
```kotlin
onViewOrderClick = {
    val intent = Intent(this, OrderDetailActivity::class.java)
    startActivity(intent)
}
```

**New**:
```kotlin
onViewOrderClick = {
    val intent = Intent(this, OrderTrackingActivity::class.java).apply {
        putExtra("orderId", orderId)
        putExtra("cartItems", ArrayList(cartItems))
        putExtra("cartTotal", cartTotal)
        putExtra("paymentMethod", paymentMethod)
        putExtra("pointsGenerated", pointsChange)
        putExtra("orderStatus", "cooking")  // Estado inicial
        putExtra("orderTimestamp", System.currentTimeMillis())
    }
    startActivity(intent)
}
```

### 2. Crear OrderTrackingActivity
**Ubicación**: `app/src/main/java/.../presentation/activity/checkout/OrderTrackingActivity.kt`

**Componentes Composable necesarios**:
- `OrderTrackingScreen()` - Pantalla principal
- `OrderStatusTimeline()` - Timeline visual de estados
- `OrderSummaryCard()` - Resumen de la compra

### 3. Agregar estado a OrderModel (futuro)
Para manejar estados real-time desde Firebase:
```kotlin
// En OrderModel.kt
@SerializedName("status")
val status: String = "cooking"  // "cooking", "ready", "delivered"

@SerializedName("estimatedTime")
val estimatedTime: Int = 15  // minutos
```

---

## 📋 TAREAS

### Fase 1: Crear pantalla básica (MVP)
- [ ] Crear `OrderTrackingActivity.kt`
- [ ] Crear composables para layout
- [ ] Pasar datos desde ConfirmationScreen
- [ ] Mostrar número de orden y resumen
- [ ] Timeline visual estático (siempre muestra "En cocina")

### Fase 2: Estados dinámicos (futuro)
- [ ] Guardar status en Firebase al crear orden
- [ ] Listener en tiempo real para cambios de estado
- [ ] Simular cambios de estado (para testing)
- [ ] Notificaciones cuando cambia estado

### Fase 3: Historial de pedidos (futuro)
- [ ] Nueva Activity: `OrderHistoryActivity`
- [ ] Ver todos los pedidos del usuario
- [ ] Hacer clic para ver detalle de cada pedido

---

## 🎨 ESTADO INICIAL

Para el MVP, mostrar siempre:
- ✅ Estado actual: "EN COCINA"
- ✅ Próximo estado: "PREPARADO" (gris, futuro)
- ✅ Último estado: "ENTREGADO" (gris, futuro)
- ✅ Estimado: ~15 minutos

**Para después**: Cambiar dinámicamente según Firebase

---

## ✅ CRITERIOS DE ACEPTACIÓN

1. ✅ Al hacer clic "Ver detalle" en ConfirmationScreen, voy a OrderTrackingActivity (NO CartActivity)
2. ✅ Veo el número de orden grande y claro
3. ✅ Veo el resumen de la compra (items, total, puntos)
4. ✅ Veo timeline visual de estados
5. ✅ Puedo volver al inicio desde esta pantalla
6. ✅ No hay ciclo infinito de pago

---

## 🚀 SIGUIENTE PASO

¿Procedemos con crear OrderTrackingActivity?
