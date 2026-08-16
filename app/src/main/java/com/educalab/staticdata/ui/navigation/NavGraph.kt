package com.educalab.staticdata.ui.navigation

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.educalab.staticdata.ui.illustration.DatiMascot
import com.educalab.staticdata.ui.illustration.MascotMood
import com.educalab.staticdata.ui.screens.academy.AcademyScreen
import com.educalab.staticdata.ui.screens.cases.CaseDetailScreen
import com.educalab.staticdata.ui.screens.cases.CasesScreen
import com.educalab.staticdata.ui.screens.classify.ClassifyScreen
import com.educalab.staticdata.ui.screens.frequency.FrequencyScreen
import com.educalab.staticdata.ui.screens.home.HomeScreen
import com.educalab.staticdata.ui.screens.onboarding.OnboardingScreen
import com.educalab.staticdata.ui.screens.profile.ProfileScreen
import com.educalab.staticdata.ui.screens.progress.ProgressScreen
import com.educalab.staticdata.ui.screens.sampling.SamplingScreen
import com.educalab.staticdata.ui.screens.stats.StatsScreen
import com.educalab.staticdata.ui.screens.survey.SurveyScreen
import com.educalab.staticdata.util.CurrentUser
import com.educalab.staticdata.util.LocalAppContainer

@Composable
fun StaticdataNavGraph() {
    val container = LocalAppContainer.current
    var startDestination by remember { mutableStateOf<String?>(null) }

    LaunchedEffect(Unit) {
        val profile = container.profileRepository.getOrCreateProfile("Detective", 0)
        CurrentUser.id = profile.id
        startDestination = if (profile.onboardingCompleted) Routes.HOME else Routes.ONBOARDING
    }

    val destination = startDestination
    if (destination == null) {
        Column(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            DatiMascot(mood = MascotMood.THINKING)
            Text("Abriendo la agencia…", style = MaterialTheme.typography.titleMedium)
        }
        return
    }

    val navController = rememberNavController()
    NavHost(navController = navController, startDestination = destination) {
        composable(Routes.ONBOARDING) {
            OnboardingScreen(onFinished = {
                navController.navigate(Routes.HOME) { popUpTo(Routes.ONBOARDING) { inclusive = true } }
            })
        }
        composable(Routes.HOME) {
            HomeScreen(onNavigate = { route -> navController.navigate(route) })
        }
        composable(Routes.ACADEMY) { AcademyScreen(onBack = { navController.popBackStack() }) }
        composable(Routes.SURVEY) { SurveyScreen(onBack = { navController.popBackStack() }) }
        composable(Routes.CLASSIFY) { ClassifyScreen(onBack = { navController.popBackStack() }) }
        composable(Routes.FREQUENCY) { FrequencyScreen(onBack = { navController.popBackStack() }) }
        composable(Routes.STATS) { StatsScreen(onBack = { navController.popBackStack() }) }
        composable(Routes.SAMPLING) { SamplingScreen(onBack = { navController.popBackStack() }) }
        composable(Routes.CASES) {
            CasesScreen(
                onBack = { navController.popBackStack() },
                onOpenCase = { caseId -> navController.navigate(Routes.caseDetail(caseId)) }
            )
        }
        composable(
            route = Routes.CASE_DETAIL,
            arguments = listOf(navArgument("caseId") { type = NavType.LongType })
        ) { backStackEntry ->
            val caseId = backStackEntry.arguments?.getLong("caseId") ?: 0L
            CaseDetailScreen(caseId = caseId, onBack = { navController.popBackStack() })
        }
        composable(Routes.PROGRESS) { ProgressScreen(onBack = { navController.popBackStack() }) }
        composable(Routes.PROFILE) { ProfileScreen(onBack = { navController.popBackStack() }) }
    }
}
