import { Component, inject, signal, computed, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { InvestmentService } from '../../services/investment.service';
import { Investment } from '../../models/dashboard';
import { MatButtonModule } from '@angular/material/button';
import { MatIconModule } from '@angular/material/icon';
import { MatDialog, MatDialogModule } from '@angular/material/dialog';
import { MatSnackBar, MatSnackBarModule } from '@angular/material/snack-bar';
import { MatTooltipModule } from '@angular/material/tooltip';
import { FormsModule } from '@angular/forms';
import { AddInvestmentDialogComponent } from './add-investment-dialog/add-investment-dialog.component';

@Component({
  selector: 'app-investment',
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
  templateUrl: './investment.component.html',
  styleUrls: ['./investment.component.scss'],
})
export class InvestmentComponent implements OnInit {
  investmentService = inject(InvestmentService);
  dialog = inject(MatDialog);
  private snackBar = inject(MatSnackBar);

  investments = signal<Investment[]>([]);
  searchQuery = signal('');

  filteredInvestments = computed(() => {
    const query = this.searchQuery().toLowerCase();
    const all = this.investments();
    if (!query) return all;
    return all.filter(
      (item) =>
        item.title.toLowerCase().includes(query) ||
        item.symbol.toLowerCase().includes(query),
    );
  });

  ngOnInit() {
    this.loadInvestments();
  }

  loadInvestments() {
    this.investmentService.getAllInvestments().subscribe({
      next: (data) => this.investments.set(data),
      error: (err) => console.error('Error fetching investments', err),
    });
  }

  onSearchChange(event: Event) {
    this.searchQuery.set((event.target as HTMLInputElement).value);
  }

  openAddDialog() {
    const dialogRef = this.dialog.open(AddInvestmentDialogComponent, {
      width: '500px',
    });
    dialogRef.afterClosed().subscribe((result) => {
      if (result) {
        this.investmentService.createInvestment(result).subscribe({
          next: () => {
            this.loadInvestments();
            this.snackBar.open('Investment added!', 'Close', {
              duration: 2000,
            });
          },
        });
      }
    });
  }

  openEditDialog(item: Investment) {
    const dialogRef = this.dialog.open(AddInvestmentDialogComponent, {
      width: '500px',
      data: { item },
    });
    dialogRef.afterClosed().subscribe((result) => {
      if (result) {
        this.investmentService.updateInvestment(item.id, result).subscribe({
          next: () => {
            this.loadInvestments();
            this.snackBar.open('Investment updated!', 'Close', {
              duration: 2000,
            });
          },
        });
      }
    });
  }

  deleteInvestment(id: number) {
    if (confirm('Are you sure you want to delete this investment?')) {
      this.investmentService.deleteInvestment(id).subscribe({
        next: () => {
          this.loadInvestments();
          this.snackBar.open('Investment deleted', 'Close', { duration: 2000 });
        },
      });
    }
  }

  getProfitOrLoss(invested: number, current: number): string {
    const diff = current - invested;
    const sign = diff >= 0 ? '+' : '';
    return `${sign}$${Math.abs(diff).toFixed(2)}`;
  }

  getProfitOrLossPercentage(invested: number, current: number): string {
    if (invested === 0) return '0%';
    const diff = current - invested;
    const perc = (diff / invested) * 100;
    const sign = perc >= 0 ? '+' : '';
    return `${sign}${Math.abs(perc).toFixed(2)}%`;
  }
}
