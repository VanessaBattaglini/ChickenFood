# 💳 Tarjetas de Prueba - Datos para Testing

## ✅ Números de Tarjeta de Prueba (No Real)

Estos son números de tarjeta de prueba válidos que puedes usar en la pantalla de checkout:

### 🟢 Tarjetas Válidas para Testing

| Tipo | Número | Vencimiento | CVC | Estado |
|------|--------|-------------|-----|--------|
| **Visa** | `4532 1234 5678 9010` | `12/25` | `123` | ✅ Válida |
| **Mastercard** | `5425 2334 3010 9903` | `06/24` | `456` | ✅ Válida |
| **Visa** | `4111 1111 1111 1111` | `03/26` | `789` | ✅ Válida |
| **Amex** | `3782 822463 10005` | `08/25` | `1234` | ✅ Válida |

---

## 📋 Ejemplo Completo de Formulario

### **Forma 1: Visa Básica** (Recomendada para empezar)
```
Número de Tarjeta:  4532 1234 5678 9010
Nombre del Titular: JOHN DOE
Vencimiento:        12/25
CVC:                123
```

### **Forma 2: Mastercard**
```
Número de Tarjeta:  5425 2334 3010 9903
Nombre del Titular: MARIA GARCIA
Vencimiento:        06/24
CVC:                456
```

### **Forma 3: Visa Simple** (Patrón fácil de recordar)
```
Número de Tarjeta:  4111 1111 1111 1111
Nombre del Titular: TEST USER
Vencimiento:        03/26
CVC:                789
```

---

## 🔍 Validaciones en la App

La app valida automáticamente los datos:

### ✅ Validaciones Implementadas

| Campo | Regla | Ejemplo |
|-------|-------|---------|
| **Número** | Exactamente 16 dígitos | `4532123456789010` |
| **Titular** | Mínimo 2 caracteres | `JOHN DOE` |
| **Vencimiento** | Formato MM/YY | `12/25` (diciembre 2025) |
| **CVC** | Exactamente 3 dígitos | `123` |

---

## ⚠️ Formato Correcto

### Número de Tarjeta
```
❌ Incorrecto:  4532-1234-5678-9010  (guiones no permitidos)
❌ Incorrecto:  453212345678901      (15 dígitos)
✅ Correcto:    4532123456789010     (16 dígitos sin espacios)
             o  4532 1234 5678 9010  (con espacios, app los limpia)
```

### Vencimiento
```
❌ Incorrecto:  12-25       (guión no permitido)
❌ Incorrecto:  2025        (sin mes)
❌ Incorrecto:  12/25/2025  (demasiados dígitos)
✅ Correcto:    12/25       (MM/YY)
✅ Correcto:    06/24       (06 = junio, 24 = 2024)
```

### CVC
```
❌ Incorrecto:  12          (2 dígitos)
❌ Incorrecto:  1234        (4 dígitos)
✅ Correcto:    123         (3 dígitos)
```

---

## 🧪 Flujo de Testing Completo

### Paso 1: Abrir Checkout
```
1. Agregar items al carrito
2. Click en "Proceder al Pago"
3. Seleccionar método "Pagar con Tarjeta"
```

### Paso 2: Llenar Formulario
```
Usa una de las tarjetas de arriba, ej:
- Número:  4532 1234 5678 9010
- Titular: JOHN DOE
- Venci:   12/25
- CVC:     123
```

### Paso 3: Confirmar Pago
```
1. Click en "Confirmar Pago"
2. Debe validarse (checkmark verde en cada campo)
3. Si algo falta → error en rojo
```

### Paso 4: Confirmación
```
✅ Si datos válidos → Ir a ConfirmationScreen
❌ Si datos inválidos → Mostrar error
```

---

## 📊 Estados de Validación en Tiempo Real

La app muestra validación en tiempo real:

### ✅ Válido (Campo con ✓)
```
Número de Tarjeta:  [4532123456789010] ✓
```

### ❌ Inválido (Campo con ✗)
```
Número de Tarjeta:  [453212345678] ✗
                    Debe tener 16 dígitos
```

### ⏳ Vacío (Sin símbolo)
```
Número de Tarjeta:  [] 
```

---

## 🎯 Casos de Testing

