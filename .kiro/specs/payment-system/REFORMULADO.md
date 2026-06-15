# 💳 Sistema de Pagos - REFORMULADO (Versión Simplificada)

**Objetivo**: Sistema de pagos para PRUEBAS con 2 métodos: Tarjeta (Stripe) + Puntos

**Complejidad**: Baja (para testing, no producción)

---

## 🎯 Visión General

### Lo que el Usuario VE (Una sola pantalla)

```
┌─────────────────────────────────┐
│   CHECKOUT SCREEN               │
├─────────────────────────────────┤
│                                 │
│ RESUMEN DE COMPRA              │
│ ────────────────────────────   │
│ Pollo Frito x2        $15.98   │
│ Papas x1              $3.99    │
│ ────────────────────────────   │
│ TOTAL:                $19.97   │
│                                 │
├─────────────────────────────────┤
│                                 │
│ MIS PUNTOS ANTES DEL PAGO:     │
│ ┌─────────────────────────────┐│
│ │ Saldo: 500 puntos           ││
│ │ (Equivale a: $5.00)         ││
│ └─────────────────────────────┘│
│                                 │
├─────────────────────────────────┤
│                                 │
│ SELECCIONA MÉTODO DE PAGO:     │
│                                 │
│ ◉ Pagar con Tarjeta            │
│   └─ Pagar: $19.97             │
│                                 │
│ ○ Pagar con Puntos             │
│   └─ Necesitas: 1997 puntos    │
│      (Tienes: 500 - TE FALTAN) │
│                                 │
├─────────────────────────────────┤
│ [Cancelar]       [Pagar]        │
└─────────────────────────────────┘
```

---

## 📋 FLUJO PASO A PASO

### PASO 1: Usuario Abre CartActivity

```
Usuario en CartActivity (carrito actual)
    ├─ Ve items: Pollo Frito x2, Papas x1
    ├─ Ve Total: $19.97
    ├─ Botón "Proceder al Pago"
    └─ Click en botón
```

**Acciones internas**:
- App obtiene carrito del helper (ManagementCart)
- App obtiene saldo de puntos de Firebase (RewardsViewModel)
- App navega a CheckoutScreen

---

### PASO 2: CheckoutScreen Se Abre

```
CheckoutScreen renderiza:

1. Header
   └─ Título: "Confirmar Compra"

2. ResumenCarrito Card
   ├─ Lista items del carrito
   ├─ Subtotal por item
   └─ Total: $19.97

3. Información de Puntos
   ├─ Saldo ACTUAL: 500 puntos
   ├─ Equivalencia: $5.00
   └─ (Se muestra ANTES del pago)

4. Selector de Método de Pago
   ├─ Option A: Radio Button "Pagar con Tarjeta"
   │  └─ Monto: $19.97 (en rojo o naranja)
   │
   └─ Option B: Radio Button "Pagar con Puntos"
      ├─ Si usuario tiene suficientes puntos:
      │  └─ Mostrar: "Necesitas 1997 puntos"
      │  └─ Botón habilitado (verde)
      │
      └─ Si usuario NO tiene suficientes:
         ├─ Mostrar: "Te faltan 1497 puntos"
         ├─ ❌ Mostrar en rojo
         └─ Botón deshabilitado (gris)

5. Botones de Acción
   ├─ Botón "Cancelar" (gris)
   │  └─ Vuelve a CartActivity
   └─ Botón "Pagar" (rojo/naranja)
      └─ Habilidad/Deshabilitado según opción
```

---

### PASO 3: Usuario Selecciona Método

#### OPCIÓN A: Usuario selecciona "Pagar con Tarjeta"

```
Usuario hace click en Radio Button "Pagar con Tarjeta"
    ↓
CheckoutScreen actualiza UI
    ├─ Opción A: Radio activo ◉
    ├─ Opción B: Radio inactivo ○
    ├─ Muestra: "Monto a pagar: $19.97"
    ├─ Muestra input de tarjeta (Stripe Card Widget)
    │  ├─ Campo: Número de tarjeta
    │  ├─ Campo: Nombre
    │  ├─ Campo: Mes/Año
    │  └─ Campo: CVC
    ├─ Input validación en tiempo real
    │  ├─ Si tarjeta válida: botón verde
    │  └─ Si tarjeta inválida: botón gris
    └─ Botón "Pagar $19.97" habilitado

Usuario ingresa datos de tarjeta
    ├─ Stripe CardWidget valida
    ├─ UI feedback: verde si válido, rojo si no
    └─ [Espera a que usuario haga click "Pagar"]
```

#### OPCIÓN B: Usuario selecciona "Pagar con Puntos"

