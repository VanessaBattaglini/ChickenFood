# 📅 Etapas de Trabajo - Plan de Implementación

**Basado en**: ARQUITECTURA_DETALLADA.md  
**Versión**: 1.0  
**Duración Total**: 10-12 días de desarrollo  
**Metodología**: Desarrollo por capas (UI → Lógica → Data)

---

## 🎯 Visión General

```
ETAPA 1: Setup (1-2 días)
  └─ Crear modelos, helpers, servicios base

ETAPA 2: UI Foundation (2-3 días)
  └─ Crear CheckoutScreen + ConfirmationScreen

ETAPA 3: ViewModel & Lógica (2-3 días)
  └─ CheckoutViewModel + MockPaymentService

ETAPA 4: Integración Firebase (1-2 días)
  └─ Crear órdenes + actualizar puntos

ETAPA 5: Testing & Refinamiento (1-2 días)
  └─ Flujos completos, errores, edge cases

TOTAL: 10-12 días
```

---

# ETAPA 1: SETUP (Día 1-2)

## Objetivo
Preparar la infraestructura: modelos, helpers, servicios, navegación.

## Tareas

### 1.1 Crear Modelos de Datos

**Qué**: Clases de datos para órdenes, transacciones, pagos

**Archivos a crear**:
- `domain/model/OrderModel.kt` - Estructura completa de una orden
- `domain/model/PaymentModel.kt` - Datos de pago (tarjeta o puntos)
- `domain/model/TransactionModel.kt` - Transacción de puntos
- `domain/model/CheckoutState.kt` - Estado del checkout

**Contenido (sin código)**:
```
OrderModel:
  ├─ orderId: String
  ├─ userId: String
  ├─ items: List<CartItem>
  ├─ totalAmount: Double
  ├─ paymentMethod: String (card/points)
  ├─ chargeId: String?
  ├─ pointsUsed: Int
  ├─ pointsEarned: Int
  ├─ status: String
  └─ timestamp: Long

PaymentModel:
  ├─ method: String (card/points)
  ├─ cardNumber: String? (solo si tarjeta)
  ├─ cardHolder: String?
  ├─ expiryDate: String?
  ├─ cvc: String?
  ├─ pointsToUse: Int? (solo si puntos)
  └─ amount: Double

TransactionModel:
  ├─ transactionId: String
  ├─ userId: String
  ├─ type: String (earned/spent)
  ├─ points: Int
  ├─ reason: String
  ├─ orderId: String
  └─ timestamp: Long

CheckoutState:
  ├─ cartItems: List<CartItem>
  ├─ cartTotal: Double
  ├─ userPoints: Int
  ├─ selectedPaymentMethod: String
  ├─ isLoading: Boolean
  ├─ error: String?
  ├─ paymentStatus: String (IDLE/PROCESSING/SUCCESS/ERROR)
  └─ orderData: OrderModel?
```

**Dependencias**: Ninguna (models puros)

**Tiempo**: 1-2 horas

---

### 1.2 Crear Repositorio para Órdenes

**Qué**: Interface + implementación para guardar/leer órdenes de Firebase

**Archivos a crear**:
- `domain/repository/OrderRepository.kt` - Interface
- `data/repository/OrderRepositoryImpl.kt` - Implementación

**Responsabilidades**:
- `createOrder(orderData: OrderModel): Flow<Result>`
- `getOrderHistory(userId: String): Flow<List<OrderModel>>`
- `getOrderById(userId: String, orderId: String): Flow<OrderModel>`

**Patrón**: Flow + Coroutines (ya lo haces en tu app)

**Dependencias**: Firebase Realtime Database (ya existe)

**Tiempo**: 1-2 horas

---

### 1.3 Crear Repositorio para Transacciones de Puntos

**Qué**: Guardar historial de ganancias/gastos de puntos

**Archivos a crear**:
- `domain/repository/PointTransactionRepository.kt` - Interface
- `data/repository/PointTransactionRepositoryImpl.kt` - Implementación

**Responsabilidades**:
- `recordTransaction(transactionData: TransactionModel): Flow<Result>`
- `getTransactionHistory(userId: String): Flow<List<TransactionModel>>`

**Patrón**: Flow + Coroutines

**Dependencias**: Firebase

**Tiempo**: 1-2 horas

---

### 1.4 Extender RewardsRepository

**Qué**: Agregar métodos para sumar/restar puntos

**Archivos a modificar**:
- `domain/repository/RewardsRepository.kt` - Agregar métodos
- `data/repository/RewardsRepositoryImpl.kt` - Implementar

**Nuevos métodos**:
- `addPoints(userId: String, points: Int, reason: String): Flow<Result>`
- `deductPoints(userId: String, points: Int, reason: String): Flow<Result>`
- `getCurrentPoints(userId: String): Flow<Int>`

