# 🐛 Solución de Errores

## Errores Comunes

### 1. ApiException: 10

**Descripción:** Error al intentar autenticarse con Google Sign-In.

**Causa:** El Web Client ID no está configurado correctamente en `strings.xml`.

**Síntomas:**
```
E/SignUpActivity: Google Sign-Up falló: 10
```

#### Solución

**Paso 1:** Obtener SHA-1

```bash
./gradlew signingReport
```

Busca en la salida:
```
SHA1: AB:CD:EF:12:34:56:78:90:AB:CD:EF:12:34:56:78:90:AB:CD:EF:12
```

Copia sin los dos puntos: `ABCDEF1234567890ABCDEF1234567890ABCDEF12`

**Paso 2:** Agregar SHA-1 a Firebase Console

1. Ve a https://console.firebase.google.com/
2. Selecciona proyecto: `chickenfood-b5459`
3. Ve a **Configuración del proyecto** → **Aplicaciones**
4. Selecciona tu app Android: `com.daniel.chickenfood`
5. En **Huella digital SHA-1**, haz clic en **Agregar huella digital**
6. Pega el SHA-1
7. Haz clic en **Guardar**

**Paso 3:** Crear Web Client ID en Google Cloud Console

1. Ve a https://console.cloud.google.com/
2. Selecciona proyecto: `chickenfood-b5459`
3. Ve a **APIs y servicios** → **Credenciales**
4. Haz clic en **+ Crear credenciales** → **ID de cliente OAuth**
5. Selecciona **Aplicación web**
6. Haz clic en **Crear**
7. Copia el **Client ID** (termina en `.apps.googleusercontent.com`)

**Paso 4:** Actualizar strings.xml

Abre `/app/src/main/res/values/strings.xml`:

```xml
<!-- Antes -->
<string name="default_web_client_id">YOUR_WEB_CLIENT_ID_HERE</string>

<!-- Después -->
<string name="default_web_client_id">1234567890-abcdefghijklmnopqrstuvwxyz.apps.googleusercontent.com</string>
```

**Paso 5:** Descargar google-services.json actualizado

1. En Firebase Console: **Configuración del proyecto** → **Aplicaciones**
2. Selecciona tu app
3. Haz clic en **Descargar google-services.json**
4. Reemplaza en `/app/google-services.json`

**Paso 6:** Reconstruir

```bash
./gradlew clean
./gradlew build
```

**Paso 7:** Probar

- Corre la app
- Haz clic en "Inscribete"
- Deberías ver el selector de cuentas Google

---

### 2. "Cuenta de Google no disponible"

**Descripción:** No hay cuentas Google autenticadas en el dispositivo/emulador.

**Causa:** El dispositivo no tiene configurada una cuenta Google.

#### Solución

**Para Emulador:**

1. Abre Configuración del emulador
2. Ve a Cuentas
3. Haz clic en "Agregar cuenta"
4. Selecciona Google
5. Sigue las instrucciones para agregar tu cuenta

**Para Dispositivo Físico:**

1. Abre Configuración
2. Ve a Cuentas
3. Haz clic en "Agregar cuenta"
4. Selecciona Google
5. Sigue las instrucciones

---

### 3. "Configuration failed: Could not resolve"

**Descripción:** Error de compilación relacionado con dependencias.

**Síntomas:**
```
Could not resolve com.google.firebase:...
```

#### Solución

```bash
./gradlew clean
./gradlew build --refresh-dependencies
```

Si sigue fallando:

1. Verifica conexión a internet
2. Revisa que el archivo `build.gradle.kts` tenga los repositorios:
   ```kotlin
   repositories {
       google()
       mavenCentral()
   }
   ```

---

### 4. "google-services.json not found"

**Descripción:** El compilador no encuentra el archivo `google-services.json`.

**Síntomas:**
```
WARNING: Please initialize the Google Play services library...
```

#### Solución

1. Verifica que el archivo exista en: `/app/google-services.json`
2. Si no existe, descárgalo:
   - Firebase Console → Configuración del proyecto → Aplicaciones → Descargar google-services.json
3. Verifica que el archivo no esté vacío
4. Reconstruye: `./gradlew clean build`

---

### 5. "Unable to instantiate the default WebSocketFactory"

**Descripción:** Error de Firebase Realtime Database al abrir WebSocket.

**Síntomas:** Aparece en logs pero la app sigue funcionando.

#### Solución

Este error es normal en algunos casos. Intenta:

```bash
./gradlew build --no-daemon
```

Si persiste:
1. Verifica que Firebase Realtime Database esté habilitada en Firebase Console
2. Verifica las reglas de seguridad
3. Intenta desinstalar la app e instalar de nuevo

---

### 6. "Permission denied" en Realtime Database

**Descripción:** Firebase rechaza la operación por falta de permisos.

**Síntomas:**
```
E/Firebase: Permission denied at /users/...
```

#### Solución

1. Ve a Firebase Console → Realtime Database → Pestaña **Reglas**
2. Reemplaza con reglas permisivas para desarrollo:
   ```json
   {
     "rules": {
       ".read": true,
       ".write": true
     }
   }
   ```
