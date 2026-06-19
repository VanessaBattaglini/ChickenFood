# 🎨 Guía Visual - SuperCrunchy Pollo

Una guía visual de la aplicación, sus componentes y flujos principales.

---

## 🍗 Logo de la Aplicación

**Ubicación**: `app/src/main/res/mipmap-*/ic_launcher_pollo.webp`

Logo principal del pollo de SuperCrunchy Pollo - representa la marca.

---

## 📱 Pantallas Principales

### 1. Splash Screen
**Ubicación**: `SplashActivity`

Pantalla inicial que aparece al abrir la app.

```
┌─────────────────────┐
│                     │
│      🍗 LOGO        │
│   SuperCrunchy      │
│      Pollo          │
│                     │
│  [Cargando...]      │
│                     │
└─────────────────────┘
```

**Función**: 
- Verificar autenticación
- Cargar datos iniciales
- Navegar a Login o Dashboard

---

### 2. Dashboard (MainActivity)
**Ubicación**: `presentation/activity/dashboard/MainActivity.kt`

Pantalla principal después de autenticarse.

```
┌──────────────────────────────┐
│ [Perfil] 💎 500pts [🔔]      │  ← TopBar
├──────────────────────────────┤
│  ┌────────────────────────┐  │
│  │    BANNER PROMOCIONAL  │  │  ← Banner
│  └────────────────────────┘  │
├──────────────────────────────┤
│ CATEGORÍAS:                   │
│ [Pollos] [Alas] [Combos]     │  ← Categories
├──────────────────────────────┤
│ PRODUCTOS:                    │
│ ┌──────┐ ┌──────┐            │
│ │ 🍗   │ │ 🍗   │            │  ← Products Grid
│ │ $5.0 │ │ $8.0 │            │
│ └──────┘ └──────┘            │
├──────────────────────────────┤
│ [🏠] [🔍] [🛒:3] [📦] [👤]    │  ← BottomBar
└──────────────────────────────┘
```

**Componentes**:
- `TopBar.kt` - Información del usuario y puntos
- `Banner.kt` - Banner promocional
- `CategorySection.kt` - Categorías de productos
- `PointsCard.kt` - Tarjeta de puntos (en TopBar)

---

### 3. Carrito (CartActivity)
**Ubicación**: `presentation/activity/cart/CartActivity.kt`

Gestión del carrito de compras.

```
┌──────────────────────────────┐
│ [←] Mi Carrito          [🗑️] │  ← Header
├──────────────────────────────┤
│ ┌────────────────────────┐   │
│ │ 🍗 Pollo Crujiente     │   │
│ │ $5.00 x2 = $10.00      │   │  ← Items
│ │ Cantidad: 2            │   │
│ │         [✕]            │   │
│ └────────────────────────┘   │
│                              │
│ ┌────────────────────────┐   │
│ │ 🍗 Alitas BBQ          │   │
│ │ $3.00 x1 = $3.00       │   │
│ │ Cantidad: 1            │   │
│ │         [✕]            │   │
│ └────────────────────────┘   │
├──────────────────────────────┤
│ Subtotal (3 items): $13.00   │  ← Footer
│ Envío: $0.00                 │
│ 💎 Puntos disponibles: 100   │
│ ────────────────────────     │
│ Total: $13.00                │
│                              │
│ [Proceder al Pago]  [Cont.]  │
└──────────────────────────────┘
```

---

### 4. Checkout (CheckoutActivity)
**Ubicación**: `presentation/activity/checkout/CheckoutActivity.kt`

Confirmación y pago.

```
┌──────────────────────────────┐
│ [←] Confirmar Compra         │
├──────────────────────────────┤
│ ┌─────────────────────────┐  │
│ │ RESUMEN DE COMPRA       │  │
│ │ 🍗 Pollo x2 = $10.00    │  │
│ │ 🍗 Alitas x1 = $3.00    │  │
│ │ Total: $13.00           │  │
│ └─────────────────────────┘  │
├──────────────────────────────┤
│ ┌─────────────────────────┐  │
│ │ 💎 Usar Puntos          │  │  ← Dialog (v3.5)
│ │                         │  │
│ │ Tienes 100 puntos       │  │
│ │ Valor: $1.00            │  │
│ │                         │  │
│ │ ¿Deseas usar puntos?    │  │
│ │                         │  │
│ │ [Sí] [No]               │  │
│ └─────────────────────────┘  │
├──────────────────────────────┤
│ MÉTODO DE PAGO:              │
│ [💳 Tarjeta] [💎 Puntos]    │
├──────────────────────────────┤
│ Tarjeta:                     │
│ [4532 1234 5678 9010]        │
│ JOHN DOE                     │
│ 12/25  [123]                 │
├──────────────────────────────┤
│ [Confirmar Pago] [Cancelar]  │
└──────────────────────────────┘
```

**Features (v3.5)**:
- Dialog automático de puntos
- Opción de pago con puntos
- Información de descuento
- Validación de datos

---

### 5. Confirmación de Compra (ConfirmationScreen)
**Ubicación**: `presentation/activity/checkout/ConfirmationScreen.kt`

