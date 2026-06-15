# ✅ Sistema de Pagos - Tasks (Tareas)

**Fecha**: 12 de Junio, 2026  
**Versión**: 1.0  
**Total Tasks**: 24

---

## 📋 Formato de Task

Cada tarea sigue este formato:

```
## [ID] Nombre de la Tarea

**Categoría**: Frontend / Backend / Infrastructure / Database

**Descripción**: Qué se debe hacer

**Dependencias**: 
- Tarea que debe completarse antes

**Criterios de Aceptación**:
- [ ] Criterio 1
- [ ] Criterio 2

**Notas**: Información adicional

**Estimación**: X horas
```

---

# FASE 1: SETUP E INFRAESTRUCTURA

## [T-001] Crear Backend con Node.js + Express

**Categoría**: Infrastructure

**Descripción**: 
Inicializar proyecto Node.js con Express, estructura básica de carpetas, y configuración inicial.

**Dependencias**: Ninguna

**Criterios de Aceptación**:
- [ ] Proyecto Node.js inicializado
- [ ] Express configurado y escuchando en puerto 3000
- [ ] Estructura de carpetas: routes/, controllers/, middleware/, config/
- [ ] Archivo .env con variables de entorno
- [ ] npm scripts: dev, build, start
- [ ] Helmet configurado (seguridad básica)
- [ ] CORS habilitado para localhost y ChickenFood

**Notas**:
- Usar `npm init` y `npm install express helmet cors dotenv`
- Crear archivo `.env.example` con placeholders

**Estimación**: 1 hora

---

## [T-002] Configurar Stripe en Backend

**Categoría**: Infrastructure

**Descripción**:
Obtener credenciales de Stripe, configurar SDK y crear archivos de configuración.

**Dependencias**: T-001

**Criterios de Aceptación**:
- [ ] Cuenta Stripe sandbox creada
- [ ] Public Key y Secret Key obtenidos
- [ ] Secret Key guardado en .env (NUNCA en código)
- [ ] `config/stripe.js` creado
- [ ] Stripe SDK inicializado en backend
- [ ] Método para crear charges disponible

**Notas**:
- Ir a https://dashboard.stripe.com/register
- Usar Sandbox mode para testing
- Public Key se usa en app (público)
- Secret Key se usa en backend (privado)

**Estimación**: 1 hora

---

## [T-003] Configurar Firebase Admin SDK en Backend

**Categoría**: Infrastructure

**Descripción**:
Configurar Firebase Admin SDK en backend para validar tokens y acceder a BD.

**Dependencias**: T-001

**Criterios de Aceptación**:
- [ ] Firebase SDK instalado: `npm install firebase-admin`
- [ ] Service Account key descargada de Firebase
- [ ] `config/firebase.js` creado
- [ ] Admin SDK inicializado
- [ ] Método para validar Firebase tokens disponible
- [ ] Método para escribir en Realtime Database disponible

**Notas**:
- Descargar Service Account key desde Firebase Console → Settings → Service Accounts
- Guardar en .env como JSON o path
- NUNCA commitear la key a Git

**Estimación**: 1 hora

---

## [T-004] Crear Endpoints Base en Backend

**Categoría**: Backend

**Descripción**:
Crear estructura de endpoints con autenticación base, sin lógica de pagos aún.

**Dependencias**: T-001, T-002, T-003

**Criterios de Aceptación**:
- [ ] Endpoint POST `/payment/process-card` estructura básica
- [ ] Endpoint POST `/payment/process-points` estructura básica
- [ ] Endpoint POST `/payment/process-hybrid` estructura básica
- [ ] Endpoint GET `/orders` (historial)
- [ ] Middleware de autenticación Firebase aplicado
- [ ] Validación de request básica
- [ ] Error handling general

**Notas**:
- Endpoints deben responder con estructura definida en design.md
- Todavía sin lógica de pagos, solo scaffold

**Estimación**: 2 horas

---

## [T-005] Crear CheckoutViewModel en App

**Categoría**: Frontend

**Descripción**:
Crear ViewModel para manejar lógica de checkout.

**Dependencias**: Ninguna

**Criterios de Aceptación**:
- [ ] CheckoutViewModel creado con Koin
- [ ] Estados (CheckoutUiState) definidos
- [ ] Métodos selectPaymentMethod() implementados
- [ ] Método calcularMontos() implementado
- [ ] Método validarPago() implementado
- [ ] _checkoutUiState StateFlow expuesto
- [ ] Integrarse con OrderViewModel existente

