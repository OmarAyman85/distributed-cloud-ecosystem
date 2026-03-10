import { Component, computed, Input, signal, inject } from '@angular/core';
import { MatListModule } from '@angular/material/list';
import { MatIconModule } from '@angular/material/icon';
import { MatButtonModule } from '@angular/material/button';
import { CommonModule } from '@angular/common';
import { RouterModule } from '@angular/router';
import { AuthService } from '../../services/auth.service';
import { ThemeService } from '../../services/theme.service';

export type MenuItem = {
  icon: String;
  label: String;
  route?: string;
};

@Component({
  selector: 'app-custom-sidenav',
  imports: [
    CommonModule,
    MatListModule,
    MatIconModule,
    MatButtonModule,
    RouterModule,
  ],
  templateUrl: './custom-sidenav.component.html',
  styleUrl: './custom-sidenav.component.scss',
})
export class CustomSidenavComponent {
  authService = inject(AuthService);
  themeService = inject(ThemeService);

  toggleTheme() {
    this.themeService.toggle();
  }

  sideNavCollapsed = signal<boolean>(false);
  @Input() set collapsed(value: boolean) {
    this.sideNavCollapsed.set(value);
  }
  profilePicSize = computed(() => (this.sideNavCollapsed() ? '50' : '200'));

  menuItems = signal<MenuItem[]>([
    { icon: 'home', label: 'Home', route: '/' },
    { icon: 'dashboard', label: 'Dashboard', route: '/dashboard' },
    { icon: 'attach_money', label: 'Income', route: '/income' },
    { icon: 'payments', label: 'Expense', route: '/expense' },
    { icon: 'account_balance_wallet', label: 'Budgets', route: '/budget' },
    { icon: 'credit_card', label: 'Debts', route: '/debt' },
    { icon: 'savings', label: 'Savings', route: '/saving' },
    { icon: 'trending_up', label: 'Investments', route: '/investment' },
    { icon: 'assistant', label: 'Advisor', route: '/advisor' },
    { icon: 'person', label: 'Profile', route: '/profile' },
    { icon: 'settings', label: 'Settings', route: '/settings' },
  ]);
}
