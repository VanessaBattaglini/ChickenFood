# 🏗️ Sistema de Pagos - Diseño Técnico

**Fecha**: 12 de Junio, 2026  
**Versión**: 1.0

---

## 🎨 Arquitectura General

```
┌────────────────────────────────────────────────────────┐
│                    ANDROID APP                         │
├────────────────────────────────────────────────────────┤
│  CartActivity                                          │
│    ↓                                                   │
│  CheckoutActivity/Screen                              │
│    ├─ CheckoutViewModel                               │
│    ├─ PaymentMethodSelector (Tarjeta/Puntos/Híbrido) │
│    ├─ CardInputWidget (Stripe SDK)                    │
│    └─ ConfirmationScreen                              │
└────────────────────────────────────────────────────────┘
                        ↓ HTTP/HTTPS
┌────────────────────────────────────────────────────────┐
│                   BACKEND (Node.js)                    │
├────────────────────────────────────────────────────────┤
│  /payment/process-card        (Pago con tarjeta)      │
│  /payment/process-points      (Pago con puntos)       │
│  /payment/validate            (Validar sesión)        │
│  /orders/create               (Crear orden)           │
│  /orders/history              (Historial)             │
└────────────────────────────────────────────────────────┘
                    ↓ API REST
┌────────────────────────────────────────────────────────┐
│            STRIPE (Payment Processor)                  │
├────────────────────────────────────────────────────────┤
│  charges.create()  (Procesar pago)                    │
│  tokens.create()   (Tokenizar tarjeta)                │
│  charges.list()    (Historial)                        │
└────────────────────────────────────────────────────────┘

┌────────────────────────────────────────────────────────┐
│           FIREBASE (Database + Auth)                   │
├────────────────────────────────────────────────────────┤
│  /users/{uid}/rewards          (Saldo de puntos)      │
│  /users/{uid}/orders           (Órdenes del usuario)  │
│  /pointTransactions/{txnId}    (Historial puntos)     │
└────────────────────────────────────────────────────────┘
```

---

## 📱 Componentes en la App

### 1. CheckoutActivity/Screen

**Responsabilidad**: Mostrar opciones de pago y coordinar el flujo

**Estructura**:
```
CheckoutScreen
├─ Header
│  └─ "Checkout" + botón atrás
├─ ResumenCarrito
│  ├─ Items
│  ├─ Subtotal
│  └─ Total
├─ SaldoPuntos
│  └─ "Puntos disponibles: 1500"
├─ PaymentMethodSelector (RadioGroup/Tabs)
│  ├─ Option 1: "Solo Tarjeta"
│  ├─ Option 2: "Solo Puntos"
│  └─ Option 3: "Puntos + Tarjeta"
├─ ConditionalContent (según selección)
│  ├─ Si "Solo Tarjeta" → CardInputForm
│  ├─ Si "Solo Puntos" → ConfirmDialog
│  └─ Si "Puntos + Tarjeta" → PuntosPicker + CardInputForm
└─ ActionButtons
   ├─ "Cancelar"
   └─ "Proceder al Pago"
```

### 2. CheckoutViewModel

**Responsabilidad**: Lógica de negocio del checkout

**Estados**:
```kotlin
data class CheckoutUiState(
    val carritoItems: List<CartItem> = emptyList(),
    val subtotal: Double = 0.0,
    val total: Double = 0.0,
    val puntosTotalesDisponibles: Int = 0,
    val metodoPago: PaymentMethod = PaymentMethod.CREDIT_CARD,
    val puntosPorUsar: Int = 0,  // 0 si no usa puntos
    val montoPorTarjeta: Double = 0.0,
    val isLoading: Boolean = false,
    val error: String? = null,
    val success: Boolean = false,
    val ordenId: String? = null
)

enum class PaymentMethod {
    CREDIT_CARD,
    POINTS,
    HYBRID
}
```

**Métodos principales**:
```
fun selectPaymentMethod(method: PaymentMethod)
fun onPointsChanged(cantidad: Int)  // Si selecciona híbrido
fun calcularMontos()  // Actualiza totales
fun validarPago(): ValidationResult
fun procesarPago()
```

### 3. CardInputWidget (Stripe)

**Responsabilidad**: Capturar datos de tarjeta de forma segura

