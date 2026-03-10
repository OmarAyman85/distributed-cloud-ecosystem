import { Component, inject, signal } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { MatButtonModule } from '@angular/material/button';
import { MatIconModule } from '@angular/material/icon';
import { MatDialog, MatDialogModule } from '@angular/material/dialog';
import { MatSnackBar, MatSnackBarModule } from '@angular/material/snack-bar';
import { MatProgressBarModule } from '@angular/material/progress-bar';
import { MatTooltipModule } from '@angular/material/tooltip';
import { MatDialogRef, MAT_DIALOG_DATA } from '@angular/material/dialog';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatInputModule } from '@angular/material/input';
import { MatSelectModule } from '@angular/material/select';
import { BudgetService, BudgetDTO } from '../../services/budget.service';

@Component({
  selector: 'app-budget',
  standalone: true,
  imports: [
    CommonModule,
    FormsModule,
    MatButtonModule,
    MatIconModule,
    MatDialogModule,
    MatSnackBarModule,
    MatProgressBarModule,
    MatTooltipModule,
  ],
  templateUrl: './budget.component.html',
  styleUrl: './budget.component.scss',
})
export class BudgetComponent {
  budgetService = inject(BudgetService);
  dialog = inject(MatDialog);
  private snackBar = inject(MatSnackBar);

  budgets = this.budgetService.budgets;

  openAddBudgetDialog() {
    const dialogRef = this.dialog.open(AddBudgetDialogComponent, {
      width: '450px',
    });

    dialogRef.afterClosed().subscribe((result) => {
      if (result) {
        this.budgetService.createBudget(result).subscribe({
          next: () => {
            this.budgetService.fetchBudgets();
            this.snackBar.open('Budget created!', 'Close', { duration: 2000 });
          },
          error: (err) => console.error('Failed to create budget', err),
        });
      }
    });
  }

  deleteBudget(id: number) {
    if (confirm('Delete this budget?')) {
      this.budgetService.deleteBudget(id).subscribe({
        next: () => {
          this.budgetService.fetchBudgets();
          this.snackBar.open('Budget deleted', 'Close', { duration: 2000 });
        },
        error: (err) => console.error('Failed to delete budget', err),
      });
    }
  }

  getProgressColor(percentage: number): string {
    if (percentage >= 90) return 'warn';
    if (percentage >= 70) return 'accent';
    return 'primary';
  }

  getStatusLabel(percentage: number): string {
    if (percentage >= 100) return 'Over Budget!';
    if (percentage >= 90) return 'Critical';
    if (percentage >= 70) return 'Warning';
    return 'On Track';
  }

  getStatusClass(percentage: number): string {
    if (percentage >= 90) return 'status-danger';
    if (percentage >= 70) return 'status-warning';
    return 'status-good';
  }
}

// Inline dialog component for adding budgets
@Component({
  selector: 'app-add-budget-dialog',
  standalone: true,
  imports: [
    CommonModule,
    FormsModule,
    MatFormFieldModule,
    MatInputModule,
    MatSelectModule,
    MatButtonModule,
    MatIconModule,
    MatDialogModule,
  ],
  template: `
    <h2 mat-dialog-title>Create Budget</h2>
    <div mat-dialog-content>
      <mat-form-field appearance="outline" class="full-width">
        <mat-label>Category</mat-label>
        <mat-select [(value)]="category">
          <mat-option value="Food & Dining">Food & Dining</mat-option>
          <mat-option value="Transportation">Transportation</mat-option>
          <mat-option value="Entertainment">Entertainment</mat-option>
          <mat-option value="Shopping">Shopping</mat-option>
          <mat-option value="Healthcare">Healthcare</mat-option>
          <mat-option value="Utilities">Utilities</mat-option>
          <mat-option value="Education">Education</mat-option>
          <mat-option value="Housing">Housing</mat-option>
          <mat-option value="Other">Other</mat-option>
        </mat-select>
      </mat-form-field>

      <mat-form-field appearance="outline" class="full-width">
        <mat-label>Budget Limit ($)</mat-label>
        <input matInput type="number" [(ngModel)]="budgetLimit" min="1" />
      </mat-form-field>

      <mat-form-field appearance="outline" class="full-width">
        <mat-label>Period</mat-label>
        <mat-select [(value)]="period">
          <mat-option value="WEEKLY">Weekly</mat-option>
          <mat-option value="MONTHLY">Monthly</mat-option>
          <mat-option value="YEARLY">Yearly</mat-option>
        </mat-select>
      </mat-form-field>
    </div>
    <div mat-dialog-actions align="end">
      <button mat-button [mat-dialog-close]="null">Cancel</button>
      <button
        mat-flat-button
        color="primary"
        [mat-dialog-close]="getResult()"
        [disabled]="!category || !budgetLimit"
      >
        Create
      </button>
    </div>
  `,
  styles: [
    `
      .full-width {
        width: 100%;
        margin-bottom: 0.5rem;
      }
    `,
  ],
})
export class AddBudgetDialogComponent {
  category = '';
  budgetLimit: number | null = null;
  period = 'MONTHLY';

  getResult(): BudgetDTO | null {
    if (!this.category || !this.budgetLimit) return null;
    return {
      category: this.category,
      budgetLimit: this.budgetLimit,
      period: this.period as 'WEEKLY' | 'MONTHLY' | 'YEARLY',
    };
  }
}
