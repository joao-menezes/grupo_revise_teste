package com.desafiogruporevise.frete_teste.client;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public record BrasilApiCepResponse(
        String cep,
        String state,
        String city,
        String neighborhood,
        String street,
        Location location
) {
    @JsonIgnoreProperties(ignoreUnknown = true)
    public record Location(Coordinates coordinates) {
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    public record Coordinates(String longitude, String latitude) {
    }
}