-- ============================================================
-- Staticdata — Datos de ejemplo
-- El contenido semilla COMPLETO (30 datasets, 10 casos con 30
-- ejercicios calculados por StatsEngine, 3 experimentos de
-- muestreo y 10 insignias) se genera en tiempo de ejecución por
-- app/src/main/java/com/educalab/staticdata/data/local/seed/SeedProvider.kt
-- para poder derivar respuestas correctas de forma consistente
-- con StatsEngine en cada instalación.
--
-- Este archivo es un EXTRACTO representativo (dos datasets, un
-- caso completo con sus ejercicios, un experimento de muestreo,
-- una encuesta de ejemplo y las 10 insignias) útil para inspeccionar
-- la forma real de los datos sin tener que ejecutar la app.
-- ============================================================

-- --- Insignias (10) ---
INSERT INTO badge (id, code, title, description, iconKey, requirement) VALUES
 (1, 'PRIMEROS_PASOS', 'Primeros pasos', 'Completa tu primer caso.', 'badge_first_case', 'CASES:1'),
 (2, 'INVESTIGADOR_CONSTANTE', 'Investigador constante', 'Completa 3 casos.', 'badge_cases_3', 'CASES:3'),
 (3, 'MAESTRO_DE_CASOS', 'Maestro de casos', 'Completa 8 casos.', 'badge_cases_8', 'CASES:8'),
 (4, 'CEREBRO_DE_DATOS', 'Cerebro de datos', 'Resuelve correctamente 10 ejercicios.', 'badge_exercises_10', 'EXERCISES:10'),
 (5, 'COLECCIONISTA_DE_DATOS', 'Coleccionista de datos', 'Resuelve correctamente 25 ejercicios.', 'badge_exercises_25', 'EXERCISES:25'),
 (6, 'RACHA_PERFECTA', 'Racha perfecta', 'Acierta 5 ejercicios a la primera.', 'badge_first_try_5', 'FIRST_TRY:5'),
 (7, 'NIVEL_EXPERTO', 'Nivel experto', 'Alcanza el nivel 4.', 'badge_level_4', 'LEVEL:4'),
 (8, 'DETECTIVE_COMPLETO', 'Detective completo', 'Alcanza el nivel 6.', 'badge_level_6', 'LEVEL:6'),
 (9, 'CIENTIFICO_DE_MUESTRAS', 'Científico de muestras', 'Realiza 5 tiradas en el laboratorio.', 'badge_samples_5', 'SAMPLES:5'),
 (10, 'ENCUESTADOR_ESTRELLA', 'Encuestador estrella', 'Crea 3 encuestas propias.', 'badge_surveys_3', 'SURVEYS:3');

-- --- Dataset 1: categórico ("Frutas favoritas de 3ºA") ---
INSERT INTO dataset (id, title, category, isUserGenerated, createdAtEpochMillis) VALUES
 (1, 'Frutas favoritas de 3ºA', 'Frutas', 0, 1735689600000);
INSERT INTO data_variable (id, datasetId, name, type) VALUES
 (1, 1, 'Fruta preferida', 'CATEGORICA');
INSERT INTO data_value (variableId, label) VALUES
 (1,'Manzana'),(1,'Manzana'),(1,'Plátano'),(1,'Fresa'),(1,'Manzana'),(1,'Naranja'),
 (1,'Plátano'),(1,'Fresa'),(1,'Manzana'),(1,'Uva'),(1,'Fresa'),(1,'Plátano');
-- (el dataset real generado por la app contiene 32 filas; se muestran 12 a modo de ejemplo)

-- --- Dataset 6: numérico ("Altura de las plantas del huerto (cm)") ---
INSERT INTO dataset (id, title, category, isUserGenerated, createdAtEpochMillis) VALUES
 (26, 'Altura de las plantas del huerto (cm)', 'Mediciones', 0, 1735689600000);
INSERT INTO data_variable (id, datasetId, name, type) VALUES
 (26, 26, 'Altura en cm', 'NUMERICA');
INSERT INTO data_value (variableId, label, numericValue) VALUES
 (26,'Medición 1', 18),(26,'Medición 2', 24),(26,'Medición 3', 31),(26,'Medición 4', 12),
 (26,'Medición 5', 27),(26,'Medición 6', 22),(26,'Medición 7', 35),(26,'Medición 8', 19);

-- --- Caso 1: "El misterio del cesto de frutas" (usa el dataset 1) ---
INSERT INTO case_file (id, title, briefing, category, datasetId, minLevel, status, orderIndex) VALUES
 (1, 'El misterio del cesto de frutas',
     'La cafetería del cole quiere saber qué fruta comprar más la próxima semana. Dati necesita tu ayuda para leer los datos del cesto y descubrir la favorita.',
     'Frutas', 1, 1, 'DISPONIBLE', 0);

-- Ejercicios reales del caso 1 (respuestas derivadas de los datos anteriores por StatsEngine)
INSERT INTO exercise (id, caseId, type, prompt, datasetId, optionsEncoded, correctAnswerEncoded, explanation, difficulty) VALUES
 (1, 1, 'ORDENAR_FRECUENCIA', 'Ordena estas opciones de fruta preferida de la más elegida a la menos elegida.', 1,
     'Plátano' || X'1F' || 'Manzana' || X'1F' || 'Fresa' || X'1F' || 'Naranja' || X'1F' || 'Uva',
     'Manzana' || X'1F' || 'Plátano' || X'1F' || 'Fresa' || X'1F' || 'Naranja' || X'1F' || 'Uva',
     'El orden correcto de mayor a menor frecuencia es: Manzana > Plátano > Fresa > Naranja > Uva.', 1),
 (2, 1, 'DATO_EXTRANO', 'De estas opciones, ¿cuál es el dato extraño (el menos frecuente)?', 1,
     'Manzana' || X'1F' || 'Plátano' || X'1F' || 'Fresa' || X'1F' || 'Uva',
     'Uva',
     'Uva aparece solo 1 vez, mucho menos que el resto.', 1),
 (3, 1, 'IDENTIFICAR_MODA', '¿Cuál es la moda de fruta preferida en este caso?', 1,
     'Manzana' || X'1F' || 'Plátano' || X'1F' || 'Fresa' || X'1F' || 'Naranja',
     'Manzana',
     'La moda es el valor que más se repite: Manzana, con la mayor cantidad de apariciones.', 1);

-- --- Experimento de muestreo (usa el dataset 3 "Frutas en la lonchera de la semana" como población) ---
INSERT INTO sample_experiment (id, title, populationDatasetId, description) VALUES
 (1, 'Laboratorio: Frutas en la lonchera de la semana', 3,
     'Dati reunió los datos de fruta de todo el mercado. Extrae muestras pequeñas y compara si siempre "ganan" las mismas frutas.');

-- --- Encuesta local de ejemplo, creada por un usuario ---
INSERT INTO survey (id, question, createdAtEpochMillis) VALUES
 (1, '¿Cuál es tu mascota favorita?', 1735689600000);
INSERT INTO survey_option (id, surveyId, label, orderIndex) VALUES
 (1, 1, 'Perro', 0), (2, 1, 'Gato', 1), (3, 1, 'Pez', 2);
INSERT INTO survey_response (surveyId, optionId, respondentAlias) VALUES
 (1, 1, 'AlumnoA'), (1, 1, 'AlumnoB'), (1, 2, 'AlumnoC');
