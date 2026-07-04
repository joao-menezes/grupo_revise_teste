package com.desafiogruporevise.frete_teste.client;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

@Component
public class BrasilApiCepClient {

    private final RestClient restClient;

    @Value("${brasilapi.cep-url}")
    private String cepUrl;

    public BrasilApiCepClient(RestClient.Builder restClientBuilder) {
        this.restClient = restClientBuilder.build();
    }

    @Cacheable(value = "cep", key = "#cep")
    public BrasilApiCepResponse buscarPorCep(String cep) {
        return restClient.get()
                .uri(cepUrl, cep)
                .retrieve()
                .body(BrasilApiCepResponse.class);
    }
}