### 1️⃣ Testing Exitoso
```
Tarjeta:   4532 1234 5678 9010
Titular:   JOHN DOE
Venci:     12/25
CVC:       123
Resultado: ✅ Pago confirmado → ConfirmationScreen
```

### 2️⃣ Testing Número Inválido
```
Tarjeta:   1234 5678 9012
Titular:   JOHN DOE
Venci:     12/25
CVC:       123
Resultado: ❌ "Debe tener 16 dígitos"
```

### 3️⃣ Testing Vencimiento Inválido
```
Tarjeta:   4532 1234 5678 9010
Titular:   JOHN DOE
Venci:     13/25 (mes 13 no existe)
CVC:       123
Resultado: ❌ "Formato MM/YY"
```

### 4️⃣ Testing CVC Inválido
```
Tarjeta:   4532 1234 5678 9010
Titular:   JOHN DOE
Venci:     12/25
CVC:       12 (solo 2 dígitos)
Resultado: ❌ "3 dígitos"
```

### 5️⃣ Testing Campos Vacíos
```
Tarjeta:   [vacío]
Titular:   [vacío]
Venci:     [vacío]
CVC:       [vacío]
Resultado: ❌ Botón "Confirmar Pago" deshabilitado
```

---

## 💡 Tips de Testing

### ✅ Mejores Prácticas

1. **Empieza con Visa Simple**
   - `4111 1111 1111 1111`
   - Más fácil de recordar

2. **Prueba Diferentes Años**
   - `12/24` (2024)
   - `12/25` (2025)
   - `12/26` (2026)

3. **Prueba Diferentes Meses**
   - `01/25` (enero)
   - `06/25` (junio)
   - `12/25` (diciembre)

4. **Prueba Titular Corto**
   - Mínimo: `AB` (2 caracteres)
   - Normal: `JOHN DOE`
   - Largo: `JOHN PATRICK DOE JR`

---

## 🔒 Notas de Seguridad

### ⚠️ IMPORTANTE

- **Estos números NO son reales**
- Son solo para testing local
- No funcionan en procesadores de pago reales
- Es solo simulación

### En Producción:

- Se necesitaría integración con Stripe, PayPal, etc.
- Los datos de tarjeta se procesarían en servidor seguro
- Nunca guardar números completos localmente
- Usar tokenización PCI-DSS

---

## 📝 Validaciones Detalladas

### Luhn Algorithm (Checksum)
```
La app VALIDA números usando Luhn algorithm
Los números de arriba pasan esta validación
Si usas número random probablemente fallará
```

### Fecha de Vencimiento
```
✅ Válida: Fecha >= hoy (dentro de vigencia)
❌ Inválida: Mes > 12
❌ Inválida: Año < 24 (asume 2024+)
```

---

## 🎬 Ejemplo Paso a Paso

```
INICIO
  ↓
[Abrir carrito con items]
  ↓
[Click "Proceder al Pago"]
  ↓
[CHECKOUT SCREEN aparece]
  ↓
[Seleccionar "Pagar con Tarjeta"]
  ↓
[Llenar campos]
  Número:  4532 1234 5678 9010
  Titular: JOHN DOE
  Venci:   12/25
  CVC:     123
  ↓
[Cada campo se valida en tiempo real]
  ✓ Número OK
  ✓ Titular OK
  ✓ Venci OK
  ✓ CVC OK
  ↓
[Botón "Confirmar Pago" se activa]
  ↓
[Click "Confirmar Pago"]
  ↓
[CONFIRMATION SCREEN aparece]
  ✅ Pago exitoso!
  ✅ Orden: ORD_[timestamp]
  ✅ Puntos ganados: +XX
  ↓
[Click "Volver a Inicio"]
  ↓
[DASHBOARD + CARRITO VACÍO]
  ✅ Testing exitoso!
```

---

## 📞 Contacto/Soporte

Si tienes problemas:
1. Verifica el formato del número (16 dígitos)
2. Verifica formato vencimiento (MM/YY)
3. Verifica CVC (3 dígitos)
4. Revisa los logs de Android Studio

---

**Última actualización**: 2024-06-16
**Versión**: Testing v1.0
**Estado**: ✅ Listo para usar

¡Prueba con confianza! 🚀
