import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { CalculatorService, CalculationRequest, CalculationResponse } from './services/calculator.service';
import { Subject, debounceTime, distinctUntilChanged } from 'rxjs';

@Component({
  selector: 'app-root',
  standalone: true,
  imports: [CommonModule, FormsModule],
  templateUrl: './app.component.html',
  styleUrls: ['./app.component.scss']
})
export class AppComponent implements OnInit {
  title = 'Salary Calculator';

  country: 'UKRAINE' | 'PORTUGAL' = 'UKRAINE';
  uiEmploymentMode: 'EMPLOYMENT' | 'B2B' | 'EMPLOYMENT_NHR' = 'EMPLOYMENT';
  
  currencySymbol: string = '₴';

  // State for all fields
  data: CalculationResponse = {
    annualNet: 0, annualGross: 0,
    monthlyNet: 0, monthlyGross: 0,
    dailyNet: 0, dailyGross: 0,
    hourlyNet: 0, hourlyGross: 0,
    usdAnnualNet: 0, usdAnnualGross: 0,
    usdMonthlyNet: 0, usdMonthlyGross: 0,
    usdDailyNet: 0, usdDailyGross: 0,
    usdHourlyNet: 0, usdHourlyGross: 0
  };

  private inputSubject = new Subject<{field: string, value: number}>();
  private lastKnownField: string = 'MONTHLY_NET';

  constructor(private calculatorService: CalculatorService) {}

  ngOnInit() {
    this.inputSubject.pipe(
      debounceTime(500),
      distinctUntilChanged((prev, curr) => prev.field === curr.field && prev.value === curr.value)
    ).subscribe(({field, value}) => {
      this.doCalculate(field, value);
    });

    this.updateCurrency();
    // initial state (e.g. 50000 UAH monthly net)
    this.onInputChange('MONTHLY_NET', 50000);
  }

  onInputChange(field: string, value: number) {
    if (value === null || value === undefined) return;
    this.lastKnownField = field;
    this.inputSubject.next({field, value});
  }

  onCountryChange() {
    this.updateCurrency();
    // Reset or adjust uiEmploymentMode if needed when switching countries
    if (this.country === 'UKRAINE' && this.uiEmploymentMode === 'EMPLOYMENT_NHR') {
      this.uiEmploymentMode = 'EMPLOYMENT';
    }
    this.recalculate();
  }

  onEmploymentChange() {
    this.recalculate();
  }

  private updateCurrency() {
    this.currencySymbol = '€';
  }

  private recalculate() {
    // Determine a fallback value from the last known field to trigger recalculation
    let amount = 0;
    switch(this.lastKnownField) {
      case 'ANNUAL_NET': amount = this.data.annualNet; break;
      case 'ANNUAL_GROSS': amount = this.data.annualGross; break;
      case 'MONTHLY_NET': amount = this.data.monthlyNet; break;
      case 'MONTHLY_GROSS': amount = this.data.monthlyGross; break;
      case 'DAILY_NET': amount = this.data.dailyNet; break;
      case 'DAILY_GROSS': amount = this.data.dailyGross; break;
      case 'HOURLY_NET': amount = this.data.hourlyNet; break;
      case 'HOURLY_GROSS': amount = this.data.hourlyGross; break;
      default: amount = this.data.monthlyNet; this.lastKnownField = 'MONTHLY_NET';
    }
    this.doCalculate(this.lastKnownField, amount);
  }

  private doCalculate(field: string, amount: number) {
    const isNhr = this.uiEmploymentMode === 'EMPLOYMENT_NHR';
    const backendEmpType = this.uiEmploymentMode === 'EMPLOYMENT_NHR' ? 'EMPLOYMENT' : this.uiEmploymentMode;

    const req: CalculationRequest = {
      country: this.country,
      employmentType: backendEmpType,
      nhr: isNhr,
      knownField: field,
      amount: amount
    };

    this.calculatorService.calculate(req).subscribe({
      next: (res) => {
        // Prevent overwriting the currently typing field to avoid cursor jumping/glitching
        const currentField = this.lastKnownField;
        
        if (currentField !== 'ANNUAL_NET') this.data.annualNet = res.annualNet;
        if (currentField !== 'ANNUAL_GROSS') this.data.annualGross = res.annualGross;
        if (currentField !== 'MONTHLY_NET') this.data.monthlyNet = res.monthlyNet;
        if (currentField !== 'MONTHLY_GROSS') this.data.monthlyGross = res.monthlyGross;
        if (currentField !== 'DAILY_NET') this.data.dailyNet = res.dailyNet;
        if (currentField !== 'DAILY_GROSS') this.data.dailyGross = res.dailyGross;
        if (currentField !== 'HOURLY_NET') this.data.hourlyNet = res.hourlyNet;
        if (currentField !== 'HOURLY_GROSS') this.data.hourlyGross = res.hourlyGross;

        this.data.usdAnnualNet = res.usdAnnualNet;
        this.data.usdAnnualGross = res.usdAnnualGross;
        this.data.usdMonthlyNet = res.usdMonthlyNet;
        this.data.usdMonthlyGross = res.usdMonthlyGross;
        this.data.usdDailyNet = res.usdDailyNet;
        this.data.usdDailyGross = res.usdDailyGross;
        this.data.usdHourlyNet = res.usdHourlyNet;
        this.data.usdHourlyGross = res.usdHourlyGross;
      },
      error: (err) => console.error(err)
    });
  }
}
