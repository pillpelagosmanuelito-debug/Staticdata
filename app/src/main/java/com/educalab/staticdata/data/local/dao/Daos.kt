package com.educalab.staticdata.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import androidx.room.Update
import com.educalab.staticdata.data.local.entity.*
import kotlinx.coroutines.flow.Flow

@Dao
interface UserProfileDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(profile: UserProfileEntity): Long

    @Update
    suspend fun update(profile: UserProfileEntity)

    @Query("SELECT * FROM user_profile LIMIT 1")
    fun observeProfile(): Flow<UserProfileEntity?>

    @Query("SELECT * FROM user_profile LIMIT 1")
    suspend fun getProfileOnce(): UserProfileEntity?

    @Query("SELECT COUNT(*) FROM user_profile")
    suspend fun countProfiles(): Int
}

@Dao
interface ProgressDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(progress: ProgressEntity)

    @Update
    suspend fun update(progress: ProgressEntity)

    @Query("SELECT * FROM progress WHERE userId = :userId LIMIT 1")
    fun observeProgress(userId: Long): Flow<ProgressEntity?>

    @Query("SELECT * FROM progress WHERE userId = :userId LIMIT 1")
    suspend fun getProgressOnce(userId: Long): ProgressEntity?
}

@Dao
interface DatasetDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertDataset(dataset: DatasetEntity): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertVariable(variable: DataVariableEntity): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertValues(values: List<DataValueEntity>): List<Long>

    @Query("SELECT * FROM dataset ORDER BY id")
    fun observeAll(): Flow<List<DatasetEntity>>

    @Query("SELECT * FROM dataset WHERE id = :id")
    suspend fun getDataset(id: Long): DatasetEntity?

    @Query("SELECT * FROM data_variable WHERE datasetId = :datasetId LIMIT 1")
    suspend fun getVariableForDataset(datasetId: Long): DataVariableEntity?

    @Query("SELECT * FROM data_value WHERE variableId = :variableId")
    suspend fun getValuesForVariable(variableId: Long): List<DataValueEntity>

    @Query("SELECT COUNT(*) FROM dataset")
    suspend fun countDatasets(): Int

    @Transaction
    suspend fun getValuesForDataset(datasetId: Long): List<DataValueEntity> {
        val variable = getVariableForDataset(datasetId) ?: return emptyList()
        return getValuesForVariable(variable.id)
    }
}

@Dao
interface CaseFileDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(cases: List<CaseFileEntity>)

    @Update
    suspend fun update(case: CaseFileEntity)

    @Query("SELECT * FROM case_file ORDER BY orderIndex")
    fun observeAll(): Flow<List<CaseFileEntity>>

    @Query("SELECT * FROM case_file WHERE id = :id")
    suspend fun getById(id: Long): CaseFileEntity?

    @Query("SELECT COUNT(*) FROM case_file")
    suspend fun count(): Int

    @Query("SELECT COUNT(*) FROM case_file WHERE status = 'COMPLETADO' OR status = 'DOMINADO'")
    suspend fun countCompleted(): Int
}

@Dao
interface SurveyDao {
    @Insert
    suspend fun insertSurvey(survey: SurveyEntity): Long

    @Insert
    suspend fun insertOptions(options: List<SurveyOptionEntity>): List<Long>

    @Insert
    suspend fun insertResponse(response: SurveyResponseEntity): Long

    @Query("SELECT * FROM survey ORDER BY createdAtEpochMillis DESC")
    fun observeAll(): Flow<List<SurveyEntity>>

    @Query("SELECT * FROM survey_option WHERE surveyId = :surveyId ORDER BY orderIndex")
    suspend fun getOptions(surveyId: Long): List<SurveyOptionEntity>

    @Query("SELECT * FROM survey_response WHERE surveyId = :surveyId")
    suspend fun getResponses(surveyId: Long): List<SurveyResponseEntity>

    @Query("SELECT COUNT(*) FROM survey_response WHERE surveyId = :surveyId")
    suspend fun countResponses(surveyId: Long): Int

    @Query("SELECT COUNT(*) FROM survey")
    suspend fun countSurveys(): Int
}

