package com.desafiogruporevise.simulador_frete.dto;

public record SimulacaoFreteResponse(
        LocalizacaoDTO origem,
        LocalizacaoDTO destino,
        OpcaoFreteDTO freteMaisBarato,
        OpcaoFreteDTO freteMaisRapido
) {
}