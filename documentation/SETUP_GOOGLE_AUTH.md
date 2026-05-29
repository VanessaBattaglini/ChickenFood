# Setup Rápido: Autenticación con Google

## 🚀 4 Pasos para Activar Google Auth

### Paso 1: Obtener Web Client ID (2 minutos)

1. Ve a [Firebase Console](https://console.firebase.google.com/)
2. Selecciona tu proyecto "ChickenFood"
3. Ve a **Authentication** → **Sign-in method**
4. Busca la sección de **Google**
5. Copia el **Web Client ID** (algo como: `123456789-abcdefg.apps.googleusercontent.com`)

### Paso 2: Actualizar strings.xml (1 minuto)

Abre `app/src/main/res/values/strings.xml` y reemplaza:

```xml
<string name="default_web_client_id">YOUR_WEB_CLIENT_ID_HERE</string>
```

Con tu Web Client ID:

```xml
<string name="default_web_client_id">123456789-abcdefg.apps.googleusercontent.com</string>
```

### Paso 3: Obtener SHA-1 (2 minutos)

En terminal, ejecuta:

```bash
cd /Users/danielalvarado/AndroidStudioProjects/ChickenFood
./gradlew signingReport
```

Busca en la salida algo como:

```
SHA1: AB:CD:EF:12:34:56:78:90:AB:CD:EF:12:34:56:78:90:AB:CD:EF:12
```

Copia el SHA-1 (sin los dos puntos).

### Paso 4: Agregar SHA-1 a Firebase (2 minutos)

1. Ve a [Firebase Console](https://console.firebase.google.com/)
2. Selecciona tu proyecto
3. Ve a **Project Settings** (engranaje arriba a la izquierda)
4. En la sección **Your apps**, selecciona tu app Android
5. En **SHA certificate fingerprints**, haz clic en **Add fingerprint**
6. Pega el SHA-1 (sin los dos puntos)
7. Haz clic en **Save**

---

## ✅ Verificación

Después de completar los 4 pasos:

1. Compila la app:
   ```bash
   ./gradlew clean build
   ```

2. Ejecuta en emulador:
   ```bash
   ./gradlew installDebug
   ```

3. Abre la app:
   - Deberías ver la pantalla de Splash
   - Después de 3 segundos, deberías ver LoginActivity
   - Haz clic en "Iniciar sesión con Google"
   - Selecciona una cuenta de Google
   - Deberías ser redirigido al Dashboard

---

## 🎯 Flujo Completo

```
Splash (3s)
    ↓
¿Autenticado?
    ├─ SÍ → Dashboard
    └─ NO → Login
        ↓
    Botón "Iniciar sesión con Google"
        ↓
    Google Sign-In
        ↓
    Dashboard
```

---

## 📝 Archivos Modificados

- ✅ `LoginActivity.kt` - Creado
- ✅ `AuthHelper.kt` - Creado
- ✅ `SplashActivity.kt` - Actualizado
- ✅ `TopBar.kt` - Actualizado (botón logout)
- ✅ `strings.xml` - Actualizado
- ✅ `AndroidManifest.xml` - Actualizado
- ✅ `gradle/libs.versions.toml` - Actualizado
- ✅ `app/build.gradle.kts` - Actualizado

---

## 🔐 Funciones Disponibles

Usa `AuthHelper` en cualquier parte de tu código:

```kotlin
// Obtener información del usuario
val email = AuthHelper.getUserEmail()
val name = AuthHelper.getUserName()
val photoUrl = AuthHelper.getUserPhotoUrl()

// Verificar si está autenticado
if (AuthHelper.isUserLoggedIn()) {
    // Usuario autenticado
}

// Cerrar sesión
AuthHelper.signOut()
```

---

## 🐛 Si Algo No Funciona

### Error: "Google Sign-In falló"
- Verifica que el SHA-1 está en Firebase
- Verifica que el Web Client ID es correcto
- Verifica que tienes internet

### Error: "Firebase Auth falló"
- Verifica que Google Sign-In está habilitado en Firebase
- Verifica que google-services.json está en `app/`

### No aparece LoginActivity
- Verifica que compilaste con `./gradlew clean build`
- Verifica que no hay errores de compilación

---

## ⏱️ Tiempo Total

- Paso 1: 2 minutos
- Paso 2: 1 minuto
- Paso 3: 2 minutos
- Paso 4: 2 minutos
- **Total: ~7 minutos**

---

## 🎉 ¡Listo!

Después de estos 4 pasos, tu app tendrá autenticación con Google completamente funcional.

**Próximo paso**: Compilar y probar.

```bash
./gradlew clean build && ./gradlew installDebug
```
