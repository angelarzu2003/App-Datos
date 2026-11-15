# 04 · Diseño UX/UI

Dirección: **minimalista, limpia, honesta.** Mucho aire, un número protagonista por pantalla, casi sin adornos.

## 1. Principios de diseño

1. **Un héroe por pantalla.** Una sola cifra grande; todo lo demás es secundario.
2. **Color con propósito.** Neutros casi siempre; el color aparece solo para estado (ok / atención / peligro) y para el acento.
3. **Sin decoración.** Sin sombras, sin degradados, sin ilustraciones recargadas. Bordes finos de 1 dp.
4. **Números como protagonistas.** Tipografía grande, cifras tabulares para que no “bailen” al actualizarse.
5. **Lenguaje simple.** “Te quedan 6.5 GB”, no “Saldo residual del ciclo”.
6. **Movimiento mínimo y útil.** Solo para mostrar cambio (barra que avanza, número que se actualiza).

## 2. Sistema visual

### 2.1 Color

| Rol | Claro | Oscuro |
|---|---|---|
| Fondo | `#FAFAF9` | `#0C0A09` |
| Superficie (tarjetas) | `#FFFFFF` | `#1C1917` |
| Borde | `#E7E5E4` | `#292524` |
| Texto principal | `#1C1917` | `#FAFAF9` |
| Texto secundario | `#78716C` | `#A8A29E` |
| **Acento** | `#0F766E` | `#2DD4BF` |
| Atención (ámbar) | `#B45309` | `#FBBF24` |
| Peligro (rojo) | `#B91C1C` | `#F87171` |

- Contraste de texto y acento ≥ 4.5:1 sobre su fondo (WCAG AA); se verificará con herramienta en la Fase 1.
- Estados del plan: **< 60 %** acento · **60–85 %** ámbar · **> 85 %** rojo.
- El estado **nunca** se comunica solo con color: siempre hay texto (“Vas rápido”) o ícono.
- Modo oscuro: sigue el sistema; se puede forzar en Ajustes.

### 2.2 Tipografía

Fuente: **Inter** (alternativa: Roboto del sistema). Cifras tabulares activadas en todos los números.

| Estilo | Tamaño / interlineado | Peso | Uso |
|---|---|---|---|
| Display | 48 / 52 | SemiBold | Cifra principal (“3.42 GB”) |
| Título 1 | 28 / 34 | SemiBold | Encabezado de pantalla |
| Título 2 | 20 / 26 | Medium | Secciones |
| Cuerpo | 16 / 24 | Regular | Texto general |
| Etiqueta | 14 / 20 | Medium | Botones, chips |
| Nota | 13 / 18 | Regular | Texto de apoyo |

### 2.3 Espaciado, forma y elevación

- Rejilla de **8 dp** (pasos 4, 8, 12, 16, 24, 32, 48).
- Márgenes de pantalla: 20 dp.
- Radio: tarjetas 16 dp, botones 12 dp, chips completos.
- **Sin sombras**: la jerarquía se hace con borde de 1 dp y espacio.
- Iconos: línea de 1.75 dp, estilo Lucide/Material Symbols “outlined”.

### 2.4 Formato de cantidades

- Unidad automática: `< 1000 KB → KB`, `< 1000 MB → MB`, si no `GB`.
- GB con 2 decimales, MB con 0 o 1, siempre con espacio fino (`3.42 GB`).
- Base **binaria** por defecto (1 GB = 1,024 MB), que es como Telcel define sus paquetes ante el IFT; opción en Ajustes para base decimal.
- Velocidad: `1.2 MB/s`, `340 KB/s`, `0 B/s` cuando está quieto.

## 3. Componentes

| Componente | Descripción |
|---|---|
| **Cifra héroe** | Número Display + unidad en Etiqueta, con subtítulo en texto secundario |
| **Barra de progreso del plan** | 8 dp de alto, extremos redondeados, color según estado |
| **Fila de app** | Ícono 40 dp · nombre · barra fina relativa · bytes alineados a la derecha |
| **Tarjeta** | Superficie + borde 1 dp + radio 16 + padding 16 |
| **Chip de periodo** | Hoy · 7 días · Ciclo · Personalizado |
| **Barras diarias** | Una barra por día, sin ejes pesados, valor al tocar |
| **Medidor de velocidad** | Número grande + sparkline de los últimos 60 s |
| **Aviso (banner)** | Una línea con ícono; ámbar/rojo/neutro |
| **Estado vacío** | Ícono simple + frase + un botón |

## 4. Navegación

Barra inferior con **3 destinos** (suficiente y limpio):

```
[ Resumen ]   [ En vivo ]   [ Apps ]
```

- Ajustes y Plan: ícono de engranaje arriba a la derecha en Resumen.
- Detalle de app, Calibración y Plan: pantallas apiladas con flecha atrás.

```
Primer uso:  Bienvenida → Permiso de uso → Configurar plan → Resumen
Día a día:   Resumen ⇄ En vivo ⇄ Apps → Detalle de app
Plan:        Resumen → ⚙ → Plan → Calibrar con Telcel
```

## 5. Pantallas (wireframes)

