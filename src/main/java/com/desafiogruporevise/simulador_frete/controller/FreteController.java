package com.desafiogruporevise.simulador_frete.controller;

import com.desafiogruporevise.simulador_frete.dto.SimulacaoFreteRequest;
import com.desafiogruporevise.simulador_frete.dto.SimulacaoFreteResponse;
import com.desafiogruporevise.simulador_frete.service.FreteCalculatorService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/frete")
public class FreteController {

    private final FreteCalculatorService freteCalculatorService;

    public FreteController(FreteCalculatorService freteCalculatorService) {
        this.freteCalculatorService = freteCalculatorService;
    }

    @PostMapping("/simular")
    public ResponseEntity<SimulacaoFreteResponse> simular(@Valid @RequestBody SimulacaoFreteRequest request) {
        return ResponseEntity.ok(freteCalculatorService.simular(request));
    }
}