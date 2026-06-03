# Cómo Convertir JWT de Google en Credential para Firebase

## 🎯 Tu Pregunta

> "El JWT que recibo de Google, ¿cómo lo transformo en la credential o cómo hago esa credential para que Firebase la procese?"

## ✅ Respuesta Corta

**Es muy simple. Una línea de código:**

```kotlin
val credential = GoogleAuthProvider.getCredential(idToken, null)
```

Eso es todo. Firebase hace el resto automáticamente.

---

## 📋 Explicación Detallada

### ¿Qué es un JWT?

JWT = JSON Web Token. Es un token firmado que contiene información del usuario.

```
JWT de Google:
eyJhbGciOiJSUzI1NiIsImtpZCI6IjEyMyJ9.eyJpc3MiOiJodHRwczovL2FjY291bnRzLmdvb2dsZS5jb20iLCJhenAiOiIxMjM0NTY3ODkwLmFwcHMuZ29vZ2xldXNlcmNvbnRlbnQuY29tIiwiYXVkIjoiMTIzNDU2Nzg5MC5hcHBzLmdvb2dsZXVzZXJjb250ZW50LmNvbSIsInN1YiI6IjEwNzY5MTUwMzUwMDA2MTUwNzE2IiwiZW1haWwiOiJ1c2VyQGdtYWlsLmNvbSIsImVtYWlsX3ZlcmlmaWVkIjp0cnVlLCJhdF9oYXNoIjoiWDVyVl9aMTBTRzVUWTJhMEhRIiwibmFtZSI6IkpvaG4gRG9lIiwicGljdHVyZSI6Imh0dHBzOi8vbGgzLmdvb2dsZXVzZXJjb250ZW50LmNvbS9hL2RlZmF1bHQtdXNlciIsImdpdmVuX25hbWUiOiJKb2huIiwiZmFtaWx5X25hbWUiOiJEb2UiLCJsb2NhbGUiOiJlcyIsImlhdCI6MTcxNzAwMDAwMCwiZXhwIjoxNzE3MDAzNjAwfQ.signature...

Estructura:
[Header].[Payload].[Signature]
```

### ¿Qué es una Credential?

Una Credential es un objeto que Firebase entiende. Es como un "pase" que le dices a Firebase: "Este usuario es válido, confía en mí".

```kotlin
// Credential para Firebase
val credential = GoogleAuthProvider.getCredential(idToken, null)

// Firebase entiende este objeto y lo procesa
```

### ¿Cómo Funciona la Conversión?

```
JWT de Google
    ↓
GoogleAuthProvider.getCredential(idToken, null)
    ↓
Credential (objeto que Firebase entiende)
    ↓
firebaseAuth.signInWithCredential(credential)
    ↓
Firebase verifica el JWT
    ↓
Firebase crea la sesión del usuario
```

---

## 💻 Código Paso a Paso

### Paso 1: Obtener el JWT de Google

```kotlin
private val googleSignInLauncher = registerForActivityResult(
    ActivityResultContracts.StartActivityForResult()
) { result ->
    try {
        val task = GoogleSignIn.getSignedInAccountFromIntent(result.data)
        val account = task.getResult(ApiException::class.java)
        
        // ✅ AQUÍ TIENES EL JWT
        val idToken = account.idToken  // "eyJhbGc..."
        
        Log.d(TAG, "JWT obtenido: ${idToken?.take(30)}...")
        
        // Pasar al siguiente paso
        firebaseAuthWithGoogle(idToken!!)
    } catch (e: ApiException) {
        Log.e(TAG, "Error: ${e.statusCode}", e)
    }
}
```

### Paso 2: Convertir JWT a Credential

```kotlin
private fun firebaseAuthWithGoogle(idToken: String) {
    Log.d(TAG, "JWT recibido: ${idToken.take(30)}...")
    
    // ✅ CONVERTIR JWT A CREDENTIAL (UNA LÍNEA)
    val credential = GoogleAuthProvider.getCredential(idToken, null)
    
    Log.d(TAG, "Credential creada: $credential")
    
    // Pasar al siguiente paso
    authenticateWithFirebase(credential)
}
```

