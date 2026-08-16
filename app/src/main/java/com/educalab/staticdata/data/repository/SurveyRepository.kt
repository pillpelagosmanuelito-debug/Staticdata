package com.educalab.staticdata.data.repository

import com.educalab.staticdata.data.local.dao.SurveyDao
import com.educalab.staticdata.data.local.entity.SurveyEntity
import com.educalab.staticdata.data.local.entity.SurveyOptionEntity
import com.educalab.staticdata.data.local.entity.SurveyResponseEntity
import com.educalab.staticdata.domain.logic.StatsEngine
import com.educalab.staticdata.domain.model.DataValue
import com.educalab.staticdata.domain.model.Survey
import com.educalab.staticdata.domain.model.SurveyOption
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class SurveyRepository(private val dao: SurveyDao) {

    fun observeSurveys(): Flow<List<Survey>> = dao.observeAll().map { list -> list.map { it.toDomainShallow() } }

    suspend fun countSurveys(): Int = dao.countSurveys()

    /** Crea una encuesta validando pregunta y opciones con StatsEngine antes de persistir. */
    suspend fun createSurvey(question: String, options: List<String>): Long {
        StatsEngine.validateSurveyQuestion(question)
        StatsEngine.validateSurveyOptions(options)

        val surveyId = dao.insertSurvey(SurveyEntity(question = question.trim(), createdAtEpochMillis = System.currentTimeMillis()))
        val optionEntities = options.mapIndexed { index, label -> SurveyOptionEntity(surveyId = surveyId, label = label.trim(), orderIndex = index) }
        dao.insertOptions(optionEntities)
        return surveyId
    }

    suspend fun recordResponse(surveyId: Long, optionId: Long, respondentAlias: String) {
        dao.insertResponse(SurveyResponseEntity(surveyId = surveyId, optionId = optionId, respondentAlias = respondentAlias.trim().ifBlank { "Anónimo" }))
    }

    suspend fun getOptions(surveyId: Long): List<SurveyOption> = dao.getOptions(surveyId).map { SurveyOption(it.id, it.surveyId, it.label, it.orderIndex) }

    /** Convierte las respuestas de una encuesta en DataValue genéricos, listos para StatsEngine. */
    suspend fun getSurveyAsDataValues(surveyId: Long): List<DataValue> {
        val options = dao.getOptions(surveyId).associateBy { it.id }
        return dao.getResponses(surveyId).mapNotNull { response ->
            val label = options[response.optionId]?.label ?: return@mapNotNull null
            DataValue(id = response.id, datasetId = surveyId, label = label)
        }
    }

    suspend fun responseCount(surveyId: Long): Int = dao.countResponses(surveyId)
}

private fun SurveyEntity.toDomainShallow() = Survey(id, question, createdAtEpochMillis)
