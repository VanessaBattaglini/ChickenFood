# 🏗️ Arquitectura Detallada del Flujo de Pago - Mock Gratuito

**Objetivo**: Sistema de pagos simulado 100% gratuito usando solo Jetpack Compose + Firebase

**Alcance**: Explicación conceptual sin código

---

# PARTE 1: DISEÑO DE LA PANTALLA DE SELECCIÓN (UI)

## 1.1 Estructura Lógica de la UI

La pantalla de checkout se divide en **4 secciones verticales**:

```
┌────────────────────────────────────────┐
│ SECCIÓN 1: HEADER Y NAVEGACIÓN         │
├────────────────────────────────────────┤
│ [←] CONFIRMAR COMPRA      [?]          │
├────────────────────────────────────────┤
│                                        │
│ SECCIÓN 2: INFORMACIÓN DEL CARRITO    │
├────────────────────────────────────────┤
│ Resumen de Items                       │
│ • Pollo Frito x2          $15.98      │
│ • Papas x1                $3.99       │
│ ────────────────────────────────       │
│ SUBTOTAL:                 $19.97      │
│                                        │
├────────────────────────────────────────┤
│                                        │
│ SECCIÓN 3: INFORMACIÓN DE PUNTOS      │
├────────────────────────────────────────┤
│ 💰 TUS PUNTOS DE FIDELIDAD            │
│ ┌──────────────────────────────────┐  │
│ │ Saldo Actual:  500 puntos        │  │
│ │ Valor en Dinero: $5.00           │  │
│ │ (1 punto = $0.01)                │  │
│ │                                  │  │
│ │ Nivel: 🥉 BRONCE (450-999)       │  │
│ │ Progreso: ████░░░░ (50%)        │  │
│ └──────────────────────────────────┘  │
│                                        │
├────────────────────────────────────────┤
│                                        │
│ SECCIÓN 4: SELECTOR DE MÉTODO DE PAGO│
├────────────────────────────────────────┤
│                                        │
│ ¿CÓMO DESEAS PAGAR?                   │
│                                        │
│ ┌─ OPCIÓN A ──────────────────────┐  │
│ │ [◉] Pagar con Tarjeta/PayPal   │  │
│ │     Monto: $19.97              │  │
│ │     ────────────────────────    │  │
│ │     (Ganas 2 puntos bonus)      │  │
│ │                                 │  │
│ │     Campos de entrada:          │  │
│ │     [Input Card Number]         │  │
│ │     [Input Cardholder Name]     │  │
│ │     [MM/YY] [CVC]              │  │
│ │                                 │  │
│ │     ⚠️ SIMULADO - Solo TEST    │  │
│ └─────────────────────────────────┘  │
│                                        │
│ ┌─ OPCIÓN B ──────────────────────┐  │
│ │ [○] Pagar con Puntos            │  │
│ │     Puntos necesarios: 1997     │  │
│ │                                 │  │
│ │     ✅ Tienes suficientes       │  │
│ │     ○ Botón HABILITADO (verde)  │  │
│ │                                 │  │
│ │     O                           │  │
│ │                                 │  │
│ │     ❌ Te faltan 1497 puntos    │  │
│ │     ○ Botón DESHABILITADO (gris)│  │
│ └─────────────────────────────────┘  │
│                                        │
├────────────────────────────────────────┤
│ [Cancelar]         [Confirmar Pago]   │
└────────────────────────────────────────┘
```

---

## 1.2 Componentes Visuales por Sección

### SECCIÓN 1: Header
**Propósito**: Orientación y ayuda

**Elementos**:
- Botón atrás (←) → vuelve a CartActivity
- Título: "Confirmar Compra"
- Icono info (?) → muestra información sobre puntos

**Comportamiento**: Static, no cambia durante el flujo

---

### SECCIÓN 2: Resumen del Carrito
**Propósito**: Que el usuario vea QUÉ está comprando

**Elementos**:
- Lista scrolleable de items del carrito
- Cada item muestra:
  - Nombre del producto
  - Cantidad
  - Precio unitario
  - Subtotal (cantidad × precio)
- Línea separadora
- **SUBTOTAL en grande** (ej: $19.97)

**Comportamiento**: 
- Datos provienen del CarritoHelper (ya existe en tu app)
- Solo lectura, no editable

**Validaciones**:
- Si carrito está vacío → no llega a esta pantalla
- Si total = $0 → mostrar error

---

### SECCIÓN 3: Información de Puntos
**Propósito**: Mostrar estado actual ANTES del pago

**Elementos Principales**:
```
┌─────────────────────────────────────┐
│ 💰 TUS PUNTOS DE FIDELIDAD         │
├─────────────────────────────────────┤
│                                     │
│ 1. SALDO ACTUAL                     │
│    • Número grande: "500"           │
│    • Texto: "puntos"                │
│                                     │
│ 2. EQUIVALENCIA EN DINERO           │
│    • "Valor: $5.00"                 │
│    • "(1 punto = $0.01)"            │
│                                     │
│ 3. INFORMACIÓN DE NIVEL             │
│    • Icono (🥉 BRONCE)              │
│    • Rango: "(450-999 puntos)"      │
│    • Barra de progreso: ████░░░░    │
│    • Porcentaje: "50% del siguiente"│
│    • Puntos faltantes: "50 puntos"  │
│                                     │
│ 4. BENEFICIOS                       │
│    • Si pagas con tarjeta: "+2 pts" │
│    • Si pagas con puntos: "+0 pts"  │
│                                     │
└─────────────────────────────────────┘
```

