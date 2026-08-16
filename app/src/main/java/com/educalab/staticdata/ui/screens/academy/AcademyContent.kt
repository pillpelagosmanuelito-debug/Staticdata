package com.educalab.staticdata.ui.screens.academy

data class AcademyItem(val label: String, val isCategorical: Boolean, val hint: String)

/** Contenido curado para introducir "qué es un dato" y los tipos básicos. */
object AcademyContent {
    val items = listOf(
        AcademyItem("Color de tu mochila", true, "Describe una cualidad: no se mide con números."),
        AcademyItem("Número de hermanos", false, "Se puede contar y sumar: es una cantidad."),
        AcademyItem("Tu deporte favorito", true, "Es una categoría que eliges, no una medida."),
        AcademyItem("Altura en centímetros", false, "Se mide con una unidad (cm): es numérica."),
        AcademyItem("Tipo de mascota", true, "Perro, gato, pez... son categorías."),
        AcademyItem("Minutos de tarea", false, "Se cuenta en minutos: es una medida."),
        AcademyItem("Género de película preferido", true, "Aventura, comedia, terror... son categorías."),
        AcademyItem("Peso de la mochila en kg", false, "Se mide en kilogramos: es numérica."),
        AcademyItem("Medio de transporte al cole", true, "Bici, bus, a pie... son categorías."),
        AcademyItem("Goles marcados en el torneo", false, "Se cuenta: es una cantidad numérica."),
    )
}
