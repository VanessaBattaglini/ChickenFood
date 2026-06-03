# 👤 Guía del Usuario No Premium (Sin Autenticación)

## Resumen

Un usuario No Premium es quien accede a la app sin autenticarse con Google. Puede ver el catálogo de comidas, seleccionar productos y verlos en detalle, pero **NO puede:**
- Acumular puntos
- Hacer compras finales
- Ver historial de transacciones

## 🎯 Actividades Disponibles

### 1️⃣ Ver Dashboard

**Actividad:** `MainActivity.kt`

El usuario ve el Dashboard con:

```
┌─────────────────────────────────┐
│  TopBar                         │
│  ├─ 🔍 Búsqueda                 │
│  ├─ 🛒 Carrito                  │
│  └─ 👤 Perfil (No disponible)   │
├─────────────────────────────────┤
│  Banner                         │
│  ├─ Logo de ChickenFood         │
│  └─ Información promocional     │
├─────────────────────────────────┤
│  Categorías                     │
│  ├─ 🍔 Hamburguesas             │
│  ├─ 🍕 Pizzas                   │
│  ├─ 🥤 Bebidas                  │
│  └─ 🍟 Acompañamientos          │
├─────────────────────────────────┤
│  Lista de Comidas               │
│  ├─ Item 1 (Hamburguesa)        │
│  ├─ Item 2 (Pizza)              │
│  └─ Item 3 (Bebida)             │
└─────────────────────────────────┘
```

**Métodos:**
```kotlin
// Cargar productos
mainViewModel.getMainData()

// Filtrar por categoría
mainViewModel.filterByCategory(categoryName)

// Buscar productos
mainViewModel.searchProducts(query)
```

**Estados que se muestran:**
```kotlin
when (mainState) {
    is MainState.Loading -> CircularProgressIndicator()
    is MainState.Success -> MostrarProductos(products)
    is MainState.Error -> MostrarError(state.message)
    else -> {}
}
```

---

### 2️⃣ Seleccionar Categoría

**Actividad:** `MainActivity.kt` → `CategorySection`

El usuario hace clic en una categoría para filtrar productos:

```kotlin
// CategorySection.kt
private fun handleCategoryClick(category: String) {
    viewModel.filterByCategory(category)
}
```

**Ejemplo de categorías disponibles:**
- Hamburguesas
- Pizzas
- Bebidas
- Acompañamientos
- Postres

**Resultado:** La lista de comidas se actualiza mostrando solo productos de esa categoría.

---

### 3️⃣ Ver Detalle de Producto

**Actividad:** `DetailEachFoodActivity.kt`

El usuario hace clic en un producto y ve:

```
┌──────────────────────────────┐
│  ← (Botón de atrás)          │
├──────────────────────────────┤
│                              │
│  [  Imagen Grande  ]         │
│                              │
├──────────────────────────────┤
│  Nombre del Producto         │
│  ⭐⭐⭐⭐⭐ (5.0)             │
│  $12.99                      │
├──────────────────────────────┤
│  Descripción:                │
│  "Deliciosa hamburguesa con  │
│  queso y tomate fresco"      │
├──────────────────────────────┤
│  Cantidad: [1] [+] [-]       │
│                              │
│  ┌──────────────────────────┐│
│  │ 🛒 AGREGAR AL CARRITO    ││
│  └──────────────────────────┘│
└──────────────────────────────┘
```

**Métodos:**
```kotlin
// Incrementar cantidad
quantity++

// Decrementar cantidad
if (quantity > 1) quantity--

// Agregar al carrito
ManagmentCart.addToCart(product, quantity)
```

**Características:**
- Imagen grande del producto
- Rating del producto
- Precio
- Descripción completa
- Selector de cantidad
- Botón "Agregar al Carrito"

**Toast mostrado:**
```kotlin
Toast.makeText(
    this,
    "Se agregó: ${product.name} × $quantity",
    Toast.LENGTH_SHORT
).show()
```

---

### 4️⃣ Agregar al Carrito

**Clase:** `ManagmentCart.kt`

Cuando el usuario hace clic en "Agregar al Carrito":

```kotlin
fun addToCart(product: FoodModel, quantity: Int) {
    val cartItem = CartItem(
        product = product,
        quantity = quantity,
        totalPrice = product.price * quantity
    )
    cart.add(cartItem)
    notifyListeners()
}
```

**Ejemplo de datos guardados:**
```
Cart (En memoria):
├─ Item 1: Hamburguesa × 2 = $25.98
├─ Item 2: Pizza × 1 = $12.99
└─ Item 3: Bebida × 3 = $9.99
```

**Métodos disponibles:**
```kotlin
// Obtener carrito actual
cart: MutableList<CartItem>

// Obtener cantidad de items
getCartItemCount(): Int

// Obtener total del carrito
getTotalPrice(): Double

// Limpiar carrito
clearCart()
```

---

### 5️⃣ Ver Carrito

**Actividad:** `CartActivity.kt`

El usuario hace clic en el ícono del carrito y ve:

