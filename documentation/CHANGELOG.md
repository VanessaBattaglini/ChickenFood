# CHANGELOG - Historial de Cambios

## [3.2] - 16 de Junio, 2024 - FIX: BADGE DEL CARRITO SE ACTUALIZA

### 🐛 Bug Arreglado
- ✅ **Badge del carrito en BottomBar no se actualizaba después de vaciar**
- ✅ **Causa**: MainScreen solo actualizaba cartItemCount una sola vez
- ✅ **Solución**: Activity.onResume() + Activity-Composable callback pattern

### 🔧 Mejoras Implementadas

#### Patrón: Activity-Composable Callback

```kotlin
// Activity nivel: Guardar callback
private var cartUpdateCallback: (() -> Unit)? = null

override fun onResume() {
    super.onResume()
    cartUpdateCallback?.invoke()  // ← Invoca desde Activity
}
```

```kotlin
// Composable nivel: Registrar callback
LaunchedEffect(Unit) {
    onScreenReady?.invoke {
        // Se ejecuta desde onResume
        cartItemCount = managmentCart.getListCart().size
    }
}
```

### 📝 Cambios de Código

#### Archivo: MainActivity.kt

**Cambio 1**: Variable global para callback
```kotlin
// Línea ~49
private var cartUpdateCallback: (() -> Unit)? = null
```

**Cambio 2**: Implementar onResume()
```kotlin
// Línea ~57
override fun onResume() {
    super.onResume()
    Log.d(TAG, "onResume called - updating cart count")
    cartUpdateCallback?.invoke()
}
```

**Cambio 3**: Pasar callback a MainScreen
```kotlin
// Línea ~73
setContent {
    MainScreen(
        ...
        onScreenReady = { callback ->
            cartUpdateCallback = callback
        }
    )
}
```

**Cambio 4**: Agregar parámetro a MainScreen
```kotlin
// Línea ~119
fun MainScreen(
    ...
    onScreenReady: ((callback: () -> Unit) -> Unit)? = null
) {
```

**Cambio 5**: Actualizar carrito en LaunchedEffect
```kotlin
// Línea ~157-172
val context = androidx.compose.ui.platform.LocalContext.current

LaunchedEffect(Unit) {
    val managmentCart = ManagmentCart(context)
    val newCount = managmentCart.getListCart().size
    cartItemCount = newCount
}

LaunchedEffect(Unit) {
    onScreenReady?.invoke {
        val managmentCart = ManagmentCart(context)
        val newCount = managmentCart.getListCart().size
        cartItemCount = newCount
        Log.d(TAG, "Cart counter updated from onResume: $newCount items")
    }
}
```

### 🎯 Flujo de Funcionamiento

```
Usuario vacía carrito en CartActivity
            ↓
Android llama onResume() en MainActivity
            ↓
cartUpdateCallback?.invoke() se ejecuta
            ↓
Lambda registrada en onScreenReady se ejecuta
            ↓
cartItemCount se actualiza a 0
            ↓
Compose se recompone
            ↓
Badge desaparece o se actualiza ✅
            ↓
BottomBar refleja estado correcto
```

### 📊 Antes vs. Después

| Acción | Antes | Después |
|--------|-------|---------|
| Vaciar carrito | ✅ Funciona | ✅ Funciona |
| Badge CartActivity | ✅ Se actualiza | ✅ Se actualiza |
| Badge MainActivity | ❌ No se actualiza | ✅ Se actualiza |
| Consistencia | ❌ No | ✅ Sí |

### 🧪 Compilación
```
✅ BUILD SUCCESSFUL
   - Compile: OK
   - Lint: OK
   - Tests: OK
   - Package: OK
   Tiempo: 1m 1s
```

### 📄 Documentación
- ✅ NUEVA: `13_FIX_BADGE_CARRITO.md`
- ✅ ACTUALIZADO: `README.md`
- ✅ ACTUALIZADO: `CHANGELOG.md` (este archivo)

### 🎯 Testing Completo

