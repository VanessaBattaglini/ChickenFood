# 🚀 Inicio Rápido - ChickenFood App

**Objetivo**: Entiende cómo funciona la app en 5 minutos.

---

## ¿Qué es ChickenFood?

Una aplicación de comida rápida donde:
- 📱 Ves productos (pollo asado, hamburguesas, etc.)
- 🔍 Buscas lo que quieres
- 🛒 Agregas al carrito
- 💳 Pagas
- 💰 Ganas puntos para descuentos

---

## Flujo de Usuario

### Opción 1: Sin Autenticar (Rápido)

```
Abrir app
    ↓
Botón "Empecemos" (SplashScreen)
    ↓
Dashboard (Home)
    ↓
Buscar o ver categorías
    ↓
Agregar al carrito
    ↓
Pagar
    ↓
Fin (sin puntos)
```

### Opción 2: Con Autenticación (Con Puntos)

```
Abrir app
    ↓
Botón "Inscribete" (SplashScreen)
    ↓
Google Sign-In (elige tu cuenta)
    ↓
Dashboard (Home) + Tarjeta de Puntos
    ↓
Buscar o ver categorías
    ↓
Agregar al carrito
    ↓
Pagar
    ↓
Ganas 10% de puntos
    ↓
Ves tus puntos en Dashboard
```

---

## Pantallas Principales

### 1. SplashScreen (Inicio)
```
┌─────────────────────────┐
│  🍗 ChickenFood         │
├─────────────────────────┤
│                         │
│   [Empecemos]           │
│                         │
│   [Inscribete]          │
└─────────────────────────┘
```

**Empecemos**: Sin autenticación, acceso normal  
**Inscribete**: Con Google Sign-In, acceso premium

---

### 2. Dashboard (Home)
```
┌─────────────────────────┐
│ Logout                  │ (TopBar)
├─────────────────────────┤
│ [Buscar alimentos...]   │ (SearchBar)
├─────────────────────────┤
│ 👤 150 puntos          │ (PointsCard - solo si autenticado)
│ Nivel: Bronce          │
│ Progreso: ████░░░░░░  │
├─────────────────────────┤
│ [Banner: PROMO 50%]     │ (Banner)
├─────────────────────────┤
│ Categorías              │
│ • Aves        • Carnes  │
│ • Bebidas     • Postre  │
├─────────────────────────┤
│                    🛒 0 │ (BottomBar)
│              Home | Carrito
└─────────────────────────┘
```

---

### 3. Búsqueda en Tiempo Real
```
Dashboard → Escribir "pollo" → Ver resultados dropdown

┌─────────────────────────┐
│ [🔍 pollo       ✕]      │
├─────────────────────────┤
│ Pollo Asado        $8.99│
│ Pollo Frito        $7.99│
│ Pollo a la Piña    $9.99│
└─────────────────────────┘
```

**Tocar un resultado** → Abre DetailScreen

---

### 4. Detalle del Producto
```
┌─────────────────────────┐
│ [←]  Pollo Asado   [❤] │
├─────────────────────────┤
│ [Imagen grande]         │
│ ⭐⭐⭐⭐⭐ (4.8)         │
├─────────────────────────┤
│ $8.99                   │
│ Pollo marinado con      │
│ especias tradicionales  │
│                         │
│ Cantidad: [- 1 +]       │
│                         │
│ [Agregar al Carrito]    │
└─────────────────────────┘
```

---

### 5. Carrito
```
┌─────────────────────────┐
│ Mi Carrito              │
├─────────────────────────┤
│ Pollo Asado         $8  │ [x] (eliminar)
│ Hamburguesa × 2    $14  │ [x]
│                         │
│ Total: $22              │
│                         │
│ [Continuar Comprando]   │
│ [Proceder al Pago]      │
└─────────────────────────┘
```

---

## 💰 Sistema de Puntos

### Cómo Funciona

