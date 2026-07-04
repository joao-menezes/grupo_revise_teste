-- RápidaLog: R$50 + R$0,50/km + R$2/kg | seguro 0,1% NF-e | bloqueia Região Norte
-- peso 0-30kg | aceita Geral e Frágil, bloqueia Química | prazo: 1 dia a cada 500km (mín 1)
INSERT INTO transportadoras (
    nome, preco_base, preco_por_km, preco_por_kg,
    seguro_percentual, seguro_fixo,
    peso_min, peso_max, peso_min_exclusivo, peso_max_exclusivo,
    ufs_bloqueadas, tipos_carga_aceitos,
    prazo_base_dias, prazo_km_por_dia
) VALUES (
    'RápidaLog', 50.00, 0.50, 2.00,
    0.0010, 0,
    0, 30, FALSE, FALSE,
    ARRAY['AC','AM','AP','PA','RO','RR','TO'], ARRAY['GERAL','FRAGIL'],
    1, 500
);

-- TartarugaFretes: R$20 + R$0,20/km + R$1/kg | isento seguro | bloqueia RJ, ES
-- peso ESTRITAMENTE entre 10 e 200kg | aceita Geral e Química, bloqueia Frágil
-- prazo: 5 dias + 1 dia a cada 200km
INSERT INTO transportadoras (
    nome, preco_base, preco_por_km, preco_por_kg,
    seguro_percentual, seguro_fixo,
    peso_min, peso_max, peso_min_exclusivo, peso_max_exclusivo,
    ufs_bloqueadas, tipos_carga_aceitos,
    prazo_base_dias, prazo_km_por_dia
) VALUES (
    'TartarugaFretes', 20.00, 0.20, 1.00,
    0, 0,
    10, 200, TRUE, TRUE,
    ARRAY['RJ','ES'], ARRAY['GERAL','QUIMICA'],
    5, 200
);

-- RegionalTrans: preço em FAIXA por distância | seguro 0,2% NF-e
-- só atende Sul/Sudeste (bloqueia todo o resto) | peso até 100kg | aceita só Geral
-- prazo em faixa: <300km = 2 dias fixos, >=300km = 10 dias fixos
INSERT INTO transportadoras (
    nome, preco_base, preco_por_km, preco_por_kg,
    distancia_limite_km, preco_base_acima, preco_km_acima, preco_kg_acima,
    seguro_percentual, seguro_fixo,
    peso_min, peso_max, peso_min_exclusivo, peso_max_exclusivo,
    ufs_bloqueadas, tipos_carga_aceitos,
    prazo_base_dias, prazo_limite_km, prazo_dias_abaixo_limite, prazo_dias_acima_limite
) VALUES (
    'RegionalTrans', 15.00, 0, 1.50,
    300, 150.00, 0, 3.00,
    0.0020, 0,
    0, 100, FALSE, FALSE,
    ARRAY['AC','AM','AP','PA','RO','RR','TO','DF','GO','MT','MS','AL','CE','MA','PB','PE','PI','RN','SE','BA'],
    ARRAY['GERAL'],
    2, 300, 2, 10
);

-- EcoTrans: R$10 + R$0,10/km (sem custo por kg) | isento seguro, MAS bloqueia se NF-e > R$2.000
-- bloqueia Centro-Oeste | peso até 15kg | aceita só Geral | prazo fixo 3 dias
INSERT INTO transportadoras (
    nome, preco_base, preco_por_km, preco_por_kg,
    seguro_percentual, seguro_fixo,
    peso_min, peso_max, peso_min_exclusivo, peso_max_exclusivo,
    nfe_max,
    ufs_bloqueadas, tipos_carga_aceitos,
    prazo_base_dias
) VALUES (
    'EcoTrans', 10.00, 0.10, 0,
    0, 0,
    0, 15, FALSE, FALSE,
    2000.00,
    ARRAY['DF','GO','MT','MS'], ARRAY['GERAL'],
    3
);

-- SafeCargo: R$100 + R$0,80/km + R$5/kg | seguro 1,5% NF-e | EXIGE NF-e mínima de R$1.000
-- bloqueia Nordeste | peso até 50kg | aceita Geral e Frágil | prazo: 2 dias + 1 a cada 400km
INSERT INTO transportadoras (
    nome, preco_base, preco_por_km, preco_por_kg,
    seguro_percentual, seguro_fixo,
    peso_min, peso_max, peso_min_exclusivo, peso_max_exclusivo,
    nfe_min,
    ufs_bloqueadas, tipos_carga_aceitos,
    prazo_base_dias, prazo_km_por_dia
) VALUES (
    'SafeCargo', 100.00, 0.80, 5.00,
    0.0150, 0,
    0, 50, FALSE, FALSE,
    1000.00,
    ARRAY['AL','CE','MA','PB','PE','PI','RN','SE','BA'], ARRAY['GERAL','FRAGIL'],
    2, 400
);

-- QuimiTrans: R$300 fixo + R$1,20/km (sem custo por kg) | seguro 0,5% NF-e
-- bloqueia RJ, DF, SP | peso ESTRITAMENTE? não, "entre 20 e 500kg" (inclusive) | aceita SÓ Química
-- prazo: 4 dias + 1 a cada 300km
INSERT INTO transportadoras (
    nome, preco_base, preco_por_km, preco_por_kg,
    seguro_percentual, seguro_fixo,
    peso_min, peso_max, peso_min_exclusivo, peso_max_exclusivo,
    ufs_bloqueadas, tipos_carga_aceitos,
    prazo_base_dias, prazo_km_por_dia
) VALUES (
    'QuimiTrans', 300.00, 1.20, 0,
    0.0050, 0,
    20, 500, FALSE, FALSE,
    ARRAY['RJ','DF','SP'], ARRAY['QUIMICA'],
    4, 300
);

-- BrasilExpress: R$40 + R$0,60/km + R$2,50/kg | GRIS fixo R$15 + 0,15% NF-e
-- atende todo o Brasil (nenhuma UF bloqueada) | peso até 60kg | aceita todos os tipos
-- prazo fixo 4 dias pra qualquer rota
INSERT INTO transportadoras (
    nome, preco_base, preco_por_km, preco_por_kg,
    seguro_percentual, seguro_fixo,
    peso_min, peso_max, peso_min_exclusivo, peso_max_exclusivo,
    ufs_bloqueadas, tipos_carga_aceitos,
    prazo_base_dias
) VALUES (
    'BrasilExpress', 40.00, 0.60, 2.50,
    0.0015, 15.00,
    0, 60, FALSE, FALSE,
    ARRAY[]::TEXT[], ARRAY['GERAL','FRAGIL','QUIMICA'],
    4
);