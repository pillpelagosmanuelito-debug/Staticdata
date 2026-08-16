package com.educalab.staticdata.domain

import com.educalab.staticdata.domain.logic.ProgressRules
import com.educalab.staticdata.domain.model.Badge
import com.educalab.staticdata.domain.model.Progress
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test

class ProgressRulesTest {

    private fun progress(xp: Int = 0, cases: Int = 0, exercises: Int = 0, firstTry: Int = 0) =
        Progress(userId = 1, totalXp = xp, level = ProgressRules.levelForXp(xp), casesCompleted = cases, exercisesCompleted = exercises, exercisesCorrectFirstTry = firstTry)

    @Test
    fun `level 1 for zero xp`() {
        assertEquals(1, ProgressRules.levelForXp(0))
    }

    @Test
    fun `level increases as xp crosses thresholds`() {
        assertEquals(1, ProgressRules.levelForXp(59))
        assertEquals(2, ProgressRules.levelForXp(60))
        assertEquals(3, ProgressRules.levelForXp(150))
    }

    @Test
    fun `level never exceeds the number of defined thresholds`() {
        assertEquals(8, ProgressRules.levelForXp(100000))
    }

    @Test
    fun `xpForNextLevel returns null at max level`() {
        assertNull(ProgressRules.xpForNextLevel(100000))
    }

    @Test
    fun `xpForNextLevel returns remaining xp to next threshold`() {
        assertEquals(60, ProgressRules.xpForNextLevel(0))
    }

    @Test
    fun `xpAfterExercise adds base xp on correct answer`() {
        val p = progress()
        assertEquals(ProgressRules.XP_PER_CORRECT_EXERCISE, ProgressRules.xpAfterExercise(p, wasCorrect = true, wasFirstTry = false))
    }

    @Test
    fun `xpAfterExercise adds bonus for correct first try`() {
        val p = progress()
        val expected = ProgressRules.XP_PER_CORRECT_EXERCISE + ProgressRules.XP_PER_CORRECT_FIRST_TRY_BONUS
        assertEquals(expected, ProgressRules.xpAfterExercise(p, wasCorrect = true, wasFirstTry = true))
    }

    @Test
    fun `xpAfterExercise does not change xp on incorrect answer`() {
        val p = progress(xp = 20)
        assertEquals(20, ProgressRules.xpAfterExercise(p, wasCorrect = false, wasFirstTry = true))
    }

    @Test
    fun `xpAfterCaseCompleted adds the case bonus`() {
        val p = progress(xp = 10)
        assertEquals(10 + ProgressRules.XP_PER_CASE_COMPLETED, ProgressRules.xpAfterCaseCompleted(p))
    }

    @Test
    fun `evaluateUnlocks returns badges whose requirement is met and not yet unlocked`() {
        val badges = listOf(
            Badge(1, "B1", "Primeros pasos", "desc", "icon", "CASES:1"),
            Badge(2, "B2", "Racha", "desc", "icon", "FIRST_TRY:5")
        )
        val p = progress(cases = 1, firstTry = 2)
        val unlocked = ProgressRules.evaluateUnlocks(p, badges, emptySet(), sampleRunsCompleted = 0, surveysCreated = 0)
        assertEquals(listOf(1L), unlocked.map { it.id })
    }

    @Test
    fun `evaluateUnlocks excludes already unlocked badges`() {
        val badges = listOf(Badge(1, "B1", "Primeros pasos", "desc", "icon", "CASES:1"))
        val p = progress(cases = 5)
        val unlocked = ProgressRules.evaluateUnlocks(p, badges, setOf(1L), 0, 0)
        assertTrue(unlocked.isEmpty())
    }

    @Test
    fun `evaluateUnlocks supports sample and survey based requirements`() {
        val badges = listOf(
            Badge(1, "B1", "Científico", "desc", "icon", "SAMPLES:3"),
            Badge(2, "B2", "Encuestador", "desc", "icon", "SURVEYS:2")
        )
        val p = progress()
        val unlocked = ProgressRules.evaluateUnlocks(p, badges, emptySet(), sampleRunsCompleted = 3, surveysCreated = 2)
        assertEquals(2, unlocked.size)
    }
}
