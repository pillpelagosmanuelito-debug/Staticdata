-- ============================================================
-- Staticdata — Esquema de base de datos (Room / SQLite)
-- Generado a partir de las entidades reales en
-- app/src/main/java/com/educalab/staticdata/data/local/entity/Entities.kt
-- ============================================================

PRAGMA foreign_keys = ON;

CREATE TABLE user_profile (
    id INTEGER PRIMARY KEY AUTOINCREMENT,
    alias TEXT NOT NULL,
    avatarId INTEGER NOT NULL,
    createdAtEpochMillis INTEGER NOT NULL,
    onboardingCompleted INTEGER NOT NULL DEFAULT 0,
    soundEnabled INTEGER NOT NULL DEFAULT 1,
    hapticsEnabled INTEGER NOT NULL DEFAULT 1
);

CREATE TABLE progress (
    userId INTEGER PRIMARY KEY NOT NULL,
    totalXp INTEGER NOT NULL DEFAULT 0,
    level INTEGER NOT NULL DEFAULT 1,
    casesCompleted INTEGER NOT NULL DEFAULT 0,
    exercisesCompleted INTEGER NOT NULL DEFAULT 0,
    exercisesCorrectFirstTry INTEGER NOT NULL DEFAULT 0,
    FOREIGN KEY (userId) REFERENCES user_profile(id) ON DELETE CASCADE
);

CREATE TABLE dataset (
    id INTEGER PRIMARY KEY AUTOINCREMENT,
    title TEXT NOT NULL,
    category TEXT NOT NULL,
    isUserGenerated INTEGER NOT NULL,
    createdAtEpochMillis INTEGER NOT NULL
);

CREATE TABLE data_variable (
    id INTEGER PRIMARY KEY AUTOINCREMENT,
    datasetId INTEGER NOT NULL,
    name TEXT NOT NULL,
    type TEXT NOT NULL, -- 'CATEGORICA' | 'NUMERICA'
    FOREIGN KEY (datasetId) REFERENCES dataset(id) ON DELETE CASCADE
);
CREATE INDEX index_data_variable_datasetId ON data_variable(datasetId);

CREATE TABLE data_value (
    id INTEGER PRIMARY KEY AUTOINCREMENT,
    variableId INTEGER NOT NULL,
    label TEXT NOT NULL,
    numericValue REAL,
    FOREIGN KEY (variableId) REFERENCES data_variable(id) ON DELETE CASCADE
);
CREATE INDEX index_data_value_variableId ON data_value(variableId);

CREATE TABLE case_file (
    id INTEGER PRIMARY KEY AUTOINCREMENT,
    title TEXT NOT NULL,
    briefing TEXT NOT NULL,
    category TEXT NOT NULL,
    datasetId INTEGER NOT NULL,
    minLevel INTEGER NOT NULL,
    status TEXT NOT NULL DEFAULT 'BLOQUEADO', -- BLOQUEADO|DISPONIBLE|INICIADO|COMPLETADO|DOMINADO
    orderIndex INTEGER NOT NULL,
    FOREIGN KEY (datasetId) REFERENCES dataset(id) ON DELETE CASCADE
);
CREATE INDEX index_case_file_datasetId ON case_file(datasetId);

CREATE TABLE survey (
    id INTEGER PRIMARY KEY AUTOINCREMENT,
    question TEXT NOT NULL,
    createdAtEpochMillis INTEGER NOT NULL
);

CREATE TABLE survey_option (
    id INTEGER PRIMARY KEY AUTOINCREMENT,
    surveyId INTEGER NOT NULL,
    label TEXT NOT NULL,
    orderIndex INTEGER NOT NULL,
    FOREIGN KEY (surveyId) REFERENCES survey(id) ON DELETE CASCADE
);
CREATE INDEX index_survey_option_surveyId ON survey_option(surveyId);

CREATE TABLE survey_response (
    id INTEGER PRIMARY KEY AUTOINCREMENT,
    surveyId INTEGER NOT NULL,
    optionId INTEGER NOT NULL,
    respondentAlias TEXT NOT NULL,
    FOREIGN KEY (surveyId) REFERENCES survey(id) ON DELETE CASCADE,
    FOREIGN KEY (optionId) REFERENCES survey_option(id) ON DELETE CASCADE
);
CREATE INDEX index_survey_response_surveyId ON survey_response(surveyId);
CREATE INDEX index_survey_response_optionId ON survey_response(optionId);