> Los dibujos son de estructura, no de estilo final.

### 5.1 Resumen (pantalla principal)

```
┌────────────────────────────────┐
│ Datos móviles               ⚙  │
│                                │
│ Usado en tu plan               │
│ 3.42 GB                        │  ← cifra héroe
│ de 10 GB · 34 %                │
│ ▓▓▓▓▓▓▓░░░░░░░░░░░░░░░░░░░░    │
│                                │
│ Quedan 6.58 GB · 12 días       │
│ ✓ Al ritmo actual te alcanza   │  ← estado con texto
│                                │
│ ┌──────────┬──────────┬──────┐ │
│ │ Hoy      │ Ayer     │7 días│ │
│ │ 412 MB   │ 380 MB   │2.9GB │ │
│ └──────────┴──────────┴──────┘ │
│                                │
│ Apps que más gastan            │
│ ▣ YouTube            1.21 GB   │
│ ▣ WhatsApp             640 MB  │
│ ▣ Chrome               512 MB  │
│ Ver todas →                    │
│                                │
│   Resumen    En vivo    Apps   │
└────────────────────────────────┘
```

Variante con alerta:

```
│ ⚠ Vas rápido: al ritmo actual   │
│   se acaban el 22 nov (faltan 8)│
```

Variante con redes sociales incluidas (la cifra héroe es el **estimado en tu paquete**):

```
│ Usado en tu paquete            │
│ 1.12 GB                        │
│ de 1.5 GB · 75 %               │
│ ▓▓▓▓▓▓▓▓▓▓▓▓▓▓░░░░░            │
│                                │
│ Medido en total      1.62 GB   │
│ Redes sociales sin costo       │
│ (WhatsApp, Instagram…) 0.50 GB │
```

Si se acaban los MB (prepago): banner rojo *“Se acabó tu paquete. Desde ahora Telcel cobra los datos de tu saldo.”*

### 5.2 En vivo

```
┌────────────────────────────────┐
│ En vivo                        │
│                                │
│ ↓ 1.2 MB/s        ↑ 85 KB/s    │  ← velocidades
│ ▁▂▃▅▇▆▃▂▁▁▂▅▇▇▅▂  (últimos 60s)│
│                                │
│ Ahora mismo                    │
│ ▣ YouTube          ↓ 1.1 MB/s  │
│ ▣ Google Play      ↓  95 KB/s  │
│ ▣ WhatsApp         ↑  12 KB/s  │
│                                │
│ Esta sesión: 38.4 MB           │
│ Red: Datos móviles · 4G        │
│                                │
│   Resumen    En vivo    Apps   │
└────────────────────────────────┘
```

Si está en Wi‑Fi: “Estás en Wi‑Fi. No se gastan datos móviles.” (la velocidad se muestra en gris).

### 5.3 Apps

```
┌────────────────────────────────┐
│ Apps                           │
│ [Hoy] [7 días] [Ciclo] [···]   │
│ Móvil ▾                        │
│                                │
│ ▣ YouTube          1.21 GB     │
│   ▓▓▓▓▓▓▓▓▓▓▓▓▓▓░░░░░░  35 %   │
│ ▣ WhatsApp           640 MB    │
│   ▓▓▓▓▓▓▓░░░░░░░░░░░░░  19 %   │
│ ▣ Chrome             512 MB    │
│ ▣ Sistema Android    210 MB    │
│ ▣ Compartir internet 180 MB    │
│ ...                            │
│                                │
│   Resumen    En vivo    Apps   │
└────────────────────────────────┘
```

### 5.4 Detalle de app

```
┌────────────────────────────────┐
│ ←  YouTube                     │
│                                │
│ 1.21 GB                        │
│ en los últimos 7 días          │
│                                │
│ ▁ ▃ ▂ ▇ ▅ ▂ ▃   (por día)      │
│ L  M  M  J  V  S  D            │
│                                │
│ Móvil          1.21 GB         │
│ Wi‑Fi          4.80 GB         │
│                                │
│ Primer plano   0.46 GB   38 %  │
│ Segundo plano  0.75 GB   62 %  │
│ ℹ Más de la mitad se gastó con │
│   la app cerrada o minimizada. │
│                                │
│ [ Ver ajustes de la app ]      │
└────────────────────────────────┘
```

### 5.5 Plan

```
┌────────────────────────────────┐
│ ←  Mi plan                     │
│                                │
│ Nombre         Paquete 10 GB   │
│ Datos totales  [ 10 ] GB       │
│ Inicia         14 nov 2026     │
│ Vigencia       (•) 30 días     │
│                ( ) Mensual     │
│                                │
│ Avisarme al    50 % · 80 % · 100 % │
│                                │
│ [ Calibrar con Mi Telcel ]     │
│ [ Guardar ]                    │
└────────────────────────────────┘
```

### 5.6 Calibrar con Mi Telcel

