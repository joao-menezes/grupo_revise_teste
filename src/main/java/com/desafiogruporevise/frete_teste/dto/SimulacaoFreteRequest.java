package com.desafiogruporevise.frete_teste.dto;

import com.desafiogruporevise.frete_teste.enums.TipoCarga;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Positive;

import java.math.BigDecimal;

public record SimulacaoFreteRequest(

        @NotNull(message = "CEP de origem é obrigatório")
        @Pattern(regexp = "\\d{8}", message = "CEP de origem deve conter 8 dígitos numéricos")
        String cepOrigem,

        @NotNull(message = "CEP de destino é obrigatório")
        @Pattern(regexp = "\\d{8}", message = "CEP de destino deve conter 8 dígitos numéricos")
        String cepDestino,

        @NotNull(message = "Valor do pedido é obrigatório")
        @Positive(message = "Valor do pedido deve ser positivo")
        BigDecimal valorPedido,

        @NotNull(message = "Peso é obrigatório")
        @Positive(message = "Peso deve ser positivo")
        BigDecimal peso,

        @NotNull(message = "Tipo de carga é obrigatório")
        TipoCarga tipoCarga

) {
}