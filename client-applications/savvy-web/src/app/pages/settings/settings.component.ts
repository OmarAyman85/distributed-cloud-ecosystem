import { Component, inject } from '@angular/core';
import { CommonModule } from '@angular/common';
import { MatCardModule } from '@angular/material/card';
import { MatSlideToggleModule } from '@angular/material/slide-toggle';
import { MatSelectModule } from '@angular/material/select';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatButtonModule } from '@angular/material/button';
import { MatIconModule } from '@angular/material/icon';
import { MatDividerModule } from '@angular/material/divider';
import { MatSnackBar, MatSnackBarModule } from '@angular/material/snack-bar';
import { ThemeService } from '../../services/theme.service';
import { AuthService } from '../../services/auth.service';
import { FormsModule } from '@angular/forms';

@Component({
  selector: 'app-settings',
  standalone: true,
  imports: [
    CommonModule,
    FormsModule,
    MatCardModule,
    MatSlideToggleModule,
    MatSelectModule,
    MatFormFieldModule,
    MatButtonModule,
    MatIconModule,
    MatDividerModule,
    MatSnackBarModule,
  ],
  templateUrl: './settings.component.html',
  styleUrl: './settings.component.scss',
})
export class SettingsComponent {
  themeService = inject(ThemeService);
  authService = inject(AuthService);
  private snackBar = inject(MatSnackBar);

  selectedCurrency = 'USD';
  currencies = [
    { value: 'USD', label: 'USD ($)' },
    { value: 'EUR', label: 'EUR (€)' },
    { value: 'GBP', label: 'GBP (£)' },
    { value: 'EGP', label: 'EGP (E£)' },
    { value: 'SAR', label: 'SAR (﷼)' },
    { value: 'AED', label: 'AED (د.إ)' },
  ];

  notificationsEnabled = true;
  emailReports = false;

  constructor() {
    const saved = localStorage.getItem('savvy_currency');
    if (saved) this.selectedCurrency = saved;

    const notifSaved = localStorage.getItem('savvy_notifications');
    if (notifSaved) this.notificationsEnabled = notifSaved === 'true';

    const emailSaved = localStorage.getItem('savvy_email_reports');
    if (emailSaved) this.emailReports = emailSaved === 'true';
  }

  onCurrencyChange() {
    localStorage.setItem('savvy_currency', this.selectedCurrency);
    this.snackBar.open('Currency updated', 'Close', { duration: 2000 });
  }

  onNotificationsChange() {
    localStorage.setItem(
      'savvy_notifications',
      String(this.notificationsEnabled),
    );
    this.snackBar.open(
      `Notifications ${this.notificationsEnabled ? 'enabled' : 'disabled'}`,
      'Close',
      { duration: 2000 },
    );
  }

  onEmailReportsChange() {
    localStorage.setItem('savvy_email_reports', String(this.emailReports));
  }

  logout() {
    this.authService.logout();
  }

  exportData() {
    this.snackBar.open('Data export started...', 'Close', { duration: 3000 });
  }

  deleteAccount() {
    if (
      confirm(
        'Are you sure you want to delete your account? This action cannot be undone.',
      )
    ) {
      this.snackBar.open(
        'Account deletion requested. Contact support.',
        'Close',
        { duration: 5000 },
      );
    }
  }
}
