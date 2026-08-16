package com.educalab.staticdata.data.local.seed

import com.educalab.staticdata.data.local.StaticdataDatabase
import com.educalab.staticdata.data.local.entity.*
import com.educalab.staticdata.domain.logic.StatsEngine
import com.educalab.staticdata.domain.model.DataValue
import com.educalab.staticdata.domain.model.DataVariableType
import com.educalab.staticdata.domain.model.ExerciseType
import kotlin.random.Random

/**
 * Genera y persiste el contenido semilla (30 datasets, 10 casos con 3
 * ejercicios cada uno = 30 ejercicios, 3 experimentos de muestreo y 10
 * insignias) para que la primera instalación se sienta completa.
 *
 * Los ejercicios NO están escritos "a mano" con respuestas arbitrarias:
 * se calculan a partir de los datos generados usando [StatsEngine], por
 * lo que la respuesta correcta siempre es consistente con los datos reales
 * que el niño puede explorar en el módulo de datasets.
 */
object SeedProvider {

    suspend fun seedIfEmpty(db: StaticdataDatabase) {
        if (db.datasetDao().countDatasets() > 0) return

        val specs = DatasetSeedCatalog.all()
        // datasetId real (autogenerado) por índice de spec, en el mismo orden que specs
        val datasetIds = mutableListOf<Long>()
        val valuesByDatasetIndex = mutableListOf<List<DataValueEntity>>()

        specs.forEach { spec ->
            val datasetId = db.datasetDao().insertDataset(
                DatasetEntity(
                    title = spec.title,
                    category = spec.category,
                    isUserGenerated = false,
                    createdAtEpochMillis = System.currentTimeMillis()
                )
            )
            val variableId = db.datasetDao().insertVariable(
                DataVariableEntity(datasetId = datasetId, name = spec.variableName, type = spec.type.name)
            )
            val values = generateValues(spec, variableId)
            db.datasetDao().insertValues(values)
            datasetIds += datasetId
            valuesByDatasetIndex += values
        }

        seedCases(db, specs, datasetIds, valuesByDatasetIndex)
        seedSampleExperiments(db, specs, datasetIds)
        seedBadges(db)
    }

    // -------------------------------------------------------------------
    // Generación de valores
    // -------------------------------------------------------------------

    private fun generateValues(spec: DatasetSeedSpec, variableId: Long): List<DataValueEntity> {
        val rng = Random(spec.seed)
        return if (spec.type == DataVariableType.CATEGORICA) {
            (1..spec.sampleSize).map {
                DataValueEntity(
                    variableId = variableId,
                    label = weightedPick(rng, spec.labelPool, spec.weights)
                )
            }
        } else {
            val range = spec.numericRange!!
            (1..spec.sampleSize).map { i ->
                DataValueEntity(
                    variableId = variableId,
                    label = "Medición $i",
                    numericValue = rng.nextInt(range.min, range.max + 1).toDouble()
                )
            }
        }
    }

    private fun weightedPick(rng: Random, labels: List<String>, weights: List<Int>): String {
        val total = weights.sum()
        var r = rng.nextInt(total)
        for (i in labels.indices) {
            if (r < weights[i]) return labels[i]
            r -= weights[i]
        }
        return labels.last()
    }

    // -------------------------------------------------------------------
    // Casos y ejercicios
    // -------------------------------------------------------------------

    private data class CaseBlueprint(
        val specIndex: Int,
        val title: String,
        val briefing: String,
        val minLevel: Int,
        val status: String
    )