```
1. Agregar 3+ items al carrito
   ✅ Badge muestra "3"

2. Abrir CartActivity
   ✅ Badge sigue mostrando "3"

3. Click 🗑️ y confirmar vaciar
   ✅ UI muestra "Carrito vacío"
   ✅ Badge en CartActivity desaparece

4. Volver a MainActivity (onResume ejecutado)
   ✅ Badge DESAPARECE correctamente
   ✅ Estado consistente entre pantallas

5. Agregar items nuevamente
   ✅ Badge reaparece con cuenta correcta
```

### 🚀 Impacto

**Problema Resuelto**: Badge desactualizado después de vaciar carrito  
**Patrón Implementado**: Activity-Composable callback  
**Alcance**: MainActivity ↔ CartActivity sincronización  
**Reusabilidad**: Patrón aplicable a otros cambios de estado

### 📌 Puntos Clave

1. **onResume() siempre se llama** cuando vuelves a una Activity
2. **Callback pattern** permite comunicación Activity → Composable
3. **LaunchedEffect(Unit)** se ejecuta cada vez que se recompone
4. **cartItemCount** es el source of truth
5. **BottomBar** responde dinámicamente a cambios

### ✅ Estado Final

- ✅ Botón 🗑️ funciona (Fix v2)
- ✅ Badge se actualiza (Fix Badge)
- ✅ UI consistente entre pantallas
- ✅ Patrón documentado
- ✅ BUILD SUCCESSFUL
- ✅ Etapa 3 completada exitosamente

---

## [3.1] - 16 de Junio, 2024 - FIX BOTÓN VACIAR CARRITO V2

### 🐛 Bug Arreglado
- ✅ **Botón 🗑️ "Vaciar Carrito" no actualizaba UI correctamente**
- ✅ **Causa Raíz**: Múltiples capas de caché en Compose que no detectaban cambio
- ✅ **Solución**: Refresh trigger + ArrayList nueva referencia

### 🔧 Mejoras Implementadas

#### Cambio 1: Inicialización de CartScreen
```kotlin
// ANTES (Incorrecto)
var cartItems by remember { mutableStateOf(managmentCart.getListCart()) }

// DESPUÉS (Correcto)
var cartItems by remember { mutableStateOf(ArrayList(managmentCart.getListCart())) }
var refreshTrigger by remember { mutableStateOf(0) }  // ← NUEVO
```

#### Cambio 2: ChangeListener
```kotlin
// Ahora también incrementa refreshTrigger
override fun onChanged() {
    cartItems = ArrayList(managmentCart.getListCart())
    totalPrice = managmentCart.getTotalFee()
    refreshTrigger++  // ← NUEVO
}
```

#### Cambio 3: Confirm Button Dialog
```kotlin
// Ahora incrementa refreshTrigger
confirmButton = {
    onClick = {
        managmentCart.clearCart()
        cartItems = ArrayList(managmentCart.getListCart())
        totalPrice = managmentCart.getTotalFee()
        refreshTrigger++  // ← FUERZA recomposición
        showClearDialog = false
    }
}
```

### 📊 Flujo de Funcionamiento
```
[Usuario clicks 🗑️]
        ↓
[showClearDialog = true]
        ↓
[Dialog aparece con confirmación]
        ↓
[Usuario clicks "Sí, Vaciar"]
        ↓
[managmentCart.clearCart() ejecutado]
        ↓
[cartItems = ArrayList(empty list)]
[refreshTrigger++]  ← Fuerza recomposición
        ↓
✅ Dialog cierra
✅ Toast "Carrito limpiado"
✅ UI actualiza a "Carrito vacío"
✅ Botón 🗑️ desaparece
```

### 🎯 Patrón: Refresh Trigger

Este patrón es útil cuando:
- Cambios complejos de estado simultáneamente
- Compose cachea estados y no detecta cambio
- Necesitas garantizar recomposición

```kotlin
// Pattern reutilizable
var refreshTrigger by remember { mutableStateOf(0) }
fun triggerRecompose() { refreshTrigger++ }
```