Confirmación exitosa.

```
┌──────────────────────────────┐
│                              │
│      ✅ ¡EXITOSO!            │
│                              │
│ Order ID: ORD-1718641800     │  ← Número de orden
│                              │
│ ┌──────────────────────────┐ │
│ │ RESUMEN:                 │ │
│ │ 🍗 Pollo x2 = $10.00     │ │
│ │ 🍗 Alitas x1 = $3.00     │ │
│ │                          │ │
│ │ Total pagado: $13.00     │ │
│ │ Método: Tarjeta          │ │
│ │                          │ │
│ │ Puntos generados: +130   │ │
│ └──────────────────────────┘ │
│                              │
│ [Volver] [Ver Detalle]       │
└──────────────────────────────┘
```

---

## 💎 Sistema de Puntos - Flujo Visual (v3.5)

### Acumular Puntos
```
Compra con Tarjeta
       $50.00
         ↓
    × 10% cashback
         ↓
   = 5 puntos nuevos
         ↓
   Se guardan en Firebase
         ↓
   Aparecen en Dashboard
   💎 Mis Puntos: 5 pts
```

### Usar Puntos en Checkout

#### Opción A: Pago Mixto (Puntos + Tarjeta)
```
Usuario tiene: 200 puntos ($2.00)
Total compra: $10.00

    ↓ [Dialog] "¿Usar puntos?"
    ↓ Click: "Sí"
    
    Descuento aplicado: -$2.00
    A pagar con tarjeta: $8.00
    
    ↓ Confirma pago con tarjeta
    
    Puntos después: 0
```

#### Opción B: Compra Cubierta
```
Usuario tiene: 500 puntos ($5.00)
Total compra: $3.00

    ↓ [Dialog] "¿Usar puntos?"
    ↓ Click: "Sí"
    
    ✅ ¡Compra completamente cubierta!
    A pagar con tarjeta: $0.00
    
    ↓ Confirma pago (sin tarjeta)
    
    Puntos después: 0
```

---

## 🔄 Flujo Completo de Compra

```
┌─────────────────────────────────┐
│  1. DASHBOARD                   │
│     Explorar productos          │
│     Ver puntos: 500 pts         │
└──────────────┬──────────────────┘
               │
┌──────────────▼──────────────────┐
│  2. CARRITO                     │
│     Agregar productos           │
│     Ver total: $13.00           │
│     Ver puntos disponibles: 500 │
└──────────────┬──────────────────┘
               │
┌──────────────▼──────────────────┐
│  3. CHECKOUT (v3.5)             │
│     💎 Dialog automático        │
│     "¿Usar 500 puntos?"         │
│     ├─ Sí → Puntos como descto  │
│     └─ No → Pagar solo tarjeta  │
└──────────────┬──────────────────┘
               │
┌──────────────▼──────────────────┐
│  4. PAGO                        │
│     Tarjeta: 4532...            │
│     Confirmar pago              │
└──────────────┬──────────────────┘
               │
┌──────────────▼──────────────────┐
│  5. CONFIRMACIÓN                │
│     ✅ Exitoso                  │
│     Order: ORD-XXX              │
│     Puntos gastados: -500       │
│     Puntos después: 0           │
└──────────────┬──────────────────┘
               │
┌──────────────▼──────────────────┐
│  6. DASHBOARD (actualizado)     │
│     Puntos: 0 (se gastan todos) │
│     Próxima compra: Gana pts    │
└─────────────────────────────────┘
```

---

## 🗂️ Estructura de Carpetas Visualizada

```
app/src/main/
│
├─ java/com/daniel/chickenfood/
│  ├─ presentation/
│  │  ├─ activity/
│  │  │  ├─ dashboard/
│  │  │  │  ├─ MainActivity.kt           🎬 Pantalla principal
│  │  │  │  ├─ TopBar.kt                 👤 Barra superior
│  │  │  │  ├─ Banner.kt                 📢 Banner promo
│  │  │  │  ├─ CategorySection.kt        🏷️  Categorías
│  │  │  │  └─ PointsCard.kt             💎 Tarjeta de puntos
│  │  │  │
│  │  │  ├─ cart/
│  │  │  │  └─ CartActivity.kt           🛒 Carrito
│  │  │  │
│  │  │  └─ checkout/
│  │  │     ├─ CheckoutActivity.kt       💳 Checkout
│  │  │     ├─ CheckoutScreen.kt         🧾 UI del checkout
│  │  │     └─ ConfirmationScreen.kt     ✅ Confirmación
│  │  │
│  │  └─ viewModel/
│  │     ├─ RewardsViewModel.kt          💎 Lógica de puntos
│  │     └─ OrderViewModel.kt            📦 Lógica de órdenes
│  │
│  ├─ domain/
│  │  ├─ model/
│  │  │  ├─ UserRewardsModel.kt          💎 Modelo de puntos
│  │  │  ├─ OrderModel.kt                📦 Modelo de orden
│  │  │  └─ PointsTransactionModel.kt    📝 Transacciones
│  │  │
│  │  └─ reposity/
│  │     └─ RewardsRepository.kt         💎 Interface
│  │
│  ├─ data/
│  │  ├─ repository/
│  │  │  └─ RewardsRepositoryImpl.kt      💎 Implementación
│  │  │
│  │  └─ service/
│  │     └─ MockPaymentService.kt        💳 Pagos
│  │
│  ├─ di/
│  │  └─ AppModule.kt                    ⚙️  Inyección
│  │
│  └─ helper/
│     ├─ AuthHelper.kt                   🔐 Autenticación
│     ├─ ManagmentCart.kt                🛒 Carrito
│     └─ RewardsHelper.kt                💎 Puntos
│
└─ res/
   ├─ drawable/
   │  ├─ logo.png                        🍗 Logo
   │  ├─ pollo.png                       🍗 Pollo
   │  └─ ic_launcher_logo.xml            🎯 Launcher
   │
   ├─ mipmap-*/
   │  └─ ic_launcher_pollo.webp          🍗 App icon
   │
   └─ values/
      └─ colors.xml                      🎨 Colores

```

