package com.educalab.staticdata.ui.navigation

object Routes {
    const val ONBOARDING = "onboarding"
    const val HOME = "home"
    const val PROFILE = "profile"
    const val ACADEMY = "academy"
    const val SURVEY = "survey"
    const val CLASSIFY = "classify"
    const val FREQUENCY = "frequency"
    const val STATS = "stats"
    const val SAMPLING = "sampling"
    const val CASES = "cases"
    const val CASE_DETAIL = "case_detail/{caseId}"
    const val PROGRESS = "progress"

    fun caseDetail(caseId: Long) = "case_detail/$caseId"
}
