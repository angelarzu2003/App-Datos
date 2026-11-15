# App-Datos · DatoReal

App para Android que mide cuántos datos móviles usa realmente tu celular y qué app los gasta, en tiempo real, con diseño minimalista. Pensada para tu **vivo X300 Ultra** (ROM de China, OriginOS 7) con **Telcel prepago**.

## Mapa de carpetas

```
App-Datos\
├─ README.md                ← este archivo
├─ docs\                    ← planeación completa (8 documentos)
│   ├─ README.md            ← resumen y decisiones
│   ├─ 01-vision-y-alcance.md
│   ├─ 02-requisitos.md
│   ├─ 03-arquitectura-tecnica.md
│   ├─ 04-diseno-ux-ui.md
│   ├─ 05-roadmap-y-backlog.md
│   ├─ 06-riesgos-y-decisiones.md
│   ├─ 07-plan-de-pruebas.md
│   └─ 08-paquetes-telcel.md
├─ guias\                   ← instrucciones paso a paso para ti
│   ├─ 00-instalar-android-studio.md
│   ├─ 01-abrir-y-compilar.md
│   ├─ 02-instalar-en-tu-vivo.md
│   └─ 03-hacer-la-prueba-fase0.md
├─ fase0\                   ← aquí van los resultados de las pruebas
│   ├─ plantilla-resultados.md
│   └─ registro-pruebas.csv
├─ datos-ejemplo\
│   ├─ sms-telcel\          ← pega aquí tus SMS de Telcel (LEEME.md)
│   └─ catalogo\paquetes-telcel.seed.json
└─ DatoReal\                ← el proyecto de Android (se abre con Android Studio)
    ├─ app\src\main\java\mx\datoreal\app\
    │   ├─ MainActivity.kt
    │   ├─ measurement\     ← lectura de datos del sistema
    │   ├─ ui\              ← pantallas y tema
    │   └─ util\            ← formatos y periodos
    └─ gradle\libs.versions.toml   ← versiones de herramientas
```

## Dónde estamos

| Fase | Estado |
|---|---|
| Planeación | Hecha (carpeta `docs\`) |
| **Fase 0 · Prototipo de diagnóstico** | **Código escrito, falta compilarlo y probarlo en tu teléfono** |
| Fase 1 en adelante | Pendiente (depende de los resultados de la Fase 0) |

La app actual (Fase 0) tiene cuatro pestañas de diagnóstico: **Estado**, **Apps**, **En vivo** y **Prueba**. Su único fin es comprobar con tu teléfono real qué tan precisa y rápida es la medición antes de construir el resto.

## Qué sigue (tú)
1. Instalar Android Studio → `guias\00`.
2. Abrir el proyecto y generar el APK → `guias\01`.
3. Instalarlo en tu vivo → `guias\02`.
4. Hacer las pruebas → `guias\03`.
5. Pegarme los informes, y los 2–3 SMS de Telcel (`datos-ejemplo\sms-telcel\`).

## Reglas del proyecto
- La app **no tiene permiso de internet**: nada sale de tu teléfono.
- Todo lo importante se decide en `docs\06-riesgos-y-decisiones.md`.