CREATE TABLE frequency_table (
    id INTEGER PRIMARY KEY AUTOINCREMENT,
    sourceType TEXT NOT NULL, -- 'DATASET' | 'SURVEY'
    sourceId INTEGER NOT NULL,
    total INTEGER NOT NULL,
    computedAtEpochMillis INTEGER NOT NULL
);

CREATE TABLE frequency_row (
    id INTEGER PRIMARY KEY AUTOINCREMENT,
    tableId INTEGER NOT NULL,
    label TEXT NOT NULL,
    count INTEGER NOT NULL,
    relativeFrequency REAL NOT NULL,
    percentage REAL NOT NULL,
    FOREIGN KEY (tableId) REFERENCES frequency_table(id) ON DELETE CASCADE
);
CREATE INDEX index_frequency_row_tableId ON frequency_row(tableId);

CREATE TABLE exercise (
    id INTEGER PRIMARY KEY AUTOINCREMENT,
    caseId INTEGER NOT NULL,
    type TEXT NOT NULL, -- ORDENAR_FRECUENCIA|DATO_EXTRANO|COMPLETAR_TABLA|IDENTIFICAR_MODA|INTERPRETAR_PORCENTAJE|CLASIFICAR_TIPO
    prompt TEXT NOT NULL,
    datasetId INTEGER,
    optionsEncoded TEXT NOT NULL,        -- lista codificada con separador U+001F
    correctAnswerEncoded TEXT NOT NULL,  -- lista codificada con separador U+001F
    explanation TEXT NOT NULL,
    difficulty INTEGER NOT NULL,
    FOREIGN KEY (caseId) REFERENCES case_file(id) ON DELETE CASCADE
);
CREATE INDEX index_exercise_caseId ON exercise(caseId);

CREATE TABLE attempt (
    id INTEGER PRIMARY KEY AUTOINCREMENT,
    exerciseId INTEGER NOT NULL,
    givenAnswerEncoded TEXT NOT NULL,
    correct INTEGER NOT NULL,
    timestampEpochMillis INTEGER NOT NULL,
    FOREIGN KEY (exerciseId) REFERENCES exercise(id) ON DELETE CASCADE
);
CREATE INDEX index_attempt_exerciseId ON attempt(exerciseId);

CREATE TABLE sample_experiment (
    id INTEGER PRIMARY KEY AUTOINCREMENT,
    title TEXT NOT NULL,
    populationDatasetId INTEGER NOT NULL,
    description TEXT NOT NULL,
    FOREIGN KEY (populationDatasetId) REFERENCES dataset(id) ON DELETE CASCADE
);
CREATE INDEX index_sample_experiment_populationDatasetId ON sample_experiment(populationDatasetId);

CREATE TABLE sample_run (
    id INTEGER PRIMARY KEY AUTOINCREMENT,
    experimentId INTEGER NOT NULL,
    sampleSize INTEGER NOT NULL,
    seed INTEGER NOT NULL,
    drawnLabelsEncoded TEXT NOT NULL,
    timestampEpochMillis INTEGER NOT NULL,
    FOREIGN KEY (experimentId) REFERENCES sample_experiment(id) ON DELETE CASCADE
);
CREATE INDEX index_sample_run_experimentId ON sample_run(experimentId);

CREATE TABLE badge (
    id INTEGER PRIMARY KEY AUTOINCREMENT,
    code TEXT NOT NULL,
    title TEXT NOT NULL,
    description TEXT NOT NULL,
    iconKey TEXT NOT NULL,
    requirement TEXT NOT NULL -- "TIPO:VALOR", p.ej. "CASES:3"
);

CREATE TABLE user_badge (
    userId INTEGER NOT NULL,
    badgeId INTEGER NOT NULL,
    unlockedAtEpochMillis INTEGER NOT NULL,
    PRIMARY KEY (userId, badgeId),
    FOREIGN KEY (userId) REFERENCES user_profile(id) ON DELETE CASCADE,
    FOREIGN KEY (badgeId) REFERENCES badge(id) ON DELETE CASCADE
);
CREATE INDEX index_user_badge_badgeId ON user_badge(badgeId);
