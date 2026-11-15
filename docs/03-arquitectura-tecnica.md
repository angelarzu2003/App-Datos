# 03 · Arquitectura técnica

## 1. Plataforma y stack

| Capa | Elección | Por qué |
|---|---|---|
| Plataforma | Android nativo | Es la única donde se puede leer consumo por app sin root |
| Lenguaje | Kotlin | Estándar de Android, corrutinas y Flow para datos en vivo |
| UI | Jetpack Compose + Material 3 (tema propio minimalista) | Menos código, fácil de animar y de tematizar |
| Arquitectura | MVVM + casos de uso, flujo unidireccional (`StateFlow`) | Simple y testeable |
| Inyección | Hilt | Permite cambiar fuentes reales por falsas en pruebas |
| Base de datos | Room (SQLite) | Historial local, consultas por rango |
| Segundo plano | WorkManager + Foreground Service (solo en vivo) | Fiable ante Doze y reinicios |
| Gráficas | Dibujo propio con Compose Canvas | Evita librerías pesadas; solo barras y línea simple |
| Min SDK | 26 (Android 8.0) | Cubre casi todo el parque en México |
| Target SDK | La versión estable más reciente | Requisito de tiendas |
| Dispositivo de referencia | vivo X300 Ultra, ROM de China, OriginOS 7 (Android 17), una SIM Telcel en uso | Es el teléfono real donde se validará todo |

## 2. Cómo se mide (el corazón del proyecto)

Android ya cuenta los bytes por app en el kernel. La app **no espía tráfico**: lee esos contadores.

### 2.1 Fuentes disponibles

| Fuente | Qué da | Requiere | Uso en la app |
|---|---|---|---|
| `NetworkStatsManager` | Historial por UID, tipo de red (móvil/Wi‑Fi), estado (primer/segundo plano), en buckets de tiempo | Permiso especial **Acceso a datos de uso** (`PACKAGE_USAGE_STATS`) | Fuente principal de historial y ranking |
| `TrafficStats` | Contadores acumulados desde el arranque: totales de móvil/Wi‑Fi y por UID | Ninguno para totales; el acceso por UID ajeno puede estar limitado según versión | Velocidad en vivo (diferencia entre dos lecturas) |
| `VpnService` local | Ve cada paquete, por app y por dominio | Ocupa el único “slot” de VPN del teléfono; más batería | **Descartado en v1**, posible “modo preciso” en v2 |
| Leer `/proc/net/...` | Contadores crudos | Bloqueado desde Android 10 para apps normales | No se usa |

### 2.2 Estrategia híbrida

```
En vivo (cada 1–2 s)     →  TrafficStats (totales) + NetworkStatsManager (por app, ventana corta)
Historial (cada ~15 min) →  NetworkStatsManager → snapshots → Room
Al abrir la app          →  Sincroniza lo pendiente y refresca
```

**Punto a validar en la Fase 0:** la frescura real de `NetworkStatsManager` (el sistema puede actualizar sus estadísticas con cierto retraso) y si `TrafficStats` por UID funciona en las versiones objetivo. Si el dato por app no es lo bastante fresco, la vista En vivo mostrará velocidad **total** en tiempo real y las apps más activas con un retraso de pocos segundos, indicándolo en la interfaz.

### 2.3 Cálculo sin errores: deltas, no totales

Los contadores son acumulados y se reinician. Por eso la app **guarda lecturas y calcula diferencias**:

```
delta = lectura_actual − lectura_anterior
si lectura_actual < lectura_anterior → hubo reinicio: delta = lectura_actual
```

Reglas:
- Cada lectura se guarda con su marca de tiempo y el identificador de arranque (`boot_id`/tiempo desde arranque) para detectar reinicios.
- Un bucket (UID + red + estado + intervalo) se **reemplaza**, no se suma, para evitar duplicados al volver a consultar el mismo intervalo.
- Se redondea solo al mostrar, nunca al guardar.

### 2.4 Atribución especial

| UID del sistema | Se muestra como |
|---|---|
| UID de apps normales | Nombre e ícono de la app |
| `UID_REMOVED` | “Apps desinstaladas” |
| `UID_TETHERING` | “Compartir internet” |
| UID 1000 / root | “Sistema Android” |
| UIDs compartidos por varias apps | Se muestra el nombre del grupo o la app principal, con nota |

