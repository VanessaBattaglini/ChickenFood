# 🍗 ChickenFood App - Documentación

**Última actualización**: 2 de Junio, 2026  
**Estado actual**: ✅ Completamente funcional

---

## 📚 Índice de Documentación

### Guías Principales

1. **[01_INICIO_RAPIDO.md](01_INICIO_RAPIDO.md)** 🚀
   - Cómo empezar a usar la app
   - Flujo principal de usuario
   - Pasos básicos

2. **[02_AUTENTICACION.md](02_AUTENTICACION.md)** 🔐
   - Google Sign-In passwordless
   - Gestión de tokens
   - Login/Logout

3. **[03_BUSCADOR.md](03_BUSCADOR.md)** 🔍
   - Buscar productos en tiempo real
   - Navegación a detalle desde búsqueda
   - Ejemplos de búsqueda

4. **[04_CARRITO_COMPRAS.md](04_CARRITO_COMPRAS.md)** 🛒
   - Agregar/eliminar productos
   - Gestión de cantidades
   - Checkout

5. **[05_SISTEMA_PUNTOS.md](05_SISTEMA_PUNTOS.md)** 💰
   - Cashback 10% por compra
   - Niveles de usuario
   - Canjear puntos

6. **[06_ARQUITECTURA_TECNICA.md](06_ARQUITECTURA_TECNICA.md)** 🏗️
   - Estructura del proyecto
   - ViewModels y Repositories
   - Firebase integration
   - Flujos de datos

7. **[07_CONFIGURACION_INICIAL.md](07_CONFIGURACION_INICIAL.md)** ⚙️
   - Configurar Firebase
   - Obtener SHA-1
   - Google Cloud Console setup

8. **[08_SOLUCION_ERRORES.md](08_SOLUCION_ERRORES.md)** ⚠️
   - Errores comunes
   - Soluciones paso a paso
   - Debugging

---

## 🎯 Funcionalidades Principales

### Para Usuario No Autenticado
- ✅ Ver productos por categoría
- ✅ Buscar productos
- ✅ Ver detalle de productos
- ✅ Agregar al carrito
- ✅ Ver carrito
- ✅ Checkout (sin recompensas)

### Para Usuario Autenticado
- ✅ Todo lo anterior, más:
- ✅ Ver saldo de puntos
- ✅ Ver nivel de usuario (Regular/Bronce/Plata/Oro/Platino)
- ✅ Accumular 10% cashback por compra
- ✅ Canjear puntos por descuentos

---

## 📱 Pantallas Principales

```
SplashScreen (Empecemos / Inscribete)
    ↓
┌─────────────────────────────────┐
│  Dashboard (Home)               │
├─────────────────────────────────┤
│ • SearchBar (buscador)          │
│ • PointsCard (mis puntos)       │
│ • Banner (promociones)          │
│ • Categorías (productos)        │
└─────────────────────────────────┘
    ↓
┌─ CategoryList ────────────────┐
│ Productos por categoría        │
└───────────────────────────────┘
    ↓
┌─ DetailScreen ────────────────┐
│ Detalle del producto           │
│ Botón: Agregar al Carrito     │
└───────────────────────────────┘
    ↓
┌─ CartScreen ──────────────────┐
│ Productos en el carrito        │
│ Botón: Proceder al Pago        │
└───────────────────────────────┘
```

---

## 🔄 Flujos Principales

### Flujo 1: Buscar Producto
```
Dashboard → Escribir en SearchBar → Ver resultados → 
Tocar resultado → DetailScreen
```

### Flujo 2: Comprar Producto
```
Dashboard → Categoría → DetailScreen → 
Agregar al carrito → CartScreen → Pagar → 
Gana 10% puntos
```

### Flujo 3: Usar Puntos
```
Compra 1: $100 → Gana 10 puntos (Regular)
Compra 2: $100 → Gana 12 puntos (Bronce)
Compra 3: $100 → Gana 15 puntos (Platino)
...
Total: 37 puntos = $0.37 descuento
```

---

## 🛠️ Stack Tecnológico

- **Lenguaje**: Kotlin
- **UI**: Jetpack Compose
- **Backend**: Firebase Realtime Database
- **Auth**: Google Sign-In
- **DI**: Koin
- **HTTP**: Retrofit (si aplica)
- **Bases de datos locales**: Room (si aplica)

---

## 📊 Estructura del Proyecto

```
app/src/main/java/com/daniel/chickenfood/
├── presentation/
│   ├── activity/
│   │   ├── dashboard/     (MainActivity, SearchBar, PointsCard)
│   │   ├── detailEachFood/
│   │   ├── itemList/
│   │   ├── cart/
│   │   ├── auth/          (SignUpActivity)
│   │   ├── splash/        (SplashActivity)
│   │   └── BaseActivity
│   └── viewModel/         (MainViewModel, RewardsViewModel, etc)
├── domain/
│   ├── model/             (FoodModel, UserRewardsModel, etc)
│   └── repository/        (Interfaces)
├── data/
│   └── repository/        (Implementaciones)
├── helper/                (AuthHelper, ManagmentCart, RewardsHelper)
└── di/                    (AppModule - Koin config)
```

---

## ✅ Estado Actual

| Componente | Estado | Última actualización |
|-----------|--------|---------------------|
| Autenticación | ✅ Funcional | 2 de Junio |
| Búsqueda | ✅ Funcional | 2 de Junio |
| Carrito | ✅ Funcional | 2 de Junio |
| Puntos | ✅ Funcional | 2 de Junio |
| Dashboard | ✅ Funcional | 2 de Junio |

---

## 🚀 Cómo Comenzar

1. **Lee primero**: [01_INICIO_RAPIDO.md](01_INICIO_RAPIDO.md)
2. **Configura Firebase**: [07_CONFIGURACION_INICIAL.md](07_CONFIGURACION_INICIAL.md)
3. **Prueba la app**: Emulador o dispositivo físico
4. **Si tienes errores**: [08_SOLUCION_ERRORES.md](08_SOLUCION_ERRORES.md)

---

## 📞 Contacto

**Proyecto**: ChickenFood Android App  
**Último commit**: 2 de Junio, 2026  
**Desarrollador**: Daniel Alvarado

---

**Nota**: Esta documentación es la versión 2.0, limpia y organizada. Consulta estos documentos para entender cómo funciona cada parte de la app.