### 📝 Cambios de Código
- **Archivo**: `CartActivity.kt`
- **Líneas modificadas**: ~143-146, ~153-160, ~298-309
- **Métodos**: `CartScreen()` Composable
- **Cambios totales**: 3 secciones principales

### 🧪 Testing Checklist
```
✅ Agregar 3+ items al carrito
✅ Ver que botón 🗑️ aparece
✅ Click en 🗑️ → Dialog aparece
✅ Click "Sí, Vaciar" → Cart se vacía inmediatamente
✅ Toast "Carrito limpiado" aparece
✅ UI muestra "Tu carrito está vacío"
✅ Botón 🗑️ desaparece
✅ Agregar nuevos items funciona
✅ Logcat muestra todos los logs correctamente
```

### 📄 Documentación
- ✅ NUEVA: `12_FIX_BOTON_VACIAR_V2.md` (guía completa)
- ✅ ACTUALIZADO: `README.md` (links y novedades)

### 🧪 Compilación
```
✅ BUILD SUCCESSFUL
   - Compile: OK
   - Lint: OK
   - Tests: OK
   - Package: OK
   Time: 1m 16s
```

### 🚀 Impacto
| Aspecto | Antes | Después |
|---------|-------|---------|
| **Funcionalidad** | ❌ No actualiza UI | ✅ Actualiza al instante |
| **Robustez** | Media | ✅ Alta |
| **Logging** | Básico | ✅ Con refreshTrigger |
| **Reusabilidad** | N/A | ✅ Patrón claro |

### 📌 Estado Final
- ✅ Botón 🗑️ funciona perfectamente
- ✅ UI se actualiza inmediatamente
- ✅ Dialog funciona correctamente
- ✅ Logging mejorado
- ✅ Patrón documentado para futuros usos
- ✅ BUILD SUCCESSFUL

---

## [3.0] - 16 de Junio, 2024 - ETAPA 3: SESIÓN PERSISTENTE Y FIREBASE TIMEOUTS

### 🌐 NUEVA FEATURE - Lectura Pública de Datos
- ✅ **Usuarios sin autenticación pueden ver productos, categorías e imágenes**
- ✅ **Configuradas reglas de Firebase** para acceso público
- ✅ **Búsqueda y detalle funcionan sin login**
- ✅ **Checkout aún requiere autenticación (seguro)**

### 🛡️ Reglas Firebase Implementadas
```json
{
  "banners": { ".read": true, ".write": false },
  "category": { ".read": true, ".write": false },
  "foods": { ".read": true, ".write": false },
  "orders": { ".read": "auth != null", ".write": "auth != null" },
  "rewards": { ".read": "auth != null", ".write": "auth != null" },
  "users": { ".read": "auth != null && auth.uid == $uid", ".write": "auth != null && auth.uid == $uid" }
}
```

### 📚 Documentación
- ✅ **NUEVA**: `03_ACCESO_PUBLICO.md` - Guía completa de acceso público
- ✅ **ACTUALIZADO**: `README.md` - Índice con nueva feature
- ✅ **ACTUALIZADO**: `00_INICIO.md` - Funcionalidades organizadas
- ✅ **ACTUALIZADO**: `02_AUTENTICACION.md` - Autenticación opcional
- ✅ **RENUMERADOS**: Todos los documentos posteriores
- ✅ **ELIMINADOS**: 4 documentos de hotfixes obsoletos

### 🗑️ Documentos Eliminados (Obsoletos)
```
❌ UI_CONDICIONADA_AUTENTICACION.md    - Información integrada en 02_AUTENTICACION
❌ FIX_AUTH_STATE_PERSISTENCE.md       - Hotfix ya resuelto en v2.1
❌ RESUMEN_HOTFIX_3.md                 - Hotfix ya resuelto en v2.1
❌ TEST_AUTH_FIX.md                    - Hotfix ya resuelto en v2.1
```

### 📊 Cambios en Funcionalidades
```
ANTES (v2.1):
- "Empecemos" → Dashboard (limitado)
- "Inscribete" → Dashboard completo (con puntos)

DESPUÉS (v2.2):
- "Empecemos" → Dashboard completo (sin puntos, sin checkout)
- "Inscribete" → Dashboard completo (con puntos, con checkout)
```