### 2.5 Dos SIM: medir la principal

Una app normal no puede pedir al sistema las estadísticas “de la SIM 1” (el identificador de suscriptor es privilegiado desde Android 10). Pero en cada momento **solo una SIM lleva los datos móviles**, así que se resuelve con una línea de tiempo:

1. La app registra **qué suscripción lleva los datos** (`SubscriptionManager` / `TelephonyManager`, permiso `READ_PHONE_STATE`) y cada cambio, con su hora.
2. En cada sincronización calcula el consumo móvil del intervalo y lo **asigna a la SIM que llevaba los datos** en ese intervalo.
3. Si hubo un cambio de SIM a mitad del intervalo, se sincroniza justo en el cambio (escuchando el evento) y el intervalo se parte en dos.
4. El usuario elige cuál es su **SIM principal** (la Telcel de su paquete). Resumen, plan y alertas usan solo esa SIM; la otra aparece como “Otra SIM” en el detalle.

**Límites a validar en la Fase 0:** (a) que el sistema avise bien los cambios de SIM de datos en OriginOS; (b) la función de cambio temporal de datos a la otra SIM cuando la principal no tiene señal; (c) precisión del reparto cuando el cambio ocurre entre dos sincronizaciones. Si no cumple, plan B: pedir al usuario que desactive los datos de la otra SIM y medir todo el móvil.

### 2.6 Qué cuenta y qué no

Los contadores del sistema miden bytes a nivel IP. Eso incluye cabeceras de red, pero no la sobrecarga de capas inferiores del operador. Esa es la razón principal de una pequeña diferencia con el operador; por eso existe la calibración (RF-40…42).

## 3. Arquitectura por capas

```
┌─────────────────────────── UI (Compose) ───────────────────────────┐
│  Resumen │ En vivo │ Apps │ Detalle app │ Plan │ Calibración │ Ajustes │
└───────────────▲───────────────────────────────────────────────────┘
                │ StateFlow
┌───────────────┴──────────── ViewModels ────────────────────────────┐
└───────────────▲───────────────────────────────────────────────────┘
                │
┌───────────────┴──────────── Dominio (casos de uso) ────────────────┐
│ ObtenerResumenPlan · ObtenerRankingApps · ObservarVelocidad         │
│ CalcularProyeccion · EvaluarAlertas · CalibrarContraOperador        │
└───────────────▲───────────────────────────────────────────────────┘
                │
┌───────────────┴──────────── Datos (repositorios) ──────────────────┐
│ UsageRepository · PlanRepository · AlertRepository · AppInfoRepo    │
└──────▲──────────────────▲───────────────────▲─────────────────────┘
       │                  │                   │
 NetworkStatsSource   TrafficStatsSource    Room (BD local)
 (API del sistema)    (API del sistema)     PackageManagerSource
```

### Módulos Gradle sugeridos

```
app/                  → Application, navegación, DI
core/designsystem/    → tema, colores, tipografía, componentes
core/common/          → formato de bytes, fechas, utilidades
core/model/           → clases de dominio
core/database/        → Room, DAOs, entidades
core/measurement/     → NetworkStatsSource, TrafficStatsSource, reglas de delta
feature/summary/      → pantalla Resumen
feature/live/         → pantalla En vivo
feature/apps/         → lista y detalle de apps
feature/plan/         → configuración y calibración
feature/settings/     → ajustes, permisos, alertas
work/                 → WorkManager, servicio en vivo, receptores
```

## 4. Modelo de datos (Room)

