package com.educalab.staticdata.data.repository

import com.educalab.staticdata.data.local.converters.StringListConverter
import com.educalab.staticdata.data.local.dao.ExerciseDao
import com.educalab.staticdata.data.local.entity.AttemptEntity
import com.educalab.staticdata.data.local.entity.ExerciseEntity
import com.educalab.staticdata.domain.model.Attempt
import com.educalab.staticdata.domain.model.Exercise
import com.educalab.staticdata.domain.model.ExerciseType
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class ExerciseRepository(private val dao: ExerciseDao) {

    suspend fun getForCase(caseId: Long): List<Exercise> = dao.getForCase(caseId).map { it.toDomain() }

    suspend fun getExercise(id: Long): Exercise? = dao.getById(id)?.toDomain()

    suspend fun recordAttempt(exerciseId: Long, givenAnswer: List<String>, correct: Boolean) {
        dao.insertAttempt(
            AttemptEntity(
                exerciseId = exerciseId,
                givenAnswerEncoded = StringListConverter.encode(givenAnswer),
                correct = correct,
                timestampEpochMillis = System.currentTimeMillis()
            )
        )
    }

    suspend fun isFirstAttempt(exerciseId: Long): Boolean = dao.countAttempts(exerciseId) == 0

    suspend fun hasCorrectAttempt(exerciseId: Long): Boolean = dao.countCorrectAttempts(exerciseId) > 0

    suspend fun wasFirstTryCorrect(exerciseId: Long): Boolean = dao.firstAttemptCorrect(exerciseId) == true

    fun observeAllAttempts(): Flow<List<Attempt>> = dao.observeAllAttempts().map { list -> list.map { it.toDomain() } }

    suspend fun getExercisesForReview(limit: Int = 6): List<Exercise> = dao.getExercisesForReview(limit).map { it.toDomain() }
}

private fun ExerciseEntity.toDomain() = Exercise(
    id = id, caseId = caseId,
    type = runCatching { ExerciseType.valueOf(type) }.getOrDefault(ExerciseType.IDENTIFICAR_MODA),
    prompt = prompt, datasetId = datasetId,
    options = StringListConverter.decode(optionsEncoded),
    correctAnswer = StringListConverter.decode(correctAnswerEncoded),
    explanation = explanation, difficulty = difficulty
)

private fun AttemptEntity.toDomain() = Attempt(id, exerciseId, StringListConverter.decode(givenAnswerEncoded), correct, timestampEpochMillis)