3. Haz clic en **Publicar**

**⚠️ Advertencia:** Estas reglas son solo para desarrollo. Para producción, implementa reglas seguras.

---

### 7. "NullPointerException en MainActivity"

**Descripción:** La app crashea al abrir MainActivity.

**Síntomas:**
```
Fatal Exception: java.lang.NullPointerException
```

#### Solución

1. Verifica en los logs cuál línea causa el error
2. Generalmente es porque no se inicializó Koin correctamente
3. Verifica que `ChickenFoodApp` extienda `Application`:
   ```kotlin
   class ChickenFoodApp : Application() {
       override fun onCreate() {
           super.onCreate()
           startKoin {
               androidContext(this@ChickenFoodApp)
               modules(appModule)
           }
       }
   }
   ```
4. Verifica en `AndroidManifest.xml` que la app esté registrada:
   ```xml
   <application
       android:name=".ChickenFoodApp"
       ...
   >
   ```

---

### 8. "Carrito muestra cantidad incorrecta"

**Descripción:** Al agregar un producto, la cantidad no es la esperada.

**Causa:** `ManagmentCart` estaba sumando cantidades en lugar de reemplazar.

#### Solución

Verifica que `ManagmentCart.addToCart()` REEMPLACE y no SUME:

```kotlin
// ✅ Correcto (Reemplazar)
fun addToCart(product: FoodModel, quantity: Int) {
    val existingItem = cart.find { it.product.id == product.id }
    if (existingItem != null) {
        existingItem.quantity = quantity  // Reemplazar
    } else {
        cart.add(CartItem(product, quantity))
    }
}

// ❌ Incorrecto (Sumar)
fun addToCart(product: FoodModel, quantity: Int) {
    val existingItem = cart.find { it.product.id == product.id }
    if (existingItem != null) {
        existingItem.quantity += quantity  // ❌ Esto suma
    } else {
        cart.add(CartItem(product, quantity))
    }
}
```

---

### 9. "TopBar no muestra logout"

**Descripción:** El botón de logout no aparece en TopBar.

#### Solución

Verifica que `TopBar.kt` tenga:

```kotlin
@Composable
fun TopBar(
    onLogoutClick: () -> Unit
) {
    // ... código ...
    
    IconButton(onClick = onLogoutClick) {
        Icon(
            painter = painterResource(R.drawable.ic_logout),
            contentDescription = "Logout",
            tint = Color.White
        )
    }
}
```

Y que `MainActivity.kt` pase el callback:

```kotlin
TopBar(
    onLogoutClick = { handleLogout() }
)
```

---

### 10. "El Splash Screen no navega"

**Descripción:** El usuario hace clic en un botón pero no navega.

#### Solución

Verifica que `SplashActivity.kt` tenga los botones configurados:

```kotlin
@Composable
fun GetStartedButtons(
    onEmpecemosClick: () -> Unit,
    onInscribeteClick: () -> Unit
) {
    Button(
        onClick = onEmpecemosClick,
        // ...
    ) {
        Text("Empecemos")
    }
    
    Button(
        onClick = onInscribeteClick,
        // ...
    ) {
        Text("Inscribete")
    }
}
```

Y que `SplashActivity` tenga:

```kotlin
fun handleEmpecemosClick() {
    startActivity(Intent(this, MainActivity::class.java))
    finish()
}

fun handleInscribeteClick() {
    startActivity(Intent(this, SignUpActivity::class.java))
}
```

---

## 🔍 Debugging

### Ver Logs

**Android Studio:**
1. Abre **Logcat** (abajo de la pantalla)
2. Filtra por tag: `MainActivity`, `SignUpActivity`, etc.
3. Selecciona nivel: **Error** (E)

**Terminal:**
```bash
adb logcat | grep -i "error\|exception"
```

### Breakpoints

**Android Studio:**
1. Haz clic al lado del número de línea
2. Ejecuta en modo Debug (Shift + F9)
3. La ejecución se pausará en el breakpoint
4. Inspecciona variables en la ventana Debugger

### Inspeccionar Firebase

**Firebase Console:**
1. Ve a **Realtime Database**
2. Expande `/users` para ver datos guardados
3. Expande `/orders` para ver órdenes
4. Expande `/pointsTransactions` para ver transacciones

---

## 📝 Reporte de Errores

Si encuentras un error no listado:

1. Toma nota del error exacto (log)
2. Anota los pasos para reproducir
3. Verifica que la configuración sea correcta (ver paso 5)
4. Intenta `./gradlew clean build`
5. Si persiste, reporta con:
   - Mensaje de error exacto
   - Pasos para reproducir
   - Versión de Android
   - Versión de Android Studio

---

## 🆘 Emergencia: Resetear Todo

Si nada funciona:

```bash
./gradlew clean
rm -rf ~/.gradle
rm -rf .gradle
./gradlew build
```

Luego:
1. Desinstala la app del dispositivo
2. Cierra Android Studio
3. Reabre Android Studio
4. Abre el proyecto
5. `./gradlew build`

---

**Última actualización:** 2026-06-01

