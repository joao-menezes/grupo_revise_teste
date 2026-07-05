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
import static org.mockito.Mockito.when;
import static org.mockito.Mockito.lenient;

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

    private static final BrasilApiCepResponse ORIGEM_SP =
        new BrasilApiCepResponse("01310930", "SP", "São Paulo", "Bela Vista", "Av. Paulista");

    private static final BrasilApiCepResponse DESTINO_SP =
        new BrasilApiCepResponse("01310100", "SP", "São Paulo", "Bela Vista", "Av. Paulista");
    
    private static final BrasilApiCepResponse DESTINO_MG =
        new BrasilApiCepResponse("30130010", "MG", "Belo Horizonte", "Centro", "Rua Tal");

    @BeforeEach
    void setUp() {
        lenient().when(cepClient.buscarPorCep("01310930")).thenReturn(ORIGEM_SP);
        lenient().when(cepClient.buscarPorCep("01310100")).thenReturn(DESTINO_SP);
        NominatimResponse coordenada = new NominatimResponse("-23.5505", "-46.6333");
        lenient().when(geocodingClient.geocodificar("São Paulo", "SP")).thenReturn(coordenada);
    }

    private SimulacaoFreteRequest request(BigDecimal valorPedido, BigDecimal peso, TipoCarga tipoCarga) {
        return new SimulacaoFreteRequest("01310930", "01310100", valorPedido, peso, tipoCarga);
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
    void tartarugaFretes_pesoNoLimiteExclusivo_deveRejeitar() {
        when(transportadoraRepository.findAll()).thenReturn(List.of(tartarugaFretes()));
        SimulacaoFreteRequest req = request(new BigDecimal("300"), new BigDecimal("10.00"), TipoCarga.GERAL);

        assertThrows(NenhumaTransportadoraDisponivelException.class, () -> service.simular(req));
    }

    @Test
    void tartarugaFretes_pesoAcimaDoLimiteExclusivo_deveAceitar() {
        when(transportadoraRepository.findAll()).thenReturn(List.of(tartarugaFretes()));
        SimulacaoFreteRequest req = request(new BigDecimal("300"), new BigDecimal("10.01"), TipoCarga.GERAL);

        SimulacaoFreteResponse resposta = service.simular(req);

        assertEquals("TartarugaFretes", resposta.freteMaisBarato().transportadora());
    }

    @Test
    void ecoTrans_nfeExatamenteNoLimite_deveAceitar() {
        when(transportadoraRepository.findAll()).thenReturn(List.of(ecoTrans()));
        SimulacaoFreteRequest req = request(new BigDecimal("2000.00"), new BigDecimal("5"), TipoCarga.GERAL);

        SimulacaoFreteResponse resposta = service.simular(req);

        assertEquals("EcoTrans", resposta.freteMaisBarato().transportadora());
    }

    @Test
    void ecoTrans_nfeAcimaDoLimite_deveRejeitar() {
        when(transportadoraRepository.findAll()).thenReturn(List.of(ecoTrans()));
        SimulacaoFreteRequest req = request(new BigDecimal("2000.01"), new BigDecimal("5"), TipoCarga.GERAL);

        assertThrows(NenhumaTransportadoraDisponivelException.class, () -> service.simular(req));
    }

    @Test
    void safeCargo_nfeAbaixoDoMinimo_deveRejeitar() {
        when(transportadoraRepository.findAll()).thenReturn(List.of(safeCargo()));
        SimulacaoFreteRequest req = request(new BigDecimal("999.99"), new BigDecimal("10"), TipoCarga.FRAGIL);

        assertThrows(NenhumaTransportadoraDisponivelException.class, () -> service.simular(req));
    }

    @Test
    void safeCargo_nfeExatamenteNoMinimo_deveAceitar() {
        when(transportadoraRepository.findAll()).thenReturn(List.of(safeCargo()));
        SimulacaoFreteRequest req = request(new BigDecimal("1000.00"), new BigDecimal("10"), TipoCarga.FRAGIL);

        SimulacaoFreteResponse resposta = service.simular(req);

        assertEquals("SafeCargo", resposta.freteMaisBarato().transportadora());
    }

    @Test
    void quimiTrans_cargaGeral_deveRejeitar() {
        when(transportadoraRepository.findAll()).thenReturn(List.of(quimiTrans()));
        SimulacaoFreteRequest req = request(new BigDecimal("300"), new BigDecimal("100"), TipoCarga.GERAL);

        assertThrows(NenhumaTransportadoraDisponivelException.class, () -> service.simular(req));
    }

    @Test
    void quimiTrans_cargaQuimica_deveAceitar() {
        when(cepClient.buscarPorCep("30130010")).thenReturn(DESTINO_MG);
        when(geocodingClient.geocodificar("Belo Horizonte", "MG"))
                .thenReturn(new NominatimResponse("-19.9167", "-43.9345"));

        when(transportadoraRepository.findAll()).thenReturn(List.of(quimiTrans()));
        SimulacaoFreteRequest req = new SimulacaoFreteRequest(
                "01310930", "30130010", new BigDecimal("300"), new BigDecimal("100"), TipoCarga.QUIMICA);

        SimulacaoFreteResponse resposta = service.simular(req);

        assertEquals("QuimiTrans", resposta.freteMaisBarato().transportadora());
    }

    @Test
    void nenhumaTransportadoraElegivel_deveLancarExcecao() {
        when(transportadoraRepository.findAll()).thenReturn(List.of());
        SimulacaoFreteRequest req = request(new BigDecimal("300"), new BigDecimal("5"), TipoCarga.GERAL);

        assertThrows(NenhumaTransportadoraDisponivelException.class, () -> service.simular(req));
    }

    @Test
    void escolheCorretamenteMaisBaratoEMaisRapido() {
        when(transportadoraRepository.findAll()).thenReturn(List.of(ecoTrans(), safeCargo()));
        SimulacaoFreteRequest req = request(new BigDecimal("1500"), new BigDecimal("10"), TipoCarga.GERAL);

        SimulacaoFreteResponse resposta = service.simular(req);

        assertEquals("EcoTrans", resposta.freteMaisBarato().transportadora());
    }

    @Test
    void cepComHifen_deveSerNormalizado() {
        SimulacaoFreteRequest req = new SimulacaoFreteRequest(
                "20040-020", "01310-930", new BigDecimal("500"), new BigDecimal("5.5"), TipoCarga.GERAL);

        assertEquals("20040020", req.cepOrigem());
        assertEquals("01310930", req.cepDestino());
    }
}