```
Usuario hace click en Radio Button "Pagar con Puntos"
    ↓
CheckoutScreen actualiza UI
    ├─ Opción A: Radio inactivo ○
    ├─ Opción B: Radio activo ◉
    ├─ Calcula puntos necesarios: $19.97 * 100 = 1997 puntos
    ├─ Compara con saldo: 500 puntos
    ├─ Resultado: 1997 > 500 (INSUFICIENTES)
    ├─ Muestra dialog/snackbar:
    │  "❌ Te faltan 1497 puntos"
    ├─ Botón "Pagar" DESHABILITADO (gris)
    └─ Mensaje: "Necesitas más puntos para esta compra"

(Alternativa: si usuario tuviera 3000 puntos)
    ├─ Calcula: 3000 >= 1997 (SUFICIENTES)
    ├─ Muestra: "✅ Puedes pagar con puntos"
    ├─ Botón "Pagar" HABILITADO (verde)
    └─ Dialog de confirmación: "¿Usar 1997 puntos?"
```

---

### PASO 4: Usuario Hace Click "Pagar"

#### ESCENARIO A: Pago con Tarjeta

```
Usuario ingresa datos tarjeta y hace click "Pagar $19.97"
    ↓
CheckoutScreen
    ├─ Desactiva botón (previene doble click)
    ├─ Muestra spinner/loading
    └─ Mensaje: "Procesando pago..."
    ↓
CheckoutViewModel
    ├─ Valida que tarjeta no esté vacía
    ├─ CardWidget de Stripe genera token temporal
    │  └─ Token válido solo 15 minutos
    ├─ Obtiene Firebase ID Token del usuario
    └─ Envía a Backend:
       {
         "token": "tok_visa_XXXXX",
         "amount": 1997,  (cents)
         "userId": "uid123",
         "orderId": "order_ABC123"
       }
    ↓
Backend (Node.js)
    ├─ Recibe request
    ├─ Valida Firebase token
    ├─ Valida monto > 0
    ├─ Llamada a Stripe API:
    │  stripe.charges.create({
    │    amount: 1997,
    │    source: "tok_visa_XXXXX",
    │    currency: "USD"
    │  })
    ├─ Stripe responde: chargeId = "ch_1234567890"
    ├─ ✅ Pago exitoso
    ├─ Crea orden en Firebase:
    │  /orders/uid123/order_ABC123
    │  {
    │    items: [...],
    │    totalAmount: 19.97,
    │    paymentMethod: "card",
    │    chargeId: "ch_1234567890",
    │    status: "completed"
    │  }
    ├─ Actualiza puntos: +2 (10% de $19.97)
    │  /users/uid123/rewards
    │  {
    │    currentPoints: 502  (era 500, sumó 2)
    │  }
    └─ Responde a App:
       {
         "success": true,
         "orderId": "order_ABC123",
         "pointsEarned": 2
       }
    ↓
App recibe respuesta
    ├─ Muestra ConfirmationScreen
    ├─ Limpia carrito
    └─ Muestra:
       "✅ ¡Pago exitoso!"
       "Orden: order_ABC123"
       "Pagado: $19.97 con tarjeta"
       "Puntos ganados: +2"
       "Nuevo saldo: 502 puntos"
```

#### ESCENARIO B: Pago con Puntos

```
Usuario selecciona "Pagar con Puntos" y hace click "Pagar"
    ↓
Dialog de confirmación:
    "¿Usar 1997 puntos para esta compra?"
    [Cancelar]  [Confirmar]

Usuario hace click "Confirmar"
    ↓
CheckoutScreen
    ├─ Desactiva botón
    ├─ Muestra spinner
    └─ Mensaje: "Procesando pago con puntos..."
    ↓
CheckoutViewModel
    ├─ Obtiene Firebase ID Token
    └─ Envía a Backend:
       {
         "paymentMethod": "points",
         "pointsToUse": 1997,
         "userId": "uid123",
         "orderId": "order_DEF456"
       }
    ↓
Backend
    ├─ Recibe request
    ├─ Valida Firebase token
    ├─ Lee puntos en BD: 500
    ├─ Valida: 500 >= 1997?
    ├─ Resultado: NO (1997 > 500)
    ├─ ❌ Responde error:
    │  {
    │    "success": false,
    │    "error": "Puntos insuficientes",
    │    "available": 500,
    │    "needed": 1997
    │  }
    ↓
App recibe error
    ├─ Muestra snackbar rojo:
    │  "❌ No tienes suficientes puntos"
    ├─ Botón vuelve a habilitarse
    └─ Usuario puede reintentar o cambiar método

(ALTERNATIVA: si usuario tuviera 3000 puntos)
    ├─ Valida: 3000 >= 1997 ✅
    ├─ Transacción ATOMICA:
    │  ├─ Deduce puntos: 3000 - 1997 = 1003
    │  ├─ Crea orden:
    │  │  {
    │  │    paymentMethod: "points",
    │  │    pointsUsed: 1997,
    │  │    pointsEarned: 0  ← NO gana nuevos
    │  │  }
    │  └─ Guarda en DB
    ├─ ✅ Responde:
    │  {
    │    "success": true,
    │    "orderId": "order_DEF456",
    │    "pointsRemaining": 1003
    │  }
    ↓
App recibe éxito
    ├─ Muestra ConfirmationScreen
    ├─ Limpia carrito
    └─ Muestra:
       "✅ ¡Compra realizada!"
       "Orden: order_DEF456"
       "Pagado: 1997 puntos"
       "Puntos gastados: 1997"
       "Nuevo saldo: 1003 puntos"
```

