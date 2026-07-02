package com.salarycalc.backend.model;

import lombok.Data;

@Data
public class CalculationRequest {
    private String country; // "UKRAINE" or "PORTUGAL"
    private String employmentType; // "EMPLOYMENT" or "B2B"
    private boolean nhr; // true or false
    private String knownField; // e.g. "ANNUAL_NET", "MONTHLY_GROSS"
    private double amount;
}
