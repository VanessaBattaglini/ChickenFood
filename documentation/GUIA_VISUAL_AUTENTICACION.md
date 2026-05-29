# Guía Visual: Autenticación Passwordless

## 📱 Pantallas de la Aplicación

### Pantalla 1: Splash Screen
```
┌─────────────────────────────────────┐
│                                     │
│         [Pollo Logo]                │
│                                     │
│  El sabor que convierte cada        │
│  comida en un crujiente placer      │
│                                     │
│  Cada sabor crea momentos, para     │
│  disfrutar y compartir en familia   │
│                                     │
│                                     │
│  ┌─────────────────────────────┐   │
│  │      [Empecemos]            │   │
│  └─────────────────────────────┘   │
│                                     │
│  ┌─────────────────────────────┐   │
│  │      [Inscribete]           │   │
│  └─────────────────────────────┘   │
│                                     │
└─────────────────────────────────────┘

Botones:
- "Empecemos" (Naranja) → Dashboard sin autenticación
- "Inscribete" (Blanco con borde) → SignUpActivity
```

### Pantalla 2: Sign Up Screen
```
┌─────────────────────────────────────┐
│  [←]                                │
│                                     │
│                                     │
│         Inscribete                  │
│                                     │
│                                     │
│  ┌─────────────────────────────┐   │
│  │  [Google] Continuar con     │   │
│  │           Google            │   │
│  └─────────────────────────────┘   │
│                                     │
│                                     │
│                                     │
└─────────────────────────────────────┘

Elementos:
- Botón atrás (esquina superior izquierda)
- Título "Inscribete"
- Botón "Continuar con Google" (azul con logo de Google)
```

### Pantalla 3: Google Sign-In Selector
```
┌─────────────────────────────────────┐
│  Google Sign-In                     │
│                                     │
│  Selecciona una cuenta              │
│                                     │
│  ☐ usuario1@gmail.com              │
│    Nombre Usuario 1                 │
│                                     │
│  ☐ usuario2@gmail.com              │
│    Nombre Usuario 2                 │
│                                     │
│  ☐ usuario3@gmail.com              │
│    Nombre Usuario 3                 │
│                                     │
│  ┌─────────────────────────────┐   │
│  │  [+] Usar otra cuenta       │   │
│  └─────────────────────────────┘   │
│                                     │
│  ✅ NO PIDE CONTRASEÑA              │
│                                     │
└─────────────────────────────────────┘

Nota: Si la cuenta está autenticada en el dispositivo,
NO pide contraseña. Solo selecciona y listo.
```

### Pantalla 4: Dashboard (MainActivity)
```
┌─────────────────────────────────────┐
│  [Menu]  ChickenFood  [Logout]      │
├─────────────────────────────────────┤
│                                     │
│  [Banner Image]                     │
│                                     │
│  Categorías                         │
│  ┌──────┐ ┌──────┐ ┌──────┐        │
│  │ Cat1 │ │ Cat2 │ │ Cat3 │        │
│  └──────┘ └──────┘ └──────┘        │
│                                     │
│  Productos                          │
│  ┌──────────────────────────────┐   │
│  │ [Img] Producto 1             │   │
│  │ Descripción                  │   │
│  │ $10.00                       │   │
│  └──────────────────────────────┘   │
│                                     │
│  ┌──────────────────────────────┐   │
│  │ [Img] Producto 2             │   │
│  │ Descripción                  │   │
│  │ $12.00                       │   │
│  └──────────────────────────────┘   │
│                                     │
│  [🛒] Carrito                       │
│                                     │
└─────────────────────────────────────┘

Elementos:
- TopBar con botón de logout
- Banner
- Categorías
- Productos
- Carrito
```

---

## 🔄 Flujo de Navegación

```
┌──────────────────┐
│  SplashActivity  │
└────────┬─────────┘
         │
    ┌────┴────┐
    │          │
    ▼          ▼
┌─────────┐  ┌──────────────┐
│Empecemos│  │ Inscribete   │
└────┬────┘  └──────┬───────┘
     │              │
     │              ▼
     │         ┌──────────────┐
     │         │SignUpActivity│
     │         └──────┬───────┘
     │                │
     │                ▼
     │         ┌──────────────────┐
     │         │Google Sign-In    │
     │         │Selector          │
     │         └──────┬───────────┘
     │                │
     │                ▼
     │         ┌──────────────────┐
     │         │Firebase Auth     │
     │         │(verifica token)  │
     │         └──────┬───────────┘
     │                │
     └────────┬───────┘
              │
              ▼
         ┌──────────────┐
         │MainActivity  │
         │(Dashboard)   │
         └──────┬───────┘
                │
                ▼
         ┌──────────────┐
         │Logout Button │
         │(TopBar)      │
         └──────┬───────┘
                │
                ▼
         ┌──────────────┐
         │SplashActivity│
         │(de nuevo)    │
         └──────────────┘
```

