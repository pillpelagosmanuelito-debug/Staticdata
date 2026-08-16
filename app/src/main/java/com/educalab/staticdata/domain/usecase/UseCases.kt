package com.educalab.staticdata.domain.usecase

import com.educalab.staticdata.data.repository.CaseRepository
import com.educalab.staticdata.data.repository.ExerciseRepository
import com.educalab.staticdata.data.repository.ProfileRepository
import com.educalab.staticdata.data.repository.SampleLabRepository
import com.educalab.staticdata.data.repository.SurveyRepository
import com.educalab.staticdata.domain.logic.EvaluationResult
import com.educalab.staticdata.domain.logic.ExerciseEvaluator
import com.educalab.staticdata.domain.logic.ProgressRules
import com.educalab.staticdata.domain.model.Badge
import com.educalab.staticdata.domain.model.CaseStatus
import com.educalab.staticdata.domain.model.Exercise

data class SubmitAnswerResult(
    val evaluation: EvaluationResult,
    val xpGained: Int,
    val leveledUp: Boolean,
    val newBadges: List<Badge>,
    val caseCompleted: Boolean
)

/**
 * Orquesta el flujo completo de "responder un ejercicio": evalúa la
 * respuesta, registra el intento, actualiza XP/nivel, revisa si el caso
 * quedó completo/dominado y comprueba desbloqueo de insignias.
 * Todo el progreso resultante proviene de esta única fuente de verdad.
 */
class SubmitExerciseAnswerUseCase(
    private val exerciseRepository: ExerciseRepository,
    private val caseRepository: CaseRepository,
    private val profileRepository: ProfileRepository,
    private val sampleLabRepository: SampleLabRepository,
    private val surveyRepository: SurveyRepository
) {
    suspend operator fun invoke(userId: Long, exercise: Exercise, givenAnswer: List<String>): SubmitAnswerResult {
        val wasFirstTry = exerciseRepository.isFirstAttempt(exercise.id)
        val evaluation = ExerciseEvaluator.evaluate(exercise, givenAnswer)
        exerciseRepository.recordAttempt(exercise.id, givenAnswer, evaluation.correct)

        var progress = profileRepository.getProgressOnce(userId)
        val levelBefore = progress.level

        if (evaluation.correct) {
            val newXp = ProgressRules.xpAfterExercise(progress, wasCorrect = true, wasFirstTry = wasFirstTry)
            progress = progress.copy(
                totalXp = newXp,
                level = ProgressRules.levelForXp(newXp),
                exercisesCompleted = progress.exercisesCompleted + 1,
                exercisesCorrectFirstTry = progress.exercisesCorrectFirstTry + if (wasFirstTry) 1 else 0
            )
        }

        var caseCompleted = false
        if (evaluation.correct) {
            val caseExercises = exerciseRepository.getForCase(exercise.caseId)
            // El caso se da por completado cuando TODOS sus ejercicios tienen ya
            // al menos un intento correcto (el que se acaba de registrar incluido).
            val allCorrectAtLeastOnce = caseExercises.all { ex ->
                ex.id == exercise.id || exerciseRepository.hasCorrectAttempt(ex.id)
            }
            if (allCorrectAtLeastOnce) {
                caseRepository.getCase(exercise.caseId)?.let { case ->
                    if (case.status != CaseStatus.COMPLETADO && case.status != CaseStatus.DOMINADO) {
                        val masteredFirstTry = caseExercises.all { ex ->
                            if (ex.id == exercise.id) wasFirstTry else exerciseRepository.wasFirstTryCorrect(ex.id)
                        }
                        caseRepository.updateStatus(
                            exercise.caseId,
                            if (masteredFirstTry) CaseStatus.DOMINADO else CaseStatus.COMPLETADO
                        )
                        progress = progress.copy(
                            totalXp = ProgressRules.xpAfterCaseCompleted(progress),
                            casesCompleted = progress.casesCompleted + 1
                        )
                        progress = progress.copy(level = ProgressRules.levelForXp(progress.totalXp))
                        caseCompleted = true
                    }
                }
            }
        } else {
            caseRepository.getCase(exercise.caseId)?.let { case ->
                if (case.status == CaseStatus.DISPONIBLE) {
                    caseRepository.updateStatus(exercise.caseId, CaseStatus.INICIADO)
                }
            }
        }

        profileRepository.saveProgress(progress)
        caseRepository.refreshAvailability(progress.level)

        val newBadges = refreshBadges(userId, progress)

        return SubmitAnswerResult(
            evaluation = evaluation,
            xpGained = progress.totalXp,
            leveledUp = progress.level > levelBefore,
            newBadges = newBadges,
            caseCompleted = caseCompleted
        )
    }

    private suspend fun refreshBadges(userId: Long, progress: com.educalab.staticdata.domain.model.Progress): List<Badge> {
        val allBadges = allBadgesSnapshot ?: return emptyList()
        val unlockedIds = profileRepository.getUnlockedBadgeIds(userId)
        val samples = sampleLabRepository.countRuns()
        val surveys = surveyRepository.countSurveys()
        val unlocks = ProgressRules.evaluateUnlocks(progress, allBadges, unlockedIds, samples, surveys)
        unlocks.forEach { profileRepository.unlockBadge(userId, it.id) }
        return unlocks
    }

    // Pequeña caché en memoria de la lista de insignias, refrescada por el ViewModel raíz.
    companion object {
        var allBadgesSnapshot: List<Badge>? = null
    }
}

/** Registra una tirada del laboratorio de muestras y otorga la XP correspondiente. */
class RunSampleExperimentUseCase(
    private val sampleLabRepository: SampleLabRepository,
    private val profileRepository: ProfileRepository
) {
    suspend operator fun invoke(userId: Long, experimentId: Long, populationDatasetId: Long, sampleSize: Int) {
        sampleLabRepository.runSample(experimentId, populationDatasetId, sampleSize)
        val progress = profileRepository.getProgressOnce(userId)
        val updated = progress.copy(
            totalXp = ProgressRules.xpAfterSampleRun(progress),
        ).let { it.copy(level = ProgressRules.levelForXp(it.totalXp)) }
        profileRepository.saveProgress(updated)
    }
}

/** Crea una encuesta local validada por StatsEngine. */
class CreateSurveyUseCase(private val surveyRepository: SurveyRepository) {
    suspend operator fun invoke(question: String, options: List<String>): Long =
        surveyRepository.createSurvey(question, options)
}
