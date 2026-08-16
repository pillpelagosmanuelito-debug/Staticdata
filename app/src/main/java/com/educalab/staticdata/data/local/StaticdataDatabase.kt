package com.educalab.staticdata.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.educalab.staticdata.data.local.converters.StringListConverter
import com.educalab.staticdata.data.local.dao.*
import com.educalab.staticdata.data.local.entity.*

@Database(
    entities = [
        UserProfileEntity::class,
        ProgressEntity::class,
        DatasetEntity::class,
        DataVariableEntity::class,
        DataValueEntity::class,
        CaseFileEntity::class,
        SurveyEntity::class,
        SurveyOptionEntity::class,
        SurveyResponseEntity::class,
        FrequencyTableEntity::class,
        FrequencyRowEntity::class,
        ExerciseEntity::class,
        AttemptEntity::class,
        SampleExperimentEntity::class,
        SampleRunEntity::class,
        BadgeEntity::class,
        UserBadgeEntity::class
    ],
    version = 1,
    exportSchema = true
)
@TypeConverters(StringListConverter::class)
abstract class StaticdataDatabase : RoomDatabase() {
    abstract fun userProfileDao(): UserProfileDao
    abstract fun progressDao(): ProgressDao
    abstract fun datasetDao(): DatasetDao
    abstract fun caseFileDao(): CaseFileDao
    abstract fun surveyDao(): SurveyDao
    abstract fun frequencyDao(): FrequencyDao
    abstract fun exerciseDao(): ExerciseDao
    abstract fun sampleDao(): SampleDao
    abstract fun badgeDao(): BadgeDao

    companion object {
        @Volatile private var INSTANCE: StaticdataDatabase? = null

        fun getInstance(context: Context): StaticdataDatabase =
            INSTANCE ?: synchronized(this) {
                INSTANCE ?: Room.databaseBuilder(
                    context.applicationContext,
                    StaticdataDatabase::class.java,
                    "staticdata.db"
                ).build().also { INSTANCE = it }
            }
    }
}
