package com.desafiogruporevise.frete_teste.exception;

public class CepSemCoordenadaException extends RuntimeException {
    public CepSemCoordenadaException(String cep) {
        super("Não foi possível obter coordenadas geográficas para o CEP " + cep);
    }
}