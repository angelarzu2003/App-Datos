# 06 · Riesgos, decisiones y preguntas abiertas

## 1. Registro de decisiones (ADR)

| ID | Decisión | Estado | Motivo | Consecuencia |
|---|---|---|---|---|
| ADR-01 | Solo Android en v1 | Aceptada | iOS no permite leer consumo por app a apps de terceros | No habrá versión iPhone |
| ADR-02 | Kotlin + Jetpack Compose | Aceptada | Acceso nativo a las APIs de red y UI moderna | Equipo necesita conocer Android |
| ADR-03 | **Sin permiso INTERNET** | Aceptada | Una app que mide datos no debe gastar ni filtrar datos; es el argumento de confianza central | Sin analítica remota, sin actualizaciones de configuración en línea, sin nube |
| ADR-04 | Medición híbrida: `NetworkStatsManager` (historial/por app) + `TrafficStats` (velocidad) | Aceptada, sujeta a F0 | Es lo que ofrece el sistema sin root | La vista En vivo por app depende de la frescura del dato |
| ADR-05 | VPN local diferida a “modo preciso” opcional | Aceptada | Más batería, complejidad y ocupa el slot de VPN | Sin dominios ni bloqueo en v1 |
| ADR-06 | Historial propio en Room | Aceptada | El sistema conserva un historial limitado y variable por fabricante | La app necesita sincronizar con regularidad |
| ADR-07 | Unidades **binarias** (1 GB = 1,024 MB) por defecto en el plan; decimal opcional | Aceptada | Los registros de Telcel ante el IFT definen 1 GB = 1,024 MB | Una sola función de formato; se verifica en F0 |
| ADR-08 | Calibración manual contra Mi Telcel | Aceptada | No hay API pública de Telcel; el usuario es la fuente | Pequeña fricción, pero la diferencia queda cuantificada |
| ADR-09 | 3 pestañas: Resumen, En vivo, Apps | Aceptada | Minimalismo; cada una responde una pregunta distinta | Plan y Ajustes quedan detrás del engranaje |
| ADR-10 | Uso personal; distribución solo por APK en tu teléfono | Aceptada | La app es para uso propio, sin tienda | R-06 deja de bloquear; se retomará si se publica |
| ADR-11 | SIM principal por línea de tiempo de datos | Aceptada, sujeta a F0 | Una app normal no puede filtrar por SIM; solo una SIM lleva datos a la vez | Requiere `READ_PHONE_STATE` y manejar cambios de SIM |
| ADR-12 | Catálogo de paquetes editable; el usuario o el SMS confirman MB y vigencia | Aceptada | Las fuentes públicas se contradicen y las promociones cambian | Fricción mínima, pero el dato es confiable |
| ADR-13 | Registro de recargas: manual en v1, SMS opcional después | Aceptada | El SMS requiere permiso sensible y ejemplos reales del formato | Ver `08` §5 |
| ADR-14 | Cifra principal = “estimado en tu paquete”; el “medido total” siempre visible | Aceptada | Respeta que los datos reales son la base y refleja lo que Telcel descuenta | Hay riesgo de subestimar (R-15) |
| ADR-15 | VPN local (dominios, bloqueo) para la siguiente versión | Aceptada | El usuario lo quiere, pero no en la v1 | Ver B-39 |
| ADR-16 | La Fase 0 usa un **solo módulo Gradle** y solo dependencias de Compose; Room, WorkManager y Hilt se agregan en la Fase 1 | Aceptada | No se puede compilar en el entorno de Claude; menos piezas = menos fallos al primer intento | Reemplaza la lista de módulos de `03` §3 hasta la Fase 1 |
| ADR-17 | Estructura de carpetas: `docs/`, `guias/`, `fase0/`, `datos-ejemplo/`, `DatoReal/` | Aceptada | Separar planeación, instrucciones, resultados y código | Los enlaces entre documentos son relativos dentro de `docs/` |

## 2. Riesgos

Probabilidad (P) e impacto (I): **A**lta, **M**edia, **B**aja.

