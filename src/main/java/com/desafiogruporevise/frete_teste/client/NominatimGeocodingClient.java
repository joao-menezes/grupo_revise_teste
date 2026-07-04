package com.desafiogruporevise.frete_teste.client;

import com.desafiogruporevise.frete_teste.exception.CidadeNaoGeocodificadaException;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

import java.util.List;

@Component
public class NominatimGeocodingClient {

    private static final String URL = "https://nominatim.openstreetmap.org/search"
            + "?city={cidade}&state={estado}&country=Brazil&format=json&limit=1";

    private final RestClient restClient;

    public NominatimGeocodingClient(RestClient.Builder restClientBuilder) {
        this.restClient = restClientBuilder
                .defaultHeader(HttpHeaders.USER_AGENT, "frete-teste-desafio-tecnico/1.0")
                .build();
    }

    @Cacheable(value = "geo", key = "#cidade + '-' + #estado")
    public NominatimResponse geocodificar(String cidade, String estado) {
        List<NominatimResponse> resultados = restClient.get()
                .uri(URL, cidade, estado)
                .retrieve()
                .body(new ParameterizedTypeReference<List<NominatimResponse>>() {
                });

        if (resultados == null || resultados.isEmpty()) {
            throw new CidadeNaoGeocodificadaException(cidade, estado);
        }
        return resultados.get(0);
    }
}