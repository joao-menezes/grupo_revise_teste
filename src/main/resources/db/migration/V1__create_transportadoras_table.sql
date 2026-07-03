CREATE TABLE transportadoras (
    id                          BIGSERIAL PRIMARY KEY,
    nome                        VARCHAR(100)     NOT NULL,

    preco_base                  NUMERIC(10,2)    NOT NULL,
    preco_por_km                NUMERIC(10,2)    NOT NULL,
    preco_por_kg                NUMERIC(10,2)    NOT NULL DEFAULT 0,

    distancia_limite_km         NUMERIC(10,2),
    preco_base_acima            NUMERIC(10,2),
    preco_km_acima              NUMERIC(10,2),
    preco_kg_acima              NUMERIC(10,2),

    seguro_percentual           NUMERIC(6,4)     NOT NULL DEFAULT 0,
    seguro_fixo                 NUMERIC(10,2)    NOT NULL DEFAULT 0,

    peso_min                    NUMERIC(10,2),
    peso_max                    NUMERIC(10,2),
    peso_min_exclusivo          BOOLEAN          NOT NULL DEFAULT FALSE,
    peso_max_exclusivo          BOOLEAN          NOT NULL DEFAULT FALSE,

    nfe_min                     NUMERIC(10,2),
    nfe_max                     NUMERIC(10,2),

    ufs_bloqueadas              TEXT[]           NOT NULL DEFAULT '{}',
    tipos_carga_aceitos         TEXT[]           NOT NULL,

    prazo_base_dias             INT              NOT NULL,
    prazo_km_por_dia            NUMERIC(10,2),
    prazo_limite_km             NUMERIC(10,2),
    prazo_dias_abaixo_limite    INT,
    prazo_dias_acima_limite     INT,

    criado_em                   TIMESTAMP        NOT NULL DEFAULT now()
);

COMMENT ON COLUMN transportadoras.ufs_bloqueadas IS 'UFs que esta transportadora NÃO atende';
COMMENT ON COLUMN transportadoras.tipos_carga_aceitos IS 'GERAL, FRAGIL, QUIMICA';