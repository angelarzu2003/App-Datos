# Guía 02 · Instalar DatoReal en tu vivo X300 Ultra

Tu teléfono usa la **ROM de China (OriginOS 7)**, sin Google Play. Se instala directo el archivo APK.
Los nombres de los menús pueden variar un poco según el idioma y la versión; si no encuentras algo, busca la palabra en el buscador de Ajustes o dime y lo vemos.

## Opción A · Copiar el APK al teléfono (la más sencilla)

1. Pasa el archivo `app-debug.apk` al teléfono:
   - Con cable USB en modo **transferencia de archivos**, o
   - Enviándotelo por Telegram/WhatsApp a ti mismo, o por correo.
2. En el teléfono abre el archivo con el administrador de archivos.
3. Si pide permiso, activa **“Permitir instalar apps desconocidas”** para esa app (administrador de archivos, navegador o la que hayas usado).
4. Puede salir un **análisis de seguridad** del sistema. Es normal; elige continuar o instalar de todos modos.
5. Abre **DatoReal**.

## Opción B · Con cable y depuración USB (instala directo desde Android Studio)

1. En el teléfono activa las **Opciones de desarrollador**: Ajustes → Acerca del teléfono → Información de versión (o “Versión de software”) → toca **7 veces** el número de versión.
2. Ajustes → Sistema (o Ajustes adicionales) → **Opciones de desarrollador** → activa **Depuración USB**. Si ves una opción parecida a “Depuración USB (ajustes de seguridad)” o “Instalar vía USB”, actívala también.
3. Conecta el cable. Acepta en el teléfono “¿Permitir depuración USB?”.
4. En Android Studio aparecerá tu teléfono arriba. Pulsa el botón verde **Run ▶**.
5. Si Windows no reconoce el teléfono, hay que instalar el controlador USB de vivo; avísame y vemos cuál.

## Dar el permiso a la app
1. Al abrir DatoReal verás **“Dar acceso”**. Tócalo.
2. Android abre la lista de **Acceso a datos de uso**. Busca **DatoReal** y actívalo.
3. Regresa a la app. La pestaña **Estado** ya debería mostrar datos.

Si el botón no te lleva a la pantalla correcta: Ajustes → busca “acceso a datos de uso” (o “Usage access”).

## Evitar que el sistema la cierre
Para las pruebas largas, deja DatoReal sin restricciones de batería: Ajustes → Batería → busca DatoReal → permitir actividad en segundo plano / sin restricciones. En la Fase 0 anotaremos los nombres exactos que usa tu OriginOS.

## La app no tiene internet (a propósito)
No vas a ver un permiso de red, y es intencional: nada de lo que mide sale de tu teléfono.
