# Diagrama Visual: JWT → Credential → Firebase

## 🎯 El Proceso en 3 Pasos

### Paso 1: Obtener JWT de Google

```
┌─────────────────────────────────────────────────────────────┐
│ Google Sign-In                                              │
│                                                             │
│ Usuario selecciona su cuenta                                │
│ ↓                                                           │
│ Google genera JWT                                           │
│ ↓                                                           │
│ Tu app recibe el JWT                                        │
│                                                             │
│ account.idToken = "eyJhbGc..."                              │
└─────────────────────────────────────────────────────────────┘
```

### Paso 2: Convertir JWT a Credential

```
┌─────────────────────────────────────────────────────────────┐
│ GoogleAuthProvider.getCredential(idToken, null)             │
│                                                             │
│ Entrada:                                                    │
│ ├─ idToken = "eyJhbGc..."                                   │
│ └─ accessToken = null                                       │
│                                                             │
│ Proceso:                                                    │
│ ├─ Extrae información del JWT                               │
│ ├─ Crea objeto AuthCredential                               │
│ └─ Marca que es de Google                                   │
│                                                             │
│ Salida:                                                     │
│ └─ credential = AuthCredential                              │
└─────────────────────────────────────────────────────────────┘
```

### Paso 3: Enviar Credential a Firebase

```
┌─────────────────────────────────────────────────────────────┐
│ firebaseAuth.signInWithCredential(credential)               │
│                                                             │
│ Entrada:                                                    │
│ └─ credential = AuthCredential                              │
│                                                             │
│ Firebase Verifica:                                          │
│ ├─ Firma del JWT (con clave pública de Google)              │
│ ├─ Expiración del JWT                                       │
│ ├─ Audience (aud) del JWT                                   │
│ └─ Validez general del JWT                                  │
│                                                             │
│ Si es válido:                                               │
│ ├─ Crea usuario en Firebase Auth                            │
│ ├─ Genera Firebase ID Token                                 │
│ ├─ Genera Firebase Refresh Token                            │
│ └─ task.isSuccessful = true                                 │
│                                                             │
│ Si es inválido:                                             │
│ └─ task.isSuccessful = false                                │
└─────────────────────────────────────────────────────────────┘
```

---

## 📊 Flujo Completo

