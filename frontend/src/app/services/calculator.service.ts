import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';

export interface CalculationRequest {
  country: string;
  employmentType: string;
  nhr: boolean;
  knownField: string;
  amount: number;
}

export interface CalculationResponse {
  annualNet: number;
  annualGross: number;
  monthlyNet: number;
  monthlyGross: number;
  dailyNet: number;
  dailyGross: number;
  hourlyNet: number;
  hourlyGross: number;
  
  usdAnnualNet: number;
  usdAnnualGross: number;
  usdMonthlyNet: number;
  usdMonthlyGross: number;
  usdDailyNet: number;
  usdDailyGross: number;
  usdHourlyNet: number;
  usdHourlyGross: number;
}

@Injectable({
  providedIn: 'root'
})
export class CalculatorService {
  private apiUrl = 'http://localhost:8080/api/v1/calculator';

  constructor(private http: HttpClient) {}

  calculate(request: CalculationRequest): Observable<CalculationResponse> {
    return this.http.post<CalculationResponse>(`${this.apiUrl}/calculate`, request);
  }
}