**Nota**: Ya tienes `loadUserRewards()`, agregar estos métodos

**Tiempo**: 1 hora

---

### 1.5 Crear MockPaymentService

**Qué**: Servicio local que simula procesamiento de pagos

**Archivos a crear**:
- `data/service/MockPaymentService.kt` - Interface
- `data/service/MockPaymentServiceImpl.kt` - Implementación

**Responsabilidades**:
- `processCardPayment(cardData, amount): Flow<MockPaymentResult>`
- `processPointsPayment(pointsAmount): Flow<MockPaymentResult>`

**Comportamiento simulado**:
- Delay de 1-2 segundos (simula latencia de red)
- Valida número de tarjeta (4111 = OK, 4000 = RECHAZADA)
- Valida puntos suficientes
- Retorna: `{ success: Boolean, chargeId: String?, error: String? }`

**Patrón**: Suspendable functions + Delay

**Tiempo**: 2 horas

---

### 1.6 Crear Navegación a CheckoutScreen

**Qué**: Agregar ruta de navegación desde CartActivity a CheckoutActivity

**Archivos a modificar**:
- `presentation/activity/cart/CartActivity.kt` - Agregar botón + intento
- `presentation/activity/checkout/CheckoutActivity.kt` - Crear (vacío por ahora)
- `AndroidManifest.xml` - Registrar activity

**Navegación**:
```
CartActivity
  ├─ Botón "Proceder al Pago"
  └─ onClick → Intent(this, CheckoutActivity::class.java)
     ├─ Pasa datos del carrito como extras
     └─ CheckoutActivity recibe y procesa
```

**Tiempo**: 1 hora

---

### 1.7 Registrar Dependencias en Koin

**Qué**: Agregar inyección de dependencias para nuevos repositorios y servicios

**Archivos a modificar**:
- `di/AppModule.kt` - Agregar bindings

**Bindings a agregar**:
```
// Repositorios
single<OrderRepository> { OrderRepositoryImpl(get(), get()) }
single<PointTransactionRepository> { PointTransactionRepositoryImpl(get(), get()) }

// Servicios
single<MockPaymentService> { MockPaymentServiceImpl() }

// ViewModels
viewModel { CheckoutViewModel(
    orderRepository = get(),
    rewardsRepository = get(),
    mockPaymentService = get()
) }
```

**Tiempo**: 30 minutos

---

## ETAPA 1: Checklist Completado

```
☐ OrderModel.kt creado
☐ PaymentModel.kt creado
☐ TransactionModel.kt creado
☐ CheckoutState.kt creado
☐ OrderRepository (interface + impl)
☐ PointTransactionRepository (interface + impl)
☐ RewardsRepository extendido (addPoints, deductPoints)
☐ MockPaymentService creado
☐ CheckoutActivity creado (vacío)
☐ Navegación desde CartActivity implementada
☐ Koin bindings actualizados
☐ Proyecto compila sin errores
```

**Tiempo Total Etapa 1**: 10-12 horas

---

# ETAPA 2: UI FOUNDATION (Día 3-4)

## Objetivo
Crear las pantallas visuales: CheckoutScreen y ConfirmationScreen

## Tareas

### 2.1 Crear CheckoutScreen (Composable)

**Qué**: Pantalla principal de checkout con Compose

**Archivos a crear**:
- `presentation/activity/checkout/CheckoutScreen.kt` - Composable principal

**Estructura**:
```
CheckoutScreen()
├─ Header
│  ├─ Botón atrás
│  ├─ Título "Confirmar Compra"
│  └─ Botón info
│
├─ Section 1: Resumen del Carrito
│  ├─ LazyColumn de items
│  ├─ Subtotal por item
│  └─ TOTAL grande
│
├─ Section 2: Información de Puntos
│  ├─ Saldo actual (grande)
│  ├─ Equivalencia en dinero
│  ├─ Nivel y barra de progreso
│  └─ Puntos necesarios
│
├─ Section 3: Selector de Método de Pago
│  ├─ RadioButton "Tarjeta"
│  │  ├─ Inputs de tarjeta (condicional)
│  │  └─ Validación en tiempo real
│  │
│  └─ RadioButton "Puntos"
│     ├─ Texto de validación
│     └─ Botón habilitado/deshabilitado
│
└─ Bottom: Botones
   ├─ "Cancelar"
   └─ "Confirmar Pago"
```

**Componentes secundarios a crear**:
- `presentation/activity/checkout/CartSummaryCard.kt` - Resumen carrito
- `presentation/activity/checkout/PointsInfoCard.kt` - Info de puntos
- `presentation/activity/checkout/PaymentMethodSelector.kt` - Selector
- `presentation/activity/checkout/CardInputForm.kt` - Inputs de tarjeta

