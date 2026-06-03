# ✅ Checklist: Próximos Pasos del Proyecto

## 📖 Documentación

Toda la documentación está en `/documentation/`

- 📘 **[README.md](./documentation/README.md)** - Inicio
- 📗 **[INDEX.md](./documentation/INDEX.md)** - Índice de documentación
- 📙 **[01_AUTENTICACION.md](./documentation/01_AUTENTICACION.md)** - Flujo de autenticación
- 📕 **[02_USUARIO_NO_PREMIUM.md](./documentation/02_USUARIO_NO_PREMIUM.md)** - Sin auth
- 📓 **[03_USUARIO_PREMIUM.md](./documentation/03_USUARIO_PREMIUM.md)** - Con auth
- 📔 **[04_SISTEMA_RECOMPENSAS.md](./documentation/04_SISTEMA_RECOMPENSAS.md)** - Sistema de puntos
- 📕 **[05_CONFIGURACION_INICIAL.md](./documentation/05_CONFIGURACION_INICIAL.md)** - Setup
- 📗 **[06_SOLUCION_ERRORES.md](./documentation/06_SOLUCION_ERRORES.md)** - Solución de problemas

---

## 🚨 PASO 1 (CRÍTICO): Resolver Error ApiException: 10

**Objetivo:** Hacer que Google Sign-In funcione correctamente

### Substeps:

- [ ] **1.1** Obtener SHA-1
  ```bash
  cd /Users/danielalvarado/AndroidStudioProjects/ChickenFood
  ./gradlew signingReport
  ```
  - Copia el SHA-1 de la salida (sin dos puntos)
  - **Referencia:** `documentation/SOLUCION_ERROR_APIEXCEPTION_10.md`

- [ ] **1.2** Agregar SHA-1 a Firebase Console
  - Ve a https://console.firebase.google.com/
  - Selecciona proyecto: **chickenfood-b5459**
  - Ve a **Configuración del proyecto** → **Aplicaciones**
  - Selecciona tu app: **com.daniel.chickenfood**
  - En **Huella digital SHA-1**, haz clic en **Agregar huella digital**
  - Pega el SHA-1
  - Guarda

- [ ] **1.3** Crear Web Client ID en Google Cloud Console
  - Ve a https://console.cloud.google.com/
  - Selecciona proyecto: **chickenfood-b5459**
  - Ve a **APIs y servicios** → **Credenciales**
  - Haz clic en **+ Crear credenciales** → **ID de cliente OAuth**
  - Selecciona **Aplicación web**
  - Haz clic en **Crear**
  - Copia el **Client ID** (termina en `.apps.googleusercontent.com`)

- [ ] **1.4** Actualizar strings.xml
  - Abre `/app/src/main/res/values/strings.xml`
  - Busca: `<string name="default_web_client_id">YOUR_WEB_CLIENT_ID_HERE</string>`
  - Reemplaza con el Web Client ID del paso 1.3

- [ ] **1.5** Descargar google-services.json actualizado
  - En Firebase Console: **Configuración del proyecto** → **Aplicaciones**
  - Selecciona tu app
  - Haz clic en **Descargar google-services.json**
  - Reemplaza el archivo en `/app/google-services.json`

- [ ] **1.6** Limpiar y reconstruir
  ```bash
  cd /Users/danielalvarado/AndroidStudioProjects/ChickenFood
  ./gradlew clean
  ./gradlew build
  ```

- [ ] **1.7** Probar en emulador/dispositivo
  - Corre la app
  - Haz clic en "Inscribete"
  - Haz clic en "Continuar con Google"
  - Deberías ver el selector de cuentas Google
  - Selecciona una cuenta
  - Deberías ver: Toast "Autenticación exitosa"
  - Deberías navegar a MainActivity (Dashboard)

- [ ] **1.8** Verificar en Firebase Console
  - Ve a **Realtime Database**
  - Expande `/users`
  - Deberías ver tu userId
  - Expande `/tokens`
  - Deberías ver tus datos de token

---

## 📊 PASO 2: Crear UI para Mostrar Puntos en Dashboard

**Objetivo:** Mostrar saldo de puntos y nivel del usuario

