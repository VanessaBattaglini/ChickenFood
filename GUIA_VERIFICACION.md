# ✅ GUÍA DE VERIFICACIÓN - Paso a Paso

## 🎯 Objetivo
Verificar que el flujo completo funciona correctamente desde la pantalla de splash hasta el botón "Agregar al carrito".

---

## 📋 CHECKLIST COMPLETO

### PASO 1: PREPARAR EL ENTORNO
- [ ] Abrir Android Studio
- [ ] Abrir el proyecto ChickenFood
- [ ] Conectar emulador o dispositivo
- [ ] Abrir logcat (View → Tool Windows → Logcat)
- [ ] Filtrar por tag: `DetailEachFoodActivity`, `DetailScreen`, `FooterSection`, `CartActivity`

### PASO 2: COMPILAR Y EJECUTAR
- [ ] Ejecutar `./gradlew build` para compilar
- [ ] Ejecutar la app en el emulador/dispositivo
- [ ] Esperar a que la app inicie

### PASO 3: VERIFICAR SPLASH SCREEN
- [ ] Se muestra la pantalla de splash
- [ ] Se ve la imagen de introducción
- [ ] Se ve el logo del pollo
- [ ] Se ve el texto motivacional
- [ ] Se ve el botón "Get Started"
- [ ] Hacer click en "Get Started"
- [ ] Se abre MainActivity

### PASO 4: VERIFICAR DASHBOARD
- [ ] Se muestra el dashboard
- [ ] Se ven las categorías (Pollo, Bebidas, Postres, etc.)
- [ ] Se ven los banners (carrusel)
- [ ] Se ve el BottomBar con Home, Search, Cart, Profile
- [ ] El badge del Cart muestra "0" (carrito vacío)
- [ ] Hacer click en la categoría "Pollo"
- [ ] Se abre ItemsListActivity

### PASO 5: VERIFICAR LISTA DE PRODUCTOS
- [ ] Se muestra la lista de productos
- [ ] Se ven los productos de la categoría "Pollo"
- [ ] Se ve el nombre, imagen, precio, tiempo, rating
- [ ] Hacer click en un producto (ej: "Combo Pollo Asado")
- [ ] Se abre DetailEachFoodActivity

### PASO 6: VERIFICAR DETALLE DEL PRODUCTO
- [ ] Se muestra la imagen del producto
- [ ] Se muestra el nombre del producto
- [ ] Se muestra el tiempo de preparación
- [ ] Se muestra el rating
- [ ] Se muestra la descripción
- [ ] Se muestra el selector de cantidad (-, cantidad, +)
- [ ] Se muestra el precio unitario
- [ ] Se muestra el precio total
- [ ] Se muestra el botón "Agregar al carrito"

### PASO 7: VERIFICAR SELECTOR DE CANTIDAD
- [ ] La cantidad inicial es 1
- [ ] Hacer click en "+"
- [ ] La cantidad aumenta a 2
- [ ] El precio total se actualiza (se multiplica por 2)
- [ ] Hacer click en "+" nuevamente
- [ ] La cantidad aumenta a 3
- [ ] El precio total se actualiza (se multiplica por 3)
- [ ] Hacer click en "-"
- [ ] La cantidad disminuye a 2
- [ ] El precio total se actualiza (se divide por 2)
- [ ] Hacer click en "-" nuevamente
- [ ] La cantidad disminuye a 1
- [ ] Hacer click en "-" nuevamente
- [ ] La cantidad NO disminuye (mínimo 1)

### PASO 8: VERIFICAR BOTÓN "AGREGAR AL CARRITO"
- [ ] Seleccionar una cantidad (ej: 2)
- [ ] Hacer click en el botón "Agregar al carrito"
- [ ] **VERIFICAR EN LOGCAT:**
  - [ ] Buscar: "D/FooterSection: Agregar al carrito button clicked"
  - [ ] Buscar: "D/DetailScreen: FooterSection.onAddToCartClick called with quantity: 2"
  - [ ] Buscar: "D/DetailEachFoodActivity: onAddToCartClick triggered for: Combo Pollo Asado, quantity: 2"
  - [ ] Buscar: "D/DetailEachFoodActivity: Updated item quantity to: 2"
  - [ ] Buscar: "D/DetailEachFoodActivity: insertItem completed successfully"
  - [ ] Buscar: "D/DetailEachFoodActivity: Navigating to CartActivity"