### Paso 3: Enviar Credential a Firebase

```kotlin
private fun authenticateWithFirebase(credential: AuthCredential) {
    Log.d(TAG, "Enviando credential a Firebase...")
    
    // ✅ FIREBASE PROCESA LA CREDENTIAL
    firebaseAuth.signInWithCredential(credential)
        .addOnCompleteListener(this) { task ->
            if (task.isSuccessful) {
                Log.d(TAG, "✅ Firebase autenticó exitosamente")
                Log.d(TAG, "Usuario: ${firebaseAuth.currentUser?.email}")
                
                // Usuario autenticado
                navigateToDashboard()
            } else {
                Log.e(TAG, "❌ Firebase rechazó la credential: ${task.exception?.message}")
            }
        }
}
```

---

## 🔍 Desglose de `GoogleAuthProvider.getCredential()`

### ¿Qué Hace Esta Función?

```kotlin
GoogleAuthProvider.getCredential(idToken, null)
```

**Parámetro 1: idToken**
```
El JWT que recibiste de Google
Ejemplo: "eyJhbGc..."
```

**Parámetro 2: accessToken**
```
null (no lo necesitas para autenticación)
Solo se usa si quieres acceder a APIs de Google
```

**Retorna:**
```
Un objeto AuthCredential que Firebase entiende
```

### ¿Qué Sucede Internamente?

```
GoogleAuthProvider.getCredential(idToken, null)
    ↓
1. Extrae la información del JWT
2. Crea un objeto AuthCredential
3. Marca que es de Google
4. Retorna el objeto
    ↓
Credential listo para Firebase
```

---

## 📊 Flujo Completo

```
┌─────────────────────────────────────────────────────────────┐
│ 1. Usuario hace clic en "Continuar con Google"              │
│    ↓                                                         │
│ 2. Google Sign-In Selector se abre                          │
│    ↓                                                         │
│ 3. Usuario selecciona su cuenta                             │
│    ↓                                                         │
│ 4. Google genera JWT                                        │
│    ├─ Header: { "alg": "RS256", "typ": "JWT" }             │
│    ├─ Payload: { "email": "...", "name": "...", ... }      │
│    └─ Signature: Firmado con clave privada de Google       │
│    ↓                                                         │
│ 5. Tu app recibe el JWT                                     │
│    ├─ account.idToken = "eyJhbGc..."                        │
│    └─ Log: "JWT obtenido: eyJhbGc..."                       │
│    ↓                                                         │
│ 6. CONVERTIR JWT A CREDENTIAL (UNA LÍNEA)                  │
│    ├─ val credential = GoogleAuthProvider.getCredential(    │
│    │     idToken, null                                      │
│    │ )                                                      │
│    └─ Log: "Credential creada: ..."                         │
│    ↓                                                         │
│ 7. ENVIAR CREDENTIAL A FIREBASE                            │
│    ├─ firebaseAuth.signInWithCredential(credential)         │
│    └─ Firebase procesa la credential                        │
│    ↓                                                         │
│ 8. Firebase Verifica el JWT                                 │
│    ├─ Verifica la firma con clave pública de Google         │
│    ├─ Verifica que no está expirado                         │
│    └─ Verifica que es válido                                │
│    ↓                                                         │
│ 9. Firebase Crea Sesión                                     │
│    ├─ Crea usuario en Firebase Auth                         │
│    ├─ Genera Firebase ID Token                              │
│    └─ Genera Firebase Refresh Token                         │
│    ↓                                                         │
│ 10. Usuario Autenticado                                     │
│    ├─ firebaseAuth.currentUser != null                      │
│    ├─ Log: "✅ Firebase autenticó exitosamente"             │
│    └─ Navegar al Dashboard                                  │
└─────────────────────────────────────────────────────────────┘
```

---

## 💻 Código Completo en SignUpActivity

