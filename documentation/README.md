# 📚 Documentación de ChickenFood

Bienvenido a la documentación de la aplicación ChickenFood. Esta documentación está organizadapor el tipo de usuario y las actividades que realiza en la app.

## 📖 Índice Principal

### 🔐 Autenticación

- **[Flujo de Autenticación](./01_AUTENTICACION.md)** - Cómo funciona el login con Google Sign-In

### 👤 Usuarios

#### 1. Usuario No Premium (Sin Autenticación)
- **[Guía del Usuario No Premium](./02_USUARIO_NO_PREMIUM.md)** - Actividades y funciones disponibles

#### 2. Usuario Premium (Con Autenticación)
- **[Guía del Usuario Premium](./03_USUARIO_PREMIUM.md)** - Todas las actividades, métodos y características

### 💳 Sistema de Recompensas

- **[Sistema de Puntos](./04_SISTEMA_RECOMPENSAS.md)** - Cómo funciona el cashback 10% y niveles

### 🔧 Configuración y Solución de Problemas

- **[Configuración Inicial](./05_CONFIGURACION_INICIAL.md)** - Pasos para configurar la app
- **[Solución de Errores](./06_SOLUCION_ERRORES.md)** - Errores comunes y soluciones

### 👨‍💻 Para Desarrolladores

- **[Arquitectura del Proyecto](./ARQUITECTURA.md)** - Estructura del código y patrones
- **[API Reference](./API_REFERENCE.md)** - Métodos y clases principales

---

## 📱 Flujo General de la Aplicación

```
┌─────────────────────────────────────┐
│     SplashActivity (Bienvenida)     │
└─────────────────────────────────────┘
           ↓
    ┌──────┴──────┐
    ↓             ↓
  SIN AUTH      CON AUTH (Google)
    ↓             ↓
┌─────────┐  ┌──────────────┐
│Dashboard│  │SignUpActivity│
│(Usuario │  │ (Autenticación
│  NO     │  │  con Google) │
│Premium) │  └──────────────┘
└─────────┘        ↓
    ↓         ┌──────────────┐
    │         │  Dashboard   │
    │         │ (Usuario     │
    │         │  Premium)    │
    │         └──────────────┘
    │              ↓
    └──────┬───────┘
           ↓
    ┌─────────────┐
    │ Ver Comidas │
    └─────────────┘
           ↓
    ┌──────────────┐
    │ Seleccionar  │
    │   Comida     │
    └──────────────┘
           ↓
    ┌──────────────┐
    │Ver Detalle &│
    │Agregar Carrito
    └──────────────┘
           ↓
    ┌──────────────┐
    │  Ver Carrito │
    └──────────────┘
           ↓
         (Premium)
           ↓
    ┌──────────────┐
    │  Usar Puntos │
    │  (Descuento) │
    └──────────────┘
           ↓
    ┌──────────────┐
    │ Confirmar    │
    │  Compra      │
    └──────────────┘
```

---

## 🎯 Guía Rápida

### Quiero saber...

- **Cómo un usuario se autentica** → Ver [Flujo de Autenticación](./01_AUTENTICACION.md)
- **Qué puede hacer un usuario sin login** → Ver [Guía del Usuario No Premium](./02_USUARIO_NO_PREMIUM.md)
- **Qué puede hacer un usuario con login** → Ver [Guía del Usuario Premium](./03_USUARIO_PREMIUM.md)
- **Cómo funcionan los puntos** → Ver [Sistema de Recompensas](./04_SISTEMA_RECOMPENSAS.md)
- **Cómo configurar la app** → Ver [Configuración Inicial](./05_CONFIGURACION_INICIAL.md)
- **Error en autenticación** → Ver [Solución de Errores](./06_SOLUCION_ERRORES.md)
- **Arquitectura del código** → Ver [Arquitectura del Proyecto](./ARQUITECTURA.md)

---

## 📊 Estado del Proyecto

**Completado:** 75%

✅ Autenticación con Google Sign-In
✅ Sistema de recompensas con 10% cashback
✅ UI responsiva
✅ Gestión de tokens
⏳ UI para mostrar puntos (próximamente)
⏳ Canje de puntos en carrito (próximamente)

---

## 🔄 Próximos Pasos

1. Resolver ApiException: 10 en Google Sign-In (Ver [Solución de Errores](./06_SOLUCION_ERRORES.md))
2. Crear UI para mostrar puntos en Dashboard
3. Integrar canje de puntos en carrito
4. Crear pantalla de historial de transacciones

---

## 📞 Soporte

Si tienes preguntas o encuentras errores, consulta:
1. [Solución de Errores](./06_SOLUCION_ERRORES.md)
2. [Preguntas Frecuentes](#)

---

**Última actualización:** 2026-06-01
**Versión:** 1.0

