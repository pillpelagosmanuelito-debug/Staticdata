package com.educalab.staticdata.data.local.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(tableName = "user_profile")
data class UserProfileEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val alias: String,
    val avatarId: Int,
    val createdAtEpochMillis: Long,
    val onboardingCompleted: Boolean = false,
    val soundEnabled: Boolean = true,
    val hapticsEnabled: Boolean = true
)

@Entity(tableName = "progress")
data class ProgressEntity(
    @PrimaryKey val userId: Long,
    val totalXp: Int = 0,
    val level: Int = 1,
    val casesCompleted: Int = 0,
    val exercisesCompleted: Int = 0,
    val exercisesCorrectFirstTry: Int = 0
)

@Entity(tableName = "dataset")
data class DatasetEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val title: String,
    val category: String,
    val isUserGenerated: Boolean,
    val createdAtEpochMillis: Long
)

@Entity(
    tableName = "data_variable",
    foreignKeys = [ForeignKey(
        entity = DatasetEntity::class, parentColumns = ["id"], childColumns = ["datasetId"],
        onDelete = ForeignKey.CASCADE
    )],
    indices = [Index("datasetId")]
)
data class DataVariableEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val datasetId: Long,
    val name: String,
    val type: String // "CATEGORICA" | "NUMERICA"
)

@Entity(
    tableName = "data_value",
    foreignKeys = [ForeignKey(
        entity = DataVariableEntity::class, parentColumns = ["id"], childColumns = ["variableId"],
        onDelete = ForeignKey.CASCADE
    )],
    indices = [Index("variableId")]
)
data class DataValueEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val variableId: Long,
    val label: String,
    val numericValue: Double? = null
)

@Entity(
    tableName = "case_file",
    foreignKeys = [ForeignKey(
        entity = DatasetEntity::class, parentColumns = ["id"], childColumns = ["datasetId"],
        onDelete = ForeignKey.CASCADE
    )],
    indices = [Index("datasetId")]
)
data class CaseFileEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val title: String,
    val briefing: String,
    val category: String,
    val datasetId: Long,
    val minLevel: Int,
    val status: String = "BLOQUEADO", // BLOQUEADO|DISPONIBLE|INICIADO|COMPLETADO|DOMINADO
    val orderIndex: Int
)

@Entity(tableName = "survey")
data class SurveyEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val question: String,
    val createdAtEpochMillis: Long
)

@Entity(
    tableName = "survey_option",
    foreignKeys = [ForeignKey(
        entity = SurveyEntity::class, parentColumns = ["id"], childColumns = ["surveyId"],
        onDelete = ForeignKey.CASCADE
    )],
    indices = [Index("surveyId")]
)
data class SurveyOptionEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val surveyId: Long,
    val label: String,
    val orderIndex: Int
)

@Entity(
    tableName = "survey_response",
    foreignKeys = [
        ForeignKey(entity = SurveyEntity::class, parentColumns = ["id"], childColumns = ["surveyId"], onDelete = ForeignKey.CASCADE),
        ForeignKey(entity = SurveyOptionEntity::class, parentColumns = ["id"], childColumns = ["optionId"], onDelete = ForeignKey.CASCADE)
    ],
    indices = [Index("surveyId"), Index("optionId")]
)
data class SurveyResponseEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val surveyId: Long,
    val optionId: Long,
    val respondentAlias: String
)

/** Caché persistida de una tabla de frecuencias ya calculada (dataset o encuesta). */
@Entity(tableName = "frequency_table")
data class FrequencyTableEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val sourceType: String, // "DATASET" | "SURVEY"
    val sourceId: Long,
    val total: Int,
    val computedAtEpochMillis: Long
)

@Entity(
    tableName = "frequency_row",
    foreignKeys = [ForeignKey(
        entity = FrequencyTableEntity::class, parentColumns = ["id"], childColumns = ["tableId"],
        onDelete = ForeignKey.CASCADE
    )],
    indices = [Index("tableId")]
)
data class FrequencyRowEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val tableId: Long,
    val label: String,
    val count: Int,
    val relativeFrequency: Double,
    val percentage: Double
)

@Entity(
    tableName = "exercise",
    foreignKeys = [ForeignKey(
        entity = CaseFileEntity::class, parentColumns = ["id"], childColumns = ["caseId"],
        onDelete = ForeignKey.CASCADE
    )],
    indices = [Index("caseId")]
)
data class ExerciseEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val caseId: Long,
    val type: String,
    val prompt: String,
    val datasetId: Long?,
    val optionsEncoded: String,
    val correctAnswerEncoded: String,
    val explanation: String,
    val difficulty: Int
)

@Entity(
    tableName = "attempt",
    foreignKeys = [ForeignKey(
        entity = ExerciseEntity::class, parentColumns = ["id"], childColumns = ["exerciseId"],
        onDelete = ForeignKey.CASCADE
    )],
    indices = [Index("exerciseId")]
)
data class AttemptEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val exerciseId: Long,
    val givenAnswerEncoded: String,
    val correct: Boolean,
    val timestampEpochMillis: Long
)

@Entity(
    tableName = "sample_experiment",
    foreignKeys = [ForeignKey(
        entity = DatasetEntity::class, parentColumns = ["id"], childColumns = ["populationDatasetId"],
        onDelete = ForeignKey.CASCADE
    )],
    indices = [Index("populationDatasetId")]
)
data class SampleExperimentEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val title: String,
    val populationDatasetId: Long,
    val description: String
)

@Entity(
    tableName = "sample_run",
    foreignKeys = [ForeignKey(
        entity = SampleExperimentEntity::class, parentColumns = ["id"], childColumns = ["experimentId"],
        onDelete = ForeignKey.CASCADE
    )],
    indices = [Index("experimentId")]
)
data class SampleRunEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val experimentId: Long,
    val sampleSize: Int,
    val seed: Long,
    val drawnLabelsEncoded: String,
    val timestampEpochMillis: Long
)

@Entity(tableName = "badge")
data class BadgeEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val code: String,
    val title: String,
    val description: String,
    val iconKey: String,
    val requirement: String // "TIPO:VALOR", p.ej. "CASES:3"
)

@Entity(
    tableName = "user_badge",
    primaryKeys = ["userId", "badgeId"],
    foreignKeys = [
        ForeignKey(entity = UserProfileEntity::class, parentColumns = ["id"], childColumns = ["userId"], onDelete = ForeignKey.CASCADE),
        ForeignKey(entity = BadgeEntity::class, parentColumns = ["id"], childColumns = ["badgeId"], onDelete = ForeignKey.CASCADE)
    ],
    indices = [Index("badgeId")]
)
data class UserBadgeEntity(
    val userId: Long,
    val badgeId: Long,
    val unlockedAtEpochMillis: Long
)
