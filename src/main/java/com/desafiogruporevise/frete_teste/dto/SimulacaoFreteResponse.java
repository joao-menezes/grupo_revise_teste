package com.desafiogruporevise.frete_teste.dto;

public record SimulacaoFreteResponse(
        LocalizacaoDTO origem,
        LocalizacaoDTO destino,
        OpcaoFreteDTO freteMaisBarato,
        OpcaoFreteDTO freteMaisRapido
) {
}