### 🎯 Impacto
| Aspecto | Antes | Después |
|--------|-------|---------|
| Usuarios sin auth | ❌ No pueden ver productos | ✅ Ven todo excepto comprar |
| Imágenes públicas | ❌ Requieren auth | ✅ Sin auth |
| Búsqueda | ❌ Requiere auth | ✅ Sin auth |
| Carrito | ❌ Requiere auth | ✅ Sin auth |
| Checkout | - | ✅ Requiere auth |
| Puntos | - | ✅ Solo si autenticado |

### ✅ Estado
- ✅ BUILD SUCCESSFUL
- ✅ 0 errores
- ✅ Código sin cambios (solo reglas Firebase)
- ✅ Documentación reorganizada
- ✅ Todos los links actualizados

---

### 🐛 Bug Arreglado
- ✅ **PointsCard y Logout visibles para usuarios NO autenticados** (CRÍTICO)
- ✅ **Causa**: Uso de `rememberSaveable` para auth state
  - `rememberSaveable` persiste estado entre recomposiciones
  - Estado viejo se restauraba después de logout + navegación a MainActivity
- ✅ **Solución**: Verificación fresca de Firebase sin persistencia local

### 📝 Cambios Principales
```
MainActivity.kt (Lines 87-99):

ANTES ❌:
  var currentUser by rememberSaveable { mutableStateOf<String?>(null) }
  LaunchedEffect(Unit) {
      currentUser = AuthHelper.getCurrentUser()?.uid
  }

DESPUÉS ✅:
  val currentUser = AuthHelper.getCurrentUser()?.uid
  LaunchedEffect(currentUser) {
      if (currentUser != null) {
          rewardsViewModel.loadUserRewards(currentUser)
      }
  }
```

### 🎯 Comportamiento Corregido
```
✅ Usuario sin auth ("Empecemos"):
   - PointsCard ❌ (oculto)
   - Logout ❌ (oculto)

✅ Usuario autenticado ("Inscribete"):
   - PointsCard ✅ (visible)
   - Logout ✅ (visible)

✅ Después de logout:
   - Usuario regresa a SplashScreen
   - Clicks "Empecemos" → MainActivity SIN state guardado
   - PointsCard/Logout quedan ocultos ✅
```

### 🧪 Compilación
- ✅ BUILD SUCCESSFUL (0 errores)
- ✅ No diagnostics found

### 📄 Documentación
- ✅ Agregado: `FIX_AUTH_STATE_PERSISTENCE.md` (guía detallada)

---

## Hotfix 2 (3 de Junio, 2026) - UI Condicionada por Autenticación

### ✨ Mejora Implementada
- ✅ **PointsCard**: Solo visible para usuarios autenticados
- ✅ **Botón Logout**: Solo visible para usuarios autenticados
- ✅ Dashboard diferente según auth status

### 📝 Cambios
```
MainScreen (MainActivity.kt):
  + showLogout = currentUser != null
  + Pasar parámetro a TopBar

TopBar.kt:
  + showLogout: Boolean = false (nuevo parámetro)
  + if (showLogout) { mostrar botón logout }
```

### 🎯 Resultado
```
Usuario sin auth ("Empecemos"):
  - SearchBar ✅
  - Banner ✅
  - Categorías ✅
  - PointsCard ❌ (oculto)
  - Logout ❌ (oculto)

Usuario autenticado ("Inscribete"):
  - SearchBar ✅
  - PointsCard ✅ (visible)
  - Banner ✅
  - Categorías ✅
  - Logout ✅ (visible)
```

### 🧪 Compilación
- ✅ BUILD SUCCESSFUL (31s, 0 errores)

### 📄 Documentación
- ✅ Agregado: `UI_CONDICIONADA_AUTENTICACION.md`
- ✅ Actualizado: `README.md`

---