**Origen de Datos**:
- RewardsViewModel (ya existe)
- Lee de Firebase: `/users/{uid}/rewards`

**Comportamiento**:
- Se actualiza en tiempo real cuando el usuario observa
- Muestra datos ANTES del pago (no simula cambios)
- Es informativo, no interactivo

---

### SECCIÓN 4: Selector de Método de Pago
**Propósito**: Que el usuario elija CÓMO pagar

#### OPCIÓN A: Pagar con Tarjeta/PayPal

```
┌─ Componente RadioButton ─────────────────────┐
│ [◉] Pagar con Tarjeta/PayPal               │
│     Monto: $19.97                          │
│     (Ganas 2 puntos bonus al pagar)        │
│     ⚠️ SIMULADO - Solo para Testing        │
│                                             │
│ ─────────────────────────────────────────  │
│ SI ESTÁ SELECCIONADO, MUESTRA:             │
│                                             │
│ 🎫 DATOS DE TARJETA (MockCard Widget)     │
│                                             │
│ Número de Tarjeta:                         │
│ [____  ____  ____  ____]                  │
│                                             │
│ Nombre del Titular:                        │
│ [________________________]                  │
│                                             │
│ Fecha de Vencimiento:  CVC:                │
│ [MM/YY]               [CVC]                │
│                                             │
│ 💡 Usa para testing: 4111 1111 1111 1111  │
│                      Mes/Año cualquiera    │
│                      CVC: 123              │
│                                             │
│ Validación en tiempo real:                 │
│ ✅ Validación correcta (inputs se ponen    │
│    en verde)                               │
│ ❌ Validación incorrecta (inputs en rojo)  │
│                                             │
│ Botón "Confirmar Pago":                    │
│ • Habilitado si todos los campos válidos   │
│ • Deshabilitado si falta información      │
│                                             │
└─────────────────────────────────────────────┘
```

**Lógica de Validación**:
1. Número de tarjeta: 16 dígitos (validar formato)
2. Nombre: no puede estar vacío
3. MM/YY: formato válido (01-12 para mes)
4. CVC: 3 dígitos

**Nota Importante**: Todo es SIMULADO
- No se conecta a Stripe
- No es un pago real
- Es solo un mock para testing

---

#### OPCIÓN B: Pagar con Puntos

```
┌─ Componente RadioButton ─────────────────────┐
│ [○] Pagar con Puntos                       │
│                                             │
│ ─────────────────────────────────────────  │
│ SI ESTÁ SELECCIONADO:                     │
│                                             │
│ Puntos necesarios para esta compra:       │
│ $19.97 × 100 = 1997 puntos                │
│                                             │
│ ──────────────────────────────────────    │
│ VALIDACIÓN AUTOMÁTICA:                    │
│                                             │
│ ✅ CASO 1: Usuario tiene suficientes      │
│    "✅ Tienes 500 puntos"                 │
│    "Necesitas: 1997 puntos"               │
│    "❌ Te faltan 1497 puntos"             │
│    Botón "Confirmar Pago": DESHABILITADO  │
│    Color: Gris                            │
│                                             │
│ ❌ CASO 2: Usuario no tiene suficientes   │
│    (mismo mensaje)                        │
│    Botón: DESHABILITADO                   │
│                                             │
│ ✅ CASO 3: Usuario tiene exacto           │
│    "✅ Tienes exactamente 1997 puntos"   │
│    Botón "Confirmar Pago": HABILITADO    │
│    Color: Verde                           │
│                                             │
│ ✅ CASO 4: Usuario tiene de sobra        │
│    "✅ Tienes suficientes puntos"        │
│    "Te sobrarían 503 puntos"             │
│    Botón "Confirmar Pago": HABILITADO    │
│    Color: Verde                           │
│                                             │
└─────────────────────────────────────────────┘
```

**Cálculo**: Conversión automática
- Total en dólares: $19.97
- Multiplicar por 100: 1997 puntos
- Comparar con saldo: 500 vs 1997
- Resultado: insuficientes

---

## 1.3 Estados Interactivos de la UI

### Estado 1: Inicial (Carga)
```
✅ Datos cargando desde Firebase
✅ Spinner visible mientras se obtienen puntos
✅ Botones deshabilitados
✅ Después: transición suave a Estado 2
```

### Estado 2: Listo (Default)
```
✅ Opción A (Tarjeta) seleccionada por defecto
✅ Inputs de tarjeta visibles
✅ Inputs vacíos (enfoque en primer campo)
✅ Botón "Confirmar Pago" deshabilitado (campos inválidos)
✅ Opción B radio button inactivo
```

### Estado 3: Usuario Selecciona Puntos
```
✅ Radio button cambia a Opción B
✅ Inputs de tarjeta desaparecen (animación fade-out)
✅ Información de cálculo de puntos aparece
✅ Botón habilitar/deshabilitar según validación
✅ Opción A radio button inactivo
```

