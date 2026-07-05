package com.desafiogruporevise.simulador_frete.dto;

import java.math.BigDecimal;

public record OpcaoFreteDTO(
        String transportadora,
        BigDecimal valor,
        int prazoDias
) {
}