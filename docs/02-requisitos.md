# 02 · Requisitos

Prioridades con **MoSCoW**: **M**ust (imprescindible), **S**hould (debería), **C**ould (podría), **W**on't (no en v1).

## 1. Requisitos funcionales

### Medición
| ID | Requisito | Prio |
|---|---|---|
| RF-01 | Medir bytes recibidos y enviados por app (UID) en red móvil | M |
| RF-02 | Medir lo mismo en Wi‑Fi, mostrado por separado | M |
| RF-03 | Separar consumo en primer plano y segundo plano | M |
| RF-04 | Mostrar tráfico no atribuible como categorías: *Sistema Android*, *Compartir internet (hotspot)*, *Apps desinstaladas* | M |
| RF-05 | Guardar historial propio en el dispositivo (no depender de la retención del sistema) | M |
| RF-06 | Manejar reinicios del teléfono sin perder ni duplicar bytes | M |
| RF-07 | Velocidad actual de bajada y subida (total y por app) | M |

### Plan y proyección
| ID | Requisito | Prio |
|---|---|---|
| RF-10 | Configurar el plan: GB totales, fecha de inicio, vigencia (mensual o N días) | M |
| RF-11 | Mostrar usado, restante y porcentaje del plan | M |
| RF-12 | Proyectar “al ritmo actual te alcanza hasta…” | M |
| RF-13 | Soportar más de un plan guardado (p. ej. recarga nueva) | S |
| RF-14 | Datos extra / bonos con su propia vigencia (bolsas) | S |
| RF-15 | Catálogo de paquetes Telcel editable (monto, MB, vigencia, redes incluidas) | M |
| RF-16 | Selección rápida “Acabo de recargar”: elegir el paquete, confirmar MB y vigencia, guardar | M |
| RF-17 | Detección automática de recargas leyendo los SMS de Telcel, solo en el teléfono y solo si el usuario la activa | S |
| RF-18 | Redes sociales incluidas: separar “medido total” de “estimado en tu paquete”, con modo por app | M |
| RF-19 | Medir la SIM principal (la que lleva los datos) en teléfonos de dos SIM | M |

### Visualización
| ID | Requisito | Prio |
|---|---|---|
| RF-20 | Pantalla Resumen: plan, hoy, 7 días, top apps | M |
| RF-21 | Pantalla En vivo: velocidad y apps activas, actualizada cada 1–2 s | M |
| RF-22 | Pantalla Apps: lista completa ordenada y filtrable por periodo | M |
| RF-23 | Detalle por app: histórico diario, móvil vs Wi‑Fi, primer vs segundo plano | M |
| RF-24 | Filtros de periodo: Hoy, 7 días, Ciclo, Personalizado | M |
| RF-25 | Unidades: base binaria (1 GB = 1,024 MB) por defecto en el plan Telcel, decimal opcional | M |

### Alertas
| ID | Requisito | Prio |
|---|---|---|
| RF-30 | Notificar al 50 %, 80 % y 100 % del plan (configurable) | M |
| RF-31 | Notificar consumo anómalo (una app supera X MB en segundo plano en un día) | S |
| RF-32 | Notificación persistente opcional con velocidad actual | S |
| RF-33 | Aviso al faltar N días con restante insuficiente | S |
| RF-34 | Aviso de que, al agotarse los MB, Telcel cobra los datos del saldo (prepago) | M |

### Calibración
| ID | Requisito | Prio |
|---|---|---|
| RF-40 | El usuario captura el saldo restante que ve en Mi Telcel y la app compara con lo medido | M |
| RF-41 | Mostrar la desviación (%) y permitir aplicar un factor de ajuste opcional | S |
| RF-42 | Historial de calibraciones | C |

### Extras
| ID | Requisito | Prio |
|---|---|---|
| RF-50 | Widget de pantalla de inicio | S |
| RF-51 | Tile en ajustes rápidos | C |
| RF-52 | Exportar historial a CSV | C |
| RF-53 | Límite por app con aviso | C |
| RF-54 | Tráfico por dominio / bloqueo de apps (VPN local) | W |
| RF-55 | iOS | W |
| RF-56 | Cuentas y nube | W |

## 2. Requisitos no funcionales

| ID | Categoría | Requisito |
|---|---|---|
| RNF-01 | Privacidad | La app **no declara permiso INTERNET**. Ningún dato sale del teléfono |
| RNF-02 | Privacidad | Sin analítica, anuncios ni SDK de terceros que usen red |
| RNF-03 | Precisión | Desviación ≤ 3 % contra Ajustes de Android en pruebas controladas |
| RNF-04 | Latencia | Vista En vivo se refresca cada ≤ 2 s |
| RNF-05 | Batería | ≤ 1 % de batería diaria atribuible a la app en uso normal |
| RNF-06 | Rendimiento | Arranque en frío < 1.5 s; lista de 200 apps a 60 fps |
| RNF-07 | Tamaño | APK < 15 MB |
| RNF-08 | Compatibilidad | Android 8.0 (API 26) o superior; dispositivo de referencia: vivo X300 Ultra, ROM de China (OriginOS 7, Android 17) |
| RNF-09 | Accesibilidad | Contraste WCAG AA, objetivos táctiles ≥ 48 dp, TalkBack, escala de fuente al 200 % |
| RNF-10 | Idioma | Español (México) primero; cadenas externalizadas para traducir |
| RNF-11 | Robustez | Funciona sin conexión; se recupera tras reinicio, modo avión, cambio de SIM |
| RNF-12 | Mantenibilidad | Cobertura de pruebas unitarias ≥ 80 % en la capa de cálculo |

