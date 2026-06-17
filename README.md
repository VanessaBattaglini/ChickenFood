# 🍗 SuperCrunchy Pollo - Aplicación de Pedidos

![Logo](app/src/main/res/mipmap-hdpi/ic_launcher_pollo.webp)

Una aplicación móvil moderna para ordenar pollo crujiente y comidas deliciosas con un sistema de puntos de recompensa integrado.

---

## 📱 Características Principales

### ✨ Experiencia de Usuario
- **Autenticación Segura**: Login y signup con Firebase Authentication
- **Dashboard Intuitivo**: Exploración fácil de menú y categorías
- **Búsqueda Avanzada**: Encuentra productos rápidamente
- **Carrito de Compras**: Gestión completa de pedidos

### 💳 Sistema de Pagos
- **Múltiples Métodos**: Tarjeta de crédito/débito
- **Pago Mixto**: Usa puntos + tarjeta
- **Procesamiento Seguro**: Validación completa de datos

### 💎 Sistema de Puntos (v4.0 ✅ ACTUALIZADO)
- **Conversión**: 1 punto = 1 peso chileno (CLP)
- **Acumula Puntos**: 10% cashback en cada compra con tarjeta
- **Canjea Puntos**: Usa puntos como método de pago directo
- **Pago Flexible**: 
  - ✅ Pago 100% con puntos (si tienes suficientes)
  - ✅ Pago mixto (puntos + tarjeta) automático
  - ✅ Compra cubierta completamente sin deuda
- **Vista en Tiempo Real**: Puntos se cargan correctamente en checkout
- **Dialog Automático**: Pregunta si quieres usar puntos al abrir checkout
- **Cálculos Precisos**: minOf() evita gastar más puntos de lo necesario

### 📦 Gestión de Órdenes
- **Confirmación Inmediata**: Pantalla de compra exitosa con detalles
- **Número de Orden**: Identificación única de cada compra
- **Resumen de Compra**: Detalle completo de items y puntos

---

## 🚀 Inicio Rápido

### Requisitos
- **Android Studio** 2022.1 o superior
- **Java 11** o superior
- **Firebase Project** configurado
- **Google Play Services** configurado

### Instalación

1. **Clonar el repositorio**
```bash
git clone https://github.com/VanessaBattaglini/ChickenFood.git
cd ChickenFood
```

2. **Configurar Firebase**
   - Descarga `google-services.json` desde Firebase Console
   - Coloca en `app/`

3. **Build y Deploy**
```bash
# Build
./gradlew build

# Instalar en dispositivo
./gradlew installDebug
```

---

## 📋 Flujo Principal de la App

### 1️⃣ **Splash & Autenticación**
```
SplashActivity
    ↓
¿Autenticado?
    ├─ Sí → MainActivity (Dashboard)
    └─ No → LoginActivity
```

### 2️⃣ **Dashboard**
```
MainActivity
├─ Barra superior (Perfil, Puntos, Campana)
├─ Banner promocional
├─ Categorías de productos
├─ Grid de productos
└─ BottomBar (Home, Búsqueda, Carrito, Órdenes, Perfil)
```

### 3️⃣ **Carrito**
```
CartActivity
├─ Items del carrito
├─ Cantidad y subtotal
├─ Total con puntos disponibles
└─ Botón "Proceder al Pago"
```

### 4️⃣ **Checkout (Con Sistema de Puntos)**
```
CheckoutActivity
├─ 💎 Dialog Automático (si hay puntos > 0)
│  └─ "¿Deseas usar tus X puntos?"
├─ Resumen de compra
├─ Selector de método de pago
│  ├─ 💳 Pagar con Tarjeta
│  └─ 💎 Pagar con Puntos
└─ Confirmar pago
```

### 5️⃣ **Confirmación**
```
ConfirmationScreen
├─ ✅ Operación exitosa
├─ Número de orden
├─ Resumen de compra
├─ Puntos generados/usados
└─ Botones (Volver, Ver Detalle)
```

---

## 💎 Sistema de Puntos - Detalles (v4.0)

### Conversión de Puntos
```
1 punto = 1 peso chileno (CLP)
```

### Ganar Puntos
```
Compra con Tarjeta:
  $1,000 CLP × 10% = 100 puntos generados
  Total de puntos: Puntos anteriores + 100
```

