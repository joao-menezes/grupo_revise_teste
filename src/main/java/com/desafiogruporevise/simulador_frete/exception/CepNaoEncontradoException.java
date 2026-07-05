package com.desafiogruporevise.simulador_frete.exception;

public class CepNaoEncontradoException extends RuntimeException {
    public CepNaoEncontradoException(String cep) {
        super("CEP não encontrado: " + cep);
    }
}