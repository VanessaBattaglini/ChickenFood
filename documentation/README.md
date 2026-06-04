# 🍗 ChickenFood - Documentación Oficial

**Última actualización**: 3 de Junio, 2026  
**Versión**: 2.1 (Con Hotfix Critical)  
**Estado**: ✅ Completamente funcional (Auth State Fixed)

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
- Gestión de tokens (JWT → Firebase)
- Login / Logout
- Modelos: UserTokenModel, SignUpActivity, TokenViewModel

**[UI_CONDICIONADA_AUTENTICACION.md](UI_CONDICIONADA_AUTENTICACION.md)** - UI Basada en Autenticación  
- PointsCard visible solo para usuarios autenticados
- Botón Logout visible solo para autenticados
- Dashboard diferente según auth status

---

### 🛍️ Características Principales

**[03_BUSCADOR.md](03_BUSCADOR.md)** - Búsqueda en Tiempo Real  
- Buscar productos (case-insensitive)
- Resultados dinámicos en dropdown
- Navegación a DetailScreen
- Ejemplos: "pollo", "hamburguesa", "pizza"

**[04_CARRITO_COMPRAS.md](04_CARRITO_COMPRAS.md)** - Gestión del Carrito  
- Agregar productos
- Eliminar completamente (no decremental)
- Limpiar carrito después de pagar
- Cálculo de total

**[05_SISTEMA_PUNTOS.md](05_SISTEMA_PUNTOS.md)** - Cashback y Recompensas  
- 10-15% cashback por compra
- 5 niveles: Regular, Bronce, Plata, Oro, Platino
- Tarjeta de puntos (PointsCard)
- Conversión: 1 punto = $0.01

---

### 🏗️ Técnica y Arquitectura

**[06_ARQUITECTURA_TECNICA.md](06_ARQUITECTURA_TECNICA.md)** - Stack Completo  
- Patrón MVVM
- Inyección con Koin
- Firebase Realtime Database
- Jetpack Compose
- Estructura de carpetas completa

---

### 🐛 Bugs Resueltos

**[FIX_AUTH_STATE_PERSISTENCE.md](FIX_AUTH_STATE_PERSISTENCE.md)** - 🔴 CRÍTICO: Auth State Persistence  
- Problema: PointsCard y Logout visibles para no-autenticados
- Causa: `rememberSaveable` persistía estado viejo
- Solución: Verificación fresca de Firebase
- Status: ✅ RESUELTO
- Ver también: [RESUMEN_HOTFIX_3.md](RESUMEN_HOTFIX_3.md) | [TEST_AUTH_FIX.md](TEST_AUTH_FIX.md)

**[FIX_SEARCH_TO_DETAIL_BUG.md](FIX_SEARCH_TO_DETAIL_BUG.md)** - Fix: NullPointerException en Búsqueda  
- Problema: Mismatch de Intent keys
- Solución: Cambiar key + null validation
- Status: ✅ Resuelto

---

### ⚠️ Solución de Problemas

**[07_SOLUCION_ERRORES.md](07_SOLUCION_ERRORES.md)** - Errores Comunes y Soluciones  
1. ApiException 10 (Google Sign-In)
2. Productos no aparecen
3. Carrito no se limpia
4. Selector Google no aparece
5. Puntos no se actualizan
6. Búsqueda no encuentra
7. App crashea en Detail
8. Logout no funciona
9. PointsCard no aparece
10. Buscador lento

---

## 🎯 Acceso Rápido por Tema

### Para Usuario/QA
- Cómo usar → [01_INICIO_RAPIDO.md](01_INICIO_RAPIDO.md)
- Errores → [07_SOLUCION_ERRORES.md](07_SOLUCION_ERRORES.md)

### Para Desarrollador
- Arquitectura → [06_ARQUITECTURA_TECNICA.md](06_ARQUITECTURA_TECNICA.md)
- Autenticación → [02_AUTENTICACION.md](02_AUTENTICACION.md)
- Buscador → [03_BUSCADOR.md](03_BUSCADOR.md)
- Carrito → [04_CARRITO_COMPRAS.md](04_CARRITO_COMPRAS.md)
- Puntos → [05_SISTEMA_PUNTOS.md](05_SISTEMA_PUNTOS.md)

### Para DevOps/Setup
- Solución de errores → [07_SOLUCION_ERRORES.md](07_SOLUCION_ERRORES.md)
- Arquitectura técnica → [06_ARQUITECTURA_TECNICA.md](06_ARQUITECTURA_TECNICA.md)

---

## 📊 Funcionalidades

### ✅ Completamente Funcionales

- [x] Autenticación Google passwordless
- [x] Ver productos por categoría
- [x] Búsqueda en tiempo real
- [x] Ver detalle de producto
- [x] Agregar/eliminar del carrito
- [x] Calcular total del carrito
- [x] Simular pago (checkout)
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
| Autenticación | ✅ Funcional | 2 de Junio |
| Dashboard | ✅ Funcional | 2 de Junio |
| Búsqueda | ✅ Funcional | 2 de Junio |
| Carrito | ✅ Funcional | 2 de Junio |
| Puntos | ✅ Funcional | 2 de Junio |
| Detalle | ✅ Funcional | 2 de Junio |
| Compilación | ✅ Build Success | 2 de Junio |

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
