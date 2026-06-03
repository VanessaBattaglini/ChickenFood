# Ejemplo Práctico: Guardar Tokens en Firebase Paso a Paso

## 🎯 Objetivo

Mostrar exactamente cómo guardar los tokens de Google en Firebase con datos reales.

---

## 📊 Ejemplo Completo

### Paso 1: Usuario se Autentica

```kotlin
// En SignUpActivity
private val googleSignInLauncher = registerForActivityResult(
    ActivityResultContracts.StartActivityForResult()
) { result ->
    try {
        val task = GoogleSignIn.getSignedInAccountFromIntent(result.data)
        val account = task.getResult(ApiException::class.java)
        
        // ✅ AQUÍ OBTIENES EL GOOGLE ID TOKEN
        val googleIdToken = account.idToken
        Log.d(TAG, "Google ID Token obtenido: ${googleIdToken?.take(30)}...")
        
        // Enviar a Firebase
        firebaseAuthWithGoogle(googleIdToken!!)
    } catch (e: ApiException) {
        Log.e(TAG, "Google Sign-Up falló: ${e.statusCode}", e)
    }
}
```

### Paso 2: Autenticar con Firebase

```kotlin
private fun firebaseAuthWithGoogle(idToken: String) {
    Log.d(TAG, "Autenticando con Firebase...")
    
    val credential = GoogleAuthProvider.getCredential(idToken, null)
    firebaseAuth.signInWithCredential(credential)
        .addOnCompleteListener(this) { task ->
            if (task.isSuccessful) {
                Log.d(TAG, "✅ Firebase Auth exitoso")
                
                // ✅ AQUÍ TIENES ACCESO A LOS DATOS DEL USUARIO
                val currentUser = firebaseAuth.currentUser
                Log.d(TAG, "Usuario: ${currentUser?.email}")
                Log.d(TAG, "UID: ${currentUser?.uid}")
                Log.d(TAG, "Nombre: ${currentUser?.displayName}")
                Log.d(TAG, "Foto: ${currentUser?.photoUrl}")
                
                // Ahora obtener el Firebase Token
                obtenerFirebaseToken(idToken)
            } else {
                Log.e(TAG, "Firebase Auth falló: ${task.exception?.message}")
            }
        }
}
```

### Paso 3: Obtener Firebase Token

```kotlin
private fun obtenerFirebaseToken(googleIdToken: String) {
    Log.d(TAG, "Obteniendo Firebase Token...")
    
    firebaseAuth.currentUser?.getIdToken(false)?.addOnCompleteListener { task ->
        if (task.isSuccessful) {
            // ✅ AQUÍ OBTIENES EL FIREBASE TOKEN
            val firebaseToken = task.result?.token
            Log.d(TAG, "✅ Firebase Token obtenido: ${firebaseToken?.take(30)}...")
            
            // Ahora guardar en Firebase
            guardarTokensEnFirebase(googleIdToken, firebaseToken ?: "")
        } else {
            Log.e(TAG, "Error obteniendo Firebase token: ${task.exception?.message}")
        }
    }
}
```

### Paso 4: Guardar en Firebase

```kotlin
private fun guardarTokensEnFirebase(googleIdToken: String, firebaseToken: String) {
    Log.d(TAG, "Guardando tokens en Firebase...")
    
    val currentUser = firebaseAuth.currentUser ?: return
    val userId = currentUser.uid
    
    // ✅ AQUÍ LLENAS MANUALMENTE TODOS LOS DATOS
    val userToken = UserTokenModel(
        userId = userId,                                    // "firebase-uid-456"
        email = currentUser.email ?: "",                   // "user@gmail.com"
        googleIdToken = googleIdToken,                     // "eyJhbGc..."
        firebaseToken = firebaseToken,                     // "eyJhbGc..."
        refreshToken = firebaseToken,                      // Firebase lo maneja
        displayName = currentUser.displayName ?: "",       // "John Doe"
        photoUrl = currentUser.photoUrl?.toString() ?: "", // "https://..."
        isEmailVerified = currentUser.isEmailVerified,     // true/false
        createdAt = System.currentTimeMillis(),            // 1234567890
        lastLoginAt = System.currentTimeMillis(),          // 1234567890
        expiresAt = System.currentTimeMillis() + (3600 * 1000),  // 1234571490
        isActive = true                                    // true
    )
    
    // ✅ ENVIAR A FIREBASE
    val tokenId = UUID.randomUUID().toString()
    val ref = database.getReference("users/$userId/tokens/$tokenId")
    
    ref.setValue(userToken).addOnCompleteListener { task ->
        if (task.isSuccessful) {
            Log.d(TAG, "✅ Tokens guardados exitosamente en Firebase")
            
            // Mostrar lo que se guardó
            Log.d(TAG, "Datos guardados:")
            Log.d(TAG, "  userId: $userId")
            Log.d(TAG, "  email: ${currentUser.email}")
            Log.d(TAG, "  displayName: ${currentUser.displayName}")
            Log.d(TAG, "  photoUrl: ${currentUser.photoUrl}")
            Log.d(TAG, "  googleIdToken: ${googleIdToken.take(20)}...")
            Log.d(TAG, "  firebaseToken: ${firebaseToken.take(20)}...")
            Log.d(TAG, "  createdAt: ${System.currentTimeMillis()}")
            Log.d(TAG, "  isActive: true")
            
            navigateToDashboard()
        } else {
            Log.e(TAG, "❌ Error guardando tokens: ${task.exception?.message}")
            navigateToDashboard()
        }
    }
}
```

