# 07 · Plan de pruebas

El objetivo central de las pruebas es responder con evidencia: **¿los números de la app son correctos?** Luego, que sea estable, ligera y comprensible.

## 1. Niveles de prueba

| Nivel | Qué cubre | Herramienta | Cuándo |
|---|---|---|---|
| Unitarias | Cálculo de deltas, reinicios, ciclos, proyecciones, formato de bytes, alertas | JUnit + fuentes falsas | Cada cambio (CI) |
| Integración | Room + repositorios + WorkManager | Pruebas instrumentadas | Cada cambio |
| UI | Flujos clave y estados vacíos | Compose UI Test | Cada cambio |
| Precisión en dispositivo | Coincidencia con Ajustes y con Telcel | Protocolo manual (sección 2) | F0, F5, F6 |
| Batería y memoria | Consumo en uso normal | `adb dumpsys batterystats`, Android Studio Profiler | F5 |
| Compatibilidad | Versiones y marcas | Dispositivos físicos | F5, F6 |
| Accesibilidad | TalkBack, contraste, fuente grande | Accessibility Scanner + manual | F5 |
| Usabilidad | Comprensión de la pantalla principal | 5 usuarios | F3, F6 |

## 2. Protocolo de precisión (el más importante)

### 2.1 Preparación
1. Teléfono con chip Telcel y datos móviles activos; **Wi‑Fi apagado**.
2. Cerrar apps que no se usen; desactivar actualizaciones automáticas durante la prueba.
3. Anotar antes de empezar:
   - Consumo móvil total en **Ajustes de Android** (B₀).
   - Consumo de la app (A₀).
   - Saldo en **Mi Telcel** (T₀) y hora de la lectura.
4. Tener un archivo de prueba de tamaño conocido (p. ej. 100 MB) desde un servidor propio o público.

### 2.2 Ejecución (repetir 5 veces por escenario)
1. Descargar el archivo con el navegador (tamaño conocido = K).
2. Esperar 2 minutos para que se actualicen los contadores.
3. Anotar B₁, A₁ y T₁ (Mi Telcel puede tardar en actualizar; registrar la hora).

### 2.3 Cálculos
```
Δ Ajustes = B₁ − B₀
Δ App     = A₁ − A₀
Δ Telcel  = T₀ − T₁

Desviación app vs Ajustes = (Δ App − Δ Ajustes) / Δ Ajustes
Desviación app vs Telcel  = (Δ App − Δ Telcel)  / Δ Telcel
Sobrecarga contra tamaño  = (Δ App − K) / K
```

### 2.4 Criterios de aceptación
| Comparación | Tolerancia |
|---|---|
| App vs Ajustes de Android | ≤ **3 %** |
| App vs tamaño conocido (K) | Debe ser ligeramente mayor que K (cabeceras); documentar % |
| App vs Telcel | **Sin tolerancia fija**: se registra y se analiza si es estable. Si es estable, la calibración debe reducirla a ≤ 3 % |

### 2.5 Escenarios
| # | Escenario | Qué verifica |
|---|---|---|
| E1 | Descarga simple por datos móviles | Línea base |
| E2 | Descarga grande (500 MB–1 GB) | Que el error no se acumule |
| E3 | Varias apps a la vez | Atribución correcta por app |
| E4 | App en segundo plano | Separación primer/segundo plano |
| E5 | Reinicio del teléfono a mitad de la prueba | Reglas de reinicio (sin doble conteo) |
| E6 | Modo avión y volver a conectar | Continuidad |
| E7 | Cambio Wi‑Fi ↔ móvil durante la descarga | Separación de redes |
| E8 | Hotspot con otro dispositivo | Categoría “Compartir internet” |
| E9 | Apps desinstaladas durante el ciclo | Categoría “Apps desinstaladas” |
| E10 | Cambio de zona horaria o de fecha | Cortes de día y de ciclo |
| E11 | Uso de un día completo (≥ 24 h) | Deriva acumulada vs Ajustes |
| E12 | Uso de una semana completa | Estabilidad y comparación con Telcel |
| E13 | Redes sociales incluidas: 50–100 MB solo en WhatsApp e Instagram, luego 50–100 MB en navegador | Cuánto descuenta realmente Telcel; validar el “estimado en tu paquete” |
| E14 | Cambiar la SIM de datos (principal ↔ otra) durante una descarga | Asignación de bytes a la SIM correcta |
| E15 | Recargar un paquete nuevo con uno vigente | Reglas de acumulación y orden de consumo de bolsas |
| E16 | Agotar el paquete a propósito (prueba corta con un paquete chico) | Aviso de saldo en riesgo y comportamiento “bajo demanda” |

### 2.6 Plantilla de registro

| Fecha | Teléfono / Android | Escenario | K (MB) | B₀ | B₁ | A₀ | A₁ | T₀ | T₁ | Desv. vs Ajustes | Desv. vs Telcel | Notas |
|---|---|---|---|---|---|---|---|---|---|---|---|---|
| | | | | | | | | | | | | |