**Validaciones UI**:
- Número tarjeta: validación en tiempo real (16 dígitos)
- Nombre: no vacío
- MM/YY: formato correcto
- CVC: 3 dígitos
- Puntos: validación de saldo

**Tiempo**: 4-5 horas

---

### 2.2 Crear Estados Visuales

**Qué**: Diferentes apariencias según estado

**Estados a implementar**:
```
Estado 1: INITIAL
├─ Todo habilitado
└─ Tarjeta seleccionada por defecto

Estado 2: LOADING
├─ Spinner visible
├─ Botones deshabilitados
├─ Inputs deshabilitados
└─ Mensaje "Procesando..."

Estado 3: SUCCESS
├─ Navega a ConfirmationScreen
└─ Animación de transición

Estado 4: ERROR
├─ Snackbar rojo
├─ Botones rehabilitados
└─ Mensaje de error específico
```

**Implementación**: Condicionales basados en `CheckoutState`

**Tiempo**: 1-2 horas

---

### 2.3 Crear ConfirmationScreen (Composable)

**Qué**: Pantalla de éxito con detalles de la compra

**Archivos a crear**:
- `presentation/activity/checkout/ConfirmationScreen.kt` - Composable

**Estructura**:
```
ConfirmationScreen()
├─ Header
│  └─ ✅ ¡ÉXITO! (animado)
│
├─ Número de Orden
│  └─ Texto grande: #ORD_abc123_1718000000
│
├─ Card: Resumen de Compra
│  ├─ Items
│  ├─ Total
│  └─ Método de pago
│
├─ Card: Información de Puntos
│  ├─ Saldo anterior
│  ├─ Cambio (+ o -)
│  ├─ Saldo actual
│  └─ Equivalencia en dinero
│
└─ Botones
   ├─ "Volver al Inicio"
   └─ "Ver Detalle de Orden"
```

**Animaciones**:
- Entrada: Slide-in from bottom (con duración 300ms)
- Icono: Rotación del ✅ (pequeña animación)

**Componentes secundarios**:
- `presentation/activity/checkout/OrderSummaryCard.kt`
- `presentation/activity/checkout/PointsSummaryCard.kt`

**Tiempo**: 2-3 horas

---

### 2.4 Crear CardInputForm Reutilizable

**Qué**: Componente de inputs de tarjeta con validación

**Archivos a crear**:
- `presentation/activity/checkout/CardInputForm.kt`

**Campos**:
- Número de tarjeta (16 dígitos)
- Nombre del titular
- Mes/Año (MM/YY)
- CVC (3 dígitos)

**Validaciones visuales**:
- Input válido: verde + icono ✓
- Input inválido: rojo + icono ✗
- Feedback en tiempo real (sin delay)

**Retorno**: `CardData` object con campos validados

**Tiempo**: 2 horas

---

### 2.5 Crear Componentes de Información

**Qué**: Cards reutilizables para mostrar información

**Archivos a crear**:
- `presentation/activity/checkout/InfoCards.kt`

**Componentes**:
```
PointsCard()
├─ Saldo (número grande)
├─ Nivel (icono + nombre)
├─ Barra de progreso
└─ Información adicional

SummaryCard()
├─ Título
├─ Items (LazyColumn)
└─ Total

MethodCard()
├─ Icono método
├─ Descripción
└─ Detalles específicos
```

**Reutilización**: Se usan en CheckoutScreen y ConfirmationScreen

**Tiempo**: 1.5 horas

---

## ETAPA 2: Checklist Completado

```
☐ CheckoutScreen creado
☐ ConfirmationScreen creado
☐ CartSummaryCard componente
☐ PointsInfoCard componente
☐ PaymentMethodSelector componente
☐ CardInputForm componente
☐ Estados visuales implementados
☐ Validaciones visuales funcionando
☐ Animaciones implementadas
☐ Componentes reutilizables extraídos
☐ Navegación CheckoutScreen ↔ CartActivity
☐ Navegación CheckoutScreen → ConfirmationScreen
☐ Preview de Composables funcionales
```

**Tiempo Total Etapa 2**: 14-18 horas

---

# ETAPA 3: VIEWMODEL & LÓGICA (Día 5-6)

## Objetivo
Implementar CheckoutViewModel y conectar UI con lógica de negocio

## Tareas

### 3.1 Crear CheckoutViewModel

**Qué**: ViewModel con lógica central del checkout

**Archivos a crear**:
- `presentation/viewModel/CheckoutViewModel.kt`

