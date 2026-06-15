# 💳 Sistema de Pagos - Especificación Completa

**Proyecto**: ChickenFood  
**Versión Spec**: 1.0  
**Fecha**: 12 de Junio, 2026  
**Estado**: 📋 En Planificación

---

## 📖 Descripción General

Sistema de pagos para ChickenFood que permite a usuarios autenticados comprar productos usando:

1. **Tarjeta de Crédito** (Stripe)
2. **Puntos Acumulados** (Firebase)
3. **Híbrido** (Tarjeta + Puntos combinados)

---

## 📚 Documentación

### 1. [Requirements](./requirements.md)
Requisitos funcionales y no-funcionales del sistema.

**Incluye**:
- Funcionalidades por método de pago
- Manejo de errores
- Requisitos de seguridad
- Casos especiales
- Flujos de usuario

**Lee esto si**: Necesitas entender QUÉ debe hacer el sistema

---

### 2. [Design](./design.md)
Diseño técnico e implementación.

**Incluye**:
- Arquitectura general
- Componentes en la app
- Flujos de procesamiento detallados
- Estructura de datos en Firebase
- APIs de backend
- Manejo de errores
- Dependencias

**Lee esto si**: Necesitas entender CÓMO implementar el sistema

---

### 3. [Tasks](./tasks.md)
Tareas concretas para desarrollar.

**Incluye**:
- 24 tareas específicas
- Criterios de aceptación
- Dependencias entre tareas
- Estimaciones de tiempo
- Orden recomendado (5 sprints)

**Lee esto si**: Necesitas saber QUÉ tareas hacer y en qué orden

---

## 🎯 Alcance

### ✅ Incluido

- [x] Pago con tarjeta (Stripe)
- [x] Pago con puntos
- [x] Pago híbrido (puntos + tarjeta)
- [x] Validaciones de seguridad
- [x] Manejo de errores
- [x] Historial de órdenes
- [x] Transacciones de puntos

### ❌ Excluido (Futuro)

- [ ] PayPal
- [ ] Otros métodos de pago
- [ ] Wallet digital
- [ ] Criptomonedas
- [ ] Suscripciones

---

## 🏗️ Arquitectura Alta

```
┌─────────────────────────┐
│   ChickenFood App       │
│   (Android + Compose)   │
└────────────┬────────────┘
             │ HTTPS
             ↓
┌─────────────────────────┐
│   Backend (Node.js)     │
│   - Auth validation     │
│   - Pago processing     │
│   - BD operations       │
└────────────┬────────────┘
             │ API
             ↓
    ┌────────────────┐
    │    Stripe      │  ← Procesa tarjetas
    │  (Charges API) │
    └────────────────┘

┌─────────────────────────┐
│   Firebase             │
│   - Órdenes            │
│   - Puntos             │
│   - Transacciones      │
└─────────────────────────┘
```

---

## 🚀 Fases de Desarrollo

### Fase 1: Setup (1 sprint)
- Backend con Node.js + Express
- Configurar Stripe
- Configurar Firebase Admin
- Endpoints base

**Tiempo**: 4-5 horas

---

### Fase 2: Pago con Tarjeta (2 sprints)
- Frontend: CheckoutScreen + CardInput
- Backend: Procesar pago Stripe
- Crear órdenes y agregar puntos

**Tiempo**: 12-14 horas

---

### Fase 3: Pago con Puntos (1 sprint)
- Frontend: UI de puntos
- Backend: Deducir puntos
- Transacciones

**Tiempo**: 5-6 horas

---

### Fase 4: Pago Híbrido (1 sprint)
- Frontend: Selector de cantidad
- Backend: Combinación de lógicas

**Tiempo**: 5-6 horas

---

### Fase 5: Polish (1 sprint)
- Historial de órdenes
- Error recovery
- Testing

**Tiempo**: 4-5 horas

---

**Total**: ~30-35 horas

---

## 📋 Dependencias Externas

### Stripe
- **Qué**: Payment processor
- **Por qué**: Seguro, confiable, cumple PCI compliance
- **Costo**: 2.9% + $0.30 por transacción
- **Setup**: 1-2 horas

