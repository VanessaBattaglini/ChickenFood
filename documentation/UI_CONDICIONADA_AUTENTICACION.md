# 🔒 UI Condicionada por Autenticación

**Fecha**: 3 de Junio, 2026  
**Estado**: ✅ COMPLETADO  
**Compilación**: BUILD SUCCESSFUL

---

## Objetivo

Mostrar información y funciones **SOLO** a usuarios autenticados:
- ✅ PointsCard (puntos, nivel, progreso)
- ✅ Botón Logout (TopBar)

Usuarios sin autenticar (`Empecemos`):
- ✅ Ver productos
- ✅ Buscar
- ✅ Agregar al carrito
- ❌ NO ver puntos
- ❌ NO botón logout

---

## Implementación

### 1. PointsCard - Ya Condicionado ✅

**En MainScreen (MainActivity.kt)**:

```kotlin
// Cargar puntos del usuario si está autenticado
val currentUser = AuthHelper.getCurrentUser()
if (currentUser != null && userRewards == null) {
    Log.d(TAG, "Loading rewards for user: ${currentUser.uid}")
    rewardsViewModel.loadUserRewards(currentUser.uid)
}

// ... en LazyColumn ...

// Mostrar PointsCard si el usuario está autenticado
if (currentUser != null) {  // ← Condición de autenticación
    item {
        PointsCard(
            userRewards = userRewards,
            modifier = Modifier
        )
    }
}
```

**Resultado**:
```
Usuario autenticado  → PointsCard visible ✅
Usuario sin auth    → PointsCard OCULTO ✅
```

---

### 2. Botón Logout - Ahora Condicionado ✅

**En MainScreen (MainActivity.kt)**:

```kotlin
topBar = {
    TopBar(
        modifier = Modifier.padding(top = 35.dp),
        showLogout = currentUser != null,  // ← Nuevo parámetro
        onLogoutClick = onLogoutClick
    )
}
```

**En TopBar.kt**:

```kotlin
@Composable
fun TopBar(
    modifier: Modifier = Modifier,
    showLogout: Boolean = false,  // ← Parámetro nuevo
    onMenuClick: () -> Unit = {},
    onNotificationClick: () -> Unit = {},
    onLogoutClick: () -> Unit = {}
) {
    Row(
        // ... código existing ...
        
        Row {
            IconButton(onClick = onNotificationClick) { ... }
            
            // Solo mostrar botón Logout si está autenticado
            if (showLogout) {  // ← Condición
                IconButton(onClick = onLogoutClick) { ... }
            }
        }
    )
}
```

**Resultado**:
```
Usuario autenticado  → Botón logout visible ✅
Usuario sin auth    → Botón logout OCULTO ✅
```

---

## Flujo de Usuario

### Caso 1: Usuario sin Autenticar ("Empecemos")

```
SplashScreen
    ↓
[Empecemos] button
    ↓
Dashboard abierto
├─ SearchBar visible ✅
├─ Banner visible ✅
├─ Categorías visible ✅
├─ PointsCard OCULTO ❌ (currentUser == null)
├─ Botón logout OCULTO ❌ (showLogout = false)
└─ TopBar: solo Settings + ChickenFood + Notifications
```

### Caso 2: Usuario Autenticado ("Inscribete")

```
SplashScreen
    ↓
[Inscribete] button
    ↓
Google Sign-In
    ↓
Firebase auth exitosa
    ↓
Dashboard abierto
├─ SearchBar visible ✅
├─ PointsCard visible ✅ (currentUser != null)
│  ├─ Muestra saldo
│  ├─ Muestra nivel
│  └─ Muestra progreso
├─ Banner visible ✅
├─ Categorías visible ✅
└─ TopBar: Settings + ChickenFood + Notifications + Logout ✅
   (showLogout = true)
```

---

## Cambios Exactos

### Archivo 1: MainActivity.kt

**Línea ~163** (Scaffold topBar):

```kotlin
// De:
topBar = {
    TopBar(
        modifier = Modifier.padding(top = 35.dp),
        onLogoutClick = onLogoutClick
    )
}

// A:
topBar = {
    TopBar(
        modifier = Modifier.padding(top = 35.dp),
        showLogout = currentUser != null,  // ← Agregar
        onLogoutClick = onLogoutClick
    )
}
```

### Archivo 2: TopBar.kt

**Línea ~30** (función TopBar):

```kotlin
// De:
@Composable
fun TopBar(
    modifier: Modifier = Modifier,
    onMenuClick: () -> Unit = {},
    onNotificationClick: () -> Unit = {},
    onLogoutClick: () -> Unit = {}
)

// A:
@Composable
fun TopBar(
    modifier: Modifier = Modifier,
    showLogout: Boolean = false,  // ← Agregar parámetro
    onMenuClick: () -> Unit = {},
    onNotificationClick: () -> Unit = {},
    onLogoutClick: () -> Unit = {}
)
```

**Línea ~79** (Row con botones):

```kotlin
// De:
Row {
    IconButton(onClick = onNotificationClick) { ... }
    IconButton(onClick = onLogoutClick) { ... }
}

// A:
Row {
    IconButton(onClick = onNotificationClick) { ... }
    if (showLogout) {  // ← Agregar condicional
        IconButton(onClick = onLogoutClick) { ... }
    }
}
```

---

## Lógica de Autenticación

### AuthHelper.getCurrentUser()

```kotlin
fun getCurrentUser(): FirebaseUser? {
    return FirebaseAuth.getInstance().currentUser
}
```

**Retorna**:
- `FirebaseUser` → Usuario autenticado ✅
- `null` → Usuario sin autenticar ❌

### Uso en MainScreen

```kotlin
val currentUser = AuthHelper.getCurrentUser()

when {
    currentUser != null → Usuario autenticado
    currentUser == null → Usuario sin autenticar
}
```

---

## Validación

### Checklist de Testing

- [ ] Abrir app → [Empecemos] → Dashboard
  - [ ] No ver PointsCard ✅
  - [ ] No ver botón Logout ✅
  - [ ] Ver SearchBar, Banner, Categorías ✅

- [ ] Volver a SplashScreen → [Inscribete] → Google Auth
  - [ ] Ver PointsCard con 0 puntos (primera vez) ✅
  - [ ] Ver botón Logout ✅
  - [ ] Hacer una compra → Gana puntos ✅
  - [ ] Volver a Dashboard → PointsCard actualizado ✅

- [ ] Logout
  - [ ] Vuelve a SplashScreen ✅
  - [ ] Dashboard sin PointsCard nuevamente ✅

---

## Compilación

✅ **BUILD SUCCESSFUL in 31s**
- 0 errores
- 0 warnings
- Todas las dependencias resueltas

---

## Resumen

| Elemento | Sin Auth | Con Auth |
|---------|----------|----------|
| SearchBar | ✅ Visible | ✅ Visible |
| Banner | ✅ Visible | ✅ Visible |
| Categorías | ✅ Visible | ✅ Visible |
| PointsCard | ❌ Oculto | ✅ Visible |
| Botón Logout | ❌ Oculto | ✅ Visible |

---

**Estado Final**: ✅ UI completamente condicionada por autenticación