**Responsabilidades principales**:
```
Estados (StateFlow):
├─ checkoutState: StateFlow<CheckoutState>
├─ isLoading: StateFlow<Boolean>
├─ error: StateFlow<String?>
└─ selectedPaymentMethod: StateFlow<String>

Métodos:
├─ loadCheckoutData(cartItems)
├─ selectPaymentMethod(method: String)
├─ validateCardData(card: CardData): Boolean
├─ validatePoints(pointsNeeded: Int): Boolean
├─ processPayment()
├─ clearError()
└─ navigateToConfirmation()
```

**Inicialización**:
- Recibe CartItems del Intent
- Carga puntos del usuario desde RewardsRepository
- Calcula monto total
- Prepara estado inicial

**Tiempo**: 3-4 horas

---

### 3.2 Implementar Lógica de Validación

**Qué**: Validaciones que ocurren en el ViewModel

**Validaciones a implementar**:

**Para Tarjeta**:
```
validateCardData(cardData):
├─ Número: 16 dígitos exactos
├─ Nombre: no vacío + min 2 caracteres
├─ MM/YY: formato correcto (01-12 para mes)
├─ CVC: 3 dígitos
└─ Retorna: Boolean (true si todas pasan)
```

**Para Puntos**:
```
validatePoints(puntosPorUsar):
├─ Obtener saldo actual de RewardsViewModel
├─ Validar: saldo >= puntos_necesarios
├─ Retorna: { canPay: Boolean, needMore: Int }
```

**Métodos helper**:
- `calculatePointsNeeded(amount: Double): Int` → amount * 100
- `calculatePointsEarned(amount: Double): Int` → (amount * 100) * 0.10
- `generateOrderId(): String` → "ORD_${UUID}_${timestamp}"

**Tiempo**: 2 horas

---

### 3.3 Implementar Flujo de Procesamiento de Pago

**Qué**: Orquestación del proceso completo

**Métodos a implementar**:

```
processPayment():
├─ Validar datos
├─ Mostrar loading (isLoading = true)
├─ Si Tarjeta:
│  └─ procesarPagoConTarjeta()
├─ Si Puntos:
│  └─ procesarPagoConPuntos()
├─ Esperar respuesta (1-2 segundos)
├─ Si éxito:
│  ├─ Crear orden
│  ├─ Actualizar puntos
│  ├─ Limpiar carrito
│  └─ paymentStatus = SUCCESS
└─ Si error:
   ├─ Mostrar mensaje
   └─ paymentStatus = ERROR

procesarPagoConTarjeta():
├─ Preparar payload
├─ Invocar MockPaymentService.processCardPayment()
├─ Esperar resultado
└─ Retornar: { success, chargeId, error }

procesarPagoConPuntos():
├─ Preparar payload
├─ Invocar MockPaymentService.processPointsPayment()
├─ Esperar resultado
└─ Retornar: { success, pointsUsed, error }
```

**Manejo de errores**:
- Capturar excepciones en try-catch
- Mostrar mensaje amigable al usuario
- Permitir reintentar (no limpiar datos ingresados)

**Tiempo**: 3 horas

---

### 3.4 Implementar MockPaymentService

**Qué**: Simular procesamiento de pagos localmente

**Métodos a implementar**:

```
processCardPayment(
  cardData: CardData,
  amount: Double,
  orderId: String
): Flow<PaymentResult>

Lógica:
├─ delay(1000) // Simular latencia
├─ Validar número tarjeta:
│  ├─ Si "4111 1111 1111 1111" → OK
│  ├─ Si empieza con "4000" → RECHAZADA
│  └─ Otros → OK (por simplicidad)
├─ Validar nombre no vacío
├─ Generar chargeId: "ch_${random}"
├─ Retornar:
│  {
│    success: true/false,
│    chargeId: "ch_XXXXX",
│    message: "Pago procesado"
│  }
```

```
processPointsPayment(
  pointsToUse: Int,
  currentPoints: Int,
  orderId: String
): Flow<PaymentResult>

Lógica:
├─ delay(800) // Simular latencia (más rápido)
├─ Validar: currentPoints >= pointsToUse
├─ Generar paymentId: "pmt_${random}"
├─ Retornar:
│  {
│    success: true/false,
│    paymentId: "pmt_XXXXX",
│    remainingPoints: currentPoints - pointsToUse
│  }
```

**Implementación**: Sin corrutinas complejas, usar `flow { emit() }`

**Tiempo**: 2 horas

---

### 3.5 Conectar ViewModel a CheckoutScreen

**Qué**: Pasar datos del ViewModel a la UI