> Nota: los saldos de Mi Telcel pueden actualizarse con retraso; apunta siempre la hora de cada lectura.

## 3. Pruebas unitarias clave (cálculo)

| Caso | Entrada | Esperado |
|---|---|---|
| Delta normal | lectura 1000 → 1500 | +500 |
| Reinicio | lectura 5000 → 200 | +200, marca de reinicio |
| Mismo intervalo consultado dos veces | mismo bucket dos veces | No duplica |
| Intervalo vacío | sin bytes | 0, sin registro |
| UID desinstalado | UID de eliminadas | Suma a “Apps desinstaladas” |
| Hotspot | UID de tethering | Suma a “Compartir internet” |
| Ciclo mensual con día 31 en mes de 30 | reset_day=31, mes corto | Corta el último día del mes |
| Cambio de zona horaria | UTC‑6 → UTC‑5 | Día no se duplica ni se salta |
| Proyección con 0 consumo | 0 B en 7 días | “No hay suficiente consumo para estimar” |
| Umbral ya disparado | 80 % superado dos veces | Una sola notificación por ciclo |
| Formato de bytes en el límite | 999 999 B y 1 000 000 B | Ambos se muestran como “1.00 MB” (nunca “1000 KB”) |
| Base decimal vs binaria | 1 GB | 1 000 000 000 vs 1 073 741 824 |
| Beneficio en modo “Descontar” | 100 MB de WhatsApp, 300 MB de Chrome | Estimado = 300 MB; medido = 400 MB |
| Beneficio en modo “Contar contra el paquete” | Igual | Estimado = 400 MB |
| Bolsa social limitada agotada | Bolsa de 200 MB, consumo 260 MB | Los 60 MB sobrantes pasan a navegación |
| Orden de consumo de bolsas | Promo vence antes que navegación | Se consume primero la que vence antes |
| Corte de intervalo por cambio de SIM | Cambio a la mitad de un intervalo | El intervalo se parte; cada SIM recibe sus bytes |
| SMS duplicado | El mismo SMS llega dos veces | Una sola recarga propuesta |
| SMS de otro remitente | Mensaje que no es de Telcel | Se ignora y no se guarda |

## 4. Pruebas de batería y rendimiento

| Prueba | Método | Aceptación |
|---|---|---|
| Consumo en reposo (24 h) | `dumpsys batterystats` tras 24 h con la app instalada, sin abrirla | ≤ 1 % |
| Consumo con En vivo abierto 10 min | Perfil de energía | Sin wake locks permanentes; CPU baja |
| Memoria en segundo plano | Android Studio Profiler | < 40 MB |
| Arranque en frío | `adb shell am start -W` | < 1.5 s |
| Lista de 200 apps | Perfil de renderizado | 60 fps, sin jank notable |
| Tamaño de APK | Tras compilar release | < 15 MB |

## 5. Compatibilidad

Matriz mínima de dispositivos físicos:

| Versión Android | Marca sugerida |
|---|---|
| 8.x | Cualquiera disponible |
| 11 | Samsung o Motorola |
| 13 | Samsung o Xiaomi |
| 14 o superior | Samsung, Motorola o Xiaomi |

Verificar por cada una: permiso de uso, lectura por app, sincronización en segundo plano, notificaciones, reinicio.

## 6. Accesibilidad
- Recorrido completo con TalkBack (cada cifra y barra se anuncia con sentido).
- Fuente al 200 % sin cortes ni solapamientos.
- Contraste AA en claro y oscuro.
- Áreas táctiles ≥ 48 dp.
- Respeto de “reducir animaciones”.

## 7. Usabilidad (5 usuarios)
Tareas sin ayuda:
1. “¿Cuántos datos llevas gastados de tu paquete?”
2. “¿Qué app gasta más?”
3. “¿Te alcanzarán hasta fin de mes?”
4. “Configura un aviso al 80 %.”
5. “Compara con lo que dice Mi Telcel.”

Éxito: ≥ 4 de 5 completan cada tarea en < 30 s y sin preguntas.

## 8. Verificaciones de privacidad
- El manifiesto final **no** contiene `android.permission.INTERNET` (`aapt dump permissions`).
- Ninguna dependencia agrega ese permiso al fusionar manifiestos.
- La app no aparece con tráfico propio en sus propios contadores.
- `allowBackup="false"` presente.

## 9. Criterios de salida de F0 (decisión de seguir)

- [ ] Desviación app vs Ajustes ≤ 3 % en E1–E4.
- [ ] Frescura documentada y aceptable para En vivo.
- [ ] Diferencia contra Telcel medida y explicada (o al menos caracterizada).
- [ ] Decisión sobre unidades (decimal o binaria).
- [ ] Decisión sobre alcance de En vivo por app.

Si no se cumplen, se activa el plan B del riesgo R-01 en `06`.
