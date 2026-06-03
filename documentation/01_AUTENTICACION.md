# 🔐 Flujo de Autenticación con Google Sign-In

## Resumen

ChickenFood ofrece autenticación sin contraseña usando Google Sign-In. Los usuarios pueden acceder a la app de dos formas:

1. **"Empecemos"** - Acceso sin autenticación (Usuario No Premium)
2. **"Inscribete"** - Autenticación con Google (Usuario Premium)

## Flujo Completo: Paso a Paso

### 1️⃣ SplashActivity - Pantalla de Bienvenida

El usuario abre la app y ve dos botones:

```
┌─────────────────────────────────┐
│      ChickenFood                │
│                                 │
│   Logo + Bienvenida             │
│                                 │
│  ┌──────────────────────────┐  │
│  │   🍗 Empecemos           │  │
│  │ (Sin autenticación)      │  │
│  └──────────────────────────┘  │
│                                 │
│  ┌──────────────────────────┐  │
│  │   👤 Inscribete          │  │
│  │ (Con Google)             │  │
│  └──────────────────────────┘  │
└─────────────────────────────────┘
```

**Clase:** `SplashActivity.kt`
**Lógica:**
```kotlin
// Si hay token válido guardado
if (AuthHelper.isAuthenticated()) {
    navigateToDashboard() // Va al Dashboard Premium
} else {
    showSplashScreen() // Muestra los dos botones
}
```

### 2️⃣ Opción 1: "Empecemos" (Sin Autenticación)

Si el usuario hace clic en "Empecemos":

```kotlin
// SplashActivity.kt
private fun handleGetStartedClick() {
    val intent = Intent(this, MainActivity::class.java)
    startActivity(intent)
    finish()
}
```

**Resultado:** Accede al Dashboard como Usuario No Premium

---

### 3️⃣ Opción 2: "Inscribete" (Con Google)

Si el usuario hace clic en "Inscribete":

```
┌──────────────────────────────────┐
│      SignUpActivity              │
│                                  │
│      Inscribete                  │
│                                  │
│  ┌───────────────────────────┐  │
│  │  🔷 Continuar con Google  │  │
│  └───────────────────────────┘  │
│                                  │
└──────────────────────────────────┘
                ↓
    Abre selector de cuentas Google
```

**Clase:** `SignUpActivity.kt`

**Configuración:**
```kotlin
private fun setupGoogleSignIn() {
    val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
        .requestIdToken(getString(R.string.default_web_client_id))
        .requestEmail()
        .build()
    
    googleSignInClient = GoogleSignIn.getClient(this, gso)
}

private fun signUpWithGoogle() {
    val signInIntent = googleSignInClient.signInIntent
    googleSignInLauncher.launch(signInIntent)
}
```

### 4️⃣ Selector de Cuentas Google

Google muestra las cuentas autenticadas en el dispositivo:

```
┌────────────────────────────────┐
│   Selecciona una cuenta         │
├────────────────────────────────┤
│ 👤 user1@gmail.com             │
│    Cuenta principal             │
├────────────────────────────────┤
│ 👤 user2@gmail.com             │
│    Secundaria                   │
├────────────────────────────────┤
│ ➕ Agregar otra cuenta          │
└────────────────────────────────┘
```

**Característica:** Passwordless
- Si la cuenta ya está autenticada en el dispositivo → **NO pide contraseña**
- Si es la primera vez → Pide permiso una sola vez

### 5️⃣ Firebase Authentication

Cuando el usuario selecciona una cuenta, Google devuelve un JWT (ID Token):

```
User selects account (Google)
    ↓
Google returns JWT (ID Token)
    ↓
Convert JWT to Firebase Credential:
GoogleAuthProvider.getCredential(idToken, null)
    ↓
Send Credential to Firebase:
firebaseAuth.signInWithCredential(credential)
    ↓
Firebase verifies JWT with Google's public key
    ↓
Firebase creates user account
    ↓
Firebase generates Firebase ID Token
```

**Código:**
```kotlin
private fun firebaseAuthWithGoogle(idToken: String) {
    val credential = GoogleAuthProvider.getCredential(idToken, null)
    firebaseAuth.signInWithCredential(credential)
        .addOnCompleteListener { task ->
            if (task.isSuccessful) {
                val user = firebaseAuth.currentUser
                // Usuario autenticado exitosamente
                saveTokenAndNavigate(user, idToken)
            } else {
                // Error en autenticación
                showError(task.exception?.message)
            }
        }
}
```

### 6️⃣ Guardar Tokens

Después de autenticar, se guardan los tokens en Firebase Realtime Database:

