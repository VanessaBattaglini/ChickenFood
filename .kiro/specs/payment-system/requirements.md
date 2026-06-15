# 💳 Sistema de Pagos - Requisitos

**Fecha**: 12 de Junio, 2026  
**Versión**: 1.0  
**Alcance**: Tarjeta de Crédito + Puntos Acumulados

---

## 🎯 Objetivo General

Permitir que usuarios autenticados compren productos usando:
1. **Tarjeta de crédito** (procesada por Stripe)
2. **Puntos acumulados** (deducidos localmente)
3. **Híbrido**: Puntos + Tarjeta (juntos en una compra)

---

## 📋 Requisitos Funcionales

### RF-01: Pantalla de Checkout

**Usuario debe ver**:
- ✅ Resumen del carrito (ítems, cantidades, precios)
- ✅ Subtotal
- ✅ Total a pagar
- ✅ Saldo de puntos disponibles
- ✅ Selector de método de pago (Tarjeta, Puntos, Tarjeta+Puntos)
- ✅ Botón para proceder

**No visible si**:
- Usuario no está autenticado
- Carrito está vacío

---

### RF-02: Opción 1 - Pago Solo con Tarjeta

**Flujo**:
1. Usuario selecciona "Pagar con Tarjeta"
2. Ingresa datos: número, fecha vencimiento, CVC
3. Revisa monto total
4. Confirma pago
5. App envía a backend
6. Backend procesa con Stripe
7. Si exitoso: Crear orden, agregar puntos (10% del monto), limpiar carrito
8. Si falla: Mostrar error y permitir reintentar

**Validaciones**:
- Tarjeta válida (formato)
- Monto > $0
- Usuario autenticado

---

### RF-03: Opción 2 - Pago Solo con Puntos

**Flujo**:
1. Usuario selecciona "Pagar con Puntos"
2. App calcula puntos necesarios (monto * 100)
   - Ej: $10.00 = 1000 puntos (1 punto = $0.01)
3. Si usuario tiene suficientes puntos:
   - ✅ Mostrar: "¿Usar 1000 puntos para esta compra?"
   - ✅ Botón "Confirmar" habilitado
4. Si NO tiene suficientes:
   - ❌ Mostrar: "Te faltan XXX puntos"
   - ❌ Botón "Confirmar" deshabilitado
5. Si confirma:
   - Deducir puntos
   - Crear orden
   - NO gana nuevos puntos (ya los usó)
   - Limpiar carrito

**Validaciones**:
- Puntos >= puntos necesarios
- Usuario autenticado
- Usuario tiene orden previa (para tener puntos)

---

### RF-04: Opción 3 - Pago Híbrido (Puntos + Tarjeta)

**Flujo**:
1. Usuario selecciona "Puntos + Tarjeta"
2. App muestra:
   - "Saldo: 500 puntos"
   - "Total: $20.00"
   - Slider/Input: "¿Cuántos puntos usar? (0-500)"
3. Usuario entra cantidad: 300 puntos
4. App calcula:
   - 300 puntos = $3.00
   - A pagar con tarjeta: $20.00 - $3.00 = $17.00
5. Usuario ingresa datos de tarjeta
6. Confirma pago
7. Backend procesa:
   - Deduce 300 puntos
   - Cobra $17.00 en tarjeta con Stripe
   - Gana puntos solo sobre lo pagado ($17.00 * 10% = 1.7 puntos ≈ 2)
   - Crea orden
   - Limpia carrito

**Validaciones**:
- Puntos a usar <= puntos disponibles
- Monto con tarjeta > $0 (al menos $1)
- Tarjeta válida

---

### RF-05: Manejo de Errores de Pago

**Si Stripe rechaza**:
- ❌ Mostrar razón: "Tarjeta rechazada", "Fondos insuficientes", etc
- ✅ NO deducir puntos
- ✅ Carrito permanece intacto
- ✅ Permitir reintentar o cambiar método

