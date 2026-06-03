# ⚙️ Configuración Inicial

## Requisitos Previos

- Android Studio 2022.1.1 o superior
- JDK 11 o superior
- Emulador o dispositivo Android 8.0 (API 26) o superior
- Cuenta de Google

## 🚀 Pasos de Configuración

### Paso 1: Clonar el Repositorio

```bash
git clone <repository_url>
cd ChickenFood
```

### Paso 2: Configurar Firebase

#### 2.1 Crear Proyecto en Firebase Console

1. Ve a https://console.firebase.google.com/
2. Haz clic en "Agregar proyecto"
3. Nombre del proyecto: `chickenfood-b5459`
4. Habilita Google Analytics (opcional)
5. Haz clic en "Crear proyecto"

#### 2.2 Agregar Aplicación Android

1. En Firebase Console, haz clic en "Agregar aplicación"
2. Selecciona "Android"
3. Nombre del paquete: `com.daniel.chickenfood`
4. Nombre de la app (opcional): `ChickenFood`
5. SHA-1 (obtén en paso 2.3)

#### 2.3 Obtener SHA-1

Abre Terminal en Android Studio y ejecuta:

```bash
./gradlew signingReport
```

Busca en la salida:

```
SHA1: AB:CD:EF:12:34:56:78:90:AB:CD:EF:12:34:56:78:90:AB:CD:EF:12
```

**Copia sin los dos puntos:** `ABCDEF1234567890ABCDEF1234567890ABCDEF12`

#### 2.4 Descargar google-services.json

1. En Firebase Console, después de registrar la app, haz clic en "Descargar google-services.json"
2. Coloca el archivo en: `/app/google-services.json`

### Paso 3: Configurar Google Sign-In

#### 3.1 Crear Web Client ID

1. Ve a https://console.cloud.google.com/
2. Selecciona tu proyecto: `chickenfood-b5459`
3. Ve a **APIs y servicios** → **Credenciales**
4. Haz clic en **+ Crear credenciales** → **ID de cliente OAuth**
5. Selecciona **Aplicación web**
6. Haz clic en **Crear**
7. Copia el **Client ID** (termina en `.apps.googleusercontent.com`)

#### 3.2 Actualizar strings.xml

Abre `/app/src/main/res/values/strings.xml` y reemplaza:

```xml
<!-- Antes -->
<string name="default_web_client_id">YOUR_WEB_CLIENT_ID_HERE</string>

<!-- Después -->
<string name="default_web_client_id">YOUR_WEB_CLIENT_ID.apps.googleusercontent.com</string>
```

Donde `YOUR_WEB_CLIENT_ID` es el que obtuviste en paso 3.1.

### Paso 4: Actualizar google-services.json en Firebase Console

1. En Firebase Console, ve a **Configuración del proyecto** → **Aplicaciones**
2. Selecciona tu app
3. Haz clic en **Descargar google-services.json**
4. Reemplaza el archivo en `/app/google-services.json`

### Paso 5: Habilitar Realtime Database

1. En Firebase Console, ve a **Realtime Database**
2. Haz clic en **Crear base de datos**
3. Selecciona región: `us-central1` (o cercana a ti)
4. Modo de seguridad: **Comenzar en modo de prueba**
5. Haz clic en **Crear**

#### 5.1 Configurar reglas de seguridad (Opcional)

Para producción, actualiza las reglas en la pestaña **Reglas**:

```json
{
  "rules": {
    "users": {
      "$uid": {
        ".read": "$uid === auth.uid",
        ".write": "$uid === auth.uid",
        "tokens": {
          ".validate": "newData.hasChildren(['userId', 'email', 'googleIdToken', 'firebaseToken', 'createdAt', 'lastLoginAt', 'isActive'])"
        },
        "rewards": {
          ".validate": "newData.hasChildren(['totalPoints', 'pointsBalance', 'pointsSpent'])"
        }
      }
    },
    "orders": {
      "$orderId": {
        ".read": "root.child('users').child(auth.uid).child('orders').child($orderId).exists()",
        ".write": "root.child('users').child(auth.uid).child('orders').child($orderId).exists() || !data.exists()",
        ".validate": "newData.hasChildren(['userId', 'items', 'totalPrice', 'status'])"
      }
    },
    "pointsTransactions": {
      "$txnId": {
        ".read": "root.child('users').child(auth.uid).child('transactions').child($txnId).exists()",
        ".write": "newData.child('userId').val() === auth.uid"
      }
    }
  }
}
```

### Paso 6: Habilitar Autenticación Google

1. En Firebase Console, ve a **Autenticación**
2. Ve a la pestaña **Métodos de inicio de sesión**
3. Haz clic en **Google**
4. Cambia el estado a **Habilitado**
5. Haz clic en **Guardar**

