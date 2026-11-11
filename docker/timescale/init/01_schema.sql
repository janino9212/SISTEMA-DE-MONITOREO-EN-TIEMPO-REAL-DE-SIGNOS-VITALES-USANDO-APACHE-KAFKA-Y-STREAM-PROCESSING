-- Se ejecuta una sola vez, al crear el volumen de TimescaleDB.
CREATE EXTENSION IF NOT EXISTS timescaledb;

-- RF-19: lecturas (hipertabla particionada por tiempo)
CREATE TABLE IF NOT EXISTS lecturas (
    t            TIMESTAMPTZ NOT NULL,
    id_paciente  TEXT        NOT NULL,
    fc           INT,
    pas          INT,
    pad          INT,
    temp         NUMERIC(4,1),
    spo2         INT,
    id_escenario TEXT,
    PRIMARY KEY (id_paciente, t)   -- permite INSERT ... ON CONFLICT DO NOTHING (RF-23)
);
SELECT create_hypertable('lecturas', 't', if_not_exists => TRUE);

-- RF-20: alertas
CREATE TABLE IF NOT EXISTS alertas (
    id_alerta    TEXT        NOT NULL,
    t_alerta     TIMESTAMPTZ NOT NULL,
    t_lectura    TIMESTAMPTZ NOT NULL,
    id_paciente  TEXT        NOT NULL,
    regla        TEXT        NOT NULL,
    severidad    TEXT        NOT NULL,
    signo        TEXT,
    valor        DOUBLE PRECISION,
    puntaje      INT,
    id_escenario TEXT,
    PRIMARY KEY (id_alerta, t_alerta)
);
SELECT create_hypertable('alertas', 't_alerta', if_not_exists => TRUE);

-- RF-22: retención de lecturas crudas
SELECT add_retention_policy('lecturas', INTERVAL '30 days', if_not_exists => TRUE);

-- TODO RF-21: agregados continuos horarios (se crean aparte, fuera de una transacción).
