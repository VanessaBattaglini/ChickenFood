# Debug: Splash Screen abre SignUpActivity

## 🔍 Problema

Al abrir la app, se abre SignUpActivity (con Google Sign-In) en lugar de SplashActivity.

## 🔧 Solución

Se han agregado logs en SplashActivity para debuggear el problema:

```kotlin
private fun navigateToDashboard() {
    Log.d("SplashActivity", "Navegando al Dashboard")
    val intent = Intent(this, MainActivity::class.java)
    startActivity(intent)
    finish()
}

private fun navigateToSignUp() {
    Log.d("SplashActivity", "Navegando a SignUpActivity")
    val intent = Intent(this, SignUpActivity::class.java)
    startActivity(intent)
    finish()
}
```

## 📋 Pasos para Debuggear

1. Compilar:
   ```bash
   ./gradlew clean build
   ```

2. Ejecutar:
   ```bash
   ./gradlew installDebug
   ```

3. Abrir Logcat en Android Studio:
   - View → Tool Windows → Logcat

4. Filtrar por "SplashActivity":
   - En el campo de búsqueda, escribe: `SplashActivity`

5. Abrir la app y observar los logs:
   - Si ves "Navegando a SignUpActivity", significa que algo está llamando a `onSignUpClick`
   - Si ves "Navegando al Dashboard", significa que algo está llamando a `onGetStartedClick`
   - Si no ves ninguno de estos logs, significa que SplashActivity no se está abriendo

## 🎯 Posibles Causas

1. **Hay un Intent que está siendo lanzado automáticamente**
   - Buscar en el código si hay algo que esté lanzando un Intent a SignUpActivity

2. **GetStartedButtons está llamando a onSignUpClick automáticamente**
   - Revisar el código de GetStartedButtons para ver si está llamando a onSignUpClick sin que el usuario haga clic

3. **Hay un código que está navegando automáticamente**
   - Revisar si hay algo en SplashActivity o en BaseActivity que esté navegando automáticamente

## 📝 Próximos Pasos

1. Compilar y ejecutar
2. Abrir Logcat
3. Observar los logs
4. Identificar cuál es el problema
5. Reportar los logs para debuggear

## ✨ Conclusión

Se han agregado logs para debuggear el problema. Ahora puedes ver exactamente qué está pasando cuando abres la app.

**Estado**: 🔍 DEBUGGING
