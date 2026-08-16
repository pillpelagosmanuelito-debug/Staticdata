package com.educalab.staticdata.domain.logic

import com.educalab.staticdata.domain.model.DataValue
import com.educalab.staticdata.domain.model.FrequencyRow
import com.educalab.staticdata.domain.model.FrequencyTable
import com.educalab.staticdata.domain.model.SampleRun
import com.educalab.staticdata.domain.model.SampleVariabilityResult
import kotlin.random.Random

/**
 * StatsEngine: motor de dominio, puro y testeable (sin dependencias de Android),
 * con las reglas estadísticas reales usadas por toda la aplicación.
 *
 * Todas las funciones son deterministas dado el mismo input (el muestreo usa
 * una semilla explícita), por lo que son 100% verificables con JUnit plano.
 */
object StatsEngine {

    class InvalidDataException(message: String) : IllegalArgumentException(message)

    /** Número total de observaciones. */
    fun count(values: List<DataValue>): Int = values.size

    /**
     * Tabla de frecuencias absoluta, relativa y porcentual agrupada por etiqueta.
     * Ordenada de mayor a menor frecuencia; en empate, orden alfabético para
     * que el resultado sea determinista.
     */
    fun frequencyTable(values: List<DataValue>): FrequencyTable {
        if (values.isEmpty()) {
            return FrequencyTable(rows = emptyList(), total = 0, modes = emptyList())
        }
        val total = values.size
        val grouped: Map<String, Int> = values
            .groupingBy { it.label.trim() }
            .eachCount()

        val rows = grouped.entries
            .sortedWith(compareByDescending<Map.Entry<String, Int>> { it.value }.thenBy { it.key })
            .map { (label, cnt) ->
                FrequencyRow(
                    label = label,
                    count = cnt,
                    relativeFrequency = cnt.toDouble() / total,
                    percentage = roundTo(cnt.toDouble() / total * 100.0, 1)
                )
            }

        val maxCount = rows.maxOf { it.count }
        val modes = rows.filter { it.count == maxCount }.map { it.label }

        return FrequencyTable(rows = rows, total = total, modes = modes)
    }

    /** Conteo absoluto de una etiqueta concreta dentro de un conjunto de datos. */
    fun frequencyOf(values: List<DataValue>, label: String): Int =
        values.count { it.label.equals(label.trim(), ignoreCase = true) }

    /** Porcentaje que representa `part` sobre `total`. Lanza si total es 0 o part negativo. */
    fun percentage(part: Int, total: Int): Double {
        if (total <= 0) throw InvalidDataException("El total debe ser mayor que 0 para calcular un porcentaje.")
        if (part < 0) throw InvalidDataException("La parte no puede ser negativa.")
        if (part > total) throw InvalidDataException("La parte no puede ser mayor que el total.")
        return roundTo(part.toDouble() / total * 100.0, 1)
    }

    /**
     * Moda(s) del conjunto: puede haber más de una etiqueta (multimodal).
     * Lista vacía si no hay datos.
     */
    fun mode(values: List<DataValue>): List<String> = frequencyTable(values).modes

    /** Media aritmética de los valores numéricos disponibles (ignora nulos). */
    fun mean(values: List<DataValue>): Double {
        val numeric = values.mapNotNull { it.numericValue }
        if (numeric.isEmpty()) throw InvalidDataException("No hay valores numéricos para calcular la media.")
        return roundTo(numeric.sum() / numeric.size, 2)
    }

    /** Rango (máximo - mínimo) de los valores numéricos disponibles. */
    fun range(values: List<DataValue>): Double {
        val numeric = values.mapNotNull { it.numericValue }
        if (numeric.isEmpty()) throw InvalidDataException("No hay valores numéricos para calcular el rango.")
        return roundTo(numeric.max() - numeric.min(), 2)
    }

    /**
     * Extrae una muestra aleatoria SIN reemplazo de tamaño [sampleSize] a partir
     * de una población, usando una semilla explícita para que el resultado sea
     * reproducible y testeable.
     */
    fun sample(population: List<DataValue>, sampleSize: Int, seed: Long): List<DataValue> {
        if (population.isEmpty()) throw InvalidDataException("La población está vacía.")
        if (sampleSize <= 0) throw InvalidDataException("El tamaño de muestra debe ser mayor que 0.")
        if (sampleSize > population.size) {
            throw InvalidDataException("El tamaño de muestra no puede superar el tamaño de la población.")
        }
        val rng = Random(seed)
        return population.shuffled(rng).take(sampleSize)
    }

    /**
     * Compara la variabilidad de una etiqueta concreta entre varias tiradas de
     * muestreo: calcula la proporción de cada etiqueta en cada muestra y el
     * rango de variación observado entre muestras (dato clave del Laboratorio
     * de Muestras: "muestras distintas de la misma población dan resultados
     * parecidos pero no idénticos").
     */
    fun sampleVariability(runs: List<SampleRun>): SampleVariabilityResult {
        if (runs.isEmpty()) throw InvalidDataException("No hay tiradas de muestreo para comparar.")

        val proportionsPerRun: List<Map<String, Double>> = runs.map { run ->
            if (run.drawnLabels.isEmpty()) return@map emptyMap<String, Double>()
            val size = run.drawnLabels.size
            run.drawnLabels.groupingBy { it }.eachCount()
                .mapValues { (_, c) -> roundTo(c.toDouble() / size, 3) }
        }

        val allLabels = proportionsPerRun.flatMap { it.keys }.toSet()
        val rangeByLabel = allLabels.associateWith { label ->
            val values = proportionsPerRun.map { it[label] ?: 0.0 }
            roundTo((values.maxOrNull() ?: 0.0) - (values.minOrNull() ?: 0.0), 3)
        }

        return SampleVariabilityResult(
            runs = runs,
            proportionByLabelPerRun = proportionsPerRun,
            rangeByLabel = rangeByLabel
        )
    }

    // ---------------------------------------------------------------------
    // Validaciones de negocio reutilizables
    // ---------------------------------------------------------------------

    fun validateSurveyQuestion(question: String) {
        val trimmed = question.trim()
        if (trimmed.isEmpty()) throw InvalidDataException("La pregunta no puede estar vacía.")
        if (trimmed.length > 80) throw InvalidDataException("La pregunta no puede superar 80 caracteres.")
    }

    fun validateSurveyOptions(options: List<String>) {
        val cleaned = options.map { it.trim() }.filter { it.isNotEmpty() }
        if (cleaned.size < 2) throw InvalidDataException("Se necesitan al menos 2 opciones.")
        if (cleaned.size > 6) throw InvalidDataException("No se permiten más de 6 opciones.")
        val duplicates = cleaned.groupingBy { it.lowercase() }.eachCount().filter { it.value > 1 }
        if (duplicates.isNotEmpty()) throw InvalidDataException("Hay opciones duplicadas: ${duplicates.keys.joinToString()}")
    }

    private fun roundTo(value: Double, decimals: Int): Double {
        val factor = Math.pow(10.0, decimals.toDouble())
        return Math.round(value * factor) / factor
    }
}
