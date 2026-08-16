# Build Report — Staticdata v1.0.0

**Generado**: 2026-08-16 23:44 UTC
**Entorno de generación**: contenedor Linux efímero sin acceso a red
saliente y sin Android SDK / Gradle / Kotlin compiler preinstalados.

## COMPILACIÓN NO VERIFICADA

Este es un reporte honesto: ningún paso de compilación pudo ejecutarse ni
verificarse en el entorno donde se generó este proyecto. No se ha
inventado ningún resultado, APK, SHA-256 ni cifra de tests "en verde".

## 1. Evidencia del entorno

```
$ which gradle kotlinc adb
(sin resultados — ninguno de los tres está instalado)

$ echo $ANDROID_HOME
(vacío — no hay Android SDK instalado)

$ curl -s -m 3 -o /dev/null -w "%{http_code}" https://repo.maven.apache.org
403  (egress de red bloqueado por la configuración del entorno)
```

## 2. Intento real de build

Se ejecutó el wrapper incluido en el proyecto para documentar el fallo
real en lugar de omitirlo:

```
$ ./gradlew clean
ERROR: gradle-wrapper.jar no está presente.
Ejecuta 'gradle wrapper --gradle-version 8.7' una vez (con Gradle instalado
o mediante el workflow de GitHub Actions incluido) para generarlo.
```

`gradle-wrapper.jar` es un binario que normalmente se descarga desde
`services.gradle.org`; al no haber egress de red, no se pudo obtener ni
generar en este entorno. El resto de la cadena (`testDebugUnitTest`,
`lintDebug`, `assembleDebug`) depende de este mismo paso previo y por
tanto tampoco se ejecutó.

| Comando | Estado |
|---|---|
| `./gradlew clean` | NO EJECUTADO (bloqueado por falta de `gradle-wrapper.jar`) |
| `./gradlew testDebugUnitTest` | NO EJECUTADO |
| `./gradlew lintDebug` | NO EJECUTADO |
| `./gradlew assembleDebug` | NO EJECUTADO |

## 3. Qué SÍ se verificó localmente

- **Estructura del proyecto**: 65 archivos Kotlin (59 en `app/src/main`,
  6 en `app/src/test`) organizados en `data/`, `domain/`, `ui/` según lo
  especificado.
- **Balance sintáctico básico**: comprobación automática de llaves `{}`
  y paréntesis `()` en los 65 archivos `.kt` → 0 desbalances detectados
  (comprobación superficial; no sustituye a una compilación real del
  frontend de Kotlin).
- **Ausencia de placeholders**: búsqueda de `TODO(`, `FIXME`,
  `NotImplementedError`, "Próximamente" en todo `app/src/main` → sin
  resultados.
- **Recuento real de pruebas**: `grep -c "@Test"` sobre los 6 archivos de
  `app/src/test` → **74 pruebas** unitarias escritas (JVM puro sobre
  `StatsEngine`/`ExerciseEvaluator`/`ProgressRules`/el conversor de
  listas, y Robolectric + Room en memoria sobre el seeding completo y el
  repositorio de encuestas). Ninguna de estas 74 pruebas se ha *ejecutado*
  — su corrección se basa en revisión manual del código, no en un run
  real de JUnit.

## 4. Artefactos

| Artefacto | Estado |
|---|---|
| APK (`deliverables/Staticdata-v1.0.0.apk`) | **No generado** — depende de `assembleDebug`, no ejecutado |
| SHA-256 del APK | **No aplicable** — no existe APK |
| `deliverables/Staticdata-v1.0.0-source.zip` | Generado (código fuente completo) |
| `docs/pdf/MEMORIA_DESCRIPTIVA.pdf` | Generado (PDF real vía ReportLab) |
| `docs/pdf/MANUAL_USUARIO.pdf` | Generado (PDF real vía ReportLab) |
| `docs/pdf/MANUAL_TECNICO.pdf` | Generado (PDF real vía ReportLab) |

## 5. Cómo obtener una compilación verificada

**Opción A — GitHub Actions (recomendada)**: haz push de este repositorio
a GitHub. El workflow `.github/workflows/android-build.yml` instala JDK 17
y Gradle 8.7, regenera `gradle-wrapper.jar`, y ejecuta exactamente
`clean`, `testDebugUnitTest`, `lintDebug` y `assembleDebug`, publicando el
APK de depuración y los reportes de pruebas/lint como artefactos
descargables de la ejecución.

**Opción B — local**: con JDK 17, Android SDK (compileSdk 34) y conexión
a Internet instalados:

```bash
gradle wrapper --gradle-version 8.7   # solo la primera vez
./gradlew clean testDebugUnitTest lintDebug assembleDebug
```

## 6. Limitaciones honestas de este entregable

- Ningún resultado de compilación, ejecución de pruebas o lint mostrado
  en este documento ha sido inventado; todo lo marcado como "NO EJECUTADO"
  refleja la imposibilidad real de ejecutarlo en el entorno de
  generación, no una omisión.
- El código fue escrito con la mejor corrección posible mediante revisión
  manual línea por línea y las comprobaciones automáticas descritas en
  §3, pero **no reemplaza una compilación real**: pueden existir errores
  de tipos, imports o API que solo un compilador de Kotlin detectaría.
- Se recomienda ejecutar la Opción A antes de considerar el APK apto para
  distribución.