### Substeps:

- [ ] **2.1** Crear componente `PointsCard.kt`
  - Muestre saldo de puntos actual
  - Muestre nivel del usuario
  - Muestre progreso a siguiente nivel
  - Ubicación: `/app/src/main/java/com/daniel/chickenfood/presentation/activity/dashboard/PointsCard.kt`

- [ ] **2.2** Crear PointsViewModel
  - Inyecte RewardsRepository
  - Método: `getRewards(userId: String)`
  - Estados: Loading, Success, Error
  - Ubicación: `/app/src/main/java/com/daniel/chickenfood/presentation/viewModel/PointsViewModel.kt`

- [ ] **2.3** Actualizar AppModule
  - Agregar: `viewModel { PointsViewModel(rewardsRepository = get()) }`

- [ ] **2.4** Integrar PointsCard en MainActivity
  - Inyectar PointsViewModel
  - Obtener recompensas del usuario
  - Mostrar PointsCard
  - Manejar loading y errores

- [ ] **2.5** Agregar PointsCard al TopBar o como sección separada
  - Puede ir en el TopBar
  - O debajo del Banner
  - Elije la ubicación que se vea mejor

- [ ] **2.6** Probar
  - La app debería mostrar 0 puntos (sin compras aún)
  - No debería haber errores en los logs

---

## 💳 PASO 3: Integrar Canje de Puntos en Cart

**Objetivo:** Permitir usar puntos para descuento en carrito

### Substeps:

- [ ] **3.1** Actualizar CartActivity para mostrar puntos disponibles
  - Inyectar RewardsViewModel
  - Obtener puntos disponibles
  - Mostrar en la UI

- [ ] **3.2** Agregar selector de puntos a usar
  - Input field para número de puntos
  - Validar que no supere puntos disponibles
  - Mostrar descuento en tiempo real

- [ ] **3.3** Actualizar cálculo de total
  - Total original: `$X`
  - Descuento por puntos: `$Y`
  - Total final: `$X - $Y`

- [ ] **3.4** Agregar método `canjePoints` a RewardsViewModel
  - Parámetros: `userId`, `puntos`, `orderId`
  - Guardar transacción en Firebase
  - Actualizar saldo de puntos

- [ ] **3.5** Al confirmar compra
  - Validar puntos no superen disponibles
  - Llamar a `canjePoints`
  - Crear orden con descuento aplicado
  - Mostrar confirmación

- [ ] **3.6** Probar
  - Agregar producto al carrito
  - Ver puntos disponibles (inicialmente 0)
  - No debería permitir usar puntos si hay 0

---

## 📋 PASO 4: Crear Pantalla de Transacciones

**Objetivo:** Mostrar historial de puntos ganados y gastados

### Substeps:

- [ ] **4.1** Crear TransactionsActivity
  - Mostrar lista de transacciones
  - Filtrar por tipo: Ganado, Gastado, Total
  - Mostrar fecha, monto, tipo

- [ ] **4.2** Crear TransactionsViewModel
  - Método: `getTransactions(userId: String)`
  - Método: `filterByType(type: String)`
  - Estados: Loading, Success, Error

- [ ] **4.3** Agregar botón en Dashboard para ver transacciones
  - Ubicar en TopBar o en PointsCard
  - Click → Abre TransactionsActivity

- [ ] **4.4** Diseñar UI
  - Card por cada transacción
  - Mostrar: Fecha, Tipo, Monto, Balance después
  - Scroll vertical para ver historial

- [ ] **4.5** Probar
  - Hacer compra
  - Ver transacción en historial
  - Historial debería mostrar puntos ganados

---

## 🔄 PASO 5: Renovación Automática de Tokens

**Objetivo:** Renovar tokens cuando expire

### Substeps:

- [ ] **5.1** Agregar método a TokenViewModel
  - `refreshToken(userId: String)`
  - Llamar a Firebase para renovar
  - Guardar nuevo token

- [ ] **5.2** Agregar listener en BaseActivity
  - Verificar validez de token al abrir app
  - Si está expirado, renovar automáticamente
  - Si no puede renovar, hacer logout

