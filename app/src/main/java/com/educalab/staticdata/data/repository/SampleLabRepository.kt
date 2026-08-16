package com.educalab.staticdata.data.repository

import com.educalab.staticdata.data.local.dao.DatasetDao
import com.educalab.staticdata.data.local.dao.SampleDao
import com.educalab.staticdata.data.local.entity.SampleExperimentEntity
import com.educalab.staticdata.data.local.entity.SampleRunEntity
import com.educalab.staticdata.domain.logic.StatsEngine
import com.educalab.staticdata.domain.model.DataValue
import com.educalab.staticdata.domain.model.SampleExperiment
import com.educalab.staticdata.domain.model.SampleRun
import com.educalab.staticdata.domain.model.SampleVariabilityResult
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class SampleLabRepository(
    private val sampleDao: SampleDao,
    private val datasetDao: DatasetDao
) {
    fun observeExperiments(): Flow<List<SampleExperiment>> =
        sampleDao.observeExperiments().map { list -> list.map { it.toDomain() } }

    fun observeRuns(experimentId: Long): Flow<List<SampleRun>> =
        sampleDao.observeRuns(experimentId).map { list -> list.map { it.toDomain() } }

    suspend fun countRuns(): Int = sampleDao.countRuns()

    /** Extrae una muestra real (StatsEngine.sample) de la población del experimento y la persiste. */
    suspend fun runSample(experimentId: Long, populationDatasetId: Long, sampleSize: Int): SampleRun {
        val population = datasetDao.getValuesForDataset(populationDatasetId).map { DataValue(it.id, it.variableId, it.label, it.numericValue) }
        val seed = System.nanoTime()
        val drawn = StatsEngine.sample(population, sampleSize, seed)
        val entity = SampleRunEntity(
            experimentId = experimentId, sampleSize = sampleSize, seed = seed,
            drawnLabelsEncoded = com.educalab.staticdata.data.local.converters.StringListConverter.encode(drawn.map { it.label }),
            timestampEpochMillis = System.currentTimeMillis()
        )
        val id = sampleDao.insertRun(entity)
        return entity.copy(id = id).toDomain()
    }

    suspend fun variabilityFor(experimentId: Long, allRuns: List<SampleRun>): SampleVariabilityResult =
        StatsEngine.sampleVariability(allRuns.filter { it.experimentId == experimentId })
}

private fun SampleExperimentEntity.toDomain() = SampleExperiment(id, title, populationDatasetId, description)
private fun SampleRunEntity.toDomain() = SampleRun(
    id, experimentId, sampleSize, seed,
    com.educalab.staticdata.data.local.converters.StringListConverter.decode(drawnLabelsEncoded), timestampEpochMillis
)