---

### PASO 5: ConfirmationScreen

```
Después de pago exitoso:

┌──────────────────────────────────┐
│   ✅ ¡COMPRA EXITOSA!            │
├──────────────────────────────────┤
│                                  │
│ Número de Orden:                 │
│ #order_ABC123                    │
│                                  │
│ ────────────────────────────     │
│ RESUMEN:                         │
│ ────────────────────────────     │
│ Total pagado:      $19.97        │
│ Método:            Tarjeta       │
│ Puntos ganados:    +2            │
│                                  │
│ ────────────────────────────     │
│ MIS PUNTOS DESPUÉS:              │
│ ┌──────────────────────────────┐ │
│ │ Saldo anterior:  500 puntos │ │
│ │ Ganados:         +2 puntos  │ │
│ │ SALDO ACTUAL:    502 puntos │ │
│ │ (Equivale a: $5.02)         │ │
│ └──────────────────────────────┘ │
│                                  │
│ [Volver al Inicio]  [Ver Orden] │
│                                  │
└──────────────────────────────────┘
```

---

## 🎨 Pantalla Checkout (Detallada)

### Layout Visual

```
┌─ CheckoutScreen ─────────────────────────────┐
│                                              │
│ [X]                          CHECKOUT        │
│                                              │
├──────────────────────────────────────────────┤
│                                              │
│  📋 RESUMEN DE COMPRA                       │
│  ┌──────────────────────────────────────┐   │
│  │ • Pollo Frito x2        $15.98       │   │
│  │ • Papas x1              $3.99        │   │
│  │ ────────────────────────────────     │   │
│  │ TOTAL:                  $19.97       │   │
│  └──────────────────────────────────────┘   │
│                                              │
├──────────────────────────────────────────────┤
│                                              │
│  💰 MIS PUNTOS                              │
│  ┌──────────────────────────────────────┐   │
│  │ Saldo: 500 puntos                    │   │
│  │ Equivalencia: $5.00                  │   │
│  └──────────────────────────────────────┘   │
│                                              │
├──────────────────────────────────────────────┤
│                                              │
│  💳 MÉTODO DE PAGO                          │
│                                              │
│  [◉] Pagar con Tarjeta                     │
│      ┌────────────────────────────────────┐ │
│      │ Número:      [ ____ ____ ____ ]  │ │
│      │ Nombre:      [ ________________ ]  │ │
│      │ MM/YY: [ __ ] CVC: [ ___ ]         │ │
│      └────────────────────────────────────┘ │
│      Monto: $19.97                         │
│                                              │
│  [○] Pagar con Puntos                      │
│      Necesitas: 1997 puntos                │
│      ❌ Te faltan 1497 puntos              │
│      (Botón deshabilitado)                 │
│                                              │
├──────────────────────────────────────────────┤
│                                              │
│  [Cancelar]          [Pagar $19.97]        │
│                                              │
└──────────────────────────────────────────────┘
```

---

## 🔄 Estados de la UI

### 1. Estado Inicial (Tarjeta Seleccionada)
- ✅ Radio Tarjeta activo
- ⭕ Radio Puntos inactivo
- 📝 Inputs de tarjeta visibles
- 🔘 Botón Pagar habilitado si tarjeta válida

### 2. Estado Cargando
- 🔄 Spinner visible
- 🔘 Botón deshabilitado
- 📝 Inputs de tarjeta deshabilitados
- 💬 Mensaje: "Procesando..."

### 3. Estado Error
- ❌ Snackbar rojo
- 🔘 Botón rehabilitado
- 📝 Inputs de tarjeta habilitados
- 💬 Mensaje: "Error: ..."

### 4. Estado Éxito
- ✅ Navega a ConfirmationScreen
- 🎉 Muestra información de compra
- 📊 Muestra puntos antes y después

