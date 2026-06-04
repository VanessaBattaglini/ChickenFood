# 💰 Sistema de Puntos - Cashback y Recompensas

**Estado**: ✅ Completamente funcional  
**Tipo**: Cashback del 10-15% por compra  
**Niveles**: 5 niveles progresivos  
**Conversión**: 1 punto = $0.01

---

## ¿Cómo Funciona?

### Flujo Simple

```
Usuario autenticado realiza compra
    ↓
Calcula 10-15% del total (según nivel)
    ↓
Agrega puntos a su cuenta
    ↓
PointsCard en Dashboard se actualiza
    ↓
Usuario puede canjear puntos por descuentos
```

---

## Niveles de Usuario

### 5 Niveles Progresivos

| Nivel | Puntos | Cashback | Emoji | Color |
|-------|--------|----------|-------|-------|
| Regular | 0 | 10% | 👤 | Gris |
| Bronce | 1-99 | 10% | 🥉 | Cobre |
| Plata | 100-499 | 11% | 🥈 | Plata |
| Oro | 500-999 | 12% | 🏆 | Oro |
| Platino | 1000+ | 15% | 👑 | Platino |

### Cómo Subir de Nivel

```
Regular (0 puntos)
    ↓ Compra $10 (gana 1 punto)
Bronce (1-99 puntos)
    ↓ Compra $10 × 10 (gana 100+ puntos)
Plata (100-499 puntos)
    ↓ Compra $40 × 10 (gana 400+ puntos)
Oro (500-999 puntos)
    ↓ Compra $50 × 10 (gana 500+ puntos)
Platino (1000+ puntos) ⭐ MÁXIMO
```

---

## Cálculo de Puntos

### Fórmula General

```
Puntos ganados = (Total compra × Cashback %) / 100

Ejemplos:
─────────────────────────────────────
Nivel Regular (10%):
  Compra $100 → Gana (100 × 10%) = 10 puntos

Nivel Bronce (10%):
  Compra $100 → Gana (100 × 10%) = 10 puntos

Nivel Oro (12%):
  Compra $100 → Gana (100 × 12%) = 12 puntos

Nivel Platino (15%):
  Compra $100 → Gana (100 × 15%) = 15 puntos
```

---

## Componentes

### 1. UserRewardsModel

**Ubicación**: `domain/model/`

```kotlin
data class UserRewardsModel(
    val userId: String = "",           // UID de Firebase
    val totalPoints: Int = 0,           // Todos los puntos ganados
    val pointsBalance: Int = 0,         // Disponibles para canjear
    val pointsSpent: Int = 0,           // Ya canjeados
    val userLevel: String = "regular",  // Regular/Bronce/Plata/Oro/Platino
    val createdDate: Long = 0           // Fecha de creación
)
```

**En Firebase**:
```
users/
  {userId}/
    rewards/
      totalPoints: 150
      pointsBalance: 120
      pointsSpent: 30
      userLevel: "bronce"
      createdDate: 1622505600000
```

---

### 2. RewardsViewModel

**Ubicación**: `presentation/viewModel/`

**Métodos**:
```kotlin
// Cargar puntos del usuario
fun loadUserRewards(userId: String)

// Canjear puntos
fun redeemPoints(userId: String, points: Int, description: String)

// Agregar puntos de compra
fun addPointsFromPurchase(userId: String, orderTotal: Double, orderId: String)

// Cargar historial de transacciones
fun loadPointsHistory(userId: String)
```

---

### 3. PointsCard

**Ubicación**: `presentation/activity/dashboard/`

**Ubicación en UI**: Entre SearchBar y Banner

**Muestra**:
```
┌─────────────────────────────┐
│ 👤 Mis Puntos              │
│ 150 puntos                  │
│                             │
│ Nivel: Bronce 🥉          │
│ (150 totales)              │
│                             │
│ Progreso: ████░░░░░░      │
│ Faltan 350 puntos para     │
│ el siguiente nivel         │
│                             │
│ ┌───────────────────────┐  │
│ │ Disponibles: 150      │  │
│ │ Gastados: 0           │  │
│ │ Equivalencia: $1.50   │  │
│ └───────────────────────┘  │
└─────────────────────────────┘
```