**Estructura**:
```
CardInputWidget
├─ Campo: Número de tarjeta
├─ Campo: Nombre titular
├─ Fila:
│  ├─ Campo: MM/YY
│  └─ Campo: CVC
└─ Validación en tiempo real
```

**Seguridad**:
- ✅ Stripe maneja el widget
- ✅ La app NUNCA ve datos crudos
- ✅ Stripe devuelve un token temporal

### 4. ConfirmationScreen

**Responsabilidad**: Mostrar resultado de la compra

**Estructura**:
```
ConfirmationScreen
├─ ✅ Ícono de éxito
├─ "¡Pedido completado!"
├─ Número de orden
├─ Resumen:
│  ├─ Total: $XX.XX
│  ├─ Método: Tarjeta/Puntos/Híbrido
│  ├─ Puntos gastados: XXX (si aplica)
│  └─ Puntos ganados: XXX (si paga con tarjeta)
├─ "Detalles de la orden"
└─ Botones:
   ├─ "Volver al Inicio"
   └─ "Ver mi orden"
```

---

## 🔄 Flujos de Procesamiento

### Flujo 1: Pago con Tarjeta

```
Usuario en CheckoutScreen
    ↓
1. Selecciona "Solo Tarjeta"
    ↓
2. Ingresa: Número, Exp, CVC, Nombre
    ↓
3. CardInputWidget de Stripe valida
    ↓
4. Click "Proceder al Pago"
    ↓
5. CheckoutViewModel.validarPago()
    ├─ ✅ Usuario autenticado
    ├─ ✅ Carrito no está vacío
    ├─ ✅ Monto > 0
    └─ ✅ Tarjeta pasó validación Stripe
    ↓
6. CardInputWidget.getCardToken()
    └─ Stripe devuelve: tok_visa_XXXXX (token seguro)
    ↓
7. CheckoutViewModel.procesarPago()
    ├─ GET Firebase ID Token
    └─ POST a Backend: {
         token: "tok_visa_XXXXX",
         amount: 2999 (cents),
         currency: "USD",
         userId: "uid",
         orderId: "order_XXXXX"
       }
    ↓
Backend (Node.js)
    ├─ Valida Firebase token
    ├─ Valida usuario existe
    ├─ Valida monto == carrito calculado
    ├─ Carga tarjeta con Stripe
    │  └─ stripe.charges.create({
    │       amount: 2999,
    │       source: "tok_visa_XXXXX",
    │       currency: "USD",
    │       description: "ChickenFood Order"
    │     })
    ├─ Stripe responde: chargeId = "ch_XXXXX"
    ├─ Crea orden en Firebase
    │  /orders/{userId}/{orderId}
    │  {
    │    items: [...],
    │    totalAmount: 29.99,
    │    paymentMethod: "card",
    │    chargeId: "ch_XXXXX",
    │    status: "completed",
    │    timestamp: Date.now()
    │  }
    ├─ Actualiza puntos: +3 (10% de $29.99)
    │  /users/{userId}/rewards
    │  {
    │    currentPoints: 1503,
    │    totalEarned: 1003
    │  }
    ├─ Guarda transacción
    │  /pointTransactions/{txId}
    │  {
    │    type: "earned",
    │    points: 3,
    │    reason: "Purchase order_XXXXX",
    │    timestamp: Date.now()
    │  }
    └─ Responde a App: {
         success: true,
         orderId: "order_XXXXX",
         chargeId: "ch_XXXXX"
       }
    ↓
App recibe respuesta
    ├─ setState(success = true)
    ├─ clearCart()
    ├─ Navigate a ConfirmationScreen
    └─ Muestra: "Orden XXXXX completada, +3 puntos"
```

### Flujo 2: Pago con Puntos

