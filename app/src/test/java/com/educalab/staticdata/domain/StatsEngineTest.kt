package com.educalab.staticdata.domain

import com.educalab.staticdata.domain.logic.StatsEngine
import com.educalab.staticdata.domain.model.DataValue
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertThrows
import org.junit.Assert.assertTrue
import org.junit.Test

class StatsEngineTest {

    private fun dv(label: String, value: Double? = null) = DataValue(0, 0, label, value)

    // ---------------- count ----------------

    @Test
    fun `count returns zero for empty list`() {
        assertEquals(0, StatsEngine.count(emptyList()))
    }

    @Test
    fun `count returns size for non-empty list`() {
        val data = listOf(dv("Manzana"), dv("Pera"), dv("Uva"))
        assertEquals(3, StatsEngine.count(data))
    }

    // ---------------- frequencyTable ----------------

    @Test
    fun `frequencyTable on empty list returns empty rows and no modes`() {
        val table = StatsEngine.frequencyTable(emptyList())
        assertTrue(table.rows.isEmpty())
        assertEquals(0, table.total)
        assertTrue(table.modes.isEmpty())
    }

    @Test
    fun `frequencyTable groups and counts labels correctly`() {
        val data = listOf(dv("Perro"), dv("Gato"), dv("Perro"), dv("Perro"), dv("Gato"))
        val table = StatsEngine.frequencyTable(data)
        assertEquals(5, table.total)
        assertEquals(2, table.rows.size)
        assertEquals("Perro", table.rows.first().label)
        assertEquals(3, table.rows.first().count)
    }

    @Test
    fun `frequencyTable computes relative frequency and percentage`() {
        val data = listOf(dv("A"), dv("A"), dv("B"), dv("B"), dv("B"), dv("B"))
        val table = StatsEngine.frequencyTable(data)
        val rowB = table.rows.first { it.label == "B" }
        assertEquals(4.0 / 6.0, rowB.relativeFrequency, 0.0001)
        assertEquals(66.7, rowB.percentage, 0.01)
    }

    @Test
    fun `frequencyTable breaks ties alphabetically for determinism`() {
        val data = listOf(dv("Zebra"), dv("Abeja"))
        val table = StatsEngine.frequencyTable(data)
        assertEquals(listOf("Abeja", "Zebra"), table.rows.map { it.label })
    }

    @Test
    fun `frequencyTable trims whitespace in labels before grouping`() {
        val data = listOf(dv("Gato "), dv(" Gato"), dv("Gato"))
        val table = StatsEngine.frequencyTable(data)
        assertEquals(1, table.rows.size)
        assertEquals(3, table.rows.first().count)
    }

    @Test
    fun `frequencyOf counts a specific label case-insensitively`() {
        val data = listOf(dv("Perro"), dv("perro"), dv("Gato"))
        assertEquals(2, StatsEngine.frequencyOf(data, "PERRO"))
    }

    // ---------------- percentage ----------------

    @Test
    fun `percentage computes correctly for a valid part and total`() {
        assertEquals(25.0, StatsEngine.percentage(5, 20), 0.001)
    }

    @Test
    fun `percentage throws when total is zero`() {
        assertThrows(StatsEngine.InvalidDataException::class.java) { StatsEngine.percentage(1, 0) }
    }

    @Test
    fun `percentage throws when part is negative`() {
        assertThrows(StatsEngine.InvalidDataException::class.java) { StatsEngine.percentage(-1, 10) }
    }

    @Test
    fun `percentage throws when part exceeds total`() {
        assertThrows(StatsEngine.InvalidDataException::class.java) { StatsEngine.percentage(11, 10) }
    }

    @Test
    fun `percentage of zero part is zero`() {
        assertEquals(0.0, StatsEngine.percentage(0, 10), 0.001)
    }

    // ---------------- mode ----------------

    @Test
    fun `mode returns single most frequent label`() {
        val data = listOf(dv("A"), dv("B"), dv("A"), dv("A"))
        assertEquals(listOf("A"), StatsEngine.mode(data))
    }

    @Test
    fun `mode returns multiple labels when multimodal`() {
        val data = listOf(dv("A"), dv("B"), dv("A"), dv("B"))
        assertEquals(setOf("A", "B"), StatsEngine.mode(data).toSet())
    }

    @Test
    fun `mode on empty data returns empty list`() {
        assertTrue(StatsEngine.mode(emptyList()).isEmpty())
    }

    // ---------------- mean / range ----------------

    @Test
    fun `mean computes average of numeric values`() {
        val data = listOf(dv("m1", 10.0), dv("m2", 20.0), dv("m3", 30.0))
        assertEquals(20.0, StatsEngine.mean(data), 0.001)
    }

    @Test
    fun `mean throws when there are no numeric values`() {
        val data = listOf(dv("A"), dv("B"))
        assertThrows(StatsEngine.InvalidDataException::class.java) { StatsEngine.mean(data) }
    }

    @Test
    fun `range computes max minus min`() {
        val data = listOf(dv("m1", 5.0), dv("m2", 12.0), dv("m3", 8.0))
        assertEquals(7.0, StatsEngine.range(data), 0.001)
    }

