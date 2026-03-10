import { Component, inject } from '@angular/core';
import { CommonModule } from '@angular/common';
import { MatIconModule } from '@angular/material/icon';
import { MatButtonModule } from '@angular/material/button';
import { DashboardService } from '../../../services/dashboard.service';

@Component({
  selector: 'app-transaction-list',
  standalone: true,
  imports: [CommonModule, MatIconModule, MatButtonModule],
  templateUrl: './transaction-list.component.html',
  styleUrl: './transaction-list.component.scss',
})
export class TransactionListComponent {
  store = inject(DashboardService);

  constructor() {
    // Fetch full list on load
    this.store.fetchAllTransactions();
  }

  deleteTransaction(id: number, type: 'income' | 'expense') {
    if (confirm('Are you sure you want to delete this transaction?')) {
      this.store.deleteTransaction(id, type);
    }
  }

  getIconColor(type: string): string {
    return type === 'income' ? 'var(--success)' : 'var(--danger)';
  }

  getBgColor(type: string): string {
    return type === 'income'
      ? 'rgba(16, 185, 129, 0.1)'
      : 'rgba(239, 68, 68, 0.1)';
  }
}