### Paso 7: Sincronizar Gradle

1. En Android Studio, abre `File` → `Sync Now`
2. Espera a que termine la sincronización
3. Verifica que no haya errores

### Paso 8: Limpiar y Construir

```bash
./gradlew clean
./gradlew build
```

### Paso 9: Ejecutar en Emulador o Dispositivo

#### Opción 1: Emulador

```bash
./gradlew installDebug
```

Luego haz clic en ▶ Play en Android Studio.

#### Opción 2: Dispositivo Físico

1. Conecta el dispositivo por USB
2. Habilita "Depuración por USB" en Configuración → Opciones de Desarrollador
3. Ejecuta: `./gradlew installDebug`

## ✅ Verificación

Después de la configuración, verifica:

- [ ] La app abre sin errores
- [ ] Se ve SplashActivity con botones "Empecemos" e "Inscribete"
- [ ] Hacer clic en "Empecemos" va a MainActivity
- [ ] Hacer clic en "Inscribete" abre selector de cuentas Google (si hay error ApiException: 10, ver [Solución de Errores](./06_SOLUCION_ERRORES.md))
- [ ] Puedes ver productos en el Dashboard
- [ ] Puedes agregar productos al carrito
- [ ] Puedes ver el carrito

## 🔧 Configuración del Emulador

### Agregar Cuenta Google al Emulador

```bash
# Abre el emulador
emulator -avd <nombre_del_emulador>

# En otra terminal, abre adb shell
adb shell

# Una vez dentro, ejecuta
settings put global add_users_when_locked 0
```

Luego:

1. Abre Configuración en el emulador
2. Ve a Cuentas
3. Haz clic en "Agregar cuenta"
4. Selecciona Google
5. Sigue las instrucciones

## 📝 Variables de Entorno (Opcional)

Puedes crear un archivo `.env` en la raíz del proyecto (no se versionará):

```
FIREBASE_PROJECT_ID=chickenfood-b5459
FIREBASE_DATABASE_URL=https://chickenfood-b5459-default-rtdb.firebaseio.com
GOOGLE_WEB_CLIENT_ID=YOUR_WEB_CLIENT_ID.apps.googleusercontent.com
```

## 🐛 Troubleshooting

### Error: "Configuration failed: Could not resolve"

**Solución:**
```bash
./gradlew clean
./gradlew build --refresh-dependencies
```

### Error: "google-services.json not found"

**Solución:**
1. Verifica que el archivo esté en `/app/google-services.json`
2. Si no existe, descárgalo de Firebase Console

### Error: "Unable to instantiate the default WebSocketFactory"

**Solución:** Este error aparece a veces pero es normal. Intenta:
```bash
./gradlew build --no-daemon
```

### ApiException: 10 en Google Sign-In

**Solución:** Ver [Solución de Errores](./06_SOLUCION_ERRORES.md)

## 📊 Estructura del Proyecto

```
ChickenFood/
├── app/
│   ├── build.gradle.kts
│   ├── google-services.json ← Archivo importante
│   ├── proguard-rules.pro
│   └── src/
│       └── main/
│           ├── AndroidManifest.xml
│           ├── java/
│           │   └── com/daniel/chickenfood/
│           │       ├── di/
│           │       ├── domain/
│           │       ├── data/
│           │       ├── presentation/
│           │       └── helper/
│           └── res/
│               ├── values/
│               │   └── strings.xml ← Web Client ID aquí
│               ├── drawable/
│               └── layout/
├── build.gradle.kts
├── settings.gradle.kts
└── documentation/
```

## 🎯 Próximos Pasos

Después de configurar:

1. ✅ Lee [Flujo de Autenticación](./01_AUTENTICACION.md)
2. ✅ Lee [Guía del Usuario No Premium](./02_USUARIO_NO_PREMIUM.md)
3. ✅ Lee [Guía del Usuario Premium](./03_USUARIO_PREMIUM.md)
4. ✅ Lee [Sistema de Recompensas](./04_SISTEMA_RECOMPENSAS.md)

## 💡 Consejos

- **Para desarrollo:** Usa el emulador con velocidad "Very Fast"
- **Para debugging:** Usa Android Studio Debugger (tecla `Shift + F9`)
- **Para logs:** Usa Logcat en Android Studio
- **Para Firebase:** Monitorea cambios en tiempo real en Firebase Console

## 📞 Soporte

Si encuentras problemas:

1. Consulta [Solución de Errores](./06_SOLUCION_ERRORES.md)
2. Verifica que todos los pasos estén completados
3. Revisa los logs en Logcat (Android Studio)
4. Verifica que Firebase esté configurado correctamente

---

**Estado:** Completado
**Última actualización:** 2026-06-01