**Cambios en CheckoutScreen**:
```
CheckoutScreen(
  viewModel: CheckoutViewModel = hiltViewModel()
) {
  val checkoutState by viewModel.checkoutState.collectAsState()
  val isLoading by viewModel.isLoading.collectAsState()
  val error by viewModel.error.collectAsState()
  
  // Observar cambios
  LaunchedEffect(checkoutState.paymentStatus) {
    if (checkoutState.paymentStatus == "SUCCESS") {
      // Navegar a ConfirmationScreen
    }
  }
  
  // Pasar callbacks
  PaymentMethodSelector(
    selected = checkoutState.selectedPaymentMethod,
    onSelect = { viewModel.selectPaymentMethod(it) }
  )
  
  Button("Confirmar Pago") {
    viewModel.processPayment()
  }
}
```

**Comunicación**:
- UI observa StateFlows del ViewModel
- UI dispara eventos (callbacks) al ViewModel
- ViewModel actualiza state
- Compose recompone automáticamente

**Tiempo**: 1.5 horas

---

## ETAPA 3: Checklist Completado

```
☐ CheckoutViewModel creado
☐ Métodos de validación implementados
☐ Método processPayment() implementado
☐ procesarPagoConTarjeta() funcionando
☐ procesarPagoConPuntos() funcionando
☐ MockPaymentService completamente implementado
☐ Delays simulados configurados (1s y 800ms)
☐ Generación de IDs (OrderID, ChargeID, PaymentID)
☐ CheckoutScreen conectada al ViewModel
☐ Estados se actualizan correctamente
☐ Errores se muestran en UI
☐ Transición a ConfirmationScreen al éxito
```

**Tiempo Total Etapa 3**: 15-17 horas

---

# ETAPA 4: INTEGRACIÓN FIREBASE (Día 7-8)

## Objetivo
Guardar órdenes y actualizar puntos en Firebase

## Tareas

### 4.1 Implementar Creación de Órdenes

**Qué**: Guardar orden completa en Firebase

**En OrderRepositoryImpl**:
```
createOrder(orderData: OrderModel): Flow<Result<String>>
├─ Validar que orderData tenga campos requeridos
├─ Preparar documento:
│  {
│    orderId: "ORD_ABC123_1718000000",
│    userId: "uid123",
│    items: [...],
│    totalAmount: 19.97,
│    paymentMethod: "card",
│    chargeId: "ch_1234567890",
│    pointsUsed: 0,
│    pointsEarned: 2,
│    status: "completed",
│    timestamp: 1718000000000
│  }
├─ Escribir en Firebase:
│  /orders/{userId}/{orderId}
├─ Si éxito: Flow.emit(Result.Success(orderId))
└─ Si error: Flow.emit(Result.Error(exception))
```

**Patrón**: Flow + Firebase + try-catch

**Tiempo**: 1.5 horas

---

### 4.2 Implementar Suma de Puntos

**Qué**: Agregar puntos cuando paga con tarjeta

**En RewardsRepositoryImpl**:
```
addPoints(
  userId: String,
  points: Int,
  reason: String
): Flow<Result<Int>>

Lógica:
├─ Leer saldo actual:
│  /users/{userId}/rewards/currentPoints
├─ Calcular nuevo saldo:
│  newBalance = current + points
├─ Actualizar en Firebase:
│  /users/{userId}/rewards
│  { currentPoints: newBalance, ... }
├─ Guardar transacción:
│  /pointTransactions/{transactionId}
│  {
│    type: "earned",
│    points: points,
│    reason: reason,
│    timestamp: now()
│  }
└─ Retornar nuevo saldo
```

**Validaciones**:
- points > 0
- userId válido
- Validar que transacción se guardó

**Tiempo**: 1.5 horas

---

### 4.3 Implementar Resta de Puntos

**Qué**: Deducir puntos cuando paga con puntos

**En RewardsRepositoryImpl**:
```
deductPoints(
  userId: String,
  points: Int,
  reason: String
): Flow<Result<Int>>

Lógica:
├─ Leer saldo actual
├─ Validar: current >= points (si falla → error)
├─ Calcular: newBalance = current - points
├─ Actualizar en Firebase
├─ Guardar transacción:
│  {
│    type: "spent",
│    points: points,
│    reason: reason
│  }
└─ Retornar nuevo saldo
```

**Diferencia con suma**: Validación de saldo insuficiente

**Tiempo**: 1.5 horas

---

### 4.4 Implementar Historial de Órdenes

**Qué**: Poder leer órdenes previas del usuario

**En OrderRepositoryImpl**:
```
getOrderHistory(userId: String): Flow<List<OrderModel>>

Lógica:
├─ Query Firebase:
│  /orders/{userId}
│  .orderByChild("timestamp")
│  .limitToLast(50)
├─ Mapear documentos a OrderModel
├─ Retornar lista ordenada (más reciente primero)
```

