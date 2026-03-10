import { computed, inject, Injectable, signal } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import {
  Widget,
  Transaction,
  StatsDTO,
  GraphDTO,
  Income,
  Expense,
} from '../models/dashboard';
import { map, tap } from 'rxjs/operators';
import { Observable } from 'rxjs';

@Injectable({
  providedIn: 'root',
})
export class DashboardService {
  private http = inject(HttpClient);
  private apiUrl = '/api/stats';

  constructor() {
    this.fetchStats();
  }

  // Initial widgets structure (could be fetched or static config)
  widgets = signal<Widget[]>([
    {
      id: 1,
      title: 'Dashboard',
      description: 'Overview',
      category: 'Dashboard',
      amount: 0,
      date: new Date(),
    },
    {
      id: 2,
      title: 'Income',
      description: 'Total Income',
      category: 'Income',
      amount: 0,
      date: new Date(),
    },
    {
      id: 3,
      title: 'Expense',
      description: 'Total Expense',
      category: 'Expense',
      amount: 0,
      date: new Date(),
    },
  ]);

  addedWidgets = signal<Widget[]>([
    {
      id: 1,
      title: 'Total Balance',
      description: 'Available funds',
      category: 'Dashboard',
      amount: 0,
      date: new Date(),
      backgroundColor: '#000000',
      color: '#ffffff',
      rows: 1,
      columns: 2,
      trend: 'neutral',
      trendValue: '0%',
    },
    {
      id: 2,
      title: 'Income',
      description: 'Total earnings',
      category: 'Income',
      amount: 0,
      date: new Date(),
      rows: 1,
      columns: 1,
      trend: 'up',
      trendValue: '+0%',
    },
    {
      id: 3,
      title: 'Expense',
      description: 'Total spending',
      category: 'Expense',
      amount: 0,
      date: new Date(),
      rows: 1,
      columns: 1,
      trend: 'down',
      trendValue: '-0%',
    },
  ]);

  transactions = signal<Transaction[]>([]);

  widgetsToAdd = computed(() => {
    const addedIds = this.addedWidgets().map((widget) => widget.id);
    return this.widgets().filter((widget) => !addedIds.includes(widget.id));
  });

  fetchStats() {
    this.http.get<StatsDTO>(this.apiUrl).subscribe({
      next: (stats) => {
        this.updateDashboardState(stats);
      },
      error: (err) => {
        console.error('Failed to fetch stats', err);
      },
    });
  }

  private updateDashboardState(stats: StatsDTO) {
    // Update Widgets
    const currentWidgets = this.addedWidgets();

    // Update Total Balance (ID 1)
    const balanceWidgetIndex = currentWidgets.findIndex((w) => w.id === 1);
    if (balanceWidgetIndex !== -1) {
      currentWidgets[balanceWidgetIndex] = {
        ...currentWidgets[balanceWidgetIndex],
        amount: stats.balance,
        date: new Date(),
      };
    }

    // Update Income (ID 2)
    const incomeWidgetIndex = currentWidgets.findIndex((w) => w.id === 2);
    if (incomeWidgetIndex !== -1) {
      currentWidgets[incomeWidgetIndex] = {
        ...currentWidgets[incomeWidgetIndex],
        amount: stats.income,
        date: new Date(),
      };
    }

    // Update Expense (ID 3)
    const expenseWidgetIndex = currentWidgets.findIndex((w) => w.id === 3);
    if (expenseWidgetIndex !== -1) {
      currentWidgets[expenseWidgetIndex] = {
        ...currentWidgets[expenseWidgetIndex],
        amount: stats.expense,
        date: new Date(),
      };
    }

    this.addedWidgets.set([...currentWidgets]);

    // Update Transactions
    const newTransactions: Transaction[] = [];

    if (stats.latestIncome) {
      newTransactions.push({
        id: stats.latestIncome.id,
        title: stats.latestIncome.title,
        category: stats.latestIncome.category || 'Income',
        amount: stats.latestIncome.amount,
        date: new Date(stats.latestIncome.date),
        type: 'income',
        icon: 'trending_up',
      });
    }

    if (stats.latestExpense) {
      newTransactions.push({
        id: stats.latestExpense.id,
        title: stats.latestExpense.title,
        category: stats.latestExpense.category || 'Expense',
        amount: stats.latestExpense.amount,
        date: new Date(stats.latestExpense.date),
        type: 'expense',
        icon: 'trending_down',
      });
    }

    // Sort by date descending
    newTransactions.sort((a, b) => b.date.getTime() - a.date.getTime());

    this.transactions.set(newTransactions);
  }

