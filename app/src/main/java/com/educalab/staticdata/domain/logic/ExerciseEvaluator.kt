package com.educalab.staticdata.domain.logic

import com.educalab.staticdata.domain.model.Exercise
import com.educalab.staticdata.domain.model.ExerciseType

/** Resultado de evaluar la respuesta de un niño a un ejercicio. */
data class EvaluationResult(
    val correct: Boolean,
    val explanation: String
)

/**
 * Evalúa la respuesta dada por el usuario contra la respuesta correcta de un
 * [Exercise]. La comparación depende del tipo de ejercicio:
 *  - ORDENAR_FRECUENCIA / CLASIFICAR_TIPO: el orden importa (secuencia exacta).
 *  - DATO_EXTRANO / IDENTIFICAR_MODA / INTERPRETAR_PORCENTAJE / COMPLETAR_TABLA:
 *    el orden no importa (conjunto de respuestas).
 */
object ExerciseEvaluator {

    fun evaluate(exercise: Exercise, givenAnswer: List<String>): EvaluationResult {
        val given = givenAnswer.map { it.trim().lowercase() }
        val correct = exercise.correctAnswer.map { it.trim().lowercase() }

        val isCorrect = when (exercise.type) {
            ExerciseType.ORDENAR_FRECUENCIA, ExerciseType.CLASIFICAR_TIPO -> given == correct
            ExerciseType.DATO_EXTRANO,
            ExerciseType.IDENTIFICAR_MODA,
            ExerciseType.INTERPRETAR_PORCENTAJE,
            ExerciseType.COMPLETAR_TABLA -> given.toSet() == correct.toSet() && given.isNotEmpty()
        }

        return EvaluationResult(
            correct = isCorrect,
            explanation = exercise.explanation
        )
    }
}