---

## 🔐 Flujo de Autenticación Detallado

```
┌─────────────────────────────────────────────────────────────┐
│ PASO 1: Usuario hace clic en "Inscribete"                  │
│                                                             │
│ SplashActivity.navigateToSignUp()                          │
│ ↓                                                           │
│ Intent(this, SignUpActivity::class.java)                   │
│ ↓                                                           │
│ SignUpActivity se abre                                     │
└─────────────────────────────────────────────────────────────┘
                            ↓
┌─────────────────────────────────────────────────────────────┐
│ PASO 2: Usuario hace clic en "Continuar con Google"        │
│                                                             │
│ SignUpActivity.signUpWithGoogle()                          │
│ ↓                                                           │
│ val signInIntent = googleSignInClient.signInIntent         │
│ ↓                                                           │
│ googleSignInLauncher.launch(signInIntent)                  │
│ ↓                                                           │
│ Google Sign-In Selector se abre                            │
└─────────────────────────────────────────────────────────────┘
                            ↓
┌─────────────────────────────────────────────────────────────┐
│ PASO 3: Usuario selecciona su cuenta Google                │
│                                                             │
│ Google verifica:                                            │
│ ✓ ¿Dispositivo está registrado?                            │
│ ✓ ¿Cuenta está autenticada en el dispositivo?              │
│ ✓ ¿Usuario tiene permiso para usar esta app?               │
│                                                             │
│ Si todo es correcto:                                        │
│ ✅ NO pide contraseña                                       │
│ ✅ Genera ID Token (JWT)                                    │
│ ✅ Devuelve el token a tu app                               │
└─────────────────────────────────────────────────────────────┘
                            ↓
┌─────────────────────────────────────────────────────────────┐
│ PASO 4: googleSignInLauncher recibe el resultado           │
│                                                             │
│ val task = GoogleSignIn.getSignedInAccountFromIntent()     │
│ ↓                                                           │
│ val account = task.getResult(ApiException::class.java)     │
│ ↓                                                           │
│ firebaseAuthWithGoogle(account.idToken!!)                  │
└─────────────────────────────────────────────────────────────┘
                            ↓
┌─────────────────────────────────────────────────────────────┐
│ PASO 5: Se autentica con Firebase                          │
│                                                             │
│ val credential = GoogleAuthProvider.getCredential(         │
│     idToken, null                                           │
│ )                                                           │
│ ↓                                                           │
│ firebaseAuth.signInWithCredential(credential)              │
│ ↓                                                           │
│ Firebase verifica la firma del token                        │
│ ↓                                                           │
│ Firebase crea sesión del usuario                           │
└─────────────────────────────────────────────────────────────┘
                            ↓
┌─────────────────────────────────────────────────────────────┐
│ PASO 6: Usuario autenticado                                │
│                                                             │
│ navigateToDashboard()                                       │
│ ↓                                                           │
│ Intent(this, MainActivity::class.java)                     │
│ ↓                                                           │
│ MainActivity (Dashboard) se abre                            │
│ ↓                                                           │
│ Usuario puede acceder a:                                    │
│ - AuthHelper.getCurrentUser()                              │
│ - AuthHelper.getUserEmail()                                │
│ - AuthHelper.getUserName()                                 │
│ - AuthHelper.getUserPhotoUrl()                             │
└─────────────────────────────────────────────────────────────┘
```

---

## 🔑 Tokens y Seguridad

### ID Token (JWT)
```
┌─────────────────────────────────────────────────────────────┐
│ ID Token (JWT) generado por Google                          │
│                                                             │
│ Header:                                                     │
│ {                                                           │
│   "alg": "RS256",                                           │
│   "typ": "JWT"                                              │
│ }                                                           │
│                                                             │
│ Payload:                                                    │
│ {                                                           │
│   "iss": "https://accounts.google.com",                     │
│   "email": "usuario@gmail.com",                             │
│   "name": "Nombre Usuario",                                 │
│   "picture": "https://...",                                 │
│   "aud": "YOUR_WEB_CLIENT_ID",                              │
│   "sub": "GOOGLE_UID",                                      │
│   "iat": 1234567890,                                        │
│   "exp": 1234571490                                         │
│ }                                                           │
│                                                             │
│ Signature:                                                  │
│ HMACSHA256(                                                 │
│   base64UrlEncode(header) + "." +                           │
│   base64UrlEncode(payload),                                 │
│   secret                                                    │
│ )                                                           │
│                                                             │
│ Firmado con clave privada de Google                         │
│ Verificado con clave pública de Google                      │
└─────────────────────────────────────────────────────────────┘
```