  // Fetch all transactions (Income + Expense)
  fetchAllTransactions() {
    this.http
      .get<Income[]>(`${this.apiUrl.replace('/stats', '/income')}/all`)
      .subscribe({
        next: (incomes) => {
          this.http
            .get<Expense[]>(`${this.apiUrl.replace('/stats', '/expense')}/all`)
            .subscribe({
              next: (expenses) => {
                this.mergeTransactions(incomes, expenses);
              },
              error: (err) => console.error('Failed to fetch expenses', err),
            });
        },
        error: (err) => console.error('Failed to fetch incomes', err),
      });
  }

  private mergeTransactions(incomes: Income[], expenses: Expense[]) {
    const transactionList: Transaction[] = [];

    incomes.forEach((inc) => {
      transactionList.push({
        id: inc.id,
        title: inc.title,
        category: inc.category || 'Income',
        amount: inc.amount,
        date: new Date(inc.date),
        type: 'income',
        icon: 'trending_up',
      });
    });

    expenses.forEach((exp) => {
      transactionList.push({
        id: exp.id,
        title: exp.title,
        category: exp.category || 'Expense',
        amount: exp.amount,
        date: new Date(exp.date),
        type: 'expense',
        icon: 'trending_down',
      });
    });

    // Sort by date descending
    transactionList.sort((a, b) => b.date.getTime() - a.date.getTime());
    this.transactions.set(transactionList);
  }

  // Delete Transaction
  deleteTransaction(id: number, type: 'income' | 'expense') {
    const endpoint = type === 'income' ? '/income' : '/expense';
    this.http
      .delete(`${this.apiUrl.replace('/stats', endpoint)}/${id}`, {
        responseType: 'text',
      })
      .subscribe({
        next: () => {
          this.fetchStats(); // Update balance/widgets
          this.fetchAllTransactions(); // Update list
        },
        error: (err) => console.error(`Failed to delete ${type}`, err),
      });
  }

  // Add Income
  addIncome(income: Partial<Income>): Observable<Income> {
    return this.http
      .post<Income>(`${this.apiUrl.replace('/stats', '/income')}`, income)
      .pipe(
        tap(() => {
          this.fetchStats();
          this.fetchAllTransactions();
        }),
      );
  }

  // Add Expense
  addExpense(expense: Partial<Expense>): Observable<Expense> {
    return this.http
      .post<Expense>(`${this.apiUrl.replace('/stats', '/expense')}`, expense)
      .pipe(
        tap(() => {
          this.fetchStats();
          this.fetchAllTransactions();
        }),
      );
  }

  getChartData(): Observable<GraphDTO> {
    return this.http.get<GraphDTO>(`${this.apiUrl}/chart`);
  }

  addWidget(w: Widget) {
    this.addedWidgets.set([...this.addedWidgets(), { ...w }]);
  }

  updateWidget(id: number, widget: Partial<Widget>) {
    const index = this.addedWidgets().findIndex((w) => w.id === id);
    if (index !== -1) {
      const newWidgets = [...this.addedWidgets()];
      newWidgets[index] = { ...newWidgets[index], ...widget };
      this.addedWidgets.set(newWidgets);
    }
  }

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

  removeWidget(id: number) {
    this.addedWidgets.set(
      this.addedWidgets().filter((widget) => widget.id !== id),
    );
  }
}
