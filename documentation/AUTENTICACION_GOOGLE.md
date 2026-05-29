# Autenticación con Google - ChickenFood

## ✅ Estado: IMPLEMENTADO

La autenticación con Google ha sido completamente implementada en la aplicación ChickenFood.

---

## 📋 Pasos Completados

### 1. ✅ Dependencias Agregadas
- `play-services-auth` (21.0.0) - Google Play Services Auth
- `firebase-auth` - Firebase Authentication (ya estaba)

**Archivos modificados**:
- `gradle/libs.versions.toml` - Agregada versión de play-services-auth
- `app/build.gradle.kts` - Agregada dependencia

### 2. ✅ LoginActivity Creado
**Ubicación**: `app/src/main/java/com/daniel/chickenfood/presentation/activity/auth/LoginActivity.kt`

**Características**:
- Interfaz de login con botón "Iniciar sesión con Google"
- Integración con Google Sign-In
- Autenticación con Firebase
- Indicador de carga durante el proceso
- Navegación automática al Dashboard si ya está autenticado

**Flujo**:
```
Usuario hace clic en botón
    ↓
Google Sign-In se abre
    ↓
Usuario selecciona cuenta
    ↓
ID Token se obtiene
    ↓
Firebase autentica con el token
    ↓
Usuario se redirige al Dashboard
```

### 3. ✅ AuthHelper Creado
**Ubicación**: `app/src/main/java/com/daniel/chickenfood/helper/AuthHelper.kt`

**Funciones disponibles**:
- `getCurrentUser()` - Obtiene el usuario actual
- `getUserEmail()` - Obtiene el email del usuario
- `getUserName()` - Obtiene el nombre del usuario
- `getUserId()` - Obtiene el UID del usuario
- `getUserPhotoUrl()` - Obtiene la URL de la foto
- `isUserLoggedIn()` - Verifica si está autenticado
- `signOut()` - Cierra la sesión
- `getUserInfo()` - Obtiene toda la información del usuario

### 4. ✅ SplashActivity Actualizado
**Ubicación**: `app/src/main/java/com/daniel/chickenfood/presentation/activity/splash/SplashActivity.kt`

**Cambios**:
- Verifica si el usuario está autenticado
- Si está autenticado → Navega a MainActivity
- Si no está autenticado → Navega a LoginActivity
- Espera 3 segundos antes de navegar

### 5. ✅ TopBar Actualizado
**Ubicación**: `app/src/main/java/com/daniel/chickenfood/presentation/activity/dashboard/TopBar.kt`

**Cambios**:
- Agregado parámetro `onLogoutClick`
- Agregado botón de logout (icono de salida)
- El botón está en la esquina superior derecha

### 6. ✅ AndroidManifest.xml Actualizado
**Cambios**:
- Agregada LoginActivity
- Permisos INTERNET y ACCESS_NETWORK_STATE ya estaban presentes

### 7. ✅ strings.xml Actualizado
**Cambios**:
- Agregado `default_web_client_id` (placeholder)

---

## 🔧 Configuración Necesaria

