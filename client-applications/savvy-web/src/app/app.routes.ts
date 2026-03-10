import { Routes } from '@angular/router';
import { HomeComponent } from './pages/home/home.component';
import { DashboardComponent } from './pages/dashboard/dashboard.component';
import { ProfileComponent } from './pages/profile/profile.component';
import { SettingsComponent } from './pages/settings/settings.component';
import { IncomeComponent } from './pages/income/income.component';
import { ExpenseComponent } from './pages/expense/expense.component';
import { BudgetComponent } from './pages/budget/budget.component';
import { DebtComponent } from './pages/debt/debt.component';
import { SavingComponent } from './pages/saving/saving.component';
import { InvestmentComponent } from './pages/investment/investment.component';
import { AdvisorComponent } from './pages/advisor/advisor.component';

import { authGuard } from './guards/auth.guard';

export const routes: Routes = [
  {
    path: '',
    component: HomeComponent,
  },
  {
    path: 'login',
    loadComponent: () =>
      import('./pages/login/login.component').then((m) => m.LoginComponent),
  },
  {
    path: 'register',
    loadComponent: () =>
      import('./pages/register/register.component').then(
        (m) => m.RegisterComponent,
      ),
  },
  {
    path: 'dashboard',
    component: DashboardComponent,
    canActivate: [authGuard],
  },
  {
    path: 'income',
    component: IncomeComponent,
    canActivate: [authGuard],
  },
  {
    path: 'expense',
    component: ExpenseComponent,
    canActivate: [authGuard],
  },
  {
    path: 'budget',
    component: BudgetComponent,
    canActivate: [authGuard],
  },
  {
    path: 'debt',
    component: DebtComponent,
    canActivate: [authGuard],
  },
  {
    path: 'saving',
    component: SavingComponent,
    canActivate: [authGuard],
  },
  {
    path: 'investment',
    component: InvestmentComponent,
    canActivate: [authGuard],
  },
  {
    path: 'advisor',
    component: AdvisorComponent,
    canActivate: [authGuard],
  },
  {
    path: 'profile',
    component: ProfileComponent,
    canActivate: [authGuard],
  },
  {
    path: 'settings',
    component: SettingsComponent,
    canActivate: [authGuard],
  },
  {
    path: '**',
    redirectTo: 'dashboard',
  },
];