    @Test
    fun `range throws when there are no numeric values`() {
        assertThrows(StatsEngine.InvalidDataException::class.java) { StatsEngine.range(listOf(dv("A"))) }
    }

    // ---------------- sample ----------------

    @Test
    fun `sample returns requested size without repeats beyond population counts`() {
        val population = (1..20).map { dv("item$it") }
        val sample = StatsEngine.sample(population, 5, seed = 42L)
        assertEquals(5, sample.size)
    }

    @Test
    fun `sample is reproducible with the same seed`() {
        val population = (1..20).map { dv("item$it") }
        val s1 = StatsEngine.sample(population, 6, seed = 99L)
        val s2 = StatsEngine.sample(population, 6, seed = 99L)
        assertEquals(s1.map { it.label }, s2.map { it.label })
    }

    @Test
    fun `sample differs across different seeds in general`() {
        val population = (1..30).map { dv("item$it") }
        val s1 = StatsEngine.sample(population, 10, seed = 1L)
        val s2 = StatsEngine.sample(population, 10, seed = 2L)
        assertFalse(s1.map { it.label } == s2.map { it.label })
    }

    @Test
    fun `sample throws when population is empty`() {
        assertThrows(StatsEngine.InvalidDataException::class.java) { StatsEngine.sample(emptyList(), 1, 1L) }
    }

    @Test
    fun `sample throws when size is zero or negative`() {
        val population = listOf(dv("A"))
        assertThrows(StatsEngine.InvalidDataException::class.java) { StatsEngine.sample(population, 0, 1L) }
    }

    @Test
    fun `sample throws when size exceeds population`() {
        val population = listOf(dv("A"), dv("B"))
        assertThrows(StatsEngine.InvalidDataException::class.java) { StatsEngine.sample(population, 5, 1L) }
    }

    // ---------------- sampleVariability ----------------

    @Test
    fun `sampleVariability computes proportion per run`() {
        val run1 = com.educalab.staticdata.domain.model.SampleRun(1, 1, 4, 1L, listOf("A", "A", "B", "B"), 0L)
        val run2 = com.educalab.staticdata.domain.model.SampleRun(2, 1, 4, 2L, listOf("A", "A", "A", "B"), 0L)
        val result = StatsEngine.sampleVariability(listOf(run1, run2))
        assertEquals(0.5, result.proportionByLabelPerRun[0]["A"]!!, 0.001)
        assertEquals(0.75, result.proportionByLabelPerRun[1]["A"]!!, 0.001)
    }

    @Test
    fun `sampleVariability computes range between runs for each label`() {
        val run1 = com.educalab.staticdata.domain.model.SampleRun(1, 1, 4, 1L, listOf("A", "A", "B", "B"), 0L)
        val run2 = com.educalab.staticdata.domain.model.SampleRun(2, 1, 4, 2L, listOf("A", "A", "A", "B"), 0L)
        val result = StatsEngine.sampleVariability(listOf(run1, run2))
        assertEquals(0.25, result.rangeByLabel["A"]!!, 0.001)
    }

    @Test
    fun `sampleVariability throws on empty run list`() {
        assertThrows(StatsEngine.InvalidDataException::class.java) { StatsEngine.sampleVariability(emptyList()) }
    }

    // ---------------- validations ----------------

    @Test
    fun `validateSurveyQuestion accepts a normal question`() {
        StatsEngine.validateSurveyQuestion("¿Cuál es tu fruta favorita?")
    }

    @Test
    fun `validateSurveyQuestion rejects empty question`() {
        assertThrows(StatsEngine.InvalidDataException::class.java) { StatsEngine.validateSurveyQuestion("   ") }
    }

    @Test
    fun `validateSurveyQuestion rejects question longer than 80 characters`() {
        val longQuestion = "a".repeat(81)
        assertThrows(StatsEngine.InvalidDataException::class.java) { StatsEngine.validateSurveyQuestion(longQuestion) }
    }

    @Test
    fun `validateSurveyOptions accepts between 2 and 6 options`() {
        StatsEngine.validateSurveyOptions(listOf("Perro", "Gato"))
        StatsEngine.validateSurveyOptions(listOf("A", "B", "C", "D", "E", "F"))
    }

    @Test
    fun `validateSurveyOptions rejects fewer than 2 options`() {
        assertThrows(StatsEngine.InvalidDataException::class.java) { StatsEngine.validateSurveyOptions(listOf("Solo")) }
    }

    @Test
    fun `validateSurveyOptions rejects more than 6 options`() {
        val options = (1..7).map { "Opcion$it" }
        assertThrows(StatsEngine.InvalidDataException::class.java) { StatsEngine.validateSurveyOptions(options) }
    }

    @Test
    fun `validateSurveyOptions rejects duplicate options ignoring case`() {
        assertThrows(StatsEngine.InvalidDataException::class.java) {
            StatsEngine.validateSurveyOptions(listOf("Perro", "perro", "Gato"))
        }
    }
}
