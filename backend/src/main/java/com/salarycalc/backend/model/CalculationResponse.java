package com.salarycalc.backend.model;

import lombok.Data;
import lombok.Builder;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CalculationResponse {
    // Primary currency (UAH or EUR)
    private double annualNet;
    private double annualGross;
    private double monthlyNet;
    private double monthlyGross;
    private double dailyNet;
    private double dailyGross;
    private double hourlyNet;
    private double hourlyGross;

    // USD equivalents
    private double usdAnnualNet;
    private double usdAnnualGross;
    private double usdMonthlyNet;
    private double usdMonthlyGross;
    private double usdDailyNet;
    private double usdDailyGross;
    private double usdHourlyNet;
    private double usdHourlyGross;
}