### Flujo de Tokens
```
┌──────────────┐
│ Google       │
│ Genera       │
│ ID Token     │
└──────┬───────┘
       │
       ▼
┌──────────────────────┐
│ Tu App               │
│ Recibe ID Token      │
│ (NO lo almacena)     │
└──────┬───────────────┘
       │
       ▼
┌──────────────────────┐
│ Firebase             │
│ Verifica ID Token    │
│ (verifica firma)     │
└──────┬───────────────┘
       │
       ▼
┌──────────────────────┐
│ Firebase             │
│ Crea sesión          │
│ Genera token de      │
│ sesión de Firebase   │
└──────┬───────────────┘
       │
       ▼
┌──────────────────────┐
│ Tu App               │
│ Almacena token de    │
│ sesión de Firebase   │
│ (en SharedPreferences)
└──────────────────────┘
```

---

## 🛡️ Seguridad

### ¿Dónde está la contraseña?
```
┌─────────────────────────────────────────────────────────────┐
│ Contraseña del usuario                                      │
│                                                             │
│ ❌ NO está en tu app                                        │
│ ❌ NO se envía a tu app                                     │
│ ❌ NO se almacena en tu app                                 │
│ ✅ Google la maneja de forma segura                         │
│ ✅ Firebase NUNCA la ve                                     │
│ ✅ Tu app NUNCA la ve                                       │
└─────────────────────────────────────────────────────────────┘
```

### Información que tiene tu app
```
┌─────────────────────────────────────────────────────────────┐
│ Información disponible en tu app                            │
│                                                             │
│ ✅ Email del usuario                                        │
│ ✅ Nombre del usuario                                       │
│ ✅ Foto del usuario                                         │
│ ✅ UID de Firebase                                          │
│ ✅ Token de sesión de Firebase                              │
│                                                             │
│ ❌ Contraseña del usuario                                   │
│ ❌ Tokens de Google (solo ID Token)                         │
│ ❌ Información sensible                                     │
└─────────────────────────────────────────────────────────────┘
```

---

## 📊 Comparación: Con Contraseña vs Passwordless

### Con Contraseña (❌ NO recomendado)
```
┌─────────────────────────────────────────────────────────────┐
│ Usuario ingresa email                                       │
│ ↓                                                           │
│ Usuario ingresa contraseña                                  │
│ ↓                                                           │
│ Tu app verifica contraseña                                  │
│ ↓                                                           │
│ Problemas:                                                  │
│ ❌ Contraseña se envía a tu app                             │
│ ❌ Contraseña se almacena en tu app                         │
│ ❌ Riesgo de robo de contraseña                             │
│ ❌ Responsabilidad de seguridad en tu app                   │
│ ❌ Experiencia de usuario pobre                             │
└─────────────────────────────────────────────────────────────┘
```

### Passwordless con Google (✅ Recomendado)
```
┌─────────────────────────────────────────────────────────────┐
│ Usuario selecciona cuenta Google                            │
│ ↓                                                           │
│ Google verifica autenticación del dispositivo               │
│ ↓                                                           │
│ Google genera ID Token                                      │
│ ↓                                                           │
│ Tu app recibe ID Token                                      │
│ ↓                                                           │
│ Firebase verifica token                                     │
│ ↓                                                           │
│ Ventajas:                                                   │
│ ✅ Contraseña NO se envía a tu app                          │
│ ✅ Contraseña NO se almacena en tu app                      │
│ ✅ Google maneja la seguridad                               │
│ ✅ Tu app solo recibe un token                              │
│ ✅ Experiencia de usuario excelente                         │
└─────────────────────────────────────────────────────────────┘
```

---

## 🎯 Resumen Visual

```
┌─────────────────────────────────────────────────────────────┐
│ AUTENTICACIÓN PASSWORDLESS CON GOOGLE SIGN-IN               │
│                                                             │
│ ✅ Segura: Google + Firebase manejan la seguridad          │
│ ✅ Fácil: Usuario solo selecciona su cuenta                │
│ ✅ Sin contraseña: NO se pide contraseña                   │
│ ✅ Moderna: Usa OAuth 2.0 y JWT                            │
│ ✅ Escalable: Firebase maneja millones de usuarios         │
│                                                             │
│ FLUJO:                                                      │
│ 1. Usuario abre app → SplashActivity                       │
│ 2. Haz clic en "Inscribete" → SignUpActivity              │
│ 3. Haz clic en "Continuar con Google" → Google Sign-In    │
│ 4. Selecciona tu cuenta → Google verifica                 │
│ 5. ✅ NO pide contraseña → ID Token generado              │
│ 6. Firebase autentica → Sesión creada                     │
│ 7. Dashboard → Usuario autenticado                         │
│                                                             │
│ LOGOUT:                                                     │
│ 1. Haz clic en botón de logout (TopBar)                   │
│ 2. AuthHelper.signOut() → Sesión cerrada                  │
│ 3. Vuelve a SplashActivity                                │
└─────────────────────────────────────────────────────────────┘
```

---

**Última actualización**: 29 de Mayo de 2026
