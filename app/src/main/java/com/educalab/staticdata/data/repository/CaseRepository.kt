package com.educalab.staticdata.data.repository

import com.educalab.staticdata.data.local.dao.CaseFileDao
import com.educalab.staticdata.data.local.dao.DatasetDao
import com.educalab.staticdata.data.local.entity.CaseFileEntity
import com.educalab.staticdata.data.local.entity.DataValueEntity
import com.educalab.staticdata.data.local.entity.DatasetEntity
import com.educalab.staticdata.domain.model.CaseFile
import com.educalab.staticdata.domain.model.CaseStatus
import com.educalab.staticdata.domain.model.DataValue
import com.educalab.staticdata.domain.model.DataVariableType
import com.educalab.staticdata.domain.model.Dataset
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class CaseRepository(
    private val caseFileDao: CaseFileDao,
    private val datasetDao: DatasetDao
) {
    fun observeCases(): Flow<List<CaseFile>> = caseFileDao.observeAll().map { list -> list.map { it.toDomain() } }

    suspend fun getCase(id: Long): CaseFile? = caseFileDao.getById(id)?.toDomain()

    suspend fun totalCases(): Int = caseFileDao.count()
    suspend fun completedCases(): Int = caseFileDao.countCompleted()

    suspend fun updateStatus(caseId: Long, status: CaseStatus) {
        val current = caseFileDao.getById(caseId) ?: return
        caseFileDao.update(current.copy(status = status.name))
    }

    /**
     * Recalcula qué casos deben pasar de BLOQUEADO a DISPONIBLE según el nivel
     * actual del jugador. No desbloquea todo de golpe: respeta minLevel.
     */
    suspend fun refreshAvailability(currentLevel: Int) {
        caseFileDao.observeAll()
        val all = (1..caseFileDao.count()).mapNotNull { caseFileDao.getById(it.toLong()) }
        all.filter { it.status == "BLOQUEADO" && it.minLevel <= currentLevel }
            .forEach { caseFileDao.update(it.copy(status = "DISPONIBLE")) }
    }

    fun observeDatasets(): Flow<List<Dataset>> = datasetDao.observeAll().map { list -> list.map { it.toDomainShallow() } }

    suspend fun getDatasetWithValues(datasetId: Long): Dataset? {
        val entity = datasetDao.getDataset(datasetId) ?: return null
        val variable = datasetDao.getVariableForDataset(datasetId)
        val values = datasetDao.getValuesForDataset(datasetId).map { it.toDomain() }
        return Dataset(
            id = entity.id,
            title = entity.title,
            category = entity.category,
            variableName = variable?.name ?: "",
            variableType = if (variable?.type == "NUMERICA") DataVariableType.NUMERICA else DataVariableType.CATEGORICA,
            isUserGenerated = entity.isUserGenerated,
            values = values
        )
    }

    suspend fun createUserDataset(
        title: String, category: String, variableName: String, type: DataVariableType, values: List<String>
    ): Long {
        val datasetId = datasetDao.insertDataset(
            DatasetEntity(title = title, category = category, isUserGenerated = true, createdAtEpochMillis = System.currentTimeMillis())
        )
        val variableId = datasetDao.insertVariable(
            com.educalab.staticdata.data.local.entity.DataVariableEntity(datasetId = datasetId, name = variableName, type = type.name)
        )
        datasetDao.insertValues(values.map { DataValueEntity(variableId = variableId, label = it) })
        return datasetId
    }
}

private fun CaseFileEntity.toDomain() = CaseFile(
    id = id, title = title, briefing = briefing, category = category, datasetId = datasetId,
    minLevel = minLevel, status = runCatching { CaseStatus.valueOf(status) }.getOrDefault(CaseStatus.BLOQUEADO),
    order = orderIndex
)

private fun DatasetEntity.toDomainShallow() = Dataset(
    id = id, title = title, category = category, variableName = "", variableType = DataVariableType.CATEGORICA,
    isUserGenerated = isUserGenerated, values = emptyList()
)

private fun DataValueEntity.toDomain() = DataValue(id, variableId, label, numericValue)