@Dao
interface FrequencyDao {
    @Insert
    suspend fun insertTable(table: FrequencyTableEntity): Long

    @Insert
    suspend fun insertRows(rows: List<FrequencyRowEntity>): List<Long>

    @Query("SELECT * FROM frequency_table WHERE sourceType = :sourceType AND sourceId = :sourceId ORDER BY computedAtEpochMillis DESC LIMIT 1")
    suspend fun getLatestTable(sourceType: String, sourceId: Long): FrequencyTableEntity?

    @Query("SELECT * FROM frequency_row WHERE tableId = :tableId ORDER BY count DESC")
    suspend fun getRows(tableId: Long): List<FrequencyRowEntity>

    @Transaction
    suspend fun saveComputedTable(table: FrequencyTableEntity, rows: List<FrequencyRowEntity>): Long {
        val tableId = insertTable(table)
        insertRows(rows.map { it.copy(tableId = tableId) })
        return tableId
    }
}

@Dao
interface ExerciseDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(exercises: List<ExerciseEntity>)

    @Insert
    suspend fun insertAttempt(attempt: AttemptEntity): Long

    @Query("SELECT * FROM exercise WHERE caseId = :caseId ORDER BY difficulty")
    suspend fun getForCase(caseId: Long): List<ExerciseEntity>

    @Query("SELECT * FROM exercise WHERE id = :id")
    suspend fun getById(id: Long): ExerciseEntity?

    @Query("SELECT * FROM attempt WHERE exerciseId = :exerciseId ORDER BY timestampEpochMillis DESC")
    fun observeAttempts(exerciseId: Long): Flow<List<AttemptEntity>>

    @Query("SELECT * FROM attempt ORDER BY timestampEpochMillis DESC")
    fun observeAllAttempts(): Flow<List<AttemptEntity>>

    @Query("""
        SELECT e.* FROM exercise e
        INNER JOIN attempt a ON a.exerciseId = e.id
        WHERE a.correct = 0
        GROUP BY e.id
        ORDER BY MAX(a.timestampEpochMillis) DESC
        LIMIT :limit
    """)
    suspend fun getExercisesForReview(limit: Int): List<ExerciseEntity>

    @Query("SELECT COUNT(*) FROM attempt WHERE exerciseId = :exerciseId")
    suspend fun countAttempts(exerciseId: Long): Int

    @Query("SELECT COUNT(*) FROM attempt WHERE exerciseId = :exerciseId AND correct = 1")
    suspend fun countCorrectAttempts(exerciseId: Long): Int

    @Query("SELECT correct FROM attempt WHERE exerciseId = :exerciseId ORDER BY timestampEpochMillis ASC LIMIT 1")
    suspend fun firstAttemptCorrect(exerciseId: Long): Boolean?
}

@Dao
interface SampleDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertExperiments(experiments: List<SampleExperimentEntity>)

    @Insert
    suspend fun insertRun(run: SampleRunEntity): Long

    @Query("SELECT * FROM sample_experiment")
    fun observeExperiments(): Flow<List<SampleExperimentEntity>>

    @Query("SELECT * FROM sample_run WHERE experimentId = :experimentId ORDER BY timestampEpochMillis DESC")
    fun observeRuns(experimentId: Long): Flow<List<SampleRunEntity>>

    @Query("SELECT COUNT(*) FROM sample_run")
    suspend fun countRuns(): Int

    @Query("SELECT COUNT(*) FROM sample_experiment")
    suspend fun countExperiments(): Int
}

@Dao
interface BadgeDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(badges: List<BadgeEntity>)

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun unlock(userBadge: UserBadgeEntity)

    @Query("SELECT * FROM badge ORDER BY id")
    fun observeAllBadges(): Flow<List<BadgeEntity>>

    @Query("SELECT * FROM user_badge WHERE userId = :userId")
    fun observeUnlocked(userId: Long): Flow<List<UserBadgeEntity>>

    @Query("SELECT badgeId FROM user_badge WHERE userId = :userId")
    suspend fun getUnlockedIds(userId: Long): List<Long>

    @Query("SELECT COUNT(*) FROM badge")
    suspend fun countBadges(): Int
}
