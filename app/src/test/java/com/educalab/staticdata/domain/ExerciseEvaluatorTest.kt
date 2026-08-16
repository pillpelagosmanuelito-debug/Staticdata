package com.educalab.staticdata.domain

import com.educalab.staticdata.domain.logic.ExerciseEvaluator
import com.educalab.staticdata.domain.model.Exercise
import com.educalab.staticdata.domain.model.ExerciseType
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class ExerciseEvaluatorTest {

    private fun exercise(type: ExerciseType, correct: List<String>) = Exercise(
        id = 1, caseId = 1, type = type, prompt = "prompt", datasetId = null,
        options = correct, correctAnswer = correct, explanation = "explicación"
        , difficulty = 1
    )

    @Test
    fun `ordering exercise requires exact sequence match`() {
        val ex = exercise(ExerciseType.ORDENAR_FRECUENCIA, listOf("Perro", "Gato", "Pez"))
        val result = ExerciseEvaluator.evaluate(ex, listOf("Perro", "Gato", "Pez"))
        assertTrue(result.correct)
    }

    @Test
    fun `ordering exercise fails when sequence is different`() {
        val ex = exercise(ExerciseType.ORDENAR_FRECUENCIA, listOf("Perro", "Gato", "Pez"))
        val result = ExerciseEvaluator.evaluate(ex, listOf("Gato", "Perro", "Pez"))
        assertFalse(result.correct)
    }

    @Test
    fun `classify exercise requires exact sequence match per item`() {
        val ex = exercise(ExerciseType.CLASIFICAR_TIPO, listOf("Categórica", "Numérica"))
        assertTrue(ExerciseEvaluator.evaluate(ex, listOf("Categórica", "Numérica")).correct)
        assertFalse(ExerciseEvaluator.evaluate(ex, listOf("Numérica", "Categórica")).correct)
    }

    @Test
    fun `mode exercise ignores answer order`() {
        val ex = exercise(ExerciseType.IDENTIFICAR_MODA, listOf("A", "B"))
        assertTrue(ExerciseEvaluator.evaluate(ex, listOf("B", "A")).correct)
    }

    @Test
    fun `odd one out exercise is case and whitespace tolerant`() {
        val ex = exercise(ExerciseType.DATO_EXTRANO, listOf("Uva"))
        assertTrue(ExerciseEvaluator.evaluate(ex, listOf("  uva ")).correct)
    }

    @Test
    fun `percentage interpretation exercise matches exact string`() {
        val ex = exercise(ExerciseType.INTERPRETAR_PORCENTAJE, listOf("40.0%"))
        assertTrue(ExerciseEvaluator.evaluate(ex, listOf("40.0%")).correct)
        assertFalse(ExerciseEvaluator.evaluate(ex, listOf("50.0%")).correct)
    }

    @Test
    fun `complete table exercise fails on empty answer`() {
        val ex = exercise(ExerciseType.COMPLETAR_TABLA, listOf("7"))
        assertFalse(ExerciseEvaluator.evaluate(ex, emptyList()).correct)
    }

    @Test
    fun `evaluation always returns the exercise explanation`() {
        val ex = exercise(ExerciseType.IDENTIFICAR_MODA, listOf("A"))
        val result = ExerciseEvaluator.evaluate(ex, listOf("A"))
        assertEquals("explicación", result.explanation)
    }
}
