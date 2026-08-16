# Staticdata — Agencia de Detectives de Datos

App educativa Android (nativa, Kotlin + Jetpack Compose) para enseñar
estadística básica (dato, tipos de dato, encuestas, tablas de frecuencia,
moda, porcentajes y muestreo) a niños de 9 a 13 años, ambientada como una
agencia de detectives guiada por **Dati**, un dron-analista.

100% offline: sin cuentas, sin anuncios, sin analítica, sin backend.
Todos los datos viven únicamente en el dispositivo (Room/SQLite).

## Estado de este entregable

Este proyecto fue generado en un entorno **sin acceso a red y sin Android
SDK / Gradle / Kotlin compiler instalados**. Todo el código fuente, la base
de datos, las pruebas, la documentación y los PDF son reales y completos,
pero la compilación **no pudo ejecutarse ni verificarse en este entorno**.
Ver `docs/BUILD_REPORT.md` para el detalle honesto de qué se pudo y qué no
se pudo comprobar, y `.github/workflows/android-build.yml` para obtener un
APK verificado automáticamente en el primer push a GitHub.

## Compilar

Requisitos: JDK 17, Android SDK (compileSdk 34), conexión a internet para
resolver dependencias de Gradle la primera vez.

```bash
./gradlew clean
./gradlew testDebugUnitTest
./gradlew lintDebug
./gradlew assembleDebug
```

Si `gradle-wrapper.jar` no está presente (no se pudo descargar en este
entorno), genera el wrapper una vez con Gradle instalado localmente:

```bash
gradle wrapper --gradle-version 8.7
```

o simplemente haz push a GitHub: el workflow en
`.github/workflows/android-build.yml` lo regenera automáticamente y
publica el APK de depuración como artefacto de la ejecución.

## Estructura del proyecto

```
app/        Código fuente Android (Kotlin + Jetpack Compose, MVVM)
database/   schema.sql y sample_data.sql (documentación del modelo Room)
docs/       Documentación funcional y técnica + PDF
tools/      Scripts auxiliares (generación de PDF)
gradle/     Gradle Wrapper
```

## Stack técnico

Kotlin · Jetpack Compose · Material 3 · Navigation Compose · MVVM ·
Repository Pattern · Room · Coroutines/Flow · Gradle Kotlin DSL · JDK 17 ·
minSdk 24 / targetSdk 34.

## Módulos de la app

1. Perfil (alias + avatar, sin datos personales)
2. Oficina de casos (tablero de misiones)
3–4. Academia (qué es un dato / tipos de dato)
5. Encuestas locales
6. Organizador y clasificación
7. Tablas de frecuencia
8. Moda y porcentajes
9. Laboratorio de muestras
10. Casos y ejercicios (motor `StatsEngine` + `ExerciseEvaluator`)
11. Progreso y colección de insignias

## Documentación

- `docs/MEMORIA_DESCRIPTIVA.md` / `.pdf`
- `docs/MANUAL_USUARIO.md` / `.pdf`
- `docs/MANUAL_TECNICO.md` / `.pdf`
- `docs/BASE_DE_DATOS.md`
- `docs/BUILD_REPORT.md`