- [ ] Se abre CartActivity

### PASO 9: VERIFICAR CARRITO
- [ ] Se muestra el carrito
- [ ] Se ve el producto agregado (Combo Pollo Asado)
- [ ] Se ve la cantidad (2)
- [ ] Se ve el precio unitario
- [ ] Se ve el subtotal (precio * cantidad)
- [ ] Se ve el total
- [ ] El badge del Cart en el BottomBar muestra "1" (1 producto en el carrito)
- [ ] **VERIFICAR EN LOGCAT:**
  - [ ] Buscar: "D/CartActivity: CartActivity opened"
  - [ ] Buscar: "D/CartActivity: Cart items loaded: 1 items"
  - [ ] Buscar: "D/CartActivity:   - Combo Pollo Asado x2 = $50000"

### PASO 10: VERIFICAR NAVEGACIÓN DESDE CARRITO
- [ ] Hacer click en el botón "Home" en el header del carrito
- [ ] Se abre MainActivity
- [ ] Se ve el dashboard
- [ ] El badge del Cart muestra "1" (el producto sigue en el carrito)

### PASO 11: AGREGAR OTRO PRODUCTO
- [ ] Hacer click en la categoría "Bebidas"
- [ ] Se abre ItemsListActivity
- [ ] Se ven los productos de la categoría "Bebidas"
- [ ] Hacer click en un producto (ej: "Coca Cola")
- [ ] Se abre DetailEachFoodActivity
- [ ] Seleccionar una cantidad (ej: 3)
- [ ] Hacer click en "Agregar al carrito"
- [ ] Se abre CartActivity
- [ ] Se ven 2 productos en el carrito:
  - [ ] Combo Pollo Asado x2
  - [ ] Coca Cola x3
- [ ] El badge del Cart muestra "2" (2 productos en el carrito)
- [ ] El total se calcula correctamente

### PASO 12: VERIFICAR ELIMINACIÓN DE PRODUCTO
- [ ] En el carrito, hacer click en el botón "X" de un producto
- [ ] El producto se elimina del carrito
- [ ] El total se actualiza
- [ ] El badge del Cart se actualiza

---

## 🔍 LOGS ESPERADOS EN LOGCAT

### Cuando se abre DetailEachFoodActivity
```
D/DetailEachFoodActivity: DetailEachFoodActivity opened with item: Combo Pollo Asado (id=6)
```

### Cuando se renderiza DetailScreen
```
D/DetailScreen: DetailScreen rendering for item: Combo Pollo Asado
```

### Cuando se renderiza FooterSection
```
D/DetailScreen: Rendering FooterSection with totalPrice: 25000
```

### Cuando el usuario hace click en el botón
```
D/FooterSection: Agregar al carrito button clicked
D/DetailScreen: FooterSection.onAddToCartClick called with quantity: 2
D/DetailEachFoodActivity: onAddToCartClick triggered for: Combo Pollo Asado, quantity: 2
D/DetailEachFoodActivity: Updated item quantity to: 2
D/DetailEachFoodActivity: insertItem completed successfully
D/DetailEachFoodActivity: Navigating to CartActivity
```

### Cuando se abre CartActivity
```
D/CartActivity: CartActivity opened
D/CartActivity: Cart items loaded: 1 items
D/CartActivity:   - Combo Pollo Asado x2 = $50000
```

---

## ⚠️ PROBLEMAS COMUNES Y SOLUCIONES

### Problema: El botón no responde
**Síntomas:**
- Usuario hace click en "Agregar al carrito"
- Nada ocurre
- No aparecen logs en logcat

**Solución:**
1. Verificar que DetailScreen está renderizando (buscar log "DetailScreen rendering")
2. Verificar que FooterSection está renderizando (buscar log "Rendering FooterSection")
3. Verificar que Button recibe clicks (buscar log "button clicked")
4. Si no aparecen logs, revisar que DetailEachFoodActivity tiene:
   - `enableEdgeToEdge()`
   - `MaterialTheme { DetailScreen(...) }`

### Problema: DetailScreen no se ve
**Síntomas:**
- Se abre DetailEachFoodActivity
- Pero la pantalla está en blanco o no se ve nada

