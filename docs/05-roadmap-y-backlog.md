# 05 · Roadmap y backlog

Estimaciones para **1 desarrollador a tiempo completo**. Si es medio tiempo, duplica los tiempos. Son estimaciones de planeación, no compromisos.

## 1. Resumen de fases

| Fase | Nombre | Duración | Resultado | Puerta (gate) |
|---|---|---|---|---|
| **F0** | Spike técnico | 1 semana | Prototipo mínimo que lee consumo por app en un celular real con Telcel | ¿Los números coinciden con Ajustes ≤ 3 %? ¿Qué tan fresco es el dato? |
| **F1** | Fundaciones | 1–2 sem | Proyecto, tema, navegación, base de datos, permisos | App compila, tema claro/oscuro, onboarding de permiso |
| **F2** | Medición núcleo | 2 sem | Snapshots, deltas, reinicios, categorías especiales, WorkManager | Pruebas unitarias ≥ 80 % en cálculo; historial correcto tras reinicio |
| **F3** | UI del MVP | 2 sem | Resumen, Apps, Detalle, Plan | Un usuario nuevo entiende el resumen sin ayuda |
| **F4** | En vivo y alertas | 1–2 sem | Vista En vivo, notificaciones, proyección | Latencia ≤ 2 s; alertas sin repetirse |
| **F5** | Calibración y pulido | 1–2 sem | Calibración con Telcel, accesibilidad, rendimiento | Auditoría de accesibilidad y batería ≤ 1 %/día |
| **F6** | Beta cerrada | 2 sem | 5–10 usuarios de Telcel con distintas marcas | Criterios de éxito de `01` §9 |
| **F7** | Lanzamiento v1.0 | 1 sem | Distribución y documentación | Checklist de salida |

**Total hasta beta:** ~11–14 semanas (se sumó el trabajo de paquetes, redes sociales y SIM principal). **Hasta v1.0:** ~14–17 semanas.

```
Sem  1   2   3   4   5   6   7   8   9  10  11  12  13
F0  ███
F1      ██████
F2          ████████
F3                  ████████
F4                          ██████
F5                              ██████
F6                                  ████████
F7                                          ███
```

## 2. Detalle por fase

### F0 · Spike técnico (la más importante)
Objetivo: **eliminar la mayor incertidumbre antes de invertir en diseño y UI.**

Tareas:
1. Proyecto mínimo en Android Studio (carpeta `DatoReal/`, ya creada con cuatro pestañas de diagnóstico: Estado, Apps, En vivo y Prueba). Guías en `guias/`.
2. Solicitar *Acceso a datos de uso* y listar consumo móvil por app con `NetworkStatsManager`.
3. Leer `TrafficStats` total y por UID; anotar qué funciona por versión de Android.
4. Medir la **frescura**: descargar un archivo y ver en cuántos segundos aparece reflejado.
5. Prueba de precisión: descarga de tamaño conocido (p. ej. 100 MB) por datos móviles; comparar contra Ajustes de Android y contra Mi Telcel antes/después.
6. Probar en el **vivo X300 Ultra** con la SIM Telcel principal (y, si es posible, en un segundo teléfono de otra marca).
6b. Probar la **línea de tiempo de SIM**: cambiar la SIM de datos y ver si la app lo detecta y a qué SIM asigna los bytes.
6c. Anotar los ajustes exactos de OriginOS de inicio automático y consumo en segundo plano.
7. Escribir un informe de 1–2 páginas: `docs/f0-resultados.md`.

Decisiones que sale de F0:
- ¿La vista En vivo puede mostrar apps o solo total?
- ¿Base decimal o binaria coincide mejor con Telcel?
- ¿Qué tan grande es la diferencia con Telcel? ¿Es constante?
- ¿Hace falta VPN local para cumplir la promesa?

**Si F0 falla** (los datos por app no son confiables): el plan B es el “modo preciso” con VPN local, que se adelanta y cambia el alcance (ver riesgo R-01).

### F1 · Fundaciones
- Repositorio, módulos Gradle, CI, ktlint/detekt.
- Tema (colores, tipografía, espaciado) según `04`.
- Navegación con 3 destinos y pantallas vacías.
- Room con entidades y migraciones.
- Hilt con fuentes reales y falsas.
- Onboarding y detección del permiso.

