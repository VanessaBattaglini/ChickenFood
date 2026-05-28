# 🍗 ChickenFood - Aplicación de E-commerce de Comida Rápida

## 📋 Descripción

ChickenFood es una aplicación Android moderna para comprar comida rápida. Implementa **Clean Architecture** con **MVVM** y utiliza **Jetpack Compose** para la interfaz de usuario.

## 🏗️ Arquitectura

```
┌─────────────────────────────────────────┐
│      PRESENTATION LAYER (UI)            │
│  Activities, Composables, ViewModels    │
└─────────────────────────────────────────┘
                    ↓
┌─────────────────────────────────────────┐
│      DOMAIN LAYER (Lógica de Negocio)   │
│  Interfaces, Modelos, Use Cases         │
└─────────────────────────────────────────┘
                    ↓
┌─────────────────────────────────────────┐
│      DATA LAYER (Fuentes de Datos)      │
│  Repositorios, Firebase, Local Storage  │
└─────────────────────────────────────────┘
```

## 🛠️ Tecnologías Utilizadas

- **Lenguaje**: Kotlin
- **UI Framework**: Jetpack Compose
- **Arquitectura**: Clean Architecture + MVVM
- **Base de Datos**: Firebase Realtime Database
- **Inyección de Dependencias**: Koin
- **Serialización**: Gson
- **Corrutinas**: Kotlin Coroutines
- **Reactive**: Flow & StateFlow

## 📱 Pantallas

1. **SplashActivity**: Pantalla de inicio
2. **MainActivity**: Dashboard con categorías y banners
3. **ItemsListActivity**: Lista de productos por categoría
4. **DetailEachFoodActivity**: Detalle del producto
5. **CartActivity**: Carrito de compras

## 🔄 Flujo de la Aplicación

```
SplashActivity
    ↓
MainActivity (Dashboard)
    ↓ (click en categoría)
ItemsListActivity (Lista de productos)
    ↓ (click en producto)
DetailEachFoodActivity (Detalle + Cantidad)
    ↓ (click "Agregar al carrito")
CartActivity (Carrito de compras)
```

## 📦 Estructura de Carpetas

```
app/src/main/java/com/daniel/chickenfood/
├── ChickenFoodApp.kt
├── di/
│   └── AppModule.kt
├── presentation/
│   ├── activity/
│   │   ├── dashboard/
│   │   ├── itemList/
│   │   ├── detailEachFood/
│   │   ├── cart/
│   │   └── splash/
│   └── viewModel/
├── domain/
│   ├── model/
│   └── reposity/
├── data/
│   └── repository/
└── helper/
```

## 🔌 Inyección de Dependencias (Koin)

```kotlin
val appModule = module {
    single { Gson() }
    single { FirebaseDatabase.getInstance() }
    single<MainRepository> { MainRepositoryImpl(get(), get()) }
    viewModel { MainViewModel(repository = get()) }
}
```

## 📊 Modelos de Datos

### BannerModel
```kotlin
data class BannerModel(
    val image: String
)
```

### CategoryModel
```kotlin
data class CategoryModel(
    val id: Int,
    val imagePath: String,
    val name: String
)
```

### FoodModel
```kotlin
data class FoodModel(
    val id: Int,
    val title: String,
    val price: Int,
    val imagePath: String,
    val categoryId: Int,
    val description: String,
    val star: Double,
    val timeValue: Int,
    val calorie: Int,
    var numberInCart: Int
)
```

## 🎯 Funcionalidades Principales

✅ Ver categorías de productos
✅ Ver lista de productos por categoría
✅ Ver detalle de cada producto
✅ Ajustar cantidad de productos
✅ Agregar productos al carrito
✅ Ver carrito con detalles de compra
✅ Aumentar/disminuir cantidad desde el carrito
✅ Calcular subtotal y total

## 📚 Documentación Completa

Para una documentación detallada sobre:
- Arquitectura Clean Architecture
- Inyección de Dependencias
- Flujo de datos
- Conexiones entre archivos
- Conceptos clave

Ver: **DOCUMENTACION_PROYECTO.md**

## 🚀 Cómo Ejecutar

1. Clonar el repositorio
2. Abrir en Android Studio
3. Sincronizar Gradle
4. Ejecutar en emulador o dispositivo

## 📝 Requisitos

- Android SDK 24+
- Kotlin 1.8+
- Gradle 8.0+

## 👨‍💻 Autor

Daniel Alvarado

## 📄 Licencia

Este proyecto es de código abierto.

---

**Última actualización**: 26 de Mayo de 2026
