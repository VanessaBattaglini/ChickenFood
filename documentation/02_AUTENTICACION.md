# 🔐 Autenticación - Google Sign-In Passwordless

**Estado**: ✅ Completamente funcional  
**Tipo**: Passwordless (sin contraseña)  
**Proveedor**: Google + Firebase  
**Obligatoria para**: Compras y acceso a puntos
**Opcional para**: Ver productos, imágenes, búsqueda

---

## ¿Cómo Funciona?

### Flujo Simple

```
Usuario abre app
    ↓
┌─ Opción 1: "Empecemos" ─┐      ┌─ Opción 2: "Inscribete" ─┐
│ Sin autenticación       │      │ Con autenticación       │
│ Ver productos ✅        │      │ Google Sign-In          │
│ Sin puntos ❌          │      │ Firebase autentica      │
│ Sin checkout ❌        │      │ Dashboard con puntos ✅ │
└─────────────────────────┘      └─────────────────────────┘
```

### Sin Contraseña

- ✅ No necesita contraseña
- ✅ Usa cuentas ya autenticadas en el dispositivo
- ✅ Rápido y seguro
- ✅ Puede navegar sin login

---

## Componentes Principales

### 1. SplashActivity.kt

**Ubicación**: `presentation/activity/splash/`

**Funcionalidad**:
- Muestra 2 botones: "Empecemos" y "Inscribete"
- "Empecemos" → Dashboard sin auth
- "Inscribete" → SignUpActivity (Google Auth)

**Código clave**:
```kotlin
// Botón Empecemos
Button(onClick = {
    val intent = Intent(this, MainActivity::class.java)
    startActivity(intent)
    finish()
})

// Botón Inscribete
Button(onClick = {
    val intent = Intent(this, SignUpActivity::class.java)
    startActivity(intent)
})
```

---

### 2. SignUpActivity.kt

**Ubicación**: `presentation/activity/auth/`

**Funcionalidad**:
- Google Sign-In launcher
- Recibe JWT de Google
- Convierte JWT a Credential
- Autentica en Firebase
- Guarda tokens en UserTokenModel
- Navega a MainActivity

**Flujo de Autenticación**:
```kotlin
// 1. Lanzo Google Sign-In
googleSignInLauncher.launch(signInIntent)

// 2. Google devuelve JWT en onActivityResult
val account = GoogleSignIn.getSignedInAccountFromIntent(data)
val idToken = account.idToken

// 3. Convierto JWT a Credential
val credential = GoogleAuthProvider.getCredential(idToken, null)

// 4. Autentico en Firebase
firebaseAuth.signInWithCredential(credential)

// 5. Guardo tokens
tokenViewModel.saveUserToken(user, idToken, firebaseIdToken)

// 6. Navego a Home
MainActivity.startActivity()
```

---

### 3. AuthHelper.kt

**Ubicación**: `helper/`

**Funcionalidad**: Singleton con métodos de autenticación

**Métodos principales**:
```kotlin
// Obtener usuario actual
fun getCurrentUser(): FirebaseUser?

// Validar si está autenticado
fun isUserLoggedIn(): Boolean

// Sign out (logout)
fun signOut()

// Obtener UID del usuario
fun getCurrentUserId(): String?
```

---

### 4. TokenViewModel.kt

**Ubicación**: `presentation/viewModel/`

**Funcionalidad**: Gestionar tokens y autenticación

**Estados**:
```kotlin
val tokenState: StateFlow<TokenState>
val isLoading: StateFlow<Boolean>
val error: StateFlow<String?>
```

**Métodos**:
```kotlin
// Guardar token de usuario
fun saveUserToken(user: FirebaseUser, idToken: String, firebaseIdToken: String)

// Validar token
fun isTokenValid(userId: String)

// Actualizar último login
fun updateLastLogin(userId: String)

// Revocar token (logout)
fun revokeToken(userId: String)
```

---

## Modelos de Datos

### UserTokenModel

```kotlin
data class UserTokenModel(
    val userId: String = "",                    // UID de Firebase
    val googleIdToken: String = "",             // JWT de Google
    val firebaseIdToken: String = "",           // ID Token de Firebase
    val refreshToken: String = "",              // Para renovar sesión
    val userEmail: String = "",
    val userName: String = "",
    val userPhotoUrl: String = "",
    val createdDate: Long = 0,
    val lastLoginDate: Long = 0
)
```

**Ubicación en Firebase**:
```
users/
  {userId}/
    tokens/
      googleIdToken: "..."
      firebaseIdToken: "..."
      refreshToken: "..."
      userEmail: "..."
```

---

## Flujo de Tokens

### 1. Google Sign-In

```
Google Sign-In Button
    ↓
Google Account Selector (si hay múltiples cuentas)
    ↓
Usuario selecciona cuenta
    ↓
Google devuelve JWT (ID Token)
    ↓
JWT = token de identidad de Google
```

**JWT estructura**:
```
eyJhbGc...     .eyJpc3M...     .QJWEB...
│ Header      │ Payload      │ Signature
│ alg: RS256  │ iss: Google  │ Firmado por Google
```

### 2. Conversión a Firebase Credential

```kotlin
// Una línea mágica
val credential = GoogleAuthProvider.getCredential(idToken, null)

// Firebase valida JWT:
// ✅ Firma válida (firmado por Google)
// ✅ No expirado
// ✅ Audiencia correcta
```

### 3. Autenticación en Firebase

```
FirebaseAuth.signInWithCredential(credential)
    ↓
Firebase valida credential
    ↓
Firebase crea usuario si no existe
    ↓
Firebase genera Firebase ID Token
    ↓
Firebase genera Refresh Token
    ↓
Usuario autenticado ✅
```

