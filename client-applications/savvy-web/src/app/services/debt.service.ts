import { Injectable, inject } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { Debt } from '../models/dashboard';

@Injectable({
  providedIn: 'root',
})
export class DebtService {
  private http = inject(HttpClient);
  private apiUrl = '/api/debt';

  createDebt(debtData: Partial<Debt>): Observable<Debt> {
    return this.http.post<Debt>(this.apiUrl, debtData);
  }

  getAllDebts(): Observable<Debt[]> {
    return this.http.get<Debt[]>(`${this.apiUrl}/all`);
  }

  getDebtById(id: number): Observable<Debt> {
    return this.http.get<Debt>(`${this.apiUrl}/${id}`);
  }

  updateDebt(id: number, debtData: Partial<Debt>): Observable<Debt> {
    return this.http.put<Debt>(`${this.apiUrl}/${id}`, debtData);
  }

  deleteDebt(id: number): Observable<void> {
    return this.http.delete<void>(`${this.apiUrl}/${id}`);
  }
}