### Estado 4: Usuario Ingresa Datos Tarjeta
```
✅ Inputs en foco (azul)
✅ Validación en tiempo real (rojo/verde)
✅ Botón se habilita cuando todo es válido
✅ Sin latency perceptible (validación local)
```

### Estado 5: Procesando Pago
```
✅ Botón deshabilitado y muestra spinner
✅ Inputs deshabilitados (no se puede cambiar)
✅ Mensaje: "Procesando tu pago..."
✅ No se puede ir atrás (back button deshabilitado)
✅ Se espera respuesta del mock backend (1-2 segundos)
```

### Estado 6: Éxito
```
✅ Transición suave a ConfirmationScreen
✅ Animación: slide-in from bottom
✅ Muestra resumen y puntos antes/después
✅ Opción para volver al inicio
```

### Estado 7: Error
```
✅ Snackbar rojo en la parte inferior
✅ Mensaje: "Error: [razón específica]"
✅ Botones vuelven a habilitarse
✅ Usuario puede reintentar
✅ Inputs no se limpian (conservan datos)
```

---

## 1.4 Flujo Visual del Usuario en la UI

```
Usuario abre CheckoutScreen
    ↓
Ve SECCIÓN 2: Resumen del carrito
    ↓
Lee SECCIÓN 3: "Tengo 500 puntos"
    ↓
Ve SECCIÓN 4: Opciones de pago
    ↓
┌─────────────────────┬─────────────────────┐
│ OPCIÓN A: TARJETA   │ OPCIÓN B: PUNTOS    │
├─────────────────────┼─────────────────────┤
│ 1. Radio seleccionado│ 1. Lee que necesita │
│ 2. Inputs aparecen   │    1997 puntos      │
│ 3. Ingresa datos     │ 2. Ve que tiene 500 │
│ 4. Valida campos     │ 3. Ve botón gris    │
│ 5. Botón se habilita │    (deshabilitado)  │
│ 6. Presiona "Pagar"  │ 4. No puede pagar   │
│ 7. Vuelve a Opción A │    (insuficientes)  │
│ 8. Presiona "Pagar"  │                     │
│ 9. Procesa...        │                     │
│ 10. Éxito            │                     │
└─────────────────────┴─────────────────────┘
```

---

# PARTE 2: LÓGICA DEL PROCESO DE PAGO (Backend Mock)

## 2.1 Arquitectura del Sistema

```
┌─────────────────────────────────────────────────────────────┐
│                    JETPACK COMPOSE (UI)                     │
│   CheckoutScreen + CheckoutViewModel (StateFlow)            │
└────────────────────────┬────────────────────────────────────┘
                         │ User clicks "Confirmar Pago"
                         ↓
┌─────────────────────────────────────────────────────────────┐
│            VIEWMODEL (Lógica de negocio)                    │
│                                                              │
│  CheckoutViewModel                                          │
│  ├─ Valida datos ingresados                                 │
│  ├─ Prepara payload                                         │
│  ├─ Obtiene Firebase ID Token                              │
│  └─ Invoca MockPaymentService                              │
└────────────────────────────────────┬────────────────────────┘
                                     │
                         ┌───────────┴───────────┐
                         ↓                       ↓
            ┌────────────────────┐  ┌────────────────────┐
            │  MOCK PAYMENT      │  │  FIREBASE          │
            │  SERVICE (LOCAL)   │  │  DATABASE          │
            │                    │  │                    │
            │ Simula:            │  │ Almacena:          │
            │ • Validación       │  │ • Órdenes          │
            │ • Procesamiento    │  │ • Transacciones    │
            │ • Respuesta        │  │ • Saldo de puntos  │
            │   (1-2 seg)        │  │                    │
            └────────────────────┘  └────────────────────┘
```

---

## 2.2 Flujo de Pago con Tarjeta (Simulado)

### PASO 1: Usuario Presiona "Confirmar Pago"

```
Usuario en CheckoutScreen
    │
    └─ Presiona botón "Confirmar Pago $19.97"
       (opción Tarjeta seleccionada)
       │
       ├─ Datos ingresados:
       │  • Número: 4111 1111 1111 1111
       │  • Nombre: John Doe
       │  • MM/YY: 12/25
       │  • CVC: 123
       │
       └─ CheckoutViewModel.procesarPagoConTarjeta()
```

---

### PASO 2: Validación en ViewModel

```
CheckoutViewModel recibe el click
    │
    └─ Ejecuta validaciones locales:
       │
       ├─ ✅ Número tarjeta es válido (16 dígitos)
       ├─ ✅ Nombre no está vacío
       ├─ ✅ MM/YY tiene formato correcto (01-12)
       ├─ ✅ CVC es 3 dígitos
       ├─ ✅ Usuario está autenticado (Firebase)
       ├─ ✅ Carrito no está vacío
       └─ ✅ Total > $0
       │
       └─ Si alguna falla → mostrar error local, NO continuar
       └─ Si todas pasan → continuar a PASO 3
```

---