```
app_info
  uid              INTEGER  PK
  package_name     TEXT
  label            TEXT
  is_system        INTEGER
  first_seen_at    INTEGER
  uninstalled_at   INTEGER NULL

usage_bucket                    -- un registro por UID + red + estado + intervalo
  uid              INTEGER
  network          TEXT     -- MOBILE | WIFI
  state            TEXT     -- FOREGROUND | BACKGROUND
  start_ts         INTEGER  -- epoch ms
  end_ts           INTEGER
  sub_id           INTEGER NULL  -- SIM que llevaba los datos en el intervalo (ver 2.5)
  rx_bytes         INTEGER
  tx_bytes         INTEGER
  PRIMARY KEY (uid, network, state, start_ts, sub_id)

counter_snapshot                -- para detectar reinicios y calcular deltas
  id               INTEGER PK
  taken_at         INTEGER
  boot_elapsed_ms  INTEGER
  mobile_rx        INTEGER
  mobile_tx        INTEGER

plan
  id               INTEGER PK
  name             TEXT
  total_bytes      INTEGER
  cycle_type       TEXT     -- MONTHLY | DAYS
  cycle_start      INTEGER  -- epoch ms
  cycle_days       INTEGER NULL
  reset_day        INTEGER NULL
  thresholds       TEXT     -- "50,80,100"
  active           INTEGER

alert_event
  id               INTEGER PK
  plan_id          INTEGER
  kind             TEXT     -- THRESHOLD | ANOMALY | PROJECTION
  threshold        INTEGER NULL
  fired_at         INTEGER
  cycle_start      INTEGER  -- para no repetir en el mismo ciclo

calibration
  id               INTEGER PK
  plan_id          INTEGER
  taken_at         INTEGER
  operator_used_bytes   INTEGER   -- lo que dice Telcel
  measured_used_bytes   INTEGER   -- lo que midió la app
  factor           REAL            -- operator / measured
  applied          INTEGER

sim_profile
  sub_id           INTEGER PK
  slot             INTEGER
  carrier          TEXT
  is_primary       INTEGER

sim_timeline                    -- qué SIM llevó los datos y cuándo
  id               INTEGER PK
  sub_id           INTEGER
  started_at       INTEGER
  ended_at         INTEGER NULL

package_template                -- catálogo editable (ver 08)
  id               INTEGER PK
  name             TEXT     -- "Amigo Sin Límite 100"
  price_mxn        INTEGER
  nav_mb           INTEGER NULL
  valid_days       INTEGER NULL
  social_mode      TEXT     -- UNLIMITED | SHARED_MB | NONE
  social_mb        INTEGER NULL
  verified_at      INTEGER NULL   -- NULL = "por confirmar"
  source           TEXT     -- IFT | SMS | USER

recharge                        -- una compra real
  id               INTEGER PK
  template_id      INTEGER NULL
  sub_id           INTEGER
  activated_at     INTEGER
  price_mxn        INTEGER
  detected_by      TEXT     -- MANUAL | SMS
  sms_hash         TEXT NULL  -- para no duplicar; nunca se guarda el texto

data_bag                        -- bolsas de una recarga
  id               INTEGER PK
  recharge_id      INTEGER
  kind             TEXT     -- NAV | PROMO | APP_BONUS | SOCIAL
  total_bytes      INTEGER NULL   -- NULL = ilimitado
  starts_at        INTEGER
  expires_at       INTEGER
  consume_order    INTEGER

benefit_rule                    -- apps con beneficio
  package_name     TEXT PK
  mode             TEXT     -- DEDUCT_FROM_BENEFIT | COUNT_AGAINST_PLAN | SPLIT
  split_pct        INTEGER NULL
```

Índices: `usage_bucket(start_ts)`, `usage_bucket(uid, start_ts)`.
Retención: 13 meses por defecto, limpieza semanal.

## 5. Ciclo de vida de la medición

| Situación | Mecanismo | Frecuencia |
|---|---|---|
| App abierta en *En vivo* | Corrutina + (si hace falta) Foreground Service | 1–2 s |
| App abierta en otra pantalla | Sincronización al abrir | Una vez |
| App cerrada | `WorkManager` periódico | ~15 min (mínimo del sistema) |
| Reinicio del teléfono | `RECEIVE_BOOT_COMPLETED` → encola sincronización | Una vez |
| Cambio de red (móvil ↔ Wi‑Fi) | `ConnectivityManager.NetworkCallback` mientras hay proceso | Evento |
| Medianoche / cambio de ciclo | `WorkManager` + comprobación al abrir | Diaria |

Regla de ahorro: **el muestreo de 1–2 s solo existe con la vista En vivo abierta y la pantalla encendida.** Nada de wake locks permanentes.

