# ⚠️ Solución de Errores Comunes

**Esta guía te ayuda a resolver los problemas más comunes en ChickenFood.**

---

## 1. Error: ApiException 10 (Google Sign-In)

### Síntoma
```
E/SignUpActivity: Google Sign-Up failed: 10
com.google.android.gms.common.api.ApiException: 10
```

### Causa
Web Client ID no está configurado en Firebase Console.

### Solución PASO A PASO

#### Paso 1: Obtén el SHA-1 de tu app
```bash
# macOS/Linux
./gradlew signingReport

# Windows
gradlew.bat signingReport
```

**Busca**: `SHA1: XX:XX:XX:...`

#### Paso 2: Agrega SHA-1 a Firebase
1. Ve a [Firebase Console](https://console.firebase.google.com)
2. Selecciona tu proyecto
3. Settings (⚙️) → Project Settings
4. Pestaña "Your apps"
5. Selecciona tu app Android
6. Add fingerprint
7. Pega el SHA-1
8. Save

#### Paso 3: Crea Web Client ID
1. Ve a [Google Cloud Console](https://console.cloud.google.com)
2. Selecciona el mismo proyecto
3. APIs & Services → Credentials
4. Create Credentials → OAuth 2.0 Client IDs
5. Application type: Web application
6. Name: "ChickenFood Web"
7. Authorized redirect URIs:
   - Déjalo en blanco por ahora
8. Create
9. Copia el Client ID

#### Paso 4: Agrega a strings.xml
```xml
<!-- res/values/strings.xml -->
<resources>
    ...
    <string name="web_client_id">YOUR_WEB_CLIENT_ID_HERE</string>
</resources>
```

#### Paso 5: Descarga google-services.json
1. Firebase Console → Project Settings
2. "Your apps" → Selecciona app
3. google-services.json
4. Download
5. Copia a `app/google-services.json`
6. Build → Clean Build Folder
7. Rebuild

#### Paso 6: Verifica build.gradle
```gradle
// project/build.gradle
plugins {
    id 'com.google.gms.google-services' version '4.3.15'
}

// app/build.gradle
dependencies {
    implementation 'com.google.android.gms:play-services-auth:21.0.0'
    implementation 'com.google.firebase:firebase-auth-ktx:22.1.1'
}

apply plugin: 'com.google.gms.google-services'
```

#### Prueba
1. Recompila: `./gradlew clean build`
2. Abre app
3. Toca "Inscribete"
4. ✅ Deberías ver Google Account Selector

---

## 2. Productos No Aparecen en Dashboard

### Síntoma
Dashboard carga pero sin banners ni categorías.

### Causa
- Firebase no está conectado
- Datos no existen en Firebase
- Conexión de red lenta

### Solución

#### Paso 1: Verifica conexión a Internet
```
En emulador:
Settings → WiFi → Conecta a red

En dispositivo:
Activa WiFi o datos móviles
```

#### Paso 2: Verifica Firebase Realtime Database

1. [Firebase Console](https://console.firebase.google.com)
2. Selecciona tu proyecto
3. Realtime Database
4. ¿Existe data en `/banners` y `/categories`?

Si no existe:
```
Crea manualmente:
/banners/
  banner1/
    image: "https://..."
    title: "Promo 50%"
    
/categories/
  1/
    name: "Aves"
    image: "https://..."
  
/categories/1/foods/
  food1/
    id: "1"
    title: "Pollo Asado"
    price: 8.99
    description: "..."
    image: "https://..."
```

#### Paso 3: Verifica Security Rules
```
Firebase Console → Realtime Database → Rules

Debería permitir lectura:
{
  "rules": {
    ".read": true,
    ".write": false
  }
}
```

#### Paso 4: Verifica Logs
```
D/MainViewModel: Loading banners... (timeout: 10000ms)

Si ves:
- "⚠️ Banners load timed out" → Firebase lento/sin conexión
- "❌ Error loading banners" → Problema con datos
- "✅ Banners loaded successfully" → OK
```

#### Paso 5: Recarga
1. Cierra app completamente
2. Abre de nuevo
3. Espera a que cargue

---

## 3. Carrito No Se Limpia Después de Pagar

### Síntoma
Después de "Proceder al Pago", el carrito sigue con productos.

### Solución

#### Verificar Código
En `CartActivity.kt`:
```kotlin
private fun proceedToPayment() {
    // ...
    managmentCart.clearCart()  // Debe estar
    // ...
}
```

#### Solución Rápida
1. Reinicia la app
2. El carrito se debería limpiar

#### Debug
```
D/CartActivity: proceedToPayment() called
D/ManagmentCart: Clearing cart
D/CartActivity: Cart cleared, navigating to home
```

---

## 4. Google Sign-In Selector No Aparece

### Síntoma
Toca "Inscribete" pero no aparece el selector de cuentas.

### Solución

#### Opción 1: Verifica que haya cuentas Google
```
Emulador:
Settings → Accounts → Agrega cuenta Google

Dispositivo:
Abre Play Store (te pide cuenta Google automáticamente)
```

#### Opción 2: Verifica Web Client ID
```
En SignUpActivity.kt:
val webClientId = context.getString(R.string.web_client_id)

Abre strings.xml y verifica que:
<string name="web_client_id">DEBE_TENER_VALOR</string>
```

#### Opción 3: Limpia caché
```bash
./gradlew clean build
```

---

## 5. Puntos No Se Actualizan Después de Compra

### Síntoma
Usuario compra pero PointsCard sigue mostrando 0 puntos.

### Solución

#### Paso 1: Verifica Autenticación
```
En MainActivity:
val currentUser = AuthHelper.getCurrentUser()

Si es null:
  → Usuario no autenticado
  → No gana puntos
```

#### Paso 2: Verifica Firebase Structure
```
/users/{userId}/rewards/
  ├─ totalPoints: debe existir
  ├─ pointsBalance: debe existir
  └─ userLevel: debe existir
```

#### Paso 3: Verifica que se llamó addPointsFromPurchase
```
En CartActivity.proceedToPayment():

rewardsViewModel.addPointsFromPurchase(
    currentUser.uid,
    total,
    orderId
)  // Debe estar llamado
```

#### Paso 4: Logs
```
D/RewardsViewModel: Adding points from purchase: $100 USD
D/RewardsViewModel: Points added successfully

Si ves error:
E/RewardsViewModel: Error adding points: ...
```

---

## 6. Búsqueda No Encuentra Productos

### Síntoma
Escribe "pollo" pero no aparecen resultados.

### Síntoma
Causa: Productos no cargados en Firebase.

### Solución

#### Paso 1: Verifica que existan datos
```
Firebase Console → Realtime Database
→ /categories/1/foods/
```

Si no hay datos, crealos manualmente.

#### Paso 2: Verifica Case
```
Búsqueda: "pollo"
Resultado: Debe encontrar "Pollo Asado" (case-insensitive)

Si no:
→ Verifica que el título en Firebase sea correcto
```

#### Paso 3: Verifica Descripción
```
Si tienes: título="Pizza" descripción="con pollo"
Búsqueda: "pollo"
Resultado: Debe encontrarlo (busca en descripción también)
```

---

## 7. App Crashea al Abrir DetailScreen

### Síntoma
```
java.lang.NullPointerException: Cannot access 'field' on null object
```

### Causa
FoodModel no se pasó correctamente en Intent.

### Solución

#### Verificar MainActivity.navigateToDetail()
```kotlin
private fun navigateToDetail(food: FoodModel) {
    val intent = Intent(this, DetailEachFoodActivity::class.java).apply {
        putExtra("food", food)  // Debe estar
    }
    startActivity(intent)
}
```

#### Verificar DetailEachFoodActivity
```kotlin
override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    
    val food = intent.getParcelableExtra<FoodModel>("food")  // Obtener
    
    if (food == null) {
        Log.e(TAG, "Food is null")
        finish()
        return
    }
    
    // Usar food...
}
```

---

## 8. Logout No Funciona

### Síntoma
Toca logout pero no vuelve a SplashScreen.

### Solución

#### Verificar TopBar
```kotlin
// En TopBar.kt
Button(onClick = {
    onLogoutClick()  // Callback debe estar
})
```

#### Verificar MainActivity
```kotlin
private fun logout() {
    AuthHelper.signOut()  // Debe estar
    val intent = Intent(this, SplashActivity::class.java)
    startActivity(intent)
    finish()  // Debe estar
}
```

---

## 9. PointsCard No Aparece en Dashboard

### Síntoma
Dashboard se carga pero sin tarjeta de puntos.

### Causa
Usuario no autenticado O no se cargó RewardsViewModel.

### Solución

#### Verificar Autenticación
```kotlin
val currentUser = AuthHelper.getCurrentUser()

if (currentUser != null) {
    item {
        PointsCard(...)  // Solo aparece si autenticado
    }
}
```

#### Verificar RewardsViewModel
```kotlin
val rewardsViewModel: RewardsViewModel = koinViewModel()  // Debe estar

if (currentUser != null && userRewards == null) {
    rewardsViewModel.loadUserRewards(currentUser.uid)  // Carga inicial
}
```

---

## 10. Buscador Muy Lento

### Síntoma
Búsqueda tarda 5+ segundos en mostrar resultados.

### Causa
Cargando muchas categorías o mala conexión.

### Solución

#### Verificar Conexión
```
Conexión lenta:
→ Puede tardar más tiempo
→ Usar WiFi en lugar de datos
```

#### Verifica Logs
```
D/MainViewModel: Searching across 10 categories
D/MainViewModel:   Loaded 50 foods from category...

Si ves muchas categorías:
→ Puede ser lento
→ Normal si tiene muchas categorías
```

---

## Checklist de Verificación

Antes de reportar un error, verifica:

- ✅ Firebase está configurado correctamente
- ✅ google-services.json está en app/
- ✅ Web Client ID está en strings.xml
- ✅ SHA-1 está en Firebase Console
- ✅ Tienes conexión de internet
- ✅ Build es reciente (clean build)
- ✅ Datos existen en Firebase
- ✅ Security Rules permiten lectura

---

## Logs Útiles para Debugging

### Filtrar logs relevantes
```bash
./gradlew build | grep "D/"

O en Android Studio:
Logcat → Filter: MainViewModel
```

### Logs de cada ViewModel
```
D/MainViewModel      - Datos principales
D/RewardsViewModel   - Puntos
D/TokenViewModel     - Autenticación
D/SearchBar          - Búsqueda
D/CartActivity       - Carrito
```

---

## Contacto y Soporte

**Si el error no está aquí**:
1. Toma screenshot del error
2. Copia los logs completos
3. Describe qué hiciste antes del error
4. Reporta en GitHub issues

---

**Nota**: La mayoría de los errores se resuelven con:
1. Internet conexión ✅
2. Firebase correctamente configurado ✅
3. Clean build ✅

