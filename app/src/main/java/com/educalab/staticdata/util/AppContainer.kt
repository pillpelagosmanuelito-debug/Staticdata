package com.educalab.staticdata.util

import android.content.Context
import com.educalab.staticdata.data.local.StaticdataDatabase
import com.educalab.staticdata.data.repository.CaseRepository
import com.educalab.staticdata.data.repository.ExerciseRepository
import com.educalab.staticdata.data.repository.FrequencyRepository
import com.educalab.staticdata.data.repository.ProfileRepository
import com.educalab.staticdata.data.repository.SampleLabRepository
import com.educalab.staticdata.data.repository.SurveyRepository
import com.educalab.staticdata.domain.usecase.CreateSurveyUseCase
import com.educalab.staticdata.domain.usecase.RunSampleExperimentUseCase
import com.educalab.staticdata.domain.usecase.SubmitExerciseAnswerUseCase

/**
 * Contenedor de dependencias manual y liviano (sin frameworks de DI externos)
 * para mantener el proyecto simple, predecible y fácil de testear.
 */
class AppContainer(context: Context) {
    val database: StaticdataDatabase = StaticdataDatabase.getInstance(context)

    val profileRepository by lazy { ProfileRepository(database.userProfileDao(), database.progressDao(), database.badgeDao()) }
    val caseRepository by lazy { CaseRepository(database.caseFileDao(), database.datasetDao()) }
    val surveyRepository by lazy { SurveyRepository(database.surveyDao()) }
    val frequencyRepository by lazy { FrequencyRepository(database.frequencyDao()) }
    val exerciseRepository by lazy { ExerciseRepository(database.exerciseDao()) }
    val sampleLabRepository by lazy { SampleLabRepository(database.sampleDao(), database.datasetDao()) }

    val submitExerciseAnswerUseCase by lazy {
        SubmitExerciseAnswerUseCase(exerciseRepository, caseRepository, profileRepository, sampleLabRepository, surveyRepository)
    }
    val runSampleExperimentUseCase by lazy { RunSampleExperimentUseCase(sampleLabRepository, profileRepository) }
    val createSurveyUseCase by lazy { CreateSurveyUseCase(surveyRepository) }
}