```
┌─────────────────────────────┐
│  Mi Carrito                 │
├─────────────────────────────┤
│  Item 1                     │
│  Hamburguesa × 2           │
│  $12.99 c/u = $25.98       │
│  [-] 2 [+]                 │
├─────────────────────────────┤
│  Item 2                     │
│  Pizza × 1                 │
│  $12.99                    │
│  [-] 1 [+]                 │
├─────────────────────────────┤
│  TOTAL: $38.97             │
├─────────────────────────────┤
│  ┌─────────────────────────┐│
│  │ 🛒 Proceder al Pago     ││
│  └─────────────────────────┘│
│                             │
│  ┌─────────────────────────┐│
│  │ Continuar Comprando     ││
│  └─────────────────────────┘│
└─────────────────────────────┘
```

**Métodos:**
```kotlin
// Cambiar cantidad
ManagmentCart.updateQuantity(itemIndex, newQuantity)

// Eliminar item
ManagmentCart.removeItem(itemIndex)

// Obtener total
totalPrice = ManagmentCart.getTotalPrice()
```

**Estados:**

**Carrito Vacío:**
```
┌─────────────────────────┐
│  Mi Carrito             │
├─────────────────────────┤
│   🛒 Carrito Vacío      │
│                         │
│  ┌───────────────────┐  │
│  │Continuar Comprando│  │
│  └───────────────────┘  │
└─────────────────────────┘
```

**Carrito con Items:**
```
Muestra lista de productos
+ Total del carrito
+ Botón "Proceder al Pago"
+ Botón "Continuar Comprando"
```

---

### 6️⃣ Continuar Comprando

**Botón:** "Continuar Comprando" en CartActivity

```kotlin
private fun handleContinueShoppingClick() {
    navigateToHome()
}
```

**Acción:** Regresa a MainActivity y mantiene el carrito

---

## 📊 Resumen de Actividades

| Actividad | Descripción | Componentes |
|-----------|-------------|------------|
| Ver Dashboard | Mostrar productos | MainViewModel, Banner, CategorySection |
| Seleccionar Categoría | Filtrar productos | CategorySection, MainViewModel |
| Ver Detalle | Ver producto completo | DetailEachFoodActivity, DetailViewModel |
| Agregar al Carrito | Guardar producto + cantidad | ManagmentCart |
| Ver Carrito | Mostrar items guardados | CartActivity, CartViewModel |
| Proceder al Pago | ⚠️ No disponible para No Premium | - |
| Usar Puntos | ⚠️ No disponible para No Premium | - |
| Ver Puntos | ⚠️ No disponible para No Premium | - |

---

## 🎯 Limitaciones del Usuario No Premium

```
✅ Puede hacer:
├─ Ver productos
├─ Filtrar por categoría
├─ Ver detalles de producto
├─ Agregar al carrito
├─ Ver carrito
└─ Cambiar cantidades en carrito

❌ NO puede hacer:
├─ Completar compra
├─ Acumular puntos
├─ Ver saldo de puntos
├─ Usar puntos para descuento
├─ Ver historial de compras
└─ Acceder a perfil premium
```

---

## 🚀 Cómo Convertirse en Usuario Premium

Para pasar de Usuario No Premium a Premium:

1. Desde SplashActivity → Hacer clic en "Inscribete"
2. Seleccionar cuenta Google
3. Autenticarse
4. Se guardará token y pasará a ser Usuario Premium

**Diferencia después de autenticarse:**
- Pueden ver saldo de puntos
- Pueden usar puntos para descuento
- Pueden ver historial de compras
- Pueden completar compras exitosamente

---

## 💾 Persistencia de Datos

### Carrito:
```kotlin
// En ManagmentCart (Singleton)
private val cart: MutableList<CartItem> = mutableListOf()
```

**Nota:** El carrito se mantiene en memoria mientras la app está abierta. Si cierra la app, se pierde el carrito.

### Preferencias del Usuario:
```kotlin
// Búsquedas recientes, favoritos (si implementado)
// Se guardarían en SharedPreferences o Firebase
```

---

## 🔄 Flujo Completo del Usuario No Premium

```
1. Abre app
   ↓
2. Ve SplashActivity → Hace clic "Empecemos"
   ↓
3. Va a MainActivity (Dashboard)
   ↓
4. Ve categorías y productos
   ↓
5. Selecciona categoría (opcional)
   ↓
6. Hace clic en producto
   ↓
7. Ve DetailEachFoodActivity
   ↓
8. Elige cantidad y hace clic "Agregar al Carrito"
   ↓
9. Hace clic en ícono carrito
   ↓
10. Ve CartActivity con items
    ↓
11. Puede cambiar cantidades o volver a comprar
    ↓
12. Si intenta "Proceder al Pago" → Se muestra mensaje:
    "Debe autenticarse para completar la compra"
```

---

## 📝 Componentes Relacionados

- [Flujo de Autenticación](./01_AUTENTICACION.md)
- [Guía del Usuario Premium](./03_USUARIO_PREMIUM.md)
- [Sistema de Recompensas](./04_SISTEMA_RECOMPENSAS.md)

---

**Estado:** Funcional
**Última actualización:** 2026-06-01