- [ ] **5.3** Implementar lógica de retry
  - Reintentar hasta 3 veces si falla
  - Esperar 2 segundos entre intentos
  - Si sigue fallando, logout

- [ ] **5.4** Logging
  - Agregar logs de renovación
  - Logging de errores

- [ ] **5.5** Probar
  - Simular token expirado
  - Verificar que se renueva automáticamente

---

## ✔️ PASO 6: Validación de Tokens en SplashActivity

**Objetivo:** Verificar que usuario siga autenticado

### Substeps:

- [ ] **6.1** Actualizar SplashActivity
  - Inyectar TokenViewModel
  - Cuando abre app, verificar si hay token válido

- [ ] **6.2** Si token válido
  - Navegar directamente a MainActivity
  - Sin mostrar los botones "Empecemos" / "Inscribete"

- [ ] **6.3** Si token inválido o no existe
  - Mostrar los botones "Empecemos" / "Inscribete"

- [ ] **6.4** Durante validación
  - Mostrar splash screen mientras verifica
  - Mostrar CircularProgressIndicator

- [ ] **6.5** Probar
  - Abrir app habiendo sido autenticado antes
  - Debería navegar directamente a Dashboard
  - Sin mostrar botones de autenticación

---

## 🎉 PASO 7: Confirmación de Orden

**Objetivo:** Mostrar confirmación después de compra

### Substeps:

- [ ] **7.1** Crear OrderConfirmationActivity
  - Mostrar detalles de orden
  - Mostrar items comprados
  - Mostrar puntos ganados
  - Mostrar total pagado

- [ ] **7.2** Agregar botón en CartActivity
  - "Proceder al Pago" → Abre OrderConfirmationActivity
  - Pasar detalles de orden

- [ ] **7.3** Diseñar UI
  - Header con marca de éxito ✓
  - Número de orden
  - Lista de items
  - Puntos ganados (destacado)
  - Botón "Ir al Dashboard"

- [ ] **7.4** Guardar orden en Firebase
  - Crear orden en `/orders/{orderId}`
  - Guardar en `/users/{userId}/orders/{orderId}`
  - Guardar transacción de puntos

- [ ] **7.5** Probar
  - Hacer compra
  - Ver confirmación
  - Verificar en Firebase Console que se guardó

---

## 📝 Notas Importantes

### Prioridades

1. **CRÍTICA**: Resolver ApiException: 10 (Paso 1)
2. **ALTA**: Crear UI para puntos (Paso 2)
3. **MEDIA**: Canje de puntos en cart (Paso 3)
4. **MEDIA**: Pantalla de transacciones (Paso 4)
5. **BAJA**: Renovación automática (Paso 5)
6. **BAJA**: Validación en Splash (Paso 6)
7. **BAJA**: Confirmación de orden (Paso 7)

### Dependencias

- Paso 2 depende de: Paso 1 ✅
- Paso 3 depende de: Paso 2 ✓
- Paso 4 depende de: Paso 3 ✓
- Paso 5 depende de: Ninguno ✓
- Paso 6 depende de: Ninguno ✓
- Paso 7 depende de: Paso 3 ✓

### Recomendación

**Hacer los pasos en orden, pero puedes hacer Pasos 5 y 6 en paralelo con otros.**

---

## ✅ Checklist de Verificación

Después de cada paso:

- [ ] ¿Compila sin errores?
- [ ] ¿Sin warnings?
- [ ] ¿Se ve bien en UI?
- [ ] ¿Los logs no muestran errores?
- [ ] ¿Firebase Console muestra los datos?
- [ ] ¿Funciona en emulador Y dispositivo?

---

## 🎯 Objetivo Final

Cuando completes todos los pasos:

✅ Google Sign-In funcionando
✅ Tokens guardados automáticamente
✅ Puntos mostrados en Dashboard
✅ Canje de puntos funcionando
✅ Historial de transacciones
✅ Tokens renovándose automáticamente
✅ Validación de sesión
✅ Confirmación de compras

**App lista para producción.**

---

**Estado:** En progreso
**Último update:** 2026-06-01
**Próximo paso:** Paso 1 (Resolver ApiException: 10)