```kotlin
package com.daniel.chickenfood.presentation.activity.auth

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.daniel.chickenfood.R
import com.daniel.chickenfood.presentation.activity.BaseActivity
import com.daniel.chickenfood.presentation.activity.dashboard.MainActivity
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider

private const val TAG = "SignUpActivity"

class SignUpActivity : BaseActivity() {

    private lateinit var googleSignInClient: GoogleSignInClient
    private lateinit var firebaseAuth: FirebaseAuth

    // ✅ PASO 1: OBTENER JWT DE GOOGLE
    private val googleSignInLauncher = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result ->
        try {
            val task = GoogleSignIn.getSignedInAccountFromIntent(result.data)
            val account = task.getResult(ApiException::class.java)
            
            // ✅ AQUÍ TIENES EL JWT
            val idToken = account.idToken
            Log.d(TAG, "✅ JWT obtenido de Google: ${idToken?.take(30)}...")
            
            // Pasar al siguiente paso
            firebaseAuthWithGoogle(idToken!!)
        } catch (e: ApiException) {
            Log.e(TAG, "❌ Error en Google Sign-In: ${e.statusCode}", e)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        firebaseAuth = FirebaseAuth.getInstance()

        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(getString(R.string.default_web_client_id))
            .requestEmail()
            .build()

        googleSignInClient = GoogleSignIn.getClient(this, gso)

        setContent {
            SignUpScreen(
                onGoogleSignUpClick = { signUpWithGoogle() },
                onBackClick = { finish() }
            )
        }
    }

    private fun signUpWithGoogle() {
        Log.d(TAG, "Usuario hace clic en 'Continuar con Google'")
        val signInIntent = googleSignInClient.signInIntent
        googleSignInLauncher.launch(signInIntent)
    }

    // ✅ PASO 2: CONVERTIR JWT A CREDENTIAL
    private fun firebaseAuthWithGoogle(idToken: String) {
        Log.d(TAG, "Paso 2: Convertir JWT a Credential")
        Log.d(TAG, "JWT recibido: ${idToken.take(30)}...")
        
        // ✅ ESTA ES LA LÍNEA MÁGICA
        // Convierte el JWT en un objeto que Firebase entiende
        val credential = GoogleAuthProvider.getCredential(idToken, null)
        
        Log.d(TAG, "✅ Credential creada exitosamente")
        Log.d(TAG, "Credential: $credential")
        
        // Pasar al siguiente paso
        authenticateWithFirebase(credential)
    }

    // ✅ PASO 3: ENVIAR CREDENTIAL A FIREBASE
    private fun authenticateWithFirebase(credential: com.google.firebase.auth.AuthCredential) {
        Log.d(TAG, "Paso 3: Enviar Credential a Firebase")
        Log.d(TAG, "Enviando credential a Firebase...")
        
        firebaseAuth.signInWithCredential(credential)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    Log.d(TAG, "✅ Firebase autenticó exitosamente")
                    Log.d(TAG, "Usuario: ${firebaseAuth.currentUser?.email}")
                    Log.d(TAG, "UID: ${firebaseAuth.currentUser?.uid}")
                    
                    // Obtener Firebase Token
                    firebaseAuth.currentUser?.getIdToken(false)?.addOnCompleteListener { tokenTask ->
                        if (tokenTask.isSuccessful) {
                            val firebaseToken = tokenTask.result?.token
                            Log.d(TAG, "✅ Firebase Token obtenido: ${firebaseToken?.take(30)}...")
                            
                            navigateToDashboard()
                        }
                    }
                } else {
                    Log.e(TAG, "❌ Firebase rechazó la credential: ${task.exception?.message}")
                }
            }
    }

    private fun navigateToDashboard() {
        Log.d(TAG, "Navegando al Dashboard")
        val intent = Intent(this, MainActivity::class.java)
        startActivity(intent)
        finish()
    }
}

@Composable
fun SignUpScreen(
    onGoogleSignUpClick: () -> Unit,
    onBackClick: () -> Unit
) {
    var isLoading by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(colorResource(R.color.darkBrown))
            .padding(16.dp),
        verticalArrangement = Arrangement.Top,
        horizontalAlignment = Alignment.Start
    ) {
        IconButton(
            onClick = onBackClick,
            modifier = Modifier.padding(top = 8.dp)
        ) {
            Icon(
                painter = painterResource(R.drawable.back_grey),
                contentDescription = "Back",
                tint = Color.White
            )
        }

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(top = 32.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "Inscribete",
                fontSize = 32.sp,
                fontWeight = FontWeight.Bold,
                color = Color.White,
                modifier = Modifier.padding(bottom = 48.dp)
            )

            if (isLoading) {
                CircularProgressIndicator(
                    color = colorResource(R.color.orange),
                    modifier = Modifier.padding(bottom = 24.dp)
                )
                Text(
                    text = "Autenticando...",
                    fontSize = 16.sp,
                    color = Color.White
                )
            } else {
                Button(
                    onClick = {
                        isLoading = true
                        onGoogleSignUpClick()
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(56.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color(0xFF1F7FE8)
                    ),
                    shape = androidx.compose.foundation.shape.RoundedCornerShape(8.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.Center
                    ) {
                        Image(
                            painter = painterResource(R.drawable.ic_google_logo),
                            contentDescription = "Google Logo",
                            modifier = Modifier
                                .height(24.dp)
                                .padding(end = 12.dp)
                        )
                        
                        Text(
                            text = "Continuar con Google",
                            fontSize = 16.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = Color.White
                        )
                    }
                }
            }
        }
    }
}
```

