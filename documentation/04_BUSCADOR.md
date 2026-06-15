# 🔍 Buscador - Búsqueda de Productos en Tiempo Real

**Estado**: ✅ Completamente funcional  
**Tipo**: Búsqueda en tiempo real (sin Enter necesario)  
**Alcance**: Todas las categorías

---

## ¿Cómo Funciona?

### Flujo Simple

```
Usuario escribe en SearchBar
    ↓
SearchBar.onValueChange() activado
    ↓
MainViewModel.searchFoods(query)
    ↓
Busca en TODAS las categorías
    ↓
Filtra coincidencias
    ↓
Muestra resultados en dropdown
    ↓
Usuario toca resultado
    ↓
Abre DetailScreen con ese producto
```

---

## Búsqueda Paso a Paso

### 1. Escribir en SearchBar

```
Dashboard
    ↓
Ver: [🔍 ¿Qué deseas comer?   ✕]
    ↓
Usuario escribe: "p"
    → Se activa searchFoods("p")
    → Se busca en todas las categorías
    → Resultados: Pizza, Pollo, Postre
    ↓
Usuario escribe: "po"
    → Se actualiza searchFoods("po")
    → Resultados: Pollo, Postre (Pizza desaparece)
    ↓
Usuario escribe: "pol"
    → Se actualiza searchFoods("pol")
    → Resultados: Pollo
    ↓
Usuario escribe: "pollo"
    → Se actualiza searchFoods("pollo")
    → Resultados: Pollo Asado, Pollo Frito, Pollo a la Piña
```

---

## Características de Búsqueda

### ✅ Búsqueda Case-Insensitive

Todas estas búsquedas retornan lo mismo:
- "pollo" ✅
- "POLLO" ✅
- "Pollo" ✅
- "PoLLo" ✅

**Implementación**:
```kotlin
food.title.lowercase().contains(query.lowercase())
```

### ✅ Búsqueda en Profundidad

Busca coincidencias en:
1. **Título** del producto
2. **Descripción** del producto

Ejemplo:
- Query: "asado"
- Coincidencias:
  - Título: "Pollo Asado" ✅
  - Descripción: "Pollo marinado al asado" ✅

### ✅ Búsqueda Global

Busca en **TODAS las categorías**:
- Aves: Pollo Asado, Pollo Frito
- Carnes: Res Asada, Cerdo Asado
- Mariscos: Camarones Asados
- Etc...

No importa la categoría, encuentra todo.

---

## Estados de Búsqueda

### 1. Búsqueda Vacía

```
[🔍 ¿Qué deseas comer?   ✕]
```

- No muestra dropdown
- Campo normal
- Botón "X" deshabilitado

### 2. Buscando

```
[🔍 pollo                 ✕]
├─ ⏳ Buscando...
```

- Muestra spinner
- "Buscando..." texto
- Máximo timeout: 10 segundos

### 3. Sin Resultados

```
[🔍 xyz123                ✕]
├─ No encontramos 'xyz123' en nuestro menú
```

- Mensaje rojo amable
- Animación normal

### 4. Con Resultados

```
[🔍 pollo                 ✕]
├─ Pollo Asado          $8.99
├─ Pollo Frito          $7.99
└─ Pollo a la Piña      $9.99
```

- Lista scrolleable
- Altura máxima: 300.dp
- Cada item muestra: nombre + descripción + precio

---

## UI de Resultados

### SearchResultItem

Cada resultado muestra:

```
┌─────────────────────────────────┐
│ Pollo Asado              $8.99  │ ← Clickeable
│ Pollo marinado con es... (truncado)
└─────────────────────────────────┘
```

**Componentes**:
- 🔤 **Nombre**: Título del producto (truncado si es muy largo)
- 📝 **Descripción**: Primeras 50 caracteres + "..."
- 💵 **Precio**: En naranja (R.color.orange)
- 🎯 **Área clickeable**: Todo el item

---

## Ejemplos de Búsqueda

### Ejemplo 1: "Pollo"

```
Query: "pollo"
Busca en todas las categorías

Resultados encontrados (3):
├─ Pollo Asado Tradicional         $8.99
├─ Pollo Frito con Papas           $7.99
└─ Pollo a la Piña con Arroz       $9.99

Usuario toca "Pollo Asado"
    ↓
Abre DetailScreen con ese producto
```

### Ejemplo 2: "Hamburguesa"

```
Query: "hamburguesa"

Resultados encontrados (3):
├─ Hamburguesa Clásica             $6.99
├─ Hamburguesa Premium             $8.99
└─ Hamburguesa Doble con Queso    $10.99
```

### Ejemplo 3: "Pizza"

```
Query: "pizza"

Resultados encontrados (2):
├─ Pizza Margarita                 $7.99
└─ Pizza Pepperoni                 $8.99
```

### Ejemplo 4: "Xyz" (No existe)

```
Query: "xyz"

Resultados: NINGUNO

Muestra: "No encontramos 'xyz' en nuestro menú"
```

### Ejemplo 5: "Asado"

```
Query: "asado"
Busca en TÍTULO y DESCRIPCIÓN

Resultados encontrados (5):
├─ Pollo Asado                     $8.99 (en título)
├─ Res Asada                       $12.99 (en título)
├─ Cerdo Marinado al Asado         $10.99 (en descripción)
├─ Camarones Asados                $15.99 (en título)
└─ Pechuga a la Asadoreña          $9.99 (en descripción)
```

---

## Componentes

### 1. SearchBar.kt

**Ubicación**: `presentation/activity/dashboard/`