---

## 📊 Datos Reales Guardados en Firebase

### Antes (En tu App - Objeto Kotlin)
```kotlin
UserTokenModel(
    userId = "firebase-uid-456",
    email = "user@gmail.com",
    googleIdToken = "eyJhbGciOiJSUzI1NiIsImtpZCI6IjEyMyJ9...",
    firebaseToken = "eyJhbGciOiJSUzI1NiIsImtpZCI6IjQ1NiJ9...",
    refreshToken = "eyJhbGciOiJSUzI1NiIsImtpZCI6IjQ1NiJ9...",
    displayName = "John Doe",
    photoUrl = "https://lh3.googleusercontent.com/a/default-user",
    isEmailVerified = true,
    createdAt = 1717000000000,
    lastLoginAt = 1717000000000,
    expiresAt = 1717003600000,
    isActive = true
)
```

### Después (En Firebase - JSON)
```json
{
  "users": {
    "firebase-uid-456": {
      "tokens": {
        "token-id-1": {
          "userId": "firebase-uid-456",
          "email": "user@gmail.com",
          "googleIdToken": "eyJhbGciOiJSUzI1NiIsImtpZCI6IjEyMyJ9...",
          "firebaseToken": "eyJhbGciOiJSUzI1NiIsImtpZCI6IjQ1NiJ9...",
          "refreshToken": "eyJhbGciOiJSUzI1NiIsImtpZCI6IjQ1NiJ9...",
          "displayName": "John Doe",
          "photoUrl": "https://lh3.googleusercontent.com/a/default-user",
          "isEmailVerified": true,
          "createdAt": 1717000000000,
          "lastLoginAt": 1717000000000,
          "expiresAt": 1717003600000,
          "isActive": true
        }
      }
    }
  }
}
```

---

## 🔍 Desglose de Cada Campo

### userId
```kotlin
// ¿De dónde viene?
val userId = firebaseAuth.currentUser?.uid

// Valor real
userId = "firebase-uid-456"

// ¿Qué es?
// El identificador único del usuario en Firebase Auth
```

### email
```kotlin
// ¿De dónde viene?
val email = firebaseAuth.currentUser?.email

// Valor real
email = "user@gmail.com"

// ¿Qué es?
// El email del usuario que se autenticó con Google
```

### googleIdToken
```kotlin
// ¿De dónde viene?
val googleIdToken = account.idToken  // Del googleSignInLauncher

// Valor real
googleIdToken = "eyJhbGciOiJSUzI1NiIsImtpZCI6IjEyMyJ9.eyJpc3MiOiJodHRwczovL2FjY291bnRzLmdvb2dsZS5jb20iLCJhenAiOiIxMjM0NTY3ODkwLmFwcHMuZ29vZ2xldXNlcmNvbnRlbnQuY29tIiwiYXVkIjoiMTIzNDU2Nzg5MC5hcHBzLmdvb2dsZXVzZXJjb250ZW50LmNvbSIsInN1YiI6IjEwNzY5MTUwMzUwMDA2MTUwNzE2IiwiZW1haWwiOiJ1c2VyQGdtYWlsLmNvbSIsImVtYWlsX3ZlcmlmaWVkIjp0cnVlLCJhdF9oYXNoIjoiWDVyVl9aMTBTRzVUWTJhMEhRIiwibmFtZSI6IkpvaG4gRG9lIiwicGljdHVyZSI6Imh0dHBzOi8vbGgzLmdvb2dsZXVzZXJjb250ZW50LmNvbS9hL2RlZmF1bHQtdXNlciIsImdpdmVuX25hbWUiOiJKb2huIiwiZmFtaWx5X25hbWUiOiJEb2UiLCJsb2NhbGUiOiJlcyIsImlhdCI6MTcxNzAwMDAwMCwiZXhwIjoxNzE3MDAzNjAwfQ.signature..."

// ¿Qué es?
// JWT firmado por Google que contiene la información del usuario
```

### firebaseToken
```kotlin
// ¿De dónde viene?
val firebaseToken = task.result?.token  // De getIdToken()

// Valor real
firebaseToken = "eyJhbGciOiJSUzI1NiIsImtpZCI6IjQ1NiJ9.eyJpc3MiOiJodHRwczovL3NlY3VyZXRva2VuLmdvb2dsZS5jb20iLCJhdWQiOiJjaGlja2VuZm9vZC1wcm9qZWN0IiwiYXV0aF90aW1lIjoxNzE3MDAwMDAwLCJ1c2VyX2lkIjoiZmlyZWJhc2UtdWlkLTQ1NiIsInN1YiI6ImZpcmViYXNlLXVpZC00NTYiLCJpYXQiOjE3MTcwMDAwMDAsImV4cCI6MTcxNzAwMzYwMCwiZW1haWwiOiJ1c2VyQGdtYWlsLmNvbSIsImVtYWlsX3ZlcmlmaWVkIjpmYWxzZSwiZmlyZWJhc2UiOnsiaWRlbnRpdGllcyI6eyJnb29nbGUuY29tIjpbIjEwNzY5MTUwMzUwMDA2MTUwNzE2Il19LCJzaWduX2luX3Byb3ZpZGVyIjoiZ29vZ2xlLmNvbSJ9fQ.signature..."

// ¿Qué es?
// JWT firmado por Firebase que se usa para autenticar solicitudes
```

