package com.desafiogruporevise.simulador_frete.client;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public record BrasilApiCepResponse(
        String cep,
        String state,
        String city,
        String neighborhood,
        String street
) {
}