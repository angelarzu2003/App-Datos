# 08 · Paquetes Telcel, redes sociales sin costo y detección de recargas

Documento nuevo (15 nov 2026). Recoge lo que se pidió: que la app **reconozca los paquetes que se carguen, o que uno pueda registrar a mano el que acaba de poner**, y que **tome en cuenta las redes sociales incluidas** en cada paquete.

## 1. Resumen de decisiones

| Tema | Decisión |
|---|---|
| Registro de paquetes | Tres caminos: **A)** selección rápida “Acabo de recargar”, **B)** detección automática por SMS de Telcel (opcional), **C)** calibración con Mi Telcel |
| Catálogo | Plantillas de paquetes **editables**; los MB y la vigencia se confirman con el usuario antes de guardar |
| Redes sociales incluidas | La app las **reconoce por aplicación** y muestra dos cifras: *medido total* y *estimado contra tu paquete* |
| Base de unidades | **1 GB = 1,024 MB** para el plan, porque así lo definen los registros de Telcel ante el IFT |
| SIM | Se mide la **SIM principal** (la que lleva los datos) |
| Datos “reales” | El número base siempre es lo que el celular midió; las reglas de Telcel solo se aplican encima, nunca lo sustituyen |

## 2. Cómo funciona el esquema Amigo Sin Límite (lo que se pudo verificar)

Fuentes oficiales: página de Telcel de Paquetes Amigo Sin Límite y los registros de tarifas del IFT (enlaces al final).

**Montos de paquete publicados:** $10, 20, 30, 50, 80, 100, 150, 200, 270, 300, 400 y 500, más los prepagados de $1,200 (6 meses) y $2,400 (1 año), que renuevan la cuota cada 30 días.

**Redes sociales y mensajería incluidas (según Telcel):**
- **WhatsApp:** en México, Estados Unidos y Canadá (las llamadas y videollamadas solo en México).
- **Facebook, Messenger, X, Instagram y Snapchat:** solo en México y solo para ciertas funciones (por ejemplo, no incluyen transmisiones “En vivo” ni llamadas de Messenger).
- Si se usa una función **no incluida**, esos datos se descuentan de los MB de navegación.
- Hay una **política de uso**: los servicios “sin límite” son para comunicación entre personas; están prohibidos usos como reventa, spam, torrent o streaming continuo.

**Orden de descuento:** primero los MB del paquete, luego el Saldo de Regalo y después el Saldo Amigo (tu dinero). En el registro del paquete 270 el orden es: MB de navegación incluidos → paquete adicional → consumo bajo demanda por MB.

**Al agotarse los MB:** la navegación **no se corta**; sigue “bajo demanda” y se cobra por MB de tu saldo hasta que actives otro paquete. Los MB sin usar se pierden al terminar la vigencia.

**Equivalencia:** los registros indican 1 GB = 1,024 MB.

**En paquetes pequeños** (documentos oficiales de versiones anteriores), WhatsApp era ilimitado pero Facebook/Messenger/X tenían una **bolsa de MB compartida** (200 MB en el paquete de $20); si se agotaba, se descontaba de los MB de navegación. Telcel indica en su página que las demás redes son ilimitadas **a partir del paquete de $100**, así que el catálogo debe distinguir por paquete.

## 3. Catálogo de paquetes: qué está confirmado y qué no

Las promociones de Telcel cambian seguido (hay promociones de “más datos” por temporada que suman GB al paquete) y los sitios de terceros **se contradicen**. Por eso la app **no trae los MB como verdad absoluta**.

### Confirmado en documentos oficiales del IFT que pude abrir

| Paquete | Navegación | Vigencia | Redes sociales | Documento |
|---|---|---|---|---|
| ASL 270 | 2,560 MB | 30 días | Facebook, Messenger, X, Instagram, Snapchat y WhatsApp ilimitados | Registro de sep 2025 |
| ASL 300 | 4,096 MB | 30 días | Ilimitadas | Documento de 2020, probablemente desactualizado |
| ASL 20 | 100 MB | 1 día | WhatsApp ilimitado; Facebook, Messenger y X con 200 MB compartidos | Documento de 2020, probablemente desactualizado |

### Reportado por sitios de terceros (inconsistente, por confirmar)

