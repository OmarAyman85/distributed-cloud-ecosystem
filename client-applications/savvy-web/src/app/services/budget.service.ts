import { Injectable, inject, signal } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';

export interface BudgetDTO {
  id?: number;
  category: string;
  budgetLimit: number;
  period: 'WEEKLY' | 'MONTHLY' | 'YEARLY';
  spent?: number;
  remaining?: number;
  percentage?: number;
}

@Injectable({
  providedIn: 'root',
})
export class BudgetService {
  private http = inject(HttpClient);
  private apiUrl = '/api/budgets';

  budgets = signal<BudgetDTO[]>([]);

  constructor() {
    this.fetchBudgets();
  }

  fetchBudgets() {
    this.http.get<BudgetDTO[]>(this.apiUrl).subscribe({
      next: (data) => this.budgets.set(data),
      error: (err) => console.error('Error fetching budgets', err),
    });
  }

  createBudget(budget: BudgetDTO): Observable<any> {
    return this.http.post(this.apiUrl, budget);
  }

  updateBudget(id: number, budget: BudgetDTO): Observable<any> {
    return this.http.put(`${this.apiUrl}/${id}`, budget);
  }

  deleteBudget(id: number): Observable<any> {
    return this.http.delete(`${this.apiUrl}/${id}`, { responseType: 'text' });
  }
}
