package com.educalab.staticdata.data.local.seed

import com.educalab.staticdata.domain.model.DataVariableType

/** Especificación declarativa de un dataset semilla (sin datos aún generados). */
data class DatasetSeedSpec(
    val title: String,
    val category: String,
    val variableName: String,
    val type: DataVariableType,
    val labelPool: List<String> = emptyList(),
    val weights: List<Int> = emptyList(),
    val numericRange: IntPair? = null,
    val sampleSize: Int,
    val seed: Long
)

data class IntPair(val min: Int, val max: Int)

/** Los 6 pools temáticos reutilizados por las 30 fichas semilla. */
object LabelPools {
    val FRUTAS = listOf("Manzana", "Plátano", "Naranja", "Fresa", "Uva", "Pera")
    val MASCOTAS = listOf("Perro", "Gato", "Pez", "Hámster", "Tortuga", "Conejo")
    val DEPORTES = listOf("Fútbol", "Baloncesto", "Natación", "Atletismo", "Ciclismo", "Voleibol")
    val LIBROS = listOf("Aventura", "Misterio", "Fantasía", "Ciencia ficción", "Cómic", "Poesía")
    val TRANSPORTES = listOf("Bicicleta", "Autobús", "Coche", "Patineta", "A pie", "Metro")
}

/** 30 datasets semilla: 25 categóricos (5 por categoría) + 5 numéricos (mediciones). */
object DatasetSeedCatalog {

    fun all(): List<DatasetSeedSpec> = frutas() + mascotas() + deportes() + libros() + transportes() + mediciones()

    private fun frutas() = listOf(
        DatasetSeedSpec("Frutas favoritas de 3ºA", "Frutas", "Fruta preferida", DataVariableType.CATEGORICA,
            LabelPools.FRUTAS, listOf(9, 6, 5, 7, 3, 2), sampleSize = 32, seed = 101L),
        DatasetSeedSpec("Frutas del puesto del mercado", "Frutas", "Fruta vendida", DataVariableType.CATEGORICA,
            LabelPools.FRUTAS, listOf(4, 8, 6, 3, 9, 5), sampleSize = 35, seed = 102L),
        DatasetSeedSpec("Frutas en la lonchera de la semana", "Frutas", "Fruta llevada", DataVariableType.CATEGORICA,
            LabelPools.FRUTAS, listOf(7, 7, 4, 10, 2, 6), sampleSize = 36, seed = 103L),
        DatasetSeedSpec("Frutas del club de excursionismo", "Frutas", "Fruta elegida", DataVariableType.CATEGORICA,
            LabelPools.FRUTAS, listOf(5, 3, 9, 4, 8, 6), sampleSize = 35, seed = 104L),
        DatasetSeedSpec("Frutas más vendidas en el kiosco", "Frutas", "Fruta comprada", DataVariableType.CATEGORICA,
            LabelPools.FRUTAS, listOf(6, 9, 3, 5, 4, 7), sampleSize = 34, seed = 105L),
    )

    private fun mascotas() = listOf(
        DatasetSeedSpec("Mascotas del barrio Girasol", "Mascotas", "Mascota en casa", DataVariableType.CATEGORICA,
            LabelPools.MASCOTAS, listOf(10, 8, 3, 4, 2, 5), sampleSize = 32, seed = 201L),
        DatasetSeedSpec("Mascotas de la clase de 4ºB", "Mascotas", "Mascota favorita", DataVariableType.CATEGORICA,
            LabelPools.MASCOTAS, listOf(7, 9, 2, 5, 3, 6), sampleSize = 32, seed = 202L),
        DatasetSeedSpec("Mascotas adoptadas este año", "Mascotas", "Mascota adoptada", DataVariableType.CATEGORICA,
            LabelPools.MASCOTAS, listOf(6, 6, 5, 3, 4, 8), sampleSize = 32, seed = 203L),
        DatasetSeedSpec("Mascotas del refugio Vallecito", "Mascotas", "Mascota en el refugio", DataVariableType.CATEGORICA,
            LabelPools.MASCOTAS, listOf(5, 4, 7, 6, 3, 9), sampleSize = 34, seed = 204L),
        DatasetSeedSpec("Mascotas favoritas del campamento", "Mascotas", "Mascota soñada", DataVariableType.CATEGORICA,
            LabelPools.MASCOTAS, listOf(8, 7, 3, 5, 2, 6), sampleSize = 31, seed = 205L),
    )