**Si falla conexión**:
- ❌ Mostrar: "Error de conexión. Intenta de nuevo"
- ✅ NO procesar orden
- ✅ NO deducir puntos
- ✅ Carrito permanece intacto

**Si falla servidor**:
- ❌ Mostrar: "Error temporal. Por favor intenta más tarde"
- ✅ Guardar registro del intento
- ✅ NO duplicar orden

---

### RF-06: Confirmación de Compra

**Usuario debe ver**:
- ✅ Número de orden
- ✅ Monto pagado (tarjeta + puntos desglosado)
- ✅ Método de pago usado
- ✅ Puntos ganados (si aplica)
- ✅ Puntos gastados (si pagó con puntos)
- ✅ Botón "Volver al Inicio"
- ✅ Opción para ver detalles de orden

---

### RF-07: Historial de Órdenes

**Usuario puede ver**:
- ✅ Listado de órdenes previas
- ✅ Fecha de cada orden
- ✅ Total pagado
- ✅ Método de pago
- ✅ Estado (completada, pendiente)
- ✅ Detalles al tocar una orden

---

## 🔐 Requisitos de Seguridad

### RS-01: Datos de Tarjeta

- ❌ NUNCA guardar número completo de tarjeta
- ❌ NUNCA enviar datos crudos de tarjeta a Firebase
- ✅ Usar Stripe para tokenizar
- ✅ Stripe devuelve token seguro
- ✅ Enviar solo token al backend
- ✅ Backend procesa con Secret Key (privado)

### RS-02: Autenticación

- ✅ Usuario debe estar logueado
- ✅ Verificar token Firebase
- ✅ Cada orden vinculada a UID del usuario

### RS-03: Autorización

- ✅ Usuario solo puede ver sus órdenes
- ✅ Usuario solo puede gastar sus puntos
- ✅ Backend valida que puntos >= monto pedido

### RS-04: Validaciones

- ✅ Backend revalida TODO (no confiar en cliente)
- ✅ Monto debe coincidir con carrito
- ✅ Puntos debe estar dentro del saldo
- ✅ Prevenir duplicados (idempotencia)
- ✅ Loguear todos los intentos

---

## 📊 Requisitos de Datos

### RD-01: Orden debe guardar

```
orderId: string (único)
userId: string (UID Firebase)
items: array
  - productId
  - name
  - quantity
  - price
  - subtotal
totalAmount: decimal ($XX.XX)
paymentMethod: string ("card", "points", "hybrid")
pointsUsed: int (0 si no usó puntos)
pointsEarned: int (10% del monto en tarjeta)
amountPaidWithCard: decimal (0 si solo puntos)
amountPaidWithPoints: decimal (calculado)
chargeId: string (ID de Stripe, si aplica)
status: string ("completed", "failed", "pending")
timestamp: long
```

### RD-02: Transacción de Puntos debe guardar

```
transactionId: string (único)
userId: string
type: string ("earned", "spent")
points: int
reason: string ("Purchase order123", "Refund", etc)
orderId: string (vinculada a orden)
timestamp: long
```

---

## 📱 Requisitos de UX

### RUX-01: Flujo Intuitivo

- ✅ Clara diferencia entre opciones de pago
- ✅ Información en tiempo real (puntos, totales)
- ✅ Confirmación antes de procesar
- ✅ Feedback durante proceso (cargando, procesando)

### RUX-02: Accesibilidad

- ✅ Botones claramente etiquetados
- ✅ Errores en rojo, éxito en verde
- ✅ Validaciones inline
- ✅ Fácil de volver atrás

### RUX-03: Performance

- ✅ Carga UI en < 1 segundo
- ✅ Pago procesa en < 5 segundos
- ✅ Timeout si tarda > 30 segundos

---

## 🎬 Flujos de Usuario

### Flujo 1: Pago Solo con Tarjeta