```
users/{userId}/tokens/{tokenId}/
├── userId: "abc123xyz"
├── email: "user@gmail.com"
├── googleIdToken: "eyJhbGc..." (JWT de Google)
├── firebaseToken: "eyJhbGc..." (Token de Firebase)
├── displayName: "John Doe"
├── photoUrl: "https://..."
├── createdAt: 1717000000000
├── lastLoginAt: 1717000000000
├── isActive: true
└── expiresAt: 0
```

**Métodos utilizados:**
```kotlin
// En SignUpActivity
val userToken = UserTokenModel(
    userId = user.uid,
    email = user.email ?: "",
    googleIdToken = idToken,
    firebaseToken = firebaseToken,
    displayName = user.displayName ?: "",
    photoUrl = user.photoUrl?.toString() ?: "",
    createdAt = System.currentTimeMillis(),
    lastLoginAt = System.currentTimeMillis(),
    expiresAt = 0L,
    isActive = true
)

// Guardar usando TokenViewModel
tokenViewModel.saveUserToken(userToken)
```

**Componentes:**
- `TokenViewModel` - Gestiona el guardado
- `TokenRepository` - Accede a Firebase
- `UserTokenModel` - Modelo de datos

### 7️⃣ Navegación

**Resultado:** Toast "Autenticación exitosa" + Navega a MainActivity

```kotlin
private fun navigateToDashboard() {
    val intent = Intent(this, MainActivity::class.java)
    startActivity(intent)
    finish()
}
```

---

## 📊 Diagrama Completo

```
SplashActivity
    ├─ Usuario hace clic "Empecemos"
    │   └─ MainActivity (Sin autenticación) ← Usuario No Premium
    │
    └─ Usuario hace clic "Inscribete"
        └─ SignUpActivity
            ├─ Selector de cuentas Google
            └─ Usuario selecciona cuenta
                ├─ Google devuelve JWT
                ├─ Firebase autentica
                ├─ Se guardan tokens
                └─ MainActivity (Con autenticación) ← Usuario Premium
```

---

## 🔑 Datos Clave

| Concepto | Valor | Descripción |
|----------|-------|-------------|
| **Tipo de Auth** | Passwordless | Sin contraseña |
| **Proveedor** | Google Sign-In | OAuth 2.0 |
| **Almacenamiento** | Firebase Auth + DB | Seguro y escalable |
| **Tokens guardados** | Google JWT + Firebase Token | Para validación |
| **Multi-dispositivo** | ✅ Soportado | Un usuario puede usar múltiples dispositivos |

---

## 🔄 Logout

Cuando el usuario hace clic en el botón logout (en TopBar):

```kotlin
// TopBar.kt
private fun handleLogout() {
    AuthHelper.logout()
    tokenViewModel.logout(userId)
    val intent = Intent(this, SplashActivity::class.java)
    startActivity(intent)
    finish()
}
```

**Qué sucede:**
1. Se marca el token como inactivo en Firebase
2. Se cierra sesión en Firebase Auth
3. Se retorna a SplashActivity
4. Los botones "Empecemos" / "Inscribete" aparecen nuevamente

---

## ⚠️ Errores Comunes

### ApiException: 10
**Causa:** Web Client ID no configurado correctamente
**Solución:** Ver [Solución de Errores](./06_SOLUCION_ERRORES.md)

### "Cuenta de Google no disponible"
**Causa:** No hay cuentas Google autenticadas en el dispositivo
**Solución:** 
1. Abre Configuración → Cuentas
2. Agrega una cuenta Google
3. Intenta nuevamente

---

## 🛡️ Seguridad

✅ **Passwordless:** No se transmiten contraseñas
✅ **JWT Verificado:** Firebase verifica la firma del JWT con Google
✅ **HTTPS:** Todas las comunicaciones cifradas
✅ **Tokens Seguros:** Se guardan en Firebase con encriptación en tránsito
✅ **Session Management:** Tokens se renuevan automáticamente

---

## 🎯 Resumen

1. Usuario abre app → SplashActivity
2. Elige "Empecemos" (sin auth) O "Inscribete" (con auth)
3. Si "Inscribete" → Google Sign-In selector
4. Usuario selecciona cuenta (sin contraseña si ya autenticada)
5. Firebase verifica y autentica
6. Se guardan tokens en Firebase
7. Navega a MainActivity (Dashboard)
8. Usuario ve su perfil y puntos

---

**Componentes Relacionados:**
- [Guía del Usuario No Premium](./02_USUARIO_NO_PREMIUM.md)
- [Guía del Usuario Premium](./03_USUARIO_PREMIUM.md)
- [Sistema de Recompensas](./04_SISTEMA_RECOMPENSAS.md)

