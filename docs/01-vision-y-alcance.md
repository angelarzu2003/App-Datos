# 01 · Visión y alcance

## 1. Problema

Los paquetes de datos móviles (Telcel, en este caso) se terminan antes de lo que el usuario espera. Hoy el usuario:

- No sabe **qué app** gastó los datos.
- No ve **cuándo** se gastaron (¿de madrugada? ¿en segundo plano? ¿en la calle?).
- No tiene forma de saber si el consumo que reporta el operador es **coherente** con lo que su teléfono realmente transfirió.
- Descubre el problema cuando el saldo ya llegó a cero.

Las pantallas nativas de Android muestran algo de esto, pero están enterradas en ajustes, no avisan a tiempo, no proyectan cuánto durará el paquete y no se comparan con el saldo del operador.

## 2. Visión

> **La verdad sobre tus datos, medida en tu propio celular.**

Una app pequeña, rápida y silenciosa que responde cuatro preguntas en menos de tres segundos:

1. ¿Cuánto he gastado de mi paquete?
2. ¿Cuánto me queda y para cuántos días me alcanza?
3. ¿Quién lo está gastando (apps)?
4. ¿Qué está consumiendo **ahora mismo**?

## 3. Objetivos

| # | Objetivo | Medida de éxito |
|---|---|---|
| O1 | Contar datos móviles por app con precisión | Diferencia ≤ **3 %** contra los contadores de Ajustes de Android en pruebas controladas |
| O2 | Mostrar consumo en vivo | Actualización cada **1–2 s** en la vista En vivo |
| O3 | Prevenir que el paquete se acabe de sorpresa | Alertas al 50 / 80 / 100 % y proyección de días restantes |
| O4 | Explicar la diferencia con el operador | Función de calibración que muestra la desviación en % |
| O5 | No ser parte del problema | Consumo de batería < **1 % diario**; **0 bytes** enviados por la app (sin permiso de INTERNET) |
| O6 | Interfaz limpia | Pantalla principal entendible sin tutorial; máximo 3 pestañas |

## 4. Qué significa “real” (y qué no)

Esta sección es importante: define lo que la app promete.

**Lo que la app mide:** los bytes que entran y salen por la interfaz de red móvil del teléfono, según los contadores del kernel de Android, atribuidos por aplicación (UID), separados en móvil y Wi‑Fi, y en primer/segundo plano.

**Por qué puede diferir del contador del operador** (hipótesis a verificar en la Fase 0, no afirmaciones):

| Causa posible | Efecto esperado |
|---|---|
| El operador cuenta en su red central; el teléfono cuenta en la capa IP | Pequeñas diferencias de overhead por protocolo |
| Redondeos o bloques de cobro del operador | El operador puede registrar más que el teléfono |
| Tráfico que el teléfono no atribuye a una app (sistema, hotspot, apps desinstaladas) | Existe, pero la app lo mostrará como categorías propias |
| Servicios del operador (llamadas por datos, mensajes multimedia, etc.) | Pueden contar de forma distinta |
| Redes sociales incluidas en el paquete (WhatsApp, Facebook, Messenger, X, Instagram y Snapchat, según paquete) | El teléfono lo cuenta; Telcel no lo descuenta de tus MB. La app lo separa en “estimado en tu paquete” |
| Funciones no incluidas dentro de esas apps (por ejemplo, transmisiones en vivo) | Telcel sí las descuenta, pero el teléfono no distingue la función; el estimado puede quedar corto |
| Equivalencia de unidades: Telcel define 1 GB = 1,024 MB | Si se usara base decimal, se vería una diferencia de ~7 % |
| Ciclos distintos (el operador corta a otra hora o fecha) | Descuadre aparente al inicio/fin del ciclo |
| Uso con otra SIM, o con el chip en otro equipo | No lo ve este teléfono |
| Datos compartidos por hotspot | Se cuentan en el teléfono como “Compartir internet” |

**Lo que la app NO promete:** reproducir exactamente la facturación de Telcel. Promete ser una **segunda fuente de verdad independiente**, medida y reproducible, y dar herramientas (calibración) para cuantificar la diferencia.

## 5. Usuario objetivo

**Persona principal — “Usuario con paquete limitado”**
- Usa Telcel (prepago o pospago) con un paquete de pocos GB.
- Sospecha que los datos “no rinden” y quiere pruebas.
- No es técnico; quiere un número claro y una lista de culpables.
- Android de gama baja o media (Samsung, Motorola, Xiaomi, etc.).

**Persona secundaria — “Quien comparte internet”**
- Usa el teléfono como hotspot y necesita ver cuánto consume eso.

## 6. Alcance

### Dentro de la v1
- Android 8.0 o superior.
- Medición de datos móviles y Wi‑Fi por app, con historial local.
- Vista de resumen del plan, ranking de apps, detalle por app y vista en vivo.
- Configuración del plan (GB, fecha de inicio/corte, vigencia) **y registro de recargas de Telcel** con catálogo editable (ver `08`).
- Reconocimiento de **redes sociales incluidas** en el paquete (WhatsApp, Facebook, Messenger, X, Instagram, Snapchat) y cifra “estimado en tu paquete”.
- Medición de la **SIM principal** en teléfonos de dos SIM.
- Proyección y alertas.
- Calibración manual contra el saldo del operador.
- Modo claro y oscuro, español de México.

### Fuera de la v1 (decisión consciente)
| Fuera | Motivo |
|---|---|
| iOS | iOS no expone consumo por app a apps de terceros |
| Bloquear apps o cortar datos | Requiere VPN local o permisos privilegiados; es otro producto |
| Cuentas, nube, sincronización | Contradice la promesa de privacidad |
| Leer contenido o dominios del tráfico | Invasivo y requiere VPN local |
| Consultar el saldo directo a Telcel | No hay API pública estable; sería frágil |
| Mostrar dos SIM lado a lado | v1 mide la SIM principal (ver `03` §2.5) |

### Posible después (v2+)
- Widget y tile de ajustes rápidos.
- Límites por app con aviso.
- Exportar a CSV.
- **Siguiente versión (confirmado):** “Modo preciso” con VPN local para ver tráfico por dominio y bloquear apps.

## 7. Principios del producto

1. **Honesta antes que bonita:** si no se puede medir algo, se dice.
2. **Privada por diseño:** sin red, sin cuentas, sin anuncios.
3. **Ligera:** no debe gastar batería ni datos para medir datos.
4. **Silenciosa:** solo avisa cuando importa.
5. **Minimalista:** un número protagonista por pantalla.

## 8. Supuestos a validar

| # | Supuesto | Cómo se valida |
|---|---|---|
| S1 | Las APIs de Android devuelven consumo por app sin root | Fase 0 |
| S2 | Los datos del sistema tienen granularidad suficiente para “tiempo real” | Fase 0 (medir latencia) |
| S3 | La diferencia contra Telcel es explicable y estable | Fase 0 y beta |
| S4 | Los fabricantes populares no matan el servicio de medición | Pruebas en 3+ marcas |
| S5 | Los usuarios aceptan conceder “Acceso a datos de uso” | Beta, tasa de finalización del onboarding |

## 9. Métricas de éxito del proyecto

- **Técnicas:** desviación ≤ 3 % vs Ajustes; ≤ 1 % de batería/día; 0 cierres por cada 1000 sesiones.
- **Producto:** ≥ 80 % de testers entienden la pantalla principal sin ayuda; ≥ 70 % completan el onboarding.
- **Valor:** ≥ 70 % de testers identifican al menos una app con consumo inesperado en la primera semana.
