import { computed, Injectable, inject, signal } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Widget } from '../models/dashboard';
import { Observable } from 'rxjs';

@Injectable()
export class ExpenseService {
  private http = inject(HttpClient); // Use inject() instead of constructor
  private apiUrl = '/api/expense';

  widgets = signal<Widget[]>([]);
  addedWidgets = signal<Widget[]>([]);

  constructor() {
    this.fetchWidgets();
  }
  // ********************************************************************
  // ****************FETCHING ALL EXPENSES********************************
  // ********************************************************************
  fetchWidgets() {
    this.http.get<Widget[]>(`${this.apiUrl}/all`).subscribe({
      next: (data) => {
        this.widgets.set(data);
        this.addedWidgets.set(data);
        // this.addedWidgets.set(data.slice(0, 100));
      },
      error: (err) => console.error('Error fetching widgets', err),
    });
  }
  // ********************************************************************
  // ****************CREATION********************************************
  // ********************************************************************
  createExpense(expenseData: any): Observable<any> {
    return this.http.post<any>(this.apiUrl, expenseData);
  }

  addWidget(w: Widget) {
    this.addedWidgets.set([...this.addedWidgets(), { ...w }]);
  }
  // ********************************************************************
  // ****************UPDATING********************************************
  // ********************************************************************
  updateExpense(id: number, expenseData: Widget): Observable<Widget> {
    return this.http.put<Widget>(`${this.apiUrl}/${id}`, expenseData);
  }

  updateWidget(id: number, widget: Partial<Widget>) {
    const index = this.addedWidgets().findIndex((w) => w.id === id);
    if (index !== -1) {
      const newWidgets = [...this.addedWidgets()];
      newWidgets[index] = { ...newWidgets[index], ...widget };
      this.addedWidgets.set(newWidgets);
    }
  }
  // ********************************************************************
  // ****************DELETION********************************************
  // ********************************************************************
  deleteExpense(id: number): Observable<void> {
    return this.http.delete<void>(`${this.apiUrl}/${id}`);
  }

  removeWidget(id: number) {
    this.addedWidgets.set(
      this.addedWidgets().filter((widget) => widget.id !== id),
    );
  }
  // ********************************************************************
  // ****************MANIPULATION****************************************
  // ********************************************************************
  moveWidgetToRight(id: number) {
    const index = this.addedWidgets().findIndex((w) => w.id === id);
    if (index == this.addedWidgets().length - 1) {
      return;
    }

    const newWidgets = [...this.addedWidgets()];
    [newWidgets[index], newWidgets[index + 1]] = [
      { ...newWidgets[index + 1] },
      { ...newWidgets[index] },
    ];
    this.addedWidgets.set(newWidgets);
  }

  moveWidgetToLeft(id: number) {
    const index = this.addedWidgets().findIndex((w) => w.id === id);
    if (index == 0) {
      return;
    }

    const newWidgets = [...this.addedWidgets()];
    [newWidgets[index], newWidgets[index - 1]] = [
      { ...newWidgets[index - 1] },
      { ...newWidgets[index] },
    ];
    this.addedWidgets.set(newWidgets);
  }
  // ********************************************************************
}