**Notas**:
- Usar StateFlow para actualizaciones reactivas
- Usar LaunchEffect para cargar datos
- Debe obtener puntos del RewardsViewModel

**Estimación**: 2 horas

---

## [T-006] Crear CheckoutActivity/Screen UI

**Categoría**: Frontend

**Descripción**:
Crear UI de checkout con Jetpack Compose.

**Dependencias**: T-005

**Criterios de Aceptación**:
- [ ] CheckoutScreen Composable creado
- [ ] ResumenCarrito Component visible
- [ ] SaldoPuntos Component visible
- [ ] PaymentMethodSelector (Radio buttons) visible
- [ ] ConditionalContent renderiza según selección
- [ ] Layout responsivo
- [ ] Navegación: back button → CartActivity
- [ ] Loading state mostrado durante pago

**Notas**:
- Usar RadioButton para método de pago
- Usar Scaffold para estructura básica
- Colors y typography del design system existente

**Estimación**: 3 horas

---

# FASE 2: PAGO CON TARJETA

## [T-007] Integrar Stripe CardInput en CheckoutScreen

**Categoría**: Frontend

**Descripción**:
Agregar Stripe CardInputWidget a la UI de checkout.

**Dependencias**: T-006

**Criterios de Aceptación**:
- [ ] Stripe SDK agregado: `implementation 'com.stripe:stripe-android:20.x.x'`
- [ ] CardInputWidget renderizado cuando usuario selecciona tarjeta
- [ ] Validación de tarjeta en tiempo real (UI feedback)
- [ ] Botón deshabilitado si tarjeta inválida
- [ ] Método getCardToken() disponible
- [ ] Manejo de excepciones de Stripe

**Notas**:
- CardInputWidget valida automáticamente
- No almacenar datos de tarjeta localmente
- Usar Stripe Public Key

**Estimación**: 2 horas

---

## [T-008] Crear CheckoutService para comunicación Backend

**Categoría**: Frontend

**Descripción**:
Crear servicio HTTP para comunicarse con backend de pagos.

**Dependencias**: T-007, T-004

**Criterios de Aceptación**:
- [ ] CheckoutService creado (Retrofit)
- [ ] Método `processCardPayment(token, amount, userId, orderId)` definido
- [ ] Método `processPointsPayment(points, userId, orderId)` definido
- [ ] Método `processHybridPayment(...)` definido
- [ ] Autenticación con Firebase token agregada en headers
- [ ] Error handling implementado
- [ ] Timeout configurado (30 segundos)

**Notas**:
- Usar Retrofit + OkHttp
- Base URL debe ser configurable
- Incluir Firebase ID Token en headers

**Estimación**: 2 horas

---

## [T-009] Implementar Flujo de Pago con Tarjeta en ViewModel

**Categoría**: Frontend

**Descripción**:
Conectar CardInputWidget con backend para procesar pago.

**Dependencias**: T-008

**Criterios de Aceptación**:
- [ ] Método `procesarPagoConTarjeta()` implementado
- [ ] CardInputWidget.getCardToken() llamado
- [ ] Token enviado a backend
- [ ] States actualizado: Loading → Success/Error
- [ ] Error message mostrado si falla
- [ ] Carrito limpiado si éxito
- [ ] Navegación a ConfirmationScreen si éxito

**Notas**:
- Usar try-catch para excepciones
- Mostrar loading spinner
- Permitir reintentar en caso de error

**Estimación**: 2 horas

---

## [T-010] Crear ConfirmationScreen

**Categoría**: Frontend

**Descripción**:
Pantalla que muestra confirmación de compra exitosa.

**Dependencias**: T-009

**Criterios de Aceptación**:
- [ ] ConfirmationScreen Composable creado
- [ ] Muestra ✅ ícono de éxito
- [ ] Número de orden visible
- [ ] Resumen: Total, Método, Puntos (si aplica)
- [ ] Botón "Volver al Inicio"
- [ ] Botón "Ver Detalles de Orden"
- [ ] Layout atractivo y claro

**Notas**:
- Recibir orderId y detalles por parámetros
- Integración con navegación

**Estimación**: 2 horas

---

## [T-011] Implementar Lógica de Pago Tarjeta en Backend

**Categoría**: Backend

