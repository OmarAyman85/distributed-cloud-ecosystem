import { Component, inject, signal, computed } from '@angular/core';
import { CommonModule } from '@angular/common';
import { IncomeService } from '../../services/income.service';
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
  selector: 'app-income',
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
  providers: [IncomeService],
  templateUrl: './income.component.html',
  styleUrls: ['./income.component.scss'],
})
export class IncomeComponent {
  incomeService = inject(IncomeService);
  dialog = inject(MatDialog);
  private snackBar = inject(MatSnackBar);

  incomes = this.incomeService.widgets;
  searchQuery = signal('');

  filteredIncomes = computed(() => {
    const query = this.searchQuery().toLowerCase();
    const all = this.incomes();
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

  openAddIncomeDialog() {
    const dialogRef = this.dialog.open(AddTransactionDialogComponent, {
      width: '450px',
      data: { type: 'income' },
    });

    dialogRef.afterClosed().subscribe((result) => {
      if (result) {
        this.createIncome(result);
      }
    });
  }

  openEditDialog(item: any) {
    const dialogRef = this.dialog.open(AddTransactionDialogComponent, {
      width: '450px',
      data: { type: 'income', editing: true, item },
    });

    dialogRef.afterClosed().subscribe((result) => {
      if (result) {
        this.incomeService.updateIncome(item.id, result).subscribe({
          next: () => {
            this.incomeService.fetchWidgets();
            this.snackBar.open('Income updated!', 'Close', { duration: 2000 });
          },
          error: (err) => console.error('Failed to update income', err),
        });
      }
    });
  }

  deleteIncome(id: number) {
    if (confirm('Are you sure you want to delete this income?')) {
      this.incomeService.deleteIncome(id).subscribe({
        next: () => {
          this.incomeService.fetchWidgets();
          this.snackBar.open('Income deleted', 'Close', { duration: 2000 });
        },
        error: (err) => console.error('Failed to delete income', err),
      });
    }
  }

  createIncome(data: any) {
    this.incomeService.createIncome(data).subscribe({
      next: () => {
        this.incomeService.fetchWidgets();
        this.snackBar.open('Income added!', 'Close', { duration: 2000 });
      },
      error: (err) => console.error('Failed to add income', err),
    });
  }
}