| ID | Riesgo | P | I | Mitigación | Dueño |
|---|---|---|---|---|---|
| R-01 | Los datos por app de Android no son lo bastante frescos o fiables para “tiempo real” por app | M | A | Spike F0; mostrar total en vivo y apps con retraso indicado; plan B con VPN local | Dev |
| R-02 | La diferencia con Telcel es grande o variable y el usuario percibe la app como “incorrecta” | A | A | Explicarla en la app; calibración; mensajes honestos; documentar causas | Producto |
| R-03 | Fabricantes (Xiaomi, Samsung, etc.) detienen la sincronización en segundo plano | A | M | WorkManager; reconstruir al abrir; guía de batería; pruebas en 3+ marcas | Dev |
| R-04 | Usuarios no conceden “Acceso a datos de uso” (el flujo es poco intuitivo) | M | A | Onboarding claro con botón directo; mostrar privacidad; pruebas de usabilidad | Diseño |
| R-05 | Cambios de comportamiento de las APIs entre versiones de Android | M | M | Matriz de pruebas (8, 11, 13, 14+); aislar en interfaces | Dev |
| R-06 | Políticas de Google Play para ver la lista de apps instaladas (`QUERY_ALL_PACKAGES`) | M | M | Verificar política vigente antes de publicar; alternativa con `<queries>` o distribución por APK/F‑Droid | Producto |
| R-07 | Foreground Service exige declarar tipo y justificar en versiones recientes | M | B | Usar solo con la vista En vivo o notificación opcional; declarar tipo correcto | Dev |
| R-08 | Doble SIM: el reparto por SIM es aproximado (se basa en qué SIM llevaba los datos) | M | M | Ver ADR-11 y R-17; pruebas en F0 | Dev |
| R-09 | Reinicios o cambios de fecha producen doble conteo | M | A | Reglas de delta, `boot_id`, pruebas unitarias dirigidas | Dev |
| R-10 | La app consume más batería de la prometida | B | A | Muestreo solo en pantalla En vivo; perfil en F5; límites de frecuencia | Dev |
| R-11 | Las apps con UID compartido confunden la atribución | B | B | Mostrar grupo y nota informativa | Dev |
| R-12 | Alcance crece (bloqueo, dominios, nube) | M | M | Mantener “Fuera de v1” en `01`; revisar cada fase | Producto |
| R-13 | Sin INTERNET, no hay forma de recibir fallas de usuarios | M | B | Pantalla “Copiar informe de diagnóstico” que el usuario comparte manualmente | Dev |
| R-14 | El catálogo de paquetes queda desactualizado o las fuentes se contradicen | A | M | Valores “por confirmar”, confirmación del usuario, SMS manda sobre el catálogo, revisión de registros IFT | Producto |
| R-15 | El “estimado en tu paquete” subestima porque Telcel sí descuenta funciones no incluidas (ej. transmisiones en vivo) | A | M | Mostrar siempre el medido total; calibración contra Mi Telcel | Dev |
| R-16 | El permiso de SMS es sensible o el usuario no lo da | M | B | Camino manual siempre disponible; alternativa por notificaciones | Dev |
| R-17 | La app no detecta a tiempo el cambio de SIM de datos y asigna mal los bytes | M | M | Escuchar el evento, sincronizar al cambio, pruebas F0 (6b), plan B de una sola SIM activa. **Por ahora se ignora la segunda SIM** | Dev |
| R-18 | La ROM de China (OriginOS 7) cierra apps en segundo plano de forma más agresiva y no trae Google Play | A | M | Guía de ajustes de vivo, reconstruir el historial al abrir, instalar por APK | Dev |
| R-19 | El primer intento de compilar falla por versiones de herramientas o errores de código no probados | A | B | Guía de errores comunes; el usuario copia el mensaje y Claude lo corrige | Claude |

## 3. Respuestas recibidas y preguntas pendientes

### Respondidas (15 nov 2026)

| # | Tema | Respuesta | Dónde quedó |
|---|---|---|---|
| P1 | Teléfono | vivo X300 Ultra, **ROM de China con OriginOS 7** (Android 17 según las fuentes) | `03`, `05` F0 |
| P2 | Línea | Prepago | `08` |
| P3 | Paquetes | Que la app reconozca los paquetes que se recarguen, o poder registrarlos a mano; y que considere las redes sociales incluidas | `08`, RF-15…18 |
| P4 | SIM | Tiene dos SIM, pero **por ahora solo se usa la Telcel** (principal); la otra se ignora | `03` §2.5, RF-19 |
| P5 | Uso | Personal | ADR-10 |
| P6 | Quién construye | Claude escribe todo el código | `05` |
| P7 | Datos | Siempre los datos reales que se gastan | ADR-14 |
| P8 | Tema | Seguir al sistema | `04` |
| P9 | VPN local, bloqueo | Para la siguiente versión | ADR-15, B-39 |
| P10 | Versión del teléfono | **ROM de China, OriginOS 7** (sin Google Play) | ADR-16, `guias/` |
| P11 | Herramientas | No tiene Android Studio; quiere que Claude cree toda la estructura | `guias/00`, `DatoReal/` |

### Pendientes

| # | Pregunta | Por qué importa |
|---|---|---|
| Q1 | **2–3 SMS reales de Telcel** (al recargar y al activar un paquete), sin tu número ni saldo | Sin el formato real no puedo escribir el lector de recargas (RF-17) |
| Q5 | ¿Qué paquete tienes ahora (monto y fecha de recarga)? | Sirve para precargar la primera prueba y comparar contra Mi Telcel |

## 4. Hipótesis sobre por qué “los datos no duran” (para verificar con la propia app)

La app existe para comprobar esto con datos. Lista de sospechosos habituales:

1. Actualizaciones automáticas de apps y del sistema usando datos móviles.
2. Video o contenido que se reproduce solo (redes sociales, previsualizaciones).
3. Respaldos de fotos o archivos a la nube.
4. Apps con actividad intensa en segundo plano.
5. Hotspot usado por otra persona o dispositivo.
6. Diferencia de conteo entre el teléfono y el operador (ver `01` §4).
7. Datos consumidos por servicios del operador o por tráfico que el teléfono no atribuye a ninguna app.
8. Vigencia o corte del paquete distinto al que se asume.

Tras las primeras semanas de uso, la app debe poder confirmar o descartar cada una.

## 5. Cómo se mantiene este documento

- Cada cambio relevante agrega una fila nueva en el ADR (no se borra la anterior; se marca “Reemplazada”).
- Los riesgos se revisan al cierre de cada fase.
- Las preguntas abiertas se mueven al ADR cuando se resuelven.