```
CartActivity
    ↓ Click "Proceder al Pago"
CheckoutActivity
    ↓ Muestra carrito + opciones
Usuario selecciona "Pagar con Tarjeta"
    ↓
CheckoutActivity
    ├─ Muestra input de tarjeta (Stripe CardInput)
    ├─ Usuario entra: Número, Exp, CVC
    ├─ Muestra total $29.99
    └─ Click "Pagar $29.99"
    ↓
App procesa
    ├─ CardInput de Stripe valida
    ├─ Genera token seguro
    ├─ Envía a backend: { token, amount, userId, orderId }
    ↓
Backend (Node.js)
    ├─ Recibe token
    ├─ Verifica usuario
    ├─ Verifica monto
    ├─ Carga tarjeta con Stripe
    ├─ Paga $29.99
    ├─ Crea orden en Firebase
    ├─ Agrega 3 puntos (10% de $29.99)
    └─ Responde: { success, orderId, chargeId }
    ↓
App recibe respuesta
    ├─ Si exitoso:
    │  ├─ Limpia carrito
    │  ├─ Muestra SuccessScreen
    │  └─ Muestra: "Orden XXX completada, +3 puntos"
    └─ Si falla:
       ├─ Muestra error
       ├─ Permite reintentar
       └─ Carrito intacto
```

### Flujo 2: Pago Solo con Puntos

```
CartActivity
    ↓ Click "Proceder al Pago"
CheckoutActivity
    ↓ Muestra carrito + opciones
Usuario selecciona "Pagar con Puntos"
    ↓
CheckoutActivity
    ├─ Calcula: $29.99 * 100 = 2999 puntos
    ├─ Usuario tiene 1500 puntos
    ├─ Muestra: "Te faltan 1499 puntos"
    └─ Botón "Confirmar" DESHABILITADO
    ↓
(Alternativa: Usuario tiene 3000 puntos)
    ├─ Muestra: "¿Usar 2999 puntos?"
    ├─ Botón "Confirmar" HABILITADO
    └─ Click "Confirmar"
    ↓
App procesa (LOCAL + Firebase)
    ├─ Deduce 2999 puntos de usuario
    ├─ Crea orden
    ├─ Guarda en Firebase
    ├─ Limpia carrito
    └─ Muestra SuccessScreen
       └─ "Orden XXX completada, -2999 puntos"
```

### Flujo 3: Pago Híbrido (Puntos + Tarjeta)

```
CartActivity
    ↓ Click "Proceder al Pago"
CheckoutActivity
    ↓ Muestra carrito + opciones
Usuario selecciona "Puntos + Tarjeta"
    ↓
CheckoutActivity (Puntos Step)
    ├─ Muestra: "Saldo: 1500 puntos"
    ├─ Total a pagar: $29.99
    ├─ Input/Slider: "¿Cuántos puntos usar?"
    ├─ Usuario entra: 1000 puntos
    ├─ App calcula:
    │  ├─ 1000 puntos = $10.00
    │  └─ A pagar: $29.99 - $10.00 = $19.99
    └─ Click "Siguiente"
    ↓
CheckoutActivity (Tarjeta Step)
    ├─ Muestra: "A pagar con tarjeta: $19.99"
    ├─ Muestra input de tarjeta
    ├─ Usuario entra datos
    └─ Click "Pagar $19.99"
    ↓
App procesa
    ├─ CardInput genera token
    ├─ Envía: { token, pointsToUse: 1000, cardAmount: 1999, userId, orderId }
    ↓
Backend
    ├─ Verifica usuario
    ├─ Verifica: 1000 puntos <= saldo
    ├─ Verifica: $19.99 coincide con cálculo
    ├─ Deduce 1000 puntos
    ├─ Carga tarjeta $19.99 con Stripe
    ├─ Crea orden
    ├─ Gana puntos SOLO sobre tarjeta: $19.99 * 10% = 2 puntos
    └─ Responde éxito
    ↓
App muestra SuccessScreen
    └─ "Orden XXX completada"
    └─ "Puntos gastados: 1000"
    └─ "Pagado con tarjeta: $19.99"
    └─ "Puntos ganados: +2"
```