---

## Flujo de Recompensas

### 1. Usuario Realiza Compra

```
CartActivity
├─ Total: $100
├─ [Proceder al Pago]
│
└─ Pago simulado exitoso
   ├─ Si NO autenticado:
   │  └─ No gana puntos
   │
   └─ Si AUTENTICADO:
      ├─ Obtener nivel actual
      ├─ Calcular cashback según nivel
      ├─ puntos = (100 × cashback%) / 100
      ├─ Ejemplo: Nivel Bronce (10%)
      │  └─ puntos = 10
      │
      └─ rewardsViewModel.addPointsFromPurchase()
         ├─ Crear OrderModel
         ├─ Guardar en Firebase
         ├─ Actualizar UserRewardsModel
         ├─ totalPoints += 10
         ├─ pointsBalance += 10
         │
         └─ Dashboard se actualiza
            └─ PointsCard muestra nuevo saldo
```

---

### 2. Actualizaciones en Tiempo Real

```
CompraActivity (paga)
    ↓
RewardsViewModel.addPointsFromPurchase()
    ↓
Firebase actualiza /users/{uid}/rewards
    ↓
PointsCard observa (collectAsState)
    ↓
Dashboard se renderiza nuevamente
    ↓
Usuario ve nuevo saldo en PointsCard
```

---

## Casos de Uso

### Caso 1: Usuario Regular Hace 3 Compras

```
Compra 1: $100 (Regular 10%)
  Gana: 10 puntos
  Total: 10 puntos
  Nivel: Regular (0-99 puntos)

Compra 2: $100 (Regular 10%)
  Gana: 10 puntos
  Total: 20 puntos
  Nivel: Regular (0-99 puntos)

Compra 3: $100 (Regular 10%)
  Gana: 10 puntos
  Total: 30 puntos
  Nivel: Regular (0-99 puntos)

...

Compra 10: $100 (Regular 10%)
  Gana: 10 puntos
  Total: 100 puntos ⭐ SUBIÓ A BRONCE
  Nivel: Bronce (100-499 puntos)
  Cashback ahora: 10% (sin cambio, pero indicador de progreso)

Compra 11: $100 (Bronce 10%)
  Gana: 10 puntos
  Total: 110 puntos
```

---

### Caso 2: Usuario Bronce Quiere Llegar a Platino

```
Actual: Bronce (110 puntos)
Necesita: 1000 puntos para Platino
Falta: 890 puntos

Si compra $100/día con cashback 10%:
  Puntos/día: 10
  Días necesarios: 890 ÷ 10 = 89 días

Si compra $100/día y llega a Platino (15%):
  Puntos/día: 15
  Acelera proceso ⭐
```

---

### Caso 3: Canjear Puntos

```
Usuario: 150 puntos disponibles
Quiere canjear: 100 puntos

redeemPoints(userId, 100, "Descuento en compra")
    ↓
pointsBalance: 150 - 100 = 50
pointsSpent: 0 + 100 = 100
    ↓
Obtiene: $1.00 descuento (100 × $0.01)
    ↓
Próxima compra: -$1.00 del total
```

---

## RewardsHelper

**Ubicación**: `helper/`

**Métodos útiles**:

```kotlin
// Calcular puntos de total
calculatePointsFromTotal(totalPrice: Double): Int
// Ejemplo: $100 → 10 puntos (10% por defecto)

// Obtener nivel por puntos
getUserLevel(totalPoints: Int): String
// Ejemplo: 150 → "bronce"

// Obtener porcentaje de cashback por nivel
getCashbackPercentage(userLevel: String): Double
// Ejemplo: "platino" → 0.15 (15%)

// Calcular progreso hacia siguiente nivel (0.0 a 1.0)
getLevelProgress(totalPoints: Int): Float
// Ejemplo: 250 puntos → 0.75 (75% de Plata)

// Puntos faltantes para siguiente nivel
getPointsToNextLevel(totalPoints: Int): Int
// Ejemplo: 250 puntos → 250 faltantes para Oro
```