**Orden**: Descending por timestamp

**Tiempo**: 1 hora

---

### 4.5 Implementar Historial de Transacciones

**Qué**: Poder leer historial de ganancia/gasto de puntos

**En PointTransactionRepositoryImpl**:
```
getTransactionHistory(userId: String): Flow<List<TransactionModel>>

Lógica:
├─ Query Firebase:
│  /pointTransactions
│  .orderByChild("userId")
│  .equalTo(userId)
│  .limitToLast(100)
├─ Mapear a TransactionModel
├─ Ordenar por timestamp descending
└─ Retornar lista
```

**Nota**: Las transacciones pueden estar bajo path global, no por usuario

**Tiempo**: 1 hora

---

### 4.6 Conectar ViewModel a Repositorios

**Qué**: Que CheckoutViewModel invoque los repositorios correctamente

**En CheckoutViewModel**:
```
Después de pago exitoso:

Si Tarjeta:
  ├─ orderRepository.createOrder(orderData)
  ├─ rewardsRepository.addPoints(userId, pointsEarned, "Purchase")
  └─ clearCart()

Si Puntos:
  ├─ orderRepository.createOrder(orderData)
  ├─ rewardsRepository.deductPoints(userId, pointsUsed, "Purchase")
  └─ clearCart()

Observar cambios:
  └─ rewardsRepository.getCurrentPoints(userId)
     ├─ Actualizar UI con nuevo saldo
     └─ Navegar a ConfirmationScreen
```

**Manejo de errores**:
- Si createOrder falla → mostrar error, no deducir/sumar puntos
- Si actualización de puntos falla → mostrar warning (pero orden está creada)

**Tiempo**: 2 horas

---

### 4.7 Limpiar Carrito Después de Compra

**Qué**: Vaciar carrito cuando la compra es exitosa

**En CheckoutViewModel**:
```
Después de pago exitoso:
  └─ ManagementCart.clearCart()
     ├─ Vacía lista de items
     └─ Resetea total
```

**Nota**: Ya existe `ManagementCart.clearCart()` en tu app

**Tiempo**: 15 minutos

---

## ETAPA 4: Checklist Completado

```
☐ OrderRepositoryImpl.createOrder() funcionando
☐ Órdenes se guardan en Firebase
☐ Órdenes tienen estructura correcta
☐ RewardsRepository.addPoints() funcionando
☐ RewardsRepository.deductPoints() funcionando
☐ Transacciones se guardan correctamente
☐ PointTransactionRepository implementado
☐ Historial de órdenes se puede leer
☐ Historial de transacciones se puede leer
☐ CheckoutViewModel conectado a repositorios
☐ Puntos se actualizan en Firebase
☐ Firebase listener actualiza UI automáticamente
☐ Carrito se limpia después de compra
☐ Mensajes de error específicos por caso
```

**Tiempo Total Etapa 4**: 10-12 horas

---

# ETAPA 5: TESTING & REFINAMIENTO (Día 9-10)

## Objetivo
Probar flujos completos, manejar edge cases, pulir UI

## Tareas

### 5.1 Prueba Completa: Pago con Tarjeta

**Qué**: Ejecutar flujo completo de pago con tarjeta

**Pasos a ejecutar**:
```
1. Abrir app
2. Agregar productos al carrito
3. Ir a CartActivity
4. Presionar "Proceder al Pago"
5. CheckoutScreen abre
   ├─ Verifica puntos se cargan
   ├─ Verifica carrito se muestra
   └─ Verifica método "Tarjeta" está seleccionado
6. Ingresar datos tarjeta válida:
   ├─ Número: 4111 1111 1111 1111
   ├─ Nombre: Test User
   ├─ MM/YY: 12/25
   └─ CVC: 123
7. Verifica botón se habilita
8. Presionar "Confirmar Pago"
9. Verifica spinner muestra por 1 segundo
10. Verifica ConfirmationScreen aparece
11. Verifica puntos: Antes + Ganados = Después
12. Verifica orden se creó en Firebase
13. Verifica transacción de puntos se grabó
14. Presionar "Volver al Inicio"
15. Verifica carrito está vacío
16. Verifica PointsCard muestra puntos nuevos
```

**Validaciones**:
- ✅ Todos los pasos se ejecutan sin error
- ✅ Tiempos correctos (delay 1 segundo)
- ✅ Puntos coinciden (si había 500, ahora 502)
- ✅ Orden se ve en historial

**Tiempo**: 1-2 horas (manual)

---

### 5.2 Prueba Completa: Pago con Puntos

**Qué**: Ejecutar flujo completo de pago con puntos