```
Usuario en CheckoutScreen
    ↓
1. Selecciona "Solo Puntos"
    ↓
2. CheckoutViewModel.calcularMontos()
    ├─ Total: $29.99
    ├─ Puntos necesarios: $29.99 * 100 = 2999
    ├─ Puntos disponibles: 1500
    └─ Resultado: Insuficientes
    ↓
3. UI bloquea botón
    ├─ Muestra rojo: "Te faltan 1499 puntos"
    └─ Botón "Proceder" DESHABILITADO
    ↓
(Alternativa: Usuario tiene 5000 puntos)
    ├─ Botón habilitado
    ├─ Dialog: "¿Usar 2999 puntos?"
    └─ Click "Confirmar"
    ↓
4. CheckoutViewModel.procesarPago()
    ├─ GET Firebase ID Token
    └─ POST a Backend: {
         paymentMethod: "points",
         pointsToUse: 2999,
         userId: "uid",
         orderId: "order_XXXXX"
       }
    ↓
Backend
    ├─ Valida Firebase token
    ├─ Lee puntos en BD: 5000
    ├─ Valida: 5000 >= 2999 ✅
    ├─ Transacción ATOMICA:
    │  ├─ Deduce puntos: 5000 - 2999 = 2001
    │  ├─ Crea orden
    │  │  {
    │  │    paymentMethod: "points",
    │  │    pointsUsed: 2999,
    │  │    pointsEarned: 0,  ← NO gana nuevos puntos
    │  │    status: "completed"
    │  │  }
    │  └─ Guarda transacción
    │     {
    │       type: "spent",
    │       points: 2999,
    │       reason: "Purchase order_XXXXX"
    │     }
    └─ Responde: {
         success: true,
         orderId: "order_XXXXX",
         pointsRemaining: 2001
       }
    ↓
App recibe
    ├─ setState(success = true)
    ├─ clearCart()
    ├─ Navigate a ConfirmationScreen
    └─ Muestra: "Orden XXXXX, -2999 puntos"
```

### Flujo 3: Pago Híbrido (Puntos + Tarjeta)

```
Usuario en CheckoutScreen
    ↓
1. Selecciona "Puntos + Tarjeta"
    ↓
2. Slider/Input: "¿Cuántos puntos usar?"
    └─ Disponibles: 1500
    └─ Usuario entra: 1000
    ↓
3. CheckoutViewModel.onPointsChanged(1000)
    ├─ Calcula: 1000 puntos = $10.00
    ├─ A pagar con tarjeta: $29.99 - $10.00 = $19.99
    └─ Actualiza UI en tiempo real
    ↓
4. Usuario ingresa datos de tarjeta
    ↓
5. Click "Proceder al Pago"
    ↓
6. CardInputWidget.getCardToken()
    └─ Stripe devuelve: tok_visa_XXXXX
    ↓
7. CheckoutViewModel.procesarPago()
    └─ POST a Backend: {
         paymentMethod: "hybrid",
         pointsToUse: 1000,
         cardAmount: 1999,  (cents)
         token: "tok_visa_XXXXX",
         userId: "uid",
         orderId: "order_XXXXX"
       }
    ↓
Backend
    ├─ Valida usuario
    ├─ Valida: puntos >= 1000
    ├─ Valida: cardAmount + pointsValue == totalAmount
    ├─ Transacción ATOMICA:
    │  ├─ Deduce puntos: 1500 - 1000 = 500
    │  ├─ Carga tarjeta con Stripe ($19.99)
    │  │  └─ stripe.charges.create({
    │  │       amount: 1999,
    │  │       source: "tok_visa_XXXXX"
    │  │     })
    │  ├─ Crea orden
    │  │  {
    │  │    pointsUsed: 1000,
    │  │    amountPaidWithCard: 19.99,
    │  │    pointsEarned: 2,  ← 10% de $19.99
    │  │    chargeId: "ch_XXXXX"
    │  │  }
    │  └─ Guarda transacciones (puntos gastados + puntos ganados)
    └─ Responde éxito
    ↓
App muestra ConfirmationScreen
    └─ "Orden XXXXX completada"
    └─ "Puntos gastados: 1000"
    └─ "Pagado con tarjeta: $19.99"
    └─ "Puntos ganados: +2"
```

---

## 💾 Estructura de Datos en Firebase

### Órdenes

```
/orders/{userId}/{orderId}
├─ orderId: "order_ABC123"
├─ userId: "uid_XXXXX"
├─ items: [
│   {
│     "productId": 5,
│     "name": "Pollo Frito",
│     "quantity": 2,
│     "price": 7.99,
│     "subtotal": 15.98
│   }
│ ]
├─ totalAmount: 29.99
├─ paymentMethod: "card" | "points" | "hybrid"
├─ pointsUsed: 0  (si usó puntos)
├─ pointsEarned: 3  (10% del monto en tarjeta)
├─ amountPaidWithCard: 29.99  (si pago con tarjeta)
├─ amountPaidWithPoints: 0.00  (si pago con puntos, = pointsUsed * 0.01)
├─ chargeId: "ch_1234567890"  (Stripe ID, si aplica)
├─ status: "completed" | "failed" | "pending"
├─ timestamp: 1718000000000
└─ deliveryAddress: "123 Main St"
```