### Firebase
- **Qué**: Database + Auth
- **Por qué**: Ya está en el proyecto
- **Costo**: Included (usage-based)
- **Setup**: Ya configurado

### Node.js Backend
- **Qué**: API para procesar pagos
- **Por qué**: Necesario para seguridad (Secret Key Stripe)
- **Hosting**: Digital Ocean, AWS, Heroku, etc.
- **Setup**: 2-3 horas

---

## 🔒 Consideraciones de Seguridad

### Datos de Tarjeta

```
❌ NUNCA:
- Guardar número de tarjeta
- Loguear datos crudos
- Enviar sin encriptación

✅ SIEMPRE:
- Usar Stripe tokenization
- HTTPS/TLS
- Validar en backend
- PCI compliance
```

### Autenticación

```
✅ Validar Firebase token en CADA request
✅ Verificar UID coincida
✅ Rate limiting
✅ Logging de intentos
```

### Validación

```
✅ Backend revalida TODO
✅ No confiar en cliente
✅ Verificar montos
✅ Prevenir duplicados
```

---

## 📊 Datos Generados

### Órdenes
- Una por compra
- Almacenadas en Firebase
- Vinculadas a usuario
- Inmutables

### Transacciones de Puntos
- Ganadas: 10% del monto en tarjeta
- Gastadas: cuando se usan puntos
- Historial completo
- Para auditoría

### Registros de Pago
- Stripe chargeId guardado
- Timestamp
- Status (completada/fallida)
- Razón de error (si aplica)

---

## ✅ Criterios de Éxito

La feature se considera **completada** cuando:

1. ✅ Todos los 24 tasks completados
2. ✅ Pruebas en dispositivo real (Android 10+)
3. ✅ Sandbox de Stripe funciona correctamente
4. ✅ Errores se manejan gracefully
5. ✅ Datos de tarjeta NUNCA se loguean
6. ✅ Órdenes se guardan correctamente
7. ✅ Puntos se actualizan correctamente
8. ✅ Build sin errores/warnings
9. ✅ Documentación de usuario completada
10. ✅ Documentación técnica actualizada

---

## 🗓️ Timeline Recomendado

```
Semana 1 (Lunes-Miércoles):
  Sprint 1: Setup infraestructura

Semana 1 (Jueves-Viernes) + Semana 2 (Lunes-Miércoles):
  Sprint 2-3: Pago con tarjeta

Semana 2 (Jueves-Viernes):
  Sprint 4: Pago con puntos

Semana 3 (Lunes-Miércoles):
  Sprint 5: Pago híbrido

Semana 3 (Jueves-Viernes):
  Sprint 6: Polish y testing

Semana 4:
  QA y fixes finales
  Deployment a producción
```

---

## 📞 Preguntas Frecuentes

### ¿Qué pasa si se corta la internet durante un pago?

Backend intenta ser idempotente. Si ya creó la orden, responde OK. App intenta reconectar automáticamente (3 reintentos).

### ¿Qué moneda se usa?

USD ($). Convertible a puntos: 1 punto = $0.01

### ¿Puede el usuario usar puntos en el futuro?

Sí, la feature soporta pago con puntos y pago híbrido.

### ¿Es seguro usar Stripe?

Sí. Stripe es PCI compliant nivel 1. Nunca ve datos crudos de tarjeta.

### ¿Se puede reembolsar?

No está en este spec, pero se puede agregar después.

### ¿Qué pasa si falla la tarjeta?

Se muestra el error de Stripe, usuario puede reintentar o cambiar método.

---

## 🔗 Referencias

- [Stripe Documentation](https://stripe.com/docs)
- [Firebase Realtime Database](https://firebase.google.com/docs/database)
- [Node.js Express](https://expressjs.com/)
- [Android Stripe Integration](https://stripe.dev/stripe-android-sdk)

---

## 📝 Notas

- Todos los documentos en este directorio son especificaciones
- NO tocar código hasta que el spec esté completamente aprobado
- Hacer preguntas durante la etapa de planificación, no durante desarrollo
- Cualquier cambio al spec debe actualizarse en los 3 documentos

---

**Última actualización**: 12 de Junio, 2026  
**Responsable**: Daniel Alvarado  
**Status**: 📋 En Planificación