**Setup previo**:
- Primero hacer 1-2 compras con tarjeta para acumular puntos
- Asegurar que usuario tiene 3000+ puntos

**Pasos**:
```
1-5. Igual que pago tarjeta
6. Seleccionar "Pagar con Puntos"
   ├─ Verifica se muestra:
   │  "Necesitas 1997 puntos"
   │  "Tienes 3000 puntos"
   │  "Te sobrarían 1003 puntos"
   └─ Verifica botón está HABILITADO (verde)
7. Presionar "Confirmar Pago"
8. Verifica modal de confirmación
9. Presionar "Confirmar"
10. Verifica spinner por 800ms (más rápido que tarjeta)
11. Verifica ConfirmationScreen aparece
12. Verifica puntos: Antes - Gastados = Después
    (3000 - 1997 = 1003)
13. Verifica NO gana nuevos puntos
14. Verifica en Firebase:
    - Orden creada con paymentMethod: "points"
    - pointsUsed: 1997
    - pointsEarned: 0
15. Verifica PointsCard muestra 1003
```

**Validaciones**:
- ✅ Puntos se restan correctamente
- ✅ No gana puntos por pago con puntos
- ✅ Orden tiene markado como "points"

**Tiempo**: 1-2 horas (manual)

---

### 5.3 Prueba: Usuario sin Suficientes Puntos

**Qué**: Usuario intenta pagar con puntos pero no tiene suficientes

**Setup**:
- Usuario con 500 puntos
- Carrito total: $19.97 (1997 puntos necesarios)

**Pasos**:
```
1. Abrir CheckoutScreen
2. Seleccionar "Pagar con Puntos"
3. Verifica se muestra:
   "Necesitas 1997 puntos"
   "Tienes 500 puntos"
   "❌ Te faltan 1497 puntos"
4. Verifica botón está DESHABILITADO (gris)
5. Intenta presionar botón (no debe responder)
6. Seleccionar "Pagar con Tarjeta"
7. Verifica botón "Confirmar Pago" está habilitado
8. Completa pago con tarjeta
```

**Validaciones**:
- ✅ Botón está deshabilitado si no hay suficientes puntos
- ✅ Usuario no puede accidentalmente "hacer click"
- ✅ Mensaje es claro

**Tiempo**: 30 minutos

---

### 5.4 Prueba: Errores de Validación

**Qué**: Probar validaciones de tarjeta

**Casos a probar**:

**Caso 1: Número tarjeta inválido**
```
1. Ingresar: "4000 0000 0000 0000"
2. Verifica input en rojo (validación falla)
3. Verifica botón deshabilitado
4. Ingresar: "4111 1111 1111 1111"
5. Verifica input en verde
6. Verifica botón habilitado
```

**Caso 2: Nombre vacío**
```
1. Dejar nombre en blanco
2. Verifica botón deshabilitado
3. Ingresar nombre
4. Verifica botón habilitado
```

**Caso 3: MM/YY inválido**
```
1. Ingresar "13/25" (mes inválido)
2. Verifica input rojo, botón deshabilitado
3. Ingresar "12/25"
4. Verifica input verde, botón habilitado
```

**Caso 4: CVC inválido**
```
1. Ingresar "12" (solo 2 dígitos)
2. Verifica rojo, botón deshabilitado
3. Ingresar "123"
4. Verifica verde, botón habilitado
```

**Tiempo**: 1 hora

---

### 5.5 Prueba: Error Simulado en Mock

**Qué**: Probar qué pasa si MockPaymentService retorna error

**Setup**:
- Modificar temporalmente MockPaymentService para retornar error

**Pasos**:
```
1. Ingresar tarjeta que comience con "4000"
2. Presionar "Confirmar Pago"
3. Verifica spinner por 1 segundo
4. Verifica snackbar rojo: "Pago rechazado"
5. Verifica botón rehabilitado
6. Verifica inputs aún tienen datos (no se limpian)
7. Intentar nuevamente con "4111..."
8. Verifica que ahora sí funciona
9. Verifica NO se crea orden en Firebase
10. Verifica puntos NO se actualicen
```

**Validaciones**:
- ✅ Errores no crean órdenes
- ✅ Puntos no se modifican si hay error
- ✅ Usuario puede reintentar sin volver a cargar datos

**Tiempo**: 45 minutos

---

### 5.6 Prueba: Persistencia en Firebase

**Qué**: Cerrar y abrir app, verificar que todo persiste

**Pasos**:
```
1. Hacer compra exitosa con tarjeta
2. Verifica ConfirmationScreen
3. Presionar "Volver al Inicio"
4. Cerrar app completamente
5. Abrir app nuevamente
6. Ir a PointsCard
7. Verifica puntos son los actualizados (no vuelven atrás)
8. Ir a historial de órdenes (si existe)
9. Verifica orden creada está en la lista
10. Presionar orden
11. Verifica detalles correctos
```