---

## PointsCard en Dashboard

### Actualización Automática

```
1. Usuario llega a Dashboard
2. MainScreen.onMount()
3. Obtiene currentUser
4. rewardsViewModel.loadUserRewards(uid)
5. Obtiene UserRewardsModel de Firebase
6. PointsCard observa el estado
7. Renderiza con los datos actuales

Si Usuario realiza compra:
  1. CartActivity guarda puntos
  2. Firebase actualiza /users/{uid}/rewards
  3. PointsCard observa cambios
  4. Dashboard se actualiza automáticamente
  5. Usuario ve nuevo saldo sin recargar
```

---

## Conversión de Puntos

### Equivalencia en Dinero

```
1 punto = $0.01

Ejemplos:
─────────────────────────────
10 puntos = $0.10
100 puntos = $1.00
500 puntos = $5.00
1000 puntos = $10.00
```

### En PointsCard

```
PointsCard muestra:
├─ Disponibles: 150 puntos
├─ Equivalencia: $1.50
│
└─ Cálculo: 150 × $0.01 = $1.50
```

---

## Historial de Transacciones

### PointsTransactionModel

```kotlin
data class PointsTransactionModel(
    val transactionId: String = "",
    val userId: String = "",
    val type: String = "",              // "earned" o "spent"
    val points: Int = 0,
    val amount: Double = 0.0,
    val description: String = "",       // "Compra" o "Descuento aplicado"
    val date: Long = 0,
    val orderIdReference: String = ""   // ID de la compra relacionada
)
```

### En Firebase

```
users/
  {userId}/
    transactions/
      transaction1/
        type: "earned"
        points: 10
        amount: 100
        description: "Compra en dashboard"
        date: 1622505600000
      transaction2/
        type: "spent"
        points: 50
        amount: 0
        description: "Descuento aplicado"
        date: 1622505700000
```

---

## Estados de Puntos

### En PointsCard

```
Loading:
├─ ⏳ Spinner + "Cargando puntos..."

Loaded (Bronce):
├─ 👤 Mis Puntos
├─ 150 puntos
├─ Nivel: Bronce 🥉
├─ Progreso: ████░░░░░░
└─ Disponibles: 150 | Gastados: 0 | Equivalencia: $1.50

Loaded (Platino):
├─ 👑 Mis Puntos
├─ 1500 puntos
├─ Nivel: Platino 👑
├─ Progreso: ██████████ (100%)
│ 🎉 ¡Nivel Máximo Alcanzado!
└─ Disponibles: 1500 | Gastados: 0 | Equivalencia: $15.00

Error:
├─ ❌ Error cargando puntos
```

---

## Debugging

### Logs

```
D/RewardsViewModel: Loading rewards for user: dyBCD...
D/RewardsViewModel: Rewards loaded: UserRewardsModel(...)
D/RewardsViewModel: Adding points from purchase: $100 USD
D/RewardsHelper: Calculated points: $100 USD = 10 points
D/RewardsViewModel: Points added successfully for user: dyBCD...
D/PointsCard: Rendering PointsCard with rewards: 150 puntos
```

---

## Limitaciones Actuales

⚠️ No hay UI para canjear puntos (backend funciona)  
⚠️ No hay historial visual de transacciones  
⚠️ No hay notificaciones de cambios de nivel

---

## Mejoras Futuras

✏️ UI para canjear puntos  
✏️ Pantalla de historial de transacciones  
✏️ Notificaciones push al subir de nivel  
✏️ Tablas de líderes (top usuarios por puntos)  
✏️ Bonificaciones por actividades especiales

---

**Estado**: ✅ Sistema de puntos completamente funcional y en tiempo real
