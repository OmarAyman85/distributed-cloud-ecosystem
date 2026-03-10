import { Component, inject, signal, computed, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { SavingService } from '../../services/saving.service';
import { Saving } from '../../models/dashboard';
import { MatButtonModule } from '@angular/material/button';
import { MatIconModule } from '@angular/material/icon';
import { MatDialog, MatDialogModule } from '@angular/material/dialog';
import { MatSnackBar, MatSnackBarModule } from '@angular/material/snack-bar';
import { MatTooltipModule } from '@angular/material/tooltip';
import { FormsModule } from '@angular/forms';
import { AddSavingDialogComponent } from './add-saving-dialog/add-saving-dialog.component';

@Component({
  selector: 'app-saving',
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
  templateUrl: './saving.component.html',
  styleUrls: ['./saving.component.scss'],
})
export class SavingComponent implements OnInit {
  savingService = inject(SavingService);
  dialog = inject(MatDialog);
  private snackBar = inject(MatSnackBar);

  savings = signal<Saving[]>([]);
  searchQuery = signal('');

  filteredSavings = computed(() => {
    const query = this.searchQuery().toLowerCase();
    const all = this.savings();
    if (!query) return all;
    return all.filter((item) => item.title.toLowerCase().includes(query));
  });

  ngOnInit() {
    this.loadSavings();
  }

  loadSavings() {
    this.savingService.getAllSavings().subscribe({
      next: (data) => this.savings.set(data),
      error: (err) => console.error('Error fetching savings', err),
    });
  }

  onSearchChange(event: Event) {
    this.searchQuery.set((event.target as HTMLInputElement).value);
  }

  openAddDialog() {
    const dialogRef = this.dialog.open(AddSavingDialogComponent, {
      width: '450px',
    });
    dialogRef.afterClosed().subscribe((result) => {
      if (result) {
        this.savingService.createSaving(result).subscribe({
          next: () => {
            this.loadSavings();
            this.snackBar.open('Saving goal added!', 'Close', {
              duration: 2000,
            });
          },
        });
      }
    });
  }

  openEditDialog(item: Saving) {
    const dialogRef = this.dialog.open(AddSavingDialogComponent, {
      width: '450px',
      data: { item },
    });
    dialogRef.afterClosed().subscribe((result) => {
      if (result) {
        this.savingService.updateSaving(item.id, result).subscribe({
          next: () => {
            this.loadSavings();
            this.snackBar.open('Saving goal updated!', 'Close', {
              duration: 2000,
            });
          },
        });
      }
    });
  }

  deleteSaving(id: number) {
    if (confirm('Are you sure you want to delete this saving goal?')) {
      this.savingService.deleteSaving(id).subscribe({
        next: () => {
          this.loadSavings();
          this.snackBar.open('Saving goal deleted', 'Close', {
            duration: 2000,
          });
        },
      });
    }
  }

  // Helper method to get progress percentage
  getProgress(current: number, target: number): number {
    return target > 0 ? Math.min(Math.round((current / target) * 100), 100) : 0;
  }
}
