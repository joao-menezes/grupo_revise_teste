package com.desafiogruporevise.frete_teste.exception;

public class CidadeNaoGeocodificadaException extends RuntimeException {
    public CidadeNaoGeocodificadaException(String cidade, String estado) {
        super("Não foi possível geocodificar a localização: " + cidade + "/" + estado);
    }
}