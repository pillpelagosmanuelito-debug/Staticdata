package com.educalab.staticdata.data.repository

import com.educalab.staticdata.data.local.dao.FrequencyDao
import com.educalab.staticdata.data.local.entity.FrequencyRowEntity
import com.educalab.staticdata.data.local.entity.FrequencyTableEntity
import com.educalab.staticdata.domain.logic.StatsEngine
import com.educalab.staticdata.domain.model.DataValue
import com.educalab.staticdata.domain.model.FrequencyTable

/**
 * Calcula tablas de frecuencia con StatsEngine y guarda una copia (caché)
 * en Room para que el historial de resultados persista entre sesiones,
 * sin tener que recalcular todo cada vez que el niño revisa un caso pasado.
 */
class FrequencyRepository(private val dao: FrequencyDao) {

    suspend fun computeAndCache(sourceType: String, sourceId: Long, values: List<DataValue>): FrequencyTable {
        val table = StatsEngine.frequencyTable(values)
        if (table.rows.isNotEmpty()) {
            val tableEntity = FrequencyTableEntity(
                sourceType = sourceType, sourceId = sourceId, total = table.total,
                computedAtEpochMillis = System.currentTimeMillis()
            )
            val rowEntities = table.rows.map {
                FrequencyRowEntity(tableId = 0, label = it.label, count = it.count, relativeFrequency = it.relativeFrequency, percentage = it.percentage)
            }
            dao.saveComputedTable(tableEntity, rowEntities)
        }
        return table
    }

    suspend fun getCached(sourceType: String, sourceId: Long): FrequencyTable? {
        val tableEntity = dao.getLatestTable(sourceType, sourceId) ?: return null
        val rows = dao.getRows(tableEntity.id)
        if (rows.isEmpty()) return null
        return FrequencyTable(
            rows = rows.map { com.educalab.staticdata.domain.model.FrequencyRow(it.label, it.count, it.relativeFrequency, it.percentage) },
            total = tableEntity.total,
            modes = rows.filter { it.count == rows.maxOf { r -> r.count } }.map { it.label }
        )
    }
}