    private fun caseBlueprints(): List<CaseBlueprint> = listOf(
        CaseBlueprint(0, "El misterio del cesto de frutas",
            "La cafetería del cole quiere saber qué fruta comprar más la próxima semana. Dati necesita tu ayuda para leer los datos del cesto y descubrir la favorita.",
            1, "DISPONIBLE"),
        CaseBlueprint(1, "El puesto que no repone bien la fruta",
            "El puesto del mercado se queda sin la fruta más pedida casi todos los días. Analiza el registro de ventas y dile al vendedor qué fruta necesita reponer antes.",
            1, "DISPONIBLE"),
        CaseBlueprint(5, "La feria de mascotas perdida",
            "En la feria del barrio Girasol se perdió la lista de mascotas más comunes. Reconstrúyela a partir de los datos que Dati recuperó del cuaderno de campo.",
            2, "BLOQUEADO"),
        CaseBlueprint(6, "El álbum de mascotas de la clase",
            "4ºB quiere hacer un álbum con la mascota más popular en la portada. Ayuda a la clase a decidir cuál usar con datos, no con opiniones.",
            2, "BLOQUEADO"),
        CaseBlueprint(10, "El campeonato del recreo",
            "Van a organizar un campeonato en el recreo largo, pero solo caben tres deportes. Usa los datos para elegir los tres más jugados.",
            2, "BLOQUEADO"),
        CaseBlueprint(11, "Inscripciones del torneo de verano",
            "El torneo de verano necesita saber cuántas personas se anotaron en cada deporte para reservar las canchas correctas.",
            3, "BLOQUEADO"),
        CaseBlueprint(15, "La biblioteca sin estantería clara",
            "La bibliotecaria quiere reorganizar las estanterías según lo que más se presta. Analiza el registro de préstamos y ayúdala a decidir el orden.",
            3, "BLOQUEADO"),
        CaseBlueprint(16, "El club de lectura en apuros",
            "El club de lectura solo puede elegir un género para el próximo mes. Descubre cuál prefiere la mayoría del club.",
            4, "BLOQUEADO"),
        CaseBlueprint(20, "El atasco a la entrada del cole",
            "Hay demasiado tráfico a la entrada del colegio. El ayuntamiento quiere saber cómo llega la mayoría del alumnado para planificar mejor la calle.",
            4, "BLOQUEADO"),
        CaseBlueprint(21, "El plan del fin de semana perfecto",
            "Un grupo de amigos quiere planear una salida y necesita saber qué transporte usa la mayoría los fines de semana.",
            5, "BLOQUEADO"),
    )

    private suspend fun seedCases(
        db: StaticdataDatabase,
        specs: List<DatasetSeedSpec>,
        datasetIds: List<Long>,
        valuesByIndex: List<List<DataValueEntity>>
    ) {
        val blueprints = caseBlueprints()
        val caseEntities = blueprints.mapIndexed { order, bp ->
            CaseFileEntity(
                title = bp.title,
                briefing = bp.briefing,
                category = specs[bp.specIndex].category,
                datasetId = datasetIds[bp.specIndex],
                minLevel = bp.minLevel,
                status = bp.status,
                orderIndex = order
            )
        }
        db.caseFileDao().insertAll(caseEntities)

        // Recuperamos los ids reales autogenerados (secuenciales) para poder
        // enlazar cada caso con sus ejercicios recién calculados.
        val casesPersisted = fetchCasesOnce(db)

        blueprints.forEachIndexed { order, bp ->
            val caseEntity = casesPersisted.first { it.orderIndex == order }
            val values = valuesByIndex[bp.specIndex].map { DataValue(it.id, it.variableId, it.label, it.numericValue) }
            val freq = StatsEngine.frequencyTable(values)
            val topic = specs[bp.specIndex].variableName.lowercase()

            val exercises = buildExercisesForCase(
                caseId = caseEntity.id,
                datasetId = caseEntity.datasetId,
                freq = freq,
                topic = topic,
                caseIndexSeed = order.toLong()
            )
            db.exerciseDao().insertAll(exercises)
        }
    }

    private suspend fun fetchCasesOnce(db: StaticdataDatabase): List<CaseFileEntity> {
        // Pequeño helper: como CaseFileDao solo expone Flow para lecturas generales,
        // usamos getById iterando por count() ya que autogenerate es secuencial desde 1.
        val total = db.caseFileDao().count()
        return (1..total).mapNotNull { id -> db.caseFileDao().getById(id.toLong()) }
    }

    private fun buildExercisesForCase(
        caseId: Long,
        datasetId: Long,
        freq: com.educalab.staticdata.domain.model.FrequencyTable,
        topic: String,
        caseIndexSeed: Long
    ): List<ExerciseEntity> {
        val rng = Random(1000 + caseIndexSeed)
        val difficulty = (1 + (caseIndexSeed / 2)).toInt().coerceIn(1, 5)

        val rotation = when ((caseIndexSeed % 3).toInt()) {
            0 -> listOf(ExerciseType.ORDENAR_FRECUENCIA, ExerciseType.DATO_EXTRANO, ExerciseType.IDENTIFICAR_MODA)
            1 -> listOf(ExerciseType.COMPLETAR_TABLA, ExerciseType.ORDENAR_FRECUENCIA, ExerciseType.INTERPRETAR_PORCENTAJE)
            else -> listOf(ExerciseType.CLASIFICAR_TIPO, ExerciseType.DATO_EXTRANO, ExerciseType.ORDENAR_FRECUENCIA)
        }

        return rotation.mapIndexed { i, type ->
            buildSingleExercise(type, caseId, datasetId, freq, topic, difficulty + i, rng, caseIndexSeed)
        }
    }

