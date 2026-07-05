package com.desafiogruporevise.simulador_frete.exception;

public class NenhumaTransportadoraDisponivelException extends RuntimeException {
    public NenhumaTransportadoraDisponivelException() {
        super("Nenhuma transportadora atende às regras de negócio para esse pedido");
    }
}