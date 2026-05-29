# Pasos Finales para Configurar Google Sign-In

## 📋 Checklist de Configuración

Tu implementación de autenticación passwordless está **100% lista**. Solo necesitas completar estos pasos de configuración en Firebase:

---

## ✅ Paso 1: Obtener Web Client ID

### En Firebase Console:

1. Ve a [Firebase Console](https://console.firebase.google.com/)
2. Selecciona tu proyecto **ChickenFood**
3. Ve a **Project Settings** (⚙️ en la esquina superior izquierda)
4. Haz clic en la pestaña **General**
5. Desplázate hasta **Your apps**
6. Busca tu app Android
7. Copia el **Web Client ID** (formato: `XXXXXXX.apps.googleusercontent.com`)

### En tu proyecto:

1. Abre: `app/src/main/res/values/strings.xml`
2. Reemplaza:
```xml
<string name="default_web_client_id">YOUR_WEB_CLIENT_ID_HERE</string>
```
Con:
```xml
<string name="default_web_client_id">TU_WEB_CLIENT_ID_AQUI</string>
```

---

## ✅ Paso 2: Obtener SHA-1 del Certificado

### En Terminal:

```bash
cd /Users/danielalvarado/AndroidStudioProjects/ChickenFood
./gradlew signingReport
```

### Busca en la salida:

```
debugAndroidDebugKey:
  SHA1: XX:XX:XX:XX:XX:XX:XX:XX:XX:XX:XX:XX:XX:XX:XX:XX:XX:XX:XX:XX
```

Copia el valor SHA1 (sin los dos puntos).

---

## ✅ Paso 3: Agregar SHA-1 a Firebase

### En Firebase Console:

1. Ve a **Project Settings** → **General**
2. Desplázate hasta **Your apps**
3. Haz clic en tu app Android
4. Desplázate hasta **SHA certificate fingerprints**
5. Haz clic en **Add fingerprint**
6. Pega el SHA-1 que copiaste
7. Haz clic en **Save**

---

## ✅ Paso 4: Descargar google-services.json

### En Firebase Console:

1. Ve a **Project Settings** → **General**
2. Desplázate hasta **Your apps**
3. Haz clic en tu app Android
4. Haz clic en **Download google-services.json**
5. Guarda el archivo

### En tu proyecto:

1. Abre el archivo descargado
2. Cópialo a: `app/google-services.json`
3. Reemplaza el archivo existente

---

## ✅ Paso 5: Compilar y Probar

### Compilar:

```bash
cd /Users/danielalvarado/AndroidStudioProjects/ChickenFood
./gradlew clean build
```

### Instalar en dispositivo/emulador:

```bash
./gradlew installDebug
```

### Probar:

1. Abre la app
2. Haz clic en **"Inscribete"**
3. Haz clic en **"Continuar con Google"**
4. Selecciona tu cuenta Google
5. ✅ **NO debería pedir contraseña**
6. Deberías ver el Dashboard

---

## 🔍 Verificación de Configuración

### Archivo: `app/src/main/res/values/strings.xml`
```xml
<string name="default_web_client_id">TU_WEB_CLIENT_ID_AQUI</string>
```
✅ Debe tener tu Web Client ID real

### Archivo: `app/google-services.json`
```json
{
  "project_info": {
    "project_number": "XXXXXXX",
    "project_id": "chickenfood-xxxxx",
    ...
  }
}
```
✅ Debe ser el archivo descargado de Firebase

### Archivo: `app/build.gradle.kts`
```kotlin
implementation(libs.play.services.auth)
implementation(libs.firebase.auth)
```
✅ Dependencias ya están instaladas

---

## 🧪 Pruebas Recomendadas

### Test 1: Acceso sin autenticación
- [ ] Abre la app
- [ ] Haz clic en "Empecemos"
- [ ] Deberías ver el Dashboard
- [ ] ✅ Funciona sin autenticación

### Test 2: Google Sign-In
- [ ] Abre la app
- [ ] Haz clic en "Inscribete"
- [ ] Haz clic en "Continuar con Google"
- [ ] Selecciona tu cuenta Google
- [ ] ✅ NO pide contraseña
- [ ] ✅ Ves el Dashboard

### Test 3: Logout
- [ ] En el Dashboard, haz clic en el botón de logout (TopBar)
- [ ] ✅ Vuelves a SplashActivity
- [ ] ✅ Puedes hacer clic en "Inscribete" de nuevo

### Test 4: Verificar usuario autenticado
- [ ] Después de autenticarte con Google
- [ ] En el Dashboard, el usuario debería estar disponible
- [ ] Puedes acceder a: email, nombre, foto, UID

---

## 🐛 Solución de Problemas

### Error: "Invalid Web Client ID"
**Solución**: Verifica que el Web Client ID en `strings.xml` sea correcto

### Error: "SHA-1 not registered"
**Solución**: Agrega el SHA-1 a Firebase Console en Project Settings

### Error: "google-services.json not found"
**Solución**: Descarga el archivo de Firebase y colócalo en `app/google-services.json`

### Google Sign-In no abre
**Solución**: Verifica que:
- El Web Client ID sea correcto
- El SHA-1 esté registrado en Firebase
- El `google-services.json` esté en la carpeta correcta

### Pide contraseña al hacer clic en "Continuar con Google"
**Solución**: Esto es normal si:
- La cuenta Google NO está autenticada en el dispositivo
- Es la primera vez que usas esa cuenta en el dispositivo
- Google necesita verificar la identidad

---

## 📊 Resumen de Configuración

| Paso | Tarea | Estado |
|------|-------|--------|
| 1 | Obtener Web Client ID | ⏳ Pendiente |
| 2 | Actualizar strings.xml | ⏳ Pendiente |
| 3 | Obtener SHA-1 | ⏳ Pendiente |
| 4 | Agregar SHA-1 a Firebase | ⏳ Pendiente |
| 5 | Descargar google-services.json | ⏳ Pendiente |
| 6 | Copiar google-services.json | ⏳ Pendiente |
| 7 | Compilar y probar | ⏳ Pendiente |

---

## 🎯 Después de Completar la Configuración

Una vez que completes todos los pasos:

1. ✅ La autenticación passwordless estará **completamente funcional**
2. ✅ Los usuarios podrán hacer clic en "Inscribete" y autenticarse con Google
3. ✅ NO se pedirá contraseña (si la cuenta está autenticada en el dispositivo)
4. ✅ El usuario será autenticado en Firebase
5. ✅ Podrás acceder a la información del usuario con `AuthHelper`
6. ✅ El logout funcionará correctamente

---

## 📝 Notas Importantes

- La autenticación es **completamente passwordless**
- Google maneja toda la seguridad
- El usuario solo selecciona su cuenta
- No hay entrada de contraseña en tu app
- Firebase verifica el token de Google
- La sesión se mantiene en el dispositivo

---

## 🔗 Recursos Útiles

- [Firebase Console](https://console.firebase.google.com/)
- [Google Sign-In Documentation](https://developers.google.com/identity/sign-in/android)
- [Firebase Authentication](https://firebase.google.com/docs/auth)

---

**Última actualización**: 29 de Mayo de 2026
**Tiempo estimado de configuración**: 10-15 minutos
