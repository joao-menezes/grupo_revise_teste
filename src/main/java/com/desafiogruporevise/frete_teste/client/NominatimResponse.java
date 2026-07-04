package com.desafiogruporevise.frete_teste.client;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public record NominatimResponse(
        String lat,
        String lon
) {
}