### PASO 3: Preparar Payload para Mock Backend

```
Si validaciones pasan:
    │
    └─ ViewModel prepara objeto:
       {
         "paymentMethod": "card",
         "cardData": {
           "number": "4111 1111 1111 1111",
           "holderName": "John Doe",
           "expiryDate": "12/25",
           "cvc": "123"
         },
         "amount": 1997,  // en cents ($19.97)
         "currency": "USD",
         "orderId": "ORD_${UUID}_${timestamp}",
         "userId": "${Firebase UID}",
         "items": [...carrito...],
         "timestamp": ${System.currentTimeMillis()}
       }
       │
       └─ Obtiene Firebase ID Token
          (para validar al usuario)
```

---

### PASO 4: Invocar Mock Payment Service (OFFLINE)

```
ViewModel.procesarPago(payload, firebaseToken)
    │
    └─ Invoca MockPaymentService.processCardPayment(...)
       
       MockPaymentService (SIMULADO, TODO LOCAL):
       │
       ├─ PASO 4A: Simula latencia de red
       │   └─ delay(1000 ms)  // Espera 1 segundo
       │
       ├─ PASO 4B: Valida tarjeta (mock)
       │   ├─ Si número es "4111 1111 1111 1111" → OK
       │   ├─ Si número empieza con "4000" → simula RECHAZADA
       │   └─ Otros números → simula error de conexión
       │
       ├─ PASO 4C: Genera transacción simulada
       │   ├─ chargeId: "ch_${random}"
       │   ├─ status: "succeeded"
       │   ├─ amount: 1997
       │   └─ created: ${timestamp}
       │
       └─ RETORNA:
          {
            "success": true,
            "chargeId": "ch_1234567890",
            "amount": 1997,
            "message": "Pago procesado exitosamente"
          }
```

---

### PASO 5: Guardar Orden en Firebase

```
Cuando Mock Service retorna "success": true
    │
    └─ ViewModel invoca:
       OrderRepository.createOrder(
         orderId: "ORD_ABC123_1718000000",
         userId: "uid123",
         items: [...],
         totalAmount: 19.97,
         paymentMethod: "card",
         chargeId: "ch_1234567890",
         status: "completed"
       )
       │
       └─ OrderRepository escribe en Firebase:
          /orders/{userId}/{orderId}
          {
            "orderId": "ORD_ABC123_1718000000",
            "userId": "uid123",
            "items": [
              {
                "productId": 5,
                "name": "Pollo Frito",
                "quantity": 2,
                "price": 7.99,
                "subtotal": 15.98
              },
              ...
            ],
            "totalAmount": 19.97,
            "paymentMethod": "card",
            "chargeId": "ch_1234567890",
            "status": "completed",
            "pointsEarned": 2,  // 10% de $19.97
            "pointsUsed": 0,
            "timestamp": 1718000000000,
            "deliveryAddress": "123 Main St"
          }
```

---

### PASO 6: Actualizar Puntos (SUMA)

```
Después de crear orden:
    │
    └─ ViewModel invoca:
       RewardsRepository.addPoints(
         userId: "uid123",
         pointsToAdd: 2,  // 10% de $19.97
         reason: "Purchase order_ABC123_1718000000"
       )
       │
       └─ RewardsRepository:
          1. Lee saldo actual de Firebase:
             /users/uid123/rewards/currentPoints
             └─ Valor actual: 500
             
          2. Calcula nuevo saldo:
             500 + 2 = 502
             
          3. Escribe en Firebase:
             /users/uid123/rewards
             {
               "currentPoints": 502,  ← ACTUALIZADO
               "totalPointsEarned": 1002,
               "level": "Regular",
               "lastTransactionDate": 1718000000000
             }
             
          4. Guarda transacción en historial:
             /pointTransactions/{transactionId}
             {
               "type": "earned",
               "points": 2,
               "reason": "Purchase order_ABC123",
               "orderId": "ORD_ABC123_1718000000",
               "timestamp": 1718000000000
             }
```

---

## 2.3 Flujo de Pago con Puntos

### PASO 1: Usuario Presiona "Confirmar Pago"

```
Usuario en CheckoutScreen
    │
    └─ Opción B (Puntos) está seleccionada
       └─ Presiona botón "Confirmar Pago"
          (Botón solo habilitado si tiene suficientes)
```

---

### PASO 2: Validación de Puntos

```
CheckoutViewModel.procesarPagoConPuntos()
    │
    └─ Valida:
       ├─ ✅ Usuario autenticado
       ├─ ✅ Saldo de puntos >= puntos_necesarios
       │   (500 >= 1997?)  → NO
       │
       └─ Si NO cumple → Error:
          "Puntos insuficientes: tienes 500, necesitas 1997"
          └─ Mostrar snackbar rojo
          └─ Botón vuelve a habilitarse
          └─ NO continuar
```

---

### PASO 3: (Si usuario sí tiene suficientes) Preparar Payload

```
Supongamos usuario tiene 3000 puntos:
    │
    └─ ViewModel prepara:
       {
         "paymentMethod": "points",
         "pointsToUse": 1997,
         "orderId": "ORD_XYZ789_1718000000",
         "userId": "uid123",
         "items": [...carrito...],
         "timestamp": ${timestamp}
       }
```