## Hotfix 1 (3 de Junio, 2026) - Fix: Bug al Navegar desde Búsqueda

### 🐛 Bug Arreglado
- ✅ **NullPointerException** al clickear resultado de búsqueda
- ✅ **Causa**: Mismatch de Intent keys ("food" vs "object")
- ✅ **Solución**: Cambiar key a "food" + agregar null validation

### 📝 Cambios
```
DetailEachFoodActivity.kt:35-37
- item = intent.getSerializableExtra("object") as FoodModel  ❌
+ val foodExtra = intent.getSerializableExtra("food") as? FoodModel
+ if (foodExtra == null) { finish(); return }
+ item = foodExtra  ✅
```

### 🧪 Compilación
- ✅ BUILD SUCCESSFUL (22s, 0 errores)
- ✅ Navegación de búsqueda a detail funciona
- ✅ Sin crashes

### 📄 Documentación
- ✅ Agregado: `FIX_SEARCH_TO_DETAIL_BUG.md`

---

## Versión 2.0 (2 de Junio, 2026) - LIMPIEZA MASIVA DE DOCUMENTACIÓN

### 📚 Documentación
- ✅ **ELIMINADOS**: 50+ archivos .md obsoletos y repetidos
- ✅ **CREADOS**: 8 nuevos documentos organizados y estructurados
- ✅ **REORGANIZADO**: Estructura clara por usuario (inicio, autenticación, características, técnica, errores)
- ✅ **ACTUALIZADO**: Todos los documentos con cambios más recientes

### Nuevos Documentos
```
00_INICIO.md                    - Portada y navegación
01_INICIO_RAPIDO.md             - Cómo usar en 5 minutos
02_AUTENTICACION.md             - Google Sign-In, tokens, JWT
03_BUSCADOR.md                  - Búsqueda en tiempo real
04_CARRITO_COMPRAS.md           - Agregar/eliminar/pagar
05_SISTEMA_PUNTOS.md            - Cashback, niveles, conversión
06_ARQUITECTURA_TECNICA.md      - MVVM, Koin, Firebase, Compose
07_SOLUCION_ERRORES.md          - 10 errores comunes y soluciones
README.md                       - Índice y punto de entrada
CHANGELOG.md                    - Este archivo
```

### Eliminados (50+ archivos)
```
AUTENTICACION.md
USUARIO_NO_PREMIUM.md
USUARIO_PREMIUM.md
SISTEMA_RECOMPENSAS.md
CONFIGURACION_INICIAL.md
(+ 45 más)
```

### Organización
- Antes: 60+ documentos caóticos sin estructura
- Después: 9 documentos limpios con propósito definido
- Estructura: Por usuario/tema, no por tecnología

---

## Versión 1.3 (Antes de Limpieza)

### 🔄 Sistema de Búsqueda
- ✅ Implementado buscador funcional en tiempo real
- ✅ SearchBar.kt: Búsqueda case-insensitive
- ✅ MainViewModel.searchFoods(): Busca en todas las categorías
- ✅ SearchResultItem: Dropdown con resultados
- ✅ Navegación automática a DetailScreen

### 📊 Componentes
- ✅ PointsCard en Dashboard (si autenticado)
- ✅ PointsCard muestra: saldo, nivel, progreso
- ✅ PointsCard actualización en tiempo real
- ✅ Emojis por nivel: 👤 Regular, 🥉 Bronce, 🥈 Plata, 🏆 Oro, 👑 Platino

### 🏗️ Métodos Agregados a RewardsHelper
- ✅ `getLevelProgress(totalPoints)`: Progreso hacia siguiente nivel
- ✅ `getPointsToNextLevel(totalPoints)`: Puntos faltantes

### 🧪 Compilación
- ✅ BUILD SUCCESSFUL (0 errores)
- ✅ compileDebugKotlin exitoso
- ✅ Todas las dependencias resueltas

---

## Versión 1.2 (Antes)

### 🔐 Autenticación
- ✅ Google Sign-In passwordless implementado
- ✅ SplashActivity: "Empecemos" y "Inscribete"
- ✅ SignUpActivity: Google Account Selector
- ✅ Logout devuelve a SplashScreen

