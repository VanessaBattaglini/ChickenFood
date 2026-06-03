# Solución: Error ApiException: 10 en Google Sign-In

## ¿Qué significa el error?

El error `ApiException: 10` significa que la configuración de OAuth no coincide. Específicamente:
- El Web Client ID en tu app no está registrado en Firebase Console
- O el Web Client ID es incorrecto/placeholder

## Causa Raíz

En tu `strings.xml` tienes:
```xml
<string name="default_web_client_id">YOUR_WEB_CLIENT_ID_HERE</string>
```

Este es un placeholder. Necesitas reemplazarlo con el Web Client ID real de Firebase Console.

## Solución Paso a Paso

### Paso 1: Obtener el SHA-1 de tu app

Abre Terminal en Android Studio y ejecuta:

```bash
cd /Users/danielalvarado/AndroidStudioProjects/ChickenFood
./gradlew signingReport
```

Busca en la salida algo como:
```
SHA1: AB:CD:EF:12:34:56:78:90:AB:CD:EF:12:34:56:78:90:AB:CD:EF:12
```

Copia el SHA-1 (sin los dos puntos).

### Paso 2: Agregar SHA-1 a Firebase Console

1. Ve a [Firebase Console](https://console.firebase.google.com/)
2. Selecciona tu proyecto: **chickenfood-b5459**
3. Ve a **Configuración del proyecto** (engranaje arriba a la izquierda)
4. Ve a la pestaña **Aplicaciones**
5. Selecciona tu app Android: **com.daniel.chickenfood**
6. En la sección **Huella digital SHA-1**, haz clic en **Agregar huella digital**
7. Pega el SHA-1 que obtuviste
8. Haz clic en **Guardar**

### Paso 3: Crear Web Client ID

1. En Firebase Console, ve a **Configuración del proyecto**
2. Ve a la pestaña **Cuentas de servicio**
3. Haz clic en **Generar nueva clave privada**
4. Se descargará un JSON (guárdalo en lugar seguro)

**O mejor aún:**

1. Ve a [Google Cloud Console](https://console.cloud.google.com/)
2. Selecciona tu proyecto: **chickenfood-b5459**
3. Ve a **APIs y servicios** → **Credenciales**
4. Haz clic en **+ Crear credenciales** → **ID de cliente OAuth**
5. Selecciona **Aplicación web**
6. En **Orígenes autorizados de JavaScript**, agrega:
   - `http://localhost:8080`
   - `http://localhost:3000`
7. En **URI de redirección autorizados**, agrega:
   - `http://localhost:8080/callback`
   - `http://localhost:3000/callback`
8. Haz clic en **Crear**
9. Se abrirá un modal con tu **Client ID**. Cópialo.

### Paso 4: Actualizar strings.xml

Abre `/Users/danielalvarado/AndroidStudioProjects/ChickenFood/app/src/main/res/values/strings.xml`

Reemplaza:
```xml
<string name="default_web_client_id">YOUR_WEB_CLIENT_ID_HERE</string>
```

Con:
```xml
<string name="default_web_client_id">YOUR_WEB_CLIENT_ID.apps.googleusercontent.com</string>
```

Donde `YOUR_WEB_CLIENT_ID` es el que obtuviste en el Paso 3.

### Paso 5: Descargar google-services.json actualizado

1. En Firebase Console, ve a **Configuración del proyecto**
2. Ve a la pestaña **Aplicaciones**
3. Selecciona tu app Android
4. Haz clic en **Descargar google-services.json**
5. Reemplaza el archivo en `/Users/danielalvarado/AndroidStudioProjects/ChickenFood/app/google-services.json`

### Paso 6: Limpiar y reconstruir

En Terminal:
```bash
cd /Users/danielalvarado/AndroidStudioProjects/ChickenFood
./gradlew clean
./gradlew build
```

### Paso 7: Probar

1. Ejecuta la app en el emulador o dispositivo
2. Haz clic en "Inscribete"
3. Haz clic en "Continuar con Google"
4. Selecciona tu cuenta Google
5. Deberías ver el Toast "Firebase Auth exitoso"

## Verificación

Si todo está correcto, deberías ver en los logs:
```
D/SignUpActivity: Google Sign-Up exitoso: tu.email@gmail.com
D/SignUpActivity: Autenticando con Firebase usando Google ID Token
D/SignUpActivity: Firebase Auth exitoso: tu.email@gmail.com
D/SignUpActivity: Firebase token obtenido: eyJhbGc...
D/SignUpActivity: Navegando al Dashboard
```

## Troubleshooting

### Si aún ves ApiException: 10

1. Verifica que el SHA-1 esté agregado en Firebase Console
2. Verifica que el Web Client ID sea correcto en strings.xml
3. Verifica que el Web Client ID esté en formato: `XXXXX.apps.googleusercontent.com`
4. Limpia el caché: `./gradlew clean`
5. Desinstala la app del dispositivo
6. Reconstruye y ejecuta nuevamente

### Si ves "Cuenta de Google no disponible"

Significa que no hay cuentas Google configuradas en el dispositivo/emulador.

**Para emulador:**
1. Abre Configuración del emulador
2. Ve a Cuentas
3. Agrega una cuenta Google

**Para dispositivo físico:**
1. Abre Configuración
2. Ve a Cuentas
3. Agrega una cuenta Google

## Resumen

| Paso | Acción |
|------|--------|
| 1 | Obtener SHA-1 con `./gradlew signingReport` |
| 2 | Agregar SHA-1 a Firebase Console |
| 3 | Crear Web Client ID en Google Cloud Console |
| 4 | Actualizar `strings.xml` con Web Client ID |
| 5 | Descargar `google-services.json` actualizado |
| 6 | Limpiar y reconstruir: `./gradlew clean build` |
| 7 | Probar en emulador/dispositivo |

