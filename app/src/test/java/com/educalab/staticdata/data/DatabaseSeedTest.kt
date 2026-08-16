package com.educalab.staticdata.data

import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import com.educalab.staticdata.data.local.StaticdataDatabase
import com.educalab.staticdata.data.local.seed.SeedProvider
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner

/**
 * Verifica, contra una base Room real en memoria, que el contenido semilla
 * se genera con las cantidades comprometidas y que el seeding es idempotente
 * (no duplica datos si se ejecuta más de una vez).
 */
@RunWith(RobolectricTestRunner::class)
class DatabaseSeedTest {

    private lateinit var db: StaticdataDatabase

    @Before
    fun setUp() {
        db = Room.inMemoryDatabaseBuilder(ApplicationProvider.getApplicationContext(), StaticdataDatabase::class.java)
            .allowMainThreadQueries()
            .build()
    }

    @After
    fun tearDown() { db.close() }

    @Test
    fun `seeding creates exactly 30 datasets`() = runTest {
        SeedProvider.seedIfEmpty(db)
        assertEquals(30, db.datasetDao().countDatasets())
    }

    @Test
    fun `seeding creates 10 case files`() = runTest {
        SeedProvider.seedIfEmpty(db)
        assertEquals(10, db.caseFileDao().count())
    }

    @Test
    fun `seeding creates 3 exercises per case, 30 in total`() = runTest {
        SeedProvider.seedIfEmpty(db)
        val cases = (1..10).mapNotNull { db.caseFileDao().getById(it.toLong()) }
        val totalExercises = cases.sumOf { db.exerciseDao().getForCase(it.id).size }
        assertEquals(30, totalExercises)
    }

    @Test
    fun `seeding creates at least 8 badges`() = runTest {
        SeedProvider.seedIfEmpty(db)
        assertTrue(db.badgeDao().countBadges() >= 8)
    }

    @Test
    fun `seeding creates exactly 3 sample experiments`() = runTest {
        SeedProvider.seedIfEmpty(db)
        assertEquals(3, db.sampleDao().countExperiments())
    }

    @Test
    fun `seeding twice does not duplicate datasets`() = runTest {
        SeedProvider.seedIfEmpty(db)
        SeedProvider.seedIfEmpty(db)
        assertEquals(30, db.datasetDao().countDatasets())
    }

    @Test
    fun `first two cases are available without progress`() = runTest {
        SeedProvider.seedIfEmpty(db)
        val first = db.caseFileDao().getById(1L)!!
        val second = db.caseFileDao().getById(2L)!!
        assertEquals("DISPONIBLE", first.status)
        assertEquals("DISPONIBLE", second.status)
    }

    @Test
    fun `every exercise has a non-empty correct answer`() = runTest {
        SeedProvider.seedIfEmpty(db)
        val cases = (1..10).mapNotNull { db.caseFileDao().getById(it.toLong()) }
        cases.forEach { case ->
            db.exerciseDao().getForCase(case.id).forEach { exercise ->
                assertTrue("Ejercicio ${exercise.id} sin respuesta correcta", exercise.correctAnswerEncoded.isNotBlank())
            }
        }
    }
}