    private fun buildSingleExercise(
        type: ExerciseType,
        caseId: Long,
        datasetId: Long,
        freq: com.educalab.staticdata.domain.model.FrequencyTable,
        topic: String,
        difficulty: Int,
        rng: Random,
        caseIndexSeed: Long
    ): ExerciseEntity = when (type) {
        ExerciseType.ORDENAR_FRECUENCIA -> {
            val correct = freq.rows.map { it.label }
            val options = correct.shuffled(rng)
            ExerciseEntity(
                caseId = caseId, type = type.name,
                prompt = "Ordena estas opciones de $topic de la más elegida a la menos elegida.",
                datasetId = datasetId,
                optionsEncoded = enc(options), correctAnswerEncoded = enc(correct),
                explanation = "El orden correcto de mayor a menor frecuencia es: ${correct.joinToString(" > ")}.",
                difficulty = difficulty
            )
        }
        ExerciseType.DATO_EXTRANO -> {
            val top3 = freq.rows.take(3).map { it.label }
            val lowest = freq.rows.last().label
            val options = (top3 + lowest).distinct().shuffled(rng)
            ExerciseEntity(
                caseId = caseId, type = type.name,
                prompt = "De estas opciones, ¿cuál es el dato extraño (el menos frecuente)?",
                datasetId = datasetId,
                optionsEncoded = enc(options), correctAnswerEncoded = enc(listOf(lowest)),
                explanation = "'$lowest' aparece solo ${freq.rows.last().count} veces, mucho menos que el resto.",
                difficulty = difficulty
            )
        }
        ExerciseType.IDENTIFICAR_MODA -> {
            val options = freq.rows.take(4).map { it.label }
            ExerciseEntity(
                caseId = caseId, type = type.name,
                prompt = "¿Cuál es la moda de $topic en este caso?",
                datasetId = datasetId,
                optionsEncoded = enc(options), correctAnswerEncoded = enc(freq.modes),
                explanation = "La moda es el valor que más se repite: ${freq.modes.joinToString()} con ${freq.rows.first().count} apariciones.",
                difficulty = difficulty
            )
        }
        ExerciseType.INTERPRETAR_PORCENTAJE -> {
            val top = freq.rows.first()
            val correctPct = "${top.percentage}%"
            val d1 = "${niceRound(top.percentage + 9)}%"
            val d2 = "${niceRound((top.percentage - 12).coerceAtLeast(1.0))}%"
            val options = listOf(correctPct, d1, d2).distinct().shuffled(rng)
            ExerciseEntity(
                caseId = caseId, type = type.name,
                prompt = "¿Qué porcentaje del total representa '${top.label}'?",
                datasetId = datasetId,
                optionsEncoded = enc(options), correctAnswerEncoded = enc(listOf(correctPct)),
                explanation = "'${top.label}' aparece ${top.count} de ${freq.total} veces: ${top.count}/${freq.total} × 100 = ${top.percentage}%.",
                difficulty = difficulty
            )
        }
        ExerciseType.COMPLETAR_TABLA -> {
            val hidden = freq.rows[freq.rows.size / 2]
            val correct = hidden.count.toString()
            val options = listOf(correct, (hidden.count + 2).toString(), (hidden.count - 2).coerceAtLeast(0).toString()).distinct()
            ExerciseEntity(
                caseId = caseId, type = type.name,
                prompt = "A la tabla de $topic le falta un dato. ¿Cuántas veces aparece '${hidden.label}'?",
                datasetId = datasetId,
                optionsEncoded = enc(options), correctAnswerEncoded = enc(listOf(correct)),
                explanation = "Contando los datos originales, '${hidden.label}' aparece ${hidden.count} veces.",
                difficulty = difficulty
            )
        }
        ExerciseType.CLASIFICAR_TIPO -> {
            val pool = classifyPools[(caseIndexSeed % classifyPools.size).toInt()]
            ExerciseEntity(
                caseId = caseId, type = type.name,
                prompt = "Clasifica cada variable como categórica o numérica.",
                datasetId = null,
                optionsEncoded = enc(pool.map { it.first }),
                correctAnswerEncoded = enc(pool.map { it.second }),
                explanation = "Una variable es categórica si describe una cualidad (colores, nombres, tipos) y numérica si describe una cantidad medible.",
                difficulty = difficulty
            )
        }
    }