---

## ⚠️ Casos Especiales

### CE-01: Tarjeta Rechazada

Usuario paga $29.99, Stripe rechaza.

**Backend debe**:
- ❌ NO deducir puntos
- ❌ NO crear orden
- Responder error a app

**App debe**:
- Mostrar: "Tarjeta rechazada: Fondos insuficientes"
- Permitir reintentar
- Carrito permanece igual

### CE-02: Conexión Perdida Después de Pagar

Backend cargó tarjeta, pero se cortó conexión antes de responder.

**Backend debe** (idempotencia):
- Si orden ya existe: responder OK
- Si no existe: crear orden
- Siempre responder confirmación

**App debe**:
- Reintentar conexión
- Esperar máximo 30 segundos
- Si timeout: guardar intento, mostrar "Verificando con servidor..."
- En siguiente login: verificar si orden se creó

### CE-03: Usuario Intenta Pagar más Puntos que tiene

Usuario dice "Usar 3000 puntos" pero solo tiene 1500.

**Backend DEBE**:
- Verificar en BD: puntos >= 3000
- Si no: rechazar con "Saldo insuficiente"
- NO procesar

### CE-04: Doble Compra (Spamming)

Usuario hace click 2 veces en "Pagar" rápido.

**Prevención**:
- App: Botón deshabilitado durante procesamiento
- Backend: Verificar orderId único + timestamp
- Firebase: Escribir en transacción (atomic)

---

## ✅ Criterios de Aceptación

### CA-01: Pago con Tarjeta

- [ ] Usuario ve UI de pago
- [ ] Ingresa datos de tarjeta válidos
- [ ] Stripe genera token
- [ ] Backend recibe y procesa
- [ ] Orden se crea en Firebase
- [ ] Puntos se añaden (10%)
- [ ] Carrito se limpia
- [ ] Usuario ve confirmación

### CA-02: Pago con Puntos

- [ ] Usuario ve saldo de puntos
- [ ] Si puntos < necesarios: botón deshabilitado
- [ ] Si puntos >= necesarios: botón habilitado
- [ ] Al confirmar: puntos se deducen
- [ ] Orden se crea sin puntos ganados nuevos
- [ ] Carrito se limpia
- [ ] Historial muestra gasto de puntos

### CA-03: Pago Híbrido

- [ ] Usuario selecciona cantidad de puntos
- [ ] App calcula monto restante
- [ ] Usuario paga con tarjeta
- [ ] Puntos se deducen
- [ ] Tarjeta se carga solo el monto restante
- [ ] Puntos ganados = 10% del monto en tarjeta (no del total)
- [ ] Orden muestra desglose

### CA-04: Seguridad

- [ ] Datos de tarjeta NUNCA en logs
- [ ] Secret Key NUNCA en app
- [ ] Token Stripe SOLO en tránsito
- [ ] Cada orden vinculada a usuario
- [ ] Backend valida TODO

### CA-05: Errores

- [ ] Tarjeta rechazada: mostrar error, permitir reintentar
- [ ] Conexión perdida: reintentar automático
- [ ] Timeout: mostrar "Procesando..."
- [ ] Puntos insuficientes: bloquear opción

---

## 🎯 Definiciones de Hecho

Una feature se considera **completa** cuando:

1. ✅ Todos los casos de uso funcionan
2. ✅ Todos los errores se manejan
3. ✅ Datos se guardan correctamente
4. ✅ No hay seguridad quebrantada
5. ✅ Se probó en dispositivo real
6. ✅ Documentación actualizada
7. ✅ Build sin errores/warnings