| Monto | Datos reportados | Vigencia reportada |
|---|---|---|
| $30 | 160 MB | 3 o 7 días (las fuentes difieren) |
| $50 | 500 MB (otra fuente: 1 GB) | 7 o 30 días |
| $100 | 1.5 GB | 15 o 30 días |
| $150 | 2.5 GB | 30 días |
| $200 | 3.5 GB | 30 días |
| $300 | 5.5 GB | 30 o 60 días |
| $500 | 8 GB | 60 días |
| $1,200 / $2,400 | 7 GB cada 30 días | 180 / 365 días |

**Promoción “Más datos con Amigo Sin Límite”** (registro de oct 2025): suma datos sobre el paquete, por ejemplo +0.75 GB en el de $100, +1.25 GB en el de $150, +3.5 GB en el de $200, +2.5 GB en el de $270, +5.5 GB en los de $300 y $400, y +8 GB en el de $500. Su vigencia depende de la promoción; la app debe tratarlos como una **bolsa aparte**.

### Consecuencia de diseño

1. El catálogo semilla trae: **monto, nombre, redes incluidas y política**. Los **MB y días** se muestran como *sugeridos* y piden confirmación.
2. El usuario puede **editar** cualquier valor y guardar su propia plantilla.
3. Al aceptar un SMS de Telcel (camino B), los números del mensaje **mandan** sobre el catálogo.
4. Tarea pendiente: descargar los registros vigentes del IFT de cada paquete y blindar el catálogo (ver `05`, B-40).

## 4. Cómo lo modela la app

### 4.1 Conceptos

```
Plantilla de paquete   →  catálogo (editable, con versión y fecha de verificación)
Recarga / Activación   →  una compra real: fecha, monto, plantilla usada, valores confirmados
Bolsa                  →  un depósito de datos con vigencia propia:
                          · navegación (MB)
                          · promoción extra (MB)
                          · bono de app (ej. 500 MB para Claro Música, según el paquete)
                          · redes sociales (ilimitado, o MB compartidos en paquetes pequeños)
Regla de descuento     →  qué apps cuentan contra qué bolsa
```

### 4.2 Contabilidad: tres números, no uno

| Número | Qué es | Dónde se ve |
|---|---|---|
| **Medido** | Todo lo que el celular transfirió por datos móviles | Detalle y Apps |
| **Estimado en tu paquete** | Medido − tráfico de apps que Telcel no descuenta | Cifra principal del Resumen |
| **Según Telcel (calibrado)** | Estimado × factor de calibración, si el usuario lo activó | Resumen, con etiqueta “ajustado” |

La cifra grande del Resumen es el **estimado en tu paquete**, con un renglón visible debajo: *“Medido en total: X · Redes sociales sin costo: Y”*. Así el usuario ve lo real y también lo que Telcel debería cobrar.

### 4.3 Apps con beneficio (valores iniciales)

| App | Paquete (Android) |
|---|---|
| WhatsApp | `com.whatsapp`, `com.whatsapp.w4b` |
| Facebook | `com.facebook.katana`, `com.facebook.lite` |
| Messenger | `com.facebook.orca`, `com.facebook.mlite` |
| Instagram | `com.instagram.android` |
| X | `com.twitter.android` |
| Snapchat | `com.snapchat.android` |

Modos por app: **Descontar del beneficio** (por defecto) · **Contar contra el paquete** · **Dividir** (porcentaje definido por el usuario).

### 4.4 Límites honestos de esta estimación

- La app **no puede saber** qué parte del tráfico de Instagram o Facebook es una “función incluida” y cuál no (por ejemplo, un video en vivo o un enlace externo). Por eso el estimado puede **subestimar** el consumo real contra tu paquete.
- El beneficio de Facebook, X, Instagram y Snapchat solo aplica **en México**; en el extranjero el tráfico cuenta como navegación.
- La **calibración** (`02`, RF-40…42) mide la diferencia real contra Mi Telcel y la corrige con el tiempo. Esa es la herramienta que convierte la estimación en un número confiable.

## 5. Registrar una recarga

### Camino A · Selección rápida (v1, imprescindible)

```
┌────────────────────────────────┐
│ ←  Nueva recarga               │
│                                │
│ ¿Qué paquete pusiste?          │
│ [ $50 ] [ $100 ] [ $150 ]      │
│ [ $200 ] [ $300 ] [ $500 ]     │
│ [ Otro / personalizado ]       │
│                                │
│ Fecha de activación            │
│ Hoy 15 nov · 16:50     [editar]│
│                                │
│ Paquete de $100                │
│ Navegación     [ 1.5 ] GB      │ ← sugerido, edítalo
│ Vigencia       [ 30 ] días     │
│ Redes incluidas  WhatsApp,     │
│   Facebook, Messenger, X,      │
│   Instagram, Snapchat          │
│                                │
│ ℹ Confirma con tu SMS o con    │
│   Mi Telcel que coincide.      │
│                                │
│ [ Guardar recarga ]            │
└────────────────────────────────┘
```