---

### PASO 4: Invocar Mock Payment Service

```
MockPaymentService.processPointsPayment(payload)
    │
    ├─ PASO 4A: Simula latencia
    │   └─ delay(800 ms)
    │
    ├─ PASO 4B: Valida puntos (mock)
    │   ├─ Lee puntos actuales de variable local: 3000
    │   ├─ Compara: 3000 >= 1997 → OK
    │   └─ Genera paymentId: "pmt_${random}"
    │
    └─ RETORNA:
       {
         "success": true,
         "paymentId": "pmt_98765",
         "pointsUsed": 1997,
         "remainingPoints": 1003,  // 3000 - 1997
         "message": "Puntos debitados exitosamente"
       }
```

---

### PASO 5: Crear Orden (igual que con tarjeta)

```
/orders/{userId}/{orderId}
{
  "orderId": "ORD_XYZ789_1718000000",
  "userId": "uid123",
  "items": [...],
  "totalAmount": 19.97,
  "paymentMethod": "points",
  "paymentId": "pmt_98765",
  "pointsUsed": 1997,
  "pointsEarned": 0,  ← NO gana puntos si paga con puntos
  "status": "completed",
  "timestamp": 1718000000000
}
```

---

### PASO 6: Deducir Puntos (RESTA)

```
RewardsRepository.deductPoints(
  userId: "uid123",
  pointsToDeduct: 1997,
  reason: "Purchase with points"
)
    │
    └─ 1. Lee saldo: 3000
    └─ 2. Calcula: 3000 - 1997 = 1003
    └─ 3. Escribe:
       /users/uid123/rewards
       {
         "currentPoints": 1003,  ← ACTUALIZADO (MENOS)
         "totalPointsSpent": 1997,
         "lastTransactionDate": 1718000000000
       }
    │
    └─ 4. Guarda transacción:
       /pointTransactions/{transactionId}
       {
         "type": "spent",
         "points": 1997,
         "reason": "Purchase with points order_XYZ",
         "orderId": "ORD_XYZ789_1718000000",
         "timestamp": 1718000000000
       }
```

---

## 2.4 Diferencias Clave: Tarjeta vs Puntos

```
┌─────────────────┬──────────────────┬──────────────────┐
│ ASPECTO         │ PAGO CON TARJETA │ PAGO CON PUNTOS  │
├─────────────────┼──────────────────┼──────────────────┤
│ Backend Mock    │ Valida tarjeta   │ Valida saldo     │
│                 │ (número 4111OK)  │ (suficientes?)   │
├─────────────────┼──────────────────┼──────────────────┤
│ Latencia        │ 1000 ms          │ 800 ms           │
│ Simulada        │ (más realista)   │ (más rápido)     │
├─────────────────┼──────────────────┼──────────────────┤
│ Operación en    │ CREATE           │ UPDATE (DEDUCT)  │
│ Puntos          │ (suma puntos)    │ (resta puntos)   │
├─────────────────┼──────────────────┼──────────────────┤
│ Puntos          │ +2 (10% del      │ 0 (ya los usó)   │
│ Ganados         │ monto)           │                  │
├─────────────────┼──────────────────┼──────────────────┤
│ Lógica de       │ Valida formato   │ Valida saldo >=  │
│ Validación      │ (16 dígitos, etc)│ necesarios       │
├─────────────────┼──────────────────┼──────────────────┤
│ Orden Creada    │ chargeId de      │ paymentId mock   │
│ Con            │ Stripe (mock)    │ generado local   │
└─────────────────┴──────────────────┴──────────────────┘
```

---

# PARTE 3: ACTUALIZACIÓN DE ESTADOS (Antes y Después)

## 3.1 Conceptos Clave de Estado

En Jetpack Compose con StateFlow, el estado se maneja así:

```
┌─────────────────────────────────┐
│     VIEWMODEL STATE             │
│  (Single Source of Truth)       │
└────────────┬────────────────────┘
             │
             ├─ Antes del pago:
             │  • currentPoints: 500
             │  • paymentStatus: IDLE
             │
             ├─ Durante pago:
             │  • paymentStatus: PROCESSING
             │  • isLoading: true
             │
             └─ Después del pago:
                • currentPoints: 502 (o 1003 si con puntos)
                • paymentStatus: SUCCESS
                • isLoading: false
                • orderData: {...}
```

---

## 3.2 Ciclo de Estado: Pago con Tarjeta