### Transacciones de Puntos

```
/pointTransactions/{transactionId}
├─ transactionId: "txn_XYZ789"
├─ userId: "uid_XXXXX"
├─ type: "earned" | "spent"
├─ points: 3  (cantidad)
├─ reason: "Purchase order_ABC123" | "Refund" | etc
├─ orderId: "order_ABC123"  (referencia a orden)
└─ timestamp: 1718000000000
```

### Rewards (Saldo de Puntos)

```
/users/{userId}/rewards
├─ userId: "uid_XXXXX"
├─ currentPoints: 2997  (balance actual)
├─ totalPointsEarned: 15000  (total acumulado histórico)
├─ totalPointsSpent: 12003  (total gastado histórico)
├─ level: "Regular" | "Bronze" | "Silver" | "Gold" | "Platinum"
├─ lastTransactionDate: 1718000000000
└─ nextLevelThreshold: 10000  (puntos necesarios para next level)
```

---

## 🔒 Seguridad - Detalles

### Validación Backend (CRÍTICO)

```
ANTES de procesar cualquier pago:

1. Validar Firebase Token
   - Token válido y no expirado
   - Extraer UID del token

2. Validar Carrito
   - Ítems existen en BD
   - Precios coinciden (no cliente edite)
   - Cantidad válida (> 0)

3. Validar Monto
   - Monto > 0
   - Monto <= presupuesto razonable ($500 máximo)
   - Monto coincide con carrito calculado (server-side)

4. Validar Puntos (si aplica)
   - Lee de BD, NO confía en cliente
   - Puntos >= puntos_a_usar
   - Puntos_a_usar >= 0

5. Validar Tarjeta (si aplica)
   - Token de Stripe válido
   - Token no expirado (generados hace poco)
   - Monto con tarjeta > $0.50 (Stripe mínimo)

6. Prevenir Duplicados
   - OrderID único
   - Si orderId ya existe: responder OK (idempotencia)
   - Timestamp + userID combo

7. Loguear Todo
   - Intento de pago: usuario, monto, método, timestamp
   - Resultado: éxito/error, detalles
   - Errores: razón exacta
```

### Reglas Firestore

```json
{
  "orders": {
    "{userId}": {
      "{orderId}": {
        ".read": "auth != null && auth.uid == $userId",
        ".write": false,  // Solo backend puede escribir
        ".validate": "newData.hasChildren(['items', 'totalAmount', 'paymentMethod'])"
      }
    }
  },
  "pointTransactions": {
    "{transactionId}": {
      ".read": "root.child('orders').child(auth.uid).child(data.child('orderId').val()).exists()",
      ".write": false
    }
  },
  "users": {
    "{userId}": {
      "rewards": {
        ".read": "auth != null && auth.uid == $userId",
        ".write": false
      }
    }
  }
}
```

---

## 🛠️ Dependencias Necesarias

### App (Android)

```gradle
// Stripe para pagos con tarjeta
implementation 'com.stripe:stripe-android:20.x.x'

// FirebaseAuth (ya está)
implementation 'com.google.firebase:firebase-auth:22.x.x'

// Firebase Realtime Database (ya está)
implementation 'com.google.firebase:firebase-database:20.x.x'

// HTTP Client para backend
implementation 'com.squareup.okhttp3:okhttp:4.x.x'
implementation 'com.squareup.retrofit2:retrofit:2.x.x'
implementation 'com.squareup.retrofit2:converter-gson:2.x.x'

// Compose (ya está, pero necesita):
implementation 'androidx.compose.material3:material3:1.x.x'
```

### Backend (Node.js)

```json
{
  "dependencies": {
    "express": "4.x.x",
    "stripe": "14.x.x",
    "firebase-admin": "12.x.x",
    "dotenv": "16.x.x",
    "cors": "2.x.x",
    "helmet": "7.x.x"
  }
}
```

---

## 📊 API Endpoints (Backend)

### 1. Procesar Pago con Tarjeta