**Descripción**:
Implementar procesamiento de pago con Stripe en backend.

**Dependencias**: T-004, T-002

**Criterios de Aceptación**:
- [ ] Endpoint POST `/payment/process-card` implementado
- [ ] Validación de Firebase token
- [ ] Validación de monto (debe coincidir con carrito)
- [ ] Validación de token Stripe
- [ ] Carga de tarjeta con Stripe.charges.create()
- [ ] Si éxito: crear orden en Firebase
- [ ] Si éxito: agregar puntos (10% del monto)
- [ ] Si éxito: guardar transacción de puntos
- [ ] Error handling completo
- [ ] Logging de intentos

**Notas**:
- Monto en CENTS (2999 = $29.99)
- Stripe devuelve chargeId
- Guardar chargeId en orden para referencia

**Estimación**: 3 horas

---

## [T-012] Crear Orden en Firebase después de Pago

**Categoría**: Backend

**Descripción**:
Guardar orden en Firebase después de pago exitoso.

**Dependencias**: T-011, T-003

**Criterios de Aceptación**:
- [ ] Orden guardada en `/orders/{userId}/{orderId}`
- [ ] Todos los campos del schema guardados
- [ ] Items copiados del carrito original
- [ ] Timestamp asignado
- [ ] Transacción atómica (no falla a mitad)
- [ ] OrderID único (prevenir duplicados)

**Notas**:
- Usar Firebase Admin SDK
- Orden debe ser inmutable (no editable por cliente)

**Estimación**: 1.5 horas

---

## [T-013] Agregar Puntos después de Pago con Tarjeta

**Categoría**: Backend

**Descripción**:
Actualizar saldo de puntos y crear transacción de puntos.

**Dependencias**: T-011

**Criterios de Aceptación**:
- [ ] Puntos calculados: 10% del monto en tarjeta
- [ ] Saldo actualizado en `/users/{userId}/rewards`
- [ ] Transacción guardada en `/pointTransactions`
- [ ] Type: "earned"
- [ ] Reason: "Purchase order_XXXXX"
- [ ] Timestamp asignado
- [ ] Actualización atómica

**Notas**:
- 1 punto = $0.01, entonces: $29.99 → 3 puntos (redondear abajo)
- Guardar en transacciones para auditoría

**Estimación**: 1.5 horas

---

# FASE 3: PAGO CON PUNTOS

## [T-014] Implementar Validación de Puntos en ViewModel

**Categoría**: Frontend

**Descripción**:
Validar si usuario tiene suficientes puntos.

**Dependencias**: T-005

**Criterios de Aceptación**:
- [ ] Método `validarPuntosDisponibles(montoEnDolares)` implementado
- [ ] Calcula puntos necesarios: monto * 100
- [ ] Compara con puntos actuales
- [ ] Retorna: { suficientes: boolean, faltantes: int }
- [ ] Muestra estado en UI
- [ ] Bloquea botón si insuficientes

**Notas**:
- $10.00 = 1000 puntos
- Mostrar cuántos puntos faltan (rojo)

**Estimación**: 1 hora

---

## [T-015] Crear UI para Pago con Puntos

**Categoría**: Frontend

**Descripción**:
UI específica cuando usuario selecciona pagar solo con puntos.

**Dependencias**: T-006, T-014

**Criterios de Aceptación**:
- [ ] Dialog mostrado: "¿Usar XXX puntos?"
- [ ] Muestra puntos disponibles vs necesarios
- [ ] Si insuficientes: mensaje rojo, botón deshabilitado
- [ ] Si suficientes: botón "Confirmar" habilitado
- [ ] Botón "Cancelar"

**Notas**:
- Dialog AlertDialog de Compose
- Usar colores del sistema (rojo/verde)

**Estimación**: 1.5 horas

---

## [T-016] Implementar Flujo de Pago con Puntos en ViewModel

**Categoría**: Frontend

**Descripción**:
Conectar UI de puntos con backend.

**Dependencias**: T-015

**Criterios de Aceptación**:
- [ ] Método `procesarPagoConPuntos()` implementado
- [ ] Envía: { pointsToUse, userId, orderId } a backend
- [ ] Estados: Loading → Success/Error
- [ ] Si éxito: limpia carrito, navega a ConfirmationScreen
- [ ] Si falla: muestra error, permite reintentar

**Notas**:
- Backend hace toda la validación real
- App es solo UI

**Estimación**: 1.5 horas

