package com.desafiogruporevise.frete_teste.dto;

import java.math.BigDecimal;

public record OpcaoFreteDTO(
        String transportadora,
        BigDecimal valor,
        int prazoDias
) {
}