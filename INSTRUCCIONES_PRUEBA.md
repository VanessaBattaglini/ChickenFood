# 🚀 Instrucciones de Prueba - Sistema de Puntos Reparado

**Fecha**: 17 de Junio, 2026  
**Estado**: ✅ LISTO PARA PROBAR  

---

## 📋 Lo Que Se Reparó

### Problema Original
> "El resumen muestra que quedan 0 puntos cuando están quedando más de 20,000 puntos"

### Lo Que Hacía Mal
- Al pagar con puntos, gastaba TODOS los puntos disponibles
- La confirmación mostraba 0 puntos en lugar del saldo real

### Lo Que Ahora Hace Bien ✅
- Solo gasta los puntos necesarios para la compra
- Los puntos restantes se guardan en la cuenta
- La confirmación muestra el saldo real correcto

---

## 🧪 Cómo Probar

### Paso 1: Compilar el Código
```bash
cd /Users/danielalvarado/AndroidStudioProjects/ChickenFood
./gradlew :app:assembleDebug
```

**Resultado esperado**:
```
BUILD SUCCESSFUL in 11s
✅ Sin errores
✅ APK listo
```

### Paso 2: Instalar en tu dispositivo
Usar Android Studio para instalar en emulador/dispositivo

### Paso 3: Registrarse
1. Abre la app
2. Click en "Registrarse con Google"
3. Completa el registro
4. **Deberías recibir 500 puntos de bienvenida** ✅

### Paso 4: Prueba Básica (5 minutos)

1. **Ve a Dashboard**
   - Deberías ver: "500 PUNTOS" en la PointsCard

2. **Abre un item del menú**
   - Selecciona cualquier comida

3. **Agrega al carrito**
   - Click en agregar

4. **Ve al carrito**
   - Completa cantidad si es necesario

5. **Click en "Confirmar Compra"**
   - **Debería aparecer un diálogo**: "💎 Usar Puntos Acumulados"
   - Muestra: "Tienes 500 puntos acumulados"

6. **Click en "Sí, Usar Puntos"**
   - El diálogo desaparece
   - Se selecciona "💎 Pagar con Puntos"

7. **Lee la información de puntos**
   - "Puntos disponibles: 500 pts"
   - "Puntos a usar: XXX pts" (cantidad exacta para la compra)
   - **IMPORTANTE**: "Te quedarán: YYY pts" (NO DEBE DECIR 0)

8. **Click en "Confirmar Pago"**
   - Ve a pantalla de confirmación

9. **Verifica los puntos en confirmación**
   - "Saldo anterior: 500 pts"
   - "Gastados: -XXX pts"
   - **IMPORTANTE**: "Saldo actual: YYY pts" (NO DEBE SER 0)

---

## 📊 Ejemplo Práctico

### Si compras comida por $5:

**En CheckoutScreen deberías ver:**
```
Puntos disponibles: 500 pts
Puntos a usar: 500 pts = $5.00
Te quedarán: 0 pts        ← Correcto porque se gasta todo

Total original: $5.00
Aún debes pagar: $0.00
✅ ¡Compra completamente cubierta!
```

### Si compras comida por $2:

**En CheckoutScreen deberías ver:**
```
Puntos disponibles: 500 pts
Puntos a usar: 200 pts = $2.00
Te quedarán: 300 pts      ← ✅ CORRECTO! (NO debe ser 0)

Total original: $2.00
Aún debes pagar: $0.00
✅ ¡Compra completamente cubierta!
```

### En la Confirmación:

```
Información de Puntos
Saldo anterior: 500 pts
Gastados: -200 pts
─────────────────────
Saldo actual: 300 pts     ← ✅ CORRECTO! (NO debe ser 0)
Equivalencia: $3.00
```

---

## 🧪 Prueba Completa (10 minutos)

### Primera Compra - Con Tarjeta

1. **Agrega items al carrito** - Total: $10

2. **Va a checkout, selecciona "Pagar con Tarjeta"**

3. **Los datos vienen precargados** - Click confirmar

4. **En confirmación verás:**
   - "Ganados: +100 pts" (10% de $10)
   - "Saldo actual: 600 pts" (500 + 100)

5. **Vuelve al dashboard**
   - Points Card muestra: "600 PUNTOS"

### Segunda Compra - Con Puntos

1. **Agrega items al carrito** - Total: $5

2. **Va a checkout, selecciona "Pagar con Puntos"**
   - Deberías ver el diálogo de puntos automáticamente

3. **En la información de puntos verás:**
   ```
   Puntos disponibles: 600 pts
   Puntos a usar: 500 pts = $5.00
   Te quedarán: 100 pts      ← ✅ CORRECTO
   ```

