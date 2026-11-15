# DatoReal — Medidor real de datos móviles

> Nombre de trabajo. Cámbialo cuando quieras; solo afecta al paquete `mx.datoreal.app` y a los textos.

App para Android que mide **cuántos datos móviles usa realmente tu celular, y cuáles apps los gastan**, en tiempo real, sin depender de lo que reporte el operador (Telcel).

## El problema en una línea

Los datos del paquete se acaban antes de lo esperado y no hay forma confiable de saber **quién** los consumió ni **cuánto** gastó cada app.

## La solución en una línea

Leer los contadores que el propio sistema Android mantiene por aplicación, guardarlos en un historial local, y mostrarlos con una interfaz minimalista: cuánto llevas, cuánto te queda, quién gasta, y qué pasa ahora mismo.

## Decisiones clave (resumen)

| Tema | Decisión |
|---|---|
| Plataforma | **Solo Android** (iOS no permite ver datos por app) |
| Fuente de medición | Contadores del kernel vía `NetworkStatsManager` + `TrafficStats` |
| Privacidad | **Sin permiso de INTERNET**: la app no puede enviar nada fuera del teléfono |
| Datos guardados | Solo locales, en el dispositivo |
| Stack | Kotlin, Jetpack Compose, Room, WorkManager, Hilt |
| Android mínimo | 8.0 (API 26) |
| Diseño | Minimalista, claro/oscuro, un solo color de acento, números grandes |
| Idioma inicial | Español (México) |
| Teléfono de referencia | vivo X300 Ultra, ROM de China, OriginOS 7 (Android 17); se usa una SIM Telcel prepago |
| Paquetes Telcel | Catálogo editable + selección rápida + detección opcional por SMS (ver `08`) |
| Redes sociales incluidas | La app las reconoce y separa *medido total* de *estimado en tu paquete* |
| Unidades | 1 GB = 1,024 MB en el plan (así lo definen los registros de Telcel) |
| SIM | Se mide la SIM principal (la que lleva los datos) |
| VPN local (bloqueo, dominios) | Confirmado para la **siguiente versión**, no la v1 |

## Sobre la palabra “real”

La app mide lo que **el celular ve pasar por su interfaz de red móvil**, byte por byte, por aplicación. Es la medición más precisa que se puede hacer desde el teléfono, pero **no necesariamente coincide al 100 % con el contador de Telcel** (ver `01-vision-y-alcance.md`, sección 4). Por eso el proyecto incluye una función de **calibración** contra el saldo que reporta Mi Telcel, para que la diferencia sea visible y medible en lugar de un misterio.

## Índice de documentos

| # | Archivo | Contenido |
|---|---|---|
| 01 | [`01-vision-y-alcance.md`](01-vision-y-alcance.md) | Problema, objetivos, usuarios, alcance, qué significa “real”, métricas de éxito |
| 02 | [`02-requisitos.md`](02-requisitos.md) | Requisitos funcionales y no funcionales, historias de usuario, criterios de aceptación |
| 03 | [`03-arquitectura-tecnica.md`](03-arquitectura-tecnica.md) | Cómo se mide, capas, modelo de datos, permisos, servicio en segundo plano |
| 04 | [`04-diseno-ux-ui.md`](04-diseno-ux-ui.md) | Principios, paleta, tipografía, componentes, wireframes de cada pantalla, textos |
| 05 | [`05-roadmap-y-backlog.md`](05-roadmap-y-backlog.md) | Fases, hitos, backlog priorizado, definición de “terminado” |
| 06 | [`06-riesgos-y-decisiones.md`](06-riesgos-y-decisiones.md) | Registro de decisiones (ADR), riesgos y preguntas abiertas |
| 07 | [`07-plan-de-pruebas.md`](07-plan-de-pruebas.md) | Cómo comprobar que los números son correctos, pruebas automáticas y de batería |
| 08 | [`08-paquetes-telcel.md`](08-paquetes-telcel.md) | Paquetes Telcel, redes sociales sin costo, registro de recargas y detección por SMS |

## Orden de lectura recomendado

1. `01` para confirmar que el problema y el alcance son los correctos.
2. `06` (sección “Preguntas abiertas”) para decidir lo que falta.
3. `04` para validar la dirección visual.
4. `03`, `05` y `07` cuando se vaya a construir.

## Estado

- **Fase actual:** Fase 0. El código del prototipo de diagnóstico está en `../DatoReal/`; las guías para compilarlo están en `../guias/`.
- **Siguiente paso:** Fase 0, *spike técnico* en un celular real con chip Telcel, para confirmar la precisión de las APIs antes de diseñar a fondo (ver `05`).
- **Creado:** 14 de noviembre de 2026. **Actualizado:** 15 de noviembre de 2026 (equipo de pruebas, paquetes Telcel, redes sociales, SIM principal).
- **Quién construye:** Claude escribe el código; el usuario solo compila e instala en su teléfono (ver `05` y `../guias/`).

## Glosario rápido

- **UID:** identificador que Android asigna a cada app; el sistema cuenta el tráfico por UID.
- **Bucket:** intervalo de tiempo (p. ej. 1–2 h) en el que el sistema agrupa bytes.
- **Primer plano / segundo plano:** tráfico mientras la app está en pantalla vs. cuando no.
- **Ciclo:** periodo de vigencia del paquete de datos (mensual o de N días).
- **Calibración:** comparar lo medido por la app con el saldo que reporta el operador.