**Solución:**
1. Verificar que DetailEachFoodActivity tiene `enableEdgeToEdge()`
2. Verificar que DetailScreen está envuelto con `MaterialTheme`
3. Revisar los logs en logcat para ver si hay errores

### Problema: El carrito no muestra el producto
**Síntomas:**
- Se hace click en "Agregar al carrito"
- Se abre CartActivity
- Pero el producto no aparece en el carrito

**Solución:**
1. Verificar que `ManagmentCart.insertItem()` se ejecutó (buscar log "insertItem completed")
2. Verificar que el producto se guardó en SQLite
3. Revisar que `CartActivity.onCreate()` obtiene la lista correctamente

### Problema: La cantidad no es correcta
**Síntomas:**
- Se selecciona cantidad 2
- Se agrega al carrito
- Pero en el carrito aparece cantidad 1 (o diferente)

**Solución:**
1. Verificar que `item.numberInCart = quantity` se ejecutó
2. Verificar que el callback recibe la cantidad correcta
3. Revisar que `ManagmentCart.insertItem()` REEMPLAZA la cantidad (no suma)

### Problema: El precio total no se actualiza
**Síntomas:**
- Se hace click en "+"
- La cantidad aumenta
- Pero el precio total no cambia

**Solución:**
1. Verificar que el precio se calcula como `item.price * quantity`
2. Verificar que FooterSection recibe el `totalPrice` correcto
3. Revisar que el estado `quantity` se actualiza correctamente

---

## 📊 TABLA DE VERIFICACIÓN RÁPIDA

| Paso | Verificar | ✅/❌ |
|------|-----------|-------|
| 1 | Splash Screen se muestra | |
| 2 | Botón "Get Started" funciona | |
| 3 | Dashboard se muestra | |
| 4 | Categorías se cargan | |
| 5 | Click en categoría funciona | |
| 6 | Lista de productos se muestra | |
| 7 | Productos se cargan desde Firebase | |
| 8 | Click en producto funciona | |
| 9 | DetailScreen se muestra | |
| 10 | Selector de cantidad funciona | |
| 11 | Precio total se actualiza | |
| 12 | Botón "Agregar al carrito" responde | |
| 13 | Logs aparecen en logcat | |
| 14 | CartActivity se abre | |
| 15 | Producto aparece en carrito | |
| 16 | Cantidad es correcta | |
| 17 | Precio es correcto | |
| 18 | Badge del Cart se actualiza | |

---

## 🎯 VERIFICACIÓN FINAL

Si todos los pasos anteriores funcionan correctamente, entonces:

✅ **El flujo completo funciona correctamente**
✅ **El botón "Agregar al carrito" responde a clicks**
✅ **Los productos se agregan al carrito correctamente**
✅ **Las cantidades se persisten correctamente**
✅ **Los precios se calculan correctamente**
✅ **La navegación funciona correctamente**

---

## 📝 NOTAS IMPORTANTES

1. **Logcat es tu mejor amigo**
   - Siempre revisa los logs en logcat
   - Busca los tags específicos (DetailEachFoodActivity, DetailScreen, etc.)
   - Los logs te dirán exactamente dónde se detiene el flujo

2. **Verifica los datos en SQLite**
   - Puedes usar Android Studio para inspeccionar la base de datos
   - Verifica que los productos se guardan correctamente
   - Verifica que las cantidades son correctas

3. **Prueba en diferentes dispositivos**
   - Prueba en emulador
   - Prueba en dispositivo físico
   - Verifica que funciona en ambos

4. **Prueba con diferentes productos**
   - Prueba con productos de diferentes categorías
   - Prueba con diferentes cantidades
   - Verifica que los precios se calculan correctamente

5. **Prueba la navegación completa**
   - Splash → Dashboard → Lista → Detalle → Carrito → Dashboard
   - Verifica que puedes navegar en ambas direcciones
   - Verifica que los datos se persisten correctamente

---

## 🚀 PRÓXIMOS PASOS

1. **Ejecutar la app**
2. **Seguir el checklist anterior**
3. **Revisar los logs en logcat**
4. **Identificar cualquier problema**
5. **Aplicar los fixes necesarios**
6. **Hacer prueba end-to-end completa**

¡Buena suerte! 🍀