### 4. Almacenamiento de Tokens

```
// En Firebase Realtime Database
/users/{userId}/tokens
  ├─ googleIdToken: "JWT de Google"
  ├─ firebaseIdToken: "Token de Firebase"
  ├─ refreshToken: "Para renovar sesión"
  └─ lastLoginDate: 1622505600000
```

---

## Seguridad

### ✅ Qué está protegido

1. **JWT**: Firmado por Google (no se puede falsificar)
2. **Firebase**: Valida JWT antes de crear sesión
3. **Tokens**: Se guardan en Firebase Realtime Database (protegido por reglas)
4. **Refresh**: Se puede renovar sesión con Refresh Token

### ⚠️ Notas de Seguridad

1. **No guardes tokens en SharedPreferences** (inseguro)
2. **No logs con tokens** (exponen información)
3. **Usa HTTPS siempre** (encriptado)
4. **Revoca sesión en logout** (invalida tokens)

---

## Flujo Completo

### Caso 1: Usuario sin Autenticarse ("Empecemos")

```
SplashScreen
    ↓ [Empecemos]
Dashboard abierto
├─ Ver productos ✅
├─ Buscar productos ✅
├─ Ver imágenes ✅
├─ Agregar al carrito ✅
├─ Ver carrito ✅
├─ PointsCard OCULTO ❌
├─ Botón Logout OCULTO ❌
├─ Intenta checkout → Redirige a SignUp
```

### Caso 2: Primera Vez (Nuevo Usuario)

```
SplashScreen
    ↓ [Inscribete]
SignUpActivity
    ↓ [Google Sign-In]
Google Account Selector
    ↓ [Selecciona cuenta]
Google Login (si necesita)
    ↓ Google devuelve JWT
SignUpActivity recibe JWT
    ↓ Convierte a Credential
Firebase.signInWithCredential()
    ↓ Firebase crea usuario
Firebase devuelve FirebaseUser
    ↓ TokenViewModel.saveUserToken()
Guardar en /users/{uid}/tokens
    ↓ Navega a MainActivity
Dashboard muestra:
├─ SearchBar ✅
├─ PointsCard con 0 puntos ✅
├─ Botón Logout ✅
├─ Banner ✅
├─ Categorías ✅
└─ Puede completar compra ✅
```

### Caso 3: Usuario Devolviendo (Cuenta Existente)

```
SplashScreen
    ↓ [Inscribete]
SignUpActivity
    ↓ [Google Sign-In]
Google Account Selector
    ↓ [Selecciona cuenta]
⚡ Sin login (ya autenticado en dispositivo)
    ↓ Google devuelve JWT
SignUpActivity recibe JWT
    ↓ Convierte a Credential
Firebase.signInWithCredential()
    ↓ Firebase reconoce usuario
    ↓ Carga datos existentes
    ↓ Devuelve FirebaseUser
Dashboard con puntos guardados ✅
```

---

## Logout

### Proceso

```
Usuario toca Logout (TopBar)
    ↓
MainActivity.logout()
    ↓
AuthHelper.signOut()
    ↓
FirebaseAuth.signOut()
    ↓ Invalida sesión
tokenViewModel.revokeToken()
    ↓ Revoca tokens
Intent a SplashActivity
    ↓
SplashScreen nuevamente
```

---

## Debugging

### Logs Importante

```
// Usuario autenticado
D/AuthHelper: Current user: dyBCD...@gmail.com

// Login exitoso
D/SignUpActivity: Firebase Auth Success for user: dyBCD...

// Token guardado
D/TokenViewModel: Saving token for user: dyBCD...

// Logout
D/MainActivity: Logout clicked
```

### Verificar Autenticación

```kotlin
// En MainActivity
val currentUser = AuthHelper.getCurrentUser()
if (currentUser != null) {
    Log.d(TAG, "User: ${currentUser.email}")
    Log.d(TAG, "UID: ${currentUser.uid}")
    Log.d(TAG, "Authenticated: true")
} else {
    Log.d(TAG, "Not authenticated")
}
```

---

## Errores Comunes

### Error: ApiException 10

**Causa**: Web Client ID no configurado  
**Solución**: Ver [07_CONFIGURACION_INICIAL.md](07_CONFIGURACION_INICIAL.md)

```
E/SignUpActivity: Google Sign-Up failed: 10
```

### Error: signInWithCredential failed

**Causa**: JWT inválido o expirado  
**Solución**: Reintentar login

```
E/SignUpActivity: Firebase Auth failed: [ERROR ...]
```

### Error: awaitClose should be used

**Causa**: CallbackFlow sin cleanup  
**Solución**: Ya está arreglado en TokenRepositoryImpl

---

## Configuración Requerida

### 1. Firebase Project
- Crear proyecto en Firebase Console
- Habilitar Firebase Authentication
- Habilitar Google Sign-In

### 2. Google Cloud Console
- Crear OAuth 2.0 Web Client ID
- Descargar `google-services.json`

### 3. Código
- `strings.xml`: Agregar Web Client ID
- `build.gradle`: Agregar Google Play Services
- `AndroidManifest.xml`: Agregar permisos

---

## Próximos Pasos

1. **Verificar configuración**: [07_CONFIGURACION_INICIAL.md](07_CONFIGURACION_INICIAL.md)
2. **Si hay errores**: [08_SOLUCION_ERRORES.md](08_SOLUCION_ERRORES.md)
3. **Entender puntos**: [05_SISTEMA_PUNTOS.md](05_SISTEMA_PUNTOS.md)

---

**Estado**: ✅ Autenticación completamente funcional y passwordless
