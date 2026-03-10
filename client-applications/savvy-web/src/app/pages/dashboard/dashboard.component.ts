import { Component, inject } from '@angular/core';
import { WidgetComponent } from './widget/widget.component';
import { DashboardService } from '../../services/dashboard.service';
import { MatButtonModule } from '@angular/material/button';
import { MatIcon, MatIconModule } from '@angular/material/icon';
import { CommonModule } from '@angular/common';
import { DashboardHeaderComponent } from './dashboard-header.component';
import { TransactionListComponent } from './transaction-list/transaction-list.component';
import { MatDialog, MatDialogModule } from '@angular/material/dialog';
import { AddTransactionDialogComponent } from './add-transaction-dialog/add-transaction-dialog.component';
import { ChartComponent } from './chart/chart.component';

@Component({
  selector: 'app-dashboard',
  standalone: true,
  imports: [
    CommonModule,
    WidgetComponent,
    DashboardHeaderComponent,
    TransactionListComponent,
    MatButtonModule,
    MatIconModule,
    MatDialogModule,
    ChartComponent,
  ],
  providers: [DashboardService],
  templateUrl: './dashboard.component.html',
  styleUrl: './dashboard.component.scss',
})
export class DashboardComponent {
  store = inject(DashboardService);
  dialog = inject(MatDialog);

  openAddTransactionDialog() {
    const dialogRef = this.dialog.open(AddTransactionDialogComponent, {
      width: '500px',
    });

    dialogRef.afterClosed().subscribe((result) => {
      if (result) {
        if (result.type === 'income') {
          this.store.addIncome(result).subscribe();
        } else {
          this.store.addExpense(result).subscribe();
        }
      }
    });
  }
}
