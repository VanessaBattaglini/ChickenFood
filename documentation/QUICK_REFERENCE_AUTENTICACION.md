# Quick Reference: Autenticación Passwordless

## 🚀 Inicio Rápido

### ¿Qué es?
Autenticación sin contraseña usando Google Sign-In. El usuario solo selecciona su cuenta Google.

### ¿Cómo funciona?
```
Usuario abre app → Haz clic en "Inscribete" → Selecciona cuenta Google → ✅ Autenticado
```

### ¿Pide contraseña?
❌ **NO** (si la cuenta está autenticada en el dispositivo)

---

## 📱 Flujo de la App

```
SplashActivity
├─ "Empecemos" → Dashboard (sin auth)
└─ "Inscribete" → SignUpActivity
                  └─ "Continuar con Google" → Google Sign-In
                                              └─ Dashboard (con auth)
```

---

## 🔧 Configuración Requerida

### 1. Web Client ID
```bash
# Firebase Console → Project Settings → General
# Copiar Web Client ID
# Pegar en: app/src/main/res/values/strings.xml
```

### 2. SHA-1
```bash
./gradlew signingReport
# Copiar SHA-1 de "debugAndroidDebugKey"
# Pegar en: Firebase Console → Project Settings → Your apps
```

### 3. google-services.json
```bash
# Descargar de: Firebase Console → Project Settings → Your apps
# Copiar a: app/google-services.json
```

---

## 📝 Archivos Principales

| Archivo | Función |
|---------|---------|
| `SplashActivity.kt` | Punto de entrada, dos botones |
| `SignUpActivity.kt` | Google Sign-In UI |
| `AuthHelper.kt` | Acceso a información del usuario |
| `TopBar.kt` | Botón de logout |
| `strings.xml` | Web Client ID |
| `build.gradle.kts` | Dependencias |

---

## 💻 Código Clave

### Verificar si usuario está autenticado
```kotlin
if (AuthHelper.isUserLoggedIn()) {
    // Usuario autenticado
    val email = AuthHelper.getUserEmail()
    val name = AuthHelper.getUserName()
}
```

### Cerrar sesión
```kotlin
AuthHelper.signOut()
// Navegar a SplashActivity
```

### Obtener información del usuario
```kotlin
val user = AuthHelper.getCurrentUser()
val email = AuthHelper.getUserEmail()
val name = AuthHelper.getUserName()
val photoUrl = AuthHelper.getUserPhotoUrl()
```

---

## 🧪 Pruebas Rápidas

### Test 1: Sin autenticación
```
1. Abre la app
2. Haz clic en "Empecemos"
3. ✅ Ves el Dashboard
```

### Test 2: Con Google Sign-In
```
1. Abre la app
2. Haz clic en "Inscribete"
3. Haz clic en "Continuar con Google"
4. Selecciona tu cuenta
5. ✅ NO pide contraseña
6. ✅ Ves el Dashboard
```

### Test 3: Logout
```
1. En Dashboard, haz clic en logout (TopBar)
2. ✅ Vuelves a SplashActivity
```

---

## 🐛 Problemas Comunes

| Problema | Solución |
|----------|----------|
| "Invalid Web Client ID" | Verifica que el Web Client ID sea correcto en `strings.xml` |
| "SHA-1 not registered" | Agrega el SHA-1 a Firebase Console |
| "google-services.json not found" | Descarga el archivo de Firebase y colócalo en `app/` |
| Google Sign-In no abre | Verifica que el Web Client ID sea correcto |
| Pide contraseña | Normal si la cuenta NO está autenticada en el dispositivo |

---

## 📊 Resumen

| Aspecto | Detalles |
|--------|---------|
| **Tipo** | OAuth 2.0 Passwordless |
| **Proveedor** | Google |
| **¿Contraseña?** | ❌ NO |
| **Seguridad** | ✅ Muy alta |
| **Experiencia** | ✅ Muy buena |
| **Estado** | ✅ Listo |

---

## 🎯 Checklist de Configuración

- [ ] Obtener Web Client ID de Firebase
- [ ] Actualizar `strings.xml` con Web Client ID
- [ ] Obtener SHA-1 con `./gradlew signingReport`
- [ ] Agregar SHA-1 a Firebase Console
- [ ] Descargar `google-services.json`
- [ ] Copiar `google-services.json` a `app/`
- [ ] Compilar: `./gradlew clean build`
- [ ] Instalar: `./gradlew installDebug`
- [ ] Probar: Abre la app y prueba los flujos

---

## 🔗 Recursos

- [Firebase Console](https://console.firebase.google.com/)
- [Google Sign-In Docs](https://developers.google.com/identity/sign-in/android)
- [Firebase Auth Docs](https://firebase.google.com/docs/auth)

---

## 📚 Documentación Completa

- `RESUMEN_AUTENTICACION_PASSWORDLESS.md` - Resumen general
- `VERIFICACION_AUTENTICACION_PASSWORDLESS.md` - Verificación técnica
- `PASOS_FINALES_CONFIGURACION.md` - Guía de configuración
- `COMO_FUNCIONA_PASSWORDLESS.md` - Explicación técnica

---

**Última actualización**: 29 de Mayo de 2026