```
┌──────────────────────────────────────────────────────────────────┐
│                                                                  │
│  USUARIO ABRE LA APP                                             │
│  ↓                                                               │
│  HACE CLIC EN "CONTINUAR CON GOOGLE"                             │
│  ↓                                                               │
│  ┌────────────────────────────────────────────────────────────┐  │
│  │ GOOGLE SIGN-IN SELECTOR SE ABRE                            │  │
│  │ ├─ Muestra cuentas disponibles                             │  │
│  │ └─ Usuario selecciona su cuenta                            │  │
│  └────────────────────────────────────────────────────────────┘  │
│  ↓                                                               │
│  ┌────────────────────────────────────────────────────────────┐  │
│  │ GOOGLE GENERA JWT                                          │  │
│  │ ├─ Header: { "alg": "RS256", "typ": "JWT" }               │  │
│  │ ├─ Payload: {                                              │  │
│  │ │   "iss": "https://accounts.google.com",                 │  │
│  │ │   "email": "user@gmail.com",                            │  │
│  │ │   "name": "John Doe",                                   │  │
│  │ │   "picture": "https://...",                             │  │
│  │ │   "aud": "YOUR_WEB_CLIENT_ID",                          │  │
│  │ │   "sub": "GOOGLE_UID",                                  │  │
│  │ │   "iat": 1717000000,                                    │  │
│  │ │   "exp": 1717003600                                     │  │
│  │ │ }                                                        │  │
│  │ └─ Signature: Firmado con clave privada de Google         │  │
│  └────────────────────────────────────────────────────────────┘  │
│  ↓                                                               │
│  ┌────────────────────────────────────────────────────────────┐  │
│  │ TU APP RECIBE EL JWT                                       │  │
│  │ ├─ account.idToken = "eyJhbGc..."                          │  │
│  │ └─ Log: "JWT obtenido: eyJhbGc..."                         │  │
│  └────────────────────────────────────────────────────────────┘  │
│  ↓                                                               │
│  ┌────────────────────────────────────────────────────────────┐  │
│  │ CONVERTIR JWT A CREDENTIAL (UNA LÍNEA)                     │  │
│  │ ├─ val credential = GoogleAuthProvider.getCredential(      │  │
│  │ │     idToken, null                                        │  │
│  │ │ )                                                        │  │
│  │ └─ Log: "Credential creada: ..."                           │  │
│  └────────────────────────────────────────────────────────────┘  │
│  ↓                                                               │
│  ┌────────────────────────────────────────────────────────────┐  │
│  │ ENVIAR CREDENTIAL A FIREBASE                               │  │
│  │ ├─ firebaseAuth.signInWithCredential(credential)           │  │
│  │ └─ Log: "Enviando credential a Firebase..."                │  │
│  └────────────────────────────────────────────────────────────┘  │
│  ↓                                                               │
│  ┌────────────────────────────────────────────────────────────┐  │
│  │ FIREBASE VERIFICA EL JWT                                   │  │
│  │ ├─ Obtiene clave pública de Google                         │  │
│  │ ├─ Verifica la firma                                       │  │
│  │ ├─ Verifica expiración                                     │  │
│  │ ├─ Verifica audience (aud)                                 │  │
│  │ └─ Log: "Verificando JWT..."                               │  │
│  └────────────────────────────────────────────────────────────┘  │
│  ↓                                                               │
│  ┌────────────────────────────────────────────────────────────┐  │
│  │ FIREBASE CREA SESIÓN                                       │  │
│  │ ├─ Crea usuario en Firebase Auth                           │  │
│  │ ├─ Genera Firebase ID Token                                │  │
│  │ ├─ Genera Firebase Refresh Token                           │  │
│  │ └─ Log: "✅ Firebase autenticó exitosamente"               │  │
│  └────────────────────────────────────────────────────────────┘  │
│  ↓                                                               │
│  ┌────────────────────────────────────────────────────────────┐  │
│  │ USUARIO AUTENTICADO                                        │  │
│  │ ├─ firebaseAuth.currentUser != null                        │  │
│  │ ├─ firebaseAuth.currentUser?.email = "user@gmail.com"      │  │
│  │ ├─ firebaseAuth.currentUser?.uid = "firebase-uid-456"      │  │
│  │ └─ Log: "Usuario: user@gmail.com"                          │  │
│  └────────────────────────────────────────────────────────────┘  │
│  ↓                                                               │
│  NAVEGAR AL DASHBOARD                                           │
│                                                                  │
└──────────────────────────────────────────────────────────────────┘
```

---

## 🔍 Desglose de Cada Componente

### JWT de Google

```
┌─────────────────────────────────────────────────────────────┐
│ JWT = JSON Web Token                                        │
│                                                             │
│ Estructura:                                                 │
│ [Header].[Payload].[Signature]                              │
│                                                             │
│ Ejemplo:                                                    │
│ eyJhbGciOiJSUzI1NiIsImtpZCI6IjEyMyJ9.                       │
│ eyJpc3MiOiJodHRwczovL2FjY291bnRzLmdvb2dsZS5jb20iLCJhenAi │
│ OiIxMjM0NTY3ODkwLmFwcHMuZ29vZ2xldXNlcmNvbnRlbnQuY29tIiwi │
│ YXVkIjoiMTIzNDU2Nzg5MC5hcHBzLmdvb2dsZXVzZXJjb250ZW50LmNv │
│ bSIsInN1YiI6IjEwNzY5MTUwMzUwMDA2MTUwNzE2IiwiZW1haWwiOiJ1 │
│ c2VyQGdtYWlsLmNvbSIsImVtYWlsX3ZlcmlmaWVkIjp0cnVlLCJhdF9o │
│ YXNoIjoiWDVyVl9aMTBTRzVUWTJhMEhRIiwibmFtZSI6IkpvaG4gRG9l │
│ IiwicGljdHVyZSI6Imh0dHBzOi8vbGgzLmdvb2dsZXVzZXJjb250ZW50 │
│ LmNvbS9hL2RlZmF1bHQtdXNlciIsImdpdmVuX25hbWUiOiJKb2huIiwi │
│ ZmFtaWx5X25hbWUiOiJEb2UiLCJsb2NhbGUiOiJlcyIsImlhdCI6MTcx │
│ NzAwMDAwMCwiZXhwIjoxNzE3MDAzNjAwfQ.                         │
│ signature...                                                │
│                                                             │
│ Contiene:                                                   │
│ ├─ Email del usuario                                        │
│ ├─ Nombre del usuario                                       │
│ ├─ Foto del usuario                                         │
│ ├─ UID de Google                                            │
│ ├─ Fecha de creación (iat)                                  │
│ ├─ Fecha de expiración (exp)                                │
│ └─ Firma digital de Google                                  │
└─────────────────────────────────────────────────────────────┘
```

