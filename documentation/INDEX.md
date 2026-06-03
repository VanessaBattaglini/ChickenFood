# 📖 Índice de Documentación - ChickenFood

## 📚 Documentación Esencial

Estos son los archivos que debes leer en orden:

### 1. **README.md** - Punto de partida
Visión general del proyecto y guía de navegación rápida.

### 2. **01_AUTENTICACION.md** - Cómo funciona el login
- Flujo completo de autenticación
- Google Sign-In passwordless
- Guardado de tokens en Firebase

### 3. **02_USUARIO_NO_PREMIUM.md** - Usuario sin autenticación
- Actividades disponibles
- Limitaciones
- Flujo de uso

### 4. **03_USUARIO_PREMIUM.md** - Usuario con autenticación
- Todas las actividades (Premium)
- Métodos utilizados
- Características exclusivas
- Cómo usar puntos

### 5. **04_SISTEMA_RECOMPENSAS.md** - Sistema de puntos
- Cálculo de puntos
- Niveles de usuario
- Canje de puntos
- Estructura en Firebase

### 6. **05_CONFIGURACION_INICIAL.md** - Setup paso a paso
- Requisitos previos
- Configuración de Firebase
- Configuración de Google Sign-In
- Verificación

### 7. **06_SOLUCION_ERRORES.md** - Solucionar problemas
- Errores comunes
- Soluciones paso a paso
- Debugging

---

## 📁 Archivos Adicionales (Referencia)

### Arquitectura y Desarrollo
- **ARQUITECTURA.md** - Estructura del código, patrones y capas
- **API_REFERENCE.md** - Métodos y clases principales

### Diagramas y Visualizaciones
- **DIAGRAMA_JWT_CREDENTIAL.md** - Cómo funciona JWT → Firebase Credential
- **GESTION_TOKENS_GOOGLE.md** - Gestión de tokens Google

---

## 🎯 Guía de Lectura Rápida

**Si quieres:**

- ✅ **Entender cómo funciona la app** → Lee README.md + 01_AUTENTICACION.md
- ✅ **Saber qué puede hacer cada usuario** → Lee 02_USUARIO_NO_PREMIUM.md + 03_USUARIO_PREMIUM.md
- ✅ **Entender el sistema de puntos** → Lee 04_SISTEMA_RECOMPENSAS.md
- ✅ **Configurar la app localmente** → Lee 05_CONFIGURACION_INICIAL.md
- ✅ **Solucionar un error** → Ve a 06_SOLUCION_ERRORES.md
- ✅ **Entender la arquitectura del código** → Lee ARQUITECTURA.md

---

## 📊 Estructura General

```
Documentation/
├── README.md (Inicio aquí)
│
├── Guías de Usuario:
├── 01_AUTENTICACION.md (Flujo de login)
├── 02_USUARIO_NO_PREMIUM.md (Sin auth)
├── 03_USUARIO_PREMIUM.md (Con auth)
├── 04_SISTEMA_RECOMPENSAS.md (Puntos)
│
├── Setup y Configuración:
├── 05_CONFIGURACION_INICIAL.md (Setup)
├── 06_SOLUCION_ERRORES.md (Problemas)
│
├── Para Desarrolladores:
├── ARQUITECTURA.md (Código)
├── API_REFERENCE.md (Métodos)
├── DIAGRAMA_JWT_CREDENTIAL.md (JWT)
├── GESTION_TOKENS_GOOGLE.md (Tokens)
│
└── INDEX.md (Este archivo)
```

---

## 🚀 Flujo Recomendado

### Primera Vez (Setup inicial)
1. README.md (5 min)
2. 05_CONFIGURACION_INICIAL.md (20 min)
3. 01_AUTENTICACION.md (10 min)

### Como Usuario
1. README.md (repaso)
2. 02_USUARIO_NO_PREMIUM.md (si no autenticado)
3. 03_USUARIO_PREMIUM.md (si autenticado)

### Como Desarrollador
1. ARQUITECTURA.md
2. API_REFERENCE.md
3. Los archivos específicos según la función

---

## 📝 Resumen de Contenido

| Documento | Tema | Duración |
|-----------|------|----------|
| README.md | Visión general | 5 min |
| 01_AUTENTICACION.md | Google Sign-In | 10 min |
| 02_USUARIO_NO_PREMIUM.md | Flujo sin auth | 10 min |
| 03_USUARIO_PREMIUM.md | Flujo con auth | 15 min |
| 04_SISTEMA_RECOMPENSAS.md | Sistema de puntos | 10 min |
| 05_CONFIGURACION_INICIAL.md | Setup local | 20 min |
| 06_SOLUCION_ERRORES.md | Solucionar problemas | Por necesidad |
| ARQUITECTURA.md | Código y patrones | 15 min |
| API_REFERENCE.md | Métodos y clases | Por referencia |

---

## 🔄 Estado de la Documentación

✅ **Completa:**
- Autenticación
- Usuario No Premium
- Usuario Premium
- Sistema de Recompensas
- Configuración Inicial
- Solución de Errores

⏳ **Parcial:**
- ARQUITECTURA.md
- API_REFERENCE.md

---

## 💡 Notas Importantes

1. **La documentación está estructurada por actividades del usuario**, no por tecnología
2. **Cada documento incluye métodos específicos** utilizados para cada actividad
3. **Ejemplos prácticos** en todos los documentos
4. **Diagramas visuales** para entender mejor los flujos
5. **Códigos actuales** del proyecto

---

## 🎯 Qué Falta en la Documentación

Estos temas no están documentados porque aún no se han implementado:

- [ ] UI para mostrar puntos en Dashboard
- [ ] UI para canjear puntos en carrito
- [ ] UI para confirmación de orden
- [ ] UI para historial de compras
- [ ] Renovación automática de tokens
- [ ] Validación de tokens en SplashActivity

Ver [CHECKLIST_PROXIMOPASOS.md](../CHECKLIST_PROXIMOPASOS.md) para detalles.

---

## 📞 Necesitas Ayuda?

1. **Primer viaje:**  Lee README.md y 01_AUTENTICACION.md
2. **Quiero hacer una compra:** Lee 02_USUARIO_NO_PREMIUM.md o 03_USUARIO_PREMIUM.md
3. **Quiero entender los puntos:** Lee 04_SISTEMA_RECOMPENSAS.md
4. **Tengo un error:** Ve a 06_SOLUCION_ERRORES.md
5. **Quiero entender el código:** Lee ARQUITECTURA.md

---

**Última actualización:** 2026-06-01
**Versión:** 1.0 (Limpia y Reorganizada)