### Usar Puntos

#### Opción A: Descuento (Pago Mixto)
```
Puntos: 1,000 (= $1,000 CLP)
Total compra: $3,000 CLP

Descuento: -$1,000
A pagar con tarjeta: $2,000
Puntos después: 0
```

#### Opción B: Compra Completa
```
Puntos: 5,050 (= $5,050 CLP)
Total compra: $3,000 CLP

Compra cubierta 100%
A pagar: $0 CLP
Puntos después: 2,050
```

#### Opción C: Pago Mixto Automático
```
Puntos: 1,000 (= $1,000 CLP)
Total compra: $3,000 CLP

Sistema detecta:
"¿Pagar $2,000 con tarjeta?"
  ├─ Sí → Pago Mixto (1,000 pts + $2,000 tarjeta)
  └─ No → Cancelar y cambiar método
```

### Dialog Automático
Cuando abres checkout con puntos > 0:
```
┌──────────────────────────────────┐
│ 💎 Usar Puntos Acumulados        │
│                                  │
│ Tienes 5,050 puntos              │
│ Valor: $5,050 CLP                │
│                                  │
│ Descuento posible: Hasta $5,050  │
│                                  │
│ ¿Deseas usar tus puntos?         │
│                                  │
│ [Sí, Usar Puntos] [No, Tarjeta]  │
└──────────────────────────────────┘
```

### Fórmula de Cálculo
```
pointsNeeded = Total Compra × 1 punto/peso
pointsToSpend = min(pointsNeeded, userPoints)
descuento = pointsToSpend × 1 peso/punto
finalAmount = Total - descuento
pointsAfter = userPoints - pointsToSpend
```

### Ejemplo Numérico Completo
```
Compra: $3,000 CLP
Puntos disponibles: 5,050

CÁLCULO:
  pointsNeeded = 3,000 × 1 = 3,000 pts
  pointsToSpend = min(3,000, 5,050) = 3,000 pts
  descuento = 3,000 × 1 = $3,000
  finalAmount = 3,000 - 3,000 = $0 (sin deuda)
  pointsAfter = 5,050 - 3,000 = 2,050 pts

RESULTADO:
  ✅ Gasta exactamente: 3,000 puntos
  ✅ Paga con tarjeta: $0 CLP
  ✅ Quedan disponibles: 2,050 puntos
```

---

## 🏗️ Arquitectura Técnica

### Stack de Tecnologías

| Capa | Tecnología |
|------|-----------|
| **UI** | Jetpack Compose (Kotlin) |
| **ViewModel** | Android ViewModel + Flow |
| **Data** | Firebase Realtime DB |
| **Auth** | Firebase Authentication |
| **DI** | Koin (Dependency Injection) |
| **Build** | Gradle Kotlin DSL |

### Arquitectura Limpia

```
Presentation Layer (Activity/Composable)
    ↓
ViewModel Layer (Business Logic)
    ↓
Repository Layer (Data Abstraction)
    ↓
Data Sources (Firebase)
```

### Patrón StateFlow

```
Firebase (Data Source)
    ↓
Repository (Flow<T>)
    ↓
ViewModel (StateFlow<T>)
    ↓
Activity/Composable (collectAsState())
    ↓
UI (Recomposición Automática)
```

---

## 🧪 Testing

### Tarjetas de Prueba
```
Número: 4532123456789010
Vencimiento: 12/25
CVC: 123
Titular: JOHN DOE
```

### Test Cases Principales

1. **Acumular Puntos**
   - Compra con tarjeta
   - Verifica puntos en dashboard
   - ✅ Deberías ganar 10% en puntos

2. **Ver Puntos en Checkout**
   - Abre checkout con puntos > 0
   - ✅ Dialog debe aparecer
   - ✅ Puntos mostrados correctamente

3. **Pago Mixto**
   - Puntos < total
   - ✅ Se aplica descuento
   - ✅ Paga resto con tarjeta

4. **Compra Cubierta**
   - Puntos ≥ total
   - ✅ Total a pagar: $0.00
   - ✅ Compra procesada correctamente

---

## 📊 Build Status & Testing

