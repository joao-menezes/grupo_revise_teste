package com.desafiogruporevise.simulador_frete.util;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertTrue;

class GeoUtilsTest {

    @Test
    void distanciaSaoPauloRioDeJaneiro_deveSerAproximadamente360Km() {
        double distancia = GeoUtils.calcularDistanciaKm(
                -23.5505, -46.6333,
                -22.9068, -43.1729
        );

        assertTrue(distancia > 350 && distancia < 370,
                "Distância calculada foi " + distancia + "km, esperado entre 350 e 370km");
    }

    @Test
    void distanciaEntrePontosIguais_deveSerZero() {
        double distancia = GeoUtils.calcularDistanciaKm(-23.5505, -46.6333, -23.5505, -46.6333);

        assertTrue(distancia < 0.001);
    }
}