### F2 · Medición núcleo
- `NetworkStatsSource` y `TrafficStatsSource` detrás de interfaces.
- Reglas de delta, reinicio y reemplazo de buckets.
- Atribución especial (sistema, hotspot, desinstaladas).
- Sincronización inicial (recuperar lo que el sistema retiene).
- `WorkManager` periódico + receptor de arranque.
- Pruebas unitarias exhaustivas con fuentes falsas.

### F3 · UI del MVP
- Resumen (héroe, barra, hoy/ayer/7 días, top apps).
- Lista de Apps con filtros de periodo.
- Detalle de app (barras diarias, móvil/Wi‑Fi, primer/segundo plano).
- Pantalla Plan y cálculo de ciclo.
- Estados vacíos y de error.

### F4 · En vivo y alertas
- Muestreo cada 1–2 s solo con la vista abierta.
- Velocidad, sparkline y apps activas.
- Notificaciones por umbral y por proyección, sin repetirse por ciclo.
- Notificación persistente opcional.

### F5 · Calibración y pulido
- Pantalla de calibración y factor de ajuste.
- Auditoría de accesibilidad (TalkBack, contraste, fuente 200 %).
- Perfil de batería y memoria.
- Guía para fabricantes agresivos.
- Textos finales en español.

### F6 · Beta cerrada
- Distribución por APK firmado o pruebas internas.
- Formulario corto de comentarios (fuera de la app).
- Registro de la diferencia contra Telcel por tester (con permiso).
- Corrección de errores y ajuste de proyecciones.

### F7 · Lanzamiento
- Ficha de tienda, capturas, aviso de privacidad.
- Declaración de permisos (ver R-06).
- Versión 1.0 firmada.

## 3. Backlog priorizado

Tamaños: **S** (≤ 1 día) · **M** (2–3 días) · **L** (4–5 días) · **XL** (> 1 semana).