```
ESTADO 1: INITIAL (Before Payment)
┌────────────────────────────────┐
│ UI SHOWS:                      │
│ • Puntos mostrados: 500        │
│ • Carrito: $19.97              │
│ • Botón habilitado             │
│ • Sin mensajes                 │
│                                │
│ ViewModel Data:                │
│ {                              │
│   currentPoints: 500,          │
│   cartTotal: 1997,             │
│   paymentStatus: "IDLE",       │
│   isLoading: false,            │
│   error: null                  │
│ }                              │
└────────────────────────────────┘
    │ Usuario presiona "Pagar"
    ↓

ESTADO 2: PROCESSING (During Payment)
┌────────────────────────────────┐
│ UI SHOWS:                      │
│ • Spinner visible              │
│ • Mensaje: "Procesando..."     │
│ • Botón deshabilitado          │
│ • Inputs deshabilitados        │
│                                │
│ ViewModel Data:                │
│ {                              │
│   currentPoints: 500,  ← SIN CAMBIOS AÚN
│   cartTotal: 1997,             │
│   paymentStatus: "PROCESSING", │
│   isLoading: true,             │
│   error: null                  │
│ }                              │
└────────────────────────────────┘
    │ Mock backend responde (1-2 seg)
    │ • Orden creada ✅
    │ • Puntos sumados en Firebase ✅
    ↓

ESTADO 3: SUCCESS (After Payment - Immediate Update)
┌────────────────────────────────┐
│ ViewModel ACTUALIZA:           │
│ (Lee de Firebase)              │
│ {                              │
│   currentPoints: 502, ← ACTUALIZADO
│   cartTotal: 1997,             │
│   paymentStatus: "SUCCESS",    │
│   isLoading: false,            │
│   error: null,                 │
│   orderData: {                 │
│     orderId: "ORD_ABC123...",  │
│     chargeId: "ch_1234567890", │
│     pointsEarned: 2            │
│   }                            │
│ }                              │
│                                │
│ CheckoutScreen RECOMPONE:      │
│ • Desaparece spinne            │
│ • Navega a ConfirmationScreen  │
│   (con transición animada)     │
└────────────────────────────────┘
```

---

## 3.3 Ciclo de Estado: Pago con Puntos

```
ESTADO 1: INITIAL
┌────────────────────────────────┐
│ UI SHOWS:                      │
│ • Puntos mostrados: 3000       │
│ • Necesita: 1997               │
│ • Botón HABILITADO (verde)     │
│                                │
│ ViewModel Data:                │
│ {                              │
│   currentPoints: 3000,         │
│   pointsNeeded: 1997,          │
│   paymentStatus: "IDLE",       │
│   isLoading: false,            │
│   canPayWithPoints: true       │
│ }                              │
└────────────────────────────────┘
    │ Usuario presiona "Pagar"
    ↓

ESTADO 2: PROCESSING
┌────────────────────────────────┐
│ UI SHOWS:                      │
│ • Spinner                      │
│ • Mensaje: "Debitando puntos..."│
│ • Botón deshabilitado          │
│                                │
│ ViewModel Data:                │
│ {                              │
│   currentPoints: 3000, ← SIN CAMBIOS AÚN
│   paymentStatus: "PROCESSING", │
│   isLoading: true              │
│ }                              │
└────────────────────────────────┘
    │ Mock backend responde
    │ • 1997 puntos debitados ✅
    ↓

ESTADO 3: SUCCESS
┌────────────────────────────────┐
│ ViewModel ACTUALIZA:           │
│ (Lee de Firebase)              │
│ {                              │
│   currentPoints: 1003, ← ACTUALIZADO (MENOS)
│   pointsUsed: 1997,            │
│   paymentStatus: "SUCCESS",    │
│   isLoading: false,            │
│   orderData: {                 │
│     orderId: "ORD_XYZ789...",  │
│     paymentId: "pmt_98765",    │
│     pointsUsed: 1997           │
│   }                            │
│ }                              │
│                                │
│ CheckoutScreen RECOMPONE:      │
│ • Navega a ConfirmationScreen  │
└────────────────────────────────┘
```

---

## 3.4 Manejo de Errores en Estados

```
ESTADO: ERROR
┌────────────────────────────────┐
│ ESCENARIO 1: Tarjeta Rechazada │
├────────────────────────────────┤
│ ViewModel Data:                │
│ {                              │
│   currentPoints: 500, ← SIN CAMBIOS
│   paymentStatus: "ERROR",      │
│   error: "Tarjeta rechazada",  │
│   isLoading: false             │
│ }                              │
│                                │
│ UI SHOWS:                      │
│ • Snackbar rojo                │
│ • Mensaje: "Tarjeta rechazada" │
│ • Botón rehabilitado           │
│ • Inputs rehabilitados         │
│ • Datos se conservan (no se    │
│   limpian)                     │
└────────────────────────────────┘

┌────────────────────────────────┐
│ ESCENARIO 2: Puntos            │
│ Insuficientes                  │
├────────────────────────────────┤
│ (Error ocurre ANTES de procesar│
│  porque botón está deshabilitado)
│                                │
│ Pero si por alguna razón ocurre│
│ durante procesamiento:         │
│                                │
│ ViewModel Data:                │
│ {                              │
│   currentPoints: 3000, ← SIN CAMBIOS
│   paymentStatus: "ERROR",      │
│   error: "Saldo insuficiente", │
│   isLoading: false             │
│ }                              │
│                                │
│ UI SHOWS:                      │
│ • Snackbar: "Puntos           │
│   insuficientes"               │
│ • Botón deshabilitado (sigue   │
│   deshabilitado)               │
│ • Usuario NO puede reintentar  │
└────────────────────────────────┘
```

---

## 3.5 Flujo de Actualización en Tiempo Real