### Credential

```
┌─────────────────────────────────────────────────────────────┐
│ AuthCredential                                              │
│                                                             │
│ Es un objeto que Firebase entiende                          │
│                                                             │
│ Contiene:                                                   │
│ ├─ El JWT                                                   │
│ ├─ El proveedor (Google)                                    │
│ └─ Información para verificación                            │
│                                                             │
│ Creado por:                                                 │
│ GoogleAuthProvider.getCredential(idToken, null)             │
│                                                             │
│ Usado por:                                                  │
│ firebaseAuth.signInWithCredential(credential)               │
└─────────────────────────────────────────────────────────────┘
```

---

## 🔐 Verificación en Firebase

```
┌─────────────────────────────────────────────────────────────┐
│ Firebase Recibe la Credential                               │
│ ↓                                                           │
│ 1. Extrae el JWT                                            │
│    └─ credential.getJWT()                                   │
│ ↓                                                           │
│ 2. Obtiene clave pública de Google                          │
│    └─ Descarga de: https://www.googleapis.com/oauth2/v1/certs
│ ↓                                                           │
│ 3. Verifica la firma                                        │
│    ├─ Decodifica el JWT                                     │
│    ├─ Verifica que la firma es válida                       │
│    └─ Si no es válida → RECHAZA                             │
│ ↓                                                           │
│ 4. Verifica expiración                                      │
│    ├─ Obtiene "exp" del JWT                                 │
│    ├─ Compara con hora actual                               │
│    └─ Si está expirado → RECHAZA                            │
│ ↓                                                           │
│ 5. Verifica audience (aud)                                  │
│    ├─ Obtiene "aud" del JWT                                 │
│    ├─ Compara con tu Web Client ID                          │
│    └─ Si no coincide → RECHAZA                              │
│ ↓                                                           │
│ 6. Si todo es válido                                        │
│    ├─ Crea usuario en Firebase Auth                         │
│    ├─ Genera Firebase ID Token                              │
│    ├─ Genera Firebase Refresh Token                         │
│    └─ task.isSuccessful = true                              │
│ ↓                                                           │
│ 7. Si algo falla                                            │
│    ├─ task.isSuccessful = false                             │
│    └─ task.exception contiene el error                      │
└─────────────────────────────────────────────────────────────┘
```

---

## 💻 Código Paso a Paso

### Paso 1: Obtener JWT

```kotlin
private val googleSignInLauncher = registerForActivityResult(
    ActivityResultContracts.StartActivityForResult()
) { result ->
    val task = GoogleSignIn.getSignedInAccountFromIntent(result.data)
    val account = task.getResult(ApiException::class.java)
    
    // ✅ AQUÍ TIENES EL JWT
    val idToken = account.idToken  // "eyJhbGc..."
    
    Log.d(TAG, "JWT: ${idToken?.take(30)}...")
}
```

### Paso 2: Convertir a Credential

```kotlin
private fun firebaseAuthWithGoogle(idToken: String) {
    // ✅ UNA LÍNEA PARA CONVERTIR
    val credential = GoogleAuthProvider.getCredential(idToken, null)
    
    Log.d(TAG, "Credential: $credential")
}
```

### Paso 3: Enviar a Firebase

```kotlin
private fun authenticateWithFirebase(credential: AuthCredential) {
    // ✅ FIREBASE PROCESA LA CREDENTIAL
    firebaseAuth.signInWithCredential(credential)
        .addOnCompleteListener(this) { task ->
            if (task.isSuccessful) {
                Log.d(TAG, "✅ Autenticado")
            } else {
                Log.e(TAG, "❌ Error: ${task.exception?.message}")
            }
        }
}
```

---

## 🎯 Resumen

### ¿Cómo convertir JWT a Credential?

```kotlin
val credential = GoogleAuthProvider.getCredential(idToken, null)
```

### ¿Qué sucede después?

1. Firebase recibe la credential
2. Firebase verifica el JWT
3. Firebase crea la sesión
4. Usuario autenticado

### ¿Cuánto código necesitas?

```
Obtener JWT: 1 línea (account.idToken)
Convertir a Credential: 1 línea (GoogleAuthProvider.getCredential)
Enviar a Firebase: 1 línea (signInWithCredential)
Total: 3 líneas de código
```

---

**Última actualización**: 29 de Mayo de 2026
