# Memoria Descriptiva — Staticdata

## 1. Identificación

| Campo | Valor |
|---|---|
| Nombre | Staticdata |
| Package | com.educalab.staticdata |
| Versión | 1.0.0 |
| Plataforma | Android nativo (Kotlin + Jetpack Compose) |
| Público objetivo | Niñas y niños de 9 a 13 años |
| Área curricular | Estadística descriptiva básica (dato, tipos de dato, frecuencia, moda, porcentaje, muestreo) |
| Conectividad | Ninguna requerida; funciona 100% offline |

## 2. Problema

La estadística suele introducirse con hojas de ejercicios repetitivos
(tablas ya hechas, preguntas de opción múltiple) que no dejan que el niño
*recoja*, *organice* ni *interprete* datos por sí mismo. El resultado es
que se memorizan fórmulas ("moda es lo que más se repite") sin entender
para qué sirven ni cómo se llega a ellas a partir de datos reales.

## 3. Justificación

Staticdata convierte cada concepto estadístico en una tarea de
investigación con propósito narrativo: contar frutas de un cesto para
decidir qué comprar, clasificar mascotas para reconstruir un registro,
comparar muestras para ver si "siempre gana lo mismo". La app obliga a
manipular datos reales (contarlos, ordenarlos, clasificarlos, calcular
porcentajes) en vez de reconocer la respuesta correcta entre cuatro
opciones. Todo el cálculo mostrado en pantalla proviene de un motor de
dominio real (`StatsEngine`) aplicado sobre datos igualmente reales
(generados una vez, de forma determinista, al instalar la app), nunca de
valores inventados a mano para "que cuadre" con la pregunta.

## 4. Objetivos

**General**: ofrecer una introducción práctica y entretenida a la
estadística descriptiva para niños de 9 a 13 años mediante una app Android
offline con identidad propia.

**Específicos**:
- Enseñar qué es un dato y la diferencia entre dato categórico y numérico.
- Permitir crear encuestas propias y ver sus resultados calculados en vivo.
- Enseñar a construir e interpretar tablas de frecuencia (conteo,
  frecuencia relativa, porcentaje).
- Enseñar el concepto de moda con datos reales, no con una definición.
- Introducir la idea de muestreo y variabilidad muestral mediante un
  laboratorio manipulable.
- Dar sensación de progreso (niveles, insignias, casos) ligada siempre a
  acciones reales del jugador.

## 5. Público

Niños y niñas de 9 a 13 años con capacidad de lectura autónoma de textos
breves. La interfaz evita estética "de preescolar" (sin exceso de emojis,
sin lenguaje condescendiente) y usa una identidad de "agencia de
detectives" pensada para sentirse aventurera e inteligente, no infantil.

## 6. Alcance y exclusiones

**Incluye**: los 11 módulos descritos en la sección 9, generación y
persistencia real de datos, motor estadístico propio, gamificación local
basada en acciones reales, ilustraciones vectoriales propias.

**Excluye explícitamente**: cuentas de usuario, login, sincronización en
la nube, publicidad, compras dentro de la app, analítica/telemetría,
rankings entre usuarios, chat o comunidad entre menores, estadística
inferencial avanzada (desviación típica, regresión, probabilidad formal).

## 7. Requisitos funcionales (resumen)

- RF1: crear/editar un perfil local (alias + avatar) sin datos personales.
- RF2: explorar un catálogo de 30 datasets semilla (categóricos y numéricos).
- RF3: crear encuestas locales (pregunta ≤ 80 caracteres, 2–6 opciones) y
  registrar respuestas.
- RF4: calcular conteo, frecuencia relativa, porcentaje y moda de
  cualquier dataset o encuesta mediante `StatsEngine`.
- RF5: resolver 10 casos narrativos con 3 ejercicios cada uno (30 en
  total), de 6 mecánicas distintas.
- RF6: clasificar datos en contenedores (módulo Organizador).
- RF7: extraer muestras aleatorias reproducibles de una población y
  comparar variabilidad entre tiradas (Laboratorio de muestras).
- RF8: acumular XP, subir de nivel, desbloquear casos e insignias según
  reglas deterministas (`ProgressRules`).
- RF9: persistir todo el progreso entre sesiones (Room).

## 8. Requisitos no funcionales

- RNF1: funcionamiento 100% offline (sin permiso INTERNET).
- RNF2: sin recolección de datos personales identificables.
- RNF3: minSdk 24 (Android 7.0) — targetSdk 34.
- RNF4: tiempo de sesión objetivo de 5–20 minutos, con guardado automático.
- RNF5: cobertura de pruebas unitarias del dominio y persistencia (71
  pruebas, ver `docs/BUILD_REPORT.md`).
- RNF6: accesibilidad básica (contraste suficiente, texto + icono en
  estados, tamaños de toque ≥ 44dp).

## 9. Módulos y pantallas

1. **Onboarding** (3 pantallas de introducción + creación de perfil).
2. **Oficina de casos** (Home/dashboard): tablero con próxima misión,
   XP/nivel y accesos a los demás módulos.