```
┌──────────────────────────────────────────────────┐
│ Firebase Real-time Listener Activo               │
│ (Escucha cambios en /users/{uid}/rewards)        │
└────────────────┬─────────────────────────────────┘
                 │
    ┌────────────┴─────────────┐
    │                          │
    ↓ Antes del pago          ↓ Después del pago
    
    currentPoints = 500        currentPoints = 502
    totalEarned = 500         totalEarned = 502
    level = "Regular"         level = "Regular"
    
                    │
                    └─ Firebase listener detecta cambio
                       │
                       └─ Emite nuevo valor a StateFlow
                          │
                          └─ CheckoutViewModel.currentPoints.value = 502
                             │
                             └─ UI se recompone automáticamente
                                │
                                └─ ConfirmationScreen muestra:
                                   • Antes: 500
                                   • Ganados: +2
                                   • Después: 502
```

---

# PARTE 4: CONFIRMACIÓN Y VALIDACIÓN

## 4.1 Identificadores Necesarios

Para asegurar que la compra se procesó correctamente, se generan estos IDs:

```
1. ORDER ID
   ├─ Formato: "ORD_${UUID}_${timestamp}"
   ├─ Ejemplo: "ORD_a1b2c3d4-e5f6-4g7h_1718000000"
   ├─ Generado: En ViewModel ANTES de enviar al backend
   ├─ Propósito: Identificar unívocamente cada compra
   └─ Almacenado en: Firebase /orders/{userId}/{orderId}

2. CHARGE ID (solo si pago con tarjeta)
   ├─ Formato: "ch_${random}" (mock)
   ├─ Ejemplo: "ch_1234567890"
   ├─ Generado: En MockPaymentService
   ├─ Propósito: Referencia del "cargo" (simulado)
   └─ Almacenado en: Orden en Firebase

3. PAYMENT ID (solo si pago con puntos)
   ├─ Formato: "pmt_${random}"
   ├─ Ejemplo: "pmt_98765"
   ├─ Generado: En MockPaymentService
   ├─ Propósito: Referencia del pago con puntos
   └─ Almacenado en: Orden en Firebase

4. TRANSACTION ID (para historial de puntos)
   ├─ Formato: "txn_${UUID}_${timestamp}"
   ├─ Ejemplo: "txn_9z8y7x6w_1718000000"
   ├─ Generado: Automáticamente por Firebase
   ├─ Propósito: Auditoría de puntos
   └─ Almacenado en: /pointTransactions/{transactionId}
```

---

## 4.2 Validación Antes de Mostrar Éxito

```
CHECKLIST que debe cumplirse ANTES de navegar
a ConfirmationScreen:

1. ✅ Mock backend devuelve "success": true
   └─ Si false → mostrar error y NO continuar

2. ✅ Orden se guardó en Firebase
   └─ Verificar: /orders/{uid}/{orderId} existe

3. ✅ Puntos se actualizaron en Firebase
   └─ Verificar: /users/{uid}/rewards/currentPoints
      fue actualizado

4. ✅ Firebase listener detectó el cambio
   └─ El ViewModel recibió nuevo valor de points

5. ✅ UI State es SUCCESS
   └─ paymentStatus == "SUCCESS"
   └─ isLoading == false
   └─ error == null

6. ✅ OrderData tiene toda la información
   └─ orderId: válido
   └─ totalAmount: > 0
   └─ status: "completed"
```

---

## 4.3 Flujo de Confirmación Visual

```
POST-PAGO: ConfirmationScreen

┌──────────────────────────────────────────┐
│                                          │
│              ✅ ¡ÉXITO!                 │
│                                          │
├──────────────────────────────────────────┤
│                                          │
│ Número de Orden:                         │
│ #ORD_a1b2c3d4_1718000000                │
│                                          │
├──────────────────────────────────────────┤
│                                          │
│ RESUMEN DE COMPRA                        │
│ ────────────────────────────            │
│ • Pollo Frito x2       $15.98           │
│ • Papas x1             $3.99            │
│ ────────────────────────────            │
│ TOTAL:                 $19.97           │
│                                          │
├──────────────────────────────────────────┤
│                                          │
│ MÉTODO DE PAGO:                          │
│ ✅ Tarjeta (****1111)                   │
│    O                                     │
│ ✅ Puntos                                │
│                                          │
├──────────────────────────────────────────┤
│                                          │
│ MIS PUNTOS:                              │
│                                          │
│ Saldo Anterior:    500 puntos            │
│ + Ganados:         2 puntos              │
│ ─────────────────────────────            │
│ Saldo Actual:      502 puntos            │
│ (Equivalencia: $5.02)                   │
│                                          │
│ O (si pagó con puntos):                 │
│                                          │
│ Saldo Anterior:    3000 puntos           │
│ - Gastados:        1997 puntos           │
│ ─────────────────────────────            │
│ Saldo Actual:      1003 puntos           │
│ (Equivalencia: $10.03)                  │
│                                          │
├──────────────────────────────────────────┤
│                                          │
│ [Volver al Inicio]   [Ver Orden]        │
│                                          │
└──────────────────────────────────────────┘
```

---

## 4.4 Persistencia de Datos