**Validaciones**:
- ✅ Puntos persisten
- ✅ Órdenes persisten
- ✅ Datos no se pierden entre sesiones

**Tiempo**: 45 minutos

---

### 5.7 Refinamiento de UI

**Qué**: Pulir detalles visuales

**Checklist de UI**:
```
☐ Espaciado consistente entre elementos
☐ Colores acordes al design de la app
☐ Animaciones suaves (sin saltos)
☐ Fuentes y tamaños legibles
☐ Botones tienen feedback al presionar
☐ Inputs muestran cursor
☐ Teclado virtual no tapa inputs
☐ ScrollView funciona si hay poco espacio
☐ Spinners son visibles y claros
☐ Snackbars aparecen y desaparecen suave
☐ ConfirmationScreen animación smooth
☐ Texto de error es claro y útil
```

**Tiempo**: 1-2 horas

---

### 5.8 Limpieza de Código

**Qué**: Refactoring y limpieza

**Tareas**:
```
☐ Remover código comentado
☐ Remover logs de debug
☐ Consolidar constantes en un solo lugar
☐ Extraer strings a strings.xml
☐ Verificar nombres de variables sean claros
☐ Comentarios explicativos donde necesarios
☐ Organizar imports
☐ Verificar no hay warnings en compiler
```

**Tiempo**: 1 hora

---

## ETAPA 5: Checklist Completado

```
☐ Pago tarjeta: flujo completo probado
☐ Pago puntos: flujo completo probado
☐ Validación puntos insuficientes funciona
☐ Validación tarjeta inválida funciona
☐ Error handling funciona correctamente
☐ Órdenes se crean en Firebase
☐ Puntos se actualizan en Firebase
☐ Persistencia funciona (close/open app)
☐ UI pulida (espaciado, colores, animaciones)
☐ Código limpio (sin warnings)
☐ Todos los mensajes de error son claros
☐ Buttons no permiten double-click accidentalmente
☐ Spinners muestran durante tiempo correcto
☐ Transición entre pantallas es suave
```

**Tiempo Total Etapa 5**: 8-10 horas

---

# RESUMEN GENERAL

## Timeline

```
ETAPA 1 (Setup):              Día 1-2   (10-12 horas)
ETAPA 2 (UI):                 Día 3-4   (14-18 horas)
ETAPA 3 (ViewModel & Lógica): Día 5-6   (15-17 horas)
ETAPA 4 (Firebase):           Día 7-8   (10-12 horas)
ETAPA 5 (Testing & Polish):   Día 9-10  (8-10 horas)

TOTAL: 10 días / 57-69 horas de desarrollo
```

## Por Día

```
Día 1: Modelos + Repositorios + Navigation
Día 2: MockPaymentService + Koin setup
Día 3: CheckoutScreen componentes
Día 4: ConfirmationScreen + Estados visuales
Día 5: CheckoutViewModel implementado
Día 6: MockPaymentService integrado
Día 7: Firebase: órdenes + puntos
Día 8: Firebase: historial + sync
Día 9: Testing flujos completos
Día 10: Refinamiento y limpieza
```

## Puntos de Verificación (Checkpoints)

```
EOD Día 2: 
  ✅ Proyecto compila sin errores
  ✅ Navegación funciona

EOD Día 4:
  ✅ CheckoutScreen renderiza correctamente
  ✅ Inputs validados visualmente

EOD Día 6:
  ✅ ViewModel conectado a UI
  ✅ MockPaymentService simula pagos

EOD Día 8:
  ✅ Órdenes se guardan en Firebase
  ✅ Puntos se actualizan correctamente

EOD Día 10:
  ✅ Flujos completos probados
  ✅ App lista para usar
```

## Riesgos & Mitigación

```
Riesgo 1: Validaciones complejas de tarjeta
Mitigación: Usar librería o regex probado

Riesgo 2: Firebase no actualiza UI en tiempo real
Mitigación: Usar listeners con collectAsState()

Riesgo 3: MockPaymentService con delays indefinidos
Mitigación: Usar withTimeoutOrNull() en ViewModel

Riesgo 4: Carrito no se limpia correctamente
Mitigación: Verificar ManagementCart.clearCart() existe

Riesgo 5: Bugs en integración Firebase
Mitigación: Pruebas unitarias tempranas en ETAPA 4
```

---

**Documento**: ETAPAS_DE_TRABAJO.md  
**Versión**: 1.0  
**Estado**: Listo para ejecutar  
**Última actualización**: 12 de Junio, 2026

