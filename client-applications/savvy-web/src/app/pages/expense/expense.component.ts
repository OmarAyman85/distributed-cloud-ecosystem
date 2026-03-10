import { Component, inject, signal, computed } from '@angular/core';
import { CommonModule } from '@angular/common';
import { ExpenseService } from '../../services/expense.service';
import { MatButtonModule } from '@angular/material/button';
import { MatIconModule } from '@angular/material/icon';
import { MatDialog, MatDialogModule } from '@angular/material/dialog';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatInputModule } from '@angular/material/input';
import { MatSnackBar, MatSnackBarModule } from '@angular/material/snack-bar';
import { MatTooltipModule } from '@angular/material/tooltip';
import { FormsModule } from '@angular/forms';
import { AddTransactionDialogComponent } from '../dashboard/add-transaction-dialog/add-transaction-dialog.component';

@Component({
  selector: 'app-expense',
  standalone: true,
  imports: [
    CommonModule,
    FormsModule,
    MatButtonModule,
    MatIconModule,
    MatDialogModule,
    MatFormFieldModule,
    MatInputModule,
    MatSnackBarModule,
    MatTooltipModule,
  ],
  providers: [ExpenseService],
  templateUrl: './expense.component.html',
  styleUrls: ['./expense.component.scss'],
})
export class ExpenseComponent {
  expenseService = inject(ExpenseService);
  dialog = inject(MatDialog);
  private snackBar = inject(MatSnackBar);

  expenses = this.expenseService.widgets;
  searchQuery = signal('');

  filteredExpenses = computed(() => {
    const query = this.searchQuery().toLowerCase();
    const all = this.expenses();
    if (!query) return all;
    return all.filter(
      (item: any) =>
        item.title?.toLowerCase().includes(query) ||
        item.category?.toLowerCase().includes(query),
    );
  });

  onSearchChange(event: Event) {
    this.searchQuery.set((event.target as HTMLInputElement).value);
  }

  openAddExpenseDialog() {
    const dialogRef = this.dialog.open(AddTransactionDialogComponent, {
      width: '450px',
      data: { type: 'expense' },
    });

    dialogRef.afterClosed().subscribe((result) => {
      if (result) {
        this.createExpense(result);
      }
    });
  }

  openEditDialog(item: any) {
    const dialogRef = this.dialog.open(AddTransactionDialogComponent, {
      width: '450px',
      data: { type: 'expense', editing: true, item },
    });

    dialogRef.afterClosed().subscribe((result) => {
      if (result) {
        this.expenseService.updateExpense(item.id, result).subscribe({
          next: () => {
            this.expenseService.fetchWidgets();
            this.snackBar.open('Expense updated!', 'Close', {
              duration: 2000,
            });
          },
          error: (err) => console.error('Failed to update expense', err),
        });
      }
    });
  }

  deleteExpense(id: number) {
    if (confirm('Are you sure you want to delete this expense?')) {
      this.expenseService.deleteExpense(id).subscribe({
        next: () => {
          this.expenseService.fetchWidgets();
          this.snackBar.open('Expense deleted', 'Close', { duration: 2000 });
        },
        error: (err) => console.error('Failed to delete expense', err),
      });
    }
  }

  createExpense(data: any) {
    this.expenseService.createExpense(data).subscribe({
      next: () => {
        this.expenseService.fetchWidgets();
        this.snackBar.open('Expense added!', 'Close', { duration: 2000 });
      },
      error: (err) => console.error('Failed to add expense', err),
    });
  }
}