    private val classifyPools = listOf(
        listOf("Color favorito" to "Categórica", "Altura en cm" to "Numérica", "Deporte practicado" to "Categórica", "Peso en kg" to "Numérica"),
        listOf("Número de goles marcados" to "Numérica", "Nombre del equipo" to "Categórica", "Tiempo en segundos" to "Numérica", "Género del libro" to "Categórica"),
        listOf("Medio de transporte" to "Categórica", "Minutos de viaje" to "Numérica", "Mascota preferida" to "Categórica", "Edad en años" to "Numérica")
    )

    private fun niceRound(v: Double): Double = Math.round(v * 10.0) / 10.0

    private fun enc(list: List<String>) = com.educalab.staticdata.data.local.converters.StringListConverter.encode(list)

    // -------------------------------------------------------------------
    // Laboratorio de muestras
    // -------------------------------------------------------------------

    private suspend fun seedSampleExperiments(db: StaticdataDatabase, specs: List<DatasetSeedSpec>, datasetIds: List<Long>) {
        val experimentSpecIndexes = listOf(2, 7, 22) // dataset índices no usados en casos
        val descriptions = listOf(
            "Dati reunió los datos de fruta de todo el mercado. Extrae muestras pequeñas y compara si siempre 'ganan' las mismas frutas.",
            "Esta es la población completa de mascotas registradas en el refugio Vallecito. Compara varias muestras para ver cuánto varían.",
            "Los datos de transporte del viaje de estudios son tu población. Comprueba si una muestra pequeña representa bien al grupo completo."
        )
        val experiments = experimentSpecIndexes.mapIndexed { i, specIdx ->
            SampleExperimentEntity(
                title = "Laboratorio: ${specs[specIdx].title}",
                populationDatasetId = datasetIds[specIdx],
                description = descriptions[i]
            )
        }
        db.sampleDao().insertExperiments(experiments)
    }

    // -------------------------------------------------------------------
    // Insignias
    // -------------------------------------------------------------------

    private suspend fun seedBadges(db: StaticdataDatabase) {
        val badges = listOf(
            BadgeEntity(code = "PRIMEROS_PASOS", title = "Primeros pasos", description = "Completa tu primer caso.", iconKey = "badge_first_case", requirement = "CASES:1"),
            BadgeEntity(code = "INVESTIGADOR_CONSTANTE", title = "Investigador constante", description = "Completa 3 casos.", iconKey = "badge_cases_3", requirement = "CASES:3"),
            BadgeEntity(code = "MAESTRO_DE_CASOS", title = "Maestro de casos", description = "Completa 8 casos.", iconKey = "badge_cases_8", requirement = "CASES:8"),
            BadgeEntity(code = "CEREBRO_DE_DATOS", title = "Cerebro de datos", description = "Resuelve correctamente 10 ejercicios.", iconKey = "badge_exercises_10", requirement = "EXERCISES:10"),
            BadgeEntity(code = "COLECCIONISTA_DE_DATOS", title = "Coleccionista de datos", description = "Resuelve correctamente 25 ejercicios.", iconKey = "badge_exercises_25", requirement = "EXERCISES:25"),
            BadgeEntity(code = "RACHA_PERFECTA", title = "Racha perfecta", description = "Acierta 5 ejercicios a la primera.", iconKey = "badge_first_try_5", requirement = "FIRST_TRY:5"),
            BadgeEntity(code = "NIVEL_EXPERTO", title = "Nivel experto", description = "Alcanza el nivel 4.", iconKey = "badge_level_4", requirement = "LEVEL:4"),
            BadgeEntity(code = "DETECTIVE_COMPLETO", title = "Detective completo", description = "Alcanza el nivel 6.", iconKey = "badge_level_6", requirement = "LEVEL:6"),
            BadgeEntity(code = "CIENTIFICO_DE_MUESTRAS", title = "Científico de muestras", description = "Realiza 5 tiradas en el laboratorio.", iconKey = "badge_samples_5", requirement = "SAMPLES:5"),
            BadgeEntity(code = "ENCUESTADOR_ESTRELLA", title = "Encuestador estrella", description = "Crea 3 encuestas propias.", iconKey = "badge_surveys_3", requirement = "SURVEYS:3"),
        )
        db.badgeDao().insertAll(badges)
    }
}
