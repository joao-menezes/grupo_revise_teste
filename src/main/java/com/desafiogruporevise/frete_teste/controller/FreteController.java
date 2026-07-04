package com.desafiogruporevise.frete_teste.controller;

import com.desafiogruporevise.frete_teste.dto.SimulacaoFreteRequest;
import com.desafiogruporevise.frete_teste.dto.SimulacaoFreteResponse;
import com.desafiogruporevise.frete_teste.service.FreteCalculatorService;
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