```
POST /payment/process-card

Request:
{
  "token": "tok_visa_XXXXX",
  "amount": 2999,
  "currency": "USD",
  "userId": "uid",
  "orderId": "order_XXXXX"
}

Response (200):
{
  "success": true,
  "orderId": "order_XXXXX",
  "chargeId": "ch_1234567890",
  "pointsEarned": 3,
  "timestamp": 1718000000000
}

Response (400):
{
  "success": false,
  "error": "Tarjeta rechazada",
  "code": "card_declined"
}

Response (401):
{
  "success": false,
  "error": "No autenticado"
}

Response (409):
{
  "success": false,
  "error": "Orden duplicada"
}
```

### 2. Procesar Pago con Puntos

```
POST /payment/process-points

Request:
{
  "pointsToUse": 2999,
  "userId": "uid",
  "orderId": "order_XXXXX"
}

Response (200):
{
  "success": true,
  "orderId": "order_XXXXX",
  "pointsRemaining": 2001,
  "timestamp": 1718000000000
}

Response (400):
{
  "success": false,
  "error": "Saldo de puntos insuficiente"
}
```

### 3. Procesar Pago Híbrido

```
POST /payment/process-hybrid

Request:
{
  "token": "tok_visa_XXXXX",
  "pointsToUse": 1000,
  "cardAmount": 1999,
  "userId": "uid",
  "orderId": "order_XXXXX"
}

Response (200):
{
  "success": true,
  "orderId": "order_XXXXX",
  "chargeId": "ch_XXXXX",
  "pointsRemaining": 500,
  "pointsEarned": 2,
  "timestamp": 1718000000000
}

Response (400):
{
  "success": false,
  "error": "Validación fallida",
  "details": "Monto con tarjeta debe ser > $0.50"
}
```

### 4. Obtener Historial de Órdenes

```
GET /orders?userId=uid

Response (200):
{
  "orders": [
    {
      "orderId": "order_ABC123",
      "totalAmount": 29.99,
      "paymentMethod": "card",
      "status": "completed",
      "timestamp": 1718000000000
    }
  ]
}
```

---

## ⚡ Manejo de Errores

### Tipo 1: Error Stripe (Tarjeta Rechazada)

```
Backend recibe error de Stripe
    ↓
Log del error
    ↓
Responde a App: {
  success: false,
  error: "Tarjeta rechazada",
  code: "card_declined",
  details: "Fondos insuficientes"
}
    ↓
App muestra al usuario:
"Tarjeta rechazada: Fondos insuficientes"
+ Botón "Reintentar"
    ↓
Carrito permanece intacto
```

### Tipo 2: Error de Validación

```
Backend detecta monto != carrito
    ↓
Responde: {
  success: false,
  error: "Validación fallida",
  code: "validation_error"
}
    ↓
App muestra:
"Error en cálculo. Por favor intenta de nuevo"
    ↓
Backend LOGUEA como potencial fraude
```

### Tipo 3: Conexión Perdida

```
App pierde conexión durante pago
    ↓
App intenta reintentar (exponential backoff)
    ↓
Si falla 3 veces: muestra modal
"Procesando tu pago..."
"Verificando estado con el servidor"
    ↓
En background: Keep trying cada 10 segundos
    ↓
Si orden se creó: mostrar éxito
Si no: mostrar error, carrito intacto
```

---

## 📱 Navegación

```
CartActivity
    ↓ "Proceder al Pago"
CheckoutActivity (UI de pago)
    ├─ Camino 1: Éxito
    │   ↓
    │   ConfirmationSuccessScreen
    │   ├─ "Volver al Inicio" → MainActivity
    │   └─ "Ver Orden" → OrderDetailScreen
    │
    ├─ Camino 2: Error
    │   ↓
    │   CheckoutActivity (error visible)
    │   ├─ "Reintentar" (reprocesar)
    │   ├─ "Cambiar método" (volver a seleccionar)
    │   └─ "Cancelar" → CartActivity (carrito intacto)
    │
    └─ Camino 3: Usuario cancela
        ↓
        CartActivity (carrito intacto)
```

---

## 🧪 Testing Strategy

### Pruebas Unitarias
- Validación de montos
- Cálculo de puntos
- Conversión puntos ↔ dinero

### Pruebas de Integración
- Flujo completo pago con tarjeta (Sandbox Stripe)
- Flujo completo pago con puntos (Firebase local)
- Flujo híbrido

### Pruebas de Seguridad
- No loguear datos de tarjeta
- Validar tokens
- Prevenir inyección SQL/NoSQL

### Pruebas de UI
- Botones deshabilitados correctamente
- Validaciones en tiempo real
- Errores mostrados claramente