**Props**:
```kotlin
fun SearchBar(
    modifier: Modifier = Modifier,
    viewModel: MainViewModel = koinViewModel(),
    onSearchResultClick: (FoodModel) -> Unit = {},
    onSearch: (String) -> Unit = {}
)
```

**Funcionalidad**:
- Campo de texto para búsqueda
- Icono de búsqueda (lupa)
- Botón "X" para limpiar
- Dropdown con resultados

### 2. SearchResultItem.kt

**Componente**:
```kotlin
@Composable
fun SearchResultItem(
    food: FoodModel,
    modifier: Modifier = Modifier,
    onItemClick: () -> Unit = {}
)
```

**Funcionalidad**:
- Muestra un resultado individual
- Clickeable
- Nombre + descripción + precio

### 3. MainViewModel.searchFoods()

**Ubicación**: `presentation/viewModel/`

**Método**:
```kotlin
fun searchFoods(query: String)
```

**Funcionalidad**:
```
1. Si query está vacío → limpiar búsqueda
2. Si query tiene texto:
   a. Cargar todas las categorías
   b. Para cada categoría:
      - Cargar productos de esa categoría
   c. Filtrar por coincidencia en título/descripción
   d. Case-insensitive
   e. Retornar lista filtrada
```

---

## Flujo de Datos

```
SearchBar (UI)
    ↓
user types "pollo"
    ↓
SearchBar.onValueChange()
    ↓
viewModel.searchFoods("pollo")
    ↓
MainViewModel (búsqueda)
    ├─ _isSearching.value = true
    ├─ Busca en Categoría 1: 12 productos
    ├─ Busca en Categoría 2: 8 productos
    ├─ Busca en Categoría 3: 5 productos
    ├─ Total: 25 productos cargados
    ├─ Filtra: "pollo" en título o descripción
    ├─ Resultados: 3 coincidencias
    ├─ _searchResults.value = [3 items]
    └─ _isSearching.value = false
    ↓
SearchBar (observa searchResults)
    ↓
LazyColumn renderiza 3 items
    ↓
Usuario ve resultados
```

---

## Performance

### ⚡ Optimizaciones

1. **Async**: Búsqueda no bloquea UI
2. **Timeout**: 10 segundos máximo por categoría
3. **Lazy Rendering**: LazyColumn solo renderiza items visibles
4. **Height Limit**: Máximo 300.dp para evitar lentitud
5. **Flow**: Coroutines StateFlow para actualizaciones eficientes

### 📊 Casos de Prueba

| Escenario | Tiempo | Resultado |
|-----------|--------|-----------|
| 1-10 resultados | <500ms | ✅ Muy rápido |
| 10-50 resultados | 500-1000ms | ✅ Rápido |
| 50-100 resultados | 1-2s | ✅ Aceptable |
| Sin conexión | Timeout | ⚠️ Mensaje error |

---

## Interactividad

### Limpiar Búsqueda

**Opción 1: Botón X**
```
[🔍 pollo                 ✕]
                           ← Tocar aquí
    ↓
[🔍 ¿Qué deseas comer?   ✕] (Limpiado)
```

**Opción 2: Seleccionar resultado**
```
Usuario toca "Pollo Asado"
    ↓
SearchBar se limpia automáticamente
    ↓
Abre DetailScreen
```

### Seleccionar Resultado

```
Usuario toca "Pollo Asado"
    ↓
SearchResultItem.onItemClick()
    ↓
SearchBar.onSearchResultClick(food)
    ↓
MainActivity.navigateToDetail(food)
    ↓
Intent a DetailEachFoodActivity
    ↓
Pasa FoodModel al detail
```

---

## Validaciones

### ✅ Query Válido

```kotlin
if (query.isBlank()) {
    clearSearch()
    return
}
```

Solo busca si hay texto no vacío.

### ✅ Resultados Vacíos

```kotlin
if (searchResults.isEmpty()) {
    // Mostrar: "No encontramos 'query' en nuestro menú"
}
```

Mensaje amable si no hay coincidencias.

### ✅ Error en Búsqueda

```kotlin
catch (e: Exception) {
    _searchResults.value = emptyList()
    Log.e(TAG, "Error searching: ${e.message}")
}
```

Si hay error, muestra lista vacía y loguea.

---

## Debugging

### Logs

```
D/MainViewModel: Searching foods for query: 'pollo'
D/MainViewModel: Searching across 3 categories
D/MainViewModel:   Loaded 12 foods from category 'Aves'
D/MainViewModel:   Loaded 8 foods from category 'Carnes'
D/MainViewModel:   Loaded 5 foods from category 'Mariscos'
D/MainViewModel: ✅ Search results: 3 items found for 'pollo'
D/MainViewModel:   Result 0: 'Pollo Asado' from category 1
D/MainViewModel:   Result 1: 'Pollo Frito' from category 1
D/MainViewModel:   Result 2: 'Pollo a la Piña' from category 1
D/SearchBar: Selected food: Pollo Asado
```

---

## Limitaciones Actuales

⚠️ **Búsqueda exacta**: Necesita coincidencia exacta en título/descripción  
⚠️ **Sin sugerencias**: No hay auto-complete  
⚠️ **Sin historial**: No guarda búsquedas recientes

---

## Mejoras Futuras

✏️ Búsqueda fonética (encontrar "poyo" cuando busca "pollo")  
✏️ Historial de búsquedas  
✏️ Sugerencias automáticas  
✏️ Búsqueda por categoría  
✏️ Filtros avanzados (precio, rating)

---

**Estado**: ✅ Buscador completamente funcional y en tiempo real
