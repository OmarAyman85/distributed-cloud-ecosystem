import { Component, inject, signal, computed, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { DebtService } from '../../services/debt.service';
import { Debt } from '../../models/dashboard';
import { MatButtonModule } from '@angular/material/button';
import { MatIconModule } from '@angular/material/icon';
import { MatDialog, MatDialogModule } from '@angular/material/dialog';
import { MatSnackBar, MatSnackBarModule } from '@angular/material/snack-bar';
import { MatTooltipModule } from '@angular/material/tooltip';
import { FormsModule } from '@angular/forms';
import { AddDebtDialogComponent } from './add-debt-dialog/add-debt-dialog.component';

@Component({
  selector: 'app-debt',
  standalone: true,
  imports: [
    CommonModule,
    FormsModule,
    MatButtonModule,
    MatIconModule,
    MatDialogModule,
    MatSnackBarModule,
    MatTooltipModule,
  ],
  templateUrl: './debt.component.html',
  styleUrls: ['./debt.component.scss'],
})
export class DebtComponent implements OnInit {
  debtService = inject(DebtService);
  dialog = inject(MatDialog);
  private snackBar = inject(MatSnackBar);

  debts = signal<Debt[]>([]);
  searchQuery = signal('');

  filteredDebts = computed(() => {
    const query = this.searchQuery().toLowerCase();
    const all = this.debts();
    if (!query) return all;
    return all.filter((item) => item.title.toLowerCase().includes(query));
  });

  ngOnInit() {
    this.loadDebts();
  }

  loadDebts() {
    this.debtService.getAllDebts().subscribe({
      next: (data) => this.debts.set(data),
      error: (err) => console.error('Error fetching debts', err),
    });
  }

  onSearchChange(event: Event) {
    this.searchQuery.set((event.target as HTMLInputElement).value);
  }

  openAddDialog() {
    const dialogRef = this.dialog.open(AddDebtDialogComponent, {
      width: '450px',
    });
    dialogRef.afterClosed().subscribe((result) => {
      if (result) {
        this.debtService.createDebt(result).subscribe({
          next: () => {
            this.loadDebts();
            this.snackBar.open('Debt added!', 'Close', { duration: 2000 });
          },
        });
      }
    });
  }

  openEditDialog(item: Debt) {
    const dialogRef = this.dialog.open(AddDebtDialogComponent, {
      width: '450px',
      data: { item },
    });
    dialogRef.afterClosed().subscribe((result) => {
      if (result) {
        this.debtService.updateDebt(item.id, result).subscribe({
          next: () => {
            this.loadDebts();
            this.snackBar.open('Debt updated!', 'Close', { duration: 2000 });
          },
        });
      }
    });
  }

  deleteDebt(id: number) {
    if (confirm('Are you sure you want to delete this debt?')) {
      this.debtService.deleteDebt(id).subscribe({
        next: () => {
          this.loadDebts();
          this.snackBar.open('Debt deleted', 'Close', { duration: 2000 });
        },
      });
    }
  }
}
