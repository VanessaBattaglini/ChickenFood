# 🔧 Solución: Error CallbackFlow Memory Leak

## Error Reportado

```
Excepción en saveUserToken: 'awaitClose { yourCallbackOrListener.cancel() }' 
should be used in the end of callbackFlow block. Otherwise, a callback/listener 
may leak in case of external cancellation.
```

## ¿Qué Significa?

Cuando usas `callbackFlow` en Kotlin coroutines, **DEBES** cerrar correctamente los listeners/callbacks al final, de lo contrario se produce un memory leak.

## ¿Dónde Estaba el Problema?

En `TokenRepositoryImpl.kt`, los métodos que usaban Firebase Task callbacks no tenían `awaitClose` al final:

### ❌ INCORRECTO (Causaba el error)

```kotlin
override fun saveUserToken(token: UserTokenModel): Flow<Boolean> = callbackFlow {
    try {
        val ref = database.getReference("users/${token.userId}/tokens/${UUID.randomUUID()}")
        
        ref.setValue(token).addOnCompleteListener { task ->
            // ...
            close()
        }
    } catch (e: Exception) {
        close(e)
    }
    // ❌ Falta awaitClose aquí!
}
```

### ✅ CORRECTO (Solución)

```kotlin
override fun saveUserToken(token: UserTokenModel): Flow<Boolean> = callbackFlow {
    try {
        val ref = database.getReference("users/${token.userId}/tokens/${UUID.randomUUID()}")
        
        ref.setValue(token).addOnCompleteListener { task ->
            // ...
            close()
        }
    } catch (e: Exception) {
        close(e)
    }
    awaitClose { }  // ✅ Correcto - limpia recursos
}
```

## ¿Qué Hace `awaitClose`?

`awaitClose` es un bloque especial que se ejecuta cuando el Flow se cancela. Sirve para:

1. **Limpiar listeners** - Remover ValueEventListeners de Firebase
2. **Cerrar conexiones** - Liberar recursos
3. **Prevenir memory leaks** - Evitar que los listeners queden en memoria
4. **Manejar cancelación** - Responder cuando el usuario se va de la pantalla

## Ejemplo Completo

### Para Firebase Task (No persistent listener)

```kotlin
override fun saveUserToken(token: UserTokenModel): Flow<Boolean> = callbackFlow {
    try {
        val ref = database.getReference("users/${token.userId}/tokens/${UUID.randomUUID()}")
        
        ref.setValue(token).addOnCompleteListener { task ->
            if (task.isSuccessful) {
                trySend(true).isSuccess
            } else {
                trySend(false).isSuccess
            }
            close()  // Cierra después de la tarea
        }
    } catch (e: Exception) {
        close(e)
    }
    awaitClose { }  // ✅ Necesario para Task callbacks
}
```

### Para Firebase Listener (Persistent listener)

```kotlin
override fun getUserToken(userId: String): Flow<UserTokenModel> = callbackFlow {
    val ref = database.getReference("users/$userId/tokens")
    val listener = object : ValueEventListener {
        override fun onDataChange(snapshot: DataSnapshot) {
            // ...
            trySend(token).isSuccess
        }

        override fun onCancelled(error: DatabaseError) {
            close(error.toException())
        }
    }
    ref.addValueEventListener(listener)
    awaitClose { 
        // ✅ Remover listener cuando se cancela el Flow
        ref.removeEventListener(listener) 
    }
}
```

## Métodos Arreglados

Se arreglaron **6 métodos** en `TokenRepositoryImpl.kt`:

| Método | Cambio |
|--------|--------|
| `saveUserToken` | ✅ Agregado `awaitClose { }` |
| `updateFirebaseToken` | ✅ Agregado `awaitClose { }` |
| `updateLastLogin` | ✅ Agregado `awaitClose { }` |
| `isTokenValid` | ✅ Agregado `awaitClose { }` |
| `deleteUserToken` | ✅ Agregado `awaitClose { }` |
| `revokeToken` | ✅ Agregado `awaitClose { }` |

Los métodos con listeners persistentes ya tenían `awaitClose`:

| Método | Estado |
|--------|--------|
| `getUserToken` | ✅ Ya tenía `awaitClose { ref.removeEventListener(listener) }` |
| `getActiveTokens` | ✅ Ya tenía `awaitClose { ref.removeEventListener(listener) }` |

## Compilación

✅ **BUILD SUCCESSFUL**
✅ **Tiempo:** 15 segundos
✅ **Errores:** 0

## Verificación

Para verificar que el error está resuelto:

1. Abre la app
2. Haz clic en "Inscribete"
3. Selecciona tu cuenta Google
4. Verifica que:
   - No aparezca el error en logs
   - Toast muestre "Autenticación exitosa"
   - Navegue a MainActivity

## Explicación Técnica

### ¿Por Qué Faltaba awaitClose?

Los `callbackFlow` tienen un ciclo de vida:

```
1. Se crea el Flow
   └─ addListener/callback
   
2. Se observa (collect)
   └─ Se envían valores (trySend)
   
3. Se cancela (usuario se va de la pantalla)
   └─ awaitClose se ejecuta
   └─ Cleanup/removeListener
   
4. Se completa
   └─ Flow finaliza
```

Sin `awaitClose`, el step 3 no se ejecuta → memory leak

### ¿Por Qué Algunos Ya Tenían awaitClose?

Los métodos `getUserToken` y `getActiveTokens` usan `addValueEventListener`, que es un listener **persistente** (se queda observando cambios). Estos **REQUIEREN** `awaitClose` para remover el listener.

Los otros métodos usan `Task` callbacks (que se ejecutan una sola vez), pero aún necesitan `awaitClose` para limpiar correctamente.

## Referencia

**Documentación oficial:**
- https://kotlin.github.io/kotlinx.coroutines/kotlinx-coroutines-core/kotlinx.coroutines.flow/callback-flow.html

**Regla de oro:**
> Todo `callbackFlow` DEBE tener `awaitClose` al final para evitar memory leaks

## Resumen

✅ Error: Memory leak en callbackFlow
✅ Causa: Falta `awaitClose` al final
✅ Solución: Agregar `awaitClose { }` a todos los callbackFlow
✅ Resultado: No hay memory leak, compilación exitosa

---

**Fecha de corrección:** 2026-06-02
**Estado:** ✅ RESUELTO