3–4. **Academia**: mini-juego de clasificación categórica/numérica con
   10 ítems curados y explicación tras cada respuesta.
5. **Encuestas**: creación + respuesta + gráfico de resultados en vivo.
6. **Organizador y clasificación**: clasificar datos reales en
   contenedores por toque.
7. **Tablas de frecuencia**: el niño estima el conteo de cada categoría y
   lo compara con el resultado real calculado por `StatsEngine`.
8. **Moda y porcentajes**: exploración de tabla completa + calculadora de
   "predice el porcentaje antes de verlo".
9. **Laboratorio de muestras**: extracción de muestras reproducibles y
   comparación de variabilidad entre tiradas.
10. **Casos** (lista) y **Detalle de caso** (reproductor de ejercicios de
    3 retos con 6 mecánicas distintas: ordenar, dato extraño, completar
    tabla, identificar moda, interpretar porcentaje, clasificar tipo).
11. **Progreso y colección**: XP, nivel, estadísticas propias y galería
    de 10 insignias ilustradas.
12. **Perfil**: edición de alias/avatar y preferencias de sonido/háptica.

## 10. Flujo principal

Onboarding → Oficina de casos → (elige módulo) → interacción real con
datos → feedback educativo inmediato → XP/insignias → vuelta al tablero.
El progreso siempre puede pausarse y continuarse: todo se guarda en Room
tras cada acción relevante.

## 11. Arquitectura

MVVM + Repository Pattern sobre tres capas (`data`, `domain`, `ui`).
Ver detalle en `docs/MANUAL_TECNICO.md`.

## 12. Datos

17 entidades Room (perfil, progreso, datasets/variables/valores, casos,
encuestas/opciones/respuestas, tablas y filas de frecuencia calculadas,
ejercicios/intentos, experimentos y tiradas de muestreo, insignias).
Detalle completo en `docs/BASE_DE_DATOS.md`.

## 13. Reglas de negocio relevantes

- Una encuesta requiere 2–6 opciones sin duplicados y pregunta ≤ 80
  caracteres (`StatsEngine.validateSurveyOptions/validateSurveyQuestion`).
- Un caso se marca `COMPLETADO` cuando todos sus ejercicios tienen al
  menos un intento correcto, y `DOMINADO` si además todos se acertaron a
  la primera (`SubmitExerciseAnswerUseCase`).
- El XP y el nivel solo suben tras una respuesta correcta, la finalización
  de un caso o una tirada de muestreo real; nunca se regala progreso.
- Las insignias se evalúan tras cada acción relevante comparando el
  progreso actual contra su requisito (`ProgressRules.evaluateUnlocks`).

## 14. UX

Identidad "agencia de detectives": paleta cálida de archivo (tinta
nocturna, pergamino, ámbar, turquesa), mascota Dati con 4 estados de
ánimo, insignias y avatares ilustrados a medida, tablero de misiones en
vez de lista de botones, feedback educativo (nunca solo
"Correcto/Incorrecto").

## 15. Privacidad

Sin nombre real, email, teléfono, dirección, contactos ni ubicación. Sin
permiso de Internet, cámara ni micrófono (no se usan). Todo el
almacenamiento es local y privado a la app.

## 16. Pruebas

71 pruebas unitarias (JVM puro + Robolectric/Room en memoria) sobre
`StatsEngine`, `ExerciseEvaluator`, `ProgressRules`, el conversor de
listas, el seeding completo de la base de datos y los repositorios de
encuestas. Detalle en `docs/BUILD_REPORT.md`.

## 17. Limitaciones conocidas

- La compilación no pudo verificarse en el entorno de generación (sin
  Android SDK/Gradle/red); ver `docs/BUILD_REPORT.md`.
- El drag-and-drop del módulo Organizador se implementó como "seleccionar
  y luego tocar el contenedor" en lugar de arrastre físico con gestos,
  por la complejidad de gestos de arrastre robustos en Compose dentro del
  tiempo disponible; la tarea de clasificación en sí es idéntica.
- No incluye estadística inferencial (fuera de alcance para el rango de
  edad objetivo).

## 18. Mejoras futuras

- Arrastre físico (drag gesture) real en el Organizador.
- Modo "reto diario" con semilla distinta cada día.
- Exportar el progreso a un archivo local para transferirlo entre
  dispositivos sin necesidad de nube.
- Más datasets y casos por categoría.

## 19. Conclusiones

Staticdata cumple simultáneamente los ejes de funcionalidad (motor y
persistencia reales), contenido (30 datasets, 10 casos, 30 ejercicios con
respuestas derivadas de datos reales), diseño visual (identidad propia
ilustrada), interacción (6 mecánicas distintas, ninguna dominada por
opción múltiple), experiencia infantil (narrativa, progreso, feedback),
persistencia (Room real) y pruebas (71 tests). La única pieza pendiente de
verificación objetiva es la compilación, por carecer el entorno de
generación de Android SDK/Gradle y acceso a red.