---

## 🔐 ¿Qué Verifica Firebase?

Cuando envías la credential a Firebase, Firebase hace esto:

```
1. Extrae el JWT de la credential
2. Verifica la firma del JWT
   ├─ Obtiene la clave pública de Google
   └─ Verifica que la firma es válida
3. Verifica que el JWT no está expirado
4. Verifica que el JWT es para tu app
   ├─ Comprueba el "aud" (audience)
   └─ Debe coincidir con tu Web Client ID
5. Si todo es válido:
   ├─ Crea usuario en Firebase Auth
   ├─ Genera Firebase ID Token
   └─ Genera Firebase Refresh Token
6. Si algo falla:
   └─ Rechaza la credential
```

---

## ❓ Preguntas Frecuentes

### P: ¿Qué es GoogleAuthProvider?
**R:** Es una clase de Firebase que sabe cómo procesar JWTs de Google.

### P: ¿Por qué el segundo parámetro es null?
**R:** El segundo parámetro es para el accessToken de Google. No lo necesitas para autenticación, solo si quieres acceder a APIs de Google.

### P: ¿Qué pasa si el JWT es inválido?
**R:** Firebase rechaza la credential y `task.isSuccessful` es false.

### P: ¿Cuánto tiempo tarda Firebase en verificar?
**R:** Típicamente 1-2 segundos. Depende de la conexión a internet.

### P: ¿Puedo reutilizar el mismo JWT?
**R:** NO. Los JWTs expiran (típicamente en 1 hora). Debes obtener uno nuevo.

### P: ¿Dónde se verifica el JWT?
**R:** En los servidores de Firebase. Tu app solo envía la credential.

---

## 📊 Resumen Visual

```
JWT de Google
    ↓
    │ GoogleAuthProvider.getCredential(idToken, null)
    ↓
Credential
    ↓
    │ firebaseAuth.signInWithCredential(credential)
    ↓
Firebase Verifica
    ├─ Firma del JWT
    ├─ Expiración
    └─ Audience (aud)
    ↓
Usuario Autenticado
    ├─ Firebase ID Token
    ├─ Firebase Refresh Token
    └─ currentUser != null
```

---

## 🎯 Resumen

### ¿Cómo convertir JWT a Credential?

**Una línea de código:**
```kotlin
val credential = GoogleAuthProvider.getCredential(idToken, null)
```

### ¿Qué hace Firebase?

1. Verifica la firma del JWT
2. Verifica que no está expirado
3. Verifica que es para tu app
4. Crea la sesión del usuario

### ¿Qué haces tú?

1. Obtener el JWT de Google
2. Convertir a Credential (una línea)
3. Enviar a Firebase
4. Esperar la respuesta

---

**Última actualización**: 29 de Mayo de 2026
