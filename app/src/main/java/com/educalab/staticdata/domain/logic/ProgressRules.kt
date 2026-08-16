package com.educalab.staticdata.domain.logic

import com.educalab.staticdata.domain.model.Badge
import com.educalab.staticdata.domain.model.Progress

/**
 * Reglas de progresión: cálculo de XP, nivel y condiciones de desbloqueo de
 * insignias. El progreso siempre se deriva de acciones reales registradas
 * en la base de datos (casos completados, ejercicios correctos, muestras
 * realizadas) — nunca se otorga XP "de regalo".
 */
object ProgressRules {

    const val XP_PER_CORRECT_EXERCISE = 10
    const val XP_PER_CORRECT_FIRST_TRY_BONUS = 5
    const val XP_PER_CASE_COMPLETED = 30
    const val XP_PER_SAMPLE_RUN = 4

    /** Umbrales de XP acumulada para cada nivel (índice 0 = nivel 1). */
    private val LEVEL_THRESHOLDS = listOf(0, 60, 150, 280, 450, 660, 920, 1230)

    fun levelForXp(totalXp: Int): Int {
        var level = 1
        for (i in LEVEL_THRESHOLDS.indices) {
            if (totalXp >= LEVEL_THRESHOLDS[i]) level = i + 1
        }
        return level.coerceAtMost(LEVEL_THRESHOLDS.size)
    }

    fun xpForNextLevel(totalXp: Int): Int? {
        val currentLevel = levelForXp(totalXp)
        if (currentLevel >= LEVEL_THRESHOLDS.size) return null
        return LEVEL_THRESHOLDS[currentLevel] - totalXp
    }

    fun xpAfterExercise(progress: Progress, wasCorrect: Boolean, wasFirstTry: Boolean): Int {
        if (!wasCorrect) return progress.totalXp
        var gained = XP_PER_CORRECT_EXERCISE
        if (wasFirstTry) gained += XP_PER_CORRECT_FIRST_TRY_BONUS
        return progress.totalXp + gained
    }

    fun xpAfterCaseCompleted(progress: Progress): Int = progress.totalXp + XP_PER_CASE_COMPLETED

    fun xpAfterSampleRun(progress: Progress): Int = progress.totalXp + XP_PER_SAMPLE_RUN

    /**
     * Evalúa qué insignias (aún no desbloqueadas) cumple ahora el progreso dado.
     * Cada [Badge.requirement] usa un código simple "TIPO:VALOR" (ver seed).
     */
    fun evaluateUnlocks(
        progress: Progress,
        allBadges: List<Badge>,
        alreadyUnlockedBadgeIds: Set<Long>,
        sampleRunsCompleted: Int,
        surveysCreated: Int
    ): List<Badge> {
        return allBadges.filter { badge ->
            if (badge.id in alreadyUnlockedBadgeIds) return@filter false
            val (type, valueStr) = badge.requirement.split(":").let { it[0] to it.getOrElse(1) { "0" } }
            val value = valueStr.toIntOrNull() ?: 0
            when (type) {
                "CASES" -> progress.casesCompleted >= value
                "EXERCISES" -> progress.exercisesCompleted >= value
                "FIRST_TRY" -> progress.exercisesCorrectFirstTry >= value
                "LEVEL" -> levelForXp(progress.totalXp) >= value
                "SAMPLES" -> sampleRunsCompleted >= value
                "SURVEYS" -> surveysCreated >= value
                else -> false
            }
        }
    }
}
