# Guía 01 · Abrir el proyecto y generar la app

El proyecto ya está creado en `D:\App-Datos\DatoReal`. Solo hay que abrirlo.

## 1. Abrir el proyecto
1. En Android Studio: **Open** (Abrir).
2. Elige la carpeta `D:\App-Datos\DatoReal` (la que contiene `settings.gradle.kts`, **no** la carpeta `App-Datos`).
3. Si pregunta “Trust project”, elige **Trust Project**.

## 2. Esperar la sincronización (Gradle sync)
- Abajo a la derecha verás una barra de progreso. La primera vez descarga herramientas y librerías (1–2 GB) y puede tardar **5–20 minutos**.
- Termina bien cuando aparece “Gradle sync finished” o desaparece la barra sin errores en rojo.

### Mensajes que pueden salir (y qué hacer)
| Mensaje | Qué hacer |
|---|---|
| “Install missing platform(s)” / “Android SDK Platform 36” | Clic en **Install** o **Accept** y espera |
| “Update Android Gradle Plugin” / “AGP Upgrade Assistant” | Elige **Remind me later** o cierra el aviso. No actualices todavía |
| “Gradle JDK” o “Invalid Gradle JDK” | Menú **File → Settings → Build, Execution, Deployment → Build Tools → Gradle** y en “Gradle JDK” elige la opción que diga **jbr** o **Embedded JDK** |
| Errores en rojo al terminar el sync | Copia el texto de la pestaña **Build** (abajo) y envíamelo. Puede ser una versión de herramienta que debo ajustar |

## 3. Generar la app (el archivo APK)
1. Menú **Build → Build Bundle(s) / APK(s) → Build APK(s)**.
2. Espera a que aparezca abajo a la derecha “APK(s) generated successfully”.
3. Clic en **locate**. El archivo está en:
   `D:\App-Datos\DatoReal\app\build\outputs\apk\debug\app-debug.apk`

Ese `app-debug.apk` es la app. Sigue con la **guía 02** para instalarla en tu vivo.

## Si algo falla
Envíame el mensaje de error completo. Es normal que el **primer intento** necesite un ajuste: yo escribí el código sin poder compilarlo aquí, y el primer build en tu computadora es la verdadera prueba.