---

## [T-017] Implementar Lógica de Pago con Puntos en Backend

**Categoría**: Backend

**Descripción**:
Procesar pago usando puntos en backend.

**Dependencias**: T-004, T-003

**Criterios de Aceptación**:
- [ ] Endpoint POST `/payment/process-points` implementado
- [ ] Valida Firebase token
- [ ] Lee puntos actuales de BD (NO confía en cliente)
- [ ] Valida: puntos >= pointsToUse
- [ ] Transacción ATOMICA:
│  - [ ] Deduce puntos
│  - [ ] Crea orden
│  - [ ] Guarda transacción (tipo: "spent")
- [ ] Responde con éxito o error
- [ ] No gana nuevos puntos (ya los usó)

**Notas**:
- Esto es local (sin Stripe)
- Validar en backend, NO en app

**Estimación**: 2 horas

---

## [T-018] Crear Transacción de Gasto de Puntos

**Categoría**: Backend

**Descripción**:
Guardar registro de puntos gastados en transacciones.

**Dependencias**: T-017

**Criterios de Aceptación**:
- [ ] Transacción guardada en `/pointTransactions`
- [ ] Type: "spent"
- [ ] Points: cantidad exacta gastada
- [ ] Reason: "Purchase order_XXXXX"
- [ ] OrderID vinculado
- [ ] Timestamp asignado

**Notas**:
- Importante para auditoría
- Permite historial completo

**Estimación**: 1 hora

---

# FASE 4: PAGO HÍBRIDO (PUNTOS + TARJETA)

## [T-019] Crear UI para Selector de Puntos (Slider/Input)

**Categoría**: Frontend

**Descripción**:
UI para que usuario seleccione cuántos puntos usar en pago híbrido.

**Dependencias**: T-006

**Criterios de Aceptación**:
- [ ] Slider: 0 ← [========●========] → puntos_máximos
- [ ] Input: usuario puede tipear cantidad
- [ ] Validación: no permitir > puntos disponibles
- [ ] Cálculo en tiempo real: X puntos = $Y.YY
- [ ] Muestra monto restante con tarjeta
- [ ] Update UI mientras usuario cambia

**Notas**:
- Usar Slider de Compose
- Input con TextField
- Feedback inmediato

**Estimación**: 2 horas

---

## [T-020] Implementar Cálculos de Pago Híbrido en ViewModel

**Categoría**: Frontend

**Descripción**:
Lógica para calcular desglose de puntos + tarjeta.

**Dependencias**: T-019

**Criterios de Aceptación**:
- [ ] Método `onPuntosChanged(cantidad)` implementado
- [ ] Calcula: puntos_valor = cantidad * 0.01
- [ ] Calcula: monto_tarjeta = total - puntos_valor
- [ ] Valida: monto_tarjeta >= $0.50 (mínimo Stripe)
- [ ] Valida: puntos <= puntos_disponibles
- [ ] Actualiza UI con valores calculados
- [ ] Bloquea botón si valores inválidos

**Notas**:
- 1 punto = $0.01
- Mínimo tarjeta: $0.50 (por Stripe)

**Estimación**: 1.5 horas

---

## [T-021] Implementar Flujo de Pago Híbrido en ViewModel

**Categoría**: Frontend

**Descripción**:
Conectar UI híbrida con backend.

**Dependencias**: T-020, T-008

**Criterios de Aceptación**:
- [ ] Método `procesarPagoHibrido()` implementado
- [ ] Obtiene token de CardInputWidget
- [ ] Envía: { token, pointsToUse, cardAmount, userId, orderId }
- [ ] Estados: Loading → Success/Error
- [ ] Si éxito: limpia carrito, navega a ConfirmationScreen
- [ ] Si falla: muestra error

**Notas**:
- Combina lógica tarjeta + puntos

**Estimación**: 1.5 horas

---

## [T-022] Implementar Lógica de Pago Híbrido en Backend

**Categoría**: Backend

**Descripción**:
Procesar pago híbrido (puntos + tarjeta) en backend.

**Dependencias**: T-004, T-002, T-011, T-017