### 💰 Sistema de Puntos
- ✅ 5 niveles de usuario (Regular a Platino)
- ✅ 10-15% cashback según nivel
- ✅ UserRewardsModel, OrderModel, PointsTransactionModel
- ✅ RewardsViewModel, OrderViewModel, TokenViewModel
- ✅ Firebase Realtime Database integración

### 🛒 Carrito
- ✅ ManagmentCart.clearCart() agregado
- ✅ CartActivity llama clearCart() después de pagar
- ✅ Fix: Productos con qty>1 se eliminan completamente

### 🐛 Fixes
- ✅ Fix CallbackFlow memory leak en TokenRepositoryImpl
- ✅ Fix Cart issues (no se limpiaba, items no se eliminaban correctamente)
- ✅ Fix response design con mejor padding

---

## Roadmap Futuro

### Próximas Características
- [ ] Canjear puntos (UI + flow)
- [ ] Historial de transacciones (visual)
- [ ] Notificaciones push
- [ ] Favoritos/Wishlist
- [ ] Historial de órdenes
- [ ] Rating de productos

### Optimizaciones
- [ ] Caché de búsquedas
- [ ] Historial de búsquedas
- [ ] Sugerencias automáticas
- [ ] Búsqueda fonética
- [ ] Tablas de líderes

---

## Estadísticas

### Antes (v1.0-v1.2)
- 📄 **Documentos**: 60+
- 🔀 **Estructura**: Caótica
- 🔄 **Duplicados**: Muchos
- ⏱️ **Tiempo para entender**: 30+ minutos

### Después (v2.0)
- 📄 **Documentos**: 9
- 🔀 **Estructura**: Clara
- 🔄 **Duplicados**: Ninguno
- ⏱️ **Tiempo para entender**: 5-10 minutos

### Mejoras
- 🗑️ **Eliminados**: 50+ archivos (83% reducción)
- 📚 **Mejor organización**: Por usuario/tema
- 🎯 **Más útil**: Enfocado en lo que importa
- 🔍 **Fácil de encontrar**: Índice claro

---

## Detalles Técnicos del Cleanup

### Criterios de Eliminación
1. **Duplicados**: Si la información estaba en 2+ documentos
2. **Obsoletos**: Si el código cambió pero el doc no
3. **Innecesarios**: Si eran notas de trabajo o logs
4. **Redundantes**: Si el info estaba en otro doc actualizado

### Criterios de Mantención
1. **Únicos**: Información no repetida en otros lados
2. **Actualizados**: Reflejan el estado actual del código
3. **Esenciales**: Cubren: inicio, auth, características, técnica, errores

---

## Validaciones Post-Cleanup

✅ Todos los .md están en `/documentation/`  
✅ No hay .md duplicados  
✅ No hay .md obsoletos  
✅ README.md apunta a documentos existentes  
✅ Estructura clara y naveg able  
✅ 10 documentos finales:
   - 1 índice (README.md)
   - 1 changelog (este)
   - 1 portada (00_INICIO.md)
   - 1 inicio rápido (01_INICIO_RAPIDO.md)
   - 5 características (02-05)
   - 1 técnica (06)
   - 1 errores (07)
   - 2 fixes (FIX_SEARCH_TO_DETAIL_BUG.md, FIX_AUTH_STATE_PERSISTENCE.md)

---

## Commits Relacionados

```
commit: "Refactor: Clean documentation and reorganize into 8 main docs"
files changed: +50 (new), -50 (old)
net: 0 (replaced, not added)

commit: "Fix: NullPointerException on search result navigation"
files changed: 1 (DetailEachFoodActivity.kt)

commit: "Fix CRITICAL: Auth state persistence in Dashboard"
files changed: 1 (MainActivity.kt)
```

---

**Última actualización**: 3 de Junio, 2026 - 14:45  
**Realizado por**: Daniel Alvarado  
**Impacto**: Eliminadas vulnerabilidades críticas de UX en autenticación