1. **Compra 1**: Gastaste $100 → Ganas **10 puntos** (Regular)
2. **Compra 2**: Gastaste $100 → Ganas **12 puntos** (Bronce) 
3. **Compra 3**: Gastaste $100 → Ganas **15 puntos** (Platino)

### Niveles

| Nivel | Puntos | Cashback | Emoji |
|-------|--------|----------|-------|
| Regular | 0 | 10% | 👤 |
| Bronce | 1-99 | 10% | 🥉 |
| Plata | 100-499 | 11% | 🥈 |
| Oro | 500-999 | 12% | 🏆 |
| Platino | 1000+ | 15% | 👑 |

### Conversión de Puntos

- 1 punto = **$0.01**
- 100 puntos = **$1.00**
- 1000 puntos = **$10.00**

---

## 🔍 Buscador

### Cómo Funciona

1. **Escribe** en el SearchBar (ej: "pollo")
2. **Ve resultados** en tiempo real (dropdown)
3. **Toca un resultado** para ver detalle
4. **Agrega al carrito** desde la pantalla de detalle

### Ejemplos

| Query | Resultados |
|-------|-----------|
| "pollo" | Pollo Asado, Pollo Frito, Pollo a la Piña |
| "hamburguesa" | Hamburguesa Clásica, Premium, Doble |
| "pizza" | Pizza Margarita, Pepperoni |
| "xyz" | No encontramos 'xyz' en nuestro menú |

---

## 🛒 Carrito

### Agregar Producto

1. Ir a DetailScreen
2. Ajustar cantidad con [- 1 +]
3. Tocar **"Agregar al Carrito"**
4. Ir a CartScreen

### Eliminar Producto

1. En CartScreen
2. Tocar **"X"** en el producto
3. Se elimina completamente

### Limpiar Carrito

1. En CartScreen
2. Tocar **"Proceder al Pago"**
3. Carrito se limpia automáticamente
4. Ganas puntos (si autenticado)

---

## 🔐 Autenticación

### Sin Autenticación
- Acceso inmediato
- Ver productos
- Comprar
- **No ganas puntos**

### Con Autenticación (Google)
- Toca **"Inscribete"**
- Elige tu cuenta Google
- Acceso a:
  - Ver puntos
  - Ver nivel
  - Ganar puntos por compras
  - Canjear puntos

### Logout
- Toca **"Logout"** en TopBar (arriba a la derecha)
- Vuelves a SplashScreen

---

## 🎯 Checklist de Funcionalidades

- ✅ Ver productos por categoría
- ✅ Buscar productos en tiempo real
- ✅ Ver detalle de producto
- ✅ Agregar/eliminar del carrito
- ✅ Ver carrito
- ✅ Pagar
- ✅ Autenticación Google
- ✅ Ver puntos (si autenticado)
- ✅ Ganar puntos por compra
- ✅ Niveles de usuario
- ✅ Logout

---

## ⚠️ Notas Importantes

1. **Primera vez**: Necesitas WiFi o datos para cargar productos
2. **Firebase**: Debe estar configurado (ver [07_CONFIGURACION_INICIAL.md](07_CONFIGURACION_INICIAL.md))
3. **Google Sign-In**: Requiere cuenta Google
4. **Puntos**: Solo se ganan si estás autenticado
5. **Carrito**: Se guarda localmente (no en Firebase)

---

## 🆘 Si Algo No Funciona

1. **No aparecen productos**: 
   - Verifica conexión a internet
   - Verifica Firebase está configurado

2. **Error en Google Sign-In**:
   - Ver [08_SOLUCION_ERRORES.md](08_SOLUCION_ERRORES.md)

3. **Carrito no se limpia**:
   - Recarga la app

4. **No ves puntos**:
   - Debes estar autenticado
   - Debes hacer una compra primero

---

**¡Listo!** Ahora entiendes cómo funciona ChickenFood. 

**Próximo paso**: Configura Firebase en [07_CONFIGURACION_INICIAL.md](07_CONFIGURACION_INICIAL.md)
