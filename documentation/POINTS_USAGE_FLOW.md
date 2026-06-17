# 💎 Flujo de Uso de Puntos al Hacer Checkout

**Versión**: 1.0  
**Fecha**: Junio 2026  
**Status**: ✅ Implementado

---

## 📋 Descripción

Cuando el usuario tiene puntos acumulados y entra al checkout, se le mostrará automáticamente un diálogo preguntando si desea usar sus puntos. Esto mejora la UX y le recuerda al usuario que tiene puntos disponibles.

---

## 🎯 Flujo de Uso

### Paso 1: Usuario abre el carrito
```
Usuario tiene: 250 puntos acumulados
Total compra: $20.00
```

### Paso 2: Usuario hace clic en "Proceder al Pago"
```
CheckoutScreen se abre
RewardsViewModel carga los puntos
```

### Paso 3: Aparece diálogo automático (SI tiene puntos)
```
┌─────────────────────────────────┐
│  💎 Usar Puntos Acumulados      │
├─────────────────────────────────┤
│  Tienes 250 puntos acumulados   │
│  Valor: $2.50                   │
│                                 │
│  Pagarías $17.50 en tarjeta +   │
│  $2.50 de descuento             │
│                                 │
│  ¿Deseas usar tus puntos?       │
├─────────────────────────────────┤
│  [Sí, Usar Puntos]  [No, Tarjeta]
└─────────────────────────────────┘
```

### Opción A: Usuario dice "Sí, Usar Puntos"
```
✅ Pago mixto habilitado
   - Método: Puntos + Tarjeta
   - Se mostrarán detalles del descuento
   - Usuario puede confirmar el pago
```

### Opción B: Usuario dice "No, Usar Tarjeta"
```
✅ Pago con tarjeta
   - Método: Tarjeta
   - Puntos NO se usan
   - Usuario puede confirmar el pago
```

### Paso 4: Confirmación de pago
```
Dependiendo de la opción:
- Con puntos: Se gastan los puntos + se paga con tarjeta (mixto)
- Sin puntos: Se gana cashback (10%) por la compra con tarjeta
```

---

## 💻 Cambios Técnicos

### CheckoutScreen.kt

#### 1. Nuevo estado
```kotlin
var showPointsDialog by remember { mutableStateOf(userPoints > 0) }  // Mostrar si hay puntos
```

#### 2. AlertDialog que pregunta al usuario
```kotlin
if (showPointsDialog && userPoints > 0) {
    AlertDialog(
        title = "💎 Usar Puntos Acumulados",
        text = "Tienes $userPoints puntos = $$discount",
        confirmButton = {
            // Usuario elige usar puntos
            selectedPaymentMethod = "points"
        },
        dismissButton = {
            // Usuario elige NO usar puntos
            selectedPaymentMethod = "card"
        }
    )
}
```

#### 3. Información en el diálogo
- Cantidad de puntos disponibles
- Valor en dinero ($)
- Total a pagar con tarjeta (si es pago mixto)
- O "Compra completa" (si los puntos cubren todo)

---

## 🎨 Ejemplos Visuales

### Ejemplo 1: Usuario con pocos puntos (pago mixto)
```
Compra: $30.00
Puntos: 250 pts ($2.50)

Dialog:
├─ Tienes 250 puntos
├─ Valor: $2.50
├─ Pagarías $27.50 en tarjeta + $2.50 descuento
└─ [Sí, Usar Puntos] [No, Tarjeta]

Si elige "Sí":
├─ Se usan 250 puntos
├─ Se paga $27.50 con tarjeta
├─ Descuento: $2.50
└─ Total pagado: $27.50 tarjeta + $2.50 puntos
```

### Ejemplo 2: Usuario con muchos puntos (compra gratis)
```
Compra: $20.00
Puntos: 2000 pts ($20.00)

Dialog:
├─ Tienes 2000 puntos
├─ Valor: $20.00
├─ ✅ ¡Puedes pagar la compra COMPLETA con puntos!
└─ [Sí, Usar Puntos] [No, Tarjeta]

Si elige "Sí":
├─ Se usan 2000 puntos
├─ Se paga $0.00 con tarjeta
├─ COMPRA GRATIS
└─ Total pagado: 2000 puntos (sin tarjeta)
```

### Ejemplo 3: Usuario sin puntos
```
Puntos: 0 pts

Dialog: ❌ NO aparece (userPoints == 0)

Checkout continúa:
├─ Mostrar métodos de pago normales
├─ Tarjeta seleccionada por defecto
└─ Usuario paga normalmente
```

---

## ✅ Criterios de Aceptación

✅ Si usuario tiene > 0 puntos, aparece diálogo automático  
✅ Diálogo muestra cantidad de puntos en valor $  
✅ Si elige "Sí", se selecciona pago con puntos automáticamente  
✅ Si elige "No", se mantiene tarjeta como método  
✅ Diálogo solo aparece UNA VEZ al abrir checkout  
✅ Usuario puede cambiar de opinión después (seleccionar manualmente)  
✅ Si no hay puntos, NO aparece diálogo  

---

## 🔄 Flujo Alternativo: Cambiar de opinión

Después de responder el diálogo, el usuario PUEDE cambiar de opinión:

```
1. Dialog aparece
2. Usuario elige "No, Usar Tarjeta"
3. Checkout muestra métodos de pago
4. Usuario puede hacer clic en "💎 Pagar con Puntos"
5. CheckoutScreen cambia a puntos
```

O vice versa:
```
1. Dialog aparece
2. Usuario elige "Sí, Usar Puntos"
3. Checkout muestra "Pago mixto"
4. Usuario puede hacer clic en "💳 Pagar con Tarjeta"
5. CheckoutScreen cambia a tarjeta
```

---

## 📊 Comportamiento por Escenario

| Escenario | userPoints | Dialog | Default | Comportamiento |
|-----------|-----------|--------|---------|---|
| Primera compra | 0 | ❌ No | Card | Solo tarjeta |
| Compra normal | 50 | ✅ Sí | Points | Pago mixto o tarjeta |
| Compra con muchos puntos | 2000 | ✅ Sí | Points | Gratis o tarjeta |
| Usuario sin puntos en 2da compra | 0 | ❌ No | Card | Solo tarjeta |

---

## 🚀 Implementación

**Archivo modificado**:
- `CheckoutScreen.kt` - Agregado AlertDialog automático

**Build**: ✅ BUILD SUCCESSFUL

**Funcionalidad**: 
- ✅ Dialog aparece automáticamente
- ✅ Usuario puede elegir usar puntos o tarjeta
- ✅ Opción seleccionada se refleja en checkout
- ✅ Usuario puede cambiar de opinión

---

## 🎯 Próximas Mejoras (Futuro)

- [ ] Animación al aparecerdiálogo
- [ ] Mostrar histórico de puntos gastos
- [ ] Opción de "No mostrar este diálogo de nuevo"
- [ ] Mostrar expiration date de puntos (si aplica)
- [ ] Sugerir usar puntos basado en historial

