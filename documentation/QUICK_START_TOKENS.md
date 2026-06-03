# Quick Start: Gestión de Tokens de Google

## 🚀 Inicio Rápido

### 1. Obtener Google ID Token
```kotlin
// En SignUpActivity
private val googleSignInLauncher = registerForActivityResult(
    ActivityResultContracts.StartActivityForResult()
) { result ->
    val task = GoogleSignIn.getSignedInAccountFromIntent(result.data)
    val account = task.getResult(ApiException::class.java)
    
    // ✅ Google ID Token aquí
    val googleIdToken = account.idToken
    firebaseAuthWithGoogle(googleIdToken!!)
}
```

### 2. Autenticar con Firebase
```kotlin
private fun firebaseAuthWithGoogle(idToken: String) {
    val credential = GoogleAuthProvider.getCredential(idToken, null)
    firebaseAuth.signInWithCredential(credential)
        .addOnCompleteListener(this) { task ->
            if (task.isSuccessful) {
                // ✅ Obtener Firebase ID Token
                firebaseAuth.currentUser?.getIdToken(false)?.addOnCompleteListener { tokenTask ->
                    if (tokenTask.isSuccessful) {
                        val firebaseToken = tokenTask.result?.token ?: ""
                        saveUserTokens(idToken, firebaseToken)
                    }
                }
            }
        }
}
```

### 3. Guardar Tokens en Base de Datos
```kotlin
private fun saveUserTokens(googleIdToken: String, firebaseToken: String) {
    val userId = AuthHelper.getUserId() ?: return
    
    val userToken = UserTokenModel(
        userId = userId,
        email = AuthHelper.getUserEmail() ?: "",
        googleIdToken = googleIdToken,
        firebaseToken = firebaseToken,
        refreshToken = firebaseToken,
        displayName = AuthHelper.getUserName() ?: "",
        photoUrl = AuthHelper.getUserPhotoUrl() ?: "",
        isActive = true
    )
    
    tokenRepository.saveUserToken(userToken).collect { success ->
        if (success) {
            Log.d(TAG, "Tokens guardados")
            navigateToDashboard()
        }
    }
}
```

---

## 🔑 Usar Tokens

### Obtener Token Actual
```kotlin
AuthHelper.getFirebaseToken { token ->
    if (token != null) {
        // Usar para autenticar solicitudes
        makeAuthenticatedRequest(token)
    }
}
```

### Verificar Autenticación con Google
```kotlin
if (AuthHelper.isGoogleAuthenticated()) {
    Log.d(TAG, "Usuario autenticado con Google")
}
```

### Obtener Información del Usuario
```kotlin
val userInfo = AuthHelper.getUserInfo()
val email = userInfo["email"]
val name = userInfo["displayName"]
val photo = userInfo["photoUrl"]
```

---

## 🔄 Renovar Tokens

### Renovar Firebase ID Token
```kotlin
firebaseAuth.currentUser?.getIdToken(true)?.addOnCompleteListener { task ->
    if (task.isSuccessful) {
        val newToken = task.result?.token
        
        // Actualizar en base de datos
        tokenRepository.updateFirebaseToken(
            userId = userId,
            firebaseToken = newToken ?: "",
            refreshToken = newToken ?: ""
        ).collect { success ->
            if (success) {
                Log.d(TAG, "Token renovado")
            }
        }
    }
}
```

---

## 🚪 Logout

### Logout Simple
```kotlin
AuthHelper.signOut()
```

### Logout Completo (Revocar Tokens)
```kotlin
val userId = AuthHelper.getUserId() ?: return

tokenRepository.deleteUserToken(userId).collect { success ->
    if (success) {
        AuthHelper.signOut()
        val intent = Intent(this, SplashActivity::class.java)
        startActivity(intent)
        finish()
    }
}
```

---

## 📊 Estructura de Datos

