# Guía 03 · Hacer la prueba de la Fase 0

Objetivo: comprobar con datos reales **si lo que mide DatoReal coincide con Ajustes de Android y con Mi Telcel**, y **qué tan rápido** se refleja el consumo.
El protocolo completo está en `docs/07-plan-de-pruebas.md`. Aquí va la versión práctica.

## Antes de empezar
- Apaga el **Wi-Fi**. Deja los **datos móviles** de la SIM Telcel encendidos.
- Cierra las apps que no uses y pausa las descargas automáticas.
- Ten a la mano un archivo para descargar de **tamaño conocido** (50–100 MB, por ejemplo una actualización o un video cuyo peso veas antes). Anota el tamaño.

## Prueba 1 · Estado (2 minutos)
1. Abre DatoReal, pestaña **Estado**.
2. Toca **Copiar informe** y pégalo en `fase0/resultados-estado.txt`.
Esto me dice qué APIs funcionan en tu teléfono.

## Prueba 2 · Antes y después (10 minutos)
1. Anota el **consumo móvil de Ajustes de Android** (Ajustes → busca “uso de datos”) y tu **saldo de datos en Mi Telcel**. Escribe también la hora.
2. En DatoReal → pestaña **Prueba** → **Marcar INICIO**.
3. Descarga el archivo.
4. Espera **2 minutos** después de que termine.
5. En **Prueba** → **Marcar FIN**.
6. Vuelve a leer Ajustes de Android y Mi Telcel. Anota cuánto subió/bajó cada uno, **en MB**.
7. Escribe esas cifras en los cuadros de la app (la app calcula la desviación). Apaga o enciende el interruptor de la base de MB según cómo estén las cifras que copiaste.
8. Toca **Copiar informe completo** y pégalo en `fase0/resultados-prueba-N.txt` (N = 1, 2, 3…).
9. Repite la prueba **3 a 5 veces**.

## Prueba 3 · Frescura (3 minutos)
1. Pestaña **Prueba** → sección 3 → **Empezar**.
2. Descarga algo grande durante los 2 minutos.
3. La app dice en cuántos segundos se reflejaron los primeros 100 KB en cada fuente.
4. Copia el informe.

## Prueba 4 · En vivo (opcional)
Pestaña **En vivo**: reproduce un video con datos móviles y observa si suben la velocidad y las apps activas.

## Qué me tienes que enviar
Pega aquí en el chat el texto de los informes (o dime la ruta de los archivos en `fase0/`). Con eso decido:
- si la medición por app sirve para “tiempo real”,
- qué base de MB coincide con Mi Telcel,
- cuánta diferencia hay con Telcel y si es estable,
- si hace falta el plan B (VPN local).

También anota en `fase0/registro-pruebas.csv` una fila por prueba.