```
Después de cerrar y abrir la app:

1. User abre CartActivity nuevamente
2. ManagementCart.getCart()
   └─ Retorna: [] (carrito vacío)
   └─ Razón: se limpió después del pago

3. User abre PointsCard
4. RewardsViewModel carga puntos de Firebase
   └─ Lee: /users/{uid}/rewards/currentPoints
   └─ Muestra: 502 (o 1003 si fue puntos)
   └─ ✅ PERSISTE correctamente

5. User abre historial de órdenes
6. Puede ver la orden creada
   └─ Lee: /orders/{uid}/{orderId}
   └─ Muestra: ORD_a1b2c3d4_1718000000
   └─ ✅ PERSISTE correctamente

CONCLUSIÓN: Todo se guarda en Firebase,
persiste entre sesiones ✅
```

---

## 4.5 Resumen: Ciclo Completo

```
START
  │
  ├─ Usuario ve CheckoutScreen
  │  └─ Datos: Carrito $19.97, Puntos 500
  │
  ├─ Selecciona método de pago
  │  ├─ Opción A: Tarjeta
  │  └─ Opción B: Puntos
  │
  ├─ Presiona "Confirmar Pago"
  │
  ├─ ViewModel valida
  │  └─ Genera OrderID: ORD_a1b2c3d4_1718000000
  │
  ├─ Invoca MockPaymentService
  │  └─ Simula pago: 1-2 segundos
  │  └─ Retorna: success: true
  │
  ├─ Crea orden en Firebase
  │  └─ /orders/uid123/ORD_a1b2c3d4_1718000000
  │  └─ Status: "completed"
  │
  ├─ Actualiza puntos
  │  ├─ Si tarjeta: +2 puntos → 502
  │  └─ Si puntos: -1997 puntos → 1003
  │
  ├─ Firebase listener detecta cambio
  │  └─ ViewModel.currentPoints.value actualiza
  │
  ├─ UI se recompone
  │
  └─ Navega a ConfirmationScreen
     └─ Muestra: Orden #, Total, Puntos Antes/Después
     └─ Usuario ve éxito ✅
END
```

---

## 4.6 Validaciones de Integridad

```
ANTES DE HACER VISIBLE EL ÉXITO:

1. Firebase escribió la orden?
   ├─ Query: Firebase.child("orders")
   │          .child(uid)
   │          .child(orderId)
   │          .addListenerForSingleValueEvent()
   └─ Si no existe → Mostrar error (retry)

2. Puntos fueron actualizados?
   ├─ Leer: Firebase.child("users")
   │         .child(uid)
   │         .child("rewards")
   │         .child("currentPoints")
   └─ Comparar con UI state esperado

3. Órdenes se pueden leer desde historial?
   ├─ Query: Firebase.child("orders")
   │          .child(uid)
   │          .orderByChild("timestamp")
   │          .limitToLast(1)
   └─ Si ORD_a1b2c3d4 está en lista → OK

4. Transacción de puntos se creó?
   ├─ (Solo para auditoría)
   ├─ Query: Firebase.child("pointTransactions")
   │          .orderByChild("orderId")
   │          .equalTo("ORD_a1b2c3d4")
   └─ Si existe → OK

Si TODAS pasan → Mostrar éxito
Si ALGUNA falla → Mostrar error y permitir retry
```

---

# RESUMEN EJECUTIVO

## Componentes Principales

```
┌─────────────────────────────────────────────────┐
│ 1. CheckoutScreen (Compose UI)                  │
│    • Muestra carrito, puntos, opciones de pago  │
│                                                 │
│ 2. CheckoutViewModel (Lógica de negocio)       │
│    • Valida, orquesta, maneja estados          │
│                                                 │
│ 3. MockPaymentService (Backend local)           │
│    • Simula procesamiento de pagos (1-2 seg)   │
│                                                 │
│ 4. Firebase (Database)                          │
│    • Almacena órdenes, transacciones, puntos   │
│                                                 │
│ 5. ConfirmationScreen (Compose UI)              │
│    • Muestra éxito con puntos antes/después    │
└─────────────────────────────────────────────────┘
```

## Flujo Principal

```
CheckoutScreen → Selecciona método → Valida datos 
  → MockPaymentService (simula 1-2 seg)
  → Firebase (crea orden + actualiza puntos)
  → ConfirmationScreen (muestra éxito)
```

## Estados Principales

```
INITIAL → PROCESSING → SUCCESS → ConfirmationScreen
                   ↓
               ERROR → Snackbar rojo → Vuelve a INITIAL
```

## Puntos Clave

```
✅ TODO es LOCAL (sin conexión real a Stripe)
✅ Almacenamiento persiste en Firebase
✅ UI se actualiza automáticamente via StateFlow
✅ Puntos ANTES/DESPUÉS se muestran claramente
✅ Validaciones en 2 capas (UI + ViewModel)
✅ Órdenes tienen IDs únicos para auditoría
```

---

**Documento**: ARQUITECTURA_DETALLADA.md  
**Versión**: 1.0 (Conceptual)  
**Estado**: Listo para implementar sin código  
**Última actualización**: 12 de Junio, 2026