### UserTokenModel
```kotlin
UserTokenModel(
    userId = "firebase-uid",
    email = "user@gmail.com",
    googleIdToken = "eyJhbGc...",
    firebaseToken = "eyJhbGc...",
    refreshToken = "eyJhbGc...",
    displayName = "John Doe",
    photoUrl = "https://...",
    createdAt = 1234567890,
    lastLoginAt = 1234567890,
    expiresAt = 1234571490,
    isActive = true
)
```

---

## 🔐 Validaciones

### Verificar Token Válido
```kotlin
tokenRepository.isTokenValid(userId).collect { isValid ->
    if (isValid) {
        Log.d(TAG, "Token válido")
    } else {
        Log.d(TAG, "Token expirado, renovar")
    }
}
```

### Obtener Tokens Activos (Múltiples Dispositivos)
```kotlin
tokenRepository.getActiveTokens(userId).collect { tokens ->
    tokens.forEach { token ->
        println("Dispositivo: ${token.lastLoginAt}")
    }
}
```

---

## 🎯 Casos de Uso

### Caso 1: Usuario se Autentica
```kotlin
// 1. Google Sign-In
// 2. Obtener Google ID Token
// 3. Autenticar con Firebase
// 4. Obtener Firebase ID Token
// 5. Guardar en base de datos
// 6. Navegar a Dashboard
```

### Caso 2: Usuario Hace Solicitud Autenticada
```kotlin
// 1. Obtener token actual
AuthHelper.getFirebaseToken { token ->
    // 2. Usar token en header
    val headers = mapOf("Authorization" to "Bearer $token")
    // 3. Hacer solicitud
    makeRequest(headers)
}
```

### Caso 3: Token Expirado
```kotlin
// 1. Detectar que token expiró
// 2. Renovar token
firebaseAuth.currentUser?.getIdToken(true)
// 3. Actualizar en base de datos
// 4. Reintentar solicitud
```

### Caso 4: Usuario Hace Logout
```kotlin
// 1. Revocar todos los tokens
tokenRepository.deleteUserToken(userId)
// 2. Cerrar sesión en Firebase
AuthHelper.signOut()
// 3. Navegar a SplashActivity
```

---

## 📱 Integración en Activities

### En SignUpActivity
```kotlin
// Guardar tokens después de autenticar
private fun firebaseAuthWithGoogle(idToken: String) {
    // ... autenticar con Firebase ...
    firebaseAuth.currentUser?.getIdToken(false)?.addOnCompleteListener { task ->
        if (task.isSuccessful) {
            saveUserTokens(idToken, task.result?.token ?: "")
        }
    }
}
```

### En MainActivity
```kotlin
// Usar token para solicitudes autenticadas
override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    
    // Obtener token para usar en la app
    AuthHelper.getFirebaseToken { token ->
        if (token != null) {
            // Guardar en variable global o ViewModel
            currentToken = token
        }
    }
}
```

### En TopBar (Logout)
```kotlin
// Logout button
Button(onClick = {
    val userId = AuthHelper.getUserId() ?: return@Button
    tokenRepository.deleteUserToken(userId).collect { success ->
        if (success) {
            AuthHelper.signOut()
            navigateToSplash()
        }
    }
})
```

---

## 🔗 Referencia Rápida

| Función | Uso |
|---------|-----|
| `AuthHelper.getFirebaseToken()` | Obtener token actual |
| `AuthHelper.isGoogleAuthenticated()` | Verificar proveedor |
| `AuthHelper.getUserInfo()` | Obtener info del usuario |
| `tokenRepository.saveUserToken()` | Guardar token |
| `tokenRepository.getUserToken()` | Obtener token guardado |
| `tokenRepository.updateFirebaseToken()` | Renovar token |
| `tokenRepository.isTokenValid()` | Verificar validez |
| `tokenRepository.deleteUserToken()` | Logout (revocar) |
| `tokenRepository.getActiveTokens()` | Ver dispositivos |

---

## 🚀 Próximos Pasos

1. **Integrar TokenRepository en AppModule**
2. **Guardar tokens en SignUpActivity**
3. **Usar tokens en solicitudes autenticadas**
4. **Renovar tokens automáticamente**
5. **Validar tokens en el backend**

---

**Última actualización**: 29 de Mayo de 2026
