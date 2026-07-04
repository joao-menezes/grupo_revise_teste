package com.desafiogruporevise.frete_teste.exception;

public class NenhumaTransportadoraDisponivelException extends RuntimeException {
    public NenhumaTransportadoraDisponivelException() {
        super("Nenhuma transportadora atende às regras de negócio para esse pedido");
    }
}