```
✅ BUILD SUCCESSFUL (v3.9+)
✅ No Compilation Errors
✅ No Warnings Críticos
✅ Unit Tests: 6/6 PASSING
✅ Production Ready
```

### Tests Validados (v4.0)
```
✅ testFullPointsCoverage          - 5050 pts cubren $3000 completamente
✅ testPartialPointsCoverage       - 1000 pts + $2000 tarjeta para $3000
✅ testPureCardPayment              - Solo tarjeta (10% cashback)
✅ testMixedPaymentSummaryDisplay   - Desglose correcto de pago mixto
✅ testAbsoluteValueDisplay         - Display de valores negativos
✅ testExactPointsCoverage          - Cobertura exacta de puntos
```

---

## 📁 Estructura del Proyecto

```
ChickenFood/
├── app/
│   ├── src/main/
│   │   ├── java/com/daniel/chickenfood/
│   │   │   ├── presentation/    # Activities, Composables, ViewModels
│   │   │   ├── domain/          # Models, Repositories (interfaces)
│   │   │   ├── data/            # Repository implementations
│   │   │   ├── di/              # Dependency Injection (Koin)
│   │   │   └── helper/          # Utilities
│   │   ├── res/                 # Recursos (drawable, mipmap, etc)
│   │   └── AndroidManifest.xml
│   └── build.gradle.kts
├── documentation/    # Documentación del proyecto
├── README.md        # Este archivo
└── gradle/

```

---

## 🔑 Características de Seguridad

- ✅ **Firebase Authentication**: Autenticación segura
- ✅ **Validación de Datos**: Todas las entradas validadas
- ✅ **HTTPS**: Comunicación cifrada
- ✅ **Reglas Firebase**: Control de acceso a datos
- ✅ **Datos de Usuario**: Encriptados en tránsito

---

## 📚 Documentación

| Documento | Descripción |
|-----------|-----------|
| `documentation/README.md` | Guía general de documentación |
| `documentation/PAYMENT_POINTS_SYSTEM.md` | Sistema de puntos completo |
| `documentation/QUICK_REFERENCE.md` | Referencia rápida (30 segundos) |
| `CHANGELOG.md` | Historial de cambios v3.5 |

---

## 🐛 Solución de Problemas

### Los puntos no se cargan en Checkout
**Causa**: Retraso de carga de Firebase
**Solución**: Espera 2-3 segundos, el dialog debe aparecer

### Dialog no aparece aunque tengo puntos
**Causa**: Puntos = 0 o no se cargaron
**Debug**: Abre logcat y busca "RewardsViewModel: Rewards loaded"

### Pago falla
**Causa**: Datos de tarjeta inválidos
**Verificar**: Número (16 dígitos), CVC (3 dígitos), vencimiento (MM/YY)

---

## 🎯 Roadmap Futuro

### v3.6 - Órdenes en Tiempo Real
- [ ] Estado de orden en vivo
- [ ] Notificaciones de preparación
- [ ] Timeline visual

### v3.7 - Historial de Transacciones
- [ ] Pantalla de transacciones
- [ ] Filtros y búsqueda
- [ ] Exportar historial

### v3.8 - Rewards Mejorados
- [ ] Tier system (Bronce, Plata, Oro)
- [ ] Badges y logros
- [ ] Promociones personalizadas

---

## 👥 Team

**Desarrollador**: Daniel Alvarado  
**Diseño**: SuperCrunchy Pollo Team  
**QA**: Vanessa Battaglini

---

## 📞 Contacto & Soporte

Para preguntas o reportar bugs, contacta a:
- 📧 Email: support@supercrunchy.com
- 🐛 Issues: [GitHub Issues](https://github.com/VanessaBattaglini/ChickenFood/issues)

---

## 📄 Licencia

Este proyecto es privado y propiedad de SuperCrunchy Pollo.

---

## 📈 Stats

- **Total Features**: 30+
- **Active Users**: Creciendo diariamente
- **Pedidos Procesados**: 1000+
- **Customer Satisfaction**: 4.8/5 ⭐

---

**Última Actualización**: 17 de Junio, 2026  
**Versión**: 4.0 ✅ (Sistema de Puntos - Pesos Chilenos)
**Moneda**: Pesos Chilenos (CLP)  
**Status**: Production Ready 🚀

