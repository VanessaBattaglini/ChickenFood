# 🍗 ChickenFood - Documentación Oficial

**Última actualización**: 12 de Junio, 2026  
**Versión**: 2.2 (Acceso Público a Datos)  
**Estado**: ✅ Completamente funcional (Sin auth requerida para ver productos)

---

## 📚 Índice de Documentación

### 🚀 Comienza Aquí

**[00_INICIO.md](00_INICIO.md)** - Portada y navegación principal  
Versión general de la app, cómo funciona, pantallas principales.

**[01_INICIO_RAPIDO.md](01_INICIO_RAPIDO.md)** - Inicio en 5 minutos  
Aprende rápidamente cómo usar la app con ejemplos prácticos.

---

### 🔐 Autenticación y Seguridad

**[02_AUTENTICACION.md](02_AUTENTICACION.md)** - Google Sign-In Passwordless  
- Google Sign-In sin contraseña
- Login/Logout
- Autenticación opcional: ver productos sin login
- Modelos: UserTokenModel, SignUpActivity, TokenViewModel

**[03_ACCESO_PUBLICO.md](03_ACCESO_PUBLICO.md)** - Acceso Público a Datos ⭐ NUEVO  
- Ver productos, categorías, banners SIN autenticación
- Reglas Firebase para lectura pública
- Comprar requiere autenticación

---

### 🛍️ Características Principales

**[04_BUSCADOR.md](04_BUSCADOR.md)** - Búsqueda en Tiempo Real  
- Buscar productos (case-insensitive)
- Resultados dinámicos en dropdown
- Navegación a DetailScreen
- Funciona sin autenticación

**[05_CARRITO_COMPRAS.md](05_CARRITO_COMPRAS.md)** - Gestión del Carrito  
- Agregar/eliminar productos
- Carrito disponible para todos
- Checkout requiere autenticación
- Cálculo de total

**[06_SISTEMA_PUNTOS.md](06_SISTEMA_PUNTOS.md)** - Cashback y Recompensas  
- 10-15% cashback por compra (solo autenticados)
- 5 niveles: Regular, Bronce, Plata, Oro, Platino
- PointsCard (solo para autenticados)
- Conversión: 1 punto = $0.01

---

### 🏗️ Técnica y Arquitectura

**[07_ARQUITECTURA_TECNICA.md](07_ARQUITECTURA_TECNICA.md)** - Stack Completo  
- Patrón MVVM
- Inyección con Koin
- Firebase Realtime Database + Reglas de Seguridad
- Jetpack Compose
- Estructura de carpetas completa

---

### ⚠️ Solución de Problemas

**[08_SOLUCION_ERRORES.md](08_SOLUCION_ERRORES.md)** - Errores Comunes y Soluciones  
1. ApiException 10 (Google Sign-In)
2. Productos no aparecen
3. Carrito no se limpia
4. Selector Google no aparece
5. Puntos no se actualizan
6. Búsqueda no encuentra

---

## 🎯 Acceso Rápido por Tema

### Para Usuario/QA
- Cómo usar → [01_INICIO_RAPIDO.md](01_INICIO_RAPIDO.md)
- Errores → [08_SOLUCION_ERRORES.md](08_SOLUCION_ERRORES.md)

### Para Desarrollador
- Arquitectura → [07_ARQUITECTURA_TECNICA.md](07_ARQUITECTURA_TECNICA.md)
- Autenticación → [02_AUTENTICACION.md](02_AUTENTICACION.md)
- Acceso Público → [03_ACCESO_PUBLICO.md](03_ACCESO_PUBLICO.md) ⭐ NUEVO
- Buscador → [04_BUSCADOR.md](04_BUSCADOR.md)
- Carrito → [05_CARRITO_COMPRAS.md](05_CARRITO_COMPRAS.md)
- Puntos → [06_SISTEMA_PUNTOS.md](06_SISTEMA_PUNTOS.md)

### Para DevOps/Setup
- Solución de errores → [08_SOLUCION_ERRORES.md](08_SOLUCION_ERRORES.md)
- Arquitectura técnica → [07_ARQUITECTURA_TECNICA.md](07_ARQUITECTURA_TECNICA.md)

---

## 📊 Funcionalidades

### ✅ Completamente Funcionales

#### Públicas (sin autenticación)
- [x] Ver productos por categoría
- [x] Búsqueda en tiempo real
- [x] Ver detalle de producto
- [x] Agregar/eliminar del carrito
- [x] Ver carrito
- [x] Acceder a imágenes desde la base de datos

