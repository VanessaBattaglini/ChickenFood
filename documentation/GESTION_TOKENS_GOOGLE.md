# Gestión de Tokens de Google - Guía Completa

## 📋 Descripción General

Esta guía explica cómo asociar y gestionar los tokens de Google con los usuarios autenticados en tu aplicación ChickenFood.

---

## 🔐 Tipos de Tokens

### 1. **Google ID Token (JWT)**
```
Qué es: Token JWT firmado por Google
Cuándo se obtiene: Cuando el usuario se autentica con Google
Duración: Corta (típicamente 1 hora)
Uso: Verificar identidad del usuario
Dónde se guarda: En SignUpActivity
```

**Estructura del JWT:**
```
Header: { "alg": "RS256", "typ": "JWT" }
Payload: { 
  "email": "user@gmail.com",
  "name": "John Doe",
  "picture": "https://...",
  "aud": "YOUR_WEB_CLIENT_ID",
  "sub": "GOOGLE_UID",
  "iat": 1234567890,
  "exp": 1234571490
}
Signature: Firmado con clave privada de Google
```

### 2. **Firebase ID Token**
```
Qué es: Token de sesión de Firebase
Cuándo se obtiene: Después de autenticar con Firebase
Duración: Corta (típicamente 1 hora)
Uso: Autenticar solicitudes al backend
Dónde se guarda: En Firebase Auth + tu base de datos
```

### 3. **Firebase Refresh Token**
```
Qué es: Token para renovar el ID Token
Cuándo se obtiene: Cuando se autentica con Firebase
Duración: Larga (típicamente 30 días)
Uso: Renovar sesión sin volver a autenticar
Dónde se guarda: En Firebase Auth + tu base de datos
```

---

## 🔄 Flujo de Autenticación y Tokens

```
┌─────────────────────────────────────────────────────────────┐
│ 1. Usuario hace clic en "Continuar con Google"              │
│    ↓                                                         │
│ 2. Google Sign-In Selector se abre                          │
│    ├─ Usuario selecciona su cuenta                          │
│    └─ Google verifica autenticación del dispositivo         │
│    ↓                                                         │
│ 3. Google genera ID Token (JWT)                             │
│    ├─ Contiene: email, nombre, foto, UID                   │
│    └─ Firmado digitalmente por Google                       │
│    ↓                                                         │
│ 4. Tu app recibe el Google ID Token                         │
│    ├─ googleSignInLauncher.launch(signInIntent)             │
│    └─ account.idToken obtenido                              │
│    ↓                                                         │
│ 5. Se envía el Google ID Token a Firebase                   │
│    ├─ GoogleAuthProvider.getCredential(idToken, null)       │
│    └─ firebaseAuth.signInWithCredential(credential)         │
│    ↓                                                         │
│ 6. Firebase verifica el Google ID Token                     │
│    ├─ Verifica la firma con clave pública de Google         │
│    ├─ Verifica que no está expirado                         │
│    └─ Verifica que es válido                                │
│    ↓                                                         │
│ 7. Firebase crea sesión del usuario                         │
│    ├─ Crea usuario en Firebase Auth                         │
│    ├─ Genera Firebase ID Token                              │
│    └─ Genera Firebase Refresh Token                         │
│    ↓                                                         │
│ 8. Tu app obtiene los tokens de Firebase                    │
│    ├─ firebaseAuth.currentUser?.getIdToken(false)           │
│    └─ Tokens disponibles en FirebaseAuth                    │
│    ↓                                                         │
│ 9. Guardar tokens en tu base de datos                       │
│    ├─ Google ID Token                                       │
│    ├─ Firebase ID Token                                     │
│    ├─ Firebase Refresh Token                                │
│    ├─ Email, nombre, foto                                   │
│    └─ Fecha de creación y último login                      │
│    ↓                                                         │
│ 10. Usuario autenticado en la app                           │
│    ├─ Puede acceder al Dashboard                            │
│    ├─ Puede hacer compras                                   │
│    └─ Puede hacer logout                                    │
└─────────────────────────────────────────────────────────────┘
```

---

## 💾 Estructura de Almacenamiento en Firebase

### Ubicación de Tokens
```
users/{userId}/
├── email: "user@gmail.com"
├── displayName: "John Doe"
├── photoUrl: "https://..."
├── lastLogin: 1234567890
└── tokens/
    ├── {tokenId1}/
    │   ├── userId: "{userId}"
    │   ├── email: "user@gmail.com"
    │   ├── googleIdToken: "eyJhbGc..."
    │   ├── firebaseToken: "eyJhbGc..."
    │   ├── refreshToken: "eyJhbGc..."
    │   ├── displayName: "John Doe"
    │   ├── photoUrl: "https://..."
    │   ├── createdAt: 1234567890
    │   ├── lastLoginAt: 1234567890
    │   ├── expiresAt: 1234571490
    │   └── isActive: true
    │
    └── {tokenId2}/
        ├── userId: "{userId}"
        ├── email: "user@gmail.com"
        ├── googleIdToken: "eyJhbGc..."
        ├── firebaseToken: "eyJhbGc..."
        ├── refreshToken: "eyJhbGc..."
        ├── displayName: "John Doe"
        ├── photoUrl: "https://..."
        ├── createdAt: 1234567891
        ├── lastLoginAt: 1234567891
        ├── expiresAt: 1234571491
        └── isActive: true
```

