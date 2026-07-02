package com.salarycalc.backend.service;

import com.salarycalc.backend.model.CalculationRequest;
import com.salarycalc.backend.model.CalculationResponse;
import org.springframework.stereotype.Service;

@Service
public class TaxCalculatorService {

    private static final double USD_TO_UAH = 40.5;
    private static final double EUR_TO_USD = 1.08;
    private static final double EUR_TO_UAH = USD_TO_UAH * EUR_TO_USD;

    private static final int MONTHS = 12;
    private static final int DAYS = 252;
    private static final int HOURS = 2016;

    public CalculationResponse calculate(CalculationRequest request) {
        double annualGross = findAnnualGross(request);
        
        double annualNet = calculateAnnualNet(annualGross, request.getCountry(), request.getEmploymentType(), request.isNhr());
        // Since base currency for both countries is now EUR, USD conversion is always EUR_TO_USD
        double usdRate = EUR_TO_USD;

        return CalculationResponse.builder()
                .annualGross(round(annualGross))
                .annualNet(round(annualNet))
                .monthlyGross(round(annualGross / MONTHS))
                .monthlyNet(round(annualNet / MONTHS))
                .dailyGross(round(annualGross / DAYS))
                .dailyNet(round(annualNet / DAYS))
                .hourlyGross(round(annualGross / HOURS))
                .hourlyNet(round(annualNet / HOURS))
                .usdAnnualGross(round(annualGross * usdRate))
                .usdAnnualNet(round(annualNet * usdRate))
                .usdMonthlyGross(round(annualGross / MONTHS * usdRate))
                .usdMonthlyNet(round(annualNet / MONTHS * usdRate))
                .usdDailyGross(round(annualGross / DAYS * usdRate))
                .usdDailyNet(round(annualNet / DAYS * usdRate))
                .usdHourlyGross(round(annualGross / HOURS * usdRate))
                .usdHourlyNet(round(annualNet / HOURS * usdRate))
                .build();
    }

    private double findAnnualGross(CalculationRequest request) {
        String field = request.getKnownField().toUpperCase();
        double amount = request.getAmount();

        if (field.contains("GROSS")) {
            if (field.contains("ANNUAL")) return amount;
            if (field.contains("MONTHLY")) return amount * MONTHS;
            if (field.contains("DAILY")) return amount * DAYS;
            if (field.contains("HOURLY")) return amount * HOURS;
        } else if (field.contains("NET")) {
            double targetNet;
            if (field.contains("ANNUAL")) targetNet = amount;
            else if (field.contains("MONTHLY")) targetNet = amount * MONTHS;
            else if (field.contains("DAILY")) targetNet = amount * DAYS;
            else if (field.contains("HOURLY")) targetNet = amount * HOURS;
            else targetNet = amount;

            return reverseCalculateGrossFromNet(targetNet, request.getCountry(), request.getEmploymentType(), request.isNhr());
        }
        return amount; // fallback
    }

    private double reverseCalculateGrossFromNet(double targetNet, String country, String employmentType, boolean nhr) {
        double low = 0.0;
        double high = targetNet * 5; // Safe upper bound
        double precision = 0.01;

        for (int i = 0; i < 100; i++) { // Binary search
            double mid = (low + high) / 2;
            double currentNet = calculateAnnualNet(mid, country, employmentType, nhr);
            if (Math.abs(currentNet - targetNet) < precision) {
                return mid;
            }
            if (currentNet < targetNet) {
                low = mid;
            } else {
                high = mid;
            }
        }
        return (low + high) / 2;
    }

    private double calculateAnnualNet(double annualGross, String country, String employmentType, boolean nhr) {
        if ("UKRAINE".equalsIgnoreCase(country)) {
            if ("B2B".equalsIgnoreCase(employmentType)) {
                // 5% tax + 1760 UAH/month (21120 UAH/year). We convert 21120 UAH to EUR.
                double esvInEur = 21120.0 / EUR_TO_UAH;
                return annualGross * 0.95 - esvInEur;
            } else {
                // 18% PIT + 1.5% Military = 19.5% tax -> 80.5% net
                return annualGross * 0.805;
            }
        } else if ("PORTUGAL".equalsIgnoreCase(country)) {
            if ("B2B".equalsIgnoreCase(employmentType)) {
                // Simplified regime: 75% is taxable
                double taxableIncome = annualGross * 0.75;
                // SS: ~15% of total gross roughly or 21.4% on 70%
                double ss = annualGross * 0.15;
                double irs;
                if (nhr) {
                    irs = taxableIncome * 0.20;
                } else {
                    irs = calculatePortugalProgressiveIRS(taxableIncome);
                }
                return annualGross - ss - irs;
            } else {
                // Employment
                double ss = annualGross * 0.11;
                double irs;
                if (nhr) {
                    irs = annualGross * 0.20;
                } else {
                    double taxableIncome = Math.max(0, annualGross - 4104); // standard deduction
                    irs = calculatePortugalProgressiveIRS(taxableIncome);
                }
                return annualGross - ss - irs;
            }
        }
        return annualGross; // Unknown
    }

    private double calculatePortugalProgressiveIRS(double taxableIncome) {
        if (taxableIncome <= 7703) return taxableIncome * 0.1325;
        if (taxableIncome <= 11623) return 7703 * 0.1325 + (taxableIncome - 7703) * 0.18;
        if (taxableIncome <= 16472) return 7703 * 0.1325 + 3920 * 0.18 + (taxableIncome - 11623) * 0.23;
        if (taxableIncome <= 21321) return 7703 * 0.1325 + 3920 * 0.18 + 4849 * 0.23 + (taxableIncome - 16472) * 0.26;
        if (taxableIncome <= 27146) return 7703 * 0.1325 + 3920 * 0.18 + 4849 * 0.23 + 4849 * 0.26 + (taxableIncome - 21321) * 0.3275;
        if (taxableIncome <= 39791) return 7703 * 0.1325 + 3920 * 0.18 + 4849 * 0.23 + 4849 * 0.26 + 5825 * 0.3275 + (taxableIncome - 27146) * 0.37;
        if (taxableIncome <= 51997) return 7703 * 0.1325 + 3920 * 0.18 + 4849 * 0.23 + 4849 * 0.26 + 5825 * 0.3275 + 12645 * 0.37 + (taxableIncome - 39791) * 0.435;
        if (taxableIncome <= 81199) return 7703 * 0.1325 + 3920 * 0.18 + 4849 * 0.23 + 4849 * 0.26 + 5825 * 0.3275 + 12645 * 0.37 + 12206 * 0.435 + (taxableIncome - 51997) * 0.45;
        
        return 7703 * 0.1325 + 3920 * 0.18 + 4849 * 0.23 + 4849 * 0.26 + 5825 * 0.3275 + 12645 * 0.37 + 12206 * 0.435 + 29202 * 0.45 + (taxableIncome - 81199) * 0.48;
    }

    private double round(double value) {
        return Math.round(value * 100.0) / 100.0;
    }
}