#### Requieren Autenticación
- [x] Autenticación Google passwordless
- [x] Calcular total y checkout
- [x] Sistema de 10% cashback
- [x] 5 niveles de usuario
- [x] Tarjeta de puntos en Dashboard
- [x] Logout (volver a SplashScreen)

---

## 🏗️ Stack Tecnológico

| Componente | Tecnología |
|-----------|-----------|
| Lenguaje | Kotlin |
| UI Framework | Jetpack Compose |
| Backend | Firebase Realtime Database |
| Autenticación | Google Sign-In + Firebase Auth |
| Inyección | Koin |
| Coroutines | Kotlin Coroutines + Flow |
| Build | Gradle Kotlin DSL |

---

## 📁 Estructura de Proyecto

```
ChickenFood/
├── app/
│   └── src/main/java/com/daniel/chickenfood/
│       ├── presentation/       (UI + ViewModels)
│       ├── domain/            (Modelos + Interfaces)
│       ├── data/              (Implementaciones)
│       ├── helper/            (Utilidades)
│       └── di/                (Koin config)
├── documentation/             (Esta carpeta)
└── build.gradle.kts
```

---

## 🚀 Cómo Empezar

### 1. Primer Viaje
```
→ Lee 00_INICIO.md (portada)
→ Lee 01_INICIO_RAPIDO.md (cómo funciona)
→ Abre la app en emulador
→ Prueba: "Empecemos" y "Inscribete"
```

### 2. Configuración Técnica
```
→ Lee 07_SOLUCION_ERRORES.md (Error ApiException 10)
→ Configura Firebase y Google Cloud
→ Ejecuta app nuevamente
```

### 3. Entender la Arquitectura
```
→ Lee 06_ARQUITECTURA_TECNICA.md
→ Abre el código fuente
→ Ve cómo se conectan ViewModels, Repos, Firebase
```

### 4. Explorar Características
```
→ 03_BUSCADOR.md - Buscar productos
→ 04_CARRITO_COMPRAS.md - Agregar al carrito
→ 05_SISTEMA_PUNTOS.md - Sistema de puntos
```

---

## ✅ Estado Actual

| Componente | Estado | Última actualización |
|-----------|--------|---------------------|
| Acceso Público a Datos | ✅ Funcional | 12 de Junio |
| Autenticación | ✅ Funcional | 12 de Junio |
| Dashboard | ✅ Funcional | 12 de Junio |
| Búsqueda | ✅ Funcional | 12 de Junio |
| Carrito | ✅ Funcional | 12 de Junio |
| Puntos | ✅ Funcional | 12 de Junio |
| Detalle | ✅ Funcional | 12 de Junio |
| Compilación | ✅ Build Success | 12 de Junio |

---

## 📞 Notas Importantes

### Antes de Empezar
- ✅ Necesitas Java 11+ instalado
- ✅ Android Studio 2022.2+
- ✅ Android SDK 31+
- ✅ Cuenta Google
- ✅ Conexión a Internet

### Configuración Inicial
1. Clona el proyecto
2. Abre en Android Studio
3. Configura Firebase (ver 07_SOLUCION_ERRORES.md)
4. Descarga google-services.json
5. Clean build y ejecuta

### Common Issues
- **No aparecen productos**: Verifica Firebase Realtime Database
- **Error Google Sign-In**: Sigue paso a paso en 07_SOLUCION_ERRORES.md
- **App crashea**: Chequea todos los .md en este orden

---

## 📖 Convenciones Usadas

### En esta Documentación

**[Links internos]** → Otros documentos .md  
**Code blocks** → Código Kotlin/XML  
**Logs** → Salidas de logcat  
**├─, └─** → Estructura de carpetas/datos  
**✅, ⚠️, ❌** → Estados e indicadores  

---

## 🎓 Sobre esta Documentación

Esta es la **versión 2.0**, limpia y reescrita completamente:

- ✅ Eliminadas 50+ documentos repetidos y obsoletos
- ✅ Reorganizada por usuario y tema
- ✅ Actualizada con los últimos cambios (Búsqueda, Sistema de Puntos)
- ✅ Incluye solución de 10 errores comunes
- ✅ Estructura clara de inicio a fin

---

**Última revisión**: 2 de Junio, 2026  
**Documentación**: v2.0 (Limpia)  
**Proyecto**: ChickenFood Android App

👉 **Comienza por**: [00_INICIO.md](00_INICIO.md) o [01_INICIO_RAPIDO.md](01_INICIO_RAPIDO.md)
