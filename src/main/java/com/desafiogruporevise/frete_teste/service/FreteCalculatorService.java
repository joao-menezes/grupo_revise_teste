package com.desafiogruporevise.frete_teste.service;

import com.desafiogruporevise.frete_teste.client.BrasilApiCepClient;
import com.desafiogruporevise.frete_teste.client.BrasilApiCepResponse;
import com.desafiogruporevise.frete_teste.dto.LocalizacaoDTO;
import com.desafiogruporevise.frete_teste.dto.OpcaoFreteDTO;
import com.desafiogruporevise.frete_teste.dto.SimulacaoFreteRequest;
import com.desafiogruporevise.frete_teste.dto.SimulacaoFreteResponse;
import com.desafiogruporevise.frete_teste.exception.CepSemCoordenadaException;
import com.desafiogruporevise.frete_teste.exception.NenhumaTransportadoraDisponivelException;
import com.desafiogruporevise.frete_teste.model.Transportadora;
import com.desafiogruporevise.frete_teste.repository.TransportadoraRepository;
import com.desafiogruporevise.frete_teste.util.GeoUtils;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Comparator;
import java.util.List;

@Service
public class FreteCalculatorService {

    private final BrasilApiCepClient cepClient;
    private final TransportadoraRepository transportadoraRepository;

    public FreteCalculatorService(BrasilApiCepClient cepClient,
                                   TransportadoraRepository transportadoraRepository) {
        this.cepClient = cepClient;
        this.transportadoraRepository = transportadoraRepository;
    }

    public SimulacaoFreteResponse simular(SimulacaoFreteRequest request) {
        BrasilApiCepResponse origem = cepClient.buscarPorCep(request.cepOrigem());
        BrasilApiCepResponse destino = cepClient.buscarPorCep(request.cepDestino());

        double distanciaKm = calcularDistancia(origem, destino);

        List<OpcaoCalculada> opcoesElegiveis = transportadoraRepository.findAll().stream()
                .filter(t -> elegivel(t, request, destino))
                .map(t -> calcular(t, request, distanciaKm))
                .toList();

        if (opcoesElegiveis.isEmpty()) {
            throw new NenhumaTransportadoraDisponivelException();
        }

        OpcaoCalculada maisBarato = opcoesElegiveis.stream()
                .min(Comparator.comparing(OpcaoCalculada::valor))
                .orElseThrow();

        OpcaoCalculada maisRapido = opcoesElegiveis.stream()
                .min(Comparator.comparingInt(OpcaoCalculada::prazoDias))
                .orElseThrow();

        return new SimulacaoFreteResponse(
                new LocalizacaoDTO(origem.city(), origem.state()),
                new LocalizacaoDTO(destino.city(), destino.state()),
                toDTO(maisBarato),
                toDTO(maisRapido)
        );
    }

    private double calcularDistancia(BrasilApiCepResponse origem, BrasilApiCepResponse destino) {
        double[] coordOrigem = extrairCoordenadas(origem);
        double[] coordDestino = extrairCoordenadas(destino);
        return GeoUtils.calcularDistanciaKm(
                coordOrigem[0], coordOrigem[1],
                coordDestino[0], coordDestino[1]);
    }

    private double[] extrairCoordenadas(BrasilApiCepResponse resposta) {
        if (resposta.location() == null || resposta.location().coordinates() == null) {
            throw new CepSemCoordenadaException(resposta.cep());
        }
        String lat = resposta.location().coordinates().latitude();
        String lon = resposta.location().coordinates().longitude();
        if (lat == null || lat.isBlank() || lon == null || lon.isBlank()) {
            throw new CepSemCoordenadaException(resposta.cep());
        }
        try {
            return new double[]{Double.parseDouble(lat), Double.parseDouble(lon)};
        } catch (NumberFormatException e) {
            throw new CepSemCoordenadaException(resposta.cep());
        }
    }

    private boolean elegivel(Transportadora t, SimulacaoFreteRequest request, BrasilApiCepResponse destino) {
        if (!pesoValido(t, request.peso())) {
            return false;
        }
        if (!t.getTiposCargaAceitos().contains(request.tipoCarga().name())) {
            return false;
        }
        if (t.getUfsBloqueadas().contains(destino.state())) {
            return false;
        }
        if (t.getNfeMin() != null && request.valorPedido().compareTo(t.getNfeMin()) < 0) {
            return false;
        }
        if (t.getNfeMax() != null && request.valorPedido().compareTo(t.getNfeMax()) > 0) {
            return false;
        }
        return true;
    }

    private boolean pesoValido(Transportadora t, BigDecimal peso) {
        if (t.getPesoMin() != null) {
            int cmp = peso.compareTo(t.getPesoMin());
            boolean invalido = Boolean.TRUE.equals(t.getPesoMinExclusivo()) ? cmp <= 0 : cmp < 0;
            if (invalido) return false;
        }
        if (t.getPesoMax() != null) {
            int cmp = peso.compareTo(t.getPesoMax());
            boolean invalido = Boolean.TRUE.equals(t.getPesoMaxExclusivo()) ? cmp >= 0 : cmp > 0;
            if (invalido) return false;
        }
        return true;
    }

    private OpcaoCalculada calcular(Transportadora t, SimulacaoFreteRequest request, double distanciaKm) {
        BigDecimal valor = calcularValor(t, request, distanciaKm);
        int prazo = calcularPrazo(t, distanciaKm);
        return new OpcaoCalculada(t.getNome(), valor, prazo);
    }

    private BigDecimal calcularValor(Transportadora t, SimulacaoFreteRequest request, double distanciaKm) {
        BigDecimal distancia = BigDecimal.valueOf(distanciaKm);
        BigDecimal peso = request.peso();

        boolean usaFaixaSuperior = t.getDistanciaLimiteKm() != null
                && distancia.compareTo(t.getDistanciaLimiteKm()) >= 0;

        BigDecimal precoBase = usaFaixaSuperior ? t.getPrecoBaseAcima() : t.getPrecoBase();
        BigDecimal precoPorKm = usaFaixaSuperior ? t.getPrecoKmAcima() : t.getPrecoPorKm();
        BigDecimal precoPorKg = usaFaixaSuperior ? t.getPrecoKgAcima() : t.getPrecoPorKg();

        BigDecimal custoFrete = precoBase
                .add(precoPorKm.multiply(distancia))
                .add(precoPorKg.multiply(peso));

        BigDecimal seguro = t.getSeguroFixo()
                .add(t.getSeguroPercentual().multiply(request.valorPedido()));

        return custoFrete.add(seguro).setScale(2, RoundingMode.HALF_UP);
    }

    private int calcularPrazo(Transportadora t, double distanciaKm) {
        if (t.getPrazoLimiteKm() != null) {
            boolean abaixo = distanciaKm < t.getPrazoLimiteKm().doubleValue();
            return abaixo ? t.getPrazoDiasAbaixoLimite() : t.getPrazoDiasAcimaLimite();
        }
        if (t.getPrazoKmPorDia() != null) {
            int diasAdicionais = (int) Math.ceil(distanciaKm / t.getPrazoKmPorDia().doubleValue());
            return t.getPrazoBaseDias() + diasAdicionais;
        }
        return t.getPrazoBaseDias();
    }

    private OpcaoFreteDTO toDTO(OpcaoCalculada calculada) {
        return new OpcaoFreteDTO(calculada.transportadora(), calculada.valor(), calculada.prazoDias());
    }

    private record OpcaoCalculada(String transportadora, BigDecimal valor, int prazoDias) {
    }
}