---

## 🎨 Colores Principales

| Color | Código | Uso |
|-------|--------|-----|
| Marrón Oscuro | `#6B4423` | Fondo principal |
| Naranja | `#FF7A00` | Botones, acentos |
| Blanco | `#FFFFFF` | Texto, tarjetas |
| Gris | `#CCCCCC` | Bordes, separadores |
| Verde | `#4CAF50` | Éxito, confirmación |
| Rojo | `#FF6B6B` | Error, eliminar |
| Amarillo | `#FFF9E6` | Alerta, info puntos |

---

## 🧾 Recursos Visuales Disponibles

### Imágenes
- `drawable/logo.png` - Logo principal
- `drawable/pollo.png` - Imagen de pollo
- `drawable/intro_pic.png` - Introducción
- `drawable/banner.png` - Banner promocional
- `mipmap-*/ic_launcher_pollo.webp` - Icono de la app

### Iconos
- `drawable/home_icon.xml` - Inicio
- `drawable/search.xml` - Búsqueda
- `drawable/cart.png` - Carrito
- `drawable/orders_icon.xml` - Órdenes
- `drawable/profile_icon.xml` - Perfil
- `drawable/heart_icon.xml` - Favoritos

---

## 📊 Información Visual de Datos

### Tarjeta de Puntos
```
┌─────────────────────┐
│ 💎 Mis Puntos       │
│ 500 pts             │
│ = $5.00             │
│                     │
│ ████████░░░░░░░░░░ │ (Progress)
│                     │
│ Próximo nivel: 600  │
└─────────────────────┘
```

### Badge de Carrito
```
┌───┐
│🛒3│  ← Badge con número de items
└───┘
```

### Dialog de Puntos (v3.5)
```
┌──────────────────────────────┐
│ 💎 Usar Puntos Acumulados    │
├──────────────────────────────┤
│ Tienes 500 puntos            │
│ Valor: $5.00                 │
│                              │
│ ¿Deseas usar tus puntos?     │
├──────────────────────────────┤
│ [Sí, Usar Puntos][No,Tarjeta]│
└──────────────────────────────┘
```

---

## 🔐 Componentes de Seguridad (Visuales)

### Validación en Formularios
```
[4532 1234 5678 9010]  ← 16 dígitos ✅
JOHN DOE               ← Válido ✅
12/25                  ← Formato MM/YY ✅
[123]                  ← 3 dígitos ✅
```

### Estados de Error
```
┌──────────────────────────────┐
│ ❌ Error                     │
│ Por favor verifica los datos │
│ de la tarjeta                │
└──────────────────────────────┘
```

---

## 🎬 Transiciones de Pantalla

```
SplashActivity
    ↓ ¿Autenticado?
    ├─ Sí → MainActivity (Dashboard)
    └─ No → LoginActivity
         ↓
         LoginActivity
         ├─ [Login] → MainActivity
         └─ [Signup] → SignUpActivity
             ↓
             SignUpActivity → LoginActivity → MainActivity

MainActivity
├─ Click Producto → DetailActivity
├─ Click Búsqueda → SearchActivity
├─ Click Carrito → CartActivity
├─ Click Órdenes → OrdersActivity
└─ Click Perfil → ProfileActivity
    ↓
    CartActivity
    └─ Click "Proceder al Pago" → CheckoutActivity
        ↓
        CheckoutActivity
        ├─ Dialog: "¿Usar puntos?"
        ├─ Selecciona método de pago
        └─ Click "Confirmar" → ConfirmationScreen
            ↓
            ConfirmationScreen
            └─ Click "Volver" → MainActivity
```

---

## 📱 Responsive Design

### Dispositivos Soportados
- ✅ Phones (4.7" - 6.8")
- ✅ Tablets (7" - 12")
- ✅ Landscape/Portrait

### Breakpoints
```
Small:  < 540 dp
Medium: 540 - 900 dp
Large:  > 900 dp
```

---

**Última Actualización**: 17 de Junio, 2024  
**Versión**: 3.5 ✅

