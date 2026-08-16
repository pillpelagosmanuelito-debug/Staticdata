package com.educalab.staticdata.util

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.compose.runtime.Composable

/** Fábrica genérica mínima: evita repetir boilerplate de Factory por cada ViewModel. */
class SimpleViewModelFactory(private val creator: () -> ViewModel) : ViewModelProvider.Factory {
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T = creator() as T
}

@Composable
inline fun <reified VM : ViewModel> rememberAppViewModel(noinline creator: () -> VM): VM {
    return viewModel(factory = SimpleViewModelFactory(creator))
}