**Criterios de Aceptación**:
- [ ] Endpoint POST `/payment/process-hybrid` implementado
- [ ] Valida usuario autenticado
- [ ] Valida: puntos >= pointsToUse
- [ ] Valida: cardAmount coincide con cálculo
- [ ] Valida: cardAmount >= $0.50
- [ ] Transacción ATOMICA:
│  - [ ] Deduce puntos
│  - [ ] Carga tarjeta con Stripe (solo cardAmount)
│  - [ ] Crea orden
│  - [ ] Guarda gasto de puntos
│  - [ ] Guarda ganancia de puntos (10% de cardAmount)
- [ ] Error handling

**Notas**:
- Si Stripe falla ANTES de deducir puntos: rollback
- Puntos ganados = 10% del cardAmount, NO del total

**Estimación**: 3 horas

---

# FASE 5: CARACTERÍSTICAS ADICIONALES

## [T-023] Crear Pantalla de Historial de Órdenes

**Categoría**: Frontend

**Descripción**:
Pantalla que muestra historial de compras del usuario.

**Dependencias**: T-011

**Criterios de Aceptación**:
- [ ] OrderHistoryScreen Composable creado
- [ ] Listado de órdenes del usuario
- [ ] Cada orden muestra: fecha, total, método, estado
- [ ] Al tocar: navega a OrderDetailScreen
- [ ] Loading state
- [ ] Error state
- [ ] Empty state

**Notas**:
- Llamar endpoint GET `/orders`
- Mostrar más recientes primero

**Estimación**: 2 horas

---

## [T-024] Implementar Error Recovery y Retry Logic

**Categoría**: Frontend

**Descripción**:
Manejo robusto de errores y reintentos automáticos.

**Dependencias**: T-008, T-011

**Criterios de Aceptación**:
- [ ] Si falla conexión: mostrar snackbar + botón reintentar
- [ ] Si timeout: exponential backoff (1s, 2s, 4s)
- [ ] Máximo 3 reintentos automáticos
- [ ] Si sigue fallando: mostrar "Error temporal"
- [ ] Carrito permanece intacto
- [ ] User puede cambiar método de pago y reintentar

**Notas**:
- Importante para UX
- Manejar casos de internet lento

**Estimación**: 2 horas

---

# RESUMEN DE TAREAS POR CATEGORÍA

## Frontend (13 tareas)
- T-005: CheckoutViewModel
- T-006: CheckoutScreen UI
- T-007: Stripe CardInput
- T-008: CheckoutService
- T-009: Flujo Tarjeta
- T-010: ConfirmationScreen
- T-015: UI Puntos
- T-016: Flujo Puntos
- T-019: UI Slider Puntos
- T-020: Cálculos Híbridos
- T-021: Flujo Híbrido
- T-023: Historial Órdenes
- T-024: Error Recovery

## Backend (8 tareas)
- T-001: Setup Node.js
- T-004: Endpoints Base
- T-011: Pago Tarjeta
- T-012: Crear Orden
- T-013: Agregar Puntos
- T-017: Pago Puntos
- T-018: Transacción Puntos
- T-022: Pago Híbrido

## Infrastructure (3 tareas)
- T-002: Stripe Setup
- T-003: Firebase Admin
- T-014: Validación Puntos

---

# ORDEN RECOMENDADO DE IMPLEMENTACIÓN

### Sprint 1: Infraestructura (4-5 horas)
1. T-001: Backend Setup
2. T-002: Stripe Config
3. T-003: Firebase Admin
4. T-004: Endpoints Base

### Sprint 2: Frontend Base (6-7 horas)
5. T-005: CheckoutViewModel
6. T-006: CheckoutScreen UI
7. T-008: CheckoutService
8. T-010: ConfirmationScreen

### Sprint 3: Pago Tarjeta (6-7 horas)
9. T-007: Stripe CardInput
10. T-009: Flujo Tarjeta Frontend
11. T-011: Pago Tarjeta Backend
12. T-012: Crear Orden
13. T-013: Agregar Puntos

### Sprint 4: Pago Puntos (5-6 horas)
14. T-014: Validar Puntos
15. T-015: UI Puntos
16. T-016: Flujo Puntos Frontend
17. T-017: Pago Puntos Backend
18. T-018: Transacción Puntos

### Sprint 5: Híbrido (5-6 horas)
19. T-019: UI Slider
20. T-020: Cálculos Híbridos
21. T-021: Flujo Híbrido Frontend
22. T-022: Pago Híbrido Backend

### Sprint 6: Polish (4-5 horas)
23. T-023: Historial Órdenes
24. T-024: Error Recovery

---

**Total Estimado**: ~30-35 horas de desarrollo

