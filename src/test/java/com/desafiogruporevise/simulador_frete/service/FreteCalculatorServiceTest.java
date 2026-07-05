package com.desafiogruporevise.simulador_frete.service;

import com.desafiogruporevise.simulador_frete.client.BrasilApiCepClient;
import com.desafiogruporevise.simulador_frete.client.BrasilApiCepResponse;
import com.desafiogruporevise.simulador_frete.client.NominatimGeocodingClient;
import com.desafiogruporevise.simulador_frete.client.NominatimResponse;
import com.desafiogruporevise.simulador_frete.dto.SimulacaoFreteRequest;
import com.desafiogruporevise.simulador_frete.dto.SimulacaoFreteResponse;
import com.desafiogruporevise.simulador_frete.enums.TipoCarga;
import com.desafiogruporevise.simulador_frete.exception.NenhumaTransportadoraDisponivelException;
import com.desafiogruporevise.simulador_frete.model.Transportadora;
import com.desafiogruporevise.simulador_frete.repository.TransportadoraRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.lenient;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class FreteCalculatorServiceTest {

    @Mock
    private BrasilApiCepClient cepClient;

    @Mock
    private NominatimGeocodingClient geocodingClient;

    @Mock
    private TransportadoraRepository transportadoraRepository;

    @InjectMocks
    private FreteCalculatorService service;

    private static final BrasilApiCepResponse ORIGIN_SP =
            new BrasilApiCepResponse("01310930", "SP", "São Paulo", "Bela Vista", "Av. Paulista");

    private static final BrasilApiCepResponse DESTINATION_SP =
            new BrasilApiCepResponse("01310100", "SP", "São Paulo", "Bela Vista", "Av. Paulista");

    private static final BrasilApiCepResponse DESTINATION_MG =
            new BrasilApiCepResponse("30130010", "MG", "Belo Horizonte", "Centro", "Rua Tal");

    @BeforeEach
    void setUp() {
        lenient().when(cepClient.buscarPorCep("01310930")).thenReturn(ORIGIN_SP);
        lenient().when(cepClient.buscarPorCep("01310100")).thenReturn(DESTINATION_SP);
        NominatimResponse coordinates = new NominatimResponse("-23.5505", "-46.6333");
        lenient().when(geocodingClient.geocodificar("São Paulo", "SP")).thenReturn(coordinates);
    }

    private SimulacaoFreteRequest buildRequest(BigDecimal orderValue, BigDecimal weight, TipoCarga cargoType) {
        return new SimulacaoFreteRequest("01310930", "01310100", orderValue, weight, cargoType);
    }

    private Transportadora tartarugaFretes() {
        Transportadora t = new Transportadora();
        t.setNome("TartarugaFretes");
        t.setPrecoBase(new BigDecimal("20.00"));
        t.setPrecoPorKm(new BigDecimal("0.20"));
        t.setPrecoPorKg(new BigDecimal("1.00"));
        t.setSeguroPercentual(BigDecimal.ZERO);
        t.setSeguroFixo(BigDecimal.ZERO);
        t.setPesoMin(new BigDecimal("10"));
        t.setPesoMax(new BigDecimal("200"));
        t.setPesoMinExclusivo(true);
        t.setPesoMaxExclusivo(true);
        t.setUfsBloqueadas(List.of("RJ", "ES"));
        t.setTiposCargaAceitos(List.of("GERAL", "QUIMICA"));
        t.setPrazoBaseDias(5);
        t.setPrazoKmPorDia(new BigDecimal("200"));
        return t;
    }

    private Transportadora ecoTrans() {
        Transportadora t = new Transportadora();
        t.setNome("EcoTrans");
        t.setPrecoBase(new BigDecimal("10.00"));
        t.setPrecoPorKm(new BigDecimal("0.10"));
        t.setPrecoPorKg(BigDecimal.ZERO);
        t.setSeguroPercentual(BigDecimal.ZERO);
        t.setSeguroFixo(BigDecimal.ZERO);
        t.setPesoMin(BigDecimal.ZERO);
        t.setPesoMax(new BigDecimal("15"));
        t.setPesoMinExclusivo(false);
        t.setPesoMaxExclusivo(false);
        t.setNfeMax(new BigDecimal("2000.00"));
        t.setUfsBloqueadas(List.of("DF", "GO", "MT", "MS"));
        t.setTiposCargaAceitos(List.of("GERAL"));
        t.setPrazoBaseDias(3);
        return t;
    }

    private Transportadora safeCargo() {
        Transportadora t = new Transportadora();
        t.setNome("SafeCargo");
        t.setPrecoBase(new BigDecimal("100.00"));
        t.setPrecoPorKm(new BigDecimal("0.80"));
        t.setPrecoPorKg(new BigDecimal("5.00"));
        t.setSeguroPercentual(new BigDecimal("0.0150"));
        t.setSeguroFixo(BigDecimal.ZERO);
        t.setPesoMin(BigDecimal.ZERO);
        t.setPesoMax(new BigDecimal("50"));
        t.setPesoMinExclusivo(false);
        t.setPesoMaxExclusivo(false);
        t.setNfeMin(new BigDecimal("1000.00"));
        t.setUfsBloqueadas(List.of("AL", "CE", "MA", "PB", "PE", "PI", "RN", "SE", "BA"));
        t.setTiposCargaAceitos(List.of("GERAL", "FRAGIL"));
        t.setPrazoBaseDias(2);
        t.setPrazoKmPorDia(new BigDecimal("400"));
        return t;
    }

    private Transportadora quimiTrans() {
        Transportadora t = new Transportadora();
        t.setNome("QuimiTrans");
        t.setPrecoBase(new BigDecimal("300.00"));
        t.setPrecoPorKm(new BigDecimal("1.20"));
        t.setPrecoPorKg(BigDecimal.ZERO);
        t.setSeguroPercentual(new BigDecimal("0.0050"));
        t.setSeguroFixo(BigDecimal.ZERO);
        t.setPesoMin(new BigDecimal("20"));
        t.setPesoMax(new BigDecimal("500"));
        t.setPesoMinExclusivo(false);
        t.setPesoMaxExclusivo(false);
        t.setUfsBloqueadas(List.of("RJ", "DF", "SP"));
        t.setTiposCargaAceitos(List.of("QUIMICA"));
        t.setPrazoBaseDias(4);
        t.setPrazoKmPorDia(new BigDecimal("300"));
        return t;
    }

    @Test
    void tartarugaFretes_weightAtExclusiveLimit_shouldReject() {
        when(transportadoraRepository.findAll()).thenReturn(List.of(tartarugaFretes()));
        SimulacaoFreteRequest req = buildRequest(new BigDecimal("300"), new BigDecimal("10.00"), TipoCarga.GERAL);

        assertThrows(NenhumaTransportadoraDisponivelException.class, () -> service.simular(req));
    }

    @Test
    void tartarugaFretes_weightAboveExclusiveLimit_shouldAccept() {
        when(transportadoraRepository.findAll()).thenReturn(List.of(tartarugaFretes()));
        SimulacaoFreteRequest req = buildRequest(new BigDecimal("300"), new BigDecimal("10.01"), TipoCarga.GERAL);

        SimulacaoFreteResponse response = service.simular(req);

        assertEquals("TartarugaFretes", response.freteMaisBarato().transportadora());
    }

    @Test
    void ecoTrans_invoiceValueExactlyAtLimit_shouldAccept() {
        when(transportadoraRepository.findAll()).thenReturn(List.of(ecoTrans()));
        SimulacaoFreteRequest req = buildRequest(new BigDecimal("2000.00"), new BigDecimal("5"), TipoCarga.GERAL);

        SimulacaoFreteResponse response = service.simular(req);

        assertEquals("EcoTrans", response.freteMaisBarato().transportadora());
    }

    @Test
    void ecoTrans_invoiceValueAboveLimit_shouldReject() {
        when(transportadoraRepository.findAll()).thenReturn(List.of(ecoTrans()));
        SimulacaoFreteRequest req = buildRequest(new BigDecimal("2000.01"), new BigDecimal("5"), TipoCarga.GERAL);

        assertThrows(NenhumaTransportadoraDisponivelException.class, () -> service.simular(req));
    }

    @Test
    void safeCargo_invoiceValueBelowMinimum_shouldReject() {
        when(transportadoraRepository.findAll()).thenReturn(List.of(safeCargo()));
        SimulacaoFreteRequest req = buildRequest(new BigDecimal("999.99"), new BigDecimal("10"), TipoCarga.FRAGIL);

        assertThrows(NenhumaTransportadoraDisponivelException.class, () -> service.simular(req));
    }

    @Test
    void safeCargo_invoiceValueExactlyAtMinimum_shouldAccept() {
        when(transportadoraRepository.findAll()).thenReturn(List.of(safeCargo()));
        SimulacaoFreteRequest req = buildRequest(new BigDecimal("1000.00"), new BigDecimal("10"), TipoCarga.FRAGIL);

        SimulacaoFreteResponse response = service.simular(req);

        assertEquals("SafeCargo", response.freteMaisBarato().transportadora());
    }

    @Test
    void quimiTrans_generalCargo_shouldReject() {
        when(transportadoraRepository.findAll()).thenReturn(List.of(quimiTrans()));
        SimulacaoFreteRequest req = buildRequest(new BigDecimal("300"), new BigDecimal("100"), TipoCarga.GERAL);

        assertThrows(NenhumaTransportadoraDisponivelException.class, () -> service.simular(req));
    }

    @Test
    void quimiTrans_chemicalCargo_shouldAccept() {
        when(cepClient.buscarPorCep("30130010")).thenReturn(DESTINATION_MG);
        when(geocodingClient.geocodificar("Belo Horizonte", "MG"))
                .thenReturn(new NominatimResponse("-19.9167", "-43.9345"));

        when(transportadoraRepository.findAll()).thenReturn(List.of(quimiTrans()));
        SimulacaoFreteRequest req = new SimulacaoFreteRequest(
                "01310930", "30130010", new BigDecimal("300"), new BigDecimal("100"), TipoCarga.QUIMICA);

        SimulacaoFreteResponse response = service.simular(req);

        assertEquals("QuimiTrans", response.freteMaisBarato().transportadora());
    }

    @Test
    void noCarrierEligible_shouldThrowException() {
        when(transportadoraRepository.findAll()).thenReturn(List.of());
        SimulacaoFreteRequest req = buildRequest(new BigDecimal("300"), new BigDecimal("5"), TipoCarga.GERAL);

        assertThrows(NenhumaTransportadoraDisponivelException.class, () -> service.simular(req));
    }

    @Test
    void picksCheapestAndFastestCorrectly() {
        when(transportadoraRepository.findAll()).thenReturn(List.of(ecoTrans(), safeCargo()));
        SimulacaoFreteRequest req = buildRequest(new BigDecimal("1500"), new BigDecimal("10"), TipoCarga.GERAL);

        SimulacaoFreteResponse response = service.simular(req);

        assertEquals("EcoTrans", response.freteMaisBarato().transportadora());
    }

    @Test
    void zipCodeWithHyphen_shouldBeNormalized() {
        SimulacaoFreteRequest req = new SimulacaoFreteRequest(
                "20040-020", "01310-930", new BigDecimal("500"), new BigDecimal("5.5"), TipoCarga.GERAL);

        assertEquals("20040020", req.cepOrigem());
        assertEquals("01310930", req.cepDestino());
    }

    @Test
    void weightExceedsAllCarriersLimit_shouldReject() {
        when(transportadoraRepository.findAll()).thenReturn(
                List.of(tartarugaFretes(), ecoTrans(), safeCargo(), quimiTrans()));
        SimulacaoFreteRequest req = buildRequest(new BigDecimal("57800"), new BigDecimal("999.5"), TipoCarga.GERAL);

        assertThrows(NenhumaTransportadoraDisponivelException.class, () -> service.simular(req));
    }
}