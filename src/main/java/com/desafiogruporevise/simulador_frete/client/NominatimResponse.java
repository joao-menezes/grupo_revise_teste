package com.desafiogruporevise.simulador_frete.client;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public record NominatimResponse(
        String lat,
        String lon
) {
}