- Si ya hay un paquete vigente, pregunta: **Sumar** (nueva bolsa que se consume después) o **Reemplazar**. Por defecto crea una bolsa nueva que se usa cuando venza o se agote la anterior.
- Siempre se puede editar o borrar la recarga.

### Camino B · Detección por SMS (opcional, v1.1)

- Telcel confirma recargas y paquetes por SMS. La app puede leer esos mensajes **solo en el teléfono** y proponer: *“Detecté un Paquete de $100 activado hoy. ¿Lo registro?”*
- Requiere el permiso de SMS (`READ_SMS`/`RECEIVE_SMS`), que es sensible. Se pide solo si el usuario activa “Detectar recargas por SMS”, con explicación clara. Sigue sin haber permiso de INTERNET, así que el contenido de los mensajes **no puede salir** del teléfono.
- La app **solo procesa mensajes de los remitentes de Telcel** y descarta el resto sin guardarlo.
- **Necesito 2–3 ejemplos reales** de los SMS que recibes de Telcel al recargar y al activar un paquete (con tu número y datos borrados) para escribir el lector. Sin ejemplos reales no voy a inventar el formato.
- Alternativa si el permiso de SMS no convence: acceso a notificaciones, que lee la notificación del SMS sin pedir permiso de SMS. Se decide en la Fase 5.

### Camino C · Calibración

- Compara el saldo de Mi Telcel con lo medido y corrige. Detalle en `04` §5.6.

## 6. Aviso cuando se acaba el paquete

Para prepago es la alerta más importante: al terminar los MB incluidos, **Telcel sigue dando internet y cobra por MB de tu saldo**.

- Al llegar al 100 % de la bolsa de navegación: *“Se acabó tu paquete. Desde ahora Telcel cobra los datos de tu saldo.”*
- Aviso previo al 90 %: *“Te quedan pocos MB. Si se acaban, se cobrará de tu saldo.”*
- Opción en ajustes: **“Avisarme cada vez que gaste más de X MB después de acabarse el paquete”**.

## 7. SIM principal

El Vivo X300 Ultra es de dos SIM y se pidió medir **la SIM principal de preferencia**. Diseño en `03` §2.5. Resumen: la app registra qué SIM lleva los datos en cada momento y asigna a esa SIM los bytes medidos en ese intervalo.

## 8. Fuentes consultadas

- Telcel, Paquetes Amigo Sin Límite: https://www.telcel.com/personas/amigo/paquetes/paquetes-amigo-sin-limite
- IFT, registro del Paquete Amigo Sin Límite 270 (sep 2025): https://tarifas.ift.org.mx/ift_visor/assets/ratefiles/17570862128877nacional-registropasl270final040925.pdf
- IFT, registro del Paquete Amigo Sin Límite 300 (2020): https://tarifas.ift.org.mx/ift_visor/assets/ratefiles/15820496887852nacional-actualizacionpasl_3004gbfinal170220.pdf
- IFT, registro del Paquete Amigo Sin Límite 20 (2020): https://tarifas.ift.org.mx/ift_visor/assets/ratefiles/16001068889468nacional-actualizacionpasl_20final140920.pdf
- IFT, Más datos con Amigo Sin Límite (oct 2025): https://tarifas.ift.org.mx/ift_visor/assets/ratefiles/17607491407617nacional-registromasdatosconamigosinlimitefinal171025.pdf
- Buscador de tarifas del IFT: https://tarifas.ift.org.mx/ift_visor/
- Terceros (solo orientativos, se contradicen entre sí): internetencasa.mx, selectra.mx, tecnoguias.net

## 9. Pendientes de este documento

- [ ] Conseguir los registros IFT vigentes de los 12 paquetes y sustituir los datos “por confirmar”.
- [ ] Ejemplos reales de SMS de Telcel para el lector (camino B).
- [ ] Confirmar con tu propio paquete cuál es la regla de acumulación al recargar antes de que venza el anterior.
- [ ] Confirmar con pruebas (`07`, E13) cuánto tráfico de redes sociales descuenta realmente Telcel.