```
┌────────────────────────────────┐
│ ←  Calibrar                    │
│                                │
│ Abre Mi Telcel y escribe cuántos│
│ datos te quedan.               │
│                                │
│ Me quedan   [ 5.9 ] GB         │
│                                │
│ ───────────────────────────    │
│ Telcel dice que usaste 4.10 GB │
│ Tu teléfono midió      3.42 GB │
│ Diferencia             +20 %   │
│                                │
│ Esa diferencia puede deberse a │
│ cómo cuenta cada uno. Puedes   │
│ ajustar la app a Telcel.       │
│                                │
│ [ Ajustar a Telcel ]  [ Solo ver ] │
└────────────────────────────────┘
```

Los valores son de ejemplo. Si se aplica el ajuste, el resumen muestra la etiqueta “ajustado ×1.20”.

### 5.7 Onboarding

```
┌────────────────────────────────┐
│                                │
│        (ícono simple)          │
│                                │
│  Mide tus datos de verdad      │
│                                │
│  Necesitamos “Acceso a datos   │
│  de uso” para ver cuántos datos│
│  gasta cada app.               │
│                                │
│  ✓ Todo se queda en tu celular │
│  ✓ La app no tiene internet    │
│                                │
│  [ Dar acceso ]                │
│  Más información               │
└────────────────────────────────┘
```

### 5.8 Ajustes

Lista simple: Plan y recargas · SIM principal · Redes sociales incluidas · Alertas · Unidades (binaria/decimal) · Tema · Notificación en vivo · Detectar recargas por SMS (opcional) · Optimización de batería (guía OriginOS) · Privacidad · Acerca de.

### 5.9 Nueva recarga

Ver el wireframe en `08-paquetes-telcel.md` §5. Principios de diseño: montos como botones grandes, valores sugeridos editables y una línea que invita a confirmar con el SMS de Telcel; nunca guarda en silencio.

### 5.10 Redes sociales incluidas

```
┌────────────────────────────────┐
│ ←  Redes sociales incluidas    │
│                                │
│ Tu paquete incluye estas apps  │
│ sin descontar tus MB:          │
│                                │
│ ▣ WhatsApp        [Descontar ▾]│
│ ▣ Instagram       [Descontar ▾]│
│ ▣ Facebook        [Descontar ▾]│
│ ▣ Messenger       [Descontar ▾]│
│ ▣ X               [Descontar ▾]│
│ ▣ Snapchat        [Descontar ▾]│
│                                │
│ ℹ Telcel no incluye funciones  │
│   como transmisiones en vivo.  │
│   La app no puede separarlas;  │
│   usa Calibrar para ajustar.   │
└────────────────────────────────┘
```

## 6. Estados y errores

| Estado | Qué se muestra |
|---|---|
| Sin permiso de uso | Tarjeta con explicación y botón “Dar acceso”; el resto sigue visible en gris |
| Sin plan configurado | Se muestran totales por periodo sin barra; botón “Configurar plan” |
| Sin datos todavía | “Aún no hay datos. Usa tu celular un rato y vuelve.” |
| Wi‑Fi activo | La vista En vivo lo indica; Resumen no cambia |
| Fabricante restringe segundo plano | Banner discreto con enlace a la guía |
| Error de lectura | “No pudimos leer los datos. Intenta de nuevo.” + botón reintentar |

## 7. Notificaciones y widget

**Alertas**
- `Llevas el 80 % de tu paquete` — *Te quedan 2 GB y 9 días. Toca para ver quién gasta.*
- `YouTube gastó 420 MB en segundo plano hoy`
- `Se acabarán tus datos antes del fin del ciclo` — *Al ritmo actual, el 22 nov.*

**Notificación en vivo (opcional):** `↓ 1.2 MB/s · ↑ 85 KB/s · Hoy 412 MB`.

**Widget 2×1:** usado/total, barra y días restantes. Mismo estilo de tarjeta.

## 7.1 Tono y microcopy (es‑MX)

- Directo, cercano, sin tecnicismos: “datos”, no “tráfico de red”.
- Tutea al usuario.
- Evita lenguaje alarmista; informa y sugiere una acción.
- Números siempre con unidad; fechas como “22 nov”.

## 8. Accesibilidad

- Contraste AA, comprobado en claro y oscuro.
- Áreas táctiles ≥ 48 dp.
- Compatible con TalkBack: cada barra y cifra tiene descripción (“YouTube, 1.21 gigabytes, 35 por ciento”).
- Escala de fuente hasta 200 % sin cortar contenido.
- Estado nunca solo por color.
- Respeta “reducir animaciones”.

## 9. Movimiento

| Elemento | Animación |
|---|---|
| Barra del plan | Crece desde 0 al abrir, 400 ms ease-out |
| Cifra héroe | Cambia con fundido corto, sin saltos de ancho (cifras tabulares) |
| Velocidad en vivo | Suavizado de 300 ms entre lecturas |
| Cambio de pantalla | Transición estándar de Material, sin extras |

## 10. Entregables de diseño siguientes

1. Prototipo en Figma de las 7 pantallas, claro y oscuro.
2. Tokens de diseño exportados a Compose (`Color.kt`, `Type.kt`, `Spacing.kt`).
3. Revisión de contraste y de accesibilidad.
4. Prueba de comprensión con 5 usuarios (“¿cuánto te queda? ¿quién gasta más?”).
