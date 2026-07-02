package com.salarycalc.backend.controller;

import com.salarycalc.backend.model.CalculationRequest;
import com.salarycalc.backend.model.CalculationResponse;
import com.salarycalc.backend.service.TaxCalculatorService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/calculator")
@CrossOrigin(origins = "*") // Allow all for simplicity, restrict in prod if needed
public class CalculatorController {

    private final TaxCalculatorService calculatorService;

    public CalculatorController(TaxCalculatorService calculatorService) {
        this.calculatorService = calculatorService;
    }

    @PostMapping("/calculate")
    public ResponseEntity<CalculationResponse> calculate(@RequestBody CalculationRequest request) {
        return ResponseEntity.ok(calculatorService.calculate(request));
    }
}