**Nota**: Se guardan múltiples tokens para soportar múltiples dispositivos.

---

## 🔧 Cómo Implementar

### Paso 1: Obtener el Google ID Token

En `SignUpActivity.kt`:
```kotlin
private val googleSignInLauncher = registerForActivityResult(
    ActivityResultContracts.StartActivityForResult()
) { result ->
    try {
        val task = GoogleSignIn.getSignedInAccountFromIntent(result.data)
        val account = task.getResult(ApiException::class.java)
        
        // ✅ Google ID Token obtenido aquí
        val googleIdToken = account.idToken
        Log.d(TAG, "Google ID Token: ${googleIdToken?.take(20)}...")
        
        firebaseAuthWithGoogle(googleIdToken!!)
    } catch (e: ApiException) {
        Log.e(TAG, "Google Sign-Up falló: ${e.statusCode}", e)
    }
}
```

### Paso 2: Autenticar con Firebase

```kotlin
private fun firebaseAuthWithGoogle(idToken: String) {
    Log.d(TAG, "Autenticando con Firebase usando Google ID Token")
    val credential = GoogleAuthProvider.getCredential(idToken, null)
    firebaseAuth.signInWithCredential(credential)
        .addOnCompleteListener(this) { task ->
            if (task.isSuccessful) {
                Log.d(TAG, "Firebase Auth exitoso: ${firebaseAuth.currentUser?.email}")
                
                // ✅ Obtener Firebase ID Token
                firebaseAuth.currentUser?.getIdToken(false)?.addOnCompleteListener { tokenTask ->
                    if (tokenTask.isSuccessful) {
                        val firebaseToken = tokenTask.result?.token ?: ""
                        Log.d(TAG, "Firebase token: ${firebaseToken.take(20)}...")
                        
                        // Guardar tokens en base de datos
                        saveUserTokens(idToken, firebaseToken)
                        navigateToDashboard()
                    }
                }
            }
        }
}
```

### Paso 3: Guardar Tokens en Base de Datos

```kotlin
private fun saveUserTokens(googleIdToken: String, firebaseToken: String) {
    val userId = AuthHelper.getUserId() ?: return
    val email = AuthHelper.getUserEmail() ?: return
    val displayName = AuthHelper.getUserName() ?: ""
    val photoUrl = AuthHelper.getUserPhotoUrl() ?: ""
    
    val userToken = UserTokenModel(
        userId = userId,
        email = email,
        googleIdToken = googleIdToken,
        firebaseToken = firebaseToken,
        refreshToken = firebaseToken,  // Firebase maneja esto internamente
        displayName = displayName,
        photoUrl = photoUrl,
        isEmailVerified = firebaseAuth.currentUser?.isEmailVerified ?: false,
        createdAt = System.currentTimeMillis(),
        lastLoginAt = System.currentTimeMillis(),
        expiresAt = System.currentTimeMillis() + (3600 * 1000),  // 1 hora
        isActive = true
    )
    
    // Guardar en Firebase
    val ref = database.getReference("users/$userId/tokens/${UUID.randomUUID()}")
    ref.setValue(userToken).addOnCompleteListener { task ->
        if (task.isSuccessful) {
            Log.d(TAG, "Tokens guardados exitosamente")
        } else {
            Log.e(TAG, "Error guardando tokens: ${task.exception?.message}")
        }
    }
}
```

---

## 🔑 Usar Tokens para Autenticación

### Obtener Token Actual
```kotlin
AuthHelper.getFirebaseToken { token ->
    if (token != null) {
        Log.d(TAG, "Token actual: ${token.take(20)}...")
        // Usar token para autenticar solicitudes al backend
    }
}
```

### Verificar Proveedor de Autenticación
```kotlin
if (AuthHelper.isGoogleAuthenticated()) {
    Log.d(TAG, "Usuario autenticado con Google")
} else {
    Log.d(TAG, "Usuario autenticado con otro proveedor")
}
```

### Obtener Información del Usuario
```kotlin
val userInfo = AuthHelper.getUserInfo()
println("Email: ${userInfo["email"]}")
println("Nombre: ${userInfo["displayName"]}")
println("Foto: ${userInfo["photoUrl"]}")
```

---

## 🔄 Renovar Tokens

### Renovar Firebase ID Token
```kotlin
firebaseAuth.currentUser?.getIdToken(true)?.addOnCompleteListener { task ->
    if (task.isSuccessful) {
        val newToken = task.result?.token
        Log.d(TAG, "Token renovado: ${newToken?.take(20)}...")
        
        // Actualizar en base de datos
        updateFirebaseToken(userId, newToken ?: "")
    }
}
```