### Fabricantes agresivos con la batería

Xiaomi, Samsung, Huawei, Oppo, Realme y otros pueden matar procesos. Mitigación:
- WorkManager (sobrevive mejor que servicios propios).
- Todo lo importante se reconstruye desde `NetworkStatsManager` al abrir, porque el sistema conserva el historial.
- Pantalla de ayuda: “Excluir de optimización de batería” con enlace directo al ajuste.

**ROM de China (OriginOS 7):** no trae Google Play ni servicios de Google; la app no los necesita. La instalación se hace por APK.

**vivo (OriginOS):** además de la batería, el sistema tiene gestión propia de inicio automático y de consumo en segundo plano. En la Fase 0 se anotan los nombres exactos de esos ajustes en el X300 Ultra y se incluyen en la guía de la app. No se asumen de memoria.

## 6. Permisos

| Permiso | Para qué | Tipo | Cuándo se pide |
|---|---|---|---|
| `PACKAGE_USAGE_STATS` | Leer consumo por app | Especial (el usuario lo activa en Ajustes) | Onboarding, con explicación |
| `POST_NOTIFICATIONS` (Android 13+) | Alertas | En ejecución | Al activar la primera alerta |
| `FOREGROUND_SERVICE` + tipo declarado | Vista En vivo con pantalla apagada o notificación persistente | Normal | No se pide al usuario |
| `RECEIVE_BOOT_COMPLETED` | Reanudar tras reinicio | Normal | No se pide |
| `READ_PHONE_STATE` | Saber qué SIM lleva los datos | En ejecución | Al elegir la SIM principal |
| `READ_SMS` + `RECEIVE_SMS` | Detectar recargas de Telcel (opcional) | En ejecución, sensible | Solo si el usuario activa “Detectar recargas por SMS”. Alternativa: acceso a notificaciones |
| `QUERY_ALL_PACKAGES` o consultas `<queries>` | Mostrar nombre e ícono de cada app | Requiere justificación en Google Play | Ver riesgo R-06 |
| **`INTERNET`** | **NO se declara** | — | Nunca |

Además: `android:allowBackup="false"` para que el historial no salga a la nube del sistema.

## 7. Privacidad y seguridad

- Sin permiso de INTERNET, la app no puede transmitir datos aunque tuviera un fallo.
- Sin analítica ni SDK de terceros con red. Las fallas se ven con `adb`/registros locales en beta.
- Solo se guardan bytes, UID y marcas de tiempo; **nunca** contenido ni direcciones de destino.
- Exportación (si se agrega) siempre iniciada por el usuario, vía selector de archivos del sistema.
- Aviso de privacidad de una página, en español, dentro de la app.

## 8. Rendimiento y batería

- Consultas con ventanas pequeñas y caché en memoria de la última lectura.
- Un solo hilo de medición; el cálculo pesado va en `Dispatchers.Default`.
- La lista de apps usa `LazyColumn` con claves estables.
- Carga de íconos con caché de `LruCache`; sin librería de imágenes externa.
- Presupuesto: < 1 % de batería diaria, < 40 MB de RAM en segundo plano.

## 9. Estructura de repositorio y calidad

```
datoreal/
├─ app/ core/ feature/ work/
├─ docs/               ← estos .md
├─ gradle/libs.versions.toml
└─ .github/workflows/  ← compilar, pruebas, lint
```

- Análisis estático: ktlint + detekt.
- CI: compilación, pruebas unitarias y lint en cada cambio.
- Fuentes falsas (`FakeNetworkStatsSource`) para probar la lógica de deltas, reinicios y proyecciones sin teléfono.

## 10. Alternativas evaluadas

| Alternativa | Veredicto |
|---|---|
| Multiplataforma (Flutter / React Native) | Descartada: casi todo el valor está en APIs nativas de Android |
| VPN local desde el inicio | Diferida: más precisa y con dominios, pero más batería, más complejidad y ocupa la VPN del usuario |
| Raíz / Shizuku | Descartada: reduce el público y complica la distribución |
| Consultar Mi Telcel automáticamente | Descartada: sin API pública, frágil y riesgo de seguridad |
