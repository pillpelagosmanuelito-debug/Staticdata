package com.educalab.staticdata.domain.model

/**
 * Modelos de dominio de Staticdata (independientes de Room).
 * La UI y la lógica de negocio trabajan contra estas clases; el
 * mapeo hacia/desde entidades de persistencia ocurre en el repositorio.
 */

enum class DataVariableType(val etiqueta: String) {
    CATEGORICA("Categórica"),
    NUMERICA("Numérica")
}

enum class CaseStatus { BLOQUEADO, DISPONIBLE, INICIADO, COMPLETADO, DOMINADO }

enum class ModuleId {
    PERFIL, OFICINA, QUE_ES_UN_DATO, TIPOS_DE_DATO, ENCUESTAS,
    ORGANIZADOR, TABLAS_FRECUENCIA, MODA_PORCENTAJES, LABORATORIO_MUESTRAS,
    CASOS, PROGRESO
}

/** Un caso/expediente: agrupa un dataset con una misión narrativa. */
data class CaseFile(
    val id: Long,
    val title: String,
    val briefing: String,
    val category: String,
    val datasetId: Long,
    val minLevel: Int,
    val status: CaseStatus,
    val order: Int
)

/** Colección de observaciones sobre una misma variable. */
data class Dataset(
    val id: Long,
    val title: String,
    val category: String,
    val variableName: String,
    val variableType: DataVariableType,
    val isUserGenerated: Boolean,
    val values: List<DataValue> = emptyList()
)

/** Una observación individual dentro de un dataset. */
data class DataValue(
    val id: Long,
    val datasetId: Long,
    val label: String,
    val numericValue: Double? = null
)

/** Encuesta creada localmente por el jugador. */
data class Survey(
    val id: Long,
    val question: String,
    val createdAtEpochMillis: Long,
    val options: List<SurveyOption> = emptyList(),
    val responseCount: Int = 0
)

data class SurveyOption(
    val id: Long,
    val surveyId: Long,
    val label: String,
    val orderIndex: Int
)

data class SurveyResponse(
    val id: Long,
    val surveyId: Long,
    val optionId: Long,
    val respondentAlias: String
)

/** Fila calculada de una tabla de frecuencias. */
data class FrequencyRow(
    val label: String,
    val count: Int,
    val relativeFrequency: Double, // 0..1
    val percentage: Double         // 0..100
)

data class FrequencyTable(
    val rows: List<FrequencyRow>,
    val total: Int,
    val modes: List<String>
)

enum class ExerciseType {
    ORDENAR_FRECUENCIA,       // ordenar etiquetas de mayor a menor frecuencia
    DATO_EXTRANO,             // detectar el valor que no encaja
    COMPLETAR_TABLA,          // completar una celda numérica de una tabla
    IDENTIFICAR_MODA,         // seleccionar la moda entre opciones visuales
    INTERPRETAR_PORCENTAJE,   // responder una pregunta basada en % calculado
    CLASIFICAR_TIPO           // clasificar datos como categóricos/numéricos
}

data class Exercise(
    val id: Long,
    val caseId: Long,
    val type: ExerciseType,
    val prompt: String,
    val datasetId: Long?,
    val options: List<String>,
    val correctAnswer: List<String>,
    val explanation: String,
    val difficulty: Int
)

data class Attempt(
    val id: Long,
    val exerciseId: Long,
    val givenAnswer: List<String>,
    val correct: Boolean,
    val timestampEpochMillis: Long
)

data class SampleExperiment(
    val id: Long,
    val title: String,
    val populationDatasetId: Long,
    val description: String
)

data class SampleRun(
    val id: Long,
    val experimentId: Long,
    val sampleSize: Int,
    val seed: Long,
    val drawnLabels: List<String>,
    val timestampEpochMillis: Long
)

data class UserProfile(
    val id: Long,
    val alias: String,
    val avatarId: Int,
    val createdAtEpochMillis: Long,
    val onboardingCompleted: Boolean,
    val soundEnabled: Boolean,
    val hapticsEnabled: Boolean
)

data class Progress(
    val userId: Long,
    val totalXp: Int,
    val level: Int,
    val casesCompleted: Int,
    val exercisesCompleted: Int,
    val exercisesCorrectFirstTry: Int
)

data class Badge(
    val id: Long,
    val code: String,
    val title: String,
    val description: String,
    val iconKey: String,
    val requirement: String
)

data class UserBadge(
    val badgeId: Long,
    val unlockedAtEpochMillis: Long
)

/** Resultado de comparar variabilidad entre varias muestras del laboratorio. */
data class SampleVariabilityResult(
    val runs: List<SampleRun>,
    val proportionByLabelPerRun: List<Map<String, Double>>,
    val rangeByLabel: Map<String, Double> // diferencia entre proporción máx y mín observada
)
