package com.desafiogruporevise.frete_teste.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name = "transportadoras")
@Getter
@Setter
@NoArgsConstructor
public class Transportadora {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String nome;

    // --- Preço padrão ---
    @Column(name = "preco_base", nullable = false)
    private BigDecimal precoBase;

    @Column(name = "preco_por_km", nullable = false)
    private BigDecimal precoPorKm;

    @Column(name = "preco_por_kg", nullable = false)
    private BigDecimal precoPorKg;

    // --- Preço em faixa (RegionalTrans) ---
    @Column(name = "distancia_limite_km")
    private BigDecimal distanciaLimiteKm;

    @Column(name = "preco_base_acima")
    private BigDecimal precoBaseAcima;

    @Column(name = "preco_km_acima")
    private BigDecimal precoKmAcima;

    @Column(name = "preco_kg_acima")
    private BigDecimal precoKgAcima;

    // --- Seguro ---
    @Column(name = "seguro_percentual", nullable = false)
    private BigDecimal seguroPercentual;

    @Column(name = "seguro_fixo", nullable = false)
    private BigDecimal seguroFixo;

    // --- Restrição de peso ---
    @Column(name = "peso_min")
    private BigDecimal pesoMin;

    @Column(name = "peso_max")
    private BigDecimal pesoMax;

    @Column(name = "peso_min_exclusivo", nullable = false)
    private Boolean pesoMinExclusivo;

    @Column(name = "peso_max_exclusivo", nullable = false)
    private Boolean pesoMaxExclusivo;

    // --- Restrição de NF-e ---
    @Column(name = "nfe_min")
    private BigDecimal nfeMin;

    @Column(name = "nfe_max")
    private BigDecimal nfeMax;

    // --- Restrições de UF e tipo de carga (arrays nativos do Postgres) ---
    @JdbcTypeCode(SqlTypes.ARRAY)
    @Column(name = "ufs_bloqueadas", nullable = false)
    private List<String> ufsBloqueadas;

    @JdbcTypeCode(SqlTypes.ARRAY)
    @Column(name = "tipos_carga_aceitos", nullable = false)
    private List<String> tiposCargaAceitos;

    // --- Prazo ---
    @Column(name = "prazo_base_dias", nullable = false)
    private Integer prazoBaseDias;

    @Column(name = "prazo_km_por_dia")
    private BigDecimal prazoKmPorDia;

    @Column(name = "prazo_limite_km")
    private BigDecimal prazoLimiteKm;

    @Column(name = "prazo_dias_abaixo_limite")
    private Integer prazoDiasAbaixoLimite;

    @Column(name = "prazo_dias_acima_limite")
    private Integer prazoDiasAcimaLimite;

    @Column(name = "criado_em", nullable = false, updatable = false)
    private LocalDateTime criadoEm;
}