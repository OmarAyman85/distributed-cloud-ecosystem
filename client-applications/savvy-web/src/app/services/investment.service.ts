import { Injectable, inject } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { Investment } from '../models/dashboard';

@Injectable({
  providedIn: 'root',
})
export class InvestmentService {
  private http = inject(HttpClient);
  private apiUrl = '/api/investment';

  createInvestment(
    investmentData: Partial<Investment>,
  ): Observable<Investment> {
    return this.http.post<Investment>(this.apiUrl, investmentData);
  }

  getAllInvestments(): Observable<Investment[]> {
    return this.http.get<Investment[]>(`${this.apiUrl}/all`);
  }

  getInvestmentById(id: number): Observable<Investment> {
    return this.http.get<Investment>(`${this.apiUrl}/${id}`);
  }

  updateInvestment(
    id: number,
    investmentData: Partial<Investment>,
  ): Observable<Investment> {
    return this.http.put<Investment>(`${this.apiUrl}/${id}`, investmentData);
  }

  deleteInvestment(id: number): Observable<void> {
    return this.http.delete<void>(`${this.apiUrl}/${id}`);
  }
}