4. **Click confirmar**

5. **En confirmación verás:**
   - "Gastados: -500 pts"
   - "Saldo actual: 100 pts" ← ✅ CORRECTO (NO es 0)

6. **Vuelve al dashboard**
   - Points Card muestra: "100 PUNTOS"

---

## 🔍 Cómo Verificar en Firebase (Opcional)

1. Abre Firebase Console
2. Ve a: Database → Realtime Database
3. Navega a: `users → [usuario] → rewards`
4. Deberías ver:
```json
{
  "pointsBalance": 100,      ← NO debe ser 0
  "totalPoints": 600,        ← Acumulativo
  "pointsSpent": 500,        ← Aumentó
  ...
}
```

---

## 📱 Qué Buscar en Logcat

### Abre Android Studio → Logcat
### Filtra por: `CheckoutActivity`

**Deberías ver logs como:**
```
D/CheckoutActivity: Payment with points: spend 500 points, remaining: 100
D/CheckoutScreen: Showing points dialog - user has 600 points
```

---

## ❌ Si Algo Sale Mal

### El diálogo no aparece
- Asegúrate de que tienes puntos (≥ 1)
- Revisa que Firebase está conectado
- Toma screenshot del Logcat

### Sigue mostrando 0 puntos
- Limpia datos de la app: Settings → Apps → ChickenFood → Storage → Clear Data
- Reinstala la app
- Inicia sesión de nuevo

### Otros problemas
- Toma screenshot del error
- Comparte el Logcat
- Describe qué pasó exactamente

---

## ✅ Checklist de Prueba

```
PRUEBA BÁSICA (5 min)
☐ App se inicia
☐ Sign up con Google
☐ Recibe 500 puntos
☐ Diálogo aparece en checkout
☐ Puede seleccionar "Pagar con Puntos"
☐ Muestra puntos a gastar correctamente
☐ Muestra puntos restantes (NO es 0)
☐ Compra se completa
☐ Confirmación muestra puntos restantes correctos

PRUEBA COMPLETA (10 min)
☐ Compra 1 con tarjeta (gana puntos)
☐ Dashboard muestra puntos acumulados
☐ Compra 2 con puntos (gasta algunos)
☐ Puntos restantes es correcto (NO es 0)
☐ Dashboard actualiza correctamente
☐ Firebase tiene valores correctos
☐ Logcat muestra logs de puntos
☐ Todas las transacciones se guardan
```

---

## 💡 Puntos Clave

### Conversión de Puntos
```
100 puntos = $1.00 de descuento

Cuando pagas con tarjeta:
$100 compra → ganas 10 puntos (10% cashback)

Cuando pagas con puntos:
$100 compra → gastas 10,000 puntos (100 × 100)
```

### Lo Que Cambió
- ❌ Antes: Gastaba todos los puntos
- ✅ Ahora: Solo gasta los puntos necesarios
- ❌ Antes: Mostraba 0 puntos restantes
- ✅ Ahora: Muestra cantidad real restante

---

## 📊 Resultado Esperado

### Compra de $10 con 500 puntos
```
Puntos para gastar: 1000 pts (necesarios para $10)
No tienes suficientes: Solo tienes 500
Sistema inteligente: Usa los 500 que tienes

Antes de compra:    500 pts
Puntos a usar:     -500 pts
Después de compra:   0 pts    ← CORRECTO (se gasta todo lo que tienes)

PLUS: Se paga $5 más con tarjeta
```

### Compra de $2 con 500 puntos
```
Puntos para gastar: 200 pts (necesarios para $2)
Tienes más que suficiente: Tienes 500

Antes de compra:     500 pts
Puntos a usar:      -200 pts
Después de compra:   300 pts   ← ✅ CORRECTO! (NO es 0)

Compra completamente cubierta: No se paga con tarjeta
```

---

## 🚀 Próximos Pasos

1. ✅ Compilar: `./gradlew :app:assembleDebug`
2. ✅ Instalar en dispositivo
3. ✅ Prueba el app siguiendo los pasos arriba
4. ✅ Reporta si funciona correctamente
5. ✅ Si hay problemas, comparte:
   - Screenshot del problema
   - Logcat output
   - Descripción de qué pasó

---

## 🎉 Resumen

**Lo que se arregló**: El sistema de puntos ahora solo gasta lo necesario, mantiene el resto

**Cómo lo verás**: La confirmación mostrará puntos restantes, NO 0

**Por qué importa**: Ahora puedes usar puntos para descuentos sin perder todo tu balance

**Estado**: ✅ LISTO PARA PROBAR

---

¡A probar! 🚀