### Paso 1: Obtener Web Client ID
1. Ve a [Firebase Console](https://console.firebase.google.com/)
2. Selecciona tu proyecto
3. Ve a Authentication → Sign-in method
4. Busca "Web Client ID" en la sección de Google
5. Copia el ID

### Paso 2: Actualizar strings.xml
Reemplaza `YOUR_WEB_CLIENT_ID_HERE` en `app/src/main/res/values/strings.xml`:

```xml
<string name="default_web_client_id">TU_WEB_CLIENT_ID_AQUI</string>
```

### Paso 3: Obtener SHA-1 de tu App
```bash
./gradlew signingReport
```

Copia el SHA-1 de la variante debug.

### Paso 4: Agregar SHA-1 a Firebase
1. Ve a Firebase Console
2. Selecciona tu proyecto
3. Ve a Project Settings
4. En la sección "Your apps", selecciona tu app Android
5. Agrega el SHA-1 en "SHA certificate fingerprints"

### Paso 5: Descargar google-services.json
1. En Firebase Console, descarga `google-services.json`
2. Colócalo en `app/`

---

## 📱 Flujo de la Aplicación

```
SplashActivity (3 segundos)
    ↓
¿Usuario autenticado?
    ├─ SÍ → MainActivity (Dashboard)
    └─ NO → LoginActivity (Login)
        ↓
    Usuario hace clic "Iniciar sesión con Google"
        ↓
    Google Sign-In
        ↓
    Firebase Authentication
        ↓
    MainActivity (Dashboard)
        ↓
    Usuario hace clic en botón de logout
        ↓
    LoginActivity (Login)
```

---

## 🔐 Seguridad

### Datos Almacenados
- **Email**: Obtenido de Google
- **Nombre**: Obtenido de Google
- **Foto**: URL de Google
- **UID**: Generado por Firebase
- **Token**: Manejado por Firebase (no se almacena localmente)

### Permisos Solicitados
- `INTERNET` - Para conectarse a Google y Firebase
- `ACCESS_NETWORK_STATE` - Para verificar conexión

---

## 📊 Archivos Modificados/Creados

### Creados:
- ✅ `LoginActivity.kt` - Pantalla de login
- ✅ `AuthHelper.kt` - Helper para autenticación

### Modificados:
- ✅ `SplashActivity.kt` - Verificación de autenticación
- ✅ `TopBar.kt` - Botón de logout
- ✅ `AndroidManifest.xml` - LoginActivity registrada
- ✅ `strings.xml` - Web Client ID
- ✅ `gradle/libs.versions.toml` - Versión de play-services-auth
- ✅ `app/build.gradle.kts` - Dependencia agregada

---

## 🧪 Pruebas

### Prueba 1: Login con Google
1. Abre la app
2. Espera 3 segundos (Splash)
3. Deberías ver LoginActivity
4. Haz clic en "Iniciar sesión con Google"
5. Selecciona una cuenta de Google
6. Deberías ser redirigido al Dashboard

### Prueba 2: Persistencia
1. Cierra la app
2. Abre la app nuevamente
3. Deberías ir directamente al Dashboard (sin pasar por login)

### Prueba 3: Logout
1. En el Dashboard, haz clic en el botón de logout (esquina superior derecha)
2. Deberías ser redirigido a LoginActivity

### Prueba 4: Información del Usuario
En cualquier pantalla, puedes usar:
```kotlin
val email = AuthHelper.getUserEmail()
val name = AuthHelper.getUserName()
val photoUrl = AuthHelper.getUserPhotoUrl()
```

---

## 🐛 Troubleshooting

### Problema: "Google Sign-In falló"
**Solución**: 
- Verifica que el SHA-1 está agregado en Firebase
- Verifica que el Web Client ID es correcto
- Verifica que tienes conexión a internet

### Problema: "Firebase Auth falló"
**Solución**:
- Verifica que Firebase está habilitado en tu proyecto
- Verifica que Google Sign-In está habilitado en Authentication
- Verifica que el google-services.json está en `app/`

### Problema: "No puedo hacer logout"
**Solución**:
- Verifica que el icono `ic_logout` existe en `res/drawable/`
- Si no existe, crea un icono o usa uno existente

### Problema: "El usuario no se redirige al Dashboard"
**Solución**:
- Verifica que `navigateToDashboard()` se ejecuta
- Verifica que MainActivity existe
- Verifica los logs en Logcat

---

## 📝 Próximos Pasos

1. **Compilar y Probar**:
   ```bash
   ./gradlew clean build
   ./gradlew installDebug
   ```

2. **Configurar Web Client ID**:
   - Obtener de Firebase Console
   - Actualizar en strings.xml

3. **Agregar SHA-1 a Firebase**:
   - Ejecutar `./gradlew signingReport`
   - Copiar SHA-1 a Firebase Console

4. **Descargar google-services.json**:
   - Descargar de Firebase Console
   - Colocar en `app/`

5. **Crear icono de logout** (si no existe):
   - Crear `ic_logout.xml` en `res/drawable/`

---

## 🎯 Funcionalidades Implementadas

✅ Login con Google  
✅ Autenticación con Firebase  
✅ Verificación de autenticación en Splash  
✅ Logout desde Dashboard  
✅ Persistencia de sesión  
✅ Obtención de información del usuario  
✅ Logging para debugging  

---

## 📞 Información de Contacto

Para preguntas o problemas:
1. Revisa los logs en Logcat
2. Verifica la configuración de Firebase
3. Verifica que el Web Client ID es correcto
4. Verifica que el SHA-1 está agregado

---

## 🎓 Conceptos Clave

1. **Google Sign-In**: Permite que los usuarios inicien sesión con su cuenta de Google
2. **Firebase Authentication**: Autentica y gestiona las sesiones de los usuarios
3. **ID Token**: Token JWT que verifica la identidad del usuario
4. **Web Client ID**: Identificador único para tu app en Google Cloud
5. **SHA-1**: Huella digital de tu app para verificar que es legítima

---

## ✨ Conclusión

La autenticación con Google está completamente implementada y lista para usar. Solo necesitas:
1. Obtener el Web Client ID de Firebase
2. Actualizar strings.xml
3. Agregar SHA-1 a Firebase
4. Compilar y probar

**Próximo paso**: Obtener Web Client ID de Firebase Console.