    private fun deportes() = listOf(
        DatasetSeedSpec("Deportes en el recreo largo", "Deportes", "Deporte practicado", DataVariableType.CATEGORICA,
            LabelPools.DEPORTES, listOf(9, 7, 4, 6, 3, 5), sampleSize = 34, seed = 301L),
        DatasetSeedSpec("Deportes del torneo de verano", "Deportes", "Deporte inscrito", DataVariableType.CATEGORICA,
            LabelPools.DEPORTES, listOf(6, 8, 5, 4, 7, 3), sampleSize = 33, seed = 302L),
        DatasetSeedSpec("Deportes favoritos de 5ºC", "Deportes", "Deporte favorito", DataVariableType.CATEGORICA,
            LabelPools.DEPORTES, listOf(5, 5, 9, 3, 4, 7), sampleSize = 33, seed = 303L),
        DatasetSeedSpec("Deportes del club deportivo Aurora", "Deportes", "Deporte elegido", DataVariableType.CATEGORICA,
            LabelPools.DEPORTES, listOf(7, 4, 6, 8, 3, 5), sampleSize = 33, seed = 304L),
        DatasetSeedSpec("Deportes practicados en vacaciones", "Deportes", "Deporte practicado", DataVariableType.CATEGORICA,
            LabelPools.DEPORTES, listOf(4, 6, 7, 5, 8, 3), sampleSize = 33, seed = 305L),
    )

    private fun libros() = listOf(
        DatasetSeedSpec("Libros prestados en la biblioteca", "Libros", "Género prestado", DataVariableType.CATEGORICA,
            LabelPools.LIBROS, listOf(8, 6, 9, 4, 5, 3), sampleSize = 35, seed = 401L),
        DatasetSeedSpec("Libros del club de lectura", "Libros", "Género leído", DataVariableType.CATEGORICA,
            LabelPools.LIBROS, listOf(5, 7, 6, 8, 3, 4), sampleSize = 33, seed = 402L),
        DatasetSeedSpec("Libros favoritos de 6ºA", "Libros", "Género favorito", DataVariableType.CATEGORICA,
            LabelPools.LIBROS, listOf(9, 4, 5, 6, 7, 2), sampleSize = 33, seed = 403L),
        DatasetSeedSpec("Libros del rincón de lectura", "Libros", "Género elegido", DataVariableType.CATEGORICA,
            LabelPools.LIBROS, listOf(6, 6, 4, 9, 5, 3), sampleSize = 33, seed = 404L),
        DatasetSeedSpec("Libros regalados en Navidad", "Libros", "Género regalado", DataVariableType.CATEGORICA,
            LabelPools.LIBROS, listOf(4, 8, 6, 5, 3, 7), sampleSize = 33, seed = 405L),
    )

    private fun transportes() = listOf(
        DatasetSeedSpec("Transporte para ir al cole", "Transportes", "Medio de transporte", DataVariableType.CATEGORICA,
            LabelPools.TRANSPORTES, listOf(6, 9, 5, 4, 8, 3), sampleSize = 35, seed = 501L),
        DatasetSeedSpec("Transporte usado el fin de semana", "Transportes", "Medio de transporte", DataVariableType.CATEGORICA,
            LabelPools.TRANSPORTES, listOf(5, 5, 7, 6, 4, 8), sampleSize = 34, seed = 502L),
        DatasetSeedSpec("Transporte del viaje de estudios", "Transportes", "Medio de transporte", DataVariableType.CATEGORICA,
            LabelPools.TRANSPORTES, listOf(3, 4, 9, 5, 6, 7), sampleSize = 34, seed = 503L),
        DatasetSeedSpec("Transporte preferido en verano", "Transportes", "Medio de transporte", DataVariableType.CATEGORICA,
            LabelPools.TRANSPORTES, listOf(8, 6, 4, 7, 5, 3), sampleSize = 33, seed = 504L),
        DatasetSeedSpec("Transporte de la excursión al parque", "Transportes", "Medio de transporte", DataVariableType.CATEGORICA,
            LabelPools.TRANSPORTES, listOf(7, 5, 6, 8, 3, 4), sampleSize = 33, seed = 505L),
    )

    private fun mediciones() = listOf(
        DatasetSeedSpec("Altura de las plantas del huerto (cm)", "Mediciones", "Altura en cm", DataVariableType.NUMERICA,
            numericRange = IntPair(8, 42), sampleSize = 28, seed = 601L),
        DatasetSeedSpec("Tiempo de la carrera de 100m (seg)", "Mediciones", "Tiempo en segundos", DataVariableType.NUMERICA,
            numericRange = IntPair(14, 26), sampleSize = 28, seed = 602L),
        DatasetSeedSpec("Peso de las mochilas escolares (kg)", "Mediciones", "Peso en kg", DataVariableType.NUMERICA,
            numericRange = IntPair(2, 9), sampleSize = 28, seed = 603L),
        DatasetSeedSpec("Temperatura registrada al mediodía (°C)", "Mediciones", "Temperatura en °C", DataVariableType.NUMERICA,
            numericRange = IntPair(12, 34), sampleSize = 28, seed = 604L),
        DatasetSeedSpec("Minutos de lectura diaria del grupo", "Mediciones", "Minutos de lectura", DataVariableType.NUMERICA,
            numericRange = IntPair(5, 45), sampleSize = 28, seed = 605L),
    )
}
