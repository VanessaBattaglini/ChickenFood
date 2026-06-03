# Integración de TokenRepository en AppModule

## ✅ Completado

Se ha integrado exitosamente `TokenRepository` en el módulo de inyección de dependencias (AppModule).

## Cambios Realizados

### 1. AppModule.kt - Actualizado

Se agregaron las siguientes líneas:

```kotlin
// Importaciones
import com.daniel.chickenfood.data.repository.TokenRepositoryImpl
import com.daniel.chickenfood.domain.reposity.TokenRepository
import com.daniel.chickenfood.presentation.viewModel.TokenViewModel

// En el módulo
single<TokenRepository> { TokenRepositoryImpl(get(), get()) }
viewModel { TokenViewModel(tokenRepository = get()) }
```

**Qué hace:**
- `single<TokenRepository>`: Crea una instancia única de TokenRepositoryImpl que se reutiliza en toda la app
- `viewModel { TokenViewModel(...) }`: Crea TokenViewModel con TokenRepository inyectado automáticamente

### 2. TokenViewModel.kt - Creado

Nuevo ViewModel que encapsula toda la lógica de gestión de tokens:

```kotlin
class TokenViewModel(
    private val tokenRepository: TokenRepository
) : ViewModel()
```

**Métodos disponibles:**
- `saveUserToken(token: UserTokenModel)` - Guardar token después de autenticación
- `getUserToken(userId: String)` - Obtener token guardado
- `updateFirebaseToken(userId, firebaseToken, refreshToken)` - Actualizar token
- `isTokenValid(userId: String)` - Verificar si token es válido
- `getActiveTokens(userId: String)` - Obtener tokens de múltiples dispositivos
- `logout(userId: String)` - Eliminar tokens (logout)
- `revokeToken(userId, tokenId)` - Revocar token específico

**Estados disponibles:**
- `saveTokenState` - Estado de guardado
- `getUserTokenState` - Estado de obtención
- `updateTokenState` - Estado de actualización
- `isTokenValidState` - Estado de validación
- `activeTokensState` - Estado de tokens activos
- `logoutState` - Estado de logout

## Cómo Usar en SignUpActivity

### Opción 1: Con Compose (Recomendado)

```kotlin
import androidx.lifecycle.viewmodel.compose.viewModel
import org.koin.androidx.compose.koinViewModel

@Composable
fun SignUpScreen() {
    val tokenViewModel: TokenViewModel = koinViewModel()
    val saveTokenState by tokenViewModel.saveTokenState.collectAsState()
    
    // Usar el estado
    when (saveTokenState) {
        is TokenState.Loading -> {
            CircularProgressIndicator()
        }
        is TokenState.Success -> {
            Text("Token guardado exitosamente")
        }
        is TokenState.Error -> {
            Text("Error: ${(saveTokenState as TokenState.Error).message}")
        }
        else -> {}
    }
}
```

### Opción 2: En Activity (Tradicional)

```kotlin
import org.koin.androidx.viewmodel.ext.android.viewModel

class SignUpActivity : BaseActivity() {
    private val tokenViewModel: TokenViewModel by viewModel()
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        
        // Observar cambios
        lifecycleScope.launch {
            tokenViewModel.saveTokenState.collect { state ->
                when (state) {
                    is TokenState.Loading -> {
                        // Mostrar loading
                    }
                    is TokenState.Success -> {
                        // Token guardado
                    }
                    is TokenState.Error -> {
                        // Mostrar error
                    }
                    else -> {}
                }
            }
        }
    }
}
```

## Flujo Completo: Autenticación → Guardar Token

### Paso 1: Usuario se autentica con Google

```kotlin
private fun firebaseAuthWithGoogle(idToken: String) {
    val credential = GoogleAuthProvider.getCredential(idToken, null)
    firebaseAuth.signInWithCredential(credential)
        .addOnCompleteListener(this) { task ->
            if (task.isSuccessful) {
                val user = firebaseAuth.currentUser
                
                // Obtener Firebase token
                user?.getIdToken(false)?.addOnCompleteListener { tokenTask ->
                    if (tokenTask.isSuccessful) {
                        val firebaseToken = tokenTask.result?.token ?: ""
                        
                        // Crear modelo de token
                        val userToken = UserTokenModel(
                            userId = user.uid,
                            email = user.email ?: "",
                            googleIdToken = idToken,
                            firebaseToken = firebaseToken,
                            refreshToken = "", // Firebase lo proporciona internamente
                            displayName = user.displayName ?: "",
                            photoUrl = user.photoUrl?.toString() ?: "",
                            createdAt = System.currentTimeMillis(),
                            lastLoginAt = System.currentTimeMillis(),
                            expiresAt = 0L, // Firebase maneja expiración
                            isActive = true
                        )
                        
                        // Guardar token usando ViewModel
                        tokenViewModel.saveUserToken(userToken)
                    }
                }
            }
        }
}
```

### Paso 2: Observar resultado

```kotlin
lifecycleScope.launch {
    tokenViewModel.saveTokenState.collect { state ->
        when (state) {
            is TokenState.Success -> {
                Log.d(TAG, "Token guardado, navegando al dashboard")
                navigateToDashboard()
            }
            is TokenState.Error -> {
                Log.e(TAG, "Error guardando token: ${state.message}")
                showErrorDialog(state.message)
            }
            else -> {}
        }
    }
}
```

## Estructura de Datos Guardada en Firebase

```
users/{userId}/tokens/{tokenId}/
├── userId: "abc123xyz"
├── email: "user@gmail.com"
├── googleIdToken: "eyJhbGc..."
├── firebaseToken: "eyJhbGc..."
├── refreshToken: "eyJhbGc..."
├── displayName: "John Doe"
├── photoUrl: "https://..."
├── createdAt: 1717000000000
├── lastLoginAt: 1717000000000
├── expiresAt: 0
└── isActive: true
```

## Ventajas de esta Arquitectura

✅ **Separación de responsabilidades**
- TokenRepository: Acceso a datos
- TokenViewModel: Lógica de negocio
- Activity: UI

✅ **Reutilizable**
- Cualquier Activity/Fragment puede usar TokenViewModel

✅ **Testeable**
- Fácil de mockear TokenRepository para tests

✅ **Reactive**
- StateFlow para observar cambios en tiempo real

✅ **Manejo de errores**
- Estados claros para éxito, error y loading

✅ **Soporte multi-dispositivo**
- Métodos para obtener y revocar tokens específicos

## Próximos Pasos

1. **Actualizar SignUpActivity** para usar TokenViewModel
2. **Crear UI para mostrar puntos** en Dashboard
3. **Integrar canje de puntos** en Carrito
4. **Implementar renovación automática** de tokens
5. **Agregar validación de tokens** en SplashActivity

## Verificación

✅ Código compila sin errores
✅ TokenRepository integrado en AppModule
✅ TokenViewModel creado con todos los métodos
✅ Estados bien definidos
✅ Documentación completa