| ID | Historia / tarea | Req. | Prio | Fase | Tam. | Depende de |
|---|---|---|---|---|---|---|
| B-01 | Spike: leer consumo por app con `NetworkStatsManager` | RF-01 | M | F0 | M | — |
| B-02 | Spike: prueba de precisión y frescura | RNF-03 | M | F0 | M | B-01 |
| B-03 | Informe de resultados F0 | — | M | F0 | S | B-02 |
| B-04 | Proyecto, módulos, CI, lint | — | M | F1 | M | B-03 |
| B-05 | Tema claro/oscuro y tokens | — | M | F1 | M | B-04 |
| B-06 | Navegación de 3 destinos | — | M | F1 | S | B-05 |
| B-07 | Room: entidades, DAOs, migraciones | RF-05 | M | F1 | M | B-04 |
| B-08 | Onboarding + detección de permiso de uso | HU-08 | M | F1 | M | B-06 |
| B-09 | Interfaces de fuentes + implementaciones | RF-01,02 | M | F2 | L | B-07 |
| B-10 | Lógica de deltas y reinicios | RF-06 | M | F2 | L | B-09 |
| B-11 | Atribución sistema/hotspot/desinstaladas | RF-04 | M | F2 | M | B-09 |
| B-12 | Primer/segundo plano | RF-03 | M | F2 | M | B-09 |
| B-13 | WorkManager periódico + receptor de arranque | RF-05 | M | F2 | M | B-10 |
| B-14 | Sincronización inicial del historial | RF-05 | M | F2 | M | B-10 |
| B-15 | Pantalla Resumen | RF-20 | M | F3 | L | B-10 |
| B-16 | Cálculo de ciclo y plan | RF-10,11 | M | F3 | M | B-07 |
| B-17 | Pantalla Plan | RF-10 | M | F3 | M | B-16 |
| B-18 | Pantalla Apps con periodos | RF-22,24 | M | F3 | L | B-10 |
| B-19 | Detalle de app con barras diarias | RF-23 | M | F3 | L | B-18 |
| B-20 | Estados vacíos y errores | — | M | F3 | M | B-15 |
| B-21 | Proyección (“te alcanza hasta…”) | RF-12 | M | F4 | M | B-16 |
| B-22 | Vista En vivo | RF-07,21 | M | F4 | L | B-09 |
| B-23 | Notificaciones por umbral | RF-30 | M | F4 | M | B-16 |
| B-24 | Notificación persistente de velocidad | RF-32 | S | F4 | M | B-22 |
| B-25 | Alertas de consumo anómalo | RF-31 | S | F4 | M | B-12 |
| B-26 | Aviso por proyección | RF-33 | S | F4 | S | B-21 |
| B-27 | Calibración con Telcel | RF-40,41 | M | F5 | L | B-16 |
| B-28 | Auditoría de accesibilidad | RNF-09 | M | F5 | M | B-20 |
| B-29 | Perfil de batería y memoria | RNF-05 | M | F5 | M | B-22 |
| B-30 | Guía para fabricantes agresivos | — | S | F5 | S | B-13 |
| B-31 | Unidades decimal/binaria | RF-25 | S | F5 | S | B-15 |
| B-32 | Beta cerrada y correcciones | — | M | F6 | XL | B-28 |
| B-33 | Ficha de tienda y privacidad | — | M | F7 | M | B-32 |
| B-34 | Múltiples planes | RF-13 | S | v1.1 | M | B-17 |
| B-35 | Widget | RF-50 | S | v1.1 | L | B-15 |
| B-36 | Exportar CSV | RF-52 | C | v1.1 | M | B-18 |
| B-37 | Tile de ajustes rápidos | RF-51 | C | v1.1 | S | B-22 |
| B-38 | Límites por app | RF-53 | C | v1.2 | L | B-25 |
| B-39 | Modo preciso con VPN local (confirmado para la siguiente versión) | RF-54 | C | v2 | XL | B-32 |
| B-40 | Bajar los registros IFT vigentes de los 12 paquetes y blindar el catálogo | RF-15 | M | F1 | M | — |
| B-41 | Catálogo de paquetes en Room con datos semilla y “por confirmar” | RF-15 | M | F2 | M | B-07, B-40 |
| B-42 | SIM principal: línea de tiempo y asignación de bytes | RF-19 | M | F2 | L | B-09 |
| B-43 | Pantalla Nueva recarga (selección rápida) | RF-16 | M | F3 | L | B-41 |
| B-44 | Bolsas de datos y orden de consumo | RF-14 | S | F3 | L | B-43 |
| B-45 | Redes sociales incluidas y cifra “estimado en tu paquete” | RF-18 | M | F3 | L | B-44, B-12 |
| B-46 | Aviso de saldo en riesgo al agotarse el paquete | RF-34 | M | F4 | M | B-44 |
| B-47 | Detección de recargas por SMS de Telcel | RF-17 | S | F5 | L | B-43 y SMS de ejemplo |
| B-48 | Guía de ajustes de OriginOS | — | S | F5 | S | B-13 |

## 4. Definición de “terminado” (DoD)

Una tarea está terminada cuando:
- Cumple sus criterios de aceptación (`02`).
- Tiene pruebas unitarias si toca cálculo, y prueba de UI si es una pantalla clave.
- Pasa lint y compila en CI.
- Se ve bien en claro y oscuro y con fuente grande.
- Tiene descripción para TalkBack.
- No agrega permisos nuevos sin decisión registrada en `06`.
- Se probó en un teléfono real, no solo en emulador (los emuladores no tienen red móvil real).

## 5. Checklist de salida v1.0

- [ ] Desviación ≤ 3 % vs Ajustes en las pruebas de `07`.
- [ ] Batería ≤ 1 %/día en 3 teléfonos.
- [ ] Sin permiso INTERNET en el manifiesto (verificado en el APK final).
- [ ] Accesibilidad AA revisada.
- [ ] Textos en español revisados por una persona ajena al proyecto.
- [ ] Aviso de privacidad publicado.
- [ ] Probada en Android 8, 11, 13 y 14 o superior.
- [ ] Probada en al menos Samsung, Motorola y Xiaomi.
- [ ] Guía de usuario de una página.

## 6. Primeros 5 pasos (para arrancar ya)

1. Enviar 2–3 SMS de ejemplo de Telcel (sin tu número) y responder las preguntas que quedan abiertas en `06`.
2. Usar tu vivo X300 Ultra para el spike; Claude escribe el código y tú solo lo compilas e instalas (Android Studio, una sola vez).
3. Hacer F0 y registrar resultados.
4. Con los resultados, confirmar o ajustar `03` y `04`.
5. Prototipo en Figma de las 7 pantallas mientras se avanza en F1.