---

## 📊 Datos en Pantalla - ANTES vs DESPUÉS

### ANTES DEL PAGO (en CheckoutScreen)
```
Puntos: 500
(mostrado en card)
```

### DESPUÉS DEL PAGO (en ConfirmationScreen)
```
Saldo anterior:  500 puntos
Ganados:         +2 puntos
Saldo actual:    502 puntos

O si pagó con puntos:

Saldo anterior:  3000 puntos
Gastados:        -1997 puntos
Saldo actual:    1003 puntos
```

---

## 🔌 Conexión con Backend

### Request que envía la App

**Para Tarjeta**:
```
POST http://backend:3000/payment/process-card
Headers:
  Authorization: Bearer {firebase_id_token}
Body:
{
  "token": "tok_visa_XXXXX",
  "amount": 1997,
  "userId": "uid123",
  "orderId": "order_ABC123"
}
```

**Para Puntos**:
```
POST http://backend:3000/payment/process-points
Headers:
  Authorization: Bearer {firebase_id_token}
Body:
{
  "paymentMethod": "points",
  "pointsToUse": 1997,
  "userId": "uid123",
  "orderId": "order_DEF456"
}
```

### Response del Backend

**Éxito**:
```json
{
  "success": true,
  "orderId": "order_ABC123",
  "chargeId": "ch_1234567890",
  "pointsEarned": 2,
  "timestamp": 1718000000000
}
```

**Error**:
```json
{
  "success": false,
  "error": "Puntos insuficientes",
  "available": 500,
  "needed": 1997
}
```

---

## 📱 Componentes Necesarios en App

### 1. **CheckoutScreen** (Pantalla Principal)
- Muestra resumen
- Selector de método
- Inputs de tarjeta
- Botón pagar

### 2. **CheckoutViewModel**
- Valida puntos
- Calcula totales
- Maneja estados
- Llama backend

### 3. **ConfirmationScreen**
- Muestra éxito
- Resumen de compra
- Puntos antes/después

### 4. **CheckoutService** (HTTP Client)
- POST /payment/process-card
- POST /payment/process-points

---

## 🔐 Validaciones

### En App (Frontend)

```
✅ Carrito no está vacío
✅ Usuario autenticado
✅ Tarjeta validada por Stripe (si selecciona tarjeta)
✅ Puntos > 0 (si selecciona puntos)
```

### En Backend

```
✅ Firebase token válido
✅ UID coincide
✅ Monto == carrito calculado
✅ Puntos >= puntos_a_usar
✅ Prevenir duplicados (orderID único)
```

---

## ⏱️ Timeline Simplificado

### Día 1: Setup
- T-001: Backend Node.js
- T-002: Stripe config
- T-003: Firebase Admin

### Día 2-3: Frontend Tarjeta
- T-004: CheckoutScreen UI
- T-005: CheckoutViewModel
- T-006: Flujo pago tarjeta
- T-007: Backend pago tarjeta

### Día 4: Frontend Puntos
- T-008: Validar puntos
- T-009: Flujo pago puntos
- T-010: Backend pago puntos

### Día 5: Polish
- T-011: ConfirmationScreen
- T-012: Error handling
- T-013: Testing

**Total**: 5 días (40 horas)

---

## ✅ Checklist Final

```
PANTALLA CHECKOUT:
☐ Resumen de carrito visible
☐ Total correcto
☐ Saldo de puntos visible ANTES
☐ Radio Button Tarjeta
☐ Radio Button Puntos
☐ Inputs de tarjeta aparecen si tarjeta seleccionada
☐ Validación puntos correcta
☐ Botón Pagar habilitado/deshabilitado correctamente

FLUJO PAGO TARJETA:
☐ Envía token a backend
☐ Backend procesa con Stripe
☐ Orden se crea en Firebase
☐ Puntos se actualizan (+10%)
☐ ConfirmationScreen muestra puntos nuevos

FLUJO PAGO PUNTOS:
☐ Valida puntos suficientes
☐ Si insuficientes: bloquea botón
☐ Si suficientes: permite pagar
☐ Backend deduce puntos
☐ ConfirmationScreen muestra puntos deducidos

PANTALLA CONFIRMACIÓN:
☐ Muestra número de orden
☐ Muestra saldo ANTES del pago
☐ Muestra cambio (ganados o gastados)
☐ Muestra saldo DESPUÉS del pago
☐ Botón para volver al inicio
```

---

**Versión Simplificada: REFORMULADO.md**  
**Fecha**: 12 de Junio, 2026  
**Alcance**: Pruebas de Pago (Tarjeta + Puntos)  
**Complejidad**: Baja

