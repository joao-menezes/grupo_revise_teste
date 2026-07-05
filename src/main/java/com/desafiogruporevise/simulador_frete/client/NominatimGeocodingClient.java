package com.desafiogruporevise.simulador_frete.client;

import com.desafiogruporevise.simulador_frete.exception.CidadeNaoGeocodificadaException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

import java.util.List;

@Component
public class NominatimGeocodingClient {

    private static final Logger log = LoggerFactory.getLogger(NominatimGeocodingClient.class);

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
        log.info(">>> CHAMANDO API EXTERNA (Nominatim) para {}/{} <<<", cidade, estado);
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