### displayName
```kotlin
// ¿De dónde viene?
val displayName = firebaseAuth.currentUser?.displayName

// Valor real
displayName = "John Doe"

// ¿Qué es?
// El nombre del usuario que se autenticó con Google
```

### photoUrl
```kotlin
// ¿De dónde viene?
val photoUrl = firebaseAuth.currentUser?.photoUrl?.toString()

// Valor real
photoUrl = "https://lh3.googleusercontent.com/a/default-user"

// ¿Qué es?
// URL de la foto de perfil del usuario en Google
```

### createdAt
```kotlin
// ¿De dónde viene?
val createdAt = System.currentTimeMillis()

// Valor real
createdAt = 1717000000000  // Milisegundos desde 1970

// ¿Qué es?
// Fecha y hora en que se guardó el token
```

### expiresAt
```kotlin
// ¿De dónde viene?
val expiresAt = System.currentTimeMillis() + (3600 * 1000)

// Valor real
expiresAt = 1717003600000  // 1 hora después de createdAt

// ¿Qué es?
// Fecha y hora en que expira el token (1 hora)
```

### isActive
```kotlin
// ¿De dónde viene?
val isActive = true

// Valor real
isActive = true

// ¿Qué es?
// Indica si el token está activo (true) o revocado (false)
```

---

## 🔄 Flujo Completo con Logs

```
D/SignUpActivity: Google Sign-Up iniciado
D/SignUpActivity: Google ID Token obtenido: eyJhbGc...
D/SignUpActivity: Autenticando con Firebase...
D/SignUpActivity: ✅ Firebase Auth exitoso
D/SignUpActivity: Usuario: user@gmail.com
D/SignUpActivity: UID: firebase-uid-456
D/SignUpActivity: Nombre: John Doe
D/SignUpActivity: Foto: https://lh3.googleusercontent.com/a/default-user
D/SignUpActivity: Obteniendo Firebase Token...
D/SignUpActivity: ✅ Firebase Token obtenido: eyJhbGc...
D/SignUpActivity: Guardando tokens en Firebase...
D/SignUpActivity: ✅ Tokens guardados exitosamente en Firebase
D/SignUpActivity: Datos guardados:
D/SignUpActivity:   userId: firebase-uid-456
D/SignUpActivity:   email: user@gmail.com
D/SignUpActivity:   displayName: John Doe
D/SignUpActivity:   photoUrl: https://lh3.googleusercontent.com/a/default-user
D/SignUpActivity:   googleIdToken: eyJhbGc...
D/SignUpActivity:   firebaseToken: eyJhbGc...
D/SignUpActivity:   createdAt: 1717000000000
D/SignUpActivity:   isActive: true
D/SignUpActivity: Navegando al Dashboard
```

---

## 📱 Verificar en Firebase Console

### 1. Abre Firebase Console
```
https://console.firebase.google.com/
```

### 2. Selecciona tu proyecto
```
ChickenFood
```

### 3. Ve a Realtime Database
```
Realtime Database → Data
```

### 4. Navega a la estructura
```
users
└── firebase-uid-456
    └── tokens
        └── token-id-1
            ├── userId: "firebase-uid-456"
            ├── email: "user@gmail.com"
            ├── googleIdToken: "eyJhbGc..."
            ├── firebaseToken: "eyJhbGc..."
            ├── displayName: "John Doe"
            ├── photoUrl: "https://..."
            ├── createdAt: 1717000000000
            ├── lastLoginAt: 1717000000000
            ├── expiresAt: 1717003600000
            └── isActive: true
```

---

## ✅ Checklist

- [ ] Obtener Google ID Token en googleSignInLauncher
- [ ] Autenticar con Firebase usando el Google ID Token
- [ ] Obtener Firebase ID Token con getIdToken(false)
- [ ] Llenar el objeto UserTokenModel con todos los datos
- [ ] Enviar a Firebase con setValue()
- [ ] Verificar en Firebase Console que los datos se guardaron
- [ ] Ver los logs para confirmar que todo funcionó

---

## 🎯 Resumen

1. **Google ID Token** → Obtenido de Google Sign-In
2. **Firebase Auth** → Verifica el Google ID Token
3. **Firebase ID Token** → Obtenido de Firebase Auth
4. **UserTokenModel** → Llenas manualmente con todos los datos
5. **Firebase Realtime Database** → Guardas el objeto con setValue()
6. **JSON en Firebase** → Firebase convierte automáticamente a JSON

---

**Última actualización**: 29 de Mayo de 2026
