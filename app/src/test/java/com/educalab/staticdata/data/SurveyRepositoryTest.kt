package com.educalab.staticdata.data

import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import com.educalab.staticdata.data.local.StaticdataDatabase
import com.educalab.staticdata.data.repository.FrequencyRepository
import com.educalab.staticdata.data.repository.SurveyRepository
import com.educalab.staticdata.domain.logic.StatsEngine
import kotlinx.coroutines.test.runTest
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertThrows
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner

@RunWith(RobolectricTestRunner::class)
class SurveyRepositoryTest {

    private lateinit var db: StaticdataDatabase
    private lateinit var surveyRepo: SurveyRepository
    private lateinit var freqRepo: FrequencyRepository

    @Before
    fun setUp() {
        db = Room.inMemoryDatabaseBuilder(ApplicationProvider.getApplicationContext(), StaticdataDatabase::class.java)
            .allowMainThreadQueries().build()
        surveyRepo = SurveyRepository(db.surveyDao())
        freqRepo = FrequencyRepository(db.frequencyDao())
    }

    @After
    fun tearDown() { db.close() }

    @Test
    fun `creating a survey persists question and options`() = runTest {
        val id = surveyRepo.createSurvey("¿Cuál es tu mascota favorita?", listOf("Perro", "Gato", "Pez"))
        val options = surveyRepo.getOptions(id)
        assertEquals(3, options.size)
    }

    @Test
    fun `creating a survey rejects a question over 80 characters`() = runTest {
        val longQuestion = "¿".repeat(81)
        assertThrows(StatsEngine.InvalidDataException::class.java) {
            kotlinx.coroutines.runBlocking { surveyRepo.createSurvey(longQuestion, listOf("A", "B")) }
        }
    }

    @Test
    fun `creating a survey rejects fewer than 2 options`() = runTest {
        assertThrows(StatsEngine.InvalidDataException::class.java) {
            kotlinx.coroutines.runBlocking { surveyRepo.createSurvey("¿Pregunta?", listOf("Única")) }
        }
    }

    @Test
    fun `survey with zero responses yields an empty frequency table without crashing`() = runTest {
        val id = surveyRepo.createSurvey("¿Cuál es tu deporte favorito?", listOf("Fútbol", "Natación"))
        val values = surveyRepo.getSurveyAsDataValues(id)
        val table = StatsEngine.frequencyTable(values)
        assertEquals(0, table.total)
    }

    @Test
    fun `recorded responses are reflected in the computed frequency table`() = runTest {
        val id = surveyRepo.createSurvey("¿Cuál es tu transporte favorito?", listOf("Bici", "Bus"))
        val options = surveyRepo.getOptions(id)
        val bici = options.first { it.label == "Bici" }
        surveyRepo.recordResponse(id, bici.id, "AlumnoA")
        surveyRepo.recordResponse(id, bici.id, "AlumnoB")
        val values = surveyRepo.getSurveyAsDataValues(id)
        val table = StatsEngine.frequencyTable(values)
        assertEquals(listOf("Bici"), table.modes)
        assertEquals(2, table.total)
    }

    @Test
    fun `computeAndCache stores a frequency table that can be retrieved later`() = runTest {
        val id = surveyRepo.createSurvey("¿Cuál es tu libro favorito?", listOf("Aventura", "Misterio"))
        val options = surveyRepo.getOptions(id)
        surveyRepo.recordResponse(id, options.first().id, "A")
        val values = surveyRepo.getSurveyAsDataValues(id)
        freqRepo.computeAndCache("SURVEY", id, values)
        val cached = freqRepo.getCached("SURVEY", id)
        assertEquals(1, cached?.total)
    }
}