## 3. Historias de usuario y criterios de aceptación

### HU-01 · Ver cuánto he gastado
*Como usuario quiero ver de inmediato cuánto llevo gastado de mi paquete para saber si voy bien.*
- **Dado** un plan de 10 GB configurado, **cuando** abro la app, **entonces** veo el usado, el restante y el % en la parte superior sin hacer scroll.
- El número principal se actualiza al abrir la app y al volver a primer plano.

### HU-02 · Saber quién gasta
*Como usuario quiero una lista de apps ordenada por consumo para identificar al culpable.*
- La lista muestra ícono, nombre, bytes y % del total móvil del periodo.
- Al tocar una app se abre su detalle.
- Apps con 0 bytes en el periodo no aparecen por defecto.

### HU-03 · Ver consumo en vivo
*Como usuario quiero ver qué está descargando ahora mismo mi celular.*
- Muestra velocidad ↓/↑ total y las 5 apps más activas.
- Se actualiza cada 1–2 s mientras la pantalla está encendida y la vista abierta.
- Al salir de la vista deja de muestrear para ahorrar batería.

### HU-04 · Saber cuánto me durará
*Como usuario quiero saber si mi paquete me alcanza hasta el final del ciclo.*
- Muestra la fecha estimada de agotamiento con el ritmo de los últimos 7 días.
- Si el estimado es anterior al fin del ciclo, se marca en ámbar o rojo y se explica.

### HU-05 · Recibir avisos
*Como usuario quiero que la app me avise antes de quedarme sin datos.*
- Notificaciones al 50, 80 y 100 % (editables).
- Una notificación por umbral y por ciclo; no se repite.
- Se pide permiso de notificaciones en el momento adecuado, no al abrir por primera vez.

### HU-06 · Comparar con Telcel
*Como usuario quiero comprobar si lo que mide mi teléfono coincide con Telcel.*
- Pantalla de calibración: el usuario escribe el saldo que ve en Mi Telcel.
- La app muestra: *Telcel dice que usaste X; tu teléfono midió Y; diferencia Z %.*
- Opción de aplicar un factor de ajuste que se refleja en el resumen, marcada visiblemente como “ajustado”.

### HU-07 · Entender el detalle de una app
*Como usuario quiero ver el historial de una app específica.*
- Gráfica de barras diaria (14–30 días), total móvil/Wi‑Fi, primer/segundo plano.
- Texto de apoyo en lenguaje simple: “YouTube gastó 62 % de su consumo en segundo plano”.

### HU-08 · Primer uso sin fricción
*Como usuario nuevo quiero entender qué permiso piden y por qué.*
- Una pantalla explica “Acceso a datos de uso” con un botón que abre el ajuste exacto del sistema.
- La app detecta cuando se concedió y continúa sola.
- Si se niega, se muestra el estado vacío con la explicación, sin bloquear la configuración.

### HU-09 · Compartir internet
*Como usuario quiero ver cuánto gasta mi hotspot.*
- Aparece como categoría “Compartir internet” en el ranking.

### HU-10 · Registrar lo que acabo de recargar
*Como usuario prepago quiero decirle a la app qué paquete puse, o que lo detecte sola.*
- Desde Resumen, “Nueva recarga” abre una lista de montos; al elegir uno, la app propone MB, vigencia y redes incluidas.
- Los valores propuestos son editables y se piden confirmar con el SMS o Mi Telcel.
- Si el usuario activó la detección por SMS, aparece una propuesta “Detecté un paquete de $X; ¿lo registro?” y nada se guarda sin su toque.
- Si ya hay un paquete vigente, se pregunta si se suma como nueva bolsa o reemplaza.

### HU-11 · Redes sociales incluidas
*Como usuario quiero que la app no me cuente lo que Telcel no me cobra.*
- La cifra principal es “estimado en tu paquete”; debajo se muestra “medido en total” y “redes sociales sin costo”.
- En Ajustes puede cambiar, app por app, si se descuenta del beneficio o cuenta contra el paquete.
- Si el paquete tiene bolsa limitada de redes (paquetes chicos), esa bolsa se muestra aparte y, al agotarse, pasa a descontar de navegación.

### HU-12 · Aviso de saldo en riesgo
*Como usuario prepago quiero saber cuándo se me va a cobrar de mi saldo.*
- Aviso al 90 % y al 100 % de la bolsa de navegación, con texto claro.
- Después de agotarse, aviso opcional cada X MB gastados.

## 4. Casos límite que el diseño debe cubrir

- Reinicio del teléfono (los contadores del kernel vuelven a cero).
- Cambio de SIM o eSIM.
- Modo avión durante el muestreo.
- App instalada o desinstalada a mitad del ciclo.
- Cambio de zona horaria o de fecha del sistema.
- Primer arranque: no hay historial; se reconstruye con lo que el sistema retiene y se avisa del rango disponible.
- Doble SIM: v1 mide la SIM principal con la línea de tiempo de datos (`03` §2.5) y muestra la otra como “Otra SIM”.
- Fabricante que restringe el servicio en segundo plano (guía en pantalla).

## 5. Trazabilidad con objetivos

| Objetivo (01) | Requisitos |
|---|---|
| O1 Precisión | RF-01…07, RNF-03 |
| O2 Tiempo real | RF-07, RF-21, RNF-04 |
| O3 Prevención | RF-10…12, RF-30…33 |
| O4 Explicar diferencia con operador | RF-40…42 |
| O5 No ser parte del problema | RNF-01, RNF-02, RNF-05 |
| O6 Interfaz limpia | RF-20…24, RNF-09 |
