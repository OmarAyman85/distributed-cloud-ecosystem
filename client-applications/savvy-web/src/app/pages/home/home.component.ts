import { Component, inject } from '@angular/core';
import { RouterLink, Router } from '@angular/router';
import { MatIconModule } from '@angular/material/icon';
import { CommonModule } from '@angular/common';
import { AuthService } from '../../services/auth.service';

@Component({
  selector: 'app-home',
  standalone: true,
  imports: [CommonModule, RouterLink, MatIconModule],
  templateUrl: './home.component.html',
  styleUrl: './home.component.scss',
})
export class HomeComponent {
  authService = inject(AuthService);
  router = inject(Router);

  isLoggedIn = this.authService.isAuthenticated;

  logout() {
    this.authService.logout();
    // derived state will update automatically
  }
}