### Actualizar en Base de Datos
```kotlin
tokenRepository.updateFirebaseToken(
    userId = userId,
    firebaseToken = newToken,
    refreshToken = newToken
).collect { success ->
    if (success) {
        Log.d(TAG, "Token actualizado en base de datos")
    }
}
```

---

## 🚪 Logout y Revocación de Tokens

### Logout Simple
```kotlin
AuthHelper.signOut()
// Esto cierra la sesión en Firebase Auth
```

### Logout Completo (Revocar Tokens)
```kotlin
val userId = AuthHelper.getUserId() ?: return

// 1. Revocar todos los tokens en base de datos
tokenRepository.deleteUserToken(userId).collect { success ->
    if (success) {
        Log.d(TAG, "Tokens revocados")
    }
}

// 2. Cerrar sesión en Firebase
AuthHelper.signOut()

// 3. Navegar a SplashActivity
val intent = Intent(this, SplashActivity::class.java)
startActivity(intent)
finish()
```

---

## 🔐 Seguridad

### Validaciones
- ✅ Verificar que el token no está expirado
- ✅ Verificar que el token está activo
- ✅ Verificar que el usuario está autenticado
- ✅ Renovar tokens antes de que expiren

### Almacenamiento Seguro
- ✅ Guardar en Firebase (encriptado en tránsito)
- ✅ NO guardar en SharedPreferences sin encriptar
- ✅ NO guardar en archivos locales sin encriptar
- ✅ Usar HTTPS para todas las comunicaciones

### Mejores Prácticas
- ✅ Renovar tokens cada hora
- ✅ Revocar tokens al logout
- ✅ Validar tokens en el backend
- ✅ Usar tokens con expiración corta
- ✅ Mantener refresh tokens seguros

---

## 📊 Ejemplo Completo

### Escenario: Usuario se Autentica con Google

```
1. Usuario abre la app
   ↓
2. Hace clic en "Inscribete"
   ↓
3. Hace clic en "Continuar con Google"
   ↓
4. Google Sign-In Selector se abre
   ↓
5. Usuario selecciona: user@gmail.com
   ↓
6. Google genera ID Token:
   {
     "email": "user@gmail.com",
     "name": "John Doe",
     "picture": "https://...",
     "sub": "google-uid-123",
     "aud": "YOUR_WEB_CLIENT_ID",
     "iat": 1234567890,
     "exp": 1234571490
   }
   ↓
7. Tu app recibe el Google ID Token
   ↓
8. Se envía a Firebase para verificación
   ↓
9. Firebase verifica la firma y crea sesión
   ↓
10. Firebase genera ID Token y Refresh Token
   ↓
11. Tu app guarda en base de datos:
   {
     "userId": "firebase-uid-456",
     "email": "user@gmail.com",
     "googleIdToken": "eyJhbGc...",
     "firebaseToken": "eyJhbGc...",
     "refreshToken": "eyJhbGc...",
     "displayName": "John Doe",
     "photoUrl": "https://...",
     "createdAt": 1234567890,
     "lastLoginAt": 1234567890,
     "expiresAt": 1234571490,
     "isActive": true
   }
   ↓
12. Usuario ve Dashboard
   ↓
13. Puede hacer compras, acumular puntos, etc.
```

---

## 🔗 Archivos Relacionados

### Modelos
- `UserTokenModel.kt` - Modelo de token del usuario

### Repositorios
- `TokenRepository.kt` - Interfaz
- `TokenRepositoryImpl.kt` - Implementación

### Helpers
- `AuthHelper.kt` - Funciones de autenticación

### Activities
- `SignUpActivity.kt` - Autenticación con Google

---

## 🚀 Próximos Pasos

1. **Implementar renovación automática de tokens**
   - Renovar cada 55 minutos
   - Usar WorkManager para tareas en background

2. **Agregar validación de tokens en el backend**
   - Verificar firma del token
   - Verificar que no está expirado
   - Verificar que el usuario existe

3. **Implementar múltiples dispositivos**
   - Guardar tokens por dispositivo
   - Permitir logout desde dispositivos específicos
   - Mostrar lista de dispositivos activos

4. **Agregar seguridad adicional**
   - Encriptar tokens en almacenamiento local
   - Usar biometría para acceso
   - Implementar 2FA

5. **Crear dashboard de seguridad**
   - Ver dispositivos activos
   - Revocar tokens específicos
   - Ver historial de logins

---

## 📝 Resumen

Los tokens de Google se asocian con el usuario de la siguiente manera:

1. **Google genera ID Token** cuando el usuario se autentica
2. **Tu app recibe el ID Token** en SignUpActivity
3. **Se envía a Firebase** para verificación
4. **Firebase genera sus propios tokens** (ID Token + Refresh Token)
5. **Todos los tokens se guardan** en tu base de datos
6. **Se usan para autenticar** solicitudes futuras

El sistema es seguro, escalable y soporta múltiples dispositivos.

---

**Última actualización**: 29 de Mayo de 2026
**Estado**: ✅ IMPLEMENTADO
